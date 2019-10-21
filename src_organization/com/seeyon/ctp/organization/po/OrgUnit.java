package com.seeyon.ctp.organization.po;

import org.hibernate.annotations.Columns;

import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;


/**
 * This is an object that contains data related to the ORG_UNIT table.
 *
 *  机构 
 *
 * @hibernate.class
 *  table="ORG_UNIT"
 */
public class OrgUnit extends BasePO {

/*[IDTCODE MARKER BEGIN]*/


// fields
  /**
   *  名称 
   */
  private java.lang.String _name;
  /**
   *  第二名称 
   */
  private java.lang.String _secondName;
  /**
   *  编号 
   */
  private java.lang.String _code;
  /**
   *  简称 
   */
  private java.lang.String _shortName;
  /**
   *  机构类型: OrgConstants.UnitType(Account,Department) 
   */
  private java.lang.String _type;
  /**
   *  是否是集团 
   */
  private java.lang.Boolean _group = false;
  /**
   *  路径 
   */
  private java.lang.String _path;
  /**
   *  是否是内部机构 
   */
  private java.lang.Boolean _internal = false;
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
   *  0 - 正常
1 - 停用
2 - 删除 
   */
  private java.lang.Integer _status;
  
  /**
   * V-Join元素类型
   */
  private Integer externalType  = OrgConstants.ExternalType.Inner.ordinal();
  /**
   *  只对type=account有效 
   */
  private java.lang.Long _levelScope;
  private java.lang.Long _orgAccountId;
  
  
  /**
   *  是否公开
   */
  private java.lang.Boolean isPublic = false;
  
  /**
   *  创建者id
   */
  private java.lang.Long createrId;
  
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
   *  预留字段1 
   */
  private java.lang.String _extAttr1;
  /**
   *  预留字段2 
   */
  private java.lang.String _extAttr2;
  /**
   *  预留字段3 
   */
  private java.lang.String _extAttr3;
  /**
   *  预留字段4 
   */
  private java.lang.String _extAttr4;
  /**
   *  预留字段5 
   */
  private java.lang.String _extAttr5;
  /**
   *  预留字段6 
   */
  private java.lang.String _extAttr6;
  /**
   *  预留字段7 
   */
  private java.lang.String _extAttr7;
  /**
   *  预留字段8 
   */
  private java.lang.String _extAttr8;
  /**
   *  预留字段9 
   */
  private java.lang.String _extAttr9;
  /**
   *  预留字段10 
   */
  private java.lang.String _extAttr10;
  /**
   *  预留字段11 
   */
  private java.lang.Long _extAttr11;
  /**
   *  预留字段12 
   */
  private java.lang.Long _extAttr12;
  /**
   *  预留字段13 
   */
  private java.lang.Long _extAttr13;
  /**
   *  预留字段14 
   */
  private java.lang.Long _extAttr14;
  /**
   *  预留字段15 
   */
  private java.lang.Long _extAttr15;


  // constructors
  public OrgUnit () {
    initialize();
  }

  /**
   * Constructor for primary key
   */
  public OrgUnit (java.lang.Long _id) {
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
   *  第二名称 
   */
  public java.lang.String getSecondName () {
    return _secondName;
  }

  /**
   *  第二名称 
   * @param _secondName the SECOND_NAME value
   */
  public void setSecondName (java.lang.String _secondName) {
    this._secondName = _secondName;
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
   *  简称 
   */
  public java.lang.String getShortName () {
    return _shortName;
  }

  /**
   *  简称 
   * @param _shortName the SHORT_NAME value
   */
  public void setShortName (java.lang.String _shortName) {
    this._shortName = _shortName;
  }


  /**
   *  机构类型: OrgConstants.UnitType(Account,Department) 
   */
  public java.lang.String getType () {
    return _type;
  }

  /**
   *  机构类型: OrgConstants.UnitType(Account,Department) 
   * @param _机构类型 the TYPE value
   */
  public void setType (java.lang.String _type) {
    this._type = _type;
  }


  /**
   *  是否是集团 
   */
  public java.lang.Boolean isGroup () {
    return _group;
  }

  /**
   *  是否是集团 
   * @param _group the IS_GROUP value
   */
  public void setGroup (java.lang.Boolean _group) {
    this._group = _group;
  }


  /**
   *  路径 
   */
  public java.lang.String getPath () {
    return _path;
  }

  /**
   *  路径 
   * @param _path the PATH value
   */
  public void setPath (java.lang.String _path) {
    this._path = _path;
  }


  /**
   *  是否是内部机构 
   */
  public java.lang.Boolean isInternal () {
    return _internal;
  }

  /**
   *  是否是内部机构 
   * @param _internal the IS_INTERNAL value
   */
  public void setInternal (java.lang.Boolean _internal) {
    this._internal = _internal;
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
   *  0 - 正常
1 - 停用
2 - 删除 
   */
  public java.lang.Integer getStatus () {
    return _status;
  }

  /**
   *  0 - 正常
1 - 停用
2 - 删除 
   * @param _status the STATUS value
   */
  public void setStatus (java.lang.Integer _status) {
    this._status = _status;
  }


  /**
   *  只对type=account有效 
   */
  public java.lang.Long getLevelScope () {
    return _levelScope;
  }

  /**
   *  只对type=account有效 
   * @param _levelScope the LEVEL_SCOPE value
   */
  public void setLevelScope (java.lang.Long _levelScope) {
    this._levelScope = _levelScope;
  }


  public java.lang.Long getOrgAccountId () {
    return _orgAccountId;
  }

  /**
   * @param _orgAccountId the ORG_ACCOUNT_ID value
   */
  public void setOrgAccountId (java.lang.Long _orgAccountId) {
    this._orgAccountId = _orgAccountId;
  }

	public java.lang.Boolean getIsPublic() {
		return isPublic;
	}
	
	public void setIsPublic(java.lang.Boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	public java.lang.Long getCreaterId() {
		return createrId;
	}
	
	public void setCreaterId(java.lang.Long createrId) {
		this.createrId = createrId;
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
   *  预留字段1 
   */
  public java.lang.String getExtAttr1 () {
    return _extAttr1;
  }

  /**
   *  预留字段1 
   * @param _extAttr1 the EXT_ATTR_1 value
   */
  public void setExtAttr1 (java.lang.String _extAttr1) {
    this._extAttr1 = _extAttr1;
  }


  /**
   *  预留字段2 
   */
  public java.lang.String getExtAttr2 () {
    return _extAttr2;
  }

  /**
   *  预留字段2 
   * @param _extAttr2 the EXT_ATTR_2 value
   */
  public void setExtAttr2 (java.lang.String _extAttr2) {
    this._extAttr2 = _extAttr2;
  }


  /**
   *  预留字段3 
   */
  public java.lang.String getExtAttr3 () {
    return _extAttr3;
  }

  /**
   *  预留字段3 
   * @param _extAttr3 the EXT_ATTR_3 value
   */
  public void setExtAttr3 (java.lang.String _extAttr3) {
    this._extAttr3 = _extAttr3;
  }


  /**
   *  预留字段4 
   */
  public java.lang.String getExtAttr4 () {
    return _extAttr4;
  }

  /**
   *  预留字段4 
   * @param _extAttr4 the EXT_ATTR_4 value
   */
  public void setExtAttr4 (java.lang.String _extAttr4) {
    this._extAttr4 = _extAttr4;
  }


  /**
   *  预留字段5 
   */
  public java.lang.String getExtAttr5 () {
    return _extAttr5;
  }

  /**
   *  预留字段5 
   * @param _extAttr5 the EXT_ATTR_5 value
   */
  public void setExtAttr5 (java.lang.String _extAttr5) {
    this._extAttr5 = _extAttr5;
  }


  /**
   *  预留字段6 
   */
  public java.lang.String getExtAttr6 () {
    return _extAttr6;
  }

  /**
   *  预留字段6 
   * @param _extAttr6 the EXT_ATTR_6 value
   */
  public void setExtAttr6 (java.lang.String _extAttr6) {
    this._extAttr6 = _extAttr6;
  }


  /**
   *  预留字段7 
   */
  public java.lang.String getExtAttr7 () {
    return _extAttr7;
  }

  /**
   *  预留字段7 
   * @param _extAttr7 the EXT_ATTR_7 value
   */
  public void setExtAttr7 (java.lang.String _extAttr7) {
    this._extAttr7 = _extAttr7;
  }


  /**
   *  预留字段8 
   */
  public java.lang.String getExtAttr8 () {
    return _extAttr8;
  }

  /**
   *  预留字段8 
   * @param _extAttr8 the EXT_ATTR_8 value
   */
  public void setExtAttr8 (java.lang.String _extAttr8) {
    this._extAttr8 = _extAttr8;
  }


  /**
   *  预留字段9 
   */
  public java.lang.String getExtAttr9 () {
    return _extAttr9;
  }

  /**
   *  预留字段9 
   * @param _extAttr9 the EXT_ATTR_9 value
   */
  public void setExtAttr9 (java.lang.String _extAttr9) {
    this._extAttr9 = _extAttr9;
  }


  /**
   *  预留字段10 
   */
  public java.lang.String getExtAttr10 () {
    return _extAttr10;
  }

  /**
   *  预留字段10 
   * @param _extAttr10 the EXT_ATTR_10 value
   */
  public void setExtAttr10 (java.lang.String _extAttr10) {
    this._extAttr10 = _extAttr10;
  }


  /**
   *  预留字段11 
   */
  public java.lang.Long getExtAttr11 () {
    return _extAttr11;
  }

  /**
   *  预留字段11 
   * @param _extAttr11 the EXT_ATTR_11 value
   */
  public void setExtAttr11 (java.lang.Long _extAttr11) {
    this._extAttr11 = _extAttr11;
  }


  /**
   *  预留字段12 
   */
  public java.lang.Long getExtAttr12 () {
    return _extAttr12;
  }

  /**
   *  预留字段12 
   * @param _extAttr12 the EXT_ATTR_12 value
   */
  public void setExtAttr12 (java.lang.Long _extAttr12) {
    this._extAttr12 = _extAttr12;
  }


  /**
   *  预留字段13 
   */
  public java.lang.Long getExtAttr13 () {
    return _extAttr13;
  }

  /**
   *  预留字段13 
   * @param _extAttr13 the EXT_ATTR_13 value
   */
  public void setExtAttr13 (java.lang.Long _extAttr13) {
    this._extAttr13 = _extAttr13;
  }


  /**
   *  预留字段14 
   */
  public java.lang.Long getExtAttr14 () {
    return _extAttr14;
  }

  /**
   *  预留字段14 
   * @param _extAttr14 the EXT_ATTR_14 value
   */
  public void setExtAttr14 (java.lang.Long _extAttr14) {
    this._extAttr14 = _extAttr14;
  }


  /**
   *  预留字段15 
   */
  public java.lang.Long getExtAttr15 () {
    return _extAttr15;
  }

  /**
   *  预留字段15 
   * @param _extAttr15 the EXT_ATTR_15 value
   */
  public void setExtAttr15 (java.lang.Long _extAttr15) {
    this._extAttr15 = _extAttr15;
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