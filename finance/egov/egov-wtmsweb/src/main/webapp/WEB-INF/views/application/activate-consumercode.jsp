<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2015>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<form:form method ="post" action="" class="form-horizontal form-groups-bordered"  modelAttribute="activateConnection" id="activateConnectionform"
			cssClass="form-horizontal form-groups-bordered"
			enctype="multipart/form-data">
					<div class="panel panel-primary" data-collapsed="0">
						<div class="panel-heading">
						<spring:message
			code="lbl.activate.connection" />
						</div>
						<div class="panel-body custom-form">
<div class="form-group">
<label class="col-sm-3 control-label text-right"><spring:message
								code="lbl.effective.fromdate" />:<span class="mandatory"></span></label>
						<div class="col-sm-3 add-margin">
							<form:input  path="activateDate"  
								class="form-control datepicker" data-date-end-date="0d"
								id="effectiveDate" data-inputmask="'mask': 'd/m/y'" required="required" />
								<form:errors path="activateDate" cssClass="add-margin error-msg" />
						</div>
	<label class="col-sm-2 control-label text-right"><spring:message
			code="lbl.referenceno" />:<span class="mandatory"></span></label> 
	<div class="col-sm-3 add-margin">
		<form:input class="form-control patternvalidation" data-pattern="alphanumericwithspace" maxlength="50" id="referenceNo" path="referenceNo" />
		<form:errors path="referenceNo" cssClass="add-margin error-msg" />		
	</div>				
</div>
							<div class="form-group text-center" >
								<button type="button" class="btn btn-primary" id="buttonid"><spring:message code="lbl.Submit.button"/></button>
								<a onclick="self.close()" class="btn btn-default" href="javascript:void(0)"><spring:message code="lbl.close"/></a>
							</div>
						</div>
					</div>
 					

		</form:form>			
				<link rel="stylesheet" href="<cdn:url value='/resources/global/js/jquery/plugins/datatables/responsive/css/datatables.responsive.css' context='/egi'/>">
				<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/egi'/>"/>
				<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/egi'/>">
                <script src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/egi'/>"
	            type="text/javascript"></script>
                <script src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/egi'/>"
	            type="text/javascript"></script>
                <script src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/responsive/js/datatables.responsive.js' context='/egi'/>"
	            type="text/javascript"></script>
	            <script src="<cdn:url value='/resources/js/app/activate-consumercode.js?rnd=${app_release_no}'/>"></script>