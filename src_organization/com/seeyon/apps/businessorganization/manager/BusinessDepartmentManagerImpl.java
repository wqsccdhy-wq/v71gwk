package com.seeyon.apps.businessorganization.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.RelationshipType;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.CompareSortRelationship;
import com.seeyon.ctp.organization.bo.EntityIdTypeDsBO;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.BusinessOrgManagerDirect;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.po.OrgRelationship;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.organization.util.OrgTree;
import com.seeyon.ctp.organization.util.OrgTreeNode;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgAccount;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgMember;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.ctp.util.json.JSONUtil;

@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator,Role_NAME.BusinessOrganizationManager})
@ProcessInDataSource(name = DataSourceName.BASE)
public class BusinessDepartmentManagerImpl implements BusinessDepartmentManager {
	private final static Log logger = LogFactory.getLog(BusinessDepartmentManagerImpl.class);
	protected OrgCache orgCache;
    private BusinessOrgManagerDirect businessOrgManagerDirect;
    private OrgDao orgDao;
    private OrgManager orgManager;
    protected PrincipalManager principalManager;
    protected AppLogManager       appLogManager;
    private OrgManagerDirect orgManagerDirect;
	
    public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}
	public void setBusinessOrgManagerDirect(
			BusinessOrgManagerDirect businessOrgManagerDirect) {
		this.businessOrgManagerDirect = businessOrgManagerDirect;
	}
	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	@Override
	@AjaxAccess
	public HashMap addDept(String accountId) throws Exception {
		Integer maxSortNum = orgDao.getMaxSortId(V3xOrgAccount.class, Long.valueOf(accountId), OrgConstants.ExternalType.Interconnect4.ordinal());
		HashMap m = new HashMap();
		m.put("sortId", maxSortNum+1);
		return m;
	}
	
	@Override
	@AjaxAccess
	public HashMap getDepRoles(String accountId) throws Exception {
		List<V3xOrgRole> deproles = orgManager.getAllDepRoles(Long.valueOf(accountId));
		HashMap m = new HashMap();
		m.put("deproles", deproles);
		return m;
	}

	@SuppressWarnings("unchecked")
    @Override
    @AjaxAccess
	public Object createDept(String accountId, Map dept) throws Exception {
		Long accId = Long.parseLong(accountId); 
		V3xOrgAccount account = orgManager.getAccountById(accId);
		V3xOrgDepartment newdept = new V3xOrgDepartment();
		String parentdeptId = String.valueOf(dept.get("superDepartment"));
		if(parentdeptId!=null&&parentdeptId.contains("|")){
			parentdeptId = parentdeptId.split("\\|")[1];
		}else{
			parentdeptId = String.valueOf(accId);
		}

		ParamUtil.mapToBean(dept, newdept, false);
		newdept.setOrgAccountId(accId);
		newdept.setIsInternal(false);
		newdept.setExternalType(OrgConstants.ExternalType.Interconnect4.ordinal());
		newdept.setSuperior(Long.valueOf(parentdeptId));
		OrganizationMessage mes = null;
        if (newdept.getId() == null) {
        	newdept.setIdIfNew();
            //部门排序号的重复处理 新建的部门直接插入
            String isInsert = dept.get("sortIdtype").toString();
            boolean isDuplicated = orgDao.isPropertyDuplicated(V3xOrgDepartment.class, "sortId", newdept.getSortId(),accId,newdept.getId(),OrgConstants.ExternalType.Interconnect4.ordinal());
            if ("1".equals(isInsert) && isDuplicated) {
            	orgDao.insertRepeatSortNum(V3xOrgDepartment.class, accId , newdept.getSortId(), false, OrgConstants.ExternalType.Interconnect4.ordinal());
            }
            
            mes = businessOrgManagerDirect.addDepartment(newdept);
            Map result = OrgHelper.getBusinessExceptionMessage(mes);
            if("false".equals(result.get(OrganizationMessage.MessageStatus.SUCCESS.name()))){
            	return result;
            }
            appLogManager.insertLog(AppContext.getCurrentUser(), 876, AppContext.getCurrentUser().getName(), account.getName(), newdept.getName());
        } else {
            String isInsert = dept.get("sortIdtype").toString();
            boolean isDuplicated = orgDao.isPropertyDuplicated(V3xOrgDepartment.class, "sortId", newdept.getSortId(),accId,newdept.getId(),OrgConstants.ExternalType.Interconnect4.ordinal());
            if ("1".equals(isInsert) && isDuplicated) {
            	orgDao.insertRepeatSortNum(V3xOrgDepartment.class, accId , newdept.getSortId(), false, OrgConstants.ExternalType.Interconnect4.ordinal());
            }
            
            mes = businessOrgManagerDirect.updateDepartment(newdept);
            Map result = OrgHelper.getBusinessExceptionMessage(mes);
            if("false".equals(result.get(OrganizationMessage.MessageStatus.SUCCESS.name()))){
            	return result;
            }
            appLogManager.insertLog(AppContext.getCurrentUser(), 877 , AppContext.getCurrentUser().getName(), account.getName(), newdept.getName());
        }
        
        
        //处理部门成员
        String deptMember = dept.get("deptMember").toString();
        dealDepartmentMember2Relationship(newdept, deptMember);
        
        //处理部门角色
        List<V3xOrgRole> rolelist = orgManager.getAllDepRoles(accId);
        dealDeptRole(dept, newdept, rolelist);
        
		return newdept.getId();
	}
	
	
	@SuppressWarnings("unchecked")
    @Override
    @AjaxAccess
	public void saveDeptMembers(Long deptId, String deptMember) throws Exception {
		V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
		if(dept != null){
			dealDepartmentMember2Relationship(dept,deptMember);
		}
		orgCache.updateModifiedTimeStamp();
	}
	
	
    private void dealDepartmentMember2Relationship(V3xOrgDepartment dept, String deptMember) {
    	try {
    		//先删除
    		orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.BusinessDepartment_Member.name(), dept.getId(), null, null);
    		//后添加
    		List<OrgRelationship> relPoList = new ArrayList<OrgRelationship>();
    		if(Strings.isNotBlank(deptMember)){
    			String[] dm = deptMember.split(",");
    			Long i=0L;
    			for(String item : dm){
    				String[] a = item.split("\\|");
    				String type = a[0];
    				String id = a[1];
    				String excludeSubDept = "0";
    				if(a.length >= 3){
    					excludeSubDept = a[2];
    				}
    				V3xOrgRelationship deptMemberRel = new V3xOrgRelationship();
    				deptMemberRel.setKey(OrgConstants.RelationshipType.BusinessDepartment_Member.name());
    				deptMemberRel.setSourceId(dept.getId());
    				deptMemberRel.setObjective0Id(Long.valueOf(id));
    				deptMemberRel.setOrgAccountId(dept.getOrgAccountId());
    				deptMemberRel.setObjective5Id(type);
    				deptMemberRel.setObjective7Id(excludeSubDept);//是否包含子部门
    				deptMemberRel.setSortId(i++);
    				relPoList.add((OrgRelationship) deptMemberRel.toPO());
    			}
    		}
    		if(Strings.isNotEmpty(relPoList)){
    			orgDao.insertOrgRelationship(relPoList);
    		}
		} catch (Exception e) {
			logger.error("处理分部人员失败！",e);
		}
    }
	
    @Override
    @AjaxAccess
    public void dealDeptRole(Map map, V3xOrgDepartment dept, List<V3xOrgRole> rolelist) throws BusinessException {
    	try {
    		List<V3xOrgRelationship> deleteRelationShips = new UniqueList<V3xOrgRelationship>();
    		List<V3xOrgRelationship> addRelationShips = new UniqueList<V3xOrgRelationship>();
    		//添加或更新部门角色人员
    		for (int i = 0; i < rolelist.size(); i++) {
    			V3xOrgRole role = rolelist.get(i);
    			
    			List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
    			String membersstr = null;
    			if (map.containsKey("deptrole" + i) && map.get("deptrole" + i) != null
    					&& !"".equals(map.get("deptrole" + i))) {
    				membersstr = (map.get("deptrole" + i).toString());
    			}
    			if (membersstr != null) {
    				members = orgManagerDirect.getmembersByEntity(membersstr);
    			}
    			//如果这个部门某个角色人员没有发生任何变化，则对角色信息不做任何操作，直接跳过。提高性能
    			List<V3xOrgMember> membersByRole = orgManager.getMembersByRole(dept.getId(), role.getId());
    			if(Strings.isNotEmpty(members) && Strings.isNotEmpty(membersByRole)){
    				boolean isEquals = false;
    				if(members.size()==membersByRole.size()){
    					for(int j=0;j<members.size();j++){
    						if(!members.get(j).getId().equals(membersByRole.get(j).getId())){
    							break;
    						}
    						if(j+1 == members.size()){
    							isEquals = true;
    						}
    					}
    				}
    				if(isEquals){
    					continue;
    				}
    			}else if(Strings.isEmpty(members) && Strings.isEmpty(membersByRole)){
    				continue;
    			}
    			
    			//先删
    			EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
    			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective0Id, dept.getId());
    			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective1Id, role.getId());
    			deleteRelationShips.addAll(orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, (Long)null, (Long)null, objectiveIds));
    			
    			//后加
    			int index = 0;
    			for (V3xOrgMember member : members) {
    				V3xOrgRelationship relBO = new V3xOrgRelationship();
    				relBO.setId(UUIDLong.longUUID());
    				relBO.setKey(OrgConstants.RelationshipType.Member_Role.name());
    				relBO.setSourceId(member.getId());
    				relBO.setObjective0Id(dept.getId());
    				relBO.setObjective1Id(role.getId());
    				relBO.setObjective5Id(OrgConstants.ORGENT_TYPE.Member.name());//保存Member/Department/Post/Level等
    				relBO.setOrgAccountId(dept.getOrgAccountId());
    				relBO.setSortId(Long.valueOf(index++));
    				addRelationShips.add(relBO);
    			}
    		}
    		
    		orgManagerDirect.deleteOrgRelationships(deleteRelationShips);
    		orgManagerDirect.addOrgRelationships(addRelationShips);
		} catch (Exception e) {
			logger.error("处理部门角色失败!",e);
		}
    }

    @Override
    @AjaxAccess
    public Object deleteDept(Map dept) throws Exception {
        V3xOrgDepartment deldept = orgManager.getDepartmentById(Long.valueOf(dept.get("id").toString()));
        OrganizationMessage mes = businessOrgManagerDirect.deleteDepartment(deldept);
        Map result = OrgHelper.getBusinessExceptionMessage(mes);
        if("false".equals(result.get(OrganizationMessage.MessageStatus.SUCCESS.name()))){
        	return result;
        }
        V3xOrgAccount account = orgManager.getAccountById(deldept.getOrgAccountId());
        appLogManager.insertLog(AppContext.getCurrentUser(), 878 , AppContext.getCurrentUser().getName(), account.getName(),deldept.getName());
        return "";
    }

    @Override
    @AjaxAccess
    public Object deleteDepts(List<Map<String, Object>> depts) throws Exception {
        List<V3xOrgDepartment> deptlist = new ArrayList<V3xOrgDepartment>();
        List<OrgUnit> mapsToBeans = ParamUtil.mapsToBeans(depts, OrgUnit.class, false);
        
        for (OrgUnit orgUnit : mapsToBeans) {
        	deptlist.add((V3xOrgDepartment)OrgHelper.poTobo(orgUnit));
		}
        OrganizationMessage mes = businessOrgManagerDirect.deleteDepartments(deptlist);
        
        Map result = OrgHelper.getBusinessExceptionMessage(mes);
        if("false".equals(result.get(OrganizationMessage.MessageStatus.SUCCESS.name()))){
        	return result;
        }
        V3xOrgAccount account = orgManager.getAccountById(deptlist.get(0).getOrgAccountId());
        for (V3xOrgDepartment orgUnit : deptlist) {
        	appLogManager.insertLog(AppContext.getCurrentUser(), 878 , AppContext.getCurrentUser().getName(), account.getName(), orgUnit.getName());
        }
        return "";
    }

    @SuppressWarnings("unchecked")
	@Override
	@AjaxAccess
    public HashMap viewDept(Long deptId) throws Exception {
        HashMap map = new HashMap();
        V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
        ParamUtil.beanToMap(dept, map, false);
        String json = OrgHelper.getSelectPeopleStr(orgManager.getParentUnit(dept));
        Map entityMap = (Map)JSONUtil.parseJSONString(json);
        map.put("superDepartment", entityMap.get("value"));
        String superDepartment_txt = entityMap.get("text").toString();
        superDepartment_txt = superDepartment_txt.replace(dept.getPreName(), "");
        map.put("superDepartment_txt", superDepartment_txt);

		Map deptMemberElements = getDeptMemberElements(deptId);
		map.put("deptMember", deptMemberElements.get("deptMember"));
		map.put("deptMember_txt", deptMemberElements.get("deptMember_txt"));
		
        //显示部门角色
        List<V3xOrgRole> rolelist = orgManager.getAllDepRoles(dept.getOrgAccountId());
        for (int i = 0; i < rolelist.size(); i++) {
            List memberlist = orgManager.getMembersByRole(deptId, rolelist.get(i).getId());
            map.put("deptrole" + i, OrgHelper.getSelectPeopleStr(memberlist));
        }

        return map;
    }
    
    @SuppressWarnings("unchecked")
	@Override
	@AjaxAccess
    public HashMap deptMemberElements(Long deptId) throws Exception {
    	HashMap map = new HashMap();
		Map deptMemberElements = getDeptMemberElements(deptId);
		map.put("deptMember", deptMemberElements.get("deptMember"));
		map.put("deptMember_txt", deptMemberElements.get("deptMember_txt"));
		return  map;
    }
    
    private Map<String,String> getDeptMemberElements(Long deptId) throws BusinessException{
    	V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
    	if(dept == null){
    		return null;
    	}
    	Map<String,String> result = new HashMap<String, String>();
		List<V3xOrgRelationship> list = orgManager.getV3xOrgRelationship(RelationshipType.BusinessDepartment_Member, dept.getId(), dept.getOrgAccountId(),null);
		Collections.sort(list, CompareSortRelationship.getInstance());
		List<EntityIdTypeDsBO> entityList = new ArrayList<EntityIdTypeDsBO>();
		for(V3xOrgRelationship rel : list){
			String type = rel.getObjective5Id();
			Long id = rel.getObjective0Id();
			V3xOrgEntity entity = orgManager.getEntity(type, id);
			if(entity == null || !entity.isValid()){
				continue;
			}
			EntityIdTypeDsBO en = new EntityIdTypeDsBO();
			en.setId(entity.getId());
			en.setEntity(entity);
			en.setDsc(rel.getObjective5Id());
			en.setDscType(rel.getObjective7Id());
			entityList.add(en);
		}
		
		result.put("deptMember", OrgHelper.parseElementsExt(entityList, "id", "dsc","dscType"));
		result.put("deptMember_txt", OrgHelper.showOrgEntitiesExt(entityList, "id", "dsc","dscType", null));
		
		return result;
    }
    
	@Override
	@AjaxAccess
	public List showDepartmentTree(Map params) throws Exception {
		List resultlist = new ArrayList();
	    Long accId = Long.parseLong(params.get("accountId").toString());
		V3xOrgAccount account = orgManager.getAccountById(accId);

		List<V3xOrgDepartment> deptlist = orgManagerDirect.getAllDepartments(account.getId(), true, null, null, null, null);
		
        OrgTree orgTree = OrgHelper.getTree(deptlist, accId);
        OrgTreeNode accountNode = orgTree.getRoot();
		
		Collections.sort(deptlist, CompareSortEntity.getInstance());
		for (int i = 0; i < deptlist.size(); i++) {
			V3xOrgDepartment dept = (V3xOrgDepartment) deptlist.get(i);
			if (null == dept.getSuperior() || dept.getSuperior() == -1) continue;//防护
			
			WebV3xOrgDepartment webdept = new WebV3xOrgDepartment(dept.getId(),dept.getName(), dept.getSuperior());
			webdept.setV3xOrgDepartment(dept);
			
			webdept.setIconSkin("department");
            List<Long> childrenDepts = this.orgCache.getSubDeptList(dept.getId(),null);
            if(Strings.isNotEmpty(childrenDepts)){
            	webdept.setIconSkin("treeDepartment");
            }
			OrgTreeNode deptNode= OrgTree.getOrgTreeNodeById(accountNode, dept.getPath());
			if(!deptNode.isLeaf()) {
			    webdept.setIconSkin("treeDepartment");
			}
			resultlist.add(webdept);
		}

		WebV3xOrgAccount webrootaccount = new WebV3xOrgAccount(account.getId(),
				account.getName(), account.getId());
		webrootaccount.setIconSkin("treeAccount");
		resultlist.add(webrootaccount);
		return resultlist;
	}
	
	@AjaxAccess
	@Override
	public FlipInfo deptMembers(FlipInfo fi, Map params) throws Exception {
		Long depId = Long.parseLong(params.get("deptId").toString());
		V3xOrgDepartment dept = orgManager.getDepartmentById(depId);
		boolean searchName = false;
		Object name = params.get("name");
		if(name != null && Strings.isNotBlank(name.toString())){
			searchName = true;
		}
		List<V3xOrgMember> memberList = businessOrgManagerDirect.getMembersByDepartment(dept.getId(), true);
		
		List<WebV3xOrgMember> result = new ArrayList<WebV3xOrgMember>();
		for(V3xOrgMember m : memberList){
			WebV3xOrgMember o = new WebV3xOrgMember();
			if(searchName && m.getName().indexOf(name.toString())==-1){
				continue;
			}
			o.setName(m.getName());
			o.setBusinessRoleName("");
			o.setAccountName(orgManager.getAccountById(m.getOrgAccountId()).getName());
			
			String deptName = "";
			V3xOrgDepartment department = orgManager.getDepartmentById(m.getOrgDepartmentId());
			if(department != null) {
				deptName = department.getWholeName();
			}
			o.setDepartmentFullName(deptName);
			
			String postName = "";
			V3xOrgPost post = orgManager.getPostById(m.getOrgPostId());
			if(post != null) {
				postName = post.getName();
			}
			o.setPostName(postName);
			result.add(o);
		}
		DBAgent.memoryPaging(result, fi);
		return fi;
	}
	
	@Override
	@AjaxAccess
	public FlipInfo showDepList(FlipInfo fi, Map params)throws BusinessException {
	    Long accountId = Long.parseLong(params.get("accountId").toString());
		if(params.size()==1||params.get("value")==null||"".equals(params.get("value"))||"[, ]".equals(params.get("value").toString())||"[\"\",\"\"]".equals(params.get("value").toString())){
			orgManagerDirect.getAllDepartments(accountId, null, null, null, null, fi);			
		}else{
			orgManagerDirect.getAllDepartments(accountId, null, null,String.valueOf(params.get("condition")), getSelectPeopleSerchStr(params), fi);
		}
		
		List list = fi.getData();
		List rellist = new ArrayList();
		for (Object object : list) {
			HashMap m = new HashMap();
			OrgUnit dept = (OrgUnit)object;
			ParamUtil.beanToMap(dept, m, true);
			V3xOrgUnit punit = orgManager.getParentUnit(orgManager.getDepartmentById(Long.valueOf(m.get("id").toString())));
			if(punit!=null){
				m.put("superDepartment", punit.getName());
			}else{
				m.put("superDepartment", "");
			}
			m.put("DepManager", "");
			m.put("DepLeader", "");
			EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<OrgConstants.RelationshipObjectiveName, Object>(
                    RelationshipObjectiveName.class);
            objectiveIds.put(RelationshipObjectiveName.objective0Id,dept.getId());
            objectiveIds.put(RelationshipObjectiveName.objective5Id,OrgConstants.ORGENT_TYPE.Member.name());
			List<V3xOrgRelationship> reflist = orgManager.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, null, null, objectiveIds);
			for (V3xOrgRelationship v3xOrgRelationship : reflist) {
				if(orgManager.getRoleById(v3xOrgRelationship.getObjective1Id()).getName().equals(OrgConstants.Role_NAME.DepManager.name())){
					V3xOrgMember mem = orgManager.getMemberById(v3xOrgRelationship.getSourceId());
					if(mem!=null && mem.isValid()){
						m.put("DepManager", m.get("DepManager")+mem.getName()+",");
					}
				}
				
				if(orgManager.getRoleById(v3xOrgRelationship.getObjective1Id()).getName().equals(OrgConstants.Role_NAME.DepLeader.name())){
					V3xOrgMember mem = orgManager.getMemberById(v3xOrgRelationship.getSourceId());
					if(mem!=null && mem.isValid()){
						m.put("DepLeader", m.get("DepLeader")+mem.getName()+",");
					}
				}
			}
			if(String.valueOf(m.get("DepManager")).lastIndexOf(",")!=-1&&String.valueOf(m.get("DepManager")).lastIndexOf(",")==String.valueOf(m.get("DepManager")).length()-1){
				m.put("DepManager", String.valueOf(m.get("DepManager")).substring(0, String.valueOf(m.get("DepManager")).length()-1));
			}
			
			if(String.valueOf(m.get("DepLeader")).lastIndexOf(",")!=-1&&String.valueOf(m.get("DepLeader")).lastIndexOf(",")==String.valueOf(m.get("DepLeader")).length()-1){
				m.put("DepLeader", String.valueOf(m.get("DepLeader")).substring(0, String.valueOf(m.get("DepLeader")).length()-1));
			}
			rellist.add(m);
			
		}
		fi.setData(rellist);
		return fi;
	}
	
    private Object getSelectPeopleSerchStr(Map params) throws BusinessException{
        String condition = String.valueOf(params.get("condition"));
        if("path".equals(condition)) {//这种情况是从选人界面选过来的
        	List<String> strs = (List<String>) params.get("value");
            return ((V3xOrgUnit)orgManager.getEntities(strs.get(1).trim()).get(0)).getPath()+"0";
        } else {
            return String.valueOf(params.get("value"));
        }
    }
    
}
