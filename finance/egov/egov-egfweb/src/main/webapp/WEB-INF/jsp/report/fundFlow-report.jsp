<!--  #-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#      accountability and the service delivery of the government  organizations.
#   
#       Copyright (C) <2015>  eGovernments Foundation
#   
#       The updated version of eGov suite of products as by eGovernments Foundation 
#       is available at http://www.egovernments.org
#   
#       This program is free software: you can redistribute it and/or modify
#       it under the terms of the GNU General Public License as published by
#       the Free Software Foundation, either version 3 of the License, or
#       any later version.
#   
#       This program is distributed in the hope that it will be useful,
#       but WITHOUT ANY WARRANTY; without even the implied warranty of
#       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#       GNU General Public License for more details.
#   
#       You should have received a copy of the GNU General Public License
#       along with this program. If not, see http://www.gnu.org/licenses/ or 
#       http://www.gnu.org/licenses/gpl.html .
#   
#       In addition to the terms of the GPL license to be adhered to in using this
#       program, the following additional terms are to be complied with:
#   
#   	1) All versions of this program, verbatim or modified must carry this 
#   	   Legal Notice.
#   
#   	2) Any misrepresentation of the origin of the material is prohibited. It 
#   	   is required that all modified versions of this material be marked in 
#   	   reasonable ways as different from the original version.
#   
#   	3) This license does not grant any rights to any user of the program 
#   	   with regards to rights under trademark law for use of the trade names 
#   	   or trademarks of eGovernments Foundation.
#   
#     In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#-------------------------------------------------------------------------------  -->
<%@ include file="/includes/taglibs.jsp" %>
<%@ page language="java"%>

<html>  
<head> 
 <script type="text/javascript" src="/EGF/resources/javascript/fundFlow.js"></script>
    <title><s:text name="fundflowreport"/></title>
    
  
    <script type="text/javascript">
    function onloadFundFlow()
    {
    <s:if test="%{mode=='edit'}">
       document.getElementById("search").name="method:beforeEdit";
    </s:if>
	<s:if test="receiptList!=null && receiptList.size()>0">
    calculateFunds(document.getElementById('receiptList[0].openingBalance'));
    </s:if>
    <s:if test="paymentList!=null && paymentList.size()>0">
    calculateFundsForPayment(document.getElementById('paymentList[0].openingBalance'));
    </s:if>
    }
    function validateFundFlow()
    {
    
/*    if(document.getElementById("fund").value=="")
    {
     alert("Select Fund");
     return false;
     } */
     if(document.getElementById("asOnDate").value=="")
     {
     alert("Select Date");
     return false;
     }
     return true;
    }


</script>
    
</head>
<body onload="onloadFundFlow()">  
	<div class="subheadnew">Fund Flow Analysis Report</div>
	<s:form  name="fundFlowReport" action="fundFlow" theme="simple">
	<%@include file="fundFlow-form.jsp" %>	

	<s:if test="(receiptList!=null && receiptList.size()>0) ||( paymentList!=null && paymentList.size()>0) ">


<div class="buttonbottom">
	<s:submit value="Export Excel" method="exportXls" cssClass="button" />
	<s:submit value="Export Pdf" method="exportPdf" cssClass="button" />
</div>	
</s:if>	
 	</s:form>                   
 </body>
 </html>
 
      
      
      
