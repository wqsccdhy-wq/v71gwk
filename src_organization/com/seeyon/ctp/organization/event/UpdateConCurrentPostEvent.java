package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;

public class UpdateConCurrentPostEvent extends Event {
	private static final long serialVersionUID = -765297924290515425L;
	private V3xOrgRelationship oldRel;
	private V3xOrgRelationship newRel;

    public UpdateConCurrentPostEvent(Object source) {
        super(source);
    }

	public V3xOrgRelationship getOldRel() {
		return oldRel;
	}

	public void setOldRel(V3xOrgRelationship oldRel) {
		this.oldRel = oldRel;
	}

	public V3xOrgRelationship getNewRel() {
		return newRel;
	}

	public void setNewRel(V3xOrgRelationship newRel) {
		this.newRel = newRel;
	}

}
