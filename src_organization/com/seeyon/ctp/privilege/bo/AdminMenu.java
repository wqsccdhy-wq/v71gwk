package com.seeyon.ctp.privilege.bo;

import java.util.List;

public class AdminMenu {

    private String          code;
    private String          name;
    private String          desc;
    private String          icon;
    private String          target;
    private String          plugin;
    private String          url;
    private Integer         sort;
    private List<AdminMenu> submenus;

    public AdminMenu(String code, String name, String desc, String icon, String target, String plugin, String url, Integer sort) {
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.icon = icon;
        this.target = target;
        this.plugin = plugin;
        this.url = url;
        this.sort = sort;
    }

    public AdminMenu(AdminMenu adminMenu) {
        this.code = adminMenu.getCode();
        this.name = adminMenu.getName();
        this.desc = adminMenu.getDesc();
        this.icon = adminMenu.getIcon();
        this.target = adminMenu.getTarget();
        this.plugin = adminMenu.getPlugin();
        this.url = adminMenu.getUrl();
        this.sort = adminMenu.getSort();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<AdminMenu> getSubmenus() {
        return submenus;
    }

    public void setSubmenus(List<AdminMenu> submenus) {
        this.submenus = submenus;
    }

}
