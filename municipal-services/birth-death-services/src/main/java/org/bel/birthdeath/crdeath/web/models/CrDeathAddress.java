package org.bel.birthdeath.crdeath.web.models;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrDeathAddress {

    @Size(max = 64)
    @JsonProperty("id")
    private String id ;

    @JsonProperty("deathDtlId")
    private String deathDtlId ;

    @JsonProperty("tenantId")
    private String tenantId ;

    @JsonProperty("addrTypeId")
    private String addrTypeId ;

    @JsonProperty("houseNo")
    private String houseNo ;

    @JsonProperty("residenceAsscNo")
    private String residenceAsscNo ;

    @JsonProperty("streetNameEn")
    private String streetNameEn ;

    @JsonProperty("streetNameMl")
    private String streetNameMl;

    @JsonProperty("localityEn")
    private String localityEn ;

    @JsonProperty("localityMl")
    private String localityMl ;

    @JsonProperty("cityEn")
    private String cityEn ;

    @JsonProperty("cityMl")
    private String cityMl ;

    @JsonProperty("wardId")
    private String wardId ;

    @JsonProperty("talukId")
    private String talukId ;

    @JsonProperty("villageId")
    private String villageId ;

    @JsonProperty("postOfficeId")
    private String postOfficeId ;

    @JsonProperty("pincode")
    private long pincode ;

    @JsonProperty("districtId")
    private String districtId ;

    @JsonProperty("stateId")
    private String stateId ;

    @JsonProperty("countryId")
    private String countryId ;

    @JsonProperty("talukNameEn")
    private String talukNameEn ;

    @JsonProperty("talukNameMl")
    private String talukNameMl ;

    @JsonProperty("villageNameEn")
    private String villageNameEn ;

    @JsonProperty("villageNameMl")
    private String villageNameMl ;

    @JsonProperty("postofficeNameEn")
    private String postofficeNameEn ;

    @JsonProperty("postofficeNameMl")
    private String postofficeNameMl ;

    
}
