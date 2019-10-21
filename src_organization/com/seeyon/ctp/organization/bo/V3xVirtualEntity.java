package com.seeyon.ctp.organization.bo;

import java.io.Serializable;
import com.seeyon.ctp.common.po.BasePO;

/**
 * <p>Title: 虚拟实体类BO对象</p>
 * <p>Description: 代码描述</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: seeyon.com</p>
 */
public class V3xVirtualEntity extends V3xOrgEntity implements Serializable {

    private static final long   serialVersionUID = -1332273704873347999L;
    private String entityType; //虚拟实体的类型
    public V3xVirtualEntity(String entityType) {
    	this.entityType = entityType;
    }

    public void setEntityType(String entityType) {
    	this.entityType = entityType;
    }
    
	public String getEntityType() {
		return entityType;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public V3xOrgEntity fromPO(BasePO po) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BasePO toPO() {
		// TODO Auto-generated method stub
		return null;
	}
    
}