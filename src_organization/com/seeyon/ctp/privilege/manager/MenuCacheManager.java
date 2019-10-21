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

package com.seeyon.ctp.privilege.manager;

import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;

/**
 * @author renwei
 * @editor futao
 * @deprecated 
 * MenuCacheManager接口MenuManager接口合并和<br />
 * 请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager
 */
public interface MenuCacheManager {
    
    /**
     * 根据人员ID_单位ID获取菜单list
     * @param memberId
     * @return
     * @throws BusinessException 
     * MenuCacheManager接口MenuManager接口合并和<br />
     * 
     * 请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.getMenus
     */
    public Map<Long, PrivMenuBO> getMenus(Long memberId, Long accountId) throws BusinessException;
    
    /**
     * 更新时间戳
     */
    public void updateBiz();
    
    /**
     * 
     * @方法名称: reSetMM1Menus
     * @功能描述: 判断M1是否启用，如果启用缓存中没有数据则重新加载数据
     * @参数 ：@param memberId
     * @参数 ：@param accountId
     * @参数 ：@return
     * @参数 ：@throws BusinessException
     * @返回类型：Map<Long,PrivMenuBO>
     * @创建时间 ：2016年4月6日 上午11:35:11
     * @创建人 ： FuTao
     * @修改人 ： 
     * @修改时间 ：
     * 
     * MenuCacheManager接口MenuManager接口合并和<br />
     * 请替换为com.seeyon.ctp.privilege.manager.PrivilegeMenuManager.reSetMM1Menus
     */
    public Map<Long, PrivMenuBO> reSetMM1Menus(Long memberId, Long accountId) throws BusinessException;
}
