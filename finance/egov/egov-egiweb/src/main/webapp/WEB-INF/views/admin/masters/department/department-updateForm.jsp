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
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<link rel="stylesheet" href="<c:url value='/resources/global/css/font-icons/entypo/css/entypo.css'/>">
<link rel="stylesheet" href="<c:url value='/resources/global/css/bootstrap/typeahead.css'/>">

<div class="row" id="page-content">
	<div class="col-md-12">
		<div class="panel" data-collapsed="0">
			<div class="panel-body">
				<c:if test="${not empty message}">
					<div id="message" class="success">${message}</div>
				</c:if>
				<form:form method="post" class="form-horizontal form-groups-bordered" id="updateDepartmentForm" modelAttribute="department" >
					<div class="panel panel-primary" data-collapsed="0">
						<div class="panel-heading">
							<div class="panel-title">
								<strong><spring:message code="title.department.update"/></strong>
							</div>
						</div>
						<div class="panel-body custom-form">
							<div class="form-group">
								<label class="col-sm-3 control-label">
									<spring:message code="lbl.departmentName" /><small><i class="entypo-star error-msg"></i></small>
								</label>
								<div class="col-sm-6">
									<form:input path="name" id="name" type="text" class="form-control low-width" placeholder="" autocomplete="off" required="required"/>
									<form:errors path="name" class="add-margin error-msg"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">
									<spring:message code="lbl.departmentCode"/><small><i class="entypo-star error-msg"></i></small>
								</label>
								<div class="col-sm-6">
									<form:input path="code" id="code" type="text" class="form-control low-width" placeholder="" autocomplete="off" required="required"/>
									<form:errors path="code" class="add-margin error-msg"/>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="text-center">
							<button type="submit" class="btn btn-primary"><spring:message code="lbl.update"/></button>
					        <button type="reset" class="btn btn-default"><spring:message code="lbl.reset"/></button>
					        <button type="button" class="btn btn-default" onclick="self.close()"><spring:message code="lbl.close"/></button>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
<script src="<c:url value='/resources/global/js/bootstrap/typeahead.bundle.js'/>"></script>
<script src="<c:url value='/resources/global/js/jquery/plugins/exif.js'/>"></script>
<script src="<c:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js'/>"></script>
