package com.seeyon.ctp.privilege.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;

public interface FormMenuManager {

    /**
     * 判断业务生成器菜单的权限
     * 
     * @param memberId
     * @param accountId
     * @param roleMenus
     * @return
     * @throws BusinessException
     */
    List<PrivMenuBO> checkMenuAuth(Long memberId, Long accountId, Map<V3xOrgRole, List<PrivMenuBO>> roleMenus) throws BusinessException;

}
