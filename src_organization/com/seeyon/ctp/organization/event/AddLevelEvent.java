package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;

public class AddLevelEvent extends Event {
	private static final long serialVersionUID = -8264640695409868059L;
	private V3xOrgLevel level;

	public V3xOrgLevel getLevel() {
		return level;
	}

	public void setLevel(V3xOrgLevel level) {
		this.level = level;
	}

	public AddLevelEvent(Object source) {
		super(source);
	}

}