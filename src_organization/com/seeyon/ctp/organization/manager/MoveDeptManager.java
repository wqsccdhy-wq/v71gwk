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

import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgResult;


public interface MoveDeptManager {
	
	/**
	 * 调整部门
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public List<WebV3xOrgResult> movedept(Map team)  throws BusinessException;

	/**
	 * 调整部门动作Manager
	 * @param deptId
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	public List<String[]> moveDept(Long deptId, Long accountId) throws BusinessException;
	
	/**
	 * 调整部门的校验方法
	 * @param deptId
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	public List<String> validateMoveDept(Long deptId, Long accountId) throws BusinessException;
}