CREATE TABLE IF NOT EXISTS public.eg_birth_details_audit
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    dateofreport bigint,
    dateofbirth bigint,
    timeofbirth bigint,
    am_pm character varying(20) COLLATE pg_catalog."default",
    firstname_en character varying(200) COLLATE pg_catalog."default",
    firstname_ml character varying(200) COLLATE pg_catalog."default",
    middlename_en character varying(200) COLLATE pg_catalog."default",
    middlename_ml character varying(200) COLLATE pg_catalog."default",
    lastname_en character varying(200) COLLATE pg_catalog."default",
    lastname_ml character varying(200) COLLATE pg_catalog."default",
    tenantid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    gender character varying(20) COLLATE pg_catalog."default",
    remarks_en character varying(2500) COLLATE pg_catalog."default",
    remarks_ml character varying(2500) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    esign_user_code character varying(64) COLLATE pg_catalog."default",
    esign_user_desig_code character varying(64) COLLATE pg_catalog."default",
    is_adopted boolean,
    is_abandoned boolean,
    is_multiple_birth boolean,
    is_father_info_missing boolean,
    is_mother_info_missing boolean,
    no_of_alive_birth integer,
    multiplebirthdetid character varying(64) COLLATE pg_catalog."default",
    is_born_outside boolean,
    ot_passportno character varying(100) COLLATE pg_catalog."default",
    ot_dateofarrival bigint,
    applicationtype character varying(64) COLLATE pg_catalog."default" NOT NULL,
    businessservice character varying(64) COLLATE pg_catalog."default" NOT NULL,
    workflowcode character varying(64) COLLATE pg_catalog."default" NOT NULL,
    fm_fileno character varying(64) COLLATE pg_catalog."default",
    file_date bigint,
    file_status character varying(64) COLLATE pg_catalog."default",
    applicationno character varying(64) COLLATE pg_catalog."default",
    registrationno character varying(64) COLLATE pg_catalog."default",
    registration_date bigint,
    action character varying(64) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    adopt_deed_order_no character varying(64) COLLATE pg_catalog."default",
    adopt_dateoforder_deed bigint,
    adopt_issuing_auththority character varying(64) COLLATE pg_catalog."default",
    adopt_has_agency boolean,
    adopt_agency_name character varying(2000) COLLATE pg_catalog."default",
    adopt_agency_address character varying(5000) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    is_stillbirth boolean,
    adopt_decree_order_no character varying(64) COLLATE pg_catalog."default",
    adopt_dateoforder_decree bigint,
    adopt_agency_contact_person character varying(64) COLLATE pg_catalog."default",
    adopt_agency_contact_person_mobileno character varying(12) COLLATE pg_catalog."default",
    old_birth_reg_no character varying(64) COLLATE pg_catalog."default",
    nac_order_of_child integer,
    application_sub_type character varying(1000) COLLATE pg_catalog."default",
    has_payment boolean,
    is_payment_success boolean,
    amount numeric,
    birthdate date,
    assignee character varying(128) COLLATE pg_catalog."default",
    rdo_proceedings_no character varying(250) COLLATE pg_catalog."default",
    nac_registration_no character varying(250) COLLATE pg_catalog."default"
    );
CREATE TABLE IF NOT EXISTS public.eg_birth_father_information_audit
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    firstname_en character varying(1000) COLLATE pg_catalog."default",
    firstname_ml character varying(1000) COLLATE pg_catalog."default",
    middlename_en character varying(1000) COLLATE pg_catalog."default",
    middlename_ml character varying(1000) COLLATE pg_catalog."default",
    lastname_en character varying(1000) COLLATE pg_catalog."default",
    lastname_ml character varying(1000) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    ot_passportno character varying(100) COLLATE pg_catalog."default",
    emailid character varying(300) COLLATE pg_catalog."default",
    mobileno character varying(12) COLLATE pg_catalog."default",
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    bio_adopt character varying(64) COLLATE pg_catalog."default",
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint
    );
CREATE TABLE IF NOT EXISTS public.eg_birth_initiator_audit (
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    initiator_name character varying(1000) COLLATE pg_catalog."default",
    initiator_institution character varying(64) COLLATE pg_catalog."default",
    initiator_inst_desig character varying(64) COLLATE pg_catalog."default",
    relation character varying(64) COLLATE pg_catalog."default",
    initiator_address character varying(2000) COLLATE pg_catalog."default",
    is_declared boolean,
    declaration_id character varying(64) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    mobileno character varying(12) COLLATE pg_catalog."default",
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    is_care_taker boolean,
    is_esigned boolean,
    initiator character varying(128) COLLATE pg_catalog."default",
    isguardian boolean
    );

CREATE TABLE IF NOT EXISTS public.eg_birth_mother_information_audit (
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    firstname_en character varying(200) COLLATE pg_catalog."default",
    firstname_ml character varying(200) COLLATE pg_catalog."default",
    middlename_en character varying(200) COLLATE pg_catalog."default",
    middlename_ml character varying(200) COLLATE pg_catalog."default",
    lastname_en character varying(200) COLLATE pg_catalog."default",
    lastname_ml character varying(200) COLLATE pg_catalog."default",
    aadharno character varying(150) COLLATE pg_catalog."default",
    ot_passportno character varying(100) COLLATE pg_catalog."default",
    emailid character varying(300) COLLATE pg_catalog."default",
    mobileno character varying(150) COLLATE pg_catalog."default",
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    bio_adopt character varying(64) COLLATE pg_catalog."default",
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    addressofmother character varying(2500) COLLATE pg_catalog."default");

CREATE TABLE IF NOT EXISTS public.eg_birth_permanent_address_audit (
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    resdnce_addr_type character varying(64) COLLATE pg_catalog."default",
    buildingno character varying(200) COLLATE pg_catalog."default",
    houseno character varying(200) COLLATE pg_catalog."default",
    res_asso_no character varying(250) COLLATE pg_catalog."default",
    res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
    housename_no_en character varying(2500) COLLATE pg_catalog."default",
    housename_no_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    ot_zipcode character varying(10) COLLATE pg_catalog."default",
    villageid character varying(64) COLLATE pg_catalog."default",
    village_name character varying(1000) COLLATE pg_catalog."default",
    tenantid character varying(64) COLLATE pg_catalog."default",
    talukid character varying(64) COLLATE pg_catalog."default",
    taluk_name character varying(1000) COLLATE pg_catalog."default",
    ward_code character varying(64) COLLATE pg_catalog."default",
    doorno integer,
    subno character varying(10) COLLATE pg_catalog."default",
    locality_en character varying(1000) COLLATE pg_catalog."default",
    locality_ml character varying(1000) COLLATE pg_catalog."default",
    street_name_en character varying(2000) COLLATE pg_catalog."default",
    street_name_ml character varying(2000) COLLATE pg_catalog."default",
    districtid character varying(64) COLLATE pg_catalog."default",
    stateid character varying(64) COLLATE pg_catalog."default",
    poid character varying(64) COLLATE pg_catalog."default",
    pinno character varying(10) COLLATE pg_catalog."default",
    countryid character varying(64) COLLATE pg_catalog."default",
    birthdtlid character varying(64) COLLATE pg_catalog."default",
    bio_adopt character varying(64) COLLATE pg_catalog."default",
    same_as_present integer,
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    family_emailid character varying(300) COLLATE pg_catalog."default",
    family_mobileno character varying(20) COLLATE pg_catalog."default",
    postoffice_en character varying(1000) COLLATE pg_catalog."default",
    postoffice_ml character varying(1000) COLLATE pg_catalog."default");

CREATE TABLE IF NOT EXISTS public.eg_birth_place_audit (
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    placeofbirthid character varying(64) COLLATE pg_catalog."default",
    hospitalid character varying(64) COLLATE pg_catalog."default",
    public_place_id character varying(64) COLLATE pg_catalog."default",
    institution_type_id character varying(64) COLLATE pg_catalog."default",
    institution_id character varying(64) COLLATE pg_catalog."default",
    vehicletypeid character varying(64) COLLATE pg_catalog."default",
    vehicle_registration_no character varying(64) COLLATE pg_catalog."default",
    vehicle_from_en character varying(1000) COLLATE pg_catalog."default",
    vehicle_to_en character varying(1000) COLLATE pg_catalog."default",
    vehicle_from_ml character varying(1000) COLLATE pg_catalog."default",
    vehicle_to_ml character varying(1000) COLLATE pg_catalog."default",
    vehicle_other_en character varying(1000) COLLATE pg_catalog."default",
    vehicle_other_ml character varying(1000) COLLATE pg_catalog."default",
    vehicle_admit_hospital_en character varying(1000) COLLATE pg_catalog."default",
    vehicle_admit_hospital_ml character varying(1000) COLLATE pg_catalog."default",
    ho_householder_en character varying(2000) COLLATE pg_catalog."default",
    ho_householder_ml character varying(2000) COLLATE pg_catalog."default",
    ho_buildingno character varying(200) COLLATE pg_catalog."default",
    ho_houseno character varying(200) COLLATE pg_catalog."default",
    ho_res_asso_no character varying(200) COLLATE pg_catalog."default",
    ho_res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
    ho_locality_en character varying(1000) COLLATE pg_catalog."default",
    ho_locality_ml character varying(1000) COLLATE pg_catalog."default",
    ho_street_name_en character varying(2000) COLLATE pg_catalog."default",
    ho_street_name_ml character varying(2000) COLLATE pg_catalog."default",
    ho_doorno integer,
    ho_subno character varying(10) COLLATE pg_catalog."default",
    ho_housename_en character varying(2000) COLLATE pg_catalog."default",
    ho_housename_ml character varying(2000) COLLATE pg_catalog."default",
    ho_villageid character varying(64) COLLATE pg_catalog."default",
    ho_talukid character varying(64) COLLATE pg_catalog."default",
    ho_districtid character varying(64) COLLATE pg_catalog."default",
    ho_stateid character varying(64) COLLATE pg_catalog."default",
    ho_poid character varying(2000) COLLATE pg_catalog."default",
    ho_pinno character varying(10) COLLATE pg_catalog."default",
    ho_countryid character varying(64) COLLATE pg_catalog."default",
    ward_id character varying(64) COLLATE pg_catalog."default",
    oth_details_en character varying(2000) COLLATE pg_catalog."default",
    oth_details_ml character varying(2000) COLLATE pg_catalog."default",
    auth_officer_id character varying(64) COLLATE pg_catalog."default",
    auth_officer_desig_id character varying(64) COLLATE pg_catalog."default",
    oth_auth_officer_name character varying(2000) COLLATE pg_catalog."default",
    oth_auth_officer_desig character varying(2000) COLLATE pg_catalog."default",
    informantsaddress_en character varying(2500) COLLATE pg_catalog."default",
    informantsaddress_ml character varying(2500) COLLATE pg_catalog."default",
    informants_mobileno character varying(12) COLLATE pg_catalog."default",
    informants_aadhaar_no character varying(20) COLLATE pg_catalog."default",
    is_born_outside boolean,
    vehicle_haltplace_en character varying(1000) COLLATE pg_catalog."default",
    vehicle_hospitalid character varying(64) COLLATE pg_catalog."default",
    informant_addressline2 character varying(1000) COLLATE pg_catalog."default",
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    vehicle_haltplace_ml character varying(1000) COLLATE pg_catalog."default",
    vehicle_desc character varying(1000) COLLATE pg_catalog."default",
    public_place_desc character varying(2000) COLLATE pg_catalog."default",
    public_locality_en character varying(1000) COLLATE pg_catalog."default",
    public_locality_ml character varying(1000) COLLATE pg_catalog."default",
    public_street_name_en character varying(1000) COLLATE pg_catalog."default",
    public_street_name_ml character varying(1000) COLLATE pg_catalog."default",
    ot_birth_place_en character varying(2500) COLLATE pg_catalog."default",
    ot_birth_place_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    ot_zipcode character varying(10) COLLATE pg_catalog."default",
    is_inform_declare boolean,
    relation character varying(1000) COLLATE pg_catalog."default",
    informantsname_en character varying(1000) COLLATE pg_catalog."default",
    informants_office_name character varying(2500) COLLATE pg_catalog."default",
    ot_country character varying(200) COLLATE pg_catalog."default",
    ot_town_village_en character varying(1000) COLLATE pg_catalog."default",
    ot_town_village_ml character varying(1000) COLLATE pg_catalog."default");

CREATE TABLE IF NOT EXISTS public.eg_birth_present_address_audit (
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    resdnce_addr_type character varying(64) COLLATE pg_catalog."default",
    buildingno character varying(200) COLLATE pg_catalog."default",
    houseno character varying(200) COLLATE pg_catalog."default",
    res_asso_no character varying(250) COLLATE pg_catalog."default",
    res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
    housename_no_en character varying(2500) COLLATE pg_catalog."default",
    housename_no_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    ot_zipcode character varying(10) COLLATE pg_catalog."default",
    villageid character varying(64) COLLATE pg_catalog."default",
    village_name character varying(1000) COLLATE pg_catalog."default",
    tenantid character varying(64) COLLATE pg_catalog."default",
    talukid character varying(64) COLLATE pg_catalog."default",
    taluk_name character varying(1000) COLLATE pg_catalog."default",
    ward_code character varying(64) COLLATE pg_catalog."default",
    doorno integer,
    subno character varying(10) COLLATE pg_catalog."default",
    locality_en character varying(1000) COLLATE pg_catalog."default",
    locality_ml character varying(1000) COLLATE pg_catalog."default",
    street_name_en character varying(2000) COLLATE pg_catalog."default",
    street_name_ml character varying(2000) COLLATE pg_catalog."default",
    districtid character varying(64) COLLATE pg_catalog."default",
    stateid character varying(64) COLLATE pg_catalog."default",
    poid character varying(64) COLLATE pg_catalog."default",
    pinno character varying(10) COLLATE pg_catalog."default",
    countryid character varying(64) COLLATE pg_catalog."default",
    birthdtlid character varying(64) COLLATE pg_catalog."default",
    bio_adopt character varying(64) COLLATE pg_catalog."default",
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    postoffice_en character varying(1000) COLLATE pg_catalog."default",
    postoffice_ml character varying(1000) COLLATE pg_catalog."default");

CREATE TABLE IF NOT EXISTS public.eg_birth_statitical_information_audit (
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    weight_of_child double precision,
    duration_of_pregnancy_in_week integer,
    nature_of_medical_attention character varying(64) COLLATE pg_catalog."default",
    way_of_pregnancy character varying(64) COLLATE pg_catalog."default",
    delivery_method character varying(64) COLLATE pg_catalog."default",
    deliverytypeothers_en character varying(1000) COLLATE pg_catalog."default",
    deliverytypeothers_ml character varying(1000) COLLATE pg_catalog."default",
    religionid character varying(64) COLLATE pg_catalog."default",
    father_nationalityid character varying(64) COLLATE pg_catalog."default",
    father_educationid character varying(64) COLLATE pg_catalog."default",
    father_education_subid character varying(64) COLLATE pg_catalog."default",
    father_proffessionid character varying(64) COLLATE pg_catalog."default",
    mother_educationid character varying(64) COLLATE pg_catalog."default",
    mother_education_subid character varying(64) COLLATE pg_catalog."default",
    mother_proffessionid character varying(64) COLLATE pg_catalog."default",
    mother_nationalityid character varying(64) COLLATE pg_catalog."default",
    mother_age_marriage integer,
    mother_age_delivery integer,
    mother_no_of_birth_given integer,
    mother_maritalstatusid character varying(45) COLLATE pg_catalog."default",
    mother_unmarried smallint,
    mother_res_lbid integer,
    mother_res_lb_code_id numeric,
    mother_res_place_type_id integer,
    mother_res_lb_type_id integer,
    mother_res_district_id integer,
    mother_res_state_id integer,
    mother_res_country_id numeric,
    mother_resdnce_addr_type character varying(64) COLLATE pg_catalog."default",
    mother_resdnce_tenant character varying(64) COLLATE pg_catalog."default",
    mother_resdnce_placetype character varying(64) COLLATE pg_catalog."default",
    mother_resdnce_place_en character varying(2500) COLLATE pg_catalog."default",
    mother_resdnce_place_ml character varying(2500) COLLATE pg_catalog."default",
    mother_resdnce_lbtype character varying(64) COLLATE pg_catalog."default",
    mother_resdnce_district character varying(64) COLLATE pg_catalog."default",
    mother_resdnce_state character varying(64) COLLATE pg_catalog."default",
    mother_resdnce_country character varying(64) COLLATE pg_catalog."default",
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    mother_order_of_cur_delivery integer,
    mother_order_cur_child integer,
    mother_res_no_of_years integer,
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    cause_of_foetal_death character varying(64) COLLATE pg_catalog."default");

CREATE TABLE IF NOT EXISTS public.eg_nac_certificate_request_audit (
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    registrationno character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    registrydetailsid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(256) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    additionaldetail jsonb,
    embeddedurl character varying(64) COLLATE pg_catalog."default",
    dateofissue bigint,
    tenantid character varying(64) COLLATE pg_catalog."default",
    certificateno character varying(64) COLLATE pg_catalog."default",
    applicationid character varying(64) COLLATE pg_catalog."default",
    ack_no character varying(64) COLLATE pg_catalog."default");

CREATE TABLE IF NOT EXISTS public.eg_birth_application_document_audit (
    id character varying(128) COLLATE pg_catalog."default",
    tenantid character varying(128) COLLATE pg_catalog."default",
    document_name character varying(128) COLLATE pg_catalog."default",
    document_type character varying(128) COLLATE pg_catalog."default" NOT NULL,
    document_description character varying(140) COLLATE pg_catalog."default",
    filestoreid character varying(1024) COLLATE pg_catalog."default",
    document_link character varying(1024) COLLATE pg_catalog."default",
    file_type character varying(20) COLLATE pg_catalog."default",
    file_size bigint,
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    active boolean NOT NULL,
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint );

CREATE TABLE IF NOT EXISTS public.eg_birth_certificate_request_audit (
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    registrationno character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    registrydetailsid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(256) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    additionaldetail jsonb,
    embeddedurl character varying(64) COLLATE pg_catalog."default",
    dateofissue bigint,
    tenantid character varying(64) COLLATE pg_catalog."default",
    certificateno character varying(64) COLLATE pg_catalog."default");



