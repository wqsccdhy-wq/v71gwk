package com.seeyon.ctp.organization.listener;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.event.DeleteConCurrentPostEvent;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.annotation.ListenEvent;

/**
 * 删除兼职监听事件，如果这个兼职是在兼职单位的最后一条兼职信息，则删除所有兼职角色
 * @author MrBean
 *
 */
public class DelConPostListener {
    
    private final static Log   logger = LogFactory.getLog(DelConPostListener.class);
    
    @ListenEvent(event = DeleteConCurrentPostEvent.class, async = true)
    public void cleanOldDeptRolesForMember(DeleteConCurrentPostEvent event) {
        V3xOrgRelationship rel = event.getRel();
        Long memberId = rel.getSourceId();
        Long conAccountId = rel.getOrgAccountId();
        OrgManager orgManager = OrgHelper.getOrgManager();
        
        try {
            List<V3xOrgRelationship> memberPost = orgManager.getMemberPostRelastionships(memberId, conAccountId, null);
            if(memberPost.size() == 0) {
                OrgDao orgDao = (OrgDao) AppContext.getBean("orgDao");
                orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Role.name(), memberId, conAccountId, null);
            }
        } catch (BusinessException e) {
            logger.error("删除兼职角色失败!", e);
        }
        
    }
}
