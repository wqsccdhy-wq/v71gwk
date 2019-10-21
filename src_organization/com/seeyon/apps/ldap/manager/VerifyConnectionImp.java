package com.seeyon.apps.ldap.manager;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.config.LDAPProperties;
import com.seeyon.apps.ldap.dao.AdDaoImp;
import com.seeyon.apps.ldap.domain.V3xLdapSwitchBean;
import com.seeyon.ctp.util.ServerDetector;

public class VerifyConnectionImp extends AdDaoImp implements VerifyConnection {
    
    private static Log log = LogFactory.getLog(VerifyConnectionImp.class);

    /* (non-Javadoc)
     * @see com.seeyon.apps.ldap.manager.VerifyConnection#verify(com.seeyon.apps.ldap.domain.V3xLdapSwitchBean)
     */
    @Override
    public boolean verify(V3xLdapSwitchBean ldapSwitchBean) throws Exception {
        DirContext ctx = null;
        boolean verificationResults = true;
        try {
            Hashtable<Object, Object> env = new Hashtable<Object, Object>();
            env.put(Context.SECURITY_AUTHENTICATION, LDAPProperties.LDAP_SIMPLE);
            env.put(Context.PROVIDER_URL,
                    LDAPConfig.getInstance().createUrlString(ldapSwitchBean.getLdapUrl(),
                            Integer.parseInt(ldapSwitchBean.getLdapPort())));
            env.put(Context.SECURITY_PRINCIPAL, ldapSwitchBean.getLdapAdmin());
            env.put(Context.SECURITY_CREDENTIALS, ldapSwitchBean.getLdapPassword());
            env.put(Context.INITIAL_CONTEXT_FACTORY, SUN_JNDI_PROVIDER);
            if ("1".equals(ldapSwitchBean.getLdapSSLEnabled())) {//SSl连接验证
                env.put(Context.SECURITY_PROTOCOL, "ssl");
                if(ServerDetector.isWebSphere()){
                    env.put("javax.net.ssl.trustStore",KEYSTORE);
                }else{
                    System.setProperty("javax.net.ssl.trustStore", KEYSTORE);
                }
            }
            
/*            {
                //调试日志
                log.info("------------------ldap info ------------------------");
                log.info(Context.SECURITY_AUTHENTICATION + " : " + LDAPProperties.LDAP_SIMPLE);
                log.info(Context.PROVIDER_URL + " : " + 
                        LDAPConfig.getInstance().createUrlString(ldapSwitchBean.getLdapUrl(),
                                Integer.parseInt(ldapSwitchBean.getLdapPort())));
                log.info(Context.SECURITY_PRINCIPAL + " : " + ldapSwitchBean.getLdapAdmin());
                log.info(Context.SECURITY_CREDENTIALS + " : " + ldapSwitchBean.getLdapPassword());
                log.info(Context.INITIAL_CONTEXT_FACTORY + " : " + SUN_JNDI_PROVIDER);
                
                log.info("getLdapSSLEnabled : " + ldapSwitchBean.getLdapSSLEnabled());
                log.info("ServerDetector.isWebLogic : "+ ServerDetector.isWebLogic());
                log.info("javax.net.ssl.trustStore : "+ KEYSTORE);
                log.info("serverId : " + ServerDetector.getServerId());
                log.info("----------------------------------------------------");
            }*/
            
            ctx = new InitialDirContext(env);
        } catch (Exception e) {
            log.error("目录服务器配置错误", e);
            verificationResults = false;
        } finally {
            closeCtx(ctx);
        }
        return verificationResults;
    }
}
