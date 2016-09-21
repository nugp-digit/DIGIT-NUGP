/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2016>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.council.utils.constants; 

import java.util.LinkedHashMap;

public class CouncilConstants {
    
    public static final String CREATED = "CREATED";
    public static final String MODULE_NAME = "COUNCIL";
    public static final String MODULE_FULLNAME ="Council Management";
    public static final String COUNCILMEETING="COUNCILMEETING";
    public final static String PREAMBLE_STATUS_CREATED="CREATED";
    public final static String PREAMBLE_STATUS_APPROVED="APPROVED";
    public final static String PREAMBLE_STATUS_ADJOURNED="ADJOURNED";
    public final static String PREAMBLE_MODULENAME = "COUNCILPREAMBLE";
    public final static String AGENDA_STATUS_APPROVED ="APPROVED";
    public final static String AGENDA_STATUS_INWORKFLOW ="INWORKFLOW";
    public final static String COUNCIL_RESOLUTION ="COUNCILRESOLUTION";	
    public final static String MOM_FINALISED="MOM FINALISED";
    public static final String REVENUE_HIERARCHY_TYPE = "ADMINISTRATION";
    public static final String WARD = "Ward";
    
    public final static String AGENDA_MODULENAME = "COUNCILAGENDA";
    public final static String MEETING_MODULENAME = "COUNCILMEETING";
    public static final String PREAMBLEUSEDINAGENDA = "PREAMBLE USED IN AGENDA";
    public static final String AGENDAUSEDINMEETING = "AGENDA USED IN MEETING";
    public static final String MEETINGUSEDINRMOM = "MOM CREATED";
    public static final String MOM_STATUS_APPROVED ="APPROVED";
    public static final String RESOLUTION_STATUS_APPROVED ="APPROVED";
    public static final String RESOLUTION_STATUS_ADJURNED ="ADJOURNED";
    public final static String APPROVED ="APPROVED";
    public final static String REJECTED ="REJECTED";
    public final static String ADJOURNED ="ADJOURNED";
    
    public static final LinkedHashMap<String, String> MEETING_TIMINGS = new LinkedHashMap<String, String>() {
        {
            put("9.00 AM", "9.00 AM");
            put("9.30 AM", "9.30 AM");
            put("10.00 AM", "10.00 AM");
            put("10.30 AM", "10.30 AM");
            put("11.00 AM", "11.00 AM");
            put("11.30 AM", "11.30 AM");
            put("12.00 PM", "12.00 PM");
            put("12.30 PM", "12.30 PM");
            put("01.00 PM", "01.00 PM");
            put("01.30 PM", "01.30 PM");
            put("02.00 PM", "02.00 PM");
            put("02.30 PM", "02.30 PM");
            put("03.00 PM", "03.00 PM");
            put("03.30 PM", "03.30 PM");
            put("04.00 PM", "04.00 PM");
            put("04.30 PM", "04.30 PM");
            put("05.00 PM", "05.00 PM");
            put("05.30 PM", "05.30 PM");
            put("06.00 PM", "06.00 PM");

        }
    };
    
    public static final String SENDSMSFORCOUNCIL = "SENDSMSFORCOUNCILMEMBER";
    public static final String SENDEMAILFORCOUNCIL = "SENDEMAILFORCOUNCILMEMBER";
    
    public static final String SMSEMAILTYPEFORCOUNCILMEETING="COUNCILMEETING";
    public static final String WF_STATE_REJECT =  "Reject";
    public static final String WF_REJECT_STATE = "Rejected";
    public static final String WF_NEW_STATE = "NEW";
    public static final String COLON_CONCATE = "::";
    public static final String NATURE_OF_WORK = "Preamble";
    public static final String PREAMBLE_MODULE_TYPE = "COUNCILPREAMBLE";
    public static final String WF_APPROVE_BUTTON = "Approve";
    public static final String MEETINGSTATUSCREATED = "CREATED";
    public final static String MEETINGSTATUSAPPROVED ="APPROVED";
    public final static String MEETINGRESOLUTIONFILENAME = "MeetingResolution";
}
