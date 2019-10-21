package com.seeyon.ctp.ociporg.manager;

import java.util.List;

import com.seeyon.ctp.ociporg.po.OrgDepartmentTemp;
import com.seeyon.ctp.util.FlipInfo;

public interface OrgDepartmentTempManager {

	public OrgDepartmentTemp findOrgDepartmentTempById(String id);

	public List<OrgDepartmentTemp> findOrgDepartmentTempByGrade(Short grade, Short isFlag, String resourceId,
			FlipInfo flipInfo);

	public Long getCount(Short grade, Short isFlag, String resourceId);

	public void updatOrgDepartmentTemp(OrgDepartmentTemp orgDepartmentTemp) throws Exception;

	public List<Short> findOrgDepartmentTemByGrade(String resourceId);

}
