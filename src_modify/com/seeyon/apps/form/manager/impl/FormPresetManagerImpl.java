package com.seeyon.apps.form.manager.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.element.manager.ElementManager;
import com.seeyon.apps.element.po.GovElement;
import com.seeyon.apps.form.bo.FormBO;
import com.seeyon.apps.form.bo.FormBO.FormStatusEnum;
import com.seeyon.apps.form.constants.FormConstant;
import com.seeyon.apps.form.dao.FormDao;
import com.seeyon.apps.form.manager.FormElementManager;
import com.seeyon.apps.form.manager.FormExtendInfoManager;
import com.seeyon.apps.form.manager.FormManager;
import com.seeyon.apps.form.manager.FormPermissionBoundManager;
import com.seeyon.apps.form.manager.FormPresetManager;
import com.seeyon.apps.form.po.GovForm;
import com.seeyon.apps.form.po.GovFormAcl;
import com.seeyon.apps.form.po.GovFormElement;
import com.seeyon.apps.form.po.GovFormExtendInfo;
import com.seeyon.apps.form.po.GovFormFlowPermBound;
import com.seeyon.apps.form.service.FormCacheManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.form.util.StringUtils;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.Strings;

public class FormPresetManagerImpl implements FormPresetManager {

    private static final Log log = LogFactory.getLog(FormPresetManagerImpl.class);

    private FormDao govformDao;
    private OrgManager orgManager;
    private FormManager govformManager;
    private FormElementManager govformElementManager;
    private FormPermissionBoundManager govformPermissionBoundManager;
    private FormExtendInfoManager govformExtendInfoManager;
    private ElementManager elementManager;
    private FormCacheManager govformCacheManager;

    /**
     * 预置信息报送单
     * 
     * @param domainId
     * @throws BusinessException
     */
    public void generateInfoFormByAccountId(Long domainId) throws BusinessException {

        /** 单组织升级到多组织 */
        V3xOrgAccount account = orgManager.getRootAccount();
        List<GovForm> forms = govformDao.getAclFormListByDomainId(account.getId());

        List<GovFormExtendInfo> formExtendInfos = new ArrayList<GovFormExtendInfo>();
        for (GovForm form : forms) {

            if (form.getIsSystem()) {
                continue;
            }
            GovFormExtendInfo extendInfo = new GovFormExtendInfo();
            extendInfo.setIdIfNew();
            extendInfo.setDomainId(domainId);
            extendInfo.setStatus(FormStatusEnum.enabel.ordinal());
            extendInfo.setIsDefault(Boolean.FALSE);
            extendInfo.setFormId(form.getId());
            extendInfo.setOptionFormatSet(FormConstant.FORM_DEFUALT_FORMAT_SET);
            formExtendInfos.add(extendInfo);
        }

        govformExtendInfoManager.saveGovFormExtendInfo(formExtendInfos);

        List<GovForm> formList = updateFormContentToDBOnly();

        if (Strings.isNotEmpty(formList)) {

            log.info("预置报送单长度" + formList.size());
            User user = AppContext.getCurrentUser();

            long l = System.currentTimeMillis();

            List<FormBO> formBos = new ArrayList<FormBO>();

            for (GovForm oldForm : formList) {

                List<GovFormAcl> addFormAclList = new ArrayList<GovFormAcl>();
                List<GovFormElement> addFormElementList = new ArrayList<GovFormElement>();
                List<GovFormExtendInfo> addFormExtendInfoList = new ArrayList<GovFormExtendInfo>();
                List<GovFormFlowPermBound> addFormPermissionBoundList = new ArrayList<GovFormFlowPermBound>();

                GovForm formPO = new GovForm();
                formPO.setIdIfNew();
                formPO.setCategoryId(0L);
                formPO.setAppType(oldForm.getAppType());
                formPO.setType(oldForm.getType());
                formPO.setFileId(oldForm.getFileId());
                formPO.setContent(oldForm.getContent());
                formPO.setDescription(oldForm.getDescription());
                formPO.setName(oldForm.getName());
                formPO.setDomainId(domainId);
                formPO.setCreateTime(new Timestamp(l));
                formPO.setCreateUserId(user.getId());
                formPO.setLastUpdate(new Timestamp(l));
                formPO.setIsSystem(true);
                formPO.setShowLog(false);

                GovFormExtendInfo formExtendInfo = new GovFormExtendInfo();
                formExtendInfo.setIdIfNew();
                formExtendInfo.setFormId(formPO.getId());
                formExtendInfo.setDomainId(formPO.getDomainId());
                formExtendInfo.setStatus(FormStatusEnum.enabel.ordinal());
                formExtendInfo.setIsDefault(Boolean.TRUE);
                formExtendInfo.setOptionFormatSet(FormConstant.FORM_DEFUALT_FORMAT_SET);
                addFormExtendInfoList.add(formExtendInfo);

                // 复制授权信息
                GovFormAcl formAcl = new GovFormAcl();
                formAcl.setIdIfNew();
                formAcl.setFormId(formPO.getId());
                formAcl.setDomainId(formPO.getDomainId());
                formAcl.setEntityType(V3xOrgEntity.ORGENT_TYPE_ACCOUNT);// Account
                addFormAclList.add(formAcl);

                List<GovFormElement> formElementList = govformElementManager.findFormElementList(oldForm.getId());
                for (GovFormElement oldElement : formElementList) {
                    GovElement element = elementManager.getElementByElementId(String.valueOf(oldElement.getElementId()),
                        formPO.getAppType(), domainId);
                    // element = elementManager.getElementByFieldName(formPO.getAppType(), element.getFieldName(),
                    // domainId);
                    GovFormElement newElement = new GovFormElement();
                    newElement.setIdIfNew();
                    newElement.setFormId(formPO.getId());
                    newElement.setElementId(element.getId());
                    newElement.setRequired(oldElement.isRequired());
                    addFormElementList.add(newElement);
                }

                List<GovFormFlowPermBound> formPermissionBoundList =
                    govformPermissionBoundManager.findFormPermissionBoundListByFormId(oldForm.getId());
                for (GovFormFlowPermBound oldBound : formPermissionBoundList) {
                    GovFormFlowPermBound newBound = new GovFormFlowPermBound();
                    newBound.setIdIfNew();
                    newBound.setFormId(formPO.getId());
                    newBound.setProcessName(oldBound.getProcessName());
                    newBound.setFlowPermName(oldBound.getFlowPermName());
                    newBound.setFlowPermNameLabel(oldBound.getFlowPermNameLabel());
                    newBound.setSortType(oldBound.getSortType());
                    newBound.setDomainId(domainId);
                    addFormPermissionBoundList.add(newBound);
                }

                FormBO formBO = new FormBO();
                formBO.setId(formPO.getId());
                formBO.setAppType(formPO.getAppType());
                formBO.setType(formPO.getType());
                formBO.setDomainId(formPO.getDomainId());
                formBO.setFormPO(formPO);
                formBO.setFormAclList(addFormAclList);
                formBO.setGovFormElementList(addFormElementList);
                formBO.setFormExtendInfoList(addFormExtendInfoList);
                formBO.setFormPermissioinBoundList(addFormPermissionBoundList);
                formBos.add(formBO);
            }
            govformManager.saveAllForm(formBos);

            /** 新建单位后，初始化信息报送单 */
            govformCacheManager.batchAddForm2Cache(formBos);
        }
    }

    /**
     * 
     * @throws BusinessException
     */
    public List<GovForm> updateFormContentToDBOnly() throws BusinessException {
        try {
            log.info("预置报送单初始化数据......");
            /*String baseFileFold = SystemProperties.getInstance().getProperty("govform.folder");
            List<GovForm> formList = govformDao.getFormListByDomainId(FormEnums.FormAppTypeEnum.appType_info.getKey(), FormConstant.GROUP_DOMAIN_ID);
            
            List<GovForm> toUpdates = new ArrayList<GovForm>();
            for(GovForm formPO : formList) {
            	if(Strings.isBlank(formPO.getContent())) {
            	    
            		log.info("初始化报送单......");
            		//String infoXsl = StringUtils.readFileToString(baseFileFold + File.separator +"info.xml");
            		byte[] byteArray = StringUtils.readFileData(baseFileFold + File.separator +"info.xsl");
            		String infoXsl = new String(byteArray, "UTF-8");
            		formPO.setContent(infoXsl);
            		toUpdates.add(formPO);
            	}
            }
            if(Strings.isNotEmpty(toUpdates)){
                govformDao.updateAllGovForm(toUpdates);
            }
            
            return formList;*/
            return null;
        } catch (Exception e) {
            log.error("报送单初始化失败!", e);
        }
        log.info("报送单初始化完毕!");
        return null;
    }

    /**
     * 
     * @throws BusinessException
     */
    @SuppressWarnings("deprecation")
    public GovForm copyFormContentToDBOnly(GovForm formPO) throws BusinessException {
        try {
            log.info("预置报送单初始化数据......");
            String baseFileFold = SystemProperties.getInstance().getProperty("govform.folder");
            if (Strings.isBlank(formPO.getContent())) {
                log.info("初始化报送单......");
                byte[] byteArray = StringUtils.readFileData(baseFileFold + File.separator + "info.xsl");
                String infoXsl = org.apache.commons.io.IOUtils.toString(byteArray, "UTF-8");
                formPO.setContent(infoXsl);
                govformDao.updateGovForm(formPO);
            }
            return formPO;
        } catch (Exception e) {
            log.error("报送单初始化失败!", e);
        }
        log.info("报送单初始化完毕!");
        return null;
    }

    public void setGovformManager(FormManager govformManager) {
        this.govformManager = govformManager;
    }

    public void setGovformDao(FormDao govformDao) {
        this.govformDao = govformDao;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setGovformPermissionBoundManager(FormPermissionBoundManager govformPermissionBoundManager) {
        this.govformPermissionBoundManager = govformPermissionBoundManager;
    }

    public void setGovformElementManager(FormElementManager govformElementManager) {
        this.govformElementManager = govformElementManager;
    }

    public void setElementManager(ElementManager elementManager) {
        this.elementManager = elementManager;
    }

    public void setGovformExtendInfoManager(FormExtendInfoManager govformExtendInfoManager) {
        this.govformExtendInfoManager = govformExtendInfoManager;
    }

    public void setGovformCacheManager(FormCacheManager govformCacheManager) {
        this.govformCacheManager = govformCacheManager;
    }

}
