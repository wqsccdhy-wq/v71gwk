package com.seeyon.ctp.ociporg.dao;

import java.util.List;

import com.seeyon.ctp.ociporg.po.OcipResourceTemp;

public interface OcipResourceTempDao {

    public List<OcipResourceTemp> findOcipResourceTemp(Short isFlag);

    public void updatOcipResourceTemp(OcipResourceTemp ocipResourceTemp) throws Exception;

    public List<OcipResourceTemp> findOcipResourceTempById(String id);

}
