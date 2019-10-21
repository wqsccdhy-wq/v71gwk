package com.seeyon.apps.addressbook.webmodel;

import com.seeyon.ctp.organization.bo.V3xOrgMember;

public class WebSimpleV3xOrgMember {

    private V3xOrgMember v3xOrgMember;
    private Long         deptId;
    private Long         postId;
    private Long         levelId;

    public WebSimpleV3xOrgMember(V3xOrgMember v3xOrgMember, Long deptId, Long postId, Long levelId) {
        this.v3xOrgMember = v3xOrgMember;
        this.deptId = deptId;
        this.postId = postId;
        this.levelId = levelId;
    }

    public V3xOrgMember getV3xOrgMember() {
        return v3xOrgMember;
    }

    public void setV3xOrgMember(V3xOrgMember v3xOrgMember) {
        this.v3xOrgMember = v3xOrgMember;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

}
