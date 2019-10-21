/**
 * $Author: renw $
 * $Rev: 47640 $
 * $Date:: 2015-03-24 11:23:46#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;
import com.seeyon.ctp.privilege.bo.PrivTreeNodeBO;
import com.seeyon.ctp.privilege.po.PrivMenu;
import com.seeyon.ctp.privilege.po.PrivRoleMenu;
import com.seeyon.ctp.util.FlipInfo;

/**
 * <p>
 * Title: 菜单操作的接口
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
 *
 * @author futao
 * 
 * @deprecated <br />
 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager
 */
public interface MenuManager {

	/**
	 * 根据菜单ID获取到菜单
	 * 
	 * @param menuId
	 *            菜单ID
	 * @return 菜单对象
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             findById
	 */
	public PrivMenuBO findById(Long menuId);

	/**
	 * 根据人员ID和所属单位ID查找到关联的菜单
	 * 
	 * @param memberId
	 *            人员ID
	 * @param accountId
	 *            单位ID
	 * @return 菜单Map对象
	 * @throws 如果发生异常抛出异常
	 *             com.seeyon.ctp.common.exceptions.BusinessException
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getByMember
	 * 
	 * 
	 */
	public Map<Long, PrivMenuBO> getByMember(Long memberId, Long accountId) throws BusinessException;

	/**
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getByMember0
	 *
	 */
	public Map<Long, PrivMenuBO> getByMember0(Long memberId, Long accountId) throws BusinessException;

	/**
	 * 返回List
	 * 
	 * @param roleIds
	 * @return
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getListByRole
	 * 
	 */
	public List<PrivMenuBO> getListByRole(Long[] roleIds);

	/**
	 * 获取不可分配的资源
	 * 
	 * @return
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getAllocatedDisableMenu
	 * 
	 */
	public List<PrivMenuBO> getAllocatedDisableMenu() throws BusinessException;

	/**
	 * 
	 * @方法名称: getShortCutMenuOfMember
	 * @功能描述: 获取人员所有快捷菜单
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getShortCutMenuOfMember
	 * 
	 */
	public List<PrivMenuBO> getShortCutMenuOfMember(Long memberId, Long accountId) throws BusinessException;

	/**
	 * 根据角色查找到关联的菜单
	 * 
	 * @param roleIds
	 *            角色ID数组
	 * @return 菜单Map对象
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getByRole
	 * 
	 */
	public Map<Long, PrivMenuBO> getByRole(Long[] roleIds);

	/**
	 * 根据角色查找到关联的菜单不包含父菜单
	 * 
	 * @param roleIds
	 *            角色ID数组
	 * @return 菜单Map对象
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getByRoleWithoutParent
	 */
	public Map<Long, PrivMenuBO> getByRoleWithoutParent(Long[] roleIds);

	/**
	 * 根据角色查找到关联的菜单资源
	 * 
	 * @param roleIds
	 *            角色ID数组
	 * @return 菜单Map对象
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getMenuResourceByRole
	 */
	public Map<Long, List<Long>> getMenuResourceByRole(Long[] roleIds);

	/**
	 * 新建菜单
	 * 
	 * @param menu
	 *            需要新建的菜单对象
	 * @return 创建菜单的ID
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             create
	 * 
	 */
	public PrivMenuBO create(PrivMenuBO menu) throws BusinessException;

	/**
	 * 批量新建菜单
	 * 
	 * @param menus
	 *            需要新建的菜单对象
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             createPatch
	 * 
	 */
	public void createPatch(List<PrivMenuBO> menus) throws BusinessException;

	/**
	 * 更新菜单
	 * 
	 * @param menu
	 *            需要更新的菜单对象
	 * @return 更新菜单的ID
	 * @throws 如果发生异常抛出异常
	 *             com.seeyon.ctp.common.exceptions.BusinessException
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             updateMenu
	 * 
	 */
	public Long updateMenu(PrivMenuBO menu) throws BusinessException;

	/**
	 * 批量更新菜单
	 * 
	 * @param menus
	 *            需要更新的菜单对象
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             updatePatch
	 * 
	 */
	public void updatePatch(List<PrivMenuBO> menus) throws BusinessException;

	/**
	 * 更新菜单的路径和层级，用于菜单维护页面更新菜单树
	 * 
	 * @param parent
	 *            需要更新的菜单对象的父菜单ID
	 * @param menuIds
	 *            需要更新的菜单ID
	 * @throws 如果发生异常抛出异常
	 *             com.seeyon.ctp.common.exceptions.BusinessException
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             updateMenuPath
	 * 
	 */
	public void updateMenuPath(Long parent, List<String> menuIds) throws BusinessException;

	/**
	 * 删除菜单
	 * 
	 * @param menu
	 *            需要删除的菜单
	 * @return 是否成功
	 * @throws 如果发生异常抛出异常
	 *             com.seeyon.ctp.common.exceptions.BusinessException
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             deleteMenu
	 * 
	 */
	public boolean deleteMenu(PrivMenu menu) throws BusinessException;

	/**
	 * 根据父菜单删除下级菜单
	 * 
	 * @param menu
	 *            父菜单ID
	 * @return 是否成功
	 * @throws 如果发生异常抛出异常
	 *             com.seeyon.ctp.common.exceptions.BusinessException
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             deleteMenuByParentId
	 * 
	 */
	public boolean deleteMenuByParentId(Long menu) throws BusinessException;

	/**
	 * 删除菜单
	 * 
	 * @param res
	 *            要删除的菜单
	 * @return 是否成功
	 * @throws 如果发生异常抛出异常
	 *             com.seeyon.ctp.common.exceptions.BusinessException
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             deleteMenu
	 * 
	 */
	public boolean deleteMenu(Long[] menus) throws BusinessException;

	/**
	 * 查找符合条件的菜单
	 * 
	 * @param menu
	 *            使用PrivMenu的实例对象中设置的属性值作为查询条件
	 * @return 找到的菜单对象列表或者返回null在没有符合条件的菜单时
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             findMenus
	 * 
	 */
	public List<PrivMenuBO> findMenus(PrivMenuBO menu);

	/**
	 * 根据入口资源的ID查询菜单
	 * 
	 * @param resId
	 * @return
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             findMenusbyEnterRes
	 * 
	 */
	public List<PrivMenuBO> findMenusbyEnterRes(Long resId);

	/**
	 * 获取系统设置停用的菜单
	 * 
	 * @return
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getConfigDisableMenu
	 * 
	 */
	public List<PrivMenuBO> getConfigDisableMenu();

	/**
	 * 查找符合条件的菜单
	 * 
	 * @param fi
	 *            翻页信息对象
	 * @param param
	 *            查询条件Map
	 * @return 找到的菜单对象列表或者返回null在没有符合条件的菜单时
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             findMenus
	 * 
	 */
	public FlipInfo findMenus(FlipInfo fi, @SuppressWarnings("rawtypes") Map param);

	/**
	 * 复制产品版本的菜单配置
	 * 
	 * @param fromVersion
	 * @param toVersion
	 * @throws 如果发生异常抛出异常
	 *             com.seeyon.ctp.common.exceptions.BusinessException
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             copyMenus
	 * 
	 */
	public void copyMenus(String fromVersion, String toVersion) throws BusinessException;

	/**
	 * 获得菜单的父菜单
	 * 
	 * @param menu
	 *            菜单
	 * @throws BusinessException
	 *             如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             findParentMenu
	 * 
	 */
	public Long findParentMenu(PrivMenuBO menu) throws BusinessException;

	/**
	 * 根据父菜单ID获得下级菜单
	 * 
	 * @param menu
	 *            父菜单id
	 * @throws BusinessException
	 *             如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             findSubMenus
	 * 
	 */
	public List<PrivMenu> findSubMenus(Long menu) throws BusinessException;

	/**
	 * 根据人员ID获取其有权限的业务生成器的菜单
	 * 
	 * @param memberId
	 *            人员ID
	 * @return
	 * @throws BusinessException
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getBusinessMenuByMember
	 * 
	 */
	public List<PrivMenuBO> getBusinessMenuByMember(Long memberId, Long AccountId) throws BusinessException;

	/**
	 * 根据人员ID获取其有权限的业务生成器的菜单
	 * 
	 * @param memberId
	 *            人员ID
	 * @param AccountId
	 *            单位编号
	 * @param containLinkSystem
	 *            是否包含关联系统菜单
	 * @return
	 * @throws BusinessException
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getBusinessMenuByMember
	 * 
	 */
	public List<PrivMenuBO> getBusinessMenuByMember(Long memberId, Long AccountId, Boolean containLinkSystem)
			throws BusinessException;

	/**
	 * @param menu
	 * @param parent
	 * @return
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getMenuPath
	 * 
	 */
	public PrivMenuBO getMenuPath(PrivMenuBO menu, PrivMenuBO parent);

	/**
	 * @param ids
	 *            业务生成器的菜单id
	 * @return
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getPrivMenu4Form
	 * 
	 */
	Map<Long, PrivMenuBO> getPrivMenu4Form(Long[] ids);

	/**
	 * 
	 * @方法名称: getMenusOfMember
	 * @功能描述: 根据人员ID和所属单位ID查找关联的资源
	 * @参数 ：@param memberId 人员ID
	 * @参数 ：@param accountId 单位ID
	 * @参数 ：@return 资源对象列表
	 * @参数 ：@throws 如果发生异常抛出异常 BusinessException
	 * @返回类型：List<PrivMenuBO>
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getMenusOfMember
	 * 
	 */
	public List<PrivMenuBO> getMenusOfMember(Long memberId, Long accountId) throws BusinessException;

	/**
	 * 
	 * @方法名称: getTreeNodes
	 * @功能描述: 获取菜单树
	 * @参数 ：@param memberId 用户编号
	 * @参数 ：@param accountId 单位编号
	 * @参数 ：@param roleId 角色编号
	 * @参数 ：@param showAll 是否显示所有
	 * @参数 ：@param version
	 * @参数 ：@param appResCategory 资源类型
	 * @参数 ：@param isAllocated 是否可以分配
	 * @参数 ：@param treeNodes4Back
	 * @参数 ：@param treeNodes4Front 预制的
	 * @参数 ：@param isCheckBusiness 是否校验业务生成器
	 * @参数 ：@return
	 * @参数 ：@throws BusinessException
	 * 
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getTreeNodes
	 * 
	 */
	public Map<String, List<PrivTreeNodeBO>> getTreeNodes(String memberId, String accountId, String roleId,
			String showAll, String version, String appResCategory, String isAllocated,
			List<PrivTreeNodeBO> treeNodes4Back, List<PrivTreeNodeBO> treeNodes4Front, boolean isCheckBusiness)
			throws BusinessException;

	/**
	 * 
	 * @方法名称: findUnModifiable
	 * @功能描述: 获取不可用的菜单
	 * @参数 ：@return
	 * @返回类型：HashSet<Long>
	 * 
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             findUnModifiable
	 * 
	 */
	public HashSet<Long> findUnModifiable();

	/**
	 * 
	 * @方法名称: cleanPrivData
	 * @功能描述: 清理菜单数据
	 * @参数 ：@param roleId
	 * @参数 ：@throws BusinessException
	 * @返回类型：void
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             cleanPrivData
	 * 
	 */
	public void cleanPrivData(Long roleId) throws BusinessException;

	/**
	 * 
	 * @方法名称: getMenuByCode
	 * @功能描述: 通过原resource_code查询菜单
	 * @参数 ：@param code
	 * @参数 ：@return
	 * @返回类型：PrivMenuBO
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getMenuByCode
	 * 
	 */
	public PrivMenuBO getMenuByCode(String code);

	/**
	 * @param role
	 *            角色ID
	 * @return 不能修改的角色和资源关系列表, Map<菜单ID, PrivRoleMenu>
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             findUnModifiableRoleMenuByRole
	 * 
	 */
	public Map<Long, PrivRoleMenu> findUnModifiableRoleMenuByRole(Long role);

	/**
	 * 
	 * @方法名称: setPlugInMenuDao
	 * @功能描述: 加载插件菜单
	 * @参数 ：@param path 菜单path
	 * @参数 ：@param level 菜单级别
	 * @参数 ：@param existMenuId 如果已经存在不用加载 返回false
	 * @参数 ：@return
	 * @返回类型：boolean
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             setPlugInMenuDao
	 * 
	 */
	public boolean setPlugInMenuDao(String path, String level, Long existMenuId);

	/**
	 * 
	 * @throws BusinessException
	 * @方法名称: getMenusOfMemberForM1
	 * @功能描述: 获取插件菜单
	 * @参数 ：@param memberId
	 * @参数 ：@param accountId
	 * @参数 ：@return
	 * @返回类型：List<PrivMenuBO>
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getMenusOfMemberForM1
	 * 
	 */
	public List<PrivMenuBO> getMenusOfMemberForM1(Long memberId, Long accountId) throws BusinessException;

	/**
	 * 根据角色获得关联的URL列表
	 * 
	 * @param roleIds
	 *            角色ID数组
	 * @return 关联的资源URL列表
	 * 
	 * @deprecated <br />
	 *             由于该接口和开放给外部的“插件菜单”接口重名属于历史问题<br />
	 *             请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.
	 *             getUrlsByRole
	 * 
	 */
	public HashSet<String> getUrlsByRole(Long[] roleIds);

}