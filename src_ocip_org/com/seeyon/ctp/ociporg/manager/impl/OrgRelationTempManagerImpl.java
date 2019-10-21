package com.seeyon.ctp.ociporg.manager.impl;

import com.seeyon.ctp.ociporg.dao.OrgRelationTempDao;
import com.seeyon.ctp.ociporg.manager.OrgRelationTempManager;
import com.seeyon.ctp.ociporg.po.OrgRelationTemp;

public class OrgRelationTempManagerImpl implements OrgRelationTempManager {

	private OrgRelationTempDao orgRelationTempDao;

	@Override
	public OrgRelationTemp findOrgRelationTempById(String id) {
		return orgRelationTempDao.findOrgRelationTempById(id);
	}

	@Override
	public OrgRelationTemp findOrgRelationTempByUserId(String userId, String resourceId) {
		return orgRelationTempDao.findOrgRelationTempByUserId(userId, resourceId);
	}

	public OrgRelationTempDao getOrgRelationTempDao() {
		return orgRelationTempDao;
	}

	public void setOrgRelationTempDao(OrgRelationTempDao orgRelationTempDao) {
		this.orgRelationTempDao = orgRelationTempDao;
	}

}
