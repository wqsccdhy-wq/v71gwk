package com.seeyon.ctp.ociporg.po;
// Generated 2019-8-14 17:27:33 by Hibernate Tools 5.3.0.Beta2

import java.util.Date;

/**
 * OrgUserLevelTemp generated by hbm2java
 */
public class OrgUserLevelTemp implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1579885219876665452L;
	private String id;
	private String name;
	private String code;
	private Integer value;
	private String unitId;
	private String unitName;
	private String baseLevelId;
	private String resourceId;
	private Integer sortId;
	private Integer isEnable;
	private Integer synState;
	private String objectId;
	private Integer delFlag;
	private Date createTime;
	private Date updateTime;
	private Short isFlag;

	public OrgUserLevelTemp() {
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

	public Integer getValue() {
		return this.value;
	}

	public void setValue(Integer value) {
		this.value = value;
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

	public String getBaseLevelId() {
		return this.baseLevelId;
	}

	public void setBaseLevelId(String baseLevelId) {
		this.baseLevelId = baseLevelId;
	}

	public String getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
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

	public Integer getSynState() {
		return this.synState;
	}

	public void setSynState(Integer synState) {
		this.synState = synState;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
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

}
