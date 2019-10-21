package com.seeyon.ctp.ociporg.manager;

import com.seeyon.ctp.ociporg.po.OrgOcipTempLog;
import com.seeyon.ctp.ociporg.po.OrgUnitTemp;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;

public abstract class AbsOcipOrgManager<T> implements OcipOrgManager<T> {

	protected OrgManagerDirect orgManagerDirect;

	protected OrgOcipTempLogManager orgOcipTempLogManager;

	public abstract void importEntry(T t, String resourceId);
	

	@Override
	public void addLog(String msg, String resourceId, String orgId, String name, String type, Boolean success) {
		OrgOcipTempLog log = new OrgOcipTempLog();
		log.setMsg(msg);
		log.setResourceId(resourceId);
		log.setOrgId(orgId);
		log.setName(name);
		log.setType(type);
		String isOk;
		if (success == null) {
			isOk = OrgOcipTempLog.RESULT_WARN;
		} else if (success) {
			isOk = OrgOcipTempLog.RESULT_SUCCESS;
		} else {
			isOk = OrgOcipTempLog.RESULT_FAUL;
		}

		log.setIsOk(isOk);
		orgOcipTempLogManager.insertLog(log);

	}

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public OrgOcipTempLogManager getOrgOcipTempLogManager() {
		return orgOcipTempLogManager;
	}

	public void setOrgOcipTempLogManager(OrgOcipTempLogManager orgOcipTempLogManager) {
		this.orgOcipTempLogManager = orgOcipTempLogManager;
	}


}
