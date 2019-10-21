package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgMember;

public class DeleteMemberEvent extends Event {
	private static final long serialVersionUID = -4624011326353276766L;
	private V3xOrgMember member;

	public V3xOrgMember getMember() {
		return member;
	}

	public void setMember(V3xOrgMember member) {
		this.member = member;
	}

	public DeleteMemberEvent(Object source) {
		super(source);
	}

}