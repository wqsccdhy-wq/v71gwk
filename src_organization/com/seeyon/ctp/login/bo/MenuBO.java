/**
 * $Author: leikj $
 * $Rev: 6156 $
 * $Date:: 2013-04-17 19:15:52#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.login.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.util.Strings;

/**
 * <p>Title: T1开发框架</p>
 * <p>Description: 菜单BO对象，带层级和资源关系，主要为登陆使用</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * @since CTP2.0
 */
public class MenuBO implements Serializable {

    private static final long serialVersionUID = 1567272857136444000L;

    private java.lang.String  _name;

    private java.lang.String  _icon;
    private java.lang.String  _target;

    private String            url;
    private List<MenuBO>      items            = new ArrayList<MenuBO>();
    private Long id;
    private Long parentId;
    

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    private int sortid;
    private boolean           sorted           = false;

    private String resourceCode;

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public Long getId() {
        return this.id;
    }

    public int privSortid() {
        return this.sortid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean containsItem(MenuBO o) {
        return items.contains(o);
    }

    public List<MenuBO> getItems() {
        if (null==items || items.size() == 0)
            return null;
        else {
            if (!sorted) {
                Comparator<MenuBO> comparator = new Comparator<MenuBO>() {
                    @Override
                    public int compare(MenuBO o1, MenuBO o2) {
                        int r = o1.privSortid() - o2.privSortid();
                        return r;
                    }
                };
                Collections.sort(items, comparator);
                sorted = true;
            }
            return items;
        }
    }

    public void addItem(MenuBO item) {
        items.add(item);
        sorted = false;
    }
    
    public void setItems(List<MenuBO> items){
        this.items = items;
    }

    public boolean isSorted() {
        return sorted;
    }

    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }
    
    public MenuBO(Long id, Integer sortid, String name, String icon, String target, String url, String resourceCode) {
        this.id = id;
        this.sortid= sortid;
        this._name = ResourceUtil.getString(name);
        this._icon = icon;
        this._target = target;
        this.url = url;
        this.resourceCode = resourceCode;
    }
    
    public MenuBO() {
    	
    }

    /**
     *  名称 
     */
    public java.lang.String getName() {
        return _name;
    }

    /**
     *  显示图标 
     */
    public java.lang.String getIcon() {
       	//统一处理一下菜单的icon数据,vp-开头的去掉vp-
    	if(Strings.isNotBlank(_icon)) {
    		_icon = _icon.trim();
    		if(_icon.startsWith("vp-")) {
    			return _icon.substring(3);
    		}
    	}
        return _icon;
    }

    /**
     *  打开的目标窗口 
     */
    public java.lang.String getTarget() {
        return _target;
    }

    public boolean equals(Object o) {
        if (o instanceof MenuBO) {
            MenuBO m = (MenuBO) o;
            if (m.getId().equals(this.getId()))
                return true;
        }
        return false;
    }

}
