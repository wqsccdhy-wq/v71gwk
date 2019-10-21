package com.seeyon.apps.ldap.dao;

import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.domain.EntryValueBean;

/**
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @author lilong
 * @version 2008-11-6
 * @version CTP2.0
 */
public abstract class AbstractLdapDao implements LdapDao {
    private static Log            log               = LogFactory.getLog(AbstractLdapDao.class);

    //    protected DirContext ctx;

    protected LDAPConfig          lconfig           = null;

    protected static final String SUN_JNDI_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
    protected boolean             canEmptyPassword  = false;

    public AbstractLdapDao() {
    }

    /**
     * 取得LDAP连接实例
     */
    public DirContext getContext() throws Exception {
        DirContext ctx = null;
        try {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, SUN_JNDI_PROVIDER);
            env.put(Context.PROVIDER_URL, LDAPConfig.getInstance().getLdapUrl());// LDAP服务器的地址:端口。
            log.info("LDAPConfig.getInstance().getLdapUrl(): "+LDAPConfig.getInstance().getLdapUrl());
            env.put(Context.SECURITY_AUTHENTICATION, LDAPConfig.getInstance().getAuthenication());
            log.info("LDAPConfig.getInstance().getAuthenication(): "+LDAPConfig.getInstance().getAuthenication());
            env.put(Context.SECURITY_PRINCIPAL, LDAPConfig.getInstance().getAdmin());
            log.info("LDAPConfig.getInstance().getAdmin(): "+LDAPConfig.getInstance().getAdmin());
            env.put(Context.SECURITY_CREDENTIALS, LDAPConfig.getInstance().getPassWord());
            //log.info("LDAPConfig.getInstance().getPassWord(): "+LDAPConfig.getInstance().getPassWord());
            env.put(Context.REFERRAL, "follow"); 
            ctx = new InitialLdapContext(env,null);
        } catch (Exception e) {
            log.error("", e);
        }
        return ctx;
    }

    protected void closeCtx(DirContext ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    /**
     * LDAP/AD用户账号是否存在
     * @param dn 条目
     * @return boolean true or false
     */
    public boolean isUserExist(String dn) throws Exception {
        DirContext ctx = null;
        try {
            ctx = getContext();
            if (ctx == null) {
                throw new Exception("Context null");
            }
    		
        	if(!dn.toLowerCase().endsWith(LDAPConfig.getInstance().getBaseDn().toLowerCase())) {
        		dn = dn + "," + LDAPConfig.getInstance().getBaseDn();
        	}
            Attributes attrs = ctx.getAttributes(dn);
            if (attrs != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            closeCtx(ctx);
        }
    }

    public boolean auth(String username, String password) {
        DirContext dct = null;
        boolean ok = false;
        try {
            dct = createInitialContext(username, password);
            if (dct != null) {
                ok = true;
            }
        } catch (Exception e) {
            log.error("com.seeyon.apps.ldap.dao.AbstractLdapDao", e);

        }
        try {
            if (dct != null)
                dct.close();
        } catch (Exception ee) {
            log.error("com.seeyon.apps.ldap.dao.AbstractLdapDao", ee);
        }
        return ok;
    }

    public DirContext createInitialContext(String username, String password) throws NamingException {
        String tmp = catchLDAPConfig().getAuthenication();
        String url = catchLDAPConfig().getLdapUrl();
        if (url == null) {
            throw new NamingException("no ldap host");
        }
        log.info("url=" + url);
        if (tmp == null) {
            tmp = "simple";
        }
        if (!this.canEmptyPassword) {
            // 禁止空密码
            if (password == null || "".equals(password)) {
                return null;
            }
        }
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, SUN_JNDI_PROVIDER);// 必须这样写，无论用什么LDAP服务器。
        env.put(Context.PROVIDER_URL, url);// LDAP服务器的地址:端口。
        // tmp = "simple";
        env.put(Context.SECURITY_AUTHENTICATION, tmp);
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.REFERRAL, "follow"); 

        return new InitialLdapContext(env,null);

    }
    
    public DirContext createNormalInitialContext(String username, String password) throws NamingException {
        String tmp = catchLDAPConfig().getAuthenication();
        String url = catchLDAPConfig().getLdapUrl();
        if (url == null) {
        	throw new NamingException("no ldap host");
        }
        url = url.replaceAll(":"+LDAPConfig.getSSLPORT(),":"+LDAPConfig.getNORMALPORT());
        log.info("normalUrl=" + url);
        if (tmp == null) {
            tmp = "simple";
        }
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, SUN_JNDI_PROVIDER);// 必须这样写，无论用什么LDAP服务器。
        env.put(Context.PROVIDER_URL, url);// LDAP服务器的地址:端口。
        // tmp = "simple";
        env.put(Context.SECURITY_AUTHENTICATION, tmp);
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.REFERRAL, "follow"); 

        return new InitialLdapContext(env,null);

    }

    public void setLDAPConfig(LDAPConfig val) {
        this.lconfig = val;
    }

    public LDAPConfig getLDAPConfig() {
        return this.lconfig;
    }

    protected LDAPConfig catchLDAPConfig() {
        return getLDAPConfig() == null ? LDAPConfig.getInstance() : getLDAPConfig();
    }

    public boolean isCanEmptyPassword() {
        return canEmptyPassword;
    }

    public void setCanEmptyPassword(boolean canEmptyPassword) {
        this.canEmptyPassword = canEmptyPassword;
    }

    protected String getUUID() {
        return String.valueOf(UUID.randomUUID().getMostSignificantBits()).replaceAll("[-]", "_");
    }

    @Override
    public Attributes findUser(String uid) throws Exception {
        return null;
    }

    @Override
    public String getLoginName(String dn) throws Exception {
        return null;
    }

    @Override
    public void userTreeView(String baseDn, List<EntryValueBean> list) throws Exception {
    }

    @Override
    public List<EntryValueBean> ouTreeView(String baseDn, boolean isRoot) throws Exception {
        return null;
    }

    @Override
    public String[] getuserAttribute(String uid) throws Exception {
        return null;
    }

    @Override
    public boolean createNode(String dn, String[] parameter) throws Exception {
        return false;
    }

    @Override
    public void modifyUserPassWord(String rdn, String oldPassWord, String newPassword) throws Exception {
    }
}
