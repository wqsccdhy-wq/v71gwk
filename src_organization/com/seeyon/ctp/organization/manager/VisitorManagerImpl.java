package com.seeyon.ctp.organization.manager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.V3xOrgVisitor;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.po.OrgVisitor;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator})
@ProcessInDataSource(name = DataSourceName.BASE)
public class VisitorManagerImpl implements VisitorManager{
	private final static Log   log = LogFactory.getLog(VisitorManagerImpl.class);
	protected OrgDao           			orgDao;
	private VisitorManagerDirect		visitorManagerDirect;
	private OrgManager					orgManager;
	private VisitorAppLogManager		visitorAppLogManager;
	
	public void setOrgDao(OrgDao orgDao) {
        this.orgDao = orgDao;
    }
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setVisitorManagerDirect(VisitorManagerDirect visitorManagerDirect) {
		this.visitorManagerDirect = visitorManagerDirect;
	}

	public void setVisitorAppLogManager(VisitorAppLogManager visitorAppLogManager) {
		this.visitorAppLogManager = visitorAppLogManager;
	}

	@Override
	@AjaxAccess
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FlipInfo visitorInfo(FlipInfo flipInfo, Map params) throws BusinessException{
		
		Map queryParams = new HashMap<String, Object>();
		queryParams.put("orgAccountId", AppContext.getCurrentUser().getAccountId());
        /********过滤和条件搜索*******/
        if(params != null && params.containsKey("condition")){
        	String condition = String.valueOf(params.get("condition"));
            Object value = params.get("value") == null ? "" : params.get("value");
            if("name".equals(condition)) {
                queryParams.put("name", value);
            }
            if("account_name".equals(condition)) {
            	queryParams.put("account_name", value);
            }
            if("regtime".equals(condition)) {
            	String qTime = String.valueOf(value);
            	if(Strings.isNotBlank(qTime)){
            		qTime = qTime.substring(2, qTime.length()-1).replaceAll("\"", "");
            		queryParams.put("regtime", qTime);
            	}
            }
    		if ("state".equals(condition)) {//1：启用/3：停用
    			value = "1".equals(String.valueOf(params.get("value"))) ? Integer.valueOf(1) : Integer.valueOf(3);
    			queryParams.put("state", value);
    		}
        }
		List<OrgVisitor> ovList = new ArrayList<OrgVisitor>();
		FlipInfo vif = new FlipInfo();
		vif.setPage(1);
		vif.setSize(20);
		ovList = orgDao.getOrgVisitor(flipInfo, queryParams);
		if(flipInfo == null){
			flipInfo = vif;
		}
		flipInfo.setData(ovList);
		return flipInfo;
	}
	
	@Override
	@AjaxAccess
	public FlipInfo updateVisitorInfo(FlipInfo flipInfo, Map params) throws BusinessException{
		if(params.containsKey("param")){
 			if(Strings.isNotBlank(String.valueOf(params.get("param")))){
 				JSONArray idsj = new JSONArray();
 				idsj = (JSONArray) params.get("param");
 				JSONObject jo = idsj.getJSONObject(0);
 				String stt = null;
 				Integer state = OrgConstants.VISITOR_STATE.NORMAL.ordinal();
 				if(jo.containsKey("1")){
 					stt = String.valueOf(jo.get("1"));
 					state = OrgConstants.VISITOR_STATE.NORMAL.ordinal();
 				}
 				if(jo.containsKey("3")){
 					stt = String.valueOf(jo.get("3"));
 					state = OrgConstants.VISITOR_STATE.FORBIDDEN.ordinal();
 				}
 				if(Strings.isNotBlank(stt)){
 					String[] ids = stt.split(",");
 					List<V3xOrgVisitor> visitorList = new ArrayList<V3xOrgVisitor>();
 	 				for(String id : ids){
 	 					V3xOrgVisitor v3xOrgVisitor = orgManager.getVisitorById(Long.parseLong(id));
 	 					v3xOrgVisitor.setState(state);
 	 					visitorList.add(v3xOrgVisitor);
 	 				}
 	 				visitorManagerDirect.updateVisitors(visitorList);
 				}
 				
			}
		}
		return flipInfo;
	}
	
}
