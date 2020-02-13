/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.pmc.service.impl;

import java.sql.Timestamp;
import java.util.Date;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.pmc.model.Task;
import com.pmc.service.base.TaskLocalServiceBaseImpl;

/**
 * The implementation of the task local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.pmc.service.TaskLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author JE10798
 * @see com.pmc.service.base.TaskLocalServiceBaseImpl
 * @see com.pmc.service.TaskLocalServiceUtil
 */
public class TaskLocalServiceImpl extends TaskLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.pmc.service.TaskLocalServiceUtil} to access the task local service.
	 */
	private static Log log = LogFactoryUtil.getLog(TaskLocalServiceImpl.class);
	
	public Task addTask(long companyId, long userId, String name, String description, int priority, Date dueDate) throws SystemException{
		
		Task newTask = taskPersistence.create(counterLocalService.increment(Task.class.getName()));
					
		newTask.setCompanyId(companyId);				
		newTask.setCreateDate(new Timestamp(System.currentTimeMillis()));
		newTask.setModifiedDate(null);
		newTask.setUserId(userId);
				
		newTask.setName(name);
		newTask.setDescription(description);
		newTask.setDueDate(dueDate);
		newTask.setPriority(priority);
		newTask.setAccomplished(false);
	
		newTask = taskPersistence.update(newTask, false);
	
		return newTask;
	}
	
	public Task updateTask(long taskId, String name, String description, int priority, Date dueDate) throws SystemException{
		
		Task task = taskPersistence.fetchByPrimaryKey(taskId);
			
		task.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				
		task.setName(name);
		task.setDescription(description);
		task.setDueDate(dueDate);
		task.setPriority(priority);
		
		return taskPersistence.update(task, true);
	}
}