<!--
	eGov suite of products aim to improve the internal efficiency,transparency, 
    accountability and the service delivery of the government  organizations.
 
    Copyright (C) <2015>  eGovernments Foundation
 
	The updated version of eGov suite of products as by eGovernments Foundation 
    is available at http://www.egovernments.org
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.
 
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 
    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or 
    http://www.gnu.org/licenses/gpl.html .
 
    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:
 
 	1) All versions of this program, verbatim or modified must carry this 
 	   Legal Notice.
 
 	2) Any misrepresentation of the origin of the material is prohibited. It 
 	   is required that all modified versions of this material be marked in 
 	   reasonable ways as different from the original version.
 
 	3) This license does not grant any rights to any user of the program 
 	   with regards to rights under trademark law for use of the trade names 
 	   or trademarks of eGovernments Foundation.
 
   	In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
-->
<%@ taglib prefix="s" uri="/WEB-INF/taglib/struts-tags.tld"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="5">
			<div class="headingsmallbg">
				<s:text name="objection.add.hearingDetails" />
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="5">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				 <s:set var="hearingIdx" value="%{hearings.size()-1}"/>
				<tr>
					<td class="bluebox" width="25%">
						<s:text name="hearing.details" />
						<span class="mandatory1">*</span>
					</td>
					<td class="bluebox" width="25%">
						<s:textarea name="objection.hearings[%{hearingIdx}].hearingDetails"
							id="hearingDetails" cols="40" rows="2" onblur="checkLength(this)"></s:textarea>
					</td>
					<td class="bluebox" width="25%">
						<s:text name="hearing.inspection.required" />
					</td>
					<td class="bluebox" width="25%">
						<s:radio name="objection.hearings[%{hearingIdx}].inspectionRequired"
							list="#{'true':'Yes','false':'No'}" id="inspectionRequired" />
					</td>

				</tr>
				<tr>
					<td class="greybox" width="25%"><s:text name="objection.upload.document"/></td>
					<td class="greybox" width="25%">
						<input type="button" class="button" value="Upload Document"
							id="docUploadButton" onclick="showDocumentManager();" />
							<s:hidden name="objection.hearings[%{hearingIdx}].documentNumber" id="docNumber" />
					</td>
				
				</tr>
			</table>
		</td>
	</tr>
</table>
