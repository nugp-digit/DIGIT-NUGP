<!-- #-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#    accountability and the service delivery of the government  organizations.
# 
#     Copyright (C) <2015>  eGovernments Foundation
# 
#     The updated version of eGov suite of products as by eGovernments Foundation 
#     is available at http://www.egovernments.org
# 
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     any later version.
# 
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with this program. If not, see http://www.gnu.org/licenses/ or 
#     http://www.gnu.org/licenses/gpl.html .
# 
#     In addition to the terms of the GPL license to be adhered to in using this
#     program, the following additional terms are to be complied with:
# 
# 	1) All versions of this program, verbatim or modified must carry this 
# 	   Legal Notice.
# 
# 	2) Any misrepresentation of the origin of the material is prohibited. It 
# 	   is required that all modified versions of this material be marked in 
# 	   reasonable ways as different from the original version.
# 
# 	3) This license does not grant any rights to any user of the program 
# 	   with regards to rights under trademark law for use of the trade names 
# 	   or trademarks of eGovernments Foundation.
# 
#   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#------------------------------------------------------------------------------- -->
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="row">
	<div class="col-md-12">
	
		<form:form action="addConnection-create"
			modelAttribute="addConnection" id="addWaterConnectionform"
			cssClass="form-horizontal form-groups-bordered"
			enctype="multipart/form-data">
			<input type="hidden" name="validationMessage" id="validationMessage" value="${validationMessage}"> 
			
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message  code="lbl.basicdetails"/>
					</div>
				</div>
				<div class="panel-body custom-form ">
				<input type="hidden" id="currentUser" name="currentUser" value="${currentUser}"/>  
				<input type="hidden" name="validateIfPTDueExists" id="validateIfPTDueExists" value="${validateIfPTDueExists}"> 
				<form:hidden id="mode" path=""  value="${mode}"/>
				<form:hidden path="" name="approvalPosOnValidate" id="approvalPosOnValidate" value="${approvalPosOnValidate}"/> 
				<form:hidden id="documentName" path="" value="${documentName}"/>
				<form:hidden path="applicationType" id="applicationType.id" value="${addConnection.applicationType.id}"/>
				<form:hidden path="connectionStatus" id="connectionStatus" value="${addConnection.connectionStatus}"/>
				<form:hidden path="connection.parentConnection" value="${parentConnection.id}"/>
				<input type="hidden" id="waterTaxDueforParent" value="${waterTaxDueforParent}" name="waterTaxDueforParent"/>
				<form:hidden path="connection.propertyIdentifier" value="${waterConnectionDetails.connection.propertyIdentifier}"/>
					<jsp:include page="commonappdetails-view.jsp"></jsp:include>
				<c:if test="${validationMessage==''}">	
					<jsp:include page="connectiondetails.jsp"></jsp:include>	
					<jsp:include page="documentdetails.jsp"></jsp:include>	
				</c:if>
				</div>
			</div>			
				<c:if test="${validationMessage==''}">
						<jsp:include page="../common/commonWorkflowMatrix.jsp"/>
							<div class="buttonbottom" align="center">
							<jsp:include page="../common/commonWorkflowMatrix-button.jsp" />
					</div>
				</c:if>	
		</form:form>
	</div>
</div>

<script src="<c:url value='/resources/js/app/additionalconnection.js'/>"></script>
