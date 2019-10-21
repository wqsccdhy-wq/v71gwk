package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;

public class AddAccountEvent extends Event {
	private static final long serialVersionUID = -3094446318264432337L;
	private V3xOrgAccount account;

	public V3xOrgAccount getAccount() {
		return account;
	}

	public void setAccount(V3xOrgAccount account) {
		this.account = account;
	}

	public AddAccountEvent(Object source) {
		super(source);
	}

}