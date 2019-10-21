package com.seeyon.ctp.ociporg.manager;

import com.seeyon.ctp.util.FlipInfo;

public interface OcipOrgManager<T> {

	/**
	 * 导入组织机构
	 * 
	 * @param list
	 */
	public void importOrg(String resourceId, FlipInfo flipInfo);

	/**
	 * 保存日志
	 * 
	 * @param msg
	 * @param resourceId
	 * @param id
	 * @param name
	 * @param type
	 */
	public void addLog(String msg, String resourceId, String id, String name, String type, Boolean success);
}
