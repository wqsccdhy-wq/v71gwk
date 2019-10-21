package com.seeyon.apps.businessorganization.manager;

import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;

public interface BusinessAccountManager {

    /**
     * 获取业务线单位的json数据
     * @param createrId
     * @return
     * @throws BusinessException 
     */
	public String getBusinessAccountJson(Long createrId) throws BusinessException;

	/**
	 * 创建业务线
	 * @param map
	 * @return
	 * @throws BusinessException
	 */
	public Map createBusiness(Map map) throws BusinessException;

	/**
	 * 更新业务线 
	 * @param map
	 * @return
	 * @throws BusinessException
	 */
	public Map updateBusiness(Map map) throws BusinessException;

	/**
	 * 删除业务线
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	public Map deleteBusiness(Long accountId) throws BusinessException;

	/**
	 * 获取最大的排序号
	 * @param createrId
	 * @return
	 * @throws BusinessException
	 */
	public Integer getMaxSortByCreaterId(Long createrId) throws BusinessException;

	/**
	 * 查看详情
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	public Map viewBusiness(Long accountId) throws BusinessException;

}
