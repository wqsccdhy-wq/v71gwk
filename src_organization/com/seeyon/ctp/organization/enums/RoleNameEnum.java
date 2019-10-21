/**
 * $Author: yans $
 * $Rev: 6089 $
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
public enum RoleNameEnum implements EnumsCode {

    auditadmin("AuditAdmin", ResourceUtil.getString("org.auditAdminName.value")), 
    accountadmin("AccountAdmin", ResourceUtil.getString("org.account_form.adminName.value")),
    systemadmin("SystemAdmin", ResourceUtil.getString("org.account_form.systemAdminName.value")), 
    groupadmin("GroupAdmin", ResourceUtil.getString("org.account_form.groupAdminName.value"));

    private String key;
    private String text;

    RoleNameEnum(String key, String text) {
        this.key = key;
        this.text = text;
    }

    public String getKey() {
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
