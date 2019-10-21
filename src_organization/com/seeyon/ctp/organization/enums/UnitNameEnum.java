package com.seeyon.ctp.organization.enums;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.code.CustomCode;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.po.OrgUnit;

public class UnitNameEnum implements CustomCode {

    @Override
    public Map getCodesMap(Map codeCfg) {
        OrgDao orgDao = (OrgDao) AppContext.getBean("orgDao");
        List<OrgUnit> list = new ArrayList<OrgUnit>();
        list = orgDao.getAllUnitPO(null, AppContext.currentAccountId(), null, null, null, null, null);
        Map myMap = new LinkedHashMap();
        for (OrgUnit orgUnit : list) {
            myMap.put(orgUnit.getId().toString(), orgUnit.getName());
        }
        return myMap;
    }

}
