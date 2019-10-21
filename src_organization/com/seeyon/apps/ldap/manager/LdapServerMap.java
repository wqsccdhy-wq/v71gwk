package com.seeyon.apps.ldap.manager;

import java.util.HashMap;
import java.util.Map;

/**
 * ldap服务器map类，之后扩展在此进行
 * @author Yongzhang
 */
public class LdapServerMap {

    private static final String        SUN           = "sun";                         //默认
    private static final String        OPENLDAP      = "openLdap";
    private static final String        IBM           = "ibm";
    private static final String        SUN_NAME      = "Sun ONE Directory Server";
    private static final String        OPENLDAP_NAME = "openLdap";
    private static final String        IBM_NAME      = "IBM Directory Server";
    private static Map<String, String> ldapServerMap = new HashMap<String, String>();

    private LdapServerMap() {
    }

    public static Map<String, String> getMap() {
        if (ldapServerMap.isEmpty()) {
            ldapServerMap.put(SUN, LdapServerMap.SUN_NAME);
            ldapServerMap.put(OPENLDAP, LdapServerMap.OPENLDAP_NAME);
            ldapServerMap.put(IBM, LdapServerMap.IBM_NAME);
        }
        //    	Set<Entry<String, String>> ldapSet =ldapServerMap.entrySet();
        return ldapServerMap;
    }

    public static String getIBM() {
        return IBM;
    }

    public static String getOPENLDAP() {
        return OPENLDAP;
    }

    public static String getSUN() {
        return SUN;
    }
}
