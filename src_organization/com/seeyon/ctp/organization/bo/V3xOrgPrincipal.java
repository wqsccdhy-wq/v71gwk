package com.seeyon.ctp.organization.bo;

import java.io.Serializable;
import java.util.Date;
/**
 * <p>Title: 组织模型账号BO对象</p>
 * <p>Description: 描述</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * 
 * @since CTP2.0
 * @author lilong
 */
public class V3xOrgPrincipal implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9104016834462591670L;
    private String            loginName;
    private String            password;
    private Long              memberId;
    private Date              expiration_date;
    
    public V3xOrgPrincipal(){}

    public V3xOrgPrincipal(Long memberId, String loginName, String password,Date expiration_date) {
        this();
        this.memberId = memberId;
        this.loginName = loginName;
        this.password = password;
        this.expiration_date = expiration_date;
    }
    public V3xOrgPrincipal(Long memberId, String loginName, String password) {
        this();
        this.memberId = memberId;
        this.loginName = loginName;
        this.password = password;
    }
    public V3xOrgPrincipal(Long memberId, String loginName) {
        this();
        this.memberId = memberId;
        this.loginName = loginName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

	public Date getExpiration_date() {
		return expiration_date;
	}

	public void setExpiration_date(Date expiration_date) {
		this.expiration_date = expiration_date;
	}

}
