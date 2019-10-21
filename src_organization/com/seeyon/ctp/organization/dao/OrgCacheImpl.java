
package com.seeyon.ctp.organization.dao;

import com.seeyon.ctp.datasource.CtpDynamicDataSource;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StopWatch;

import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.common.cache.CacheObject;
import com.seeyon.ctp.common.cache.redis.RedisHandler;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.MemberPostType;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.RelationshipType;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.bo.V3xOrgVisitor;
import com.seeyon.ctp.organization.po.OrgLevel;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.organization.po.OrgPost;
import com.seeyon.ctp.organization.po.OrgRelationship;
import com.seeyon.ctp.organization.po.OrgRole;
import com.seeyon.ctp.organization.po.OrgTeam;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.organization.po.OrgVisitor;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.FoolishSet;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;

public class OrgCacheImpl extends AbstractSystemInitializer implements OrgCache {

    private final static Log log = LogFactory.getLog(OrgCacheImpl.class);
    private final static Log log4Rel = LogFactory.getLog("orgrel");

    // key - Id
    private CacheMap<Long, V3xOrgUnit> OrgUnitCache = null;
    //停用的单位或者部门
    private CacheMap<Long, V3xOrgUnit> OrgUnitDisableCache = null;
   /**部门下的子部门，  key-parentDeptId/accountId  - subDeptList
    * parentDeptId    所有有效子部门（含外部部门）
    * parentDeptId_1  内部部门-直接子部门
    * parentDeptId_2  内部部门-所有子部门
    * parentDeptId_3  编外部门（所有）
    * parentDeptId_4  编外部门/外部部门（直接子部门）  --主要用在外部互联vjoin **/
    private CacheMap<String, ArrayList<Long>> SubDeptCache = null;
    private CacheMap<Long, V3xOrgPost> OrgPostCache = null;
    private CacheMap<Long, V3xOrgMember> OrgMemberCache = null;
    private CacheMap<Long, V3xOrgRole> OrgRoleCache = null;
    private CacheMap<Long, V3xOrgTeam> OrgTeamCache = null;
    private CacheMap<Long, V3xOrgLevel> OrgLevelCache = null;
    private CacheMap<Long, V3xOrgVisitor> OrgVisitorCache = null;
    //v-join平台访问内部人员的的关系数据 Key - Member.id - Access.RS.id
    private CacheMap<Long, ArrayList<Long>> OrgRelationshipId2AccessCache = null;
    //内部人员访问v-join平台人员的关系数据  Key -RS.id Value - BeAccessed.Rs
    private CacheMap<Long, V3xOrgRelationship> OrgRelationshipId2BeAccessedCache = null;
    //v-join平台访v-join平台关系数据  - vj.entity.id- VjoinAccess.RS.id
    private CacheMap<Long, ArrayList<Long>> OrgRelationshipId2VjoinAccessCache = null;

    //Key - Member.id; Value - MemberPost.RS.id
    private CacheMap<Long, ArrayList<Long>> OrgMemberPostRSCache = null;
    //Key - Account.id;Value - MemberPost(second,concurrent).RS.id
    private CacheMap<Long, ArrayList<Long>> OrgEntityConPostRSCache = null;
    //key - Account.id.value - Member.id 单位下的人员
    private CacheMap<Long, ArrayList<Long>> OrgAccountMemberCache = null;
    //key - Member.telnum - Member.id  手机号码下的人员
    private CacheMap<String, ArrayList<Long>> OrgTelnumMemberCache = null;

    //key - Account.id.value - Level.id 单位下的职务级别
    private CacheMap<Long, ArrayList<Long>> OrgAccountLevelCache = null;

    //key - Account.id.value - Role.id 单位下的角色
    private CacheMap<Long, ArrayList<Long>> OrgAccountRoleCache = null;

    //Key - Department.id;Value - MemberPost.RS.id
    private CacheMap<Long, ArrayList<Long>> OrgDepartmentRSCache = null;
    //Key - Member.id;Value - TeamMember.RS.id
    private CacheMap<Long, ArrayList<Long>> OrgMemberTeamRSCache = null;
    //Key - Team.id;Value - TeamMember.RS.id
    private CacheMap<Long, ArrayList<Long>> OrgTeamMemberRSCache = null;
    //Key - Emtity.id; Value - MemberRole.RS.id
    private CacheMap<Long, ArrayList<Long>> OrgEntityRoleRSCache = null;
    //Key - Role.id; Value  -  MemberRole.RS.id
    private CacheMap<Long, ArrayList<Long>> OrgRoleEntityRSCache = null;
    //Key - Department.id; Value  -  DepartmentPost.RS.id
    private CacheMap<Long, ArrayList<Long>> OrgDepartmentPostRSCache = null;

    //无效实体缓存（只有被查过一次之后，才会进缓存）。
    private CacheMap<String, V3xOrgEntity> OrgDisabledEntityCache = null;

    private Map<String, CacheMap<Long, V3xOrgRelationship>> relationshipMap = new HashMap<String, CacheMap<Long,V3xOrgRelationship>>();

    private CacheObject<Date> OrgModifyState = null;

    /**
     * export loading flag
     * true exporting
     * false exported
     */
    private CacheObject<Boolean> OrgExportFlag = null;

    //无效实体的缓存， 不做集群同步 TODO 根据王文友提供的LRU\MRU组件把这个替换掉即可
    //private Map<String, V3xOrgEntity> disabledEntity = new ConcurrentHashMap<String, V3xOrgEntity>();

    private OrgDao orgDao;

    public void setOrgDao(OrgDao orgDao){
        this.orgDao = orgDao;
    }

    @Override
    public int getSortOrder() {
        return -999;
    }

    public void initialize() {

        try{
            CtpDynamicDataSource.setDataSourceKey(DataSourceName.BASE.getSource());
            doInitialize();
        }finally {
            CtpDynamicDataSource.clearDataSourceKey();
        }

    }

    private void doInitialize(){
        StopWatch watch = new StopWatch();
        watch.start();
        CacheAccessable factory = CacheFactory.getInstance(OrgCache.class);
        // key - Id
        OrgUnitCache = generateCacheMap(factory,"OrgUnit");
        OrgUnitDisableCache = generateCacheMap(factory,"OrgUnitDisableCache");
        SubDeptCache = generateCacheMap(factory,"SubDept");
        OrgPostCache = generateCacheMap(factory,"OrgPost");
        OrgMemberCache = generateCacheMap(factory,"OrgMember");
        OrgRoleCache = generateCacheMap(factory,"OrgRole");
        OrgTeamCache = generateCacheMap(factory,"OrgTeam");
        OrgLevelCache = generateCacheMap(factory,"OrgLevel");
        OrgVisitorCache = generateCacheMap(factory,"OrgVisitor");

        OrgRelationshipId2AccessCache = generateCacheMap(factory,"OrgRelationshipId2AccessCache");
        OrgRelationshipId2BeAccessedCache = generateCacheMap(factory,"OrgRelationshipId2BeAccessedCache");
        OrgRelationshipId2VjoinAccessCache = generateCacheMap(factory,"OrgRelationshipId2VjoinAccessCache");

        for (OrgConstants.RelationshipType o : OrgConstants.RelationshipType.values()) {
            CacheMap<Long, V3xOrgRelationship>  orgRelationshipId2POCacheForType = generateCacheMap(factory,"OrgRelationshipId2PO_" + o.name());
            relationshipMap.put(o.name(), orgRelationshipId2POCacheForType);
        }

        OrgMemberPostRSCache = generateCacheMap(factory,"OrgMemberPostRSCache");

        OrgEntityConPostRSCache = generateCacheMap(factory,"OrgEntityConPostRSCache");

        OrgAccountMemberCache = generateCacheMap(factory,"OrgAccountMemberCache");

        OrgTelnumMemberCache = generateCacheMap(factory,"OrgTelnumMemberCache");

        OrgAccountLevelCache = generateCacheMap(factory,"OrgAccountLevelCache");

        OrgAccountRoleCache = generateCacheMap(factory,"OrgAccountRoleCache");

        OrgDepartmentRSCache = generateCacheMap(factory,"OrgDepartmentRSCache");

        OrgEntityRoleRSCache = generateCacheMap(factory,"OrgEntityRoleRSCache");

        OrgMemberTeamRSCache = generateCacheMap(factory,"OrgMemberTeamRSCache");

        OrgTeamMemberRSCache = generateCacheMap(factory,"OrgTeamMemberRSCache");

        OrgRoleEntityRSCache = generateCacheMap(factory,"OrgRoleEntityRSCache");

        OrgDepartmentPostRSCache = generateCacheMap(factory,"OrgDepartmentPostRSCache");
        
        OrgDisabledEntityCache = generateCacheMap(factory,"OrgDisabledEntityCache");
        
        
        OrgModifyState = factory.createObject("OrgModifyState");

        OrgExportFlag = factory.createObject("OrgExportFlag");

        //	门户应用缓存Create时已经从Redis中加载，直接返回
        if(RedisHandler.isReadOnly(OrgCache.class)){
            watch.stop();
            log.info("从Redis加载组织模型完成，耗时：" + watch.getTotalTimeMillis() + "毫秒");
            return;
        }

        //如果其他节点已经正常启动，启动时不需要重新load一遍数据，会自动从其他节点同步数据过来。
        if(!CacheFactory.isSkipFillData()){
            //数据修复
            StopWatch watch0 = new StopWatch();
            watch0.start();
            OrgDataClear.clearOrgDataNoCache();
            watch0.stop();
            log.info("组织模型数据清理完成，耗时：" + watch0.getTotalTimeMillis() + "毫秒，详细信息请查看org.log!");

            Map<String, Long> unitPath2ID = new HashMap<String, Long>();

            //取所有启用的实体
            List<OrgUnit> allOrgUnit = this.orgDao.getAllUnitPO(null, null, true, null, null, null, null);
            for (OrgUnit orgUnit : allOrgUnit) {
                if(OrgConstants.UnitType.Account.name().equals(orgUnit.getType())){
                    OrgUnitCache.put(orgUnit.getId(), new V3xOrgAccount(orgUnit));
                }
                else if(OrgConstants.UnitType.Department.name().equals(orgUnit.getType())){
                    OrgUnitCache.put(orgUnit.getId(), new V3xOrgDepartment(orgUnit));
                }

                unitPath2ID.put(orgUnit.getPath(), orgUnit.getId());
            }

            List<OrgUnit> disableUnit = this.orgDao.getAllUnitPO(null, null, false, null, null, null, null);
            for (OrgUnit orgUnit : disableUnit) {
                if(OrgConstants.UnitType.Account.name().equals(orgUnit.getType())){
                    OrgUnitDisableCache.put(orgUnit.getId(), new V3xOrgAccount(orgUnit));
                }
                else if(OrgConstants.UnitType.Department.name().equals(orgUnit.getType())){
                    OrgUnitDisableCache.put(orgUnit.getId(), new V3xOrgDepartment(orgUnit));
                }

            }

            //设置Unit的父ID
            for (V3xOrgUnit orgUnit : OrgUnitCache.values()) {
                orgUnit.setSuperior(unitPath2ID.get(orgUnit.getParentPath()));
            }
            //初始化单位/部门-部门下的子部门缓存
            initSubDeptCache();

            List<OrgPost> allOrgPost = this.orgDao.getAllPostPO(null, true, null, null, null);
            for (OrgPost orgPost : allOrgPost) {
                OrgPostCache.put(orgPost.getId(), new V3xOrgPost(orgPost));
            }
            List<OrgMember> allOrgMember = this.orgDao.getAllGroupEnableMemberPO();
            for (OrgMember orgMember : allOrgMember) {
                V3xOrgMember member = new V3xOrgMember(orgMember);
                OrgMemberCache.put(orgMember.getId(), member);
                if(member != null && !member.getIsAdmin() && member.isValid()){
                    if(member.getIsInternal()){
                        add2Cache(OrgAccountMemberCache, member.getOrgAccountId(), member.getId());
                    }
                    if(Strings.isNotBlank(member.getTelNumber())){
                        add2Cache_(OrgTelnumMemberCache, member.getTelNumber(), member.getId());
                    }
                }
            }

            List<OrgVisitor> allOrgVisitor = this.orgDao.getAllVisitorPO();
            for (OrgVisitor orgVisitor : allOrgVisitor) {
                V3xOrgVisitor visitor = new V3xOrgVisitor(orgVisitor);
                OrgVisitorCache.put(orgVisitor.getId(), visitor);
            }

            List<OrgRole> allOrgRole = this.orgDao.getAllRolePO(null, true, null, null, null);
            for (OrgRole orgRole : allOrgRole) {
                OrgRoleCache.put(orgRole.getId(), new V3xOrgRole(orgRole));
                add2Cache(OrgAccountRoleCache, orgRole.getOrgAccountId(), orgRole.getId());
            }
            List<OrgLevel> allOrgLevel = this.orgDao.getAllLevelPO(null, true, null, null, null);
            for (OrgLevel orgLevel : allOrgLevel) {
                OrgLevelCache.put(orgLevel.getId(), new V3xOrgLevel(orgLevel));
                add2Cache(OrgAccountLevelCache, orgLevel.getOrgAccountId(), orgLevel.getId());
            }
            List<OrgTeam> allOrgTeam = this.orgDao.getAllTeamPO(null, null, true, null, null, null);
            for (OrgTeam orgTeam : allOrgTeam) {
                OrgTeamCache.put(orgTeam.getId(), new V3xOrgTeam(orgTeam));
            }

            //初始化关系
            List<OrgRelationship> allOrgRelationship = this.orgDao.getOrgRelationshipPO(null, null, null, null, null);

            for (OrgRelationship orgRelationship : allOrgRelationship) {
                V3xOrgRelationship rs = new V3xOrgRelationship(orgRelationship);
                CacheMap<Long, V3xOrgRelationship>  orgRelationshipId2POCacheForType = relationshipMap.get(rs.getKey());
                orgRelationshipId2POCacheForType.put(orgRelationship.getId(), rs);

                if(Strings.equals(OrgConstants.RelationshipType.Member_Post.name(), orgRelationship.getType())){

                    add2Cache(OrgMemberPostRSCache, orgRelationship.getSourceId(), orgRelationship.getId());

                    if(isConPost(orgRelationship)){
                        add2Cache(OrgEntityConPostRSCache, orgRelationship.getOrgAccountId(), orgRelationship.getId());
                    }

                    //部门下的rs信息
                    if(null!=orgRelationship.getObjective0Id()){
                        add2Cache(OrgDepartmentRSCache, orgRelationship.getObjective0Id(), orgRelationship.getId());
                    }
                }

                if(Strings.equals(OrgConstants.RelationshipType.Department_Post.name(), orgRelationship.getType())){
                    add2Cache(OrgDepartmentPostRSCache, orgRelationship.getSourceId(), orgRelationship.getId());
                }
                
                if(Strings.equals(OrgConstants.RelationshipType.Member_Role.name(), orgRelationship.getType())){
                    add2Cache(OrgEntityRoleRSCache, orgRelationship.getSourceId(), orgRelationship.getId());
                    add2Cache(OrgRoleEntityRSCache, orgRelationship.getObjective1Id(), orgRelationship.getId());
                }

                if(Strings.equals(OrgConstants.RelationshipType.Team_Member.name(), orgRelationship.getType())){
                    add2Cache(OrgMemberTeamRSCache, orgRelationship.getObjective0Id(), orgRelationship.getId());
                    add2Cache(OrgTeamMemberRSCache, orgRelationship.getSourceId(), orgRelationship.getId());
                }

                {
                    if(Strings.equals(OrgConstants.RelationshipType.External_Access.name(), orgRelationship.getType())){
                        if(orgRelationship.getObjective5Id()!=null && OrgConstants.ExternalAccessType.Access.name().equals(orgRelationship.getObjective5Id())){
                            add2Cache(OrgRelationshipId2AccessCache, orgRelationship.getSourceId(), orgRelationship.getId());
                        }

                        if(orgRelationship.getObjective5Id()!=null && OrgConstants.ExternalAccessType.BeAccessed.name().equals(orgRelationship.getObjective5Id())){
                            OrgRelationshipId2BeAccessedCache.put(orgRelationship.getId(), rs);
                        }

                        if(orgRelationship.getObjective5Id()!=null && OrgConstants.ExternalAccessType.VjoinAccess.name().equals(orgRelationship.getObjective5Id())){
                            add2Cache(OrgRelationshipId2VjoinAccessCache, orgRelationship.getSourceId(), orgRelationship.getId());
                        }
                    }

                }
            }

        }

        OrgModifyState.set(new Date());

        OrgExportFlag.set(Boolean.FALSE);

        watch.stop();
        log.info(CacheFactory.isSkipFillData()?"Geode缓存":"数据库" + "加载组织模型完成，耗时：" + watch.getTotalTimeMillis() + "毫秒");
    }

	private CacheMap generateCacheMap(CacheAccessable factory, String cacheName) {
    	return factory.createMap(cacheName);
	}

	/**
     * 重置组织模型单个实体的缓存，方便数据修复
     * 先删后加
     * 只处理实体数据和关系数据
     * @param <T>
     */
    @SuppressWarnings("unchecked")
	public <T> void reSetCache(String entityType, Long entityId){
/*    	V3xOrgEntity entity = this.getV3xOrgEntity(OrgHelper.getV3xClass(entityType), entityId);
    	//删除缓存数据
    	this.cacheRemove(entity);

    	//从数据库中查到有效的实体缓存
		T entity2 = orgDao.getEntity((Class<T>)OrgHelper.getV3xClass(entityType), entityId);
    	this.cacheUpdate(orgEntity);*/
    }


    private void initSubDeptCache(){
        //创建单位/部门下的子部门缓存
        for (V3xOrgUnit orgUnit : OrgUnitCache.values()) {
        	if(orgUnit.isValid() && !orgUnit.isGroup()){
        		SubDeptCache.removeAll(Arrays.asList(new String[]{orgUnit.getId().toString(),orgUnit.getId()+"_"+SUBDEPT_INNER_FIRST,orgUnit.getId()+"_"+SUBDEPT_INNER_ALL,orgUnit.getId()+"_"+SUBDEPT_OUTER_ALL,orgUnit.getId()+"_"+SUBDEPT_OUTER_FIRST}));
            	if(!SubDeptCache.contains(String.valueOf(orgUnit.getId()))){
            		SubDeptCache.put(String.valueOf(orgUnit.getId()), new UniqueList<Long>());
            		SubDeptCache.put(orgUnit.getId()+"_"+SUBDEPT_INNER_FIRST, new UniqueList<Long>());
            		SubDeptCache.put(orgUnit.getId()+"_"+SUBDEPT_INNER_ALL, new UniqueList<Long>());
            		SubDeptCache.put(orgUnit.getId()+"_"+SUBDEPT_OUTER_ALL, new UniqueList<Long>());
            		SubDeptCache.put(orgUnit.getId()+"_"+SUBDEPT_OUTER_FIRST, new UniqueList<Long>());
            	}
        	}
        }

        for(String parentDeptId : SubDeptCache.keySet()){
        	if(parentDeptId.indexOf("_")>0) continue;
        	List<Long> subIdList = SubDeptCache.get(parentDeptId);
        	List<Long> subIdList1 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_INNER_FIRST);
        	List<Long> subIdList2 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_INNER_ALL);
        	List<Long> subIdList3 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_OUTER_ALL);
        	List<Long> subIdList4 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_OUTER_FIRST);
        	V3xOrgUnit parentUnit = OrgUnitCache.get(Long.valueOf(parentDeptId));
        	if(null!=parentUnit){
        		for (V3xOrgUnit orgUnit : OrgUnitCache.values()) {
        			if(OrgConstants.UnitType.Department.name().equals(orgUnit.getType().name()) && orgUnit.isValid()){
        				if(orgUnit.getParentPath().startsWith(parentUnit.getPath()) && !orgUnit.getPath().equals(parentUnit.getPath()) && orgUnit.getOrgAccountId().equals(parentUnit.getOrgAccountId())){
        					//所有子部门
        					subIdList.add(orgUnit.getId());
        					if(orgUnit.getIsInternal()){
        						//内部所有子部门
        						subIdList2.add(orgUnit.getId());
        						if(orgUnit.getParentPath().equals(parentUnit.getPath())){
        							//内部一级子部门
        							subIdList1.add(orgUnit.getId());
        						}
        					}else{
        						//外部子部门
        						subIdList3.add(orgUnit.getId());
        						if(orgUnit.getParentPath().equals(parentUnit.getPath())){
        							//外部一级子部门
        							subIdList4.add(orgUnit.getId());
        						}
        					}
        				}
        			}
        		}
        	}
        }
    }

    private void updateSubDeptCache(V3xOrgUnit oldOrgUnit,V3xOrgUnit newOrgUnit){
    	if(null == oldOrgUnit && null!=newOrgUnit ){
    		//新建--部门 时 添加缓存(添加完后其实没有子部门，只是完善结构，)
    		//1.添加它自己的子部门
        	ArrayList<Long> addSubIdList = new UniqueList<Long>();
        	ArrayList<Long> addSubIdList1 = new UniqueList<Long>();
        	ArrayList<Long> addSubIdList2 = new UniqueList<Long>();
        	ArrayList<Long> addSubIdList3 = new UniqueList<Long>();
        	ArrayList<Long> addSubIdList4 = new UniqueList<Long>();

    		for (V3xOrgUnit orgUnit : OrgUnitCache.values()) {
    			if(OrgConstants.UnitType.Department.name().equals(orgUnit.getType().name()) && orgUnit.isValid()){
    				if(orgUnit.getParentPath().startsWith(newOrgUnit.getPath()) && !orgUnit.getPath().equals(newOrgUnit.getPath()) && orgUnit.getOrgAccountId().equals(newOrgUnit.getOrgAccountId())){
    					//所有子部门
    					addSubIdList.add(orgUnit.getId());
    					if(orgUnit.getIsInternal()){
    						//内部所有子部门
    						addSubIdList2.add(orgUnit.getId());
    						if(orgUnit.getParentPath().equals(newOrgUnit.getPath())){
    							//内部一级子部门
    							addSubIdList1.add(orgUnit.getId());
    						}
    					}else{
    						//外部子部门
    						addSubIdList3.add(orgUnit.getId());
    						if(orgUnit.getParentPath().equals(newOrgUnit.getPath())){
    							//外部一级子部门
    							addSubIdList4.add(orgUnit.getId());
    						}
    					}
    				}
    			}
    		}

    		SubDeptCache.put(String.valueOf(newOrgUnit.getId()), (UniqueList<Long>)addSubIdList);
    		SubDeptCache.put(newOrgUnit.getId()+"_"+SUBDEPT_INNER_FIRST, (UniqueList<Long>)addSubIdList1);
    		SubDeptCache.put(newOrgUnit.getId()+"_"+SUBDEPT_INNER_ALL, (UniqueList<Long>)addSubIdList2);
    		SubDeptCache.put(newOrgUnit.getId()+"_"+SUBDEPT_OUTER_ALL, (UniqueList<Long>)addSubIdList3);
    		SubDeptCache.put(newOrgUnit.getId()+"_"+SUBDEPT_OUTER_FIRST, (UniqueList<Long>)addSubIdList4);
    		notifySubDept(String.valueOf(newOrgUnit.getId()),addSubIdList);
			notifySubDept(newOrgUnit.getId()+"_"+SUBDEPT_INNER_FIRST,addSubIdList1);
			notifySubDept(newOrgUnit.getId()+"_"+SUBDEPT_INNER_ALL,addSubIdList2);
			notifySubDept(newOrgUnit.getId()+"_"+SUBDEPT_OUTER_ALL,addSubIdList3);
			notifySubDept(newOrgUnit.getId()+"_"+SUBDEPT_OUTER_FIRST,addSubIdList4);

    		//2.在其他单位、部门下添加该部门
    		if(OrgConstants.UnitType.Department.name().equals(newOrgUnit.getType().name()) && newOrgUnit.isValid()){
    	        for(String parentDeptId : SubDeptCache.keySet()){
    	        	if(parentDeptId.indexOf("_")>0) continue;
    	        	V3xOrgUnit parentUnit = OrgUnitCache.get(Long.valueOf(parentDeptId));
    	        	if(null!=parentUnit){
    	        		if(newOrgUnit.getParentPath().startsWith(parentUnit.getPath()) && !newOrgUnit.getPath().equals(parentUnit.getPath()) && newOrgUnit.getOrgAccountId().equals(parentUnit.getOrgAccountId())){
    	        			ArrayList<Long> subIdList = SubDeptCache.get(parentDeptId);
    	        			ArrayList<Long> subIdList1 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_INNER_FIRST);
    	        			ArrayList<Long> subIdList2 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_INNER_ALL);
    	        			ArrayList<Long> subIdList3 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_OUTER_ALL);
    	        			ArrayList<Long> subIdList4 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_OUTER_FIRST);

    						//所有子部门
    						subIdList.add(newOrgUnit.getId());
    						if(newOrgUnit.getIsInternal()){
    							//内部所有子部门
    							subIdList2.add(newOrgUnit.getId());
    							if(newOrgUnit.getParentPath().equals(parentUnit.getPath())){
    								//内部一级子部门
    								subIdList1.add(newOrgUnit.getId());
    							}
    						}else{
    							//外部子部门
    							subIdList3.add(newOrgUnit.getId());
    							if(newOrgUnit.getParentPath().equals(parentUnit.getPath())){
    								//外部一级子部门
    								subIdList4.add(newOrgUnit.getId());
    							}
    						}

    						notifySubDept(parentDeptId,subIdList);
							notifySubDept(parentDeptId+"_"+SUBDEPT_INNER_FIRST,subIdList1);
							notifySubDept(parentDeptId+"_"+SUBDEPT_INNER_ALL,subIdList2);
							notifySubDept(parentDeptId+"_"+SUBDEPT_OUTER_ALL,subIdList3);
							notifySubDept(parentDeptId+"_"+SUBDEPT_OUTER_FIRST,subIdList4);
    	        		}
    	        	}
    	        }
    		}

    	}else if(oldOrgUnit.isValid() && !newOrgUnit.isValid()){
    		//停用、删除--部门或者单位  时 删除缓存
    		if(oldOrgUnit.isValid() && !newOrgUnit.isValid()){
    			//1.删除key是自己的缓存
    			SubDeptCache.removeAll(Arrays.asList(new String[]{oldOrgUnit.getId().toString(),oldOrgUnit.getId()+"_"+SUBDEPT_INNER_FIRST,oldOrgUnit.getId()+"_"+SUBDEPT_INNER_ALL,oldOrgUnit.getId()+"_"+SUBDEPT_OUTER_ALL,oldOrgUnit.getId()+"_"+SUBDEPT_OUTER_FIRST}));

    	        //2.删除其他子部门中包含自己的缓存
        		if(OrgConstants.UnitType.Department.name().equals(newOrgUnit.getType().name())){
        			List<V3xOrgDepartment> childDepts=getChildDeptsByPath(oldOrgUnit,true);
        	        Set<Long> deptids = new HashSet<Long>();
        	        deptids.add(oldOrgUnit.getId());
        	        if(null!=childDepts){
        	        	for(V3xOrgUnit dept : childDepts){
        	        		deptids.add(dept.getId());
        	        	}
        	        }
        	        for(String parentDeptId : SubDeptCache.keySet()){
        	        	if(parentDeptId.indexOf("_")>0) continue;
        	        	V3xOrgUnit parentUnit = OrgUnitCache.get(Long.valueOf(parentDeptId));
        	        	if(null!=parentUnit){
        	        		if(oldOrgUnit.getParentPath().startsWith(parentUnit.getPath()) && !oldOrgUnit.getPath().equals(parentUnit.getPath()) && oldOrgUnit.getOrgAccountId().equals(parentUnit.getOrgAccountId())){
        	        			ArrayList<Long> subIdList = SubDeptCache.get(parentDeptId);
        	        			ArrayList<Long> subIdList1 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_INNER_FIRST);
        	        			ArrayList<Long> subIdList2 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_INNER_ALL);
        	        			ArrayList<Long> subIdList3 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_OUTER_ALL);
        	        			ArrayList<Long> subIdList4 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_OUTER_FIRST);
        	        			removeDept(subIdList,deptids);
        	        			removeDept(subIdList1,deptids);
        	        			removeDept(subIdList2,deptids);
        	        			removeDept(subIdList3,deptids);
        	        			removeDept(subIdList4,deptids);

        						notifySubDept(parentDeptId,subIdList);
    							notifySubDept(parentDeptId+"_"+SUBDEPT_INNER_FIRST,subIdList1);
    							notifySubDept(parentDeptId+"_"+SUBDEPT_INNER_ALL,subIdList2);
    							notifySubDept(parentDeptId+"_"+SUBDEPT_OUTER_ALL,subIdList3);
    							notifySubDept(parentDeptId+"_"+SUBDEPT_OUTER_FIRST,subIdList4);
        	        		}
        	        	}
        	        }
        		}
    		}
    	}else if(!oldOrgUnit.getSuperior().equals(newOrgUnit.getSuperior())){//父节点变了，才更新缓存
    		//修改了（只能是）部门父节点  时 ，更新缓存
    		//1.删除key是自己的缓存
    		//SubDeptCache.removeAll(Arrays.asList(new String[]{oldOrgUnit.getId().toString(),oldOrgUnit.getId()+"_"+SUBDEPT_INNER_FIRST,oldOrgUnit.getId()+"_"+SUBDEPT_INNER_ALL,oldOrgUnit.getId()+"_"+SUBDEPT_OUTER_ALL}));
    		//2.删除其他节点中有自己和自己的子部门的缓存
    		if(OrgConstants.UnitType.Department.name().equals(oldOrgUnit.getType().name())){
    	    	List<V3xOrgDepartment> childDepts=getChildDeptsByPath(oldOrgUnit,true);
    	        Set<Long> deptids = new HashSet<Long>();
    	        deptids.add(oldOrgUnit.getId());
    	        if(null!=childDepts){
    	        	for(V3xOrgUnit dept : childDepts){
    	        		deptids.add(dept.getId());
    	        	}
    	        }
    	        for(String parentDeptId : SubDeptCache.keySet()){
    	        	if(parentDeptId.indexOf("_")>0) continue;
    	        	V3xOrgUnit parentUnit = OrgUnitCache.get(Long.valueOf(parentDeptId));
    	        	if(null!=parentUnit){
    	        		if(oldOrgUnit.getParentPath().startsWith(parentUnit.getPath()) && !oldOrgUnit.getPath().equals(parentUnit.getPath()) && oldOrgUnit.getOrgAccountId().equals(parentUnit.getOrgAccountId())){
    	        			ArrayList<Long> subIdList = SubDeptCache.get(parentDeptId);
    	        			ArrayList<Long> subIdList1 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_INNER_FIRST);
    	        			ArrayList<Long> subIdList2 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_INNER_ALL);
    	        			ArrayList<Long> subIdList3 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_OUTER_ALL);
    	        			ArrayList<Long> subIdList4 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_OUTER_FIRST);
    	        			removeDept(subIdList,deptids);
    	        			removeDept(subIdList1,deptids);
    	        			removeDept(subIdList2,deptids);
    	        			removeDept(subIdList3,deptids);
    	        			removeDept(subIdList4,deptids);

    						notifySubDept(parentDeptId,subIdList);
							notifySubDept(parentDeptId+"_"+SUBDEPT_INNER_FIRST,subIdList1);
							notifySubDept(parentDeptId+"_"+SUBDEPT_INNER_ALL,subIdList2);
							notifySubDept(parentDeptId+"_"+SUBDEPT_OUTER_ALL,subIdList3);
							notifySubDept(parentDeptId+"_"+SUBDEPT_OUTER_FIRST,subIdList4);
    	        		}
    	        	}
    	        }
    		}

    		//3.添加自己的子部门缓存
/*        	List<Long> addSubIdList = new ArrayList<Long>();
        	List<Long> addSubIdList1 = new ArrayList<Long>();
        	List<Long> addSubIdList2 = new ArrayList<Long>();
        	List<Long> addSubIdList3 = new ArrayList<Long>();

    		for (V3xOrgUnit orgUnit : OrgUnitCache.values()) {
    			if(OrgConstants.UnitType.Department.name().equals(orgUnit.getType().name()) && orgUnit.isValid()){
    				if(orgUnit.getParentPath().startsWith(newOrgUnit.getPath()) && !orgUnit.getPath().equals(newOrgUnit.getPath())){
    					//所有子部门
    					addSubIdList.add(orgUnit.getId());
    					if(orgUnit.getIsInternal()){
    						//内部所有子部门
    						addSubIdList2.add(orgUnit.getId());
    						if(orgUnit.getParentPath().equals(newOrgUnit.getPath())){
    							//内部一级子部门
    							addSubIdList1.add(orgUnit.getId());
    						}
    					}else{
    						//外部子部门
    						addSubIdList3.add(orgUnit.getId());
    					}
    				}
    			}
    		}

    		SubDeptCache.put(String.valueOf(newOrgUnit.getId()), (ArrayList)addSubIdList);
    		SubDeptCache.put(newOrgUnit.getId()+"_"+SUBDEPT_INNER_FIRST, (ArrayList)addSubIdList1);
    		SubDeptCache.put(newOrgUnit.getId()+"_"+SUBDEPT_INNER_ALL, (ArrayList)addSubIdList2);
    		SubDeptCache.put(newOrgUnit.getId()+"_"+SUBDEPT_OUTER_ALL, (ArrayList)addSubIdList3);*/

    		//4.在他的新单位、部门下添加该部门和该部门的子部门
    		if(OrgConstants.UnitType.Department.name().equals(newOrgUnit.getType().name()) && newOrgUnit.isValid()){
    			List<V3xOrgDepartment> childDepts=getChildDeptsByPath(oldOrgUnit,false);
    	    	childDepts.add((V3xOrgDepartment)newOrgUnit);
    	        for(String parentDeptId : SubDeptCache.keySet()){
    	        	if(parentDeptId.indexOf("_")>0) continue;
    	        	V3xOrgUnit parentUnit = OrgUnitCache.get(Long.valueOf(parentDeptId));
    	        	if(null!=parentUnit){
    	        		if(newOrgUnit.getParentPath().startsWith(parentUnit.getPath()) && !newOrgUnit.getPath().equals(parentUnit.getPath()) && newOrgUnit.getOrgAccountId().equals(parentUnit.getOrgAccountId())){
    	        			ArrayList<Long> subIdList = SubDeptCache.get(parentDeptId);
    	        			ArrayList<Long> subIdList1 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_INNER_FIRST);
    	        			ArrayList<Long> subIdList2 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_INNER_ALL);
    	        			ArrayList<Long> subIdList3 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_OUTER_ALL);
    	        			ArrayList<Long> subIdList4 = SubDeptCache.get(parentDeptId+"_"+SUBDEPT_OUTER_FIRST);
    	        			addDept(parentUnit,subIdList,subIdList1,subIdList2,subIdList3,subIdList4,childDepts);
    						notifySubDept(parentDeptId,subIdList);
							notifySubDept(parentDeptId+"_"+SUBDEPT_INNER_FIRST,subIdList1);
							notifySubDept(parentDeptId+"_"+SUBDEPT_INNER_ALL,subIdList2);
							notifySubDept(parentDeptId+"_"+SUBDEPT_OUTER_ALL,subIdList3);
							notifySubDept(parentDeptId+"_"+SUBDEPT_OUTER_FIRST,subIdList4);
    	        		}
    	        	}
    	        }
    		}

    	}

    }

    private void removeDept(List<Long> subIdList,Set<Long> childDeptIds){
		Iterator <Long> it = subIdList.iterator();
		while(it.hasNext())
		{
		    //if(it.next().equals(deptId))
			if(childDeptIds.contains(it.next()))
		    {
		        it.remove();
		    }
		}
    }

    private void addDept(V3xOrgUnit parentUnit, List<Long> subIdList, List<Long> subIdList1, List<Long> subIdList2, List<Long> subIdList3,List<Long> subIdList4,List<V3xOrgDepartment> childDepts){
    	for(V3xOrgUnit dept : childDepts){
			//所有子部门
			subIdList.add(dept.getId());
			if(dept.getIsInternal()){
				//内部所有子部门
				subIdList2.add(dept.getId());
				if(dept.getParentPath().equals(parentUnit.getPath())){
					//内部一级子部门
					subIdList1.add(dept.getId());
				}
			}else{
				//外部子部门
				subIdList3.add(dept.getId());
				if(dept.getParentPath().equals(parentUnit.getPath())){
					//外部一级子部门
					subIdList4.add(dept.getId());
				}
			}
    	}
    }

    private List<V3xOrgDepartment> getChildDeptsByPath(V3xOrgUnit parentUnit,boolean includeDisable) {
    	String parentPath = parentUnit.getPath();
        List<V3xOrgDepartment> result = new UniqueList<V3xOrgDepartment>();
        List<V3xOrgUnit> allUnits = getAllUnits();
        for (V3xOrgUnit o : allUnits) {
            if (o.getPath().startsWith(parentPath)
                    && !o.getPath().equals(parentPath)
                    && OrgConstants.UnitType.Department.name().equals(o.getType().name())
                    && o.getOrgAccountId().equals(parentUnit.getOrgAccountId())) {
            	if(o.isValid() || (!o.isValid() && includeDisable)){
            		result.add((V3xOrgDepartment)o);
            	}
            }
        }
        return result;
    }

    private void notifySubDept(String key,ArrayList<Long> pos){
		Map<String, ArrayList<Long>> _SubDeptCache = new HashMap<String, ArrayList<Long>>();
		if(pos == null){
			pos = new ArrayList<Long>();
		}
		_SubDeptCache.put(key, pos);
		SubDeptCache.putAll(_SubDeptCache);
		Set updateKey = mergeCacheMap(SubDeptCache, _SubDeptCache);
		SubDeptCache.notifyUpdate(updateKey);
   }


    @SuppressWarnings("unchecked")
    private <T extends V3xOrgEntity> CacheMap<Long, T> getCacheMap(Class<T> classType){
        if(classType.equals(V3xOrgUnit.class) || classType.equals(V3xOrgAccount.class) || classType.equals(V3xOrgDepartment.class)){
            return (CacheMap<Long, T>) this.OrgUnitCache;
        }
        if(classType.equals(V3xOrgPost.class)){
            return (CacheMap<Long, T>) this.OrgPostCache;
        }
        if(classType.equals(V3xOrgMember.class)){
            return (CacheMap<Long, T>) this.OrgMemberCache;
        }
        if(classType.equals(V3xOrgVisitor.class)){
            return (CacheMap<Long, T>) this.OrgVisitorCache;
        }
        if(classType.equals(V3xOrgLevel.class)){
            return (CacheMap<Long, T>) this.OrgLevelCache;
        }
        if(classType.equals(V3xOrgTeam.class)){
            return (CacheMap<Long, T>) this.OrgTeamCache;
        }
        if(classType.equals(V3xOrgRole.class)){
            return (CacheMap<Long, T>) this.OrgRoleCache;
        }

        return null;
    }

    public List<V3xOrgAccount> getAllAccounts() {
    	return getAllAccounts(OrgConstants.ExternalType.Inner.ordinal());
    }

    @Override
    public List<V3xOrgAccount> getAllAccounts(Integer externalType) {
        List<V3xOrgAccount> result = new ArrayList<V3xOrgAccount>(100);
        CacheMap<Long, V3xOrgUnit> cache = getCacheMap(V3xOrgUnit.class);
        List<V3xOrgUnit> list = new ArrayList<V3xOrgUnit>(cache.values());
        for (V3xOrgUnit o : list) {
            if(OrgConstants.UnitType.Account.name().equals(o.getType().name())){
            	if(externalType == null || o.getExternalType().equals(externalType)){
            		result.add(OrgHelper.cloneEntity((V3xOrgAccount)o));
            	}
            }
        }

        return result;
    }

    public List<V3xOrgAccount> getChildAccount(Long accountId) {
        V3xOrgAccount currentAccount = getV3xOrgEntity(V3xOrgAccount.class, accountId);
        //增加空防护，获取某个停用单位的子单位时，从缓存中获取不到
        if(null == currentAccount) {
            currentAccount = (V3xOrgAccount) OrgHelper.poTobo(orgDao.getEntity(OrgUnit.class, accountId));
        }
        List<V3xOrgAccount> result = new ArrayList<V3xOrgAccount>(100);

        List<V3xOrgAccount> allAccounts = getAllAccounts();
        for (V3xOrgAccount o : allAccounts) {
            if(o.getPath().startsWith(currentAccount.getPath())&&!o.getPath().equals(currentAccount.getPath())){
                result.add(OrgHelper.cloneEntity(o));
            }
        }

        return result;
    }

    public List<V3xOrgDepartment> getChildDeptByAccountId(Long accountId) {
        V3xOrgAccount currentAccount = getV3xOrgEntity(V3xOrgAccount.class, accountId);
        //增加空防护，获取某个停用单位的子单位时，从缓存中获取不到
        if(null == currentAccount) {
            currentAccount = (V3xOrgAccount) OrgHelper.poTobo(orgDao.getEntity(OrgUnit.class, accountId));
        }
        List<V3xOrgDepartment> result = new ArrayList<V3xOrgDepartment>();
        List<V3xOrgDepartment> allDepts = getAllV3xOrgDepartment(accountId);
        for (V3xOrgDepartment o : allDepts) {
            if (o.getPath().startsWith(currentAccount.getPath())
                    && !o.getPath().equals(currentAccount.getPath())) {
                result.add(OrgHelper.cloneEntity(o));
            }
        }
        return result;
    }

    @Override
    public List<V3xOrgDepartment> getAllV3xOrgDepartment(Long accountId){
    	return getAllV3xOrgDepartment(accountId,OrgConstants.ExternalType.Inner.ordinal());
    }
    @Override
    public List<V3xOrgDepartment> getAllV3xOrgDepartment(Long accountId, Integer externalType){
        List<V3xOrgDepartment> result = new ArrayList<V3xOrgDepartment>(100);
        CacheMap<Long, V3xOrgUnit> cache = getCacheMap(V3xOrgUnit.class);
        List<V3xOrgUnit> list = new ArrayList<V3xOrgUnit>(cache.values());
        for (V3xOrgUnit o : list) {
            if(OrgConstants.UnitType.Department.equals(o.getType()) && o.getOrgAccountId().equals(accountId)){
                result.add(OrgHelper.cloneEntity((V3xOrgDepartment)o));
            }
        }

        return result;
    }

    /**
     * 仅供内部使用
     * @param accountId
     * @return
     */
    private List<V3xOrgDepartment> getAllV3xOrgDepartmentNoClone(Long accountId) {
        return getAllV3xOrgDepartmentNoClone(accountId, OrgConstants.ExternalType.Inner.ordinal());
    }

    private List<V3xOrgDepartment> getAllV3xOrgDepartmentNoClone(Long accountId, Integer externalType) {
        List<V3xOrgDepartment> result = new ArrayList<V3xOrgDepartment>(100);
        CacheMap<Long, V3xOrgUnit> cache = getCacheMap(V3xOrgUnit.class);
        List<V3xOrgUnit> list = new ArrayList<V3xOrgUnit>(cache.values());
        for (V3xOrgUnit o : list) {
            if (OrgConstants.UnitType.Department.equals(o.getType()) && o.getOrgAccountId().equals(accountId)) {
                if (externalType == null || o.getExternalType().equals(externalType)) {
                    result.add((V3xOrgDepartment) o);
                }
            }
        }

        return result;
    }

    private List<V3xOrgAccount> getAllV3xOrgAccount(){
    	return getAllV3xOrgAccount(OrgConstants.ExternalType.Inner.ordinal());
    }
    private List<V3xOrgAccount> getAllV3xOrgAccount(Integer externalType){
        List<V3xOrgAccount> result = new ArrayList<V3xOrgAccount>(100);
        CacheMap<Long, V3xOrgUnit> cache = getCacheMap(V3xOrgUnit.class);
        List<V3xOrgUnit> list = new ArrayList<V3xOrgUnit>(cache.values());
        for (V3xOrgUnit o : list) {
            if(OrgConstants.UnitType.Account.equals(o.getType())){
                result.add(OrgHelper.cloneEntity((V3xOrgAccount)o));
            }
        }
        return result;
    }

    private List<V3xOrgAccount> getAllV3xOrgAccountNoClone(){
        return getAllV3xOrgAccountNoClone(OrgConstants.ExternalType.Inner.ordinal());
    }

    private List<V3xOrgAccount> getAllV3xOrgAccountNoClone(Integer externalType){
        List<V3xOrgAccount> result = new ArrayList<V3xOrgAccount>(100);
        CacheMap<Long, V3xOrgUnit> cache = getCacheMap(V3xOrgUnit.class);
        List<V3xOrgUnit> list = new ArrayList<V3xOrgUnit>(cache.values());
        for (V3xOrgUnit o : list) {
            if(OrgConstants.UnitType.Account.equals(o.getType()) && o.getExternalType().equals(externalType)){
                result.add((V3xOrgAccount)o);
            }
        }
        return result;
    }

    @Override
    public <T extends V3xOrgEntity> List<T> getAllV3xOrgEntity(Class<T> classType, Long accountId) {
    	return getAllV3xOrgEntity(classType,accountId,OrgConstants.ExternalType.Inner.ordinal());
    }
    @Override
    public <T extends V3xOrgEntity> List<T> getAllV3xOrgEntity(Class<T> classType, Long accountId,Integer externalType) {
        if(classType.equals(V3xOrgDepartment.class)){
            return (List<T>)getAllV3xOrgDepartment(accountId,externalType);
        } else if(classType.equals(V3xOrgAccount.class)) {
            return (List<T>)getAllV3xOrgAccount(externalType);
        }

        CacheMap<Long, T> cache = getCacheMap(classType);

        List<T> list = new ArrayList<T>(cache.values());
        if(externalType!=null && (classType.equals(V3xOrgPost.class) || classType.equals(V3xOrgRole.class) || classType.equals(V3xOrgMember.class))){
    		List<T> result = new ArrayList<T>(cache.size());
    		for (T o : list) {
    			if(o.getExternalType().equals(externalType) &&(accountId == null || accountId.equals(o.getOrgAccountId()))){
    				result.add(OrgHelper.cloneEntityImmutableDecorator(o));
    			}
    		}
    		return result;
        }else{
    		List<T> result = new ArrayList<T>(100);
    		for (T o : list) {
    			if(accountId == null || o.getOrgAccountId().equals(accountId)){
    				result.add(OrgHelper.cloneEntityImmutableDecorator(o));
    			}
    		}

        	return result;
        }
    }

    @Override
    public <T extends V3xOrgEntity> List<T> getAllV3xOrgEntityNoClone(Class<T> classType, Long accountId) {
    	return getAllV3xOrgEntityNoClone(classType,accountId,OrgConstants.ExternalType.Inner.ordinal());
    }

    @Override
    public <T extends V3xOrgEntity> List<T> getAllV3xOrgEntityNoClone(Class<T> classType, Long accountId, Integer externalType) {
        if (classType.equals(V3xOrgDepartment.class)) {
            return (List<T>) getAllV3xOrgDepartmentNoClone(accountId, externalType);
        } else if (classType.equals(V3xOrgAccount.class)) {
            return (List<T>) getAllV3xOrgAccountNoClone(externalType);
        }

        CacheMap<Long, T> cache = getCacheMap(classType);

        List<T> list = new ArrayList<T>(cache.values());
        if(externalType!=null && (classType.equals(V3xOrgPost.class) || classType.equals(V3xOrgRole.class) || classType.equals(V3xOrgMember.class))){
    		 if(accountId == null){
    			List<T> result = new ArrayList<T>();
         		for (T o : list) {
        			if(o.getExternalType().equals(externalType)){
        				result.add(o);
        			}
        		}
         		return result;
    		 }else{
    			List<T> result = new ArrayList<T>(100);
         		for (T o : list) {
        			if(accountId.equals(o.getOrgAccountId()) && externalType.equals(o.getExternalType())){
        				result.add(o);
        			}
        		}
         		return result;
    		 }
        }else{
        	if(accountId == null){
        		return list;
        	}
        	else{
        		List<T> result = new ArrayList<T>(100);
        		for (T o : list) {
        			if(o.getOrgAccountId().equals(accountId)){
        				result.add(o);
        			}
        		}

        		return result;
        	}
        }
    }

    public <T extends V3xOrgEntity> T getV3xOrgEntity(Class<T> classType, Long id) {
        return getV3xOrgEntity(classType, id, true);
    }

    public <T extends V3xOrgEntity> T getV3xOrgEntityNoClone(Class<T> classType, Long id){
        return getV3xOrgEntity(classType, id, false);
    }

    public <T extends V3xOrgEntity> T getV3xOrgEntity(Class<T> classType, Long id, boolean clone) {
        if(classType == null || id == null){
            return null;
        }

        CacheMap<Long, T> cache = getCacheMap(classType);
        T object = cache.get(id);

        return clone ? OrgHelper.cloneEntity(object) : object;
    }
    
    @Override
    public V3xOrgVisitor getV3xOrgVisitor(Long id, boolean clone) {
        V3xOrgVisitor visitor = OrgVisitorCache.get(id);

        return clone ? OrgHelper.cloneEntity(visitor) : visitor;
    }

    public V3xOrgUnit getV3xOrgUnitByPath(String path){
        Collection<V3xOrgUnit> v3xOrgUnits = OrgUnitCache.values();
        for (V3xOrgUnit u : v3xOrgUnits) {
            if(path.equals(u.getPath())){
                return OrgHelper.cloneEntity(u);
            }
        }

        //上级有可能是停用的，从停用单位/部门缓存中查
        Collection<V3xOrgUnit> disableV3xOrgUnits = OrgUnitDisableCache.values();
        for (V3xOrgUnit u : disableV3xOrgUnits) {
            if(path.equals(u.getPath())){
                return OrgHelper.cloneEntity(u);
            }
        }

        return null;
    }

    @Override
    public V3xOrgRelationship getV3xOrgRelationshipById(Long id) {
    	return getV3xOrgRelationshipByTypeAndId(null, id);
    }

    @Override
    public V3xOrgRelationship getV3xOrgRelationshipByTypeAndId(String type,Long id) {
        for(String key : relationshipMap.keySet()){
        	if(type == null || (Strings.isNotBlank(type) && key.equals(type))){
        		CacheMap<Long, V3xOrgRelationship>  orgRelationshipId2POCacheForType = relationshipMap.get(key);
        		if(orgRelationshipId2POCacheForType.get(id) != null){
        			return orgRelationshipId2POCacheForType.get(id);
        		}
        	}
        }
        return null;
    }

    @Override
    public List<Long> getAccessMemberOrgRelationshipIds(Long memberId) {
    	List<Long> relationShipIds = new ArrayList<Long>();
    	if(OrgRelationshipId2AccessCache.get(memberId)!=null){
    		relationShipIds = OrgRelationshipId2AccessCache.get(memberId);
    	}
    	return relationShipIds;
    }

    @Override
    public Set<Long> getAllBeAccessOrgRelationshipIds() {
    	Set<Long> relationShipIds = new HashSet<Long>();
    	if(!OrgRelationshipId2BeAccessedCache.isEmpty()){
    		relationShipIds = OrgRelationshipId2BeAccessedCache.keySet();
    	}
    	return relationShipIds;
    }

    @Override
    public List<Long> getVjoinAccessEntityOrgRelationshipIds(Long entityId) {
    	List<Long> relationShipIds = new ArrayList<Long>();
    	if(OrgRelationshipId2VjoinAccessCache.get(entityId)!=null){
    		relationShipIds = OrgRelationshipId2VjoinAccessCache.get(entityId);
    	}
    	return relationShipIds;
    }

    @Override
    public List<V3xOrgAccount> getSameLengthPathUnits(String path) {
    	return getSameLengthPathUnits(path,null);
    }
    @Override
    public List<V3xOrgAccount> getSameLengthPathUnits(String path,Integer externalType) {
        List<V3xOrgAccount> result = new UniqueList<V3xOrgAccount>();
        Collection<V3xOrgUnit> v3xOrgUnits = OrgUnitCache.values();
        String parentpath = path.substring(0, path.length() - 4);
        for (V3xOrgUnit u : v3xOrgUnits) {
            if(OrgConstants.UnitType.Account.equals(u.getType())
                    && u.isValid()) {
                if (path.length() == u.getPath().length()
                        && parentpath.equals(u.getPath().substring(0, path.length() - 4))) {//上级单位相同
                	if(externalType == null || u.getExternalType().equals(externalType)){
                		result.add((V3xOrgAccount) OrgHelper.cloneEntity(u));
                	}
                    continue;
                }
            }
        }
        return result;
    }

    @Override
    public List<V3xOrgAccount> getShorterLengthPathUnits(String path) {
        List<V3xOrgAccount> result = new UniqueList<V3xOrgAccount>();
        Collection<V3xOrgUnit> v3xOrgUnits = OrgUnitCache.values();
        for (V3xOrgUnit u : v3xOrgUnits) {
            if(OrgConstants.UnitType.Account.equals(u.getType())
                    && u.isValid()) {
                if (path.length() > u.getPath().length()) {
                    result.add((V3xOrgAccount) OrgHelper.cloneEntity(u));
                    continue;
                }
            }
        }
        return result;
    }

    public List<V3xOrgRelationship> getRoleEntityRelastionships(Long roleId, Long unitId, Long accountId){

        List<V3xOrgRelationship> result = new UniqueList<V3xOrgRelationship>();
        List<Long> rsIds = this.OrgRoleEntityRSCache.get(roleId);
        //?? 不应该有空
        if(rsIds == null){
            return result;
        }
        for (Long rsId : rsIds) {
            V3xOrgRelationship rs = relationshipMap.get(OrgConstants.RelationshipType.Member_Role.name()).get(rsId);
            if(rs == null){
                continue;
            }
            if(unitId != null && !Strings.equals(rs.getObjective0Id(), unitId)){
                continue;
            }
            if(accountId != null && !Strings.equals(rs.getOrgAccountId(), accountId)){
                continue;
            }
            result.add(rs);
        }
        return result;
    }

    public List<V3xOrgRelationship> getEntityRoleRelastionships(List<Long> entityIds, Long unitId, Long accountId){

        List<V3xOrgRelationship> result = new UniqueList<V3xOrgRelationship>();
        for (Long entityId : entityIds) {
            List<Long> rsIds = this.OrgEntityRoleRSCache.get(entityId);
            //?? 不应该有空
            if(rsIds == null){
                continue;
            }
            for (Long rsId : rsIds) {
                V3xOrgRelationship rs = relationshipMap.get(OrgConstants.RelationshipType.Member_Role.name()).get(rsId);
                if(rs == null){
                    continue;
                }
                if(unitId != null && !Strings.equals(rs.getObjective0Id(), unitId)){
                    continue;
                }
                if(accountId != null && !Strings.equals(rs.getOrgAccountId(), accountId)){
                    continue;
                }
                result.add(rs);
            }
        }
        return result;
    }

    public List<V3xOrgRelationship> getMemberPostRelastionships(Long memberId, Long accountId, OrgConstants.MemberPostType... types){
        List<V3xOrgRelationship> result = new UniqueList<V3xOrgRelationship>();

        List<Long> rsIds = this.OrgMemberPostRSCache.get(memberId);
        //?? 不应该有空
        if(rsIds == null){
            return result;
        }
        Set<String> ts = new HashSet<String>();
        if(types != null && types.length > 0){
            for (MemberPostType t : types) {
                ts.add(t.name());
            }
        }
        for (Long rsId : rsIds) {
            V3xOrgRelationship rs = relationshipMap.get(OrgConstants.RelationshipType.Member_Post.name()).get(rsId);
            if(rs == null){
                continue;
            }
            if(accountId != null && !Strings.equals(rs.getOrgAccountId(), accountId)){
                continue;
            }
            if((types != null && types.length != 0) && !ts.contains(rs.getObjective5Id())){
                continue;
            }
            result.add(rs);
        }
        return result;
    }

    public List<V3xOrgRelationship> getDepartmentRelastionships(Long departmentId, OrgConstants.MemberPostType... types){
        List<V3xOrgRelationship> result = new UniqueList<V3xOrgRelationship>();

        List<Long> rsIds = this.OrgDepartmentRSCache.get(departmentId);
        //?? 不应该有空
        if(rsIds == null){
            return result;
        }
        Set<String> ts = new HashSet<String>();
        if(types != null && types.length > 0){
            for (MemberPostType t : types) {
                ts.add(t.name());
            }
        }
        for (Long rsId : rsIds) {
            V3xOrgRelationship rs = relationshipMap.get(OrgConstants.RelationshipType.Member_Post.name()).get(rsId);
            if(rs == null){
                continue;
            }
            if((types != null && types.length != 0) && !ts.contains(rs.getObjective5Id())){
                continue;
            }
            result.add(rs);
        }
        return result;
    }

    public List<V3xOrgRelationship> getDepartmentRelastionships(List<Long> departmentIds, OrgConstants.MemberPostType... types){
    	List<V3xOrgRelationship> result = new UniqueList<V3xOrgRelationship>();
    	for(Long deptId : departmentIds){
    		List<V3xOrgRelationship> rList= getDepartmentRelastionships(deptId,types);
    		result.addAll(rList);
    	}
    	return result;
    }

    public List<V3xOrgRelationship> getMemberTeamRelastionships(Long memberId, List<Long> accountIds, OrgConstants.TeamMemberType... types){
        List<V3xOrgRelationship> result = new UniqueList<V3xOrgRelationship>();

        List<Long> rsIds = this.OrgMemberTeamRSCache.get(memberId);
        //?? 不应该有空
        if(rsIds == null){
            return result;
        }
        Set<String> ts = new HashSet<String>();
        if(types != null && types.length > 0){
            for (OrgConstants.TeamMemberType t : types) {
                ts.add(t.name());
            }
        }
        for (Long rsId : rsIds) {
            V3xOrgRelationship rs = relationshipMap.get(OrgConstants.RelationshipType.Team_Member.name()).get(rsId);
            if(rs == null){
                continue;
            }
            if(accountIds != null && !accountIds.contains(rs.getOrgAccountId())){
                continue;
            }
            if((types != null && types.length != 0) && !ts.contains(rs.getObjective5Id())){
                continue;
            }
            result.add(rs);
        }
        return result;
    }

    public List<V3xOrgRelationship> getTeamMemberRelastionships(Long teamId, OrgConstants.TeamMemberType... types){

        List<Long> rsIds = this.OrgTeamMemberRSCache.get(teamId);
        //?? 不应该有空
        if(rsIds == null){
            return Collections.EMPTY_LIST;
        }
        List<V3xOrgRelationship> result = new UniqueList<V3xOrgRelationship>(rsIds.size());
        Set<String> ts = new HashSet<String>();
        if(types != null && types.length > 0){
            for (OrgConstants.TeamMemberType t : types) {
                ts.add(t.name());
            }
        }
        for (Long rsId : rsIds) {
            V3xOrgRelationship rs = relationshipMap.get(OrgConstants.RelationshipType.Team_Member.name()).get(rsId);
            if(rs == null){
                continue;
            }
            if((types != null && types.length != 0) && !ts.contains(rs.getObjective5Id())){
                continue;
            }
            result.add(rs);
        }
        return result;
    }

    public List<V3xOrgRelationship> getEntityConPostRelastionships(Long accountId, Long departmenId, OrgConstants.MemberPostType... types){

        List<V3xOrgRelationship> result = new ArrayList<V3xOrgRelationship>();
        List<Long> rsIds = null;
        if(accountId == null){
            //预估每个单位有40个兼职
            rsIds = new ArrayList<Long>(OrgEntityConPostRSCache.values().size()*40);
            for (Iterator<ArrayList<Long>> iterator = OrgEntityConPostRSCache.values().iterator(); iterator.hasNext();) {
                ArrayList<Long> e = iterator.next();
                rsIds.addAll(e);
            }
        }else{
            rsIds = this.OrgEntityConPostRSCache.get(accountId);
        }

        //?? 不应该有空
        if(rsIds == null){
            return result;
        }
        Set<String> ts = new HashSet<String>();
        if(types != null && types.length > 0){
            for (MemberPostType t : types) {
                ts.add(t.name());
            }
        }
        for (Long rsId : rsIds) {
            V3xOrgRelationship rs = relationshipMap.get(OrgConstants.RelationshipType.Member_Post.name()).get(rsId);
            if(rs == null){
                continue;
            }
            if(departmenId != null && !Strings.equals(rs.getObjective0Id(), departmenId)){
                continue;
            }
            if((types != null && types.length != 0) && !ts.contains(rs.getObjective5Id())){
                continue;
            }
            result.add(rs);
        }
        return result;
    }

    public List<V3xOrgRelationship> getV3xOrgRelationship(OrgConstants.RelationshipType type){
        List<V3xOrgRelationship> result = new ArrayList<V3xOrgRelationship>();
        CacheMap<Long, V3xOrgRelationship> orgRelationshipId2POCacheForType = relationshipMap.get(type.name());
        if(orgRelationshipId2POCacheForType.size() == 0){
            return result;
        }

        for (Long v3xOrgRSId : orgRelationshipId2POCacheForType.keySet()) {
            V3xOrgRelationship rs = orgRelationshipId2POCacheForType.get(v3xOrgRSId);
            if(rs != null){
                result.add(rs);
            }
        }

        return result;
    }


    public List<V3xOrgRelationship> getV3xOrgRelationship(OrgConstants.RelationshipType type, Long sourceId, Long accountId, EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds) {
        FoolishSet<Long> sourceIds1 = null;
        if(sourceId != null){
            sourceIds1 = new FoolishSet<Long>(sourceId);
        }

        FoolishSet<Long> accountIds1 = null;
        if(accountId != null){
            accountIds1 = new FoolishSet<Long>(accountId);
        }

        return this.getV3xOrgRelationship(type, sourceIds1, accountIds1, escapeObjectiveIds(objectiveIds));
    }

    public List<V3xOrgRelationship> getV3xOrgRelationship(OrgConstants.RelationshipType type, List<Long> sourceIds, List<Long> accountIds, EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds){
        FoolishSet<Long> sourceIds1 = null;
        if(!Strings.isEmpty(sourceIds)){
            sourceIds1 = new FoolishSet<Long>(sourceIds);
        }

        FoolishSet<Long> accountIds1 = null;
        if(!Strings.isEmpty(accountIds)){
            accountIds1 = new FoolishSet<Long>(accountIds);
        }

        return this.getV3xOrgRelationship(type, sourceIds1, accountIds1, escapeObjectiveIds(objectiveIds));
    }

    private static <T extends Object> EnumMap<RelationshipObjectiveName, FoolishSet<Object>> escapeObjectiveIds(EnumMap<RelationshipObjectiveName, Object> objectiveIds){
        if(objectiveIds == null){
            return null;
        }

        EnumMap<RelationshipObjectiveName, FoolishSet<Object>> result = new EnumMap<RelationshipObjectiveName, FoolishSet<Object>>(RelationshipObjectiveName.class);

        for(Iterator<Map.Entry<RelationshipObjectiveName, Object>> iterator = objectiveIds.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<RelationshipObjectiveName, ?> e = iterator.next();
            Object values = e.getValue();
            if(values != null){
                FoolishSet<Object> values1 = null;
                if(values instanceof Collection<?>){
                    values1 = new FoolishSet<Object>((Collection<?>)values);
                }
                else{
                    values1 = new FoolishSet<Object>(values);
                }

                result.put(e.getKey(), values1);
            }
        }

        return result;
    }

    private List<V3xOrgRelationship> getV3xOrgRelationship(OrgConstants.RelationshipType type, FoolishSet<Long> sourceIds, FoolishSet<Long> accountIds, EnumMap<OrgConstants.RelationshipObjectiveName, FoolishSet<Object>> objectiveIds){
        List<V3xOrgRelationship> result = new UniqueList<V3xOrgRelationship>(100);

        CacheMap<Long, V3xOrgRelationship> orgRelationshipId2POCacheForType = relationshipMap.get(type.name());
        if(orgRelationshipId2POCacheForType.size() == 0){
            return result;
        }

        FoolishSet<Object> objective0Id = null, objective1Id = null, objective2Id = null, objective3Id = null,objective4Id = null, objective5Id = null, objective6Id = null, objective7Id = null;
        if(objectiveIds != null){
            objective0Id = objectiveIds.get(OrgConstants.RelationshipObjectiveName.objective0Id);
            objective1Id = objectiveIds.get(OrgConstants.RelationshipObjectiveName.objective1Id);
            objective2Id = objectiveIds.get(OrgConstants.RelationshipObjectiveName.objective2Id);
            objective3Id = objectiveIds.get(OrgConstants.RelationshipObjectiveName.objective3Id);
            objective4Id = objectiveIds.get(OrgConstants.RelationshipObjectiveName.objective4Id);
            objective5Id = objectiveIds.get(OrgConstants.RelationshipObjectiveName.objective5Id);
            objective6Id = objectiveIds.get(OrgConstants.RelationshipObjectiveName.objective6Id);
            objective7Id = objectiveIds.get(OrgConstants.RelationshipObjectiveName.objective7Id);
        }

        if(isEmpty(sourceIds) && isEmpty(accountIds)
                && isEmpty(objective0Id) && isEmpty(objective1Id) && isEmpty(objective2Id) && isEmpty(objective3Id)
                && isEmpty(objective4Id) && isEmpty(objective5Id) && isEmpty(objective6Id) && isEmpty(objective7Id) ){
            return result;
        }

        // slow();//缓存访问监控

        for (Long v3xOrgRSId : orgRelationshipId2POCacheForType.keySet()) {
            V3xOrgRelationship rs = orgRelationshipId2POCacheForType.get(v3xOrgRSId);
            if(rs == null){
                continue;
            }

            if(sourceIds != null && !sourceIds.contains(rs.getSourceId())){
                continue;
            }
            if(accountIds != null && !accountIds.contains(rs.getOrgAccountId())){
                continue;
            }

            if(objective0Id != null && !objective0Id.contains(rs.getObjective0Id())){
                continue;
            }
            if(objective1Id != null && !objective1Id.contains(rs.getObjective1Id())){
                continue;
            }
            if(objective2Id != null && !objective2Id.contains(rs.getObjective2Id())){
                continue;
            }
            if(objective3Id != null && !objective3Id.contains(rs.getObjective3Id())){
                continue;
            }
            if(objective4Id != null && !objective4Id.contains(rs.getObjective4Id())){
                continue;
            }
            if(objective5Id != null && !objective5Id.contains(rs.getObjective5Id())){
                continue;
            }
            if(objective6Id != null && !objective6Id.contains(rs.getObjective6Id())){
                continue;
            }
            if(objective7Id != null && !objective7Id.contains(rs.getObjective7Id())){
                continue;
            }

            result.add(rs);
        }

        return result;
    }

    public V3xOrgAccount getGroupAccount(Long accountId){
        V3xOrgUnit account = this.OrgUnitCache.get(accountId);
        String path = account.getPath();

        CacheMap<Long, V3xOrgUnit> cache = getCacheMap(V3xOrgUnit.class);
        List<V3xOrgUnit> list = new ArrayList<V3xOrgUnit>(cache.values());
        for (V3xOrgUnit o : list) {
            if(o.isGroup() && OrgConstants.UnitType.Account.equals(o.getType())){
                while(path.length() >= 4){
                    path = path.substring(0, path.length() - 4);
                    if(o.getPath().equals(path)){
                        return OrgHelper.cloneEntity((V3xOrgAccount)o);
                    }
                }
            }
        }

        return null;
    }

    public Date getModifiedTimeStamp(String entType, Long accountId){
        return OrgModifyState.get();
    }

    @Override
    public void updateModifiedTimeStamp(){
        this.OrgModifyState.set(new Date());
    }

    public boolean isModified(String entType, Date date, Long accountId){
        return !OrgModifyState.get().equals(date);
    }


    /*******************  以下是缓存更新的代码  ******************/

    public <T extends V3xOrgEntity> void cacheUpdate(T orgEntity) {
        if(null == orgEntity) return ;
        Class<?> c = OrgHelper.getEntityType(orgEntity);

        V3xOrgUnit oldOrgUnit = null;
        if(((Class<T>)c).equals(V3xOrgDepartment.class) || ((Class<T>)c).equals(V3xOrgAccount.class)){
        	oldOrgUnit = (V3xOrgUnit)OrgUnitCache.get(orgEntity.getId());
            //停用的
            if(!orgEntity.getEnabled() && !orgEntity.getIsDeleted()){
            	OrgUnitDisableCache.put(orgEntity.getId(), (V3xOrgUnit)orgEntity);
            }

            if(orgEntity.getIsDeleted()){
            	OrgUnitDisableCache.remove(orgEntity.getId());
            }
        }

        V3xOrgMember oldMember = null;
        if(((Class<T>)c).equals(V3xOrgMember.class)){
            oldMember = (V3xOrgMember)OrgMemberCache.get(orgEntity.getId());
        }
        CacheMap<Long, T> cache = getCacheMap((Class<T>)c);
        if(!orgEntity.isValid()) {
        	if(((Class<T>)c).equals(V3xOrgVisitor.class)){
        		cache.put(orgEntity.getId(), orgEntity);
        	}else {
        		cache.remove(orgEntity.getId());
        		cacheDisabledEntity(orgEntity);
        	}

            //人员停用或者删除后，从单位下的人员缓存中删掉
            if(((Class<T>)c).equals(V3xOrgMember.class)){
                Set<Long> updateKey = new HashSet<Long>();
                ArrayList<Long> list = OrgAccountMemberCache.get(orgEntity.getOrgAccountId());
                if(Strings.isNotEmpty(list)){
                    list.remove(orgEntity.getId());
                    updateKey.add(orgEntity.getOrgAccountId());
                    OrgAccountMemberCache.notifyUpdate(updateKey);
                }

                if(oldMember != null && Strings.isNotBlank(oldMember.getTelNumber())){
                	Set<String> updateKey2 = new HashSet<String>();
                	ArrayList<Long> list2 = OrgTelnumMemberCache.get(oldMember.getTelNumber());
                	if(Strings.isNotEmpty(list2)){
                		list2.remove(orgEntity.getId());
                		updateKey2.add(oldMember.getTelNumber());
                		OrgTelnumMemberCache.notifyUpdate(updateKey2);
                	}
                }
            }

            if(((Class<T>)c).equals(V3xOrgLevel.class)){
                Set<Long> updateKey = new HashSet<Long>();
                ArrayList<Long> list = OrgAccountLevelCache.get(orgEntity.getOrgAccountId());
                if(Strings.isNotEmpty(list)){
                    list.remove(orgEntity.getId());
                    updateKey.add(orgEntity.getOrgAccountId());
                    OrgAccountLevelCache.notifyUpdate(updateKey);
                }
            }

            if(((Class<T>)c).equals(V3xOrgRole.class)){
                Set<Long> updateKey = new HashSet<Long>();
                ArrayList<Long> list = OrgAccountRoleCache.get(orgEntity.getOrgAccountId());
                if(Strings.isNotEmpty(list)){
                    list.remove(orgEntity.getId());
                    updateKey.add(orgEntity.getOrgAccountId());
                    OrgAccountRoleCache.notifyUpdate(updateKey);
                }
            }

        } else {
        	cache.put(orgEntity.getId(), orgEntity);
        	if(!((Class<T>)c).equals(V3xOrgVisitor.class)){
        		cacheDisabledRemove(orgEntity);
        	}
        	
            if(((Class<T>)c).equals(V3xOrgMember.class)){
                Set<Long> updateKey = new HashSet<Long>();

                //如果人员调到其他单位了，删除原单位下人员的缓存数据
                if(oldMember != null && !oldMember.getOrgAccountId().equals(orgEntity.getOrgAccountId())){
                    ArrayList<Long> list = OrgAccountMemberCache.get(oldMember.getOrgAccountId());
                    if(Strings.isNotEmpty(list)){
                        list.remove(orgEntity.getId());
                        updateKey.add(oldMember.getOrgAccountId());
                        OrgAccountMemberCache.notifyUpdate(updateKey);
                    }
                }

                //如果人员在单位下已经存在，不对OrgAccountMemberCache缓存做处理，不管修改了什么数据，也不会影响这个人已经在这个单位下这一事实。
                //没有才加
                if(OrgAccountMemberCache.get(orgEntity.getOrgAccountId()) == null || !OrgAccountMemberCache.get(orgEntity.getOrgAccountId()).contains(orgEntity.getId())){
                    if(orgEntity != null && ((V3xOrgMember)orgEntity).isValid() && ((V3xOrgMember)orgEntity).getIsInternal() && !((V3xOrgMember)orgEntity).getIsAdmin() ){
                        Map<Long, ArrayList<Long>> _AccountMemberCache = new HashMap<Long, ArrayList<Long>>();
                        Strings.addToMap1(_AccountMemberCache, orgEntity.getOrgAccountId(), orgEntity.getId());
                        updateKey = mergeCacheMap(OrgAccountMemberCache, _AccountMemberCache);
                        OrgAccountMemberCache.notifyUpdate(updateKey);
                    }
                }

                //1.修改前后手机号码一致，不做调整
                //2.修改前有手机，修改后没手机，直接删除该手机下缓存里的此人
                //3.修改前没手机，修改后有手机，直接把这个人加到该手机下的缓存里
                //4。修改前后都有手机，且不一样，删除之前缓存下的人，在新缓存中加入此人（即2，3步）
                String oldTelNum = oldMember == null ? "" :(oldMember.getTelNumber() == null ? "" : oldMember.getTelNumber());
                String newTelNum = ((V3xOrgMember)orgEntity).getTelNumber() == null ? "" : ((V3xOrgMember)orgEntity).getTelNumber();
                if( (Strings.isBlank(oldTelNum) && Strings.isBlank(newTelNum)) || oldTelNum.equals(newTelNum)){
                }else{
                	if( (Strings.isNotBlank(oldTelNum) && Strings.isBlank(newTelNum)) || (Strings.isNotBlank(oldTelNum) && Strings.isNotBlank(newTelNum) && !oldTelNum.equals(newTelNum))){
                		Set<String> updateKey2 = new HashSet<String>();
                		ArrayList<Long> list2 = OrgTelnumMemberCache.get(oldMember.getTelNumber());
                		if(Strings.isNotEmpty(list2)){
                			list2.remove(orgEntity.getId());
                			updateKey2.add(oldMember.getTelNumber());
                			OrgTelnumMemberCache.notifyUpdate(updateKey2);
                		}
                	}

                	if( (Strings.isBlank(oldTelNum) && Strings.isNotBlank(newTelNum)) ||  (Strings.isNotBlank(oldTelNum) && Strings.isNotBlank(newTelNum) && !oldTelNum.equals(newTelNum))){
                		Set<String> updateKey2 = new HashSet<String>();
                		Map<String, ArrayList<Long>> _OrgTelnumMemberCache = new HashMap<String, ArrayList<Long>>();
                		Strings.addToMap1(_OrgTelnumMemberCache, newTelNum, orgEntity.getId());
                		updateKey2 = mergeCacheMap(OrgTelnumMemberCache, _OrgTelnumMemberCache);
                		OrgTelnumMemberCache.notifyUpdate(updateKey2);
                	}
                }

            }

            if(((Class<T>)c).equals(V3xOrgLevel.class)){
                Set<Long> updateKey = new HashSet<Long>();
                //没有才加
                if(OrgAccountLevelCache.get(orgEntity.getOrgAccountId()) == null || !OrgAccountLevelCache.get(orgEntity.getOrgAccountId()).contains(orgEntity.getId())){
                    if(orgEntity != null && ((V3xOrgLevel)orgEntity).isValid()){
                        Map<Long, ArrayList<Long>> _AccountLevelCache = new HashMap<Long, ArrayList<Long>>();
                        Strings.addToMap1(_AccountLevelCache, orgEntity.getOrgAccountId(), orgEntity.getId());
                        updateKey = mergeCacheMap(OrgAccountLevelCache, _AccountLevelCache);
                        OrgAccountLevelCache.notifyUpdate(updateKey);
                    }
                }
            }

            if(((Class<T>)c).equals(V3xOrgRole.class)){
                Set<Long> updateKey = new HashSet<Long>();
                //没有才加
                if(OrgAccountRoleCache.get(orgEntity.getOrgAccountId()) == null || !OrgAccountRoleCache.get(orgEntity.getOrgAccountId()).contains(orgEntity.getId())){
                    if(orgEntity != null && ((V3xOrgRole)orgEntity).isValid()){
                        Map<Long, ArrayList<Long>> _AccountRoleCache = new HashMap<Long, ArrayList<Long>>();
                        Strings.addToMap1(_AccountRoleCache, orgEntity.getOrgAccountId(), orgEntity.getId());
                        updateKey = mergeCacheMap(OrgAccountRoleCache, _AccountRoleCache);
                        OrgAccountRoleCache.notifyUpdate(updateKey);
                    }
                }
            }

        }

        if(((Class<T>)c).equals(V3xOrgDepartment.class) || ((Class<T>)c).equals(V3xOrgAccount.class)){
        	updateSubDeptCache(oldOrgUnit,(V3xOrgUnit)orgEntity);
        }

        if(OrgHelper.isUpdateModifyTimestampOfCurrentThread()){
            this.OrgModifyState.set(new Date());
        }

    }

    public <T extends V3xOrgEntity> void cacheUpdate(List<T> orgEntities) {
        if(Strings.isEmpty(orgEntities)) return;
        Map<Long, T> orgEntityMap = new HashMap<Long, T>();
        Class<?> c = OrgHelper.getEntityType(orgEntities.get(0));

        Map<V3xOrgUnit,V3xOrgUnit> map = new HashMap<V3xOrgUnit,V3xOrgUnit>();
        if(((Class<T>)c).equals(V3xOrgDepartment.class) || ((Class<T>)c).equals(V3xOrgAccount.class)){
        for (T t : orgEntities) {
        		V3xOrgUnit oldOrgUnit = (V3xOrgUnit)OrgUnitCache.get(t.getId());
        		map.put(oldOrgUnit, (V3xOrgUnit)t);

                //停用的
                if(!t.getEnabled() && !t.getIsDeleted()){
                	OrgUnitDisableCache.put(t.getId(), (V3xOrgUnit)t);
                }

                if(t.getIsDeleted()){
                	OrgUnitDisableCache.remove(t.getId());
                }
        	}
        }
        CacheMap<Long, T> cache = getCacheMap((Class<T>)c);
        
        List<Long> disabledList = new ArrayList<Long>();
        for (T t : orgEntities) {
            V3xOrgMember oldMember = null;
            if(((Class<T>)c).equals(V3xOrgMember.class)){
                oldMember = (V3xOrgMember)OrgMemberCache.get(t.getId());
            }
            if(!t.isValid()) {
            	if(((Class<T>)c).equals(V3xOrgVisitor.class)){
            		cache.put(t.getId(), t);
            	}else {
            		cacheDisabledEntity(t);
            		disabledList.add(t.getId());
            	}

                //人员停用或者删除后，从单位下的人员缓存中删掉
                if(((Class<T>)c).equals(V3xOrgMember.class)){
                    Set<Long> updateKey = new HashSet<Long>();
                    ArrayList<Long> list = OrgAccountMemberCache.get(t.getOrgAccountId());
                    if(Strings.isNotEmpty(list)){
                        list.remove(t.getId());
                        updateKey.add(t.getOrgAccountId());
                        OrgAccountMemberCache.notifyUpdate(updateKey);
                    }

                    if(oldMember != null && Strings.isNotBlank(oldMember.getTelNumber())){
                    	Set<String> updateKey2 = new HashSet<String>();
                    	ArrayList<Long> list2 = OrgTelnumMemberCache.get(oldMember.getTelNumber());
                    	if(Strings.isNotEmpty(list2)){
                    		list2.remove(t.getId());
                    		updateKey2.add(oldMember.getTelNumber());
                    		OrgTelnumMemberCache.notifyUpdate(updateKey2);
                    	}
                    }
                }

                if(((Class<T>)c).equals(V3xOrgLevel.class)){
                    Set<Long> updateKey = new HashSet<Long>();
                    ArrayList<Long> list = OrgAccountLevelCache.get(t.getOrgAccountId());
                    if(Strings.isNotEmpty(list)){
                        list.remove(t.getId());
                        updateKey.add(t.getOrgAccountId());
                        OrgAccountLevelCache.notifyUpdate(updateKey);
                    }
                }

                if(((Class<T>)c).equals(V3xOrgRole.class)){
                    Set<Long> updateKey = new HashSet<Long>();
                    ArrayList<Long> list = OrgAccountRoleCache.get(t.getOrgAccountId());
                    if(Strings.isNotEmpty(list)){
                        list.remove(t.getId());
                        updateKey.add(t.getOrgAccountId());
                        OrgAccountRoleCache.notifyUpdate(updateKey);
                    }
                }

            } else {
            	orgEntityMap.put(t.getId(), t);
            	if(!((Class<T>)c).equals(V3xOrgVisitor.class)){
            		cacheDisabledRemove(t);
            	}

                if(((Class<T>)c).equals(V3xOrgMember.class)){
                    Set<Long> updateKey = new HashSet<Long>();

                    //如果人员调到其他单位了，删除原单位下人员的缓存数据
                    if(oldMember != null && !oldMember.getOrgAccountId().equals(t.getOrgAccountId())){
                        ArrayList<Long> list = OrgAccountMemberCache.get(oldMember.getOrgAccountId());
                        if(Strings.isNotEmpty(list)){
                            list.remove(t.getId());
                            updateKey.add(oldMember.getOrgAccountId());
                            OrgAccountMemberCache.notifyUpdate(updateKey);
                        }
                    }

                    //如果人员在单位下已经存在，不对OrgAccountMemberCache缓存做处理，不管修改了什么数据，也不会影响这个人已经在这个单位下这一事实。
                    //没有才加
                    if(OrgAccountMemberCache.get(t.getOrgAccountId()) == null || !OrgAccountMemberCache.get(t.getOrgAccountId()).contains(t.getId())){
                        if(t != null && ((V3xOrgMember)t).isValid() && ((V3xOrgMember)t).getIsInternal() && !((V3xOrgMember)t).getIsAdmin() ){
                            Map<Long, ArrayList<Long>> _AccountMemberCache = new HashMap<Long, ArrayList<Long>>();
                            Strings.addToMap1(_AccountMemberCache, t.getOrgAccountId(), t.getId());
                            updateKey = mergeCacheMap(OrgAccountMemberCache, _AccountMemberCache);
                            OrgAccountMemberCache.notifyUpdate(updateKey);
                        }
                    }

                    //1.修改前后手机号码一致，不做调整
                    //2.修改前有手机，修改后没手机，直接删除该手机下缓存里的此人
                    //3.修改前没手机，修改后有手机，直接把这个人加到该手机下的缓存里
                    //4。修改前后都有手机，且不一样，删除之前缓存下的人，在新缓存中加入此人（即2，3步）
                    String oldTelNum = oldMember == null ? "" :(oldMember.getTelNumber() == null ? "" : oldMember.getTelNumber());
                    String newTelNum = ((V3xOrgMember)t).getTelNumber() == null ? "" : ((V3xOrgMember)t).getTelNumber();
                    if( (Strings.isBlank(oldTelNum) && Strings.isBlank(newTelNum)) || oldTelNum.equals(newTelNum)){
                    }else{
                    	if( (Strings.isNotBlank(oldTelNum) && Strings.isBlank(newTelNum)) || (Strings.isNotBlank(oldTelNum) && Strings.isNotBlank(newTelNum) && !oldTelNum.equals(newTelNum))){
                    		Set<String> updateKey2 = new HashSet<String>();
                    		ArrayList<Long> list2 = OrgTelnumMemberCache.get(oldMember.getTelNumber());
                    		if(Strings.isNotEmpty(list2)){
                    			list2.remove(t.getId());
                    			updateKey2.add(oldMember.getTelNumber());
                    			OrgTelnumMemberCache.notifyUpdate(updateKey2);
                    		}
                    	}

                    	if( (Strings.isBlank(oldTelNum) && Strings.isNotBlank(newTelNum)) ||  (Strings.isNotBlank(oldTelNum) && Strings.isNotBlank(newTelNum) && !oldTelNum.equals(newTelNum))){
                    		Set<String> updateKey2 = new HashSet<String>();
                    		Map<String, ArrayList<Long>> _OrgTelnumMemberCache = new HashMap<String, ArrayList<Long>>();
                    		Strings.addToMap1(_OrgTelnumMemberCache, newTelNum, t.getId());
                    		updateKey2 = mergeCacheMap(OrgTelnumMemberCache, _OrgTelnumMemberCache);
                    		OrgTelnumMemberCache.notifyUpdate(updateKey2);
                    	}
                    }
                }

                if(((Class<T>)c).equals(V3xOrgLevel.class)){
                    Set<Long> updateKey = new HashSet<Long>();
                    //没有才加
                    if(OrgAccountLevelCache.get(t.getOrgAccountId()) == null || !OrgAccountLevelCache.get(t.getOrgAccountId()).contains(t.getId())){
                        if(t != null && ((V3xOrgLevel)t).isValid()){
                            Map<Long, ArrayList<Long>> _AccountLevelCache = new HashMap<Long, ArrayList<Long>>();
                            Strings.addToMap1(_AccountLevelCache, t.getOrgAccountId(), t.getId());
                            updateKey = mergeCacheMap(OrgAccountLevelCache, _AccountLevelCache);
                            OrgAccountLevelCache.notifyUpdate(updateKey);
                        }
                    }
                }

                if(((Class<T>)c).equals(V3xOrgRole.class)){
                    Set<Long> updateKey = new HashSet<Long>();
                    //没有才加
                    if(OrgAccountRoleCache.get(t.getOrgAccountId()) == null || !OrgAccountRoleCache.get(t.getOrgAccountId()).contains(t.getId())){
                        if(t != null && ((V3xOrgRole)t).isValid()){
                            Map<Long, ArrayList<Long>> _AccountRoleCache = new HashMap<Long, ArrayList<Long>>();
                            Strings.addToMap1(_AccountRoleCache, t.getOrgAccountId(), t.getId());
                            updateKey = mergeCacheMap(OrgAccountRoleCache, _AccountRoleCache);
                            OrgAccountRoleCache.notifyUpdate(updateKey);
                        }
                    }
                }

            }
        }


        cache.putAll(orgEntityMap);
        cache.removeAll(disabledList);

        //更新单位/部门-部门下的子部门缓存
        if((((Class<T>)c).equals(V3xOrgDepartment.class) || ((Class<T>)c).equals(V3xOrgAccount.class)) && map.size()>0){
        	for(V3xOrgUnit unit : map.keySet()){
        		updateSubDeptCache(unit,map.get(unit));
        	}
        }

        if(OrgHelper.isUpdateModifyTimestampOfCurrentThread()){
            this.OrgModifyState.set(new Date());
        }
    }

    public void cacheUpdateRelationship(List<OrgRelationship> rs) {
        if(rs == null || rs.isEmpty()){
            return;
        }

        Map<Long, ArrayList<Long>> _MemberPostCache = new HashMap<Long, ArrayList<Long>>();
        Map<Long, ArrayList<Long>> _DepartmentRsCache = new HashMap<Long, ArrayList<Long>>();
        Map<Long, ArrayList<Long>> _EntityRoleCache = new HashMap<Long, ArrayList<Long>>();
        Map<Long, ArrayList<Long>> _RoleEntityRSCache = new HashMap<Long, ArrayList<Long>>();
        Map<Long, ArrayList<Long>> _EntityConPostRSCache = new HashMap<Long, ArrayList<Long>>();
        Map<Long, ArrayList<Long>> _MemberTeamRSCache = new HashMap<Long, ArrayList<Long>>();
        Map<Long, ArrayList<Long>> _TeamMemberRSCache = new HashMap<Long, ArrayList<Long>>();
        Map<Long, ArrayList<Long>> _RelationshipId2AccessCache = new HashMap<Long, ArrayList<Long>>();
        Map<Long, ArrayList<Long>> _RelationshipId2VjoinAccessCache = new HashMap<Long, ArrayList<Long>>();
        Map<Long, V3xOrgRelationship> _OrgRelationshipId2BeAccessedCache = new HashMap<Long, V3xOrgRelationship>();
        Map<Long, ArrayList<Long>> _DepartmentPostCache = new HashMap<Long, ArrayList<Long>>();

        Map<String,Map<Long, V3xOrgRelationship>> relMap = new HashMap<String, Map<Long,V3xOrgRelationship>>();
        for (OrgRelationship orgRelationship : rs) {
        	String type = orgRelationship.getType();
        	Map<Long, V3xOrgRelationship> map = relMap.get(type);
        	if(map == null){
        		map = new HashMap<Long,V3xOrgRelationship>();
        		relMap.put(type, map);
        	}
            map.put(orgRelationship.getId(), new V3xOrgRelationship(orgRelationship));

            if(OrgConstants.RelationshipType.Member_Post.name().equals(orgRelationship.getType())){
                Strings.addToMap1(_MemberPostCache, orgRelationship.getSourceId(), orgRelationship.getId());
                if(isConPost(orgRelationship)){
                    Strings.addToMap1(_EntityConPostRSCache, orgRelationship.getOrgAccountId(), orgRelationship.getId());
                }
                if(null!=orgRelationship.getObjective0Id()){
                	Strings.addToMap1(_DepartmentRsCache, orgRelationship.getObjective0Id(), orgRelationship.getId());
                }
            }
            if(OrgConstants.RelationshipType.Department_Post.name().equals(orgRelationship.getType())){
                Strings.addToMap1(_DepartmentPostCache, orgRelationship.getSourceId(), orgRelationship.getId());
            }
            if(OrgConstants.RelationshipType.Member_Role.name().equals(orgRelationship.getType())){
                Strings.addToMap1(_EntityRoleCache, orgRelationship.getSourceId(), orgRelationship.getId());
                Strings.addToMap1(_RoleEntityRSCache, orgRelationship.getObjective1Id(), orgRelationship.getId());
            }
            if(OrgConstants.RelationshipType.Team_Member.name().equals(orgRelationship.getType())){
                Strings.addToMap1(_MemberTeamRSCache, orgRelationship.getObjective0Id(), orgRelationship.getId());
                Strings.addToMap1(_TeamMemberRSCache, orgRelationship.getSourceId(), orgRelationship.getId());
            }

            if(OrgConstants.RelationshipType.External_Access.name().equals(orgRelationship.getType())){
            	 if(OrgConstants.ExternalAccessType.Access.name().equals(orgRelationship.getObjective5Id())){
            		 Strings.addToMap1(_RelationshipId2AccessCache, orgRelationship.getSourceId(), orgRelationship.getId());
            	 }
            	 if(OrgConstants.ExternalAccessType.BeAccessed.name().equals(orgRelationship.getObjective5Id())){
            		 _OrgRelationshipId2BeAccessedCache.put(orgRelationship.getId(), new V3xOrgRelationship(orgRelationship));
            	 }
            	 if(OrgConstants.ExternalAccessType.VjoinAccess.name().equals(orgRelationship.getObjective5Id())){
            		 Strings.addToMap1(_RelationshipId2VjoinAccessCache, orgRelationship.getSourceId(), orgRelationship.getId());
            	 }
            }
        }

        for(String type : relMap.keySet()){
        	Map<Long, V3xOrgRelationship> map = relMap.get(type);
        	CacheMap<Long, V3xOrgRelationship> orgRelationshipId2POCacheForType = relationshipMap.get(type);
        	orgRelationshipId2POCacheForType.putAll(map);
        }
        OrgRelationshipId2BeAccessedCache.putAll(_OrgRelationshipId2BeAccessedCache);

        Set updateKey = mergeCacheMap(OrgMemberPostRSCache, _MemberPostCache);
        OrgMemberPostRSCache.notifyUpdate(updateKey);

        updateKey = mergeCacheMap(OrgEntityConPostRSCache, _EntityConPostRSCache);
        OrgEntityConPostRSCache.notifyUpdate(updateKey);

        updateKey = mergeCacheMap(OrgDepartmentRSCache, _DepartmentRsCache);
        OrgDepartmentRSCache.notifyUpdate(updateKey);

        updateKey = mergeCacheMap(OrgEntityRoleRSCache, _EntityRoleCache);
        OrgEntityRoleRSCache.notifyUpdate(updateKey);

        updateKey = mergeCacheMap(OrgRoleEntityRSCache, _RoleEntityRSCache);
        OrgRoleEntityRSCache.notifyUpdate(updateKey);

        updateKey = mergeCacheMap(OrgMemberTeamRSCache, _MemberTeamRSCache);
        OrgMemberTeamRSCache.notifyUpdate(updateKey);

        updateKey = mergeCacheMap(OrgTeamMemberRSCache, _TeamMemberRSCache);
        OrgTeamMemberRSCache.notifyUpdate(updateKey);

        updateKey = mergeCacheMap(OrgRelationshipId2AccessCache, _RelationshipId2AccessCache);
        OrgRelationshipId2AccessCache.notifyUpdate(updateKey);

        updateKey = mergeCacheMap(OrgDepartmentPostRSCache, _DepartmentPostCache);
        OrgDepartmentPostRSCache.notifyUpdate(updateKey);
        
        updateKey = mergeCacheMap(OrgRelationshipId2VjoinAccessCache, _RelationshipId2VjoinAccessCache);
        OrgRelationshipId2AccessCache.notifyUpdate(updateKey);

        if(OrgHelper.isUpdateModifyTimestampOfCurrentThread()){
            this.OrgModifyState.set(new Date());
        }
    }

    public void cacheUpdateRelationshipOnlySortId(List<Long> rsIds){
        CacheMap<Long, V3xOrgRelationship>  orgRelationshipId2POCacheForType = relationshipMap.get(OrgConstants.RelationshipType.Member_Post.name());
        orgRelationshipId2POCacheForType.notifyUpdate(rsIds);
    }

    @Override
    public void cacheUpdateV3xOrgEntityOnlySortId(List<Long> entityIds,OrgConstants.ORGENT_TYPE type){
        if (OrgConstants.ORGENT_TYPE.Level.name().equals(type.name())) {
            OrgLevelCache.notifyUpdate(entityIds);
        }else if(OrgConstants.ORGENT_TYPE.Post.name().equals(type.name())) {
        	OrgPostCache.notifyUpdate(entityIds);
        }else if(OrgConstants.ORGENT_TYPE.Unit.name().equals(type.name())) {
        	OrgUnitCache.notifyUpdate(entityIds);
        }else if(OrgConstants.ORGENT_TYPE.Team.name().equals(type.name())) {
        	OrgTeamCache.notifyUpdate(entityIds);
        }else if(OrgConstants.ORGENT_TYPE.Member.name().equals(type.name())) {
        	OrgMemberCache.notifyUpdate(entityIds);
        }else if(OrgConstants.ORGENT_TYPE.Role.name().equals(type.name())) {
        	OrgRoleCache.notifyUpdate(entityIds);
        }else if(OrgConstants.ORGENT_TYPE.Visitor.name().equals(type.name())) {
        	OrgVisitorCache.notifyUpdate(entityIds);
        }

    }


    public <T extends V3xOrgEntity> void cacheRemove(T orgEntity) {
        Class<?> c = OrgHelper.getEntityType(orgEntity);
        CacheMap<Long, T> cache = getCacheMap((Class<T>)c);
        cache.remove(orgEntity.getId());

        if(((Class<T>)c).equals(V3xOrgMember.class)){
            Set<Long> updateKey = new HashSet<Long>();
            ArrayList<Long> list = OrgAccountMemberCache.get(orgEntity.getOrgAccountId());
            if(Strings.isNotEmpty(list)){
                list.remove(orgEntity.getId());
                updateKey.add(orgEntity.getOrgAccountId());
                OrgAccountMemberCache.notifyUpdate(updateKey);
            }

            if(Strings.isNotBlank(((V3xOrgMember)orgEntity).getTelNumber())){
            	Set<String> updateKey2 = new HashSet<String>();
            	ArrayList<Long> list2 = OrgTelnumMemberCache.get(((V3xOrgMember)orgEntity).getTelNumber());
            	if(Strings.isNotEmpty(list2)){
            		list2.remove(orgEntity.getId());
            		updateKey2.add(((V3xOrgMember)orgEntity).getTelNumber());
            		OrgTelnumMemberCache.notifyUpdate(updateKey2);
            	}
            }

        }

        if(((Class<T>)c).equals(V3xOrgLevel.class)){
            Set<Long> updateKey = new HashSet<Long>();
            ArrayList<Long> list = OrgAccountLevelCache.get(orgEntity.getOrgAccountId());
            if(Strings.isNotEmpty(list)){
                list.remove(orgEntity.getId());
                updateKey.add(orgEntity.getOrgAccountId());
                OrgAccountLevelCache.notifyUpdate(updateKey);
            }
        }

        if(((Class<T>)c).equals(V3xOrgRole.class)){
            Set<Long> updateKey = new HashSet<Long>();
            ArrayList<Long> list = OrgAccountRoleCache.get(orgEntity.getOrgAccountId());
            if(Strings.isNotEmpty(list)){
                list.remove(orgEntity.getId());
                updateKey.add(orgEntity.getOrgAccountId());
                OrgAccountRoleCache.notifyUpdate(updateKey);
            }
        }

        //停用的
        if(orgEntity.getIsDeleted()){
        	OrgUnitDisableCache.remove(orgEntity.getId());
        }

        if(OrgHelper.isUpdateModifyTimestampOfCurrentThread()){
            this.OrgModifyState.set(new Date());
        }
    }

    public void cacheRemoveRelationship(List<OrgRelationship> rs) {
        if(rs == null || rs.isEmpty()){
            return;
        }

        Set<Long> updateKey2 = new HashSet<Long>();
        Set<Long> updateKey3 = new HashSet<Long>();
        Set<Long> updateKey4 = new HashSet<Long>();
        Set<Long> updateKey5 = new HashSet<Long>();
        Set<Long> updateKey6 = new HashSet<Long>();
        Set<Long> updateKey7 = new HashSet<Long>();
        Set<Long> updateKey8 = new HashSet<Long>();
        Set<Long> updateKey9 = new HashSet<Long>();
        Set<Long> updateKey10 = new HashSet<Long>();
        Set<Long> updateKey11 = new HashSet<Long>();

        for (OrgRelationship o : rs) {
        	CacheMap<Long, V3xOrgRelationship> orgRelationshipId2POCacheForType = relationshipMap.get(o.getType());
        	orgRelationshipId2POCacheForType.remove(o.getId());
            OrgRelationshipId2BeAccessedCache.remove(o.getId());

            if(Strings.equals(o.getType(),OrgConstants.RelationshipType.Member_Post.name())){
                ArrayList<Long> list2 = OrgMemberPostRSCache.get(o.getSourceId());
                list2.remove(o.getId());
                updateKey2.add(o.getSourceId());

                if(isConPost(o)){
                    ArrayList<Long> list5 = OrgEntityConPostRSCache.get(o.getOrgAccountId());
                    list5.remove(o.getId());
                    updateKey5.add(o.getOrgAccountId());
                }

                	ArrayList<Long> list8 = OrgDepartmentRSCache.get(o.getObjective0Id());
                	if(null!=o.getObjective0Id() && null!=list8){
	                	list8.remove(o.getId());
	                	updateKey8.add(o.getObjective0Id());
                }
            }

            if(Strings.equals(o.getType(),OrgConstants.RelationshipType.Department_Post.name())){
                ArrayList<Long> list11 = OrgDepartmentPostRSCache.get(o.getSourceId());
                list11.remove(o.getId());
                updateKey11.add(o.getSourceId());
            }
            
            if(Strings.equals(o.getType(),OrgConstants.RelationshipType.Member_Role.name())){

                ArrayList<Long> list3 = OrgEntityRoleRSCache.get(o.getSourceId());
                if(null!=o.getSourceId() && null!=list3){
                	list3.remove(o.getId());
                	updateKey3.add(o.getSourceId());
                }

                ArrayList<Long> list4 = OrgRoleEntityRSCache.get(o.getObjective1Id());
                if(null!=o.getObjective1Id() && null!=list4){
                	list4.remove(o.getId());
                	updateKey4.add(o.getObjective1Id());
                }
            }
            if(Strings.equals(o.getType(),OrgConstants.RelationshipType.Team_Member.name())){
                ArrayList<Long> list6 = OrgMemberTeamRSCache.get(o.getObjective0Id());
                if(null!=o.getObjective0Id() && null!=list6){
                	list6.remove(o.getId());
                	updateKey6.add(o.getObjective0Id());
                }

                ArrayList<Long> list7 = OrgTeamMemberRSCache.get(o.getSourceId());
                if(null!=o.getSourceId() && null!=list7){
                	list7.remove(o.getId());
                	updateKey7.add(o.getSourceId());
                }
            }

            if(Strings.equals(o.getType(),OrgConstants.RelationshipType.External_Access.name())){
            	if(OrgConstants.ExternalAccessType.Access.name().equals(o.getObjective5Id())){
            		ArrayList<Long> list9 = OrgRelationshipId2AccessCache.get(o.getSourceId());
            		list9.remove(o.getId());
            		updateKey9.add(o.getSourceId());
            	}

            	if(OrgConstants.ExternalAccessType.VjoinAccess.name().equals(o.getObjective5Id())){
            		ArrayList<Long> list10 = OrgRelationshipId2VjoinAccessCache.get(o.getSourceId());
            		list10.remove(o.getId());
            		updateKey10.add(o.getSourceId());
            	}
            }

        }

        OrgMemberPostRSCache.notifyUpdate(updateKey2);
        OrgEntityRoleRSCache.notifyUpdate(updateKey3);
        OrgRoleEntityRSCache.notifyUpdate(updateKey4);
        OrgEntityConPostRSCache.notifyUpdate(updateKey5);
        OrgMemberTeamRSCache.notifyUpdate(updateKey6);
        OrgTeamMemberRSCache.notifyUpdate(updateKey7);
        OrgDepartmentRSCache.notifyUpdate(updateKey8);
        OrgRelationshipId2AccessCache.notifyUpdate(updateKey9);
        OrgRelationshipId2VjoinAccessCache.notifyUpdate(updateKey10);
        OrgDepartmentPostRSCache.notifyUpdate(updateKey11);

        if(OrgHelper.isUpdateModifyTimestampOfCurrentThread()){
            this.OrgModifyState.set(new Date());
        }
    }

    @Override
    public <T extends V3xOrgUnit> List<V3xOrgUnit> getChildUnitsByPid(Class<T> classType, Long parentId) {
    	return getChildUnitsByPid(classType,parentId,null,false);
    }

    @Override
    public <T extends V3xOrgUnit> List<V3xOrgUnit> getChildUnitsByPid(Class<T> classType, Long parentId,Boolean disIncludeDisable, boolean firtLayer) {
    	 Object lock = new Object();
    	 synchronized(lock){
    		 List<V3xOrgUnit> result = new UniqueList<V3xOrgUnit>();
    		 V3xOrgUnit parentUnit = (V3xOrgUnit)this.getV3xOrgEntity(V3xOrgUnit.class, parentId);
    		 if(parentUnit == null){
    			 if(disIncludeDisable == null || disIncludeDisable == true){
    				 V3xOrgUnit disableUnit = OrgUnitDisableCache.get(parentId);
    				 if( disableUnit== null){
    					 return result;
    				 }else{
    					 parentUnit = disableUnit;
    				 }
    			 }else{
    				 return result;
    			 }
    		 }
    		 
    		 String path = parentUnit.getPath();
    		 //取有效的单位和部门
    		 for(Long  unitId : OrgUnitCache.keySet()){
    			 V3xOrgUnit subUnit = OrgUnitCache.get(unitId);
    			 if(subUnit == null || Strings.isBlank(subUnit.getPath())){
    				 continue;
    			 }
    			 String parentPath = subUnit.getParentPath();
    			 if (!firtLayer) {
    				 if(parentPath.startsWith(path)){
    					 result.add(OrgHelper.cloneEntity(subUnit));
    				 }
    			 } else {
    				 if(path.equals(parentPath)){
    					 result.add(OrgHelper.cloneEntity(subUnit));
    				 }
    			 }
    		 }
    		 
    		 //取停用的单位和部门
    		 if(disIncludeDisable == null || disIncludeDisable == true){
    			 for(Long  unitId : OrgUnitDisableCache.keySet()){
    				 V3xOrgUnit subUnit = OrgUnitDisableCache.get(unitId);
    				 if(subUnit == null || Strings.isBlank(subUnit.getPath())){
    					 continue;
    				 }
    				 String parentPath = subUnit.getParentPath();
    				 if (!firtLayer) {
    					 if(parentPath.startsWith(path)){
    						 result.add(OrgHelper.cloneEntity(subUnit));
    					 }
    				 } else {
    					 if(path.equals(parentPath)){
    						 result.add(OrgHelper.cloneEntity(subUnit));
    					 }
    				 }
    			 }
    		 }
    		 
    		 return result;
    	 }
    }
    @Override
    public <T extends V3xOrgUnit> List<V3xOrgUnit> getChildUnitsByPath(Class<T> classType, String parentPath) {
        List<V3xOrgUnit> result = new ArrayList<V3xOrgUnit>();
        List<V3xOrgUnit> allUnits = getAllUnits();
        for (V3xOrgUnit o : allUnits) {
            if (o.getPath().startsWith(parentPath)
                    && !o.getPath().equals(parentPath)) {
                result.add(o);
            }
        }
        return result;
    }

    @Override
    public List<V3xOrgUnit> getAllUnits() {
        List<V3xOrgUnit> result = new UniqueList<V3xOrgUnit>();
        CacheMap<Long, V3xOrgUnit> cache = getCacheMap(V3xOrgUnit.class);
        List<V3xOrgUnit> list = new ArrayList<V3xOrgUnit>(cache.values());
        for (V3xOrgUnit o : list) {
            if(OrgConstants.UnitType.Account.name().equals(o.getType().name())
                    || OrgConstants.UnitType.Department.name().equals(o.getType().name())){
                result.add(o);
            }
        }
        return result;
    }

    @Override
    public List<V3xOrgUnit> getDisableUnits(Long accountId,OrgConstants.UnitType ... types) {
    	List<V3xOrgUnit> result = new UniqueList<V3xOrgUnit>();
        Set<String> ts = new HashSet<String>();
        if(types != null && types.length > 0){
            for (OrgConstants.UnitType t : types) {
                ts.add(t.name());
            }
        }

    	for(Long unitId : OrgUnitDisableCache.keySet()){
    		V3xOrgUnit unit = OrgUnitDisableCache.get(unitId);
    		if(unit != null){
    			if(accountId != null && !accountId.equals(unit.getOrgAccountId())){
    				continue;
    			}

    			if(Strings.isNotEmpty(ts) && !ts.contains(unit.getType().name())){
    				continue;
    			}

    			if(!unit.getEnabled() && !unit.getIsDeleted()){
    				result.add(OrgHelper.cloneEntity(unit));
    			}
    		}
    	}

        return result;
    }


    @Override
    public void setOrgExportFlag(boolean flag) {
        this.OrgExportFlag.set(Boolean.valueOf(flag));
    }

    @Override
    public boolean getOrgExportFlag() {
        boolean flag = false;
        flag = this.OrgExportFlag.get();
        return flag;
    }

    @Override
    public V3xOrgEntity getEntityOnlyById(Long id) {
        V3xOrgEntity ent = null;

        V3xOrgLevel l = this.OrgLevelCache.get(id);
        if (null != l) {
            return OrgHelper.cloneEntity(l);
        }
        V3xOrgPost p = this.OrgPostCache.get(id);
        if (null != p) {
            return OrgHelper.cloneEntity(p);
        }
        V3xOrgUnit u = this.OrgUnitCache.get(id);
        if (null != u) {
            return OrgHelper.cloneEntity(u);
        }
        V3xOrgTeam t = this.OrgTeamCache.get(id);
        if (null != t) {
            return OrgHelper.cloneEntity(t);
        }
        V3xOrgMember m = this.OrgMemberCache.get(id);
        if (null != m) {
            return OrgHelper.cloneEntity(m);
        }
        V3xOrgVisitor v = this.OrgVisitorCache.get(id);
        if (null != v) {
            return OrgHelper.cloneEntity(v);
        }

        return ent;
    }

    public <T extends V3xOrgEntity> T getDisabledEntity(Class<T> entityClass, Long id) {
        return getDisabledEntity(entityClass, id, true);
    }

    public <T extends V3xOrgEntity> T getDisabledEntity(Class<T> entityClass, Long id, boolean isClone) {
    	String className = entityClass.getSimpleName();
    	if(V3xOrgAccount.class.getSimpleName().equals(className) || V3xOrgDepartment.class.getSimpleName().equals(className) || V3xOrgUnit.class.getSimpleName().equals(className)) {
    		className = V3xOrgUnit.class.getSimpleName();
    	}
        T entity = (T) OrgDisabledEntityCache.get(className + id);

        return isClone ? OrgHelper.cloneEntity(entity) : entity;
    }

    public void cacheDisabledEntity(V3xOrgEntity entity){
    	String className = entity.getClass().getSimpleName();
    	if(V3xOrgAccount.class.getSimpleName().equals(className) || V3xOrgDepartment.class.getSimpleName().equals(className) || V3xOrgUnit.class.getSimpleName().equals(className)) {
    		className = V3xOrgUnit.class.getSimpleName();
    	}
    	String key = className + entity.getId();
    	OrgDisabledEntityCache.put(key, entity);
    }
    
    private void cacheDisabledRemove(V3xOrgEntity entity){
    	String key = entity.getClass().getSimpleName() + entity.getId();
    	if(OrgDisabledEntityCache.contains(key)) {
    		OrgDisabledEntityCache.remove(key);
    	}
    }
    
    /**
     * 访问orgrel的监控日志记录方法
     */
    private static void slow(){
        int count = Strings.escapeNULL((Integer)AppContext.getThreadContext("OrgRS-Count"), (Integer)0);
        String threshold = Strings.escapeNULL(SystemProperties.getInstance().getProperty("org.rs.count.max"), "3");//阀值，通过系统可配置，缺省值为3
        Object flag = AppContext.getThreadContext("OrgRS-Count-flag");
        if(count > Integer.valueOf(threshold).intValue()){
            if(!Strings.equals(flag, true)){
                log4Rel.debug("[" + count + "]", new Exception());
                AppContext.putThreadContext("OrgRS-Count-flag", true);
            }
        }
        AppContext.putThreadContext("OrgRS-Count", ++count);
    }

    /**
     *
     * @param cachMap
     * @param key
     * @param value
     */
    private void add2Cache(CacheMap<Long, ArrayList<Long>> cachMap, Long key, Long value){
        ArrayList<Long> ids = cachMap.get(key);
        if(ids == null){
            ids = new ArrayList<Long>();
        }
        ids.add(value);
        cachMap.put(key, ids);
    }

    /**
     *
     * @param cachMap
     * @param key
     * @param value
     */
    private void add2Cache_(CacheMap<String, ArrayList<Long>> cachMap, String key, Long value){
        ArrayList<Long> ids = cachMap.get(key);
        if(ids == null){
            ids = new ArrayList<Long>();
        }
        ids.add(value);
        cachMap.put(key, ids);
    }

    /**
     * 把两个Map里面的Value/list按照key合并
     * @param <T>
     * @param <V>
     * @param oldMap
     * @param newMap
     * @return
     */
    private <T extends Serializable, V> Set<T> mergeCacheMap(CacheMap<T, ArrayList<V>> cacheMap, Map<T, ArrayList<V>> newMap){
        Set<T> updateKey = new HashSet<T>();

        for (Iterator<Map.Entry<T, ArrayList<V>>> iterator = newMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<T, ArrayList<V>> e = iterator.next();

            T key = e.getKey();

            ArrayList<V> newValue = newMap.get(key);
            ArrayList<V> oldValue = cacheMap.get(key);

            if(oldValue == null){
                cacheMap.put(key, new ArrayList<V>(newValue));
            }
            else{
                oldValue.addAll(newValue);
            }

            updateKey.add(key);
        }

        return updateKey;
    }

    private boolean isConPost(OrgRelationship orgRelationship){
        return
        Strings.equals(orgRelationship.getObjective5Id(),OrgConstants.MemberPostType.Concurrent.name())||
        Strings.equals(orgRelationship.getObjective5Id(),OrgConstants.MemberPostType.Second.name());
    }

    private boolean isLeaderOrMember(OrgRelationship orgRelationship){
        return
        Strings.equals(orgRelationship.getObjective5Id(),OrgConstants.TeamMemberType.Leader.name())||
        Strings.equals(orgRelationship.getObjective5Id(),OrgConstants.TeamMemberType.Member.name());
    }

    @Override
    public List<Long> getSubDeptList(Long parentDpetId,String subDeptType){
    	List<Long> subDeptList = new UniqueList<Long>();
    	subDeptType = (!Strings.isBlank(subDeptType))?parentDpetId +"_"+subDeptType:parentDpetId.toString();
    	List<Long> list = SubDeptCache.get(subDeptType);
    	if(Strings.isNotEmpty(list)){
    		subDeptList.addAll(list);
    	}
    	return subDeptList;
    }

    @Override
    public List<Long> getSubDeptList(Long parentDpetId){
    	String subDeptType = "";
    	return getSubDeptList(parentDpetId,subDeptType);
    }

    @Override
    public List<Long> getAllMembers(Long accountId){
        List<Long> memberIds = new UniqueList<Long>();
        //主岗的人
        List<Long> mainMemberIds = OrgAccountMemberCache.get(accountId);
        if(Strings.isNotEmpty(mainMemberIds)){
            memberIds.addAll(mainMemberIds);
        }

        //副岗和兼职的人，只挑出兼职的人就可以了，副岗的人已经在主岗中包含了
        List<Long> conPostRelationshipIds = OrgEntityConPostRSCache.get(accountId);
        if(Strings.isNotEmpty(conPostRelationshipIds)){
            for(Long relId : conPostRelationshipIds){
                V3xOrgRelationship relationShip = getV3xOrgRelationshipByTypeAndId(RelationshipType.Member_Post.name(),relId);
                if(relationShip != null){
                    Long memberId = relationShip.getSourceId();
                    if(memberId != null && !memberIds.contains(memberId)){
                        memberIds.add(memberId);
                    }
                }
            }
        }

        return memberIds;
    }

    @Override
    public List<Long> getMembersByTelnum(String telNum){
    	List<Long> result = new UniqueList<Long>();
        List<Long> memberIds = OrgTelnumMemberCache.get(telNum);
        if(Strings.isNotEmpty(memberIds)){
        	result.addAll(memberIds);
        }

        return result;
    }

    @Override
    public List<Long> getAllLevels(Long accountId){
        List<Long> levelIds = new UniqueList<Long>();
        if(accountId != null){
        	levelIds.addAll(OrgAccountLevelCache.get(accountId));
        }else{
        	for(Long aId : OrgAccountLevelCache.keySet()){
        		levelIds.addAll(OrgAccountLevelCache.get(aId));
        	}
        }
        return levelIds;
    }

    @Override
    public List<Long> getAllRoles(Long accountId){
        List<Long> roleIds = new UniqueList<Long>();
        if(accountId != null){
        	roleIds.addAll(OrgAccountRoleCache.get(accountId));
        }else{
        	for(Long aId : OrgAccountRoleCache.keySet()){
        		V3xOrgUnit unit = OrgUnitCache.get(aId);
        		if(unit != null && (unit.getExternalType() == null || unit.getExternalType() == OrgConstants.ExternalType.Inner.ordinal())) {
        			roleIds.addAll(OrgAccountRoleCache.get(aId));
        		}
        	}
        }
        return roleIds;
    }



    private <T> boolean isEmpty(FoolishSet<T> c){
        return c == null || c.isEmpty();
    }

	@Override
	public void init() {
		// TODO Auto-generated method stub
	}
	
	@Override
    public List<V3xOrgRelationship> getDepartmentPostRelastionships(Long departmentId){
        
        List<Long> rsIds = this.OrgDepartmentPostRSCache.get(departmentId);
        //?? 不应该有空
        if(rsIds == null){
            return Collections.EMPTY_LIST;
        }
        List<V3xOrgRelationship> result = new UniqueList<V3xOrgRelationship>(rsIds.size());
        
        for (Long rsId : rsIds) {
            V3xOrgRelationship rs = relationshipMap.get(OrgConstants.RelationshipType.Department_Post.name()).get(rsId);
            if(rs == null){
                continue;
            }
            result.add(rs);
        }
        return result;
    }
}
