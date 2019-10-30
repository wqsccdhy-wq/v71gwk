package com.seeyon.ctp.ociporg.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.seeyon.ctp.ociporg.dao.OrgRelationTempDao;
import com.seeyon.ctp.ociporg.po.OrgRelationTemp;
import com.seeyon.ctp.util.DBAgent;

public class OrgRelationTempDaoImpl implements OrgRelationTempDao {

    @Override
    public OrgRelationTemp findOrgRelationTempById(String id) {
        return DBAgent.get(OrgRelationTemp.class, id);
    }

    @Override
    public OrgRelationTemp findOrgRelationTempByUserId(String userId, String resourceId) {
        OrgRelationTemp temp = null;
        String hql = "from OrgRelationTemp ort where ort.userId=:userId and ort.delFlag=:delFlag";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("userId", userId);
        // parameterMap.put("resourceId", resourceId);
        parameterMap.put("delFlag", 0);
        List<OrgRelationTemp> find = DBAgent.find(hql, parameterMap);
        if (!CollectionUtils.isEmpty(find)) {
            temp = find.get(0);
        }
        return temp;
    }

}
