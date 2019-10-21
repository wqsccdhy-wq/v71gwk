package com.seeyon.ctp.organization.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.addressbook.manager.AddressBookManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.excel.DataRecord;
import com.seeyon.ctp.common.excel.DataRow;
import com.seeyon.ctp.common.excel.FileToExcelManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.LocaleContext;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.metadata.bo.MetadataColumnBO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.inexportutil.DataUtil;
import com.seeyon.ctp.organization.inexportutil.manager.IOManager;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

@CheckRoleAccess(roleTypes = { Role_NAME.GroupAdmin, Role_NAME.AccountAdministrator, Role_NAME.HrAdmin})
public class OrganizationControll extends BaseController{
    private final static Log   log = LogFactory.getLog(OrganizationControll.class);
    protected OrgManager       orgManager;
    protected IOManager        iOManager;
    protected OrgManagerDirect orgManagerDirect;
    protected AppLogManager    appLogManager;
    protected FileToExcelManager fileToExcelManager;
    protected AddressBookManager addressBookManager;

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setIOManager(IOManager iOManager) {
        this.iOManager = iOManager;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }
	
	public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }

    public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}
    
	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}

	private String clientAbortExceptionName = "ClientAbortException";

	/**
	 * 下载导入xls模板
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    @CheckRoleAccess(roleTypes = { Role_NAME.GroupAdmin, Role_NAME.AccountAdministrator, Role_NAME.HrAdmin,Role_NAME.BusinessOrganizationManager })
    public ModelAndView downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String type = request.getParameter("type"); //member, post		
        String path = "";
        String filename = "";

        response.setContentType("application/x-msdownload; charset=UTF-8");
        
        Long accountId = AppContext.currentAccountId();
        String accountIdStr = request.getParameter("accountId");
        if (Strings.isNotBlank(accountIdStr)) {
            accountId = Long.valueOf(accountIdStr);
        }
        boolean isGroup = OrgConstants.GROUPID.equals(accountId);
        String local = AppContext.getLocale().toString();

        if (type.equals(V3xOrgEntity.ORGENT_TYPE_MEMBER)) {
        	/*            path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/member";
            filename = "member.xls";*/
        	downloadDynamicTemplate(request,response);
        	return null;

        } else if (type.equals(V3xOrgEntity.ORGENT_TYPE_POST)) {
            if (isGroup) {
                path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/group_post";
            } else {
                path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/post";
                
                if("en".equals(local.toLowerCase())){
                    path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/post_en";
                }
                if("zh_tw".equals(local.toLowerCase())){
                    path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/post_zh_TW";
                }
            }
            filename = "post.xls";
        }
        else if (type.equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT)) {
        	if("1".equals(request.getParameter("isBusinessOrg"))){//业务组织
        		path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/businessDepartment";
        		filename = "businessDepartment.xls";
        		if("en".equals(local.toLowerCase())){
        			filename = "businessDepartment_en.xls";
        			path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/businessDepartment_en";
        		}
        		if("zh_tw".equals(local.toLowerCase())){
        			filename = "businessDepartment_zh_TW.xls";
        			path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/businessDepartment_zh_TW";
        		}
        	}else{
        		path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/department";
        		filename = "department.xls";
        		if("en".equals(local.toLowerCase())){
        			filename = "department_en.xls";
        			path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/department_en";
        		}
        		if("zh_tw".equals(local.toLowerCase())){
        			filename = "department_zh_TW.xls";
        			path = SystemEnvironment.getApplicationFolder() + "/apps_res/edoc/file/orgnization/department_zh_TW";
        		}
        	}
        }
        
        response.setHeader("Content-disposition", "attachment;filename=\"" + filename + "\"");

        OutputStream out = null;
        InputStream in = null;
        try {
            in = new FileInputStream(new File(path));
            out = response.getOutputStream();

            IOUtils.copy(in, out);
        } catch (Exception e) {
            if (e.getClass().getSimpleName().equals(this.clientAbortExceptionName)) {
                log.debug("用户关闭下载窗口: " + e.getMessage());
            } else {
                log.error("", e);
            }
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        return null;

    }

    /**
     * 執行导入操作的页面跳转控制器
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("apps/organization/common/selectImportExcel");
        String importType = request.getParameter("importType");
        List<Locale> allLoc = LocaleContext.getAllLocales();
        List<String> allLocales = new ArrayList<String>();
        for (Locale l : allLoc) {
            allLocales.add(l.toString());
        }
        Locale locale = LocaleContext.getLocale(request);
        request.setAttribute("allLocales", allLocales);
        request.setAttribute("local", locale.toString());
        modelAndView.addObject("importType", importType);
        HttpSession session = request.getSession();
        session.setAttribute("importType", importType);
        List<V3xOrgAccount> accountlst = orgManager.getAllAccounts();
        modelAndView.addObject("accountlst", accountlst);
        return modelAndView;
    }
    
    /**
     * 执行导入
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView doImport(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setContentType("text/html;charset=UTF-8");

        User u = AppContext.getCurrentUser();
        if (u == null) {
            //DataUtil.outNullUserAlertScript(out);
            return null;
        }
        if (DataUtil.doingImpExp(u.getId())) {
            //DataUtil.outDoingImpExpAlertScript(out);
            return null;
        }

        Long accountId = AppContext.currentAccountId();
        String accountIdStr = request.getParameter("accountId");
        if (Strings.isNotBlank(accountIdStr)) {
            accountId = Long.valueOf(accountIdStr);
        }
        
        iOManager.setOpUser(u);
        iOManager.setVaccountByUser(accountId);

        String reportUrl = null;
        DataUtil.putImpExpAction(u.getId(), "import");
        try {
            reportUrl = iOManager.doImport4Redirect(request, response);
            log.info("reportUrl=" + reportUrl);
        } catch (Exception e) {
            DataUtil.removeImpExpAction(u.getId());
            //throw e;
        }
        DataUtil.removeImpExpAction(u.getId());
        //记录日志
        User user = AppContext.getCurrentUser();
        String selectvalue = request.getParameter("selectvalue");
        if (selectvalue == null) {
            selectvalue = (String) request.getAttribute("selectvalue");
        }
        if (selectvalue == null || "".equals(selectvalue) || "null".equals(selectvalue)) {
            throw new Exception("请上传文件对应的表！");
        } else {
            if ("post".equals(selectvalue)) {
                appLogManager.insertLog4Account(user, user.isGroupAdmin() ? accountId : AppContext.currentAccountId(), AppLogAction.Organization_BatchAddPost, user.getName());
            } else if ("member".equals(selectvalue)) {
                appLogManager.insertLog4Account(user, user.isGroupAdmin() ? accountId : AppContext.currentAccountId(), AppLogAction.Organization_BatchAddMember, user.getName());
            }else if("department".equals(selectvalue)){
                String deptOrRole = request.getParameter("depart");
                if("1".equals(deptOrRole)){
                    appLogManager.insertLog4Account(user, user.isGroupAdmin() ? accountId : AppContext.currentAccountId(), OrgConstants.AppLogAction.Organization_BatchAddDepartment.getKey(), user.getName());
                }else{
                    appLogManager.insertLog4Account(user, user.isGroupAdmin() ? accountId : AppContext.currentAccountId(), OrgConstants.AppLogAction.Organization_BatchAddDepartmentRole.getKey(), user.getName());
                }
            }
        }

        return iOManager.importReport(request, response);
    }
    
    
    public ModelAndView importReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //		ModelAndView modelAndView = new ModelAndView("organization/importReport");
        return iOManager.importReport(request, response);
    }
    
    /**
     * 导出结果报告
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes = { Role_NAME.GroupAdmin, Role_NAME.AccountAdministrator, Role_NAME.HrAdmin })
    public ModelAndView exportReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return iOManager.exportReport(request, response);
    }
    
    /*
     * 导入人员动态模板
     */
    public void downloadDynamicTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String listname = "member";

        String key = null;
        DataUtil.putImpExpAction(AppContext.getCurrentUser().getId(), "export");
        DataRecord dataRecord = null;
        try {
        	if(request.getParameter("isInternal")!=null && "false".equals(request.getParameter("isInternal").toString())){
        		listname = "ext_member";
        		dataRecord = dynamicTemplateForExt(request, fileToExcelManager);
        	}else{
        		dataRecord = dynamicTemplateForInner(request, fileToExcelManager);
        	}
            key = DataUtil.createTempSaveKey4Sheet(dataRecord);
        } catch (Exception e) {
            DataUtil.removeImpExpAction(AppContext.getCurrentUser().getId());
            log.error(e);
            throw new BusinessException("模板下载失败！");
        }
        DataUtil.removeImpExpAction(AppContext.getCurrentUser().getId());

        String url = DataUtil.getOrgDownloadExpToExcelUrl(key, listname);
        DataUtil.removeImpExpAction(AppContext.getCurrentUser().getId());
        try {
            OrgHelper.exportToExcel(request, response, fileToExcelManager, listname, dataRecord);
        } catch (Exception e) {
            log.error(e);
            throw e;
        } finally {
        }

    }
    
	/**
	 * 动态模板模拟数据(内部人员)
	 * @param request
	 * @param fileToExcelManager
	 * @return
	 * @throws Exception
	 */
    private DataRecord dynamicTemplateForInner(HttpServletRequest request, FileToExcelManager fileToExcelManager) throws Exception {
        DataRecord dataRecord = new DataRecord();

        //导出excel文件的国际化
        String member_name = ResourceUtil.getString("org.member_form.name.label");
        String member_loginName = ResourceUtil.getString("org.member_form.loginName.label");
        String member_code = ResourceUtil.getString("org.member_form.code");
        String member_sortId = ResourceUtil.getString("org.member_form.sort");
        String member_deptName = ResourceUtil.getString("org.member_form.deptName.label"); 
        String member_primaryPost = ResourceUtil.getString("org.member_form.primaryPost.label");
        String member_secondPost = ResourceUtil.getString("org.member_form.secondPost.label");
        String member_levelName = ResourceUtil.getString("org.member_form.levelName.label");
        String member_tel = ResourceUtil.getString("org.member_form.tel");
        String member_account = ResourceUtil.getString("org.member_form.account");
        String member_email = ResourceUtil.getString("org.member.emailaddress");
        String member_gender = ResourceUtil.getString("org.memberext_form.base_fieldset.sexe");
        String member_birthday = ResourceUtil.getString("org.memberext_form.base_fieldset.birthday");
        String member_officeNumber = ResourceUtil.getString("member.office.number");
        String member_list = ResourceUtil.getString("org.member_form.list");
        String member_primaryLanguange = ResourceUtil.getString("org.member_form.primaryLanguange");
        //String member_communication = ResourceUtil.getString("hr.staffInfo.communication.label");
        String member_location = ResourceUtil.getString("member.location");
        String member_hiredate = ResourceUtil.getString("member.hiredate");
        String member_reporter = ResourceUtil.getString("member.report2");
        String customerAddressBookFields="";
            
        //自定义的通讯录字段
        List<MetadataColumnBO> metadataColumnList=addressBookManager.getCustomerAddressBookList();
        for(MetadataColumnBO metadataColumn : metadataColumnList){
    		customerAddressBookFields=customerAddressBookFields+","+metadataColumn.getLabel();
    	}
        
        DataRow[] datarow = new DataRow[1];
            DataRow row = new DataRow();
            row.addDataCell(ResourceUtil.getString("import.templet.cell1"), 1);
            row.addDataCell(ResourceUtil.getString("import.templet.cell2"), 1);
            row.addDataCell("", 1);
            row.addDataCell("1", 1);
            row.addDataCell(ResourceUtil.getString("import.templet.cell3"), 1);
            row.addDataCell(ResourceUtil.getString("import.templet.cell4"), 1);
            row.addDataCell(ResourceUtil.getString("import.templet.cell5"), 1);
            row.addDataCell(ResourceUtil.getString("import.templet.cell6"), 1);
            row.addDataCell(ResourceUtil.getString("import.templet.cell7"), 1);
            row.addDataCell("", 1);
            row.addDataCell("123@sina.com", 1);
            row.addDataCell("", 1);
            row.addDataCell("1971-10-12", 1);
            row.addDataCell("58011234", 1);
            row.addDataCell(ResourceUtil.getString("import.templet.cell8"), 1);
           // row.addDataCell("北京市海淀区北坞村路甲25号静芯园n座致远软件", 1);
            row.addDataCell("北京-北京市-海淀区", 1);
            row.addDataCell("2000-01-01", 1);
            row.addDataCell(ResourceUtil.getString("import.templet.cell9"), 1);
            //自定义的通讯录字段
            for(MetadataColumnBO metadataColumn : metadataColumnList){
                row.addDataCell("",1);
            }
            datarow[0] = row;

            
        try {
            dataRecord.addDataRow(datarow);
        } catch (Exception e) {
            log.error("error", e);
        }
            
    	String columnName=member_name+","+member_loginName+","+member_code+","+member_sortId+","+member_account+","+member_deptName+","
    			+ member_primaryPost+","+member_secondPost+","+member_levelName+","+member_tel+","+member_email+","+member_gender+","+member_birthday+","+member_officeNumber+","+member_primaryLanguange+ ","+member_location+","+member_hiredate+","+member_reporter;
        String columnNameAllStr=columnName+customerAddressBookFields;
        String[] columnNameAll=columnNameAllStr.split(",");
        dataRecord.setColumnName(columnNameAll);
            
        dataRecord.setTitle(member_list);
        dataRecord.setSheetName(member_list);
        return dataRecord;
    }
    
	/**
	 * 动态模板模拟数据(编外人员)
	 * @param request
	 * @param fileToExcelManager
	 * @return
	 * @throws Exception
	 */
    private DataRecord dynamicTemplateForExt(HttpServletRequest request, FileToExcelManager fileToExcelManager) throws Exception {
        DataRecord dataRecord = new DataRecord();

        //导出excel文件的国际化
        String member_list = ResourceUtil.getString("org.member_ext_form.list");
        String member_name = ResourceUtil.getString("org.member_form.name.label");
        String member_loginName = ResourceUtil.getString("org.member_form.loginName.label");
        String member_code = ResourceUtil.getString("org.member_form.code");
        String member_sortId = ResourceUtil.getString("org.member_form.sort");
        String member_deptName = ResourceUtil.getString("org.member_form.ExtDeptName4export.label");
        String member_primaryPost = ResourceUtil.getString("org.member_form.primaryPost.label");
        String member_levelName = ResourceUtil.getString("org.member_form.levelName.label");
        String member_tel = ResourceUtil.getString("org.member_form.tel");
        String member_account = ResourceUtil.getString("org.member_form.account");
        String member_email = ResourceUtil.getString("org.member.emailaddress");
        String member_gender = ResourceUtil.getString("org.memberext_form.base_fieldset.sexe");
        String member_birthday = ResourceUtil.getString("org.memberext_form.base_fieldset.birthday");
        String member_officeNumber = ResourceUtil.getString("member.office.number");
        String member_primaryLanguange = ResourceUtil.getString("org.member_form.primaryLanguange");
        String member_workscope = ResourceUtil.getString("org.external.member.form.work");
        
        String customerAddressBookFields="";
            
        //自定义的通讯录字段
        List<MetadataColumnBO> metadataColumnList=addressBookManager.getCustomerAddressBookList();
        for(MetadataColumnBO metadataColumn : metadataColumnList){
    		customerAddressBookFields=customerAddressBookFields+","+metadataColumn.getLabel();
    	}
        
        DataRow[] datarow = new DataRow[1];
        
        DataRow row = new DataRow();
        row.addDataCell(ResourceUtil.getString("import.templet.cell1"), 1);
        row.addDataCell(ResourceUtil.getString("import.templet.cell2"), 1);
        row.addDataCell("", 1);
        row.addDataCell("1", 1);
        row.addDataCell(ResourceUtil.getString("import.templet.cell3"), 1);
        row.addDataCell(ResourceUtil.getString("import.templet.cell4"), 1);
        row.addDataCell(ResourceUtil.getString("import.templet.cell10"), 1);//
        row.addDataCell("", 1);
        row.addDataCell("", 1);
        row.addDataCell("", 1);
        row.addDataCell("123@sina.com", 1);
        row.addDataCell("", 1);
        row.addDataCell("1971-10-12", 1);
        row.addDataCell("58011234", 1);
        row.addDataCell(ResourceUtil.getString("import.templet.cell8"), 1);
        //自定义的通讯录字段
        for(MetadataColumnBO metadataColumn : metadataColumnList){
            row.addDataCell("",1);
        }
        datarow[0] = row;

            
        try {
            dataRecord.addDataRow(datarow);
        } catch (Exception e) {
            log.error("error", e);
        }
            
    	String columnName=member_name+","+member_loginName+","+member_code+","+member_sortId+","+member_account+","+member_deptName+","+member_workscope+","
    			+ member_primaryPost+","+member_levelName+","+member_tel+","+member_email+","+member_gender+","+member_birthday+","+member_officeNumber+","+member_primaryLanguange;
        String columnNameAllStr=columnName+customerAddressBookFields;
        String[] columnNameAll=columnNameAllStr.split(",");
        dataRecord.setColumnName(columnNameAll);
        dataRecord.setTitle(member_list);
        short subHeight = 2000; 
        dataRecord.setSubHeight(subHeight);
        dataRecord.setSubTitle(ResourceUtil.getString("org.member_ext_form.list.desc"));
        dataRecord.setSheetName(member_list);
        return dataRecord;
    }
}
