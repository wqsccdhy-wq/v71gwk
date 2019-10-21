package com.seeyon.apps.businessorganization.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

/**
 * <p>
 * Title: T2组织模型部门维护控制器
 * </p>
 * <p>
 * Description: 主要针对单位组织进行维护功能
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: seeyon.com
 * </p>
 * 
 * @version CTP2.0
 */
@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator,Role_NAME.BusinessOrganizationManager})
public class BusinessRoleController extends BaseController {
	private final static Log   log = LogFactory.getLog(BusinessRoleController.class);

    protected OrgManager       orgManager;
    protected OrgManagerDirect orgManagerDirect;

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }

    public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("apps/businessorganization/role/roleList");
		return result;
	}
    
    public ModelAndView roleEdit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("apps/businessorganization/role/roleNew");
		return result;
	}
}
