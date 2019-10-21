package com.seeyon.ctp.privilege.manager;

import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.authenticate.domain.UserHelper;
import com.seeyon.ctp.common.constants.Constants;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.po.safetyprotection.SafetyProtectionLog;
import com.seeyon.ctp.common.safetyprotection.enums.SafetyProtectionHandleResultEnum;
import com.seeyon.ctp.common.safetyprotection.enums.SafetyProtectionTypeEnum;
import com.seeyon.ctp.common.safetyprotection.manager.SafetyProtectionLogManager;
import com.seeyon.ctp.common.web.util.WebUtil;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.dubbo.RefreshInterfacesAfterUpdate;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.ExtendCheck;
import org.apache.commons.logging.Log;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ProcessInDataSource(name = DataSourceName.BASE)
public class PrivlegeSecurityManagerImpl extends AbstractSystemInitializer implements PrivlegeSecurityManager {

    private static final Log log = CtpLogFactory.getLog(PrivlegeSecurityManagerImpl.class);

    private SafetyProtectionLogManager safetyProtectionLogManager;
    //不用做集群
    private Map<String, ExtendCheck> extendNameChecks = new ConcurrentHashMap<String, ExtendCheck>();

    public SafetyProtectionLogManager getSafetyProtectionLogManager() {
        if(safetyProtectionLogManager == null){
            safetyProtectionLogManager = (SafetyProtectionLogManager)AppContext.getBean("safetyProtectionLogManager");
        }
        return safetyProtectionLogManager;
    }

    public void setSafetyProtectionLogManager(SafetyProtectionLogManager safetyProtectionLogManager) {
        this.safetyProtectionLogManager = safetyProtectionLogManager;
    }

    @RefreshInterfacesAfterUpdate(inface = ExtendCheck.class)
    public void initialize() {
        Map<String, ExtendCheck> extendChecks = AppContext.getBeansOfType(ExtendCheck.class);
        if (extendChecks != null && !extendChecks.isEmpty()) {
            for (ExtendCheck extendCheck : extendChecks.values()) {
                extendNameChecks.put(extendCheck.getName(), extendCheck);
            }
        }

        log.info("加载RBAC权限校验扩展角色：" + extendNameChecks.keySet());
    }

    @SuppressWarnings("unchecked")
    public void validateRole(User user, String methodName, Set<String> roleTypes, Set<String> extendRoles) throws BusinessException {
        if (Strings.isNotEmpty(roleTypes)) {
            if (roleTypes.contains(Role_NAME.NULL.toString())) {
                return;
            }

            
            Set<String> userRoles = (Set<String>) user.getProperty(UserHelper.USERROLES);
//            if (Strings.isEmpty(userRoles)) {
//                userRoles = orgManager.getMemberRolesForSet(user.getId(), user.getLoginAccount());
//                user.setProperty(key, userRoles);
//            }

            if (Strings.isNotEmpty(userRoles)) {
                if (userRoles.contains(Role_NAME.AccountAdministrator.name())) {
                    String systemVer = AppContext.getSystemProperty("system.ProductId");
                    if ("0".equals(systemVer) || "7".equals(systemVer) || "8".equals(systemVer) || "12".equals(systemVer)) {
                        if (roleTypes.contains(Role_NAME.AccountAdministrator.name()) || roleTypes.contains(Role_NAME.GroupAdmin.name()) || roleTypes.contains(Role_NAME.SystemAdmin.name())) {
                            return;
                        }
                    }
                }

                if (isContainOne(roleTypes, userRoles)) {
                    return;
                }
            }
        }

        if (Strings.isNotEmpty(extendRoles) && isExtendCheck(WebUtil.getRequest(), user, extendRoles)) {
            return;
        }

        //资源权限验证失败
        String msg = null;
        if(isFromMobile(user)) {
        	msg = new StringBuilder("{\"success\":false,").append("\"code\":").append(404).append(",\"message\":\"")
        			.append(ResourceUtil.getStringByParams("loginUserState.wuquanfangwen"))
        			.append("\"").append("}").toString();
        }else {
            StringBuilder sb = new StringBuilder(ResourceUtil.getStringByParams("loginUserState.wuquanfangwen"));
            //为方便开发和测试，把资源地址隐藏输出
            sb.append("<div style=\"display:none\">").append(Strings.toHTML(methodName)).append("</div>");
            msg = sb.toString();
        }

        saveSafetyProtectionLog();// 记录越权访问日志

		BusinessException be = new BusinessException(msg);
        //此errorCode在CTPDispatcherServlet做日志记录处理 
        be.setCode("invalid_resource_code");
        //此类异常全页面显示，带有整体UE界面设计效果
        be.setFullPage(true);
        throw be;
    }
    private boolean isFromMobile( User user) {
        String agentFrom = user == null ? null : user.getUserAgentFrom();
        return Constants.login_useragent_from.mobile.name().equals(agentFrom);
    }
    private boolean isContainOne(Set<String> roleTypes, Set<String> memberRoles) {
        Set<String> a, b;
        if (roleTypes.size() > memberRoles.size()) {
            a = roleTypes;
            b = memberRoles;
        } else {
            a = memberRoles;
            b = roleTypes;
        }

        for (String r : b) {
            if (a.contains(r)) {
                return true;
            }
        }

        return false;
    }

    private boolean isExtendCheck(HttpServletRequest request, User user, Set<String> extendRoles) {
        for (String extendRole : extendRoles) {
            ExtendCheck extendCheck = extendNameChecks.get(extendRole);
            boolean b = extendCheck != null && extendCheck.check(request, user);
            if (b) {
                return true;
            }
        }

        return false;
    }

    /**
     * 保存越权访问日志
     */
    private void saveSafetyProtectionLog(){
        try{
            HttpServletRequest request = AppContext.getRawRequest();
            User user = AppContext.getCurrentUser();

            SafetyProtectionLog safetyProtectionLog = new SafetyProtectionLog();
            safetyProtectionLog.setIdIfNew();
            safetyProtectionLog.setSafetyType(SafetyProtectionTypeEnum.UNAUTHORIZED_ACCESS.getKey());
            safetyProtectionLog.setLoginName(user.getLoginName());
            safetyProtectionLog.setMemberId(AppContext.currentUserId());
            safetyProtectionLog.setAccountId(AppContext.currentAccountId());
            safetyProtectionLog.setDepartmentId(user.getDepartmentId());
            safetyProtectionLog.setHappenTime(new Date());
            safetyProtectionLog.setIpAddress(Strings.getRemoteAddr(request));
            safetyProtectionLog.setHandleResult(SafetyProtectionHandleResultEnum.HOLD_BACK.getKey());
            safetyProtectionLog.setRequestUrl(request.getRequestURL().toString());

            getSafetyProtectionLogManager().save(safetyProtectionLog);
        }catch(Exception e){// 出现异常方法内部处理，不影响其它业务功能
            log.error("保存越权访问日志出错：",e);
        }
    }
}
