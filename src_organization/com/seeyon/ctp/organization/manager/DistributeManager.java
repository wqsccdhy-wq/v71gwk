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


public interface DistributeManager {
	/**
	 * 组管理进入页面
	 * @return
	 * @throws Exception
	 */
	public HashMap addTeam() throws BusinessException;
	/**
	 * 保存组
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public Long saveTeam(Map team)  throws BusinessException;
	/**
	 * 删除未分配人员
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public String deleteMember(List<Map<String,Object>> Member) throws BusinessException;
	
	/**
	 * 获取组位列表
	 * @param fi
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	FlipInfo showDistributeList(FlipInfo fi, Map params) throws BusinessException;
	/**
	 * 调整未分配人员
	 * @param Member
	 */
	void saveDistributeMessage(List<Map<String,Object>> Member,String accId) throws BusinessException;
	

}