package com.seeyon.ctp.ociporg.manager.impl;

import java.util.List;

import com.seeyon.ctp.ociporg.dao.OrgUserLevelTempDao;
import com.seeyon.ctp.ociporg.manager.OrgUserLevelTempManager;
import com.seeyon.ctp.ociporg.po.OrgUserLevelTemp;
import com.seeyon.ctp.util.FlipInfo;

public class OrgUserLevelTempManagerImpl implements OrgUserLevelTempManager {

    private OrgUserLevelTempDao orgUserLevelTempDao;

    @Override
    public OrgUserLevelTemp findOrgUserLevelTempById(String id) {
        return orgUserLevelTempDao.findOrgUserLevelTempById(id);
    }

    @Override
    public List<OrgUserLevelTemp> findOrgUserLevelTempByFlag(Short isFlag, String resourceId, FlipInfo flipInfo) {
        return orgUserLevelTempDao.findOrgUserLevelTempByFlag(isFlag, resourceId, flipInfo);
    }

    @Override
    public void updateOrgUserLevelTemp(OrgUserLevelTemp orgUserLevelTemp) {
        orgUserLevelTempDao.updateOrgUserLevelTemp(orgUserLevelTemp);
    }

    @Override
    public Long getCount(Short isFlag, String resourceId) {
        return orgUserLevelTempDao.getCount(isFlag, resourceId);
    }

    public OrgUserLevelTempDao getOrgUserLevelTempDao() {
        return orgUserLevelTempDao;
    }

    public void setOrgUserLevelTempDao(OrgUserLevelTempDao orgUserLevelTempDao) {
        this.orgUserLevelTempDao = orgUserLevelTempDao;
    }

}
