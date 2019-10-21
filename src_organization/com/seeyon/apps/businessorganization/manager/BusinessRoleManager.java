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

package com.seeyon.apps.businessorganization.manager;

import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.FlipInfo;

public interface BusinessRoleManager {

	/**
	 * 角色列表
	 * @param fi
	 * @param param
	 * @return
	 * @throws BusinessException
	 */
	FlipInfo getRoleList(FlipInfo fi, Map param) throws BusinessException;

	/**
	 * 最大排序号
	 * @param accountId
	 * @return
	 */
	int getMaxSortId(Long accountId);
	
	/**
	 * 创建角色
	 * @param accountId
	 * @param role
	 * @return
	 * @throws BusinessException
	 */
	Map createRole(String accountId, Map role) throws BusinessException;

	/**
	 * 更新角色
	 * @param accountId
	 * @param role
	 * @return
	 * @throws BusinessException
	 */
	Map updateRole(String accountId, Map role) throws BusinessException;

	/**
	 * 删除角色
	 * @param roles
	 * @return
	 * @throws BusinessException
	 */
	Map deleteRole(Long[] roles) throws BusinessException;

	/**
	 *查看角色信息
	 * @param roleId
	 * @return
	 * @throws BusinessException
	 */
	Map viewRole(Long roleId) throws BusinessException;
	
}