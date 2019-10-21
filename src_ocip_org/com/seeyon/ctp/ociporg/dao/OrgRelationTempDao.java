package com.seeyon.ctp.ociporg.dao;

import com.seeyon.ctp.ociporg.po.OrgRelationTemp;

public interface OrgRelationTempDao {

	public OrgRelationTemp findOrgRelationTempById(String id);

	public OrgRelationTemp findOrgRelationTempByUserId(String userId, String resourceId);

}
