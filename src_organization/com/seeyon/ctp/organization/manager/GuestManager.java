package com.seeyon.ctp.organization.manager;

import java.util.HashMap;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.FlipInfo;

public interface GuestManager {

	/**
	 * 判断登录前guest账号是否启用
	 * @return
	 * @throws BusinessException
	 */
	boolean isGuestEnable() throws BusinessException;

	/**
	 * 更新guest账号状态
	 * @return
	 * @throws BusinessException
	 */
	boolean changeEnable() throws BusinessException;

	/**
	 * guest用户列表
	 * @param fi
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	FlipInfo guestList(FlipInfo fi, Map params) throws BusinessException;

	/**
	 * 创建特殊账号
	 * @param map
	 * @return
	 * @throws BusinessException
	 */
	Long createGuest(Map map) throws BusinessException;
	
	/**
	 * 更新特殊账号
	 * @param map
	 * @return
	 * @throws BusinessException
	 */
	Long updateGuest(Map map) throws BusinessException;
	
	/**
	 * 删除特殊账号
	 * @param ids
	 * @return
	 * @throws BusinessException
	 */
	Boolean deleteGuests(Long[] ids) throws BusinessException;

	/**
	 * 查看特殊账号详细信息
	 * @param memberId
	 * @return
	 * @throws BusinessException
	 */
	HashMap viewOne(Long memberId) throws BusinessException;

	/**
	 * 获取系统默认的登录前门户账号
	 * @return
	 */
	String getDefaultLoginName();

	/**
	 * 获取系统默认的登录前门户账号密码
	 * @return
	 */
	String getDefaultLoginPassword();


}
