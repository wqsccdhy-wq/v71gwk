package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgDutyLevel;

public class UpdateDutyLevelEvent extends Event {
	private static final long serialVersionUID = -7054773500704010648L;
	private V3xOrgDutyLevel dutyLevel;
	private V3xOrgDutyLevel oldDutyLevel;

	public V3xOrgDutyLevel getDutyLevel() {
		return dutyLevel;
	}

	public void setDutyLevel(V3xOrgDutyLevel dutyLevel) {
		this.dutyLevel = dutyLevel;
	}

	public UpdateDutyLevelEvent(Object source) {
		super(source);
	}

    public V3xOrgDutyLevel getOldDutyLevel() {
        return oldDutyLevel;
    }

    public void setOldDutyLevel(V3xOrgDutyLevel oldDutyLevel) {
        this.oldDutyLevel = oldDutyLevel;
    }
	
}