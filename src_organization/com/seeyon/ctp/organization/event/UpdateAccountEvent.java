package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;

public class UpdateAccountEvent extends Event {
	private static final long serialVersionUID = 8838248630941447447L;
	private V3xOrgAccount account;
	private V3xOrgAccount oldAccount;

	public V3xOrgAccount getAccount() {
		return account;
	}

	public void setAccount(V3xOrgAccount account) {
		this.account = account;
	}

	public UpdateAccountEvent(Object source) {
		super(source);
	}

    public V3xOrgAccount getOldAccount() {
        return oldAccount;
    }

    public void setOldAccount(V3xOrgAccount oldAccount) {
        this.oldAccount = oldAccount;
    }

}