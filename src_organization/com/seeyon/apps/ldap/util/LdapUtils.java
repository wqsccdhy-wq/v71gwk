package com.seeyon.apps.ldap.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.sso.ADSSOHandShake;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.constants.LoginConstants;
import com.seeyon.ctp.common.constants.LoginResult;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.usermapper.CtpOrgUserMapper;
import com.seeyon.ctp.common.usermapper.dao.UserMapperDao;
import com.seeyon.ctp.login.LoginAuthenticationException;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.principal.NoSuchPrincipalException;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.util.Strings;

public class LdapUtils {
    
    private static final Log log              = LogFactory.getLog(LdapUtils.class);

    private static UserMapperDao    userMapperDao = null;

    private static PrincipalManager principalManager = null;

    private static OrgManager       orgManager       = null;
    
    /**
     * @return the userMapperDao
     */
    public static UserMapperDao getUserMapperDao() {
        if (userMapperDao == null) {
            userMapperDao = (UserMapperDao) AppContext.getBean("userMapperDao");
        }       
        return userMapperDao;
    }

    /**
     * @return the principalManager
     */
    public static PrincipalManager getPrincipalManager() {
        if (principalManager == null) {
            principalManager = (PrincipalManager) AppContext.getBean("principalManager");
        }       
        return principalManager;
    }

    /**
     * @return the orgManager
     */
    public static OrgManager getOrgManager() {
        if (orgManager == null) {
            orgManager = (OrgManager) AppContext.getBean("orgManager");
        }       
        return orgManager;
    }

    /**
     * 判断系统的ldap插件是否启用
     * @return
     */
    public static boolean isLdapEnabled() {
        boolean hasLdap = SystemEnvironment.hasPlugin(LdapConstants.LDAP_PLUGIN_ID);
        boolean isLdapEnabled = false;
        if(hasLdap) {
            isLdapEnabled = LDAPConfig.getInstance().getIsEnableLdap();
        }
        return isLdapEnabled;
    }
    
    /**
     * 判斷此人是否進行了ldap綁定
     * @return
     */
    public static boolean isBind(Long memberId) {
    	if(!isLdapEnabled()){
    		return false;
    	}
    	UserMapperDao userMapperDao =(UserMapperDao)AppContext.getBean("userMapperDao");
    	return userMapperDao.isbind(memberId);
    }
    
    /**
     * oa是否可以进行ad密码修改
     * @return
     */
    public static boolean isOaCanModifyLdapPwd() {
    	if(!isLdapEnabled()){
    		return false;
    	}
    	
    	boolean disableModifyLdapPsw = "1".equals(AppContext.getSystemProperty("ldap.disable.modify.password"));
    	return !disableModifyLdapPsw;
    }
    
    public static String[] authenticate(String username, String password) throws LoginAuthenticationException {
        //采用LDAP，username即为LDAP的用户名，否则应该A8自身的用户名
        if (!LDAPTool.canLocalAuth()) {
            if (password == null) {
                CtpOrgUserMapper ep = getUserMapperDao().getLoginName(username, LDAPTool.catchLDAPConfig().getType());
                if (null != ep) {
                    try {
                        V3xOrgMember t = getOrgManager().getMemberById(ep.getMemberId());
                        if(null == t || !t.isValid()) {
                            return null;
                        }
                    } catch (BusinessException e) {
                        // ignore
                    }
                    
                    return new String[] { ep.getLoginName(), password };
                } else {
                    throw new LoginAuthenticationException(LoginResult.ERROR_AD_ACCOUNT_BINDING);
                }

            }
                
            try {
                long memberId = getPrincipalManager().getMemberIdByLoginName(username);
                V3xOrgMember member = getOrgManager().getMemberById(memberId);
                if (member == null) {
                    return null;
                } else if (member.getIsAdmin()) { //管理员走本地认证
                    return null;
                }
            } catch (NoSuchPrincipalException e) {
                //ignore
            } catch (Exception e) {
                log.error("LDAP ERROR:", e);
                //ignore
            }

            Authenticator a = LDAPTool.createAuthenticator(getUserMapperDao());
            CtpOrgUserMapper ep = a.auth(username, password);

            if (ep != null) {
                return new String[] { ep.getLoginName(), password };
            } else { 
                //如果该账号已经绑定了，验证失败,不再往下走
                boolean isBind = getUserMapperDao().isbind(username);
                if(isBind){
                    throw new LoginAuthenticationException(LoginResult.ERROR_UNKNOWN_USER);
                }else if(!LDAPConfig.getInstance().getLdapCanOauserLogon()){
                //如果该账号没有绑定，并且默認沒有勾选可以进行a8验证，則验证失败,不再往下走
                    throw new LoginAuthenticationException(LoginResult.ERROR_UNKNOWN_USER);
                }
                //勾选可以进行a8验证，接着往下走
                return null;
            }
        } else { //采用A8本地认证，接着往下走
            return null;
        }
    }
    
    public static String[] authenticate(HttpServletRequest request, HttpServletResponse response)
            throws LoginAuthenticationException {
        if (!LdapUtils.isLdapEnabled()) {//如果ladp没有启用直接return
            return null;
        }
        //AD域登录代码
        String adssoToken = Strings.isBlank(request.getHeader("authorization"))? request.getHeader("Authorization"):request.getHeader("authorization");
        if(Strings.isBlank(adssoToken)){
            adssoToken=request.getParameter("authorization");
        }
        log.info("AD单点登录:"+adssoToken);
        String username = request.getParameter(LoginConstants.USERNAME);//用户名
        String password = request.getParameter(LoginConstants.PASSWORD);//密码
        
        try {
            long memberId = getPrincipalManager().getMemberIdByLoginName(username);
            V3xOrgMember member = getOrgManager().getMemberById(memberId);
            if(member.isVJoinExternal()){
              //采用A8本地认证，接着往下走
                return null;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        } 
        
        //不输入用户名和密码，token有值则走单点登录
        //token有值，但输入了用户名和密码则走下步认证（ldap的或进入下个登录认证）
        if (StringUtils.isNotBlank(adssoToken) && StringUtils.isBlank(username) && StringUtils.isBlank(password)) {
            String adLoginName = ADSSOHandShake.getInstance().getUserName(adssoToken);
            log.info("AD单点登录adLoginName:"+adLoginName);
            if (StringUtils.isBlank(adLoginName)) {
                throw new LoginAuthenticationException(LoginResult.ERROR_AD_ACCOUNT_ERROR);
            }
            return authenticate(adLoginName, null);
        }
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }
        return authenticate(username, password);
    }
}
