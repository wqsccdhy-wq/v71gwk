package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.event.Event;

public class AddAdminMemberEvent extends Event {
	private static final long serialVersionUID = -5719181273255663177L;
	private V3xOrgMember member;
	private boolean isBatch = false;;

	public V3xOrgMember getMember() {
		return member;
	}

	public void setMember(V3xOrgMember member) {
		this.member = member;
	}

	public AddAdminMemberEvent(Object source) {
		super(source);
	}

    public boolean isBatch() {
        return isBatch;
    }

    public void setBatch(boolean isBatch) {
        this.isBatch = isBatch;
    }

}