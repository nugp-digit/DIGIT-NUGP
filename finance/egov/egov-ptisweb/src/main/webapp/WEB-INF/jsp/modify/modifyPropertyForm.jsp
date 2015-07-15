<!-------------------------------------------------------------------------------
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
------------------------------------------------------------------------------->
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="bluebox2" width="5%">
			&nbsp;
		</td>
		<td class="bluebox" width="20%">
			<s:text name="prop.Id" />
			:
		</td>
		<td class="bluebox" width="15%">
			<span class="bold"><s:property default="N/A"
					value="%{basicProp.upicNo}" /> </span>						
		</td>
		<td class="bluebox" width="20%">
			&nbsp;
		</td>
		<td class="bluebox" width="20%">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td class="greybox2">
			&nbsp;
		</td>
		<td class="greybox">
			<s:text name="OwnerName" />
			:
		</td>
		<td class="greybox">
			<span class="bold"><s:property default="N/A"
					value="%{ownerName}" /> </span>
		</td>
		<td class="greybox">
			<s:text name="MobileNumber" />:
		</td>
		<td class="greybox">
			<span class="bold"><s:property value="%{basicProp.mobileNumber}" default="N/A"/></span>
		</td>
	</tr>

	<tr>
		<td class="bluebox2">
			&nbsp;
		</td>
		<td class="bluebox" width="8%">
			<s:text name="PropertyAddress" />
			:
		</td>
		<td class="bluebox">
			<span class="bold"><s:property default="N/A"
					value="%{propAddress}" /> </span>
		</td>
		<td class="bluebox">
			&nbsp;
		</td>
		<td class="bluebox">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="assessmentDetails.title"/></span>
			</div>
		</td>
	</tr>
	<tr>
		<td class="greybox" width="5%">&nbsp;</td>
		<td class="greybox" width="25%"><s:text name="extent.site"/> :</td>
		<td class="greybox" width="">
		   <span class="bold"><s:property value="%{propertyDetail.extentSite}" default="N/A"/></span>
		   <s:hidden name="propertyDetail.extentSite" value="%{propertyDetail.extentSite}"/>
		</td>
		<td class="greybox" width="25%"></td>
		<td class="greybox">
		</td>
	</tr>
	<tr>
		<td class="greybox" width="5%">&nbsp;</td>
		<td class="greybox" width="25%"><s:text name="reg.docno"/> :</td>
		<td class="greybox" width="">
			<span class="bold"><s:property value="%{basicProp.regdDocNo}" default="N/A"/></span>
		</td>
		<td class="greybox" width="25%"><s:text name="reg.docdate"/> :</td>
		<td class="greybox">
			<span class="bold"><s:property value="%{basicProp.regdDocDate}" default="N/A"/></span>
		</td>
	</tr>

	<tr>
		<td class="greybox">&nbsp;</td>
		<td class="bluebox" width="25%"><s:text name="ModifyReason"></s:text> <span
			class="mandatory1">*</span> :</td>
		<td class="greybox" width=""><s:select headerKey="-1"
				headerValue="%{getText('default.select')}" name="reasonForModify"
				id="reasonForModify" listKey="code" listValue="mutationName"
				list="dropdownData.MutationList" value="%{reasonForModify}"
				cssClass="selectnew" onchange="return enableCourtRulingDets();" />
		</td>
	</tr>
	
	<tr>
		<td class="greybox" width="5%">&nbsp;</td>
		<td class="greybox" width="25%"><s:text name="ownership.type"></s:text>
			<span class="mandatory1">*</span> :</td>
		<td class="greybox" width=""><s:select headerKey="-1" headerValue="%{getText('default.select')}" name="propTypeId"
				id="propTypeId" listKey="id" listValue="type" list="dropdownData.PropTypeMaster" value="%{propTypeId}"
				cssClass="selectnew" onchange="populatePropTypeCategory();toggleFloorDetails();enableFieldsForPropType();" /></td>
				
		<td class="greybox" width="25%"><s:text name="property.type"></s:text>
			<span class="mandatory1" id="prntMandatory">*</span> :</td>
		<egov:ajaxdropdown id="propTypeCategoryId" fields="['Text','Value']" dropdownId="propTypeCategoryId"
			url="/common/ajaxCommon-propTypeCategoryByPropType.action" />
		<td class="greybox">
		   <s:select headerKey="-1"	headerValue="%{getText('default.select')}" name="propertyDetail.categoryType"
				id="propTypeCategoryId" listKey="key" listValue="value" list="propTypeCategoryMap" value="%{propertyDetail.categoryType}"
				cssClass="selectnew"/>
		</td>
	</tr>
	<tr>
		<td class="greybox">&nbsp;</td>
		<td class="bluebox"><s:text name="extent.appurtntland" /> : 
		<td class="bluebox"><s:checkbox name="chkIsAppartenauntLand" id="chkIsAppartenauntLand"
				value="%{chkIsAppartenauntLand}" onclick="enableAppartnaumtLandDetails();" />
		</td>
		<td class="greybox"><s:text name="certificationNumber"></s:text>:</td>
		<td class="greybox"><s:textfield maxlength="64" name="certificationNumber" id="certificationNumber"></s:textfield></td>
	</tr>
	<tr id="appartenantRow">
		<td class="greybox">&nbsp;</td>
		<td class="greybox"><s:text name="extent.appurtntland"></s:text>
			<span class="mandatory1">*</span> :</td>
		<td class="greybox"><s:textfield name="propertyDetail.extentAppartenauntLand" id="propertyDetail.extentAppartenauntLand"
				value="%{propertyDetail.extentAppartenauntLand}" size="12"	maxlength="12" onchange="trim(this,this.value);"
				onblur="validNumber(this);checkZero(this);"></s:textfield></td>
	</tr>
	
	<tr>
		<td class="greybox" width="5%">&nbsp;</td>
		<td class="greybox" width="25%"><s:text name="building.permNo"></s:text> :</td>
		<td class="greybox" width="">
		   <s:textfield name="buildingPermissionNo" id="buildingPermissionNo" size="12" maxlength="12" onchange="trim(this,this.value);" onblur = "validNumber(this);checkZero(this);"></s:textfield>
		</td>
		<td class="greybox" width="25%"><s:text name="buildingpermdate"></s:text> :</td>
		<td class="greybox">
		  <s:textfield name="buildingPermissionDate"  cssClass="datepicker" id="buildingPermissionDate" size="12" maxlength="12"></s:textfield>
		</td>
	</tr>
	<!-- Amenities section -->
	
	<tr id="amenitiesHeaderRow" class="amenities">
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"> <s:text name="amenities"></s:text>
				</span>
			</div>
		</td>
	</tr>

	<tr class="amenities">
		<td colspan="5">
			<div id="AmenitiesDiv">
				<%@ include file="../common/amenitiesForm.jsp"%>
			</div>
		</td>
	</tr>
	 
	
	<!-- Floor type details -->
	
	<tr id="constructionHeaderRow" class="construction">
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="title.constructiontypes" /></span>
			</div>
		</td>
	</tr>
	
	<tr class="construction">
		<td colspan="5">
			<div id="AmenitiesDiv">
				<%@ include file="../common/constructionForm.jsp"%>
			</div>
		</td>
	</tr>
	
	<tr id="vacantAreaRow">
		<td class="greybox" width="5%">&nbsp;</td>
		<td class="bluebox">
			<div id="plotArea">
				<s:text name="PlotArea"/>
				<span class="mandatory1">*</span> :
			</div>
			<div id="undivArea">
				<s:text name="undivArea"/>
				<span class="mandatory1">*</span> :
			</div>
		</td>
		<td class="bluebox" colspan="2"><s:textfield name="areaOfPlot"
				maxlength="15"
				onblur="trim(this,this.value);checkForTwoDecimals(this,'Area Of Plot');checkZero(this,'Area Of Plot');" />
			<span class="highlight2"><s:text
					name="msgForCompulsionOfOpenPlot" /> </span>
		</td>
		<td class="bluebox">&nbsp;</td>
	</tr>
	
	<tr id="appartmentRow">
		<td class="greybox">&nbsp;</td>
		<td class="greybox" width="25%"><s:text name="apartcomplex.name"></s:text> :</td>
		<td class="greybox" width=""><s:select headerKey="-1"
			headerValue="%{getText('default.select')}" name="appartmentId"
			id="sbapartcomplex" listKey="id" listValue="name"
			list="dropdownData.Appartments" value="%{appartmentId}"
			cssClass="selectnew" onchange="makeMandatory();" />
		</td>
	</tr>
	<tr id="floorHeaderRow" class="floordetails">
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="FloorDetailsHeader" /> </span>
			</div>
		</td>
	</tr>

	<!-- Floor Details Section -->

	<tr class="floordetails">
		<td colspan="5">
			<div align="center">
				<%@ include file="../common/FloorForm.jsp"%>
			</div>
		</td>
	</tr>

	<tr id="vacantLandRow" class="vacantlanddetaills">
		<td colspan="5">
			<div class="headingsmallbg">
				<span class="bold"><s:text name="VacantLandDetailsHeader" /> </span>
			</div>
		</td>
	</tr>

	<tr class="vacantlanddetaills">
		<td colspan="5">
			<div align="center">
				<%@ include file="../common/vacantLandForm.jsp"%>
			</div>
		</td>
	</tr>

	<tr>
		<td class="greybox" width="5%">&nbsp;</td>
		<td class="greybox" width="25%"><s:text name="constCompl.date"></s:text> :</td>
		<td class="greybox" width="">
		   <s:textfield name="dateOfCompletion" id="basicProperty.propOccupationDate" cssClass="datepicker" size="10" maxlength="10"></s:textfield>
		</td>
	</tr>
	<s:if test="%{@org.egov.ptis.constants.PropertyTaxConstants@REVENUE_OFFICER_DESGN.equalsIgnoreCase(userDesgn) || 
		@org.egov.ptis.constants.PropertyTaxConstants@WFLOW_ACTION_STEP_COMMISSIONER_REJECTED.equalsIgnoreCase(model.state.nextAction)}">
		<%@ include file="../common/DocumentUploadView.jsp"%>
	</s:if>
	<s:else>
		<%@ include file="../common/DocumentUploadForm.jsp"%>
	</s:else>
</table>
<script type="text/javascript">
	function populatePropTypeCategory() {
		populatepropTypeCategoryId({
			propTypeId : document.getElementById("propTypeId").value
		});
	}
	//hide rows and columns of fields
	jQuery('td.siteowner').hide();
	jQuery('tr.bpddetails').hide();
	jQuery('tr.vacantlanddetaills').hide();
    
</script>