/**
 * $Author: sunzhemin $
 * $Rev: 47759 $
 * $Date:: 2015-03-25 17:28:17#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 * 
 * @editor futao
 */
package com.seeyon.ctp.privilege.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.ProductEditionEnum;
import com.seeyon.ctp.common.customize.manager.CustomizeManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.menu.manager.PluginMenuManager;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.bo.MemberRole;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.enums.RoleTypeEnum;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.manager.RoleManager;
import com.seeyon.ctp.organization.po.OrgRole;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;
import com.seeyon.ctp.privilege.bo.PrivTreeNodeBO;
import com.seeyon.ctp.privilege.dao.RoleMenuDao;
import com.seeyon.ctp.privilege.enums.AppResourceCategoryEnums;
import com.seeyon.ctp.privilege.enums.MenuTypeEnums;
import com.seeyon.ctp.privilege.po.PrivRoleMenu;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;


/**
 * <p>Title: 权限数据操作接口的实现</p>
 * <p>Description: 本程序提供查询菜单资源数据和动态添加菜单项的方法</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
@ProcessInDataSource(name = DataSourceName.BASE)
public class PrivilegeManagerImpl implements PrivilegeManager {
	private final static Log logger = LogFactory.getLog(PrivilegeManagerImpl.class);
	/** */
    private PrivilegeMenuManager     privilegeMenuManager;
    
    /** */
    private OrgManager      orgManager;
    
    /** */
    private RoleManager     roleManager;
    
    private CustomizeManager customizeManager;
    
    private PluginMenuManager      pluginMenuManager;
    
    private OrgDao           orgDao;
    
    private OrgManagerDirect orgManagerDirect;
    
    private RoleMenuDao roleMenuDao;

	public void setPluginMenuManager(PluginMenuManager pluginMenuManager) {
		this.pluginMenuManager = pluginMenuManager;
	}

	private PrivilegeMenuManager getPrivilegeMenuManager(){
    	if(this.privilegeMenuManager==null){
    		this.privilegeMenuManager=(PrivilegeMenuManager) AppContext.getBean("menuManager");
    	}
    	return this.privilegeMenuManager;
    }
    
	@Override
	public List<PrivMenuBO> getMenusOfMember(Long memberId, Long accountId) throws BusinessException {
	    return privilegeMenuManager.getMenusOfMember(memberId, accountId);
	}
	@Override
	public boolean getMenuValidity(Long memberId, Long accountId) throws BusinessException{
		return privilegeMenuManager.getMenuValidity(memberId,accountId);
	}
	
	@Override
	public List<PrivMenuBO> getAllMenus() throws BusinessException {
		 return privilegeMenuManager.findMenus(null);
	}

	@Override
	public List<PrivMenuBO> getMenus(PrivMenuBO menu) throws BusinessException {
		// TODO Auto-generated method stub
		return privilegeMenuManager.findMenus(menu);
	}
	
	public CustomizeManager getCustomizeManager() {
		return customizeManager;
	}

	public void setCustomizeManager(CustomizeManager customizeManager) {
		this.customizeManager = customizeManager;
	}
	
	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}
	
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setRoleMenuDao(RoleMenuDao roleMenuDao) {
		this.roleMenuDao = roleMenuDao;
	}

	@Override
	public List<PrivMenuBO> getAllShortCutMenus() throws BusinessException {
		// TODO Auto-generated method stub
		List<PrivMenuBO> returnmenu = new ArrayList<PrivMenuBO>();
        List<PrivMenuBO> allmenu = getAllMenus();
        for (PrivMenuBO privMenuBO : allmenu) {
			if(privMenuBO.getExt4().intValue()==AppResourceCategoryEnums.ForegroundShortcut.getKey()){
				returnmenu.add(privMenuBO);
			}
		}
        return returnmenu;
	}

	@Override
	public List<PrivMenuBO> getShortCutMenusOfMember(Long memberId, Long accountId) throws BusinessException {
		List<PrivMenuBO> returnmenulist = new ArrayList<PrivMenuBO>();
    	List<PrivMenuBO> menulist = getAllShortCutMenus();
    	String productId = AppContext.getSystemProperty("system.ProductId");
    	Long parentid = 0L;
    	for (PrivMenuBO privMenuBO : menulist) {
			if(privMenuBO.getEnterResourceId()!=null){
				String resourceCode=privMenuBO.getResourceCode();
				if("T03_linkSystemViewK".equals(resourceCode)){
					returnmenulist.add(privMenuBO);
					continue;
				}
				
				int productEditionEnum = Integer.parseInt(productId);
				if("F08_WorkPerformK".equals(resourceCode)&&(ProductEditionEnum.a6p.key() == productEditionEnum || ProductEditionEnum.a6.key() == productEditionEnum || ProductEditionEnum.a6s.key() == productEditionEnum)){
					returnmenulist.add(privMenuBO);
					continue;
				}
				
				if(hasMenuCode(resourceCode.substring(0, resourceCode.length()-1))){
					returnmenulist.add(privMenuBO);
					continue;
				}
			
				if(!privMenuBO.getParentId().equals(0L)&&!privMenuBO.getParentId().equals(parentid)){
					parentid = privMenuBO.getParentId();
					returnmenulist.add(this.findMenuById(parentid));
				}
			}
		}
    	//去掉重复的快捷
    	returnmenulist = removeDuplicateWithOrder(returnmenulist);
        return returnmenulist;
	}
	@SuppressWarnings("unchecked")
	private  List removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        return newList;
    }
	
	@Override
	public void createCustomRoleResource(String roleName, String roleCode, RoleTypeEnum roleType, List<String> resourceCodes) throws BusinessException{
		if(Strings.isBlank(roleName) || Strings.isBlank(roleCode) || Strings.isEmpty(resourceCodes)){
			logger.error("报表角色名称，编码，资源数据不完整，创建失败！");
			throw new BusinessException(ResourceUtil.getString("MessageStatus.ERROR"));
		}
		
		if(roleType == RoleTypeEnum.reportflag){//第三方报表角色，先处理这一种场景
			V3xOrgAccount rootAccount = orgManager.getRootAccount();
			V3xOrgRole role = new V3xOrgRole();
			role.setId(UUIDLong.longUUID());
			role.setName(roleName);
			role.setCode(roleCode);
			role.setType(V3xOrgEntity.ROLETYPE_REPORT);//通过接口创建的自定义的报表角色
			role.setCategory("0");//默認不前臺授權
			role.setOrgAccountId(rootAccount.getId());
			role.setBond(OrgConstants.ROLE_BOND.ACCOUNT.ordinal());
			role.setEnabled(true);
			role.setIsDeleted(false);
			role.setExternalType(OrgConstants.ExternalType.Inner.ordinal());
			role.setSortId(Long.valueOf(roleManager.getMaxSortId(rootAccount.getId())));
			role.setStatus(OrgConstants.ORGENT_STATUS.NULL.ordinal());//默认不用于选人界面
			boolean success = createCustomRole(role);
			if(success){
				updateCustomRoleResource(resourceCodes,role);
			}
		}else{
			logger.info("不支持自定义创建此类型角色！");
		}
	}
	
	/**
	 * 创建自定义的角色
	 * @param role
	 * @throws BusinessException
	 */
    private boolean createCustomRole(V3xOrgRole role) throws BusinessException {
        //判断角色名称是否重复
        if(roleManager.checkDulipName(role)){
        	logger.info("自定义角色名称重复！");
        	return false;
            //throw new BusinessException(ResourceUtil.getString("role.repeat.name")+"，"+ResourceUtil.getString("MessageStatus.ERROR"));
        }
        //判断角色编码是否重复
        if(roleManager.checkDulipCode(role)){
        	logger.info("判断角色编码是否重复!");
        	return false;
        	//throw new BusinessException(ResourceUtil.getString("role.repeat")+"，"+ResourceUtil.getString("MessageStatus.ERROR"));
        }
        
        List<OrgRole> orgRolePO = new ArrayList<OrgRole>();
        OrgRole roleNew = (OrgRole) role.toPO();
        roleNew.setIdIfNew();
        orgRolePO.add(roleNew);
        orgDao.insertOrgRole(orgRolePO);
        //集团基准角色
        if((Boolean)SysFlag.sys_isGroupVer.getFlag()){
        	List<OrgUnit> allAccounts = orgDao.getAllUnitPO(OrgConstants.UnitType.Account, null, null, null, "group", false, null);
        	for (OrgUnit orgUnit : allAccounts) {
        		V3xOrgRole newrole = new V3xOrgRole(role);
        		newrole.setId(UUIDLong.longUUID());
        		newrole.setOrgAccountId(orgUnit.getId());
        		newrole.setType(V3xOrgEntity.ROLETYPE_RELATIVEROLE);
        		orgManagerDirect.addRole(newrole);
        		V3xOrgRelationship newrel = new V3xOrgRelationship();
        		newrel.setId(UUIDLong.longUUID());
        		newrel.setSourceId(newrole.getId());
        		newrel.setObjective0Id(role.getId());
        		newrel.setKey(OrgConstants.RelationshipType.Banchmark_Role.name());
        		orgManagerDirect.addOrgRelationship(newrel);
    		}
        }
        return true;
    }
	
    /**
     * 为自定义的角色授权资源
     * @param resourceCodes
     * @param role
     * @throws BusinessException
     */
    private void updateCustomRoleResource(List<String> resourceCodes, V3xOrgRole role) throws BusinessException {
        // 先删除已存在的关系
        PrivRoleMenu roleRes = new PrivRoleMenu();
        roleRes.setRoleid(role.getId());
        List<V3xOrgRelationship> rellist = new ArrayList<V3xOrgRelationship>();
        roleMenuDao.deleteRoleMenu(roleRes);
        //如果是集团基准角色，批量删除单位关系
        if(orgManager.getAccountById(orgManager.getRoleById(role.getId()).getOrgAccountId()).isGroup()){
            EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
            enummap.put(OrgConstants.RelationshipObjectiveName.objective0Id, role.getId());
            rellist = orgManager.getV3xOrgRelationship(OrgConstants.RelationshipType.Banchmark_Role, null, null, enummap);
            for (V3xOrgRelationship v3xOrgRelationship : rellist) {
                roleRes = new PrivRoleMenu();
                roleRes.setRoleid(v3xOrgRelationship.getSourceId());
                roleMenuDao.deleteRoleMenu(roleRes);
            }
        }
        
        // 新建角色资源关系
        Map<Long, PrivMenuBO> map = privilegeMenuManager.getMenusByResource(resourceCodes);
        List<PrivRoleMenu> roleReources = new ArrayList<PrivRoleMenu>();
        for(Long menuId : map.keySet()){
            PrivMenuBO menuBO = privilegeMenuManager.findById(menuId);
            if (menuBO != null) {
                roleRes = new PrivRoleMenu();
                roleRes.setNewId();
                roleRes.setResourceid(menuBO.getEnterResourceId());
                roleRes.setRoleid(role.getId());
                roleRes.setMenuid(menuBO.getId());
                roleReources.add(roleRes);
            }
        }
        roleMenuDao.insertRoleMenuPatchAll(roleReources);

        privilegeMenuManager.updateMemberMenuLastDateByRoleId(role.getId(), role.getOrgAccountId(),null);
        //如果是集团基准角色，更新各单位的关系
        if (orgManager.getAccountById(role.getOrgAccountId()).isGroup()) {
            for (V3xOrgRelationship v3xOrgRelationship : rellist) {
                List<PrivRoleMenu> accroleReources = new ArrayList<PrivRoleMenu>();
                //accroleReources.addAll(roleReources);
                for (PrivRoleMenu privRoleResource : roleReources) {
                    PrivRoleMenu accprivRoleResource = new PrivRoleMenu();
                    accprivRoleResource.setRoleid(v3xOrgRelationship.getSourceId());
                    accprivRoleResource.setId(UUIDLong.longUUID());
                    accprivRoleResource.setMenuid(privRoleResource.getMenuid());
                    accprivRoleResource.setModifiable(privRoleResource.getModifiable());
                    accprivRoleResource.setResourceid(privRoleResource.getResourceid());
                    accroleReources.add(accprivRoleResource);
                }
                roleMenuDao.insertRoleMenuPatchAll(accroleReources);
            }
        }
        privilegeMenuManager.updateBiz();
    }

    @Override
	public void insertMenus(List<PrivMenuBO> menus, V3xOrgRole role, String auth, boolean reset) throws BusinessException {

		Long roleId = role.getId();
		//先删除已存在的数据
		if(reset) {
			privilegeMenuManager.cleanPrivData(roleId);
		}
		List<Long> menuIds = createPrivData(menus);
		if (!CollectionUtils.isEmpty(menuIds)) {
			List<PrivTreeNodeBO> nodes = new ArrayList<PrivTreeNodeBO>();
			for (Long menuid : menuIds) {
				PrivMenuBO menu = findMenuById(menuid);
				nodes.add(new PrivTreeNodeBO(menu, null));

			}
			//授权给全集团

			V3xOrgRole roleTemp = roleManager.findById(roleId);
			if (roleTemp == null || StringUtils.isBlank(roleTemp.getName())) {
				role.setOrgAccountId(AppContext.currentAccountId());
				role.setType(V3xOrgEntity.ROLETYPE_USERROLE);
				roleId = roleManager.createRole(role);
			}else{
				role.setOrgAccountId(AppContext.currentAccountId());
				role.setType(V3xOrgEntity.ROLETYPE_USERROLE);
				roleId = roleManager.updateRole(role);
			}
			roleManager.batchRole2EntityToEntAccount(roleId, auth);
			roleManager.updateRoleResource(nodes, roleId, reset);
		}
	}
	@Override
	public void insertMenus(List<PrivMenuBO> menus, V3xOrgRole role, String auth) throws BusinessException {
		insertMenus(menus, role, auth, true);
	}
	
	
	private List<Long> createPrivData(List<PrivMenuBO> menus) throws BusinessException {
        List<Long> menuIds = new ArrayList<Long>();
        if (menus != null) {
            Map<Long, PrivMenuBO> menuMap = new HashMap<Long, PrivMenuBO>();
            for (PrivMenuBO privMenuBO : menus) {
                if (privMenuBO == null)
                    continue;
                menuMap.put(privMenuBO.getId(), privMenuBO);
            }
            for (PrivMenuBO privMenuBO : menus) {
                if (privMenuBO == null)
                    continue;
                // 菜单已存在的情况
                PrivMenuBO menu = privilegeMenuManager.findById(privMenuBO.getId());
                
                
                if (menu != null) {
                	//更新菜单名称
                	if(!menu.getSortid().equals(privMenuBO.getSortid()) || !menu.getName().equals(privMenuBO.getName()) || (Strings.isNotBlank(privMenuBO.getIcon()) && !menu.getIcon().equals(privMenuBO.getIcon()))){
                    	menu.setName(privMenuBO.getName());
                    	menu.setIcon(privMenuBO.getIcon());
                    	menu.setSortid(privMenuBO.getSortid());
                    	privilegeMenuManager.updateMenu(menu);
                    
                    }
                    menuMap.put(menu.getId(), menu);
                    menuIds.add(menu.getId());
                    continue;
                }
               // if (StringUtils.isBlank(privMenuBO.getPath())) {
               //     PrivMenuBO parent = menuMap.get(privMenuBO.getParentId());
               //     if (parent == null) {
               //         parent = privilegeMenuManager.findById(privMenuBO.getParentId());
               //     }
               //     privMenuBO = privilegeMenuManager.getMenuPath(privMenuBO, parent);
                //}
                menuMap.put(privMenuBO.getId(), privMenuBO);
                menuIds.add(insertNewMenu(privMenuBO, privMenuBO.getUrl()));
            }
        }
        return menuIds;
    }
	  
	@Override
	public Long insertNewMenu(PrivMenuBO menu, String url) throws BusinessException {
		// TODO Auto-generated method stub
		if (menu != null) {
            if (!StringUtils.isBlank(url)) {
                int index = 10;
                if (url.length() < 10) {
                    index = url.length();
                }
                String randomString = String.valueOf(UUIDLong.longUUID());
                String code = new StringBuilder("000_").append(url.substring(0, index)).append("_")
                        .append(randomString).toString();
                menu.setResourceNavurl(url);
                menu.setResourceCode(code);
                menu.setResourceModuleid("none");
                menu.setEnterResource(1);
            }
            menu.setExt7(menu.getName());
            menu.setExt19(0l);
            menu.setUpdateuserid(AppContext.currentUserId());
            menu.setCreateuserid(AppContext.currentUserId());          
            menu.setExt1(MenuTypeEnums.applicationfront.getValue());
            menu.setExt4(AppResourceCategoryEnums.ForegroundApplication.getKey());
            menu.setExt21(new Long(1));
            menu.setExt15(0);
            menu.setExt16(1);
            menu.setCreatedate(new Date());
            
            if(Strings.isBlank(menu.getIcon())){
            	menu.setIcon("common.png");
            }
            if(menu.getSortid()==null||menu.getSortid().equals(1)){
            	menu.setSortid(999);
            }
            return privilegeMenuManager.create(menu).getId();
        } else {
            return -1l;
        }
	}

	@Override
	public void deleteMenu(Long menuId) throws BusinessException {
		// TODO Auto-generated method stub
		privilegeMenuManager.deleteMenu(new Long[]{menuId});
	}

	@Override
	public Long updateMenu(PrivMenuBO menu) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PrivMenuBO> getAllUseAbleMenus() throws BusinessException {
		List<PrivMenuBO> returnlist = new ArrayList<PrivMenuBO>();
		Map<String, List<PrivTreeNodeBO>> map=privilegeMenuManager.getTreeNodes(null,null,null,"1",null,"1","true",new ArrayList<PrivTreeNodeBO>(),new ArrayList<PrivTreeNodeBO>(),false);
    	List<PrivTreeNodeBO> treeNodes4Front = new ArrayList<PrivTreeNodeBO>();
    	if(map!=null){
    		treeNodes4Front=map.get("treeNodes4Front");
    	}
    	for (PrivTreeNodeBO privTreeNodeBO : treeNodes4Front) {
    		String[] bos = privTreeNodeBO.getIdKey().split("_");
            if ("menu".equals(bos[0])) {
    			//PrivMenuBO menu = new PrivMenuBO();
    			//menu.setId(Long.valueOf(privTreeNodeBO.getIdKey().split("_")[1]));
    			//returnlist.add(this.findMenuById(Long.valueOf(bos[1])));
            	if(null!=privTreeNodeBO.getMenu()){
            		returnlist.add(privTreeNodeBO.getMenu());
            	}
    		}
		}
    	return returnlist;
	}
	

	 
	 /**
	  * 
	  * @方法名称: findMenusByExt4
	  * @功能描述: 通过所属版本查询菜单
	  * @参数 ：@param menu
	  * @参数 ：@return
	  * @参数 ：@throws BusinessException
	  * @返回类型：List<PrivMenuBO>
	  * @创建时间 ：2015年12月7日 下午8:24:44
	  * @创建人 ： FuTao
	  * @修改人 ： 
	  * @修改时间 ：
	  */
	   public List<PrivMenuBO> findMenusByExt4(PrivMenuBO menu) throws BusinessException {
	    	List<PrivMenuBO> list  = new ArrayList<PrivMenuBO>();
	    	List<PrivMenuBO> allMenus = getAllMenus();
	    	for (PrivMenuBO privMenuBO : allMenus) {
				if(privMenuBO.getExt4().equals(menu.getExt4())){
					list.add(privMenuBO);
				}
			}
	        return list;
	    }
	   /**
	    * 
	    * @方法名称: getMenuByRole
	    * @功能描述: 
	    * @参数 ：@param roleIds
	    * @参数 ：@return
	    * @返回类型：Map<Long,PrivMenuBO>
	    * @创建时间 ：2015年12月7日 下午8:27:54
	    * @创建人 ： FuTao
	    * @修改人 ： 
	    * @修改时间 ：
	    */
	   public Map<Long, PrivMenuBO> getMenuByRole(Long[] roleIds) {
	        return privilegeMenuManager.getByRole(roleIds);
	    }

	@Override
	public PrivMenuBO findMenuById(Long menuId) {
		// TODO Auto-generated method stub
		return privilegeMenuManager.findById(menuId);
	}

	@Override
	public List<PrivMenuBO> findMenus(PrivMenuBO menu) {
		// TODO Auto-generated method stub
		 return privilegeMenuManager.findMenus(menu);
	}
	
	
	


	@Override
	public void deleteByRole(Long roleId) throws BusinessException {
		// TODO Auto-generated method stub
		V3xOrgRole role=roleManager.findById(roleId);
		if(role!=null){
			//先删除role和menu的关系
			if(role.getBond()==OrgConstants.ROLE_BOND.SSO.ordinal()){
				 privilegeMenuManager.getListByRole(new Long[]{role.getId()}).toArray();
				 privilegeMenuManager.deleteMenu(privilegeMenuManager.getMenusByRole(new Long[]{role.getId()}));
			}
			roleManager.deleteRoleMenu(role.getId());
			//再删除角色
			roleManager.deleteRole(role.getId(), true);
			
		}
	}

	@Override
	public void cleanFormPrivData(Long roleId) throws BusinessException{
		if (roleId != null) {
            Long[] roleIds = { roleId };
            Map<Long, PrivMenuBO> menuMap = privilegeMenuManager.getPrivMenu4Form(roleIds);
            if (menuMap.size() > 0) {
                Collection<PrivMenuBO> menusTemp = menuMap.values();
                Long[] menuIds = new Long[menusTemp.size()];
                int i = 0;
                for (PrivMenuBO menu : menusTemp) {
                    menuIds[i] = menu.getId();
                    i++;
                }
                privilegeMenuManager.deleteMenu(menuIds);
            }
        }
	}

	 @Override
	    public boolean checkByMenuAndMember(Long memberId, Long accountId, Long menuId) throws BusinessException {
	        List<MemberRole> roles = orgManager.getMemberRoles(memberId, accountId);
	        if (roles != null) {
	            V3xOrgRole role = null;
	            Long[] roleIds = new Long[roles.size()];
	            for (int i = 0; i < roles.size(); i++) {
	                role = roles.get(i).getRole();
	                if (role != null) {
	                    roleIds[i] = role.getId();
	                }
	            }
	            Map<Long, PrivMenuBO> menus = privilegeMenuManager.getByRole(roleIds);
	            if (menus != null && menus.containsKey(menuId)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    /* (non-Javadoc)
	     * @see com.seeyon.ctp.privilege.manager.PrivilegeCheck#checkByUrlAndMember(java.lang.Long, java.lang.Long, java.lang.String)
	     */
	    @Override
	    public boolean checkByUrlAndMember(Long memberId, Long accountId, String url) throws BusinessException {
	        List<MemberRole> roles = orgManager.getMemberRoles(memberId, accountId);
	        if (roles != null) {
	            Long[] roleIds = new Long[roles.size()];
	            V3xOrgRole v3xOrgRole = null;
	            for (int i = 0; i < roles.size(); i++) {
	                v3xOrgRole = roles.get(i).getRole();
	                roleIds[i] = v3xOrgRole.getId();
	            }
	            /**
	             * v6权限改造
	             */
	          HashSet<String> reses = privilegeMenuManager.getUrlsByRole(roleIds);
	            if (reses != null && reses.contains(url)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    @Override
	    public boolean hasMenu(Long menuId) throws BusinessException {
	        User user = AppContext.getCurrentUser();
	        if (user == null) {
	            throw new BusinessException("没有用户登录");
	        }
	        Long accountId = user.getLoginAccount();
	        Long memberId = user.getId();
	        return checkByMenuAndMember(memberId, accountId, menuId);
	    }

	    @Override
	    public boolean hasUrl(String url) throws BusinessException {
	        User user = AppContext.getCurrentUser();
	        if (user == null) {
	            throw new BusinessException("没有用户登录");
	        }
	        Long accountId = user.getLoginAccount();
	        Long memberId = user.getId();
	        return checkByUrlAndMember(memberId, accountId, url);
	    }
	    
	    
	    
	    @Override
	    public List<V3xOrgMember> getMembersByMenu(String resourceId,Long accountId) throws BusinessException {  	
	    	
	    	return null;
	    }
	    

	    @Override
	    public boolean checkByReourceCode(String resourceCode) throws BusinessException {
	    	User user=AppContext.getCurrentUser();
	    	if(user!=null){
	    		if(user.getResourceJsonStr().contains(resourceCode)){
		    		return true;
		    	}
	    	}
	    	
	        return false;
	    }
	    @Override
	    public boolean checkByReourceCode(String resourceCode,Long memberId,Long accountId) throws BusinessException {
	    	User user=AppContext.getCurrentUser(); 
	    	if(user!=null && user.getId().equals(memberId) && user.getLoginAccount().equals(accountId)){
	    		if(Strings.isNotBlank(user.getResourceJsonStr()) && user.getResourceJsonStr().contains(resourceCode)){
		    		return true;
		    	}
	    	}else{
	    		//如果缓存里面有一个code的缓存是不是会更快
	    		PrivMenuBO menu=getPrivilegeMenuManager().getMenuByCode(resourceCode);
	    		Map<Long, PrivMenuBO>  menus=getPrivilegeMenuManager().getByMember(memberId, accountId);
	    		if(menu!=null&&menus.containsKey(menu.getId())){
	    			return true;
	    		}
	    	}
	        return false;
	    }
	    @Override
	    public boolean checkByRoleName(String roleName,Long memberId,Long accountId )throws BusinessException{
			return orgManager.hasSpecificRole(memberId, accountId, roleName);
	    }
	    
	    @Override
	    public List<V3xOrgMember> getMembersByResource(String resourceId,Long accountId) throws BusinessException {

	    	return null;
	    }
	    
	    
	    
	    /**
	     * @param orgManager the orgManager to set
	     */
	    public void setOrgManager(OrgManager orgManager) {
	        this.orgManager = orgManager;
	    }
	    /**
	     * @param roleManager the roleManager to set
	     */
	    public void setRoleManager(RoleManager roleManager) {
	        this.roleManager = roleManager;
	    }

		@Override
		public PrivMenuBO getPrivMenuBycode(String code) {
			// TODO Auto-generated method stub
			return privilegeMenuManager.getMenuByCode(code);
		}

		@Override
		public boolean hasMenuCode(String code) throws BusinessException {
			User user = AppContext.getCurrentUser();
	        if (user == null) {
	            throw new BusinessException("没有用户登录");
	        }
	        String resourceJsonStr = user.getResourceJsonStr();
			if(resourceJsonStr!=null && resourceJsonStr.contains(code)){
	    		return true;
	    	}
	        return false;
		}
		
	    /**
	     * 添加业务生成器菜单时 ，同时保持数据到个性化菜单信息表
	     * @param auth 选人界面传来的人员信息 Member|1234567,Departmet|2345678...
	     * @param menuId 添加的菜单id
	     * @throws BusinessException
	     */
		@Override
		public void insertCustomizeMenus(String auth, Long menuId) throws BusinessException {
			// 调用OrgHelper接口获取到具体人员信息
			List<V3xOrgMember> orgmemberList = OrgHelper.getMembersByElements(auth);
			PrivMenuBO menu=findMenuById(menuId);
			pluginMenuManager.appendBizMenuToPortal(orgmemberList, menu);
			//添加业务门户到信息门户导航
			pluginMenuManager.appendBizNavToMobileMasterPortal(menu,auth);
		}

		@Override
		public List<PrivMenuBO> getMenusOfMemberForM1(Long memberId, Long accountId) throws BusinessException {
			// TODO Auto-generated method stub
			return privilegeMenuManager.getMenusOfMemberForM1(memberId, accountId);
		}

		public void setPrivilegeMenuManager(PrivilegeMenuManager menuManager) {
			this.privilegeMenuManager = menuManager;
		}
}
