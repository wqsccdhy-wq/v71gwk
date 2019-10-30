package com.seeyon.ctp.ociporg.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.ociporg.dao.OrgPostTempDao;
import com.seeyon.ctp.ociporg.po.OrgPostTemp;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;

public class OrgPostTempDaoImpl implements OrgPostTempDao {

    @Override
    public OrgPostTemp findOrgPostTempById(String id) {
        return DBAgent.get(OrgPostTemp.class, id);
    }

    @Override
    public List<OrgPostTemp> findOrgPostTempByFlag(Short isFlag, String resourceId, FlipInfo flipInfo) {
        String hql = "from OrgPostTemp opt where opt.isFlag=:isFlag and opt.resourceId=:resourceId";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("isFlag", isFlag);
        parameterMap.put("resourceId", resourceId);
        List<OrgPostTemp> find = DBAgent.find(hql, parameterMap, flipInfo);
        return find;
    }

    @Override
    public void updateOrgPostTemp(OrgPostTemp orgPostTemp) {
        DBAgent.update(orgPostTemp);
    }

    @Override
    public Long getCount(Short isFlag, String resourceId) {
        Long count = 0l;
        String hql = "select count(*) from OrgPostTemp opt where opt.isFlag=:isFlag and opt.resourceId=:resourceId";
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
