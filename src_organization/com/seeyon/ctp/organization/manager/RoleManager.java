/**
 * $Author: gaohang $
 * $Rev: 23776 $
 * $Date:: 2013-05-24 15:22:24#$:
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
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.privilege.bo.PrivTreeNodeBO;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.annotation.AjaxAccess;

public interface RoleManager {

    /**
     * 将角色赋予某一些entity(授权给当前登录单位)
     * @param roleId
     * @param entityIds
     * @throws BusinessException
     */
    public void batchRole2Entity(Long roleId, String entityIds) throws BusinessException;

    /**
     * 将角色赋予某一些entity(授权给当实体的所属单位),用于业务生成器这类的跨单位授权的情况
     * @param roleId
     * @param entityIds
     * @throws BusinessException
     */
    public void batchRole2EntityToEntAccount(Long roleId, String entityIds) throws BusinessException;

    /**
     * 给角色分配人员
     * @param roleName
     * @param memberIds
     * @throws BusinessException
     */
    public void batchRole2Member(String roleName, Long accountId, String memberIds) throws BusinessException;
    /**
     * @param sortId 角色编号
     * @return 角色编号是否重复
     */
    public boolean checkDulipCode(V3xOrgRole role);
    
    /**
     * 判断角色名称是否重复
     * @param role
     * @return
     */
    public boolean checkDulipName(V3xOrgRole role);

    /**
     * @param sortId 排序号
     * @return 排序号是否重复
     */
    public boolean checkDulipSortId(int sortId);

    /**
      * 滴滴集成插件存在的情况下，需要检查此项：
      * 部门用车管理员存在则说明插件存在
      * 一个人仅能任职一个部门的“部门用车管理员”，否则要给出提示
      * @param roleId
      * @param memberId
      * @throws BusinessException
      */
    public String checkOnlyOneRole(Long roleId, String memberList,Long deptId) throws BusinessException;
    
    @AjaxAccess
	public Map<String, String> checkRoles(String roleIds, Long memberId, Long deptId) throws BusinessException;

    /**
     * 复制角色
     * @param rolelist
     * @param type
     * @throws BusinessException
     */
    void copyrole(List<Map<String,Object>> rolelistmap,String type) throws BusinessException;

    /**
     * 新建角色
     * @param role 要创建的角色
     * @return 创建角色的ID
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public Long createRole(V3xOrgRole role) throws BusinessException;
    
    /**
     * 设置默认角色
     * @param roles
     * @throws BusinessException
     */
    public void defultRole(Long roles) throws BusinessException;
    
    /**
     * 删除角色
     * @param roles 要删除的角色ID数组
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public void deleteRole(Long[] roles) throws BusinessException;
    /**
     * 删除角色
     * @param role 角色对象
     * @param whetherEnforced 是否强制删除
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public void deleteRole(Long roleId, Boolean whetherEnforced) throws BusinessException;
    
    /**
     * 删除角色分配的人员
     * @param rolename
     * @param entityIds
     * @throws BusinessException
     */
    public void delRole2Entity(String rolename, Long accountId, String entityIds) throws BusinessException;
    /**
     * 停用角色
     * @param roles
     * @throws BusinessException
     */
    public void disenableRole(Long[] roles) throws BusinessException;
    
    /**
     * 启用角色
     * @param roles
     * @throws BusinessException
     */
    public void enableRole(Long[] roles) throws BusinessException;
    /**
     * 根据角色ID得到角色
     * @param role 角色ID
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public V3xOrgRole findById(Long role) throws BusinessException;
    /**
     * 根据查询条件找到所有满足条件的角色
     * @param fi 翻页信息对象
     * @param param 查询条件Map
     * @return 找到的角色对象列表或者返回null在没有符合条件
     */
    public FlipInfo findRoles(FlipInfo fi, @SuppressWarnings("rawtypes") Map param) throws BusinessException;
    /**
     * 根据单位ID去获取一个单位内的默认角色<br>
     * 一个单位内只有一个默认角色<br>
     * 如果没有默认角色返回null
     * @param accountId
     * @throws BusinessException
     */
    public V3xOrgRole getDefultRoleByAccount(Long accountId) throws BusinessException;
    
    /**
     * 读取是否开启各单位管理全新啊
     * @return
     * @throws BusinessException
     */
    public String getGroupPrivType() throws BusinessException;
    
    /**
     * @return 当前单位角色的最大排序号
     */
    public int getMaxSortId(Long accountId);
    /**
     * 设置是否开启各单位管理全新啊
     * @param type
     * @throws BusinessException
     */
    public void setGroupPrivType(String type) throws BusinessException;
    /**
     * 获取角色下的所有人员
     */
    public FlipInfo showMembers4Role(FlipInfo fi, Map params) throws BusinessException;
    /**
     * 同步集团角色
     * type:1:add,2:modfiy,3:delete
     * @throws BusinessException
     */
    public void SycGroupRole(V3xOrgRole role,int type) throws BusinessException;
    
   
    /**
     * 更新角色
     * @param role 要创建的角色
     * @return 更新角色的ID
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public Long updateRole(V3xOrgRole role) throws BusinessException;

    /**
     * 根据选择的树节点更新角色资源关联
     * @param nodes 树节点对象
     * @param roleId 角色ID
     * @return 返回信息
     */
    public void updateRoleResource(List<PrivTreeNodeBO> nodes, Long roleId) throws BusinessException;

	/**
     * 根据选择的树节点更新角色资源关联
     * @param nodes 树节点对象
     * @param roleId 角色ID
     * @param reset 是否清除指定角色原有的绑定资源
     * @return 返回信息
     */
    public void updateRoleResource(List<PrivTreeNodeBO> nodes, Long roleId, boolean reset) throws BusinessException;
    /**
     * 
     * @方法名称: deleteRoleMenu
     * @功能描述: 根据角色id删除角色和菜单关系
     * @参数 ：@param roleId 角色ID
     * @参数 ：@throws BusinessException
     */
    public void deleteRoleMenu(Long roleId) throws BusinessException;
    
    /**
     * 同步集团角色
     */
    public void SycGroupRoleAdd(V3xOrgRole role) throws BusinessException;

    /**
     * 校验公文角色菜单
     * @param nodes
     * @param roleId
     * @throws BusinessException
     */
	public String checkGovdocRoleResource(List nodes, Long roleId) throws BusinessException;

}