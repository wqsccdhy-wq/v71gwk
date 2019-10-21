/**
 * $Author:Macx$
 * $Rev: 1.0 $
 * $Date:: 2014-3-4 上午11:43:40#$:
 *
 * Copyright (C) 2014 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */

package com.seeyon.ctp.menu.manager;

import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.login.bo.MenuBO;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;

/**
 * <p>Title: 菜单接口</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: seeyon.com</p>
 * @since APP5.1
 */
public interface PluginMenuManager {

    /**
     * 自定义菜单
    * @Description: 取到指定用户可以访问的菜单
    * @param memberId
    * @param loginAccountId
    * @return
     */
    List<MenuBO> getAccessMenuBO(long memberId, long loginAccountId);
	/**
	 * 新增业务菜单,追加到门户已配置的菜单中
	 * @param memberIds
	 * @param menu
	 */
    void appendBizMenuToPortal(List<V3xOrgMember> orgmemberList, PrivMenuBO menu) throws BusinessException ;
    void appendBizNavToMobileMasterPortal(PrivMenuBO menu,String canshare) throws BusinessException;
}