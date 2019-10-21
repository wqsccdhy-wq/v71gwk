/**
 * $Author$
 * $Rev$
 * $Date::                     $:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.organization.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.addressbook.manager.AddressBookManager;
import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.util.LdapUtils;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.init.MclclzUtil;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.CompareUnitPath;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.manager.BusinessOrgManagerDirect;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.ctp.util.json.JSONUtil;

/**
 * <p>Title: T2组织模型单位维护控制器</p>
 * <p>Description: 主要针对单位组织进行维护功能</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * @since CTP2.0
 * @author lilong
 */
public class AccountController extends BaseController {

    protected OrgManager       orgManager;
    protected OrgManagerDirect orgManagerDirect;
    private AddressBookManager         addressBookManager;
    private BusinessOrgManagerDirect businessOrgManagerDirect;
    private static final Class<?> c2 = MclclzUtil.ioiekc("com.seeyon.ctp.product.ProductInfo");

    public OrgManager getOrgManager() {
        return orgManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public OrgManagerDirect getOrgManagerDirect() {
        return orgManagerDirect;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }

	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}

	public void setBusinessOrgManagerDirect(BusinessOrgManagerDirect businessOrgManagerDirect) {
		this.businessOrgManagerDirect = businessOrgManagerDirect;
	}

	/******************************/

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return null;
    }

    /**
     * 单位展现，左侧树右侧列表
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.HrAdmin,Role_NAME.AccountAdministrator})
    public ModelAndView listAccounts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = AppContext.getCurrentUser();
        Long accountId = AppContext.currentAccountId();
        V3xOrgAccount account = orgManager.getAccountById(accountId);
        //单位管理员直接跳转查看单独维护单位信息的页面去
        if ((orgManager.isRole(user.getId(), accountId, OrgConstants.Role_NAME.AccountAdministrator.name()) 
        		|| orgManager.isRole(user.getId(), accountId, OrgConstants.Role_NAME.HrAdmin.name()))
        		&& !account.allowManagementSubunit()) {
            return viewAccount(request, response);
        }

        ModelAndView result = new ModelAndView("apps/organization/account/account");
        result.addObject("isLdapEnabled", LdapUtils.isLdapEnabled());
        result.addObject("LdapCanOauserLogon", LDAPConfig.getInstance().getLdapCanOauserLogon());
        String MxVersion = SystemEnvironment.getMxVersion();
        result.addObject("MxVersion", MxVersion);
        result.addObject("managementSubunit", false);
        if((user.isAdministrator() || orgManager.isRole(user.getId(), accountId, OrgConstants.Role_NAME.HrAdmin.name())) &&
        		account.allowManagementSubunit()){
        	result.addObject("managementSubunit", true);
        }
        
        result.addObject("currentAccountId", accountId);
        result.addObject("parentAccountId", account.getSuperior());
        
        return result;
    }
    
    /**
     * 加载子单位树
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.HrAdmin,Role_NAME.AccountAdministrator})
    public ModelAndView showAccountTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView result = new ModelAndView("addressbook/public/addressbookOrgStructure_tree");
    	
    	User user = AppContext.getCurrentUser();
    	Long accountId = AppContext.currentAccountId();
        V3xOrgAccount account = orgManager.getAccountById(accountId);
        
    	List<Map<String,Object>> treeList = new ArrayList<Map<String, Object>>();
    	List<V3xOrgAccount> orgAccounts = orgManagerDirect.getAllAccounts(null, true, null, null, null);
     	List<V3xOrgAccount> list = new UniqueList<V3xOrgAccount>();
     	String currentPath = account.getPath();
     	List<V3xOrgAccount> accessAccount0 = orgManager.accessableAccounts(user.getId(),true);
     	for(V3xOrgAccount a : orgAccounts){
     		if(a.getPath().startsWith(currentPath) && accessAccount0.contains(a)){
     			list.add(a);
     		}
     	}
  		Map<Long,Set<Long>> childrenMap = new HashMap<Long, Set<Long>>();
  		for(V3xOrgAccount a : list){
  			Set<Long> set = childrenMap.get(a.getSuperior());
  			if(set == null){
  				set = new HashSet<Long>();
  				childrenMap.put(a.getSuperior(),set);
  			}
  			set.add(a.getId());
  		}
  		
  		Collections.sort(list, CompareSortEntity.getInstance());
  		
  		int maxLevels = 4;//最多允许一次性展示到5级，即到四级子单位
  		int levels = 0;//有多少层级
  		for(V3xOrgAccount a : list){
  			Map<String,Object> tempAccount = new HashMap<String, Object>();
  			tempAccount.put("id",a.getId().toString());
  			if(a.getId().equals(accountId)){
  				continue;
  			}else if(!childrenMap.containsKey(a.getSuperior())){
  				tempAccount.put("pId",accountId);
  			}else{
  				tempAccount.put("pId",a.getSuperior().toString());
  			}
  			tempAccount.put("name",a.getName());
  			Set<Long> children = childrenMap.get(a.getId());
  			tempAccount.put("hasChildren",Strings.isEmpty(children) ? false : true);
  			tempAccount.put("type",a.getEntityType());
  			tempAccount.put("num",0);
  			tempAccount.put("level",-1);
  			
            int currentLevel = (a.getPath().length() - account.getPath().length()) / 4;
            if(currentLevel>levels){
            	levels = currentLevel;
            }
            
  			treeList.add(tempAccount);
  		}
          result.addObject("treeList",JSONUtil.toJSONString(treeList));
          
          Map<String,Object> rootAccountObj = new HashMap<String, Object>();
          rootAccountObj.put("id",account.getId().toString());
          rootAccountObj.put("name",account.getName());
          rootAccountObj.put("type",account.getEntityType());
          result.addObject("rootAccount",JSONUtil.toJSONString(rootAccountObj));
          result.addObject("levels",levels > maxLevels ? maxLevels : levels);
          
          //部门分管领导
          String depLeaderRoleName = ResourceUtil.getString("sys.role.rolename.DepLeader");
          List<V3xOrgRole> depLeaders = orgManager.getRoleByCode("DepLeader", accountId);
          if(Strings.isNotEmpty(depLeaders)) {
          	depLeaderRoleName = depLeaders.get(0).getShowName();
          }
          
          //部门主管
          String depManagerRoleName = ResourceUtil.getString("sys.role.rolename.DepManager");
          List<V3xOrgRole> depManagers = orgManager.getRoleByCode("DepManager", accountId);
          if(Strings.isNotEmpty(depManagers)) {
          	depManagerRoleName = depManagers.get(0).getShowName();
          }
          result.addObject("depLeaderRoleName",depLeaderRoleName);
          result.addObject("depManagerRoleName",depManagerRoleName);
          
    	return result;
    }

    /**
     * 单位管理员和HR管理员的单位信息管理界面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes = { Role_NAME.AccountAdministrator,Role_NAME.HrAdmin})
    public ModelAndView viewAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //如果是集团管理员直接跳转到机构管理的页面
        User user = AppContext.getCurrentUser();
        Long accountId = AppContext.currentAccountId();
        V3xOrgAccount account = orgManager.getAccountById(accountId);
        if (orgManager.isRole(user.getId(), accountId, OrgConstants.Role_NAME.GroupAdmin.name()) || account.allowManagementSubunit()) {
            return listAccounts(request, response);
        }

        ModelAndView result = new ModelAndView("apps/organization/account/account4Admin");
        result.addObject("isLdapEnabled", LdapUtils.isLdapEnabled());
        result.addObject("LdapCanOauserLogon", LDAPConfig.getInstance().getLdapCanOauserLogon());
        result.addObject("id", accountId);
        String MxVersion = SystemEnvironment.getMxVersion();
        result.addObject("MxVersion", MxVersion);
        
        return result;
    }
    
    
    /**
     * 7.0 集团后台菜单组织树显示
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	 ModelAndView mav = new ModelAndView("apps/organization/account/orgTree");
    	 Long currentAccount = AppContext.currentAccountId();
    	 V3xOrgAccount account = orgManager.getAccountById(currentAccount);
    	 
    	 mav.addObject("loginAccount", currentAccount);
    	 mav.addObject("loginAccountName", account.getShortName());
    	 return mav;
    }
    
    /**
     * 7.0 集团后台菜单组织树显示
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showOrgTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("addressbook/public/addressbookOrgStructure_tree");
        User user = AppContext.getCurrentUser();
        boolean groupAdmin = user.isGroupAdmin();
        
        Long rootId = request.getParameter("currentNodeId") == null ? AppContext.currentAccountId() : Long.valueOf(request.getParameter("currentNodeId").toString());
        String from = "admin";
        if("detail".equals(request.getParameter("from"))){
        	from = "detail";//通讯录穿透进来的。
        }
        V3xOrgUnit rootUnit = orgManager.getUnitById(rootId);
        boolean fromDept = (OrgConstants.ORGENT_TYPE.Department.name().equals(rootUnit.getEntityType()) || OrgConstants.ORGENT_TYPE.BusinessDepartment.name().equals(rootUnit.getEntityType())) ? true : false;
        int maxLevels = 4;//最多允许一次性展示到5级，即到四级子部门
        int levels = 0;//有多少层级
        boolean isBusinessOrg = false;
        if(rootUnit != null){
        	if(OrgConstants.ExternalType.Interconnect4.ordinal() == rootUnit.getExternalType()){
        		isBusinessOrg = true;
        	}
            Map<String,Object> rootAccountObj = new HashMap<String, Object>();
            rootAccountObj.put("id",rootUnit.getId().toString());
            rootAccountObj.put("name",rootUnit.getName());
            rootAccountObj.put("type",rootUnit.getEntityType());
            if(OrgConstants.ORGENT_TYPE.Account.name().equals(rootUnit.getEntityType())){
            	rootAccountObj.put("num",orgManager.getAllMembersNum(rootUnit.getId()));
            }else{
            	rootAccountObj.put("num",orgManager.getMembersNumByDepartment(rootUnit.getId(), false));

            }
            List<Map<String,Object>> treeList = new ArrayList<Map<String, Object>>();
            if(rootUnit.isGroup()){
                List<V3xOrgAccount> allAccounts = orgManager.getChildAccount(rootUnit.getId(),false);
                Collections.sort(allAccounts, CompareUnitPath.getInstance2());
                for(V3xOrgAccount tempAccount : allAccounts){
                    Map<String,Object> tempAccountMap = new HashMap<String, Object>();
                    tempAccountMap.put("id",tempAccount.getId().toString());
                    tempAccountMap.put("pId",tempAccount.getSuperior().toString());
                    tempAccountMap.put("name",tempAccount.getName());
                    tempAccountMap.put("level",tempAccount.getLevelScope());
                    tempAccountMap.put("type",tempAccount.getEntityType());
                    tempAccountMap.put("hasChildren",(orgManager.getChildAccount(tempAccount.getId(),true)).size()>1);
                    int currentLevel = (tempAccount.getPath().length() - rootUnit.getPath().length()) / 4;
                    if(currentLevel>levels){
                    	levels = currentLevel;
                    }
                    treeList.add(tempAccountMap);
                }
            }else {
            	if(fromDept){//从部门节点点进来的，还要显示上级部门/单位，把自己手动加进去
            		Map<String,Object> tempDept = new HashMap<String, Object>();
                    tempDept.put("id",rootUnit.getId().toString());
                    tempDept.put("pId",rootUnit.getSuperior().toString());
                    tempDept.put("name",rootUnit.getName());
                    tempDept.put("hasChildren",((V3xOrgDepartment)rootUnit).hasChildren(true));
                    tempDept.put("level",1);
                    tempDept.put("type",rootUnit.getEntityType());
                    
                    if("detail".equals(from) && !isBusinessOrg){
                    	tempDept.put("num",addressBookManager.getDeptMemberSize(rootUnit.getId(),false));
                    }else{
                    	tempDept.put("num",orgManager.getMembersNumByDepartment(rootUnit.getId(), false));
                    }
                    
                	String depManagerMember = "";
                	String depLeaderMember = "";
                	List<V3xOrgMember> depManager = orgManager.getMembersByRole(rootUnit.getId(),OrgConstants.Role_NAME.DepManager.name());
                	List<V3xOrgMember> depLeader = orgManager.getMembersByRole(rootUnit.getId(),OrgConstants.Role_NAME.DepLeader.name());
                	if(Strings.isNotEmpty(depManager)){
                		for(V3xOrgMember m : depManager){
                			depManagerMember = depManagerMember + (Strings.isNotBlank(depManagerMember) ? "、" : "") + m.getName();
                		}
                	}
                	if(Strings.isNotEmpty(depLeader)){
                		for(V3xOrgMember m : depLeader){
                			depLeaderMember = depLeaderMember + (Strings.isNotBlank(depLeaderMember) ? "、" : "") + m.getName();
                		}
                	}
                	tempDept.put("DepManager",depManagerMember);
                	tempDept.put("DepLeader",depLeaderMember);
                    treeList.add(tempDept);
                    
                    //重置跟节点为上级部门/单位
                    Long parentUnitId = rootUnit.getSuperior();
                    V3xOrgUnit parentUnit = orgManager.getUnitById(parentUnitId);
                    rootAccountObj.put("id",parentUnit.getId().toString());
                    rootAccountObj.put("name",parentUnit.getName());
                    String type = parentUnit.getEntityType();
                    rootAccountObj.put("type",type);
                    if(OrgConstants.ORGENT_TYPE.Account.name().equals(type)){
                    	rootAccountObj.put("num",orgManager.getAllMembersNum(parentUnit.getId()));
                    }else{
                    	 if("detail".equals(from) && !isBusinessOrg){
                    		 rootAccountObj.put("num",addressBookManager.getDeptMemberSize(parentUnit.getId(),false));
                         }else{
                        	 rootAccountObj.put("num",orgManager.getMembersNumByDepartment(parentUnit.getId(), false));
                         }
                    	 
//                    	String parentDepManagerMember = "";
//                    	String parentDepLeaderMember = "";
//                    	List<V3xOrgMember> parentDepManager = orgManager.getMembersByRole(parentUnit.getId(),OrgConstants.Role_NAME.DepManager.name());
//                    	List<V3xOrgMember> parentDepLeader = orgManager.getMembersByRole(parentUnit.getId(),OrgConstants.Role_NAME.DepLeader.name());
//                    	if(Strings.isNotEmpty(parentDepManager)){
//                    		for(V3xOrgMember m : parentDepManager){
//                    			parentDepManagerMember = parentDepManagerMember + (Strings.isNotBlank(parentDepManagerMember) ? "、" : "") + m.getName();
//                    		}
//                    	}
//                    	if(Strings.isNotEmpty(parentDepLeader)){
//                    		for(V3xOrgMember m : parentDepLeader){
//                    			parentDepLeaderMember = parentDepLeaderMember + (Strings.isNotBlank(parentDepLeaderMember) ? "、" : "") + m.getName();
//                    		}
//                    	}
//                    	tempDept.put("DepManager",parentDepManagerMember);
//                    	tempDept.put("DepLeader",parentDepLeaderMember);
                    }
            	}
                List<V3xOrgDepartment> allDepartments = orgManager.getChildDepartments(rootUnit.getId(),false,null);
                Collections.sort(allDepartments, CompareUnitPath.getInstance2());
                for(V3xOrgDepartment dept : allDepartments){
                    int level = orgManager.getDeptLevel(dept.getId());
                    Map<String,Object> tempDept = new HashMap<String, Object>();
                    tempDept.put("id",dept.getId().toString());
                    tempDept.put("pId",dept.getSuperior().toString());
                    tempDept.put("name",dept.getName());
                    tempDept.put("hasChildren",dept.hasChildren(true));
                    tempDept.put("type",dept.getEntityType());
                    tempDept.put("level",level);
                    if("detail".equals(from) && !isBusinessOrg){
                    	tempDept.put("num",addressBookManager.getDeptMemberSize(dept.getId(),false));
                    }else{
                    	tempDept.put("num",orgManager.getMembersNumByDepartment(dept.getId(), false, true));
                    	
                    }
                    
                    int currentLevel = (dept.getPath().length() - rootUnit.getPath().length()) / 4;
                    if(currentLevel>levels){
                    	levels = currentLevel;
                    }
                    
                    if(currentLevel == 1 || (currentLevel == 2 && isBusinessOrg)){//一级子部门显示 部门主管和部门分管领导
                    	String depManagerMember = "";
                    	String depLeaderMember = "";
                    	List<V3xOrgMember> depManager = orgManager.getMembersByRole(dept.getId(),OrgConstants.Role_NAME.DepManager.name());
                    	List<V3xOrgMember> depLeader = orgManager.getMembersByRole(dept.getId(),OrgConstants.Role_NAME.DepLeader.name());
                    	if(Strings.isNotEmpty(depManager)){
                    		for(V3xOrgMember m : depManager){
                    			depManagerMember = depManagerMember + (Strings.isNotBlank(depManagerMember) ? "、" : "") + m.getName();
                    		}
                    	}
                    	if(Strings.isNotEmpty(depLeader)){
                    		for(V3xOrgMember m : depLeader){
                    			depLeaderMember = depLeaderMember + (Strings.isNotBlank(depLeaderMember) ? "、" : "") + m.getName();
                    		}
                    	}
                    	tempDept.put("DepManager",depManagerMember);
                    	tempDept.put("DepLeader",depLeaderMember);
                    }
                    
                    treeList.add(tempDept);
                }
            }
            mav.addObject("treeList",JSONUtil.toJSONString(treeList));
            mav.addObject("rootAccount",JSONUtil.toJSONString(rootAccountObj));
            mav.addObject("isGroupAdmin",groupAdmin);
            mav.addObject("isAdministrator",user.isAdministrator());
            mav.addObject("isHrAdmin",orgManager.isRole(user.getId(), rootUnit.getOrgAccountId(), OrgConstants.Role_NAME.HrAdmin.name(), null));
            mav.addObject("isBusinessOrgAdmin",orgManager.isRole(user.getId(), rootUnit.getOrgAccountId(), OrgConstants.Role_NAME.BusinessOrganizationManager.name(), null));

            mav.addObject("accountLogo","");
            mav.addObject("accountShortName",rootUnit.getShortName());
            mav.addObject("accountName",rootUnit.getName());
            mav.addObject("accountSecondName",rootUnit.getSecondName());
            mav.addObject("accountCode",rootUnit.getCode());
            if(fromDept){//如果是从部门节点点进来的，还需要显示上级部门/单位，因此会多一层
            	levels+=1;
            }
            mav.addObject("levels",levels > maxLevels ? maxLevels : levels);
            boolean isBusinessOrgTree = false;
            if(rootUnit.getExternalType() == OrgConstants.ExternalType.Interconnect4.ordinal()) {
            	isBusinessOrgTree = true;
            }
            mav.addObject("isBusinessOrgTree",isBusinessOrgTree);
            
            //部门分管领导
            String depLeaderRoleName = ResourceUtil.getString("sys.role.rolename.DepLeader");
            List<V3xOrgRole> depLeaders = orgManager.getRoleByCode("DepLeader", rootUnit.getOrgAccountId());
            if(Strings.isNotEmpty(depLeaders)) {
            	depLeaderRoleName = depLeaders.get(0).getShowName();
            }
            
            //部门主管
            String depManagerRoleName = ResourceUtil.getString("sys.role.rolename.DepManager");
            List<V3xOrgRole> depManagers = orgManager.getRoleByCode("DepManager", rootUnit.getOrgAccountId());
            if(Strings.isNotEmpty(depManagers)) {
            	depManagerRoleName = depManagers.get(0).getShowName();
            }
            mav.addObject("depLeaderRoleName",depLeaderRoleName);
            mav.addObject("depManagerRoleName",depManagerRoleName);
            return mav;
        }else {
            return null;
        }
    }
    
	/**
	 * 组织架构图中，点击某一节点显示出来的部门详细信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    public ModelAndView orgDetailFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView mav = new ModelAndView("addressbook/public/orgDetailFrame");
    	String currentNodeId = request.getParameter("currentNodeId");
    	mav.addObject("currentNodeId",currentNodeId);
    	V3xOrgUnit unit = orgManager.getUnitById(Long.valueOf(currentNodeId));
    	Long pId = -1L;
    	if(OrgConstants.ORGENT_TYPE.Department.name().equals(unit.getEntityType()) || OrgConstants.ORGENT_TYPE.BusinessDepartment.name().equals(unit.getEntityType())){
    		pId = unit.getSuperior();
    	}
    	mav.addObject("pId",pId);
    	mav.addObject("accountId",unit.getOrgAccountId());
    	
    	String isAdmin = request.getParameter("isAdmin");
    	mav.addObject("isAdmin",isAdmin);//单位管理员或者hr管理员
    	return mav;
    }

    @CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator})
    public ModelAndView manageIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/organization/manageIndex/manageIndex");
        User user = AppContext.getCurrentUser();
        Long accountId = user.getLoginAccount();
        Long adminId = user.getId();
        
        boolean group = user.isGroupAdmin();
        boolean businessorganization = AppContext.hasPlugin("businessorganization");

        int divNum = 6 - (group?1:0) - (businessorganization?0:1);
        int divWidth = 100/divNum;

        mav.addObject("isGroupAdmin",group);
        mav.addObject("hasBusiness",businessorganization);
        int memberNum = 0;//单位人员
        int unitNum = 0;//集团（单位数）、单位（部门数） 
        int postNum = 0;//集团（集团基准岗数）、单位（岗位数） 
        int levelNum = 0;//集团（集团职级数）、单位（职级数）
        int roleNum = 0;//集团（集团角色数）、单位（单位角色数）
        int businessNum = 0;//集团（集团业务线数）、单位（单位业务线数）
        
        mav.addObject("memberNum",0);
        if(group){
            List<V3xOrgAccount> orgAccounts = orgManagerDirect.getAllAccounts(null, true, null, null, null);
            unitNum = orgAccounts.size() -1;
            postNum = orgManager.getAllPosts(accountId).size();
            levelNum = orgManager.getAllLevels(accountId).size();
            roleNum = orgManager.getAllRoles(accountId).size();
    		businessNum = businessOrgManagerDirect.getAccountList(adminId, true).size();
        } else {
        	unitNum = orgManager.getAllInternalDepartments(accountId).size();
        	postNum = orgManager.getAllPosts(accountId).size();
        	levelNum = orgManager.getAllLevels(accountId).size();
        	memberNum = orgManager.getAllMembersNumsWithOutConcurrent(accountId);
        	roleNum = orgManager.getAllRoles(accountId).size();
        	businessNum = businessOrgManagerDirect.getAccountList(adminId, true).size();
        }
        //单位/部门数
        mav.addObject("accountNum",unitNum);
        //基准岗/岗位数
        mav.addObject("postNum",postNum);
        //集团/单位职务级别数
        mav.addObject("levelNum",levelNum);
        //集团/单位角色数
        mav.addObject("roleNum",roleNum);
        //集团/单位多维组织数
        mav.addObject("businessNum",businessNum);
        //单位人员数
        mav.addObject("memberNum",memberNum);
        //div宽
        mav.addObject("divWidth",divWidth+"%");

        mav.addObject("divMargin",divNum==6?"2%":"0");

        return mav;
    }

}
