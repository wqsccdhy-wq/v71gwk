package com.seeyon.apps.ldap.util;

import com.seeyon.ctp.common.po.usermapper.CtpOrgUserMapper;

public interface Authenticator {
    public CtpOrgUserMapper auth(String uid, String password);
}//end class
