package com.seeyon.ctp.ociporg.manager.impl;

import java.util.List;

import com.seeyon.ctp.ociporg.dao.OrgPostTempDao;
import com.seeyon.ctp.ociporg.manager.OrgPostTempManager;
import com.seeyon.ctp.ociporg.po.OrgPostTemp;
import com.seeyon.ctp.util.FlipInfo;

public class OrgPostTempManagerImpl implements OrgPostTempManager {

	private OrgPostTempDao orgPostTempDao;

	@Override
	public OrgPostTemp findOrgPostTempById(String id) {
		return orgPostTempDao.findOrgPostTempById(id);
	}

	@Override
	public List<OrgPostTemp> findOrgPostTempByFlag(Short isFlag, String resourceId, FlipInfo flipInfo) {
		return orgPostTempDao.findOrgPostTempByFlag(isFlag, resourceId, flipInfo);
	}

	@Override
	public void updateOrgPostTemp(OrgPostTemp orgPostTemp) {
		orgPostTempDao.updateOrgPostTemp(orgPostTemp);
	}
	
	@Override
	public Long getCount(Short isFlag, String resourceId) {
		return orgPostTempDao.getCount(isFlag, resourceId);
	}

	public OrgPostTempDao getOrgPostTempDao() {
		return orgPostTempDao;
	}

	public void setOrgPostTempDao(OrgPostTempDao orgPostTempDao) {
		this.orgPostTempDao = orgPostTempDao;
	}


}
