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
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.annotation.AjaxAccess;


public interface DepartmentManager {
	/**
	 * 部门管理进入页面
	 * @return
	 * @throws Exception
	 */
	public HashMap addDept(String accountId) throws Exception;
	/**
	 * 新建一个部门
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public Object createDept(String accountId, Map dept) throws Exception;
    /**
     * 校验部门下岗位信息是否可以被删除，如果有不能被删除的岗位信息，返回提示信息供前台确定是否继续删除 可以被删除的岗位。
     * @param dept
     * @return
     * @throws Exception
     */
	@AjaxAccess
	public String postCheck(Map dept) throws Exception;
	/**
	 * 校驗后并确认后，继续删除可以被删除的岗位信息
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	@AjaxAccess
	public Object createDeptAfterCheckPost(String accountId, Map dept) throws Exception;
	/**
	 * 异步展示部门树
	 * @return
	 * @throws Exception
	 */
	public List showDepartmentTree(Map params) throws Exception;
	/**
	 * 外部部门管理进入页面
	 * @return
	 * @throws Exception
	 */
	public HashMap addOutDept(String accountId) throws Exception;
	/**
	 * 删除一个部门
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public Object deleteDept(Map dept) throws Exception;
	/**
	 * 读取某个部门的详细信息
	 * @param dept
	 * @return
	 * @throws Exception
	 */
	public HashMap viewDept(Long deptId) throws Exception;
	
	/**
	 * 获取部门角色
	 * @return
	 * @throws Exception
	 */
	public HashMap getDepRoles(String accountId) throws Exception;
	/**
	 * 获取岗位列表
	 * @param fi
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	FlipInfo showDepList(FlipInfo fi, Map params) throws BusinessException;
	/**
	 * 批量删除部门
	 * @param post
	 * @return
	 * @throws Exception
	 */
	public Object deleteDepts(List<Map<String,Object>> post) throws Exception;
	/**
	 * 获取部门列表（外部）
	 * @param fi
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	public FlipInfo showDepList4Ext(FlipInfo fi, Map params)
			throws BusinessException;
	
	
    String dealDeptRole(Map dept, V3xOrgDepartment newdept, List<V3xOrgRole> rolelist) throws BusinessException;
	
}