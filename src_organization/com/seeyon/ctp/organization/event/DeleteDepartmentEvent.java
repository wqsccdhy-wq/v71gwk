package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;

public class DeleteDepartmentEvent extends Event {
	private static final long serialVersionUID = -8474251823197399524L;
	private V3xOrgDepartment dept;

	public V3xOrgDepartment getDept() {
		return dept;
	}

	public void setDept(V3xOrgDepartment dept) {
		this.dept = dept;
	}

	public DeleteDepartmentEvent(Object source) {
		super(source);
	}

}