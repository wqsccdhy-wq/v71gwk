package com.seeyon.ctp.organization.manager;

import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.FlipInfo;

public interface VisitorManager {
	
	/**
	 * 查询访客列表
	 * @param flipInfo
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public FlipInfo visitorInfo(FlipInfo flipInfo, Map params) throws BusinessException;
	
	/**
	 * 
	 * @param flipInfo
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	public FlipInfo updateVisitorInfo(FlipInfo flipInfo, Map params) throws BusinessException;
	
}
