package com.seeyon.ctp.util.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.seeyon.ctp.organization.OrgConstants.Role_NAME;

/**
 * <pre>
 * 在系统中需要进行角色权限校验的场景，使用此注解进行声明，声明的方式有两种：
 * 1.在场景对应的Controller的方法上面声明，并加上所要进行权限校验的角色类型(如单位管理员、集团管理员等)
 * 比如，修改单位信息，是单位管理员或集团管理员可以进行的操作，其注解声明如下：<br>
 * @CheckRoleAccess(roleTypes={Role_NAME.AccountAdministrator, Role_NAME.GroupAdmin})
 * public ModelAndView editAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
 * 		......
 * }<br>
 * 2.如果某个类中的全部方法，只针对一种角色权限校验，也可以简单的在类上面进行声明：<br>
 * @CheckRoleAccess(roleTypes={Role_NAME.AccountAdministrator, Role_NAME.GroupAdmin})
 * public class BulTypeController extends BaseController {
 * 		......
 * }<br>
 * 另外支持扩展角色权限校验
 * 比如，公告版块设置，是单位管理员或集团管理员或空间管理员可以进行的操作，其注解声明如下：<br>
 * "SpaceManager"为自定义的字符串，对应{@link com.seeyon.ctp.util.annotation.ExtendCheck}的实现类中getName()，并且使用check校验<br>
 * @CheckRoleAccess(roleTypes = { Role_NAME.AccountAdministrator, Role_NAME.GroupAdmin }, extendRoles = { "SpaceManager" })
 * public class BulTypeController extends BaseController {
 *      ......
 * }<br>
 * 当请求发起时，定义好的拦截器{@link com.seeyon.ctp.common.spring.CTPHandlerInterceptor}会根据注解信息进行权限校验
 * 注意：拦截器优先处理方法上的注解，如果有，以此为准，如果没有，再去寻找类上的注解，如果有，以此为准，如果没有，表明不需进行校验
 * </pre>
 * 
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-6-30
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface CheckRoleAccess {

    Role_NAME[] roleTypes() default {};

    String[] extendRoles() default {};

}