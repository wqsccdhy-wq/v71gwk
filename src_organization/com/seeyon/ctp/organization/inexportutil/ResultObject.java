package com.seeyon.ctp.organization.inexportutil;

import java.io.Serializable;

/**
 * 主要用来做结果显示
 * 
 * @author kyt
 * @author lilong
 * @since CTP2.0
 * 
 */
public class ResultObject implements Serializable {
	static final long serialVersionUID = 1L;

	private String name;
	private String success;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

}
