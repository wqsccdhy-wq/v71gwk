package com.seeyon.ctp.privilege.po;

import com.seeyon.ctp.common.po.BasePO;

/**
 * This is an object that contains data related to the PRIV_COMMONAUTHORITY table.
 *
 *  通用授权 
 *
 * @hibernate.class
 *  table="PRIV_COMMONAUTHORITY"
 */
@SuppressWarnings("serial")
public class PrivCommonauthority extends BasePO {

    // fields
    private java.lang.Long    _resourceid;   //资源id
    private java.util.Date    _endDate;      //结束时间
    private java.util.Date    _beginDate;    //开始时间
    private java.lang.Long    _userType;     //用户类型
    private java.lang.Integer _resourceType; //资源类型
    private java.lang.Long    _userid;       //授权用户id

    // constructors
    public PrivCommonauthority() {
        initialize();
    }

    /**
     * Constructor for primary key
     */
    public PrivCommonauthority(java.lang.Long _id) {
        this.setId(_id);
        initialize();
    }

    protected void initialize() {
    }

    /**
     *  资源id 
     */
    public java.lang.Long getResourceid() {
        return _resourceid;
    }

    /**
     *  资源id 
     * @param _resourceid the RESOURCEID value
     */
    public void setResourceid(java.lang.Long _resourceid) {
        this._resourceid = _resourceid;
    }

    /**
     *  结束时间 
     */
    public java.util.Date getEndDate() {
        return _endDate;
    }

    /**
     *  结束时间 
     * @param _endDate the END_DATE value
     */
    public void setEndDate(java.util.Date _endDate) {
        this._endDate = _endDate;
    }

    /**
     *  开始时间 
     */
    public java.util.Date getBeginDate() {
        return _beginDate;
    }

    /**
     *  开始时间 
     * @param _beginDate the BEGIN_DATE value
     */
    public void setBeginDate(java.util.Date _beginDate) {
        this._beginDate = _beginDate;
    }

    /**
     *  0个人1部门等 
     */
    public java.lang.Long getUserType() {
        return _userType;
    }

    /**
     *  0个人1部门等 
     * @param _userType the USER_TYPE value
     */
    public void setUserType(java.lang.Long _userType) {
        this._userType = _userType;
    }

    /**
     *  资源类型1协同2公文等 
     */
    public java.lang.Integer getResourceType() {
        return _resourceType;
    }

    /**
     *  资源类型1协同2公文等 
     * @param _resourceType the RESOURCE_TYPE value
     */
    public void setResourceType(java.lang.Integer _resourceType) {
        this._resourceType = _resourceType;
    }

    /**
     *  授权用户id 
     */
    public java.lang.Long getUserid() {
        return _userid;
    }

    /**
     *  授权用户id 
     * @param _userid the USERID value
     */
    public void setUserid(java.lang.Long _userid) {
        this._userid = _userid;
    }

}