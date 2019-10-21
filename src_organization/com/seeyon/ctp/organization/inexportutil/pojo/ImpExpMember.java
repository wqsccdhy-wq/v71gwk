package com.seeyon.ctp.organization.inexportutil.pojo;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.ctp.organization.bo.V3xOrgEntity;

public class ImpExpMember extends ImpExpPojo {
	String code;

	String accountName;

	String loginName;

	String dept;

	String ppost;

	String spost;

	String level;

	String eMail = V3xOrgEntity.DEFAULT_EMPTY_STRING;

	String telNumber = V3xOrgEntity.DEFAULT_EMPTY_STRING;

	String gender = V3xOrgEntity.DEFAULT_EMPTY_STRING;

	String birthday = V3xOrgEntity.DEFAULT_EMPTY_STRING;

	String officeNumber = V3xOrgEntity.DEFAULT_EMPTY_STRING;
	
	String workScope = V3xOrgEntity.DEFAULT_EMPTY_STRING;
	
	//工作地,入职时间,汇报人
	String location = V3xOrgEntity.DEFAULT_EMPTY_STRING;
	String hiredate = V3xOrgEntity.DEFAULT_EMPTY_STRING;
	String reportTo = V3xOrgEntity.DEFAULT_EMPTY_STRING;
	
	//首选语言
	String primaryLanguange = V3xOrgEntity.DEFAULT_EMPTY_STRING;
	//通信地址
	String communication = V3xOrgEntity.DEFAULT_EMPTY_STRING;
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHiredate() {
		return hiredate;
	}

	public void setHiredate(String hiredate) {
		this.hiredate = hiredate;
	}

	public String getReportTo() {
		return reportTo;
	}

	public void setReportTo(String reportTo) {
		this.reportTo = reportTo;
	}

	//存放自定义通讯录字段的list
	List<String> customerAddressBooklist=new ArrayList<String>();

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getOfficeNumber() {
		return officeNumber;
	}

	public void setOfficeNumber(String officeNumber) {
		this.officeNumber = officeNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPpost() {
		return ppost;
	}

	public void setPpost(String ppost) {
		this.ppost = ppost;
	}

	public String getSpost() {
        return spost;
    }

    public void setSpost(String spost) {
        this.spost = spost;
    }

    public String getEMail() {
		return eMail;
	}

	public void setEMail(String mail) {
		eMail = mail;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	public List<String> getCustomerAddressBooklist() {
		return customerAddressBooklist;
	}

	public void setCustomerAddressBooklist(List<String> customerAddressBooklist) {
		this.customerAddressBooklist = customerAddressBooklist;
	}

	public String getPrimaryLanguange() {
		return primaryLanguange;
	}

	public void setPrimaryLanguange(String primaryLanguange) {
		this.primaryLanguange = primaryLanguange;
	}

	public String getCommunication() {
		return communication;
	}

	public void setCommunication(String communication) {
		this.communication = communication;
	}

	public String getWorkScope() {
		return workScope;
	}

	public void setWorkScope(String workScope) {
		this.workScope = workScope;
	}
	
	
}// end class
