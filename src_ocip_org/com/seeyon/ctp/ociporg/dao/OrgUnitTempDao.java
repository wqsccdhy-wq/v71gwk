package com.seeyon.ctp.ociporg.dao;

import java.util.List;

import com.seeyon.ctp.ociporg.po.OrgUnitTemp;
import com.seeyon.ctp.util.FlipInfo;

public interface OrgUnitTempDao {

    public OrgUnitTemp findOrgUnitTempById(String id);

    public List<OrgUnitTemp> findOrgUnitTempByGrade(Short grade, Short isFlag, String resourceId, FlipInfo flipInfo);

    public Long getCount(Short grade, Short isFlag, String resourceId);

    public void updatOrgUnitTemp(OrgUnitTemp orgUnitTemp) throws Exception;

    public List<Short> findOrgUnitTempByGrade(String resourceId);

}
