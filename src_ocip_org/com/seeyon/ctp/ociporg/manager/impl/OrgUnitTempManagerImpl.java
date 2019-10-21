package com.seeyon.ctp.ociporg.manager.impl;

import java.util.List;

import com.seeyon.ctp.ociporg.dao.OrgUnitTempDao;
import com.seeyon.ctp.ociporg.manager.OrgUnitTempManager;
import com.seeyon.ctp.ociporg.po.OrgUnitTemp;
import com.seeyon.ctp.util.FlipInfo;

public class OrgUnitTempManagerImpl implements OrgUnitTempManager {

	private OrgUnitTempDao orgUnitTempDao;

	@Override
	public OrgUnitTemp findOrgUnitTempById(String id) {
		return orgUnitTempDao.findOrgUnitTempById(id);
	}

	@Override
	public List<OrgUnitTemp> findOrgUnitTempByGrade(Short grade, Short isFlag, String resourceId, FlipInfo flipInfo) {
		return orgUnitTempDao.findOrgUnitTempByGrade(grade, isFlag, resourceId, flipInfo);
	}

	@Override
	public Long getCount(Short grade, Short isFlag, String resourceId) {
		return orgUnitTempDao.getCount(grade, isFlag, resourceId);
	}

	@Override
	public List<Short> findOrgUnitTempByGrade(String resourceId) {
		return orgUnitTempDao.findOrgUnitTempByGrade(resourceId);
	}

	@Override
	public void updatOrgUnitTemp(OrgUnitTemp orgUnitTemp) throws Exception {
		orgUnitTempDao.updatOrgUnitTemp(orgUnitTemp);

	}

	public OrgUnitTempDao getOrgUnitTempDao() {
		return orgUnitTempDao;
	}

	public void setOrgUnitTempDao(OrgUnitTempDao orgUnitTempDao) {
		this.orgUnitTempDao = orgUnitTempDao;
	}

}
