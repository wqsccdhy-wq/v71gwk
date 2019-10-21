package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgMember;

public class MemberUpdateDeptEvent extends Event {

    private static final long serialVersionUID = 1L;
    private V3xOrgMember      member;
    private Long              oldDepartmentId;
    private Long              newDepartmentId;

    public MemberUpdateDeptEvent(Object source) {
        super(source);
    }

    public V3xOrgMember getMember() {
        return member;
    }

    public void setMember(V3xOrgMember member) {
        this.member = member;
    }

    public Long getOldDepartmentId() {
        return oldDepartmentId;
    }

    public void setOldDepartmentId(Long oldDepartmentId) {
        this.oldDepartmentId = oldDepartmentId;
    }

    public Long getNewDepartmentId() {
        return newDepartmentId;
    }

    public void setNewDepartmentId(Long newDepartmentId) {
        this.newDepartmentId = newDepartmentId;
    }

}
