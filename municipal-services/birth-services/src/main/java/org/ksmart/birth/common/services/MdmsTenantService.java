package org.ksmart.birth.common.services;

import com.jayway.jsonpath.JsonPath;
import org.ksmart.birth.utils.BirthConstants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MdmsTenantService {

    private List<String> getTenantCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_CODE_JSONPATH);
    }

    private List<String> getLbTypeCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_LBTYPE_CODE_JSONPATH);
    }

    private List<String> getTalukCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TALUK_CODE_JSONPATH);
    }

    private List<String> getDistrictCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DISTRICT_CODE_JSONPATH);
    }

    private List<String> getStateCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_STATE_CODE_JSONPATH);
    }

    private List<String> getCountryCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_COUNTRY_CODE_JSONPATH);
    }

    private List<String> getPoCodes(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_POSTOFFICE_CODE_JSONPATH);
    }

    private List<String> getInstType(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INST_TYPE_CODE_JSONPATH);
    }

    private List<String> getVehicleType(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_VEHI_TYPE_CODE_JSONPATH);
    }

    private List<String> getPublicPlaceType(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_PUBLIC_PLACE_TYPE_CODE_JSONPATH);
    }

    private List<String> getQualification(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_QUAL_TYPE_CODE_JSONPATH);
    }

    private List<String> getProfession(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_PROF_TYPE_CODE_JSONPATH);
    }

    private List<String> getReligion(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_RELIGION_TYPE_CODE_JSONPATH);
    }

    private List<String> getNationality(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_COUNTRY_CODE_JSONPATH);
    }

    private List<String> getMedicalAttention(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_MEDICAL_ATTENTION_TYPE_CODE_JSONPATH);
    }

    private List<String> getDeliveryMethod(Object mdmsData) {
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DELIVERYMETHOD_CODE_JSONPATH);
    }

    ///Localized value

    public String getTenantNameEn(Object mdmsData, String tenantId) {
        List<String> tenants = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH + "[" + index + "].name");
    }

    public String getTenantNameMl(Object mdmsData, String tenantId) {
        List<String> tenants = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH + "[" + index + "].city.localName");
    }

    public String getTenantLbType(Object mdmsData, String tenantId) {
        List<String> tenants = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH + "[" + index + "].city.lbtypecode");
    }
    public String getTenantTaluk(Object mdmsData, String tenantId) {
        List<String> tenants = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH + "[" + index + "].city.talukcode");
    }
    public String getTenantDistrict(Object mdmsData, String tenantId) {
        List<String> tenants = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH + "[" + index + "].city.distCodeStr");
    }
    public String getTenantState(Object mdmsData, String tenantId) {
        List<String> tenants = getTenantCodes(mdmsData);
        int index = tenants.indexOf(tenantId);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TENANTS_JSONPATH + "[" + index + "].city.statecode");
    }
    public String getLbTypeNameEn(Object mdmsData, String code) {
        List<String> lists = getLbTypeCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_LBTYPE_JSONPATH + "[" + index + "].name");
    }
    public String getPostOfficeNameEn(Object mdmsData, String code) {
        List<String> lists = getPoCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_POSTOFFICE_JSONPATH + "[" + index + "].name");
    }
    public Integer getPostOfficePinCode(Object mdmsData, String code) {
        List<String> lists = getPoCodes(mdmsData);
        Integer index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_POSTOFFICE_JSONPATH + "[" + index + "].pincode");
    }
    public String getTalukNameEn(Object mdmsData, String code) {
        List<String> lists = getTalukCodes(mdmsData);
        Integer index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TALUK_JSONPATH + "[" + index + "].name");
    }
    public String getDistrictNameEn(Object mdmsData, String code) {
        List<String> lists = getDistrictCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DISTRICT_JSONPATH + "[" + index + "].name");
    }
    public String getStateNameEn(Object mdmsData, String code) {
        List<String> lists = getStateCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_STATE_JSONPATH + "[" + index + "].name");
    }
    public String getCountryNameEn(Object mdmsData, String code) {
        List<String> lists = getCountryCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_COUNTRY_JSONPATH + "[" + index + "].name");
    }
    public String getLbTypeNameMl(Object mdmsData, String code) {
        List<String> lists = getLbTypeCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_LBTYPE_JSONPATH + "[" + index + "].namelocal");
    }
    public String getPostOfficeNameMl(Object mdmsData, String code) {
        List<String> lists = getTalukCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_POSTOFFICE_JSONPATH + "[" + index + "].namelocal");
    }
    public String getTalukNameMl(Object mdmsData, String code) {
        List<String> lists = getTalukCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_TALUK_JSONPATH + "[" + index + "].namelocal");
    }
    public String getDistrictNameMl(Object mdmsData, String code) {
        List<String> lists = getDistrictCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DISTRICT_JSONPATH + "[" + index + "].namelocal");
    }
    public String getStateNameMl(Object mdmsData, String code) {
        List<String> lists = getStateCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_STATE_JSONPATH + "[" + index + "].namelocal");
    }
    public String getCountryNameMl(Object mdmsData, String code) {
        List<String> lists = getCountryCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_COUNTRY_JSONPATH + "[" + index + "].namelocal");
    }
    public String getInstitutionTypeNameEn(Object mdmsData, String code) {
        List<String> lists = getInstType(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INST_TYPE_JSONPATH + "[" + index + "].name");
    }
    public String getInstitutionTypeNameMl(Object mdmsData, String code) {
        List<String> lists = getInstType(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_INST_TYPE_JSONPATH + "[" + index + "].namelocal");
    }
    public String getVehicleTypeEn(Object mdmsData, String code) {
        List<String> lists = getVehicleType(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_VEHI_TYPE_JSONPATH + "[" + index + "].name");
    }
    public String getVehicleTypeMl(Object mdmsData, String code) {
        List<String> lists = getVehicleType(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_VEHI_TYPE_JSONPATH + "[" + index + "].namelocal");
    }
    public String getPublicPlaceTypeEn(Object mdmsData, String code) {
        List<String> lists = getPublicPlaceType(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_PUBLIC_PLACE_TYPE_JSONPATH + "[" + index + "].name");
    }
    public String getPublicPlaceTypeMl(Object mdmsData, String code) {
        List<String> lists = getPublicPlaceType(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_PUBLIC_PLACE_TYPE_JSONPATH + "[" + index + "].namelocal");
    }
    public String getQualificatioinEn(Object mdmsData, String code) {
        List<String> lists = getQualification(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_QUAL_TYPE_JSONPATH + "[" + index + "].name");
    }
    public String getQualificatioinMl(Object mdmsData, String code) {
        List<String> lists = getQualification(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_QUAL_TYPE_JSONPATH + "[" + index + "].namelocal");
    }
    public String getProfessionEn(Object mdmsData, String code) {
        List<String> lists = getProfession(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_PROF_TYPE_JSONPATH + "[" + index + "].name");
    }
    public String getProfessionMl(Object mdmsData, String code) {
        List<String> lists = getProfession(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_PROF_TYPE_JSONPATH + "[" + index + "].namelocal");
    }
    public String getNationalityEn(Object mdmsData, String code) {
        List<String> lists = getCountryCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_COUNTRY_JSONPATH + "[" + index + "].nationalityname");
    }
    public String getNationalityMl(Object mdmsData, String code) {
        List<String> lists = getCountryCodes(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_COUNTRY_JSONPATH + "[" + index + "].nationalitynamelocal");
    }
    public String getReligionEn(Object mdmsData, String code) {
        List<String> lists = getReligion(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_RELIGION_JSONPATH + "[" + index + "].name");
    }
    public String getReligionMl(Object mdmsData, String code) {
        List<String> lists = getReligion(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_RELIGION_JSONPATH + "[" + index + "].namelocal");
    }
    public String getMedicalAttentionEn(Object mdmsData, String code) {
        List<String> lists = getMedicalAttention(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_MEDICAL_ATTENTION_TYPE_JSONPATH + "[" + index + "].name");
    }
    public String getMedicalAttentionMl(Object mdmsData, String code) {
        List<String> lists = getMedicalAttention(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_MEDICAL_ATTENTION_TYPE_JSONPATH + "[" + index + "].namelocal");
    }
    public String getDeliveryMethodEn(Object mdmsData, String code) {
        List<String> lists = getDeliveryMethod(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DELIVERY_METHOD_JSONPATH + "[" + index + "].name");
    }
    public String getDeliveryMethodMl(Object mdmsData, String code) {
        List<String> lists = getDeliveryMethod(mdmsData);
        int index = lists.indexOf(code);
        return JsonPath.read(mdmsData, BirthConstants.CR_MDMS_DELIVERY_METHOD_JSONPATH + "[" + index + "].namelocal");
    }
}
