package com.seeyon.ctp.ociporg.manager.impl;

import java.util.List;

import com.seeyon.ctp.ociporg.manager.OrgUnitTempManager;
import com.seeyon.ctp.ociporg.po.OrgUnitTemp;
import com.seeyon.ctp.util.Strings;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.seeyon.ctp.ociporg.manager.AbsOcipOrgManager;
import com.seeyon.ctp.ociporg.manager.OrgPostTempManager;
import com.seeyon.ctp.ociporg.po.OrgPostTemp;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.OrganizationMessage.MessageStatus;
import com.seeyon.ctp.organization.bo.OrganizationMessage.OrgMessage;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.util.FlipInfo;

/**
 * 
 * @author Administrator 导入岗位
 *
 */
public class OcipPostManagerImpl extends AbsOcipOrgManager<OrgPostTemp> {

    private static final Logger LOGGER = Logger.getLogger(OcipPostManagerImpl.class);

    private OrgPostTempManager orgPostTempManager;

    private OrgUnitTempManager orgUnitTempManager;

    @Override
    public void importOrg(String resourceId, FlipInfo flipInfo) {
        Short isFlag = new Short("0");
        Long allCount = orgPostTempManager.getCount(isFlag, resourceId);
        if (allCount < 1) {
            String info = "resourceId:" + resourceId + "|没有需要导入的岗位,|isFlag:" + isFlag;
            LOGGER.info(info);
            System.out.println(info);
            return;
        }

        /**
         * TODO 测试 1305606665011019926
         */

        /*OrgPostTemp orgPostTemp1 = orgPostTempManager.findOrgPostTempById("1305606665011019926");
        importEntry(orgPostTemp1 , resourceId);
        
        OrgPostTemp orgPostTemp2 = orgPostTempManager.findOrgPostTempById("1310391220475938643");
        importEntry(orgPostTemp2 , resourceId);*/

        String info = "resourceId:" + resourceId + "| 的岗位一共有:" + allCount + "条数据需要导入!!!";
        LOGGER.info(info);
        System.out.println(info);
        Long count = (allCount / 20l) + 1;

        for (long i = 0; i < count; i++) {
            List<OrgPostTemp> list = orgPostTempManager.findOrgPostTempByFlag(isFlag, resourceId, flipInfo);
            if (!CollectionUtils.isEmpty(list)) {
                for (OrgPostTemp orgPostTemp : list) {
                    importEntry(orgPostTemp, resourceId);
                }
            }
        }

    }

    @Override
    public void importEntry(OrgPostTemp t, String resourceId) {
        long beginTime = System.currentTimeMillis();
        MessageStatus messageStatus = null;
        Short result = new Short("1");
        String id = t.getId();
        String name = t.getName();
        boolean success = false;
        try {
            V3xOrgPost post = initPost(t);
            if (post == null) {
                t.setIsFlag(new Short("2"));
                orgPostTempManager.updateOrgPostTemp(t);
                return;
            }
            OrganizationMessage message = orgManagerDirect.addPost(post);
            success = message.isSuccess();
            if (success) {
                result = new Short("1");
                List<OrgMessage> successMsgs = message.getSuccessMsgs();
                OrgMessage orgMessage = successMsgs.get(0);
                messageStatus = orgMessage.getCode();
            } else {
                result = new Short("2");
                List<OrgMessage> errorMsgs = message.getErrorMsgs();
                OrgMessage orgMessage = errorMsgs.get(0);
                messageStatus = orgMessage.getCode();
            }

            t.setIsFlag(result);
            long endTime = System.currentTimeMillis();
            long time = (endTime - beginTime) / 1000;
            String info = "生成岗位结果,岗位:" + name + "|id:" + id + "|result:" + result + "|messageStatus:" + messageStatus
                + "|耗时:" + time + "秒";
            LOGGER.info(info);
            System.out.println(info);
        } catch (Exception e) {
            success = false;
            t.setIsFlag(new Short("3"));
            LOGGER.error("生成岗位异常,岗位:" + name + "|id:" + id, e);
        } finally {
            try {
                orgPostTempManager.updateOrgPostTemp(t);
                String msg = "";
                if (messageStatus != null) {
                    msg = messageStatus.toString();
                }
                addLog(msg, resourceId, id, name, "POST", success);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private V3xOrgPost initPost(OrgPostTemp t) {
        V3xOrgPost post = new V3xOrgPost();
        String unitId = t.getUnitId();
        String id = t.getObjectId();
        String name = t.getName();
        String resourceId = t.getResourceId();
        post.setName(name);
        if (Strings.isEmpty(id)){
            String info = "岗位:" + name + "|id:" + t.getId() + "|resourceId:" + resourceId + ",ObjectId为空，导入失败!!";
            LOGGER.info(info);
            addLog(info, resourceId, t.getId(), name, "POST", false);
            return null;
        }

        if (Strings.isEmpty(unitId)) {
            String info = "岗位:" + name + "|id:" + t.getId() + "|resourceId:" + resourceId + ",unitId为空，导入失败!!";
            LOGGER.info(info);
            addLog(info, resourceId, id, name, "POST", false);
            return null;
        }
        OrgUnitTemp unitTemp = orgUnitTempManager.findOrgUnitTempById(unitId);
        if (unitTemp == null) {
            String info = "岗位:" + name + "|id:" + t.getId() + "|resourceId:" + resourceId + ",所属单位不存在，导入失败!!";
            LOGGER.info(info);
            addLog(info, resourceId, t.getId(), name, "POST", false);
            return null;
        }
        String objectId = unitTemp.getObjectId();
        if (Strings.isEmpty(objectId)) {
            String info = "岗位:" + name + "|id:" + t.getId() + "|resourceId:" + resourceId + ",objectId不存在，导入失败!!";
            LOGGER.info(info);
            addLog(info, resourceId, t.getId(), name, "POST", false);
            return null;
        }
        post.setOrgAccountId(Long.valueOf(objectId));
        post.setId(Long.valueOf(id));
        post.setSortId(Long.valueOf(t.getSortId()));

        Integer isEnable = t.getIsEnable();
        Integer delFlag = t.getDelFlag();

        if (0 == isEnable) {
            post.setEnabled(true);
        } else {
            post.setEnabled(false);
        }

        if (0 == delFlag) {
            post.setIsDeleted(false);
        } else {
            post.setIsDeleted(true);
        }

        post.setTypeId(1l);

        return post;
    }

    public OrgPostTempManager getOrgPostTempManager() {
        return orgPostTempManager;
    }

    public void setOrgPostTempManager(OrgPostTempManager orgPostTempManager) {
        this.orgPostTempManager = orgPostTempManager;
    }

    public OrgUnitTempManager getOrgUnitTempManager() {
        return orgUnitTempManager;
    }

    public void setOrgUnitTempManager(OrgUnitTempManager orgUnitTempManager) {
        this.orgUnitTempManager = orgUnitTempManager;
    }
}
