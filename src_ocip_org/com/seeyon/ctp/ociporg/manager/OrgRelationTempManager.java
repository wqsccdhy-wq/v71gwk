package com.seeyon.ctp.ociporg.manager;

import com.seeyon.ctp.ociporg.po.OrgRelationTemp;

public interface OrgRelationTempManager {
	
	public OrgRelationTemp findOrgRelationTempById(String id);

	public OrgRelationTemp findOrgRelationTempByUserId(String userId, String resourceId);

}
