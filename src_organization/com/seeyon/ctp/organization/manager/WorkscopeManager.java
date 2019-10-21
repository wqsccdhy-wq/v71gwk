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
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.FlipInfo;


public interface WorkscopeManager {
	/**
	 * 部门管理进入页面
	 * @return
	 * @throws Exception
	 */
	public String editScope(String accountId) throws Exception;
	/**
	 * 新建一个部门
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public void saveScope(String accountId, String scope) throws Exception;
	/**
	 * 删除一个部门
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public HashMap showScope(Long accountId, String levelscope) throws Exception;
	
	/**
	 * 获取岗位列表
	 * @param fi
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	FlipInfo showWorkscopeList(FlipInfo fi, Map params) throws BusinessException;
	

}