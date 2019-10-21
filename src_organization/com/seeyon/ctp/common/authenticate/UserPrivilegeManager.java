package com.seeyon.ctp.common.authenticate;

import java.util.List;

import com.seeyon.ctp.privilege.po.BasePrivResource;

public interface UserPrivilegeManager {
    /**
     * 获得所有缓存的资源对象
     * @return 资源对象列表
     */
    List<BasePrivResource> getAllResources();
}
