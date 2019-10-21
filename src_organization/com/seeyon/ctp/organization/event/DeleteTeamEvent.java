package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;

public class DeleteTeamEvent extends Event {
    
    
    /**
     * 
     */
    private static final long serialVersionUID = 8753661568083286386L;
    private V3xOrgTeam team;

    public DeleteTeamEvent(Object source) {
        super(source);
    }

    public V3xOrgTeam getTeam() {
        return team;
    }

    public void setTeam(V3xOrgTeam team) {
        this.team = team;
    }

}
