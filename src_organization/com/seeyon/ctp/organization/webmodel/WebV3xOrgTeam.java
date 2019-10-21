package com.seeyon.ctp.organization.webmodel;

import java.util.List;

import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;

public class WebV3xOrgTeam {
    private V3xOrgTeam         v3xOrgTeam;
    private String             deptName;
    private V3xOrgDepartment   dept;
    private List<V3xOrgMember> memberList;
    private List<V3xOrgMember> memberNames;
    private String             memberIDs;
    private List<V3xOrgMember> memberLead;
    private List<V3xOrgMember> memberSupervisors;
    private List<V3xOrgMember> memberRelatives;
    
    /** 为组树展现增加三个属性 */
    private Long          id;
    private String        name;
    private Long          parentId;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public V3xOrgTeam getV3xOrgTeam() {
        return v3xOrgTeam;
    }

    public void setV3xOrgTeam(V3xOrgTeam orgTeam) {
        v3xOrgTeam = orgTeam;
    }

    public List<V3xOrgMember> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<V3xOrgMember> memberList) {
        this.memberList = memberList;
    }

    public String getMemberIDs() {
        return memberIDs;
    }

    public void setMemberIDs(String memberIDs) {
        this.memberIDs = memberIDs;
    }

    public List<V3xOrgMember> getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(List<V3xOrgMember> memberNames) {
        this.memberNames = memberNames;
    }

    public List<V3xOrgMember> getMemberLead() {
        return memberLead;
    }

    public void setMemberLead(List<V3xOrgMember> memberLead) {
        this.memberLead = memberLead;
    }

    public List<V3xOrgMember> getMemberSupervisors() {
        return memberSupervisors;
    }

    public void setMemberSupervisors(List<V3xOrgMember> supervisors) {
        this.memberSupervisors = supervisors;
    }

    public List<V3xOrgMember> getMemberRelatives() {
        return memberRelatives;
    }

    public void setMemberRelatives(List<V3xOrgMember> memberRelatives) {
        this.memberRelatives = memberRelatives;
    }

    public V3xOrgDepartment getDept() {
        return dept;
    }

    public void setDept(V3xOrgDepartment dept) {
        this.dept = dept;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
    
    
}