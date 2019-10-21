package com.seeyon.ctp.organization.webmodel;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.ctp.organization.bo.V3xOrgDepartment;

public class WebV3xOrgDepartment {

    private V3xOrgDepartment v3xOrgDepartment;

    private String           parentName;
    private String           adminNames;
    private String           adminIds;
    private String           managerNames;
    private String           managerIds;
    private String           postNames;
    private String           postIds;
    public List<String>      rolelist = new ArrayList<String>();
    /**为组织树展现增加属性*/
    private Long             id;
    private String           name;
    private Long             parentId;
    private String           iconSkin = "";
    
    

	public WebV3xOrgDepartment(){}

    public WebV3xOrgDepartment(Long id, String name, Long parentId) {
        super();
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public List<String> getRolelist() {
        return rolelist;
    }

    public void setRolelist(List<String> rolelist) {
        this.rolelist = rolelist;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public String getIconSkin() {
		return iconSkin;
	}

	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public V3xOrgDepartment getV3xOrgDepartment() {
        return v3xOrgDepartment;
    }

    public void setV3xOrgDepartment(V3xOrgDepartment orgDepartment) {
        v3xOrgDepartment = orgDepartment;
    }

    public String getAdminNames() {
        return adminNames;
    }

    public void setAdminNames(String adminNames) {
        this.adminNames = adminNames;
    }

    public String getManagerNames() {
        return managerNames;
    }

    public void setManagerNames(String managerNames) {
        this.managerNames = managerNames;
    }

    public String getAdminIds() {
        return adminIds;
    }

    public void setAdminIds(String adminIds) {
        this.adminIds = adminIds;
    }

    public String getManagerIds() {
        return managerIds;
    }

    public void setManagerIds(String managerIds) {
        this.managerIds = managerIds;
    }

    public String getPostIds() {
        return postIds;
    }

    public void setPostIds(String postIds) {
        this.postIds = postIds;
    }

    public String getPostNames() {
        return postNames;
    }

    public void setPostNames(String postNames) {
        this.postNames = postNames;
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

}
