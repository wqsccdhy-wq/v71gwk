package com.seeyon.ctp.organization.dao;

import com.seeyon.ctp.common.AbstractSystemInitializer;

public class OrgMetadataInitializer extends AbstractSystemInitializer {

    private OrgCache orgCache;

    public void setOrgCache(OrgCache orgCache) {
        this.orgCache = orgCache;
    }

    @Override
    public void initialize() {
        orgCache.init();
    }
}
