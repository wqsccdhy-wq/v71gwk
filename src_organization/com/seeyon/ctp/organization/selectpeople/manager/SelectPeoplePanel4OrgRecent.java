package com.seeyon.ctp.organization.selectpeople.manager;

import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_id;

import java.util.Date;
import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgIndexManager;
import com.seeyon.ctp.organization.manager.OrgManager;

/**
 * 最近联系人页签
 * @author MrBean
 *
 */
public class SelectPeoplePanel4OrgRecent extends AbstractSelectPeoplePanel {

    private OrgIndexManager orgIndexManager;
    private OrgManager orgManager;
    public void setOrgIndexManager(OrgIndexManager orgIndexManager) {
        this.orgIndexManager = orgIndexManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    
    @Override
    public String getType() {
        return "OrgRecent";
    }

    @Override
    public Date getLastModifyTimestamp(Long accountId) throws BusinessException {
        return null;//每次都返回null，实时返回，不用缓存
    }

    @Override
    public String getJsonString(long memberId, long accountId, String extParameters) throws BusinessException {
        List<V3xOrgEntity> recentList = orgIndexManager.getRecentData(memberId, "" ,false);

        StringBuilder a = new StringBuilder();
        a.append("[");

        int i = 0;
        if (recentList != null) {
            for (V3xOrgEntity member : recentList) {
                if (i++ != 0) {
                    a.append(",");
                }
                
                member2JsonString(a, accountId, (V3xOrgMember) member);
            }
        }

        a.append("]");
        return a.toString();
    }
    
    private void member2JsonString(StringBuilder o, long loginAccountId, V3xOrgMember member) {
        o.append("{");
        o.append(TOXML_PROPERTY_id).append(":\"").append(member.getId()).append("\"");
        
        makeE(o, member, loginAccountId, true);
        
        o.append("}");
    }

}
