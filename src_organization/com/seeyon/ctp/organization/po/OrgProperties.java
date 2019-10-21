package com.seeyon.ctp.organization.po;

import com.seeyon.ctp.common.po.BasePO;


/**
 * This is an object that contains data related to the ORG_PROPERTIES table.
 *
 *  属性表 
 *
 * @hibernate.class
 *  table="ORG_PROPERTIES"
 */
public class OrgProperties extends BasePO {

/*[IDTCODE MARKER BEGIN]*/


  // fields
  /**
   *  资源id 
   */
  private java.lang.Long _sourceId;
  /**
   *  名字 
   */
  private java.lang.String _name;
  /**
   *  值 
   */
  private java.lang.String _value;
  /**
   *  类型 
   */
  private java.lang.Integer _type;
  /**
   *  单位id 
   */
  private java.lang.Long _orgAccountId;


  // constructors
  public OrgProperties () {
    initialize();
  }

  /**
   * Constructor for primary key
   */
  public OrgProperties (java.lang.Long _id) {
    this.setId(_id);
    initialize();
  }

  protected void initialize () {}



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
   *  名字 
   */
  public java.lang.String getName () {
    return _name;
  }

  /**
   *  名字 
   * @param _name the NAME value
   */
  public void setName (java.lang.String _name) {
    this._name = _name;
  }


  /**
   *  值 
   */
  public java.lang.String getValue () {
    return _value;
  }

  /**
   *  值 
   * @param _value the VALUE value
   */
  public void setValue (java.lang.String _value) {
    this._value = _value;
  }


  /**
   *  类型 
   */
  public java.lang.Integer getType () {
    return _type;
  }

  /**
   *  类型 
   * @param _type the TYPE value
   */
  public void setType (java.lang.Integer _type) {
    this._type = _type;
  }


  /**
   *  单位id 
   */
  public java.lang.Long getOrgAccountId () {
    return _orgAccountId;
  }

  /**
   *  单位id 
   * @param _orgAccountId the ORG_ACCOUNT_ID value
   */
  public void setOrgAccountId (java.lang.Long _orgAccountId) {
    this._orgAccountId = _orgAccountId;
  }


/*[IDTCODE MARKER END]*/

}