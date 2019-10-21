/**
 * $Author: gaohang $
 * $Rev: 15815 $
 * $Date:: 2013-03-09 14:53:54#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.ctp.privilege.bo.PrivMenuBO;
import com.seeyon.ctp.privilege.po.PrivRoleMenu;


/**
 * <p>Title: 权限Cache接口</p>
 * <p>Description: 本程序实现对内存中的权限模型的管理</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public interface PrivilegeCache {

    /**
     * 初始化缓存所有权限对象
     */
    public void init();

   

    /**
     * 获得所有缓存的菜单对象
     * @return 菜单对象列表
     */
    public List<PrivMenuBO> getAllMenu();
    /**
     * 
     * @方法名称: getAllMenuForCollection
     * @功能描述: 获得所有缓存的菜单对象
     * 为了减少一次循环
     */
    public Collection<PrivMenuBO> getAllMenuForCollection();
    /**
     * 根据菜单ID查找缓存中的菜单对象
     * @param MenuId 菜单ID
     * @return  PrivMenuBO 菜单对象
     */
    public PrivMenuBO getMenuById(Long menuId);
    
    public Map<Long, Long> findMenuEnterSource();

    
    /**
     * 根据菜单ID查找缓存中的资源ID列表
     * @param menuId 菜单ID
     * @return 资源ID列表
     */
    public Set<Long> getResourceIdsByMenu(Long menuId);

    /**
     * 根据角色ID查找缓存中的关联的资源URL
     * @param roleIds 角色ID数组
     * @return URL对象列表
     */
    public HashSet<String> getUrlsByRole(Long[] roleIds);

    /**
     * 更新菜单缓存对象
     * @param menu 菜单对象
     */
    public void updateMenu(PrivMenuBO menu);

    /**
     * 批量更新菜单缓存对象
     * @param menu 菜单列表
     */
    public void updateMenuAll(List<PrivMenuBO> menu);

    /**
     * 根据菜单ID删除缓存对象
     * @param menuId 菜单ID
     */
    public void deleteMenu(Long menuId);

    /**
     * 删除菜单缓存对象
     * @param res 菜单对象
     * @return 删除的菜单ID
     */
    public Long deleteMenu(Long[] menus);

    /**
     * 删除资源缓存对象
     * @param res 资源对象
     * @return 删除的资源ID
     */
    public Long deleteResource(Long[] res);

    /**
             *全量 更新角色资源关系
     * @param roleId 需要更新的角色ID
     * @param roleReses 要新增的角色资源对象列表
     */
    public void updateRoleMenu(Long roleId, List<PrivRoleMenu> roleReses);
    

	/**
	  *  更新角色资源关系
	 * @param roleId 需要更新的角色ID
	 * @param roleMenu 要新增的角色资源对象列表
	 * @param isIncrement 是否增量更新角色下的菜单  true：全量更新  false：增量更新
	 */
	public void updateRoleMenu(Long roleId, List<PrivRoleMenu> roleMenu, boolean isIncrement);
    
    /*v6.0 权限修改
     * 更新菜单资源关系
     * @param menuResesNew 要新增的菜单资源对象列表
     * @param menuResesOld 要更新的菜单资源对象列表
     
    public void updateMenuResource(List<PrivMenu> menuResesNew, List<PrivMenu> menuResesOld);
    */
    /**
     * 根据角色ID查找缓存中的关联的菜单
     * @param roleIds 角色ID数组
     * @return 菜单ID列表
     */
    public HashSet<Long> getMenuByRole(Long[] roleIds);
    
    /**
     * 根据角色ID查找缓存中的关联的菜单
     * @param roleIds 角色ID数组
     * @return 菜单ID列表
     */
    public HashSet<PrivMenuBO> getMenuByRoleFonEntity(Long[] roleIds);
    /**
     * 根据角色ID查找缓存中的关联的菜单不包含父菜单
     * @param roleIds 角色ID数组
     * @return 菜单ID列表
     */
    public HashSet<Long> getMenuByRoleWithoutParent(Long[] roleIds);

    /**
     * 根据角色ID查找缓存中的关联的菜单资源
     * @param roleIds 角色ID数组
     * @return 菜单ID列表
     */
    public Map<Long, List<Long>> getMenuResourceByRole(Long[] roleIds);

    /**
     * 
     * @方法名称: getMenuByCode
     * @功能描述: 通过原resource_code查询菜单
     * @参数 ：@param code
     * @参数 ：@return
     * @返回类型：PrivMenuBO
     * @创建时间 ：2015年12月9日 下午5:04:27
     * @创建人 ： FuTao
     * @修改人 ： 
     * @修改时间 ：
     */
	public PrivMenuBO getMenuByCode(String code);
	/**
     * 
     * @方法名称: setPlugInMenuDao
     * @功能描述: 加载插件菜单
     * @参数 ：@param path 菜单path
     * @参数 ：@param level 菜单级别
     * @参数 ：@param existMenuId 如果已经存在不用加载 返回false
     * @参数 ：@return
     * @返回类型：boolean
     * @创建时间 ：2016年4月5日 下午8:25:26
     */
	public boolean setPlugInMenuDao(String _path, String _level, Long existMenuId);

	/**
	  * 
	  * @方法名称: getMenuPathLength
	  * @功能描述: 查询菜单的path的长度
	  * @参数：@return
	  * @返回类型：Integer
	  * @创建时间：2016年12月13日下午8:55:58
	 */
	public Integer getMenuPathLength();


    /**
     * 查找到菜单的父菜单集合
     * @param menus 菜单列表
     * @return 父菜单集合
     */
	public Collection<Long> getParentMenus(Collection<Long> menus);

}
