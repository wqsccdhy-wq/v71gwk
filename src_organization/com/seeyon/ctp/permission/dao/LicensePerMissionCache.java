/* $Author: tanmf $
 * $Rev: 0 $
 * $Date: 2012-08-02 13:38:17#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.permission.dao;

import java.util.List;

import com.seeyon.ctp.permission.po.PrivPermission;



public interface LicensePerMissionCache {
    
    void init();
    
    List<PrivPermission> getAllPerMissionPO();
    
    PrivPermission getPerMissionPO(Long accId,Integer lictype);
  
	
    public  void cacheUpdate(PrivPermission privPermission);
    
    
	
    public  void cacheRemove();
	
	
    public  void cacheUpdate(List<PrivPermission> privPermissions);
	
}
