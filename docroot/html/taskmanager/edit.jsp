<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.util.UnicodeFormatter"%>
<%@page import="com.pmc.model.Task"%>
<%@page import="com.pmc.Priority"%>
<%@ include file="/init.jsp" %>

<portlet:renderURL var="cancelURL" />

<%	Task task = (Task)renderRequest.getAttribute("task"); %>

<liferay-ui:header backURL="${cancelURL}"  title="${title}" />

<liferay-ui:error key="ko-task-updated" message="ko-task-updated" />

<aui:model-context bean="${task}" model="<%= Task.class %>" />

<aui:form name="fm" action="${saveTaskURL}" method="post">

	<aui:input name="taskId" type="hidden" value="${task.taskId}"/>
	
	<div class="column_50">
		
		<aui:input name="name" label="name" value="${task.name}">
			<aui:validator name="required" errorMessage="field.required"></aui:validator>
		</aui:input>
		
		<aui:field-wrapper label="description">
			<liferay-ui:input-editor name="description" width="100%" initMethod="initEditorDescription" />
			<aui:input name="description" type="hidden" />
				<script type="text/javascript">
        			function <portlet:namespace />initEditorDescription() { return "<%= UnicodeFormatter.toString(task.getDescription()) %>"; }
   				</script>
		</aui:field-wrapper>		
	
	</div>
	<div class="column_50">
	
		<aui:select name="prioritySel" label="task.priority" ignoreRequestValue="true">
			<c:forEach items="${priorities}" var="priority">
				<aui:option value="${priority}" label="<%=Priority.priority[Integer.parseInt(pageContext.getAttribute(\"priority\").toString())]%>" selected="${priority == task.priority}"/>
			</c:forEach>
		</aui:select>
		
		<aui:field-wrapper name="dueDate" label="task.due-date">
			<liferay-ui:input-date  yearRangeEnd="${defaultEndYear }" yearRangeStart="${defaultStartYear}"  dayParam="dueDateDay" monthParam="dueDateMon"
				 yearParam="dueDateYear"  yearNullable="false" dayNullable="false" monthNullable="false" yearValue="${dueDateYear}" monthValue="${dueDateMon}" dayValue="${dueDateDay}"></liferay-ui:input-date>	
		</aui:field-wrapper>
	
	</div>
	
	<aui:button-row>
		<aui:button type="submit"></aui:button>	
		<aui:button onClick="${cancelURL}"  type="cancel" />
	</aui:button-row>
</aui:form>