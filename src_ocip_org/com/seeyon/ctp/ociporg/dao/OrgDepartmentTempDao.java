package com.seeyon.ctp.ociporg.dao;

import java.util.List;

import com.seeyon.ctp.ociporg.po.OrgDepartmentTemp;
import com.seeyon.ctp.util.FlipInfo;

public interface OrgDepartmentTempDao {

    public OrgDepartmentTemp findOrgDepartmentTempById(String id);

    public List<OrgDepartmentTemp> findOrgDepartmentTempByGrade(Short grade, Short isFlag, String resourceId,
        FlipInfo flipInfo);

    public Long getCount(Short grade, Short isFlag, String resourceId);

    public void updatOrgDepartmentTemp(OrgDepartmentTemp orgDepartmentTemp) throws Exception;

    public List<Short> findOrgDepartmentTemByGrade(String resourceId);

}
