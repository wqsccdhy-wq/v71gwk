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
package com.seeyon.ctp.permission.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgAccount;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;


/**
 * 单位注册数分配管理
 * @author gao
 *
 */
@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin})
public class PermissionAssignController extends BaseController {
	private final static Log   log = LogFactory.getLog(PermissionAssignController.class);

	protected OrgManager orgManager;
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

	public ModelAndView showAccountFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("apps/permission/permissionassign/permissionIndex");
		List<V3xOrgAccount> orgAccounts = orgManagerDirect.getAllAccounts(true, true, null, null, null);
        Collections.sort(orgAccounts, CompareSortEntity.getInstance());
        List<WebV3xOrgAccount> treeResult = new ArrayList<WebV3xOrgAccount>();
        for (V3xOrgAccount a : orgAccounts) {
            Long parentId = OrgHelper.getParentUnit(a)==null?-1L:OrgHelper.getParentUnit(a).getId();
            WebV3xOrgAccount treeAccount = new WebV3xOrgAccount(a.getId(), a.getName(), parentId);
            treeAccount.setV3xOrgAccount(a);
            treeResult.add(treeAccount);
        }        
        request.setAttribute("ffunitTree", treeResult);
       
		return result;
	}
	public ModelAndView showAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView();	
		result = new ModelAndView("apps/permission/permissionassign/licdistributeform");
		List<V3xOrgAccount> orgAccounts = orgManagerDirect.getAllAccounts(true, true, null, null, null);
        Collections.sort(orgAccounts, CompareSortEntity.getInstance());       
        request.setAttribute("ffunit", orgAccounts);

		return result;
	}
	
}
