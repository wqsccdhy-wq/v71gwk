package com.seeyon.ctp.organization.bo;

import com.seeyon.ctp.organization.OrgConstants;

/**
 * 角色定义，提供扩展自定义角色支持。
 * <p>
 * 在Spring配置文件中进行配置，系统按类型查找，进行角色注册（统一管理)。
 * 注册时检查角色id是否重复与名称是否合法，如不通过则抛出异常提示，不允许启动。
 * </p>
 * 
 * @author wangwenyou
 * @author tanmf
 */
public class OrgRoleDefaultDefinition {
    // 角色英文名称,只能使用字母，不能带下划线，如HRAdmin
    private String id;
    // 中文名或i18n key
    private String name;

    // 绑定到单位或部门:0 不绑定,如系统管理员和审计管理员;1 单位，新建单位时自动在V3XOrgRole中增加一条记录； 2
    // 部门，新建部门时自动增加记录 3 绑定到用户
    private int    bond     = OrgConstants.ROLE_BOND.ACCOUNT.ordinal();
    // 绑定的插件Id，绑定插件不为空则角色只在插件启用时生效
    private String pluginId;
    // 1 FIX ;2 RELATIVE;3 USERROLE;
    private int    type         = V3xOrgEntity.ROLETYPE_FIXROLE;
    private String i18NResource = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";

    protected int  sortId       = -1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBond() {
        return bond;
    }

    public void setBond(int bond) {
        this.bond = bond;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public V3xOrgRole toRole() {
        V3xOrgRole role = new V3xOrgRole();

        role.setBond(this.getBond());
        role.setName(this.getId());
        role.setType(this.getType());
        role.setSortId(new Long(this.getSortId()));

        return role;
    }
}
