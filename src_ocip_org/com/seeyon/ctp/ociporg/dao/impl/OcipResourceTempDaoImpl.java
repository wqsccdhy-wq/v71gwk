package com.seeyon.ctp.ociporg.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.ociporg.dao.OcipResourceTempDao;
import com.seeyon.ctp.ociporg.po.OcipResourceTemp;
import com.seeyon.ctp.util.DBAgent;

public class OcipResourceTempDaoImpl implements OcipResourceTempDao {

    @Override
    public List<OcipResourceTemp> findOcipResourceTemp(Short isFlag) {
        String hql = "from OcipResourceTemp ort where ort.isFlag=:isFlag";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("isFlag", isFlag);
        List<OcipResourceTemp> find = DBAgent.find(hql, parameterMap);
        return find;
    }

    @Override
    public List<OcipResourceTemp> findOcipResourceTempById(String id) {
        String hql = "from OcipResourceTemp ort where ort.id=:id";
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("id", id);
        List<OcipResourceTemp> find = DBAgent.find(hql, parameterMap);
        return find;
    }

    @Override
    public void updatOcipResourceTemp(OcipResourceTemp ocipResourceTemp) throws Exception {
        DBAgent.update(ocipResourceTemp);
    }

}
