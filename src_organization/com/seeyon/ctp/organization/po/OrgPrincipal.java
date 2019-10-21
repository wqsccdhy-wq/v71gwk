package com.seeyon.ctp.organization.po;

import com.seeyon.ctp.common.po.BasePO;


/**
 * This is an object that contains data related to the ORG_PRINCIPAL table.
 *
 *  人员账号表 
 *
 * @hibernate.class
 *  table="ORG_PRINCIPAL"
 */
public class OrgPrincipal extends BasePO {

/*[IDTCODE MARKER BEGIN]*/


  // fields
  /**
   *  登录名 
   */
  private java.lang.String _loginName;
  /**
   *  密码值 
   */
  private java.lang.String _credentialValue;
  /**
   *  加密策略 
   */
  private java.lang.String _className;
  /**
   *  密码超期时间 
   */
  private java.util.Date _expirationDate;
  /**
   *  人员Id 
   */
  private java.lang.Long _memberId;
  /**
   *  是否有效 
   *  @deprecated 废弃，无效账号，直接删除Principal数据
   */
  private java.lang.Boolean _enable = false;
  /**
   *  创建时间 
   */
  private java.util.Date _createTime;
  /**
   *  更新时间 
   */
  private java.util.Date _updateTime;


  // constructors
  public OrgPrincipal () {
    initialize();
  }

  /**
   * Constructor for primary key
   */
  public OrgPrincipal (java.lang.Long _id) {
    this.setId(_id);
    initialize();
  }

  protected void initialize () {}



  /**
   *  登录名 
   */
  public java.lang.String getLoginName () {
    return _loginName;
  }

  /**
   *  登录名 
   * @param _loginName the LOGIN_NAME value
   */
  public void setLoginName (java.lang.String _loginName) {
    this._loginName = _loginName;
  }


  /**
   *  密码值 
   */
  public java.lang.String getCredentialValue () {
    return _credentialValue;
  }

  /**
   *  密码值 
   * @param _credentialValue the CREDENTIAL_VALUE value
   */
  public void setCredentialValue (java.lang.String _credentialValue) {
    this._credentialValue = _credentialValue;
  }


  /**
   *  加密策略 
   */
  public java.lang.String getClassName () {
    return _className;
  }

  /**
   *  加密策略 
   * @param _className the CLASS_NAME value
   */
  public void setClassName (java.lang.String _className) {
    this._className = _className;
  }


  /**
   *  密码超期时间 
   */
  public java.util.Date getExpirationDate () {
    return _expirationDate;
  }

  /**
   *  密码超期时间 
   * @param _expirationDate the EXPIRATION_DATE value
   */
  public void setExpirationDate (java.util.Date _expirationDate) {
    this._expirationDate = _expirationDate;
  }


  /**
   *  人员Id 
   */
  public java.lang.Long getMemberId () {
    return _memberId;
  }

  /**
   *  人员Id 
   * @param _memberId the MEMBER_ID value
   */
  public void setMemberId (java.lang.Long _memberId) {
    this._memberId = _memberId;
  }


  /**
   *  是否有效 
   */
  public java.lang.Boolean isEnable () {
    return _enable;
  }

  /**
   *  是否有效 
   * @param _enable the IS_ENABLE value
   */
  public void setEnable (java.lang.Boolean _enable) {
    this._enable = _enable;
  }


  /**
   *  创建时间 
   */
  public java.util.Date getCreateTime () {
    return _createTime;
  }

  /**
   *  创建时间 
   * @param _createTime the CREATE_TIME value
   */
  public void setCreateTime (java.util.Date _createTime) {
    this._createTime = _createTime;
  }


  /**
   *  更新时间 
   */
  public java.util.Date getUpdateTime () {
    return _updateTime;
  }

  /**
   *  更新时间 
   * @param _updateTime the UPDATE_TIME value
   */
  public void setUpdateTime (java.util.Date _updateTime) {
    this._updateTime = _updateTime;
  }


/*[IDTCODE MARKER END]*/

}