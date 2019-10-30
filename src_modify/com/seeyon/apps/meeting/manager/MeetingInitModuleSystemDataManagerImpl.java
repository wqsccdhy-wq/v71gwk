/**
 * 
 */
package com.seeyon.apps.meeting.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.exceptions.BusinessException;

/**
 * @author : muj
 * @Date : 2017年3月16日
 */
public class MeetingInitModuleSystemDataManagerImpl implements MeetingInitModuleSystemDataManager {

    private static final Log LOG = LogFactory.getLog(MeetingInitModuleSystemDataManagerImpl.class);

    public MeetingTypeManager meetingTypeManager;
    public PublicResourceManager publicResourceManager;

    public void setMeetingTypeManager(MeetingTypeManager meetingTypeManager) {
        this.meetingTypeManager = meetingTypeManager;
    }

    public void setPublicResourceManager(PublicResourceManager publicResourceManager) {
        this.publicResourceManager = publicResourceManager;
    }

    @Override
    public void initModuleSystemData(long accountId) throws BusinessException {

        LOG.info("开始为单位" + accountId + "复制会议分类...");
        // this.meetingTypeManager.generateAccountDefaultMeetingType(accountId);
        LOG.info(accountId + "复制会议分类结束。");

        LOG.info("开始为单位" + accountId + "复制会议用品...");
        // this.publicResourceManager.generateAccountDefaultMeetingResource(accountId);
        LOG.info(accountId + "复制会议用品结束。");
    }

}
