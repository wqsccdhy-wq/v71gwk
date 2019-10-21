/**
 * $Author: yans $
 * $Rev: 5075 $
 * $Date:: #$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.enums;

import com.seeyon.ctp.common.code.EnumsCode;

/**
 * <p>Title: 资源类型枚举实现类</p>
 * <p>Description: 资源类型枚举实现类</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public enum ResourceTypeEnums implements EnumsCode {

    url(0, "url"),
    ajax(1, "ajax");

    private int    key;
    private String text;

    ResourceTypeEnums(int key, String text) {
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
        return text;
    }
}
