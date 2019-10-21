package com.seeyon.ctp.organization.memberleave.manager;

import java.sql.Timestamp;

import com.seeyon.ctp.common.exceptions.BusinessException;

public abstract class AbstractMemberLeaveData implements MemberLeaveDataInterface {
    
  //代理协同的结束时间
    protected static final Timestamp AgentEndTime = Timestamp.valueOf("9999-12-31 23:59:59");
    
    public final String getAppKey(){
        String name = this.getClass().getSimpleName();
        int dao = name.indexOf('$');
        if(dao > 0){
            name = name.substring(0, dao);
        }
        
        return name;
    }
    
    public boolean isEnabled(){
        return true;
    }
    
    public boolean isMustSetAgentMember(long leaveMemberId) throws BusinessException{
        return false;
    }

}
