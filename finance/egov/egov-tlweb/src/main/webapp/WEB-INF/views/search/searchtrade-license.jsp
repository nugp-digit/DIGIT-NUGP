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


<%@ page contentType="text/html" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>

<div class="row">
	<div class="col-md-12">
		<form:form class="form-horizontal form-groups-bordered" action=""
			id="searchForm" modelAttribute="searchForm" method="get">
			<input:hidden id="licenseId" name="licenseId" value="${licenseId}" />
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">
						<strong><spring:message code='license.search' /></strong>
					</div>
				</div>
				<div class="panel-body"></div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-right"> <spring:message code='license.applicationnumber' /></label>
					<div class="col-sm-3 add-margin">
						<input type="text" id="applicationNumber" class="form-control typeahead" placeholder="" autocomplete="off"/>
						<form:hidden path="applicationNumber" id="applicationNumber"/>						
					</div>
					<label class="col-sm-2 control-label text-right"> <spring:message code='search.license.status' /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="statusId" id="status" cssClass="form-control" cssErrorClass="form-control error">
							<form:option value="">
								<spring:message code="lbl.category.select" />
							</form:option>
							<form:options items="${statusList}" itemValue="id" itemLabel="name" />
						</form:select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-right"> <spring:message code='search.licensee.no' /></label>
					<div class="col-sm-3 add-margin">
						<input type="text" id="licenseNumber"class="form-control typeahead" placeholder="" autocomplete="off"/>
						<form:hidden path="licenseNumber" id="licenseNumber"/>				
					</div>
					<label class="col-sm-2 control-label text-right"> <spring:message code='license.oldlicensenum' /></label>
					<div class="col-sm-3 add-margin">
						<input type="text" id="oldLicenseNumber" class="form-control typeahead" placeholder="" autocomplete="off" />
					    <form:hidden path="oldLicenseNumber" id="oldLicenseNumber"/>				
				  </div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-right"> <spring:message code='search.license.category' /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="categoryId" id="category" cssClass="form-control" cssErrorClass="form-control error">
							<form:option value="">
								<spring:message code="lbl.category.select" />
							</form:option>
							<form:options items="${categoryList}" itemValue="id" itemLabel="name" />
						</form:select>
					</div>
					<label class="col-sm-2 control-label text-right"> <spring:message code='search.license.subCategory' /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="subCategoryId" id="subCategory" cssClass="form-control" cssErrorClass="form-control error">
							<form:option value="">
								<spring:message code="lbl.category.select" />
							</form:option>
							<form:options items="${subCategoryList}" itemValue="id"	itemLabel="name" />
						</form:select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-right"> <spring:message code='search.license.establishmentname' /></label>
					<div class="col-sm-3 add-margin">
						<input type="text" id="tradeTitle" class="form-control typeahead" placeholder="" autocomplete="off" />
						<form:hidden path="tradeTitle" id="tradeTitle"/>				
			       </div>
					<label class="col-sm-2 control-label text-right"> <spring:message code='licensee.applicantname' /></label>
					<div class="col-sm-3 add-margin">
						<input type="text" id="tradeOwnerName" class="form-control typeahead" placeholder="" autocomplete="off" />
						<form:hidden path="tradeOwnerName" id="tradeOwnerName"/>				
		    	  </div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label text-right"> <spring:message code='search.license.propertyNo' /></label>
					<div class="col-sm-3 add-margin">
						<input type="text" id="propertyAssessmentNo" class="form-control typeahead" placeholder="" autocomplete="off" />
						<form:hidden path="propertyAssessmentNo" id="propertyAssessmentNo"/>				
			    	</div>
					<label class="col-sm-2 control-label text-right"> <spring:message code='search.licensee.mobileNo' /></label>
					<div class="col-sm-3 add-margin">
						<input type="text" id="mobileNo" class="form-control patternvalidation typeahead" placeholder="" autocomplete="off" maxlength="10" data-pattern="number" />
						<form:hidden path="mobileNo" id="mobileNo"/>				
			       </div>
				</div>
			</div>
			<div class="row">
				<div class="text-center">
					<button type="button" id="btnsearch" class="btn btn-primary">
						<spring:message code="lbl.search" />
					</button>
					<button type="reset" class="btn btn-default"><spring:message code="lbl.reset" /></button>
					<button type="button" class="btn btn-default" data-dismiss="modal" onclick="window.close();">
						<spring:message code="lbl.close" />
					</button>
				</div>
			</div>
		</form:form>
	</div>
</div>

<div class="row display-hide report-section">
	<div class="col-md-12 table-header text-left">Search Result</div>
	<div class="col-md-12 form-group report-table-container">
		<table class="table table-bordered table-hover multiheadertbl" id="tblSearchTrade">
			<thead>
				<tr>
					<th>Application Number</th>
					<th>TL Number</th>
					<th>Old TL Number</th>
					<th>Category</th>
					<th>Sub Category</th>
					<th>Title of Trade</th>
					<th>Trade Owner</th>
					<th>Mobile Number</th>
					<th>Property Assessment Number</th>
					<th>Financial Year</th>
					<th>Status</th>
					<th>Owner Name</th>
					<th>Actions</th>
				</tr>
			</thead>
		</table>
	</div>
</div>
<link rel="stylesheet"
	href="<cdn:url  value='/resources/global/css/bootstrap/typeahead.css' context='/egi'/>">
<script type="text/javascript"
	src="<cdn:url  value='/resources/global/js/bootstrap/typeahead.bundle.js' context='/egi'/>"></script>
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/egi'/>" />
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/egi'/>">
<script type="text/javascript"
	src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url  value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url  value='/resources/js/app/searchtrade-license.js?rnd=${app_release_no}'/>"></script>




