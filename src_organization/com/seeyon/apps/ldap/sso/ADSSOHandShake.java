package com.seeyon.apps.ldap.sso;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;

import sun.misc.BASE64Decoder;

import com.seeyon.apps.ldap.config.LDAPConfig;

public class ADSSOHandShake {
    private static final Log      log       = LogFactory.getLog(ADSSOHandShake.class);
    private boolean               inited    = false;
    private static final String   keyPsName = "A8Server.keymap";
    private static ADSSOHandShake event     = null;

    private ADSSOHandShake() {

    }

    public static ADSSOHandShake getInstance() {
        if (event == null) {
            event = new ADSSOHandShake();
            try {
                event.init();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return event;
    }

    private GSContextpool pool = new GSContextpool();

    private void testInited() {
        if (inited)
            return;
        GSSContext ftemp = null;
        try {
            ftemp = pool.getContext();
            inited = true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            if (ftemp != null) {
                try {
                    ftemp.dispose();
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }

        }
    }

    public void init() throws IOException {
        try {
            setConfig();
            pool.init();
            testInited();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public String getADLoginName(String authorization) {
        if (authorization.startsWith("Negotiate ")) {
            String base64Str = authorization.substring("Negotiate ".length());
            if (base64Str.startsWith("TlRMTVNTUA")) {
                log.warn("TlRMTVNTUA 方式登录");
                return null;
            }

            BASE64Decoder de = new BASE64Decoder();
            byte[] token;
            try {
                token = de.decodeBuffer(base64Str);
                // return readKerberosUserName(token);
                GSSContext context = pool.getContext();
                try {
                    byte[] atoken = context.acceptSecContext(token, 0, token.length);
                    if (context.isEstablished()) {
                        // debug("getTargName()="+context.getTargName().toString());
                        return context.getSrcName().toString();
                    }
                } finally {
                    context.dispose();
                }
            } catch (Exception ex) {
                log.error("", ex);

            }
        }
        return authorization;
    }

    public String getUserName(String authorization) {
        if (authorization.startsWith("Negotiate ")) {
            String base64Str = authorization.substring("Negotiate ".length());
            if (base64Str.startsWith("TlRMTVNTUA")) {
                return null;
            }
            BASE64Decoder de = new BASE64Decoder();
            byte[] token;
            try {
                token = de.decodeBuffer(base64Str);
                return readKerberosUserName(token);
            } catch (Exception ex) {
                log.error("",ex);
                return null;
            }
        }
        return null;
    }

    private void setConfig() throws GSSException {
        System.setProperty("java.security.auth.login.config", "krb5Login.config");
        System.setProperty("java.security.krb5.realm", LDAPConfig.getInstance().getAdDomainName());
        System.setProperty("java.security.krb5.kdc", LDAPConfig.getInstance().getIp());
        log.debug("java.security.krb5.realm:" + LDAPConfig.getInstance().getAdDomainName());
        log.debug("java.security.krb5.kdc:" + LDAPConfig.getInstance().getPrincipal());
        log.debug(" principal:" + LDAPConfig.getInstance().getIp());
        if (log.isDebugEnabled()) {
            System.setProperty("sun.security.krb5.debug", "true");
        } else {
            System.setProperty("sun.security.krb5.debug", "false");
        }
        System.setProperty("sun.security.jgss.native", "true");
        System.setProperty("sun.security.spnego.msinterop", "true");
        if (log.isDebugEnabled()) {
            System.setProperty("sun.security.spnego.debug", "true");
        } else {
            System.setProperty("sun.security.spnego.debug", "false");
        }
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");

        File configFile = new File("krb5Login.config");
        PrintWriter writer;
        try {
            writer = new PrintWriter(configFile);
            try {
                writer.println("com.sun.security.jgss.initiate {");
                writer.println("        com.sun.security.auth.module.Krb5LoginModule required");
                writer.println("        principal=\"" + LDAPConfig.getInstance().getPrincipal() + "\" useKeyTab=true");
                writer.println("        keyTab=" + keyPsName + " storeKey=true;");
                writer.println("};");
                writer.println("com.sun.security.jgss.accept {");
                writer.println("        com.sun.security.auth.module.Krb5LoginModule required");
                writer.println("        principal=\"" + LDAPConfig.getInstance().getPrincipal() + "\" useKeyTab=true");
                writer.println("        keyTab=" + keyPsName + " storeKey=true;");
                writer.println("};");
                writer.println("logina6{");
                writer.println("        com.sun.security.auth.module.Krb5LoginModule required");
                writer.println("        principal=\"" + LDAPConfig.getInstance().getPrincipal() + "\" useKeyTab=true");
                writer.println("        keyTab=" + keyPsName + " storeKey=true;");
                writer.println("};");
                writer.flush();
            } finally {
                writer.close();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public String readKerberosUserName(byte[] ainfo) throws GSSException {

        GSSContext context = pool.getContext();
        try {
            byte[] atoken;
            atoken = context.acceptSecContext(ainfo, 0, ainfo.length);
            if (context.isEstablished()) {
                //                debug("getTargName()="+context.getTargName().toString());
                return StringUtils.substring(context.getSrcName().toString(), 0, context.getSrcName().toString()
                        .indexOf("@"));
            }
        } finally {
            context.dispose();
        }
        return null;
    }

}
