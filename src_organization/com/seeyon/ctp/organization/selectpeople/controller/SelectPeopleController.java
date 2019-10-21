package com.seeyon.ctp.organization.selectpeople.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.JoinOrgManagerDirect;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-4-25
 */
public class SelectPeopleController extends BaseController {

    private OrgManager           orgManager;
    private JoinOrgManagerDirect joinOrgManagerDirect;

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setJoinOrgManagerDirect(JoinOrgManagerDirect joinOrgManagerDirect) {
        this.joinOrgManagerDirect = joinOrgManagerDirect;
    }

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView("common/SelectPeople/SelectPeople");
        User user = AppContext.getCurrentUser();

        List<V3xOrgAccount> allAccounts = this.orgManager.getAllAccounts();
        boolean onlyShowChildrenAccount = false;
        if (null != request.getParameter("onlyShowChildrenAccount") && !"undefined".equals(request.getParameter("onlyShowChildrenAccount"))) {
            onlyShowChildrenAccount = Boolean.valueOf(request.getParameter("onlyShowChildrenAccount").toString());
        }
        V3xOrgAccount rootAccount = this.orgManager.getRootAccount();
        List<V3xOrgAccount> accessableAccounts = null;
        Set<Long> accessableAccountIds = new HashSet<Long>();
        List<String> accessableRootAccountId = new UniqueList<String>();

        if ("true".equals(request.getParameter("showAllAccount")) || user.isSystemAdmin() || user.isAuditAdmin() || user.isGroupAdmin()) {
            accessableAccounts = allAccounts;
            accessableRootAccountId.add(rootAccount.getId().toString());
        } else if(!(Boolean)SysFlag.sys_isGroupVer.getFlag()){//企业版如果配置了单位，也放开
            accessableAccounts = new ArrayList<V3xOrgAccount>();
            accessableAccounts.add(rootAccount);
            allAccounts = accessableAccounts;
            accessableRootAccountId.add(rootAccount.getId().toString());
        }else{
            List<V3xOrgAccount> _accessableAccounts = this.orgManager.accessableAccounts(user.getId());
            List<V3xOrgAccount> _childAccounts = this.orgManager.getChildAccount(user.getLoginAccount(), false);

            for (V3xOrgAccount a : _accessableAccounts) {
                accessableAccountIds.add(a.getId());
            }

            boolean isAccountInGroup = this.orgManager.isAccountInGroupTree(user.getLoginAccount());

            accessableAccounts = new ArrayList<V3xOrgAccount>(_accessableAccounts.size());

            for (V3xOrgAccount a : _accessableAccounts) {
                if (onlyShowChildrenAccount && !_childAccounts.contains(a) && !a.getId().equals(user.getLoginAccount())) {
                    continue;
                }

                V3xOrgAccount _account = new V3xOrgAccount(a);
                /*
                 * 如果上级单位不在我的访问范围内:
                 * 1. 如果是在集团树下面：直接挂在集团下面（设置父是集团ID）
                 * 2. 如果是独立单位，直接把自己作为根（设置父是-1）
                */
                if (_account.getSuperior0().longValue() != -1 && !accessableAccountIds.contains(_account.getSuperior0())) {
                    _account.setSuperior(isAccountInGroup && accessableAccountIds.contains(rootAccount.getId()) ? rootAccount.getId() : -1L);
                }

                if (onlyShowChildrenAccount && _account.getId().equals(user.getLoginAccount())) {
                    _account.setSuperior(-1L);
                }

                accessableAccounts.add(_account);

                if (_account.getSuperior0().longValue() == -1) {
                    accessableRootAccountId.add(_account.getId().toString());
                }
            }
        }

        if ((Boolean) SysFlag.selectPeople_showAccounts.getFlag()) {
            List<V3xOrgLevel> groupLevels = this.orgManager.getAllLevels(rootAccount.getId());
            mv.addObject("groupLevels", groupLevels);
        }

        //如果我的单位不能访问集团单位，就不在单位切换中显示
        List<V3xOrgAccount> accessableAccounts4Tree = new ArrayList<V3xOrgAccount>(accessableAccounts);
        boolean isGroupAccessable = false;
        if (user.isSystemAdmin() || user.isAuditAdmin() || user.isGroupAdmin()) {
            isGroupAccessable = true;
        } else {
            isGroupAccessable = orgManager.isAccessGroup(user.getLoginAccount());
        }
        if (!isGroupAccessable) {
            accessableAccounts4Tree.remove(rootAccount);
        }

        Long firstAccountId = null;
        for (V3xOrgAccount account : accessableAccounts) {
            if (firstAccountId == null && !account.isGroup() && account.getExternalType() == OrgConstants.ExternalType.Inner.ordinal()) {
                firstAccountId = account.getId();
            }
        }

        mv.addObject("accessableRootAccountId", "'" + OrgHelper.join(accessableRootAccountId, "','") + "'");
        mv.addObject("isGroupAccessable", isGroupAccessable);

        Long vJoinAllowAccount = OrgHelper.getVJoinAllowAccount();
        boolean showVJoinPanel = true;
        if (user.isV5Member()) {
            if (!user.getLoginAccount().equals(vJoinAllowAccount)) {
                showVJoinPanel = false;
            }
        } else {
            firstAccountId = vJoinAllowAccount;
        }
        mv.addObject("showVJoinPanel", showVJoinPanel);
        mv.addObject("firstAccountId", firstAccountId);

        if (AppContext.hasPlugin("vjoin")) {
            List<V3xOrgAccount> joinAccounts = joinOrgManagerDirect.getAllAccounts();
            allAccounts.addAll(joinAccounts);
        }
        
        mv.addObject("isSubVjoinAdmin", orgManager.isRole(user.getId(), null, OrgConstants.Role_NAME.VjoinSubManager.name(), null));
        Collections.sort(allAccounts, CompareSortEntity.getInstance());
        mv.addObject("allAccounts", allAccounts);
        Collections.sort(accessableAccounts, CompareSortEntity.getInstance());
        mv.addObject("accessableAccounts", accessableAccounts);

        Collections.sort(accessableAccounts4Tree, CompareSortEntity.getInstance());
        request.setAttribute("ffaccessableAccounts4Tree", tree(accessableAccounts4Tree));

        Long currentDepartment = user.getDepartmentId();
        if (!Strings.equals(user.getLoginAccount(), user.getAccountId())) {
            V3xOrgDepartment dept = orgManager.getCurrentDepartment();

            if (dept != null) {
                currentDepartment = dept.getId();
            }
        }

        mv.addObject("currentDepartment", currentDepartment);

        return mv;
    }

    private List<Map<String, Object>> tree(List<V3xOrgAccount> accessableAccounts4Tree) {
        List<Map<String, Object>> r = new ArrayList<Map<String, Object>>(accessableAccounts4Tree.size());

        for (V3xOrgAccount a : accessableAccounts4Tree) {
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("id", a.getId());
            map.put("name", a.getName());
            map.put("superior", a.getSuperior0());

            r.add(map);
        }

        return r;
    }

    public ModelAndView saveAsTeam(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView("common/SelectPeople/saveAsTeam");
        return mv;
    }

    public ModelAndView showDetailPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView("common/SelectPeople/showDetailPost");
        return mv;
    }

    public ModelAndView selectPeople4Confirm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView("common/SelectPeople/selectPeople4Confirm");
        String name = request.getParameter("name");
        mv.addObject("name", name);
        return mv;
    }

}
