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
package com.seeyon.ctp.organization.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

/**
 * <p>Title: T2组织模型职务级别维护控制器</p>
 * <p>Description: 主要针对单位组织进行维护功能</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * @version CTP2.0
 */
@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator,Role_NAME.HrAdmin})
public class DistributeController extends BaseController {

    protected OrgManager       orgManager;
    protected OrgManagerDirect orgManagerDirect;
    public OrgManager getOrgManager() {
        return orgManager;
    }
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    public OrgManagerDirect getOrgManagerDirect() {
        return orgManagerDirect;
    }
    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }
    
    public ModelAndView showDistributeframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView result = new ModelAndView("apps/organization/distribute/listdistribute");
    	User user = AppContext.getCurrentUser();
    	Long accountId = AppContext.currentAccountId();
    	V3xOrgAccount account = orgManager.getAccountById(accountId);
    	//单位管理员/hr管理员 管理子单位
        if ((orgManager.isRole(user.getId(), accountId, OrgConstants.Role_NAME.AccountAdministrator.name()) 
        		|| orgManager.isRole(user.getId(), accountId, OrgConstants.Role_NAME.HrAdmin.name()))
        		&& account.allowManagementSubunit()) {
        	result.addObject("managementSubunit", true);
        }
        return result;
    }
}
