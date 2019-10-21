package com.seeyon.ctp.ociporg.dao;

import java.util.List;

import com.seeyon.ctp.ociporg.po.OrgUserLevelTemp;
import com.seeyon.ctp.util.FlipInfo;

public interface OrgUserLevelTempDao {

	public OrgUserLevelTemp findOrgUserLevelTempById(String id);

	public List<OrgUserLevelTemp> findOrgUserLevelTempByFlag(Short isFlag, String resourceId, FlipInfo flipInfo);

	public void updateOrgUserLevelTemp(OrgUserLevelTemp orgUserLevelTemp);

	public Long getCount(Short isFlag, String resourceId);

}
