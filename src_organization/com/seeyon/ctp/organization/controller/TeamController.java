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
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

/**
 * <p>Title: T2组织模型职务级别维护控制器</p>
 * <p>Description: 主要针对单位组织进行维护功能</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * @version CTP2.0
 */
@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator,Role_NAME.HrAdmin,Role_NAME.DepAdmin})
public class TeamController extends BaseController {

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

    public ModelAndView showTeamframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView result = new ModelAndView("apps/organization/team/listteam");
        String fromSection = request.getParameter("fromSection");
        if(Strings.isNotBlank(fromSection)) {
            result.addObject("fromSection", Boolean.valueOf(fromSection));
        } else {
            result.addObject("fromSection", false);
        }
        Long accountId = AppContext.currentAccountId();
        String accountIdStr = request.getParameter("accountId");
        if (Strings.isNotBlank(accountIdStr)) {
            accountId = Long.valueOf(accountIdStr);
        }
        result.addObject("accountId", accountId);
        result.addObject("isGroup", OrgConstants.GROUPID.equals(accountId));
        return result;
    }
    
    /**
     * 组管理  批量修改
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView teamMemberBatchModify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView result = new ModelAndView("apps/organization/team/teamMemberBatchModify");
        String ids = request.getParameter("ids");
        if(Strings.isNotBlank(ids)){
        	String teamNames = "";
        	String[] idArr = ids.split(",");
        	for(String id : idArr){
        		V3xOrgTeam team = orgManager.getTeamById(Long.valueOf(id));
        		if(team != null){
        			String teamName = team.getName();
        			if(Strings.isBlank(teamNames)){
        				teamNames = teamName;
        			}else{
        				teamNames = teamNames + "," + teamName;
        			}
        		}
        	}
        	result.addObject("teamNames", teamNames);
        	String subTeamNames = teamNames;
        	if(subTeamNames.length()>40) {
        		subTeamNames = teamNames.substring(0, 40) + "...";
        	}
        	result.addObject("subTeamNames", subTeamNames);
        }
        String leaveMemberId = request.getParameter("leaveMemberId");
        if(Strings.isNotBlank(leaveMemberId)){
        	V3xOrgMember leaveMember = orgManager.getMemberById(Long.valueOf(leaveMemberId));
        	result.addObject("leaveMemberId", leaveMemberId);
        	result.addObject("leaveMemberName", leaveMember.getName());
        }
        
        AppContext.removeSessionArrribute("BatchteamMembers");
        return result;
    }
}
