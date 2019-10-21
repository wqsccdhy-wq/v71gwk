package com.seeyon.ctp.organization.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator})
public class GuestController extends BaseController {
	private OrgManager orgManager;
	
    public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public ModelAndView showGuestframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/organization/guest/guestHome");
        User user = AppContext.getCurrentUser();

        boolean isGroupVer = (Boolean) (SysFlag.sys_isGroupVer.getFlag());
        mav.addObject("isGroupVer", isGroupVer);
        mav.addObject("isGroupAdmin", user.isGroupAdmin());
        mav.addObject("isAccountAdministrator", user.isAdministrator());
        return mav;
    }
    
    public ModelAndView showGuestAccountFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/organization/guest/guestAccountFrame");
        return mav;
    }
    
    public ModelAndView showSpecialAccountFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView result = new ModelAndView("apps/organization/guest/specialAccountFrame");
        Long accountId = AppContext.currentAccountId();
        String accountIdStr = request.getParameter("accountId");
        if (Strings.isNotBlank(accountIdStr)) {
            accountId = Long.valueOf(accountIdStr);
        }
        result.addObject("accountId", accountId);
        return result;
    }
    
}
