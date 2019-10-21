/**
 * $Author: sunzhemin $
 * $Rev: 50897 $
 * $Date:: 2015-07-24 10:43:52#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.manager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.authenticate.domain.UserHelper;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.common.cache.CacheObject;
import com.seeyon.ctp.common.config.manager.ConfigManager;
import com.seeyon.ctp.common.constants.ProductEditionEnum;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.exceptions.InfrastructureException;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.plugin.PluginDefinition;
import com.seeyon.ctp.common.po.config.ConfigItem;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.login.logonlog.manager.LogonLogManager;
import com.seeyon.ctp.login.po.LogonLog;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.bo.CompareMenuSortId;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.MemberRole;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;
import com.seeyon.ctp.privilege.bo.PrivTreeNodeBO;
import com.seeyon.ctp.privilege.dao.MenuDao;
import com.seeyon.ctp.privilege.dao.PrivilegeCache;
import com.seeyon.ctp.privilege.dao.RoleMenuDao;
import com.seeyon.ctp.privilege.enums.MenuTypeEnums;
import com.seeyon.ctp.privilege.enums.PrivMenuTypeEnums;
import com.seeyon.ctp.privilege.po.PrivMenu;
import com.seeyon.ctp.privilege.po.PrivRoleMenu;
import com.seeyon.ctp.util.CTPExecutor;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.json.JSONUtil;

/**
 * <p>
 * Title: 菜单操作接口的实现类
 * </p>
 * <p>
 * Description: 菜单对象查询和更新的接口方法
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: seeyon.com
 * </p>
 */
@ProcessInDataSource(name = DataSourceName.BASE)
public class PrivilegeMenuManagerImpl extends AbstractSystemInitializer implements PrivilegeMenuManager {

	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(PrivilegeMenuManagerImpl.class);

	private final static Log logger = LogFactory.getLog(PrivilegeMenuManagerImpl.class);
	static String[][] sysCtpConfig = new String[4][3];
	static String[][] sysDisconfig = new String[2][2];
	static String[] m1MenuConfig=new String[]{"M1_signatuerServer","M1_accountChangeImg","M1_mobileOfficeOrgAuth","M1_sysChangeImg","M1_lbsSupplier","M01_orgAuth","M1_lbsAuthor","M1_msignature","M01_mobileLoginType","M01_officeAuth","M1_clientBind","CMP_mobileAppMgr"};
	static String[] m3MenuConfig=new String[]{"m3_monitoring","m3_pushConfig","m3_isignatureServer","m3_sys_startpage","m3_isignatureUserKey","m3_orgAuth","m3_connectModeSet","m3_sys_homeSkinConfig","m3_startpage","m3_orgBind","m3_mobileOfficeGuide","m3_homeSkinConfig","m3_homeSkinConfig","m3_hotDeployment","m3_bannerManager","m3_lbsAuthor"};
	private static final String UNDERLINE = "_";
	static {
		sysCtpConfig[0] = new String[] { "system_ctp_config", "dee.enable", "dee004" };
		sysCtpConfig[1] = new String[] { "system_ctp_config", "didicar.isGroupControl.isNeed",
				"F21_didi_callcar_account" };
		sysCtpConfig[2] = new String[] { "system_ctp_config", "didicar.isGroupControl.isNeed", "F21_didi_group" };
		sysCtpConfig[3] = new String[] { "system_ctp_config", "didicar.isGroupControl.isNeed",
				"F21_didi_use_money_group" };

	}
	
	static {
		sysDisconfig[0] = new String[] { "salary", "F03_hrSalary"};
		sysDisconfig[1] = new String[] { "addressbook", "F12_addressbook,F12_addressbookK"};
	}

	private CacheObject<Long> BizLastModity = null;

	private ConfigManager configManager;

	private FormMenuManager formMenuManager;

	/**
	 * 人员与业务生成器菜单缓存 key:人员ID_单位ID，value:有权限的菜单Map
	 */
	private Map<String, Map<Long, PrivMenuBO>> member2MenusMap = new ConcurrentHashMap<String, Map<Long, PrivMenuBO>>();
	
	private Map<String, Map<Long, PrivMenuBO>> innerMember2MenusMap = new ConcurrentHashMap<String, Map<Long, PrivMenuBO>>();

	private Map<String, Long> memberBizDate = new ConcurrentHashMap<String, Long>();
	
	private Map<String, Long> innerMemberBizDate = new ConcurrentHashMap<String, Long>();

	private CacheMap<String, Long> memberMenuLastDate; //最新的用户RBAC更新时间
	
	private CacheMap<String, Long> innerMemberMenuLastDate; //最新的用户RBAC更新时间

	private Map<String, Long> memberMenuLocalLastDate= new ConcurrentHashMap<String, Long>();//本地最新的用户RBAC更新时间
	
	private Map<String, Long> innerMemberMenuLocalLastDate= new ConcurrentHashMap<String, Long>();//本地最新的用户RBAC更新时间

	private Map<String, Long> memberOrgDate = new ConcurrentHashMap<String, Long>();
	
	private Map<String, Long> innerMemberOrgDate = new ConcurrentHashMap<String, Long>();

	/*
	 * v6.0权限修改 private MenuResourceDao menuResourceDao;
	 */
	private MenuDao menuDao; //菜单Dao接口

	private OrgManager orgManager;//组织模型接口

	private OrgCache  orgCache;

	private PrivilegeCache privilegeCache;	//权限缓存接口

	private RoleMenuDao roleMenuDao; //角色菜单接口
	
	private LogonLogManager logonLogManager;

	private Map<Long, PrivMenuBO> addMenus(Long memberId, Long accountId, long bizLastModify, long orgLastModify)
			throws BusinessException {
		
		String key = memberId.toString() + UNDERLINE + accountId.toString();
		Map<Long, PrivMenuBO>  privMenuBOMap= innerMember2MenusMap.get(key);

		if ( null!=privMenuBOMap && !this.getInnerMenuValidity(memberId, accountId)
				&& this.validateInnerMemberMenuLastDate(memberId, accountId)) { 
			return privMenuBOMap;
		} else {
			long startTime = System.currentTimeMillis();
			
			Map<Long, PrivMenuBO> menus = this.getByMember0(memberId, accountId);
			
			logger.debug("重新加载人员菜单缓存：" + memberId + "," + (System.currentTimeMillis() - startTime) + "ms");
			
			member2MenusMap.put(key, menus);
			innerMember2MenusMap.put(key, menus);
			
			this.updateLocalInnerMemberMenuLastDate(memberId, accountId);
			return menus;
		}
	}

	private Set<String> childNodeList(List<PrivTreeNodeBO> list) {
		Set<String> id2PrivTreeNodeBO = new HashSet<String>();
		for (PrivTreeNodeBO privTreeNodeBO2 : list) {
			id2PrivTreeNodeBO.add(privTreeNodeBO2.getpIdKey().split("_")[1]);
		}
		return id2PrivTreeNodeBO;
	}

	@Override
	public void cleanPrivData(Long roleId) throws BusinessException {
		if (roleId != null) {
			Long[] roleIds = { roleId };
			Map<Long, PrivMenuBO> menuMap = this.getByRoleWithoutParent(roleIds);
			if (menuMap.size() > 0) {
				Collection<PrivMenuBO> menusTemp = menuMap.values();
				Long[] menuIds = new Long[menusTemp.size()];
				int i = 0;
				for (PrivMenuBO menu : menusTemp) {
					if(menu.getType()!=null && menu.getType() == 0){
						logger.info("UserId: "+AppContext.getCurrentUser().getId()+"roleId :"+roleId +"menuId :"+menu.getId() + "线程ID:" + Thread.currentThread().getName()+",系统预置菜单要被删除了！！！！！！！！！。");
						throw new BusinessException("UserId: "+AppContext.getCurrentUser().getId()+"roleId :"+roleId +"menuId :"+menu.getId() + "线程ID:" + Thread.currentThread().getName()+",系统预置菜单要被删除了！！！！！！！！！。");
					}
					menuIds[i] = menu.getId();
					i++;
				}
				this.deleteMenu(menuIds);
			}

		}
	}

	@Override
	public void copyMenus(String fromVersion, String toVersion) throws BusinessException {
		// 查找被复制版本的所有菜单
		PrivMenuBO menu;
		List<PrivMenuBO> menus = getMenusVersion(fromVersion);
		//
		List<PrivMenu> menusTemp = new ArrayList<PrivMenu>();
		List<PrivMenuBO> menuBOsTemp = new ArrayList<PrivMenuBO>();
		/*
		 * v6.0权限修改 List<PrivMenuResource> menuReses = null; PrivMenuResource
		 * menuResource = null; List<PrivMenuResource> menuResesNew = new
		 * ArrayList<PrivMenuResource>();
		 */
		Map<Long, Long> oldAndNewMenuId = new ConcurrentHashMap<Long, Long>();
		Long oldId = null;
		for (PrivMenuBO privMenuBO : menus) {
			// 查找菜单的菜单资源关系数据
			/*
			 * v6.0权限修改 menuResource = new PrivMenuResource();
			 * menuResource.setMenuid(privMenuBO.getId()); menuReses =
			 * menuResourceDao.selectMenuResources(menuResource);
			 */
			//
			oldId = privMenuBO.getId();
			try {
				if (findById(privMenuBO.getId()) == null) {
					continue;
				}
				privMenuBO = (PrivMenuBO) findById(privMenuBO.getId()).clone();

			} catch (CloneNotSupportedException e) {
				throw new BusinessException("复制资源树时发生异常");
			}
			privMenuBO.setNewId();
			oldAndNewMenuId.put(oldId, privMenuBO.getId());
			if (oldAndNewMenuId.get(privMenuBO.getParentId()) != null) {

			}
			// 设置菜单资源关系的菜单ID

			/*
			 * v6.0权限修改 for (PrivMenuResource privMenuResource : menuReses) {
			 * privMenuResource.setMenuid(privMenuBO.getId()); }
			 */
			privMenuBO.setExt3(toVersion);
			menuBOsTemp.add(privMenuBO);
			menusTemp.add(privMenuBO.toPO());
			/*
			 * v6.0权限修改 menuResesNew.addAll(menuReses);
			 */
		}
		// 处理复制菜单的父目录
		Long newpId = null;
		for (PrivMenuBO menuBO : menuBOsTemp) {
			newpId = oldAndNewMenuId.get(menuBO.getParentId());
			if (newpId != null) {
				menuBO.setParentId(newpId);
			}
		}
		// 删除原版本的配置数据
		menu = new PrivMenuBO();
		menu.setExt3(toVersion);
		List<PrivMenuBO> menus4Delete = findMenus(menu);
		Long[] menuIds = new Long[menus4Delete.size()];
		PrivMenuBO menu4Delete = null;
		for (int i = 0; i < menus4Delete.size(); i++) {
			menu4Delete = menus4Delete.get(i);
			menuIds[i] = menu4Delete.getId();
		}
		menuDao.deleteMenu(menuIds);
		//
		menuDao.insertMenuPatchAll(menusTemp);
		privilegeCache.updateMenuAll(menuBOsTemp);
		/*
		 * v6.0权限修改 menuResourceDao.insertMenuResourcePatchAll(menuResesNew);
		 * privilegeCache.updateMenuResource(menuResesNew, null);
		 */
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.seeyon.ctp.privilege.manager.MenuManager#create(com.seeyon.ctp.
	 * privilege.po.PrivMenu)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public PrivMenuBO create(PrivMenuBO menu) throws BusinessException {
		if (menu != null) {
			// 处理菜单的版本为空字符串的情况
			if (StringUtils.isBlank(menu.getExt3())) {
				menu.setExt3(null);
			}
			// 父菜单
			//if (StringUtils.isBlank(menu.getPath())) {
			//	PrivMenuBO parent = privilegeCache.getMenuById(menu.getParentId());
			//	menu = getMenuPath(menu, parent);
			//}
			menu.setIdIfNew();
			Long resultId = menuDao.insertMenu(menu.toPO());
			menu.setId(resultId);
			menu.setEnterResourceId(resultId);
			privilegeCache.updateMenu(menu);
		}
		return menu;
	}

	@Override
	public void createPatch(List<PrivMenuBO> menus) throws BusinessException {
		if (menus != null) {
			for (PrivMenuBO menu : menus) {
				create(menu);
			}
		}
	}

	@Override
	public boolean deleteMenu(Long[] menus) throws BusinessException {
		// 删除菜单表数据
		menuDao.deleteMenu(menus);
		privilegeCache.deleteMenu(menus);
		// 删除菜单资源表数据
		/*
		 * v6.0权限修改 PrivMenuResource menuResource = null;
		 */
		for (int i = 0; i < menus.length; i++) {
			deleteMenu(findById(menus[i]));
			/*
			 * v6.0权限修改 menuResource = new PrivMenuResource();
			 * menuResource.setMenuid(menus[i]);
			 * menuResourceDao.deleteMenuResource(menuResource);
			 */
			privilegeCache.deleteMenu(menus[i]);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.seeyon.ctp.privilege.manager.MenuManager#delete(com.seeyon.ctp.
	 * privilege.po.PrivMenu)
	 */
	@Override
	public boolean deleteMenu(PrivMenu menu) throws BusinessException {
		boolean result = false;
		if (menu != null && menu.getId() != null) {
			List<PrivMenu> menus = null;
			PrivMenu menuTemp = findById(menu.getId());
			if (menuTemp != null) {
				PrivMenu menuNew = new PrivMenu();
				menuNew.setPath(menuTemp.getPath());
				menuNew.setExt3(menuTemp.getExt3());
				menus = menuDao.selectListByPath(menuNew, null);
			}
			if (menus != null) {
				for (PrivMenu privMenu : menus) {
					// 删除菜单表数据
					result = menuDao.deleteMenu(privMenu);
					// 删除菜单资源表数据
					/*
					 * v6.0权限修改 PrivMenuResource menuResource = new
					 * PrivMenuResource();
					 * menuResource.setMenuid(privMenu.getId());
					 * menuResourceDao.deleteMenuResource(menuResource);
					 */
					privilegeCache.deleteMenu(privMenu.getId());
				}
			}
		}
		return result;
	}

	@Override
	public boolean deleteMenuByParentId(Long menu) throws BusinessException {
		List<PrivMenu> menus = findSubMenus(menu);
		// 同时删除父菜单
		Long[] menuIds = new Long[menus.size() + 1];
		int i = 0;
		for (PrivMenu menuPO : menus) {
			menuIds[i] = menuPO.getId();
			i++;
		}
		menuIds[i] = menu;
		menuDao.deleteMenu(menuIds);
		privilegeCache.deleteMenu(menuIds);
		return true;
	}

	@Override
	public void destroy() {

	}

	public PrivMenuBO findById(Long menuId) {
		return privilegeCache.getMenuById(menuId);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public FlipInfo findMenus(FlipInfo fi, Map param) {
		List<PrivMenuBO> privMenuBOs = new ArrayList<PrivMenuBO>();
		PrivMenuBO privMenuBO = null;
		// PrivResourceBO resBO = null;
		Long enterId = null;
		PrivMenu privMenu = new PrivMenu();
		privMenu = (PrivMenu) ParamUtil.mapToBean(param, privMenu, true);
		// 当前回到第一页
		if (param.get("refreshCurPage") != null) {
			fi.setPage(1);
		}
		// 默认选取一级菜单
		String level = "1";
		// 查找下一级菜单的情况
		if (param.get("id") != null) {
			privMenuBO = privilegeCache.getMenuById(privMenu.getId());
			if (privMenuBO != null) {
				privMenu.setPath(privMenuBO.getPath());
				level = String.valueOf(Integer.parseInt(privMenuBO.getExt2()) + 1);
			}
		}
		privMenu.setExt2(level);
		// 查询数据库得到列表
		List<PrivMenu> privMenus = menuDao.selectListByPath(privMenu, fi);
		// 将po转换为bo
		for (PrivMenu menu : privMenus) {
			privMenuBO = privilegeCache.getMenuById(menu.getId());
			if (privMenuBO != null) {
				enterId = privMenuBO.getEnterResourceId();
				PrivMenuBO resBO = privilegeCache.getMenuById(enterId);
				// 入口资源名称
				if (resBO != null) {
					privMenuBO.setEnterResourceName(resBO.getExt7());
				}
				// 是否是虚节点
				privMenuBO.setIsVirtualNode();
				// 菜单级别
				privMenuBO.setMenuLevel();
				privMenuBOs.add(privMenuBO);
			} else {
				privMenuBOs.add(new PrivMenuBO(menu));
			}
		}
		fi.setData(privMenuBOs);
		return fi;
	}

	@Override
	public List<PrivMenuBO> findMenus(PrivMenuBO menu) {

		Map<Long, PrivMenuBO>  returnmenus = new HashMap<Long, PrivMenuBO>();
		List<PrivMenuBO> privMenuBOs = new ArrayList<PrivMenuBO>();
		
		// 没有条件的情况直接查询数据库
		if (menu == null) {
			List<PrivMenuBO> allMenu = privilegeCache.getAllMenu();
			for (PrivMenuBO privMenuBO : allMenu) {
				returnmenus.put(privMenuBO.getId(),privMenuBO);
			}
		} else {
			PrivMenuBO menuBO = null;
			for (PrivMenu privMenu : menuDao.selectList(menu)) {
				menuBO = findById(privMenu.getId());
				// 如果缓存没有找到菜单对象
				if (menuBO == null) {
					menuBO = new PrivMenuBO(privMenu);
				}
				returnmenus.put(menuBO.getId(),menuBO);
			}
		}
		// 过滤开关项关掉的菜单
		List<PrivMenuBO> dislist = this.getConfigDisableMenu();
		for (PrivMenuBO privMenuBO : dislist) {
			returnmenus.remove(privMenuBO.getId());
		}
		for (Entry<Long, PrivMenuBO> entry : returnmenus.entrySet()) {  
			PrivMenuBO bo  = entry.getValue();  
			privMenuBOs.add(bo);
	     }  
		return privMenuBOs;
	}

	/**
	 * 根据入口资源的ID查询菜单
	 *
	 * @param resId
	 * @return
	 */
	public List<PrivMenuBO> findMenusbyEnterRes(Long resId) {
		List<PrivMenuBO> list = new ArrayList<PrivMenuBO>();
		Map<Long, Long> map = privilegeCache.findMenuEnterSource();
		Set<Long> keySet = map.keySet();
		for (Long long1 : keySet) {
			if (map.get(long1).equals(resId)) {
				PrivMenuBO findById = findById(long1);
				if (findById != null) {
					list.add(findById);
				}
			}
		}
		return list;
	}

	/**
	 * 根据入口资源的ID查询菜单传入map
	 *
	 * @param resId
	 * @return
	 */
	private List<PrivMenuBO> findMenusbyEnterRes(Long resId, Map<Long, List<Long>> menuIds2ResId) {
		List<PrivMenuBO> list = new ArrayList<PrivMenuBO>();

		List<Long> tempmenuIds = menuIds2ResId.get(resId);
		if (tempmenuIds != null) {
			for (Long menuid : tempmenuIds) {
				PrivMenuBO findById = findById(menuid);
				if (findById != null) {
					list.add(findById);
				}
			}
		}
		return list;
	}

	public Map<Long, PrivMenuBO> findMenusByExt4(PrivMenuBO menu) {
		Map<Long, PrivMenuBO> map = new HashMap<Long, PrivMenuBO>();
		List<PrivMenuBO> allMenus = this.findMenus(null);
		for (PrivMenuBO privMenuBO : allMenus) {
			if (privMenuBO.getExt4().equals(menu.getExt4())) {
				map.put(privMenuBO.getId(),privMenuBO);
			}
		}
		return map;
	}

	@Override
	public Long findParentMenu(PrivMenuBO menu) throws BusinessException {
		return menuDao.findParentMenu(menu);
	}

	@Override
	public List<PrivMenu> findSubMenus(Long menu) throws BusinessException {
		List<PrivMenu> menus = new ArrayList<PrivMenu>();
		PrivMenu menuTemp = findById(menu);
		if (menuTemp != null) {
			PrivMenu menuNew = new PrivMenu();
			menuNew.setId(menu);
			menuNew.setPath(menuTemp.getPath());
			menuNew.setExt3(menuTemp.getExt3());
			menus = menuDao.selectListByPath(menuNew, null);
		}
		return menus;
	}

	@Override
	public HashSet<Long> findUnModifiable() {
		HashSet<Long> result = new HashSet<Long>();
		// List<PrivMenu> menus =menuDao.selectUnModifiable();
		List<PrivMenuBO> allMenus = this.findMenus(null);
		for (PrivMenuBO privMenuBO : allMenus) {
			if (privMenuBO.getExt5() != null && privMenuBO.getExt5().equals(0)) {
				result.add(privMenuBO.getId());
			}
		}
		return result;
	}

	@Override
	public Map<Long, PrivRoleMenu> findUnModifiableRoleMenuByRole(Long role) {

		Map<Long, PrivRoleMenu> result = new ConcurrentHashMap<Long, PrivRoleMenu>();
		Long menuId = null;
		List<PrivRoleMenu> roleMenus = roleMenuDao.selectUnModifiableByRole(role);
		for (PrivRoleMenu privRoleMenu : roleMenus) {
			menuId = privRoleMenu.getMenuid();
			result.put(menuId, privRoleMenu);
		}
		return result;
	}

	@Override
	public List<PrivMenuBO> getAllocatedDisableMenu() throws BusinessException {
		List<PrivMenuBO> list = new ArrayList<PrivMenuBO>();
		List<PrivMenu> rlist = menuDao.selectDisable();
		for (PrivMenu menu : rlist) {
			// if(menuBO.getExt15()==0){
			// list.add(menuBO);
			// }
			PrivMenuBO pm = new PrivMenuBO(menu);
			list.add(pm);
		}
		return list;
	}

	public List<PrivMenuBO> getBusinessMenuByMember(Long memberId, Long AccountId) throws BusinessException {
		List<PrivMenuBO> returnlist = new ArrayList<PrivMenuBO>();
		Map<Long, PrivMenuBO> menumap = getByMember(memberId, AccountId);
		Set<Long> set = menumap.keySet();
		for (Long long1 : set) {
			PrivMenuBO privMenuBO = menumap.get(long1);
			if (null!=privMenuBO && privMenuBO.getExt12() != null && privMenuBO.getExt12() != 0 && privMenuBO.getExt12() != -3
					&& "1".equals(privMenuBO.getExt2())) {
				returnlist.add(privMenuBO);
			}
		}
		return returnlist;
	}

	@Override
	public Set<PrivMenuBO> getAllBusinessMenusFirstLevel() throws BusinessException {
		Set<PrivMenuBO> returnSet = new HashSet<PrivMenuBO>();
		List<PrivMenuBO> allMenuList = privilegeCache.getAllMenu();
		for (PrivMenuBO privMenuBO:allMenuList) {
			if (null!=privMenuBO && privMenuBO.getExt12() != null && privMenuBO.getExt12() != 0 && privMenuBO.getExt12() != -3
					&& "1".equals(privMenuBO.getExt2())) {
				returnSet.add(privMenuBO);
			}
		}
		return returnSet;
	}

	@Override
	public Set<PrivMenuBO> getMemberBusinessMenusFirstLevel(String menuName) throws BusinessException {
		Set<PrivMenuBO> returnset = new HashSet<PrivMenuBO>();
		Long memberId=AppContext.currentUserId();
		Long AccountId=AppContext.getCurrentUser().getLoginAccount();
		Map<Long, PrivMenuBO> menumap = getByMember(memberId, AccountId);
		Set<Long> set = menumap.keySet();
		for (Long long1 : set) {
			PrivMenuBO privMenuBO = menumap.get(long1);
			if (null!=privMenuBO && privMenuBO.getExt12() != null && privMenuBO.getExt12() != 0 && privMenuBO.getExt12() != -3
					&& "1".equals(privMenuBO.getExt2())) {
				if(Strings.isNotBlank(menuName)&&Strings.isNotBlank(privMenuBO.getName())){
					String privMenuName = ResourceUtil.getString(privMenuBO.getName());
					if(privMenuName.indexOf(menuName)>-1){
						returnset.add(privMenuBO);
					}
				}else {
					returnset.add(privMenuBO);
				}
			}
		}
		return returnset;
	}

	@Override
	public List<PrivMenuBO> getAllBusinessMenuByMember(Long memberId, Long AccountId) throws BusinessException {
		List<PrivMenuBO> returnlist = new ArrayList<PrivMenuBO>();
		Map<Long, PrivMenuBO> menumap = getByMember(memberId, AccountId);
		Set<Long> set = menumap.keySet();
		for (Long long1 : set) {
			PrivMenuBO privMenuBO = menumap.get(long1);
			if (null!=privMenuBO && privMenuBO.getExt12() != null && privMenuBO.getExt12() != 0 && privMenuBO.getExt12() != -3) {
				//不仅仅是获取一级菜单，是将有权限的都返回
				returnlist.add(privMenuBO);
			}
		}
		return returnlist;
	}

	public List<PrivMenuBO> getBusinessMenuByMember(Long memberId, Long AccountId, Boolean containLinkSystem)
			throws BusinessException {
		List<PrivMenuBO> returnlist = new ArrayList<PrivMenuBO>();
		Map<Long, PrivMenuBO> menumap = getByMember(memberId, AccountId);
		Set<Long> set = menumap.keySet();
		for (Long long1 : set) {
			PrivMenuBO privMenuBO = menumap.get(long1);
			if (privMenuBO.getExt12() != null && privMenuBO.getExt12() != 0
					&& "1".equals(privMenuBO.getExt2())) {
				if (!containLinkSystem && privMenuBO.getExt12() == -3) {
					continue;
				} else {
					returnlist.add(privMenuBO);
				}

			}

		}
		return returnlist;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.seeyon.ctp.privilege.manager.MenuManager#getByMember(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public Map<Long, PrivMenuBO> getByMember(Long memberId, Long accountId) throws BusinessException {
		/*String ctxKey = "Menu-" + memberId + "-" + accountId;
		Map<Long, PrivMenuBO> menu = (Map<Long, PrivMenuBO>) AppContext.getSessionContext(ctxKey);

		if (menu == null) {
			menu = this.getMenus(memberId, accountId);

			AppContext.putSessionContext(ctxKey, menu);
		}

		return menu;*/
		return this.getMenus(memberId, accountId);
	}

	public Map<Long, PrivMenuBO> getByMember0(Long memberId, Long accountId) throws BusinessException { 
		boolean isAccountAdministrator = false;
		boolean isHRadminDeptadmin = false;
		boolean isHR = false;
		boolean isDept = false;
		boolean isAtte = false; //是否考勤管理
		Map<Long, PrivMenuBO> result = null;

		V3xOrgMember member  = orgManager.getMemberById(memberId);
		//如果是Guest账号，取可以看到的资源的菜单。
		if(member.isGuest()){
			return getMenusByResource(getDefaultGuestResource());
		}
		/*
		 * 取菜单的规则是： 1. 当前登录单位的角色对应的菜单（区分单位） 2. 所有有权限的业务生成器菜单（不区分单位） 3. 集团角色
		 */
		List<MemberRole> roles = orgManager.getMemberRoles(memberId, null);

		if (null != roles) {
			Set<Long> roleIds = new HashSet<Long>();

			for (MemberRole memberRole : roles) {
				V3xOrgRole role = memberRole.getRole();
				if (role != null) {
					if (role.getBond() == OrgConstants.ROLE_BOND.BUSINESS.ordinal()) {
						// 业务生成器的留下
					} else if (role.getBond() == OrgConstants.ROLE_BOND.REPORTSPACE.ordinal()) {
						// 报表空间的留下
					} else if (Strings.equals(role.getOrgAccountId(), OrgConstants.GROUPID)) {
						// 集团的留下
					} else if (Strings.equals(role.getOrgAccountId(), accountId)) {
						// 当前单位的留下
					}else if(OrgConstants.Role_NAME.BusinessOrganizationManager.name().equals(role.getCode())){
						//多维组织角色
					}else {
						continue;
					}

					roleIds.add(role.getId());

					if (role.getCode().equals(OrgConstants.Role_NAME.AccountAdministrator.name())) {
						isAccountAdministrator = true;
					}
					if (role.getCode().equals(OrgConstants.Role_NAME.HrAdmin.name())) {
						isHR = true;
					}
					if (role.getCode().equals(OrgConstants.Role_NAME.DepAdmin.name())) {
						isDept = true;
					}
					if (role.getCode().equals(OrgConstants.Role_NAME.AttendanceAdmin.name())) {
						isAtte = true;
					}
				}
			}


			result = this.getByRole(roleIds.toArray(new Long[roleIds.size()]));
			// V6.0权限改造 停用 新的权限中只需显示已经给予角色的菜单，没有给予角色的菜单直接不显示
			List<PrivMenuBO> dislist = getConfigDisableMenu();
			// 过滤业务生成器无权限的菜单
			Map<Long, PrivMenuBO> allBizPrivMenuBO = new ConcurrentHashMap<Long, PrivMenuBO>();
			Map<V3xOrgRole, List<PrivMenuBO>> role2MenuMap = new ConcurrentHashMap<V3xOrgRole, List<PrivMenuBO>>();

			for (MemberRole memberRole : roles) {
				if (memberRole.getRole().getBond() == OrgConstants.ROLE_BOND.BUSINESS.ordinal()) {
					Map<Long, PrivMenuBO> byRole = getByRole(new Long[] { memberRole.getRole().getId() });

					allBizPrivMenuBO.putAll(byRole);

					role2MenuMap.put(memberRole.getRole(), new ArrayList<PrivMenuBO>(byRole.values()));
				}
			}

			List<PrivMenuBO> checkMenuAuth = new ArrayList<PrivMenuBO>();
			if(formMenuManager != null){
				checkMenuAuth  = formMenuManager.checkMenuAuth(memberId, accountId, role2MenuMap);// 有权限的
			}

			// 业务生成器菜单，总菜单 - 有权限的 = 无权限的
			for (PrivMenuBO privMenuBO : checkMenuAuth) {
				allBizPrivMenuBO.remove(privMenuBO.getId());
			}

			dislist.addAll(allBizPrivMenuBO.values());

			// 过滤停用的菜单
			if (dislist != null) {
				for (PrivMenuBO privMenuBO : dislist) {
					result.remove(privMenuBO.getId());
				}
			}
		}

        // 如果人员同时有HR管理员、部门管理员角色和考勤管理员，将部门管理员的考勤管理、人员管理、组管理屏蔽
		
		if(AppContext.hasPlugin("hr")||AppContext.hasPlugin("attendance")){
			if (isHR && isDept & isAtte) {
				 result.remove(2658155521836796587L);
		         result.remove(858006849844301311L);
		         result.remove(6231040950096315635L);
			}else if(isDept & isAtte){
				 result.remove(2658155521836796587L);
			}else if (isHR && isDept){
				result.remove(858006849844301311L);
		        result.remove(6231040950096315635L);
			}
		}

        //A6-S屏蔽的菜单
        Integer productId = SystemProperties.getInstance().getIntegerProperty("system.ProductId");
        if (productId != null && productId.intValue() == ProductEditionEnum.a6s.ordinal()) {
            result.remove(6692870513281941850L);//HR管理-组织机构设置
            result.remove(7464083475998789371L);//HR管理-员工档案管理
            result.remove(-2933570987085508976L);//HR管理-统计分析
            result.remove(-6400113376465108785L);//HR管理-信息项设置
        }
        //A6屏蔽协同驾驶舱菜单
		if (((productId != null) && (productId.intValue() == ProductEditionEnum.a6s.ordinal()))
				|| ((productId != null) && (productId.intValue() == ProductEditionEnum.a6.ordinal()))
				|| ((productId != null) && (productId.intValue() == ProductEditionEnum.a6p.ordinal()))
				|| ((productId != null) && (productId.intValue() == ProductEditionEnum.a6u8.ordinal()))) {
			result.remove(Long.valueOf(7604927755250910653L));
			result.remove(Long.valueOf(-8868445576873029754L));
			result.remove(Long.valueOf(-6690882573217242240L));
		}
        
        if(ProductEditionEnum.isU8OEM()){
        	//屏蔽集成平台配置
        	 result.remove(-1726650702758329325L);//系统注册
             result.remove(-1461605504161199936L);//用户管理
             result.remove(1619292301944899707L);//消息配置
             result.remove(-4628815616839168895L);//待办配置
             result.remove(-2176243471020961962L);//门户配置
            //屏蔽移动office授权
             result.remove(7811846044579348610L);//移动office授权
            //屏蔽应用中心设置
             result.remove(-329292124654313901L);//云应用中心设置
        }

        if ((Boolean) (SysFlag.sys_isGroupVer.getFlag()) && isAccountAdministrator) {
            V3xOrgAccount a = orgManager.getAccountById(accountId);
            if (null != a && !a.isCustomLogin()) {
                // 如果没有独立登录页配置，不出现这个菜单"登录页设计"
                result.remove(-5067444009165867712L);
            }
        }
        //视频会议263接入时屏蔽人员同步菜单  
        if(SystemEnvironment.hasPlugin("videoconference")){
            PluginDefinition pd=SystemEnvironment.getPluginDefinition("videoconference");
            if(pd!=null&&"com.seeyon.apps.videoconference.Driver.V263Driver".equals(pd.getPluginProperty("videoconference.driverClassName"))){
            		result.remove(-1690312517599083360L);
            }
        }
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.seeyon.ctp.privilege.manager.MenuManager#getByRole(java.lang.Long[])
	 */
	@Override
	public Map<Long, PrivMenuBO> getByRole(Long[] roleIds) {
		Map<Long, PrivMenuBO> menuBOs = new ConcurrentHashMap<Long, PrivMenuBO>();
		Long menuId = null;
		PrivMenuBO menu = null;
//		HashSet<Long> menus = privilegeCache.getMenuByRole(roleIds);
//		loop1: for (Iterator<Long> iterator = menus.iterator(); iterator.hasNext();) {
//			menuId = iterator.next();
//			menu = findById(menuId);

		HashSet<PrivMenuBO> menuList = privilegeCache.getMenuByRoleFonEntity(roleIds);
		loop1: for (Iterator<PrivMenuBO> iterator = menuList.iterator(); iterator.hasNext();) {
			menu=iterator.next();
			if (menu != null) {
				menuId = menu.getId();
				if (!("none").equals(menu.getResourceModuleid()) && null != menu.getResourceModuleid()) {
					String moduleId = menu.getResourceModuleid();
					boolean hasplugin = false;
					// 多个插件，同时有才启用
					if (moduleId.contains("&")) {
						String[] moduleids = moduleId.split("&");
						hasplugin = true;
						for (String moduleid : moduleids) {
							if (!AppContext.hasPlugin(moduleid)) {
								hasplugin = false;
								break;
							}
						}
						// 多个插件，有一个就启用
					} else if (moduleId.contains(",")) {
						String[] moduleids = moduleId.split(",");
						for (String moduleid : moduleids) {
							if (AppContext.hasPlugin(moduleid)) {
								hasplugin = true;
								break;
							}
						}
					} else {
						if (AppContext.hasPlugin(moduleId)) {
							hasplugin = true;
						}
					}
					if (!hasplugin) {
						continue loop1;
					}
				}

				// 处理ext3即菜单所属版本为空字符串的情况
				if (StringUtils.isBlank(menu.getExt3())) {
					menu.setExt3(null);
				}

				// 业务生成器菜单变动
				if (menu.getExt21() != null && menu.getExt21() == 1 && menu.getExt12() != null) {
                    //公告
                    if ((menu.getExt12() == 7 || menu.getExt12() == 11) && menu.getResourceNavurl() != null && menu.getResourceNavurl().contains("bulData.do")) {
                        menu.setTarget("newWindow");
                        menu.setResourceNavurl("/bulData.do?method=bulIndex&typeId=" + menu.getExt17());
                    }
                    // 新闻
                    if ((menu.getExt12() == 8 || menu.getExt12() == 12) && menu.getResourceNavurl()!=null && menu.getResourceNavurl().contains("newsData.do")) {
                        menu.setTarget("newWindow");
                        menu.setResourceNavurl("/newsData.do?method=newsIndex&boardId=" + menu.getExt17());
                    }
                    // 讨论
                    if ((menu.getExt12() == 9 || menu.getExt12() == 13)&&menu.getResourceNavurl()!=null && menu.getResourceNavurl().contains("bbs.do")) {
                        menu.setTarget("newWindow");
                        menu.setResourceNavurl("/bbs.do?method=bbsIndex&boardId=" + menu.getExt17());
                    }
                    // 调查
                    if ((menu.getExt12() == 10 || menu.getExt12() == 14)&&menu.getResourceNavurl()!=null&&menu.getResourceNavurl().contains("inquiryData.do")) {
                        menu.setTarget("newWindow");
                        menu.setResourceNavurl("/inquiryData.do?method=inquiryBoardIndex&boardId=" + menu.getExt17());
                    }

					// 新建协同菜单
					if (menu.getExt12() == 1 && menu.getExt14() != 2) {
						menu.setTarget("newWindow");
					}
				}

				menuBOs.put(menuId, menu);
			}
		}

		return menuBOs;
	}

	@Override
	public Map<Long, PrivMenuBO> getByRoleWithoutParent(Long[] roleIds) {
		Map<Long, PrivMenuBO> menuBOs = new ConcurrentHashMap<Long, PrivMenuBO>();
		HashSet<Long> menus = privilegeCache.getMenuByRoleWithoutParent(roleIds);
		Long menuId = null;
		PrivMenuBO menu = null;
		if (menus != null) {
			for (Iterator<Long> iterator = menus.iterator(); iterator.hasNext();) {
				menuId = iterator.next();
				menu = findById(menuId);
				if (menu != null) {
					// 处理ext3即菜单所属版本为空字符串的情况
					if (StringUtils.isBlank(menu.getExt3()))
						menu.setExt3(null);
					menuBOs.put(menuId, menu);
				}
			}
		}

		return menuBOs;
	}

	@Override
	public List<PrivMenuBO> getConfigDisableMenu() {
		List<PrivMenuBO> list = new ArrayList<PrivMenuBO>();
		//屏蔽插件停用时的菜单
		for (String[] obj : sysCtpConfig) {
			if ("false".equals(AppContext.getSystemProperty(obj[1]))) {
				String[] code = obj[2].split(",");
				for (String string : code) {
					PrivMenuBO menuByCode = privilegeCache.getMenuByCode(string);
					if (menuByCode != null) {
						list.add(menuByCode);
					}
				}
			}
		}
		ConfigItem item = configManager.getConfigItem("disabled_plugin", "disabled_ids");
		if(item != null){
			String configValue = item.getConfigValue();
			if(StringUtils.isNotBlank(configValue)){
				String[] plugin = configValue.split(",");
				List<String> pluginList = Arrays.asList(plugin);
				//屏蔽插件停用时的菜单
				for (String[] obj : sysDisconfig) {
					if(pluginList.contains(obj[0])){
						String  resourceCode = obj[1];
						String[] rcodes = resourceCode.split(",");
						for(String s : rcodes){
							PrivMenuBO menuByCode = privilegeCache.getMenuByCode(s);
							if (menuByCode != null) {
								list.add(menuByCode);
							}
						}
					}
				}
			}
		}
		//M1和M3不能并存，只能允許一種方式存在
		String mxVersion=SystemEnvironment.getMxVersion();
		if("M1".equals(mxVersion)){
			for(String m3code:m3MenuConfig){
				PrivMenuBO menuByCode = privilegeCache.getMenuByCode(m3code);
				if (menuByCode != null) {
					list.add(menuByCode);
				}
			}
		}else if("M3".equals(mxVersion)){
			for(String m1code:m1MenuConfig){
				PrivMenuBO menuByCode = privilegeCache.getMenuByCode(m1code);
				if (menuByCode != null) {
					list.add(menuByCode);
				}

			}
		}

		return list;
	}

	public FormMenuManager getFormMenuManager() {
		return formMenuManager;
	}

	@Override
	public List<PrivMenuBO> getListByRole(Long[] roleIds) throws BusinessException{
		return this.getListByRole(roleIds,null);
	}

	@Override
	public List<PrivMenuBO> getListByRole(Long[] roleIds,Long subMenuId) throws BusinessException{
		List<PrivMenuBO> menuBOs = new ArrayList<PrivMenuBO>();
		HashSet<PrivMenuBO> menus = privilegeCache.getMenuByRoleFonEntity(roleIds);
		PrivMenuBO menu = null;
		for (Iterator<PrivMenuBO> iterator = menus.iterator(); iterator.hasNext();) {
			menu = iterator.next();
			if (menu != null) {
				// 处理ext3即菜单所属版本为空字符串的情况
				if (StringUtils.isBlank(menu.getExt3()))
					menu.setExt3(null);
				if(null==subMenuId){
					menuBOs.add(menu);
				}else if(menu.getId().equals(subMenuId)){
					menuBOs.add(menu);
				}
			}
		}
		Collections.sort(menuBOs, CompareMenuSortId.getInstance());
		return menuBOs;
	}

	@Override
	public PrivMenuBO getMenuByCode(String code) {

		return privilegeCache.getMenuByCode(code);
	}
	public MenuDao getMenuDao() {
		return menuDao;
	}
	private Map<Integer,String> pathMap = new ConcurrentHashMap<Integer,String>(){{put(2, "00");put(3, "000");put(4, "0000");put(5, "00000");}};
    
    private String getMenuPath(Boolean isfrist){
    	if(isfrist) {
    		return pathMap.get(privilegeCache.getMenuPathLength()-1)+"1";
    	}else{
    		return pathMap.get(privilegeCache.getMenuPathLength());
    	}
	
    	
    }
	/**
	 * @param menu
	 * @param parent
	 */
	public PrivMenuBO getMenuPath(PrivMenuBO menu, PrivMenuBO parent) {
		String pathIndex = null;
		String levelString = "1";
		Integer level = 1;
		if (parent != null) {
			pathIndex = parent.getPath();
			if (pathIndex == null) {
				pathIndex = getMenuPath(parent, null).getPath();
			}
			levelString = parent.getExt2();
			level = Integer.parseInt(levelString) + 1;
		}
		pathIndex = menuDao.selectMaxPath(pathIndex, level);
		// 拼接菜单路径
		if (StringUtils.isBlank(pathIndex)) {
			pathIndex = (parent != null ? parent.getPath() : "") + this.getMenuPath(true);
		}
		// 计算path的长度
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append(this.getMenuPath(false));
		}
		DecimalFormat df = new DecimalFormat(sb.toString());
		pathIndex = df.format(Long.parseLong(pathIndex) + 1);
		// 菜单的层级
		menu.setExt2(String.valueOf(level));
		// 菜单路径
		menu.setPath(pathIndex);
		return menu;
	}
	
	@Override
	public String getMaxPath(String pathIndex,Integer level){
		if(level==null){
			level=1;
		}
		String path = menuDao.selectMaxPath(pathIndex, level);
		return path;
	}
	
	@Override
	public Boolean verifyPath(String pathIndex,Integer level){
		//获取长度
		Integer  pathLength=privilegeCache.getMenuPathLength();
		Integer tempLeagth=pathIndex.length();
		if(tempLeagth/pathLength==level&&tempLeagth%pathLength==0){
			return true;
		}
	
		return false;
	}
	
	@Override
	public Map<Long, List<Long>> getMenuResourceByRole(Long[] roleIds) {
		return privilegeCache.getMenuResourceByRole(roleIds);
	}

	@Override
	public Map<Long, PrivMenuBO> getMenus(Long memberId, Long accountId) throws BusinessException {
		long bizLastModify = this.BizLastModity.get();
		long orgLastModify = this.orgManager.getModifiedTimeStamp(null).getTime();
		
		Map<Long, PrivMenuBO> menus = addMenus(memberId, accountId, bizLastModify, orgLastModify);

		return menus;
	}

	@Override
	public Long[] getMenusByRole(Long[] roleIds)throws BusinessException{
		HashSet<Long> menus = privilegeCache.getMenuByRole(roleIds);
		return menus.toArray(new Long[menus.size()]);
	}

	@Override
	public List<PrivMenuBO> getMenusOfMember(Long memberId, Long accountId) throws BusinessException {
		List<PrivMenuBO> menus = new ArrayList<PrivMenuBO>();
		menus.addAll(this.getByMember(memberId, accountId).values());
		return menus;
	}

	@Override
	public List<PrivMenuBO> getMenusOfMemberForM1(Long memberId, Long accountId) throws BusinessException {
		List<PrivMenuBO> menus = new ArrayList<PrivMenuBO>();
		menus.addAll(this.reSetMM1Menus(memberId, accountId).values());
		return menus;
	}

	/**
	 * 得到版本所有的菜单
	 *
	 * @param version
	 * @return
	 */
	private List<PrivMenuBO> getMenusVersion(String version) {
		PrivMenuBO menu = new PrivMenuBO();
		if (!StringUtils.isBlank(version)) {
			menu.setExt3(version);
		}
		List<PrivMenuBO> menus = findMenus(menu);
		return menus;
	}

	@Override
	public boolean getMenuValidity(Long memberId, Long accountId) throws BusinessException {

		String key = memberId.toString() + UNDERLINE + accountId.toString();

		long bizLastModify = this.BizLastModity.get();
		long orgLastModify = this.orgManager.getModifiedTimeStamp(null).getTime();

		if (!Strings.equals(bizLastModify, memberBizDate.get(key))
				|| !Strings.equals(orgLastModify, memberOrgDate.get(key))) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean getInnerMenuValidity(Long memberId, Long accountId) throws BusinessException {

		String key = memberId.toString() + UNDERLINE + accountId.toString();

		long bizLastModify = this.BizLastModity.get();
		long orgLastModify = this.orgManager.getModifiedTimeStamp(null).getTime();

		if (!Strings.equals(bizLastModify, innerMemberBizDate.get(key))
				|| !Strings.equals(orgLastModify, innerMemberOrgDate.get(key))) {
			return true;
		}
		return false;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public PrivilegeCache getPrivilegeCache() {
		return privilegeCache;
	}

	@Override
	public Map<Long, PrivMenuBO> getPrivMenu4Form(Long[] ids) {
		Map<Long, PrivMenuBO> menuBOs = new ConcurrentHashMap<Long, PrivMenuBO>();
		for (Long id : ids) {
			PrivMenuBO pmBo = privilegeCache.getMenuById(id);
			if (pmBo != null) {
				// 处理ext3即菜单所属版本为空字符串的情况
				if (StringUtils.isBlank(pmBo.getExt3())){
					pmBo.setExt3(null);
				}
				menuBOs.put(pmBo.getId(), pmBo);	
			}
		}
		return menuBOs;
	}

	@Override
	public List<String> getResourceCode(Long memberId, Long accountId)throws BusinessException{
		return getResourceCode0(memberId, accountId, false);
	}
	
	public List<String> getResourceCodeOnlySystem(Long memberId, Long accountId) throws BusinessException{
		return getResourceCode0(memberId, accountId, true);
	}
	
	private List<String> getResourceCode0(Long memberId, Long accountId, boolean isOnlySystem) throws BusinessException{
		if(memberId==null||accountId==null){
			return new ArrayList<String>();
		}
		
		V3xOrgMember member = orgManager.getMemberById(memberId);
		if(member == null){
			return new ArrayList<String>();
		}
		if(member.isGuest()){//guest 账号的资源是固定的。
			return getDefaultGuestResource();
		}
		
		Map<Long, PrivMenuBO> resources = this.getByMember(memberId, accountId);
		  List<String> resList = new ArrayList<String>();
		  for (PrivMenuBO res : resources.values()) {
		      String code = res.getResourceCode();
		      if(code == null || (isOnlySystem && code.startsWith("000_"))){
		    	  continue;
		      }
		      resList.add(code);
		  }
		  return resList;
	}

	public RoleMenuDao getRoleMenuDao() {
		return roleMenuDao;
	}

	@Override
	public List<PrivMenuBO> getShortCutMenuOfMember(Long memberId, Long accountId) throws BusinessException {

		List<PrivMenuBO> returnlist = new ArrayList<PrivMenuBO>();
		/*
		 * Map<Long, PrivMenuBO> menumap = getByMember(memberId,accountId);
		 * Set<Long> set = menumap.keySet(); for (Long long1 : set) { PrivMenuBO
		 * privMenuBO = menumap.get(long1); if(privMenuBO.getExt4()!=2){ //
		 * privMenuBO.setExt4(2);
		 * privMenuBO.setResourceCode(privMenuBO.getResourceCode()+"K"); }
		 * returnlist.add(privMenuBO); }
		 */
		return returnlist;

	}

	@Override
	public Map<String, List<PrivTreeNodeBO>> getTreeNodes(String memberId, String accountId, String roleId,
			String showAll, String version, String appResCategory, String isAllocated,
			List<PrivTreeNodeBO> treeNodes4Back0, List<PrivTreeNodeBO> treeNodes4Front0, boolean isCheckBusiness)
			throws BusinessException {
		List<PrivTreeNodeBO> treeNodes4Back = new ArrayList<PrivTreeNodeBO>();
		List<PrivTreeNodeBO> treeNodes4Front = new ArrayList<PrivTreeNodeBO>();
		Map<String, List<PrivTreeNodeBO>> hasMap = new ConcurrentHashMap<String, List<PrivTreeNodeBO>>();
		User user = AppContext.getCurrentUser();
		// 当前登录人员拥有的所有菜单列表
		Map<Long, PrivMenuBO> menus = new HashMap<Long, PrivMenuBO>();
		// 不可修改的角色资源关系列表
		Map<Long, PrivRoleMenu> roleResUnEditable = null;
		// 如果人员ID和单位ID不为空则为查看所拥有的资源
		if (memberId != null && accountId != null) {
			Long memId = Long.parseLong(memberId);
			Long accId = Long.parseLong(accountId);
			// 获得当前人员关联的菜单
			menus = this.getByMember(memId, accId);
		} else if (roleId != null) {
			Long[] roleIds = new Long[1];
			if (roleId != null) {
				roleIds[0] = Long.parseLong(roleId);
			}
			// 获得当前角色包含的菜单
			Map<Long, PrivMenuBO> menuMap = this.getByRole(roleIds);
			if (menuMap != null && menuMap.size() != 0) {
				menus = menuMap;
			}
			// 查找不能修改的角色资源关系
			roleResUnEditable = this.findUnModifiableRoleMenuByRole(Long.parseLong(roleId));
		} else if (showAll != null) {
			// 如果人员ID和单位ID为空则为查看所有资源
			PrivMenuBO menu = new PrivMenuBO();
			if (!StringUtils.isBlank(appResCategory)) {
				menu.setExt4(Integer.parseInt(appResCategory));
			}
			menus = this.findMenusByExt4(menu);
		} else {
			// 如果人员ID和单位ID为空则为查看所有资源
			PrivMenuBO menu = new PrivMenuBO();
			if (!StringUtils.isBlank(version)) {
				menu.setExt3(version);
			}
			if (!StringUtils.isBlank(appResCategory)) {
				menu.setExt4(Integer.parseInt(appResCategory));
			}
			menus = this.findMenusByExt4(menu);
		}
		if (menus != null && menus.size() != 0) {
			PrivTreeNodeBO node = null;
			PrivRoleMenu menuUnEidtable = null;
			String menuType = null;
			List<PrivTreeNodeBO> result = null;
			String sysback = MenuTypeEnums.systemback.getValue();
			String appfront = MenuTypeEnums.applicationfront.getValue();

			// 过滤停用和不可分配的菜单
			List<PrivMenuBO> dislist = this.getConfigDisableMenu();
			if (dislist != null) {
				for (PrivMenuBO privMenuBO : dislist) {
					menus.remove(privMenuBO.getId());
				}
			}

			boolean isSuperAdmin = OrgHelper.getOrgManager().isSuperAdminById(user.getId());

			for (PrivMenuBO privMenuBO : menus.values()) {
				if (isCheckBusiness) {
					PrivMenuBO p = this.findById(privMenuBO.getId());
					if (null == p || (null != p.getExt12() && p.getExt12().intValue() != 0)) {
						continue;// 不显示业务生成器菜单//OA-53898
					}
					if(p != null && p.getType() != null && PrivMenuTypeEnums.reportSpaceMenu.getKey() == p.getType()){
						continue;//不显示报表空间的菜单 OA-149830
					}

				}
				result = new ArrayList<PrivTreeNodeBO>();
				// 将菜单对象转换为树节点对象
				node = new PrivTreeNodeBO(privMenuBO, null);
				// 菜单是否可编辑
				if (roleResUnEditable != null) {
					menuUnEidtable = roleResUnEditable.get(privMenuBO.getId());
					if (menuUnEidtable != null && isAllocated != null && "true".equals(isAllocated)) {
						node.setEditKey("false");
					}
				}

				// 菜单是否可勾选
				if (privMenuBO.getExt5() != null && privMenuBO.getExt5() == 0) {
					node.setEditKey("false");
				}

				result.add(node);
				// 判断当前菜单类型
				menuType = privMenuBO.getExt1();
				if (sysback.equals(menuType)) {
					treeNodes4Back.addAll(result);
				} else if (appfront.equals(menuType)) {
					treeNodes4Front.addAll(result);
				}
			}

			// 过滤由于不可分配的菜单过滤后引起的没有子菜单且没有入口资源的菜单
			if (appResCategory != null && "1".equals(appResCategory)) {
				Set<String> idStr = childNodeList(treeNodes4Front);
				List<PrivTreeNodeBO> templist = new ArrayList<PrivTreeNodeBO>();
				for (int i = 0; i < treeNodes4Front.size(); i++) {
					PrivTreeNodeBO privTreeNodeBO = treeNodes4Front.get(i);
					if (privTreeNodeBO == null || privTreeNodeBO.getIdKey() == null) {
						continue;
					}
//					String[] type_id = privTreeNodeBO.getIdKey().split("_");
//					if ("menu".equals(type_id[0])) {
//						if (!idStr.contains(type_id[1])) {
						if(privTreeNodeBO.getIdKey().startsWith("menu") && !idStr.contains(privTreeNodeBO.getIdKey().substring(5))){
							//PrivMenuBO p = this.findById(Long.valueOf(type_id[1]));
							PrivMenuBO p = privTreeNodeBO.getMenu();
							if (isSuperAdmin) {
								if (null == p) {
									continue;
								}
							} else if (null == p || null == p.getEnterResourceId() || p.getEnterResourceId() == 0L) {
								continue;
							}
						}
//					}

					templist.add(privTreeNodeBO);

				}
				treeNodes4Front.clear();
				treeNodes4Front.addAll(templist);
			}
		}

		for(PrivTreeNodeBO tb : treeNodes4Back){
			if(isShow(tb)){
				treeNodes4Back0.add(tb);
			}
		}
		
		for(PrivTreeNodeBO tf : treeNodes4Front){
			if(isShow(tf)){
				treeNodes4Front0.add(tf);
			}
		}

		Collections.sort(treeNodes4Back0, new Comparator<PrivTreeNodeBO>() {
			public int compare(PrivTreeNodeBO p1, PrivTreeNodeBO p2) {
				int id1 = p1.getSortId();
				int id2 = p2.getSortId();
				return id1 > id2 ? 1 : (id1 < id2 ? -1 : 0);
			}
		});
		
		Collections.sort(treeNodes4Front0, new Comparator<PrivTreeNodeBO>() {
			public int compare(PrivTreeNodeBO p1, PrivTreeNodeBO p2) {
				int id1 = p1.getSortId();
				int id2 = p2.getSortId();
				return id1 > id2 ? 1 : (id1 < id2 ? -1 : 0);
			}
		});
		
		hasMap.put("treeNodes4Back", treeNodes4Back0);
		hasMap.put("treeNodes4Front", treeNodes4Front0);
		return hasMap;
	}
	
	private boolean isShow(PrivTreeNodeBO node){
		//屏蔽掉一些菜单
		Long menuId = node.getMenu().getId();
        //A6-S屏蔽的菜单
        Integer productId = SystemProperties.getInstance().getIntegerProperty("system.ProductId");
        if (productId != null && productId.intValue() == ProductEditionEnum.a6s.ordinal()) {
        	if(menuId.equals(-5229428449906440478L)
             || menuId.equals(-5229428449906440478L)//系统邮箱设置
             || menuId.equals(6692870513281941850L)//HR管理-组织机构设置
             || menuId.equals(7464083475998789371L)//HR管理-员工档案管理
             || menuId.equals(-2933570987085508976L)//HR管理-统计分析
             || menuId.equals(-6400113376465108785L)){//HR管理-信息项设置
        		return false;
        	}
        }

        if(ProductEditionEnum.isU8OEM()){
        	//屏蔽集成平台配置
        	if(menuId.equals(-1726650702758329325L)//系统注册
        	 || menuId.equals(-1461605504161199936L)//用户管理
        	 || menuId.equals(1619292301944899707L)//消息配置
        	 || menuId.equals(-4628815616839168895L)//待办配置
        	 || menuId.equals(-2176243471020961962L)//门户配置
        		//屏蔽移动office授权
        	 || menuId.equals(7811846044579348610L)//移动office授权
        		//屏蔽应用中心设置
        	 || menuId.equals(-329292124654313901L)){//云应用中心设置
        		return false;
        	}
        }
        
        return true;
	}

	
	@Override
	public Map<String, List<PrivTreeNodeBO>> getAllMenuNodes(List<PrivTreeNodeBO> treeNodes4Back, List<PrivTreeNodeBO> treeNodes4Front) throws BusinessException {
		Map<String, List<PrivTreeNodeBO>> hasMap = new ConcurrentHashMap<String, List<PrivTreeNodeBO>>();
		Map<Long, PrivMenuBO> menus = new HashMap<Long, PrivMenuBO>();
		PrivMenuBO menu = new PrivMenuBO();
		menu.setExt4(1);
		menus = this.findMenusByExt4(menu);
		if (menus != null && menus.size() != 0) {
			PrivTreeNodeBO node = null;
			String menuType = null;
			List<PrivTreeNodeBO> result = null;
			String sysback = MenuTypeEnums.systemback.getValue();
			String appfront = MenuTypeEnums.applicationfront.getValue();

			//findMenusByExt4方法已经过滤掉了停用菜单， 这里不需要过滤了
			/*List<PrivMenuBO> dislist = this.getConfigDisableMenu();
			if (dislist != null) {
				for (PrivMenuBO privMenuBO : dislist) {
					menus.remove(privMenuBO.getId());
				}
			}*/

			for (PrivMenuBO privMenuBO : menus.values()) {
				PrivMenuBO p = this.findById(privMenuBO.getId());
				if (null == p) {
					continue;
				}

				result = new ArrayList<PrivTreeNodeBO>();
				// 将菜单对象转换为树节点对象
				node = new PrivTreeNodeBO(privMenuBO, null);
				result.add(node);
				// 判断当前菜单类型
				menuType = privMenuBO.getExt1();
				if (sysback.equals(menuType)) {
					treeNodes4Back.addAll(result);
				} else if (appfront.equals(menuType)) {
					treeNodes4Front.addAll(result);
				}
			}
		}

		hasMap.put("treeNodes4Back", treeNodes4Back);
		hasMap.put("treeNodes4Front", treeNodes4Front);
		return hasMap;
	}

	@Override
	public HashSet<String> getUrlsByRole(Long[] roleIds) {
		return privilegeCache.getUrlsByRole(roleIds);
	}

	@Override
	public void initialize() {
		try {
			BizLastModity=cacheFactory.createObject("BizLastModity");
			List<V3xOrgMember> allMembers = orgManager.getAllMembers(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			final long orgLastModify = this.orgManager.getModifiedTimeStamp(null).getTime();
			final Long bizLastModifyTime = System.currentTimeMillis();
			BizLastModity.set(1L);
			memberMenuLastDate=cacheFactory.createMap("MemberMenuLastDate");
			innerMemberMenuLastDate=cacheFactory.createMap("innerMemberMenuLastDate");
			
			//只加载最近一周内登录人员的菜单。其他人登录时再加载，提高性能。
			final Set<Long> memberIds = new HashSet<Long>();
			Date beginDate = Datetimes.addDate(new Date(), -7);
			try {
				List<LogonLog> logonList = logonLogManager.getAllLogonLogs(null,beginDate,null,null);
				for(LogonLog l : logonList) {
					memberIds.add(l.getMemberId());
				}
			} catch (Exception e) {
				logger.error("获取人员登陆信息失败", e);
			}
			
			//过滤出需要加载菜单的人员
			List<V3xOrgMember> initMembers = new ArrayList<V3xOrgMember>();
			for(V3xOrgMember member : allMembers) {
				if(memberIds.contains(member.getId())) {
					initMembers.add(member);
				}
			}
			
			final List<V3xOrgMember>[] splitMembers = Strings.splitList(initMembers, 700);
			
			// 采用异步加载人员菜单缓存
			new Thread(new Runnable() {
				public void run() {
					CTPExecutor.execute(Arrays.asList(splitMembers), splitMembers.length,
							new CTPExecutor.Task<List<V3xOrgMember>>() {
								public void execute(List<V3xOrgMember> members) {
									Long startTime = System.currentTimeMillis();

									for (V3xOrgMember member : members) {
										Long memberId = member.getId();
										Long accountId = member.getOrgAccountId(); // 主单位ID

										try {
											addMenus(memberId, accountId, bizLastModifyTime, orgLastModify);
										} catch (BusinessException e) {
											logger.error("启动时加载菜单缓存异常！", e);
										}
									}
									logger.info(" -- 加载人员菜单缓存[" + members.size() + "], 耗时"
											+ (System.currentTimeMillis() - startTime) + "ms");
								}
							});
				}
			}).start();

			// 停留一会儿，以多加载一些人
			if (initMembers.size() > 1000) {
				try {
					Thread.sleep(10 * 1000L);
				} catch (Throwable e) {
				}
			}

		} catch (BusinessException e) {
			logger.error("启动时加载菜单缓存异常！", e);
		}
	}

	@Override
	public int getSortOrder() {
	    return -1;
	}

	/**
	 *
	 * @param privTreeNodeBO
	 * @param list
	 * @return
	 */
	private boolean isHaveChildNode(PrivTreeNodeBO privTreeNodeBO, List<PrivTreeNodeBO> list) {
		for (PrivTreeNodeBO privTreeNodeBO2 : list) {
			if (privTreeNodeBO2.getpIdKey().split("_")[1].equals(privTreeNodeBO.getIdKey().split("_")[1])) {
				return true;
			}

		}
		return false;
	}

	//由于M3需求修改这个地方调用方法重复，下一个版本与getMenus方法合并
	@Override
	public Map<Long, PrivMenuBO> reSetMM1Menus(Long memberId, Long accountId) throws BusinessException {
		String key = memberId.toString() + UNDERLINE + accountId.toString();
		long bizLastModify = this.BizLastModity.get();
		long orgLastModify = this.orgManager.getModifiedTimeStamp(null).getTime();
		if (AppContext.getCurrentUser().isAdmin()) {
			long startTime = System.currentTimeMillis();
			Map<Long, PrivMenuBO> menus = addMenus(memberId, accountId, bizLastModify, orgLastModify);
			logger.info("重新加载人员菜单缓存：" + memberId + "," + (System.currentTimeMillis() - startTime) + "ms");
			return menus;
		}

		return member2MenusMap.get(key);
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void setFormMenuManager(FormMenuManager formMenuManager) {
		this.formMenuManager = formMenuManager;
	}

	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}



	@Override
	public boolean setPlugInMenuDao(String path, String level, Long existMenuId) {

		return privilegeCache.setPlugInMenuDao(path, level, existMenuId);
	}


	public void setPrivilegeCache(PrivilegeCache privilegeCache) {
		this.privilegeCache = privilegeCache;
	}

	public void setRoleMenuDao(RoleMenuDao roleMenuDao) {
		this.roleMenuDao = roleMenuDao;
	}

	public void setLogonLogManager(LogonLogManager logonLogManager) {
		this.logonLogManager = logonLogManager;
	}

	@Override
	public void updateBiz() {
	    BizLastModity.set(System.currentTimeMillis());
		try {
			User currentUser = AppContext.getCurrentUser();
			if(currentUser == null){
				return;
			}
			this.updateBizCache(currentUser.getId() , currentUser.getLoginAccount());
			/*String ctxKey = "Menu	-" + currentUser.getId() + "-" + currentUser.getLoginAccount();
			AppContext.putSessionContext(ctxKey, null);
			Map<Long, PrivMenuBO> resources = this.getByMember(currentUser.getId(), currentUser.getLoginAccount());
			List<String> resList = new ArrayList<String>();
			for (PrivMenuBO res : resources.values()) {
				String code = res.getResourceCode();
				if (code == null) {
					continue;
				}
				resList.add(code);
			}*/
			UserHelper.setResourceJsonStr(JSONUtil.toJSONString(this.getResourceCode(currentUser.getId(), currentUser.getLoginAccount())));
		} catch (Throwable e) {
			logger.error("刷新resourcecode异常！", e);
		}
	}
	@Override
	public void updateBiz(Long memberid,Long accountId) {
		this.updateBizCache(memberid, accountId);
		BizLastModity.set(System.currentTimeMillis());

	}
	
	private void updateBizCache(Long memberid,Long accountId){
		try {
			
			String ctxKey = "Menu-" + memberid + "-" + accountId;
			AppContext.putSessionContext(ctxKey, null);
			
			UserHelper.setResourceJsonStr(JSONUtil.toJSONString(this.getResourceCode(memberid, accountId)));
		} catch (InfrastructureException e) {
		    //ignore
		} catch (Throwable e) {
			logger.error("刷新resourcecode异常！", e);
		}
	}
	@Override
	public void updateBiz(List<Long> memberIds) {
		try {
			for(Long mId:memberIds){
			    V3xOrgMember member=	orgManager.getMemberById(mId);
			    List<MemberPost> listPost= member.getConcurrent_post();
			    if(Strings.isNotEmpty(listPost)){
			    		for(MemberPost mpost:listPost){
			    			 this.updateBizCache(member.getId(), mpost.getOrgAccountId());
			    		}
			    }
			    this.updateBizCache(member.getId(), member.getOrgAccountId());
			}

		} catch (Throwable e) {
			logger.error("刷新resourcecode异常！", e);
		}
		BizLastModity.set(System.currentTimeMillis());

	}
	
	@Override
	public void updateMemberMenuLastDate(Long memberId, Long accountId){
		String key = memberId.toString() + UNDERLINE + accountId.toString();
		if(member2MenusMap.containsKey(key)){
			member2MenusMap.remove(key);
		}
		memberMenuLastDate.put(key, System.currentTimeMillis());
	}
	
	@Override
	public void updateInnerMemberMenuLastDate(Long memberId, Long accountId){
		String key = memberId.toString() + UNDERLINE + accountId.toString();
		if(innerMember2MenusMap.containsKey(key)){
			innerMember2MenusMap.remove(key);
		}
		innerMemberMenuLastDate.put(key, System.currentTimeMillis());
	}

	@Override
	public void updateMemberMenuLastDateByRoleId(Long roleId, Long accountId, List<V3xOrgMember> members){
		 EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
	        objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective1Id, roleId);
	        if(accountId != null){
	        	objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective0Id, accountId);
	        }

	        List<V3xOrgRelationship> remianRelationship= orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, (Long)null, (Long)null, objectiveIds);
	        if(Strings.isNotEmpty(remianRelationship)){
	            for(V3xOrgRelationship vr : remianRelationship){
	            	String key = vr.getSourceId() + UNDERLINE + vr.getObjective0Id();
	        		memberMenuLastDate.put(key, System.currentTimeMillis());
	        		innerMemberMenuLastDate.put(key, System.currentTimeMillis());
	            }

	        }else if(Strings.isNotEmpty(members)&&accountId!=null){
	        	for(V3xOrgMember m:members){
	        		String key = m.getId() + UNDERLINE + accountId;
	        		memberMenuLastDate.put(key, System.currentTimeMillis());
	        		innerMemberMenuLastDate.put(key, System.currentTimeMillis());
	        	}
	        }

	}

	@Override
	public void updateLocalMemberMenuLastDate(Long memberId, Long accountId){
		String key = memberId.toString() + UNDERLINE + accountId.toString();
		//如果共享缓存中找到对应时间戳，更新本地时间
		if(memberMenuLastDate.contains(key)){
			memberMenuLocalLastDate.put(key, memberMenuLastDate.get(key));
		}
		try {
			long bizLastModify = this.BizLastModity.get();
			long orgLastModify = this.orgManager.getModifiedTimeStamp(null).getTime();
			memberBizDate.put(key, bizLastModify);
			memberOrgDate.put(key, orgLastModify);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("更新用户最后一次登录缓存时间失败！用户编号："+memberId, e);
		}

		//如果共享缓存中没有找到对应时间戳，不做任何操作
//		else{
//			long lastModify = System.currentTimeMillis();
//			memberMenuLocalLastDate.put(key, lastModify);
//			memberMenuLastDate.put(key, lastModify);
//		}
	}
	
	@Override
	public void updateLocalInnerMemberMenuLastDate(Long memberId, Long accountId){
		String key = memberId.toString() + UNDERLINE + accountId.toString();
		//如果共享缓存中找到对应时间戳，更新本地时间
		if(innerMemberMenuLastDate.contains(key)){
			innerMemberMenuLocalLastDate.put(key, innerMemberMenuLastDate.get(key));
		}
		try {
			long bizLastModify = this.BizLastModity.get();
			long orgLastModify = this.orgManager.getModifiedTimeStamp(null).getTime();
			innerMemberBizDate.put(key, bizLastModify);
			innerMemberOrgDate.put(key, orgLastModify);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("更新用户最后一次登录缓存时间失败！用户编号："+memberId, e);
		}

		//如果共享缓存中没有找到对应时间戳，不做任何操作
//		else{
//			long lastModify = System.currentTimeMillis();
//			memberMenuLocalLastDate.put(key, lastModify);
//			memberMenuLastDate.put(key, lastModify);
//		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see com.seeyon.ctp.privilege.manager.MenuManager#update(com.seeyon.ctp.
	 * privilege.po.PrivMenu)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Long updateMenu(PrivMenuBO menu) throws BusinessException {
		Long result = -1l;
		if (menu != null) {
			result = menu.getId();

			menuDao.updateMenu(menu);
			/* menuResourceDao.insertMenuResourcePatchAll(menuResources); */
			privilegeCache.updateMenu(menu);
		}
		return result;
	}

	@Override
	public void updateMenuPath(Long parent, List<String> menuIds) throws BusinessException {
		PrivMenuBO parentMenu = findById(parent);
		List<PrivMenu> menus = new ArrayList<PrivMenu>();
		if (parentMenu != null || parent == 0) {
			String parentLevel = "0";
			if (parentMenu != null) {
				parentLevel = parentMenu.getExt2();
			}
			String level = String.valueOf(Integer.parseInt(parentLevel) + 1);
			PrivMenuBO menuTemp = null;
			PrivMenuBO menu = null;
			for (String menuId : menuIds) {
				menu = findById(Long.valueOf(menuId));
				try {
					menuTemp = (PrivMenuBO) menu.clone();
				} catch (CloneNotSupportedException e) {
				}
				menu.setParentId(parent);
				menu.setExt2(level);
				menu = getMenuPath(menu, parentMenu);
				menus.addAll(updateSubMenuPath(menu, menuTemp));
				menus.add(menu.toPO());
			}
		}
		menuDao.updateMenuPatchAll(menus);
	}

	@Override
	public void updatePatch(List<PrivMenuBO> menus) throws BusinessException {
		if (menus != null) {
			for (PrivMenuBO menu : menus) {
				updateMenu(menu);
			}
		}
	}

	private List<PrivMenu> updateSubMenuPath(PrivMenu menu, PrivMenu menuTemp) throws BusinessException {
		List<PrivMenu> menus = new ArrayList<PrivMenu>();
		String parentLevel = menu.getExt2();
		String parentPath = menu.getPath();
		String level = String.valueOf(Integer.parseInt(parentLevel) + 1);
		List<PrivMenu> subMenus = menuDao.selectSubList(menuTemp);
		PrivMenu subMenuTemp = null;
		Long index = 0l;
		for (PrivMenu privMenu : subMenus) {
			try {
				subMenuTemp = (PrivMenu) privMenu.clone();
			} catch (CloneNotSupportedException e) {
			}
			privMenu.setExt2(level);
			DecimalFormat df = new DecimalFormat(this.getMenuPath(false));
			privMenu.setPath(new StringBuilder(parentPath).append(df.format(++index)).toString());
			menus.addAll(updateSubMenuPath(privMenu, subMenuTemp));
			menus.add(privMenu);
		}
		return menus;
	}
	@Override
	public boolean validateMemberMenuLastDate(Long memberId, Long accountId){
		String key = memberId.toString() + UNDERLINE + accountId.toString();

		if(!memberMenuLastDate.contains(key)){
			return true;
		}
		long memberMenuLastModify = memberMenuLastDate.get(key);
		long memberMenuLocalLastModify = 0l;
		if(memberMenuLocalLastDate.containsKey(key)){
			memberMenuLocalLastModify=memberMenuLocalLastDate.get(key);
		}
		if (Strings.equals(memberMenuLastModify, memberMenuLocalLastModify)){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean validateInnerMemberMenuLastDate(Long memberId, Long accountId){
		String key = memberId.toString() + UNDERLINE + accountId.toString();

		if(!innerMemberMenuLastDate.contains(key)){
			return true;
		}
		long memberMenuLastModify = innerMemberMenuLastDate.get(key);
		long memberMenuLocalLastModify = 0l;
		if(innerMemberMenuLocalLastDate.containsKey(key)){
			memberMenuLocalLastModify= innerMemberMenuLocalLastDate.get(key);
		}
		if (Strings.equals(memberMenuLastModify, memberMenuLocalLastModify)){
			return true;
		}
		return false;
	}

	public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}

	@Override
	public Set<PrivMenuBO> getBusinessMenusByAccountId(Long accountId) throws BusinessException {
		Set<PrivMenuBO>  menuBo = new HashSet<PrivMenuBO>();
		PrivMenuBO  menu;
		List<V3xOrgRole> roles = new ArrayList<V3xOrgRole>();
		if(V3xOrgEntity.VIRTUAL_ACCOUNT_ID == accountId){
			List<V3xOrgAccount> listAccounts =	orgManager.getAllAccounts();
			for(V3xOrgAccount account : listAccounts){
				List<V3xOrgRole>  rs = orgManager.getAllRolesByBond(account.getId(), OrgConstants.ROLE_BOND.BUSINESS);
				roles.addAll(rs);
			}
		}else{
			roles = orgManager.getAllRolesByBond(accountId, OrgConstants.ROLE_BOND.BUSINESS);
		}
		if (null != roles) {
			Set<Long> roleIds = new HashSet<Long>();
			for (V3xOrgRole role : roles) {
				if (role != null) {
					if (role.getBond() == OrgConstants.ROLE_BOND.BUSINESS.ordinal()) {
						roleIds.add(role.getId());
					} 
				}
			}
			HashSet<PrivMenuBO> menuList = privilegeCache.getMenuByRoleFonEntity(roleIds.toArray(new Long[roleIds.size()]));
			for (Iterator<PrivMenuBO> iterator = menuList.iterator(); iterator.hasNext();) {
				menu=iterator.next();
				if(null != menu && "1".equals(menu.getExt2())){
					menuBo.add(menu);
				}
			}
		}
		return menuBo;
	}
	
	/**
	 * Guest 账号拥有的资源
	 * @return
	 */
	private List<String> getDefaultGuestResource(){
		List<String> resourceList = new ArrayList<String>();
		//新闻
		resourceList.add("F05_newsIndexAccount");
		//公告
		resourceList.add("F05_bulIndexAccount");
		//大秀
		resourceList.add("F05_show");
		//文档
		resourceList.add("F04_docIndex");//a8-文档中心
		resourceList.add("F04_knowledgeSquareFrame");//a8-知识广场
		
		resourceList.add("F04_accDocLibIndex");//a6-单位文档
		resourceList.add("F04_proDocLibIndex");//a6-项目文档
		resourceList.add("F04_eDocLibIndex");//a6-公文档案
		
		//报表
		resourceList.add("T05_formQuery");//表单查询
		resourceList.add("F08_reportviewindex");//表单统计
		resourceList.add("F08_report_view");//报表分析

		
		return resourceList;
	}
	
	/**
	 * 获取guest账号拥有的所有资源菜单
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public Map<Long, PrivMenuBO> getMenusByResource(List<String> resourceCodes) throws BusinessException{
		Map<Long, PrivMenuBO> result = new HashMap<Long, PrivMenuBO>();
		List<PrivTreeNodeBO> treeNodes4Back = new ArrayList<PrivTreeNodeBO>();
		List<PrivTreeNodeBO> treeNodes4Front= new ArrayList<PrivTreeNodeBO>();
		this.getAllMenuNodes(treeNodes4Back, treeNodes4Front);
		
		List<Long> guestMenuIds = new UniqueList<Long>();
		for(PrivTreeNodeBO node : treeNodes4Front){
			PrivMenuBO menu = node.getMenu();
			String resourceCode = menu.getResourceCode();
			if(resourceCodes.contains(resourceCode)){
				//如果菜单包含这个资源，取到这个菜单向上的所有菜单。
				//guestMenuIds.add(menu.getId());
				List<Long> pIds = new ArrayList<Long>();
				pIds.add(menu.getId());
				guestMenuIds.addAll(privilegeCache.getParentMenus(pIds));
			}
		}
		
		for(Long menuId : guestMenuIds){
			result.put(menuId, this.findById(menuId));
		}
		
		return result;
	}

	@Override
	public List<PrivMenuBO> getCAP4MenuByMember(Long memberId, Long AccountId) throws BusinessException {
		List<PrivMenuBO> returnlist = new ArrayList<PrivMenuBO>();
		Map<Long, PrivMenuBO> menumap = getByMember(memberId, AccountId);
		Set<Long> set = menumap.keySet();
		for (Long long1 : set) {
			PrivMenuBO privMenuBO = menumap.get(long1);
			if (privMenuBO.getExt12() != null && privMenuBO.getExt12() != 0
					&& "1".equals(privMenuBO.getExt2())&& (privMenuBO.getType()!= null && 3== privMenuBO.getType())) {
				returnlist.add(privMenuBO);
			}
		}
		return returnlist;
	}

	@Override
	public void updateMenuBatch(List<Map<String, Object>> batchOrder) throws BusinessException {
		if(CollectionUtils.isEmpty(batchOrder)){
			return;
		}
		
		//先更新数据库、再更新缓存，避免缓存数据库不一致
		List<PrivMenuBO> menus = Lists.newArrayList();
		for (Map<String, Object> map : batchOrder) {
			PrivMenuBO menu = privilegeCache.getMenuById((Long) map.get("id"));
			if (menu != null) {
				if(map.containsKey("sortid")){
					menu.setSortid((Integer) map.get("sortid"));
				}
				if(map.containsKey("path")){
					menu.setPath((String) map.get("path"));
				}
				if(map.containsKey("target")){
					menu.setTarget((String) map.get("target"));
				}
				if(map.containsKey("updatedate")){
					menu.setUpdatedate((Date) map.get("updatedate"));
				}
				if(map.containsKey("updateuserid")){
					menu.setUpdateuserid((Long) map.get("updateuserid"));
				}
				menus.add(menu);
				menuDao.updateMenu(map);
			}
		}
		
		if(CollectionUtils.isNotEmpty(menus)){
			privilegeCache.updateMenuAll(menus);
		}
		
	}
	
}
