package com.seeyon.ctp.organization.bo;

public class EntityIdTypeDsBO {
	//实体的id
	Long id;
	//实体
	V3xOrgEntity entity;
	//实体类型描述
	String dsc;
	//实体类型特殊标识
	String dscType;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public V3xOrgEntity getEntity() {
		return entity;
	}
	public void setEntity(V3xOrgEntity entity) {
		this.entity = entity;
	}
	public String getDsc() {
		return dsc;
	}
	public void setDsc(String dsc) {
		this.dsc = dsc;
	}
	public String getDscType() {
		return dscType;
	}
	public void setDscType(String dscType) {
		this.dscType = dscType;
	}
	
}
