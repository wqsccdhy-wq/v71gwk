package com.seeyon.ctp.organization.bo;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.po.OrgVisitor;
import com.seeyon.ctp.util.UUIDLong;

/**
 * 访客
 * @author wf
 *
 */
public class V3xOrgVisitor extends V3xOrgEntity implements Serializable {
	private static final long serialVersionUID = 4368915109392038522L;

	private Long id;
	private String name;
	private String mobile;
	private String account_name;

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
	
	public V3xOrgVisitor() {
		
	}
	
    public V3xOrgVisitor(V3xOrgVisitor orgVisitor) {
        this.id = orgVisitor.getId();
        this.orgAccountId = orgVisitor.getOrgAccountId();
        this.name = orgVisitor.getName();
        this.mobile = orgVisitor.getMobile();
        this.account_name = orgVisitor.getAccount_name();
        this.type = orgVisitor.getType();
        this.state = orgVisitor.getState();
        this.create_date = orgVisitor.getCreate_date();
        this.update_date = orgVisitor.getUpdate_date();
        this.ext_attr_1 = orgVisitor.getExt_attr_1();
        this.ext_attr_2 = orgVisitor.getExt_attr_2();
        this.ext_attr_3 = orgVisitor.getExt_attr_3();
        this.ext_attr_4 = orgVisitor.getExt_attr_4();
        this.ext_attr_5 = orgVisitor.getExt_attr_5();
        this.ext_attr_6 = orgVisitor.getExt_attr_6();
        this.ext_attr_7 = orgVisitor.getExt_attr_7();
        this.ext_attr_8 = orgVisitor.getExt_attr_8();
        this.ext_attr_9 = orgVisitor.getExt_attr_9();
        this.ext_attr_10 = orgVisitor.getExt_attr_10();
        
    }

    public V3xOrgVisitor(OrgVisitor orgVisitor) {
        this.fromPO(orgVisitor);
    }

    public V3xOrgVisitor fromPO(BasePO po) {
    	OrgVisitor orgVisitor = (OrgVisitor) po;
    	this.orgAccountId = orgVisitor.getOrgAccountId();
        this.id = orgVisitor.getId();
        this.name = orgVisitor.getName();
        this.mobile = orgVisitor.getMobile();
        this.account_name = orgVisitor.getAccount_name();
        this.type = orgVisitor.getType();
        this.state= orgVisitor.getState();
        this.create_date = orgVisitor.getCreate_date();
        this.update_date = orgVisitor.getUpdate_date();
        this.ext_attr_1 = orgVisitor.getExt_attr_1();
        this.ext_attr_2 = orgVisitor.getExt_attr_2();
        this.ext_attr_3 = orgVisitor.getExt_attr_3();
        this.ext_attr_4 = orgVisitor.getExt_attr_4();
        this.ext_attr_5 = orgVisitor.getExt_attr_5();
        this.ext_attr_6 = orgVisitor.getExt_attr_6();
        this.ext_attr_7 = orgVisitor.getExt_attr_7();
        this.ext_attr_8 = orgVisitor.getExt_attr_8();
        this.ext_attr_9 = orgVisitor.getExt_attr_9();
        this.ext_attr_10 = orgVisitor.getExt_attr_10();
        return this;
    }

    public BasePO toPO() {
    	OrgVisitor o = new OrgVisitor();
        o.setId(this.id);
        o.setOrgAccountId(this.orgAccountId);
        o.setName(this.name);
        o.setMobile(this.mobile);
        o.setAccount_name(account_name);
        o.setType(type);
        o.setState(state);
        o.setCreate_date(create_date);
        o.setUpdate_date(update_date);
        o.setExt_attr_1(ext_attr_1);
        o.setExt_attr_2(ext_attr_2);
        o.setExt_attr_3(ext_attr_3);
        o.setExt_attr_4(ext_attr_4);
        o.setExt_attr_5(ext_attr_5);
        o.setExt_attr_6(ext_attr_6);
        o.setExt_attr_7(ext_attr_7);
        o.setExt_attr_8(ext_attr_8);
        o.setExt_attr_9(ext_attr_9);
        o.setExt_attr_10(ext_attr_10);
        return o;
    }

    
    public void setIdIfNew() {
        if(this.id == null){
            this.id = UUIDLong.longUUID();
        }
    }
    
    
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
	
	
	
	@Override
	public String getEntityType() {
		return OrgConstants.ORGENT_TYPE.Visitor.name();
	}

	@Override
	public boolean isValid() {
		return this.state == OrgConstants.VISITOR_STATE.NORMAL.ordinal();
	}
}