package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;

public class UpdateTeamEvent extends Event{
	private static final long serialVersionUID = -5004482826577512345L;
	private V3xOrgTeam team;
	private V3xOrgTeam oldTeam;
	
	public V3xOrgTeam getTeam() {
		return team;
	}

	public void setTeam(V3xOrgTeam team) {
		this.team = team;
	}

	public UpdateTeamEvent(Object source) {
		super(source);
	}

    public V3xOrgTeam getOldTeam() {
        return oldTeam;
    }

    public void setOldTeam(V3xOrgTeam oldTeam) {
        this.oldTeam = oldTeam;
    }

}
