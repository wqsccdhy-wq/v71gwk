package com.seeyon.ctp.organization.inexportutil.pojo;

public class ImpExpPost extends ImpExpPojo{
	String type;
	
	String code;
	
	String accountName;
	
	String enabled;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
	
}//end class
