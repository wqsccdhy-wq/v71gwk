/**
 * $Author: gaohang $
 * $Rev: 19901 $
 * $Date:: 2013-04-19 13:09:32#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.bo;

import javax.persistence.Transient;

import com.seeyon.ctp.common.i18n.ResourceUtil;

/**
 * <p>Title: 资源树节点BO类</p>
 * <p>Description: 本程序实现资源树节点BO对象</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public class PrivTreeNodeBO {

    /** 节点ID */
    private String             idKey;

    /** 父节点ID */
    private String             pIdKey;

    /** 节点名称 */
    private String             nameKey;

    /** 是否可编辑 */
    private String             editKey      = "true";

    /** 菜单节点标识 */
    public static final String menuflag     = "menu_";

    /** 资源节点标识 */
    public static final String resourceflag = "res_";
    
    private Integer sortId = 0;
    
    private String path;
    
    /** 菜单对象 **/
    private transient PrivMenuBO menu=null;

    public PrivTreeNodeBO() {

    }

    public PrivTreeNodeBO(Object obj, Long parentId) {
        if (obj instanceof PrivMenuBO) {
            PrivMenuBO menu = (PrivMenuBO) obj;
            this.idKey = menuflag + menu.getId();
            this.pIdKey = menuflag + menu.getParentId();
            this.nameKey = ResourceUtil.getString(menu.getName());
            this.menu=menu;
            this.sortId = menu.getSortid() == null ? 0 : menu.getSortid();
            this.path = menu.getPath();
        } 
    }

    /**
     * @return the idKey
     */
    public String getIdKey() {
        return idKey;
    }

    /**
     * @param idKey the idKey to set
     */
    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    /**
     * @return the pIdKey
     */
    public String getpIdKey() {
        return pIdKey;
    }

    /**
     * @param pIdKey the pIdKey to set
     */
    public void setpIdKey(String pIdKey) {
        this.pIdKey = pIdKey;
    }

    /**
     * @return the nameKey
     */
    public String getNameKey() {
        return nameKey;
    }

    /**
     * @param nameKey the nameKey to set
     */
    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    /**
     * @return the editKey
     */
    public String getEditKey() {
        return editKey;
    }

    /**
     * @param editKey the editKey to set
     */
    public void setEditKey(String editKey) {
        this.editKey = editKey;
    }

	public PrivMenuBO getMenu() {
		return menu;
	}

	@Transient
	public void setMenu(PrivMenuBO menu) {
		this.menu = menu;
	}

	public Integer getSortId() {
		return sortId;
	}

	public void setSortId(Integer sortId) {
		this.sortId = 0;
		if(sortId != null){
			this.sortId = sortId;
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
    
}
