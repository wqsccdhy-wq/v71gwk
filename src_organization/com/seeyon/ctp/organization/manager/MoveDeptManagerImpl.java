package com.seeyon.ctp.organization.manager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.init.MclclzUtil;
import com.seeyon.ctp.event.EventDispatcher;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrgTypeIdBO;
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
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.event.MoveDepartmentEvent;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.organization.po.OrgRelationship;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgResult;
import com.seeyon.ctp.permission.bo.LicenseConst;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin})
@ProcessInDataSource(name = DataSourceName.BASE)
public class MoveDeptManagerImpl implements MoveDeptManager {
    private final static Log   logger = LogFactory.getLog(MoveDeptManagerImpl.class);
    protected OrgCache         orgCache;
    protected OrgDao           orgDao;
    protected OrgManagerDirect orgManagerDirect;
    protected OrgManager       orgManager;
    protected PrincipalManager principalManager;
    protected AppLogManager    appLogManager;
    protected RoleManager      roleManager;
    
	public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }

    public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}

	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}

	public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	private static final Class<?> c1 = MclclzUtil.ioiekc("com.seeyon.ctp.permission.bo.LicensePerInfo");

	@Override
    public List<WebV3xOrgResult> movedept(Map move) throws BusinessException {
        Long accountId = Long.valueOf(move.get("accountId").toString().split("\\|")[1]);
        // 获得调整部门
        List<V3xOrgEntity> deptents = orgManager.getEntities(move.get("deptIds").toString());
        //String deptIds = move.get("deptIds").toString();

        // 记录原单位
        List<Long> accountIds = new ArrayList<Long>();
        // 记录调整部门信息
        List<V3xOrgDepartment> moveDepts = new ArrayList<V3xOrgDepartment>();
        // 校验并移动部门
        List<WebV3xOrgResult> resultList = new ArrayList<WebV3xOrgResult>();

        User user = AppContext.getCurrentUser();

        //boolean isMoveDept = false;
        for (V3xOrgEntity strid : deptents) {
            WebV3xOrgResult resultModel = new WebV3xOrgResult();
            List<String[]> validateListStr = new ArrayList<String[]>();
            List<String[]> moveLogList = new ArrayList<String[]>();
            Long id = strid.getId();
            String str1 = "";
            if (id != null) {
                V3xOrgDepartment dept = orgManager.getDepartmentById(id);
                str1 = dept.getName();
                //部门调整前的校验
                List<String> validateList = validateMoveDept(id, accountId);
                //判断是否有调入的两个重名部门
                for (V3xOrgDepartment addDept : moveDepts) {
                    if (dept.getName().equals(addDept.getName())) {
                        validateList.add("1");
                    }
                }
                if (validateList.size() > 0) {
                    for (String str : validateList) {
                        String[] strArray = str.split("-");
                        validateListStr.add(strArray);
                    }
                } else if (validateList.size() == 0) {
                    if (!accountIds.contains(dept.getOrgAccountId())) {
                        accountIds.add(dept.getOrgAccountId());
                    }
                    moveLogList = moveDept(id, accountId);
                    //重新设置部门空间的所属单位
                    //spaceApi.transplantDepartmentSpace(id, accountId);	
                    V3xOrgAccount account = orgManager.getAccountById(accountId);
                    //记录调整部门
                    moveDepts.add(dept);
                    //记录日志
                    appLogManager.insertLog(user, AppLogAction.Organization_MoveDept, dept.getName(),
                            account.getName(), orgManager.getGroupAdmin().getName());

                    //isMoveDept = true;

                    //触发部门调整事件
                    V3xOrgDepartment d = orgManager.getDepartmentById(dept.getId());
                    d.setOrgAccountId(accountId);
                    MoveDepartmentEvent moveDeptEvent = new MoveDepartmentEvent(this);
                    moveDeptEvent.setDepartment(d);
                    moveDeptEvent.setOldDepartment(dept);
                    EventDispatcher.fireEvent(moveDeptEvent);
                }
                V3xOrgAccount account = orgManager.getAccountById(dept.getOrgAccountId());
                if (account != null)
                    str1 += "(" + account.getShortName() + ")";
            }
            resultModel.setStr1(str1);
            resultModel.setStr2(String.valueOf(accountId));
            resultModel.setValidateList(validateListStr);
            resultModel.setMoveLogList(moveLogList);
            resultList.add(resultModel);
        }

        return resultList;
    }
	
    public List<String> validateMoveDept(Long deptId, Long accountId) throws BusinessException {
        //log.info(DateUtil.formatDate(new Date())+"开始移动部门校验");		
        V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
        V3xOrgAccount account = orgManager.getAccountById(accountId);
        List<String> validateList = new ArrayList<String>();
        if (account != null) {
            if (dept != null) {
                //校验调整部门是否是调入单位的部门
                if (dept.getOrgAccountId().equals(accountId)) {
                    validateList.add("5");
                }
                //校验调入单位是否已经存在同级同名部门
                List<V3xOrgDepartment> accdepts = orgManager.getChildDepartments(accountId, true);
                if (accdepts != null && accdepts.size() > 0) {
                    for (V3xOrgDepartment accdept : accdepts) {
                        if (accdept.getName().equals(dept.getName())) {
                            validateList.add("1");
                        }
                    }
                }
                //校验调入单位中是否有相同部门编码的部门
                boolean isDuplicated = orgManagerDirect.isPropertyDuplicated(V3xOrgDepartment.class.getSimpleName(), "code",
                		dept.getCode(), accountId);
                if(dept.getCode()!=null&&!("").equals(dept.getCode())&&isDuplicated){
                	validateList.add("6");
                }
                
                // 校验部门下人员
                // OA-50828 部门调整也要检查子部门的人员的职务级别
                List<V3xOrgMember> deptMems = orgManager.getAllMembersByDepartmentId(dept.getId(), true, null, true, true, null, null, null);
                if (deptMems != null) {
                    for (V3xOrgMember deptMemEnt : deptMems) {
                        //部门下人员的职务级别是否存在
                        V3xOrgLevel memlevel = orgManager.getLevelById(deptMemEnt.getOrgLevelId());
                        if (memlevel != null) {
                        	List<V3xOrgEntity> levelList = orgManager.getEntityList(V3xOrgLevel.class.getSimpleName(), "name", memlevel.getName(), accountId, false, true);
                            if (levelList == null || levelList.size() == 0) {
                                validateList.add("2|" + deptMemEnt.getName() + "|" + memlevel.getName());
                            } else {
                                for (V3xOrgEntity level : levelList) {
                                    if (!level.getEnabled()) {
                                        validateList.add("3|" + deptMemEnt.getName() + "|" + memlevel.getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //log.info(DateUtil.formatDate(new Date())+"完成移动部门校验");
        return validateList;
    }

	public List<String[]> moveDept(Long deptId, Long accountId) throws BusinessException {
		logger.info("---------moveDept  start -----"+ deptId);
        List<String[]> moveLogListStr = new ArrayList<String[]>();
        V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
        V3xOrgAccount account = orgManager.getAccountById(accountId);
        V3xOrgRole defultRole = roleManager.getDefultRoleByAccount(accountId);
        Long oldAccountId = dept.getOrgAccountId();
        List<V3xOrgMember> deptMems = orgManagerDirect.getMembersByDepartment(deptId, oldAccountId, false, null, null);
		//校验许可数信息
		Object info ;
        if ("1".equals(MclclzUtil.invoke(c1, "getServerPermissionType"))) {
            info = MclclzUtil.invoke(c1, "getInstance",new Class[]{String.class},null,new Object[]{""});
        } else {
            info = MclclzUtil.invoke(c1, "getInstance",new Class[]{String.class},null,new Object[]{String.valueOf(accountId)});
        }
        if (((Integer)MclclzUtil.invoke(c1, "getserverType",null,info,null)).intValue()==LicenseConst.PERMISSION_TYPE_RES) {
            if (((Long)MclclzUtil.invoke(c1, "getUnuseservernum",null,info,null)).intValue() < deptMems.size()) {
                throw new BusinessException(ResourceUtil.getString("org.validate.permission"));
            }
        }
		//取所有用户所有的兼职单位
		//要清空汇报人的该部门人员
		List<OrgMember> reportToMembers = new UniqueList<OrgMember>();

		for(V3xOrgMember deptMem:deptMems){
			//删除这个部门下所有人员的汇报人 (如果汇报人也在本部门，不删除)     
			Long reportor = deptMem.getReporter();
			if(null!=reportor && reportor!=-1){
				V3xOrgMember reportMember = orgManager.getMemberById(reportor);
				if(reportMember.getOrgDepartmentId().compareTo(deptMem.getOrgDepartmentId())!=0) {
					deptMem.setReporter(null);
					OrgMember m = (OrgMember) deptMem.toPO();
					m.setExtAttr37(-1L);
					reportToMembers.add(m);
				}
			}
		}
		if(dept!=null&&account!=null){	
            //如果其他部门的汇报人在该部门,删除这些人的汇报人
			List<OrgMember> reportMembersList= orgDao.getAllMembersByReportToDept(deptId);
			if(null!=reportMembersList && reportMembersList.size()>0){
				for(OrgMember reportMem:reportMembersList){
					reportMem.setExtAttr37(-1L);
					reportToMembers.add(reportMem);
				}
			}
			
			orgDao.update(reportToMembers);
			
			//log.info(DateUtil.formatDate(new Date())+"开始移动部门:"+dept.getName());
            //删除部门岗位关系
			orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Department_Post.name(), dept.getId(), oldAccountId, null);
			
			//删除外部人员访问权限
			EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
	        enummap.put(OrgConstants.RelationshipObjectiveName.objective0Id, dept.getId());
	        
	        orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.External_Workscope.name(), null, oldAccountId, enummap);
	        
	        //老单位的人员岗位关系删除
	        /*List<V3xOrgRelationship> mp = orgManager.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Post, null, oldAccountId, enummap);
	        UniqueList<Long> memberIds = new UniqueList<Long>();
	        for(V3xOrgRelationship rel:mp){
				memberIds.add(rel.getSourceId());
			}
	        UniqueList<Long> cntMemberIds = new UniqueList<Long>();
			cntMemberIds.addAll(memberIds);*/
			//修改主岗方法 steven
			//删除被调整部门在原单位的所有Member_Post信息，包括副岗和兼职
	        orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Post.name(), null, oldAccountId, enummap);
	        
	        //删除原单位分配给该部门的角色
	        if(!oldAccountId.equals(accountId)){
	            orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Role.name(), dept.getId(), oldAccountId, null);
	        }
	        
	        //删除这个部门及其子部门下的人员在其他部门单位的部门角色//OA-50947
	        List<V3xOrgRole> deptRoles = orgManager.getDepartmentRolesByAccount(accountId);
            Map<String, Long> newDeptRoleMap = new HashMap<String, Long>();
            for (V3xOrgRole r : deptRoles) {
                newDeptRoleMap.put(r.getCode(), r.getId());
            }
            
            /**
             * 更新部门、子部门下所有人员的单位角色和部门角色 
             * 删除副岗
             */
	        moveDeptRoleRel(accountId, dept, oldAccountId, newDeptRoleMap);
			
			// 修改部门人员个人组的所属单位
			List<V3xOrgTeam> allTeams = orgManager.getAllTeams(dept.getOrgAccountId());
			for (V3xOrgTeam team : allTeams) {
				V3xOrgMember owner = orgManager.getMemberById(team.getOwnerId());
				if(owner!=null){
					if(deptId.equals(owner.getOrgDepartmentId())){
						// 部门人员建立的组
						team.setDepId(deptId);
						team.setOrgAccountId(accountId);
						// 修改组之前要先维护组员
						List<OrgTypeIdBO> teamMemberIds = new ArrayList<OrgTypeIdBO>();
						for(V3xOrgEntity m : orgManager.getTeamMember(team.getId())) {
						    teamMemberIds.add(new OrgTypeIdBO(m.getId(),m.getEntityType()));
						}
						team.setMembers(teamMemberIds);
						
						orgManagerDirect.updateTeam(team); 
						
						List<V3xOrgRelationship> rels = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Team_PubScope, team.getId(), oldAccountId, null);
						
						for (V3xOrgRelationship rel : rels) {
							// 从原单位移除
							//orgManager.deleteEntity(rel);
							// 添加到新单位
							rel.setOrgAccountId(accountId);
							//AEIGHT-5577 lilong 没有必要先删除再新增，这个Team_Leader或者Team_Member的关系直接更新单位id即可
							orgManagerDirect.updateV3xOrgRelationship(rel);
							//orgDao.updateEntity(rel);
						}
					}
				}
			}				
			
			
			//部门调整组规则
			/**
			 * OA-38032
			 * 张娜梳理规则
			 * 按照原A8的逻辑，当一个拥有部门组的部门被调整到其他单位的时候，做如下处理
             *  1、部门组升级为单位组，所属部门改成此部门的原单位
             *  2、组成员不变，因为单位组的成员允许是跨单位的
             *  3、对于5.0新增的公开范围功能，做部门调整后，升级为单位组后
             *  4、公开范围涉及到被调整部门的时候，将此部门剔除，如果剔除后公开范围一个部门都没有，则变为单位私有组 
			 */
            //删除公开范围有这个部门的所有的关系记录
            EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(
                    RelationshipObjectiveName.class);
            objectiveIds.put(RelationshipObjectiveName.objective0Id, dept.getId());
            orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Team_PubScope.name(), null,
                    dept.getOrgAccountId(), objectiveIds);
            //部门组不进行调整，变为单位组
            List<V3xOrgEntity> deptTeams = orgManager.getEntityListNoRelation(V3xOrgTeam.class.getSimpleName(),
                    "ownerId", deptId, dept.getOrgAccountId());
            if (deptTeams != null && deptTeams.size() > 0) {
                for (V3xOrgEntity deptTeam : deptTeams) {
                    V3xOrgTeam dTeam = ((V3xOrgTeam) deptTeam);
                    dTeam.setOwnerId(oldAccountId);
                    buildTeamMember(dTeam);
                    orgManagerDirect.updateTeam(dTeam);
                }
            }

			//删除子部门与组之间的关系
            //移动部门
            dept.setSuperior(accountId);
            dept.setOrgAccountId(accountId);
            
			List<V3xOrgDepartment> childDepts = orgManagerDirect.getChildDepartmentsWithInvalid(deptId, false);
			if (childDepts != null && childDepts.size() > 0) {
			    for (V3xOrgDepartment childDept : childDepts) {
			        //删除子部门的公开范围的关系记录
			        EnumMap<RelationshipObjectiveName, Object> objectiveIds1 = new EnumMap<RelationshipObjectiveName, Object>(
			                RelationshipObjectiveName.class);
			        objectiveIds.put(RelationshipObjectiveName.objective0Id, childDept.getId());
			        orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Team_PubScope.name(), null,
			                dept.getOrgAccountId(), objectiveIds1);
			        //处理组成员
			        List<V3xOrgEntity> childDeptTeams = orgManager
			                .getEntityListNoRelation(V3xOrgTeam.class.getSimpleName(), "ownerId", childDept.getId(),
			                        childDept.getOrgAccountId());
			        if (childDeptTeams != null && childDeptTeams.size() > 0) {
			            for (V3xOrgEntity childDeptTeam : childDeptTeams) {
			                V3xOrgTeam cTeam = ((V3xOrgTeam) childDeptTeam);
			                cTeam.setOwnerId(oldAccountId);
			                buildTeamMember(cTeam);
			                orgManagerDirect.updateTeam(cTeam);
			            }
			        }
			        // OA-64795 OA-64524
			        /*String oldPathString = childDept.getPath();
			        String str1 = oldPathString.substring(path.length(), oldPathString.length());
			        String newString = path + str1;
			        childDept.setPath(newString);*/
			        //childDept.setPath(childDept.getPath().replaceFirst(oldPath, path));
			        //childDept.setOrgAccountId(accountId);
                   // orgDao.update((OrgUnit) OrgHelper.boTopo(childDept));
			    }
			}
			
            orgManagerDirect.updateDepartment(dept);
			//此处将所有子部门ID整理起来以备用
			Set<Long> allDeptIds = new HashSet<Long>();
			allDeptIds.add(deptId);
            if (childDepts != null && childDepts.size() > 0) {
                for (V3xOrgDepartment childDept : childDepts) {
                    allDeptIds.add(childDept.getId());
	                //删除部门岗位关系
					orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Department_Post.name(), childDept.getId(), oldAccountId, null);
					//orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_DEP_POST,"sourceId",childDept.getId());
					//删除外部人员访问权限
					EnumMap<RelationshipObjectiveName, Object> enummap1 = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
			        enummap1.put(OrgConstants.RelationshipObjectiveName.objective0Id, childDept.getId());
			        orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.External_Workscope.name(), null, oldAccountId, enummap1);
			        
					//删除部门下的兼职
					/*List<OrgRelationship> mpc = orgDao.getOrgRelationshipPO(OrgConstants.RelationshipType.Member_Post, null, oldAccountId, enummap1, null);
//					List<V3xOrgRelationship> cntMpc = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"objectiveId",childDept.getId());
//					//mpc.addAll(cntMpc);
					//accountMp.removeAll(cntMpc);					
					List<Long> memberIdsC = new ArrayList<Long>();
					for(OrgRelationship rel:mpc){
						memberIdsC.add(rel.getSourceId());
					}*/
					//修改主岗方法 steven
					enummap.put(OrgConstants.RelationshipObjectiveName.objective0Id, childDept.getId());
					orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Post.name(), null, oldAccountId, enummap);
			        
//					orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_POST,"objectiveId",childDept.getId());
//					orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"objectiveId",childDept.getId());
//					if(memberIdsC.size()>0){
//						orgManager.deleteRelsInList(memberIdsC, V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE);
//					}	
//					cntMemberIds.addAll(memberIdsC);
					
				}
			}
			//log.info(DateUtil.formatDate(new Date())+"删除子部门关系完成");
			//移动人员
            if (deptMems != null) {
                //部门岗位关系
                for (V3xOrgMember deptMem : deptMems) {
                    if(!deptMem.getOrgDepartmentId().equals(dept.getId()) 
                            && !allDeptIds.contains(deptMem.getOrgDepartmentId())) {
                        //OA-46623 OA-41900 副岗到这里的人不考虑，但是要考虑子部门的人员
                        continue;
                    }
                    
                    //清除部门人员在调入单位的兼职
                    enummap.clear();
                    enummap.put(OrgConstants.RelationshipObjectiveName.objective5Id, OrgConstants.MemberPostType.Concurrent.name());
                    orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Post.name(), deptMem.getId(), accountId, enummap);
                    
                    deptMem.setOrgAccountId(accountId);
					//设置岗位
					//orgManager.getAllPosts(orgAccount.getId())
					
					V3xOrgPost memPost = orgManager.getPostById(deptMem.getOrgPostId());;
					if(memPost!=null){
						List<V3xOrgEntity> accountPost = orgManager.getEntityList(V3xOrgPost.class.getSimpleName(), "name", memPost.getName(), accountId, false, true);
						if(accountPost!=null&&accountPost.size()>0){
							V3xOrgPost post = (V3xOrgPost)accountPost.get(0);
							//如果岗位停用则启用岗位
							if(!post.getEnabled()){
								post.setEnabled(true);
								orgManagerDirect.updatePost(post);
							}
							deptMem.setOrgPostId(post.getId());
							//更新部门岗位关系
						}else{
							//如果新单位不存在同名岗位则创建岗位
							V3xOrgPost newPost = new V3xOrgPost();
							newPost.setTypeId(memPost.getTypeId());
							newPost.setName(memPost.getName());
							newPost.setSortId(V3xOrgEntity.SORT_STEP_NUMBER);
							newPost.setOrgAccountId(accountId);
							orgManagerDirect.addPost(newPost);
							//记录日志
							String[] addPostStr = new String[3];
							addPostStr[0] = "1";
							addPostStr[1] = memPost.getName();
							moveLogListStr.add(addPostStr);
							//创建人员岗位关系
							V3xOrgRelationship relpost = new V3xOrgRelationship();
							relpost.setKey(OrgConstants.RelationshipType.Member_Post.name());
							relpost.setOrgAccountId(accountId);
							relpost.setSourceId(deptMem.getId());
							relpost.setObjective0Id(deptMem.getOrgDepartmentId());
							relpost.setObjective1Id(newPost.getId());
							relpost.setObjective5Id(OrgConstants.MemberPostType.Main.name());
							orgManagerDirect.addOrgRelationship(relpost);
							
							//如果是集团基准岗，需要创建关系
							//List<OrgRelationship> rels = orgDao.getOrgRelationshipPO(OrgConstants.RelationshipType.Banchmark_Post, memPost.getId(), oldAccountId, null, null);
							List<V3xOrgRelationship> rels = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Banchmark_Post, memPost.getId(), oldAccountId, null);
							
                            if (rels != null && rels.size() > 0) {
                                //创建基准岗关系
                                V3xOrgRelationship rel = new V3xOrgRelationship();
                                rel.setKey(OrgConstants.RelationshipType.Banchmark_Post.name());
                                rel.setOrgAccountId(accountId);
                                rel.setSourceId(newPost.getId());
                                rel.setObjective0Id(rels.get(0).getObjective0Id());
                                orgManagerDirect.addOrgRelationship(rel);
                                //orgManager.updateEntity(rel);
                            }
							deptMem.setOrgPostId(newPost.getId());
						}
					}else{
						deptMem.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
						deptMem.setEnabled(false);
					}
					
					//设置职务级别
                    V3xOrgLevel memLevel = orgManager.getLevelById(deptMem.getOrgLevelId());
                    if (memLevel != null) {
                        List<V3xOrgEntity> accountLevel = orgManager.getEntityList(V3xOrgLevel.class.getSimpleName(), "name", memLevel.getName(), accountId, false, true);
                        if (accountLevel != null && accountLevel.size() > 0) {
                            deptMem.setOrgLevelId(accountLevel.get(0).getId());
                        } else {
                            deptMem.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
                            deptMem.setEnabled(false);
                        }
                    } else {
                        deptMem.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
                        deptMem.setEnabled(false);
                    }
                    
                    //清除人员副岗
					deptMem.setSecond_post(new ArrayList<MemberPost>());
					orgManagerDirect.updateMember(deptMem);
					
					//fix OA-18225 更新后将调入单位的普通人员角色副给这个人
					if(null != defultRole) {
					    orgManagerDirect.addRole2Entity(defultRole.getId(), accountId, deptMem);
					}
					
					//orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"sourceId",deptMem.getId(),"orgAccountId",accountId);
					//log.info(DateUtil.formatDate(new Date())+"完成移动人员:"+deptMem.getName());
				}
                
			}
		}
		
		if(moveLogListStr.size()==0){
			String[] sucStr = new String[3];
			sucStr[0] = "2";
			sucStr[1] = dept.getName();
			moveLogListStr.add(sucStr);
		}
		logger.info("---------moveDept  end -----"+ moveLogListStr.toString());
		return moveLogListStr;
	}

    private void moveDeptRoleRel(Long accountId, V3xOrgDepartment dept, Long oldAccountId,
            Map<String, Long> newDeptRoleMap) throws BusinessException {
        // 包括调整部门和子部门的所有ID集合
        Set<Long> deptIds = new HashSet<Long>();
        List<V3xOrgDepartment> childs = orgManager.getChildDepartments(dept.getId(), false);
        for (V3xOrgDepartment c : childs) {
            deptIds.add(c.getId());
        }
        deptIds.add(dept.getId());
        
        List<V3xOrgMember> allDeptMembers = orgManager.getAllMembersByDepartmentId(dept.getId(), true, null, true, true, null, null, null);
        for (V3xOrgMember v3xOrgMember : allDeptMembers) {
        	// 删除这些人在别的部门的副岗信息
        	EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
        	enummap.put(OrgConstants.RelationshipObjectiveName.objective5Id, OrgConstants.MemberPostType.Second.name());
        	List<V3xOrgRelationship> secondRel = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Post, v3xOrgMember.getId(), oldAccountId, enummap);
        	for(V3xOrgRelationship sec : secondRel){
        		orgDao.deleteOrgRelationshipPOById(sec.getId());
        	}
        	
        	//删除这些人的单位角色
            EnumMap<RelationshipObjectiveName, Object> enummap2 = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
            enummap2.put(OrgConstants.RelationshipObjectiveName.objective0Id, oldAccountId);
            orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Role.name(), v3xOrgMember.getId(), oldAccountId, enummap2);


            // 更新旧部门的部门角色关系成新部门的新角色
            EnumMap<RelationshipObjectiveName, Object> enummap3 = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
            enummap3.put(OrgConstants.RelationshipObjectiveName.objective0Id, v3xOrgMember.getOrgDepartmentId());
            List<V3xOrgRelationship> deptRels = new UniqueList<V3xOrgRelationship>();
            for(Long id : deptIds) {
                enummap3.put(OrgConstants.RelationshipObjectiveName.objective0Id, id);
                deptRels.addAll(orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, v3xOrgMember.getId(), oldAccountId, enummap3));
            }
            
            List<OrgRelationship> orgRelationshipPOs = new ArrayList<OrgRelationship>(deptRels.size());
            for (V3xOrgRelationship rel : deptRels) {
                V3xOrgRole o1 = orgCache.getV3xOrgEntity(V3xOrgRole.class, rel.getObjective1Id());
                Long newRoleIdLong = null;
                if (null != o1) {
                    newRoleIdLong = newDeptRoleMap.get(o1.getCode());
                }
                if(null == newRoleIdLong) {
                    orgDao.deleteOrgRelationshipPOById(rel.getId());
                    continue;
                }
                rel.setObjective1Id(newRoleIdLong);
                rel.setOrgAccountId(accountId);
                orgRelationshipPOs.add((OrgRelationship)rel.toPO());
            }
            orgDao.updateRelationships(orgRelationshipPOs);
        }
        
        // 此时部门角色已经调整到了新的单位下，然后删除已经不属于调整后所在单位的人员的部门角色
        for(Long id : deptIds) {
            EnumMap<RelationshipObjectiveName, Object> enummap4 = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
            enummap4.put(OrgConstants.RelationshipObjectiveName.objective0Id, id);
            orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Role.name(), null, oldAccountId, enummap4);
        }
    }
	
	private void buildTeamMember(V3xOrgTeam team) throws BusinessException {
	    //Leader
	    List<OrgTypeIdBO> leaders = new UniqueList<OrgTypeIdBO>();
	    for (V3xOrgEntity m : orgManager.getTeamMember(team.getId(), OrgConstants.TeamMemberType.Leader)) {
	        OrgTypeIdBO typeIdBO = new OrgTypeIdBO();
			typeIdBO.setId(m.getId());
	        typeIdBO.setType(m.getEntityType());
	        leaders.add(typeIdBO);
        }
	    team.setLeaders(leaders);
	    //Member
	    List<OrgTypeIdBO> members = new UniqueList<OrgTypeIdBO>();
        for (V3xOrgEntity m : orgManager.getTeamMember(team.getId(), OrgConstants.TeamMemberType.Member)) {
            OrgTypeIdBO typeIdBO = new OrgTypeIdBO();
			typeIdBO.setId(m.getId());
            typeIdBO.setType(m.getEntityType());
            members.add(typeIdBO);
        }
        team.setMembers(members);
        //Relative
        List<OrgTypeIdBO> relatives = new UniqueList<OrgTypeIdBO>();
        for (V3xOrgEntity m : orgManager.getTeamMember(team.getId(), OrgConstants.TeamMemberType.Relative)) {
            OrgTypeIdBO typeIdBO = new OrgTypeIdBO();
			typeIdBO.setId(m.getId());
            typeIdBO.setType(m.getEntityType());
            relatives.add(typeIdBO);
        }
        team.setRelatives(relatives);
        //SuperVisor
        List<OrgTypeIdBO> superVisors = new UniqueList<OrgTypeIdBO>();
        for (V3xOrgEntity m : orgManager.getTeamMember(team.getId(), OrgConstants.TeamMemberType.SuperVisor)) {
            OrgTypeIdBO typeIdBO = new OrgTypeIdBO();
			typeIdBO.setId(m.getId());
            typeIdBO.setType(m.getEntityType());
            superVisors.add(typeIdBO);
        }
        team.setSupervisors(superVisors);
	}
	
}
