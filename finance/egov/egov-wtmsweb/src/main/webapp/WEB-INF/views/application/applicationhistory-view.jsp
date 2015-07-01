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
<div class="panel-body history-slide">
					<div class="row hidden-xs visible-sm visible-md visible-lg view-content header-color">
						<div class="col-sm-2 col-xs-6 add-margin">Date</div>
						<div class="col-sm-2 col-xs-6 add-margin">Updated By</div>
						<div class="col-sm-2 col-xs-6 add-margin">Status</div>
						<div class="col-sm-2 col-xs-6 add-margin">Current Owner</div>
						<div class="col-sm-2 col-xs-6 add-margin">Department</div>
						<div class="col-sm-2 col-xs-6 add-margin">Comments</div>
					</div>
					<c:choose>
							<c:when test="${!applicationHistory.isEmpty()}">
								<c:forEach items="${applicationHistory}" var="history">
								<div class="row  add-border">
									<div class="col-sm-2 col-xs-12 add-margin">
										<fmt:formatDate value="${history.date}" var="historyDate"
											pattern="dd-MM-yyyy HH:mm a E" />
										<c:out value="${historyDate}" />
									</div>
									<div class="col-sm-2 col-xs-12 add-margin">
										<c:out value="${history.updatedBy}" />
									</div>
									<div class="col-sm-2 col-xs-12 add-margin">
										<c:out value="${history.status}" />
									</div>
									<div class="col-sm-2 col-xs-12 add-margin">
										<c:out value="${history.user}" />
									</div>
									<div class="col-sm-2 col-xs-12 add-margin">
										<c:out value="${history.department}" />
									</div>
									<div class="col-sm-2 col-xs-12 add-margin">
										<c:out value="${history.comments}" />&nbsp;
									</div>
								</div>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<div class="col-md-3 col-xs-6 add-margin">No history
									details for complaint</div>
							</c:otherwise>
						</c:choose>
					
				</div>