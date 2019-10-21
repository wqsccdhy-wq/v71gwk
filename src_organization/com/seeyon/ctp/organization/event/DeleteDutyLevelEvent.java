package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgDutyLevel;

public class DeleteDutyLevelEvent extends Event {
	private static final long serialVersionUID = -3006434735698452383L;
	private V3xOrgDutyLevel dutyLevel;

	public V3xOrgDutyLevel getDutyLevel() {
		return dutyLevel;
	}

	public void setDutyLevel(V3xOrgDutyLevel dutyLevel) {
		this.dutyLevel = dutyLevel;
	}

	public DeleteDutyLevelEvent(Object source) {
		super(source);
	}

}