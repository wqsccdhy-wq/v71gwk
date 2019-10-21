package com.seeyon.ctp.organization.po;

import com.seeyon.ctp.common.po.BasePO;


/**
 * This is an object that contains data related to the ORG_RELATIONSHIP table.
 *
 *  关系表 
 *
 * @hibernate.class
 *  table="ORG_RELATIONSHIP"
 */
public class OrgRelationship extends BasePO {

/*[IDTCODE MARKER BEGIN]*/


  // fields
  /**
   *  关系类型 
   */
  private java.lang.String _type;
  /**
   *  资源id 
   */
  private java.lang.Long _sourceId;
  /**
   *  目的 
   */
  private java.lang.Long _objective0Id;
  private java.lang.Long _objective1Id;
  /**
   *  目的2 
   */
  private java.lang.Long _objective2Id;
  /**
   *  目的3 
   */
  private java.lang.Long _objective3Id;
  /**
   *  目的4 
   */
  private java.lang.Long _objective4Id;
  private java.lang.String _objective5Id;
  private java.lang.String _objective6Id;
  private java.lang.String _objective7Id;
  /**
   *  排序 
   */
  private java.lang.Long _sortId;
  /**
   *  所属单位 
   */
  private java.lang.Long _orgAccountId;
  /**
   *  创建时间 
   */
  private java.util.Date _createTime;
  /**
   *  更新时间 
   */
  private java.util.Date _updateTime;


  // constructors
  public OrgRelationship () {
    initialize();
  }

  /**
   * Constructor for primary key
   */
  public OrgRelationship (java.lang.Long _id) {
    this.setId(_id);
    initialize();
  }

  protected void initialize () {}



  /**
   *  关系类型 
   */
  public java.lang.String getType () {
    return _type;
  }

  /**
   *  关系类型 
   * @param _type the TYPE value
   */
  public void setType (java.lang.String _type) {
    this._type = _type;
  }


  /**
   *  资源id 
   */
  public java.lang.Long getSourceId () {
    return _sourceId;
  }

  /**
   *  资源id 
   * @param _sourceId the SOURCE_ID value
   */
  public void setSourceId (java.lang.Long _sourceId) {
    this._sourceId = _sourceId;
  }


  /**
   *  目的 
   */
  public java.lang.Long getObjective0Id () {
    return _objective0Id;
  }

  /**
   *  目的 
   * @param _objective0Id the OBJECTIVE0_ID value
   */
  public void setObjective0Id (java.lang.Long _objective0Id) {
    this._objective0Id = _objective0Id;
  }


  public java.lang.Long getObjective1Id () {
    return _objective1Id;
  }

  /**
   * @param _objective1Id the OBJECTIVE1_ID value
   */
  public void setObjective1Id (java.lang.Long _objective1Id) {
    this._objective1Id = _objective1Id;
  }


  /**
   *  目的2 
   */
  public java.lang.Long getObjective2Id () {
    return _objective2Id;
  }

  /**
   *  目的2 
   * @param _objective2Id the OBJECTIVE2_ID value
   */
  public void setObjective2Id (java.lang.Long _objective2Id) {
    this._objective2Id = _objective2Id;
  }


  /**
   *  目的3 
   */
  public java.lang.Long getObjective3Id () {
    return _objective3Id;
  }

  /**
   *  目的3 
   * @param _objective3Id the OBJECTIVE3_ID value
   */
  public void setObjective3Id (java.lang.Long _objective3Id) {
    this._objective3Id = _objective3Id;
  }


  /**
   *  目的4 
   */
  public java.lang.Long getObjective4Id () {
    return _objective4Id;
  }

  /**
   *  目的4 
   * @param _objective4Id the OBJECTIVE4_ID value
   */
  public void setObjective4Id (java.lang.Long _objective4Id) {
    this._objective4Id = _objective4Id;
  }


  public java.lang.String getObjective5Id () {
    return _objective5Id;
  }

  /**
   * @param _objective5Id the OBJECTIVE5_ID value
   */
  public void setObjective5Id (java.lang.String _objective5Id) {
    this._objective5Id = _objective5Id;
  }


  public java.lang.String getObjective6Id () {
    return _objective6Id;
  }

  /**
   * @param _objective6Id the OBJECTIVE6_ID value
   */
  public void setObjective6Id (java.lang.String _objective6Id) {
    this._objective6Id = _objective6Id;
  }


  public java.lang.String getObjective7Id () {
    return _objective7Id;
  }

  /**
   * @param _objective7Id the OBJECTIVE7_ID value
   */
  public void setObjective7Id (java.lang.String _objective7Id) {
    this._objective7Id = _objective7Id;
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