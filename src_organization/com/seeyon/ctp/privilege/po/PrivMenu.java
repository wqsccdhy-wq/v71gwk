package com.seeyon.ctp.privilege.po;

import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;

/**
 * This is an object that contains data related to the PRIV_MENU table.
 *
 *  要能涵盖菜单资源、外部资源、内容脚本。 
 *
 * @hibernate.class
 *  table="PRIV_MENU"
 */
@SuppressWarnings("serial")
public class PrivMenu extends BasePO {

    // fields
    private java.lang.Integer _sortid;       // 排序号
    private java.lang.String  _name;         // 名称
    private java.lang.Long    _pluginid;     // 插件id
    private java.util.Date    _updatedate;   // 更新时间
    private java.lang.String  _path;         // 路径级别
    private java.lang.Long    _updateuserid; // 更新人员
    private java.lang.Long    _createuserid; // 创建人员
    private java.lang.String  _icon;         // 显示图标
    private java.util.Date    _createdate;   // 创建时间
    private java.lang.String  _target;       // 打开的目标窗口
    private java.lang.Integer _ext5;		 //	原Priv_Resource.Ext5 是否可勾选 0不允许勾选1允许勾选
    private java.lang.Integer _ext6;		//	是否停用0否，1是
    private java.lang.String  _ext1;         // 菜单类型 0 前台 1 后台
    private java.lang.String  _ext2;         // 菜单层级
    private java.lang.String  _ext3;         // 菜单所属版本 
    private java.lang.Integer _ext4;         // 菜单分类, 0 后台管理, 1 前台应用, 2 前台快捷, 3 前台提醒
    private java.lang.Long parentId;         //新增字段，菜单父Id，用来标识菜单的层级关系，一级菜单值为0
    private java.lang.Integer type;          //新增字段，菜单的类别：0：系统预置普通菜单；1：业务生成器菜单2：用户自定义菜单 3：cap4菜单
    // 业务生成器扩展字段
    private java.lang.String  _ext7;         //菜单备注
    private java.lang.String  _ext8;         //原Priv_Resource.Ext1 0 入口资源，1 导航资源，2 其它资源
    private java.lang.String  _ext9;         //原Priv_Resource.Ext2 其它资源的归属资源ID
    private java.lang.String  _ext10;        //原Priv_Resource.Ext3 其它资源的归属资源ID
    private java.lang.String  _ext11;        
    private java.lang.Integer _ext12;       //业务生成器菜单类型 1：流程模板 2：信息管理应用绑定 3：基础数据应用绑定 4：查询ID 5：统计ID 6：文档ID 7：公共信息ID 
    private java.lang.Integer _ext13;       //业务生成器菜单排序 
    private java.lang.Integer _ext14;       //业务生成器菜单类型 只针对流程模板：1：新建 2：列表
    private java.lang.Integer _ext15;		//原Priv_Resource.isShow是否显示,1显示0不显示
    private java.lang.Integer _ext16;		//原Priv_Resource.iscontrol 是否需要控制
    private java.lang.Long    _ext17;       //业务生成器菜单类型资源ID 流程模板ID、应用绑定ID、查询ID、统计ID、文档ID、公共信息ID其中一个。 
    private java.lang.Long    _ext18;        //业务生成器菜单对应表单ID 流程模板、信息管理应用绑定 、基础数据应用绑定有效
    private java.lang.Long    _ext19;       
    private java.lang.Long    _ext20;       //快捷菜单默认标志
    private java.lang.Long    _ext21;       //是否是动态菜单
    
    public PrivMenu(PrivMenuBO bo) {
    	this.id = bo.getId();
    	this._sortid = bo.getSortid();
    	this._name = bo.getName();
    	this._pluginid = bo.getPluginid();
    	this._updatedate = bo.getUpdatedate();
    	this._updateuserid = bo.getUpdateuserid();
    	this._createuserid = bo.getCreateuserid();
    	this._icon = bo.getIcon();
    	this._createdate = bo.getCreatedate();
    	this._target = bo.getTarget();
    	this.parentId = bo.getParentId();
    	this.type = bo.getType();
    	this._path = bo.getPath();
    	this._ext1 = bo.getExt1();
    	this._ext2 = bo.getExt2();
    	this._ext3 = bo.getExt3();
    	this._ext4 = bo.getExt4();
    	this._ext5 = bo.getExt5();
    	this._ext6 = bo.getExt6();
    	this._ext7 = bo.getExt7();
    	this._ext8 = bo.getExt8();
    	this._ext9 = bo.getExt9();
    	this._ext10 = bo.getExt10();
    	this._ext11 = bo.getExt11();
    	this._ext12 = bo.getExt12();
    	this._ext13 = bo.getExt13();
    	this._ext14 = bo.getExt14();
    	this._ext15 = bo.getExt15();
    	this._ext16 = bo.getExt16();
    	this._ext17 = bo.getExt17();
    	this._ext18 = bo.getExt18();
    	this._ext19 = bo.getExt19();
    	this._ext20 = bo.getExt20();
    	this._ext21 = bo.getExt21();
    }
    
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    /**_
     * 是否显示,1显示0不显示 原Priv_Resource.isShow是否显示,1显示0不显示
     */
    private java.lang.Boolean _show;  
    /**
     * 是否可以勾选,1选中0不选中
     */
    private java.lang.Boolean _check; 
    /**
     * P
     */
    private java.lang.Boolean _control; 
    
    private java.lang.Integer _enterResource; //入口资源
    private java.lang.String _resourceModuleid;         //模块id
	private java.lang.String  _resourceNavurl;           //链接
	private java.lang.String  _resourceCode;           //资源编码

    
    // constructors
    public PrivMenu() {
        initialize();
    }

    /**
     * Constructor for primary key
     */
    public PrivMenu(java.lang.Long _id) {
        this.setId(_id);
        initialize();
    }

    protected void initialize() {
    }

    /**
     *  排序号 
     */
    public java.lang.Integer getSortid() {
        return _sortid;
    }

    /**
     *  排序号 
     * @param _sortid the SORTID value
     */
    public void setSortid(java.lang.Integer _sortid) {
        this._sortid = _sortid;
    }

    /**
     *  名称 
     */
    public java.lang.String getName() {
        return _name;
    }

    /**
     *  名称 
     * @param _name the NAME value
     */
    public void setName(java.lang.String _name) {
        this._name = _name;
    }

    /**
     *  插件id 
     */
    public java.lang.Long getPluginid() {
        return _pluginid;
    }

    /**
     *  插件id 
     * @param _pluginid the PLUGINID value
     */
    public void setPluginid(java.lang.Long _pluginid) {
        this._pluginid = _pluginid;
    }

    /**
     *  更新时间 
     */
    public java.util.Date getUpdatedate() {
        return _updatedate;
    }

    /**
     *  更新时间 
     * @param _updatedate the UPDATEDATE value
     */
    public void setUpdatedate(java.util.Date _updatedate) {
        this._updatedate = _updatedate;
    }

    /**
     *  路径级别 
     */
    public java.lang.String getPath() {
        return _path;
    }

    /**
     *  路径级别 
     * @param _path the PATH value
     */
    public void setPath(java.lang.String _path) {
        this._path = _path;
    }

    /**
     *  更新人员 
     */
    public java.lang.Long getUpdateuserid() {
        return _updateuserid;
    }

    /**
     *  更新人员 
     * @param _updateuserid the UPDATEUSERID value
     */
    public void setUpdateuserid(java.lang.Long _updateuserid) {
        this._updateuserid = _updateuserid;
    }

    /**
     *  创建人员 
     */
    public java.lang.Long getCreateuserid() {
        return _createuserid;
    }

    /**
     *  创建人员 
     * @param _createuserid the CREATEUSERID value
     */
    public void setCreateuserid(java.lang.Long _createuserid) {
        this._createuserid = _createuserid;
    }

    /**
     *  显示图标 
     */
    public java.lang.String getIcon() {
        return _icon;
    }

    /**
     *  显示图标 
     * @param _icon the ICON value
     */
    public void setIcon(java.lang.String _icon) {
        this._icon = _icon;
    }

    /**
     *  创建时间 
     */
    public java.util.Date getCreatedate() {
        return _createdate;
    }

    /**
     *  创建时间 
     * @param _createdate the CREATEDATE value
     */
    public void setCreatedate(java.util.Date _createdate) {
        this._createdate = _createdate;
    }

    /**
     *  打开的目标窗口 
     */
    public java.lang.String getTarget() {
        return _target;
    }

    /**
     *  打开的目标窗口 
     * @param _target the TARGET value
     */
    public void setTarget(java.lang.String _target) {
        this._target = _target;
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
     *  菜单类型
     */
    public java.lang.String getExt1() {
        return _ext1;
    }

    /**
     *  菜单类型
     * @param _ext1 the EXT1 value
     */
    public void setExt1(java.lang.String _ext1) {
        this._ext1 = _ext1;
    }

    /**
     *  菜单层级
     */
    public java.lang.String getExt2() {
        return _ext2;
    }

    /**
     *  菜单层级
     * @param _ext2 the EXT2 value
     */
    public void setExt2(java.lang.String _ext2) {
        this._ext2 = _ext2;
    }

    /**
     *  菜单所属版本
     */
    public java.lang.String getExt3() {
        return _ext3;
    }

    /**
     *  菜单所属版本
     * @param _ext3 the EXT3 value
     */
    public void setExt3(java.lang.String _ext3) {
        this._ext3 = _ext3;
    }

    /**
     *  菜单分类
     */
    public java.lang.Integer getExt4() {
        return _ext4;
    }

    /**
     *  菜单分类
     * @param _ext4 the EXT4 value
     */
    public void setExt4(java.lang.Integer _ext4) {
        this._ext4 = _ext4;
    }
    
    /**
     *  业务生成器扩展
     */
    public java.lang.String getExt7() {
        return _ext7;
    }

    /**
     *  业务生成器扩展
     * @param _ext7 the EXT7 value
     */
    public void setExt7(java.lang.String _ext7) {
        this._ext7 = _ext7;
    }
    
    /**
     *  业务生成器扩展
     */
    public java.lang.String getExt8() {
        return _ext8;
    }

    /**
     *  业务生成器扩展
     * @param _ext8 the EXT8 value
     */
    public void setExt8(java.lang.String _ext8) {
        this._ext8 = _ext8;
    }
    
    /**
     *  业务生成器扩展
     */
    public java.lang.String getExt9() {
        return _ext9;
    }

    /**
     *  业务生成器扩展
     * @param _ext9 the EXT9 value
     */
    public void setExt9(java.lang.String _ext9) {
        this._ext9 = _ext9;
    }
    
    /**
     *  业务生成器扩展
     */
    public java.lang.String getExt10() {
        return _ext10;
    }

    /**
     *  业务生成器扩展
     * @param _ext10 the EXT10 value
     */
    public void setExt10(java.lang.String _ext10) {
        this._ext10 = _ext10;
    }
    
    /**
     *  业务生成器扩展
     */
    public java.lang.String getExt11() {
        return _ext11;
    }

    /**
     *  业务生成器扩展
     * @param _ext11 the EXT11 value
     */
    public void setExt11(java.lang.String _ext11) {
        this._ext11 = _ext11;
    }
    
    /**
     *  业务生成器菜单类型 1：流程模板 2：信息管理应用绑定 3：基础数据应用绑定 4：查询ID 5：统计ID 6：文档ID 7：公共信息ID 
     */
    public java.lang.Integer getExt12() {
        return _ext12;
    }

    /**
     *  业务生成器菜单类型 1：流程模板 2：信息管理应用绑定 3：基础数据应用绑定 4：查询ID 5：统计ID 6：文档ID 7：公共信息ID 
     * @param _ext12 the EXT12 value
     */
    public void setExt12(java.lang.Integer _ext12) {
        this._ext12 = _ext12;
    }
    
    /**
     *  业务生成器扩展 业务生成器菜单排序 
     */
    public java.lang.Integer getExt13() {
        return _ext13;
    }

    /**
     *  业务生成器扩展 业务生成器菜单排序 
     * @param _ext13 the EXT13 value
     */
    public void setExt13(java.lang.Integer _ext13) {
        this._ext13 = _ext13;
    }
    
    /**
     *  业务生成器扩展 业务生成器菜单类型 只针对流程模板：1：新建 2：列表
     */
    public java.lang.Integer getExt14() {
        return _ext14;
    }

    /**
     *  业务生成器扩展 业务生成器菜单类型 只针对流程模板：1：新建 2：列表
     * @param _ext14 the EXT14 value
     */
    public void setExt14(java.lang.Integer _ext14) {
        this._ext14 = _ext14;
    }
    
    /**
     *  业务生成器扩展
     */
    public java.lang.Integer getExt15() {
        return _ext15;
    }

    /**
     *  业务生成器扩展
     * @param _ext15 the EXT15 value
     */
    public void setExt15(java.lang.Integer _ext15) {
        this._ext15 = _ext15;
    }
    
    /**
     *  业务生成器扩展
     */
    public java.lang.Integer getExt16() {
        return _ext16;
    }

    /**
     *  业务生成器扩展
     * @param _ext16 the EXT16 value
     */
    public void setExt16(java.lang.Integer _ext16) {
        this._ext16 = _ext16;
    }
    
    /**
     *  业务生成器扩展 业务生成器菜单类型资源ID 流程模板ID、应用绑定ID、查询ID、统计ID、文档ID、公共信息ID其中一个。
     */
    public java.lang.Long getExt17() {
        return _ext17;
    }

    /**
     *  业务生成器扩展 业务生成器菜单类型资源ID 流程模板ID、应用绑定ID、查询ID、统计ID、文档ID、公共信息ID其中一个。
     * @param _ext17 the Ext17 value
     */
    public void setExt17(java.lang.Long _ext17) {
        this._ext17 = _ext17;
    }
    
    /**
     *  业务生成器扩展 业务生成器菜单对应表单ID 流程模板、信息管理应用绑定 、基础数据应用绑定有效
     */
    public java.lang.Long getExt18() {
        return _ext18;
    }

    /**
     *  业务生成器扩展 业务生成器菜单对应表单ID 流程模板、信息管理应用绑定 、基础数据应用绑定有效
     * @param _ext18 the EXT18 value
     */
    public void setExt18(java.lang.Long _ext18) {
        this._ext18 = _ext18;
    }
    
    /**
     *  业务生成器扩展
     */
    public java.lang.Long getExt19() {
        return _ext19;
    }

    /**
     *  业务生成器扩展
     * @param _ext19 the EXT19 value
     */
    public void setExt19(java.lang.Long _ext19) {
        this._ext19 = _ext19;
    }
    
    /**
     *  业务生成器扩展
     */
    public java.lang.Long getExt20() {
        return _ext20;
    }

    /**
     *  业务生成器扩展
     * @param _ext20 the EXT20 value
     */
    public void setExt20(java.lang.Long _ext20) {
        this._ext20 = _ext20;
    }
    
    /**
     *  业务生成器扩展
     */
    public java.lang.Long getExt21() {
        return _ext21;
    }

    /**
     *  业务生成器扩展
     * @param _ext21 the EXT21 value
     */
    public void setExt21(java.lang.Long _ext21) {
        this._ext21 = _ext21;
    }

	/**
     *  入口资源 
     */
    public java.lang.Integer getEnterResource() {
        return _enterResource;
    }

    /**
     *  入口资源 
     * @param _enterResource the ENTER_RESOURCE value
     */
    public void setEnterResource(java.lang.Integer _enterResource) {
        this._enterResource = _enterResource;
    }
  


    /**
     *  模块ID
     */
    public java.lang.String getResourceModuleid() {
        return _resourceModuleid;
    }

    /**
     *  模块ID
     * @param _moduleid the MODULEID value
     */
    public void setResourceModuleid(java.lang.String _resourceModuleid) {
        this._resourceModuleid = _resourceModuleid;
    }

    /**
     *  资源链接
     */

    public java.lang.String getResourceNavurl() {
		return _resourceNavurl;
	}

    /**
     *  资源链接
     * @param _resourceNavurl the ResourceNavurl value
     */
	public void setResourceNavurl(java.lang.String _resourceNavurl) {
		this._resourceNavurl = _resourceNavurl;
	}

	/**
     *  资源代码
     * @param _resourceNavurl the ResourceNavurl value
     */
	public java.lang.String getResourceCode() {
		return _resourceCode;
	}
	/**
     *  资源代码
     * @param _resourceNavurl the ResourceNavurl value
     */
	public void setResourceCode(java.lang.String _resourceCode) {
		this._resourceCode = _resourceCode;
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
}