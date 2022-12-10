package org.ksmart.death.birth.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ksmart.death.birth.certmodel.BirthCertAppln;
import org.ksmart.death.birth.certmodel.BirthCertRequest;
import org.ksmart.death.birth.certmodel.BirthCertificate;
import org.ksmart.death.birth.model.EgBirthDtl;
import org.ksmart.death.birth.model.SearchCriteria;
import org.ksmart.death.birth.repository.BirthRepository;
import org.ksmart.death.birth.validator.BirthValidator;
import org.ksmart.death.common.calculation.collections.models.Payment;
import org.ksmart.death.common.calculation.collections.models.PaymentDetail;
import org.ksmart.death.common.calculation.collections.models.PaymentResponse;
import org.ksmart.death.common.calculation.collections.models.PaymentSearchCriteria;
import org.ksmart.death.common.consumer.ReceiptConsumer;
import org.ksmart.death.common.contract.BirthPdfApplicationRequest;
import org.ksmart.death.common.contract.EgovPdfResp;
import org.ksmart.death.common.contract.RequestInfoWrapper;
import org.ksmart.death.common.model.AuditDetails;
import org.ksmart.death.common.repository.ServiceRequestRepository;
import org.ksmart.death.config.BirthDeathConfiguration;
import org.ksmart.death.utils.CommonUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BirthService {
	
	@Autowired
    BirthRepository repository;

	@Autowired
	ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	@Qualifier("objectMapperBnd")
	ObjectMapper objectMapper;
	
	@Autowired
    BirthValidator validator;
	
	@Autowired
	EnrichmentService enrichmentService;
	
	@Autowired
	CalculationService calculationService;
	
	@Autowired
    CommonUtils commUtils;
	
	@Autowired
	BirthDeathConfiguration config;
	
	@Autowired
    ReceiptConsumer consumer;
	
	public List<EgBirthDtl> search(SearchCriteria criteria, RequestInfo requestInfo) {
		List<EgBirthDtl> birthDtls = new ArrayList<>() ;
		if(requestInfo.getUserInfo().getType().equalsIgnoreCase("EMPLOYEE")) {
			if(validator.validateFieldsEmployee(criteria)) {
				birthDtls = repository.getBirthDtls(criteria);
			}
		}
		else {
			if(validator.validateFieldsCitizen(criteria)) {
				birthDtls = repository.getBirthDtls(criteria);
			}
		}
		return birthDtls;
	}

	public List<BirthCertificate> plainSearch(SearchCriteria criteria) {
		return repository.getBirthCertificateForPlainSearch(criteria);
	}

	public BirthCertificate download(SearchCriteria criteria, RequestInfo requestInfo) {
		try {
		BirthCertificate birthCertificate = new BirthCertificate();
		birthCertificate.setSource(criteria.getSource().toString());
		birthCertificate.setBirthDtlId(criteria.getId());
		birthCertificate.setTenantId(criteria.getTenantId());
		BirthCertRequest birthCertRequest = BirthCertRequest.builder().birthCertificate(birthCertificate).requestInfo(requestInfo).build();
		List<EgBirthDtl> birtDtls = repository.getBirthDtlsAll(criteria,requestInfo);
			birthCertificate.setBirthPlace(birtDtls.get(0).getPlaceofbirth());
			birthCertificate.setGender(birtDtls.get(0).getGenderStr());
			birthCertificate.setWard(birtDtls.get(0).getBirthPermaddr().getTehsil());
			birthCertificate.setState(birtDtls.get(0).getBirthPermaddr().getState());
			birthCertificate.setDistrict(birtDtls.get(0).getBirthPermaddr().getDistrict());
			birthCertificate.setDateofbirth(birtDtls.get(0).getDateofbirth());
			birthCertificate.setDateofreport(birtDtls.get(0).getDateofreport());
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String date = format.format(birtDtls.get(0).getDateofreport());
			String datestr= date.split("-")[2];
			birthCertificate.setYear(datestr);
		if(birtDtls.size()>1) 
			throw new CustomException("Invalid_Input","Error in processing data");
		enrichmentService.enrichCreateRequest(birthCertRequest);
		enrichmentService.setIdgenIds(birthCertRequest);
		if(birtDtls.get(0).getCounter()>0){
			enrichmentService.setDemandParams(birthCertRequest);
			enrichmentService.setGLCode(birthCertRequest);
			calculationService.addCalculation(birthCertRequest);
			birthCertificate.setApplicationStatus(BirthCertificate.StatusEnum.ACTIVE);
		}
		else{
			birtDtls.get(0).setBirthcertificateno(birthCertRequest.getBirthCertificate().getBirthCertificateNo());
			BirthPdfApplicationRequest applicationRequest = BirthPdfApplicationRequest.builder().requestInfo(requestInfo).birthCertificate(birtDtls).build();
			EgovPdfResp pdfResp = repository.saveBirthCertPdf(applicationRequest);
			birthCertificate.setEmbeddedUrl(applicationRequest.getBirthCertificate().get(0).getEmbeddedUrl());
			birthCertificate.setDateofissue(applicationRequest.getBirthCertificate().get(0).getDateofissue());
			birthCertificate.setFilestoreid(pdfResp.getFilestoreIds().get(0));
			repository.updateCounter(birthCertificate.getBirthDtlId());
			birthCertificate.setApplicationStatus(BirthCertificate.StatusEnum.FREE_DOWNLOAD);
			
		}
		birthCertificate.setCounter(birtDtls.get(0).getCounter());
		repository.save(birthCertRequest);
		return birthCertificate;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new CustomException("DOWNLOAD_ERROR","Error in Downloading Certificate");
		}
	}

	public BirthCertificate getBirthCertReqByConsumerCode(SearchCriteria criteria, RequestInfo requestInfo) {
		return repository.getBirthCertReqByConsumerCode(criteria.getConsumerCode(),requestInfo);
	}
	
	public List<BirthCertAppln> searchApplications(RequestInfoWrapper requestInfoWrapper) {
		List<BirthCertAppln> certApplns = null;
		certApplns = repository.searchApplications(requestInfoWrapper.getRequestInfo().getUserInfo().getUuid());
		for (BirthCertAppln certAppln : certApplns) {
			if (certAppln.getStatus().equalsIgnoreCase(BirthCertificate.StatusEnum.PAID.toString())) {
				try {
					BirthCertificate cert = repository.getBirthCertReqByConsumerCode(certAppln.getApplicationNumber(),
							requestInfoWrapper.getRequestInfo());
					String uuid = requestInfoWrapper.getRequestInfo().getUserInfo().getUuid();
				    AuditDetails auditDetails = commUtils.getAuditDetails(uuid, false);
					cert.getAuditDetails().setLastModifiedBy(auditDetails.getLastModifiedBy());
					cert.getAuditDetails().setLastModifiedTime(auditDetails.getLastModifiedTime());
					cert = consumer.updateBirthPDFGEN(requestInfoWrapper.getRequestInfo(), cert);
					if (null != cert.getFilestoreid()) {
						certAppln.setFileStoreId(cert.getFilestoreid());
						certAppln.setStatus(cert.getApplicationStatus().toString());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (certAppln.getStatus().equalsIgnoreCase(BirthCertificate.StatusEnum.ACTIVE.toString())) {
				PaymentSearchCriteria criteria = new PaymentSearchCriteria();
				criteria.setTenantId(certAppln.getTenantId());
				criteria.setConsumerCodes(Collections.singleton(certAppln.getApplicationNumber()));
				List<Payment> payments = getPayments(criteria, requestInfoWrapper);
				if (!CollectionUtils.isEmpty(payments)) {
					for (PaymentDetail paymentDetail : payments.get(0).getPaymentDetails()) {
						try {
							BirthCertificate cert = consumer.updateBirthPAID(requestInfoWrapper.getRequestInfo(),
									paymentDetail);
							if (null != cert) {
								certAppln.setStatus(cert.getApplicationStatus().toString());
							}
							cert = consumer.updateBirthPDFGEN(requestInfoWrapper.getRequestInfo(), cert);
							if (null != cert.getFilestoreid()) {
								certAppln.setFileStoreId(cert.getFilestoreid());
								certAppln.setStatus(cert.getApplicationStatus().toString());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return certApplns;
	}

	public void updateDownloadStatus(BirthCertRequest certRequest) {
		if(null!=certRequest.getRequestInfo() && null!=certRequest.getRequestInfo().getUserInfo() && null!=certRequest.getRequestInfo().getUserInfo().getUuid())
		{
			AuditDetails auditDetails = commUtils.getAuditDetails(certRequest.getRequestInfo().getUserInfo().getUuid(), false);
			BirthCertificate birthCert = certRequest.getBirthCertificate();
			birthCert.getAuditDetails().setLastModifiedBy(auditDetails.getLastModifiedBy());
			birthCert.getAuditDetails().setLastModifiedTime(auditDetails.getLastModifiedTime());
			birthCert.setApplicationStatus(BirthCertificate.StatusEnum.PAID_DOWNLOAD);
			repository.update(certRequest);
		}
	}

	public List<EgBirthDtl> viewCertificateData(SearchCriteria criteria) {
		return repository.viewCertificateData(criteria);
	}
	
	public List<EgBirthDtl> viewfullCertMasterData(SearchCriteria criteria,RequestInfo requestInfo) {
		return repository.viewfullCertMasterData(criteria,requestInfo);
	}
	
	
    public List<Payment> getPayments(PaymentSearchCriteria criteria, RequestInfoWrapper requestInfoWrapper) {
        StringBuilder url = getPaymentSearchUrl(criteria);
        return objectMapper.convertValue(serviceRequestRepository.fetchResult(url, requestInfoWrapper), PaymentResponse.class).getPayments();
    }
    
    public StringBuilder getPaymentSearchUrl(PaymentSearchCriteria criteria) {
        return new StringBuilder().append(config.getCollectionServiceHost())
                .append(config.getPaymentSearchEndpoint()).append("?")
                .append("tenantId=").append(criteria.getTenantId())
                .append("&").append("consumerCodes=")
                .append(StringUtils.join(criteria.getConsumerCodes(),","))
                .append("&").append("status=APPROVED,DEPOSITED,NEW");
    }
}
