<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<form:form role="form" action="create" modelAttribute="relation" id="relationform" cssClass="form-horizontal form-groups-bordered" enctype="multipart/form-data">
<%@ include file="relation-form.jsp"  %> </div></div></div></div> 
<div class="form-group"><div class="text-center"><button type='submit' class='btn btn-primary' id="buttonSubmit"><spring:message code='lbl.create'/></button><a href='javascript:void(0)' class='btn btn-default' onclick='self.close()'><spring:message code='lbl.close' /></a></div></div></form:form> <script> $('#buttonSubmit').click(function(e){ if($('form').valid()){ }else{ e.preventDefault(); }  });</script>