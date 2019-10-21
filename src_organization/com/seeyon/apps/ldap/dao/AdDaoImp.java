package com.seeyon.apps.ldap.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.config.LDAPProperties;
import com.seeyon.apps.ldap.domain.EntryValueBean;
import com.seeyon.ctp.util.ServerDetector;
import com.seeyon.ctp.util.Strings;

/**
 * AD数据底层实现类
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-11-6 & 2008-12-5
 */
public class AdDaoImp extends AbstractLdapDao {
    
    private static Log log = LogFactory.getLog(AdDaoImp.class);

    public AdDaoImp() {
        super();
    }

    protected static final String KEYPATH   = "/lib/security/cacerts";

    protected static final String JAVA_HOME = System.getProperty("java.home");              // 支持tomcat的java环境

    protected static final String KEYSTORE  = (JAVA_HOME + KEYPATH).replaceAll("\\\\", "/"); // 存放证书

    private DirContext getSSLContext(String name, String passWord) throws NamingException {
        String userName = null;
        String loginPassWord = null;
        String tmp = catchLDAPConfig().getAuthenication();
        String url = LDAPConfig.getInstance().getAdSslUrl();
        if (url == null)
            throw new NamingException("no ldap host");
        if (tmp == null)
            tmp = "simple";

        if (StringUtils.isNotBlank(name)) {
            if (!this.canEmptyPassword) {
                // 禁止空密码
                if (passWord == null || "".equals(passWord))
                    return null;
            }
            userName = name;
            loginPassWord = passWord;
        } else {
            userName = LDAPConfig.getInstance().getAdmin();
            loginPassWord = LDAPConfig.getInstance().getPassWord();
        }
        Hashtable<Object, Object> env = new Hashtable<Object, Object>();
        DirContext ctx = null;
        if(ServerDetector.isWebSphere()){
        	env.put("javax.net.ssl.trustStore",KEYSTORE);
        }else{
        	System.setProperty("javax.net.ssl.trustStore", KEYSTORE);
        }
        env.put(Context.INITIAL_CONTEXT_FACTORY, SUN_JNDI_PROVIDER);
        env.put(Context.SECURITY_AUTHENTICATION, tmp);
        env.put(Context.SECURITY_PRINCIPAL, userName);
        env.put(Context.SECURITY_CREDENTIALS, loginPassWord);
        env.put(Context.SECURITY_PROTOCOL, "ssl");
        env.put(Context.PROVIDER_URL, LDAPConfig.getInstance().getAdSslUrl());
        env.put(Context.REFERRAL, "follow"); 

        log.info(KEYSTORE);
        try {
        	ctx = new InitialLdapContext(env,null);
        } catch (NamingException e) {
        	//如果是不能在指定工作站登录，会报如下错误：
        	//"LDAP: error code 49 - 80090308: LdapErr: DSID-0C0903A9, comment: AcceptSecurityContext error, data 531, v1db1".
        	//data 531  只会在用户名密码输入正确的情况下报出来。
        	
        	//无法通过配置通过这个验证，导致无法登录。忽略这种错误，直接允许登录。
        	String mesage = e.getMessage();
        	if(StringUtils.isNotBlank(name) && mesage.toLowerCase().replace(" ", "").indexOf("data531")>0){
        		return getSSLContext(null,null);
        	}else{
        		log.info("AD 连接异常", e);
        	}
        }
        return ctx;
    }

    public void modifyUserPassWord(String rdn, String oldPassWord, String newPassword) throws Exception {
        DirContext ctx = null;
        try {
            //ctx = getSSLContext(null, null);
            ctx = creatContext(null, null);
            if(ctx == null){
            	log.info("获取ldap连接失败！ ");
            	return;
            }
            ModificationItem[] mods = new ModificationItem[1];
            String newQuotedPassword = "\"" + newPassword + "\"";
            byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd",
                    newUnicodePassword));
            rdn="\""+rdn+"\"";
            ctx.modifyAttributes(rdn, mods);
            log.info("AD set Password for: " + rdn);
        } catch (Exception e) {
            log.error(AdDaoImp.class.getName() + " " + "AD Reset Password error: ", e);
            throw new Exception(e);
        } finally {
            try {
                if (ctx != null) {
                    ctx.close();
                }
            } catch (NamingException e) {
                log.error(AdDaoImp.class.getName() + " ", e);
            }
        }
    }

    public Attributes findUser(String uid) throws Exception {
        return null;
    }

    public String getLoginName(String dn) throws Exception {
        DirContext ctx = null;

        String loginName = "";
        SearchControls search = new SearchControls();
        search.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String attrList[] = { "sAMAccountName" };
        search.setReturningAttributes(attrList);
        try {
        	ctx = creatNormalContext(null, null);
        	dn = escapeDn(dn);
            NamingEnumeration results = ctx.search(dn, "(objectClass=user)", search);
            if (results == null) {
                return loginName;
            }
            while (results.hasMore()) {
                SearchResult si = (SearchResult) results.next();
                Attributes attrs = si.getAttributes();// 从结果中取出此条目
                if (attrs == null) {
                    log.info("条目：" + dn + " objectclass非user类型不进行储存");
                    return "";
                }
                NamingEnumeration ae = attrs.getAll();// 取得此条目的所有属性
                if (ae == null) {
                    return loginName;
                }
                while (ae.hasMoreElements()) {
                    Attribute attr = (Attribute) ae.next();
                    // String id = attr.getID();
                    Enumeration vals = attr.getAll();
                    while (vals.hasMoreElements()) {
                        loginName = (String) vals.nextElement();
                    }

                }
                ae.close();
            }
			if(results!=null){
				results.close();
			};
        } catch (Exception e) {
            log.error(AdDaoImp.class.getName() + " ", e);
        } finally {
            closeCtx(ctx);
        }
        return loginName;
    }

    public void userTreeView(String baseDn, List<EntryValueBean> list) throws Exception {
        int pageSize=1000;
        DirContext dctx = null;
        LdapContext ctx = null;
        try {
        	dctx = creatNormalContext(null, null);
        	ctx = (LdapContext) dctx;
        	if (ctx == null) {
        		throw new Exception("Context null");
        	}
        	ctx.setRequestControls(new Control[] { new PagedResultsControl(
        			pageSize, Control.CRITICAL) });
        	
        	ctxSearch(baseDn, "-1", pageSize, ctx, list);
		} catch (Exception e) {
		}finally{
			this.closeCtx(dctx);
			this.closeCtx(ctx);
		}

    }
    
    /**
     * 查询并封装人员树状列表
     * @param parentDn
     * @param parentId
     * @param pageSize
     * @param ctx2
     * @param list
     * @throws NamingException
     * @throws IOException
     */
	private void ctxSearch(String parentDn, String parentId, int pageSize, LdapContext ctx2, List<EntryValueBean> list)
			throws NamingException, IOException {
		byte[] cookie;
		SearchControls search = new SearchControls();
        search.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        String attrList[] = { "cn","ou","dc","sAMAccountName", "distinguishedName","objectClass"};// memberOf,member,distinguishedName,lockoutTime>1
        search.setReturningAttributes(attrList);
        //不限制条数
        search.setCountLimit(0);
        String searchFilter = "(|(objectClass=container)(objectClass=user)(objectCategory=organizationalUnit)(objectClass=domainDNS))";
        ctx2.setRequestControls(new Control[] { new PagedResultsControl(pageSize, Control.CRITICAL) });
        do {
        	parentDn = escapeDn(parentDn);
        	NamingEnumeration results = null;
        	try {
        		 results = ctx2.search(parentDn, searchFilter, search);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
        	while (results != null && results.hasMoreElements()) {
    		    SearchResult si = (SearchResult) results.next();
    		    Attributes attrs = si.getAttributes();
    		    EntryValueBean bean = new EntryValueBean();
    		    bean.setId(getUUID());
    		    if(null != attrs.get("objectClass") && attrs.get("objectClass").contains("domainDNS")) {//子域
    				bean.setType("domainDNS");//子域
    				String dnName = (String)attrs.get("distinguishedName").get(0);
    				String name = si.getName();
    				name = name.replaceAll("ldap://", "");//去掉ldap开头
    				name = name.replaceAll("/" + dnName, "");//去掉dn结尾
    				String dc = (String)attrs.get("dc").get(0);
    				name = "DC=" + dc + "[" + name + "]";
    				bean.setDnName(dnName);
    				bean.setName(name);
    		    }else if(null != attrs.get("objectClass") && attrs.get("objectClass").contains("organizationalUnit")){//OU
    		    	bean.setType("ou");
    				bean.setDnName((String)attrs.get("distinguishedName").get(0));
    				bean.setName("OU=" + (String)attrs.get("OU").get(0));
    			}else if(null != attrs.get("objectClass") && attrs.get("objectClass").contains("user")){//user
    		    	bean.setType("user");
    		    	bean.setDnName(((String)attrs.get("distinguishedName").get(0)).replace("\\,", ",").replace("\\\\", "\\"));
    				bean.setName("CN=" + (String)attrs.get("CN").get(0));
    				bean.setParentId(parentId);
    		    }else if(null != attrs.get("objectClass") && attrs.get("objectClass").contains("container")){//user
    		    	bean.setType("container");
    		    	bean.setDnName((String)attrs.get("distinguishedName").get(0));
    				bean.setName("CN=" + (String)attrs.get("CN").get(0));
    		    }
    		    list.add(bean);
    		}
        	cookie = parseControls(ctx2.getResponseControls());
			ctx2.setRequestControls(new Control[] { new PagedResultsControl(
					pageSize, cookie, Control.CRITICAL) });
			if(results!=null){
				results.close();
			}
        } while ((cookie != null) && (cookie.length != 0));
    	
    	Collections.sort(list, new Comparator<EntryValueBean>() {
    		public int compare(EntryValueBean o1, EntryValueBean o2) {
    			return o1.getDnName().compareToIgnoreCase(o2.getDnName());
    		}
    	});
	}
    
	/**
	 * 特殊字符转义
	 * @param parentDn
	 * @return
	 */
	private String escapeDn(String parentDn) {
		String baseDn = LDAPConfig.getInstance().getBaseDn();
		if(Strings.isBlank(parentDn)) {
			return baseDn;
		}
    	if(!parentDn.toLowerCase().endsWith(baseDn.toLowerCase())) {
    		parentDn = parentDn + "," + baseDn;
    	}
		return escapeDn0(parentDn.replace("\\", "\\\\\\\\").replace("/", "\\/").replace("+", "\\+").replace(";", "\\;"));
	}
	
    private String escapeDn0(String dn) {
	  dn=dn.replace("\u00A0", " ");
	  String result = "";
	  String[] mapperArr = dn.split(",");
	  for(String mp :mapperArr){
		  if(mp.indexOf("=")>0){
			  result = result + "," + mp;
		  }else{
			  result = result + "\\\\," + mp;
		  }
	  }
	 
	  if(Strings.isNotBlank(result)){
		  result = result.substring(1);
	  }else{
		  result = dn;
	  }
	 
	  return result;
    }
	
	private byte[] parseControls(Control[] controls)
			throws NamingException {
		byte[] cookie = null;
		if (controls != null) {
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] instanceof PagedResultsResponseControl) {
					PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
					cookie = prrc.getCookie();
//					System.out.println(">>Next Page \n");
				}
			}
		}
		return (cookie == null) ? new byte[0] : cookie;
	}

    public String[] getuserAttribute(String uid) throws Exception {
        String[] userAttributes = new String[4];

        Attributes attributes = null;
        DirContext ctx = null;
        try {
        	ctx = creatNormalContext(null, null);
            if(ctx == null){
            	log.info("获取ldap连接失败！ ");
            	return null;
            }
        	uid = uid.toLowerCase();
        	String baseDn = LDAPConfig.getInstance().getBaseDn();
        	String dn = uid;
        	if(!uid.endsWith(baseDn)) {
        		dn = uid + "," + baseDn;
        		dn="\""+dn+"\"";
        	}
        	dn = escapeDn(dn);
            attributes = ctx.getAttributes(dn);
            if (attributes == null) {
                return null;
            }
            Attribute attr = attributes.get("sAMAccountName");
            userAttributes[0] = (String) attr.get();
            Attribute attr1 = attributes.get("displayName");
            userAttributes[1] = (String) attr1.get();
            Attribute attr2 = attributes.get("telephoneNumber");
            String telephoneNumber = "";
            if(attr2!=null){
            	telephoneNumber = (String) attr2.get();
            }
            userAttributes[2] = telephoneNumber;
        } catch (Exception e) {
           log.error("",e);
        } finally {
            closeCtx(ctx);
        }
        return userAttributes;
    }

    public List<EntryValueBean> ouTreeView(String baseDn, boolean isRoot) throws Exception {
    	log.info("----------ouTreeView start------------------");
        Map<String, String> map = new HashMap<String, String>();
        String dn = "";
        if (!isRoot) {

            dn = baseDn + "," + LDAPConfig.getInstance().getBaseDn();
            if (dn.indexOf("/") != -1) {
                dn = "\"" + dn + "\"";
            }
        } else {
            dn = LDAPConfig.getInstance().getBaseDn();
        }
        dn=dn.replace("\u00A0", " ");
        log.info("dn: "+dn);
        DirContext ctx = null;
        List<EntryValueBean> list = new ArrayList<EntryValueBean>();
        try {
        	ctx = creatNormalContext(null, null);
            if (ctx == null) {
                log.error("获取连接失败！");
                return null;
            }
        	SearchControls search = new SearchControls();
            search.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            String attrList[] = { "ou", "distinguishedName" ,"dc","objectClass"};
            search.setReturningAttributes(attrList);
            dn = escapeDn(dn);
            NamingEnumeration results = ctx.search(dn, "(|(objectCategory=organizationalUnit)(objectClass=domainDNS))", search);
            while (results.hasMoreElements()) {
                SearchResult si = (SearchResult) results.next();

                Attributes attrs = si.getAttributes();
                if (attrs == null) {
                    continue;
                }

                EntryValueBean bean = new EntryValueBean();
                String siName = si.getName();

                if (siName == null || "".equals(siName)) {
                    bean.setDnName(baseDn);
                    bean.setName(baseDn);
                } else {
                	if(null != attrs.get("objectClass") && attrs.get("objectClass").contains("domainDNS")) {//子域
            			bean.setId(getUUID());
                		bean.setType("domainDNS");//子域
                		String dnName = (String)attrs.get("distinguishedName").get(0);
                		siName = siName.replaceAll("ldap://", "");//去掉ldap开头
                		siName = siName.replaceAll("/" + dnName, "");//去掉dn结尾
                		String dc = (String)attrs.get("dc").get(0);
                		siName = "DC=" + dc + "[" + siName + "]";
                		bean.setDnName(dnName);
                		bean.setName(siName);
                	}else {//OU
                		siName = StringUtils.removeEnd(StringUtils.removeStart(siName, "\""), "\"");
                		bean.setSiName(siName);
                		bean.setDnName(createFullRDn(siName, baseDn));
                		bean.setName(createName(siName));
                		bean.setType("ou");
                		if(null != attrs.get("ou") || null != attrs.get("OU")){
                			String key = getUUID();
                			bean.setId(key);
                			map.put(bean.getDnName(), key);
                		}
                	}
                }
                list.add(bean);
            }
			if(results!=null){
				results.close();
			};
        } catch (Exception e) {
            log.error(this.getClass().getName() + e.getMessage());
        } finally {
            closeCtx(ctx);
        }
        log.info("--------------重新组装上下节点关系 ----------------------");
        List<EntryValueBean> resultList = new ArrayList<EntryValueBean>();
        for(EntryValueBean eb : list){
        	String  siName = eb.getSiName();
        	if(Strings.isBlank(eb.getSiName())){
        		eb.setParentId(null);
        	}else{
        		if(Strings.isBlank(eb.getParentId())) {
        			eb.setParentId(createParentId(siName, getRootID(baseDn), map, eb.getDnName()));
        		}
        	}
        	
        	resultList.add(eb);
        	
        }
        log.info("--------------组装上下节点关系  end----------------------");
        log.info("----------ouTreeView end------------------");
        log.info("resultList.size: "+resultList.size());
        return resultList;

    }
    
    
    @Override
    public List<EntryValueBean> searchCn(String baseDn,String userName) throws Exception{
    	String dn = baseDn;
    	
        if (dn.indexOf("/") != -1) {
            dn = "\"" + dn + "\"";
        }
        dn=dn.replace("\u00A0", " ");

        SearchControls search = new SearchControls();
        search.setSearchScope(SearchControls.SUBTREE_SCOPE);
      //  String searchFilter = "sAMAccountName=" + userName; 
        String searchFilter = "(|(sAMAccountName="+ userName+")(cn="+ userName+"))";
        
        //再查找查找直接下级的cn
        List<EntryValueBean> cnList = new ArrayList<EntryValueBean>();
        int pageSize=1000;
        byte[] cookie = null;
        DirContext dctx = null;
        LdapContext ctx2 = null;
        try {
        	dctx = creatNormalContext(null, null);
        	ctx2 = (LdapContext) dctx;
        	if (ctx2 == null) {
        		log.error("获取连接失败！");
        		return null;
        	}
        	ctx2.setRequestControls(new Control[] { new PagedResultsControl(pageSize, Control.CRITICAL) });
            String attrList[] = { "cn", "sAMAccountName", "distinguishedName" }; 
            search.setReturningAttributes(attrList);
            //不限制条数
            search.setCountLimit(0);
            do {
            	dn = escapeDn(dn);
            	NamingEnumeration results = ctx2.search(dn, searchFilter, search);
	            while (results != null && results.hasMoreElements()) {
	                SearchResult si = (SearchResult) results.next();
	                Attributes attrs = si.getAttributes();
	                String name = si.getName();
	                if (attrs == null || name.indexOf("cn=Users") != -1 || name.indexOf("CN=Users") != -1 || name.indexOf("CNF:") != -1 || name.indexOf("cnf:") != -1) {
	                    continue;
	                }
	                EntryValueBean bean = new EntryValueBean();
	                bean.setType("user");
    		    	bean.setDnName((String)attrs.get("distinguishedName").get(0));
    				bean.setName("CN=" + (String)attrs.get("CN").get(0));
	                cnList.add(bean);
	            }      
	            
				cookie = parseControls(ctx2.getResponseControls());
				ctx2.setRequestControls(new Control[] { new PagedResultsControl(
						pageSize, cookie, Control.CRITICAL) });
				if(results!=null){
					results.close();
				};
	        } while ((cookie != null) && (cookie.length != 0));
        } catch (Exception e) {
            log.error(AdDaoImp.class.getName() + " ", e);
        } finally {
        	closeCtx(dctx);
            closeCtx(ctx2);
        }
        
		Collections.sort(cnList, new Comparator<EntryValueBean>() {
			public int compare(EntryValueBean o1, EntryValueBean o2) {
                  return o1.getDnName().compareToIgnoreCase(o2.getDnName());
			}
		});
		
		return cnList;
    }
    

    
    /**
     * 获取父节点下的直接子节点（包含ou和cn）
     * 本节点：SearchControls.OBJECT_SCOPE  
     * 单层，直接子节点：SearchControls.ONELEVEL_SCOPE 
     * 遍历，全部子节点 SearchControls.SUBTREE_SCOPE
     * @param parentDn 父节点
     * @return 
     * @throws Exception
     */
    @Override
    public List<EntryValueBean> getSubNode(String parentDn,String parentId, String type) throws Exception{
        //如果没有传父节点，则放在根节点下
        if(Strings.isBlank(parentId)){
        	parentId = String.valueOf(-1);
        }
        int pageSize=1000;
    	DirContext dctx = null;
    	LdapContext ctx2 = null;
    	List<EntryValueBean> list = new ArrayList<EntryValueBean>();
    	try {
    		dctx = creatNormalContext(null, null);
        	ctx2 = (LdapContext) dctx;
        	if (ctx2 == null) {
        		log.error("获取连接失败！");
        		return null;
        	}
        	ctx2.setRequestControls(new Control[] { new PagedResultsControl(pageSize, Control.CRITICAL) });
        	ctxSearch(parentDn, parentId, pageSize, ctx2, list);
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			this.closeCtx(dctx);
			this.closeCtx(ctx2);
		}
        return list;
    }

    private String createFullRDn(String dn, String baseDn) {
        if ("".equals(baseDn)) {
            return dn + baseDn;
        }
        return dn + "," + baseDn;
    }

    public boolean auth(String username, String password) {
        DirContext dct = null;
        boolean ok = false;
        try {
          username = username.replace("\\", "\\\\").replace("+", "\\+").replace(";", "\\;");
      	  String result = "";
    	  String[] mapperArr = username.split(",");
    	  for(String mp :mapperArr){
    		  if(mp.indexOf("=")>0){
    			  result = result + "," + mp;
    		  }else{
    			  result = result + "\\," + mp;
    		  }
    	  }
    	 
    	  if(Strings.isNotBlank(result)){
    		  result = result.substring(1);
    	  }else{
    		  result = username;
    	  }
    	  
          dct = creatNormalContext(result, password);

            if (dct != null) {
                ok = true;
            }
        } catch (Exception e) {
        	//如果是不能在指定工作站登录，会报如下错误：
        	//"LDAP: error code 49 - 80090308: LdapErr: DSID-0C0903A9, comment: AcceptSecurityContext error, data 531, v1db1".
        	//data 531  只会在用户名密码输入正确的情况下报出来。
        	
        	//无法通过配置通过这个验证，导致无法登录。忽略这种错误，直接允许登录。
        	String mesage = e.getMessage();
        	if(mesage.toLowerCase().replace(" ", "").indexOf("data531")>0){
        		ok = true;
        	}else{
        		log.error("com.seeyon.v3x.common.ldap.dao.AdDaoImp", e);
        	}
        }
        try {
            if (dct != null)
                dct.close();
        } catch (Exception ee) {
            log.error("com.seeyon.v3x.common.ldap.dao.AdDaoImp", ee);
        }
        if(!ok){
        	log.info("ad账号验证失败："+ username);
        }
        return ok;
    }

    //ad属性对照表 https://wenku.baidu.com/view/f478c2cb650e52ea5418980b.html
    public boolean createNode(String dn, String[] parameter) throws Exception {
        DirContext ctx = null;
        ctx = getSSLContext(null, null);
        if (ctx == null) {
            log.error("获取连接失败！");
            return false;
        }
        try {
            String loginName = parameter[0];
            String userName = parameter[1];
            Attributes attrs = new BasicAttributes(true);//不区分属性名称大小写

            attrs.put("objectClass", "user");
            Attribute sAMAccountName = new BasicAttribute("sAMAccountName", loginName);
            attrs.put(sAMAccountName);
            
            Attribute cn = new BasicAttribute("cn", loginName);//不能重复在AD上创建
            attrs.put(cn);

            //可选择属性
            Attribute sn = new BasicAttribute("sn", userName);//Last Name
            attrs.put(sn);
            
            Attribute givenName = new BasicAttribute("givenName", userName);//First Name
            attrs.put(givenName);
            
            String ldap_admin = LDAPProperties.getInstance().getProperty(LDAPProperties.LDAP_ADMIN);
            if(Strings.isNotBlank(ldap_admin)){
                if(ldap_admin.indexOf("@")>0){
                    String domainName = ldap_admin.substring(ldap_admin.indexOf("@")+1);
                   // attrs.put("userPrincipalName",userPrincipalName);
                    Attribute userPrincipalName = new BasicAttribute("userPrincipalName", loginName+"@"+domainName);
                    attrs.put(userPrincipalName);
                }
            }
            Attribute displayName = new BasicAttribute("displayName", userName);
            attrs.put(displayName);
            
            Attribute description = new BasicAttribute("description", userName);
            attrs.put(description);
            
            int UF_PASSWD_NOTREQD = 0x0020;
            int UF_NORMAL_ACCOUNT = 0x0200;
            int UF_DONT_EXPIRE_PASSWD = 0x10000;
            int UF_PASSWORD_EXPIRED = 0x800000;

            attrs.put("userAccountControl",
                    Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD + UF_DONT_EXPIRE_PASSWD));
            Context result = ctx.createSubcontext(dn, attrs);
            result.close();
            ModificationItem[] mods = new ModificationItem[2];

            String newQuotedPassword = "\"" + parameter[2] + "\"";

            byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd",
                    newUnicodePassword));
            mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountControl",
					Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWORD_EXPIRED)));
            ctx.modifyAttributes(dn, mods);
            log.info("创建AD帐号: " + userName);
            return true;
        } catch (Exception e) {
            log.error(this.getClass().getName(), e);
            return false;
        } finally {
            closeCtx(ctx);
        }
    }

    private String createParentId(String dn, String rootId, Map<String, String> map, String dnName) {
        if (dn == null) {
            return null;
        }

        String[] array = split(dnName);
        if (array == null) {
            return null;
        }

        if (array.length == 1) {
            return rootId;
        }
        if (array[1].indexOf("ou") != -1 || array[1].indexOf("OU") != -1) {
            /*
            String[] array1 = dnName.split(",");
            if(array1[1]!=null)
            {
                return map.get(dnName.substring(array1[0].length()+1));
            }
            return map.get(dnName);
            */
            String parentDnName = dnName.substring(array[0].length() + 1);
            String parentId = map.get(parentDnName);
            return parentId;
        }
        return null;
    }

    private static String createName(String rdn) {
    	String[] s = split(rdn);
    	if(s != null && s.length>0){
    		return s[0];
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

    private static String[] split(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }

        return str.split("(?<!\\\\),");
    }
    /**
     * 获取对应的连接:
     *               支持SSL,获得SSL连接
     *               不支持SSL,获得普通AD连接
     *               
     */
    private DirContext creatContext(String name, String passWord) throws Exception {
        DirContext context = null;
        if (LDAPConfig.getInstance().getType().indexOf("ad") > -1 && LDAPConfig.getInstance().getIsEnableSSL()) {

            context = getSSLContext(name, passWord);

        } else if (StringUtils.isNotBlank(name)) {
            context = createInitialContext(name, passWord);
        } else {
            context = getContext();
        }
        return context;
    }
    
    /**
     * 只获取普通连接，用于只校验用户有效性
     *               
     */
    private DirContext creatNormalContext(String name, String passWord) throws Exception {
        DirContext context = null;
	    if(StringUtils.isBlank(name) && StringUtils.isBlank(passWord)){
	    	name = LDAPConfig.getInstance().getAdmin();
	    	passWord = LDAPConfig.getInstance().getPassWord();
	    }
	    
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(passWord)) {
        	context = createNormalInitialContext(name, passWord);
        }
        return context;
    }

    /**
     * LDAP/AD用户账号是否存在
     * @param dn 条目
     * @return boolean true or false
     */
    public boolean isUserExist(String dn) throws Exception {
        DirContext ctx = null;
        try {
            ctx = creatNormalContext(null, null);
            if (ctx == null) {
                throw new Exception("Context null");
            }
          /*  dn = dn.toLowerCase();
        	String baseDn = LDAPConfig.getInstance().getBaseDn();
        	if(!dn.endsWith(baseDn)) {
        		dn = dn + "," + baseDn;
        	}*/
        	dn = escapeDn(dn);
            Attributes attrs = ctx.getAttributes(dn);
            if (attrs != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("",e);
            return false;
        } finally {
            closeCtx(ctx);
        }
    }

    @Override
	public List<EntryValueBean> getOrgSubNode(String parentDn, String parentId) throws Exception{
        //如果没有传父节点，则放在根节点下
        if(Strings.isBlank(parentId)){
        	parentId = String.valueOf(-1);
        }
        DirContext ctx = creatNormalContext(null, null);
        if (ctx == null) {
            throw new Exception("Context null");
        }
        
        List<EntryValueBean> list = new ArrayList<EntryValueBean>();
        try {
            SearchControls search = new SearchControls();
            search.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            String attrList[] = { "ou", "distinguishedName" ,"dc","objectClass"};
            search.setReturningAttributes(attrList);
            //(objectClass=organizationalUnit,objectClass=domain)
            parentDn = escapeDn(parentDn);
            NamingEnumeration results = ctx.search(parentDn, "(|(objectCategory=organizationalUnit)(objectClass=domainDNS))", search);
            Map<String,EntryValueBean> sonPathMap = new HashMap<String, EntryValueBean>();
            while (results.hasMoreElements()) {
                SearchResult si = (SearchResult) results.next();

                Attributes attrs = si.getAttributes();
                if (attrs == null) {
                    continue;
                }
                
                EntryValueBean bean = new EntryValueBean();
                bean.setId(getUUID());
                String siName = si.getName();
                if(null != attrs.get("objectClass") && attrs.get("objectClass").contains("domainDNS")) {//子域
                	bean.setType("domainDNS");//子域
                	String dnName = (String)attrs.get("distinguishedName").get(0);
                	siName = siName.replaceAll("ldap://", "");//去掉ldap开头
                	siName = siName.replaceAll("/" + dnName, "");//去掉dn结尾
                	String dc = (String)attrs.get("dc").get(0);
                	siName = "DC=" + dc + "[" + siName + "]";
                	bean.setDnName(dnName);
                	bean.setName(siName);
                }else {//OU
                	bean.setName("OU=" + (String)attrs.get("ou").get(0));
                	bean.setDnName((String)attrs.get("distinguishedName").get(0));
                	bean.setType("ou");
                }
                bean.setParentId(parentId);
                list.add(bean);
            }
			if(results!=null){
				results.close();
			};
        } catch (Exception e) {
            log.error(this.getClass().getName() + e.getMessage());
        } finally {
            closeCtx(ctx);
        }
        
		Collections.sort(list, new Comparator<EntryValueBean>() {
			public int compare(EntryValueBean o1, EntryValueBean o2) {
                  return o1.getDnName().compareToIgnoreCase(o2.getDnName());
			}
		});
		return list;
	}
}
