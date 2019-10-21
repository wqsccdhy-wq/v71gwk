package com.seeyon.ctp.organization.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.po.OrgRelationship;
import com.seeyon.ctp.organization.po.OrgTeam;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgTeamResult;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.ctp.util.json.JSONUtil;
import com.seeyon.ctp.common.appLog.AppLogAction;

@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator,Role_NAME.HrAdmin,Role_NAME.DepAdmin})
@ProcessInDataSource(name = DataSourceName.BASE)
public class TeamManagerImpl implements TeamManager {
    private final static Log   logger = LogFactory.getLog(TeamManagerImpl.class);
    protected OrgCache         orgCache;
    protected OrgDao           orgDao;
    protected OrgManagerDirect orgManagerDirect;
    protected OrgManager       orgManager;
    protected PrincipalManager principalManager;
    protected AppLogManager    appLogManager;

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

	public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }

    @Override
	public HashMap addTeam(String accountId) throws BusinessException {
		Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgTeam.class.getSimpleName(), Long.parseLong(accountId));
		HashMap m = new HashMap();
		m.put("sortId", maxSortNum+1);
		return m;
	}

	@Override
	public Long saveTeam(String accountId, Map team) throws BusinessException {
		Long currentAccountId = AppContext.currentAccountId();
		User user = AppContext.getCurrentUser();
		Long accId = Long.parseLong(accountId);
		V3xOrgTeam newteam = new V3xOrgTeam();
		String ownerId = "-1";
		V3xOrgMember member = orgManager.getMemberById(user.getId());
		if(member.getIsAdmin()
				|| orgManager.isRole(user.getId(), currentAccountId, OrgConstants.Role_NAME.DepAdmin.name())
		        || orgManager.isRole(user.getId(), currentAccountId, OrgConstants.Role_NAME.HrAdmin.name())){//如果是HR管理员新建的组
			ownerId = String.valueOf(accId);
		}else{
			//考虑兼职 用memberpost取部门
			if(member.getOrgAccountId().equals(accId)){
				ownerId = String.valueOf(member.getOrgDepartmentId());
			}else{
				List<MemberPost> memposts = member.getConcurrent_post();
				for (MemberPost memberPost : memposts) {
					if(memberPost.getOrgAccountId().equals(accId)){
						ownerId = String.valueOf(memberPost.getDepId());
					}
				}

			}


		}
		if(team.get("ownerId")==null||"".equals(team.get("ownerId"))){
			team.put("ownerId", ownerId);
		}else{
			team.put("ownerId", orgManager.getEntity(String.valueOf(team.get("ownerId"))).getId());
			V3xOrgUnit unitById = orgManager.getUnitById(Long.valueOf(team.get("ownerId").toString()));
			V3xOrgUnit owner = orgManager.getUnitById(Long.valueOf(ownerId));
			if(owner.getType().equals(OrgConstants.UnitType.Department)&&!unitById.getPath().startsWith(owner.getPath())){
				throw new BusinessException(unitById.getName()+ResourceUtil.getString("org.validate.team3"));
			}

			//如果只是是部门管理员 所属范围 和公开范围 只能选择 管理的所有的部门及其子部门
	        String deptIds="";
	        if(orgManager.isDepartmentAdmin() && !orgManager.isHRAdmin()){
	    		List<V3xOrgDepartment> depts= orgManager.getDeptsByAdmin(user.getId(),accId);
	    		List<V3xOrgDepartment> allChildDepts = new UniqueList<V3xOrgDepartment>();
	    		for(V3xOrgDepartment dept : depts){
	    			if(allChildDepts.contains(dept)){
	    				continue;
	    			}
	    			allChildDepts.add(dept);
	    			allChildDepts.addAll(orgManager.getChildDepartments(dept.getId(), false));
	    		}

	    		for(V3xOrgDepartment adept : allChildDepts){
	    			deptIds+=adept.getId()+",";
	    		}

	    		//所属范围
    			if(deptIds.indexOf(unitById.getId().toString())<0){
    					throw new BusinessException(unitById.getName()+"不在管理员管理范围之内");
    			}
	    		//公开范围
	    		String scopein = team.get("scopein").toString();
	    		if(Strings.isNotBlank(scopein)){
	    			String[] scopedepts = scopein.split(",");
	    			for(String deptStr : scopedepts){
	    				String deptId =(String) deptStr.split("\\|")[1];
	    				if(Strings.isNotBlank(deptId) && deptIds.indexOf(deptId)<0){
	    					V3xOrgDepartment dept = orgManager.getDepartmentById(Long.valueOf(deptId));
	    					throw new BusinessException(dept.getName()+"不在管理员管理范围之内");
	    				}
	    			}
	    		}
	        }
		}
		ParamUtil.mapToBean(team, newteam, false);
		//处理组的创建实体
		//如果是单位管理员或HR管理员，创建实体是单位
		if(orgManager.isGroupAdminById(user.getId())||orgManager.isAdministrator()||orgManager.isHRAdmin()){
			newteam.setCreateId(accId);
		}
		//如果是部门管理员，创建实体是部门
		else{
			newteam.setCreateId(orgManager.getCurrentDepartment().getId());
		}

        if (newteam.getId() == null) {
        	//排序号的重复处理
    		String isInsert = team.get("sortIdtype").toString();
    		if("1".equals(isInsert)&&orgManagerDirect.isPropertyDuplicated(V3xOrgTeam.class.getSimpleName(), "sortId", newteam.getSortId().longValue(),accId)){
    		        orgManagerDirect.insertRepeatSortNum(V3xOrgTeam.class.getSimpleName(), accId, newteam.getSortId(),null);
    		}
            newteam.setIdIfNew();
            //newteam.setOwnerId(Long.valueOf(ownerId));
            newteam.setOrgAccountId(accId);
            //一定要在送入接口前将组成员组装好，否则分发会有问题，例如分发出去的事件中午组成员
            dealTeamMembers(team, newteam);
            orgManagerDirect.addTeam(newteam);
            //日志
            appLogManager.insertLog4Account(user, user.isGroupAdmin() ? accId : AppContext.currentAccountId(), AppLogAction.Organization_NewTeam, user.getName(), newteam.getName());
        } else {
        	String isInsert = team.get("sortIdtype").toString();
    		if("1".equals(isInsert)&&orgManagerDirect.isPropertyDuplicated(V3xOrgTeam.class.getSimpleName(), "sortId", newteam.getSortId().longValue(),accId,newteam.getId())){
    		        orgManagerDirect.insertRepeatSortNum(V3xOrgTeam.class.getSimpleName(), accId, newteam.getSortId(),null);
    		}
            dealTeamMembers(team, newteam);
            orgManagerDirect.updateTeam(newteam);
            //日志
            appLogManager.insertLog4Account(user, user.isGroupAdmin() ? accId : AppContext.currentAccountId(), AppLogAction.Organization_UpdateTeam, user.getName(), newteam.getName());
        }


		//增加组的公开范围
		List scopelist = new ArrayList();

		//公开组，范围读取
		if("1".equals(team.get("scope").toString())){
			if(!"".equals(team.get("scopein"))){
				scopelist.addAll(orgManager.getEntities(team.get("scopein").toString()));
			}else
				//部门管理员默认公开范围是部门，单位管理员、HR管理员是单位
				if(orgManager.isDepartmentAdmin() && !orgManager.isHRAdmin()){
					scopelist.add(orgManager.getDepartmentById(orgManager.getMemberById(user.getId()).getOrgDepartmentId()));
				}else{
					scopelist.add(orgManager.getAccountById(accId));
				}
		}

		orgManagerDirect.addTeamScope(scopelist, newteam);
		return newteam.getId();
	}

	/**
	 * 处理组成员列表
	 * @param team
	 * @param newteam
	 * @throws BusinessException
	 */
    private void dealTeamMembers(Map team, V3xOrgTeam newteam) throws BusinessException {
        //增加组的各种类型人员
		if(!"".equals(team.get("teamLeader"))){
			List list = orgManager.getEntities(team.get("teamLeader").toString());
			//orgManagerDirect.addTeamMembers(list, newteam, OrgConstants.TeamMemberType.Leader.name());
			newteam.setLeaders(OrgHelper.entityListToIdTypeList(list));
		}
		if(!"".equals(team.get("teamMember"))){
			String teamMembers = team.get("teamMember").toString();
		//	List list = orgManager.getEntities(teamMembers);
			//orgManagerDirect.addTeamMembers(list, newteam, OrgConstants.TeamMemberType.Member.name());
			//newteam.setMembers(OrgHelper.entityListToIdTypeList(list,getEntityMap(teamMembers)));
			newteam.setMembers(OrgHelper.mapToIdTypeList(getEntityMap(teamMembers)));
		}
		if(!"".equals(team.get("teamSuperVisor"))){
			List list = orgManager.getEntities(team.get("teamSuperVisor").toString());
			//orgManagerDirect.addTeamMembers(list, newteam, OrgConstants.TeamMemberType.SuperVisor.name());
			newteam.setSupervisors(OrgHelper.entityListToIdTypeList(list));
		}
		if(!"".equals(team.get("teamRelative"))){
			List list = orgManager.getEntities(team.get("teamRelative").toString());
			//orgManagerDirect.addTeamMembers(list, newteam, OrgConstants.TeamMemberType.Relative.name());
			newteam.setRelatives(OrgHelper.entityListToIdTypeList(list));
		}
    }

    @Override
    public String deleteTeam(List<Map<String, Object>> team) throws BusinessException {
        List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
        teamlist = ParamUtil.mapsToBeans(team, V3xOrgTeam.class, false);
        OrganizationMessage message = orgManagerDirect.deleteTeams(teamlist);
        //日志信息
        List<String[]> appLogs = new ArrayList<String[]>();
        User user = AppContext.getCurrentUser();
        for (V3xOrgTeam t : teamlist) {
            String[] appLog = new String[2];
            appLog[0] = user.getName();
            appLog[1] = t.getName();
            appLogs.add(appLog);
        }
        appLogManager.insertLogs4Account(user, user.isGroupAdmin() ? teamlist.get(0).getOrgAccountId() : AppContext.currentAccountId(), AppLogAction.Organization_DeleteTeam, appLogs);
        return String.valueOf(message.getSuccessMsgs());
    }

	@Override
    public HashMap viewTeam(Long teamId) throws BusinessException {
        HashMap map = new HashMap();
        V3xOrgTeam team = orgManager.getTeamById(teamId);
        ParamUtil.beanToMap(team, map, false);
        //组织公开范围
        List scopelist = orgManagerDirect.getTeamScope(team);
        if (scopelist.size() == 0) {
            map.put("scope", "2");
            map.put("scopein", "");
            map.put("scopein_txt", "");
        } else {
            map.put("scope", "1");
            map.put("scopein", OrgHelper.parseElements(scopelist, "id", "entityType"));
            map.put("scopein_txt", OrgHelper.showOrgEntities(scopelist, "id", "entityType", null));
        }
        //组所属的机构
        map.put("ownerId",
                OrgHelper.getSelectPeopleStr(orgManager.getUnitById(Long.valueOf(map.get("ownerId").toString()))));
        //组人员
        List teamLeader = orgManagerDirect.getTeamMembers(team, OrgConstants.TeamMemberType.Leader.name());
        List teamRelative = orgManagerDirect.getTeamMembers(team, OrgConstants.TeamMemberType.Relative.name());
        List teamSuperVisor = orgManagerDirect.getTeamMembers(team, OrgConstants.TeamMemberType.SuperVisor.name());
        String teamLeaderstr = OrgHelper.getSelectPeopleStr(teamLeader);
        String teamMemberstr = JSONUtil.toJSONString(orgManagerDirect.getTeamsMember(team, OrgConstants.TeamMemberType.Member.name()));
        String teamRelativestr = OrgHelper.getSelectPeopleStr(teamRelative);
        String teamSuperVisorstr = OrgHelper.getSelectPeopleStr(teamSuperVisor);
        map.put("teamLeader", teamLeaderstr);
        map.put("teamMember", teamMemberstr);
        map.put("teamRelative", teamRelativestr);
        map.put("teamSuperVisor", teamSuperVisorstr);

        return map;
    }

    @Override
    public FlipInfo showTeamList(FlipInfo fi, Map params)
            throws BusinessException {
    	User user = AppContext.getCurrentUser();
        Long currentUserId = AppContext.currentUserId();
        Long accountId = Long.valueOf(params.get("accountId").toString());

        Map<String, Object> param = new HashMap<String, Object>();
        List<OrgTeam> list = new ArrayList<OrgTeam>();
        if (params.size() == 1 || "scope".equals(params.get("condition"))) {
            list = orgDao.getAllTeamPO(accountId, null, null, param, null);
        } else {
        	String isMember  = null;
        	if(params.containsKey("value")){
        		isMember = params.get("value").toString();
        	}
        	String type = null;
        	if(params.containsKey("type")){
        		type = params.get("type").toString();
        	}
        	// 按人员名查询组
        	if(Strings.isNotEmpty(isMember)&&isMember.contains("Member|")&&"selectPeople".equals(type)){
        		String memberId = isMember.substring(isMember.indexOf("|")+1, isMember.length()-2);
        		param.put("Member", memberId);
        		list = orgDao.getAllTeamPO(accountId, null, null, param, null);
        	} else if(!params.containsValue("selectPeople")) {// 查询全部组；按组名、状态、权限属性查询组
        		param.put(String.valueOf(params.get("condition")), params.get("value"));
        		list = orgDao.getAllTeamPO(accountId, null, null, param, null);
        	}
        }
        List rellist = new ArrayList();

        boolean isHrAdmin = orgManager.isHRAdmin();
        boolean isAdministrator = user.isAdministrator();
        boolean isGroupAdmin = orgManager.isGroupAdminById(currentUserId);
        boolean isDeptAdmin = orgManager.isDepartmentAdmin();

        String deptIds="";
        if(!isHrAdmin && isDeptAdmin){
    		//如果只是是部门管理员,能看到 所属范围是 管理的所有的部门及其子部门下的组
    		List<V3xOrgDepartment> depts= orgManager.getDeptsByAdmin(currentUserId,accountId);
    		List<V3xOrgDepartment> allChildDepts = new UniqueList<V3xOrgDepartment>();
    		for(V3xOrgDepartment dept : depts){
    			if(allChildDepts.contains(dept)){
    				continue;
    			}
    			allChildDepts.add(dept);
    			allChildDepts.addAll(orgManager.getChildDepartments(dept.getId(), false));

    		}

    		for(V3xOrgDepartment adept : allChildDepts){
    			deptIds+=adept.getId()+",";
    		}
        }
        
        Set<Long> unitIds = new HashSet<Long>();
        List<OrgUnit> allOrgUnit = orgDao.getAllUnitPO(null, null, null, true, null, null, null);
        for(OrgUnit a : allOrgUnit) {
        	unitIds.add(a.getId());
        }

        for (OrgTeam object : list) {
            HashMap<String, String> m = new HashMap<String, String>();
            ParamUtil.beanToMap((OrgTeam)object, m, true);

            if(!String.valueOf(OrgConstants.TEAM_TYPE.SYSTEM.ordinal()).equals(String.valueOf(m.get("type")))){
                continue;//管理员只管理系统组
            }

            if(!unitIds.contains(Long.valueOf(String.valueOf(m.get("ownerId"))))) {
                continue;
            }
            V3xOrgUnit unit = orgManager.getUnitById(Long.valueOf(String.valueOf(m.get("ownerId"))));
            if(null == unit) {
                continue;
            }

            //根据查询条件过滤权限属性
            if(orgManager.isEmptyTeamScope((V3xOrgTeam)OrgHelper.poTobo(object))){
            	m.put("scope", "2");
            }else{
            	m.put("scope", "1");
            }
            if (params.size() > 1 && "scope".equals(params.get("condition"))
                    && !params.get("value").equals(m.get("scope"))) {
                continue;
            }

            if (!m.containsKey("ownerId") || "".equals(String.valueOf(m.get("ownerId")))
                    || orgManager.getUnitById(Long.valueOf(String.valueOf(m.get("ownerId")))) == null) {
                rellist.add(m);
                continue;
            }
            if (isGroupAdmin || isAdministrator || isHrAdmin) {
                if (!unit.getOrgAccountId().equals(accountId)) {
                    continue;
                }
            }else if(isDeptAdmin){
            	//如果只是部门管理员,能看到 所属范围是 管理的所有的部门及其子部门下的组
            	if(deptIds.indexOf(String.valueOf(m.get("ownerId")))<0){
            		continue;
            	}
            }
            else {
                String currentdeptid=String.valueOf(orgManager.getCurrentDepartment().getId());
                if (!String.valueOf(m.get("ownerId")).equals(currentdeptid)&&!String.valueOf(m.get("createrId")).equals(currentdeptid)) {
                    continue;
                }
            }

            rellist.add(m);
        }

        DBAgent.memoryPaging(rellist, fi);

        Map<Long,List<V3xOrgMember>> leaderMap = getTeamsLeader(accountId);

        List result = new ArrayList();
        for (Object m : fi.getData()) {
            V3xOrgTeam team = orgManager.getTeamById(Long.valueOf(((HashMap) m).get("id").toString()));
            if (orgManager.isEmptyTeamScope(team)) {
                ((HashMap) m).put("scope", "2");
            } else {
                ((HashMap) m).put("scope", "1");
            }
            List teamLeader = leaderMap.get(team.getId());

            if (teamLeader!=null && teamLeader.size() > 0) {
                String names = OrgHelper.showOrgEntities(teamLeader, "id", "entityType", null);
                ((HashMap) m).put("teamLeader", names);
            } else {
                ((HashMap) m).put("teamLeader", "");
            }
            result.add(m);
        }
        fi.setData(result);


        return fi;
    }

    private Map<String,Map<String,String>> getEntityMap(String typeAndIds) throws BusinessException {

    	String[] items = typeAndIds.split(V3xOrgEntity.ORG_ID_DELIMITER);

    	Map<String,Map<String,String>> map = new LinkedHashMap<String,Map<String,String>>(items.length);

        for (String item : items) {
        	Map<String,String> m = new HashMap<String,String>();

        	String[] data = item.split("[|]");

            if (data.length < 2) {
                throw new BusinessException("参数格式不正确 [" + item + "]; 正确的格式应该为 [Member|5129341885565]");
            }
            String type = data[0].toString();
            String id = data[1];
            String include = "0";
            if(data.length==3){
            	include = data[2];
            }else if(data.length==6){
            	include = data[5];
            }
            m.put("type", type);
            m.put("include", include);
            map.put(id, m);
        }

        return map;
    }

    private Map<Long,List<V3xOrgMember>> getTeamsLeader(Long accountId) throws BusinessException {
    	Map<Long,List<V3xOrgMember>> map = new HashMap<Long, List<V3xOrgMember>>();
    	EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
        enummap.put(OrgConstants.RelationshipObjectiveName.objective5Id, OrgConstants.TeamMemberType.Leader.name());
    	List<V3xOrgRelationship> rellist = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Team_Member, null, accountId, enummap);
    	for (V3xOrgRelationship orgRelationship : rellist) {
    		Long teamId = Long.valueOf(orgRelationship.getSourceId());
    		if(!map.containsKey(teamId)){
    			map.put(teamId, new UniqueList<V3xOrgMember>());
    		}
    		List<V3xOrgMember> memberList = map.get(teamId);
    		memberList.add(orgManager.getMemberById(orgRelationship.getObjective0Id()));
		}
    	return map;
    }

    @AjaxAccess
    @Override
    public boolean saveTeamMembers(String reamIds, String memberIds, String replaceMemberId, String type, String accId) throws BusinessException {
    	List<OrgRelationship> insertOrgRelationshipPOs = new ArrayList<OrgRelationship>();
    	List<OrgRelationship> deleteOrgRelationshipPOs = new ArrayList<OrgRelationship>();
    	String[] teamIdArr = reamIds.split(",");
    	String[] memberIdArr = memberIds.split(",");
    	OrgConstants.TeamMemberType[] types = new OrgConstants.TeamMemberType[4];
    	types[0] = OrgConstants.TeamMemberType.Leader;
    	types[1] = OrgConstants.TeamMemberType.Member;
    	types[2] = OrgConstants.TeamMemberType.SuperVisor;
    	types[3] = OrgConstants.TeamMemberType.Relative;
    	List<WebV3xOrgTeamResult> result = new ArrayList<WebV3xOrgTeamResult>();
    	Long webAccountId = Long.parseLong(accId);
    	User user = AppContext.getCurrentUser();
    	//国际化拼页面显示日志
        String add = ResourceUtil.getString("batchModify.operation.add");//新增人员
        String delete = ResourceUtil.getString("batchModify.operation.delete");// 删除人员
        String replace = ResourceUtil.getString("batchModify.operation.replace");// 替换人员
    	for(String tId : teamIdArr){
    		Long teamId = Long.valueOf(tId);
    		V3xOrgTeam team = orgManager.getTeamById(teamId);
    		Boolean enabled = team.getEnabled();
    		Long accountId = team.getOrgAccountId();
    		Set<Long> teamIds = new HashSet<Long>();
    		teamIds.add(teamId);
            List<V3xOrgMember> members = orgManager.getMembersByTeam(teamId,teamIds,types);
            Set<Long> hasMemberIds = getMemberIds(members);
            List<V3xOrgEntity> entitys = orgManager.getTeamMember(teamId);
            entitys.addAll(orgManager.getTeamMember(teamId,OrgConstants.TeamMemberType.Leader));
            entitys.addAll(orgManager.getTeamMember(teamId,OrgConstants.TeamMemberType.Member));
            entitys.addAll(orgManager.getTeamMember(teamId,OrgConstants.TeamMemberType.SuperVisor));
            entitys.addAll(orgManager.getTeamMember(teamId,OrgConstants.TeamMemberType.Relative));
            Set<Long> hasOnlyMemberIds = getOnlyMemberIds(entitys);
            String pass1and2 = "";// 判断为新增、删除跳过的条件
            String success1and2 = "";// 判断新增、删除成功的的条件
            Map<String, String> passnamesMap = new HashMap<String, String>();// 操作执行不成功拼接人员名称
            Map<String, String> successnamesMap = new HashMap<String, String>();// 操作执行成功拼接人员名称
            Map<String, String> success3and3Map = new HashMap<String, String>();// 替换成功人员名称
            Map<String, String> pass3andNotExistMap = new HashMap<String, String>();// 替换人员不存在
            Map<String, String> pass3andExistMap = new HashMap<String, String>();// 替换人员已存在
            for(String mId : memberIdArr){
            	Long memberId = Long.valueOf(mId);
            	V3xOrgMember ml = orgManager.getMemberById(memberId);
            	if("1".equals(type)){//新增
            		if(hasMemberIds.contains(memberId) && enabled){
            			//memberId 记日志  已经有此人
            			// 生成显示日志
            			pass1and2 = "pass";
            			if(!passnamesMap.containsKey("passnames1")){
            				passnamesMap.put("passnames1", ml.getName());
            			}else{
            				passnamesMap.put("passnames1", passnamesMap.get("passnames1")+"、"+ml.getName());
            			}
            			continue;
            		}
            		// 组状态为停用
            		if(!enabled){
            			pass1and2 = "pass";
            			if(!passnamesMap.containsKey("disabledteam")){
            				passnamesMap.put("disabledteam", ml.getName());
            			}else{
            				passnamesMap.put("disabledteam", passnamesMap.get("disabledteam")+"、"+ml.getName());
            			}
            			continue;
            		}
            		OrgRelationship or = new OrgRelationship();
            		or.setIdIfNew();
            		or.setType(OrgConstants.RelationshipType.Team_Member.name());
            		or.setSourceId(teamId);// 组id
            		or.setObjective0Id(memberId);// 人员id
            		or.setObjective5Id("Member");// 组中角色为人员
            		or.setObjective6Id("Member");
            		or.setSortId(0L);
            		or.setOrgAccountId(accountId);// 单位id
            		or.setCreateTime(new Date());
            		or.setUpdateTime(new Date());
            		insertOrgRelationshipPOs.add(or);
            		// 生成显示日志
            		success1and2 = "success";
        			if(!successnamesMap.containsKey("successnames1")){
        				successnamesMap.put("successnames1", ml.getName());
        			}else{
        				successnamesMap.put("successnames1", successnamesMap.get("successnames1")+"、"+ml.getName());
        			}
            	}else if("2".equals(type)){//删除
            		if(!hasOnlyMemberIds.contains(memberId) && enabled){
            			//memberId 记日志 找不到此人
            			// 生成显示日志
            			pass1and2 = "pass";
            			if(!passnamesMap.containsKey("passnames2")){
            				passnamesMap.put("passnames2", ml.getName());
            			}else{
            				passnamesMap.put("passnames2", passnamesMap.get("passnames2")+"、"+ml.getName());
            			}
            			continue;
            		}
            		if(!enabled){
            			// 生成显示日志
            			pass1and2 = "pass";
            			if(!passnamesMap.containsKey("disabledteam")){
            				passnamesMap.put("disabledteam", ml.getName());
            			}else{
            				passnamesMap.put("disabledteam", passnamesMap.get("disabledteam")+"、"+ml.getName());
            			}
            			continue;
            		}
                	EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
                    enummap.put(OrgConstants.RelationshipObjectiveName.objective0Id, memberId);
                    List<V3xOrgRelationship> rels = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Team_Member, teamId, accountId, enummap);
                    for(V3xOrgRelationship rel : rels){
	                	deleteOrgRelationshipPOs.add((OrgRelationship)rel.toPO());
                    }
                    // 生成显示日志
                    success1and2 = "success";
                    if(!successnamesMap.containsKey("successnames2")){
        				successnamesMap.put("successnames2", ml.getName());
        			}else{
        				successnamesMap.put("successnames2", successnamesMap.get("successnames2")+"、"+ml.getName());
        			}
            	}else if("3".equals(type) && Strings.isNotBlank(replaceMemberId)){//替换
            		V3xOrgMember mlr = orgManager.getMemberById(Long.parseLong(replaceMemberId));
            		String teamMemberType = "Member";
            		if(!hasOnlyMemberIds.contains(memberId) || hasOnlyMemberIds.size()==0){// 被替换人员不存在
            			if(enabled){
            				//memberId 记日志 找不到此人
            				pass3andNotExistMap.put("pass3andNotExist", ml.getName());
            				webLog(result, type, "pass3andNotExist", replaceMemberId, teamId, pass3andNotExistMap);//pass3andNotExist 判断为替换跳过的条件		 被替换人不存在当前组内
            			} else {
            				pass3andNotExistMap.put("disabledteam", ml.getName());
            				webLog(result, type, "pass", replaceMemberId, teamId, pass3andNotExistMap);
            			}
            		} else if(!hasOnlyMemberIds.contains(Long.valueOf(replaceMemberId))){// 只1  	被替换人员存在，删除被替换人员  ;滤过自己替换自己的情况
            			EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
            			enummap.put(OrgConstants.RelationshipObjectiveName.objective0Id, memberId);
            			List<V3xOrgRelationship> rels = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Team_Member, teamId, accountId, enummap);
            			for(V3xOrgRelationship rel : rels){
            				teamMemberType = rel.getObjective5Id();
            				deleteOrgRelationshipPOs.add((OrgRelationship)rel.toPO());
            			}
            		}
            		if(hasMemberIds.contains(Long.valueOf(replaceMemberId))){// 所选替换人员已经存在于组内
            			if(enabled){// 启用组
            				//memberId 记日志  已经有此人
                			pass3andExistMap.put("pass3andExist", mlr.getName());
                			webLog(result, type, "pass3andExist", replaceMemberId, teamId, pass3andExistMap);//pass3andExist:判断为替换跳过的条件	 所选人员已经存在当前组内
            			}else{// 停用组
            				pass3andExistMap.put("disabledteam", mlr.getName());
                			webLog(result, type, "disabledteam", replaceMemberId, teamId, pass3andExistMap);
            			}
            		}else if(hasOnlyMemberIds.contains(memberId) && !hasMemberIds.contains(Long.valueOf(replaceMemberId))){ // 被替换人存在，且所选替换人不存在， 添加所选替换人员
            			OrgRelationship or = new OrgRelationship();
            			or.setIdIfNew();
            			or.setType(OrgConstants.RelationshipType.Team_Member.name());
            			or.setSourceId(teamId);// 组id
            			or.setObjective0Id(Long.valueOf(replaceMemberId));// 人员id
            			or.setObjective5Id(teamMemberType);// 组中角色为人员
            			or.setObjective6Id("Member");
            			or.setSortId(0L);
            			or.setOrgAccountId(accountId);// 单位id
            			or.setCreateTime(new Date());
            			or.setUpdateTime(new Date());
            			insertOrgRelationshipPOs.add(or);
            			// 生成显示日志
            			success3and3Map.put("success3and3", ml.getName());
            			webLog(result, type, "success3and3", replaceMemberId, teamId, success3and3Map);//success3and3:判断为替换成功的条件		 所选人员不存在当前组内
            		}
            	}
            }
            // 记录页面显示日志
            if(Strings.isNotBlank(pass1and2)){
            	webLog(result, type, pass1and2, "", teamId, passnamesMap);
            }
            if(Strings.isNotBlank(success1and2)){
            	webLog(result, type, success1and2, "", teamId, successnamesMap);
            }
    	}
    	// 记录应用日志
    	if("1".equals(type)){
    		appLogManager.insertLog4Account(user, user.isGroupAdmin() ? webAccountId : AppContext.currentAccountId(), AppLogAction.Organization_TeamMGT_BatchModify, user.getName(), null, add);
    	}else if("2".equals(type)){
    		appLogManager.insertLog4Account(user, user.isGroupAdmin() ? webAccountId : AppContext.currentAccountId(), AppLogAction.Organization_TeamMGT_BatchModify, user.getName(), null, delete);
    	} else if("3".equals(type)){
    		appLogManager.insertLog4Account(user, user.isGroupAdmin() ? webAccountId : AppContext.currentAccountId(), AppLogAction.Organization_TeamMGT_BatchModify, user.getName(), null, replace);
    	}

    	orgDao.updateRelationships(insertOrgRelationshipPOs);
    	orgDao.deleteOrgRelationshipPOs(deleteOrgRelationshipPOs);
    	//处理结果加到session
    	AppContext.removeSessionArrribute("BatchteamMembers");
    	AppContext.putSessionContext("BatchteamMembers", result);
    	return true;
    }

	private Set<Long> getMemberIds(List<V3xOrgMember> members){
		Set<Long> memberIdSet = new HashSet<Long>();
		for(V3xOrgMember m : members){
			memberIdSet.add(m.getId());
		}
		return memberIdSet;
	}

	private Set<Long> getOnlyMemberIds(List<V3xOrgEntity> entities){
		Set<Long> memberIdSet = new HashSet<Long>();
		for(V3xOrgEntity entity : entities){
			if(OrgConstants.ORGENT_TYPE.Member.name().equals(entity.getEntityType())){
				memberIdSet.add(entity.getId());
			}
		}
		return memberIdSet;
	}

    @AjaxAccess
    @Override
	public FlipInfo getBatchResult(FlipInfo fi,Map params) throws BusinessException {
    	List<WebV3xOrgTeamResult> resultList = (List<WebV3xOrgTeamResult>) AppContext.getSessionContext("BatchteamMembers");
    	if(Strings.isNotEmpty(resultList)){
    		DBAgent.memoryPaging(resultList, fi);
    	}
    	return fi;
    }

    /**
     * 处理页面显示日志
     * @param result
     * @param type
     * @param oType
     * @param replaceMemberId
     * @param teamId
     * @param map
     * @return
     * @throws BusinessException
     */
    private List<WebV3xOrgTeamResult> webLog(List<WebV3xOrgTeamResult> result, String type,String oType, String replaceMemberId, Long teamId, Map<String, String> map) throws BusinessException{
    	WebV3xOrgTeamResult tm = new WebV3xOrgTeamResult();
    	//国际化拼页面显示日志
    	String add = ResourceUtil.getString("batchModify.operation.add");//新增人员
        String delete = ResourceUtil.getString("batchModify.operation.delete");// 删除人员
        String success = ResourceUtil.getString("batchModify.result.success");// 成功
        String pass = ResourceUtil.getString("batchModify.result.pass");// 跳过
        String successnames = "";
        String passnames = "";
        V3xOrgTeam team = orgManager.getTeamById(teamId);
        tm.setTeamname(team.getName());
        tm.setResult(success);
        if("1".equals(type)){//新增
			//memberId 记日志  已经有此人
			// 生成显示日志
        	if("pass".equals(oType) && map.containsKey("passnames1")){
        		tm.setResult(pass);
    			passnames = map.get("passnames1");
				tm.setOperation(add+ResourceUtil.getString("batchModify.operation.add.pass.exist",passnames));
				tm.setResult(pass);
				tm.setExplain(ResourceUtil.getString("batchModify.operation.result.pass.exit",passnames,pass));
				if(!result.contains(tm)){
					result.add(tm);
				}
        	}else if("pass".equals(oType) && map.containsKey("disabledteam")){
        		tm.setResult(pass);
    			passnames = map.get("disabledteam");
				tm.setOperation(ResourceUtil.getString("batchModify.operation.delete.operation",add,passnames));
				tm.setResult(pass);
				tm.setExplain(ResourceUtil.getString("batchModify.team.disabled",pass));
				if(!result.contains(tm)){
					result.add(tm);
				}
        	}else if("success".equals(oType) && map.containsKey("successnames1")){
        		successnames = map.get("successnames1");
				tm.setOperation(ResourceUtil.getString("batchModify.operation.delete.operation",add,successnames));
	    		tm.setExplain("");
	    		if(!result.contains(tm)){
	    			result.add(tm);
				}
        	}
        }else if("2".equals(type)){//删除
        	if("pass".equals(oType) && map.containsKey("passnames2")){
        		passnames = map.get("passnames2");
	        	tm.setResult(pass);
    			tm.setOperation(delete+passnames);
    			tm.setExplain(ResourceUtil.getString("batchModify.operation.result.pass",passnames,pass));
    			if(!result.contains(tm)){
    				result.add(tm);
    			}
        	}else if("pass".equals(oType) && map.containsKey("disabledteam")){
        		tm.setResult(pass);
    			passnames = map.get("disabledteam");
				tm.setOperation(ResourceUtil.getString("batchModify.operation.delete")+passnames);
				tm.setResult(pass);
				tm.setExplain(ResourceUtil.getString("batchModify.team.disabled",pass));
				if(!result.contains(tm)){
					result.add(tm);
				}
        	}else if("success".equals(oType) && map.containsKey("successnames2")){
    			tm.setOperation(ResourceUtil.getString("batchModify.operation.delete.operation",delete,map.get("successnames2")));
            	tm.setExplain("");
            	if(!result.contains(tm)){
    				result.add(tm);
    			}
        	}
        }else if("3".equals(type) && Strings.isNotBlank(replaceMemberId)){//替换
        	V3xOrgMember mlr = orgManager.getMemberById(Long.parseLong(replaceMemberId));
        	if("pass3andNotExist".equals(oType) && map.containsKey("pass3andNotExist")){
        		passnames = map.get("pass3andNotExist");
        		tm.setResult(pass);
    			tm.setOperation(ResourceUtil.getString("batchModify.thead.operation.replace",passnames,mlr.getName(),""));
    			tm.setExplain(ResourceUtil.getString("batchModify.operation.result.pass",passnames,pass));
    			if(!result.contains(tm)){
    				result.add(tm);
    			}
        	}else if("pass".equals(oType) && map.containsKey("disabledteam")){
        		tm.setResult(pass);
    			passnames = map.get("disabledteam");
    			tm.setOperation(ResourceUtil.getString("batchModify.thead.operation.replace",passnames,mlr.getName(),""));
				tm.setResult(pass);
				tm.setExplain(ResourceUtil.getString("batchModify.team.disabled",pass));
				if(!result.contains(tm)){
					result.add(tm);
				}
        	} else if("pass3andExist".equals(oType)){
        		tm.setResult(pass);
    			tm.setOperation(ResourceUtil.getString("batchModify.operation.add.pass.exist",mlr.getName()));
    			tm.setExplain(ResourceUtil.getString("batchModify.operation.replace.result.exist",mlr.getName(),pass));
    			if(!result.contains(tm)){
    				result.add(tm);
    			}
        	} else if("success3and3".equals(oType) && map.containsKey("success3and3")){
        		successnames = map.get("success3and3");
    			tm.setOperation(ResourceUtil.getString("batchModify.thead.operation.replace",successnames,mlr.getName(),""));
    			if(!result.contains(tm)){
    				result.add(tm);
    			}
        	}
        }
    	return result;
    }
}
