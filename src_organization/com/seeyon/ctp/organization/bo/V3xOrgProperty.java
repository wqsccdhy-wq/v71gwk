/**
 * $Author: 
 $
 * $Rev: 
 $
 * $Date:: 2012-06-05 15:14:56#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */

package com.seeyon.ctp.organization.bo;

import java.io.Serializable;

import com.seeyon.ctp.common.po.BasePO;

public class V3xOrgProperty extends V3xOrgEntity implements Serializable {
    
    private static final long serialVersionUID = 5229011043673180366L;
    private String name;
	private Long sourceId;
	private Integer type;
	private String value;

	public V3xOrgProperty() {
	}
	
    public V3xOrgEntity fromPO(BasePO po) {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public BasePO toPO() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getEntityType() {
        return null;
    }

    public boolean isValid() {
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}