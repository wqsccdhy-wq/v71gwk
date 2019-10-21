/* $Author: tanmf $
 * $Rev: 0 $
 * $Date: 2012-08-02 13:38:17#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.organization.dao;

import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.ORGENT_TYPE;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.RelationshipType;
import com.seeyon.ctp.organization.OrgConstants.UnitType;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.bo.V3xOrgVisitor;
import com.seeyon.ctp.organization.po.OrgRelationship;


/**
 * <p>Title: T2 组织模型 Cache</p>
 * <p>Description: 本程序实现对内存中的组织结构模型的管理</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * @since CTP2.0
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 */
public interface OrgCache {
	
    public  final static String SUBDEPT_INNER_FIRST = "1";
    public  final static String SUBDEPT_INNER_ALL = "2";
    public  final static String SUBDEPT_OUTER_ALL = "3";
    public  final static String SUBDEPT_OUTER_FIRST = "4";
    
    void init();
    
	/**
	 * 取所有单位。 默认为A8内实体
	 * 
	 * @return
	 */
	List<V3xOrgAccount> getAllAccounts();
	
	/**
	 * 按类型获取所有单位（内部单位，v-join平台单位）
	 * externalType  0:内部   !0:外部  null:全部
	 * @return
	 */
	List<V3xOrgAccount> getAllAccounts(Integer externalType);
	
	/**
	 * 取子单位，以及子子子单位，不包含自己
	 * 
	 * @param accountId
	 * @return
	 */
	List<V3xOrgAccount> getChildAccount(Long accountId);
	
	/**
	 * 获取某单位下的所有部门
	 * @param accountId
	 * @return
	 */
	List<V3xOrgDepartment> getChildDeptByAccountId(Long accountId);
	
	/**
	 * 得到指定单位下所有的有效部门（默认为A8内实体）
	 * 
	 * @param accountId
	 * @return
	 */
	List<V3xOrgDepartment> getAllV3xOrgDepartment(Long accountId);
	/**
	 * 得到指定单位下所有的有效部门
	 * @param accountId
	 * @param externalType  0:内部   !0:外部  null:全部
	 * @return
	 */
	List<V3xOrgDepartment> getAllV3xOrgDepartment(Long accountId,Integer externalType);

	/**
	 * 得到指定单位下(不包含子单位)，指定实体类型所有的有效数据，此方法是克隆出去一份，不影响缓存 （默认为A8内实体）
	 * 
	 * @param classType
	 * @param accountId
	 * @return
	 */
	<T extends V3xOrgEntity> List<T> getAllV3xOrgEntity(Class<T> classType, Long accountId);
	/**
	 * 得到指定单位下(不包含子单位)，指定实体类型所有的有效数据，此方法是克隆出去一份，不影响缓存
	 * 
	 * @param classType
	 * @param accountId
	 * @param externalType  0:内部   !0:外部  null:全部
	 * @return
	 */
	<T extends V3xOrgEntity> List<T> getAllV3xOrgEntity(Class<T> classType, Long accountId,Integer externalType);
	
	/**
	 * 得到指定单位下(不包含子单位)，指定实体类型所有的有效数据    默认为A8内实体
	 * 此方法慎用，仅供组织模型内部使用，此方法不克隆，用于更新某单位某类实体的缓存内容
	 * @param classType
	 * @param accountId
	 * @return
	 */
	<T extends V3xOrgEntity> List<T> getAllV3xOrgEntityNoClone(Class<T> classType, Long accountId);
	
	/**
	 * 得到指定单位下(不包含子单位)，指定实体类型所有的有效数据
	 * 此方法慎用，仅供组织模型内部使用，此方法不克隆，用于更新某单位某类实体的缓存内容
	 * @param classType
	 * @param accountId
	 * @param 0:内部   !0:外部  null:全部
	 * @return
	 */
	<T extends V3xOrgEntity> List<T> getAllV3xOrgEntityNoClone(Class<T> classType, Long accountId, Integer externalType);
	
	/**
	 * 根据Id得到指定实体类型的有效数据，克隆
	 * 
	 * @param classType
	 * @param id
	 * @return
	 */
	<T extends V3xOrgEntity> T getV3xOrgEntity(Class<T> classType, Long id);
	<T extends V3xOrgEntity> T getV3xOrgEntityNoClone(Class<T> classType, Long id);
	<T extends V3xOrgEntity> T getV3xOrgEntity(Class<T> classType, Long id, boolean clone);
	
	/**
	 * 根据path取得机构
	 * 
	 * @param path
	 * @return
	 */
	V3xOrgUnit getV3xOrgUnitByPath(String path);
	
	/**
	 * 根据单位取出兼职或副岗关系
	 * @param accountId
	 * @param departmenId
	 * @param types
	 * @return
	 */
	public List<V3xOrgRelationship> getEntityConPostRelastionships(Long accountId, Long departmenId, OrgConstants.MemberPostType... types);
	/**
	 * 获取人员的组关系数据
	 * @param memberId
	 * @param accountId
	 * @param types
	 * @return
	 */
	public List<V3xOrgRelationship> getMemberTeamRelastionships(Long memberId, List<Long> accountIds, OrgConstants.TeamMemberType... types);
	/**
	 * 获取组的人员关系数据
	 * @param teamId
	 * @param types
	 * @return
	 */
	public List<V3xOrgRelationship> getTeamMemberRelastionships(Long teamId, OrgConstants.TeamMemberType... types);
	
	/**
	 * 取得指定人员的岗位关系，包括主副兼
	 * 
	 * @param memberId
	 * @param accounId 可以为<code>null</code>，表示所有
	 * @param postTypes 可以为<code>null</code>，表示所有
	 * @return
	 */
	public List<V3xOrgRelationship> getMemberPostRelastionships(Long memberId, Long accounId, OrgConstants.MemberPostType... postTypes);
	
	/**
	 * 取得指定部门下的岗位关系，包括主副兼
	 * 
	 * @param departmentId
	 * @param postTypes 可以为<code>null</code>，表示所有
	 * @return
	 */
	public List<V3xOrgRelationship> getDepartmentRelastionships(Long departmentId, OrgConstants.MemberPostType... postTypes);
	
	/**
	 * 取得指定部门下的岗位关系，包括主副兼
	 * 
	 * @param departmentIds
	 * @param postTypes 可以为<code>null</code>，表示所有
	 * @return
	 */
	public List<V3xOrgRelationship> getDepartmentRelastionships(List<Long> departmentIds, OrgConstants.MemberPostType... postTypes);
	
	/**
	 * 取得entityId对应的角色关系
	 * @param entityId
	 * @param unitId 
	 *         单位ID  <br>
	 *         部门ID  <br>
	 *         可以为<code>null</code>，表示所有<br>
	 * @param acountId 可以为<code>null</code>，表示所有<br>
	 * @return
	 */
	public List<V3xOrgRelationship> getEntityRoleRelastionships(List<Long> entityIds, Long unitId, Long accountId);
	
	/**
	 * 取得具有该角色所有实体的关系数据
	 * 
	 * @param roleId
	 * @param unitId
	 *         单位ID  <br>
     *         部门ID  <br>
     *         可以为<code>null</code>，表示所有<br>
	 * @param accountId 可以为<code>null</code>，表示所有<br>
	 * @return
	 */
	public List<V3xOrgRelationship> getRoleEntityRelastionships(Long roleId, Long unitId, Long accountId);
	/**
	 * 获取指定类型所有的关系对象
	 * 
	 * @param type
	 * @return
	 */
	public List<V3xOrgRelationship> getV3xOrgRelationship(OrgConstants.RelationshipType type);
	
	/**
	 * 
	 * @param type <b>不可以</b>为null
	 * @param sourceId 可以为<code>null</code>
	 * @param accountId 可以为<code>null</code>
	 * @param objectiveIds 可以为<code>null</code>，value类型只能是Long/String/List&lt;Long&gt;/List&lt;String&gt;
	 * @return
	 */
	List<V3xOrgRelationship> getV3xOrgRelationship(RelationshipType type, Long sourceId, Long accountId, EnumMap<RelationshipObjectiveName, Object> objectiveIds);
	
	/**
	 * 支持批量查询条件
	 * @param type <b>不可以</b>为null
	 * @param sourceIds 可以为<code>null</code>
	 * @param accountId 可以为<code>null</code>
	 * @param objectiveIds 可以为<code>null</code>，value类型只能是Long/String/List&lt;Long&gt;/List&lt;String&gt;
	 * @return
	 */
	List<V3xOrgRelationship> getV3xOrgRelationship(RelationshipType type, List<Long> sourceIds, List<Long> accountId, EnumMap<RelationshipObjectiveName, Object> objectiveIds);
	
	/**
	 * 通过ID获取关系实体
	 * @param id 关系主键
	 * @return
	 */
	V3xOrgRelationship getV3xOrgRelationshipById(Long id);
	
	/**
	 * 根据类型和id获取关系数据对象
	 * @param type
	 * @param id
	 * @return
	 */
	V3xOrgRelationship getV3xOrgRelationshipByTypeAndId(String type, Long id);
	
	/**
	 * 取得指定单位所在的集团单位, 如果是独立单位, 就返回<code>null</code>
	 * 
	 * @param accountId
	 * @return
	 */
	V3xOrgAccount getGroupAccount(Long accountId);
	
	/**
	 * 得到指定单位、指定类型的最后修改时间戳
	 * 
	 * @param entType
	 * @param accountId
	 * @return
	 */
    public Date getModifiedTimeStamp(String entType, Long accountId);
    
    /**
     * 更新时间戳
     */
    public void updateModifiedTimeStamp();
    
    /**
     * 判断指定单位、指定类型是否修改
     * @param entType
     * @param date
     * @param accountId
     * @return
     */
    public boolean isModified(String entType, Date date, Long accountId);
	
	/**
	 * 更新缓存，只允许OrgDao访问，其它任何类不可访问此访问
	 * 
	 * @param orgEntity
	 */
	<T extends V3xOrgEntity> void cacheUpdate(T orgEntity);
	
	<T extends V3xOrgEntity> void cacheRemove(T orgEntity);
	
	/**
	 * 更新缓存，其它任何类不可访问此访问，List里面只能是一种类型
	 * 
	 * @param orgEntities 
	 */
	<T extends V3xOrgEntity> void cacheUpdate(List<T> orgEntities);
	
	public void cacheUpdateRelationship(List<OrgRelationship> rs);
	
	/**
	 * 仅仅更新SortId
	 * @param rsIds
	 */
	public void cacheUpdateRelationshipOnlySortId(List<Long> rsIds);
	
	public void cacheUpdateV3xOrgEntityOnlySortId(List<Long> entityIds,ORGENT_TYPE type);
	
	public void cacheRemoveRelationship(List<OrgRelationship> rs);
	
	
	/**
	 * 取得所有组织，主要包括单位和部门
	 * @return
	 */
	public List<V3xOrgUnit> getAllUnits();

	/**
	 * 根据父组织获取所有子组织，包括Account和Department
	 * @param classType 父节点的类型，如Account/Department
	 * @param parentId 父节点Id
	 * @return
	 */
	<T extends V3xOrgUnit> List<V3xOrgUnit> getChildUnitsByPid(Class<T> classType, Long parentId);
	/**
	 * 根据父组织获取所有子组织，包括Account和Department 是否不包含停用
	 * @方法名称: getChildUnitsByPid
	 * @功能描述: 
	 * @参数 ：@param classType 父节点的类型
	 * @参数 ：@param parentId 父节点Id
	 * @参数 ：@param disIncludeDisable 默认null|false包含停用 。true为不包含
	 * @参数 ：@param firtLayer ,true:一级子部门，false:所有子部门
	 * @参数 ：@return
	 * @返回类型：List<V3xOrgUnit>
	 * @创建时间 ：2016年6月7日 上午10:53:02
	 * @创建人 ： FuTao
	 * @修改人 ： 
	 * @修改时间 ：
	 */
	<T extends V3xOrgUnit> List<V3xOrgUnit> getChildUnitsByPid(Class<T> classType, Long parentId, Boolean disIncludeDisable, boolean firtLayer);
	
	/**
	 * 获取path长度相同的兄弟单位列表
	 * @param path
	 * @return
	 */
	List<V3xOrgAccount> getSameLengthPathUnits(String path);
	
	List<V3xOrgAccount> getSameLengthPathUnits(String path, Integer externalType);
	
	/**
	 * 获取比传入参数path短的"上级"单位列表
	 * @param path
	 * @return
	 */
	List<V3xOrgAccount> getShorterLengthPathUnits(String path);
	
	/**
	 * 设置导出动作的标志
	 * true 正在导出
	 * false 已经完成导出
	 * @param flag
	 */
	void setOrgExportFlag(boolean flag);
	
	/**
	 * 获取导出组织信息动作的标志
	 * @return true,exporting; false,end exported
	 */
	boolean getOrgExportFlag();
	
	/**
	 * 只根据ID去查询人员，部门，单位，岗位，职务，组实体的对象，慎用
	 * @param id
	 * @return
	 */
	V3xOrgEntity getEntityOnlyById(Long id);
	
	/**
	 * 从无效实体缓存中读取，clone
	 * 
	 * @param id
	 * @return
	 */
	public <T extends V3xOrgEntity> T getDisabledEntity(Class<T> entityClass, Long id);
	public <T extends V3xOrgEntity> T getDisabledEntity(Class<T> entityClass, Long id, boolean isClone);
    
	/**
	 * 把无效实体对象缓存起来
	 * @param entity
	 */
    public void cacheDisabledEntity(V3xOrgEntity entity);

    /**
     * 根据父组织获取所有子组织，包括Account和Department
     * @param classType 父节点的类型，如Account/Department
     * @param parentId 父节点Id
     * @return
     */
    <T extends V3xOrgUnit> List<V3xOrgUnit> getChildUnitsByPath(Class<T> classType, String parentPath);

    /**
     * 
     * @param parentDpetId  父部门id
     * @param subDeptType
     * null 所有有效子部门
     * 1 内部部门-直接子部门
     * 2 内部部门-所有子部门
     * 3 外部部门（只有一级子部门）  
     * @return
     */
	List<Long> getSubDeptList(Long parentDpetId, String subDeptType);
	/**
	 * 获取部门下的所有有效子部门
	 * @param parentDpetId
	 * @return
	 */
	List<Long> getSubDeptList(Long parentDpetId);

	/**
	 * 获取v-join平台，访问内部人员的关系数据
	 * @param memberId
	 * @return
	 */
	List<Long> getAccessMemberOrgRelationshipIds(Long memberId);

	/**
	 * 获取所有内部人员访问v-join平台的权限关系数据
	 * @return
	 */
	Set<Long> getAllBeAccessOrgRelationshipIds();

	/**
	 * 获取单位下的所有人员id（包含兼职人员）
	 * @param accountId
	 * @return
	 */
    List<Long> getAllMembers(Long accountId);

    /**
     * 获取单位下的职务级别
     * @param accountId
     * @return
     */
	List<Long> getAllLevels(Long accountId);

	/**
	 * 获取单位下的角色
	 * @param accountId
	 * @return
	 */
	List<Long> getAllRoles(Long accountId);

	/**
	 * 获取停用的单位或者部门
	 * @param accountId
	 * @param types
	 * @return
	 */
	List<V3xOrgUnit> getDisableUnits(Long accountId, OrgConstants.UnitType ... types);

	/**
	 * 获取vjoin对vjoin的访问权限(暂时只有外部单位对外部单位的访问权限)
	 * @param entityId
	 * @return
	 */
	List<Long> getVjoinAccessEntityOrgRelationshipIds(Long entityId);

	/**
	 * 根据手机号获取对应的人员集合
	 * 包含所有人员（内部人员，编外人员，vjoin人员）
	 * @param telNum
	 * @return
	 */
	List<Long> getMembersByTelnum(String telNum);

	List<V3xOrgRelationship> getDepartmentPostRelastionships(Long departmentId);

	/**
	 * 从缓存中获取访客（包含启用和禁用的访客）
	 * @param id
	 * @param clone
	 * @return
	 */
	V3xOrgVisitor getV3xOrgVisitor(Long id, boolean clone);
}
