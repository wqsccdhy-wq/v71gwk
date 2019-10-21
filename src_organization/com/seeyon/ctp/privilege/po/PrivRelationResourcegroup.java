package com.seeyon.ctp.privilege.po;

import com.seeyon.ctp.common.po.BasePO;

/**
 * This is an object that contains data related to the PRIV_RELATION_RESOURCEGROUP table.
 *
 *  资源组，解决两个问题，可以更加合理的呈现真正的授权，比如以后可以对协同可以两种权限：发协同、收协同。两外一个解决权限之间的关联。 
 *
 * @hibernate.class
 *  table="PRIV_RELATION_RESOURCEGROUP"
 */
@SuppressWarnings("serial")
public class PrivRelationResourcegroup extends BasePO {

    // fields
    private java.lang.Long _resourceid;      //资源id
    private java.lang.Long _resourceGroupid; //资源组id

    // constructors
    public PrivRelationResourcegroup() {
        initialize();
    }

    /**
     * Constructor for primary key
     */
    public PrivRelationResourcegroup(java.lang.Long _id) {
        this.setId(_id);
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
     *  资源组ID 
     */
    public java.lang.Long getResourceGroupid() {
        return _resourceGroupid;
    }

    /**
     *  资源组ID 
     * @param _resourceGroupid the RESOURCE_GROUPID value
     */
    public void setResourceGroupid(java.lang.Long _resourceGroupid) {
        this._resourceGroupid = _resourceGroupid;
    }

}