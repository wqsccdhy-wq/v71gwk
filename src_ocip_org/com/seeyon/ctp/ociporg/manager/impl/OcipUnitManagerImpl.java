package com.seeyon.ctp.ociporg.manager.impl;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Strings;
import com.seeyon.ctp.ociporg.manager.OcipResourceTempManager;
import com.seeyon.ctp.ociporg.manager.AbsOcipOrgManager;
import com.seeyon.ctp.ociporg.manager.OrgDepartmentTempManager;
import com.seeyon.ctp.ociporg.manager.OrgUnitTempManager;
import com.seeyon.ctp.ociporg.manager.OrgUserJoinTempManager;
import com.seeyon.ctp.ociporg.po.OcipResourceTemp;
import com.seeyon.ctp.ociporg.po.OrgUnitTemp;
import com.seeyon.ctp.ociporg.po.OrgUserJoinTemp;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.OrganizationMessage.MessageStatus;
import com.seeyon.ctp.organization.bo.OrganizationMessage.OrgMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.UUIDLong;

public class OcipUnitManagerImpl extends AbsOcipOrgManager<OrgUnitTemp> {

    private static final Logger LOGGER = Logger.getLogger(OcipUnitManagerImpl.class);

    private OrgUserJoinTempManager orgUserJoinTempManager;

    private OrgDepartmentTempManager orgDepartmentTempManager;

    private OrgUnitTempManager orgUnitTempManager;

    private OcipResourceTempManager ocipResourceTempManager;

    @Override
    public void importOrg(String resourceId, FlipInfo flipInfo) {
        Short isFlag = new Short("0");
        List<Short> gradeList = orgUnitTempManager.findOrgUnitTempByGrade(resourceId);
        if (CollectionUtils.isEmpty(gradeList)) {
            return;
        }
        Collections.sort(gradeList);

        /**
         * TODO 测试
         */
        /*OrgUnitTemp orgUnit = orgUnitTempManager.findOrgUnitTempById("-8984204239303063425");
        importEntry(orgUnit , resourceId);*/

        for (Short grade : gradeList) {
            Long allCount = orgUnitTempManager.getCount(grade, isFlag, resourceId);
            if (allCount < 1) {
                String info = "resourceId:" + resourceId + "|没有需要导入的单位：grade：" + grade + "|isFlag:" + isFlag;
                LOGGER.info(info);
                System.out.println(info);
                continue;
            }

            String info = "resourceId:" + resourceId + "|grade为:" + grade + " 的单位一共有:" + allCount + "条数据需要导入!!!";
            LOGGER.info(info);
            System.out.println(info);
            Long count = (allCount / 20l) + 1;
            for (long i = 0; i < count; i++) {
                List<OrgUnitTemp> lists =
                    orgUnitTempManager.findOrgUnitTempByGrade(grade, isFlag, resourceId, flipInfo);
                if (!CollectionUtils.isEmpty(lists)) {
                    for (OrgUnitTemp orgUnitTemp : lists) {
                        importEntry(orgUnitTemp, resourceId);
                    }
                }
            }
        }

    }

    @Override
    public void importEntry(OrgUnitTemp orgUnitTemp, String resourceId) {
        // String parentId = orgUnitTemp.getParentId();
        // String adminId = orgUnitTemp.getAdminId();
        String id = orgUnitTemp.getId();
        String name = orgUnitTemp.getName();
        /*if (Strings.isNullOrEmpty(adminId)) {
        	if (!"0".equals(parentId)) {
        		String info = "单位:" + name + "|id:" + id + "|没有单位管理员";
        		LOGGER.info(info);
        		System.out.println(info);
        		addLog(info, resourceId, id, name, "UNIT");
        		return;
        	}
        }*/
        long beginTime = System.currentTimeMillis();

        V3xOrgAccount newAccount = initAccount(orgUnitTemp, resourceId);
        if (newAccount == null){
            //orgUnitTempManager.updatOrgUnitTemp(orgUnitTemp);
            try {
                orgUnitTemp.setIsFlag(new Short("2"));
                orgUnitTempManager.updatOrgUnitTemp(orgUnitTemp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        V3xOrgMember admin = initAdminMember(orgUnitTemp);
        MessageStatus messageStatus = null;
        boolean success = false;
        String messageInfo = "";
        try {
            if (newAccount != null && admin != null) {
                OrganizationMessage message = orgManagerDirect.addAccount(newAccount, admin);
                success = message.isSuccess();
                Short result = new Short("1");

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
                orgUnitTemp.setIsFlag(result);
                long endTime = System.currentTimeMillis();
                long time = (endTime - beginTime) / 1000;
                String info = "生成单位结果,单位:" + name + "|id:" + id + "|result:" + result + "|messageStatus:"
                    + messageStatus + "|耗时:" + time + "秒";
                LOGGER.info(info);
                System.out.println(info);
            }
        } catch (Exception e) {
            messageInfo = e.getMessage();
            success = false;
            orgUnitTemp.setIsFlag(new Short("3"));
            LOGGER.error("生成单位异常1,单位:" + name + "|id:" + id, e);
        } finally {
            try {
                orgUnitTempManager.updatOrgUnitTemp(orgUnitTemp);
                if (messageStatus != null) {
                    addLog(messageStatus.toString(), resourceId, id, name, "UNIT", success);
                } else {
                    addLog(messageInfo, resourceId, id, name, "UNIT", success);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private V3xOrgMember initAdminMember(OrgUnitTemp orgUnitTemp) {
        String parentId = orgUnitTemp.getParentId();
        String adminId = orgUnitTemp.getAdminId();
        String id = orgUnitTemp.getId();
        String name = orgUnitTemp.getName();
        V3xOrgMember admin = new V3xOrgMember();
        admin.setType(OrgConstants.MEMBER_TYPE.FORMAL.ordinal());
        if ("0".equals(parentId)) {
            admin.setId(UUIDLong.longUUID());
            admin.setV3xOrgPrincipal(new V3xOrgPrincipal(admin.getId(), "admin_" + admin.getId(), "123456"));
        } else if (Strings.isNullOrEmpty(adminId) && !"0".equals(parentId)) {
            admin.setId(UUIDLong.longUUID());
            admin.setV3xOrgPrincipal(new V3xOrgPrincipal(admin.getId(), "admin_" + admin.getId(), "123456"));
            String info = "单位:" + name + "|id:" + id + "|没有单位管理员，自动生成单位管理员,账号:";
            addLog(info, orgUnitTemp.getResourceId(), orgUnitTemp.getId(), name, "UNIT", false);
        } else {
            OrgUserJoinTemp orgUserJoinTemp = orgUserJoinTempManager.findOrgUserJoinTempById(adminId);
            if (orgUserJoinTemp == null) {
                admin.setId(UUIDLong.longUUID());
                admin.setV3xOrgPrincipal(new V3xOrgPrincipal(admin.getId(), "admin_" + admin.getId(), "123456"));
                String info = "单位:" + name + "|id:" + id + "|adminId:" + adminId + "|单位管理员查不到";
                addLog(info, orgUnitTemp.getResourceId(), orgUnitTemp.getId(), name, "UNIT", false);
                return admin;
            }
            String password = orgUserJoinTemp.getPassword();
            admin.setId(Long.valueOf(orgUserJoinTemp.getId()));
            admin.setV3xOrgPrincipal(new V3xOrgPrincipal(admin.getId(), orgUserJoinTemp.getLoginName(), password));
        }

        return admin;

    }

    private V3xOrgAccount initAccount(OrgUnitTemp orgUnitTemp, String resourceId) {
        String parentId = orgUnitTemp.getParentId();
        V3xOrgAccount newAccount = new V3xOrgAccount();
        newAccount.setId(Long.valueOf(orgUnitTemp.getObjectId()));// orgUnitTemp.getObjectId()
        newAccount.setOrgAccountId(newAccount.getId());
        String name = orgUnitTemp.getName();
        String aliasName = orgUnitTemp.getAliasName();
        if (com.seeyon.ctp.util.Strings.isNotBlank(aliasName)) {
            newAccount.setName(aliasName);
        } else {
            newAccount.setName(name);
        }
        newAccount.setName(name);

        String code = orgUnitTemp.getCode();
        // newAccount.setCode(orgUnitTemp.getId());
        if (Strings.isNullOrEmpty(code)) {
            newAccount.setCode(orgUnitTemp.getId());
        } else {
            newAccount.setCode(code);
        }

        newAccount.setSortIdType(OrgConstants.SORTID_TYPE_REPEAT);
        Integer isEnable = orgUnitTemp.getIsEnable();
        Integer delFlag = orgUnitTemp.getDelFlag();
        if (isEnable == 0) {
            newAccount.setEnabled(true);
        } else {
            newAccount.setEnabled(false);
        }

        if (delFlag == 0) {
            newAccount.setIsDeleted(false);
        } else {
            newAccount.setIsDeleted(true);

        }

        Integer sortId = orgUnitTemp.getSortId();
        if (sortId != null) {
            if (sortId == 0){
                sortId = 1;
            }
            newAccount.setSortId(Long.valueOf(String.valueOf(sortId)));
        }

        Long superior = -1730833917365171641l;
        if (!Strings.isNullOrEmpty(parentId)) {

            if (!"-1955537707795132666".equals(parentId)) {
                OrgUnitTemp unitTemp = orgUnitTempManager.findOrgUnitTempById(parentId);
                if (unitTemp != null) {
                    String parentId2 = unitTemp.getParentId();
                    if ("0".equals(parentId2)) {
                        superior = Long.valueOf(parentId);
                    } else {
                        String objectId = unitTemp.getObjectId();
                        if (com.seeyon.ctp.util.Strings.isNotEmpty(objectId)) {
                            superior = Long.valueOf(objectId);
                        }
                    }
                }else{
                    Short grade = orgUnitTemp.getGrade();
                    if (grade > 1){
                        addLog("上级单位找不到", resourceId, orgUnitTemp.getId(), name, "UNIT", false);
                        return null;
                    }

                }

            }

            if ("0".equals(parentId)) {
                superior = -1730833917365171641l;
                newAccount.setId(Long.valueOf(orgUnitTemp.getId()));
                newAccount.setOrgAccountId(Long.valueOf(orgUnitTemp.getId()));
            }

        }
        newAccount.setSuperior(superior);
        String shortName = orgUnitTemp.getShortName();
        if (Strings.isNullOrEmpty(shortName)) {
            newAccount.setShortName(name);
        } else {
            newAccount.setShortName(shortName);
        }

        newAccount.setResourceId(orgUnitTemp.getResourceId());
        // 根据系统id查询conValue
        List<OcipResourceTemp> resourceTemps = ocipResourceTempManager.findOcipResourceTempById(resourceId);
        for (OcipResourceTemp ocipResourceTemp : resourceTemps) {
            newAccount.setConValue(ocipResourceTemp.getconValue());
        }
        return newAccount;
    }

    public OrgUserJoinTempManager getOrgUserJoinTempManager() {
        return orgUserJoinTempManager;
    }

    public void setOrgUserJoinTempManager(OrgUserJoinTempManager orgUserJoinTempManager) {
        this.orgUserJoinTempManager = orgUserJoinTempManager;
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

    public void setOcipResourceTempManager(OcipResourceTempManager ocipResourceTempManager) {
        this.ocipResourceTempManager = ocipResourceTempManager;
    }
}
