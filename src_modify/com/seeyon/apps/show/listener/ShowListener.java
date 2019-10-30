/**
 * Author shuqi Rev Date: 2016年2月26日 下午3:03:20
 *
 * Copyright (C) 2016 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc. Use is subject to license terms.
 * 
 * @since v5 v6.0
 */
package com.seeyon.apps.show.listener;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;

import com.seeyon.apps.show.manager.ShowManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.plugin.PluginAddEvent;
import com.seeyon.ctp.organization.event.AddAccountEvent;
import com.seeyon.ctp.util.annotation.ListenEvent;

/**
 * <p>
 * Description:大秀平台事件监听器
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: seeyon.com
 * </p>
 * <p>
 * @since v5 v6.0
 * </p>
 */
public class ShowListener {
    private ShowManager showManager;
    private Log logger = CtpLogFactory.getLog(ShowListener.class);

    @ListenEvent(event = AddAccountEvent.class)
    public void onAddAccount(AddAccountEvent evt) throws BusinessException {
        try {
            logger.info("新建了单位初始化秀开始,add account init show data starting");
            // showManager.initShowData(evt.getAccount(), false);
            logger.info("新建了单位初始化秀结束,add account init show data ");
        } catch (Exception e) {
            logger.error("新建了单位初始化秀出错了，add account init show data fail", e);
            throw new BusinessException(e);
        }
    }

    @ListenEvent(event = PluginAddEvent.class)
    public void onPluginAdd(PluginAddEvent evt) throws BusinessException {
        Set<String> plugins = evt.getPlugins();
        if (CollectionUtils.isNotEmpty(plugins) && plugins.contains("show")) {
            // 启用秀插件补偿
            try {
                logger.info("启用秀插件补偿开始 , add plugins init show data starting");
                showManager.initShowData(null, true);
                logger.info("启用秀插件补偿完成 , add plugins init show data end");
            } catch (Exception e) {
                logger.error("启用秀插件补偿出错了 , add plugins init show data fail", e);
                throw new BusinessException(e);
            }
        }
    }

    public void setShowManager(ShowManager showManager) {
        this.showManager = showManager;
    }

}