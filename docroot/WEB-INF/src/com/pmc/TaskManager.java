package com.pmc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.pmc.model.Task;
import com.pmc.model.impl.TaskImpl;
import com.pmc.service.TaskLocalServiceUtil;


/**
 * Portlet implementation class TaskManager
 */
public class TaskManager extends MVCPortlet {
	
	private static Log log = LogFactoryUtil.getLog(TaskManager.class);
	protected String viewJSP;
	protected String editJSP;
	
	@Override
	public void init() throws PortletException {
		// View Mode Pages
		viewJSP = getInitParameter("view-template");
		editJSP = getInitParameter("edit-template");
	}

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

		String jsp = renderRequest.getParameter("view");

		PortletURL renderURL = renderResponse.createRenderURL();
		renderURL.setParameter("javax.portlet.action", "doView");
		renderRequest.setAttribute("renderURL", renderURL.toString());

		log.debug("::DOVIEW::"+jsp);

		try{
			if (jsp == null || jsp.equals("")) {
				showViewDefault(renderRequest, renderResponse);
			}else if(jsp.equals("edit-task")){
				showViewEditTask(renderRequest, renderResponse);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}


	public void showViewDefault(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {		

		log.debug("::showViewDefault::");
	
		SearchContainer<Task> searchContainer = new SearchContainer<>(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, 
				ParamUtil.getInteger(renderRequest, SearchContainer.DEFAULT_DELTA_PARAM,SearchContainer.DEFAULT_DELTA), renderResponse.createRenderURL(), 
				null, "there-are-no-results");
		
		try {
			List<Task> tasks = TaskLocalServiceUtil.getTasks(searchContainer.getStart(), searchContainer.getEnd());
			int total = TaskLocalServiceUtil.getTasksCount();
			
			searchContainer.setTotal(total);
			searchContainer.setResults(tasks);
		} catch (SystemException e) {
			log.error(e);
		}
		
		renderRequest.setAttribute("searchContainer", searchContainer);
		
		include(viewJSP, renderRequest, renderResponse);
	}
	
	public void showViewEditTask(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException, PortalException, SystemException {
		long taskId = ParamUtil.getLong(renderRequest, "taskId", -1);
		log.debug("Show View edit task::  taskId: "+ taskId);
		
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		TimeZone timeZone = themeDisplay.getTimeZone();
		SimpleDateFormat formatDay = new SimpleDateFormat("dd");
		formatDay.setTimeZone(themeDisplay.getTimeZone());
		SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
		formatMonth.setTimeZone(timeZone);
		SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
		formatYear.setTimeZone(timeZone);	
				
		Date dueDate=new Date(System.currentTimeMillis());
		
		Task task = null;
		if(taskId < 0){ //NEW TASK
			task = new TaskImpl();			
			task.setDescription("");	
			task.setTaskId(-1);
		}
		else{
			task = TaskLocalServiceUtil.getTask(taskId);		
			dueDate = task.getDueDate();
		}	
		renderRequest.setAttribute("task", task);
		
		PortletURL backURL = renderResponse.createRenderURL();
		backURL.setParameter("javax.portlet.action", "doView");
		renderRequest.setAttribute("backURL", backURL.toString());
		
		PortletURL saveTaskURL = renderResponse.createActionURL();
		saveTaskURL.setParameter("javax.portlet.action", "saveTask");
		renderRequest.setAttribute("saveTaskURL", saveTaskURL.toString());
		
		renderRequest.setAttribute("dueDateDay", Integer.parseInt(formatDay.format(dueDate)));
		renderRequest.setAttribute("dueDateMonth", Integer.parseInt(formatMonth.format(dueDate))-1);
		renderRequest.setAttribute("dueDateYear", Integer.parseInt(formatYear.format(dueDate)));
		renderRequest.setAttribute("defaultStartYear", Integer.parseInt(formatYear.format(new Date())));
		renderRequest.setAttribute("defaultEndYear", Integer.parseInt(formatYear.format(new Date()))+10);
		renderRequest.setAttribute("priorities", Priority.aPriority);
		     
        include(editJSP, renderRequest, renderResponse);			
	} 
	
	@ProcessAction(name="saveTask")
	public void saveTask(ActionRequest request, ActionResponse response){
		
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		
		long taskId = ParamUtil.getLong(request, "taskId", -1);			
		String name = ParamUtil.getString(request, "name");
		String description = ParamUtil.getString(request, "description");
		int priority = ParamUtil.getInteger(request, "prioritySel");		
		int dueDateMonth = ParamUtil.getInteger(request, "dueDateMon");
		int dueDateYear = ParamUtil.getInteger(request, "dueDateYear");
		int dueDateDay = ParamUtil.getInteger(request, "dueDateDay");
				
		Calendar dueDateCalendar = Calendar.getInstance(themeDisplay.getUser().getTimeZone());
		dueDateCalendar.set(Calendar.YEAR, dueDateYear);
		dueDateCalendar.set(Calendar.MONTH, dueDateMonth);
		dueDateCalendar.set(Calendar.DAY_OF_MONTH, dueDateDay);	
							
		if (log.isDebugEnabled()){
			log.debug("taskId: "+ taskId);
			log.debug("name: "+ name);
			log.debug("description: "+ description);
			log.debug("priority: "+ priority);
			log.debug("dueDate: "+ dueDateCalendar.getTime());
		}	
		try {		
			if(taskId < 0){
				//NEW TASK				
				TaskLocalServiceUtil.addTask(themeDisplay.getCompanyId(), themeDisplay.getUserId(), name, description, priority, dueDateCalendar.getTime());
			}else{
				//EDIT TASK
				TaskLocalServiceUtil.updateTask(taskId, name, description, priority, dueDateCalendar.getTime());
			}		
					
			SessionMessages.add(request, "ok-task-updated");
		
		} catch (SystemException e) {
			SessionErrors.add(request, "ko-task-updated");
			response.setRenderParameter("view", "edit-task");
			response.setRenderParameter("taskId",String.valueOf(taskId));
			log.error(e);		
		} 
		
	}
	
	
	@ProcessAction(name="toggleAccomplished")
	public void toggleAccomplished(ActionRequest request, ActionResponse response){
		long taskId = ParamUtil.getLong(request, "taskId", -1);			
		
		try {
			Task task = TaskLocalServiceUtil.getTask(taskId);
			task.setAccomplished(!task.getAccomplished());
			TaskLocalServiceUtil.updateTask(task);
		} catch (PortalException | SystemException e) {
			log.error(e);
		}
	}
	
	@ProcessAction(name="deleteTask")
	public void deleteTask(ActionRequest request, ActionResponse response){
		long taskId = ParamUtil.getLong(request, "taskId", -1);			
		
		try {
			TaskLocalServiceUtil.deleteTask(taskId);
			SessionMessages.add(request, "ok-task-deleted");
		} catch (PortalException | SystemException e) {
			log.error(e);
			SessionErrors.add(request, "ko-task-deleted");
		}
	}
	
	@Override
	protected void include(String path, RenderRequest renderRequest,RenderResponse renderResponse) throws IOException, PortletException {

		PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(path);

		if (portletRequestDispatcher != null) {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}
}
