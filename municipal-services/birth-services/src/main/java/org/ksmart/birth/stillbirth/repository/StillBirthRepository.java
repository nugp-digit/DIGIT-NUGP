package org.ksmart.birth.stillbirth.repository;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication.RegisterRowMapperForApp;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.stillbirth.enrichment.StillBirthEnrichment;
import org.ksmart.birth.stillbirth.repository.querybuilder.StillBirthQueryBuilder;
import org.ksmart.birth.stillbirth.repository.rowmapper.StillBirthApplicationRowMapper;
import org.ksmart.birth.stillbirth.service.MdmsForStillBirthService;
import org.ksmart.birth.stillbirth.service.StillBirthService;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.stillbirth.StillBirthApplication;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class StillBirthRepository {
    private final BndProducer producer;
    private final StillBirthEnrichment enrichment;
    private final BirthConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final StillBirthQueryBuilder queryBuilder;
    private final StillBirthApplicationRowMapper rowMapper;
    private final  MdmsDataService mdmsDataService;
    private final MdmsForStillBirthService mdmsForStillBirthService;
    private final  MdmsUtil mdmsUtil;

    private final RegisterRowMapperForApp registerRowMapperForApp;
    @Autowired
    StillBirthRepository(JdbcTemplate jdbcTemplate,  StillBirthEnrichment enrichment, BirthConfiguration birthDeathConfiguration,
                         BndProducer producer, StillBirthQueryBuilder queryBuilder, StillBirthApplicationRowMapper rowMapper,
                         MdmsDataService mdmsDataService, MdmsUtil mdmsUtil, MdmsForStillBirthService mdmsForStillBirthService,RegisterRowMapperForApp registerRowMapperForApp) {
        this.jdbcTemplate = jdbcTemplate;
        this.enrichment = enrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.mdmsDataService = mdmsDataService;
        this.mdmsUtil = mdmsUtil;
        this.mdmsForStillBirthService = mdmsForStillBirthService;
        this.registerRowMapperForApp = registerRowMapperForApp;
    }
    public List<StillBirthApplication> saveBirthDetails(StillBirthDetailRequest request) {
        enrichment.enrichCreate(request);
        producer.push(birthDeathConfiguration.getSaveStillBirthTopic(), request);
        return request.getBirthDetails();
    }
    public List<StillBirthApplication> updateBirthDetails(StillBirthDetailRequest request) {
        enrichment.enrichUpdate(request);
        producer.push(birthDeathConfiguration.getUpdateStillBirthTopic(), request);
        return request.getBirthDetails();
    }

    public RegisterBirthDetailsRequest searchStillBirthDetailsForRegister(StillBirthDetailRequest request) {
        List<Object> preparedStmtValues = new ArrayList<>();
        SearchCriteria criteria = new SearchCriteria();
        List<RegisterBirthDetail> result = null;
        if (request.getBirthDetails().size() > 0) {
            criteria.setApplicationNumber(request.getBirthDetails().get(0).getApplicationNo());
            criteria.setTenantId(request.getBirthDetails().get(0).getTenantId());
            String query = queryBuilder.getApplicationSearchQueryForRegistry(criteria, preparedStmtValues);
            result = jdbcTemplate.query(query, preparedStmtValues.toArray(), registerRowMapperForApp);
        }
        return RegisterBirthDetailsRequest.builder()
                .requestInfo(request.getRequestInfo())
                .registerBirthDetails(result).build();
    }
    public List<StillBirthApplication> searchStillBirthDetails(StillBirthDetailRequest request, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();

        Object mdmsDataComm = mdmsUtil.mdmsCall(request.getRequestInfo());
        String query = queryBuilder.getNewBirthApplicationSearchQuery(criteria, request, preparedStmtValues, Boolean.FALSE);
        List<StillBirthApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);
        result.forEach(birth -> {
            Object mdmsData = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), birth.getTenantId());
            if (birth.getPlaceofBirthId() != null) {

                mdmsForStillBirthService.setLocationDetails(birth, mdmsData);
                mdmsForStillBirthService.setInstitutionDetails(birth, mdmsDataComm);
            }
            if (birth.getParentAddress().getCountryIdPermanent() != null && birth.getParentAddress().getStateIdPermanent() != null) {
                if (birth.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)) {
                    if (birth.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {

                        mdmsForStillBirthService.setTenantDetails(birth, mdmsDataComm);
                        birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());

                        birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrDistrict(birth.getParentAddress().getDistrictIdPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
                        birth.getParentAddress().setPermntInKeralaAdrLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                        birth.getParentAddress().setPermntInKeralaAdrStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
                        birth.getParentAddress().setPermntInKeralaAdrHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrPostOffice(birth.getParentAddress().getPoNoPermanent());

                    } else {
                        birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());

                        birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
                        birth.getParentAddress().setPermntOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPermanent());

                        birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());

                        birth.getParentAddress().setPermntOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
                        birth.getParentAddress().setPermntOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());

                        birth.getParentAddress().setPermntOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                        birth.getParentAddress().setPermntOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                        birth.getParentAddress().setPermntOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
                        birth.getParentAddress().setPermntOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());

                    }
                } else {
                    birth.getParentAddress().setPermntOutsideIndiaCountry(birth.getParentAddress().getCountryIdPermanent());
                    birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());
                }
            }
            if (birth.getParentAddress().getCountryIdPresent() != null && birth.getParentAddress().getStateIdPresent() != null) {
                if (birth.getParentAddress().getCountryIdPresent().contains(BirthConstants.COUNTRY_CODE)) {
                    if (birth.getParentAddress().getStateIdPresent().contains(BirthConstants.STATE_CODE_SMALL)) {

                        birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());

                        birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());

                        birth.getParentAddress().setPresentInsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());

                        birth.getParentAddress().setPresentInsideKeralaLBName(birth.getParentAddress().getPermntInKeralaAdrLBName());

                        //birth.getParentAddress().setPresentInsideKeralaVillage(birth.getParentAddress().getVillageNamePresent());

                        birth.getParentAddress().setPresentInsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
                        birth.getParentAddress().setPresentInsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());

                        birth.getParentAddress().setPresentInsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                        birth.getParentAddress().setPresentInsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                        birth.getParentAddress().setPresentInsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
                        birth.getParentAddress().setPresentInsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());

                        birth.getParentAddress().setPresentInsideKeralaPincode(birth.getParentAddress().getPinNoPresent());

                        //birth.getParentAddress().setPresentOutsideKeralaCityVilgeEn(birth.getParentAddress().getTownOrVillagePresent());

                        birth.getParentAddress().setPresentInsideKeralaPostOffice(birth.getParentAddress().getPresentInsideKeralaPostOffice());

                    } else {
                        birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());

                        birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());

                        birth.getParentAddress().setPresentOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());

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
                    birth.getParentAddress().setPresentOutSideCountry(birth.getParentAddress().getCountryIdPresent());
                    birth.getParentAddress().setPresentOutSideIndiaadrsVillage(birth.getParentAddress().getVillageNamePresent());
                    birth.getParentAddress().setPresentOutSideIndiaadrsCityTown(birth.getParentAddress().getTownOrVillagePresent());
                }
            }
        });

        return result;

    }
}

