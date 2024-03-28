-- alter table  eg_user DROP CONSTRAINT eg_user_user_name_tenant;
alter table  eg_user ADD CONSTRAINT IF NOT EXISTS eg_user_user_name_tenant UNIQUE (username, type, tenantid);

-- ALTER TABLE eg_userrole DROP CONSTRAINT eg_userrole_userid_fkey;
-- alter table  eg_user_address DROP CONSTRAINT eg_user_address_user_fkey;

alter table  eg_userrole ADD CONSTRAINT IF NOT EXISTS eg_userrole_userid_fkey FOREIGN KEY (userid, tenantid) REFERENCES eg_user (id, tenantid) ON UPDATE CASCADE ON DELETE CASCADE;
alter table  eg_user_address ADD CONSTRAINT IF NOT EXISTS eg_user_address_user_fkey FOREIGN KEY (userid, tenantid) REFERENCES eg_user (id, tenantid) ON UPDATE CASCADE ON DELETE CASCADE;