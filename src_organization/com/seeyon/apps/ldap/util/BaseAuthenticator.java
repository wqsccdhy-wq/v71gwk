package com.seeyon.apps.ldap.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.ldap.dao.LdapDao;
import com.seeyon.ctp.common.po.usermapper.CtpOrgUserMapper;
import com.seeyon.ctp.common.usermapper.dao.UserMapperDao;
import com.seeyon.ctp.util.Strings;

public class BaseAuthenticator implements Authenticator {
    private static final Log log = LogFactory.getLog(BaseAuthenticator.class);

    UserMapperDao            userMapperDao;
    LdapDao                  ldao;

    public CtpOrgUserMapper auth(String uid, String password) {
        CtpOrgUserMapper ep = null;

        if (ldao == null || userMapperDao == null)
            return ep;

        ep = userMapperDao.getLoginName(uid, getType());
        if (ep == null)
            return ep;

        String  ldapCode = getLdapUsername(ep);
        ldapCode = ldapCode.replace("\u00A0", " ");
        boolean ok = ldao.auth(ldapCode, password);

        return ok ? ep : null;
    }

    protected String getLdapUsername(CtpOrgUserMapper ep) {
        StringBuilder sb = new StringBuilder();
        
        String exUnitCode = ep.getExUnitCode();
        if(Strings.isNotBlank(exUnitCode) && (exUnitCode.indexOf(",DC=") != -1 || exUnitCode.indexOf(",dc=") != -1)) {
        	return exUnitCode;
        }

        sb.append(ep.getExUnitCode());
    	if(!exUnitCode.toLowerCase().endsWith(LDAPTool.catchLDAPConfig().getBaseDn())) {
    		sb.append(",");
    		sb.append(LDAPTool.catchLDAPConfig().getBaseDn());
    	}

        log.info("dn=" + sb.toString());
        return sb.toString();
    }

    public void setUserMapperDao(UserMapperDao userMapperDao) {
        this.userMapperDao = userMapperDao;
    }

    public LdapDao getLdao() {
        return ldao;
    }

    public void setLdao(LdapDao ldao) {
        this.ldao = ldao;
    }

    public String getType() {
        return LDAPTool.catchLDAPConfig().getType();
    }
}// end class
