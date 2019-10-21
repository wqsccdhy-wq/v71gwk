package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;

public class UpdateLevelEvent extends Event {
	private static final long serialVersionUID = -7054773500704010648L;
	private V3xOrgLevel level;
	private V3xOrgLevel oldLevel;

	public V3xOrgLevel getLevel() {
		return level;
	}

	public void setLevel(V3xOrgLevel level) {
		this.level = level;
	}

	public UpdateLevelEvent(Object source) {
		super(source);
	}

    public V3xOrgLevel getOldLevel() {
        return oldLevel;
    }

    public void setOldLevel(V3xOrgLevel oldLevel) {
        this.oldLevel = oldLevel;
    }
	
}