package com.seeyon.ctp.privilege.po;

import com.seeyon.ctp.common.po.BasePO;

/**
 * This is an object that contains data related to the PRIV_RESOURCEGROUP table.
 *
 *  资源组 
 *
 * @hibernate.class
 *  table="PRIV_RESOURCEGROUP"
 */
@SuppressWarnings("serial")
public class PrivResourcegroup extends BasePO {

    // fields
    private java.lang.String _name; //名称

    // constructors
    public PrivResourcegroup() {
        initialize();
    }

    /**
     * Constructor for primary key
     */
    public PrivResourcegroup(java.lang.Long _id) {
        this.setId(_id);
        initialize();
    }

    protected void initialize() {
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

}