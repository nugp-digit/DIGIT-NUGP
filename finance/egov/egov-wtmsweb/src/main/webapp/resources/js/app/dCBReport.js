/*#-------------------------------------------------------------------------------
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
#-------------------------------------------------------------------------------*/

/*
 * Note : Property "selectedModeBndry" used to traverse forward and backward. 
 * 1.ondrilldown at each level(ie zone/ward/block/property) this property value gets updated with the concatenated values of mode and boundary.
 * 2. Value format : mode~boundaryId. 
 * 3. Ex: zone~1 (At 1st level), zone~1-ward~6-block~8-property~10 (At last level). 
 * 4. property~10 means show all properties under block with id 10 / block~8 means show all blocks under ward id 8 and so on.  		
 */

var reportdatatable;


$(document).ready(function(e) { 
	drillDowntableContainer = $("#tbldcbdrilldown");
	$('#report-backbutton').hide();
	$('form').submit(function(e) {
		$('#mode')[0].attributes[0].value = 'zone';
		callAjaxByBoundary(e);
	});
	
	$('#backButton').click(function(e) {
		var temp=$('#selectedModeBndry').val();
		var valArray=temp.split('-');
		if($('#mode')[0].attributes[0].value=='property'){
			if(valArray.length>0){
				var propVal=valArray[2].split('~');
				if(propVal.length>0){
					$('#mode')[0].attributes[0].value = propVal[0];
					$('#boundaryId')[0].attributes[0].value = propVal[1];
				}
				$('#selectedModeBndry').val(valArray[0]+"-"+valArray[1]);
			}
		} else if($('#mode')[0].attributes[0].value =='block'){
			if(valArray.length>0){
				var blockVal=valArray[1].split('~');
				if(blockVal.length>0){
					$('#mode')[0].attributes[0].value = blockVal[0];
					$('#boundaryId')[0].attributes[0].value = blockVal[1];
				}
				$('#selectedModeBndry').val(valArray[0]);
			}
		} else if($('#mode')[0].attributes[0].value=='ward'){
			if(valArray.length>0){
				var wardVal=valArray[0].split('~');
				if(wardVal.length>0){
					$('#mode')[0].attributes[0].value = wardVal[0];
					$('#boundaryId')[0].attributes[0].value = wardVal[1]; 
				}
				$('#selectedModeBndry').val('');
			}
		} 
		callAjaxByBoundary(e); 
	});

});

function setHiddenValueByLink(obj, param,event,boundaryId) {
	$('input[name=' + $(obj).data('hiddenele') + ']')
	.val($(obj).data('eleval'));   
	if(param.attributes[0].value=='property'){
		window.open("/ptis/view/viewProperty-viewForm.action?propertyId="+boundaryId, '', 'scrollbars=yes,width=1000,height=700,status=yes');
	} else{
		$('#boundaryId')[0].attributes[0].value = boundaryId;
		if(param.attributes[0].value=='zone'){
			$('#mode')[0].attributes[0].value = "ward";
		} else if(param.attributes[0].value=='ward'){
			$('#mode')[0].attributes[0].value = "block";
		} else if(param.attributes[0].value=='block'){ 
			$('#mode')[0].attributes[0].value = "property";   
		} 
		callAjaxByBoundary(event); 
	}
}

function callAjaxByBoundary(event) {
	var modeVal = "";
	var boundary_Id = "";
	var connectiontype = "";
	var selectedModeBndry = "";
	var temp="";
	modeVal = $('#mode')[0].attributes[0].value; 
	connectiontype = $('#connectionType').val(); 
	if(modeVal=='zone'){
		boundary_Id = $('#zones').val();
		temp=modeVal+"~"+boundary_Id;
		$('#selectedModeBndry').val(temp);
		$('#report-backbutton').hide();
	}
	else{
		boundary_Id = $('#boundaryId')[0].attributes[0].value; 
		temp=$('#selectedModeBndry').val()+"-"+modeVal+"~"+boundary_Id;
		$('#selectedModeBndry').val(temp);
		$('#report-backbutton').show();
	}
	selectedModeBndry = $('#selectedModeBndry').val();
	$('.report-section').removeClass('display-hide');
	$('#report-footer').show();
	event.preventDefault();
	var reportdatatable = drillDowntableContainer
			.dataTable({
				type : 'GET',
				responsive : true,
				destroy : true,
				ajax : {
					url : "/wtms/reports/dCBReportList",      
					data : {
						'mode' : modeVal,
						'boundaryId' : boundary_Id,
						'connectionType' : connectiontype,
						'selectedModeBndry' : selectedModeBndry
					}
				},
				"sPaginationType" : "bootstrap",
				"autoWidth" : true,
				"bDestroy" : true,
				"sDom" : "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-md-3 col-xs-12'i><'col-md-3 col-xs-6 col-right'l><'col-xs-12 col-md-3 col-right'<'export-data'T>><'col-md-3 col-xs-6 text-right'p>>",
				"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ],
				"oTableTools" : {
					"sSwfPath" : "../../../../../../egi/resources/global/swf/copy_csv_xls_pdf.swf",
					"aButtons" : [ "xls", "pdf", "print" ]
				},
				columns : [{
							"data" : function(row, type, set, meta){
								if(modeVal!='property'){
									return { name:row.boundaryName, id:row.id };
								}
								else {
									return { name:row.hscno, id:row.propertyid };
								}
							},
							"render" : function(data, type, row) {
								return '<a href="javascript:void(0);" onclick="setHiddenValueByLink(this,mode,event,'+data.id +');" data-hiddenele="boundaryId" data-eleval="'
										+ data.id + '">' + data.name + '</a>';
							},
							"sTitle" : "Number"
						},{
							"data" : "username",
							"sTitle" : "User Name"
						},
						{
							"data" : "arr_demand",
							"sTitle" : "Arrears"
						}, {
							"data" : "curr_demand",
							"sTitle" : "Current"
						}, {
							"data" : "total_demand",
							"sTitle" : "Total"
						}, {
							"data" : "arr_coll",
							"sTitle" : "Arrears"
						}, {
							"data" : "curr_coll",
							"sTitle" : "Current"
						}, {
							"data" : "total_coll",
							"sTitle" : "Total"
						}, {
							"data" : "arr_balance",
							"sTitle" : "Arrears"
						}, {
							"data" : "curr_balance",
							"sTitle" : "Current"
						}, {
							"data" : "total_balance",
							"sTitle" : "Total"
						}],
				"footerCallback" : function(row, data, start, end, display) {
					var api = this.api(), data;
					if (data.length == 0) {
						$('#report-footer').hide();
					} else {
						$('#report-footer').show();
					}
					if (data.length > 0) {
						updateTotalFooter(2, api);
						updateTotalFooter(3, api);
						updateTotalFooter(4, api);
						updateTotalFooter(5, api);
						updateTotalFooter(6, api);
						updateTotalFooter(7, api);
						updateTotalFooter(8, api);
						updateTotalFooter(9, api);
						updateTotalFooter(10, api);
						
					}
				},
				"aoColumnDefs" : [ {
					"aTargets" : [2, 3,4,5,6,7,8,9,10],
					"mRender" : function(data, type, full) {
						return formatNumberInr(data);    
					}
				} ]
			});
	jQuery('.loader-class').modal('hide');
	if(modeVal!='property'){
		reportdatatable.fnSetColumnVis(1, false);
	}else{
		reportdatatable.fnSetColumnVis(1, true);
	}
	
}


function updateTotalFooter(colidx, api) {
	// Remove the formatting to get integer data for summation
	var intVal = function(i) {
		return typeof i === 'string' ? i.replace(/[\$,]/g, '') * 1
				: typeof i === 'number' ? i : 0;
	};

	// Total over all pages
	total = api.column(colidx).data().reduce(function(a, b) {
		return intVal(a) + intVal(b);
	});

	// Total over this page
	pageTotal = api.column(colidx, {
		page : 'current'
	}).data().reduce(function(a, b) {
		return intVal(a) + intVal(b);
	}, 0);

	// Update footer
	$(api.column(colidx).footer()).html(
			formatNumberInr(pageTotal) + ' (' + formatNumberInr(total)
					+ ')');
}


//inr formatting number
function formatNumberInr(x) {
	if (x) {
		x = x.toString();
		var afterPoint = '';
		if (x.indexOf('.') > 0)
			afterPoint = x.substring(x.indexOf('.'), x.length);
		x = Math.floor(x);
		x = x.toString();
		var lastThree = x.substring(x.length - 3);
		var otherNumbers = x.substring(0, x.length - 3);
		if (otherNumbers != '')
			lastThree = ',' + lastThree;
		var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",")
				+ lastThree + afterPoint;
		return res;
	}
	return x;
}