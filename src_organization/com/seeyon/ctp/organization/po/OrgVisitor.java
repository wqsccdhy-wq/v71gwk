package com.seeyon.ctp.organization.po;

import java.util.Date;

import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;

public class OrgVisitor extends BasePO {
	/**
	 * 访客
	 */
	private static final long serialVersionUID = 212629488037842487L;
	private Long  id;
	private String name;
	private String mobile;
	private String account_name;
	private Long orgAccountId;

	private String type;
	private Integer state = OrgConstants.VISITOR_STATE.NORMAL.ordinal();
	private Date create_date;
	private Date update_date;
	
	private String ext_attr_1;
	private String ext_attr_2;
	private String ext_attr_3;
	private String ext_attr_4;
	private String ext_attr_5;
	private String ext_attr_6;
	private String ext_attr_7;
	private String ext_attr_8;
	private String ext_attr_9;
	private String ext_attr_10;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAccount_name() {
		return account_name;
	}
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}
	public String getExt_attr_1() {
		return ext_attr_1;
	}
	public void setExt_attr_1(String ext_attr_1) {
		this.ext_attr_1 = ext_attr_1;
	}
	public String getExt_attr_2() {
		return ext_attr_2;
	}
	public void setExt_attr_2(String ext_attr_2) {
		this.ext_attr_2 = ext_attr_2;
	}
	public String getExt_attr_3() {
		return ext_attr_3;
	}
	public void setExt_attr_3(String ext_attr_3) {
		this.ext_attr_3 = ext_attr_3;
	}
	public String getExt_attr_4() {
		return ext_attr_4;
	}
	public void setExt_attr_4(String ext_attr_4) {
		this.ext_attr_4 = ext_attr_4;
	}
	public String getExt_attr_5() {
		return ext_attr_5;
	}
	public void setExt_attr_5(String ext_attr_5) {
		this.ext_attr_5 = ext_attr_5;
	}
	public String getExt_attr_6() {
		return ext_attr_6;
	}
	public void setExt_attr_6(String ext_attr_6) {
		this.ext_attr_6 = ext_attr_6;
	}
	public String getExt_attr_7() {
		return ext_attr_7;
	}
	public void setExt_attr_7(String ext_attr_7) {
		this.ext_attr_7 = ext_attr_7;
	}
	public String getExt_attr_8() {
		return ext_attr_8;
	}
	public void setExt_attr_8(String ext_attr_8) {
		this.ext_attr_8 = ext_attr_8;
	}
	public String getExt_attr_9() {
		return ext_attr_9;
	}
	public void setExt_attr_9(String ext_attr_9) {
		this.ext_attr_9 = ext_attr_9;
	}
	public String getExt_attr_10() {
		return ext_attr_10;
	}
	public void setExt_attr_10(String ext_attr_10) {
		this.ext_attr_10 = ext_attr_10;
	}
	public Long getOrgAccountId() {
		return orgAccountId;
	}
	public void setOrgAccountId(Long orgAccountId) {
		this.orgAccountId = orgAccountId;
	}
	
}