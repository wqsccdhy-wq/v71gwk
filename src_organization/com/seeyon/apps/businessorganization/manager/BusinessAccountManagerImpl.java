package com.seeyon.apps.businessorganization.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.CompareSortRelationship;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.BusinessOrgManagerDirect;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.po.OrgRelationship;
import com.seeyon.ctp.organization.po.OrgRole;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator,Role_NAME.BusinessOrganizationManager})
@ProcessInDataSource(name = DataSourceName.BASE)
public class BusinessAccountManagerImpl implements BusinessAccountManager {
    private final static Log        logger = LogFactory.getLog(BusinessAccountManagerImpl.class);
    private final static String CODE = "code";
    private final static String MESSAGE = "message";
    private final static String SUCCESS = "success";
    private final static String FAILURE = "failure";
    
    private BusinessOrgManagerDirect businessOrgManagerDirect;
    private OrgDao orgDao;
    private OrgManager orgManager;
    private OrgManagerDirect orgManagerDirect;
    private AppLogManager appLogManager;
    
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

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	@Override
	@AjaxAccess
    public String getBusinessAccountJson(Long userId) throws BusinessException{
		List<V3xOrgAccount> list = new UniqueList<V3xOrgAccount>();
		boolean isBusinessOrganizationManager = orgManager.isRole(userId, null, OrgConstants.Role_NAME.BusinessOrganizationManager.name());
		//集团管理员或单位管理员
		if(!isBusinessOrganizationManager){
			list = businessOrgManagerDirect.getAccountList(userId, null);
		}else{
			EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective1Id, OrgConstants.BUSINESS_ORGANIZATION_ROLE_ID);
			List<V3xOrgRelationship> myList = orgManager.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, userId, null, objectiveIds);
			for(V3xOrgRelationship rel : myList){
				V3xOrgAccount account = orgManager.getAccountById(rel.getOrgAccountId());
				if(account != null){
					list.add(account);
				}
			}
		}
		Collections.sort(list, CompareSortEntity.getInstance());
    	StringBuilder json = new StringBuilder();
    	json.append("[");
    	int i = 0;
    	for(V3xOrgAccount account : list){
    		Long accountId = account.getId();
    		String Id = account.getId().toString();
    		String name = account.getName().toString();
    		Long sortId = account.getSortId();
    		boolean isEnable = account.getEnabled();
    		//管理员
    		String manager = "";
			EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective0Id, accountId);
			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective1Id, OrgConstants.BUSINESS_ORGANIZATION_ROLE_ID);
    		List<V3xOrgRelationship> managerList = orgManager.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, null, accountId, objectiveIds);
    		Collections.sort(managerList, CompareSortRelationship.getInstance());
    		for(V3xOrgRelationship rel : managerList){
    			String type = rel.getObjective5Id();
    			Long id = rel.getSourceId();
    			V3xOrgEntity entity = orgManager.getEntity(type, id);
    			if(entity == null || !entity.isValid()){
    				continue;
    			}
    			if(Strings.isBlank(manager)){
    				manager = entity.getName();
    			}else{
    				manager = manager + "," + entity.getName();
    			}
    		}
    		//部门数
    		List<V3xOrgDepartment> deptlist = orgManagerDirect.getAllDepartments(account.getId(), true, null, null, null, null);
    		int deptCount = deptlist.size();
    		//人员数
    		List<V3xOrgMember> accountMembers = new UniqueList<V3xOrgMember>();
    		for(V3xOrgDepartment dept : deptlist){
    			accountMembers.addAll(businessOrgManagerDirect.getMembersByDepartment(dept.getId(), true));
    		}
    		int memberCount = accountMembers.size();
    		
 			if(i++ > 0){
 				json.append(",");
 			}
 			json.append("{");
 			json.append("\"I\":\""+Id+"\",");
 			json.append("\"N\":\""+Strings.escapeJavascript(name)+"\",");
 			json.append("\"M\":\""+Strings.escapeJavascript(manager)+"\",");
 			json.append("\"DC\":\""+deptCount+"\",");
 			json.append("\"MC\":\""+memberCount+"\",");	
 			json.append("\"EN\":\""+isEnable+"\",");	
 			json.append("\"S\":\""+sortId+"\"");	
 			json.append("}");
    	}
    	json.append("]");
    	
    	return json.toString();
    }
	
	@Override
	@AjaxAccess
	@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator})
	public Map createBusiness(Map map) throws BusinessException {
		Map<String,String> result  = new HashMap<String, String>();
		result.put(CODE, SUCCESS);
		V3xOrgAccount newAccount = new V3xOrgAccount();
	    ParamUtil.mapToBean(map, newAccount, false, false);
	    Long accountId = UUIDLong.longUUID();
	    newAccount.setId(accountId);
	    newAccount.setOrgAccountId(accountId);
	    newAccount.setCreaterId(AppContext.currentUserId());
	    newAccount.setSuperior(AppContext.currentAccountId());
	    newAccount.setExternalType(OrgConstants.ExternalType.Interconnect4.ordinal());
	    newAccount.setIsInternal(false);
	    
	    //公开范围
	    newAccount.setIsPublic(Boolean.valueOf(map.get("ispublic").toString()));
	    
	    //处理排序号方式
        if (OrgConstants.SORTID_TYPE_INSERT.equals((String) map.get("sortIdtype1"))) {
            newAccount.setSortIdType(OrgConstants.SORTID_TYPE_INSERT);
        } else if (OrgConstants.SORTID_TYPE_REPEAT.equals((String) map.get("sortIdtype2"))) {
            newAccount.setSortIdType(OrgConstants.SORTID_TYPE_REPEAT);
        }
        
        OrganizationMessage msg = businessOrgManagerDirect.addAccount(newAccount);
        if(!msg.isSuccess()){
        	result.put(CODE, FAILURE);
        	result.put(MESSAGE, msg.getErrorMsgInfos().get(0).getMsgInfo());
        	return result;
        }else{
        	//预置2个部门角色
        	 dealRole(accountId);
        	
        	//授权管理人员
        	dealAccessIds2Relationship(accountId,map.get("manager").toString());
        }
        appLogManager.insertLog(AppContext.getCurrentUser(), 848 , AppContext.getCurrentUser().getName(), newAccount.getName());
		return result;
	}
	
	/**
	 * 预置2个部门角色
	 */
	private void dealRole(Long orgAccountId){
        List<OrgRole> orgRolePO = new ArrayList<OrgRole>();
        //主管领导 - 部门主管
        V3xOrgRole role = new V3xOrgRole();
        role.setIdIfNew();
        role.setOrgAccountId(orgAccountId);
        role.setName(OrgConstants.Role_NAME.DepManager.name());
        role.setEnabled(true);
        role.setCode(OrgConstants.Role_NAME.DepManager.name());
        role.setCategory("0");
        role.setType(V3xOrgEntity.ROLETYPE_FIXROLE);//预制角色
        role.setIsBenchmark(false);
        role.setBond(OrgConstants.ROLE_BOND.DEPARTMENT.ordinal());
        role.setStatus(1);//默认显示在选人界面
        role.setExternalType(OrgConstants.ExternalType.Interconnect4.ordinal());//多组织角色
        role.setSortId(1L);

        OrgRole roleNew = (OrgRole) role.toPO();
        roleNew.setIdIfNew();
        orgRolePO.add(roleNew);

       //支部书记 - 分管领导
        V3xOrgRole role1 = new V3xOrgRole();
        role1.setIdIfNew();
        role1.setOrgAccountId(orgAccountId);
        role1.setName(OrgConstants.Role_NAME.DepLeader.name());
        role1.setEnabled(true);
        role1.setCode(OrgConstants.Role_NAME.DepLeader.name());
        role1.setCategory("0");
        role1.setType(V3xOrgEntity.ROLETYPE_FIXROLE);//预制角色
        role1.setIsBenchmark(false);
        role1.setBond(OrgConstants.ROLE_BOND.DEPARTMENT.ordinal());
        role1.setStatus(1);//默认显示在选人界面
        role1.setExternalType(OrgConstants.ExternalType.Interconnect4.ordinal());//多组织角色
        role1.setSortId(2L);

        OrgRole roleNew1 = (OrgRole) role1.toPO();
        roleNew1.setIdIfNew();
        orgRolePO.add(roleNew1);
		try {
			orgDao.insertOrgRole(orgRolePO);
		} catch (Exception e) {
			logger.error("处理多组织预置角色失败!",e);
		}
	}
	
	@Override
	@AjaxAccess
	public Map updateBusiness(Map map) throws BusinessException {
		Map<String,String> result  = new HashMap<String, String>();
		result.put(CODE, SUCCESS);
		Long accountId = Long.valueOf(map.get("id").toString());
		V3xOrgAccount account = orgManager.getAccountById(accountId);
	    ParamUtil.mapToBean(map, account, false, false);
	    //公开范围
	    account.setIsPublic(Boolean.valueOf(map.get("ispublic").toString()));
	    
	    //处理排序号方式
        if (OrgConstants.SORTID_TYPE_INSERT.equals((String) map.get("sortIdtype1"))) {
        	account.setSortIdType(OrgConstants.SORTID_TYPE_INSERT);
        } else if (OrgConstants.SORTID_TYPE_REPEAT.equals((String) map.get("sortIdtype2"))) {
        	account.setSortIdType(OrgConstants.SORTID_TYPE_REPEAT);
        }
        
        OrganizationMessage msg = businessOrgManagerDirect.updateAccount(account);
        if(!msg.isSuccess()){
        	result.put(CODE, FAILURE);
        	result.put(MESSAGE, msg.getErrorMsgInfos().get(0).getMsgInfo());
        	return result;
        }else{
        	dealAccessIds2Relationship(accountId,map.get("manager").toString());
        }
        appLogManager.insertLog(AppContext.getCurrentUser(), 849 , AppContext.getCurrentUser().getName(), account.getName());
		return result;
    }
	
	@Override
	@AjaxAccess
	@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator})
	public Map deleteBusiness(Long accountId) throws BusinessException {
		Map<String,String> result  = new HashMap<String, String>();
		result.put(CODE, SUCCESS);
		V3xOrgAccount account = orgManager.getAccountById(accountId);
		if(account == null){
			result.put(CODE, FAILURE);
			result.put(MESSAGE, ResourceUtil.getString("org.business.deletemsg1"));
			return result;
		}
		OrganizationMessage msg = businessOrgManagerDirect.deleteAccount(account);
		if(!msg.isSuccess()){
			result.put(CODE, FAILURE);
			result.put(MESSAGE, msg.getErrorMsgInfos().get(0).getMsgInfo());
			return result;
		}
		appLogManager.insertLog(AppContext.getCurrentUser(), 850 , AppContext.getCurrentUser().getName(), account.getName());
		return result;
    }
	
    private void dealAccessIds2Relationship(Long accountId, String accessTypeAndIds) {
    	try {
    		//先删除
    		EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective0Id, accountId);
			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective1Id, OrgConstants.BUSINESS_ORGANIZATION_ROLE_ID);
    		orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Role.name(), null, accountId, objectiveIds);
    		//后添加
    		List<OrgRelationship> accessScopePOs = new ArrayList<OrgRelationship>();
    		if(Strings.isNotBlank(accessTypeAndIds)){
    			String[] access = accessTypeAndIds.split(",");
    			Long i=0L;
    			for(String item : access){
    				String[] a = item.split("\\|");
    				String type = a[0];
    				String id = a[1];
    				V3xOrgRelationship accessManager = new V3xOrgRelationship();
    				accessManager.setKey(OrgConstants.RelationshipType.Member_Role.name());
    				accessManager.setSourceId(Long.valueOf(id));
    				accessManager.setObjective0Id(accountId);
    				accessManager.setObjective1Id(OrgConstants.BUSINESS_ORGANIZATION_ROLE_ID);
    				accessManager.setOrgAccountId(accountId);
    				accessManager.setObjective5Id(type);
    				accessManager.setSortId(i++);
    				accessScopePOs.add((OrgRelationship) accessManager.toPO());
    			}
    		}
    		if(Strings.isNotEmpty(accessScopePOs)){
    			orgDao.insertOrgRelationship(accessScopePOs);
    		}
		} catch (Exception e) {
			logger.error("处理组织管理授权失败！",e);
		}
    }
    
	@SuppressWarnings("unchecked")
	@Override
	@AjaxAccess
	public Map viewBusiness(Long accountId) throws BusinessException {
		Map result = new HashMap();
		V3xOrgAccount account = orgManager.getAccountById(accountId);
		if(account == null){
			return result;
		}
		result.put("id", account.getId());
		result.put("name", account.getName());
		result.put("shortName", account.getShortName());
		result.put("code", account.getCode());
		result.put("sortId", account.getSortId());
		result.put("ispublic", account.getIsPublic());
		result.put("enabled", account.getEnabled());
		result.put("description", account.getDescription());
		
		String manager = "";
		String managerName = "";
		
		EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
		objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective0Id, accountId);
		objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective1Id, OrgConstants.BUSINESS_ORGANIZATION_ROLE_ID);
		List<V3xOrgRelationship> list = orgManager.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, null, accountId, objectiveIds);
		Collections.sort(list, CompareSortRelationship.getInstance());
		for(V3xOrgRelationship rel : list){
			String type = rel.getObjective5Id();
			Long id = rel.getSourceId();
			V3xOrgEntity entity = orgManager.getEntity(type, id);
			if(entity == null || !entity.isValid()){
				continue;
			}
			if(Strings.isBlank(manager)){
				manager = type+"|"+id;
				//managerName = entity.getName();
			}else{
				manager = manager + "," + type+"|"+id;
				//managerName = managerName + "," + entity.getName();
			}
		}
		
		managerName = OrgHelper.showOrgEntities(manager, ",");
		
		result.put("manager", manager);
		result.put("managerName", managerName);
		return result;
	}
	
    @Override
    @AjaxAccess
    public Integer getMaxSortByCreaterId(Long createrId) throws BusinessException{
    	int maxSort = orgDao.getMaxSortId(V3xOrgAccount.class, null, OrgConstants.ExternalType.Interconnect4.ordinal(), createrId);
        return maxSort;
    }
	
}
