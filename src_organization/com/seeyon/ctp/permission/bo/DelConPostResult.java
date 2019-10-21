package com.seeyon.ctp.permission.bo;
/**
 * 
 * @author miaoyf
 *
 */
public class DelConPostResult {
	private String userName;//人员名称
	private String sourceUnitName;//原单位
	private String targetUnitName;//兼职单位
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSourceUnitName() {
		return sourceUnitName;
	}
	public void setSourceUnitName(String sourceUnitName) {
		this.sourceUnitName = sourceUnitName;
	}
	public String getTargetUnitName() {
		return targetUnitName;
	}
	public void setTargetUnitName(String targetUnitName) {
		this.targetUnitName = targetUnitName;
	}
}
