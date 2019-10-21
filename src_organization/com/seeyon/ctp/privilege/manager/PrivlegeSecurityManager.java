package com.seeyon.ctp.privilege.manager;

import java.util.Set;

import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;

public interface PrivlegeSecurityManager {

    /**
     * 
     * @方法名称: validateRole
     * @功能描述: 纯权限校验
     * @参数 ：@param user
     * @参数 ：@param methodName
     * @参数 ：@param roleTypes
     * @参数 ：@throws BusinessException
     * @返回类型：void
     * @创建时间 ：2015年11月18日 下午2:16:19
     * @创建人 ： FuTao
     * @修改人 ： 
     * @修改时间 ：
     */
    public void validateRole(User user, String methodName, Set<String> roleTypes, Set<String> extendRoles) throws BusinessException;

}
