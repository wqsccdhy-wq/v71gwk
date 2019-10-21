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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.util.FlipInfo;


public interface BusinessDepartmentManager {
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
	 * 异步展示部门树
	 * @return
	 * @throws Exception
	 */
	public List showDepartmentTree(Map params) throws Exception;
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
	 * 批量删除部门
	 * @param post
	 * @return
	 * @throws Exception
	 */
	public Object deleteDepts(List<Map<String,Object>> post) throws Exception;
	
	/**
	 * 部分成员
	 * @param fi
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public FlipInfo deptMembers(FlipInfo fi, Map params) throws Exception;
	
	/**
	 * 部门列表
	 * @param fi
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	public FlipInfo showDepList(FlipInfo fi, Map params) throws BusinessException;
	
	/**
	 * 部门角色处理（导入的时候会调用）
	 * @param map
	 * @param dept
	 * @param rolelist
	 * @throws BusinessException
	 */
	public void dealDeptRole(Map map, V3xOrgDepartment dept, List<V3xOrgRole> rolelist)throws BusinessException;
	
	/**
	 * 用于选人界面回填的部门成员信息
	 * @param deptId
	 * @return
	 * @throws Exception
	 */
	public HashMap deptMemberElements(Long deptId) throws Exception;
	
	/**
	 * 维护部门成员
	 * @param deptId
	 * @param deptMember
	 * @throws Exception
	 */
	public void saveDeptMembers(Long deptId, String deptMember) throws Exception;
	
}