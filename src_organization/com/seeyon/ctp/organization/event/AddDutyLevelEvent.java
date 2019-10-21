package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgDutyLevel;

public class AddDutyLevelEvent extends Event {
	private static final long serialVersionUID = -8264640695409868059L;
	private V3xOrgDutyLevel dutyLevel;

	public V3xOrgDutyLevel getDutyLevel() {
		return dutyLevel;
	}

	public void setDutyLevel(V3xOrgDutyLevel dutyLevel) {
		this.dutyLevel = dutyLevel;
	}

	public AddDutyLevelEvent(Object source) {
		super(source);
	}

}