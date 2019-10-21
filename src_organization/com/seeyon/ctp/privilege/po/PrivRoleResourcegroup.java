package com.seeyon.ctp.privilege.po;

import com.seeyon.ctp.common.po.BasePO;

/**
 * This is an object that contains data related to the PRIV_ROLE_RESOURCEGROUP table.
 *
 *  角色资源组表 
 *
 * @hibernate.class
 *  table="PRIV_ROLE_RESOURCEGROUP"
 */
@SuppressWarnings("serial")
public class PrivRoleResourcegroup extends BasePO {

    // fields
    private java.lang.Long _roleid;
    private java.lang.Long _resourcegroupid;

    // constructors
    public PrivRoleResourcegroup() {
        initialize();
    }

    /**
     * Constructor for primary key
     */
    public PrivRoleResourcegroup(java.lang.Long _id) {
        this.setId(_id);
        initialize();
    }

    protected void initialize() {
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
     *  资源组ID 
     */
    public java.lang.Long getResourcegroupid() {
        return _resourcegroupid;
    }

    /**
     *  资源组ID 
     * @param _resourcegroupid the RESOURCEGROUPID value
     */
    public void setResourcegroupid(java.lang.Long _resourcegroupid) {
        this._resourcegroupid = _resourcegroupid;
    }

}