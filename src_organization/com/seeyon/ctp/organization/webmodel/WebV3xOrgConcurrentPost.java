package com.seeyon.ctp.organization.webmodel;

import java.util.Date;

import com.seeyon.ctp.organization.bo.V3xOrgRelationship;

/**
 * 
 * <p>Title: 用于兼职信息展现的二次封装VO</p>
 * <p>Description: 主要兼职信息的名称的展现</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * @author lilong
 * @since CTP2。0
 */
public class WebV3xOrgConcurrentPost {

    /**兼职关系表记录id*/
    private Long               id;
    /**兼职关系对象*/
    private V3xOrgRelationship concurrentRel;
    /**兼职人员姓名*/
    private String             conMemberName;
    /**原单位名称*/
    private String             souAccountName;
    /**
     * 原单位ID
     */
    private String             souAccountID;
    /**兼职人员原主岗和单位名称*/
    private String             souPAnames;
    /**兼职编号*/
    private String             conPostCode;
    /**兼职目标单位名称*/
    private String             tarAccountName;
    /**
     * 兼职模目标单位ID
     */
    private String             tarAccountID;
    /**兼职目标部门岗位单位名称组合显示名称*/
    private String             tarDPAnames;
    /**兼职排序号*/
    private Long               conSortId;
    private String             createUserName;//创建人
    private String 			   lastModifyUserName;//最后修改人
    private Date createTime;
    private Date updateTime;

    public WebV3xOrgConcurrentPost() {
    }

    public WebV3xOrgConcurrentPost(V3xOrgRelationship bo) {
        this.id = bo.getId();
        this.concurrentRel = bo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public V3xOrgRelationship getConcurrentRel() {
        return concurrentRel;
    }

    public void setConcurrentRel(V3xOrgRelationship concurrentRel) {
        this.concurrentRel = concurrentRel;
    }

    public String getConMemberName() {
        return conMemberName;
    }

    public void setConMemberName(String conMemberName) {
        this.conMemberName = conMemberName;
    }

    public String getSouAccountName() {
        return souAccountName;
    }

    public void setSouAccountName(String souAccountName) {
        this.souAccountName = souAccountName;
    }

    public String getSouPAnames() {
        return souPAnames;
    }

    public void setSouPAnames(String souPAnames) {
        this.souPAnames = souPAnames;
    }

    public String getConPostCode() {
        return conPostCode;
    }

    public void setConPostCode(String conPostCode) {
        this.conPostCode = conPostCode;
    }

    public String getTarAccountName() {
        return tarAccountName;
    }

    public void setTarAccountName(String tarAccountName) {
        this.tarAccountName = tarAccountName;
    }

    public String getTarDPAnames() {
        return tarDPAnames;
    }

    public void setTarDPAnames(String tarDPAnames) {
        this.tarDPAnames = tarDPAnames;
    }

    public void setConSortId(Long conSortId) {
        this.conSortId = conSortId;
    }

    public Long getConSortId() {
        return conSortId;
    }

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getLastModifyUserName() {
		return lastModifyUserName;
	}

	public void setLastModifyUserName(String lastModifyUserName) {
		this.lastModifyUserName = lastModifyUserName;
	}

	public String getSouAccountID() {
		return souAccountID;
	}

	public void setSouAccountID(String souAccountID) {
		this.souAccountID = souAccountID;
	}

	public String getTarAccountID() {
		return tarAccountID;
	}

	public void setTarAccountID(String tarAccountID) {
		this.tarAccountID = tarAccountID;
	}


}
