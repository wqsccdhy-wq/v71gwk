package com.seeyon.ctp.ociporg.po;
// Generated 2019-8-10 12:00:00 by Hibernate Tools 5.3.0.Beta2

import java.util.Date;

/**
 * OcipResourceTemp generated by hbm2java
 */
public class OcipResourceTemp implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6972694830875956100L;
    private String id;
    private String sysName;
    private String code;
    private String ip;
    private String port;
    private String appName;
    private String remark;
    private String majorUser;
    private String contactInfo;
    private int isEnable;
    private Integer sortId;
    private Integer delFlag;
    private Date createTime;
    private Date updateTime;
    private String provider;
    private Long authorizedNumber;
    private Short isFlag;
    private Integer conValue;

    public OcipResourceTemp() {}

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSysName() {
        return this.sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMajorUser() {
        return this.majorUser;
    }

    public void setMajorUser(String majorUser) {
        this.majorUser = majorUser;
    }

    public String getContactInfo() {
        return this.contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public int getIsEnable() {
        return this.isEnable;
    }

    public void setIsEnable(int isEnable) {
        this.isEnable = isEnable;
    }

    public Integer getSortId() {
        return this.sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public Integer getDelFlag() {
        return this.delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Long getAuthorizedNumber() {
        return this.authorizedNumber;
    }

    public void setAuthorizedNumber(Long authorizedNumber) {
        this.authorizedNumber = authorizedNumber;
    }

    public Short getIsFlag() {
        return this.isFlag;
    }

    public void setIsFlag(Short isFlag) {
        this.isFlag = isFlag;
    }

    public Integer getconValue() {
        return this.conValue;
    }

    public void setconValue(Integer conValue) {
        this.conValue = conValue;
    }

}
