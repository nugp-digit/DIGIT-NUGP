Insert into eg_roleaction values((select id from eg_role where name='Financial Report Viewer'),(select id from eg_action where name='ViewReceipts'));
