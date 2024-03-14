CREATE TABLE egf_contractor
(
  id bigint NOT NULL,
  code character varying(50) NOT NULL,
  name character varying(100) NOT NULL,
  correspondenceaddress character varying(250),
  paymentaddress character varying(250),
  contactperson character varying(100),
  email character varying(100),
  narration character varying(1024),
  pannumber character varying(14),
  tinnumber character varying(14),
  bank bigint,
  ifsccode character varying(15),
  bankaccount character varying(22),
  registrationNumber character varying(50),
  status bigint,
  createdby bigint NOT NULL,
  lastmodifiedby bigint,
  createddate timestamp without time zone NOT NULL,
  lastmodifieddate timestamp without time zone,
  version bigint,
  CONSTRAINT pk_contractor PRIMARY KEY (id),
  CONSTRAINT unq_contractor UNIQUE (code),
  CONSTRAINT fk_contractor_bank FOREIGN KEY (bank)
      REFERENCES bank (id)
);

CREATE SEQUENCE seq_egf_contractor;
