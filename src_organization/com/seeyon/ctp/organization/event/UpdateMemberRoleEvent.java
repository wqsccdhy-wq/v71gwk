package com.seeyon.ctp.organization.event;

import java.util.List;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.MemberRole;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
//这个只针对人员管理中角色变更（专指给肖霖）
public class UpdateMemberRoleEvent extends Event {
	private static final long serialVersionUID = -5004482826577590328L;
	
	private V3xOrgMember member;
	//修改之前的用户角色
	private List<MemberRole> oldMemberRole;
	
	private List<MemberRole> newMemberRole;
	
    public V3xOrgMember getMember() {
		return member;
	}

	public void setMember(V3xOrgMember member) {
		this.member = member;
	}

	public UpdateMemberRoleEvent(Object source) {
		super(source);
	}

	public List<MemberRole> getOldMemberRole() {
		return oldMemberRole;
	}

	public void setOldMemberRole(List<MemberRole> oldMemberRole) {
		this.oldMemberRole = oldMemberRole;
	}

	public List<MemberRole> getNewMemberRole() {
		return newMemberRole;
	}

	public void setNewMemberRole(List<MemberRole> newMemberRole) {
		this.newMemberRole = newMemberRole;
	}

}