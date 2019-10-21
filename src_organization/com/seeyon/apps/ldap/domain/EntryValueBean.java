package com.seeyon.apps.ldap.domain;

/**
 * 条目显示Bean
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @author lilong
 * @version 2009-1-17
 * @version CTP2.0
 */
public class EntryValueBean {
    private String id;
    private String type;
    private String parentId;
    private String name;
    private String showName;
    private String dnName;
    private String siName;
    private int    sortId;

    public String getDnName() {
        return dnName;
    }

    public void setDnName(String dnName) {
        this.dnName = dnName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the siName
     */
    public String getSiName() {
        return siName;
    }
    /**
     * @param siName the siName to set
     */
    public void setSiName(String siName) {
        this.siName = siName;
    }

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}
    
}
