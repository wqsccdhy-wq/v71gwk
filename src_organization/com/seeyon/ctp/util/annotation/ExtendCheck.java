package com.seeyon.ctp.util.annotation;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.ctp.common.authenticate.domain.User;

/**
 * 扩展角色权限校验
 */
public interface ExtendCheck {

    /**
     * 扩展角色权限校验标识
     * {@link com.seeyon.ctp.util.annotation.CheckRoleAccess}，extendRoles注解时使用
     * @return
     */
    public String getName();

    /**
     * 扩展角色权限校验方法
     * @param request
     * @param user
     * @return
     */
    public boolean check(HttpServletRequest request, User user);

}
