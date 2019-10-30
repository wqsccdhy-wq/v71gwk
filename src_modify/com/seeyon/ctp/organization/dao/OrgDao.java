/*
 * $Author: tanmf $ $Rev: 0 $ $Date: 2012-08-01 15:08:37#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc. Use is subject to license terms.
 */
package com.seeyon.ctp.organization.dao;

import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.RelationshipType;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.po.OrgLevel;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.organization.po.OrgPost;
import com.seeyon.ctp.organization.po.OrgRelationship;
import com.seeyon.ctp.organization.po.OrgRole;
import com.seeyon.ctp.organization.po.OrgTeam;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.organization.po.OrgVisitor;
import com.seeyon.ctp.privilege.po.PrivRoleMenu;
import com.seeyon.ctp.util.FlipInfo;

/**
 * <p>
 * Title: T2 组织模型DAO
 * </p>
 * <p>
 * Description: 1.本DAO接口用来支持后台组织模型管理的数据访问，<b>其它应用禁止调用</b><br>
 * 2. The DAO interface for the entities: Department, Level, Member, Account, Property, Relationship, Post, Team, Role
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: seeyon.com
 * </p>
 * 
 * @since CTP2.0
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 */
public interface OrgDao {

    List<OrgUnit> getAllUnitPO(OrgConstants.UnitType type, Long accountId, Boolean enable, Boolean isInternal,
        String condition, Object feildvalue, FlipInfo flipInfo);

    /**
     * 从数据库中取出人员，默认条件：<code>isDelete=false and isVirtual=false and isAssigned=true</code>，即把删除人员、虚拟账号、取消分配缺省排除，通过其它接口提供
     * 
     * @param accountId
     *            所属单位(不包含兼职人员)，可以为<code>null</code>，表示不区分
     * @param type
     *            人员类型：正式/非正式/...，可以为<code>null</code>，表示不区分
     * @param isInternal
     *            可以为<code>null</code>，表示不区分
     * @param enable
     *            可以为<code>null</code>，表示不区分
     * @param condition
     *            取值：name, code, loginName, orgPostId(主岗), orgLevelId(主岗的职务级别)
     * @param feildvalue
     *            condition对应的值，类型必须也是对应的，比如name就是String，orgPostId就必须是Long，否则数据库抛出异常
     * @param flipInfo
     *            分页信息，可以为<code>null</code>，表示所有
     * @return
     */
    List<OrgMember> getAllMemberPOByAccountId(Long accountId, Integer type, Boolean isInternal, Boolean enable,
        String condition, Object feildvalue, FlipInfo flipInfo);

    /***
     * @param equal
     *            字符串类型的参数是否完全匹配，true:完全匹配 ，用=， false:模糊匹配， 用like
     * @return
     */
    List<OrgMember> getAllMemberPOByAccountId(Long accountId, Integer type, Boolean isInternal, Boolean enable,
        String condition, Object feildvalue, FlipInfo flipInfo, boolean equal);

    /**
     * 从数据库取人员，用于多个组合查询条件
     * 
     * @param accountId
     *            单位ID
     * @param enabled
     *            人员是否启用null表示不区分
     * @param param
     *            组合条件
     * @param flipInfo
     *            分页对象
     * @return
     */
    List<OrgMember> getAllMemberPOByAccountId(Long accountId, Boolean isInternal, Boolean enable,
        Map<String, Object> param, FlipInfo flipInfo);

    /**
     * 从数据库中取出人员，默认条件：<code>isDelete=false and isVirtual=false and isAssigned=true</code>，即把删除人员、虚拟账号、取消分配缺省排除，通过其它接口提供
     * 
     * @param departmentId
     *            不能为null
     * @param isCludChildDepart
     *            是否包含子部门，<code>true</code>包含
     * @param type
     *            人员类型：正式/非正式/...，可以为<code>null</code>，表示不区分
     * @param isInternal
     *            可以为<code>null</code>，表示不区分
     * @param enable
     *            可以为<code>null</code>，表示不区分
     * @param condition
     *            取值：name, code, loginName, orgPostId(主岗), orgLevelId(主岗的职务级别)
     * @param feildvalue
     *            condition对应的值，类型必须也是对应的，比如name就是String，orgPostId就必须是Long，否则数据库抛出异常
     * @param flipInfo
     *            分页信息，可以为<code>null</code>，表示所有
     * @return
     */
    List<OrgMember> getAllMemberPOByDepartmentId(Long departmentId, boolean isCludChildDepart, Integer type,
        Boolean isInternal, Boolean enable, String condition, Object feildvalue, FlipInfo flipInfo);

    /**
     * 从数据库按照部门取人员<br>
     * 默认条件：<code>isDelete=false and isVirtual=false and isAssigned=true</code>，即把删除人员、虚拟账号、取消分配缺省排除，通过其它接口提供
     * 
     * @param departmentId
     *            部门ID不能为null
     * @param isInternal
     *            可以为<code>null</code>，表示不区分
     * @param enable
     *            可以为<code>null</code>，表示不区分
     * @param param
     *            多个查询条件
     * @param flipInfo
     *            分页
     * @return
     */

    List<OrgMember> getAllMemberPOByDepartmentId(List<Long> departmentIds, Boolean isInternal, Boolean enable,
        Map<String, Object> param, FlipInfo flipInfo);

    List<OrgMember> getAllMemberPOByAccountIdAndSecondPostId(Long accountId, Long secondPostId, Boolean isInternal,
        Boolean enable, Map<String, Object> param, FlipInfo flipInfo);

    // List<OrgMember> getAllMemberPOByDepartmentIdAndSecondPostId(List<Long> departmentIds, Long secondPostId, Boolean
    // isInternal, Boolean enable, Map<String, Object> param, FlipInfo flipInfo);

    /**
     * 
     * @param accountId
     * @param type
     * @param isInternal
     * @param enable
     * @param condition
     * @param feildvalue
     * @return
     */
    Integer getAllMemberPONumsByAccountId(Long accountId, Integer type, Boolean isInternal, Boolean enable,
        String condition, Object feildvalue);

    /**
     * 从数据库中取出人员，默认条件：<code>isDelete=false and isVirtual=false and isAssigned=true</code>，即把删除人员、虚拟账号、取消分配缺省排除，通过其它接口提供
     * 
     * @param departmentIds
     *            不能为null
     * @param type
     *            人员类型：正式/非正式/...，可以为<code>null</code>，表示不区分
     * @param isInternal
     *            可以为<code>null</code>，表示不区分
     * @param enable
     *            可以为<code>null</code>，表示不区分
     * @param condition
     *            取值：name, code, loginName, orgPostId(主岗), orgLevelId(主岗的职务级别)
     * @param feildvalue
     *            condition对应的值，类型必须也是对应的，比如name就是String，orgPostId就必须是Long，否则数据库抛出异常
     * @param flipInfo
     *            分页信息，可以为<code>null</code>，表示所有
     * @return
     */
    List<OrgMember> getAllMemberPOByDepartmentIds(List<Long> departmentIds, Integer type, Boolean isInternal,
        Boolean enable, String condition, Object feildvalue, FlipInfo flipInfo);

    List<OrgRole> getAllRolePO(Long accountId, Boolean enable, String condition, Object feildvalue, FlipInfo flipInfo);

    /***
     * @param equal
     *            字符串类型的参数是否完全匹配，true:完全匹配 ，用=， false:模糊匹配， 用like
     * @return
     */
    List<OrgRole> getAllRolePO(Long accountId, Boolean enable, String condition, Object feildvalue, FlipInfo flipInfo,
        boolean equal);

    List<OrgRole> getAllRolePO(Long accountId, Boolean enable, Map<String, Object> param, FlipInfo flipInfo);

    List<OrgTeam> getAllTeamPO(Long accountId, Integer type, Boolean enable, String condition, Object feildvalue,
        FlipInfo flipInfo);

    /***
     * @param equal
     *            字符串类型的参数是否完全匹配，true:完全匹配 ，用=， false:模糊匹配， 用like
     * @return
     */
    List<OrgTeam> getAllTeamPO(Long accountId, Integer type, Boolean enable, String condition, Object feildvalue,
        FlipInfo flipInfo, boolean equal);

    List<OrgTeam> getAllTeamPO(Long accountId, Integer type, Boolean enable, Map<String, Object> param,
        FlipInfo flipInfo);

    List<OrgPost> getAllPostPO(Long accountId, Boolean enable, String condition, Object feildvalue, FlipInfo flipInfo);

    /***
     * @param equal
     *            字符串类型的参数是否完全匹配，true:完全匹配 ，用=， false:模糊匹配， 用like
     * @return
     */
    List<OrgPost> getAllPostPO(Long accountId, Boolean enable, String condition, Object feildvalue, FlipInfo flipInfo,
        boolean equal);

    /**
     * 获取集团基准角色
     * 
     * @return
     */
    public List<OrgRole> getBaseRole();

    /**
     * 根据角色获得角色-资源关系
     * 
     * @param orgrole
     * @return
     */
    public List<PrivRoleMenu> getRoleMenu(OrgRole orgrole);

    /**
     * 判断集团职务级别是否被引用
     * 
     * @param levelId
     * @return
     */
    public boolean isGroupLevelUsed(Long levelId);

    List<OrgLevel> getAllLevelPO(Long accountId, Boolean enable, String condition, Object feildvalue,
        FlipInfo flipInfo);

    /***
     * @param equal
     *            字符串类型的参数是否完全匹配，true:完全匹配 ，用=， false:模糊匹配， 用like
     * @return
     */
    List<OrgLevel> getAllLevelPO(Long accountId, Boolean enable, String condition, Object feildvalue, FlipInfo flipInfo,
        boolean equal);

    /**
     * 
     * @param type
     *            可以为<code>null</code>，表示所有，需谨慎
     * @param sourceId
     *            可以为<code>null</code>，表示所有，需谨慎
     * @param accountId
     *            可以为<code>null</code>，表示所有，需谨慎
     * @param objectiveIds
     *            可以为<code>null</code>，表示所有
     * @param flipInfo
     *            可以为<code>null</code>，表示不分页
     * @return
     */
    List<OrgRelationship> getOrgRelationshipPO(OrgConstants.RelationshipType type, Long sourceId, Long accountId,
        EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds, FlipInfo flipInfo);

    /**
     * 仅供内部使用
     * 
     * @param type
     *            可以为<code>null</code>，表示所有，需谨慎
     * @param sourceIds
     *            可以为<code>null</code>，表示所有，需谨慎
     * @param accountId
     *            可以为<code>null</code>，表示所有，需谨慎
     * @param objectiveIds
     *            可以为<code>null</code>，表示所有
     * @param flipInfo
     *            可以为<code>null</code>，表示不分页
     * @return
     */
    List<OrgRelationship> getOrgRelationshipPOByMembers(OrgConstants.RelationshipType type, List<Long> sourceIds,
        Long accountId, EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds, FlipInfo flipInfo);

    /**
     * 根据人员名称查询兼职关系接口，仅供兼职管理使用
     * 
     * @param name
     *            人员名称
     * @param openFrom
     * @param subUnitIds
     * @return
     */
    List<OrgRelationship> getOrgRelationshipPOByMemberName(String name, FlipInfo flipInfo, String openFrom,
        List<Long> subUnitIds);

    /**
     * 全局查找
     * 
     * @param entityClass
     *            OrgUnit,OrgMember...
     * @param id
     * @return
     */
    <T extends BasePO> T getEntity(Class<T> entityClass, Long id);

    /**
     * 根据Id得到单个单位，如果单位被逻辑删除、停用、取消分配，也能取到，再有上层判别isDelete
     * 
     * @param id
     * @return
     */
    OrgUnit getOrgUnitPO(Long id);

    OrgUnit getOrgUnitPOByPath(String path);

    /**
     * 根据Id得到单个人，如果单位被逻辑删除、停用、取消分配，也能取到，再有上层判别isDelete
     * 
     * @param id
     * @return
     */
    OrgMember getOrgMemberPO(Long id);

    /**
     * 根据Id得到单个角色，如果单位被逻辑删除、停用，也能取到，再有上层判别
     * 
     * @param id
     * @return
     */
    OrgRole getOrgRolePO(Long id);

    /**
     * 根据Id得到单个组，如果单位被逻辑删除、停用，也能取到，再有上层判别
     * 
     * @param id
     * @return
     */
    OrgTeam getOrgTeamPO(Long id);

    /**
     * 根据Id得到单个岗位，如果单位被逻辑删除、停用，也能取到，再有上层判别
     * 
     * @param id
     * @return
     */
    OrgPost getOrgPostPO(Long id);

    /**
     * 根据Id得到单个职务级别，如果单位被逻辑删除、停用，也能取到，再有上层判别
     * 
     * @param id
     * @return
     */
    OrgLevel getOrgLevelPO(Long id);

    void insertOrgUnit(List<OrgUnit> orgUnitPOs);

    void insertOrgMember(List<OrgMember> orgMemberPO);

    void insertOrgVisitor(List<OrgVisitor> orgVisitorPO);

    void insertOrgRole(List<OrgRole> orgRolePO);

    void insertOrgTeam(List<OrgTeam> orgTeamPO);

    void insertOrgPost(List<OrgPost> orgPostPO);

    void insertOrgLevel(List<OrgLevel> orgLevelPO);

    void insertOrgRelationship(List<OrgRelationship> orgRelationshipPO);

    void update(OrgUnit orgUnitPO);

    void update(OrgMember orgMemberPO);

    void update(List<OrgMember> orgMembers);

    void update(OrgRole orgRolePO);

    void update(OrgTeam orgTeamPO);

    void update(OrgPost orgPostPO);

    void update(OrgLevel orgLevelPO);

    void updates(List<OrgRole> orgRoles);

    void updateRelationship(OrgRelationship orgRelationshipPO);

    void updateRelationships(List<OrgRelationship> orgRelationshipPOs);

    public void deleteOrgRelationshipPOs(List<OrgRelationship> rels);

    /**
     * 按照关系类型物理删除关系数据
     * 
     * @param type
     *            <b>不可以</b>为<code>null</code>
     * @param sourceId
     *            可以为<code>null</code>
     * @param orgAccountId
     *            可以为<code>null</code>
     * @param objectiveIds
     *            可以为<code>null</code>，表示所有
     */
    void deleteOrgRelationshipPO(String type, Long sourceId, Long accountId,
        EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds);

    /**
     * 仅限组织模型内部删除单位时使用，删除单位内的所有关系数据，慎用<br>
     * 单位id为空直接return不执行sql以免造成误操作删除数据
     * 
     * @param accountId
     *            <b>不可以</b>为<code>null</code>
     */
    void deleteOrgRelationshipPOByAccountId(Long accountId);

    /**
     * 得到组织模型实体的当前最大排序号，不过不存在，就返回0
     * 
     * @param entityType
     * @param accountId
     * @return
     */
    <T extends V3xOrgEntity> int getMaxSortId(Class<T> entityType, Long accountId);

    /**
     * 插入重复排序号，后面的排序号+1
     * 
     * @param entityClass
     *            OrgUnit\OrgMember\...
     * @param accountId
     * @param sortNum
     * @param isInternal
     *            是否是内部人员
     */
    <T extends V3xOrgEntity> void insertRepeatSortNum(Class<T> entityClass, Long accountId, Long sortNum,
        Boolean isInternal);

    /**
     * 插入重复排序号，后面的排序号+1
     * 
     * @param entityClass
     *            OrgUnit\OrgMember\...
     * @param accountId
     * @param sortNum
     * @param isInternal
     *            是否是内部人员
     * @param externalType
     *            V-Join元素类型
     */
    <T extends V3xOrgEntity> void insertRepeatSortNum(Class<T> entityClass, Long accountId, Long sortNum,
        Boolean isInternal, int externalType);

    <T extends V3xOrgEntity> void insertRepeatSortNum(Class<T> entityClass, Long accountId, Long sortNum,
        Boolean isInternal, int externalType, Long selfEntityId);

    <T extends V3xOrgEntity> void insertRepeatSortNum(Class<T> entityClass, Long accountId, Long sortNum,
        Boolean isInternal, int externalType, Long selfEntityId, Long createrId);

    /**
     * 获取外部部门最大排序号
     * 
     * @param deptid
     * @return
     */
    public int getMaxOutternalDeptSortId(Long deptid);

    /**
     * 判断执行组织模型实体按照特定属性及值已经存在
     * 
     * @param entityClass
     * @param property
     * @param value
     * @param accountId
     * @return
     */
    <T extends V3xOrgEntity> boolean isPropertyDuplicated(Class<T> entityClass, String property, Object value,
        Long accountId);

    /**
     * 判断执行组织模型实体按照特定属性及值已经存在
     * 
     * @param entityClass
     * @param property
     * @param value
     * @param accountId
     * @param entId
     * @return
     */
    <T extends V3xOrgEntity> boolean isPropertyDuplicated(Class<T> entityClass, String property, Object value,
        Long accountId, Long entId);

    <T extends V3xOrgEntity> boolean isPropertyDuplicated(Class<T> entityClass, String property, Object value,
        Long accountId, Long entId, int externalType);

    <T extends V3xOrgEntity> boolean isPropertyDuplicated(Class<T> entityClass, String property, Object value,
        Long accountId, Long entId, int externalType, Long createrId);

    /**
     * 判断执行组织模型实体按照特定属性及值已经存在
     * 
     * @param entityClass
     * @param property
     * @param value
     * @return
     */
    <T extends V3xOrgEntity> boolean isPropertyDuplicated(Class<T> entityClass, String property, Object value);

    /**
     * 判断执行组织模型实体按照特定属性及值已经存在
     * 
     * @param entityClass
     * @param property
     * @param value
     * @param externalType
     *            V-Join元素类型
     * @return
     */
    <T extends V3xOrgEntity> boolean isPropertyDuplicated(Class<T> entityClass, String property, Object value,
        int externalType);

    String getMaxPathByParentPath(String parentPath);

    /**
     * 获取未分配人员
     * 
     * @param accountId
     * @param flipInfo
     * @return
     */
    public List<OrgMember> getAllUnAssignedMember(Long accountId, FlipInfo flipInfo);

    /**
     * 获取未分配人员
     * 
     * @param accountId
     * @param flipInfo
     * @return
     */
    public List<OrgMember> getAllUnAssignedMember(Long accountId, FlipInfo flipInfo, Map beforeParams);

    /**
     * 根据path获取组织（包括未启用）
     * 
     * @param path
     * @return
     */
    public OrgUnit getV3xOrgUnitByPath(String path);

    /**
     * 按照关系表id删除一条关系
     * 
     * @param id
     */
    public void deleteOrgRelationshipPOById(Long id);

    /**
     * 获取某单位的外部人员的最大排序号
     * 
     * @param accountId
     * @return
     */
    int getExtMemberMaxSortId(Long accountId);

    /**
     * 兼职管理的查询SQL
     * 
     * @param memberName
     * @param postId
     * @param accountId
     * @param objectiveIds
     * @param flipInfo
     * @return
     */
    List<OrgRelationship> getOrgRelationshipPO4ConPost(String memberName, Long postId, Long accountId,
        Long conAccountId, EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds,
        boolean isSubUnitManage, List<Long> subUnitIds, FlipInfo flipInfo);

    List<OrgMember> getAllUnenabledMembers(Long accountId, FlipInfo flipInfo);

    List<OrgPost> getAllUnenabledPosts(Long accountId, FlipInfo flipInfo);

    List<OrgLevel> getAllUnenabledLevels(Long accountId, FlipInfo flipInfo);

    List<OrgUnit> getAllUnenabledDepartments(Long accountId, FlipInfo flipInfo);

    List<OrgUnit> getAllUnenabledAccounts(Long accountId, FlipInfo flipInfo);

    List<OrgTeam> getAllUnenabledTeams(Long accountId, FlipInfo flipInfo);

    /**
     * 获取每个单位人员数
     * 
     * @return
     */
    Map<Long, Long> getMemberNumsMapWithOutConcurrent();

    /**
     * 批量删除关系
     * 
     * @param orgRelationshipPOs
     */
    void deleteRelationships(List<OrgRelationship> orgRelationshipPOs);

    /**
     * 仅供内部使用,可以查询多单位数据
     * 
     * @param type
     *            可以为<code>null</code>，表示所有，需谨慎
     * @param sourceIds
     *            可以为<code>null</code>，表示所有，需谨慎
     * @param accountIds
     *            可以为<code>null</code>，表示所有，需谨慎
     * @param objectiveIds
     *            可以为<code>null</code>，表示所有
     * @param flipInfo
     *            可以为<code>null</code>，表示不分页
     * @return
     */
    List<OrgRelationship> getOrgRelationshipPOByAccountsAndMembers(RelationshipType type, List<Long> sourceIds,
        List<Long> accountIds, EnumMap<RelationshipObjectiveName, Object> objectiveIds, FlipInfo flipInfo);

    /**
     * 查询汇报人在此部门下的所有人员
     * 
     * @param deptId
     * @return
     */
    List<OrgMember> getAllMembersByReportToDept(Long deptId);

    /**
     * 查询汇报人为此人的所有人员
     * 
     * @param deptId
     * @return
     */
    List<OrgMember> getAllMembersByReportToMember(Long[] memberIds);

    /**
     * 
     * @param type
     *            单位\部门
     * @param accountId
     * @param enable
     *            是否有效
     * @param isInternal
     *            是否内部部门或单位
     * @param condition
     *            查询条件
     * @param feildvalue
     *            查询条件对应的值
     * @param flipInfo
     *            分页对象
     * @param isEquals,表示查询字段是模糊查询还是全字匹配，true：全字匹配，false：模糊查询
     * @return
     */
    List<OrgUnit> getAllUnitPO0(OrgConstants.UnitType type, Long accountId, Boolean enable, Boolean isInternal,
        String condition, Object feildvalue, FlipInfo flipInfo, boolean isEquals);

    /**
     * 实体名称
     * 
     * @param entityClassName
     * @param accountId
     *            所属单位名称，null 表示查全集团
     * @return
     */
    List<BasePO> getAllEntityPO(String entityClassName, Long accountId, Date lastDate);

    /**
     * 实体名称
     * 
     * @param entityClassName
     * @param accountId
     *            所属单位名称，null 表示查全集团
     * @param externalType
     *            : OrgConstants.ExternalType
     * @return
     */
    List<BasePO> getAllEntityPO(String entityClassName, Long accountId, Date lastDate, int externalType);

    /**
     * 得到组织模型外部实体的当前最大排序号，不过不存在，就返回0
     * 
     * @param entityClass
     * @param accountId
     * @param externalType
     * @return
     */
    <T extends V3xOrgEntity> int getMaxSortId(Class<T> entityClass, Long accountId, int externalType);

    <T extends V3xOrgEntity> int getMaxSortId(Class<T> entityClass, Long accountId, int externalType, Long createrId);

    /**
     * 获取集团下的所有有效人员，包含管理员，用户首次初始话的时候加载所有人员
     * 
     * @return
     */
    List<OrgMember> getAllGroupEnableMemberPO();

    /**
     * 获取集团下的访客
     * 
     * @return
     */
    List<OrgVisitor> getAllVisitorPO();

    /**
     * 查询停用和删除的实体
     * 
     * @param entityClassName
     * @param accountId
     * @return
     */
    List<BasePO> getDisableEntityPO(String entityClassName, Long accountId, String condition, Object feildvalue);

    /**
     * 
     * 获取系统第一个创建的人
     * 
     * @return
     */
    OrgMember getFirstCreateMember();

    /**
     * 
     * @param entityClassName
     * @param accountId
     * @param lastDate
     * @param externalType
     * @return
     */
    List<Long> getAllEntityIds(String entityClassName, Long accountId, Date lastDate, int externalType);

    /**
     * 获取业务线单位
     * 
     * @param type
     * @param createrId
     * @param enable
     * @param condition
     * @param feildvalue
     * @param flipInfo
     * @return
     */
    List<OrgUnit> getAllBusinessUnitPO(Long createrId, Boolean enable, String condition, Object feildvalue,
        FlipInfo flipInfo);

    /**
     * 更新访客信息
     * 
     * @param orgVisitor
     */
    void update(OrgVisitor orgVisitor);

    /**
     * 查询访客信息列表
     * 
     * @param flipInfo
     * @param param
     *            state = -1,表示包含所有状态的人（启用，停用，删除）
     * @return
     * @throws BusinessException
     */
    public List<OrgVisitor> getOrgVisitor(FlipInfo flipInfo, Map param) throws BusinessException;
}