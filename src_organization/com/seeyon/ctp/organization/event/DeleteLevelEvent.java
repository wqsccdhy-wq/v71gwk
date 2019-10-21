package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;

public class DeleteLevelEvent extends Event {
	private static final long serialVersionUID = -3006434735698452383L;
	private V3xOrgLevel level;

	public V3xOrgLevel getLevel() {
		return level;
	}

	public void setLevel(V3xOrgLevel level) {
		this.level = level;
	}

	public DeleteLevelEvent(Object source) {
		super(source);
	}

}