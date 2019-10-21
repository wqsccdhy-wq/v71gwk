package com.seeyon.ctp.ociporg.po;
// Generated 2019-8-8 14:17:38 by Hibernate Tools 5.3.0.Beta2

import java.util.Date;

/**
 * OrgDepartmentTemp generated by hbm2java
 */
public class OrgDepartmentTemp implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5833085887586622153L;

	private String id;
	private String name;
	private String code;
	private String unitId;
	private String unitName;
	private String orgOcipLevel;
	private String majorId;
	private String path;
	private String resourceId;
	private String parentId;
	private String objectId;
	private Integer sortId;
	private Integer isEnable;
	private String orgPlatformDeptId;
	private String logicPath;
	private Integer synState;
	private Integer delFlag;
	private Date createTime;
	private Date updateTime;
	private Short isFlag;
	private Short grade;

	public OrgDepartmentTemp() {
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

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUnitId() {
		return this.unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return this.unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getOrgOcipLevel() {
		return this.orgOcipLevel;
	}

	public void setOrgOcipLevel(String orgOcipLevel) {
		this.orgOcipLevel = orgOcipLevel;
	}

	public String getMajorId() {
		return this.majorId;
	}

	public void setMajorId(String majorId) {
		this.majorId = majorId;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

	public String getOrgPlatformDeptId() {
		return this.orgPlatformDeptId;
	}

	public void setOrgPlatformDeptId(String orgPlatformDeptId) {
		this.orgPlatformDeptId = orgPlatformDeptId;
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
