package org.ksmart.birth.newbirth.validator;

import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.ksmart.birth.utils.enums.ErrorCodes.*;

@Component
public class NewBirthApplicationValidator {
    private final BirthConfiguration bndConfig;
    private final NewMdmsValidator mdmsValidator;


    @Autowired
    NewBirthApplicationValidator(BirthConfiguration bndConfig, NewMdmsValidator mdmsValidator) {
        this.bndConfig = bndConfig;
        this.mdmsValidator = mdmsValidator;
    }

    /**
     * Validate abirth details create request.
     *
     * @param request the {@link NewBirthApplication}
     */
    public void validateCreate(NewBirthDetailRequest request, Object mdmsData) {
        List<NewBirthApplication> birthApplications = request.getNewBirthDetails();
        if (CollectionUtils.isEmpty(request.getNewBirthDetails())) {
            throw new CustomException(ErrorCodes.BIRTH_DETAILS_REQUIRED.getCode(),
                    "Birth details is required.");
        }

        if (birthApplications.size() > 1) { // NOPMD
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Supports only single Birth  application create request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getTenantId())) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Tenant id is required for create request.");
        }

        if (birthApplications.get(0).getDateOfBirth() == null) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Date of birth is required for create request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getPlaceofBirthId())) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Place of birth id is required for create request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getApplicationType())) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Application type is required for create request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getBusinessService())) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Bussiness service is required for create request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getWorkFlowCode())) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Workflow code is required for create request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getAction())) {
            throw new CustomException(INVALID_CREATE.getCode(),
                    "Workflow action is required for create request.");
        }

        mdmsValidator.validateMdmsData(request, mdmsData);
    }

    public void validateUpdate(NewBirthDetailRequest request, Object mdmsData) {
        List<NewBirthApplication> birthApplications = request.getNewBirthDetails();
        if (CollectionUtils.isEmpty(request.getNewBirthDetails())) {
            throw new CustomException(ErrorCodes.BIRTH_DETAILS_REQUIRED.getCode(),
                    "Birth details is required.");
        }

        if (birthApplications.size() > 1) { // NOPMD
            throw new CustomException(BIRTH_DETAILS_REQUIRED.getCode(),
                    "Supports only single application create request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getTenantId())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Tenant id is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getId())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Application id is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getApplicationType())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Application type is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getBusinessService())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Bussiness service is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getApplicationNo())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Application number is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getWorkFlowCode())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Workflow code is required for update request.");
        }

        if (StringUtils.isBlank(birthApplications.get(0).getAction())) {
            throw new CustomException(INVALID_UPDATE.getCode(),
                    "Workflow action is required for update request.");
        }

        mdmsValidator.validateMdmsData(request, mdmsData);
    }

}
