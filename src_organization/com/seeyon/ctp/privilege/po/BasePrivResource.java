package com.seeyon.ctp.privilege.po;

public interface BasePrivResource {
    /**
     *  资源代码，全局不允许重复
     */
    String getResourceCode();
    /**
     *  链接
     */
    String getNavurl();
    /**
     *  是否需要控制
     */
    Boolean isControl();
}
