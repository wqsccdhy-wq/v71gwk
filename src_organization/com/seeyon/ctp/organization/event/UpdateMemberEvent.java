package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgMember;

public class UpdateMemberEvent extends Event {
	private static final long serialVersionUID = -5004482826577590328L;
	private V3xOrgMember oldMember;
	private V3xOrgMember member;
	
    public V3xOrgMember getOldMember() {
        return oldMember;
    }
    
    public void setOldMember(V3xOrgMember oldMember) {
        this.oldMember = oldMember;
    }

    public V3xOrgMember getMember() {
		return member;
	}

	public void setMember(V3xOrgMember member) {
		this.member = member;
	}

	public UpdateMemberEvent(Object source) {
		super(source);
	}

}