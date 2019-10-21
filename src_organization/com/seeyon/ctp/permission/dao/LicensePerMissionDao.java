/* $Author: tanmf $
 * $Rev: 0 $
 * $Date: 2012-08-01 15:08:37#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.permission.dao;

import java.util.List;

import com.seeyon.ctp.permission.po.PrivPermission;


public interface LicensePerMissionDao {

	List<PrivPermission> getAllPerMissionPO();

    void savePerMissionPO(PrivPermission privPermission);
    
    public PrivPermission getPerMissionPO(Long accId,Integer lictype);
    public void deleteAllPerMissionPO();
    public void savePerMissionPO(List<PrivPermission> privPermissions);

	public void saveOrUpdatePerMissionPO(List<PrivPermission> newPrivPermissions);

}