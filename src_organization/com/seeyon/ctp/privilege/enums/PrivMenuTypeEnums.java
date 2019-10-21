package com.seeyon.ctp.privilege.enums;

/**
 * Created by zhiyanqiang on 2017-11-14.
 */
public enum PrivMenuTypeEnums {
    systemPresetMenu(0,"系统预置菜单"),
    businessCapMenu(1,"系统原有的业务生成器菜单"),
    customerMenu(2,"用户自己添加的菜单"),
    businessCap4Menu(3,"cap4对应的菜单"),
    linksystemMenu(4,"关联系统中添加的菜单"),
    reportSpaceMenu(5,"报表空间添加的菜单");

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private int    key;
    private String text;
    PrivMenuTypeEnums(){

    }
    PrivMenuTypeEnums(int key, String text) {
        this.key = key;
        this.text = text;
    }

}
