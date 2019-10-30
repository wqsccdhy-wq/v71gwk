package com.seeyon.ctp.ociporg.manager;

import java.util.List;

import com.seeyon.ctp.ociporg.po.OrgUserLevelTemp;
import com.seeyon.ctp.util.FlipInfo;

public interface OrgUserLevelTempManager {

    public OrgUserLevelTemp findOrgUserLevelTempById(String id);

    public List<OrgUserLevelTemp> findOrgUserLevelTempByFlag(Short isFlag, String resourceId, FlipInfo flipInfo);

    public void updateOrgUserLevelTemp(OrgUserLevelTemp orgUserLevelTemp);

    public Long getCount(Short isFlag, String resourceId);

}
