package com.seeyon.ctp.privilege.dao; /**
 * $Author: sunzhemin $
 * $Rev: 46393 $
 * $Date:: 2015-03-03 22:25:56#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */

import com.seeyon.ctp.datasource.CtpDynamicDataSource;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemInitializer;
import com.seeyon.ctp.common.authenticate.UserPrivilegeManager;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.common.cache.CacheObject;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;
import com.seeyon.ctp.privilege.enums.AppResourceCategoryEnums;
import com.seeyon.ctp.privilege.enums.PrivMenuTypeEnums;
import com.seeyon.ctp.privilege.enums.ResourceCategoryEnums;
import com.seeyon.ctp.privilege.po.BasePrivResource;
import com.seeyon.ctp.privilege.po.PrivMenu;
import com.seeyon.ctp.privilege.po.PrivRoleMenu;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.Strings;


/**
 * <p>Title: 权限Cache实现类</p>
 * <p>Description: 本程序实现对内存中的权限模型的管理</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public class PrivilegeCacheImpl extends AbstractSystemInitializer implements PrivilegeCache,UserPrivilegeManager,SystemInitializer {

    private final static Log logger              = LogFactory.getLog(PrivilegeCacheImpl.class);

    /** */
    private MenuDao menuDao;

    /** */
    private RoleMenuDao roleMenuDao;

    /** */
    private CacheAccessable factory             = null;

    /** 菜单Path长度缓存 */
    private CacheObject<Integer> pathLengthCache = null;

    /** 菜单缓存 */
    private CacheMap<Long, PrivMenuBO> menuMap             = null;
    /** <ResourceCode, menu.id > */
    private CacheMap<String, Long> menuMapCode2Id             = null;


    /** 角色菜单资源关系缓存
     *  HashMap<Long, Long> 为菜单ID和资源ID列表的键值对
     * */
    private CacheMap<Long, HashSet<Long>> roleMenuMap         = null;



    /** 资源ID对应URL的缓存 */
    private CacheMap<Long,String> resourceUrlAndIdMap = null;
    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.PrivilegeCache#init()
     */
    
    @Override
    public int getSortOrder() {
        return -999;
    }
    @Override
    public void initialize() {

        factory = CacheFactory.getInstance(PrivilegeCache.class);
        // 注册缓存对象
        if (!factory.isExist("PrivMenu") || menuMap == null) {
            menuMap = factory.createMap("PrivMenu");
            menuMapCode2Id = factory.createMap("menuMapCode2Id");
        }
        if (!factory.isExist("roleMenuMap") || roleMenuMap == null) {
            roleMenuMap = factory.createMap("roleMenuMap");
        }
        if (!factory.isExist("PrivResourceUrlAndId") || resourceUrlAndIdMap == null) {
            resourceUrlAndIdMap = factory.createMap("PrivResourceUrlAndId");
        }
        if (!factory.isExist("pathLengthCache") || pathLengthCache == null) {
            pathLengthCache = factory.createObject("pathLengthCache");

        }

        //初始化菜单path长度
        //initPathLength();
        // 初始化缓存数据
        //doParentIdAndTypeRepair();
        //如果其他节点已经正常启动，启动时不需要重新load一遍数据，会自动从其他节点同步数据过来。
        if (!CacheFactory.isSkipFillData()) {
            try {
                CtpDynamicDataSource.setDataSourceKey(DataSourceName.BASE.getSource());
                initAllMenu();
            }finally {
                CtpDynamicDataSource.clearDataSourceKey();
            }
            try {
                CtpDynamicDataSource.setDataSourceKey(DataSourceName.BASE.getSource());
                initRoleMenu();
            }finally {
                CtpDynamicDataSource.clearDataSourceKey();
            }
        }


        logger.info("初始化菜单完成");

    }

    /**
     * 为了方便后续使用，启动时自动判定是否需要进行
     * parentId和type的补充
     */
    private void doParentIdAndTypeRepair() {
        Long testMenuId=-4140425781984149261L;
        Long menuId = null;
        Long pMenuId = null;
        PrivMenuBO privMenuBO = null;
        PrivMenu testPrivMenu=DBAgent.get(PrivMenu.class, testMenuId);
        if(testPrivMenu.getParentId()==null){
            Connection conn = null;
            try {
                conn = JDBCAgent.getRawConnection();
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);
                return;
            }
            JDBCAgent jdbcAgent = new JDBCAgent(conn);
            try{
                //需要进行parentId与type的修复
                initPathLength();
                List<PrivMenu> menus = menuDao.selectList(null);
                // 先遍历一次menus，得到Map<菜单路径+版本, 菜单ID>，用于后面获得父菜单
                Map<String, Long> pathVersionMenuMap = new ConcurrentHashMap<String, Long>();
                for (PrivMenu privMenu : menus) {
                    if (privMenu != null) {
                        menuId = privMenu.getId();
                        if (menuId != null) {
                            if(privMenu.getPath()!=null){
                                pathVersionMenuMap.put(privMenu.getPath(), menuId);
                            }
                        }
                    }
                }
                for (PrivMenu privMenu : menus) {
                    if (privMenu != null) {
                        menuId = privMenu.getId();
                        if (menuId != null) {
                            privMenuBO = new PrivMenuBO(privMenu);
                            // 获得父菜单ID
                            String path = privMenu.getPath();
                            if(path==null){
                                continue;
                            }
                            String parentPath = path.substring(0, path.length() - pathLengthCache.get());
                            pMenuId = pathVersionMenuMap.get(parentPath);
                            privMenuBO.setParentId(pMenuId != null ? pMenuId : 0L);
                            privMenu.setParentId(pMenuId != null ? pMenuId : 0L);
                            Long oldParentId = privMenu.getParentId();
                            Integer oldType = privMenu.getType();
                            //TODO 系统预置菜单需要根据url或code来标识位系统预置菜单
                            //ext12！=null&&ext12！=0来标识cap3菜单
                            //其它的为用户自定义菜单
                            if (privMenu.getExt12() == null || (privMenu.getExt12() == 0)) {
                                privMenu.setType(PrivMenuTypeEnums.systemPresetMenu.getKey());
                            } else {
                                privMenu.setType(PrivMenuTypeEnums.businessCapMenu.getKey());
                            }
                            if ((oldParentId == null) || (oldType == null)) {
                                //logger.info(privMenu.getResourceCode()+":"+privMenu.getParentId());
                                String updateSql="update PRIV_MENU set parent_id=?,menu_type=? where id=?";
                                List paramList=new ArrayList(3);
                                paramList.add(privMenu.getParentId());
                                paramList.add(privMenu.getType());
                                paramList.add(privMenu.getId());
                                logger.info(paramList);
                                jdbcAgent.execute(updateSql,paramList);
                            }
                        }
                    }
                }
                if(conn!=null){
                    conn.commit();
                }
            }
            catch (Throwable e){
                if(conn!=null){
                    try {
                        conn.rollback();
                    } catch (SQLException e1) {
                        logger.error(e1.getMessage(),e1);
                    }
                }
                logger.error(e.getMessage(),e);
            }
            finally {
                jdbcAgent.close();
                if(conn!=null){
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
        }
    }
    /**
     * 初始化菜单path长度，为后续算法提供依据
     */
    private void initPathLength(){
    	String path=menuDao.selectMaxPath(null, 1);
    	if(path!=null&&path.length()!=0){
    		pathLengthCache.set(path.length());
    	}else{
    		pathLengthCache.set(3);
    	}
    }

    /**
     * 初始化菜单缓存
     */
    private void initAllMenu() {
        Long menuId = null;
        Long pMenuId = null;
        PrivMenuBO privMenuBO = null;
       /* v6.0权限修改
        PrivResourceBO resBO = null;*/
        // 获得所有定义为菜单入口资源的关系对象
//        Map<Long, Long> menuResourceMap = menuResourceDao.findMenuEnterSource();
        // 查询数据库获得所有菜单
        List<PrivMenu> menus = menuDao.selectList(null);
        // 先遍历一次menus，得到Map<菜单路径+版本, 菜单ID>，用于后面获得父菜单
       // Map<String, Long> pathVersionMenuMap = new ConcurrentHashMap<String, Long>();
       // for (PrivMenu privMenu : menus) {
       //     if (privMenu != null) {
       //         menuId = privMenu.getId();
       //         if (menuId != null) {
       //             pathVersionMenuMap.put(privMenu.getPath(), menuId);
       //         }
        //    }
        //}
        for (PrivMenu privMenu : menus) {
            if (privMenu != null) {
                String resourceNavurl = privMenu.getResourceNavurl();
                //协同菜单高级工作流插件特殊处理
                //没有工作流高级插件时使用原来的菜单内容
                if(!AppContext.hasPlugin("workflowAdvanced")){
                    if("/bpm/bpmPortal.do?method=index&menu=T03_cooperation_work&themType=19".equals(resourceNavurl)){
                        //bmp主题空间
                        privMenu.setResourceNavurl("/portal/spaceController.do?method=showThemSpace&themType=19");
                        privMenu.setTarget("mainfrm");
                        privMenu.setName("system.menuname.ThemeSpace");
                    }
                }
                menuId = privMenu.getId();
                if (menuId != null) {
                    privMenuBO = new PrivMenuBO(privMenu);
//                    resBO = new PrivResourceBO(privMenu);
                    //过滤插件未启用的资源
                    boolean hasplugin = false;
                    // 获得父菜单ID
                   // String path = privMenu.getPath();
                   // String parentPath = path.substring(0, path.length() -  pathLengthCache.get());
                    pMenuId = privMenu.getParentId();
                    privMenuBO.setParentId(pMenuId != null ? pMenuId : 0L);
                    //privMenu.setParentId(pMenuId != null ? pMenuId : 0L);
                    //Long oldParentId=privMenu.getParentId();
                    //Integer oldType=privMenu.getType();
                    //if(privMenu.getExt12()==null||(privMenu.getExt12()==0)){
                    //    privMenu.setType(PrivMenuTypeEnums.systemPresetMenu.getKey());
                    //}
                    //else {
                    //    privMenu.setType(PrivMenuTypeEnums.businessCapMenu.getKey());
                    //}
                    //if((oldParentId==null)||(oldType==null)){
                    //    DBAgent.update(privMenu);
                    //}
                    if(privMenu.getResourceModuleid()!=null&&!("0").equals(privMenu.getResourceModuleid())){
                         //多个插件，都有才启用
                        if(privMenu.getResourceModuleid().contains("&")) {
                            String[] moduleids=privMenu.getResourceModuleid().split("&");
                            hasplugin=true;
                            for(String moduleid : moduleids) {
                                if(!AppContext.hasPlugin(moduleid)){
                                    hasplugin=false;
                                    break;
                                }
                            }
                            //多个插件，有一个就启用
                        }else if(privMenu.getResourceModuleid().contains(",")) {
                            String[] moduleids=privMenu.getResourceModuleid().split(",");

                            for(String moduleid : moduleids) {
                                if(AppContext.hasPlugin(moduleid)){
                                    hasplugin=true;
                                    break;
                                }
                            }
                        }else {
                            if(AppContext.hasPlugin(privMenu.getResourceModuleid())) {
                                hasplugin=true;
                            }
                        }
                        if (!("none").equals(privMenu.getResourceModuleid()) && !privMenu.getResourceModuleid().contains("mm1")//OA-64497
                        		&&!hasplugin) {
                            continue;
                        }
                    }
                    if(!StringUtils.isBlank(privMenuBO.getResourceNavurl())&&privMenuBO.getEnterResource()!=null){
                    	 privMenuBO.setEnterResourceId(menuId);
                    }

                    if(Strings.isNotBlank(privMenuBO.getResourceCode())){
                        menuMapCode2Id.put(privMenuBO.getResourceCode(), menuId);
                    }

                    resourceUrlAndIdMap.put(menuId,privMenuBO.getResourceNavurl());
                    if (ResourceCategoryEnums.naviresource.getValue().equals(privMenu.getExt8())) {
                    	privMenuBO.getNaviResourceIds().add(menuId);
                    }
                    // 初始化菜单的快捷资源
                    if (privMenu.getExt15() != null && AppResourceCategoryEnums.ForegroundShortcut.getKey() == privMenu.getExt15()) {
                    	privMenuBO.getShortcutResourceIds().add(menuId);
                    }
                    menuMap.put(menuId, privMenuBO);
                }
            }
        }
        Set<Long> menuIdSet = menuMap.keySet();
        logger.info("开始计算菜单级别");
        long beiginMills = System.currentTimeMillis();
        for(Long orgMenuId:menuIdSet){
            PrivMenuBO tmpMenuBo=menuMap.get(orgMenuId);
            int menuLevel=getMenuLevel(tmpMenuBo);
            tmpMenuBo.setMenuLevelInteger(menuLevel);
            menuMap.notifyUpdate(orgMenuId);
        }
        long endMills=System.currentTimeMillis();
        long useTimeMills=endMills-beiginMills;
        logger.info("结束计算菜单级别，用时="+useTimeMills);
    }
    public int getMenuLevel(PrivMenuBO privMenuBO){
        menuLevelTmp=0;
        doMenuLevel(privMenuBO);
        int menuLevel=menuLevelTmp;
        menuLevelTmp=0;
        return menuLevel;
    }
   private int menuLevelTmp=0;
   private void doMenuLevel(PrivMenuBO privMenuBO){
       menuLevelTmp=menuLevelTmp+1;
        if(privMenuBO.getParentId()==0L || menuLevelTmp>=5){
            return;
        }
        else
        {
            Long parentId = privMenuBO.getParentId();
            PrivMenuBO parentPrivMenubo = menuMap.get(parentId);
            if(parentPrivMenubo==null){
                //无效数据防护
                return;
            }
            if(!parentPrivMenubo.getPrivMenuBOItems().contains(privMenuBO)){
                parentPrivMenubo.getPrivMenuBOItems().add(privMenuBO);
                menuMap.notifyUpdate(parentId);
            }
            doMenuLevel(parentPrivMenubo);

        }

   }

    /**
     * 初始化角色资源缓存
     */
    private void initRoleMenu() {
        PrivRoleMenu privRoleMenu = null;
        Long roleId = null;
        Long menuId = null;
        HashSet<Long> resSet = null;
//        String pline=VersionEnums.getTextForKey();
        PrivRoleMenu prm=new PrivRoleMenu();
        List<PrivRoleMenu> roleReses = roleMenuDao.selectList(prm);
        for (Iterator<PrivRoleMenu> iterator = roleReses.iterator(); iterator.hasNext();) {
        	privRoleMenu = (PrivRoleMenu) iterator.next();
            if (privRoleMenu != null) {
                roleId = privRoleMenu.getRoleid();
                menuId = privRoleMenu.getMenuid();
                // 初始化角色菜单缓存
                resSet = roleMenuMap.get(roleId);
                if (resSet == null) {
                    resSet = new HashSet<Long>();
                }
//                setProductLineMenu(privRoleMenu);
                resSet.add(menuId);
                roleMenuMap.put(roleId, resSet);
            }
        }
    }

    private void setProductLineMenu(PrivRoleMenu privRoleMenu){
    	 PrivMenu pm=menuMap.get(privRoleMenu.getMenuid());
    	 boolean isDirty = false;
         /**
          * 产品线的个性化设置 合并后的坑；
          */
    	 if(pm!=null){
	         if(!StringUtils.isBlank(privRoleMenu.getExt1())){ //标题名称
	         	pm.setName(privRoleMenu.getExt1());
	         	isDirty = true;
	         }
	         if(!StringUtils.isBlank(privRoleMenu.getExt2())){ //按钮图片
	         	pm.setIcon(privRoleMenu.getExt2());
	         	isDirty = true;
	         }
	         if(!StringUtils.isBlank(privRoleMenu.getExt3())){ //路径挂载
	         	pm.setPath(privRoleMenu.getExt3());
	         	isDirty = true;
	         }
	         if(privRoleMenu.getExt4()!=null){ //排序
	         	pm.setSortid(privRoleMenu.getExt4());
	         	isDirty = true;
	         }
	         if(isDirty){
	        	 menuMap.notifyUpdate(privRoleMenu.getMenuid());
	         }
    	 }
    }


    public List<BasePrivResource> getAllResources() {
//        List<BasePrivResource> resources = new ArrayList<BasePrivResource>();
//        resources.addAll(resourceMap.values());
        return null;
    }
    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.PrivilegeCache#getAllMenu()
     */
    @Override
    public List<PrivMenuBO> getAllMenu() {
        List<PrivMenuBO> menus = new ArrayList<PrivMenuBO>();
        Collection<PrivMenuBO> menusCache = menuMap.values();
        for (PrivMenuBO privMenuBO : menusCache) {
//            if (privMenuBO.getExt3() != null&&!privMenuBO.getExt3().equals("")) {
                menus.add(privMenuBO);
//            }
        }
        return menus;
    }


    public Collection<PrivMenuBO> getAllMenuForCollection(){
    	return menuMap.values();
    }

    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.PrivilegeCache#getMenuById(java.lang.Long)
     */
    @Override
    public PrivMenuBO getMenuById(Long menuId) {
        return menuMap.get(menuId);
    }

    @Override
    public PrivMenuBO getMenuByCode(String code) {
        Long menuId = menuMapCode2Id.get(code);
        if(menuId != null){
            return this.menuMap.get(menuId);
        }

        return null;
    }


    @Override
    public Set<Long> getResourceIdsByMenu(Long menuId) {

    	return null;
    }

    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.PrivilegeCache#getUrlsByRole(java.lang.Long[])
     */
    @Override
    public HashSet<String> getUrlsByRole(Long[] roleIds) {
        Long roleId = null;
        HashSet<String> urls = new HashSet<String>();
        HashSet<String> urlSet = new HashSet<String>();
        for (int i = 0; i < roleIds.length; i++) {
            roleId = roleIds[i];
            HashSet<Long> ids = roleMenuMap.get(roleId);

            if(ids!=null){
            for (Long long1 : ids) {
            	urlSet.add(resourceUrlAndIdMap.get(long1));
            	if (urlSet != null) {
                    urls.addAll(urlSet);
                }
			}
            }


        }
        return urls;
    }

    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.PrivilegeCache#updateMenu(com.seeyon.ctp.privilege.po.PrivMenu)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void updateMenu(PrivMenuBO menu) {
        int menuLevel=getMenuLevel(menu);
        menu.setMenuLevelInteger(menuLevel);
        PrivMenuBO temp = menuMap.get(menu.getId());
        // 更新的时候只包括名称、名称和入口资源ID等修改
        if (temp != null) {
            // 名称
            if (!StringUtils.isEmpty(menu.getName())) {
                temp.setName(menu.getName());
            }
            // 图标
            if (!StringUtils.isEmpty(menu.getIcon())) {
                temp.setIcon(menu.getIcon());
            }
            // 入口资源ID
            temp.setEnterResourceId(menu.getEnterResourceId());
            // 虚节点
            temp.setTarget(menu.getTarget());
            // 导航资源
            temp.setNaviResourceIds(menu.getNaviResourceIds());
            // 排序号
            temp.setSortid(menu.getSortid());
            // 默认快捷
            temp.setExt20(menu.getExt20());
            //菜单层级
            temp.setMenuLevelInteger(menu.getMenuLevelInteger());
            // 新建的情况
        } else {
            temp = menu;
        }
        menuMap.put(menu.getId(), temp);

        if(Strings.isNotBlank(temp.getResourceCode())){
            menuMapCode2Id.put(temp.getResourceCode(), menu.getId());
        }
    }

    @Override
    public void updateMenuAll(List<PrivMenuBO> menu) {
        for (PrivMenuBO privMenuBO : menu) {
            updateMenu(privMenuBO);
        }
    }

    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.PrivilegeCache#deleteMenu(java.lang.Long)
     */
    @Override
    public void deleteMenu(Long menuId) {
        PrivMenuBO menu = menuMap.remove(menuId);
        if(menu != null && Strings.isNotBlank(menu.getResourceCode())){
            menuMapCode2Id.put(menu.getResourceCode(), menuId);
        }
//        menuResourceMap.remove(menuId);
    }

    @Override
    public Long deleteMenu(Long[] menus) {
        Long result = -1l;
        if (menus != null) {
            for (int i = 0; i < menus.length; i++) {
                PrivMenuBO menu = menuMap.remove(menus[i]);
                if(menu != null && Strings.isNotBlank(menu.getResourceCode())){
                    menuMapCode2Id.put(menu.getResourceCode(), menu.getId());
                }
//                menuResourceMap.remove(menus[i]);
                result = menus[i];
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.PrivilegeCache#deleteResource(com.seeyon.ctp.privilege.po.PrivResource)
     */
    @Override
    public Long deleteResource(Long[] res) {
        Long result = -1l;

        return result;
    }

    @Override
	public void updateRoleMenu(Long roleId, List<PrivRoleMenu> roleMenu) {
    	updateRoleMenu(roleId,roleMenu,false);//默认是全量菜单更新。
    }
    
    @Override
	public void updateRoleMenu(Long roleId, List<PrivRoleMenu> roleMenu, boolean isIncrement) {
		if (roleId != null && !isIncrement) {
			roleMenuMap.remove(roleId);
		}
        if (roleMenu != null) {
            HashSet<Long> menuIds = new HashSet<Long>();
            for (PrivRoleMenu privRoleMenu : roleMenu) {
                if (roleId == null) {
                    roleId = privRoleMenu.getRoleid();
                }
                menuIds = roleMenuMap.get(roleId);
                if (menuIds == null) {
                	menuIds = new HashSet<Long>();
                }
                if(!menuIds.contains(privRoleMenu.getMenuid())) {
                	menuIds.add(privRoleMenu.getMenuid());
                }

                if (roleId != null) {
                    roleMenuMap.put(roleId, menuIds);
                }
            }

        }
    }

    /*v6.0 权限修改
     * @Override
    public void updateMenuResource(List<PrivMenu> menuResesNew, List<PrivMenu> menuResesOld) {
        HashMap<Long,Integer> menuResesSet = null;
        for (PrivMenuResource menuRes : menuResesNew) {
            menuResesSet = menuResourceMap.get(menuRes.getMenuid());
            if (menuResesSet == null) {
                menuResesSet = new HashMap<Long,Integer>();
            }
            menuResesSet.put(menuRes.getResourceid(),menuRes.getEnterResource());
            menuResourceMap.put(menuRes.getMenuid(), menuResesSet);
        }
        if (menuResesOld != null) {
            Long menuId = null;
            Long resId = null;
            for (PrivMenuResource menuRes : menuResesOld) {
                menuId = menuRes.getMenuid();
                resId = menuRes.getResourceid();
                menuResesSet = menuResourceMap.get(menuId);
                if (menuResesSet != null) {
                    menuResesSet.remove(resId);
                }
            }
        }
    }*/

    @Override
    public HashSet<Long> getMenuByRole(Long[] roleIds) {
        Long roleId = null;
        HashSet<Long> menus = new HashSet<Long>();
//        HashMap<Long, List<Long>> menuMap = null;
        HashSet<Long> menusSet =null;
        for (int i = 0; i < roleIds.length; i++) {
            roleId = roleIds[i];
            menusSet = roleMenuMap.get(roleId);
            if (menusSet != null) {
              menus.addAll(menusSet);
            }
        }
        menus.addAll(getParentMenus(menus));
        return menus;
    }

    @Override
    public HashSet<PrivMenuBO> getMenuByRoleFonEntity(Long[] roleIds) {
        Long roleId = null;
        HashSet<PrivMenuBO> menuList = new HashSet<PrivMenuBO>();
        HashSet<Long> menus = new HashSet<Long>();
//        HashMap<Long, List<Long>> menuMap = null;
        HashSet<Long> menusSet =null;
        for (int i = 0; i < roleIds.length; i++) {
            roleId = roleIds[i];
            menusSet = roleMenuMap.get(roleId);
            if (menusSet != null) {
              menus.addAll(menusSet);
            }
        }
        menuList.addAll(getParentMenusForEntity(menus));
        return menuList;
    }
    @Override
	public HashSet<Long> getMenuByRoleWithoutParent(Long[] roleIds) {
		Long roleId = null;
		HashSet<Long> menus = new HashSet<Long>();
		for (int i = 0; i < roleIds.length; i++) {
			roleId = roleIds[i];
			if(roleMenuMap.get(roleId)!=null){
				menus.addAll(roleMenuMap.get(roleId));
			}

		}
		return menus;

	}

    @Override
    public Collection<Long> getParentMenus(Collection<Long> menus) {
        PrivMenuBO menu = null;
        Long parentId = null;
        List<Long> menusTemp = new ArrayList<Long>();
        menusTemp.addAll(menus);
        List<Long> menusAdd = new ArrayList<Long>();
        for (Long menuId : menusTemp) {
            menu = getMenuById(menuId);
            if (menu != null) {
                parentId = menu.getParentId();
                menus.add(parentId);
                if(parentId!=0)
                menusAdd.add(parentId);
            }
        }
        if (menusAdd.size() != 0) {
            menusTemp = null;
            menus.addAll(getParentMenus(menusAdd));
        }
        return menus;
    }


    private Collection<PrivMenuBO> getParentMenusForEntity(Collection<Long> menus) {
    	List<PrivMenuBO> menuList = new ArrayList<PrivMenuBO>();
    	 Long parentId = null;
         List<Long> menusTemp = new ArrayList<Long>();
         menusTemp.addAll(menus);
         List<Long> menusAdd = new ArrayList<Long>();
         for (Long menuId : menusTemp) {
        	 PrivMenuBO menu = getMenuById(menuId);
             if (menu != null) {
                 parentId = menu.getParentId();
                 menus.add(parentId);
                 if(parentId!=0)
                 menusAdd.add(parentId);
                 menuList.add(menu);
             }
         }
         if (menusAdd.size() != 0) {
             menusTemp = null;
             menuList.addAll(getParentMenusForEntity(menusAdd));
         }
         return menuList;
    }
    /**
     * @return 作为入口资源的菜单资源关系
     */
    @Override
  public Map<Long, Long> findMenuEnterSource() {
        Map<Long, Long> remenuResourceMap = new ConcurrentHashMap<Long, Long>();
//        Set<Long> set = menuResourceMap.keySet();
//        for (Long long1 : set) {
//        	HashMap<Long, Integer> map = menuResourceMap.get(long1);
//        	for (Map.Entry<Long, Integer> entry : map.entrySet()) {
//        		if(entry.getValue().equals(1)){
//        			remenuResourceMap.put(long1, entry.getKey());
//        		}
//        	}
//		}
        return remenuResourceMap;
    }


    @Override
    public Map<Long, List<Long>> getMenuResourceByRole(Long[] roleIds) {
        Long roleId = null;
        Map<Long, List<Long>> menuResAll = new ConcurrentHashMap<Long, List<Long>>();
        Map<Long, List<Long>> menuRes = null;
//        for (int i = 0; i < roleIds.length; i++) {
//            roleId = roleIds[i];
//            menuRes = roleMenuMap.get(roleId);
//            if (menuRes != null) {
//                menuResAll.putAll(menuRes);
//            }
//        }
        return menuResAll;
    }

    /**
     * @param menuDao the menuDao to set
     */
    public void setMenuDao(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    /**
     */
	public boolean setPlugInMenuDao(String _path, String _level, Long existMenuId) {
		if (existMenuId == null || menuMap.get(existMenuId) == null) {
			Long menuId = null;
			PrivMenuBO privMenuBO = null;
			PrivMenu menu = new PrivMenu();
			menu.setPath(_path);
			menu.setExt2(_level);
			List<PrivMenu> menus = menuDao.selectSubList(menu);
			for (PrivMenu privMenu : menus) {
				if (privMenu != null) {
					menuId = privMenu.getId();
					if (menuId != null) {
						privMenuBO = new PrivMenuBO(privMenu);
						menuMap.put(menuId, privMenuBO);

						if(Strings.isNotBlank(privMenuBO.getResourceCode())){
						    menuMapCode2Id.put(privMenuBO.getResourceCode(), menuId);
						}

						resourceUrlAndIdMap.put(menuId, privMenuBO.getResourceNavurl());
					}
				}
			}
			return true;
		}else{
			return false;
		}
    }

    /**
     * @param menuResourceDao the menuResourceDao to set
     */
//    public void setMenuResourceDao(MenuResourceDao menuResourceDao) {
//        this.menuResourceDao = menuResourceDao;
//    }

    /**
     */
    public void setRoleMenuDao(RoleMenuDao roleMenuDao) {
        this.roleMenuDao = roleMenuDao;
    }

	@Override
	public Integer getMenuPathLength() {
		// TODO Auto-generated method stub
		 return pathLengthCache.get();
	}


    @Override
    public void destroy() {

    }

	@Override
	public void init() {
		// TODO Auto-generated method stub
	}
}
