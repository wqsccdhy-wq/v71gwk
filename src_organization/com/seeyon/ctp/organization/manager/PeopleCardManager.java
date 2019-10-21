/**
 * $Author: $
 * $Rev: $
 * $Date:: 2012-06-05 15:14:56#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */

package com.seeyon.ctp.organization.manager;

import java.util.HashMap;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;


public interface PeopleCardManager {
	public HashMap showPeoPleCard(Long memberId) throws BusinessException;
	public HashMap showPeoPleCardMini(Long memberId) throws BusinessException;
	/**
	 * 获取人员卡片信息，
	 * @param mId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getPeopleCardInfo(Long mId) throws Exception;
}