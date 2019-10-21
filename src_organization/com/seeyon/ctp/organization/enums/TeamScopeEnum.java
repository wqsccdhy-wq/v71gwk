package com.seeyon.ctp.organization.enums;

import java.util.LinkedHashMap;
import java.util.Map;

import com.seeyon.ctp.common.code.CustomCode;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.organization.OrgConstants;

public class TeamScopeEnum implements CustomCode {

    @Override
    public Map getCodesMap(Map codeCfg) {

        Map myMap = new LinkedHashMap();
        myMap.put(String.valueOf(OrgConstants.TEAM_SCOPE.OPEN.ordinal()), ResourceUtil.getString("org.team_form.openteam"));
        myMap.put(String.valueOf(OrgConstants.TEAM_SCOPE.PERSONAL.ordinal()), ResourceUtil.getString("org.team_form.privateteam"));

        return myMap;
    }

}
