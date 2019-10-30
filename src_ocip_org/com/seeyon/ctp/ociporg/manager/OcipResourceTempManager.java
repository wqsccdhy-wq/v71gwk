package com.seeyon.ctp.ociporg.manager;

import java.util.List;

import com.seeyon.ctp.ociporg.po.OcipResourceTemp;

public interface OcipResourceTempManager {

    public List<OcipResourceTemp> findOcipResourceTemp(Short isFlag);

    public void updatOcipResourceTemp(OcipResourceTemp ocipResourceTemp) throws Exception;

    public List<OcipResourceTemp> findOcipResourceTempById(String id);
}
