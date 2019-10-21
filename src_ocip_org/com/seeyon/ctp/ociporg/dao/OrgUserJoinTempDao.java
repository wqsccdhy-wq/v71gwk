package com.seeyon.ctp.ociporg.dao;

import java.util.List;

import com.seeyon.ctp.ociporg.po.OrgUserJoinTemp;
import com.seeyon.ctp.util.FlipInfo;

public interface OrgUserJoinTempDao {

	public OrgUserJoinTemp findOrgUserJoinTempById(String id);

	public List<OrgUserJoinTemp> findOrgUserJoinTempByGrade(Short isFlag, String resourceId, FlipInfo flipInfo);

	public Long getCount(Short isFlag, String resourceId);

	public void updatOrgUserJoinTemp(OrgUserJoinTemp orgUserJoinTemp) throws Exception;

}
