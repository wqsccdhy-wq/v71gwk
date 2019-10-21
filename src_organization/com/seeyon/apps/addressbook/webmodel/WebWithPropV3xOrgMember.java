/**
 * $Id: WebWithPropV3xOrgMember.java,v 1.7 2010/03/04 06:27:57 xiegg Exp $
`* 
 * Licensed to the UFIDA
 */
package com.seeyon.apps.addressbook.webmodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.seeyon.ctp.organization.webmodel.WebV3xOrgMember;

/**
 *
 * <p/> Title: 带属性的WebV3xOrgMember
 * </p>
 * <p/> Description: 带属性的WebV3xOrgMember
 * </p>
 * <p/> Date: 2007-5-30
 * </p>
 * @author paul(qdlake@gmail.com)
 * @see com.seeyon.v3x.organization.webmodel.WebV3xOrgMember
 */
public class WebWithPropV3xOrgMember extends WebV3xOrgMember  implements Serializable {
	private String companyPhone;//外部联系人：单位电话
	private String familyPhone;//外部联系人：家庭电话
	private String mobilePhone;//外部联系人：手机
	private String fax;//外部联系人：传真
	private String address;//外部联系人：地址
	private String postcode;//外部联系人：邮编
	private String email;//外部联系人：电子邮件
	private String blog;//外部联系人：博客
	private String msn;//外部联系人：msn
	private String qq;//外部联系人：qq
	private String memo;//外部联系人：备注
	private String memberName;//人员姓名
	private String description ;//人员备注信息
    private String memberWX;     //微信
    private String memberWB;     //微博
    private String memberHome;   //家庭住址
    private String memberCode;   //邮政编码
    private String memberAddress; //通讯地址
    private String worklocalStr; //工作地
    private String fileName; //头像附件
    private Map<String,String> customerAddressbookValueMap = new HashMap<String,String>();//存放自定义的通讯录字段信息

	public WebWithPropV3xOrgMember() {
		
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBlog() {
		return blog;
	}
	public void setBlog(String blog) {
		this.blog = blog;
	}
	public String getCompanyPhone() {
		return companyPhone;
	}
	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFamilyPhone() {
		return familyPhone;
	}
	public void setFamilyPhone(String familyPhone) {
		this.familyPhone = familyPhone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getMsn() {
		return msn;
	}
	public void setMsn(String msn) {
		this.msn = msn;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	
	public void bind(Map<String, String> propMap) {
		if (null != propMap && propMap.size() > 0) {
			this.setCompanyPhone(propMap.get("companyPhone"));
			this.setFamilyPhone(propMap.get("familyPhone"));
			this.setMobilePhone(propMap.get("mobilePhone"));
			this.setFax(propMap.get("fax"));
			this.setAddress(propMap.get("address"));
			this.setPostcode(propMap.get("postcode"));
			this.setEmail(propMap.get("email"));
			this.setBlog(propMap.get("blog"));
			this.setMsn(propMap.get("msn"));
			this.setQq(propMap.get("qq"));
			this.setMemo(propMap.get("memo"));
		}
	}
	
	public Map<String, Object> properties() {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("companyPhone", this.getCompanyPhone());
		properties.put("familyPhone", this.getFamilyPhone());
		properties.put("mobilePhone", this.getMobilePhone());
		properties.put("fax", this.getFax());
		properties.put("address", this.getAddress());
		properties.put("postcode", this.getPostcode());
		properties.put("email", this.getEmail());
		properties.put("blog", this.getBlog());
		properties.put("msn", this.getMsn());
		properties.put("qq", this.getQq());
		properties.put("memo", this.getMemo());
		return properties;

	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public String getMemberWX() {
        return memberWX;
    }

    public void setMemberWX(String memberWX) {
        this.memberWX = memberWX;
    }

    public String getMemberWB() {
        return memberWB;
    }

    public void setMemberWB(String memberWB) {
        this.memberWB = memberWB;
    }

    public String getMemberHome() {
        return memberHome;
    }

    public void setMemberHome(String memberHome) {
        this.memberHome = memberHome;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getMemberAddress() {
        return memberAddress;
    }

    public void setMemberAddress(String memberAddress) {
        this.memberAddress = memberAddress;
    }

	public String getWorklocalStr() {
		return worklocalStr;
	}

	public void setWorklocalStr(String worklocalStr) {
		this.worklocalStr = worklocalStr;
	}

	public Map<String, String> getCustomerAddressbookValueMap() {
		return customerAddressbookValueMap;
	}

	public void setCustomerAddressbookValueMap(
			Map<String, String> customerAddressbookValueMap) {
		this.customerAddressbookValueMap = customerAddressbookValueMap;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	

}
