/**
 *
 */
package com.seeyon.ctp.organization.selectpeople.manager;

import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_NAME;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_id;

import java.util.Date;
import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.Strings;

/**
 * @author tanmf
 *
 */
public class SelectPeoplePanel4AdminImpl extends AbstractSelectPeoplePanel {

    private OrgManager orgManager;

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    @Override
    public String getType() {
        return "Admin";
    }

    public InitCacheType getInitCacheType(){
        return InitCacheType.Init_Global;
    }

    @Override
    public Date getLastModifyTimestamp(Long accountId) throws BusinessException {
        return orgManager.getModifiedTimeStamp(accountId);
    }

    @Override
    public String getJsonString(long memberId, long loginAccountId, String extParameters) throws BusinessException {
        StringBuilder a = new StringBuilder();
        a.append("[");

        if((Boolean)OrgHelper.getSysFlag(SysFlag.org_has_systemAdmin)){
            V3xOrgMember SystemAdmin = this.orgManager.getSystemAdmin();
            if(null!=SystemAdmin){
            	a.append("{");
            	a.append(TOXML_PROPERTY_id).append(":\"").append(SystemAdmin.getId()).append("\"");
            	a.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(ResourceUtil.getString("org.account_form.systemAdminName.value")).append("\"");
            	a.append(",C:\"").append(OrgConstants.Role_NAME.SystemAdmin.name()).append("\"");
            	a.append(",A:\"").append(SystemAdmin.getOrgAccountId()).append("\"");
            	a.append("},");
            }
        }

        if((Boolean)OrgHelper.getSysFlag(SysFlag.org_has_auditAdmin)){
            V3xOrgMember AuditAdmin = this.orgManager.getAuditAdmin();
            if(null!=AuditAdmin){
            	a.append("{");
            	a.append(TOXML_PROPERTY_id).append(":\"").append(AuditAdmin.getId()).append("\"");
            	a.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(ResourceUtil.getString("org.auditAdminName.value")).append("\"");
            	a.append(",C:\"").append(OrgConstants.Role_NAME.AuditAdmin.name()).append("\"");
            	a.append(",A:\"").append(AuditAdmin.getOrgAccountId()).append("\"");
            	a.append("},");
            }
        }

        if((Boolean)OrgHelper.getSysFlag(SysFlag.org_has_groupAdmin)){
            V3xOrgMember GroupAdmin = this.orgManager.getGroupAdmin();
            if(null!=GroupAdmin){
            	a.append("{");
            	a.append(TOXML_PROPERTY_id).append(":\"").append(GroupAdmin.getId()).append("\"");
            	a.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(ResourceUtil.getString("org.account_form.groupAdminName.value" + Strings.escapeNULL((String)SysFlag.EditionSuffix.getFlag(), ""))).append("\"");
            	a.append(",C:\"").append(OrgConstants.Role_NAME.GroupAdmin.name()).append("\"");
            	a.append(",A:\"").append(GroupAdmin.getOrgAccountId()).append("\"");
            	a.append("},");
            }
        }

        String subfix = ResourceUtil.getString( "org.account_form.adminName.value");

        List<V3xOrgMember> members = orgManager.getMembersByRole(null, OrgConstants.Role_NAME.AccountAdministrator.name());
        for(V3xOrgMember member : members){
            if(member == null || !member.isValid()){
                continue;
            }

            V3xOrgAccount account = orgManager.getAccountById(member.getOrgAccountId());
            if(account == null || !account.isValid()){
                continue;
            }

            a.append("{");
            a.append(TOXML_PROPERTY_id).append(":\"").append(member.getId()).append("\"");
            a.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(account.getName() + subfix).append("\"");
            a.append(",C:\"").append(OrgConstants.Role_NAME.AccountAdministrator.name()).append("\"");
            a.append(",A:\"").append(account.getOrgAccountId()).append("\"");
            a.append("},");
        }

        String s = a.toString();

        if(s.endsWith(",")){
            s = s.substring(0, s.length() - 1);
        }

        return s + "]";
    }

}
