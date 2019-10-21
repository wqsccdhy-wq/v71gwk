/**
 * $Author: yans $
 * $Rev: 5688 $
 * $Date:: 2012-10-19 11:35:31#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.dao;

import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.privilege.po.PrivMenu;
import com.seeyon.ctp.util.FlipInfo;

/**
 * <p>Title: 菜单的DAO接口</p>
 * <p>Description: 本程序实现对数据库中的菜单的管理</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public interface MenuDao {

    /**
     * 根据菜单ID获取菜单对象
     * @param menuId 菜单ID
     * @return 找到的菜单对象或者返回null在菜单不存在时
     */
    public PrivMenu selectById(Long menuId);

    /**
     * 根据查询条件找到所有满足条件的菜单对象
     * @param menu 使用PrivMenu的实例对象中设置的属性值作为查询条件
     * @return 找到的菜单对象列表或者返回null在没有符合条件的菜单时
     */
    public List<PrivMenu> selectList(PrivMenu menu);

    /**
     * 根据查询条件找到所有满足条件的菜单对象
     * @param menu 使用PrivMenu的实例对象中设置的属性值作为查询条件
     * @param fi 翻页信息对象
     * @return 找到的菜单对象列表或者返回null在没有符合条件的菜单时
     */
    public List<PrivMenu> selectList(PrivMenu menu, FlipInfo fi);

    /**
     * 根据查询条件找到所有满足条件的菜单对象, 模糊匹配查找菜单的Path字段
     * 用于查找当前菜单的下级菜单
     * @param menu 使用PrivMenu的实例对象中设置的属性值作为查询条件
     * @param fi 翻页信息对象
     * @return 找到的菜单对象列表或者返回null在没有符合条件的菜单时
     */
    public List<PrivMenu> selectListByPath(PrivMenu menu, FlipInfo fi);

    /**
     * 查询菜单下一级菜单
     * @param menu 使用PrivMenu的实例对象中设置的属性值作为查询条件
     * @return 找到的菜单对象列表或者返回null在没有符合条件的菜单时
     */
    public List<PrivMenu> selectSubList(PrivMenu menu);

    /**
     * 新建菜单
     * @param menu 需要新建的菜单对象
     * @return 创建菜单的ID
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public Long insertMenu(PrivMenu menu) throws BusinessException;

    /**
     * 批量新建菜单
     * @param menus 需要新建的菜单对象的列表
     * @return 创建菜单的ID列表
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    @SuppressWarnings("rawtypes")
    public List insertMenuPatchAll(List<PrivMenu> menus) throws BusinessException;

    /**
     * 删除菜单
     * @param menu 要删除的菜单属性，可以是菜单ID或者菜单对象
     * @return 删除是否成功
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public boolean deleteMenu(Object menu) throws BusinessException;

    /**
     * 更新菜单
     * @param menu 需要删除的菜单对象
     * @return 更新菜单的ID
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public Long updateMenu(PrivMenu menu) throws BusinessException;

    /**
     * 批量更新菜单对象
     * @param menu 菜单对象
     * @throws 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
     */
    public void updateMenuPatchAll(List<PrivMenu> menus) throws BusinessException;

    /**
     * 获得当前最大的路径
     * @param parentPath 父菜单路径
     * @param level 菜单层级
     * @return 当前最大的路径
     */
    public String selectMaxPath(String parentPath, Integer level);

    /**
     * 获得菜单的父菜单
     * @param menu 菜单
     */
    public Long findParentMenu(PrivMenu menu);

    /**
     * 
     * @方法名称: selectUnModifiable
     * @功能描述: 查找不可勾选的资源
     * @参数 ：@return
     * @返回类型：List<PrivMenu>
     * @创建时间 ：2015年12月9日 下午2:08:50
     * @创建人 ： FuTao
     * @修改人 ： 
     * @修改时间 ：
     */
	public List<PrivMenu> selectUnModifiable();

	List<PrivMenu> selectDisable();

	/**
	 *<p>更新菜单</p>
	 * @param map
	 * <pre>
 	 * {
	 * 		id:菜单的Id
	 * 		sortid：排序号
	 * 		path:菜单Path
	 * 		target:打开方式
	 * 		updatedate:更新时间
	 * 		updateuserid:更新人员的Id
	 * }
	 * </pre>
	 * @date	2018年6月6日 上午9:33:11
	 * @since	V5 V7.0SP1
	 * @author  shuqi
	 */
	public void updateMenu(Map<String, Object> map);
	
}