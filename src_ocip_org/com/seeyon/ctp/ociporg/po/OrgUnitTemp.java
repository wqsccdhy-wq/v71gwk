package com.seeyon.ctp.ociporg.po;
// Generated 2019-8-8 14:17:38 by Hibernate Tools 5.3.0.Beta2

import java.util.Date;

/**
 * OrgUnitTemp generated by hbm2java
 */
public class OrgUnitTemp implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -706209801453225075L;

	private String id;
	private String name;
	private String aliasName;
	private String foreignName;
	private String code;
	private String shortName;
	private Integer type;
	private String orgOcipLevel;
	private String path;
	private String majorUserId;
	private String parentId;
	private String adminId;
	private String resourceId;
	private String objectId;
	private Integer sortId;
	private Integer isEnable;
	private String orgPlatformUnitId;
	private String logicPath;
	private Integer synState;
	private Integer delFlag;
	private Date createTime;
	private Date updateTime;
	private Short isFlag;
	private Short grade;

	public OrgUnitTemp() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAliasName() {
		return this.aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getForeignName() {
		return this.foreignName;
	}

	public void setForeignName(String foreignName) {
		this.foreignName = foreignName;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOrgOcipLevel() {
		return this.orgOcipLevel;
	}

	public void setOrgOcipLevel(String orgOcipLevel) {
		this.orgOcipLevel = orgOcipLevel;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMajorUserId() {
		return this.majorUserId;
	}

	public void setMajorUserId(String majorUserId) {
		this.majorUserId = majorUserId;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getAdminId() {
		return this.adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Integer getSortId() {
		return this.sortId;
	}

	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}

	public Integer getIsEnable() {
		return this.isEnable;
	}

	public void setIsEnable(Integer isEnable) {
		this.isEnable = isEnable;
	}

	public String getOrgPlatformUnitId() {
		return this.orgPlatformUnitId;
	}

	public void setOrgPlatformUnitId(String orgPlatformUnitId) {
		this.orgPlatformUnitId = orgPlatformUnitId;
	}

	public String getLogicPath() {
		return this.logicPath;
	}

	public void setLogicPath(String logicPath) {
		this.logicPath = logicPath;
	}

	public Integer getSynState() {
		return this.synState;
	}

	public void setSynState(Integer synState) {
		this.synState = synState;
	}

	public Integer getDelFlag() {
		return this.delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Short getIsFlag() {
		return this.isFlag;
	}

	public void setIsFlag(Short isFlag) {
		this.isFlag = isFlag;
	}

	public Short getGrade() {
		return this.grade;
	}

	public void setGrade(Short grade) {
		this.grade = grade;
	}

}