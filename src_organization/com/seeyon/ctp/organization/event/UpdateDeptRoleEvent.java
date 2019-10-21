package com.seeyon.ctp.organization.event;

import java.util.List;
import java.util.Map;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;

public class UpdateDeptRoleEvent extends Event {
    
    private V3xOrgDepartment department;
    private List<Map> oldRoleList;
    private List<Map> newRoleList;

    public UpdateDeptRoleEvent(Object source) {
        super(source);
    }

    public List<Map> getOldRoleList() {
        return oldRoleList;
    }

    public void setOldRoleList(List<Map> oldRoleList) {
        this.oldRoleList = oldRoleList;
    }

    public List<Map> getNewRoleList() {
        return newRoleList;
    }

    public void setNewRoleList(List<Map> newRoleList) {
        this.newRoleList = newRoleList;
    }

    public V3xOrgDepartment getDepartment() {
        return department;
    }

    public void setDepartment(V3xOrgDepartment department) {
        this.department = department;
    }

}
