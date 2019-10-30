package com.seeyon.apps.element.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.element.constants.ElementConstants;
import com.seeyon.apps.element.convert.ElementDataConverter;
import com.seeyon.apps.element.dao.ElementDao;
import com.seeyon.apps.element.enums.ElementEnums.ElementAppTypeEnum;
import com.seeyon.apps.element.enums.ElementQueryCondition;
import com.seeyon.apps.element.manager.ElementManager;
import com.seeyon.apps.element.po.GovElement;
import com.seeyon.apps.element.util.ElementRoleHelper;
import com.seeyon.apps.element.util.ElementUtil;
import com.seeyon.apps.element.vo.ElementVO;
import com.seeyon.apps.info.constants.InfoConstant;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.excel.DataRecord;
import com.seeyon.ctp.common.excel.FileToExcelManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;

/**
 * 政务元素管理类
 * 
 * @author 唐桂林
 *
 */
public class ElementManagerImpl implements ElementManager {

    private static final Log log = LogFactory.getLog(ElementManagerImpl.class);

    private ElementDao elementDao;
    private ElementCacheManager elementCacheManager;
    private OrgManager orgManager;
    private FileToExcelManager fileToExcelManager;
    private AppLogManager appLogManager;

    /**
     * 元素列表
     * 
     * @param flipInfo
     * @param paramMap
     * @return
     * @throws BusinessException
     */
    public FlipInfo getElementList(FlipInfo flipInfo, Map<String, String> paramMap) throws BusinessException {
        // 权限验证
        User user = AppContext.getCurrentUser();
        if (!ElementRoleHelper.checkByElementResourceCode(user.getId(), user.getLoginAccount(),
            InfoConstant.InfoRole_2_DataBase)) {
            flipInfo.setData(new ArrayList<ElementVO>());
            return flipInfo;
        }
        paramMap.put("userId", String.valueOf(user.getId()));
        paramMap.put("domainId", String.valueOf(user.getAccountId()));
        paramMap.put("loginAccount", String.valueOf(user.getLoginAccount()));
        /**
         * 如果是查询
         */
        String elementIds = null;
        if (Strings.isNotBlank(paramMap.get(ElementQueryCondition.condition.name()))) {
            if (Strings.isNotBlank(paramMap.get(ElementQueryCondition.name.name()))) {
                Integer appType = Strings.isBlank(paramMap.get("appType")) ? ElementAppTypeEnum.appType_default.getKey()
                    : Integer.parseInt(paramMap.get("appType"));
                List<GovElement> allElment = elementCacheManager.getAllElementNoPage(appType, user.getLoginAccount());
                elementIds = ElementDataConverter.getElementByIds(allElment, paramMap);
                paramMap.put("elementIds", elementIds);
            }
        }
        List<GovElement> result = elementDao.findList(flipInfo, paramMap);
        List<ElementVO> data = ElementDataConverter.convertToElementVO(result);
        if (flipInfo != null) {
            flipInfo.setData(data);
        }
        return flipInfo;
    }

    /**
     * 获取元素对象
     * 
     * @param id
     *            元素ID
     * @return 元素PO类
     * @throws BusinessException
     */
    public GovElement getElementById(Long id) throws BusinessException {
        return elementCacheManager.getCacheElementById(id);
    }

    /**
     * 通过单位ID得到元素集合
     * 
     * @param domainId
     * @return
     * @throws BusinessException
     */
    public GovElement getElementByElementId(String elementId, Integer appType, Long domainId) throws BusinessException {
        return elementCacheManager.getElementByElementId(elementId, appType, domainId);
    }

    /**
     * 获取元素对象
     * 
     * @param id
     *            元素ID
     * @return 元素VO类
     * @throws BusinessException
     */
    public ElementVO getElementVOById(Long id) throws BusinessException {
        GovElement element = elementCacheManager.getCacheElementById(id);
        return ElementDataConverter.convertToElementVO(element);
    }

    /**
     * 修改元素(数据库+缓存)
     * 
     * @param element
     * @return
     * @throws BusinessException
     */
    public void updateElement(GovElement element) throws BusinessException {
        elementDao.updateGovElement(element);// 跟新数据库
        elementCacheManager.updateCacheElement(element);// 跟新缓存
        /** (应用设置) 报送单元素修改 */
        User user = AppContext.getCurrentUser();
        appLogManager.insertLog(user, AppLogAction.Information_Element_Modify, user.getName(), element.getName());
    }

    /**
     * 【停用、启用】元素(数据库+缓存)
     * 
     * @param element
     * @return
     * @throws BusinessException
     */
    public void updateElementByStatu(GovElement element) throws BusinessException {
        elementDao.updateGovElement(element);// 跟新数据库
        elementCacheManager.updateCacheElement(element);// 跟新缓存
    }

    /**
     * 获取某单位的某元素对象
     * 
     * @param appType
     * @param fieldName
     *            元素名称
     * @param domainId
     *            所属单位
     * @return
     * @throws BusinessException
     */
    public GovElement getElementByFieldName(Integer appType, String fieldName, Long domainId) throws BusinessException {
        return elementCacheManager.getElementByFieldName(fieldName, appType, domainId);
    }

    /**
     *
     * @throws BusinessException
     * @throws Exception
     */
    public synchronized void initCmpElement() throws BusinessException, Exception {
        List<V3xOrgAccount> accounts = orgManager.getAllAccounts();
        List<GovElement> groupEles = elementCacheManager.getAllElementByDomainId(ElementConstants.GROUP_DOMAIN_ID);
        List<GovElement> newEles = new ArrayList<GovElement>();
        for (V3xOrgAccount account : accounts) {
            Long domainId = account.getId();
            List<GovElement> allElements = elementCacheManager.getAllElementByDomainId(domainId);
            if (allElements != null && allElements.size() >= groupEles.size()) {
                continue;
            } else {
                int size = allElements == null ? 0 : allElements.size();
                if (size > 0) {
                    elementDao.deleteElementsByDomainId(domainId);
                }
                log.info("初始化报送单元素页面。单位ID：" + domainId + " 当前元素数目：" + size + " 集团元素数目：" + groupEles.size());
            }
            for (GovElement element : groupEles) {
                GovElement tempElement = element.clone(domainId);
                newEles.add(tempElement);
            }
        }
        DBAgent.saveAllForceFlush(newEles);

        elementCacheManager.initialize();
    }

    /**
     *
     * @param appType
     * @return
     * @throws BusinessException
     */
    public String checkElementCount(Integer appType) throws BusinessException {
        List<GovElement> elementList = elementCacheManager.getCacheElementList(appType, GovElement.C_iStatus_Active);
        String msg = "";
        if (elementList.size() == 0) {
            msg = ResourceUtil.getString("edoc.no.edit.edoc.elements");
        } else {
            msg = "have";
        }
        return msg;
    }

    /**
     * 获取元素集合
     * 
     * @param appType
     * @param status
     *            状态
     * @param start
     * @param end
     * @return
     * @throws BusinessException
     */
    public List<GovElement> getElementList(Integer appType, Integer status) throws BusinessException {
        return elementCacheManager.getCacheElementList(appType, status);
    }

    /**
     * 获取当前单位的元素
     * 
     * @param appType
     * @param domainId
     * @param hasOpinionElement
     * @return
     * @throws BusinessException
     */
    public List<GovElement> getElementList(Integer appType, Long domainId, Boolean hasOpinionElement)
        throws BusinessException {
        List<GovElement> elementList = new ArrayList<GovElement>();
        List<GovElement> list = elementCacheManager.getAllElementByDomainId(appType, domainId);
        if (hasOpinionElement) {
            return list;
        } else {
            for (GovElement element : list) {
                if (element.getStatus() == GovElement.C_iStatus_Active && element.getType() != 6) {
                    elementList.add(element);
                }
            }
        }
        return elementList;
    }

    /**
     * 新建单位复制公文元素
     * 
     * @param accountId
     */
    public void generateElementByAccountId(Long accountId) throws BusinessException {

        log.info("开始为新建单位复制系统信息报送元素...");
        /*try{
            List<GovElement> newEles = new ArrayList<GovElement>();
            List<GovElement> groupEles = elementCacheManager.getAllElementByDomainId(ElementConstants.GROUP_DOMAIN_ID);
            for(GovElement element : groupEles) {
                GovElement tempElement = element.clone(accountId);
                newEles.add(tempElement);
            }
            DBAgent.saveAllForceFlush(newEles);
            elementCacheManager.addCacheElement(newEles);
        }
        catch(Exception e){
        	log.error("新建单位的时候复制系统信息报送元素异常", e);
        }*/

        log.info("复制系统信息报送元素结束。");
    }

    /**
     * 导出Excel
     * 
     * @param request
     * @param response
     * @param condition
     * @param name
     * @param status
     * @param fieldName
     * @return
     */
    public List<GovElement> exportElementExcel(Map<String, String> paramMap, HttpServletResponse response) {
        Integer appType = Strings.isBlank(paramMap.get("appType")) ? ElementAppTypeEnum.appType_default.getKey()
            : Integer.parseInt(paramMap.get("appType"));
        User user = AppContext.getCurrentUser();
        paramMap.put("userId", String.valueOf(user.getId()));
        paramMap.put("domainId", String.valueOf(user.getAccountId()));
        paramMap.put("loginAccount", String.valueOf(user.getLoginAccount()));
        paramMap.put("appType", String.valueOf(appType));
        try {
            String elementIds = null;
            if (Strings.isNotBlank(paramMap.get(ElementQueryCondition.condition.name()))) {
                if (Strings.isNotBlank(paramMap.get(ElementQueryCondition.name.name()))) {
                    List<GovElement> allElment =
                        elementCacheManager.getAllElementNoPage(appType, user.getLoginAccount());
                    elementIds = ElementDataConverter.getElementByIds(allElment, paramMap);
                    paramMap.put("elementIds", elementIds);
                }
            }
            List<GovElement> elementList = elementDao.findList(null, paramMap);
            DataRecord dataRecord = ElementUtil.exportEdocElement(elementList);
            try {
                fileToExcelManager.save(response, ResourceUtil.getString("element.code.reflection"), dataRecord);
            } catch (Exception e) {
                log.error("导出信息元素失败", e);
            }
            /** (应用设置) 报送单元素导出Excel */
            appLogManager.insertLog(user, AppLogAction.Information_Element_Excel, user.getName(),
                dataRecord.getTitle());
            return elementList;
        } catch (BusinessException e) {
            log.error("元素导出excel错误", e);
        }
        return null;
    }

    /**
     * 修改元素状态 启用/停用
     * 
     * @param sIds
     * @param status
     */
    public void updateElementStatus(Map<String, String> params) {
        String elementIds = (String)params.get("ids");
        int state = Integer.valueOf((String)params.get("state"));
        String[] sIds = elementIds.split(",");
        if (sIds != null && sIds.length > 0) {
            try {
                User user = AppContext.getCurrentUser();
                List<Long> elementIdList = new ArrayList<Long>();
                for (int i = 0; i < sIds.length; i++) {
                    elementIdList.add(Long.valueOf(sIds[i]));
                    GovElement element = this.getElementById(Long.valueOf(sIds[i]));
                    element.setStatus(state);
                    this.updateElementByStatu(element);

                    if (state == GovElement.C_iStatus_Active) {
                        /** (应用设置) 报送单元素启用 */
                        appLogManager.insertLog(user, AppLogAction.Information_Element_Enable, user.getName(),
                            element.getName());
                    } else {
                        /** (应用设置) 报送单元素停用 */
                        appLogManager.insertLog(user, AppLogAction.Information_Element_Disabled, user.getName(),
                            element.getName());
                    }
                }
            } catch (NumberFormatException e) {
                log.error("获取修改元素状态失败", e);
            } catch (BusinessException e) {
                log.error("执行修改元素状态失败");
            }
        }
    }

    public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }

    public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
        this.fileToExcelManager = fileToExcelManager;
    }

    public void setElementDao(ElementDao elementDao) {
        this.elementDao = elementDao;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setElementCacheManager(ElementCacheManager elementCacheManager) {
        this.elementCacheManager = elementCacheManager;
    }

}
