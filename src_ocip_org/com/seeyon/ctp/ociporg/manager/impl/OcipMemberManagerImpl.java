package com.seeyon.ctp.ociporg.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Strings;
import com.seeyon.ctp.ociporg.dao.OcipOrgRoleDao;
import com.seeyon.ctp.ociporg.dao.OrgUserLevelTempDao;
import com.seeyon.ctp.ociporg.manager.AbsOcipOrgManager;
import com.seeyon.ctp.ociporg.manager.OrgDepartmentTempManager;
import com.seeyon.ctp.ociporg.manager.OrgPostTempManager;
import com.seeyon.ctp.ociporg.manager.OrgRelationTempManager;
import com.seeyon.ctp.ociporg.manager.OrgUnitTempManager;
import com.seeyon.ctp.ociporg.manager.OrgUserJoinTempManager;
import com.seeyon.ctp.ociporg.po.OrgDepartmentTemp;
import com.seeyon.ctp.ociporg.po.OrgPostTemp;
import com.seeyon.ctp.ociporg.po.OrgRelationTemp;
import com.seeyon.ctp.ociporg.po.OrgUnitTemp;
import com.seeyon.ctp.ociporg.po.OrgUserJoinTemp;
import com.seeyon.ctp.ociporg.po.OrgUserLevelTemp;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.OrganizationMessage.MessageStatus;
import com.seeyon.ctp.organization.bo.OrganizationMessage.OrgMessage;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.organization.po.OrgLevel;
import com.seeyon.ctp.organization.po.OrgPost;
import com.seeyon.ctp.util.FlipInfo;

public class OcipMemberManagerImpl extends AbsOcipOrgManager<OrgUserJoinTemp> {

    private static final Logger LOGGER = Logger.getLogger(OcipMemberManagerImpl.class);

    private OrgUserJoinTempManager orgUserJoinTempManager;

    private OrgRelationTempManager orgRelationTempManager;

    private OcipOrgRoleDao ocipOrgRoleDao;

    private OrgDepartmentTempManager orgDepartmentTempManager;

    private OrgUnitTempManager orgUnitTempManager;

    private OrgPostTempManager orgPostTempManager;

    private OrgUserLevelTempDao orgUserLevelTempDao;

    @Override
    public void importOrg(String resourceId, FlipInfo flipInfo) {
        Short isFlag = new Short("0");
        Long allCount = orgUserJoinTempManager.getCount(isFlag, resourceId);
        if (allCount < 1) {
            String info = "resourceId:" + resourceId + "|没有需要导入的人员,|isFlag:" + isFlag;
            LOGGER.info(info);
            System.out.println(info);
            return;
        }

        /**
         * TODO 测试
         */
        // 严柏鑫
        /*OrgUserJoinTemp orgUserJoinTemp = orgUserJoinTempManager.findOrgUserJoinTempById("5930610715977862833");
        importEntry(orgUserJoinTemp , resourceId);
        
        //周浩
        OrgUserJoinTemp orgUserJoinTemp1 = orgUserJoinTempManager.findOrgUserJoinTempById("-727955267357749758");
        importEntry(orgUserJoinTemp1 , resourceId);*/

        String info = "resourceId:" + resourceId + "| 的人员一共有:" + allCount + "条数据需要导入!!!";
        LOGGER.info(info);
        System.out.println(info);
        Long count = (allCount / 20l) + 1;
        for (long i = 0; i < count; i++) {
            List<OrgUserJoinTemp> lists =
                orgUserJoinTempManager.findOrgUserJoinTempByGrade(isFlag, resourceId, flipInfo);
            if (!CollectionUtils.isEmpty(lists)) {
                for (OrgUserJoinTemp orgUserJoinTemp : lists) {
                    importEntry(orgUserJoinTemp, resourceId);
                }
            }
        }

    }

    @Override
    public void importEntry(OrgUserJoinTemp orgUserJoinTemp, String resourceId) {
        long beginTime = System.currentTimeMillis();
        MessageStatus messageStatus = null;
        Short result = new Short("1");
        String id = orgUserJoinTemp.getId();
        String name = orgUserJoinTemp.getName();
        boolean success = false;
        String messageInfo = "";
        try {
            V3xOrgMember orgMember = initMember(orgUserJoinTemp, resourceId);
            if (orgMember != null) {
                OrganizationMessage message = orgManagerDirect.addMember(orgMember);
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

                orgUserJoinTemp.setIsFlag(result);
                long endTime = System.currentTimeMillis();
                long time = (endTime - beginTime) / 1000;
                String info = "生成人员结果,人员:" + name + "|id:" + id + "|result:" + result + "|messageStatus:"
                    + messageStatus + "|耗时:" + time + "秒";
                LOGGER.info(info);
                System.out.println(info);
            } else {
                orgUserJoinTemp.setIsFlag(new Short("4"));
            }
        } catch (Exception e) {
            messageInfo = e.getMessage();
            success = false;
            orgUserJoinTemp.setIsFlag(new Short("3"));
            LOGGER.error("生成人员异常,人员:" + name + "|id:" + id, e);
        } finally {
            try {
                orgUserJoinTempManager.updatOrgUserJoinTemp(orgUserJoinTemp);
                if (messageStatus != null) {
                    addLog(messageStatus.toString(), resourceId, id, name, "MEMBER", success);
                } else {
                    addLog(messageInfo, resourceId, id, name, "MEMBER", success);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    private V3xOrgMember initMember(OrgUserJoinTemp orgUserJoinTemp, String resourceId) {
        V3xOrgMember member = new V3xOrgMember();
        String id = orgUserJoinTemp.getObjectId();
        if (Strings.isNullOrEmpty(id)) {
            String name = member.getName();
            String info = "人员:" + name + "|id:" + id + "|resourceId:" + resourceId + "ObjectId为空，导入失败!!";
            LOGGER.info(info);
            System.out.println(info);
            addLog(info, resourceId, id, name, "MEMBER", false);
            return null;
        }
        member.setId(Long.valueOf(id));
        member.setCode(orgUserJoinTemp.getCode());
        List<MemberPost> second_post = new ArrayList<MemberPost>();
        member.setSecond_post(second_post);

        String name = orgUserJoinTemp.getName();

        OrgRelationTemp relationTemp =
            orgRelationTempManager.findOrgRelationTempByUserId(orgUserJoinTemp.getId(), resourceId);
        if (relationTemp == null) {
            String info = "人员:" + name + "|id:" + id + "|resourceId:" + resourceId + "关联关系不存在，导入失败!!";
            LOGGER.info(info);
            System.out.println(info);
            addLog(info, resourceId, id, name, "MEMBER", false);
            return null;
        }
        String unitId = relationTemp.getUnitId();
        String departmentId = relationTemp.getDepartmentId();

        // if (Strings.isNullOrEmpty(unitId) || Strings.isNullOrEmpty(departmentId)) {
        if (Strings.isNullOrEmpty(unitId)) {
            String info = "人员:" + name + "|id:" + id + "|resourceId:" + resourceId + "单位不存在，导入失败!!";
            LOGGER.info(info);
            System.out.println(info);
            addLog(info, resourceId, id, name, "MEMBER", false);
            return null;
        }
        OrgUnitTemp unitTemp = orgUnitTempManager.findOrgUnitTempById(unitId);
        if (unitTemp == null) {
            addLog("找不到单位:id=" + unitId, resourceId, id, name, "MEMBER", false);
            return null;
        }
        String objectId = unitTemp.getObjectId();
        if (com.seeyon.ctp.util.Strings.isEmpty(objectId)) {
            addLog("人员:" + name + ",导入失败,objectId为空:id=" + unitId, resourceId, id, name, "MEMBER", false);
            return null;
        }
        member.setOrgAccountId(Long.valueOf(objectId));
        if (!Strings.isNullOrEmpty(departmentId)) {
            OrgDepartmentTemp orgDepartmentTemp = orgDepartmentTempManager.findOrgDepartmentTempById(departmentId);
            if (orgDepartmentTemp != null) {
                String depObjId = orgDepartmentTemp.getObjectId();
                if (!Strings.isNullOrEmpty(depObjId)) {
                    member.setOrgDepartmentId(Long.valueOf(depObjId));
                }
            }

        }

        String loginName = orgUserJoinTemp.getLoginName();
        String password = orgUserJoinTemp.getPassword();
        V3xOrgPrincipal v3xOrgPrincipal = new V3xOrgPrincipal(Long.valueOf(id), loginName, password);
        member.setV3xOrgPrincipal(v3xOrgPrincipal);

        Long sort = 1l;
        Integer sortId = orgUserJoinTemp.getSortId();
        if (sortId != null && sortId != 0) {
            sort = Long.valueOf(String.valueOf(sortId));
        }
        member.setSortId(sort);
        member.setState(1);
        member.setIsInternal(true);
        member.setName(name);
        member.setCreateTime(orgUserJoinTemp.getCreateTime());
        Integer isEnable = orgUserJoinTemp.getIsEnable();
        Integer delFlag = orgUserJoinTemp.getDelFlag();
        int isAdmin = orgUserJoinTemp.getIsAdmin();
        if (1 == isAdmin) {
            member.setIsAdmin(false);
        } else {
            member.setIsAdmin(true);
        }

        if (0 == isEnable) {
            member.setEnabled(true);
        } else {
            member.setEnabled(false);
        }

        if (0 == delFlag) {
            member.setIsDeleted(false);
        } else {
            member.setIsDeleted(true);
        }

        Long orgRootId = -1730833917365171641l;

        String postId = relationTemp.getPostId();
        OrgPost orgPost = ocipOrgRoleDao.getPostByName("无", orgRootId, postId);
        if (orgPost != null) {
            member.setOrgPostId(orgPost.getId());
        }

        if (com.seeyon.ctp.util.Strings.isNotEmpty(postId)) {
            OrgPostTemp postTemp = orgPostTempManager.findOrgPostTempById(postId);
            if (postTemp != null) {
                String localPostId = postTemp.getObjectId();
                if (com.seeyon.ctp.util.Strings.isNotEmpty(localPostId)) {
                    member.setOrgPostId(Long.valueOf(localPostId));
                }
            }
        }

        String orgUserLevel = orgUserJoinTemp.getOrgUserLevel();
        OrgLevel level = ocipOrgRoleDao.getLevelByName("无", orgRootId, orgUserLevel);
        if (level != null) {
            member.setOrgLevelId(level.getId());
        }
        if (com.seeyon.ctp.util.Strings.isNotEmpty(orgUserLevel)) {
            OrgUserLevelTemp levelTemp = orgUserLevelTempDao.findOrgUserLevelTempById(orgUserLevel);
            if (levelTemp != null) {
                String localLevelId = levelTemp.getObjectId();
                if (!Strings.isNullOrEmpty(localLevelId)) {
                    member.setOrgLevelId(Long.valueOf(localLevelId));
                }
            }
        }
        return member;
    }

    public OrgUserJoinTempManager getOrgUserJoinTempManager() {
        return orgUserJoinTempManager;
    }

    public void setOrgUserJoinTempManager(OrgUserJoinTempManager orgUserJoinTempManager) {
        this.orgUserJoinTempManager = orgUserJoinTempManager;
    }

    public OrgRelationTempManager getOrgRelationTempManager() {
        return orgRelationTempManager;
    }

    public void setOrgRelationTempManager(OrgRelationTempManager orgRelationTempManager) {
        this.orgRelationTempManager = orgRelationTempManager;
    }

    public OcipOrgRoleDao getOcipOrgRoleDao() {
        return ocipOrgRoleDao;
    }

    public void setOcipOrgRoleDao(OcipOrgRoleDao ocipOrgRoleDao) {
        this.ocipOrgRoleDao = ocipOrgRoleDao;
    }

    public OrgDepartmentTempManager getOrgDepartmentTempManager() {
        return orgDepartmentTempManager;
    }

    public void setOrgDepartmentTempManager(OrgDepartmentTempManager orgDepartmentTempManager) {
        this.orgDepartmentTempManager = orgDepartmentTempManager;
    }

    public OrgUnitTempManager getOrgUnitTempManager() {
        return orgUnitTempManager;
    }

    public void setOrgUnitTempManager(OrgUnitTempManager orgUnitTempManager) {
        this.orgUnitTempManager = orgUnitTempManager;
    }

    public OrgPostTempManager getOrgPostTempManager() {
        return orgPostTempManager;
    }

    public void setOrgPostTempManager(OrgPostTempManager orgPostTempManager) {
        this.orgPostTempManager = orgPostTempManager;
    }

    public OrgUserLevelTempDao getOrgUserLevelTempDao() {
        return orgUserLevelTempDao;
    }

    public void setOrgUserLevelTempDao(OrgUserLevelTempDao orgUserLevelTempDao) {
        this.orgUserLevelTempDao = orgUserLevelTempDao;
    }
}
