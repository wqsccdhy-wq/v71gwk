/**
 * $Author: yans $
 * $Rev: 5009 $
 * $Date:: #$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.enums;

import com.seeyon.ctp.common.code.EnumsCode;
import com.seeyon.ctp.common.i18n.ResourceUtil;

/**
 * <p>Title: 自定义资源类别枚举实现类</p>
 * <p>Description: 资源类别枚举实现类</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public enum CustomResourceCategoryEnums implements EnumsCode {

    customfront(0, ResourceUtil.getString("org.enums.custom.foreground.resources")),
    customback(1, ResourceUtil.getString("org.enums.custom.background.resources"));

    private int    key;
    private String text;

    CustomResourceCategoryEnums(int key, String text) {
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
