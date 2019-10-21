package com.seeyon.ctp.organization.webmodel;

import java.util.List;

import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;

public class WebV3xOrgMember {
    private V3xOrgMember       v3xOrgMember;
    private Long               id;
    private String             departmentName;
    private String             departmentFullName;
    private String             levelName;
    private Long               levelId;
    private String             postName;
    private Long               postId;
    private String             typeName;//人员类型
    private String             stateName;//人员在职/离职状态
    private String             secondPosts;
    private String             accountName;
    private String             officeNum;
    private String             ldapLoginName;
    private List<V3xOrgEntity> workscope;
    /** 为外部人员显示工作范围名称增加的属性 */
    private String             workscopeName;
    private String             loginName;
    private String             name;
    private Long               sortId;
    private String             code;
    private String             enableName;
    private String             description;
    private String             businessRoleName;//组织角色

    //branches_a8_v350_r_gov GOV-1277 杨帆 添加职级属性 start
    private String       dutyLevelName;
    //增加性别
    private String 			   gender;

    public String getDutyLevelName() {
        return dutyLevelName;
    }

    public void setDutyLevelName(String dutyLevelName) {
        this.dutyLevelName = dutyLevelName;
    }

    //branches_a8_v350_r_gov GOV-1277 杨帆 添加职级属性end

    public String getWorkscopeName() {
        return workscopeName;
    }

    public void setWorkscopeName(String workscopeName) {
        this.workscopeName = workscopeName;
    }
    public List<V3xOrgEntity> getWorkscope() {
        return workscope;
    }

    public void setWorkscope(List<V3xOrgEntity> workscope) {
        this.workscope = workscope;
    }

    public String getOfficeNum() {
        return officeNum;
    }

    public void setOfficeNum(String officeNum) {
        this.officeNum = officeNum;
    }

    public V3xOrgMember getV3xOrgMember() {
        return v3xOrgMember;
    }

    public void setV3xOrgMember(V3xOrgMember orgMember) {
        v3xOrgMember = orgMember;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getSecondPosts() {
        return secondPosts;
    }

    public void setSecondPosts(String secondPosts) {
        this.secondPosts = secondPosts;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSortId() {
        return sortId;
    }

    public void setSortId(Long sortId) {
        this.sortId = sortId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLdapLoginName() {
        return ldapLoginName;
    }

    public void setLdapLoginName(String ldapLoginName) {
        this.ldapLoginName = ldapLoginName;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEnableName() {
		return enableName;
	}

	public void setEnableName(String enableName) {
		this.enableName = enableName;
	}

	public String getDepartmentFullName() {
		return departmentFullName;
	}

	public void setDepartmentFullName(String departmentFullName) {
		this.departmentFullName = departmentFullName;
	}

	public String getBusinessRoleName() {
		return businessRoleName;
	}

	public void setBusinessRoleName(String businessRoleName) {
		this.businessRoleName = businessRoleName;
	}

	public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }
}
