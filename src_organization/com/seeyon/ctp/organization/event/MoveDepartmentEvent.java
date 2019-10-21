package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;

/**
 * 移动部门事件
 * @author Administrator
 *
 */
public class MoveDepartmentEvent extends Event {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8448530408797398412L;
    private V3xOrgDepartment department;
    private V3xOrgDepartment oldDepartment;

    public MoveDepartmentEvent(Object source) {
        super(source);
        // TODO Auto-generated constructor stub
    }

    public V3xOrgDepartment getDepartment() {
        return department;
    }

    public void setDepartment(V3xOrgDepartment department) {
        this.department = department;
    }

    public V3xOrgDepartment getOldDepartment() {
        return oldDepartment;
    }

    public void setOldDepartment(V3xOrgDepartment oldDepartment) {
        this.oldDepartment = oldDepartment;
    }

}
