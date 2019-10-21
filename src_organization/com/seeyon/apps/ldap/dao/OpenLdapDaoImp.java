package com.seeyon.apps.ldap.dao;

import javax.naming.directory.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author YongZhang
 * @version 2009-08-10
 */
public class OpenLdapDaoImp extends LdapDaoImp {
    
    private static Log log = LogFactory.getLog(OpenLdapDaoImp.class);

    public OpenLdapDaoImp() {
        super();
    }

    public boolean createNode(String dn, String[] parameter) throws Exception {
        // 新增一个节点,忽略大小写
        Attributes attrs = new BasicAttributes(true);

        Attribute objclass = new BasicAttribute("objectclass");

        objclass.add("organizationalPerson");
        objclass.add("person");
        objclass.add("uidObject");
        attrs.put(objclass);

        Attribute uid = new BasicAttribute("uid");
        uid.add(parameter[0]);
        attrs.put(uid);

        Attribute cn = new BasicAttribute("cn");
        cn.add(parameter[1]);
        attrs.put(cn);

        Attribute sn = new BasicAttribute("sn");
        sn.add(parameter[1]);
        attrs.put(sn);

        Attribute userPassword = new BasicAttribute("userPassword");
        userPassword.add(parameter[2]);
        attrs.put(userPassword);
        DirContext ctx = getContext();
        if (ctx == null) {
            throw new Exception("Context null");
        }
        try {
            ctx.createSubcontext(dn, attrs);
        } catch (Exception e) {
            this.log.error("创建用户失败：", e);
            return false;
        } finally {
            closeCtx(ctx);
        }
        return true;
    }
}
