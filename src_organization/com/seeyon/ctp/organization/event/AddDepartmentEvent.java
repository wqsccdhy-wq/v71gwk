package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;

public class AddDepartmentEvent extends Event {
	private static final long serialVersionUID = -7205836036734973641L;
	private V3xOrgDepartment dept;

	public V3xOrgDepartment getDept() {
		return dept;
	}

	public void setDept(V3xOrgDepartment dept) {
		this.dept = dept;
	}

	public AddDepartmentEvent(Object source) {
		super(source);
	}

}