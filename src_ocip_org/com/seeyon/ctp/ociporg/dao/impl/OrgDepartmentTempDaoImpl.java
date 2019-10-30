package com.seeyon.ctp.ociporg.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.ociporg.dao.OrgDepartmentTempDao;
import com.seeyon.ctp.ociporg.po.OrgDepartmentTemp;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;

public class OrgDepartmentTempDaoImpl implements OrgDepartmentTempDao {

    @Override
    public OrgDepartmentTemp findOrgDepartmentTempById(String id) {
        return DBAgent.get(OrgDepartmentTemp.class, id);
    }

    @Override
    public List<OrgDepartmentTemp> findOrgDepartmentTempByGrade(Short grade, Short isFlag, String resourceId,
        FlipInfo flipInfo) {
        String hql =
            "from OrgDepartmentTemp odt where odt.grade=:grade and odt.isFlag=:isFlag and odt.resourceId=:resourceId";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("grade", grade);
        parameterMap.put("isFlag", isFlag);
        parameterMap.put("resourceId", resourceId);
        List<OrgDepartmentTemp> find = DBAgent.find(hql, parameterMap, flipInfo);
        return find;
    }

    @Override
    public Long getCount(Short grade, Short isFlag, String resourceId) {
        Long count = 0l;
        String hql =
            "select count(*) from OrgDepartmentTemp odt where odt.grade=:grade and odt.isFlag=:isFlag and odt.resourceId=:resourceId";
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
    public void updatOrgDepartmentTemp(OrgDepartmentTemp orgDepartmentTemp) throws Exception {
        DBAgent.update(orgDepartmentTemp);
    }

    @Override
    public List<Short> findOrgDepartmentTemByGrade(String resourceId) {
        List<Short> result = new ArrayList<Short>();
        String hql = "select odt.grade from OrgDepartmentTemp odt where odt.resourceId=:resourceId group by odt.grade";
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
