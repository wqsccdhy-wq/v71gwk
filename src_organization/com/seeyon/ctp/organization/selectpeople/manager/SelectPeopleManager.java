package com.seeyon.ctp.organization.selectpeople.manager;

import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.annotation.AjaxAccess;

public interface SelectPeopleManager {

    /**
     * 加载组织模型，不加载单位信息，此方法为AJAX服务
     * 
     * @param timestamp 时间戳，如：123123423523
     * @param loginAccountId
     * @param memberId
     * @param extParameters
     * @return
     * @throws BusinessException
     */
    String getOrgModel(String timestamp, long loginAccountId, long memberId, String extParameters) throws BusinessException;

    /**
     * 选人界面在全集团范围内查询部门、人员
     * @param name 关键字
     * @throws BusinessException
     */
    String getQueryOrgModel(String name) throws BusinessException;
    
    /**
     * 选人界面在全集团范围内查询部门、人员
     * <br>add by lilong 2012-11-30 V320SP1客户BUG，选人界面如果配置不检查职务级别，则不检查职务级别
     * @param name 关键字
     * @param isNeedCheckLevelScope 是否限制职务级别
     * @throws BusinessException
     * 
     */
    String getQueryOrgModel(String name, Boolean isNeedCheckLevelScope) throws BusinessException;
    
    Map<String, Object> saveAsTeam(String name, String memberIds) throws BusinessException;
    
    String parseElements(String originalDataValue);

    /**
     * 选人界面在全集团或单位查询部门，人员
     * <br>add by lilong 2012-11-30 V320SP1客户BUG，选人界面如果配置不检查职务级别，则不检查职务级别
     * @param name 关键字
     * @param isNeedCheckLevelScope 是否限制职务级别
     * @param accountId 单位id，不传为查全集团
     * @throws BusinessException
     * 
     */
    String getQueryOrgModel(String name, Boolean isNeedCheckLevelScope, Long accountId) throws BusinessException;

    String getQueryOrgModel(String name, Long accountId) throws BusinessException;
    
    public SelectPeoplePanel getPanel(String panel);

    /**
     * 选人界面搜索接口（主要用于在ie，edge浏览器下 选中根节点的情况下进行搜索）
     * @param name
     * @param isNeedCheckLevelScope
     * @param accountId
     * @param type
     * @return
     * @throws BusinessException
     */
    @AjaxAccess
	String getQueryOrgModelByType(String name, Boolean isNeedCheckLevelScope, Long accountId, String type) throws BusinessException;

}