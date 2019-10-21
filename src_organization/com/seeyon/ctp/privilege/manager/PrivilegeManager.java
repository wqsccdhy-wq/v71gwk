/**
 * 
 */
package com.seeyon.ctp.privilege.manager;

import java.util.List;

import com.seeyon.ctp.common.authenticate.domain.UserPrivilegeCheck;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.enums.RoleTypeEnum;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;

/**
 * <p>Title: 权限数据操作接口</p>
 * <p>Description: 本程序提供查询菜单资源数据和动态添加菜单项的方法</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: seeyon.com</p>
 */
public interface PrivilegeManager extends UserPrivilegeCheck{

	  /**
		* 
		* @方法名称: getMenusOfMember
		* @功能描述: 根据人员ID和所属单位ID查找关联的菜单
		* @参数 ：@param memberId 人员ID
		* @参数 ：@param accountId 单位ID
		* @参数 ：@return
		* @参数 ：@throws BusinessException 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
		* @返回类型：List<PrivMenuBO>
		* @创建时间 ：2015年12月3日 下午4:27:28
		* @创建人 ：
		* @修改人 ： 
		* @修改时间 ：
		*/
	    public List<PrivMenuBO> getMenusOfMember(Long memberId, Long accountId) throws BusinessException;
	    /**
	     * 
	     * @方法名称: getAllMenus
	     * @功能描述: 获取所有菜单
	     * @参数 ：@return
	     * @返回类型：List<PrivMenuBO> 菜单列表
	     * @创建时间 ：2015年12月3日 下午4:28:57
	     * @创建人 ：
	     * @修改人 ： 
	     * @修改时间 ：
	     */
	    public List<PrivMenuBO> getAllMenus()throws BusinessException;

	    /**
	     * 
	     * @方法名称: getMenus
	     * @功能描述: 根据查询条件查找菜单
	     * @参数 ：@param menu 使用PrivMenuBO的实例对象中设置的属性值作为查询条件
	     * @参数 ：@return 菜单列表
	     * @返回类型：List<PrivMenuBO>
	     * @创建时间 ：2015年12月3日 下午4:29:22
	     * @创建人 ：
	     * @修改人 ： 
	     * @修改时间 ：
	     */
	    public List<PrivMenuBO> getMenus(PrivMenuBO menu)throws BusinessException;

	    /**
	     * 
	     * @方法名称: getAllShortCutMenus
	     * @功能描述: 得到所有快捷菜单
	     * @参数 ：@return菜单列表
	     * @返回类型：List<PrivMenuBO>
	     * @创建时间 ：2015年12月3日 下午4:30:01
	     * @创建人 ：
	     * @修改人 ： 
	     * @修改时间 ：
	     */
	    public List<PrivMenuBO> getAllShortCutMenus()throws BusinessException;

	    /**
	     * 
	     * @方法名称: getShortCutMenusOfMember
	     * @功能描述: 根据人员ID和所属单位ID得到所有快捷菜单
	     * @参数 ：@param memberId
	     * @参数 ：@param accountId
	     * @参数 ：@return
	     * @参数 ：@throws BusinessException
	     * @返回类型：List<PrivMenuBO>
	     * @创建时间 ：2015年12月3日 下午4:30:22
	     * @创建人 ：
	     * @修改人 ： 
	     * @修改时间 ：
	     */
	    public List<PrivMenuBO> getShortCutMenusOfMember(Long memberId, Long accountId) throws BusinessException;
	    /**
		 * 
		 * @方法名称: insertMenus
		 * @功能描述: 添加菜单项,业务生成器批量添加菜单
		 * @参数 ：@param menus List<菜单BO>
		 * @参数 ：@param role 角色
		 * @参数 ：@param auth
		 * @参数 ：@param reset 是否清除原有role相应menu
		 * @参数 ：@throws BusinessException
		 * @返回类型：void
		 * @创建时间 ：2015年12月3日 下午4:12:38
		 * @创建人 ：
		 * @修改人 ： 
		 * @修改时间 ：
		 */
		public void insertMenus(List<PrivMenuBO> menus, V3xOrgRole role, String auth, boolean reset) throws BusinessException;

		/**
		 *
		 * @方法名称: insertMenus
		 * @功能描述: 添加菜单项,业务生成器批量添加菜单
		 * @参数 ：@param menus List<菜单BO>
		 * @参数 ：@param role 角色
		 * @参数 ：@param auth
		 * @参数 ：@throws BusinessException
		 * @返回类型：void
		 * @创建时间 ：2015年12月3日 下午4:12:38
		 * @创建人 ：
		 * @修改人 ：
		 * @修改时间 ：
		 */
		public void insertMenus(List<PrivMenuBO> menus, V3xOrgRole role, String auth) throws BusinessException;

		/**
		 * 
		 * @方法名称: insertNewMenu
		 * @功能描述: 添加菜单项
		 * @参数 ：@param menu 要生成的菜单对象
		 * @参数 ：@param url 菜单访问的URL
		 * @参数 ：@return 创建的菜单ID
		 * @参数 ：@throws BusinessException
		 * @返回类型：Long
		 * @创建时间 ：2015年12月3日 下午4:30:56
		 * @创建人 ：
		 * @修改人 ： 
		 * @修改时间 ：
		 */
	    public Long insertNewMenu(PrivMenuBO menu, String url) throws BusinessException;
	    /**
		 * 
		 * @方法名称: insertCustomizeMenus
		 * @功能描述: 添加业务生成器菜单时 ，同时保持数据到个性化菜单信息表
		 * @param auth 选人界面传来的人员信息 Member|1234567,Departmet|2345678...
	     * @param menuId 添加的菜单id
		 * @参数 ：@throws BusinessException
		 */
		public void insertCustomizeMenus(String auth, Long menuId) throws BusinessException;
	    /**
	     * 
	     * @方法名称: deleteMenu
	     * @功能描述: 删除菜单项
	     * @参数 ：@param menuId 菜单编号
	     * @参数 ：@throws BusinessException
	     * @返回类型：void
	     * @创建时间 ：2015年12月3日 下午4:39:01
	     * @创建人 ：
	     * @修改人 ： 
	     * @修改时间 ：
	     * RBAC改造，弃用该方法
	     * @deprecated
	     */
	    public void deleteMenu(Long menuId) throws BusinessException;

	    /**
	     * 
	     * @方法名称: updateMenu
	     * @功能描述: 更新菜单项
	     * @参数 ：@param menu 要更新的菜单对象<br/>
	     *             要更新的菜单只能是业务生成器创建的菜单
	     * @参数 ：@return 更新菜单的ID
	     * @参数 ：@throws BusinessException 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
	     * @返回类型：Long 
	     * @创建时间 ：2015年12月3日 下午4:39:20
	     * @创建人 ：
	     * @修改人 ： 
	     * @修改时间 ：
	     * RBAC改造，弃用该方法
	     * @deprecated
	     */
	    public Long updateMenu(PrivMenuBO menu) throws BusinessException;
	    /**
	     * 
	     * @方法名称: getAllUseAbleMenus
	     * @功能描述: 获取可出现的菜单
	     * @参数 ：@return 菜单集合
	     * @参数 ：@throws BusinessException 如果发生异常抛出异常 com.seeyon.ctp.common.exceptions.BusinessException
	     * @返回类型：List<PrivMenuBO> 
	     * @创建时间 ：2015年12月3日 下午4:37:23
	     * @创建人 ：
	     * @修改人 ： 
	     * @修改时间 ：
	     */
	    public List<PrivMenuBO> getAllUseAbleMenus() throws BusinessException;
	    
	    /**
	     * 
	     * @方法名称: hasMenuCode
	     * @功能描述: 验证是否拥有菜单权限
	     * @参数 ：@param memberId 用户编号
	     * @参数 ：@param accountId 用户账号编号
	     * @参数 ：@param menuId 菜单编号
	     * @参数 ：@return
	     * @参数 ：@throws BusinessException
	     * @返回类型：boolean
	     * @创建时间 ：2015年12月7日 上午9:28:52
	     * @创建人 ： 
	     * @修改人 ： 
	     * @修改时间 ：
	     */
	    public boolean hasMenuCode(String code)throws BusinessException;
	    /**
	     * 
	     * @方法名称: findMenuById
	     * @功能描述: 通过菜单编号获取菜单
	     * @参数 ：@param menuId
	     * @参数 ：@return
	     * @返回类型：PrivMenuBO
	     * @创建时间 ：2015年12月7日 下午12:44:43
	     * @创建人 ： FuTao
	     * @修改人 ： 
	     * @修改时间 ：
	     */
		public PrivMenuBO findMenuById(Long menuId);
		/**
		 * 
		 * @方法名称: findMenus
		 * @功能描述: 
		 * @参数 ：@param menu
		 * @参数 ：@return
		 * @返回类型：List<PrivMenuBO>
		 * @创建时间 ：2015年12月7日 下午12:47:44
		 * @创建人 ： FuTao
		 * @修改人 ： 
		 * @修改时间 ：
		 */
		public List<PrivMenuBO> findMenus(PrivMenuBO menu);
		/**
		 * 
		 * @throws BusinessException 
		 * @方法名称: deleteByRole
		 * @功能描述: 删除角色
		 * @参数 ：@param roleId
		 * @返回类型：void
		 * @创建时间 ：2015年12月8日 上午9:15:51
		 * @创建人 ： FuTao
		 * @修改人 ： 
		 * @修改时间 ：
		 */
		public void deleteByRole(Long roleId) throws BusinessException;
		/**
		 * 
		 * @throws BusinessException 
		 * @方法名称: cleanFormPrivData
		 * @功能描述: 
		 * @参数 ：@param id
		 * @返回类型：void
		 * @创建时间 ：2015年12月17日 下午6:26:51
		 * @创建人 ： FuTao
		 * @修改人 ： 
		 * @修改时间 ：
		 */
		public void cleanFormPrivData(Long id) throws BusinessException;
		/**
	     * 根据人员的ID和所在单位ID验证是否有菜单项访问权限
	     * @param memberId 人员ID
	     * @param accountId 单位ID
	     * @param menuId 菜单ID
	     */
	    public boolean checkByMenuAndMember(Long memberId, Long accountId, Long menuId) throws BusinessException;
	    
	    /**
	     * 验证当前登录人员是否有菜单项访问权限
	     * @param menuId 菜单ID
	     */
	    public boolean hasMenu(Long menuId) throws BusinessException;

	    /**
	     * 根据人员ID、单位ID和请求URL验证访问的URL是否有权限
	     * @param memId 人员ID
	     * @param accountId 单位ID
	     * @param url 资源URL
	     */
	    public boolean checkByUrlAndMember(Long memberId, Long accountId, String url) throws BusinessException;
	    
	    /**
	     * 验证当前登录人员是否有访问的URL权限
	     * @param url 资源URL
	     */
	    public boolean hasUrl(String url) throws BusinessException;
	    
	    /**
	     * 验证当前登录人员是否有资源编号对应的资源的访问权限
	     * @param resourceCode 资源编号
	     * 
	     */
	    public boolean checkByReourceCode(String resourceCode) throws BusinessException;
	    /**
	     * 验证人员是否有资源编号对应的资源的访问权限
	     * @param resourceCode   资源编码
	     * @param memeberId		人员ID		
	     * @param accountId     单位ID
	     * @return
	     * @throws BusinessException
	     *
	     */
	    public boolean checkByReourceCode(String resourceCode,Long memeberId,Long accountId) throws BusinessException;
	    
	    /**
	     * 
	     * @方法名称: checkByRoleName
	     * @功能描述: 验证人员角色对应的资源的访问权限
	     * @参数 ：@param roleName 角色名称
	     * @参数 ：@param memberId 用户编号
	     * @参数 ：@param accountId 单位编号
	     * @参数 ：@return
	     * @参数 ：@throws BusinessException
	     * @返回类型：boolean
	     * @创建时间 ：2016年1月11日 下午8:53:36
	     * @创建人 ： FuTao
	     * @修改人 ： 
	     * @修改时间 ：
	     */
	   public boolean checkByRoleName(String roleName, Long memberId, Long accountId)throws BusinessException;
	    /**
	     * 根据资源code获取人员列表（有资源权限的人员）
	     * @param resourceId 资源code
	     * @param accountId  单位Id
	     * @return
	     * @throws BusinessException
	     */
	    public List<V3xOrgMember> getMembersByMenu(String resourceId,Long accountId) throws BusinessException;
	    /**
	     * 
	     * @方法名称: getMembersByResource
	     * @功能描述: 
	     * @参数 ：@param resourceId
	     * @参数 ：@param accountId
	     * @参数 ：@return
	     * @参数 ：@throws BusinessException
	     * @返回类型：List<V3xOrgMember>
	     * @创建时间 ：2015年12月18日 下午12:27:39
	     * @创建人 ： FuTao
	     * @修改人 ： 
	     * @修改时间 ：
	     */
		List<V3xOrgMember> getMembersByResource(String resourceId, Long accountId) throws BusinessException;
		/**
		 * 
		 * @方法名称: getPrivMenuBycode
		 * @功能描述: 通过code获取菜单
		 * @参数 ：@param code菜单编码
		 * @参数 ：@return
		 * @返回类型：PrivMenu
		 * @创建时间 ：2015年12月18日 下午12:53:14
		 * @创建人 ： FuTao
		 * @修改人 ： 
		 * @修改时间 ：
		 */
		public PrivMenuBO getPrivMenuBycode(String code);
		/**
		 * 
		 * @throws BusinessException 
		 * @方法名称: getMenusOfMemberForM1
		 * @功能描述: 获取用户菜单存在M1的
		 * @参数 ：@param userId 用户编号
		 * @参数 ：@param accountId 单位编号
		 * @参数 ：@return
		 * @返回类型：List<PrivMenuBO>
		 * @创建时间 ：2016年4月6日 上午11:37:03
		 * @创建人 ： FuTao
		 * @修改人 ： 
		 * @修改时间 ：
		 */
		public List<PrivMenuBO> getMenusOfMemberForM1(Long userId, Long accountId) throws BusinessException;
		/**
		 * 
		 * @方法名称: getMenuValidity
		 * @功能描述: 过去菜单本地缓存是否过期
		 * @参数 ：@param memberId
		 * @参数 ：@param accountId
		 * @参数 ：@return
		 * @参数 ：@throws BusinessException
		 * @返回类型：boolean
		 * @创建时间 ：2016年7月27日 下午2:00:29
		 * @创建人 ： FuTao
		 * @修改人 ： 
		 * @修改时间 ：
		 */
		public boolean getMenuValidity(Long memberId, Long accountId) throws BusinessException;
		
		/**
		 * 创建自定义员角色，并设置菜单资源。此角色只允许查看，不允许修改，删除角色及对应菜单。
		 * 目前 创建第三方报表管理员角色，此角色只允许查看，不允许修改，删除角色及对应菜单。
		 * @param roleName  角色名称     eg: 帆软致远报表管理员
		 * @param roleCode  角色编码     eg: reportSeeyonAdmin
		 * @param 角色枚举类型，目前只允许创建一种类型  V3xOrgEntity.ROLETYPE_REPORT
		 * @param resourceCodes  角色拥有的资源,资源的code集合   ：F08_report_view,F08_report_manage
		 * @throws BusinessException 
		 */
		public void createCustomRoleResource(String roleName, String roleCode, RoleTypeEnum roleType, List<String> resourceCodes) throws BusinessException;
		
}