package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;

public class AddTeamEvent extends Event {

    /**
     * 
     */
    private static final long serialVersionUID = 6623582486458580190L;
    private V3xOrgTeam team;

    public AddTeamEvent(Object source) {
        super(source);
        // TODO Auto-generated constructor stub
    }

    public V3xOrgTeam getTeam() {
        return team;
    }

    public void setTeam(V3xOrgTeam team) {
        this.team = team;
    }

}
