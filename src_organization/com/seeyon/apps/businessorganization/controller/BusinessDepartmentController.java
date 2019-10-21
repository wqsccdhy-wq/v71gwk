package com.seeyon.apps.businessorganization.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
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
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.CompareSortRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.inexportutil.DataUtil;
import com.seeyon.ctp.organization.manager.BusinessOrgManagerDirect;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.util.OrgTree;
import com.seeyon.ctp.organization.util.OrgTreeNode;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;


/**
 * <p>
 * Title: T2组织模型部门维护控制器
 * </p>
 * <p>
 * Description: 主要针对单位组织进行维护功能
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: seeyon.com
 * </p>
 * 
 * @version CTP2.0
 */
@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator,Role_NAME.BusinessOrganizationManager})
public class BusinessDepartmentController extends BaseController {
	private final static Log   log = LogFactory.getLog(BusinessDepartmentController.class);

	private OrgManager       orgManager;
	private OrgManagerDirect orgManagerDirect;
    private BusinessOrgManagerDirect businessOrgManagerDirect;
    private FileToExcelManager fileToExcelManager;
    private OrgCache           orgCache;

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }

    public void setBusinessOrgManagerDirect(
			BusinessOrgManagerDirect businessOrgManagerDirect) {
		this.businessOrgManagerDirect = businessOrgManagerDirect;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}

	public ModelAndView showDepartmentFrame(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String style = request.getParameter("style");
		ModelAndView result = new ModelAndView();
		Long businessId = Long.valueOf(request.getParameter("id"));
		V3xOrgAccount account = orgManager.getAccountById(businessId);
		if ("tree".equals(style)) {
			result = new ModelAndView("apps/businessorganization/department/treeIndex");
			
			result.addObject("name", account.getName());
			result.addObject("shortname", account.getShortName());
			result.addObject("code", account.getCode());
			result.addObject("sortId", account.getSortId());
			result.addObject("enabled", account.getEnabled() ? ResourceUtil.getString("common.state.normal.label") : ResourceUtil.getString("common.state.invalidation.label"));
			result.addObject("ispublic", account.getIsPublic() ? ResourceUtil.getString("org.business.public") : ResourceUtil.getString("org.business.private"));
			result.addObject("description", account.getDescription());
			
			String managerName = "";
			EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective0Id, businessId);
			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective1Id, OrgConstants.BUSINESS_ORGANIZATION_ROLE_ID);
			
			List<V3xOrgRelationship> list = orgManager.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, null, businessId, objectiveIds);
			Collections.sort(list, CompareSortRelationship.getInstance());
			for(V3xOrgRelationship rel : list){
				String type = rel.getObjective5Id();
				Long id = rel.getSourceId();
				V3xOrgEntity entity = orgManager.getEntity(type, id);
				if(entity == null || !entity.isValid()){
					continue;
				}
				if(Strings.isBlank(managerName)){
					managerName = entity.getName();
				}else{
					managerName = managerName + "," + entity.getName();
				}
			}
			result.addObject("managerName", managerName);
			
		} else if ("list".equals(style)) {
			result = new ModelAndView("apps/businessorganization/department/listIndex");
		}
		String businessName = "";
		if(account != null){
			businessName = account.getName();
		}
		
		Long ownerAccountId = account.getSuperior();//业务线所属的单位
		result.addObject("ownerAccountId", ownerAccountId);
		result.addObject("businessName", businessName);
		result.addObject("enable", account.getEnabled());
				
		return result;
	}
    
	/**
	 * 进入部门岗位管理编辑方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView detailDeptMembers(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("apps/businessorganization/department/detailDeptMembers");
		return result;
	}
	
	public ModelAndView exportDepartments(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        orgCache.setOrgExportFlag(true);
        User u=AppContext.getCurrentUser();
        if(u==null){
            return null;
        }
        if(DataUtil.doingImpExp(u.getId())){
            return null;
        }
        
        String listname = "businessDepartments_";
        listname+=u.getLoginName();
        
        DataUtil.putImpExpAction(u.getId(), "export");
        DataRecord dataRecord=null;
        try{
            dataRecord = exportDepartments(request, 
                    response, fileToExcelManager, orgManagerDirect);
        }catch(Exception e){
            DataUtil.removeImpExpAction(u.getId());
            throw e;
        }
        DataUtil.removeImpExpAction(u.getId());
        try {
            OrgHelper.exportToExcel(request, response, fileToExcelManager, listname, dataRecord);
        } catch (Exception e) {
            log.error(e);
        } finally {
            orgCache.setOrgExportFlag(false);
        }
        return null;
	}
	
	/**
     * 批量导出部门
     * @param request
     * @param metadataManager
     * @param response
     * @param fileToExcelManager
     * @param orgManagerDirect
     * @param searchManager
     * @param path
     * @return
     * @throws Exception
     */
    public DataRecord exportDepartments(HttpServletRequest request,
            HttpServletResponse response,FileToExcelManager fileToExcelManager
            ,OrgManagerDirect orgManagerDirect) throws Exception{
        Long accountId = Long.valueOf(request.getParameter("accountId"));
        String accountIdStr = request.getParameter("accountId");
        if (Strings.isNotBlank(accountIdStr)) {
            accountId = Long.valueOf(accountIdStr);
        }
        DataRecord dataRecord = new DataRecord();
        
        V3xOrgDepartment dept = null;
        String dep_list = ResourceUtil.getString("org.dept_role.list");
        List<V3xOrgDepartment> departmentlist = businessOrgManagerDirect.getChildDepartments(accountId, false);
        
        List<OrgTreeNode> orgTreeNodes = OrgTree.changeEnititiesToOrgTreeNodes(departmentlist);
        V3xOrgAccount currentAccont =orgManager.getAccountById(accountId);
        String accountPath =currentAccont.getPath();
        //TODO：需注意是否是有效树
        OrgTree orgTree = new OrgTree(orgTreeNodes,accountPath);
        OrgTreeNode rootNode = orgTree.getRoot();
        List<OrgTreeNode> orgTreeMidList = new ArrayList<OrgTreeNode>();
        try {
            orgTreeMidList= rootNode.traversal();
        } catch (Exception e) {
            log.error("构造树error",e);
        }
        
        //导出excel文件的国际化
        String dep_Name = ResourceUtil.getString("org.dept.grade");
        String dep_Code = ResourceUtil.getString("org.business.code.label");
        String dep_Level = ResourceUtil.getString("org.dept.level");
        String dep_SortId = ResourceUtil.getString("common.sort.label");
        String dep_desc = ResourceUtil.getString("common.description.label");
        
        String[] columnName = {};
        if (null != orgTreeMidList && orgTreeMidList.size() > 0) {
            DataRow[] datarow = new DataRow[orgTreeMidList.size()];
            //树深度
            int treeDeep = orgTree.getTreeDeep();
            //显示部门角色
            List<V3xOrgRole> rolelist = orgManager.getAllDepRoles(accountId);
            int columnsize = treeDeep+4+(rolelist.size());
            columnName = new String[columnsize];
            for (int i = 0; i < orgTreeMidList.size(); i++) {
                DataRow row = new DataRow();
                OrgTreeNode currentNode= orgTreeMidList.get(i);
                dept = (V3xOrgDepartment)currentNode.getObj();
                String deptCode ="";
                String deptName = "";
                if(log.isDebugEnabled())
                    log.debug(dept.getName());
                if(dept!=null){
                    deptName = dept.getName();
                    deptCode=dept.getCode();
                }else{
                	continue;
                }
                int deptDeep = (dept.getPath().length()-accountPath.length())/4;
                
                V3xOrgDepartment tempDept = dept;
                //部门名称(各级别)
                String[] deptsName = new String[treeDeep];
                for (int l =treeDeep; l>0; l--) {
                    columnName[l-1]=l+dep_Name;
                    if(l<deptDeep && tempDept!=null){
                        V3xOrgDepartment parentDept= orgManager.getDepartmentByPath(tempDept.getParentPath());
                        deptsName[l-1]=parentDept.getName();
                        tempDept=parentDept;
                    }else if(l>deptDeep){
                        deptsName[l-1]="";
                    }else{
                        deptsName[l-1]=deptName;
                    }
                }
                for(int l =0; l <treeDeep; l++){
                    row.addDataCell(deptsName[l], 1);
                }
                int index = treeDeep;
                columnName[index++]=dep_Code;
                columnName[index++]=dep_Level;
                columnName[index++]=dep_SortId;
                columnName[index++]=dep_desc;
                row.addDataCell(deptCode, 1);
                row.addDataCell(String.valueOf((deptDeep)), 1);
                row.addDataCell(String.valueOf((dept.getSortId())), 1);
                row.addDataCell(dept.getDescription(), 1);
                
                for (int j = 0; j < rolelist.size(); j++) {
                    V3xOrgRole tempRole = rolelist.get(j);
                    List<V3xOrgMember> memberlist = orgManager.getMembersByRole(dept.getId(), rolelist.get(j).getId());
                    columnName[index++]=tempRole.getShowName();
                    row.addDataCell(memberListToString(memberlist), 1);
                }
                datarow[i] = row;
            }

            try {
                dataRecord.addDataRow(datarow);
            } catch (Exception e) {
                log.error("eeror",e);
            }
        }
        dataRecord.setColumnName(columnName);
        dataRecord.setTitle(dep_list);
        dataRecord.setSheetName(dep_list);
        return dataRecord;
    }
    
    private String memberListToString(List<V3xOrgMember> memberlist){
        StringBuilder sb = new StringBuilder();
        String memberString = "";
        for(V3xOrgMember m : memberlist){
            sb.append(m.getName()).append('、');
        }
        memberString = sb.toString();
        if(memberString.length()>0){
            return memberString.substring(0, memberString.length()-1);
        }else{
            return "";
        }
    }
	
}
