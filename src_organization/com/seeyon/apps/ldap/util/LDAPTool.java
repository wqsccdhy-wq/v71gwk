package com.seeyon.apps.ldap.util;

import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.dao.AdDaoImp;
import com.seeyon.apps.ldap.dao.LdapDao;
import com.seeyon.apps.ldap.dao.LdapDaoImp;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.usermapper.dao.UserMapperDao;

public class LDAPTool {
    
    //private static Log log = LogFactory.getLog(LDAPTool.class);

    static LDAPConfig lconfig = null;

    static public Authenticator createAuthenticator(UserMapperDao userMapperDao) {
        BaseAuthenticator a = new BaseAuthenticator();

        a.setUserMapperDao(userMapperDao);
        a.setLdao(providerLdapDao());

        return a;
    }

    static void init() {
        lconfig = LDAPConfig.createInstance(LogFactory.getLog(LDAPConfig.class));
    }

    static public boolean canLocalAuth(String uid) {
        init();

        if (0 == lconfig.getEnable())
            return true;

        Map<String, String> ms = LDAPConfig.getLocalAuthMembers();
        if (ms == null || ms.isEmpty())
            return false;

        String u = ms.get(uid);
        if (StringUtils.hasText(u))
            return true;

        return false;
    }

    /**
     * 判断是否是本地认证
     * 
     * @return
     */
    static public boolean canLocalAuth() {
        init();
        if(SystemEnvironment.hasPlugin(LdapConstants.LDAP_PLUGIN_ID)){
        	return 0 == lconfig.getEnable();
        }else{
        	return true;
        }
    }

    static public LdapDao providerLdapDao() {
        init();

        String type = providerLdapType();

        LdapDao dao = null;
        if (LDAPConfig.ADMEMBER.equals(type)) {
            dao = new AdDaoImp();
            //((AdDaoImp) dao).setLog(LogFactory.getLog(AdDaoImp.class));
        } else {
            dao = new LdapDaoImp();
            //((LdapDaoImp) dao).setLog(LogFactory.getLog(LdapDaoImp.class));
        }
        dao.setLDAPConfig(lconfig);

        return dao;
    }

    static public String providerLdapType() {
        init();
        return lconfig.getType();// +".member"
    }

    static public LDAPConfig catchLDAPConfig() {
        init();
        return lconfig;
    }
}// end class
