package com.seeyon.apps.ldap.controller;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.domain.EntryValueBean;
import com.seeyon.apps.ldap.domain.V3xLdapSwitchBean;
import com.seeyon.apps.ldap.manager.BingdingEnum;
import com.seeyon.apps.ldap.manager.LdapBindingMgr;
import com.seeyon.apps.ldap.manager.LdapServerMap;
import com.seeyon.apps.ldap.manager.VerifyConnection;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.dao.paginate.Pagination;
import com.seeyon.ctp.common.fileupload.FileuploadManagerImpl;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.po.usermapper.CtpOrgUserMapper;
import com.seeyon.ctp.common.usermapper.dao.UserMapperDao;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgMember;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.TextEncoder;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.v3x.common.web.login.CurrentUser;

/**
 * ldap/ad 相关操作控制层
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2009-1-6
 */
public class LDAPSynchronController extends BaseController {
    private static final Log    log      = LogFactory.getLog(LDAPSynchronController.class);

    private static final String ENCODING = "UTF-8";

    private OrgManagerDirect    orgManagerDirect;

    private OrgManager          orgManager;
    private LdapBindingMgr      ldapBindingMgr;
    private AppLogManager       appLogManager;

    private VerifyConnection    verifyConnection;                                          //目录服务器配置验证

    private UserMapperDao       userMapperDao;

    @CheckRoleAccess(roleTypes = {Role_NAME.SystemAdmin})
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.setLdapSwitch(request, response);
    }

    public ModelAndView setLdapSwitch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/ldap/ldapSwitch");
        V3xLdapSwitchBean ldapSwitchBean = ldapBindingMgr.viewLdapSwitch();
        //不能把密码传给前台应用DEFAULT_INTERNAL_PASSWORD="~`@%^*#?"
        ldapSwitchBean.setLdapPassword(OrgConstants.DEFAULT_INTERNAL_PASSWORD);
        mav.addObject("v3xLdapSwitchBean", ldapSwitchBean);
        mav.addObject("ldapMap", LdapServerMap.getMap());
        return mav;
    }

    @CheckRoleAccess(roleTypes = {Role_NAME.SystemAdmin})
    public ModelAndView saveLdapSwitchParams(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //ModelAndView mav = new ModelAndView("apps/ldap/ldapSwitch");
        User user = AppContext.getCurrentUser();
        V3xLdapSwitchBean bean = new V3xLdapSwitchBean();
        bind(request, bean);
        String saveTip = ResourceUtil.getString("ldap.system.set");
        boolean validate = validate(bean.getDomainName()) && validate(bean.getHostName()) && validate(bean.getLdapAdmin())
        				&& validate(bean.getLdapBasedn()) && validate(bean.getLdapPort()) && validate(bean.getLdapServerType())
        				&& validateUrl(bean.getLdapUrl());
        if(validate){
        	try {
        		//检查密码是否修改
        		if (bean.getLdapPassword().equals(OrgConstants.DEFAULT_INTERNAL_PASSWORD)) {
        			String realPassword = TextEncoder.decode(ldapBindingMgr.viewLdapSwitch().getLdapPassword());
        			bean.setLdapPassword(realPassword);
        		}
        		boolean connect = false;
        		if ("1".equals(bean.getLdapEnabled())) {
        			connect = verifyConnection.verify(bean);//验证目录服务器配置
        			if (!connect) {
        				saveTip = ResourceUtil.getString("ldap.set.error");
        			}
        		}
        		if(connect || !"1".equals(bean.getLdapEnabled())){
        			ldapBindingMgr.saveLdapSwitch(bean);
        			appLogManager.insertLog(user, AppLogAction.DirectoryConfig);
        		}
                
        	} catch (Exception e) {
        		saveTip = ResourceUtil.getString("ldap.set.error");
        		log.info(e.getMessage(), e);
        	}
        }else{
        	saveTip = ResourceUtil.getString("ldap.set.error");
        }
        
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert(\"" + saveTip + "\");");
        out.println("</script>");
        out.flush();
        return this.setLdapSwitch(request, response);
    }
    
    private boolean validate(String str){
		//校验特殊字符
		String reg = "^.*[(/)|(\\\\)|(\\|)|(')|(\")|(<)|(>)].*$";
		if(Strings.isNotBlank(str) && str.matches(reg)){
			return false;
		}
		return true;
    }

    private boolean validateUrl(String str){
		//校验特殊字符
    	boolean flag = Strings.isIPv4LiteralAddress(str);
    	if(flag){
    		return flag;
    	}else{
    		flag =Strings.isIPv6LiteralAddress(str);
    	}
    	return flag;
    }
    public ModelAndView importLDIF(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/ldap/fileUpload");
        String str = FileuploadManagerImpl.getMaxSizeStr();
        int deleteAll = BingdingEnum.deleteAll.key();
        int coverAll = BingdingEnum.coverAll.key();
        mav.addObject("deleteAll", deleteAll);
        mav.addObject("coverAll", coverAll);
        mav.addObject("maxSize", str);
        return mav;
    }

    public ModelAndView frameset(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("apps/ldap/frameset");
    }

    public ModelAndView openHelp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("apps/ldap/help");
    }

    public ModelAndView uploadReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/ldap/report");

        String time = request.getParameter("parseTime");
        if (StringUtils.isNotBlank(time)) {
            mav.addObject("showTime", showTime(new Long(time)));
        }
        return mav;
    }

    public ModelAndView uploadProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/ldap/fileUpload");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        multipartRequest.setCharacterEncoding(ENCODING);
        Iterator fileNames = multipartRequest.getFileNames();
        long maxSize = -1;
        long endTime = -1;
        try {
            long start = System.currentTimeMillis();
            while (fileNames.hasNext()) {
                Object name = fileNames.next();

                if (name == null || "".equals(name)) {
                    continue;
                }
                MultipartFile fileItem = multipartRequest.getFile(String.valueOf(name));

                String fileUpload_maxSize = SystemProperties.getInstance().getProperty("fileUpload.maxSize");
                if (fileUpload_maxSize != null && !"".equals(fileUpload_maxSize)) {
                    maxSize = Long.parseLong(fileUpload_maxSize);
                }
                if (null != fileItem && fileItem.getSize() < maxSize) {
                    log.info(fileItem.getOriginalFilename());
                    InputStream stream = fileItem.getInputStream();
                    List<String> list = IOUtils.readLines(stream);
                    if (list == null || list.size() == 0) {
                        super.rendJavaScript(response, "parent.endProcess();");
                        return null;
                    }
                    List<V3xOrgMember> memberlist = orgManager.getAllMembers(AppContext.getCurrentUser()
                            .getLoginAccount());
                    if (memberlist == null || memberlist.isEmpty()) {
                        super.rendJavaScript(response, "alert('" + ResourceUtil.getString("ldap.alert.nonemember")
                                + "');parent.endProcess();");
                        return null;
                    }
                    String[] bindingOption = multipartRequest.getParameterValues("bindingOption");

                    int isCover = 0;
                    if (bindingOption != null) {
                        for (int i = 0; i < bindingOption.length; i++) {
                            if (bindingOption[i].equals(BingdingEnum.deleteAll.key() + "")) {
                                ldapBindingMgr.deleteAllBinding(orgManagerDirect, memberlist);
                            }
                            if (bindingOption[i].equals(BingdingEnum.coverAll.key() + "")) {
                                isCover = BingdingEnum.coverAll.key();
                            }
                        }
                    }
                    ldapBindingMgr.batchBinding(orgManager, list, memberlist, isCover);

                    endTime = System.currentTimeMillis() - start;
                    log.info("解析LDIF结束用时：" + endTime);
                } else {
                    super.rendJavaScript(response, "alert(\"" + ResourceUtil.getString("ldap.alert.toomuch") + "\");"
                            + "parent.endProcess();");
                    return null;
                }
            }
        } catch (Exception e) {
            super.rendJavaScript(response,
                    "alert(\"" + ResourceUtil.getString("ldap.alert.exception") + ": " + e.getMessage() + "\");"
                            + "parent.endProcess();");
            return null;
        }
        mav.addObject("parseTime", endTime);
        super.rendJavaScript(response,"parent.endProcess();parent.window.location.reload();parent.window.parentDialogObj['ldapImportdialog'].close();");
        return null;
    }

    public ModelAndView listUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/ldap/listusers");
        List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();

        V3xOrgAccount account = orgManager.getAccountById(AppContext.getCurrentUser().getLoginAccount());
        if (account == null) {
            return mav;
        }

        List<V3xOrgMember> memberlistTemp = orgManager.getAllMembers(account.getId());
        if (memberlistTemp != null) {
            memberlist = memberlistTemp;
        } else {
            return mav;
        }
        String reload = request.getParameter("reload");
        String textfield = request.getParameter("textfield");
        if (StringUtils.isNotBlank(textfield)) {
            for (Iterator it = memberlist.iterator(); it.hasNext();) {
                V3xOrgMember member = (V3xOrgMember) it.next();
                if (!member.getLoginName().matches(".*" + textfield + ".*")) {
                    it.remove();
                }
            }
        }
        Collections.sort(memberlist, CompareSortEntity.getInstance());
        List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
        long deptId = -1;
        long levelId = -1;
        long postId = -1;
        long accountId = -1;
        int first = Pagination.getFirstResult();
        int max = Pagination.getMaxResults();
        int rowCount = Pagination.getRowCount();
        if (null != memberlist) {
            for (V3xOrgMember member : memberlist) {
                deptId = member.getOrgDepartmentId();
                levelId = member.getOrgLevelId();
                postId = member.getOrgPostId();
                accountId = member.getOrgAccountId();
                WebV3xOrgMember webMember = new WebV3xOrgMember();
                webMember.setV3xOrgMember(member);
                V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
                if (dept != null) {
                    webMember.setDepartmentName(dept.getName());
                }
                if ((Boolean) SysFlag.sys_isGroupVer.getFlag()) {
                    if (account != null) {
                        webMember.setAccountName(account.getName());
                    }
                }
                V3xOrgLevel level = orgManager.getLevelById(levelId);
                if (null != level) {
                    webMember.setLevelName(level.getName());
                }

                V3xOrgPost post = orgManager.getPostById(postId);
                if (null != post) {
                    webMember.setPostName(post.getName());
                }

                // 组装LDAP/AD字符串
                List<CtpOrgUserMapper> userMappers = userMapperDao.getExLoginNames(member.getLoginName(), LDAPConfig
                        .getInstance().getType());
                String stateNames = "";
                for (CtpOrgUserMapper map : userMappers) {
                    stateNames += map.getExLoginName() + ",";
                }
                if (!StringUtils.isBlank(stateNames)) {
                    stateNames = stateNames.substring(0, stateNames.length() - 1);
                }
                webMember.setStateName(stateNames);
                resultlist.add(webMember);
            }
        }
        Pagination.setNeedCount(true);
        Pagination.setFirstResult(first);
        Pagination.setMaxResults(max);
        Pagination.setRowCount(rowCount);
        mav.addObject("reload", reload);
        mav.addObject("userMapperList", resultlist);
        return mav;
    }

    public ModelAndView query(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/ldap/listusers");
        List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();

        String textfield = request.getParameter("textfield");
        List<V3xOrgAccount> accountList = orgManager.getAllAccounts();

        for (V3xOrgAccount account : accountList) {
            List<V3xOrgMember> memberlistTemp = orgManagerDirect.getAllMembers(account.getId(), false);
            memberlist.addAll(memberlistTemp);
        }

        if (StringUtils.isNotBlank(textfield)) {
            for (Iterator<V3xOrgMember> it = memberlist.iterator(); it.hasNext();) {
                V3xOrgMember member = it.next();
                if (!member.getLoginName().matches(".*" + textfield + ".*")) {
                    it.remove();
                }
            }
        } else {
            return super.redirectModelAndView("/ldap.do?method=listUsers");
        }
        Collections.sort(memberlist, CompareSortEntity.getInstance());
        List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
        long deptId = -1;
        long levelId = -1;
        long postId = -1;
        long accountId = -1;

        if (null != memberlist) {
            for (V3xOrgMember member : memberlist) {
                deptId = member.getOrgDepartmentId();
                levelId = member.getOrgLevelId();
                postId = member.getOrgPostId();
                accountId = member.getOrgAccountId();
                WebV3xOrgMember webMember = new WebV3xOrgMember();
                webMember.setV3xOrgMember(member);
                V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
                if (dept != null) {
                    webMember.setDepartmentName(dept.getName());
                }
                V3xOrgAccount account = orgManager.getAccountById(accountId);

                if (account != null) {
                    webMember.setAccountName(account.getName());
                }
                V3xOrgLevel level = orgManager.getLevelById(levelId);
                if (null != level) {
                    webMember.setLevelName(level.getName());
                }

                V3xOrgPost post = orgManager.getPostById(postId);
                if (null != post) {
                    webMember.setPostName(post.getName());
                }

                // 组装LDAP/AD字符串
                List<CtpOrgUserMapper> userMappers = userMapperDao.getExLoginNames(member.getLoginName(), LDAPConfig
                        .getInstance().getType());
                String stateNames = "";
                for (CtpOrgUserMapper map : userMappers) {
                    stateNames += map.getExLoginName() + ",";
                }
                if (!StringUtils.isBlank(stateNames)) {
                    stateNames = stateNames.substring(0, stateNames.length() - 1);
                }
                webMember.setStateName(stateNames);
                resultlist.add(webMember);
            }
        }
        mav.addObject("userMapperList", resultlist);
        return mav;
    }

    public ModelAndView editUserMapper(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/ldap/editUserMapper");
        Long id = Long.parseLong(request.getParameter("id"));
        V3xOrgMember member = orgManager.getMemberById(id);
        long deptId = member.getOrgDepartmentId();
        long levelId = member.getOrgLevelId();
        long postId = member.getOrgPostId();
        WebV3xOrgMember webMember = new WebV3xOrgMember();
        webMember.setV3xOrgMember(member);
        // 获取扩展属性
        //orgManagerDirect.loadEntityProperty(member);
        webMember.setOfficeNum((String) member.getProperty("officeNum"));
        V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
        if (dept != null) {
            webMember.setDepartmentName(dept.getName());
        }

        V3xOrgLevel level = orgManager.getLevelById(levelId);
        if (null != level) {
            webMember.setLevelName(level.getName());
        }

        V3xOrgPost post = orgManager.getPostById(postId);
        if (null != post) {
            webMember.setPostName(post.getName());
        }

        // 组装LDAP/AD字符串
        List<CtpOrgUserMapper> userMappers = userMapperDao.getExLoginNames(member.getLoginName(), LDAPConfig
                .getInstance().getType());
        String stateNames = "";
        for (CtpOrgUserMapper map : userMappers) {
            stateNames += map.getExUnitCode() + "|";
        }
        if (!StringUtils.isBlank(stateNames)) {
            stateNames = stateNames.substring(0, stateNames.length() - 1);
        }
        webMember.setStateName(stateNames);

        mav.addObject("member", webMember);
        mav.addObject("oper", request.getParameter("oper"));
        return mav;
    }

    public ModelAndView updateUserMapper(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/ldap/updateUserMapper");
        try {
            long id = Long.valueOf(request.getParameter("id"));
            String loginName = request.getParameter("valideLogin");
            String ldapUserCodes = request.getParameter("ldapUserCodes");
            String[] resultArray = {};
            V3xOrgMember member = orgManager.getMemberByLoginName(loginName);
            if (member != null && member.getEnabled()) {
                resultArray = ldapBindingMgr.handBinding(id, loginName, ldapUserCodes, true);
            } else {
                resultArray = ldapBindingMgr.handBinding(id, loginName, ldapUserCodes, false);
            }

            mav.addObject("resultArray", resultArray);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return mav;
    }

    public ModelAndView viewUserTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/ldap/userTree");
        List<EntryValueBean> list = new ArrayList<EntryValueBean>();
        //如果是ad的话，只取一级节点，点开具体节点再加载
        if(LDAPConfig.ADFLAG.equals(LDAPConfig.getInstance().getSys().getProperty("ldap.ad.enabled"))){
        	mav = new ModelAndView("apps/ldap/userTreeForAd");
        	long accoutId = CurrentUser.get().getLoginAccount();
        	if(Strings.isNotBlank(request.getParameter("accountId"))){
        	    accoutId = Long.valueOf(request.getParameter("accountId"));
        	}
			String value = ldapBindingMgr.getDefaultOU(accoutId);
			if(Strings.isBlank(value) || "null".equals(value)) {
				value = "";
			}else {
				ldapBindingMgr.userTreeView(list,value);
				for(EntryValueBean bean : list){
					if(bean.getName().indexOf("\\")>0){
						bean.setName(bean.getName().replace("\\", "\\\\\\"));
						bean.setDnName(bean.getDnName().replace("\\", "\\\\"));
					}
					bean.setShowName(bean.getName());
					bean.setName(bean.getName().replace("'", "\\\\'"));
					bean.setDnName(bean.getDnName().replace("'", "\\\\'"));
				}
			}
			mav.addObject("currentAccountDN", value);
        }else{
        	try {
        		 list = ldapBindingMgr.ouTreeView(false);
        		
        		if (list == null) {
        			response.setCharacterEncoding("UTF-8");
        			response.setHeader("Content-Type", "text/html;charset=UTF-8");
        			PrintWriter out = response.getWriter();
        			out.println("<script>");
        			out.println("alert(\"" + ResourceUtil.getString("ldap.alert.setdn", "") + "\");");
        			out.println("window.close();");
        			out.println("</script>");
        			out.close();
        			return null;
        		}
        		ldapBindingMgr.userTreeView(list,null);
        	} catch (Exception e) {
        		log.error(e.getMessage(), e);
        	}
        }
        mav.addObject("rootDN", LDAPConfig.getInstance().getBaseDn());
        mav.addObject("userList", list);
        mav.addObject("onlyLoginName", request.getParameter("checkOnlyLoginName"));
        return mav;
    }
    
    public ModelAndView getSubNode(HttpServletRequest request, HttpServletResponse response) throws Exception {
         response.setContentType("text/xml");
         response.setCharacterEncoding("utf-8");
         PrintWriter out = response.getWriter();
        
    	 String type = request.getParameter("type");
    	 String xmlstr ="";
    	 if(!"cn".equalsIgnoreCase(type)){
    		 String parentDn = request.getParameter("parentDn");
    		 parentDn = parentDn.replace("\\'", "'");
    		 String parentId = request.getParameter("parentId");
    		 
    		 List<EntryValueBean> list = new ArrayList<EntryValueBean>();
    		 list = ldapBindingMgr.subTreeView(parentDn, parentId,type);
    		 StringBuilder result = new StringBuilder();
    		 for(EntryValueBean ev : list){
    			 String dn = ev.getDnName();
		        if (ev.getDnName().indexOf("/") != -1) {
		            dn = ev.getDnName().replace("/", "\\/");
		        }
		        dn=URLEncoder.encode(dn, "utf-8");
    			 String icon = "/seeyon/apps_res/doc/images/docIcon/folder_open.gif";
    			 String src = "/seeyon/ldap/ldap.do?method=getSubNode&amp;parentDn="+dn+"&amp;parentId="+ev.getId()+"&amp;type=ou";
    			 if("user".equals(ev.getType())){
    				 if(ev.getName().indexOf("cn")==0 || ev.getName().indexOf("CN")==0){
    					 icon = "/seeyon/apps_res/doc/images/docIcon/person.gif";
    					 src = "/seeyon/ldap/ldap.do?method=getSubNode&amp;parentDn="+dn+"&amp;parentId="+ev.getId()+"&amp;type=cn";
    				 }
    			 }
    			 String action  = "javascript:actionRdn('"+ Strings.escapeJavascript(ev.getName().replace("&", "&amp;")) + "','"+ Strings.escapeJavascript(ev.getDnName().replace("&", "&amp;"))+"','" + ev.getType() +"')";
    	            result.append("<tree id=\"" + ev.getId() + "\" icon=\"" + icon
    	                    + "\" openIcon =\"" + icon + "\"" + " text=\"" + ev.getName().replace("&", "&amp;")
    	                    + "\" src=\""+ src + "\" action=\""+action+"\"/>");
    		 }
    		 xmlstr = result.toString();
    	 }
    	 out.println("<tree text=\"loaded\">");
    	 out.println(xmlstr);
    	 out.println("</tree>");
    	 out.close();
    	 return null;
    }
    
    public ModelAndView getOrgSubNode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/xml");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();

		String type = request.getParameter("type");
		String xmlstr = "";
		if ("domainDNS".equalsIgnoreCase(type) || "ou".equalsIgnoreCase(type)) {
			String parentDn = request.getParameter("parentDn");
			String parentId = request.getParameter("parentId");
			List<EntryValueBean> list = new ArrayList<EntryValueBean>();
			list = ldapBindingMgr.subOrgTreeView(parentDn, parentId);
			StringBuilder result = new StringBuilder();
			for (EntryValueBean ev : list) {
				String dn = ev.getDnName();
				if (ev.getDnName().indexOf("/") != -1) {
					dn = ev.getDnName().replace("/", "\\/");
				}
				dn = URLEncoder.encode(dn, "utf-8");
				String icon = "/seeyon/apps_res/doc/images/docIcon/corp_open.gif";
				String src = "/seeyon/ldap/ldap.do?method=getOrgSubNode&amp;parentDn=" + dn + "&amp;parentId=" + ev.getId()
						+ "&amp;type=" + ev.getType();
				String action = "javascript:showRdn('" + ev.getDnName() + "')";
				result.append("<tree id=\"" + ev.getId() + "\" icon=\"" + icon + "\" openIcon =\"" + icon + "\""
						+ " text=\"" + ev.getName() + "\" src=\"" + src + "\" action=\"" + action + "\"/>");
			}
			xmlstr = result.toString();
		}
		out.println("<tree text=\"loaded\">");
		out.println(xmlstr);
		out.println("</tree>");
		out.close();
		return null;
	}

    public ModelAndView viewOuTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	log.info("----------viewOuTree strart------------------");
        ModelAndView mav = new ModelAndView("apps/ldap/ouTree");

        try {
            List<EntryValueBean> list = ldapBindingMgr.ouTreeView(true);
            mav.addObject("userList", list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        mav.addObject("rootDN", LDAPConfig.getInstance().getBaseDn());
        log.info("----------viewOuTree end------------------");
        return mav;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }

    public void setLdapBindingMgr(LdapBindingMgr ldapBindingMgr) {
        this.ldapBindingMgr = ldapBindingMgr;
    }

    public void setUserMapperDao(UserMapperDao userMapperDao) {
        this.userMapperDao = userMapperDao;
    }

    /**
     * @param long
     *            starTime 输入毫秒数，返回带有小时分秒的字符串
     * @return String 小时分秒的字符串
     */
    private String showTime(long starTime) {
        if (starTime > 0) {
            long endM = 0;
            long starMinute = 0;
            long endMinuteS = 0;
            long starHour = 0;
            long endHourMi = 0;
            if (starTime > 1000) {
                long starSecond = starTime / 1000;
                endM = starTime % 1000;

                starMinute = starSecond / 60;

                endMinuteS = starSecond % 60;

                starHour = starMinute / 60;

                endHourMi = starMinute % 60;
            } else {
                endM = starTime;
            }
            return ((starHour == 0 ? "" : starHour + ResourceUtil.getString("org.synchron.hour"))
                    + (endHourMi == 0 ? "" : endHourMi + ResourceUtil.getString("org.synchron.minutes"))
                    + (endMinuteS == 0 ? "" : endMinuteS + ResourceUtil.getString("org.synchron.second")) + endM + ResourceUtil
                        .getString("org.synchron.ms"));
        }
        return null;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }

    public void setVerifyConnection(VerifyConnection verifyConnection) {
        this.verifyConnection = verifyConnection;
    }
}
