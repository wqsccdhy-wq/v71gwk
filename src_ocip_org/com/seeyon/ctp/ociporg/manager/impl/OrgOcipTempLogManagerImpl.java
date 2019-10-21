package com.seeyon.ctp.ociporg.manager.impl;

import com.seeyon.ctp.ociporg.dao.OrgOcipTempLogDao;
import com.seeyon.ctp.ociporg.manager.OrgOcipTempLogManager;
import com.seeyon.ctp.ociporg.po.OrgOcipTempLog;

public class OrgOcipTempLogManagerImpl implements OrgOcipTempLogManager {

	private OrgOcipTempLogDao orgOcipTempLogDao;

	@Override
	public void insertLog(OrgOcipTempLog log) {
		orgOcipTempLogDao.insertLog(log);

	}

	public OrgOcipTempLogDao getOrgOcipTempLogDao() {
		return orgOcipTempLogDao;
	}

	public void setOrgOcipTempLogDao(OrgOcipTempLogDao orgOcipTempLogDao) {
		this.orgOcipTempLogDao = orgOcipTempLogDao;
	}

}
