ALTER TABLE EGWTR_ESTIMATION_DETAILS DROP COLUMN QUANTITY;
ALTER TABLE EGWTR_ESTIMATION_DETAILS ADD COLUMN QUANTITY double precision;
ALTER TABLE EGWTR_ESTIMATION_DETAILS ALTER COLUMN UNITRATE DROP NOT NULL;

ALTER TABLE EGWTR_FIELDINSPECTION_DETAILS ALTER COLUMN DIGGINGCHARGES DROP NOT NULL;
ALTER TABLE EGWTR_FIELDINSPECTION_DETAILS ALTER COLUMN SUPERVISIONCHARGES DROP NOT NULL;

--rollback ALTER TABLE EGWTR_FIELDINSPECTION_DETAILS ALTER COLUMN DIGGINGCHARGES SET NOT NULL;
--rollback ALTER TABLE EGWTR_FIELDINSPECTION_DETAILS ALTER COLUMN SUPERVISIONCHARGES SET NOT NULL;
--rollback ALTER TABLE EGWTR_ESTIMATION_DETAILS ALTER COLUMN UNITRATE SET NOT NULL;
--rollback ALTER TABLE EGWTR_ESTIMATION_DETAILS DROP COLUMN QUANTITY;
--rollback ALTER TABLE EGWTR_ESTIMATION_DETAILS ADD COLUMN QUANTITY double precision NOT NULL;
