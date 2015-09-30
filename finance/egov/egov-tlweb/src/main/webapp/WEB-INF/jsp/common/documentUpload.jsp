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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<script>
	function viewDocument(fileStoreId) {
		var sUrl = "/egi/downloadfile?fileStoreId="+fileStoreId+"&moduleName=EGTL";
		window.open(sUrl,"window",'scrollbars=yes,resizable=no,height=400,width=400,status=yes');	
	}
</script>
<div class="col-sm-12 view-content header-color hidden-xs">
	<div class="col-sm-1 table-div-column"><s:text name="doctable.sno" /></div>
    <div class="col-sm-5 table-div-column"><s:text name="doctable.docname" /></div>
    <div class="col-sm-3 table-div-column"><s:text name="doctable.checklist"/></div>
    <div class="col-sm-3 table-div-column"><s:text name="doctable.attach.doc" /></div>	
</div>
<s:iterator value="documentTypes" status="status" var="documentType">
	<div class="form-group">
    	<div class="col-sm-1 text-center"><s:property value="#status.index + 1"/></div>
        <div class="col-sm-5 text-center">
        	<s:property value="name" /><s:if test="mandatory"><span class="mandatory"></span></s:if>
			<s:hidden name="documents[%{#status.index}].type.id" value="%{id}"/>
		</div>
       	<div class="col-sm-3 text-center">
       		<s:if test="mandatory">
       			<s:checkbox name="documents[%{#status.index}].enclosed" required="true"/>
       		</s:if>
       		<s:else>
       			<s:checkbox name="documents[%{#status.index}].enclosed"/>
       		</s:else>
       	</div>
       	<div class="col-sm-3 text-center">
       		<s:if test="%{documents.isEmpty()}">
       			<s:if test="mandatory">
					<s:file name="documents[%{#status.index}].uploads" value="%{documents[#status.index].uploads}" cssClass="button" required="true"/>
				</s:if>
				<s:else>
					<s:file name="documents[%{#status.index}].uploads" value="%{documents[#status.index].uploads}" cssClass="button"/>
				</s:else>
			</s:if>
			<s:elseif test="%{documents[#status.index].files.isEmpty()}">
				<s:hidden name="documents[%{#status.index}].id"/>
				<s:if test="mandatory">
					<s:file name="documents[%{#status.index}].uploads" value="%{documents[#status.index].uploads}" cssClass="button" required="true"/>
				</s:if>
				<s:else>
					<s:file name="documents[%{#status.index}].uploads" value="%{documents[#status.index].uploads}" cssClass="button"/>
				</s:else>
			</s:elseif>
			<s:else>
				<s:iterator value="%{documents[#status.index].files}">
					<s:hidden name="documents[%{#status.index}].id"/>
					<a href="javascript:viewDocument('<s:property value="fileStoreId"/>')"> 
 						<s:property value="%{fileName}"/>
					</a> 
				</s:iterator>	
			</s:else>
       	</div>
   	</div>
</s:iterator>