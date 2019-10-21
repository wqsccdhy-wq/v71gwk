/**
 * $Author: gaohang $
 * $Update Author:futao
 * $Rev: 24448 $
 * $Date:: 2013-06-06 18:00:51#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.dao;

import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.privilege.po.PrivMenu;
import com.seeyon.ctp.privilege.po.PrivRoleMenu;

/**
 * <p>Title: 角色菜单的DAO接口</p>
 * <p>Description: 本程序实现对数据库中的角色菜单的管理</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: seeyon.com</p>
 */
public interface RoleMenuDao {

    /**
     * 根据条件查询角色菜单对象
     * @param privRoleReource 查询条件
     * @return Return all persistent instances of the <code>Menu</code> entity.
     */
    public List<PrivRoleMenu> selectList(PrivRoleMenu privRoleMenu);

    /**
     * @param role 角色ID
     * @return 不能修改的角色和菜单关系列表
     */
    public List<PrivRoleMenu> selectUnModifiableByRole(Long role);

    /**
     * 创建角色菜单
     * @param privRoleReource 角色菜单对象
     * @return 创建角色菜单的ID
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public Long insertRoleMenu(PrivRoleMenu privRoleMenu) throws BusinessException;

    /**
             * 批量全量创建角色菜单
     * @param roleReources 需要创建的角色菜单对象列表
     * @return 创建角色菜单的ID列表
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public List<Long> insertRoleMenuPatchAll(List<PrivRoleMenu> roleReources) throws BusinessException;
    

    /**
                 * 批量更新创建角色菜单
     * @param roleMenu
     * @param isIncrement 是否增量更新角色下的菜单  true：全量更新  false：增量更新
     * @return
     * @throws BusinessException  
     */
	List<Long> insertRoleMenuPatchAll(List<PrivRoleMenu> roleMenu, boolean isIncrement) throws BusinessException;

    /**
     * 删除角色菜单
     * @param roleMenu 需要删除的角色菜单，可以是角色菜单ID或者角色菜单对象
     * @return 是否成功
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public boolean deleteRoleMenu(Object roleMenu) throws BusinessException;

    /**
     * 更新角色菜单
     * @param privRoleReource 角色菜单对象
     * @return 是否成功
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public boolean updateRoleMenu(PrivRoleMenu privRoleReource) throws BusinessException;

    /**
     * 批量更新角色菜单
     * @param Menus 需要更新的角色菜单对象列表
     * @return 是否成功
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public boolean updateRoleMenuPatchAll(List<PrivRoleMenu> Menus) throws BusinessException;
    /**
     * 查找不可勾选的菜单
     * @return
     */
    public List<PrivMenu> selectUnModifiable();

}
