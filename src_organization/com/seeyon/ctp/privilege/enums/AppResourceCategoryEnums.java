/**
 * $Author: gaohang $
 * $Rev: 14502 $
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
public enum AppResourceCategoryEnums implements EnumsCode {

   
    ForegroundApplication(1, ResourceUtil.getString("org.enums.fore.ground.Application")), 
    ForegroundShortcut(2, ResourceUtil.getString("org.enums.fore.ground.Shortcut"));
    

    private int    key;
    private String text;

    AppResourceCategoryEnums(int key, String text) {
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
