package com.seeyon.ctp.organization.webmodel;

/**
 * 副岗列表
 * @author MrBean
 *
 */
public class WebV3xOrgSecondPost {
    
    /** 人员ID */
    private Long memberId;
    /** 人员名称 */
    private String memberName;
    /** 排序号 */
    private Long sortId;
    /** 主岗部门名称 */
    private String deptName;
    /** 主岗名称 */
    private String postName;
    /** 副岗1 */
    private String secPost0 = "";
    /** 副岗2 */
    private String secPost1 = "";
    /** 副岗3 */
    private String secPost2 = "";
    /** 人员类型 */
    private String typeName;
    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    public Long getSortId() {
        return sortId;
    }
    public void setSortId(Long sortId) {
        this.sortId = sortId;
    }
    public String getDeptName() {
        return deptName;
    }
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
    public String getSecPost0() {
        return secPost0;
    }
    public void setSecPost0(String secPost0) {
        this.secPost0 = secPost0;
    }
    public String getSecPost1() {
        return secPost1;
    }
    public void setSecPost1(String secPost1) {
        this.secPost1 = secPost1;
    }
    public String getSecPost2() {
        return secPost2;
    }
    public void setSecPost2(String secPost2) {
        this.secPost2 = secPost2;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public Long getMemberId() {
        return memberId;
    }
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    public String getPostName() {
        return postName;
    }
    public void setPostName(String postName) {
        this.postName = postName;
    }
  
}
