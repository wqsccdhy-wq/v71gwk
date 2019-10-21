package com.seeyon.apps.ldap.manager;

import com.seeyon.apps.ldap.domain.V3xLdapSwitchBean;

public interface VerifyConnection {

    /**
     * 验证目录服务器设置
     * 
     * @return
     */
    public abstract boolean verify(V3xLdapSwitchBean ldapSwitchBean) throws Exception;

}