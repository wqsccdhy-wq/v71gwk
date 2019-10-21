package com.seeyon.ctp.organization.enums;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.code.CustomCode;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;

public class LevelScopeEnum implements CustomCode {

    private final static Log log = LogFactory.getLog(LevelScopeEnum.class);

    @Override
    public Map getCodesMap(Map codeCfg) {
        Long accountId = Long.parseLong(codeCfg.get("accountId").toString());
        OrgManagerDirect orgmanagerd = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
        List<V3xOrgLevel> levels = new ArrayList<V3xOrgLevel>();
        try {
            levels = orgmanagerd.getAllLevels(accountId, false);
        } catch (BusinessException e) {
            log.error("", e);
        }
        Map myMap = new LinkedHashMap();
        int levelMax = 0;
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getLevelId() > levelMax)
                levelMax = levels.get(i).getLevelId();
        }
        for (int i = 0; i < levelMax - 1; i++) {
            myMap.put(String.valueOf(i), String.valueOf(i));
        }
        return myMap;
    }

}
