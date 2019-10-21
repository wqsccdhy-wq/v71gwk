package com.seeyon.ctp.organization.selectpeople.manager;

import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_NAME;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.ORGENT_TYPE;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.manager.BusinessOrgManagerDirect;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.po.OrgRole;
import com.seeyon.ctp.util.Strings;

@ProcessInDataSource(name = DataSourceName.BASE)
public class SelectPeoplePanel4BusinessRoleImpl extends AbstractSelectPeoplePanel {
    
    private OrgManager orgManager;
    private OrgDao orgDao;
    private BusinessOrgManagerDirect businessOrgManagerDirect;
    
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    
    public void setBusinessOrgManagerDirect(
			BusinessOrgManagerDirect businessOrgManagerDirect) {
		this.businessOrgManagerDirect = businessOrgManagerDirect;
	}

	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}

	@Override
    public String getType() {
        return ORGENT_TYPE.BusinessRole.name();
    }

    @Override
    public Date getLastModifyTimestamp(Long accountId) throws BusinessException {
        return orgManager.getModifiedTimeStamp(accountId);
    }
    
    @Override
    public String getJsonString(long memberId, long accountId, String extParameters) throws BusinessException {
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
        StringBuilder o = new StringBuilder();
        o.append("[");

        int i = 0;
        for(V3xOrgAccount account : accountList) {
        	List<OrgRole> list = orgDao.getAllRolePO(account.getId(), true, null, null);
        	for(OrgRole r : list){
        		V3xOrgRole role = new V3xOrgRole(r);
        		if(OrgConstants.ExternalType.Interconnect4.ordinal() != role.getExternalType()){
        			continue;
        		}
        		if(i > 0){
        			o.append(",");
        		}
        		OrgConstants.Role_NAME roleName = null;
        		try {
        			roleName =  OrgConstants.Role_NAME.valueOf(role.getCode());
        		}
        		catch (Exception e) {
        		}
        		
        		o.append("{");
        		
        		if(role.getBond() != OrgConstants.ROLE_BOND.DEPARTMENT.ordinal()){
        			continue;
        		}
        		o.append(TOXML_PROPERTY_id).append(":\"").append(role.getId()).append("\"").append(",");
        		String code = "";
        		if(roleName != null) {
        			code = role.getCode();
        		}
        		o.append("C").append(":\"").append(code).append("\"").append(",");
        		o.append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(role.getShowName())) .append("\"").append(",");
        		o.append("T:").append(role.getType()).append(",");
        		//业务线名称前缀，展示格式：业务线简称（所属单位简称）
        		String ps = "";
        		if(isGroupVer){
        			ps = Strings.isNotBlank(account.getShortName()) ? account.getShortName() + "(" +currentAccount.getShortName() + ")" : account.getName() + "(" +currentAccount.getShortName() + ")";
        		}else{
        			ps = Strings.escapeJavascript(Strings.isNotBlank(account.getShortName()) ? account.getShortName() : account.getName());
        		}
        		o.append("PS").append(":\"").append(Strings.escapeJavascript(ps)).append("\"").append(",");
        		o.append("A:\"").append(accountId).append("\",");
        		o.append("B:\"").append(role.getOrgAccountId()).append("\"");
        		o.append("}");
        		i++;
        	}
        }
        o.append("]");

        return o.toString();
    }

}
