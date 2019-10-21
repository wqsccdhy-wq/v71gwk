package com.seeyon.apps.businessorganization.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator,Role_NAME.BusinessOrganizationManager})
public class BusinessAccountController extends BaseController {

    protected OrgManager       orgManager;
    protected OrgManagerDirect orgManagerDirect;

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }
	
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView result = new ModelAndView("apps/businessorganization/account/index");
        return result;
    }
	
    public ModelAndView business(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	User user = AppContext.getCurrentUser();
    	ModelAndView result = new ModelAndView("apps/businessorganization/account/business");
    	result.addObject("isAdmin", user.isGroupAdmin() || user.isAdministrator());
        return result;
    }

	/**
	 * 多维组织表单页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    public ModelAndView createBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView result = new ModelAndView("apps/businessorganization/account/businessform");
        return result;
    }
    
    public ModelAndView businessDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView result = new ModelAndView("apps/businessorganization/account/businessdetail");
        return result;
    }

}
