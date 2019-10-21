package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;

public class DeleteAccountEvent extends Event {
	private static final long serialVersionUID = -6763719723333991161L;
	private V3xOrgAccount account;

	public V3xOrgAccount getAccount() {
		return account;
	}

	public void setAccount(V3xOrgAccount account) {
		this.account = account;
	}

	public DeleteAccountEvent(Object source) {
		super(source);
	}

}