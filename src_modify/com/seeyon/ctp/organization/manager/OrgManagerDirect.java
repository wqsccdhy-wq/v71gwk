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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.util.FlipInfo;

/**
 * <p>Title: 组织模型OrgManagerDirect接口</p>
 * <p>Description: 后台组织和HR模块模型管理的组织模型信息管理，或部分特殊的模块调用进行组织模型信息管理等支持</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * <p>接口维护规则：本接口主要用于组织模型信息的维护与管理，外部应用查询或状态判断等等请不要定义此处</p>
 * 
 * @author lilong
 * 
 * @see com.seeyon.ctp.organization.bo.V3xOrgMember
 * @see com.seeyon.ctp.organization.bo.V3xOrgAccount
 * @see com.seeyon.ctp.organization.bo.V3xOrgLevel
 * @see com.seeyon.ctp.organization.bo.V3xOrgPost
 * @see com.seeyon.ctp.organization.bo.V3xOrgRole
 * @see com.seeyon.ctp.organization.bo.V3xOrgTeam
 * @see com.seeyon.ctp.organization.bo.V3xOrgDepartment
 * @see com.seeyon.ctp.organization.bo.V3xOrgRelationship
 * @see com.seeyon.ctp.organization.bo.V3xOrgEntity
 * 
 */
public interface OrgManagerDirect {
	/**
	 * 为实体添加角色（部门角色）保存关系
	 * @param roleId 部门角色ID
	 * @param accountId 单位ID
	 * @param entity 实体
	 * @param deptvo 某部门
	 * @throws BusinessException
	 */
	public void addRole2Entity(Long roleId, Long accountId, V3xOrgEntity entity,V3xOrgDepartment deptvo) throws BusinessException;
	/**
	 * 是否允许解除实体的授权关系
	 * @param roleId
	 * @param unitId
	 * @param entities
	 * @throws BusinessException
	 */
	public void isCanDeleteRoletoEnt(Long roleId, Long unitId, List<? extends V3xOrgEntity> entities) throws BusinessException;
	/**
	 * 新建兼职角色关系
	 * @param roleIds 角色id列表
	 * @param unitId 兼职单位id
	 * @param entity 兼职人员实体
	 * @throws BusinessException
	 */
	void addConcurrentRoles2Entity(List<Long> roleIds, Long unitId, V3xOrgEntity entity) throws BusinessException;
	/**
	 * 删除角色与实体的对应关系
	 * @param roleId
	 * @param unitId
	 * @throws BusinessException
	 */
	public void deleteRoleandEntity(Long roleId, Long unitId,V3xOrgEntity entity) throws BusinessException;
    /**
     * 根据id删除关系
     * @param id
     * @throws BusinessException
     */
    void deleteOrgRelationshipById(Long id) throws BusinessException;

    /**
     * 删除关系
     * @param rel 关系对象
     * @throws BusinessException
     */
    void deleteOrgRelationship(V3xOrgRelationship rel) throws BusinessException;
    /**
     * 获取外部单位最大排序号
     * @param deptid
     * @return
     * @throws BusinessException
     */
    public Integer getMaxOutternalDeptSortNum(Long accountId) throws BusinessException;

    /**
     * 根据关系类型删除实体列表的关系
     * 
     * @param sourceIds
     *            实体id列表
     * @param key
     *            实体关系类型
     * @throws BusinessException
     */
    void deleteRelsInList(List<Long> sourceIds, final String key)
            throws BusinessException;
    /**
     * 批量删除组
     * @param teams
     * @return
     * @throws BusinessException
     */
    public OrganizationMessage deleteTeams(List<V3xOrgTeam> teams) throws BusinessException;
    /**
     * 新增一个实体组
     * 
     * @param team
     *            组实体
     * @return 添加成功的组实体
     * @throws BusinessException
     */
    V3xOrgTeam addTeam(V3xOrgTeam team) throws BusinessException;
    
    /**
     * 新增一个角色
     * @param role 角色实体
     * @return 新增后的角色实体
     * @throws BusinessException
     */
    V3xOrgRole addRole(V3xOrgRole role) throws BusinessException;
    
    /**
     * 设置某个人的语言个性化信息
     * @param member
     * @param locale
     * @throws BusinessException
     */
    void setMemberLocale(V3xOrgMember member, Locale locale) throws BusinessException;
    
    /**
     * 根据人员ID获取某人员的语言设置
     * @param memberId 人员ID
     * @return
     */
    Locale getMemberLocaleById(Long memberId) throws BusinessException;

	/**
	 * 在一个部门内增加多个岗位
	 * 
	 * @param posts 岗位id列表
	 * @param depId 部门id
	 * @throws BusinessException
	 */
    void addDepartmentPost(List<V3xOrgPost> posts, Long depId) throws BusinessException;

    /**
     * 给一个组织模型实体赋予一个具体的角色，主要维护关系表<br>
     * 步骤：先清除，再新增。<br>
     * @param roleId 角色id
     * @param unitId 角色属于部门还是单位id信息;
     * @param entity 实体
     * @throws BusinessException
     */
    void addRole2Entity(Long roleId, Long unitId, V3xOrgEntity entity) throws BusinessException;
    
	/**
	 * 删除授予一个实体的指定角色
	 * @param roleId
	 * @param unitId
	 * @param entity
	 * @throws BusinessException
	 */
	public void deleteEntityRole(Long roleId, Long unitId, V3xOrgEntity entity) throws BusinessException;
	
    void addRole2Entity(Long roleId, Long unitId, V3xOrgEntity entity,List<V3xOrgRelationship> delRels, List<V3xOrgRelationship> addRels) throws BusinessException;
    
    void addRole2Entity(Long roleId, Long accountId, V3xOrgEntity entity,V3xOrgDepartment deptvo, List<V3xOrgRelationship> delRels,List<V3xOrgRelationship> addRels) throws BusinessException;
	/**
	 * 批量新增人员的角色
	 * @param roleId
	 * @param unitId
	 * @param members
	 * @throws BusinessException
	 */
	void addRole2Members(Long roleId, Long unitId, List<V3xOrgMember> members) throws BusinessException;

    /**
     * 根据ID删除兼职关系
     * @param id 实体id
     * @throws BusinessException
     */
    void deleteConcurrentPost(Long id) throws BusinessException;

    /**
     * 在实体中插入重复的排序号
     * @param entityClassName 实体bo类名称如V3xOrgMember
     * @param accountId 单位id
     * @param sortNum 排序号
     * @param isInternal 是否是内部人员
     * @throws BusinessException
     */
    void insertRepeatSortNum(String entityClassName, Long accountId, Long sortNum, Boolean isInternal) throws BusinessException;
    //插入比较时，不包含自己
    void insertRepeatSortNum(String entityClassName, Long accountId,Long sortNum, Boolean isInternal, Long selfEntityId) throws BusinessException;
    
    /**
     * 新增一个无组织的人员
     * @param member 人员实体
     * @throws BusinessException
     */
    void addUnOrganiseMember(V3xOrgMember member) throws BusinessException;

    /**
     * 更新无组织成员
     * @param member 人员实体
     * @throws BusinessException
     */
    void updateUnOrganiseMember(V3xOrgMember member) throws BusinessException;
    
    /**
     * 更新某一条关系数据
     * @param v3xOrgRelationship
     * @throws BusinessException
     */
    void updateV3xOrgRelationship(V3xOrgRelationship v3xOrgRelationship) throws BusinessException;
    
    /**
     * 取得指定id的未分配人员<br>
     * 如果该人员已分配、已停用、已删除抛BusinessException出来
     * @param id
     *            人员id
     * @return 取得人员返回人员BO对象
     * @throws BusinessException
     */
    V3xOrgMember getUnAssignedMemberById(Long id) throws BusinessException;

    /**
     * 更新组信息
     * 步骤1、校验数据 2、实例化 3、触发事件
     * @param team
     * @throws BusinessException
     */
    void updateTeam(V3xOrgTeam team) throws BusinessException;

    /**
     * 新增单位引用集团标准岗
     * @param BenchMarkPostId 标准岗ID
     * @param accountId 引用单位
     * @throws BusinessException
     */
    void addBenchMarkPostRel(Long BenchMarkPostId, Long accountId) throws BusinessException;

    /**
     * 更新外部人员的访问权限
     * @param memberId 人员id
     * @param rels 外部人员工作范围的关系实体列表
     * @throws BusinessException
     */
    void updateExternalMemberWorkScope(Long memberId, List<V3xOrgRelationship> rels) throws BusinessException;

    /**
     * 绑定集团基准岗
     * @param postId 岗位id
     * @param bmPostId 集团基准岗id
     * @throws BusinessException
     */
    void bandBmPost(Long postId, Long bmPostId) throws BusinessException;
    
    /**
     * 增加单位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param account
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addAccount(V3xOrgAccount account, V3xOrgMember adminMember) throws BusinessException;

    /**
     * 批量增加单位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param accounts
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
//    OrganizationMessage addAccounts(List<V3xOrgAccount> accounts) throws BusinessException;

    /**
     * 修改单位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param account
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage updateAccount(V3xOrgAccount account) throws BusinessException;

    /**
     * 批量修改单位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param accounts
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage updateAccounts(List<V3xOrgAccount> accounts) throws BusinessException;
    
    /**
     * 批量修改單位，是否處理单位可见范围（ 管理员修改自己的单位信息时，就不能处理。）
     * @param accounts
     * @param dealAccess
     * @return
     * @throws BusinessException
     */
    OrganizationMessage updateAccounts(List<V3xOrgAccount> accounts,boolean dealAccess) throws BusinessException;

    /**
     * 删除单位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param account
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage deleteAccount(V3xOrgAccount account) throws BusinessException;

    /**
     * 批量删除单位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param accounts
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage deleteAccounts(List<V3xOrgAccount> accounts) throws BusinessException;

    /**
     * 增加部门
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param dept
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addDepartment(V3xOrgDepartment dept) throws BusinessException;

    /**
     * 批量增加部门
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param depts
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addDepartments(List<V3xOrgDepartment> depts) throws BusinessException;

    /**
     * 修改部门
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param dept
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage updateDepartment(V3xOrgDepartment dept) throws BusinessException;

    /**
     * 批量修改部门
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param depts
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage updateDepartments(List<V3xOrgDepartment> depts) throws BusinessException;
    
    /**
     * 修改部门，用于外部接口修改部门接口不分发事件
     * 步骤：1、校验 数据   2、操作符合条件的实体
     * @param dept
     * @return
     * @throws BusinessException
     */
    OrganizationMessage updateDepartmentNoEvent(V3xOrgDepartment dept) throws BusinessException;

    /**
     * 删除部门
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param dept
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage deleteDepartment(V3xOrgDepartment dept) throws BusinessException;

    /**
     * 批量删除部门
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param depts
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage deleteDepartments(List<V3xOrgDepartment> depts) throws BusinessException;

    /**
     * 增加岗位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param post
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addPost(V3xOrgPost post) throws BusinessException;

    /**
     * 批量增加岗位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param posts
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addPosts(List<V3xOrgPost> posts) throws BusinessException;

    /**
     * 修改岗位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param post
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage updatePost(V3xOrgPost post) throws BusinessException;

    /**
     * 批量修改岗位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param posts
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage updatePosts(List<V3xOrgPost> posts) throws BusinessException;

    /**
     * 删除岗位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param post
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage deletePost(V3xOrgPost post) throws BusinessException;

    /**
     * 批量删除岗位
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param posts
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage deletePosts(List<V3xOrgPost> posts) throws BusinessException;

    /**
     * 增加职务
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param level
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addLevel(V3xOrgLevel level) throws BusinessException;

    /**
     * 政务版---增加职级
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param dutyLevel
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
//    OrganizationMessage addDutyLevel(V3xOrgDutyLevel dutyLevel) throws BusinessException;

    /**
     * 批量增加职务
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param levels
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addLevels(List<V3xOrgLevel> levels) throws BusinessException;

    /**
     * 政务版——批量增加职级
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param dutyLevels
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
//    OrganizationMessage addDutyLevels(List<V3xOrgDutyLevel> dutyLevels) throws BusinessException;

    /**
     * 修改职务
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param level
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage updateLevel(V3xOrgLevel level) throws BusinessException;

    /**
     * 批量修改职务
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param levels
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage updateLevels(List<V3xOrgLevel> levels) throws BusinessException;

    /**
     * 政务版——修改职级
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param level
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
//    OrganizationMessage updateDutyLevel(V3xOrgDutyLevel level) throws BusinessException;

    /**
     * 政务版——批量修改职级
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param levels
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
//    OrganizationMessage updateDutyLevels(List<V3xOrgDutyLevel> levels) throws BusinessException;

    /**
     * 删除职务
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param level
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage deleteLevel(V3xOrgLevel level) throws BusinessException;

    /**
     * 批量删除职务
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param levels
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage deleteLevels(List<V3xOrgLevel> levels) throws BusinessException;

    /**
     * 政务版--删除职级
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param level
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
//    OrganizationMessage deleteDutyLevel(V3xOrgDutyLevel level) throws BusinessException;

    /**
     * 政务版--批量删除职级
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * @param levels
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
//    OrganizationMessage deleteDutyLevels(List<V3xOrgDutyLevel> levels) throws BusinessException;

    /**
     * 增加人员
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件<br>
     * 人员数据校验，主要校验步骤为检验部门是否存在或启用，检验主岗与副岗是否重复，检验人员登录名
     * @param member
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addMember(V3xOrgMember member) throws BusinessException;
    
    /**
     * 增加人员
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件<br>
     * 人员数据校验，主要校验步骤为检验部门是否存在或启用，检验主岗与副岗是否重复，检验人员登录名
     * @param member
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addMember(V3xOrgMember member,String resourceId) throws BusinessException;

    /**
     * 批量增加人员
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * 人员数据校验，主要校验步骤为检验部门是否存在或启用，检验主岗与副岗是否重复，检验人员登录名
     * @param members
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addMembers(List<V3xOrgMember> members) throws BusinessException;
    
    /**
     * 批量增加人员
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * 人员数据校验，主要校验步骤为检验部门是否存在或启用，检验主岗与副岗是否重复，检验人员登录名
     * @param members
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage addMembers(List<V3xOrgMember> members,String resourceId) throws BusinessException;

    /**
     * 修改人员
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * 人员数据校验，主要校验步骤为检验部门是否存在或启用，检验主岗与副岗是否重复，检验人员登录名
     * @param member
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage updateMember(V3xOrgMember member) throws BusinessException;

    /**
     * 批量修改人员
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * 人员数据校验，主要校验步骤为检验部门是否存在或启用，检验主岗与副岗是否重复，检验人员登录名
     * @param members
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage updateMembers(List<V3xOrgMember> members) throws BusinessException;

    /**
     * 删除人员
     * 步骤：1、校验 数据   2、操作符合条件的实体     3、触发事件
     * 新逻辑完全采用事件分发的机制，暂时不需要检验数据这一步骤，但以备扩展保留，可以在实现代码处做文章
     * @param member
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage deleteMember(V3xOrgMember member) throws BusinessException;

    /**
     * 批量删除人员
     * 新逻辑完全采用事件分发的机制，暂时不需要检验数据这一步骤，但以备扩展保留，可以在实现代码处做文章
     * @param members
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    OrganizationMessage deleteMembers(List<V3xOrgMember> members) throws BusinessException;

    /**
     * 删除组
     * @param team
     * @return
     * @throws BusinessException
     */
    OrganizationMessage deleteTeam(V3xOrgTeam team) throws BusinessException;

    /**
     * 批量删除关系
     * @param rels
     * @throws BusinessException
     */
    void deleteOrgRelationships(List<V3xOrgRelationship> rels) throws BusinessException;

    /**
     * 新增一个关系
     * @param rel
     * @return
     * @throws BusinessException
     */
    void addOrgRelationship(V3xOrgRelationship rel) throws BusinessException;

    /**
     * 新增一个兼职关系
     * @param members
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    void addConurrentPost(MemberPost memberPost) throws BusinessException;
    
    /**
     * 修改兼职关系
     * @param members
     * @return OrgManagerMessage
     * @throws BusinessException 
     */
    void updateConurrentPost(Long relId, MemberPost memberPost) throws BusinessException;

    /**
     * 政务版——新建单位时预置职级。
     * 
     * @param accountId
     *            目标单位ID
     * @throws BusinessException
     */
//    void generateDutyLevelToAccount(Long accountId) throws BusinessException;
    
    /*---------组织模型维护管理类查询接口------------*/
    
    /**
     * 根据登录名获得用户
     * 
     * @param loginName
     *            登录名
     * @param includeDisabled
     *            是否包含无效
     * @return
     * @throws BusinessException
     */
    V3xOrgMember getMemberByLoginName(String loginName, boolean includeDisabled)
            throws BusinessException;

    /**
     * 根据名称获得成员，可能会有多个
     * 
     * @param memberName
     *            人员姓名
     * @param accountId
     *            单位id
     * @param includeDisabled
     *            是否包含无效
     * @return
     * @throws BusinessException
     */
    List<V3xOrgMember> getMemberByName(String memberName, Long accountId, boolean includeDisabled)
            throws BusinessException;
    /**
     * 将集团的角色和角色-资源关系同步到单位
     * @throws BusinessException 
     */
    public void saveSycGroupRole(V3xOrgAccount account) throws BusinessException;
    /**
     * 获得一个部门下的组列表。<br>
     * includeDisabled: true -- 所有部门组； false -- 只包括启用的部门组
     * 
     * @since 3.12
     */
    List<V3xOrgTeam> getDepartmentTeam(Long depId, boolean includeDisabled)
            throws BusinessException;

    /**
     * 获得所有部门
     * @param accountID 单位id
     * @param enable 是否有效
     * @param isInternal 是否为内部
     * @param condition 条件查询条件
     * @param feildvalue 条件查询值
     * @param flipInfo 翻页信息
     * @return
     * @throws BusinessException
     */
    List<V3xOrgDepartment> getAllDepartments(Long accountID, Boolean enable, Boolean isInternal, String condition,
            Object feildvalue, FlipInfo flipInfo) throws BusinessException;
    /**
     * 取指定单位的所有职务级别。
     * 
     * @param accountId
     *            单位Id。
     * @param includeDisabled
     *            是否包含停用的职务级别，为<CODE>true</CODE>时包含，否则只返回启用的职务级别。
     * @return 单位的职务级别列表。
     * @throws BusinessException
     */
    List<V3xOrgLevel> getAllLevels(Long accountId, boolean includeDisabled)
            throws BusinessException;

    /**
     * 政务版——取指定单位的所有职级。
     * 
     * @param accountId
     *            单位Id。
     * @param includeDisabled
     *            是否包含停用的职级，为<CODE>true</CODE>时包含，否则只返回启用的职级。
     * @return 单位的职级列表。
     * @throws BusinessException
     */
//    List<V3xOrgDutyLevel> getAllDutyLevels(Long accountId, boolean includeDisabled)
//            throws BusinessException;

    /**
     * 取指定单位的所有人员。
     * 
     * @param accountId
     *            单位Id
     * @param includeDisabled
     *            是否包含停用的人员，为<CODE>true</CODE>时包含，否则只返回启用的人员。
     * @return 单位人员列表
     */
    List<V3xOrgMember> getAllMembers(Long accountId, boolean includeDisabled)
            throws BusinessException;
    
    /**
     * 取指定单位的所有岗位。
     * 
     * @param accountId
     *            单位Id
     * @param includeDisabled
     *            是否包含停用的岗位，为<CODE>true</CODE>时包含，否则只返回启用的岗位。
     * @return 单位岗位列表
     * @throws BusinessException
     */
    List<V3xOrgPost> getAllPosts(Long accountId, boolean includeDisabled)
            throws BusinessException;

    /**
     * 取指定单位的所有角色。
     * 
     * @param accountId
     *            单位Id
     * @param includeDisabled
     *            是否包含停用的角色，为<CODE>true</CODE>时包含，否则只返回启用的角色。
     * @return 角色列表
     * @throws BusinessException
     */
    List<V3xOrgRole> getAllRoles(Long accountId, boolean includeDisabled)
            throws BusinessException;

    /**
     * 取指定单位的所有组。
     * 
     * @param accountId
     *            单位Id
     * @param includeDisabled
     *            是否包含停用的组，为<CODE>true</CODE>时包含，否则只返回启用的角色。
     * @return 组列表。
     * @throws BusinessException
     */
    List<V3xOrgTeam> getAllTeams(Long accountId, boolean includeDisabled)
            throws BusinessException;

    /**
     * 获得部门下的所有成员
     * 
     * @param departmentId
     *            部门id
     * @param firtLayer
     *            true只查询本部门 false查询所有子部门
     * @param includeDisabled
     *            是否包含无效人员
     * @param accountId
     *            单位id
     * @param includeOuterworker
     *            是否包含外部人员
     * @return
     * @throws BusinessException
     */
    List<V3xOrgMember> getMembersByDepartment(Long departmentId, Long accountId, Boolean firtLayer,
            Boolean includeDisabled, Boolean includeOuterworker) throws BusinessException;

    /**
     * 获得具有某个级别的所有成员
     * 
     * @param levelId
     *            职务级别id
     * @param includeDisabled
     *            是否包含无效人员
     * @return
     * @throws BusinessException
     */
    List<V3xOrgMember> getMembersByLevel(Long levelId, boolean includeDisabled)
            throws BusinessException;

    /**
     * 获取单位下所有成员。
     * 
     * @param accountId
     *            单位id
     * @param includeDisabled
     *            是否包含无效
     * @param isPaginate
     *            是否分页
     * @return
     * @throws BusinessException
     */
    List<V3xOrgMember> getAllMembers(Long accountId, boolean includeDisabled, boolean isPaginate)
            throws BusinessException;

    /**
     * 获得某个岗位上的所有成员<br>
     * 判断人员是否有效标识<code>isValid()</code>方法
     * 
     * @param postId
     *            岗位id
     * @param includeDisabled
     *            是否包含无效
     * @return
     * @throws BusinessException
     */
    List<V3xOrgMember> getMembersByPost(Long postId, boolean includeDisabled) throws BusinessException;

    /**
     * 获得一个部门下某个岗位上的所有成员<br>
     * 判断人员是否有效标识<code>isValid()</code>方法
     * 
     * @param depId
     *            部门id
     * @param postId
     *            岗位id
     * @param includeDisabled
     *            是否包含无效
     * @return
     * @throws BusinessException
     */
    List<V3xOrgMember> getMembersByPost(Long depId, Long postId,
            boolean includeDisabled) throws BusinessException;

    /**
     * 检查类的属性是否有重复，用于无视单位id来进行属性值的判断<br>
     * 其他组织模型维护请使用<code>isPropertyDuplicated(String entityClassName, String property, Object value, Long accountId)</code>方法在本单位内判断
     * 
     * @param entityClass 类
     * @param property 属性
     * @param value 对比的值，如果记录中有该值，则表示该属性有重叠
     * @return true--有重复；false--无重复
     * @throws BusinessException
     */
    boolean isPropertyDuplicated(String entityClassName, String property, Object value) throws BusinessException;

    /**
     * 检查类的属性是否有重复
     * 
     * @param entityClass 类
     * @param property 属性
     * @param value 对比的值，如果记录中有该值，则表示该属性有重叠
     * @param accountId
     * @return true--有重复；false--无重复
     * @throws BusinessException
     */
    boolean isPropertyDuplicated(String entityClassName, String property, Object value, Long accountId)
            throws BusinessException;
    
    /**
     * 检查类的属性是否有重复
     * @param entityClassName
     * @param property
     * @param value
     * @param accountId
     * @param entId
     * @return
     * @throws BusinessException
     */
    boolean isPropertyDuplicated(String entityClassName, String property, Object value, Long accountId, Long entId)
            throws BusinessException;

    /**
     * 获取单位内某实体最大排序号
     * 
     * @param entityClassName
     * @param accountId
     * @return
     * @throws BusinessException
     */
    Integer getMaxSortNum(String entityClassName, Long accountId) throws BusinessException;
    
    /**
     * 获取本单位兼职出去的兼职列表
     * 
     * @param accountId
     *            单位ID
     * @return
     * @throws BusinessException
     */
    List<V3xOrgRelationship> getAllOutConcurrentPostByAccount(Long accountId) throws BusinessException;

    /**
     * 获取某单位内所有兼职列表
     * @param accountId
     * @return 兼职关系对象列表
     */
    List<V3xOrgRelationship> findAllSidelineAccountCntPost(Long accountId);

    public void deleteAll();

    /**
     * 删除某一个角色
     * @param role
     * @return
     * @throws BusinessException
     */
    OrganizationMessage deleteRole(V3xOrgRole role) throws BusinessException;
    
    /**
     * 更新一个角色
     * @param role
     * @return
     * @throws BusinessException
     */
    OrganizationMessage updateRole(V3xOrgRole role) throws BusinessException;
    
    /**
     * 获取所有单位列表
     * @param enable 是否包含有效
     * @param isInternal 是否为内部
     * @param condition 条件
     * @param feildvalue 查询条件值
     * @param flipInfo 分页信息
     * @return
     * @throws BusinessException
     */
    List<V3xOrgAccount> getAllAccounts(Boolean enable, Boolean isInternal, String condition, Object feildvalue, FlipInfo flipInfo) throws BusinessException;

    /**
     * 直接新建一个单位，此方法没有关注单位管理员，单纯新建一个单位
     * @param account
     * @return
     * @throws BusinessException
     */
    OrganizationMessage addAccount(V3xOrgAccount account) throws BusinessException;
    
    /**
     * 添加组人员
     * @param members
     * @param team
     * @param membertype
     * @throws BusinessException
     */
    void addTeamMembers(List<V3xOrgMember> members, V3xOrgTeam team,String membertype) throws BusinessException;
    /**
     * 添加组公开范围
     * @param ents
     * @param team
     * @throws BusinessException
     */
    void addTeamScope(List<? extends V3xOrgEntity> ents, V3xOrgTeam team) throws BusinessException;
    /**
     * 获取组人员
     * @param team
     * @param membertype
     * @return
     * @throws BusinessException
     */
	List<V3xOrgEntity> getTeamMembers(V3xOrgTeam team,String membertype) throws BusinessException;
	
	/**
     * 获取组员的选人界面回填数据
     * @param team
     * @param membertype
     * @return
     * @throws BusinessException
     */
	Map<String,String> getTeamsMember(V3xOrgTeam team,String membertype) throws BusinessException;
    /**
     * 
     * @param member
     * @param unitId
     * @param roleIds
     * @throws BusinessException
     */
    public void isCanDeleteMembertoRole(V3xOrgMember member, Long unitId, List<Long> roleIds) throws BusinessException;
    
    /**
     * 获取实体列表，不考虑实体关系，适用于查询组织模型实体
     * @param entityClassName 实体类
     * @param property 属性
     * @param value 属性值
     * @param enabled 是否为启用 可以为null
     * @param accountId 单位id 可以为null
     * @return
     * @throws BusinessException
     */
    public List<V3xOrgEntity> getEntityNoRelationDirect(String entityClassName, String property, Object value, Boolean enabled,
            Long accountId);
    /**
     * 获取组公开范围
     * @param team
     * @return
     * @throws BusinessException
     */
    List<V3xOrgEntity> getTeamScope(V3xOrgTeam team) throws BusinessException;
    
    /**
     * 根据关系ID删除一条关系
     * @param id
     * @throws BusinessException
     */
    void deleteRelationById(Long id) throws BusinessException;
    
    /**
     * 批量新增关系BO
     * @param rels
     * @throws BusinessException
     */
    void addOrgRelationships(List<V3xOrgRelationship> rels) throws BusinessException;
    
    /**
     * 复制集团职务级别
     * @param accountId 单位id
     * @throws BusinessException
     */
    void copyGroupLevelToAccount(Long accountId) throws BusinessException;
    
    /**
     * 清除某人在某单位下处部门角色外的所有单位角色信息和这个人所在部门的所有部门角色<br>
     * 仅供人员管理角色管理框内部使用
     * @param member 人员
     * @param roleIds 可以看到的，单位管理员可以处理的角色列表
     * @throws BusinessException
     */
    void cleanMemberAccAndSelfDeptRoles(V3xOrgMember member, Set<Long> roleIds) throws BusinessException;
    
    /**
     * 根据单位id获取所有平级兄弟单位列表
     * @param accountId 单位id
     * @return
     * @throws BusinessException
     */
    List<V3xOrgAccount> getNeighborAccountsByAccountId(Long accountId) throws BusinessException;
    
    /**
     * 根据单位id获取所有"上级"单位<br>
     * 该接口会返回所有path短于传入的单位的单位列表，慎用
     * @param accountId 单位id
     * @return
     * @throws BusinessException
     */
    List<V3xOrgAccount> getSuperiorAccountsByAccountId(Long accountId) throws BusinessException;
    
    /**
     * 将角色赋予实体（不删除之前的关系）
     * @param roleId
     * @param unitId
     * @param entity
     * @throws BusinessException
     */
    public void addRole2EntitywithoutDel(Long roleId, Long unitId, V3xOrgEntity entity) throws BusinessException;
    /**
     * 获取某单位的外部人员最大排序号
     * @param accountId 单位id
     * @return
     * @throws BusinessException
     */
    Integer getExtMemberMaxSortNum(Long accountId) throws BusinessException;
    /**
     * 删除实体的角色关系
     * @param roleId
     * @param unitId
     * @param entity
     * @throws BusinessException
     */
    void deleteRole2Entity(Long roleId, Long unitId,List<V3xOrgMember> members) throws BusinessException;
    
    /**
     * 删除某一个角色在某一个单位内的所有角色关系
     * @param roleId 角色id
     * @param unitId 单位id
     * @throws BusinessException
     */
    void deleteRoleRelsInUnit(Long roleId, Long unitId) throws BusinessException;
    /**
     * 根据实体获取人员
     * @param entity
     * @return
     * @throws BusinessException
     */
    public List<V3xOrgMember> getmembersByEntity(String s) throws BusinessException ;
    /**
     * 根据实体获取人员
     * @param entity
     * @return
     * @throws BusinessException
     */
    public List<V3xOrgMember> getmembersByEntity(V3xOrgEntity entity) throws BusinessException;
    
    /**
     * 根据单位ID查询人员，直接查询数据库
     * @param accountId
     * @param isInternal
     * @param enable
     * @param param
     * @param flipInfo
     * @return
     */
    public List<V3xOrgMember> getAllMemberPOByAccountId(Long accountId, Boolean isInternal, Boolean enable,
            Map<String, Object> param, FlipInfo flipInfo);
    
    /**
     * 用于加密使用，返回单位名称<br>
     * @return 单组织版ID=670869647114347的单位的名称<br>多组织版ID=-1730833917365171641的集团的名称
     */
    public String getAccountName() throws BusinessException;
    
    /**
     * 用于加密使用，判断单位名称是否符合<br>
     * 如果是多组织版，对比集团名称；如果是单组织版，对比单位名称
     * @param accountName 单位名称
     * @return true 符合，false 不符合
     */
    public boolean matchAccountName(String accountName) throws BusinessException;
    
    /**
     * 用于加密使用:<br>
     * 如果是多组织版就修改集团的名称<br>
     * 如果是单组织版就修改单位的名称
     * @param name 
     */
    public void updateAccountName(String name) throws BusinessException;
    
    /**
     * 
     * @param entityTypeName
     * @param accountId
     * @return
     * @throws BusinessException
     */
    List<? extends V3xOrgEntity> getUnenabledEntities(String entityTypeName, Long accountId) throws BusinessException;

    /**
     * 批量给实体授权角色
     * @param roleId 角色ID
     * @param accountId 单位ID
     * @param entities 实体列表
     * @param departmentId 部门ID
     * @throws BusinessException
     */
    void addRole2Entities(Long roleId, Long accountId, List<V3xOrgEntity> entities, Long departmentId)
            throws BusinessException;

    /**
     * 获取某部门下的所有子部门，包括无效部门
     * @param parentDepId 父部门ID
     * @param firtLayer true只查询一层子部门 false查询所有子部门
     * @return
     * @throws BusinessException
     */
    List<V3xOrgDepartment> getChildDepartmentsWithInvalid(Long parentDepId, boolean firtLayer) throws BusinessException;
    
    /**
     * 批量删除岗位
     * @param posts
     * @param failfast 是否快速返回失败消息
     * @return
     * @throws BusinessException
     */
    OrganizationMessage deletePosts(List<V3xOrgPost> posts, boolean failfast) throws BusinessException;
    
	/**
	 * 增量增加部门下的岗位信息
	 * 先删除这个部门和这些岗位之间的关系数据，再重新添加关系数据。
	 * 
	 * @param posts 岗位id列表
	 * @param depId 部门id
	 * @throws BusinessException
	 */
	void incrementDepartmentPost(List<V3xOrgPost> posts, Long depId) throws BusinessException;
	/**
	 * 更新通讯录自定义字段
	 * @param m
	 * @throws BusinessException
	 */
	void updateAddressBookinfo(V3xOrgMember m) throws BusinessException;
	/**
	 * 添加特殊账号
	 * @param member
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addGuest(V3xOrgMember member) throws BusinessException;
	/**
	 * 更新特殊账号
	 * @param member
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updateGuest(V3xOrgMember member) throws BusinessException;
	/**
	 * 删除特殊账号
	 * @param members
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage deleteGuests(List<V3xOrgMember> members)throws BusinessException;

    /**
     * 
     * 获取系统第一个创建的人
     * @return
     */
    OrgMember getFirstCreateMember();
    
}