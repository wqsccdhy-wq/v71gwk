package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;

public class UpdateDepartmentEvent extends Event {
	private static final long serialVersionUID = -8889582299855548747L;
	private V3xOrgDepartment dept;
	private V3xOrgDepartment oldDept;

	public V3xOrgDepartment getDept() {
		return dept;
	}

	public void setDept(V3xOrgDepartment dept) {
		this.dept = dept;
	}

	public UpdateDepartmentEvent(Object source) {
		super(source);
	}

    public V3xOrgDepartment getOldDept() {
        return oldDept;
    }

    public void setOldDept(V3xOrgDepartment oldDept) {
        this.oldDept = oldDept;
    }

}