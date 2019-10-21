/**
 * 
 */
package com.seeyon.ctp.organization.memberleave.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.excel.DataRecord;
import com.seeyon.ctp.common.excel.DataRow;
import com.seeyon.ctp.common.excel.FileToExcelManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.inexportutil.DataUtil;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.memberleave.bo.MemberLeaveDetail;
import com.seeyon.ctp.organization.memberleave.bo.MemberLeavePending;
import com.seeyon.ctp.organization.memberleave.manager.MemberLeaveManager;
import com.seeyon.ctp.organization.webmodel.WebMemberLeaveAppLog;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

/**
 * @author tanmf
 *
 */
@CheckRoleAccess(roleTypes = { Role_NAME.GroupAdmin, Role_NAME.AccountAdministrator, Role_NAME.DepAdmin, Role_NAME.HrAdmin })
public class MemberLeaveController extends BaseController {

    private static final Log log = LogFactory.getLog(MemberLeaveController.class);

    /**
     * 离职办理接口
     */
    private MemberLeaveManager memberLeaveManager;
    
    private OrgManager  orgManager;
    
    protected OrgCache  orgCache;
    
    private FileToExcelManager fileToExcelManager;
    
    /**
     * @param memberLeaveManager the memberLeaveManager to set
     */
    public void setMemberLeaveManager(MemberLeaveManager memberLeaveManager) {
        this.memberLeaveManager = memberLeaveManager;
    }
    
    public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
    
	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public OrgCache getOrgCache() {
		return orgCache;
	}

	public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}

	/**
     * 显示离职人员协同相关交接页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showLeavePage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        ModelAndView mav = new ModelAndView("apps/organization/memberLeave/showLeavePage");
        
        long memberId = Long.parseLong(request.getParameter("memberId"));
        
        List<MemberLeavePending> pendings = this.memberLeaveManager.getMemberLeavePending(memberId);
        
/*        //根据userid获得用户所在的所有流程模板列表 (V)
        //根据userid获得该用户审核的公告板块列表 (V)
        //根据userid获得该用户管理的调查板块列表 (V)
        //根据userid获得该用户审核的新闻板块列表 (V)
        //根据userid获得该用户是否为综合办公的管理员
        //根据userid获得该用户还没有归还的综合办公物品列表
        List<String> handItems = this.memberLeaveManager.getMemberLeaveHandItem(MemberLeaveClearItemInterface.Type.HandItem, memberId);
        
        //根据userid获得用户的所有角色关系列表 (V)
        //根据userid获得该用户负责的项目列表
        //根据userid获得该用户的表单模板列表 
        //根据userid获得该用户管理的空间列表 
        //根据userid获得该用户管理的讨论板块列表 (V)
        //根据userid获得该用户管理的新闻板块列表 (V)
        //根据userid获得该用户管理的公告板块列表 (V)
        //根据userid获得该用户审核的调查板块列表 (V)
        List<String> roles = this.memberLeaveManager.getMemberLeaveHandItem(MemberLeaveClearItemInterface.Type.Role, memberId);
*/        
        mav.addObject("pendings", pendings);
/*        mav.addObject("handItems", handItems);
        mav.addObject("roles", roles);*/
        
        return mav;
    }
    
    
    public ModelAndView dealLeavePage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        ModelAndView mav = new ModelAndView("apps/organization/memberLeave/dealLeavePage");
        return mav;
    }
    
    public ModelAndView dealLeave(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String index = request.getParameter("index");
    	Long memberId = Long.valueOf(request.getParameter("memberId").toString());
        ModelAndView mav = new ModelAndView("apps/organization/memberLeave/dealLeave" + index);
        V3xOrgMember member = orgManager.getMemberById(memberId);
        if(member != null){
        	mav.addObject("accountId", member.getOrgAccountId());
        }
        
        if("1".equals(index)){//未处理事项
            List<MemberLeavePending> pendings = this.memberLeaveManager.getMemberLeavePending(memberId);
            mav.addObject("pendings", pendings);
        }
        
        if("61".equals(index)){//cap3表单
        	List<V3xOrgMember> members = orgManager.getMembersByRole(member.getOrgAccountId(), "FormAdmin");
        	String includeElements = "";
        	for(V3xOrgMember m : members){
        		if(Strings.isBlank(includeElements)){
        			includeElements = "Member|" + m.getId();
        		}else{
        			includeElements = includeElements + ",Member|" + m.getId();
        		}
        	}
        	if(Strings.isBlank(includeElements)) {
        		//设置一个无效人员，保证选人界面打开可选人为空
        		includeElements = "Member|-1";
        	}
        	mav.addObject("includeElements", includeElements);
        }
        
        if("62".equals(index)){//cap4表单
        	String includeElements = "";
        	List<V3xOrgMember> members = orgManager.getMembersByRole(member.getOrgAccountId(), "BusinessDesigner");
        	for(V3xOrgMember m : members){
        		if(Strings.isBlank(includeElements)){
        			includeElements = "Member|" + m.getId();
        		}else{
        			includeElements = includeElements + ",Member|" + m.getId();
        		}
        	}
        	if(Strings.isBlank(includeElements)) {
        		//设置一个无效人员，保证选人界面打开可选人为空
        		includeElements = "Member|-1";
        	}
        	mav.addObject("includeElements", includeElements);
        }
        
        if("7".equals(index)){//报表
        	String includeElements = "";
        	List<V3xOrgAccount> accounts = orgManager.getAllAccounts();
        	List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
        	for(V3xOrgAccount account : accounts) {
        		members.addAll(orgManager.getMembersByRole(account.getId(), "VReportAdmin"));
        	}
        	for(V3xOrgMember m : members){
        		if(Strings.isBlank(includeElements)){
        			includeElements = "Member|" + m.getId();
        		}else{
        			includeElements = includeElements + ",Member|" + m.getId();
        		}
        	}
        	if(Strings.isBlank(includeElements)) {
        		//设置一个无效人员，保证选人界面打开可选人为空
        		includeElements = "Member|-1";
        	}
        	mav.addObject("includeElements", includeElements);
        }
        
        return mav;
    }

   /**
    * 显示离职交接待办列表
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public ModelAndView showList4Leave(HttpServletRequest request,HttpServletResponse response) throws Exception{
        return new ModelAndView("apps/organization/memberLeave/showList4Leave");
   }

   /**
    * 保存离职交接信息
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
    public ModelAndView save4Leave(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map params = ParamUtil.getJsonParams();

        Long memberId = Long.parseLong(String.valueOf(params.get("memberId")));
        Long noupdateState = "1".equals(params.get("noupdateState"))?1L:0L;
        Map<String, Long> agentMember = new HashMap<String, Long>();
        agentMember.put("noupdateState", noupdateState);
        for (Object key : params.keySet()) {
            if(String.valueOf(key).startsWith("AgentId_")){
                String value = String.valueOf(params.get(key));
                
                if(Strings.isNotBlank(value)){
                    agentMember.put(String.valueOf(key).substring(8), Long.parseLong(value));
                }
            }
        }
        
        this.memberLeaveManager.save4Leave(memberId, agentMember);

        return null;
    }
    
    
    
    
    /**
     * 导出离职人员未完成信息
     * @param request
     * @param response
     * @return
     * @throws Exception 
     */
    public ModelAndView exportMemberLeaveList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = AppContext.getCurrentUser();
        orgCache.setOrgExportFlag(true);
        if (user == null) {
            return null;
        }
        if (DataUtil.doingImpExp(user.getId())) {
            return null;
        }
        
        Long memberId=Long.valueOf(request.getParameter("memberId"));
        
        int index = Integer.valueOf(request.getParameter("index"));
        Map paraMap = new HashMap();
        String name="";
        V3xOrgMember member=orgManager.getMemberById(memberId);
        if(null!=member){
        	name=member.getName();
        }
        paraMap.put("memberId", memberId.toString());
        if(index != 9){
        	int category = Integer.valueOf(request.getParameter("category"));
        	String condition=request.getParameter("condition").replace("\"", "");
        	String value=request.getParameter("value").replace("\"", "");
        	paraMap.put("condition", condition);
        	paraMap.put("value", value);
        	paraMap.put("category", category);
        }
        
        String listname = "MemberLeaveList_";
        listname += name;

        String key = null;
        DataUtil.putImpExpAction(user.getId(), "export");
        DataRecord dataRecord = null;
        try {
            dataRecord = exportMember(request, fileToExcelManager, paraMap);
            key = DataUtil.createTempSaveKey4Sheet(dataRecord);
        } catch (Exception e) {
            DataUtil.removeImpExpAction(user.getId());
            log.error(e);
            orgCache.setOrgExportFlag(false);
            throw new BusinessException("导出失败！");
        }
        DataUtil.removeImpExpAction(user.getId());

        String url = DataUtil.getOrgDownloadExpToExcelUrl(key, listname);
        log.info("url=" + url);
        DataUtil.removeImpExpAction(user.getId());
        try {
            OrgHelper.exportToExcel(request, response, fileToExcelManager, listname, dataRecord);
        } catch (Exception e) {
            log.error(e);
            throw e;
        } finally {
            orgCache.setOrgExportFlag(false);
        }
        return null;

    }
    
    /**
     * 将导出人员数据整理excel的数据对象
     * @param fileToExcelManager
     * @param orgManagerDirect
     * @return
     * @throws Exception
     */
    private DataRecord exportMember(HttpServletRequest request, FileToExcelManager fileToExcelManager, Map paraMap) throws Exception {
    	
    	int index = Integer.valueOf(request.getParameter("index"));
    	
        FlipInfo fi=new FlipInfo();
        fi.setSize(10000);
        List<MemberLeaveDetail> list = new ArrayList<MemberLeaveDetail>();
        if(index != 9){
        	fi=memberLeaveManager.showLeaveInfo(fi, paraMap);
        	list=fi.getData();
        }
        DataRecord dataRecord = new DataRecord();
        
		switch (index) {
		  	case 1:
		  		break;
		  	case 2:
		  		break;
		  	case 3:
			  	{
			  		paraMap.put("accountId", request.getParameter("accountId"));
			        fi = memberLeaveManager.showTeamList(fi, paraMap);
			        list=fi.getData();
			        
			  		String title = ResourceUtil.getString("team.name");
			  		String teamLeader = ResourceUtil.getString("team.charge");
			  		String type = ResourceUtil.getString("team.type");
			  		String account = ResourceUtil.getString("member.leave.account");
			  		String member_list = ResourceUtil.getString("memberleave.Category.Team");			  		
			  		if (null != list && list.size() > 0) {
			  			DataRow[] datarow = new DataRow[list.size()];
			  			for (int i=0;i<list.size();i++) {
			  				Map m = (Map)list.get(i);
			  				DataRow row = new DataRow();
			  				row.addDataCell(m.get("name").toString(), 1);
			  				row.addDataCell(m.get("teamLeader").toString(), 2);
			  				row.addDataCell(ResourceUtil.getString("org.team_form.systemteam"), 3);
			  				row.addDataCell(m.get("accountName").toString(), 4);
			  				datarow[i] = row;
			  			}
			  			dataRecord.addDataRow(datarow);
			  		}
			  		String[] columnName={title,teamLeader,type,account};
			  		dataRecord.setColumnName(columnName);
			  		dataRecord.setTitle(member_list);
			  		dataRecord.setSheetName(member_list);
			  	}
		  		break;
		  	case 4:
			  	{
			  		String title = ResourceUtil.getString("memberleave.dealleave4.role_name");
			  		String roleType = ResourceUtil.getString("memberleave.dealleave4.role_type");
			  		String enabled = ResourceUtil.getString("memberleave.dealleave4.status");
			  		String account = ResourceUtil.getString("member.leave.account");
			  		String member_list = ResourceUtil.getString("memberleave.Category.Role");			  		
			  		if (null != list && list.size() > 0) {
			  			DataRow[] datarow = new DataRow[list.size()];
			  			for (int i=0;i<list.size();i++) {
			  				MemberLeaveDetail m=list.get(i);
			  				DataRow row = new DataRow();
			  				row.addDataCell(m.getTitle(), 1);
			  				
			  				String roleTypeName = "";
			  				if(m.getRoleType() == 0){
			  					roleTypeName = ResourceUtil.getString("role.group");
			  				}else if(m.getRoleType() == 1){
			  					roleTypeName = ResourceUtil.getString("role.unit");
			  				}else if(m.getRoleType() == 2){
			  					roleTypeName = ResourceUtil.getString("role.dept");
			  				}
			  				row.addDataCell(roleTypeName, 2);
			  				
			  				String enabledName = "";
			  				if(m.getEnabled()){
			  					enabledName = ResourceUtil.getString("role.start");
			  				}else{
			  					enabledName = ResourceUtil.getString("role.stop");
			  				}
			  				row.addDataCell(enabledName, 3);
			  				
			  				row.addDataCell(m.getAccountName(), 4);
			  				datarow[i] = row;
			  			}
			  			dataRecord.addDataRow(datarow);
			  		}
			  		String[] columnName={title,roleType,enabled,account};
			  		dataRecord.setColumnName(columnName);
			  		dataRecord.setTitle(member_list);
			  		dataRecord.setSheetName(member_list);
			  	}
		  		break;
		  	case 5:
		  	{
		  		String content = ResourceUtil.getString("memberleave.dealleave5.manage_permission");
		  		String title = ResourceUtil.getString("memberleave.dealleave5.manage_content");
		  		String type = ResourceUtil.getString("memberleave.dealleave5.type");
		  		String account = ResourceUtil.getString("member.leave.account");
		  		String member_list = ResourceUtil.getString("memberleave.Category.Manager");			  		
		  		if (null != list && list.size() > 0) {
		  			DataRow[] datarow = new DataRow[list.size()];
		  			for (int i=0;i<list.size();i++) {
		  				MemberLeaveDetail m=list.get(i);
		  				DataRow row = new DataRow();
		  				row.addDataCell(m.getContent(), 1);
		  				row.addDataCell(m.getTitle(), 2);
		  				row.addDataCell(m.getType(), 3);
		  				row.addDataCell(m.getAccountName(), 4);
		  				datarow[i] = row;
		  			}
		  			dataRecord.addDataRow(datarow);
		  		}
		  		String[] columnName={content,title,type,account};
		  		dataRecord.setColumnName(columnName);
		  		dataRecord.setTitle(member_list);
		  		dataRecord.setSheetName(member_list);
		  	}
		  		break;
		  	case 61:
		  	{
		  		String title = ResourceUtil.getString("memberleave.dealleave6.manage_permission");
		  		String type = ResourceUtil.getString("memberleave.dealleave6.type");
			/*
			 * String content = "所属人"; String account =
			 * ResourceUtil.getString("member.leave.account");
			 */
		  		String member_list = ResourceUtil.getString("memberleave.Category.Business");			  		
		  		if (null != list && list.size() > 0) {
		  			DataRow[] datarow = new DataRow[list.size()];
		  			for (int i=0;i<list.size();i++) {
		  				MemberLeaveDetail m=list.get(i);
		  				DataRow row = new DataRow();
		  				row.addDataCell(m.getTitle(), 1);
		  				row.addDataCell(m.getType(), 2);
		  				row.addDataCell(m.getContent(), 3);
		  				row.addDataCell(m.getAccountName(), 4);
		  				datarow[i] = row;
		  			}
		  			dataRecord.addDataRow(datarow);
		  		}
		  		String[] columnName={title,type};
		  		dataRecord.setColumnName(columnName);
		  		dataRecord.setTitle(member_list);
		  		dataRecord.setSheetName(member_list);
		  	}
		  		break;
		  	case 62:
		  	{
		  		String title = ResourceUtil.getString("memberleave.dealleave6.manage_permission");
		  		String type = ResourceUtil.getString("memberleave.dealleave6.type");
			/*
			 * String content = "所属人"; String account =
			 * ResourceUtil.getString("member.leave.account");
			 */
		  		String member_list = ResourceUtil.getString("memberleave.Category.BusinessCap4");			  		
		  		if (null != list && list.size() > 0) {
		  			DataRow[] datarow = new DataRow[list.size()];
		  			for (int i=0;i<list.size();i++) {
		  				MemberLeaveDetail m=list.get(i);
		  				DataRow row = new DataRow();
		  				row.addDataCell(m.getTitle(), 1);
		  				row.addDataCell(m.getType(), 2);
		  				row.addDataCell(m.getContent(), 3);
		  				row.addDataCell(m.getAccountName(), 4);
		  				datarow[i] = row;
		  			}
		  			dataRecord.addDataRow(datarow);
		  		}
		  		String[] columnName={title,type};
		  		dataRecord.setColumnName(columnName);
		  		dataRecord.setTitle(member_list);
		  		dataRecord.setSheetName(member_list);
		  	}
		  		break;
		  	case 7:
		  	{
		  		String title = ResourceUtil.getString("memberleave.dealleave6.manage_permission");
		  		String type = ResourceUtil.getString("memberleave.dealleave6.type");
			/*
			 * String content = "所属人"; String account =
			 * ResourceUtil.getString("member.leave.account");
			 */
		  		String member_list = ResourceUtil.getString("memberleave.Category.reporter");			  		
		  		if (null != list && list.size() > 0) {
		  			DataRow[] datarow = new DataRow[list.size()];
		  			for (int i=0;i<list.size();i++) {
		  				MemberLeaveDetail m=list.get(i);
		  				DataRow row = new DataRow();
		  				row.addDataCell(m.getTitle(), 1);
		  				row.addDataCell(m.getType(), 2);
		  				row.addDataCell(m.getContent(), 3);
		  				row.addDataCell(m.getAccountName(), 4);
		  				datarow[i] = row;
		  			}
		  			dataRecord.addDataRow(datarow);
		  		}
		  		String[] columnName={title,type};
		  		dataRecord.setColumnName(columnName);
		  		dataRecord.setTitle(member_list);
		  		dataRecord.setSheetName(member_list);
		  	}
		  		break;
		  	case 9:
		  	{
		        fi=memberLeaveManager.queryAppLogs(fi, paraMap);
		        List<WebMemberLeaveAppLog> applogList = fi.getData();
		  		String user = ResourceUtil.getString("memberleave.deal.log.user");
		  		String actionType = ResourceUtil.getString("memberleave.deal.log.type");
		  		String actionDesc = ResourceUtil.getString("memberleave.deal.log.desc");
		  		String actionTime = ResourceUtil.getString("memberleave.deal.log.time");
		  		String ipAddress = ResourceUtil.getString("memberleave.deal.log.ip");
		  		String account = ResourceUtil.getString("memberleave.deal.log.account");
		  		//String modelName = "操作模块";
		  		
		  		String member_list = ResourceUtil.getString("memberleave.dealleave9.label");			  		
		  		if (null != applogList && applogList.size() > 0) {
		  			DataRow[] datarow = new DataRow[applogList.size()];
		  			for (int i=0;i<applogList.size();i++) {
		  				WebMemberLeaveAppLog m = applogList.get(i);
		  				DataRow row = new DataRow();
		  				row.addDataCell(m.getUser(), 1);
		  				row.addDataCell(m.getActionType(), 2);
		  				row.addDataCell(m.getActionDesc(), 3);
		  				row.addDataCell(Datetimes.format(m.getActionTime(), Datetimes.datetimeStyle), 4);
		  				row.addDataCell(m.getIpAddress(), 5);
		  				row.addDataCell(m.getAccount(), 6);
		  				//row.addDataCell(m.getModelName(), 7);
		  				datarow[i] = row;
		  			}
		  			dataRecord.addDataRow(datarow);
		  		}
		  		String[] columnName={user,actionType,actionDesc,actionTime,ipAddress,account};
		  		dataRecord.setColumnName(columnName);
		  		dataRecord.setTitle(member_list);
		  		dataRecord.setSheetName(member_list);
		  	}
		  		break;
		  	default:
			  	{
			  		//导出excel文件的国际化
			  		String account = ResourceUtil.getString("member.leave.account");
			  		String type = ResourceUtil.getString("member.leave.type");
			  		String content = ResourceUtil.getString("member.leave.content");
			  		String title = ResourceUtil.getString("member.leave.title");
			  		String member_list = ResourceUtil.getString("member.leave.list");
			  		
			  		if (null != list && list.size() > 0) {
			  			DataRow[] datarow = new DataRow[list.size()];
			  			for (int i=0;i<list.size();i++) {
			  				MemberLeaveDetail m=list.get(i);
			  				DataRow row = new DataRow();
			  				row.addDataCell(m.getTitle(), 1);
			  				row.addDataCell(m.getContent(), 2);
			  				row.addDataCell(m.getType(), 3);
			  				row.addDataCell(m.getAccountName(), 4);
			  				datarow[i] = row;
			  			}
			  			dataRecord.addDataRow(datarow);
			  		}
			  		String[] columnName={title,content,type,account};
			  		dataRecord.setColumnName(columnName);
			  		dataRecord.setTitle(member_list);
			  		dataRecord.setSheetName(member_list);
			  	}
		}

        return dataRecord;
    }

}
