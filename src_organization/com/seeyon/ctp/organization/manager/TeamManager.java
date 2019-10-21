/**
 * $Author: $
 * $Rev: $
 * $Date:: 2012-06-05 15:14:56#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */

package com.seeyon.ctp.organization.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.FlipInfo;


public interface TeamManager {
	/**
	 * 组管理进入页面
	 * @return
	 * @throws Exception
	 */
	public HashMap addTeam(String accountId) throws BusinessException;
	/**
	 * 保存组
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public Long saveTeam(String accountId, Map team)  throws BusinessException;
	/**
	 * 删除组
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public String deleteTeam(List<Map<String,Object>> team) throws BusinessException;
	/**
	 * 读取某个部门的详细信息
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public HashMap viewTeam(Long teamId) throws BusinessException;
	
	/**
	 * 获取组位列表
	 * @param fi
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	FlipInfo showTeamList(FlipInfo fi, Map params) throws BusinessException;
	
	 /**
     * 批量修改组中的人员
     * @param fi
     * @param params
     * @return
     */
	public boolean saveTeamMembers(String reamIds, String memberIds, String replaceMemberId, String type, String accountId) throws BusinessException;
	
	/**
	 * @param fi
	 * @return
	 * @throws BusinessException 
	 */
	public FlipInfo getBatchResult(FlipInfo fi,Map params) throws BusinessException;
}