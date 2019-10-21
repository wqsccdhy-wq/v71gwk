package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;

public class AddConCurrentPostEvent extends Event {
    private static final long  serialVersionUID = 1L;
    private V3xOrgRelationship rel;

    public AddConCurrentPostEvent(Object source) {
        super(source);
    }

    public V3xOrgRelationship getRel() {
        return rel;
    }

    public void setRel(V3xOrgRelationship rel) {
        this.rel = rel;
    }

}
