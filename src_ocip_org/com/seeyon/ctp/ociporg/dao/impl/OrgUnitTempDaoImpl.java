package com.seeyon.ctp.ociporg.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.ociporg.dao.OrgUnitTempDao;
import com.seeyon.ctp.ociporg.po.OrgUnitTemp;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;

public class OrgUnitTempDaoImpl implements OrgUnitTempDao {

	@Override
	public List<OrgUnitTemp> findOrgUnitTempByGrade(Short grade, Short isFlag, String resourceId, FlipInfo flipInfo) {
		String hql = "from OrgUnitTemp out where out.grade=:grade and out.isFlag=:isFlag and out.resourceId=:resourceId";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("grade", grade);
		parameterMap.put("isFlag", isFlag);
		parameterMap.put("resourceId", resourceId);
		List<OrgUnitTemp> find = DBAgent.find(hql, parameterMap, flipInfo);
		return find;
	}

	@Override
	public Long getCount(Short grade, Short isFlag, String resourceId) {
		Long count = 0l;
		String hql = "select count(*) from OrgUnitTemp out where out.grade=:grade and out.isFlag=:isFlag and out.resourceId=:resourceId";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("grade", grade);
		parameterMap.put("isFlag", isFlag);
		parameterMap.put("resourceId", resourceId);
		List find = DBAgent.find(hql, parameterMap);
		if (find != null) {
			Object object = find.get(0);
			if (object != null) {
				count = (Long)object;
			}
		}
		return count;
	}

	@Override
	public void updatOrgUnitTemp(OrgUnitTemp orgUnitTemp) throws Exception {
		DBAgent.update(orgUnitTemp);
	}

	@Override
	public OrgUnitTemp findOrgUnitTempById(String id) {
		OrgUnitTemp unitTemp = DBAgent.get(OrgUnitTemp.class, id);
		return unitTemp;
	}

	@Override
	public List<Short> findOrgUnitTempByGrade(String resourceId) {
		List<Short> result = new ArrayList<Short>();
		String hql = "select out.grade from OrgUnitTemp out where out.resourceId=:resourceId group by out.grade";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("resourceId", resourceId);
		List find = DBAgent.find(hql, parameterMap);
		if (find != null) {
			for (Object object : find) {
				Short para = new Short(String.valueOf(object));
				result.add(para);
			}
		}
		return result;
	}

}
