package org.ksmart.birth.newbirth.enrichment;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.newbirth.service.MdmsForNewBirthService;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Component
public class NewBirthResponseEnrichment {

    private final MdmsUtil mdmsUtil;

    private final MdmsForNewBirthService mdmsBirthService;
    private final MdmsTenantService mdmsTenantService;
    @Autowired
    NewBirthResponseEnrichment(MdmsTenantService mdmsTenantService, MdmsForNewBirthService mdmsBirthService, MdmsUtil mdmsUtil) {
        this.mdmsTenantService = mdmsTenantService;
        this.mdmsBirthService = mdmsBirthService;
        this.mdmsUtil = mdmsUtil;
    }
    public void setNewBirthRequestData(RequestInfo requestInfo, List<NewBirthApplication> result) {
        Object mdmsData = mdmsUtil.mdmsCall(requestInfo);
        if(result.size() == 0){
            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
        } else if(result.size() >= 1) {
            result.forEach(birth -> {
                birth.setIsWorkflow(true);
                Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(requestInfo, birth.getTenantId());
                if (birth.getPlaceofBirthId() != null) {
                    mdmsBirthService.setLocationDetails(birth, mdmsDataLoc, mdmsData);
                    mdmsBirthService.setParentsDetails(birth.getParentsDetails(), mdmsData);
                }
                if (birth.getParentAddress().getCountryIdPermanent() != null && birth.getParentAddress().getStateIdPermanent() != null) {
                    if (birth.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)) {
                        if (birth.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
                            mdmsBirthService.setTenantDetails(birth, mdmsData);
                            //Country
                            birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());
                            birth.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
                            birth.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPermanent()));

                            //State
                            birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
                            birth.getParentAddress().setStateIdPermanentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPermanent()));
                            birth.getParentAddress().setStateIdPermanentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPermanent()));

                            //District
                            birth.getParentAddress().setPermntInKeralaAdrDistrict(birth.getParentAddress().getDistrictIdPermanent());
                            birth.getParentAddress().setDistrictIdPermanentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));
                            birth.getParentAddress().setDistrictIdPermanentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));

                            //Local Body
                            birth.getParentAddress().setPermntInKeralaAdrLBNameEn(mdmsTenantService.getTenantNameEn(mdmsData, birth.getParentAddress().getPermntInKeralaAdrLBName()));
                            birth.getParentAddress().setPermntInKeralaAdrLBNameMl(mdmsTenantService.getTenantNameMl(mdmsData, birth.getParentAddress().getPermntInKeralaAdrLBName()));

                            //Post Office
                            birth.getParentAddress().setPermntInKeralaAdrPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getParentAddress().getPermntInKeralaAdrPostOffice()));
                            birth.getParentAddress().setPermntInKeralaAdrPostOfficeMl(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getParentAddress().getPermntInKeralaAdrPostOffice()));


                            birth.getParentAddress().setPermntInKeralaAdrLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
                            birth.getParentAddress().setPermntInKeralaAdrLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());

                            birth.getParentAddress().setPermntInKeralaAdrStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                            birth.getParentAddress().setPermntInKeralaAdrStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                            birth.getParentAddress().setPermntInKeralaAdrHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
                            birth.getParentAddress().setPermntInKeralaAdrHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());

                            birth.getParentAddress().setPermntInKeralaAdrPostOffice(birth.getParentAddress().getPoNoPermanent());

                        } else {
                            //Country
                            birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());
                            birth.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
                            birth.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPermanent()));

                            //State
                            birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
                            birth.getParentAddress().setStateIdPermanentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPermanent()));
                            birth.getParentAddress().setStateIdPermanentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPermanent()));

                            //District
                            birth.getParentAddress().setPermntOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPermanent());
                            birth.getParentAddress().setDistrictIdPermanentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));
                            birth.getParentAddress().setDistrictIdPermanentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPermanent()));


                            birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());

                            birth.getParentAddress().setPermntOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
                            birth.getParentAddress().setPermntOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());

                            birth.getParentAddress().setPermntOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                            birth.getParentAddress().setPermntOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                            birth.getParentAddress().setPermntOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
                            birth.getParentAddress().setPermntOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());
                        }
                    } else {
                        //Country
                        birth.getParentAddress().setPermntOutsideIndiaCountry(birth.getParentAddress().getCountryIdPermanent());
                        birth.getParentAddress().setCountryIdPermanentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPermanent()));
                        birth.getParentAddress().setCountryIdPermanentMl(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPermanent()));

                        birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());
                    }
                }
                if (birth.getParentAddress().getCountryIdPresent() != null && birth.getParentAddress().getStateIdPresent() != null) {
                    if (birth.getParentAddress().getCountryIdPresent().contains(BirthConstants.COUNTRY_CODE)) {
                        if (birth.getParentAddress().getStateIdPresent().contains(BirthConstants.STATE_CODE_SMALL)) {
                            //Country
                            birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());
                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));

                            //State
                            birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());
                            birth.getParentAddress().setStateIdPresentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPresent()));
                            birth.getParentAddress().setStateIdPresentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPresent()));

                            //District
                            birth.getParentAddress().setPresentInsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());
                            birth.getParentAddress().setDistrictIdPresentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPresent()));
                            birth.getParentAddress().setDistrictIdPresentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPresent()));

                            //Local Body
                            birth.getParentAddress().setPresentInsideKeralaLBNameEn(mdmsTenantService.getTenantNameEn(mdmsData, birth.getParentAddress().getPresentInsideKeralaLBName()));
                            birth.getParentAddress().setPresentInsideKeralaLBNameMl(mdmsTenantService.getTenantNameMl(mdmsData, birth.getParentAddress().getPresentInsideKeralaLBName()));

                            //Post Office
                            birth.getParentAddress().setPresentInsideKeralaPostOfficeEn(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getParentAddress().getPresentInsideKeralaPostOffice()));
                            birth.getParentAddress().setPresentInsideKeralaPostOfficeMl(mdmsTenantService.getPostOfficeNameEn(mdmsData, birth.getParentAddress().getPresentInsideKeralaPostOffice()));

                            birth.getParentAddress().setPresentInsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
                            birth.getParentAddress().setPresentInsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());

                            birth.getParentAddress().setPresentInsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                            birth.getParentAddress().setPresentInsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                            birth.getParentAddress().setPresentInsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
                            birth.getParentAddress().setPresentInsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());

                            birth.getParentAddress().setPresentInsideKeralaPincode(birth.getParentAddress().getPinNoPresent());
                            birth.getParentAddress().setPresentInsideKeralaPostOffice(birth.getParentAddress().getPresentInsideKeralaPostOffice());

                        } else {
                            //Country
                            birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());
                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
                            birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));

                            //State
                            birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());
                            birth.getParentAddress().setStateIdPresentEn(mdmsTenantService.getStateNameEn(mdmsData, birth.getParentAddress().getStateIdPresent()));
                            birth.getParentAddress().setStateIdPresentMl(mdmsTenantService.getStateNameMl(mdmsData, birth.getParentAddress().getStateIdPresent()));

                            //District
                            birth.getParentAddress().setPresentOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());
                            birth.getParentAddress().setDistrictIdPresentEn(mdmsTenantService.getDistrictNameEn(mdmsData, birth.getParentAddress().getDistrictIdPresent()));
                            birth.getParentAddress().setDistrictIdPresentMl(mdmsTenantService.getDistrictNameMl(mdmsData, birth.getParentAddress().getDistrictIdPresent()));

                            birth.getParentAddress().setPresentOutsideKeralaVillageName(birth.getParentAddress().getVillageNamePresent());

                            birth.getParentAddress().setPresentOutsideKeralaPincode(birth.getParentAddress().getPinNoPresent());

                            birth.getParentAddress().setPresentOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
                            birth.getParentAddress().setPresentOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());

                            birth.getParentAddress().setPresentOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPresent());
                            birth.getParentAddress().setPresentOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPresent());

                            birth.getParentAddress().setPresentOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
                            birth.getParentAddress().setPresentOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());

                            birth.getParentAddress().setPresentOutsideKeralaCityVilgeEn(birth.getParentAddress().getTownOrVillagePresent());
                        }
                    } else {
                        //Country
                        birth.getParentAddress().setPresentOutSideCountry(birth.getParentAddress().getCountryIdPresent());
                        birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameEn(mdmsData, birth.getParentAddress().getCountryIdPresent()));
                        birth.getParentAddress().setCountryIdPresentEn(mdmsTenantService.getCountryNameMl(mdmsData, birth.getParentAddress().getCountryIdPresent()));

                        birth.getParentAddress().setPresentOutSideIndiaadrsVillage(birth.getParentAddress().getVillageNamePresent());
                        birth.getParentAddress().setPresentOutSideIndiaadrsCityTown(birth.getParentAddress().getTownOrVillagePresent());
                    }
                }
            });
        }

    }
}
