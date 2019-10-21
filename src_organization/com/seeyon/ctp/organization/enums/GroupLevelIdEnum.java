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
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;

public class GroupLevelIdEnum implements CustomCode {
	
	private static final Log      log       = LogFactory.getLog(GroupLevelIdEnum.class);

    @Override
    public Map getCodesMap(Map codeCfg) {
        OrgManagerDirect orgmanagerd = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
        OrgManager orgmanager = (OrgManager) AppContext.getBean("orgManager");
        List<V3xOrgLevel> list = new ArrayList<V3xOrgLevel>();
        try {
            list = orgmanagerd.getAllLevels(orgmanager.getRootAccount().getId(), false);
        } catch (BusinessException e) {
            // TODO Auto-generated catch block
        	log.error("",e);
        }
        Map myMap = new LinkedHashMap();
        for (V3xOrgLevel v3xOrgLevel : list) {
            myMap.put(v3xOrgLevel.getId().toString(), v3xOrgLevel.getName());
        }
        return myMap;
    }

}
