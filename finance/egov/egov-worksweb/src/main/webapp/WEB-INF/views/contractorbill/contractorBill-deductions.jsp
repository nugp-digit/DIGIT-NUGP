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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		<div class="panel-title">
			<spring:message  code="lbl.credit.details"/>
		</div>
	</div>
	
	<div class="panel-heading">
		<div class="panel-title">
			<spring:message  code="lbl.deductions"/>
		</div>
	</div>
	<div class="panel-body">
		<table class="table table-bordered" id="tblcreditdetails">
			<thead>
				<tr>
					<th><spring:message code="lbl.account.code"/><span class="mandatory"></span></th>
					<th><spring:message code="lbl.account.head"/></th>
					<th><spring:message code="lbl.credit.amount"/><span class="mandatory"></span></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<input type="text" id="creditGlcode" class="form-control table-input patternvalidation" data-pattern="number" maxlength="9" required="required"> 
						<form:hidden path="billDetailes[1].glcodeid"  name="billDetailes[1].glcodeid" id="creditGlcodeId" value="${lineEstimateDetails.lineEstimate.budgetHead.minCode.id}" /> 
						<form:errors path="billDetailes[1].glcodeid" cssClass="add-margin error-msg" />
					</td>
					<td>
						<input type="text" id="creditAccountHead" class="form-control" disabled> 
					</td>
					<td>
						<form:input path="billDetailes[1].creditamount" id="creditAmount" name="billDetailes[1].creditamount" data-errormsg="Credit Amount is mandatory!" onkeyup="decimalvalue(this);" data-pattern="decimalvalue" data-idx="0" data-optional="1" class="form-control table-input text-right creditAmount" maxlength="12" required="required" />
						<form:errors path="billDetailes[1].creditamount" cssClass="add-margin error-msg" />
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<div class="panel-heading">
		<div class="panel-title">
			<spring:message  code="lbl.netpayable"/>
		</div>
	</div>
	<div class="panel-body">
		<table class="table table-bordered" id="tblcreditdetails">
			<thead>
				<tr>
					<th><spring:message code="lbl.account.code"/><span class="mandatory"></span></th>
				<%-- 	<th><spring:message code="lbl.account.head"/></th> --%>
					<th><spring:message code="lbl.credit.amount"/></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<form:select path="" data-first-option="false" name="netPayableAccountCode" id="netPayableAccountCode" class="form-control" required="required">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<c:forEach var="coa" items="${netPayableAccounCodes}">
						        <form:option value="${coa.id}"><c:out value="${coa.glcode} - ${coa.name}"/></form:option>  
						    </c:forEach>   
							<%-- <form:options items="${netPayableAccounCodes}" itemLabel="glcode" itemValue="id" />  --%>
						</form:select>
						<form:errors path="" cssClass="add-margin error-msg" />
					</td>
					<!-- <td> 
						<input type="text" id="creditAccountHead" class="form-control" disabled> 
					</td> -->
					<td>
						<input type="text" id="netPayableAmount" name="netPayableAmount" class="form-control" readonly="true">
						<form:errors path="" cssClass="add-margin error-msg" />
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>