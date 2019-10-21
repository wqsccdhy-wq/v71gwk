package com.seeyon.ctp.ociporg.manager.impl;

import java.util.List;

import com.seeyon.ctp.ociporg.dao.OrgUserJoinTempDao;
import com.seeyon.ctp.ociporg.manager.OrgUserJoinTempManager;
import com.seeyon.ctp.ociporg.po.OrgUserJoinTemp;
import com.seeyon.ctp.util.FlipInfo;

public class OrgUserJoinTempManagerImpl implements OrgUserJoinTempManager {

	private OrgUserJoinTempDao orgUserJoinTempDao;

	@Override
	public OrgUserJoinTemp findOrgUserJoinTempById(String id) {
		return orgUserJoinTempDao.findOrgUserJoinTempById(id);
	}

	@Override
	public List<OrgUserJoinTemp> findOrgUserJoinTempByGrade(Short isFlag, String resourceId, FlipInfo flipInfo) {
		return orgUserJoinTempDao.findOrgUserJoinTempByGrade(isFlag, resourceId, flipInfo);
	}

	@Override
	public Long getCount(Short isFlag, String resourceId) {
		return orgUserJoinTempDao.getCount(isFlag, resourceId);
	}

	@Override
	public void updatOrgUserJoinTemp(OrgUserJoinTemp orgUserJoinTemp) throws Exception {
		orgUserJoinTempDao.updatOrgUserJoinTemp(orgUserJoinTemp);

	}

	public OrgUserJoinTempDao getOrgUserJoinTempDao() {
		return orgUserJoinTempDao;
	}

	public void setOrgUserJoinTempDao(OrgUserJoinTempDao orgUserJoinTempDao) {
		this.orgUserJoinTempDao = orgUserJoinTempDao;
	}

}
