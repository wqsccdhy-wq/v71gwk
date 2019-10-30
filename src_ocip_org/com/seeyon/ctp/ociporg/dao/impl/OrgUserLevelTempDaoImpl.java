package com.seeyon.ctp.ociporg.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.ociporg.dao.OrgUserLevelTempDao;
import com.seeyon.ctp.ociporg.po.OrgUserLevelTemp;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;

public class OrgUserLevelTempDaoImpl implements OrgUserLevelTempDao {

    @Override
    public OrgUserLevelTemp findOrgUserLevelTempById(String id) {
        return DBAgent.get(OrgUserLevelTemp.class, id);
    }

    @Override
    public List<OrgUserLevelTemp> findOrgUserLevelTempByFlag(Short isFlag, String resourceId, FlipInfo flipInfo) {
        String hql = "from OrgUserLevelTemp oult where oult.isFlag=:isFlag and oult.resourceId=:resourceId";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("isFlag", isFlag);
        parameterMap.put("resourceId", resourceId);
        List<OrgUserLevelTemp> find = DBAgent.find(hql, parameterMap, flipInfo);
        return find;
    }

    @Override
    public void updateOrgUserLevelTemp(OrgUserLevelTemp orgUserLevelTemp) {
        DBAgent.update(orgUserLevelTemp);
    }

    @Override
    public Long getCount(Short isFlag, String resourceId) {
        Long count = 0l;
        String hql =
            "select count(*) from OrgUserLevelTemp oult where oult.isFlag=:isFlag and oult.resourceId=:resourceId";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
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

}
