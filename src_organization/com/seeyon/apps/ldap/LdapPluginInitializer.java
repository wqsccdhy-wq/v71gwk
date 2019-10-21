package com.seeyon.apps.ldap;

import org.apache.log4j.Logger;

import com.seeyon.ctp.common.plugin.PluginDefinition;
import com.seeyon.ctp.common.plugin.PluginInitializer;

public class LdapPluginInitializer implements PluginInitializer {

    @Override
    public boolean isAllowStartup(PluginDefinition pd, Logger log) {
        return "1".equals(pd.getPluginProperty("ldap.enabled"));
    }

}
