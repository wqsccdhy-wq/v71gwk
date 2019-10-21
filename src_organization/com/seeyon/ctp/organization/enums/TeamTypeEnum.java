package com.seeyon.ctp.organization.enums;

import java.util.LinkedHashMap;
import java.util.Map;

import com.seeyon.ctp.common.code.CustomCode;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.organization.OrgConstants;

public class TeamTypeEnum implements CustomCode {

    @Override
    public Map getCodesMap(Map codeCfg) {

        Map myMap = new LinkedHashMap();

        myMap.put(String.valueOf(OrgConstants.TEAM_TYPE.COLTEAM.ordinal()), ResourceUtil.getString("member.leave.collaboration.title") + ResourceUtil.getString("org.team_form.team"));
        myMap.put(String.valueOf(OrgConstants.TEAM_TYPE.DISCUSS.ordinal()), "WebIM"+ResourceUtil.getString("org.team_form.team"));
        myMap.put(String.valueOf(OrgConstants.TEAM_TYPE.PERSONAL.ordinal()), ResourceUtil.getString("org.team_form.personalteam"));
        myMap.put(String.valueOf(OrgConstants.TEAM_TYPE.PROJECT.ordinal()), ResourceUtil.getString("org.team_form.projectteam"));
        myMap.put(String.valueOf(OrgConstants.TEAM_TYPE.SYSTEM.ordinal()), ResourceUtil.getString("org.team_form.systemteam"));

        return myMap;
    }

}
