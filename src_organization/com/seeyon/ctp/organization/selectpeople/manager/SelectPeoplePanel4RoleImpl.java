/**
 * 
 */
package com.seeyon.ctp.organization.selectpeople.manager;

import java.util.Date;
import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants.ORGENT_TYPE;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.manager.OrgManager;

/**
 * 
 * @author tanmf
 */
public class SelectPeoplePanel4RoleImpl extends AbstractSelectPeoplePanel {
    
    private OrgManager orgManager;
    
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    
    @Override
    public String getType() {
        return ORGENT_TYPE.Role.name();
    }

    @Override
    public Date getLastModifyTimestamp(Long accountId) throws BusinessException {
        return orgManager.getModifiedTimeStamp(accountId);
    }

    @Override
    public String getJsonString(long memberId, long accountId, String extParameters) throws BusinessException {
        List<V3xOrgRole> allRoles = this.orgManager.getAllRoles(accountId);
        //预置的所有角色。
        String roleNames="|";
    	for(Role_NAME roleName:Role_NAME.values()){
    		roleNames=roleNames+roleName+"|";
    	}
        StringBuilder a = new StringBuilder();
        a.append("[");
        
        int i = 0;
        for (V3xOrgRole role : allRoles) {
            if(role.getStatus() != 1){
                continue;
            }
            
            if(i++ != 0){
            	//集团自定义角色
            	if(role.getType()==V3xOrgEntity.ROLETYPE_RELATIVEROLE && roleNames.indexOf("|"+role.getCode()+"|")<0){
            		//type=4,暂存为集团自定义角色，用于选人界面
            		role.setType(4);
            	}
                a.append(",");
            }
            
            role.toJsonString(a);
        }
        
        a.append("]");
        
        return a.toString();
    }

}
