package org.ksmart.birth.correction.enrichment;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.common.enrichment.BaseEnrichment;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.repository.IdGenRepository;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.correction.service.MdmsForCorrectionBirthService;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.correction.CorrectionApplication;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

@Component
public class CorrectionBirthEnrichment implements BaseEnrichment {
    @Autowired
    BirthConfiguration config;
    @Autowired
   MdmsUtil mdmsUtil;
    @Autowired
    MdmsForCorrectionBirthService mdmsBirthService;
    @Autowired
    MdmsTenantService mdmsTenantService;
    @Autowired
    IdGenRepository idGenRepository;


    public void enrichCreate(CorrectionRequest request) {
        String tenantId = null;
        Date date = new Date();
        long doreport = date.getTime();
        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);
        for (CorrectionApplication birth : request.getCorrectionDetails()) {
            tenantId = birth.getTenantId();
            birth.setDateOfReport(doreport);
        }
        setPlaceOfBirth(request, tenantId, auditDetails);
        setApplicationNumbers(request);
        setFileNumbers(request);
        setPresentAddress(request);
        setPermanentAddress(request);
    }

    public void enrichUpdate(CorrectionRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);
        request.getCorrectionDetails()
                .forEach(birth -> {
                    birth.setAuditDetails(auditDetails);
                    if ((birth.getApplicationStatus() == "APPROVED" && birth.getAction() == "APPROVE")) {
                        setRegistrationNumber(request);
                    }
                });
        setPresentAddress(request);
        setPermanentAddress(request);
    }
    private void setApplicationNumbers(CorrectionRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<CorrectionApplication> birthDetails = request.getCorrectionDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();
        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthApplNumberIdName(),
                request.getCorrectionDetails().get(0).getApplicationType(),
                "APPL",
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());

        ListIterator<String> itr = filecodes.listIterator();
        request.getCorrectionDetails()
                .forEach(birth -> {
                    birth.setApplicationNo(itr.next());
                });
    }

    private void setFileNumbers(CorrectionRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<CorrectionApplication> birthDetails = request.getCorrectionDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthFileNumberName(),
                request.getCorrectionDetails().get(0).getApplicationType(),
                "FILE",
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        ListIterator<String> itr = filecodes.listIterator();
        request.getCorrectionDetails()
                .forEach(birth -> {
                    birth.setFileNumber(itr.next());
                    birth.setFileDate(currentTime);
                });
    }

    private void setRegistrationNumber(CorrectionRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<CorrectionApplication> birthDetails = request.getCorrectionDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthRegisNumberName(),
                request.getCorrectionDetails().get(0).getApplicationType(),
                "REG",
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        ListIterator<String> itr = filecodes.listIterator();
        request.getCorrectionDetails()
                .forEach(birth -> {
                    if((birth.getApplicationStatus() == "APPROVED" && birth.getAction() == "APPROVE")) {
                        birth.setRegistrationNo(itr.next());
                        birth.setRegistrationDate(currentTime);
                    }
                });
    }
    private void setPresentAddress(CorrectionRequest request) {

        request.getCorrectionDetails()
                .forEach(birth -> {
                    if (birth.getParentAddress() != null) {
                        if(birth.getParentAddress() != null) {
                            birth.getParentAddress().setPermanentUuid(UUID.randomUUID().toString());
                            birth.getParentAddress().setPresentUuid(UUID.randomUUID().toString());
                            birth.getParentAddress().setBioAdoptPermanent("BIOLOGICAL");
                            birth.getParentAddress().setBioAdoptPresent("BIOLOGICAL");
                        }

                        if (birth.getParentAddress().getPresentaddressCountry() != null && birth.getParentAddress().getPresentaddressStateName() != null) {
                            if (birth.getParentAddress().getPresentaddressCountry().contains(BirthConstants.COUNTRY_CODE)) {
                                if (birth.getParentAddress().getPresentaddressStateName().contains(BirthConstants.STATE_CODE_SMALL)) {

                                    birth.getParentAddress().setCountryIdPresent(birth.getParentAddress().getPresentaddressCountry());

                                    birth.getParentAddress().setStateIdPresent(birth.getParentAddress().getPresentaddressStateName());

                                    birth.getParentAddress().setDistrictIdPresent(birth.getParentAddress().getPresentInsideKeralaDistrict());

                                    birth.getParentAddress().setLocalityEnPresent(birth.getParentAddress().getPresentInsideKeralaLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPresent(birth.getParentAddress().getPresentInsideKeralaLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPresent(birth.getParentAddress().getPresentInsideKeralaStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPresent(birth.getParentAddress().getPresentInsideKeralaStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPresent(birth.getParentAddress().getPresentInsideKeralaHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPresent(birth.getParentAddress().getPresentInsideKeralaHouseNameMl());

                                    birth.getParentAddress().setPinNoPresent(birth.getParentAddress().getPresentInsideKeralaPincode());
                                    birth.getParentAddress().setTownOrVillagePresent(birth.getParentAddress().getPresentOutsideKeralaCityVilgeEn());

                                } else {
                                    birth.getParentAddress().setCountryIdPresent(birth.getParentAddress().getPresentaddressCountry());

                                    birth.getParentAddress().setStateIdPresent(birth.getParentAddress().getPresentaddressStateName());

                                    birth.getParentAddress().setDistrictIdPresent(birth.getParentAddress().getPresentOutsideKeralaDistrict());

                                    birth.getParentAddress().setLocalityEnPresent(birth.getParentAddress().getPresentOutsideKeralaLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPresent(birth.getParentAddress().getPresentOutsideKeralaLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPresent(birth.getParentAddress().getPresentOutsideKeralaStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPresent(birth.getParentAddress().getPresentOutsideKeralaStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPresent(birth.getParentAddress().getPresentOutsideKeralaHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPresent(birth.getParentAddress().getPresentOutsideKeralaHouseNameMl());

                                    birth.getParentAddress().setVillageNamePresent(birth.getParentAddress().getPresentOutSideIndiaadrsVillage());

                                    birth.getParentAddress().setTownOrVillagePresent(birth.getParentAddress().getPresentOutsideKeralaCityVilgeEn());

                                }
                            } else {
                                if (birth.getParentAddress().getPresentOutSideCountry() != null) {
                                    birth.getParentAddress().setCountryIdPresent(birth.getParentAddress().getPresentOutSideCountry());
                                    birth.getParentAddress().setVillageNamePresent(birth.getParentAddress().getPresentOutSideIndiaadrsVillage());
                                    birth.getParentAddress().setTownOrVillagePresent(birth.getParentAddress().getPresentOutSideIndiaadrsCityTown());
                                }
                            }
                        }
                    }
                });
    }
    private void setPermanentAddress(CorrectionRequest request) {
        request.getCorrectionDetails()
                .forEach(birth -> {
                    if (birth.getParentAddress() != null && birth.getParentAddress().getIsPrsentAddress() != null)  {
                        birth.getParentAddress().setIsPrsentAddressInt(birth.getParentAddress().getIsPrsentAddress() == true ? 1 : 0);
                        if(birth.getParentAddress().getIsPrsentAddress()){
                            birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getCountryIdPresent());
                            birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getStateIdPresent());
                            if(birth.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)){
                                birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPresent());
                                if (birth.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
                                    birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
                                }
                            }
                        }
                        if (birth.getParentAddress().getPermtaddressCountry() != null && birth.getParentAddress().getPermtaddressStateName() != null) {
                            if (birth.getParentAddress().getPermtaddressCountry().contains(BirthConstants.COUNTRY_CODE)) {
                                if (birth.getParentAddress().getPermtaddressStateName().contains(BirthConstants.STATE_CODE_SMALL)) {
                                    if(!birth.getParentAddress().getIsPrsentAddress()){
                                        birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermtaddressCountry());
                                        birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getPermtaddressStateName());
                                    }

                                    birth.getParentAddress().setDistrictIdPermanent(birth.getParentAddress().getPermntInKeralaAdrDistrict());

                                    birth.getParentAddress().setLocalityEnPermanent(birth.getParentAddress().getPermntInKeralaAdrLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPermanent(birth.getParentAddress().getPermntInKeralaAdrLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPermanent(birth.getParentAddress().getPermntInKeralaAdrStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPermanent(birth.getParentAddress().getPermntInKeralaAdrStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPermanent(birth.getParentAddress().getPermntInKeralaAdrHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPermanent(birth.getParentAddress().getPermntInKeralaAdrHouseNameMl());

                                    birth.getParentAddress().setPinNoPermanent(birth.getParentAddress().getPermntInKeralaAdrPincode());
                                    birth.getParentAddress().setPoNoPermanent(birth.getParentAddress().getPermntInKeralaAdrPostOffice());
                                    birth.getParentAddress().setTownOrVillagePermanent(birth.getParentAddress().getPermntOutsideKeralaCityVilgeEn());
                                } else {
                                    if(!birth.getParentAddress().getIsPrsentAddress()){
                                        birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermtaddressCountry());
                                        birth.getParentAddress().setStateIdPermanent(birth.getParentAddress().getPermtaddressStateName());
                                    }

                                    birth.getParentAddress().setDistrictIdPermanent(birth.getParentAddress().getPermntOutsideKeralaDistrict());

                                    birth.getParentAddress().setLocalityEnPermanent(birth.getParentAddress().getPermntOutsideKeralaLocalityNameEn());
                                    birth.getParentAddress().setLocalityMlPermanent(birth.getParentAddress().getPermntOutsideKeralaLocalityNameMl());

                                    birth.getParentAddress().setStreetNameEnPermanent(birth.getParentAddress().getPermntOutsideKeralaStreetNameEn());
                                    birth.getParentAddress().setStreetNameMlPermanent(birth.getParentAddress().getPermntOutsideKeralaStreetNameMl());

                                    birth.getParentAddress().setHouseNameNoEnPermanent(birth.getParentAddress().getPermntOutsideKeralaHouseNameEn());
                                    birth.getParentAddress().setHouseNameNoMlPermanent(birth.getParentAddress().getPermntOutsideKeralaHouseNameMl());

                                    birth.getParentAddress().setPinNoPermanent(birth.getParentAddress().getPermntOutsideKeralaPincode());

                                    birth.getParentAddress().setVillageNamePermanent(birth.getParentAddress().getPermntOutsideKeralaVillage());
                                    birth.getParentAddress().setTownOrVillagePermanent(birth.getParentAddress().getPermntOutsideKeralaCityVilgeEn());

                                }
                            } else {
                                if (birth.getParentAddress().getPermntOutsideIndiaCountry() != null) {
                                    birth.getParentAddress().setCountryIdPermanent(birth.getParentAddress().getPermntOutsideIndiaCountry());
                                    birth.getParentAddress().setVillageNamePermanent(birth.getParentAddress().getPermntOutsideIndiaVillage());
                                    birth.getParentAddress().setTownOrVillagePermanent(birth.getParentAddress().getPermntOutsideIndiaCityTown());

                                }
                            }
                        }
                    }
                });
    }
    private void setPlaceOfBirth(CorrectionRequest request, String trnantId, AuditDetails auditDetails) {
        Object mdmsData = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), trnantId);
        Object mdmsDataComm = mdmsUtil.mdmsCall(request.getRequestInfo());
        request.getCorrectionDetails().forEach(birth -> {
            birth.setId(UUID.randomUUID().toString());
            birth.setAuditDetails(auditDetails);
            if(birth.getPlaceofBirthId() != null || !birth.getPlaceofBirthId().isEmpty()){
                mdmsBirthService.setLocationDetails(birth, mdmsData);
                mdmsBirthService.setInstitutionDetails(birth, mdmsDataComm);
            }
            birth.setBirthPlaceUuid(UUID.randomUUID().toString());
        });
    }

    private List<String> getIds(RequestInfo requestInfo, String tenantId, String idName, String moduleCode, String  fnType, int count) {
        return idGenRepository.getIdList(requestInfo, tenantId, idName, moduleCode, fnType, count);
    }
    private void validateFileCodes(List<String> fileCodes, int count) {
        if (CollectionUtils.isEmpty(fileCodes)) {
            throw new CustomException(ErrorCodes.IDGEN_ERROR.getCode(), "No file code(s) returned from idgen service");
        }

        if (fileCodes.size() != count) {
            throw new CustomException(ErrorCodes.IDGEN_ERROR.getCode(),
                    "The number of file code(s) returned by idgen service is not equal to the request count");
        }
    }
}
