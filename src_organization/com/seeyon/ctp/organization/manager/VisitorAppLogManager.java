package com.seeyon.ctp.organization.manager;

import java.util.Map;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.annotation.AjaxAccess;

public interface VisitorAppLogManager {
	
	/**
	 * 访客日志
	 * @param flipInfo
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@AjaxAccess
	public FlipInfo visitorAppLog(FlipInfo flipInfo, Map params) throws Exception;
}
