/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
$(document).ready(function() {
	
					oTable=	$('#view-appConfig-type').dataTable(
									{
										processing : true,
										serverSide : true,
										type : 'GET',
										sort : true,
										filter : true,
										responsive : true,
										destroy : true,
										"autoWidth": false,
										ajax : "/egi/appConfig/ajax/result",
										"aLengthMenu" : [ [ 10, 25, 50, -1 ],
															[ 10, 25, 50, "All" ] ],
													"sDom" : "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-md-6 col-xs-12'i><'col-md-3 col-xs-6'l><'col-md-3 col-xs-6 text-right'p>>",
										columns : [ {
											"mData" : "module",
											"sTitle" : "Module Name"
										}, 
										 {
											"mData" : "keyName",
											"sTitle" : "Key Name",
										},/*{
											"mData" : "Values",
											"sTitle" : "Department"
										}, {
											"mData" : "locationRequired",
											"sTitle" : "Location Required"
										}, {
											"mData" : "isActive",
											"sTitle" : "Active"
										},*/{
											"mData" : "description",
											"sTitle" : "Description"
										} ,{
											"mData" : "routerId",
											"visible": false
										}],
									});

				/*	$('#view-appConfig-type tbody').on(
							'click',
							'tr',
							function() {
								if ($(this).hasClass('apply-background')) {
									$(this).removeClass('apply-background');
								} else {
									$('#view-appConfig-type tbody tr')
											.removeClass('apply-background');
									$(this).addClass('apply-background');
								}

							});*/
					
					$('#view-appConfig-type').on('click','tbody tr',function() {
						if ($(this).hasClass('apply-background')) {
							$(this).removeClass('apply-background');
						}else {
							$('#view-appConfig-type tbody tr')
									.removeClass('apply-background');
							$(this).addClass('apply-background');
						}
						
						oTable.$('tr.row_selected').removeClass('row_selected');
				        $(this).addClass('row_selected');
				        var fid= oTable.fnGetData( this, 3 );
				      $('.position-modal').modal('show');
				        window.open('/egi/appConfig/viewList/'+ fid, '', 'height=800,width=800');
					
					});
					

				});

