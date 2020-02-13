<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.pmc.Priority"%>
<%@ include file="/init.jsp" %>

<portlet:renderURL var="newTaskURL">
	<portlet:param name="view" value="edit-task"></portlet:param>
	<portlet:param name="taskId" value="-1"></portlet:param>
</portlet:renderURL>

<liferay-ui:success key="ok-task-updated" message="ok-task-updated"/>
<liferay-ui:success key="ok-task-deleted" message="ok-task-deleted"/>
<liferay-ui:error key="ko-task-deleted" message="ko-task-deleted"/>

<aui:form name="newTaskFm" action="${newTaskURL}">
	<aui:button-row>
		<aui:button type="submit" value="new-task"/>
	</aui:button-row>
</aui:form>

<liferay-ui:search-container
			searchContainer="${searchContainer}"
			iteratorURL="${searchContainer.iteratorURL}" >
	<liferay-ui:search-container-results results="${searchContainer.results}" total="${searchContainer.total}"/>
		<liferay-ui:search-container-row className="com.pmc.model.Task" keyProperty="taskId" modelVar="task">			
			
			<c:set var="accomplishedClass" value="not-accomplished"></c:set>
			<c:if test="${task.accomplished}">
				<c:set var="accomplishedClass" value="accomplished"></c:set>
			</c:if>
			
			<liferay-ui:search-container-column-text name="task.accomplished" cssClass="${accomplishedClass}">
				<portlet:actionURL var="toggleAccomplishedURL" name="toggleAccomplished">
					<portlet:param name="taskId" value="${task.taskId}"></portlet:param>
				</portlet:actionURL>
				<aui:a  href="<%= toggleAccomplishedURL.toString() %>">
					<c:choose>
						<c:when test="${task.accomplished}">
							<img src="<%=request.getContextPath()%>/img/checked.png" alt="true"/>
						</c:when>
						<c:otherwise>
							<img src="<%=request.getContextPath()%>/img/square.png" alt="false"/>
						</c:otherwise>	
					</c:choose>	
				</aui:a>		
			</liferay-ui:search-container-column-text>
			<liferay-ui:search-container-column-text name="task.name" cssClass="${accomplishedClass}">
				${task.name}		
			</liferay-ui:search-container-column-text>		
			<liferay-ui:search-container-column-text name="task.priority" cssClass="${accomplishedClass}">
				<%=Priority.priority[task.getPriority()] %>
			</liferay-ui:search-container-column-text>		
			<liferay-ui:search-container-column-text name="task.dueDate" cssClass="${accomplishedClass}">
				${task.dueDate}
			</liferay-ui:search-container-column-text>		
			<liferay-ui:search-container-column-text name="task.actions" cssClass="${accomplishedClass}">
				<liferay-ui:icon-menu>
					<portlet:renderURL var="editURL">
						<portlet:param name="taskId" value="${task.taskId}" />
						<portlet:param name="view" value="edit-task" />
					</portlet:renderURL>					
					<liferay-ui:icon image="edit" message="edit" url="${editURL}" />
					<liferay-ui:icon image="delete" message="delete" url="javascript:${renderResponse.getNamespace()}deleteTask('${task.taskId}');" />					
				</liferay-ui:icon-menu>
			</liferay-ui:search-container-column-text>
		
		</liferay-ui:search-container-row>
	<liferay-ui:search-iterator />

</liferay-ui:search-container>

<portlet:actionURL name="deleteTask" var="deleteTaskURL"/>

<aui:form name="deleteTask" action="${deleteTaskURL}" method="POST">
		<aui:input type="hidden" name="taskId" value=""/>
</aui:form>

<script type="text/javascript">
	function <portlet:namespace />deleteTask(taskId) {
		if(confirm('<%=LanguageUtil.get(pageContext,"are-you-sure-you-want-to-delete-this")%>')){
			document.<portlet:namespace />deleteTask.<portlet:namespace />taskId.value = taskId;
			document.<portlet:namespace />deleteTask.submit();
		}else{
			self.focus(); 
		}
	}
</script>