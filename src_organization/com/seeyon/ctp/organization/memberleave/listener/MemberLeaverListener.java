/**
 * 
 */
package com.seeyon.ctp.organization.memberleave.listener;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.event.UpdateMemberEvent;
import com.seeyon.ctp.organization.memberleave.manager.MemberLeaveManager;
import com.seeyon.ctp.util.annotation.ListenEvent;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * @date 2013-2-21 
 */
public class MemberLeaverListener {
    
    private MemberLeaveManager memberLeaveManager;
    
    public void setMemberLeaveManager(MemberLeaveManager memberLeaveManager) {
        this.memberLeaveManager = memberLeaveManager;
    }

    /**
     * 从离职到在职，把代理交接取消掉
     * 
     * @param eventObject
     */
    @ListenEvent(event=UpdateMemberEvent.class)
    public void listenEventMemberReturnEventLister(UpdateMemberEvent eventObject) throws BusinessException{
        int oldState = eventObject.getOldMember().getState();
        int nowState = eventObject.getMember().getState();
        
        long leaveMemberId = eventObject.getMember().getId();
        
        if(oldState == OrgConstants.MEMBER_STATE.RESIGN.ordinal() && nowState == OrgConstants.MEMBER_STATE.ONBOARD.ordinal()){
            this.memberLeaveManager.transMemberReturn(leaveMemberId);
        }
    }
}
