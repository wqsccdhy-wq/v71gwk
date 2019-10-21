package com.seeyon.ctp.util.annotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.cache.CachePojoManager;

/**
 * 
 * @类名称: CheckRoleAccessAnnotationAware
 * @类描述描述: 项目启动最后扫描注解信息，加入缓存
 * @参数 ：
 * @创建时间 ：2015年11月18日 下午5:00:17
 */
public class CheckRoleAccessAnnotationAware extends AbstractSystemInitializer implements AnnotationAware {

    private static final Log   logger = LogFactory.getLog(CheckRoleAccessAnnotationAware.class);

    static Map<String, Object> cache  = CachePojoManager.getCacheMap();

    private AnnotationFactory  annotationFactory;

    public void setAnnotationFactory(AnnotationFactory annotationFactory) {
        this.annotationFactory = annotationFactory;
    }

    public void initialize() {
        logger.info(" -- 加载注解缓存开始...");
        Long startTime = System.currentTimeMillis();

        /** 通过注解读取的角色权限校验信息，key - clazzName, value - List&ltRoleType&gt */
        Map<String, Set<String>> clazzNeedRoleCheck = new ConcurrentHashMap<String, Set<String>>();
        /** 通过注解读取的扩展角色权限校验信息，key - clazzName, value - List&ltextendRole&gt */
        Map<String, Set<String>> clazzNeedExtendRoleCheck = new ConcurrentHashMap<String, Set<String>>();
        /** 通过注解读取的角色权限校验信息，key - methodName, value - List&ltRoleType&gt */
        Map<String, Set<String>> methodNeedRoleCheck = new ConcurrentHashMap<String, Set<String>>();
        /** 通过注解读取的扩展角色权限校验信息，key - methodName, value - List&ltextendRole&gt */

        Map<String, Set<String>> methodNeedExtendRoleCheck = new ConcurrentHashMap<String, Set<String>>();
        Set<ClassAnnotation> classAnnotations = annotationFactory.getAnnotationOfClass(CheckRoleAccess.class);
        Set<MethodAnnotation> methodAnnotation = annotationFactory.getAnnotationOfMethod(CheckRoleAccess.class);
        if (Strings.isNotEmpty(methodAnnotation)) {
            for (MethodAnnotation methodAnno : methodAnnotation) {
                Role_NAME[] arr = ((CheckRoleAccess) methodAnno.getAnnotation()).roleTypes();
                Set<String> set = this.parseArr2Set(arr);
                if (Strings.isNotEmpty(set)) {
                    methodNeedRoleCheck.put(methodAnno.getClazz().getCanonicalName() + '.' + methodAnno.getMethodName(), set);
                }

                String[] arr1 = ((CheckRoleAccess) methodAnno.getAnnotation()).extendRoles();
                Set<String> set1 = this.parseArr2Set(arr1);
                if (Strings.isNotEmpty(set1)) {
                    methodNeedExtendRoleCheck.put(methodAnno.getClazz().getCanonicalName() + '.' + methodAnno.getMethodName(), set1);
                }
            }
        }

        cache.put("methodNeedRoleCheck", methodNeedRoleCheck);
        cache.put("methodNeedExtendRoleCheck", methodNeedExtendRoleCheck);

        if (Strings.isNotEmpty(classAnnotations)) {
            for (ClassAnnotation clazzAnno : classAnnotations) {
                Role_NAME[] arr = ((CheckRoleAccess) clazzAnno.getAnnotation()).roleTypes();
                Set<String> set = this.parseArr2Set(arr);
                if (Strings.isNotEmpty(set)) {
                    clazzNeedRoleCheck.put(clazzAnno.getClazz().getCanonicalName(), set);
                }

                String[] arr1 = ((CheckRoleAccess) clazzAnno.getAnnotation()).extendRoles();
                Set<String> set1 = this.parseArr2Set(arr1);
                if (Strings.isNotEmpty(set1)) {
                    clazzNeedExtendRoleCheck.put(clazzAnno.getClazz().getCanonicalName(), set1);
                }
            }
        }

        cache.put("clazzNeedRoleCheck", clazzNeedRoleCheck);
        cache.put("clazzNeedExtendRoleCheck", clazzNeedExtendRoleCheck);

        logger.info(" -- 加载注解缓存结束, 耗时" + (System.currentTimeMillis() - startTime) + "ms");
    }

    /** 将数组转换为对应的集合 */
    public static <T> List<T> parseArr2List(T[] arr) {
        List<T> result = null;
        if (arr != null && arr.length > 0) {
            result = new ArrayList<T>(arr.length);
            for (T t : arr) {
                result.add(t);
            }
        }
        return result;
    }

    /** 将数组转换为对应的集合 */
    public static <T> Set<String> parseArr2Set(T[] arr) {
        Set<String> result = null;
        if (arr != null && arr.length > 0) {
            result = new HashSet<String>(arr.length);
            for (T t : arr) {
                result.add(t.toString());
            }
        }
        return result;
    }

    public void destroy() {

    }

    public int getSortOrder() {
        return -7;
    }

}
