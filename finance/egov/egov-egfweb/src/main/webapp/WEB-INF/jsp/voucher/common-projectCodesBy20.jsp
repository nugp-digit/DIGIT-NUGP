<%@ page contentType="text/json"%><%@ taglib prefix="s"
	uri="/WEB-INF/tags/struts-tags.tld"%><s:if test="fundId==0">Please select Fund</s:if>
<s:else>
	<s:if test="projectCodeStringList.size() == 0"> Nothing found to display</s:if>
	<s:else>
		<s:iterator var="pc" value="projectCodeStringList" status="status">
			<s:property value="toString()" />~^</s:iterator>
	</s:else>
</s:else>
