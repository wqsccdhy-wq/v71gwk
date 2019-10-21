package com.seeyon.ctp.organization.selectpeople.manager;

import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_NAME;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_id;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_parentId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.ORGENT_TYPE;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.BusinessOrgManagerDirect;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OuterWorkerAuthUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;

@ProcessInDataSource(name = DataSourceName.BASE)
public class SelectPeoplePanel4BusinessDepartmentImpl extends AbstractSelectPeoplePanel {
    
    private OrgManager orgManager;
    private BusinessOrgManagerDirect businessOrgManagerDirect;
    
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    
    public void setBusinessOrgManagerDirect(
			BusinessOrgManagerDirect businessOrgManagerDirect) {
		this.businessOrgManagerDirect = businessOrgManagerDirect;
	}

	@Override
    public String getType() {
        return ORGENT_TYPE.BusinessDepartment.name();
    }

    @Override
    public Date getLastModifyTimestamp(Long accountId) throws BusinessException {
        return orgManager.getModifiedTimeStamp(accountId);
    }
    
    @Override
    public String getJsonString(long memberId, long accountId, String extParameters) throws BusinessException {
    	V3xOrgMember currentMember = orgManager.getMemberById(memberId);
    	Long createrId = null;
    	V3xOrgAccount currentAccount = orgManager.getAccountById(accountId);
    	if(currentAccount.getIsGroup()){
    		createrId = OrgConstants.GROUP_ADMIN_ID;
    	}else{
    		V3xOrgMember member = orgManager.getAdministrator(accountId);
    		if(member != null){
    			createrId = member.getId();
    		}
    	}
    	
    	List<V3xOrgAccount> accountList = new ArrayList<V3xOrgAccount>();
    	if(createrId != null){
    		accountList = businessOrgManagerDirect.getAccountList(createrId, true);
    	}
    	
    	boolean isGroupVer = (Boolean) (SysFlag.sys_isGroupVer.getFlag());
        StringBuilder a = new StringBuilder();
        a.append("[");
        int i = 0;
        for(V3xOrgAccount account : accountList) {
        	if(i > 0){
        		a.append(",");
        	}
            a.append("{");
            a.append(TOXML_PROPERTY_id).append(":\"").append(account.getId()).append("\"").append(",");
            a.append(TOXML_PROPERTY_parentId).append(":\"-1\"").append(",");
            a.append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(account.getName())).append("\"").append(",");
            a.append("IP").append(":\"").append(account.getIsPublic()).append("\"").append(",");//是否公开
            StringBuilder  businessMembers = new StringBuilder();//可见业务线的人,   用于业务线可见的判断（业务线内的人+ 业务线内的部门角色下的人）
            if(!account.getIsPublic() && currentMember.getIsInternal()){
            	List<V3xOrgMember> members = businessOrgManagerDirect.getAllMembers(account.getId());
            	for(int index = 0; index<members.size(); index ++){
            		if(index > 0){
            			businessMembers.append(",");
            		}
            		businessMembers.append(members.get(index).getId());
            	}
            	
            	
    			List<V3xOrgRelationship> reflist = orgManager.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, null, account.getId(), null);
    			for (V3xOrgRelationship v3xOrgRelationship : reflist) {
    				Long roleId = v3xOrgRelationship.getObjective1Id();
    				if(OrgConstants.BUSINESS_ORGANIZATION_ROLE_ID.equals(roleId)) {
    					continue;
    				}
    				Long roleMemberId = v3xOrgRelationship.getSourceId();
    				if(businessMembers.toString().indexOf(roleMemberId.toString()) == -1){
    					businessMembers.append(","+roleMemberId);
    				}
    			}
    			
            }
            a.append("BM").append(":\"").append(businessMembers.toString()).append("\"");
            a.append("}");
            
            List<V3xOrgDepartment> depts = businessOrgManagerDirect.getChildDepartments(account.getId(), false);

            List<V3xOrgMember> outerWorkerMembers = null;
            if(currentMember.isV5External()) {//编外人员，只能看到工作范围内的人所在的多维组织部门
            	outerWorkerMembers = (List<V3xOrgMember>) OuterWorkerAuthUtil.getCanAccessMembers(memberId, null, accountId, orgManager);
            }
            for (V3xOrgDepartment dept : depts) {
            	List<V3xOrgMember> members = businessOrgManagerDirect.getMembersByDepartment(dept.getId(), true);
            	if(currentMember.isV5External()) {//编外人员，只能看到工作范围内的人所在的多维组织部门
            		List<V3xOrgMember> intersectionMembers = Strings.getIntersection(members, outerWorkerMembers);
            		if(Strings.isEmpty(intersectionMembers)) {
            			continue;
            		}
            	}
    			a.append(",");
    			a.append("{");
    			a.append(TOXML_PROPERTY_id).append(":\"").append(dept.getId()).append("\"").append(",");
    			a.append("P").append(":\"").append(dept.getPath()).append("\"").append(",");
    			a.append(TOXML_PROPERTY_parentId).append(":\"").append(dept.getSuperior()).append("\"").append(",");
    			a.append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(dept.getName())).append("\"").append(",");
                //业务线名称前缀，展示格式：业务线简称（所属单位简称）
        		String ps = "";
        		if(isGroupVer){
        			ps = Strings.isNotBlank(account.getShortName()) ? account.getShortName() + "(" +currentAccount.getShortName() + ")" : account.getName() + "(" +currentAccount.getShortName() + ")";
        		}else{
        			ps = Strings.escapeJavascript(Strings.isNotBlank(account.getShortName()) ? account.getShortName() : account.getName());
        		}
                a.append("PS").append(":\"").append(Strings.escapeJavascript(ps)).append("\"").append(",");
    			a.append("B").append(":\"").append(dept.getOrgAccountId()).append("\"").append(",");//所在业务线单位  businessId
    			a.append("A").append(":\"").append(currentAccount.getId()).append("\"").append(",");//业务线所属单位  accountId
    			
    			StringBuilder b = new StringBuilder();
    			b.append("[");
    			int j = 0;
    			for(V3xOrgMember member : members){
    	        	if(j > 0){
    	        		b.append(",");
    	        	}
    	            b.append("{");
    	            b.append(TOXML_PROPERTY_id).append(":\"").append(member.getId()).append("\"").append(",");
    	            b.append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(member.getName())).append("\"").append(",");
    	            b.append("S").append(":\"").append(member.getSortId()).append("\"").append(",");
    	            b.append("P").append(":\"").append(member.getOrgPostId()).append("\"").append(",");
    	            b.append("A").append(":\"").append(member.getOrgAccountId()).append("\"").append(",");
    	            String dn = "";
    	            String dfn = "";
    	            V3xOrgDepartment d = orgManager.getDepartmentById(member.getOrgDepartmentId());
    	            if(d != null){
    	            	dn = d.getName();
    	            	dfn = d.getWholeName();
    	            }
    	            b.append("DN").append(":\"").append(Strings.escapeJavascript(dn)).append("\"").append(",");
    	            b.append("DFN").append(":\"").append(Strings.escapeJavascript(dfn)).append("\"").append(",");
    	            b.append("}");
    	            j++;
    			}
    			b.append("]");
    			a.append("M").append(":").append(b.toString());
    			a.append("}");
            }
            i++;
        }
        a.append("]");

        return a.toString();
    }
    
    /**
     * 针对不同人员 获取 人员的业务线访问权限的数据（已经校验好的数据，前端校验太复杂）
     */
    @Override
    public String getAdditionalJsonString(long memberId, long accountId, String extParameters) throws BusinessException {
    	V3xOrgMember currentUser  = orgManager.getMemberById(memberId);
    	//我在能看到的业务线
    	//我能看到的业务线人员: 业务线上，在同一个业务部门下的人之间可访问，业务部门间的人员互访受职务级别访问控制。
    	Long createrId = null;
    	V3xOrgAccount currentAccount = orgManager.getAccountById(accountId);
    	if(currentAccount.getIsGroup()){
    		createrId = OrgConstants.GROUP_ADMIN_ID;
    	}else{
    		V3xOrgMember member = orgManager.getAdministrator(accountId);
    		if(member != null){
    			createrId = member.getId();
    		}
    	}
    	
    	List<V3xOrgAccount> accountList = new ArrayList<V3xOrgAccount>();
    	if(createrId != null){
    		accountList = businessOrgManagerDirect.getAccountList(createrId, true);
    	}
    	
        StringBuilder a = new StringBuilder();
        a.append("[");

        int i = 0;
        for(V3xOrgAccount account : accountList) {
        	if(i > 0){
        		a.append(",");
        	}
            a.append("{");
            a.append(TOXML_PROPERTY_id).append(":\"").append(account.getId()).append("\"").append(",");
            
            String accessBusinessMembersIds = "";//业务线内可见的人
            if(currentUser.getIsInternal()) {
            	List<V3xOrgDepartment> myDepts = new UniqueList<V3xOrgDepartment>();
            	List<V3xOrgDepartment> otherDepts = businessOrgManagerDirect.getChildDepartments(account.getId(), false);
            	
            	List<V3xOrgDepartment>  depts = new UniqueList<V3xOrgDepartment>();
            	depts.addAll(otherDepts);
            	
            	for (V3xOrgDepartment dept : depts) {
            		if(myDepts.contains(dept)) continue;
            		List<V3xOrgMember> deptMembers = businessOrgManagerDirect.getMembersByDepartment(dept.getId(), true);
            		if(deptMembers.contains(currentUser)){//当前人员在这个业务线部门下，那就能看到这个业务线部门及其及部门下的所有人员。
            			List<V3xOrgDepartment> childDepts = businessOrgManagerDirect.getChildDepartments(dept.getId(), false);
            			myDepts.add(dept);
            			myDepts.addAll(childDepts);
            			
            			otherDepts.remove(dept);
            			otherDepts.removeAll(childDepts);
            			
            		}
            	}
            	
            	for (V3xOrgDepartment dept : myDepts) {
            		List<V3xOrgMember> deptMembers = businessOrgManagerDirect.getMembersByDepartment(dept.getId(), true);
            		for(V3xOrgMember m : deptMembers){
            			Long id = m.getId();
            			if(accessBusinessMembersIds.indexOf(id.toString()) < 0){
            				if(Strings.isNotBlank(accessBusinessMembersIds)){
            					accessBusinessMembersIds = accessBusinessMembersIds + ",";
            				}
            				accessBusinessMembersIds = accessBusinessMembersIds + id.toString();
            			}
            		}
            	}
            	
            	for (V3xOrgDepartment dept : otherDepts) {
            		List<V3xOrgMember> deptMembers = businessOrgManagerDirect.getMembersByDepartment(dept.getId(), true);
            		for(V3xOrgMember m : deptMembers){
            			Long id = m.getId();
            			if(accessBusinessMembersIds.indexOf(id.toString()) < 0){
            				if(OrgHelper.checkLevelScope(memberId, id)){
                				if(Strings.isNotBlank(accessBusinessMembersIds)){
                					accessBusinessMembersIds = accessBusinessMembersIds + ",";
                				}
                				accessBusinessMembersIds = accessBusinessMembersIds + id.toString();
            				}
            			}
            		}
            	}
            }else if(currentUser.isV5External()) {
            	List<V3xOrgMember> outerWorkerMembers = (List<V3xOrgMember>) OuterWorkerAuthUtil.getCanAccessMembers(memberId, null, accountId, orgManager);
            	for(V3xOrgMember m : outerWorkerMembers) {
      				if(Strings.isNotBlank(accessBusinessMembersIds)){
    					accessBusinessMembersIds = accessBusinessMembersIds + ",";
    				}
    				accessBusinessMembersIds = accessBusinessMembersIds + m.getId().toString();
            	}
            }
            a.append("ABM").append(":\"").append(accessBusinessMembersIds).append("\"");//业务线内可见的人的id集合
			a.append("}");
            
            i++;
        }
        a.append("]");

        return a.toString();
    }

}
