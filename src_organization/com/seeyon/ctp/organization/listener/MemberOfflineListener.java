package com.seeyon.ctp.organization.listener;

import com.seeyon.ctp.common.constants.Constants.LoginOfflineOperation;
import com.seeyon.ctp.login.online.OnlineRecorder;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.event.DeleteMemberEvent;
import com.seeyon.ctp.organization.event.UpdateMemberEvent;
import com.seeyon.ctp.util.annotation.ListenEvent;

/**
 * 人员，离职、停用、删除等处于无法登陆状态时发送通知被踢下线的监听
 * @author lilong
 * @version CTP2.0
 * @date 2013-01-25
 *
 */
public class MemberOfflineListener {

    @ListenEvent(event = UpdateMemberEvent.class, async = true)
    public void moveUnableMemberOffline(UpdateMemberEvent event) {
        V3xOrgMember member = event.getMember();
        if (!member.isValid()) {
            OnlineRecorder.moveToOffline(member.getLoginName(), LoginOfflineOperation.adminKickoff);
        }
    }

    @ListenEvent(event = DeleteMemberEvent.class, async = true)
    public void moveDelMemberOffline(DeleteMemberEvent event) {
        V3xOrgMember member = event.getMember();
        OnlineRecorder.moveToOffline(member.getLoginName(), LoginOfflineOperation.adminKickoff);
    }

}
