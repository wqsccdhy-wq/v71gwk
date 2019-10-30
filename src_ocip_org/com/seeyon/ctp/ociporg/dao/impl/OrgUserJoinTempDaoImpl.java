package com.seeyon.ctp.ociporg.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.ociporg.dao.OrgUserJoinTempDao;
import com.seeyon.ctp.ociporg.po.OrgUserJoinTemp;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;

public class OrgUserJoinTempDaoImpl implements OrgUserJoinTempDao {

    @Override
    public OrgUserJoinTemp findOrgUserJoinTempById(String id) {
        return DBAgent.get(OrgUserJoinTemp.class, id);
    }

    @Override
    public List<OrgUserJoinTemp> findOrgUserJoinTempByGrade(Short isFlag, String resourceId, FlipInfo flipInfo) {
        String hql = "from OrgUserJoinTemp ojt where ojt.isFlag=:isFlag and ojt.resourceId=:resourceId";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("isFlag", isFlag);
        parameterMap.put("resourceId", resourceId);
        List<OrgUserJoinTemp> find = DBAgent.find(hql, parameterMap, flipInfo);
        return find;
    }

    @Override
    public Long getCount(Short isFlag, String resourceId) {
        Long count = 0l;
        String hql = "select count(*) from OrgUserJoinTemp ojt where ojt.isFlag=:isFlag and ojt.resourceId=:resourceId";
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

    @Override
    public void updatOrgUserJoinTemp(OrgUserJoinTemp orgUserJoinTemp) throws Exception {
        DBAgent.update(orgUserJoinTemp);
    }

}
