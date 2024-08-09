package digit.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.egov.common.contract.request.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import digit.bmc.model.UserSchemeApplication;
import digit.config.BmcConfiguration;
import digit.enrichment.SchemeApplicationEnrichment;
import digit.kafka.Producer;
import digit.repository.SchemeApplicationRepository;
import digit.repository.UserSchemeCitizenRepository;
import digit.validators.SchemeApplicationValidator;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import digit.web.models.SchemeApplicationSearchCriteria;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BmcApplicationService {

    private static final Logger log = LoggerFactory.getLogger(BmcApplicationService.class);
	private final BmcUserService bmcUserService;
    private final UserSchemeCitizenRepository userschemecitizenRepository;
	@Autowired
	private BmcConfiguration configuration;
	@Autowired
	private QualificationService qualificationService;
    @Autowired
	private final EgBoundaryService egBoundaryService;
    private final UserSchemeApplicationService userSchemeApplicationService;
    private final SchemeService schemeService;
    @Autowired
    private  SchemeApplicationValidator validator;
    @Autowired
    private  SchemeApplicationEnrichment enrichmentUtil;
    @Autowired
    private  UserService userService;
    @Autowired
    private  WorkflowService workflowService;
    @Autowired
    private  SchemeApplicationRepository schemeApplicationRepository;
    @Autowired
    private  Producer producer;
    @Autowired
    public BmcApplicationService(UserSchemeApplicationService userSchemeApplicationService,BmcUserService bmcUserService,SchemeService schemeService,UserSchemeCitizenRepository userschemecitizenRepository, EgBoundaryService egBoundaryService) {
        this.egBoundaryService = egBoundaryService;
		this.userSchemeApplicationService = userSchemeApplicationService;
        this.bmcUserService = bmcUserService;
		this.schemeService = schemeService;
		this.userschemecitizenRepository = userschemecitizenRepository;
    }
   

    
    public List<SchemeApplication> registerSchemeApplication(SchemeApplicationRequest schemeApplicationRequest) {
        // Validate applications
        validator.validateSchemeApplication(schemeApplicationRequest);

        // Enrich applications
        enrichmentUtil.enrichSchemeApplication(schemeApplicationRequest);
        userService.callUserService(schemeApplicationRequest);
        bmcUserService.saveUserData(schemeApplicationRequest);
        qualificationService.saveQualification(schemeApplicationRequest);
        egBoundaryService.saveEgBoundary(schemeApplicationRequest);
        // Initiate workflow for the new application
        workflowService.updateWorkflowStatus(schemeApplicationRequest);

        // Push the application to the topic for persister to listen and persist
        producer.push("save-bmc-application", schemeApplicationRequest);

        // Return the response back to user
        return schemeApplicationRequest.getSchemeApplications();
    }

    public List<SchemeApplication> searchSchemeApplications(RequestInfo requestInfo, SchemeApplicationSearchCriteria schemeApplicationSearchCriteria) {
        // Fetch applications from database according to the given search criteria
        List<SchemeApplication> applications = schemeApplicationRepository.getApplications(schemeApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if (CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        // Enrich user details of applicant objects
        applications.forEach(enrichmentUtil::enrichUserDetailsOnSearch);

        // Otherwise return the found applications
        return applications;
    }

    public SchemeApplication updateSchemeApplication(SchemeApplicationRequest schemeApplicationRequest) {
        // Validate whether the application that is being requested for update indeed exists
        SchemeApplication existingApplication = validator.validateApplicationExistence(schemeApplicationRequest.getSchemeApplications().get(0));
        existingApplication.setWorkflow(schemeApplicationRequest.getSchemeApplications().get(0).getWorkflow());
        log.info(existingApplication.toString());
        schemeApplicationRequest.setSchemeApplications(Collections.singletonList(existingApplication));

        // Enrich application upon update
        enrichmentUtil.enrichSchemeApplicationUponUpdate(schemeApplicationRequest);

        workflowService.updateWorkflowStatus(schemeApplicationRequest);

        // Just like create request, update request will be handled asynchronously by the persister
        producer.push("update-bmc-application", schemeApplicationRequest);

        return schemeApplicationRequest.getSchemeApplications().get(0);
    }


    public List<UserSchemeApplication> rendomizeCitizens(SchemeApplicationRequest schemeApplicationRequest) {
        List<UserSchemeApplication> citizens = userSchemeApplicationService.getfirstApprovalCitizen(schemeApplicationRequest);
        log.info("Value returned by getFirstApprovalCitizen: {}", citizens);
        
        Random random = new Random();
        Collections.shuffle(citizens, random);
        log.info("Shuffled citizens: {}", citizens);

        Long numberOfMachines = schemeApplicationRequest.getSchemeApplications().get(0).getNumberOfMachines();
        log.info("Number of machines: {}", numberOfMachines);
        int numberOfCitizens = Math.min(citizens.size(), numberOfMachines.intValue());
        log.info("Number of citizens to select: {}", numberOfCitizens);
        
        List<UserSchemeApplication> selectedCitizens = new ArrayList<>();
        List<Long> selectedCitizenIds = new ArrayList<>();
        for (int i = 0; i < numberOfCitizens; i++) {
            UserSchemeApplication citizen = citizens.get(i);
            citizen.setRandomSelection(true);
            selectedCitizens.add(citizen);
            selectedCitizenIds.add(citizen.getId());
        }
        log.info("Selected citizens: {}", selectedCitizens);

        userschemecitizenRepository.updateRandomSelection(selectedCitizenIds);

        return selectedCitizens;
    }
    
    
    public List<SchemeApplication> savePersnaldetails(SchemeApplicationRequest schemeApplicationRequest) {
    	bmcUserService.saveUserData(schemeApplicationRequest);
        qualificationService.saveQualification(schemeApplicationRequest);
        egBoundaryService.saveEgBoundary(schemeApplicationRequest);
       // workflowService.updateWorkflowStatus(schemeApplicationRequest);
        userService.callUserService(schemeApplicationRequest);

        return schemeApplicationRequest.getSchemeApplications();
    }

}
