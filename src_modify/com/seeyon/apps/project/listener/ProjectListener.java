/**
 * Author shuqi
 * Rev 
 * Date: Feb 27, 2017 6:15:42 PM
 *
 * Copyright (C) 2017 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 * @since v5 v6.1
 */
package com.seeyon.apps.project.listener;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;

import com.seeyon.apps.project.manager.ProjectQueryManager;
import com.seeyon.apps.project.manager.ProjectTypeManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.plugin.PluginAddEvent;
import com.seeyon.ctp.organization.event.AddAccountEvent;
import com.seeyon.ctp.organization.event.DeleteMemberEvent;
import com.seeyon.ctp.util.annotation.ListenEvent;

/**
 * 项目监听平台事件
 * @Copyright 	Copyright (c) 2017
 * @Company 	seeyon.com
 * @since 		v5 v6.1
 * @author		shuqi
 */
public class ProjectListener {
	
	private static final Log logger = CtpLogFactory.getLog(ProjectListener.class.getName());
	private ProjectTypeManager	projectTypeManager;
	private	ProjectQueryManager	projectQueryManager;
	public void setProjectTypeManager(ProjectTypeManager projectTypeManager) {
		this.projectTypeManager = projectTypeManager;
	}
	public void setProjectQueryManager(ProjectQueryManager projectQueryManager) {
		this.projectQueryManager = projectQueryManager;
	}



	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws BusinessException {
		try {
			logger.info("创建单位初始化该单位的项目类型开始");
			//projectTypeManager.initProjectType(evt.getAccount().getId());
			logger.info("创建单位初始化该单位的项目类型结束");
		} catch (Exception e) {
			logger.error("创建单位初始化该单位的项目类型失败", e);
			throw new BusinessException(e);
		}
	}
	
	@ListenEvent(event = PluginAddEvent.class)
	public void onPluginAdd(PluginAddEvent evt) throws BusinessException {
		Set<String> plugins = evt.getPlugins();
		if (CollectionUtils.isNotEmpty(plugins) && plugins.contains("project")) {
			//启用秀插件补偿
			try {
				logger.info("启用项目插件补偿开始 , add plugins init project data start");
				projectTypeManager.initProjectType(null);
				logger.info("启用项目插件补偿结束 , add plugins init project data end");
			} catch (Exception e) {
				logger.error("启用项目插件补偿出错了 , add plugins init project data fail", e);
				throw new BusinessException(e);
			}
		}
	}
	
	@ListenEvent(event = DeleteMemberEvent.class)
	public void onDeleteMember(DeleteMemberEvent evt) throws BusinessException {
		try {
			projectQueryManager.retakeClew(evt.getMember().getId());
		} catch (Exception e) {
			logger.error("删除人员事件监听失败！", e);
			throw new BusinessException(e);
		}
	}
}
