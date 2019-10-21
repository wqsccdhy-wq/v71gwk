package com.seeyon.ctp.organization.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.po.OrgLevel;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
@CheckRoleAccess(roleTypes = {Role_NAME.GroupAdmin, Role_NAME.AccountAdministrator,Role_NAME.HrAdmin})
@ProcessInDataSource(name = DataSourceName.BASE)
public class WorkscopeManagerImpl implements WorkscopeManager {
    private final static Log logger = LogFactory.getLog(com.seeyon.ctp.organization.manager.WorkscopeManagerImpl.class);
    protected OrgCache orgCache;
    protected OrgDao orgDao;
    protected OrgManagerDirect orgManagerDirect;
    protected OrgManager orgManager;
    protected PrincipalManager principalManager;
    protected AppLogManager appLogManager;

	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}
	
	public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }

    public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}

	public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	@Override
	public String editScope(String accountId) throws Exception {
		String srt = String.valueOf(orgManager.getAccountById(Long.parseLong(accountId)).getLevelScope());
		if(srt==null||"".equals(srt)){
			return "-1";
		}else{
			return srt;
		}
	}
	@Override
	public void saveScope(String accountId, String scope) throws BusinessException {
		V3xOrgAccount account = orgManager.getAccountById(Long.parseLong(accountId));
		account.setLevelScope(Integer.valueOf(scope));
		orgManagerDirect.updateAccount(account);
		User user = AppContext.getCurrentUser();
		appLogManager.insertLog4Account(user, user.isGroupAdmin() ? Long.parseLong(accountId) : AppContext.currentAccountId(), AppLogAction.Organization_UpdateWorkScope, user.getName());
	}

	@Override
    public HashMap showScope(Long accountId, String levelscope) throws BusinessException {
        HashMap<Long, String> m = new HashMap<Long, String>();
        int scope = Integer.valueOf(levelscope);
        List<V3xOrgLevel> levels = orgManagerDirect.getAllLevels(accountId, false);
        for (int i = 0; i < levels.size(); i++) {
            StringBuilder names = new StringBuilder();
            V3xOrgLevel level = levels.get(i);
            for (int j = 0; j < levels.size(); j++) {
                V3xOrgLevel namelevel = levels.get(j);
                if (scope == -1 || levels.get(i).getLevelId().intValue() - scope - 1 < levels.get(j).getLevelId().intValue()) {
                    names.append(namelevel.getName()).append("、");
                }
            }
            if("、".equals(names.substring(names.length()-1))){
                names = new StringBuilder(names.substring(0,names.length()-1));
            }
            m.put(level.getId(), names.toString());
        }

        return m;
    }

	@Override
	public FlipInfo showWorkscopeList(FlipInfo fi, Map params)
			throws BusinessException {
		Long accountId = Long.parseLong(params.get("accountId").toString());
		String scope = new String();
        if (!params.containsKey("levelscope") || String.valueOf(params.get("levelscope")) == null
                || "null".equals(String.valueOf(params.get("levelscope")))) {
            scope = "-1";
        } else {
            scope = String.valueOf(params.get("levelscope"));
        }
		
		HashMap scopem = showScope(accountId, scope);
		List rellist = new ArrayList();
		List<OrgLevel> levels = orgDao.getAllLevelPO(accountId, true, null, null, null);
		//相同级别序号的职级进行分组
		for (int i = 0; i < levels.size(); i++) {
			HashMap m = new HashMap();
			//String longname = "";
			boolean flag = false;
			for (int j = 0; j < levels.size(); j++) {
				if ((!levels.get(j).getId().equals(levels.get(i).getId()))
						&& (levels.get(j).getLevelId().equals(levels.get(i)
								.getLevelId()))) {
					//longname = longname +  levels.get(j).getName()+"、" ;
					//m.put("name", m.get("name") + "、" + levels.get(j).getName());
					m.put("levelId", levels.get(i).getLevelId());
					m.put("visit", scopem.get(levels.get(i).getId()));
				} else {
					m.put("name", levels.get(i).getName());
					m.put("levelId", levels.get(i).getLevelId());
					m.put("visit", scopem.get(levels.get(i).getId()));
				}
			}
			//m.put("name", longname);
			rellist.add(m);
		}
		
		fi.setData(rellist);
		return fi;
	}

}
