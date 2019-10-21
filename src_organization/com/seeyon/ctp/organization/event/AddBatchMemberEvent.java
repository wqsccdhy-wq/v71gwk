package com.seeyon.ctp.organization.event;

import java.util.List;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgMember;

public class AddBatchMemberEvent extends Event {
    
    private static final long serialVersionUID = 1L;
    private List<V3xOrgMember> batchMembers;
    

    public AddBatchMemberEvent(Object source) {
        super(source);
    }


    public List<V3xOrgMember> getBatchMembers() {
        return batchMembers;
    }


    public void setBatchMembers(List<V3xOrgMember> batchMembers) {
        this.batchMembers = batchMembers;
    }

}
