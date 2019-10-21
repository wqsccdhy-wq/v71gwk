/**
 * $Author: gaohang $
 * $Rev: 20025 $
 * $Date:: 2013-04-20 13:36:02#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.bo;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.privilege.po.PrivMenu;
import com.seeyon.ctp.util.StringUtil;

/**
 * <p>Title: 菜单BO类</p>
 * <p>Description: 本程序实现菜单BO对象</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public class PrivMenuBO extends PrivMenu {

    /** */
    private static final long serialVersionUID    = -2248353282574149175L;

    /** 菜单路径path代表一个层级的数字长度*/
//    public static int         pathLength          = 3;

    /** 
     * 菜单的入口资源ID
     * 每个菜单必有一个资源作为入口资源
     */
    private Long              enterResourceId;

    private String            enterResourceName;

    /** 是否是虚节点 */
    private Boolean           isVirtualNode;

    /** 菜单级别 */
    private String            menuLevel;
    private String configFrom;

    public String getConfigFrom() {
        return configFrom;
    }

    public void setConfigFrom(String configFrom) {
        this.configFrom = configFrom;
    }

    public Integer getMenuLevelInteger() {
        return menuLevelInteger;
    }

    public void setMenuLevelInteger(Integer menuLevelInteger) {
        this.menuLevelInteger = menuLevelInteger;
    }

    private Integer menuLevelInteger;

    public List<PrivMenuBO> getPrivMenuBOItems() {
        return privMenuBOItems;
    }

    public void setPrivMenuBOItems(List<PrivMenuBO> privMenuBOItems) {
        this.privMenuBOItems = privMenuBOItems;
    }

    //菜单下对应的子菜单
    private List<PrivMenuBO> privMenuBOItems=new ArrayList<PrivMenuBO>();

    /** 菜单名称 */
    private String            name;

    

	/** 父菜单ID */
    private Long              parentId            = 0l;

    /** 挂接的导航资源ID数组 */
    private List<Long>        naviResourceIds     = new ArrayList<Long>();

    /** 挂接的快捷资源ID数组 */
    private List<Long>        shortcutResourceIds = new ArrayList<Long>();

    /** 业务生成器添加的菜单访问的资源链接 */
    private String            url;
    
    /**菜单国际化name key*/
    private String resourceNameKey;

    public PrivMenuBO() {
        super();
    }

    public PrivMenuBO(Long id) {
        super(id);
    }

    public PrivMenuBO(PrivMenu privMenu) {
        this.fromPO(privMenu);
    }

    /**
     * 将菜单PO对象转换为BO对象
     * @param po 菜单PO对象
     * @return 菜单BO对象
     */
    public PrivMenuBO fromPO(BasePO po) {
        PrivMenu privMenu = (PrivMenu) po;
        this.id = privMenu.getId();
        this.setSortid(privMenu.getSortid());
        this.setName(privMenu.getName());
        this.setPluginid(privMenu.getPluginid());
        this.setUpdatedate(privMenu.getUpdatedate());
        this.setPath(privMenu.getPath());
        this.setUpdateuserid(privMenu.getUpdateuserid());
        this.setIcon(privMenu.getIcon());
        this.setCreatedate(privMenu.getCreatedate());
        this.setTarget(privMenu.getTarget());
        this.setExt5(privMenu.getExt5());
        this.setExt6(privMenu.getExt6());
        this.setExt1(privMenu.getExt1());
        this.setExt2(privMenu.getExt2());
        this.setExt3(privMenu.getExt3());
        this.setExt4(privMenu.getExt4());
        this.setExt7(privMenu.getExt7());
        this.setExt8(privMenu.getExt8());
        this.setExt9(privMenu.getExt9());
        this.setExt10(privMenu.getExt10());
        this.setExt11(privMenu.getExt11());
        this.setExt12(privMenu.getExt12());
        this.setExt13(privMenu.getExt13());
        this.setExt14(privMenu.getExt14());
        this.setExt15(privMenu.getExt15());
        this.setExt16(privMenu.getExt16());
        this.setExt17(privMenu.getExt17());
        this.setExt18(privMenu.getExt18());
        this.setExt19(privMenu.getExt19());
        this.setExt20(privMenu.getExt20());
        this.setExt21(privMenu.getExt21());
        this.setShow(privMenu.isShow());
        this.setCheck(privMenu.isCheck());
        this.setControl(privMenu.isControl());
        this.setResourceCode(privMenu.getResourceCode());
        this.setEnterResource(privMenu.getEnterResource());
        this.setResourceNavurl(privMenu.getResourceNavurl());
        this.setResourceModuleid(privMenu.getResourceModuleid());
        this.setType(privMenu.getType());
        this.setParentId(privMenu.getParentId());
        this.setResourceNameKey(privMenu.getName());
        return this;
    }

    /**
     * 将菜单BO对象转换为PO对象
     * @return 菜单PO对象
     */
    public PrivMenu toPO() {
        PrivMenu o = new PrivMenu();
        o.setId(this.getId());
        o.setSortid(this.getSortid());
        o.setName(this.getName());
        o.setPluginid(this.getPluginid());
        o.setUpdatedate(this.getUpdatedate());
        o.setPath(this.getPath());
        o.setUpdateuserid(this.getUpdateuserid());
        o.setIcon(this.getIcon());
        o.setCreatedate(this.getCreatedate());
        o.setTarget(this.getTarget());
        o.setExt5(this.getExt5());
        o.setExt6(this.getExt6());
        o.setExt1(this.getExt1());
        o.setExt2(this.getExt2());
        o.setExt3(this.getExt3());
        o.setExt4(this.getExt4());
        o.setExt7(this.getExt7());
        o.setExt8(this.getExt8());
        o.setExt9(this.getExt9());
        o.setExt10(this.getExt10());
        o.setExt11(this.getExt11());
        o.setExt12(this.getExt12());
        o.setExt13(this.getExt13());
        o.setExt14(this.getExt14());
        o.setExt15(this.getExt15());
        o.setExt16(this.getExt16());
        o.setExt17(this.getExt17());
        o.setExt18(this.getExt18());
        o.setExt19(this.getExt19());
        o.setExt20(this.getExt20());
        o.setExt21(this.getExt21());
        o.setShow(this.isShow());
        o.setCheck(this.isCheck());
        o.setControl(this.isControl());
        o.setEnterResource(this.getEnterResource());
        o.setResourceCode(this.getResourceCode());
        o.setResourceModuleid(this.getResourceModuleid());
        o.setResourceNavurl(this.getResourceNavurl());
        o.setType(this.getType());
        o.setParentId(this.getParentId());
        return o;
    }

    /**
     * @return the enterResourceId
     */
    public Long getEnterResourceId() {
        return enterResourceId;
    }

    /**
     * @param enterResourceId the enterResourceId to set
     */
    public void setEnterResourceId(Long enterResourceId) {
        this.enterResourceId = enterResourceId;
    }

    /**
     * @return the parentId
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public String getName() {
		return ResourceUtil.getString(name);
	}
    
    public String getRealName(){
    	return name;
    }

	public void setName(String name) {
		this.name = name;
	}

    /**
     * @return the enterResource
     */
    public String getEnterResourceName() {
        return enterResourceName;
    }

    /**
     * @param enterResource the enterResource to set
     */
    public void setEnterResourceName(String enterResourceName) {
        this.enterResourceName = enterResourceName;
    }

    /**
     * @return the isVirtualNode
     */
    public Boolean getIsVirtualNode() {
        setIsVirtualNode();
        return isVirtualNode;
    }

    /**
     * @param isVirtualNode the isVirtualNode to set
     */
    public void setIsVirtualNode() {
        if (StringUtil.checkNull(this.getTarget())) {
            this.isVirtualNode = true;
        } else {
            this.isVirtualNode = false;
        }
    }

    /**
     * @return the menuLevel
     */
    public String getMenuLevel() {
        return menuLevel;
    }

    /**
     * @param menuLevel the menuLevel to set
     */
    public void setMenuLevel() {
    	String level = this.getExt2();
    	if(null != level && !"".equals(level)){
    		 this.menuLevel = level;
    	}
    }   

    /**
     * @return the naviResourceIds
     */
    public List<Long> getNaviResourceIds() {
        return naviResourceIds;
    }

    /**
     * @param naviResourceIds the naviResourceIds to set
     */
    public void setNaviResourceIds(List<Long> naviResourceIds) {
        this.naviResourceIds = naviResourceIds;
    }

    /**
     * @return the shortcutResourceIds
     */
    public List<Long> getShortcutResourceIds() {
        return shortcutResourceIds;
    }

    /**
     * @param shortcutResourceIds the shortcutResourceIds to set
     */
    public void setShortcutResourceIds(List<Long> shortcutResourceIds) {
        this.shortcutResourceIds = shortcutResourceIds;
    }

    /**
     *  排序号 
     */
    public java.lang.Integer getSortid() {
        return super.getSortid() == null ? 1 : super.getSortid();
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

	public String getResourceNameKey() {
		return resourceNameKey;
	}

	public void setResourceNameKey(String resourceNameKey) {
		this.resourceNameKey = resourceNameKey;
	}
    
}
