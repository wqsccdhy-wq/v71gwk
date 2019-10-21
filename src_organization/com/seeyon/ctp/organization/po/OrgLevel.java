package com.seeyon.ctp.organization.po;

import com.seeyon.ctp.common.po.BasePO;


/**
 * This is an object that contains data related to the ORG_LEVEL table.
 *
 *  职务级别表 
 *
 * @hibernate.class
 *  table="ORG_LEVEL"
 */
public class OrgLevel extends BasePO {

/*[IDTCODE MARKER BEGIN]*/


  // fields
  /**
   *  名称 
   */
  private java.lang.String _name;
  /**
   *  编码 
   */
  private java.lang.String _code;
  /**
   *  是否启用 
   */
  private java.lang.Boolean _enable = false;
  /**
   *  级别序号 
   */
  private java.lang.Long _levelId;
  /**
   *  集团职务级别号 
   */
  private java.lang.Long _groupLevelId;
  /**
   *  排序 
   */
  private java.lang.Long _sortId;
  /**
   *  创建时间 
   */
  private java.util.Date _createTime;
  /**
   *  修改时间 
   */
  private java.util.Date _updateTime;
  /**
   *  描述 
   */
  private java.lang.String _description;
  /**
   *  所属单位 
   */
  private java.lang.Long _orgAccountId;
  /**
   *  是否被删除 
   */
  private java.lang.Boolean _deleted = false;
  /**
   *  状态 
   */
  private java.lang.Integer _status;


  // constructors
  public OrgLevel () {
    initialize();
  }

  /**
   * Constructor for primary key
   */
  public OrgLevel (java.lang.Long _id) {
    this.setId(_id);
    initialize();
  }

  protected void initialize () {}



  /**
   *  名称 
   */
  public java.lang.String getName () {
    return _name;
  }

  /**
   *  名称 
   * @param _name the NAME value
   */
  public void setName (java.lang.String _name) {
    this._name = _name;
  }


  /**
   *  编码 
   */
  public java.lang.String getCode () {
    return _code;
  }

  /**
   *  编码 
   * @param _code the CODE value
   */
  public void setCode (java.lang.String _code) {
    this._code = _code;
  }


  /**
   *  是否启用 
   */
  public java.lang.Boolean isEnable () {
    return _enable;
  }

  /**
   *  是否启用 
   * @param _enable the IS_ENABLE value
   */
  public void setEnable (java.lang.Boolean _enable) {
    this._enable = _enable;
  }


  /**
   *  级别序号 
   */
  public java.lang.Long getLevelId () {
    return _levelId;
  }

  /**
   *  级别序号 
   * @param _levelId the LEVEL_ID value
   */
  public void setLevelId (java.lang.Long _levelId) {
    this._levelId = _levelId;
  }


  /**
   *  集团职务级别号 
   */
  public java.lang.Long getGroupLevelId () {
    return _groupLevelId;
  }

  /**
   *  集团职务级别号 
   * @param _groupLevelId the GROUP_LEVEL_ID value
   */
  public void setGroupLevelId (java.lang.Long _groupLevelId) {
    this._groupLevelId = _groupLevelId;
  }


  /**
   *  排序 
   */
  public java.lang.Long getSortId () {
    return _sortId;
  }

  /**
   *  排序 
   * @param _sortId the SORT_ID value
   */
  public void setSortId (java.lang.Long _sortId) {
    this._sortId = _sortId;
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
   *  修改时间 
   */
  public java.util.Date getUpdateTime () {
    return _updateTime;
  }

  /**
   *  修改时间 
   * @param _updateTime the UPDATE_TIME value
   */
  public void setUpdateTime (java.util.Date _updateTime) {
    this._updateTime = _updateTime;
  }


  /**
   *  描述 
   */
  public java.lang.String getDescription () {
    return _description;
  }

  /**
   *  描述 
   * @param _description the DESCRIPTION value
   */
  public void setDescription (java.lang.String _description) {
    this._description = _description;
  }


  /**
   *  所属单位 
   */
  public java.lang.Long getOrgAccountId () {
    return _orgAccountId;
  }

  /**
   *  所属单位 
   * @param _orgAccountId the ORG_ACCOUNT_ID value
   */
  public void setOrgAccountId (java.lang.Long _orgAccountId) {
    this._orgAccountId = _orgAccountId;
  }


  /**
   *  是否被删除 
   */
  public java.lang.Boolean isDeleted () {
    return _deleted;
  }

  /**
   *  是否被删除 
   * @param _deleted the IS_DELETED value
   */
  public void setDeleted (java.lang.Boolean _deleted) {
    this._deleted = _deleted;
  }


  /**
   *  状态 
   */
  public java.lang.Integer getStatus () {
    return _status;
  }

  /**
   *  状态 
   * @param _status the STATUS value
   */
  public void setStatus (java.lang.Integer _status) {
    this._status = _status;
  }


/*[IDTCODE MARKER END]*/

}