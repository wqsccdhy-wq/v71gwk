package com.seeyon.ctp.ociporg.manager.impl;

import java.util.List;

import com.seeyon.ctp.ociporg.manager.OrgUnitTempManager;
import com.seeyon.ctp.ociporg.po.OrgUnitTemp;
import com.seeyon.ctp.util.Strings;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.seeyon.ctp.ociporg.manager.AbsOcipOrgManager;
import com.seeyon.ctp.ociporg.manager.OrgUserLevelTempManager;
import com.seeyon.ctp.ociporg.po.OrgUserLevelTemp;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.OrganizationMessage.MessageStatus;
import com.seeyon.ctp.organization.bo.OrganizationMessage.OrgMessage;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.util.FlipInfo;

public class OcipLevelManagerImpl extends AbsOcipOrgManager<OrgUserLevelTemp> {

    private static final Logger LOGGER = Logger.getLogger(OcipLevelManagerImpl.class);

    private OrgUserLevelTempManager orgUserLevelTempManager;

    private OrgUnitTempManager orgUnitTempManager;

    @Override
    public void importOrg(String resourceId, FlipInfo flipInfo) {
        Short isFlag = new Short("0");
        Long allCount = orgUserLevelTempManager.getCount(isFlag, resourceId);
        if (allCount < 1) {
            String info = "resourceId:" + resourceId + "|没有需要导入的职务,|isFlag:" + isFlag;
            LOGGER.info(info);
            System.out.println(info);
            return;
        }

        /**
         * TODO 测试
         */

        /*OrgUserLevelTemp orgUserLevel = orgUserLevelTempManager.findOrgUserLevelTempById("-1134340625704991845");
        importEntry(orgUserLevel , resourceId);
        
        OrgUserLevelTemp orgUserLevel1 = orgUserLevelTempManager.findOrgUserLevelTempById("-4211908984755699130");
        importEntry(orgUserLevel1 , resourceId);*/

        String info = "resourceId:" + resourceId + "| 的职务一共有:" + allCount + "条数据需要导入!!!";
        LOGGER.info(info);
        System.out.println(info);
        Long count = (allCount / 20l) + 1;

        for (long i = 0; i < count; i++) {
            List<OrgUserLevelTemp> list =
                orgUserLevelTempManager.findOrgUserLevelTempByFlag(isFlag, resourceId, flipInfo);
            if (!CollectionUtils.isEmpty(list)) {
                for (OrgUserLevelTemp orgUserLevelTemp : list) {
                    importEntry(orgUserLevelTemp, resourceId);
                }
            }
        }

    }

    @Override
    public void importEntry(OrgUserLevelTemp t, String resourceId) {
        long beginTime = System.currentTimeMillis();
        MessageStatus messageStatus = null;
        Short result = new Short("1");
        String id = t.getId();
        String name = t.getName();
        boolean success = false;
        try {
            V3xOrgLevel orgLevel = initOrgLevel(t);
            if (orgLevel == null) {
                return;
            }
            OrganizationMessage message = orgManagerDirect.addLevel(orgLevel);
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
            String info = "生成职务结果,职务:" + name + "|id:" + id + "|result:" + result + "|messageStatus:" + messageStatus
                + "|耗时:" + time + "秒";
            LOGGER.info(info);
            System.out.println(info);
        } catch (Exception e) {
            success = false;
            t.setIsFlag(new Short("3"));
            LOGGER.error("生成职务异常,职务:" + name + "|id:" + id, e);
        } finally {
            try {
                orgUserLevelTempManager.updateOrgUserLevelTemp(t);
                addLog(messageStatus.toString(), resourceId, id, name, "LEVEL", success);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private V3xOrgLevel initOrgLevel(OrgUserLevelTemp t) {
        V3xOrgLevel orgLevel = new V3xOrgLevel();
        String id = t.getObjectId();
        String unitId = t.getUnitId();
        Integer sortId = t.getSortId();
        Integer isEnable = t.getIsEnable();
        Integer delFlag = t.getDelFlag();
        String name = t.getName();
        String resourceId = t.getResourceId();

        if (Strings.isEmpty(unitId)) {
            String info = "职务:" + name + "|id:" + id + "|resourceId:" + resourceId + ",unitId为空，导入失败!!";
            LOGGER.info(info);
            addLog(info, resourceId, id, name, "POST", false);
            return null;
        }
        OrgUnitTemp unitTemp = orgUnitTempManager.findOrgUnitTempById(unitId);
        if (unitTemp == null) {
            String info = "职务:" + name + "|id:" + id + "|resourceId:" + resourceId + ",所属单位不存在，导入失败!!";
            LOGGER.info(info);
            addLog(info, resourceId, id, name, "POST", false);
            return null;
        }
        String objectId = unitTemp.getObjectId();
        if (Strings.isEmpty(objectId)) {
            String info = "职务:" + name + "|id:" + id + "|resourceId:" + resourceId + ",objectId不存在，导入失败!!";
            LOGGER.info(info);
            addLog(info, resourceId, id, name, "POST", false);
            return null;
        }

        orgLevel.setId(Long.valueOf(id));
        orgLevel.setName(t.getName());
        orgLevel.setOrgAccountId(Long.valueOf(objectId));
        orgLevel.setSortId(Long.valueOf(sortId));

        if (0 == isEnable) {
            orgLevel.setEnabled(true);
        } else {
            orgLevel.setEnabled(false);
        }

        if (0 == delFlag) {
            orgLevel.setIsDeleted(false);
        } else {
            orgLevel.setIsDeleted(true);
        }

        orgLevel.setStatus(1);
        orgLevel.setLevelId(1);

        return orgLevel;
    }

    public OrgUserLevelTempManager getOrgUserLevelTempManager() {
        return orgUserLevelTempManager;
    }

    public void setOrgUserLevelTempManager(OrgUserLevelTempManager orgUserLevelTempManager) {
        this.orgUserLevelTempManager = orgUserLevelTempManager;
    }

    public OrgUnitTempManager getOrgUnitTempManager() {
        return orgUnitTempManager;
    }

    public void setOrgUnitTempManager(OrgUnitTempManager orgUnitTempManager) {
        this.orgUnitTempManager = orgUnitTempManager;
    }
}
