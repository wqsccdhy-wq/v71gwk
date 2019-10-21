package com.seeyon.ctp.organization.util;

import java.util.Collection;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgMember;

/**
 * <p>Title: 角色关系变更校验接口</p>
 * <p>Description: 用于校验角色修改授权人员校验，以供外部业务接口实现</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * <p></p>
 * 
 * @author lilong
 * @version CTP2.0 2013-11-15
 */
public interface CheckPrivUpdate {

    /**
     * @param newMembers 角色新增的人员
     * @param delMembers 角色授权取消的人员
     * @param rolename 角色名称
     * @param unitId 单位ID或者部门ID，（如果是单位角色就传单位ID；如果是部门角色就传部门ID）
     * @return String为null可以删除，不为空不可以删除，内容为不能删除的提示信息
     */
    String processUpdate(Collection<V3xOrgMember> newMembers, Collection<V3xOrgMember> delMembers, String rolename, Long unitId) throws BusinessException;
}
