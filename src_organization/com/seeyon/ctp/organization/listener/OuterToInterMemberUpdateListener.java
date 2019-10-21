package com.seeyon.ctp.organization.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.event.UpdateMemberEvent;
import com.seeyon.ctp.util.annotation.ListenEvent;

/**
 * 外部人员转内部人员，删除其工作范围监听
 * @author MrBean
 *
 */
public class OuterToInterMemberUpdateListener {
    
    private final static Log   logger = LogFactory.getLog(OuterToInterMemberUpdateListener.class);
    
    @ListenEvent(event = UpdateMemberEvent.class, async = true)
    public void cleanOuterWorkscope(UpdateMemberEvent event) {
        V3xOrgMember oldMember = event.getOldMember();
        V3xOrgMember newMember = event.getMember();
        // 外部人员转内部人员
        if((!oldMember.getIsInternal()) && newMember.getIsInternal()) {
            OrgDao orgDao = (OrgDao) AppContext.getBean("orgDao");
            orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.External_Workscope.name(), newMember.getId(), null, null);
        }
    }
}
