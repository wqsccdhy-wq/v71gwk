/**
 * $Author: $ wff
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
import com.seeyon.ctp.common.metadata.bo.MetadataColumnBO;
import com.seeyon.ctp.organization.OrgConstants.ExternalAccessType;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.privilege.bo.PrivTreeNodeBO;

public interface JoinOrgManagerDirect {
	
    /**
     * 直接新建一个单位，此方法没有关注单位管理员，单纯新建一个单位
     * @param account
     * @return
     * @throws BusinessException
     */
    OrganizationMessage addAccount(V3xOrgAccount account) throws BusinessException;
    
	/**
	 * 修改单位（对于vjoin单位来说，只会修改名称。以后如果有多个vjoin单位的话，可能需要校验vjoin单位重名的问题TODO）
	 * @param account
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updateAccount(V3xOrgAccount account)throws BusinessException;
    
	/**
	 * 创建单位，包含单位管理员
	 * @param account
	 * @param adminMember
	 * @return
	 * @throws BusinessException
	 */
    OrganizationMessage addAccount(V3xOrgAccount account,V3xOrgMember adminMember) throws BusinessException;
	/**
	 * 新建部门
	 * @param dept
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addDepartment(V3xOrgDepartment dept) throws BusinessException;
	
	/**
	 * 新建部门（只允许新建vjoin机构的时候使用）
	 * @param dept
	 * @param subManager
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addDepartment(V3xOrgDepartment dept,V3xOrgMember subManager) throws BusinessException;

	/**
	 * 批量新建部门
	 * @param depts
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addDepartments(List<V3xOrgDepartment> depts) throws BusinessException;

	/**
	 * 获取单位、部门下的子部门
	 * @param parentDepId 父单位、部门id
	 * @param firtLayer  是否只取一级部门，true 一级，false 所有子部门
	 */
	List<V3xOrgDepartment> getChildDepartments(Long parentDepId,boolean firtLayer) throws BusinessException;
	
	/**
	 * 获取单位、部门下的子部门
	 * @param parentDepId 父单位、部门id
	 * @param firtLayer  是否只取一级部门，true 一级，false 所有子部门
	 * @param externalType  部门类型：1：外部机构，2 外部单位
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgDepartment> getChildDepartments(Long parentDepId,boolean firtLayer, Integer externalType) throws BusinessException;
	

	/**
	 * 获取外部单位、部门下的子节点（包含外机构和外单位）
	 * @param parentDepId
	 * @param firtLayer
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgDepartment> getChildUnits(Long parentDepId, boolean firtLayer) throws BusinessException;
	/**
	 * 组织模型外部实体的最大排序号，没有就返回0
	 * @param entityClassName
	 * @param accountId
	 * @param externalType
	 * @return
	 * @throws BusinessException
	 */
	Integer getMaxSortNum(String entityClassName, Long accountId,int externalType) throws BusinessException;

	/**
	 * 删除V-Join机构、单位
	 * @param dept
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage deleteDepartment(V3xOrgDepartment dept)throws BusinessException;

	/**
	 * 批量删除V-Join机构、单位
	 * @param depts
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage deleteDepartments(List<V3xOrgDepartment> depts)throws BusinessException;

	/**
	 * 获取部门下的人员
	 * @param departmentId
	 * @param firtLayer
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgMember> getMembersByDepartment(Long departmentId,boolean firtLayer) throws BusinessException;

	/**
	 * 更新外部机构
	 * @param dept
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updateDepartment(V3xOrgDepartment dept) throws BusinessException;
	
	/**
	 * 更新外部机构和管理员（特定的修改机构）
	 * @param dept
	 * @param subManager
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updateDepartment(V3xOrgDepartment dept,V3xOrgMember subManager) throws BusinessException;

	/**
	 * 批量更新外部机构
	 * @param depts
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updateDepartments(List<V3xOrgDepartment> depts) throws BusinessException;

	/**
	 * 创建岗位
	 * @param post
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addPost(V3xOrgPost post) throws BusinessException;

	/**
	 * 批量创建岗位
	 * @param posts
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addPosts(List<V3xOrgPost> posts)throws BusinessException;

	/**
	 * 更新岗位
	 * @param post
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updatePost(V3xOrgPost post) throws BusinessException;

	/**
	 * 批量更新岗位
	 * @param posts
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updatePosts(List<V3xOrgPost> posts) throws BusinessException;

	/**
	 * 删除岗位
	 * @param post
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage deletePost(V3xOrgPost post) throws BusinessException;

	/**
	 * 批量删除岗位
	 * @param posts
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage deletePosts(List<V3xOrgPost> posts) throws BusinessException;

	/**
	 * 创建人员
	 * @param member
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addMember(V3xOrgMember member) throws BusinessException;

	/**
	 * 批量创建人员
	 * @param members
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addMembers(List<V3xOrgMember> members) throws BusinessException;

	/**
	 * 修改人员
	 * @param member
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updateMember(V3xOrgMember member) throws BusinessException;

	/**
	 * 批量修改人员
	 * @param members
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updateMembers(List<V3xOrgMember> members) throws BusinessException;

	/**
	 * 删除人员
	 * @param member
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage deleteMember(V3xOrgMember member) throws BusinessException;

	/**
	 * 批量删除人员
	 * @param members
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage deleteMembers(List<V3xOrgMember> members) throws BusinessException;

	/**
	 * 获取所有外部单位
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgAccount> getAllAccounts() throws BusinessException;

	/**
	 * 获取所有部门
	 * @param accountID
	 * @param enable
	 * @param condition
	 * @param feildvalue
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgDepartment> getAllDepartments(Long accountID, Boolean enable,String condition, Object feildvalue) throws BusinessException;

	List<V3xOrgDepartment> getAllDepartments(Long accountId,Integer externalType) throws BusinessException;
	
	/**
	 * 获取单位下所有的角色
	 * @param accountID
	 * @param condition
	 * @param feildvalue
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgRole> getAllRoles(Long accountID, String condition,Object feildvalue) throws BusinessException;
	
	List<V3xOrgRole> getAllRoles(Long accountId) throws BusinessException;

	/**
	 * 获取单位下的所有岗位
	 * @param accountID
	 * @param enable
	 * @param condition
	 * @param feildvalue
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgPost> getAllPosts(Long accountID, Boolean enable,String condition, Object feildvalue) throws BusinessException;

	/**
	 * 从缓存中获取vjoin下的所有岗
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgPost> getAllPosts(Long accountId) throws BusinessException;
	/**
	 * 获取所有人员
	 * @param accountId
	 * @param enable
	 * @param condition
	 * @param feildvalue
	 * @param flipInfo
	 * @return
	 * @throws BusinessException 
	 */
	List<V3xOrgMember> getAllMembers(Long accountId, Boolean enable,String condition, Object feildvalue) throws BusinessException;

	List<V3xOrgMember> getAllMembers(Long accountId) throws BusinessException;
	
	/**
	 * 删除外部人员的互访权限
	 * @param memberId
	 * @param orgAccountId
	 * @throws BusinessException
	 */
	void deleteAccessRelation(Long memberId, Long orgAccountId) throws BusinessException;
	
	/**
	 * 按照类型删除互访权限数据（访问内部人员的权限数据   还是  被内部人员访问的权限数据）
	 * @param memberId
	 * @param orgAccountId
	 * @param externalAccessType
	 * @throws BusinessException
	 */
	void deleteAccessRelationByType(Long memberId, Long orgAccountId,ExternalAccessType externalAccessType) throws BusinessException;

	/**
	 * 处理v-join平台人员与v5平台人员的互访范围
	 * @param memberId
	 * @param accessMap
	 * @param isAdd  是否追加：true:追加， false:覆盖
	 * @throws BusinessException
	 */
	void dealExternalAccess(Long memberId, Map<String, String> accessMap,boolean isAdd) throws BusinessException;

	/**
	 * 处理V-Join平台人员与v5平台人员的互访范围的成员
	 * 处理V-Join单位与V-Join单位的访问范围
	 * 人员格式：Department|6481532690106501118,Member|-6481532690106501118
	 * 		  Department|6481532690106501118|0,Department|-6481532690106501118|1
	 * @param member
	 * @param externalAccessType  互访类型
	 * @return
	 * @throws BusinessException
	 */
	Map<String, String> getAccessMember(V3xOrgEntity vjoinEntity,String externalAccessType) throws BusinessException;

	/**
	 * 获取v-join平台外部人员能访问的v5平台人员列表
	 * @param memberId  v-join 平台的人员id
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgMember> getAccessInnerMembers(Long memberId)throws BusinessException;

	/**
	 * 获取v5平台人员能访问的v-join平台外部人员
	 * @param memberId  v5 平台的人员id
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgMember> getAccessExternalMembers(Long memberId)throws BusinessException;

	/**
	 * 获取单位下的所有有效部门角色
	 * @param accountID
	 * @param externalType
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgRole> getDepartmentRolesByAccount(Long accountID,Integer externalType) throws BusinessException;

	/**
	 * 根据角色code获取对应的角色
	 * @param code
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	V3xOrgRole getRoleByCode(String code, Long accountId)throws BusinessException;
	
	/**
	 * 获取v-join 平台下可用的外单位属性
	 * @param orgAccountId  v-join 虚拟单位id（和属性的创建者匹配）
	 * @return
	 * @throws BusinessException
	 */
	List<MetadataColumnBO> getCustomerAccountProperties(Long orgAccountId) throws BusinessException;

    /**
     * 处理vjoin人员角色的菜单资源
     * @param nodes
     * @param roleId
     * @throws BusinessException
     */
	void updateRoleResource(List nodes, Long accountId) throws BusinessException;

	/**
	 * 获取默认角色对应的菜单资源（特指vjon人员角色）
	 * @param accountId
	 * @return
	 * @throws Exception
	 */
	List<PrivTreeNodeBO> getRoleResource(Long accountId) throws Exception;

	/**
	 * 修改个人的密码
	 * @param memberId
	 * @param nowPassword
	 * @param oldPassword
	 * @return
	 * @throws BusinessException 
	 */
	OrganizationMessage modifyPwd(Long memberId, String nowPassword,String oldPassword) throws BusinessException;

	/**
	 * 修改管理员的密码
	 * @param accountId
	 * @param loginName
	 * @param nowPassword
	 * @param oldPassword
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage modifyPwd4Admin(Long accountId, String loginName,String nowPassword, String oldPassword) throws BusinessException;

	/**
	 * 获取外部人员能看到的v5的部门
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgDepartment> getAccessInnerDepts(Long memberId, Long accountId) throws BusinessException;

	/**
	 * 获取内部人员能看到的vjoin部门
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgDepartment> getAccessVjoinDepts(Long memberId, Long accountId)throws BusinessException;

	/**
	 * 获取vjon部门的所有上级部门
	 * @param depId
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgDepartment> getAllParentDepartments(Long depId) throws BusinessException;

	/**
	 * 根据枚举值，获取外单位属性中的值是该枚举值的单位下的人员
	 * @param enumItemId
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgMember> getMembersByEnumId(Long enumItemId)throws BusinessException;

	/**
	 * 获取岗位下的人员
	 * @param postId
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgMember> getMembersByPost(Long postId) throws BusinessException;

	List<V3xOrgMember> getMembersByPost(Long postId, Long accountId)throws BusinessException;

	/**
	 * 查找外机构角色，向上匹配
	 * @param departmentId
	 * @param roleNameOrId
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgMember> getMembersByDepartmentRoleOfUp(long departmentId,String roleNameOrId) throws BusinessException;

	V3xOrgRole getRoleByNameOrId(String roleId, Long unitId)throws BusinessException;

	List<V3xOrgMember> getMembersByRoleNameOrId(Long unitId, String roleId)throws BusinessException;

	public V3xOrgRole getRoleByName(String roleName, Long accountId)throws BusinessException;
    /**
     * 根据名称获取角色
     * @param roleName
     * @param accountId
     * @param externalType
     *     OrgConstants.ExternalType
     * @return
     * @throws BusinessException
     */
    public V3xOrgRole getRoleByName(String roleName, Long accountId, Integer externalType) throws BusinessException;

    /**
     * 根据登录人员，获取负责的各外部单位/负责的各外部机构
     * @param memberId
     * @param roleName
     *     Role_NAME.VjoinUnitManager.name()：机构负责人
     *     Role_NAME.VjoinAccountManager.name()：单位负责人
     * @return
     * @throws BusinessException
     */
    public List<V3xOrgDepartment> getDepartmentsByVjoinManager(Long memberId, String roleName) throws BusinessException;

    /**
     * 根据指定人员，获取指定角色负责的各外部单位/负责的各外部机构
     * @param memberId
     * @param roleName
     * @param externalType
     * @return
     * @throws BusinessException
     */
	List<V3xOrgDepartment> getDepartmentsByVjoinRole(Long memberId,String roleName, Integer externalType) throws BusinessException;

	/**
	 * 获取一个默认的vjoin虚拟单位（正常情况下只有一个）
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	Long getDefaultVjoinAccount(Long accountId) throws BusinessException;

	/**
	 * 根据名称查实体对象
	 * @param clazz
	 * @param name
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	<T extends V3xOrgEntity> List<T> getEntitiesByName(Class<T> clazz, String name, Long accountId) throws BusinessException;

	/**
	 * 删除不在准出单位下的互访权限
	 * @param oldAccountIds  调整前的准出单位
	 * @param newAccountIds  调整后的准出单位
	 * @throws BusinessException
	 */
	void deleteAllAccessRelation(List<Long> oldAccountIds, List<Long> newAccountIds)throws BusinessException;

    boolean isPropertyDuplicated(String entityClassName, String property, Object value, Long accountId) throws BusinessException;
    
    public boolean isExistRepeatProperty(List<? extends V3xOrgEntity> ents, String propertyName, Object value,V3xOrgEntity entity) throws BusinessException;

    /**
     * 获取vjoin子机构管理员
     * @param unitId
     * @return
     * @throws BusinessException 
     */
	public V3xOrgMember getSubAdmin(Long unitId) throws BusinessException;

	/**
	 * 获取vjoin人员能够访问的外部单位
	 * 1.自己所在的外部单位
	 * 2.自己所在外单位能够访问的其他外部单位
	 * @param memberId
	 * @return
	 * @throws BusinessException
	 */
	public List<V3xOrgDepartment> getVjoinAccessDepartments(Long memberId)throws BusinessException;

	List<MemberPost> getMemberPostByPost(Long postId) throws BusinessException;
	List<MemberPost> getMemberPostByPost(Long postId, Long deptId) throws BusinessException;
	List<MemberPost> getMemberPostByDepartment(Long departmentId, boolean firtLayer) throws BusinessException;
	List<MemberPost> getMemberPostByRoleNameOrId(Long unitId, String roleId) throws BusinessException;
	List<MemberPost> getMemberPostByDepartmentRoleOfUp(long departmentId,String roleNameOrId) throws BusinessException;
}