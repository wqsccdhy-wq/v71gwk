/**
 * $Author: wuym $
 * $Rev: 20897 $
 * $Date:: #$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.organization.enums;

import com.seeyon.ctp.common.code.EnumsCode;
import com.seeyon.ctp.common.i18n.ResourceUtil;

/**
 * <p>Title: 角色是否默认角色枚举实现类</p>
 * <p>Description: 资源类型枚举实现类</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public enum RoleTypeEnum implements EnumsCode {
	
    systemflag(1, "role.fixed"), relativeflag(2, "role.reciprocal"), cunstomflag(3, "role.add"),reportflag(5, "role.report");

    private int    key;
    private String text;

    RoleTypeEnum(int key, String text) {
        this.key = key;
        this.text = text;
    }

    public int getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return String.valueOf(key);
    }

    @Override
    public String getText() {
        return ResourceUtil.getString(text);
    }
}
