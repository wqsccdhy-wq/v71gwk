package com.seeyon.ctp.organization.manager;

import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;

/**
 * 用于选人快速录入，检索数据和记录常用联系人等业务Manager<br>
 * 基本用于Ajax调用
 * @author lilong
 */
public interface OrgIndexManager {

    /**
     * 展现当前人员可以访问的人员JSON数据
     * @return 经过过滤后的数据
     */
    String getOrgIndexDatas() throws BusinessException;
    
    /**
     * 保存选人界面最近联系人数据，最近50人
     * @param memberId
     * @param orgDataStr
     * @throws BusinessException
     */
    void saveCustomOrgRecent(Long memberId, String orgDataStr) throws BusinessException;

    /**
     * 获取选人界面，某人最近联系人数据，可以获取快速录入的50人
     * @param memberId
     * @param customType
     * @return
     * @throws BusinessException
     */
    List<V3xOrgEntity> getRecentData(Long memberId, String customType) throws BusinessException;
    /**
     * 获取最近联系 的 10人
     * @param memberId
     * @param customType
     * @return
     * @throws BusinessException
     */
    String getFastRecentDataMember(Long memberId, String customType) throws BusinessException;
    
    /**
     * 获取最近联系人JSON字符串<br>
     * 不用过滤权限，都是从选人界面保存过来的数据
     * @param memberId
     * @param customType
     * @return
     * @throws BusinessException
     */
    String getRecentDataStr(Long memberId, String customType) throws BusinessException;
    
    /**
     * 检查复制粘贴过来人员名称内容
     * @param beforeCheckStr
     * @return json串，其中标志位标识是否正确匹配
     * @throws BusinessException
     */
    String checkFromCopy(String beforeCheckStr) throws BusinessException;

    /**
     * 获取选人界面，某人最近联系人数据，可以获取快速录入的25人
     * @param memberId
     * @param customType
     * @param isCheckLevelScope
     * @return
     * @throws BusinessException
     */
    List<V3xOrgEntity> getRecentData(Long memberId, String string, boolean b) throws BusinessException;
	
    /**
	 * 快速查询
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	String getSearchDataStr(String key) throws BusinessException;
	
	/**
	 * 快速选人快速查询
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	String getFastSearchDataStr(String key) throws BusinessException;
	
	String getAllMembersWithDisable(Long accountId)throws BusinessException;
	String getAllDepartments(Long accountId,String key)throws BusinessException;
	String getAllTeams(Long accountId,String key)throws BusinessException;
	String getAllPosts(Long accountId,String key)throws BusinessException;
	String getAllLevels(Long accountId,String key)throws BusinessException;
	String getAllAccounts(String key)throws BusinessException;
}
