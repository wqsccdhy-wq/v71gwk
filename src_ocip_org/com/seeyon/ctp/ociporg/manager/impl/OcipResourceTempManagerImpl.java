package com.seeyon.ctp.ociporg.manager.impl;

import java.util.List;

import com.seeyon.ctp.ociporg.dao.OcipResourceTempDao;
import com.seeyon.ctp.ociporg.manager.OcipResourceTempManager;
import com.seeyon.ctp.ociporg.po.OcipResourceTemp;

public class OcipResourceTempManagerImpl implements OcipResourceTempManager {

    private OcipResourceTempDao ocipResourceTempDao;

    @Override
    public List<OcipResourceTemp> findOcipResourceTemp(Short isFlag) {
        return ocipResourceTempDao.findOcipResourceTemp(isFlag);
    }

    @Override
    public List<OcipResourceTemp> findOcipResourceTempById(String id) {
        return ocipResourceTempDao.findOcipResourceTempById(id);
    }

    @Override
    public void updatOcipResourceTemp(OcipResourceTemp ocipResourceTemp) throws Exception {
        ocipResourceTempDao.updatOcipResourceTemp(ocipResourceTemp);
    }

    public OcipResourceTempDao getOcipResourceTempDao() {
        return ocipResourceTempDao;
    }

    public void setOcipResourceTempDao(OcipResourceTempDao ocipResourceTempDao) {
        this.ocipResourceTempDao = ocipResourceTempDao;
    }

}
