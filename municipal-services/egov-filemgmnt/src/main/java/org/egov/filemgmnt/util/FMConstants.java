package org.egov.filemgmnt.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class FMConstants {

	
	   // MDMS

    public static final String TRADE_LICENSE_MODULE = "FileManagement";

    public static final String TRADE_LICENSE_MODULE_CODE = "TL";

    public static final String COMMON_MASTERS_MODULE = "common-masters";
    
    // mdms master names

    public static final String FILE_SERVICE_SUBTYPE = "FileServiceSubtype";

    // mdms path codes

    public static final String FM_JSONPATH_CODE = "$.MdmsRes.FileManagement";
    
    public static final String COMMON_MASTER_JSONPATH_CODE = "$.MdmsRes.common-masters";
    
    // error constants

    public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";

    public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenentID";


}
