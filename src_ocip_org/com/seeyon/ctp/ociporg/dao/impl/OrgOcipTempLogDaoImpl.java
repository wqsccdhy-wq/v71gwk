package com.seeyon.ctp.ociporg.dao.impl;

import com.seeyon.ctp.ociporg.dao.OrgOcipTempLogDao;
import com.seeyon.ctp.ociporg.po.OrgOcipTempLog;
import com.seeyon.ctp.util.DBAgent;

public class OrgOcipTempLogDaoImpl implements OrgOcipTempLogDao {

	@Override
	public void insertLog(OrgOcipTempLog log) {
		DBAgent.save(log);
	}

}
