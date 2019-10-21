package com.seeyon.ctp.organization.manager;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.po.JoinAccount;
import com.seeyon.ctp.util.FlipInfo;

/**
 * 外单位扩展属性
 * @author wf
 *
 */
public interface JoinAccountCustomerFieldInfoManager {

    /**
     * 外单位属性设置列表
     * @param fi
     * @param params
     * @return
     * @throws BusinessException
     */
    FlipInfo getAll(final FlipInfo fi, final Map params) throws BusinessException;

	/**
	 * 添加一条设置信息
	 * @param map
	 * @throws BusinessException
	 */
	void addJoinAccount(JoinAccount joinAccount) throws BusinessException;
	
	/**
	 * 
	 * 修改一条设置信息
	 * @param map
	 * @throws BusinessException
	 */
	void updateJoinAccount(JoinAccount joinAccount) throws BusinessException;

	/**
	 * 批量导入
	 */
	void addJoinAccounts(List<JoinAccount> joinAccounts);
	
	/**
	 * 根据外单位id查找
	 * @param departmentId
	 * @return
	 * @throws BusinessException
	 */
	JoinAccount getByDepartmentId(Long departmentId) throws BusinessException;

	/**
	 * 清空已经被删除的字段的信息
	 * @param disabledLabels
	 * @throws BusinessException
	 */
	void updateJoinAccountEmpty(String[] disabledLabels, Long orgAccountId) throws BusinessException;

	/**
	 * 获取对应的get方法
	 * @param fieldName
	 * @return
	 */
	Method getGetMethod(String fieldName);

	/**
	 * 获取自定义外单位属性对应字段的set方法
	 * @param fieldName
	 * @return
	 */
	Method getSetMethod(String fieldName);

	/**
	 * 取单位下的所有自定义属性值
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	List<JoinAccount> getAllJoinAccount(Long accountId)throws BusinessException;

}