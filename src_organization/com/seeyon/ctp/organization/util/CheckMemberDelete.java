package com.seeyon.ctp.organization.util;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgMember;

/**
 * <p>Title: 人员校验接口</p>
 * <p>Description: 用于校验人员删除前的校验接口，以供外部接口实现</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * <p></p>
 * 
 * @author lilong
 * @version CTP2.0 2013-04-07
 */
public interface CheckMemberDelete {

    /**
     * 校验某个人员是否允许被删除
     * @param member
     * @return true可以删除，false不可以删除
     */
    boolean canDeleteMember(V3xOrgMember member) throws BusinessException;
}
