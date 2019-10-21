/**
 * $Author: yans $
 * $Rev: 2638 $
 * $Date:: 2012-08-25 11:13:28#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.exceptions;

import com.seeyon.ctp.common.exceptions.BusinessException;

/**
 * <p>Title: DAO层产生的异常</p>
 * <p>Description: 本程序实现当新增和更新菜单或者资源时操作的数据已存在或者不存在时造成的异常</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public class PrivilegeExistException extends BusinessException {

    /** */
    private static final long serialVersionUID = -2147557666243633766L;

    public PrivilegeExistException() {
        super();
    }
}
