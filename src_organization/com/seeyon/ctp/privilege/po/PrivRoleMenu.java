package com.seeyon.ctp.privilege.po;

import com.seeyon.ctp.common.po.BasePO;

/**
 * This is an object that contains data related to the PRIV_ROLE_RESOURCE table.
 *
 *  角色资源关系 
 *
 * @hibernate.class
 *  table="PRIV_ROLE_RESOURCE"
 */
public class PrivRoleMenu extends BasePO {

    /*[IDTCODE MARKER BEGIN]*/

    // fields
    /**
     *  资源ID 
     */
    private java.lang.Long    _resourceid;
    
    /**
     * 适用产品线
     */
    private java.lang.Integer _productLine;
    /**
     *  角色ID 
     */
    private java.lang.Long    _roleid;
    /**
     *  菜单ID 
     */
    private java.lang.Long    _menuid;
    /**
     *  是否可修改
     */
    private java.lang.Boolean _modifiable;
    
    /**
     * 是否显示,1显示0不显示
     */
    private java.lang.Boolean _show; 
    /**
     * 是否可以勾选,1显示0不显示
     */
    private java.lang.Boolean _check; 
    /**
     * 是否需要控制
     */
    private java.lang.Boolean _control; 
    /**
     * 扩展字段1 某个产品线 名称
     */
    private java.lang.String  _ext1; 
    /**
     * 扩展字段2 某个产品线 图片
     */
    private java.lang.String  _ext2;  
    /**
     * 扩展字段3 某个产品线 路径级别
     */
    private java.lang.String  _ext3;      
    /**
     * 扩展字段4 某个产品线 排序号
     */
    private java.lang.Integer _ext4;   
    /**
     * 扩展字段5
     */
    private java.lang.Integer _ext5;	
    /**
     * 扩展字段6
     */
    private java.lang.Integer _ext6;
    
    // constructors
    public PrivRoleMenu() {
        initialize();
    }

    
    /**
     * Constructor for primary key
     */
    public PrivRoleMenu(java.lang.Long _id) {
        this.setId(_id);
        initialize();
    }
    /**
     * Constructor for primary key
     */
    public PrivRoleMenu(java.lang.Integer _pl) {
        this.setProductLine(_pl);
        initialize();
    }
    
    protected void initialize() {
    }

    /**
     *  资源ID 
     */
    public java.lang.Long getResourceid() {
        return _resourceid;
    }

    /**
     *  资源ID 
     * @param _resourceid the RESOURCEID value
     */
    public void setResourceid(java.lang.Long _resourceid) {
        this._resourceid = _resourceid;
    }

    /**
     *  角色ID 
     */
    public java.lang.Long getRoleid() {
        return _roleid;
    }

    /**
     *  角色ID 
     * @param _roleid the ROLEID value
     */
    public void setRoleid(java.lang.Long _roleid) {
        this._roleid = _roleid;
    }

    /**
     *  菜单ID 
     */
    public java.lang.Long getMenuid() {
        return _menuid;
    }

    /**
     *  菜单ID 
     * @param _menuid the MENUID value
     */
    public void setMenuid(java.lang.Long _menuid) {
        this._menuid = _menuid;
    }

    /**
     * @return the _modifiable
     */
    public java.lang.Boolean getModifiable() {
        return _modifiable;
    }

    /**
     * @param _modifiable the _modifiable to set
     */
    public void setModifiable(java.lang.Boolean _modifiable) {
        this._modifiable = _modifiable;
    }
    /**
     *  0显示1不显示
     */
    public java.lang.Boolean isShow() {
        return _show;
    }

    /**
     *  0显示1不显示
     * @param _show the IS_SHOW value
     */
    public void setCheck(java.lang.Boolean _check) {
        this._check = _check;
    }
    /**
     *  0显示1不显示
     */
    public java.lang.Boolean isCheck() {
        return _check;
    }

    /**
     *  0显示1不显示
     * @param _show the IS_SHOW value
     */
    public void setShow(java.lang.Boolean _show) {
        this._show = _show;
    }
    /*[IDTCODE MARKER END]*/
    /**
     *  是否需要控制
     */
    public java.lang.Boolean isControl() {
        return _control;
    }

    /**
     *  是否需要控制
     * @param _control the IS_CONTROL value
     */
    public void setControl(java.lang.Boolean _control) {
        this._control = _control;
    }
    /**
     *  扩展字段5
     */
    public java.lang.Integer getExt5() {
        return _ext5;
    }

    /**
     *  扩展字段5
     * @param _ext5 the EXT5 value
     */
    public void setExt5(java.lang.Integer _ext5) {
        this._ext5 = _ext5;
    }

    /**
     *  扩展字段6
     */
    public java.lang.Integer getExt6() {
        return _ext6;
    }

    /**
     *  扩展字段6
     * @param _ext6 the EXT6 value
     */
    public void setExt6(java.lang.Integer _ext6) {
        this._ext6 = _ext6;
    }

    /**
     *  扩展字段1
     */
    public java.lang.String getExt1() {
        return _ext1;
    }

    /**
     *  扩展字段1
     * @param _ext1 the EXT1 value
     */
    public void setExt1(java.lang.String _ext1) {
        this._ext1 = _ext1;
    }

    /**
     *  扩展字段2
     */
    public java.lang.String getExt2() {
        return _ext2;
    }

    /**
     *  扩展字段2
     * @param _ext2 the EXT2 value
     */
    public void setExt2(java.lang.String _ext2) {
        this._ext2 = _ext2;
    }

    /**
     *  扩展字段3
     */
    public java.lang.String getExt3() {
        return _ext3;
    }

    /**
     *  扩展字段3
     * @param _ext3 the EXT3 value
     */
    public void setExt3(java.lang.String _ext3) {
        this._ext3 = _ext3;
    }

    /**
     *  扩展字段4
     */
    public java.lang.Integer getExt4() {
        return _ext4;
    }

    /**
     *  扩展字段4
     * @param _ext4 the EXT4 value
     */
    public void setExt4(java.lang.Integer _ext4) {
        this._ext4 = _ext4;
    }

	public java.lang.Integer getProductLine() {
		return _productLine;
	}

	public void setProductLine(java.lang.Integer _productLine) {
		this._productLine = _productLine;
	}

}