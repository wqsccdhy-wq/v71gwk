/**
 * WebV3xOrgAccount.java
 */
package com.seeyon.ctp.organization.webmodel;

import com.seeyon.ctp.organization.bo.V3xOrgAccount;

public class WebV3xOrgAccount {

    private String        superiorName;
    private V3xOrgAccount v3xOrgAccount;
    private String        chiefLeader;
    private String        address;
    private String        zipCode;
    private String        telephone;
    private String        fax;
    private String        ipAddress;
    private String        accountMail;
    private String        adminPass;
    private String        accountCategory;
    private String        accountLevel;
    private String        accountNature;
    private String        adminiLevel;    //行政级别
    private String        loginName;      //管理員登錄名
    private Long          sortId;
    private String        shortName;
    private String        code;
    /** 为单位树展现增加三个属性 */
    private Long          id;
    private String        name;
    private Long          parentId;
    private Long          level;
    private String        iconSkin = "";
    private Boolean       enabled  = true;

	public WebV3xOrgAccount(){}

    public WebV3xOrgAccount(Long id, String name, Long parentId) {
        super();
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    /**
     * @return Returns the v3xOrgAccount.
     */
    public V3xOrgAccount getV3xOrgAccount() {
        return v3xOrgAccount;
    }

    /**
     * @param orgAccount The v3xOrgAccount to set.
     */
    public void setV3xOrgAccount(V3xOrgAccount orgAccount) {
        v3xOrgAccount = orgAccount;
    }
    
    public String getIconSkin() {
		return iconSkin;
	}

	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}

    /**
     * @return Returns the superiorName.
     */
    public String getSuperiorName() {
        return superiorName;
    }

    /**
     * @param superiorName The superiorName to set.
     */
    public void setSuperiorName(String superiorName) {
        this.superiorName = superiorName;
    }

    public String getAccountMail() {
        return accountMail;
    }

    public void setAccountMail(String accountMail) {
        this.accountMail = accountMail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getChiefLeader() {
        return chiefLeader;
    }

    public void setChiefLeader(String chiefLeader) {
        this.chiefLeader = chiefLeader;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAdminPass() {
        return adminPass;
    }

    public void setAdminPass(String adminPass) {
        this.adminPass = adminPass;
    }

    public String getAccountCategory() {
        return accountCategory;
    }

    public void setAccountCategory(String accountCategory) {
        this.accountCategory = accountCategory;
    }

    public String getAccountLevel() {
        return accountLevel;
    }

    public void setAccountLevel(String accountLevel) {
        this.accountLevel = accountLevel;
    }

    public String getAccountNature() {
        return accountNature;
    }

    public void setAccountNature(String accountNature) {
        this.accountNature = accountNature;
    }

    public String getAdminiLevel() {
        return adminiLevel;
    }

    public void setAdminiLevel(String adminiLevel) {
        this.adminiLevel = adminiLevel;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Long getSortId() {
        return sortId;
    }

    public void setSortId(Long sortId) {
        this.sortId = sortId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
