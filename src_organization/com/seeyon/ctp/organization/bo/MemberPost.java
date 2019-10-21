package com.seeyon.ctp.organization.bo;

import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.util.UUIDLong;

public class MemberPost implements java.io.Serializable {

    private static final long           serialVersionUID = -6358235978443111595L;

    private Long                        memberId;                                //人员id
    private Long                        depId;                                   //部门的id
    private Long                        postId;                                  //岗位的Id
    private Long                        levelId;                                 //职务级别的Id
    private Long                        dutyLevelId;                             //职级的Id
    private Long                        orgAccountId;                            //所属单位ID
    private String                      code;                                    //兼职编号
    private Long                        sortId           = 0L;
    private OrgConstants.MemberPostType type;                                    //岗位类型: 主/副/兼
    private String                      conRoles         = "";                   //本条兼职关系所选择的角色以备删除和更新时使用
    private Long 						createUserID;//创建单位ID
    /**
     *  创建时间 
     */
    private java.util.Date createTime;
    /**
     *  更改时间 
     */
    private java.util.Date updateTime;
    

    public MemberPost(){
    }
    
    public static MemberPost createMainPost(V3xOrgMember member){
        MemberPost mainPost = new MemberPost();
        
        mainPost.setType(OrgConstants.MemberPostType.Main);
        mainPost.setMemberId(member.getId());
        mainPost.setPostId(member.getOrgPostId());
        mainPost.setDepId(member.getOrgDepartmentId());
        mainPost.setLevelId(member.getOrgLevelId());
        mainPost.setOrgAccountId(member.getOrgAccountId());
        mainPost.setSortId(member.getSortId().longValue());
        
        return mainPost;
    }
    
    /**
     * 副岗
     * @param memberId
     * @param secondDeptId 副岗部门，不可以为null
     * @param secondPostId 副岗岗位，不可以为null
     * @param mainAccountId 副岗单位，同时也是主岗单位，不可以为null
     * @param mainSortId 主岗排序号，一定要填
     * @return
     */
    public static MemberPost createSecondPost(Long memberId, Long secondDeptId, Long secondPostId, Long mainAccountId, Integer mainSortId){
        MemberPost secondPost = new MemberPost();
        
        secondPost.setType(OrgConstants.MemberPostType.Second);
        secondPost.setMemberId(memberId);
        secondPost.setDepId(secondDeptId);
        secondPost.setPostId(secondPostId);
        secondPost.setOrgAccountId(mainAccountId);
        secondPost.setSortId(mainSortId.longValue());
        
        return secondPost;
    }
    
    /**
     * 兼职
     * @param memberId
     * @param concurrentDeptId 兼职部门，可以为null
     * @param concurrentPostId 兼职岗位，可以为null
     * @param concurrentLevelId 兼职职务级别，可以为null
     * @param concurrentAccountId 兼职单位，<b>不可以</b>为null
     * @param concurrentSortId 兼职排序号，如果没有指定，就给-1
     * @param concurrentCode 兼职编号
     * @param conRoles 兼职所选的兼职角色
     * @return
     */
    public static MemberPost createConcurrentPost(Long memberId, Long concurrentDeptId, Long concurrentPostId, Long concurrentLevelId, Long concurrentAccountId, Integer concurrentSortId, String concurrentCode, String conRoles){
        MemberPost concurrentPost = new MemberPost();
        
        concurrentPost.setType(OrgConstants.MemberPostType.Concurrent);
        concurrentPost.setMemberId(memberId);
        concurrentPost.setDepId(concurrentDeptId);
        concurrentPost.setPostId(concurrentPostId);
        concurrentPost.setLevelId(concurrentLevelId);
        concurrentPost.setOrgAccountId(concurrentAccountId);
        concurrentPost.setSortId(Long.valueOf(concurrentSortId));
        concurrentPost.setCode(concurrentCode);
        concurrentPost.setConRoles(conRoles);
        
        return concurrentPost;
    }
    
    public MemberPost(V3xOrgRelationship rel) {
        //修改主岗方法 steven
        if(!OrgConstants.RelationshipType.Member_Post.name().equals(rel.getKey())){
            throw new IllegalArgumentException("V3xOrgRelationship.key [" + rel.getKey() + "] must be 'Member_Post'");
        }
        this.memberId = rel.getSourceId();
        this.depId = rel.getObjective0Id();
        this.postId = rel.getObjective1Id();
        this.levelId = rel.getObjective2Id();
        this.dutyLevelId = rel.getObjective3Id();
        
        this.type = OrgConstants.MemberPostType.valueOf(rel.getObjective5Id());
        this.code = rel.getObjective6Id();
        this.sortId = rel.getSortId();
        this.orgAccountId = rel.getOrgAccountId();
        this.conRoles = rel.getObjective7Id()==null?"":rel.getObjective7Id();
        this.updateTime=rel.getUpdateTime();
        this.createTime=rel.getCreateTime();
    }

    public V3xOrgRelationship toRelationship() {
        V3xOrgRelationship rel = new V3xOrgRelationship();
        rel.setId(UUIDLong.longUUID());
        rel.setKey(OrgConstants.RelationshipType.Member_Post.name());
        rel.setSourceId(memberId);
        rel.setObjective0Id(depId);
        rel.setObjective1Id(postId);
        rel.setObjective2Id(levelId);
        rel.setObjective3Id(dutyLevelId);
        rel.setObjective5Id(type.name());
        rel.setObjective6Id(code);
        rel.setObjective7Id(conRoles);
        rel.setSortId(sortId);
        rel.setOrgAccountId(orgAccountId);

        return rel;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getDepId() {
        return depId;
    }

    public void setDepId(Long depId) {
        this.depId = depId;
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

    public Long getDutyLevelId() {
        return dutyLevelId;
    }

    public void setDutyLevelId(Long dutyLevelId) {
        this.dutyLevelId = dutyLevelId;
    }

    public Long getOrgAccountId() {
        return orgAccountId;
    }

    public void setOrgAccountId(Long orgAccountId) {
        this.orgAccountId = orgAccountId;
    }

    public Long getSortId() {
        return sortId;
    }

    public void setSortId(Long sortId) {
        this.sortId = sortId;
    }

    public OrgConstants.MemberPostType getType() {
        return type;
    }

    public void setType(OrgConstants.MemberPostType type) {
        this.type = type;
    }
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public String getConRoles() {
        return conRoles;
    }

    public void setConRoles(String conRoles) {
        this.conRoles = conRoles;
    }
    
    public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public java.util.Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(java.util.Date updateTime) {
		this.updateTime = updateTime;
	}

	public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((depId == null) ? 0 : depId.hashCode());
        result = prime * result + ((memberId == null) ? 0 : memberId.hashCode());
        result = prime * result + ((postId == null) ? 0 : postId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MemberPost other = (MemberPost) obj;
        if (depId == null) {
            if (other.depId != null)
                return false;
        } else if (!depId.equals(other.depId))
            return false;
        if (memberId == null) {
            if (other.memberId != null)
                return false;
        } else if (!memberId.equals(other.memberId))
            return false;
        if (postId == null) {
            if (other.postId != null)
                return false;
        } else if (!postId.equals(other.postId))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

	public Long getCreateUserID() {
		return createUserID;
	}

	public void setCreateUserID(Long createUserID) {
		this.createUserID = createUserID;
	}

}
