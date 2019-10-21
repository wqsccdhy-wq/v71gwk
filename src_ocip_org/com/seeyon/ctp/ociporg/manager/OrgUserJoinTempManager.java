package com.seeyon.ctp.ociporg.manager;

import java.util.List;

import com.seeyon.ctp.ociporg.po.OrgUserJoinTemp;
import com.seeyon.ctp.util.FlipInfo;

public interface OrgUserJoinTempManager {

	public OrgUserJoinTemp findOrgUserJoinTempById(String id);

	public List<OrgUserJoinTemp> findOrgUserJoinTempByGrade(Short isFlag, String resourceId, FlipInfo flipInfo);

	public Long getCount(Short isFlag, String resourceId);

	public void updatOrgUserJoinTemp(OrgUserJoinTemp orgUserJoinTemp) throws Exception;

}
