package com.seeyon.ctp.organization.po;

import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;


/**
 * This is an object that contains data related to the ORG_ROLE table.
 *
 *  角色表 
 *
 * @hibernate.class
 *  table="ORG_ROLE"
 */
public class OrgRole extends BasePO {

/*[IDTCODE MARKER BEGIN]*/


  // fields
  /**
   *  名称 
   */
  private java.lang.String _name;
  /**
   *  是否可用于前台授权
   */
  private java.lang.String _category;
  /**
   *  编号 
   */
  private java.lang.String _code;
  /**
   *  固定角色、相对角色、自建角色 
   */
  private java.lang.Integer _type;
  /**
   *  单位角色/部门角色 
   */
  private java.lang.Integer _bond;
  /**
   *  是否是基准角色 
   */
  private java.lang.Boolean _benchmark = false;
  /**
   *  所属单位 
   */
  private java.lang.Long _orgAccountId;
  /**
   *  排序 
   */
  private java.lang.Long _sortId;
  /**
   *  是否启用 
   */
  private java.lang.Boolean _enable = false;
  /**
   *  是否被删除 
   */
  private java.lang.Boolean _deleted = false;
  /**
   *  是否可用于选人界面
   */
  private java.lang.Integer _status;
  /**
   *  创建时间 
   */
  private java.util.Date _createTime;
  /**
   *  更新时间 
   */
  private java.util.Date _updateTime;
  /**
   *  描述 
   */
  private java.lang.String _description;
  
  /**
   * V-Join元素类型
   */
  private Integer externalType  = OrgConstants.ExternalType.Inner.ordinal();


  // constructors
  public OrgRole () {
    initialize();
  }

  /**
   * Constructor for primary key
   */
  public OrgRole (java.lang.Long _id) {
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
   *  所属分类 
   */
  public java.lang.String getCategory () {
    return _category;
  }

  /**
   *  所属分类 
   * @param _category the CATEGORY value
   */
  public void setCategory (java.lang.String _category) {
    this._category = _category;
  }


  /**
   *  编号 
   */
  public java.lang.String getCode () {
    return _code;
  }

  /**
   *  编号 
   * @param _code the CODE value
   */
  public void setCode (java.lang.String _code) {
    this._code = _code;
  }


  /**
   *  固定角色、相对角色、自建角色 
   */
  public java.lang.Integer getType () {
    return _type;
  }

  /**
   *  固定角色、相对角色、自建角色 
   * @param _type the TYPE value
   */
  public void setType (java.lang.Integer _type) {
    this._type = _type;
  }


  /**
   *  单位角色/部门角色 
   */
  public java.lang.Integer getBond () {
    return _bond;
  }

  /**
   *  单位角色/部门角色 
   * @param _bond the BOND value
   */
  public void setBond (java.lang.Integer _bond) {
    this._bond = _bond;
  }


  /**
   *  是否是基准角色 
   */
  public java.lang.Boolean isBenchmark () {
    return _benchmark;
  }

  /**
   *  是否是基准角色 
   * @param _benchmark the IS_BENCHMARK value
   */
  public void setBenchmark (java.lang.Boolean _benchmark) {
    this._benchmark = _benchmark;
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
   *  枚举：启用、停用、删除 
   */
  public java.lang.Integer getStatus () {
    return _status;
  }

  /**
   *  枚举：启用、停用、删除 
   * @param _status the STATUS value
   */
  public void setStatus (java.lang.Integer _status) {
    this._status = _status;
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

public Integer getExternalType() {
	if(externalType==null){
		externalType = OrgConstants.ExternalType.Inner.ordinal();
	}
	return externalType;
}

public void setExternalType(Integer externalType) {
	this.externalType = externalType;
}


/*[IDTCODE MARKER END]*/

}