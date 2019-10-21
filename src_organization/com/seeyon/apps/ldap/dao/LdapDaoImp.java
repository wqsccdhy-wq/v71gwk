package com.seeyon.apps.ldap.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.domain.EntryValueBean;

/**
 * LDAP数据底层实现类
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-11-6
 */
public class LdapDaoImp extends AbstractLdapDao {
    private final static Log    log               = LogFactory.getLog(LdapDaoImp.class);
    private static final String SEARCH_FILTER_OU  = "(objectClass=organizationalUnit)";

    private static final String SEARCH_FILTER_UID = "(|(objectClass=account)(objectClass=person))";

    public LdapDaoImp() {
        super();
    }

    public void modifyUserPassWord(String rdn, String oldPassWord, String newPassword) throws Exception {
        Attributes attrs = new BasicAttributes();
        DirContext ctx = null;
        try {
            putAttribute(attrs, "userPassword", newPassword);
            ctx = getContext();
            ctx.modifyAttributes(rdn, DirContext.REPLACE_ATTRIBUTE, attrs);
        } catch (Exception e) {
            this.log.error(LdapDaoImp.class.getName(), e);
            throw new Exception(e);
        } finally {
            closeCtx(ctx);
        }

    }

    public Attributes findUser(String uid) throws Exception {
        DirContext ctx = getContext();
        if (ctx == null) {
            throw new Exception("Context null");
        }
        return ctx.getAttributes("uid=" + uid);
    }

    public String getLoginName(String dn) throws Exception {
        try {
            if (dn.indexOf("uid") != -1) {
                String uidArrays[] = dn.split(",");

                if (uidArrays[0] != null && !"".equals(uidArrays[0])) {
                    String loginNameArrays[] = uidArrays[0].split("=");

                    if (loginNameArrays[1] != null && !"".equals(loginNameArrays[1])) {
                        return loginNameArrays[1];
                    }
                }

            } else {
            	if(!dn.toLowerCase().endsWith(LDAPConfig.getInstance().getBaseDn().toLowerCase())) {
            		dn = dn + "," + LDAPConfig.getInstance().getBaseDn();
            	}
            	
                DirContext ctx = getContext();
                Attributes attributes = null;
                try {
                    attributes = ctx.getAttributes(dn);
                    if (attributes == null) {
                        return null;
                    }
                    Attribute attr = attributes.get("uid");
                    String uid = (String) attr.get();
                    log.info("uid=" + uid);
                    return uid;
                } catch (Exception e) {
                    log.error("Exception", e);
                } finally {
                    closeCtx(ctx);
                }
            }
        } catch (Exception e) {
            log.error(LdapDaoImp.class.getName(), e);
        }
        return null;
    }

    private void putAttribute(Attributes attrs, String attrName, String attrValue) {
        if (attrValue != null && attrValue.length() != 0) {
            Attribute attr = new BasicAttribute(attrName, attrValue);
            attrs.put(attr);
        }
    }

    public List<EntryValueBean> ouTreeView(String baseDn, boolean isRoot) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        String rootId = getRootID(baseDn);
        String dn = "";
        if (!isRoot) {
            dn = baseDn + "," + LDAPConfig.getInstance().getBaseDn();

            if (dn.indexOf("/") != -1) {
                dn = "\"" + dn + "\"";
            }
        } else {
            dn = LDAPConfig.getInstance().getBaseDn();
        }
        DirContext ctx = getContext();
        if (ctx == null) {
            throw new Exception("Context null");
        }

        List<EntryValueBean> list = null;
        try {
            list = new ArrayList<EntryValueBean>();
            SearchControls search = new SearchControls();
            search.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration results = ctx.search(dn, SEARCH_FILTER_OU, search);

            while (results.hasMore()) {
                SearchResult si = (SearchResult) results.next();

                Attributes attrs = si.getAttributes();
                if (attrs == null) {
                    this.log.error(LdapDaoImp.class.getName() + " No attributes");
                    continue;
                }

                EntryValueBean bean = new EntryValueBean();
                String siName = si.getName();
                if (StringUtils.isBlank(siName)) {
                    bean.setParentId(null);
                    bean.setDnName(baseDn);
                    bean.setName(baseDn);
                } else {
                    siName = StringUtils.removeEnd(StringUtils.removeStart(siName, "\""), "\"");
                    bean.setDnName(createAllRDn(siName, baseDn));
                    bean.setParentId(createParentId(siName, rootId, map, bean.getDnName()));
                    bean.setName(createName(siName));
                }
                bean.setType("ou");
                for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {
                    Attribute attr = (Attribute) ae.next();
                    String id = attr.getID();

                    if ("ou".equals(id)) {
                        String key = getUUID();
                        bean.setId(key);
                        map.put(bean.getDnName(), key);
                    }
                }
                list.add(bean);
            }
            results.close();
        } catch (Exception e) {
        } finally {
            closeCtx(ctx);
        }
        return list;
    }

    public void userTreeView(String baseDn, List<EntryValueBean> list) throws Exception {
        Map<String, String> parentMap = new HashMap<String, String>();
        for (EntryValueBean entryValueBean : list) {
            parentMap.put(entryValueBean.getDnName(), entryValueBean.getId());
        }
        String dn = "";
        if (!"".equals(baseDn)) {
            dn = baseDn + "," + LDAPConfig.getInstance().getBaseDn();
        } else {
            dn = LDAPConfig.getInstance().getBaseDn();
        }
        DirContext ctx = getContext();
        if (ctx == null) {
            throw new Exception("Context null");
        }
        try {
            SearchControls search = new SearchControls();
            search.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String attrList[] = { "uid" };
            search.setReturningAttributes(attrList);
            NamingEnumeration results = ctx.search(dn, SEARCH_FILTER_UID, search);
            while (results.hasMore()) {
                SearchResult si = (SearchResult) results.next();

                Attributes attrs = si.getAttributes();
                if (attrs == null) {
                    this.log.error(LdapDaoImp.class.getName() + " No attributes");
                    continue;
                }
                EntryValueBean bean = new EntryValueBean();

                bean.setDnName(createAllRDn(si.getName(), baseDn));
                bean.setParentId(createUidParentId(si.getName(), baseDn, parentMap, bean.getDnName()));
                bean.setType("user");
                bean.setName(createName(si.getName()));
                NamingEnumeration ae = attrs.getAll();
                while (ae.hasMoreElements()) {
                    Attribute attr = (Attribute) ae.next();

                    String id = attr.getID();
                    //			        Enumeration vals = attr.getAll();
                    //			        while (vals.hasMoreElements())
                    //			        {
                    if ("uid".equals(id)) {
                        //			            	String value = (String) vals.nextElement();
                        String key = getUUID();
                        bean.setId(key);
                        //			            	map.put(bean.getDnName(), key+value);
                    }

                    //			        }

                }
                ae.close();
                list.add(bean);
            }
            results.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeCtx(ctx);
        }
    }

    // 得到属性
    public String[] getuserAttribute(String uid) throws Exception {
        String[] userAttributes = new String[3];
        DirContext ctx = getContext();
        Attributes attributes = null;
        try {
            String dn = uid + "," + LDAPConfig.getInstance().getBaseDn();
            attributes = ctx.getAttributes(dn);
            if (attributes == null) {
                return null;
            }
            Attribute attr = attributes.get("uid");
            userAttributes[0] = (String) attr.get();
            Attribute attr1 = attributes.get("cn");
            userAttributes[1] = (String) attr1.get();
            Attribute attr2 = attributes.get("telephoneNumber");
            userAttributes[2] = (String) attr2.get();
            // Attribute attr3 = attributes.get("userPassword");
            // userAttributes[3] = new String((byte[]) attr3.get());

            log.info("uid=" + userAttributes[0] + ",cn=" + userAttributes[1] + ",telephoneNumber=" + userAttributes[2]);
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            closeCtx(ctx);
        }
        return userAttributes;
    }

    // private String getRootRdn(String baseDn)
    // {
    // String root = baseDn;
    //
    // root = baseDn.replaceAll("," + LDAPConfig.getInstance().getBaseDn(), "");
    //
    // if (!root.equals(baseDn))
    // {
    // // return LDAPConfig.getInstance().getBaseDn();
    // return null;
    // }
    // return root;
    // }
    private String createAllRDn(String dn, String baseDn) {
        if ("".equals(baseDn)) {
            return dn + baseDn;
        }
        //    	if(baseDn.equalsIgnoreCase(LDAPConfig.getInstance().getBaseDn()))
        //    	{
        //    		return "";
        //    	}
        return dn + "," + baseDn;
    }

    private String createParentId(String dn, String rootId, Map<String, String> map, String keyDn) {
        if (dn == null) {
            return null;
        }
        String[] array = keyDn.split(",");
        if (array.length == 1) {
            return rootId;
        }
        if (array[1].indexOf("ou") != -1) {

            //            String[] array1 = array[1].split("=");
            //            return array1[1];
            //        	String[] array1 = keyDn.split(",");
            if (array[1] != null) {
                //        		this.log.error(LdapDaoImp.class.getName()+" "+array1[1]);
                return map.get(keyDn.substring(array[0].length() + 1));
            }
            return map.get(keyDn);
        }
        return null;
    }

    private String createName(String rdn) {
    	String[] s = split(rdn);
    	if(s != null && s.length>0){
    		return s[0];
    	}
    	return null;
    }

    private String createUidParentId(String dn, String rootId, Map<String, String> map, String keyDn) {
        if (dn == null) {
            return null;
        }
        String[] array = dn.split(",");
        if (array.length == 1) {
            //            return rootId;
            return map.get(rootId);
        }
        if (array[1].indexOf("ou") != -1) {
            //            String[] array1 = array[1].split("=");
            //            return array1[1];
            String[] array1 = keyDn.split(",");
            if (array1[1] != null) {
                //        		this.log.error(LdapDaoImp.class.getName()+" "+array1[1]);
                return map.get(keyDn.substring(array1[0].length() + 1));
            }
            return map.get(keyDn);
        }
        return null;
    }

    private static String getRootID(String baseDn) {
        if ("".equals(baseDn) || null == baseDn) {
            return null;
        }

        String[] array = baseDn.split(",");
        if (array != null) {
            String[] array1 = array[0].split("=");
            return array1[1];
        }
        return baseDn;
    }
    private static String[] split( String str ){
        if( str == null || "".equals( str ) ){
            return null;
        }
        
        return str.split( "(?<!\\\\)," );
    }
    public boolean createNode(String dn, String[] parameter) throws Exception {
        // 新增一个节点,忽略大小写
        Attributes attrs = new BasicAttributes(true);

        Attribute objclass = new BasicAttribute("objectclass");

        objclass.add("inetorgperson");
        objclass.add("organizationalPerson");
        objclass.add("person");
        //        objclass.add("uidObject");
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
            throw e;
        } finally {
            closeCtx(ctx);
        }
        return true;
    }
    
	@Override
	public List<EntryValueBean> getSubNode(String parentDn, String parentId, String type) throws Exception {
		return null;
	}

	@Override
	public List<EntryValueBean> searchCn(String baseDn, String key)
			throws Exception {
		return null;
	}

	@Override
	public List<EntryValueBean> getOrgSubNode(String parentDn, String parentId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
