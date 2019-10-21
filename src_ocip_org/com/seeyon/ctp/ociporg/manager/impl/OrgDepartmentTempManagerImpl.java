package com.seeyon.ctp.ociporg.manager.impl;

import java.util.List;

import com.seeyon.ctp.ociporg.dao.OrgDepartmentTempDao;
import com.seeyon.ctp.ociporg.manager.OrgDepartmentTempManager;
import com.seeyon.ctp.ociporg.po.OrgDepartmentTemp;
import com.seeyon.ctp.util.FlipInfo;

public class OrgDepartmentTempManagerImpl implements OrgDepartmentTempManager {

	private OrgDepartmentTempDao orgDepartmentTempDao;

	@Override
	public OrgDepartmentTemp findOrgDepartmentTempById(String id) {
		return orgDepartmentTempDao.findOrgDepartmentTempById(id);
	}

	@Override
	public List<OrgDepartmentTemp> findOrgDepartmentTempByGrade(Short grade, Short isFlag, String resourceId,
			FlipInfo flipInfo) {
		return orgDepartmentTempDao.findOrgDepartmentTempByGrade(grade, isFlag, resourceId, flipInfo);
	}

	@Override
	public Long getCount(Short grade, Short isFlag, String resourceId) {
		return orgDepartmentTempDao.getCount(grade, isFlag, resourceId);
	}

	@Override
	public void updatOrgDepartmentTemp(OrgDepartmentTemp orgDepartmentTemp) throws Exception {
		orgDepartmentTempDao.updatOrgDepartmentTemp(orgDepartmentTemp);

	}
	
	@Override
	public List<Short> findOrgDepartmentTemByGrade(String resourceId) {
		return orgDepartmentTempDao.findOrgDepartmentTemByGrade(resourceId);
	}

	public OrgDepartmentTempDao getOrgDepartmentTempDao() {
		return orgDepartmentTempDao;
	}

	public void setOrgDepartmentTempDao(OrgDepartmentTempDao orgDepartmentTempDao) {
		this.orgDepartmentTempDao = orgDepartmentTempDao;
	}


}
