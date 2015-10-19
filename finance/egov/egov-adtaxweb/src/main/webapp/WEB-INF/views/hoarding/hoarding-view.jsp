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
		<c:if test="${not empty message}">
			<div class="alert alert-success" role="alert"><spring:message code="${message}"/></div>
		</c:if>
		<form:form id="hoardingformview" action="" class="form-horizontal form-groups-bordered" modelAttribute="hoarding" 
		commandName="hoarding" >
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<ul class="nav nav-tabs" id="settingstab">
					<li class="active"><a data-toggle="tab"
						href="#hoardingdetails" data-tabidx="0" aria-expanded="false"><spring:message code="lbl.hoarding.details"/></a></li>
					<li class=""><a data-toggle="tab" href="#hoardingattachments"
						data-tabidx="1" aria-expanded="false"><spring:message code="lbl.hoarding.enclosure"/></a></li>
				</ul>
			</div>
			<div class="panel-body custom-form">
				<div class="tab-content">
					<div class="tab-pane fade active in" id="hoardingdetails">
							<div class="form-group">
								<label class="col-sm-3 add-margin text-right">
								<spring:message code="lbl.application.no"/>
								
								</label>
								<div class="col-sm-3 add-margin view-content view-content">
									${hoarding.applicationNumber}
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.application.date"/>
								
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.applicationDate}
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.hoarding.permission.no"/>
								
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.permissionNumber}
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.hoarding.no"/>
								
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.hoardingNumber}
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.hoarding.name"/>
								
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.hoardingName}
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.hoarding.type"/>
								</label>
								<div class="col-sm-3 add-margin view-content dynamic-span capitalize">
									${hoarding.type}
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.hoarding.agency"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.agency.name}
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.hoarding.adv"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.advertiser}
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.hoarding.ad.particulars"/>
								
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.advertisementParticular}
								</div>
									<label class="col-sm-2 control-label text-right">
									<spring:message code="lbl.hoarding.prop.type"/>
									</label>
									<div class="col-sm-3 add-margin view-content">
										${hoarding.propertyType}
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.hoarding.status"/>
									</label>
								<div class="col-sm-3 add-margin view-content">
										${hoarding.status}
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.owner.detail"/>
								
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.ownerDetail}
								</div>
							</div>
														
							<div class="panel-heading custom_form_panel_heading">
								<div class="panel-title">
								<spring:message code="lbl.hoarding.location"/>
								</div>
							</div>	
								<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.hoarding.prop.assesmt.no"/>
							
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.propertyNumber}" default="N/A"/>
								</div>
								<label class="col-sm-2 control-label text-right">
									<spring:message code="lbl.locality"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.location.name}" default="N/A"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.rev.ward"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.ward.name}" default="N/A"/>
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.block"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
										<c:out value="${hoarding.block.name}" default="N/A"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.street"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.street.name}" default="N/A"/>
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.election.ward"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.electionWard.name}" default="N/A"/>
								</div>
							</div>
								<div class="form-group">
								<label class="col-sm-3 control-label text-right">
									<spring:message code="lbl.address"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.address}" default="N/A"/>
								</div>
								<div class="col-sm-5 add-margin">&nbsp;</div>
							</div>
							<div class="panel-heading custom_form_panel_heading">
								<div class="panel-title">
								<spring:message code="lbl.hoarding.struct"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.category"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.category.name}
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.subcategory"/>
								
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.subCategory.description}
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.hoarding.measurement"/>
								
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.measurement}
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.uom"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.unitOfMeasure.description}
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.length"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.length}
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.width"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.width}
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.breadth"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.breadth}
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.hoarding.total.height"/>
								
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.totalHeight}
								</div>
							</div>
						
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.hoarding.class"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.rateClass.description}" default="N/A"/>
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.rev.inspector"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.revenueInspector.name}" default="N/A"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.hoarding.duration"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.advertisementDuration}" default="N/A"/>
								</div>
							</div>
							<div class="panel-heading custom_form_panel_heading">
								<div class="panel-title">
								<spring:message code="lbl.fee.details"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.tax"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									${hoarding.taxAmount}
								</div>
								<label class="col-sm-2 control-label text-right">
								<spring:message code="lbl.hoarding.enc.fee"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.encroachmentFee}" default="N/A"/>
								</div>
								
								
							</div>
							
							<div class="form-group">
						<label class="col-sm-3 control-label text-right">
								<spring:message code="lbl.pendingtax"/>
								</label>
								<div class="col-sm-3 add-margin view-content">
									<c:out value="${hoarding.pendingTax}" default="N/A"/>
								</div>
							</div>
					</div>
					<div class="tab-pane fade" id="hoardingattachments">
							<form:hidden path="latitude" id="latitude"/> 
							<form:hidden path="longitude" id="longitude" /> 
							<div class="col-sm-12 view-content header-color hidden-xs">
								<div class="col-sm-1 table-div-column"><spring:message code="lbl.srl.no"/></div>
								<div class="col-sm-5 table-div-column"><spring:message code="lbl.documentname"/></div>
								<div class="col-sm-3 table-div-column"><spring:message code="lbl.enclosed"/></div>
								<div class="col-sm-3 table-div-column"><spring:message code="lbl.attachdocument"/></div>
							</div>
							
							<c:forEach var="docs" items="${hoarding.documents}" varStatus="status">	
								<div class="form-group">
									<div class="col-sm-1 text-center">${status.index+1}</div>
									<div class="col-sm-5 text-center">${docs.doctype.name}</div>
									<div class="col-sm-3 text-center">${docs.enclosed ? "Yes" : "No"}</div>
									<div class="col-sm-3 text-center">
										<c:forEach var="file" items="${docs.files}">	
										<a href="/egi/downloadfile?fileStoreId=${file.fileStoreId}&moduleName=ADTAX&toSave=true"> 
											${file.fileName}
										</a>
										</c:forEach>
									</div>
								</div>
							</c:forEach> 
					</div>
				</div>
			</div>

		</div>
		<div class="text-center">
		    <a href="javascript:void(0)" class="btn btn-default" onclick="self.close()"><spring:message code="lbl.close"/></a>
		</div>
	</form:form>
	</div>
</div>
