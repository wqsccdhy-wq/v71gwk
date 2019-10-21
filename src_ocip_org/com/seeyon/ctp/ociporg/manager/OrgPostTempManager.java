package com.seeyon.ctp.ociporg.manager;

import java.util.List;

import com.seeyon.ctp.ociporg.po.OrgPostTemp;
import com.seeyon.ctp.util.FlipInfo;

public interface OrgPostTempManager {

	public OrgPostTemp findOrgPostTempById(String id);

	public List<OrgPostTemp> findOrgPostTempByFlag(Short isFlag, String resourceId, FlipInfo flipInfo);

	public void updateOrgPostTemp(OrgPostTemp orgPostTemp);
	
	public Long getCount(Short isFlag, String resourceId);

}
