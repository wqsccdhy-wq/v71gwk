
package com.seeyon.ctp.organization.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.AppLogConstants;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgVisitor;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.po.OrgVisitor;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;


public class VisitorManagerDirectImpl implements VisitorManagerDirect {

    private final static Log   log = LogFactory.getLog(VisitorManagerDirectImpl.class);

    protected OrgDao           orgDao;
    protected OrgManager       orgManager;
    protected OrgCache         orgCache;
    private OrgManagerDirect   orgManagerDirect;
    protected AppLogManager    appLogManager;


    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setOrgDao(OrgDao orgDao) {
        this.orgDao = orgDao;
    }

    public void setOrgCache(OrgCache orgCache) {
        this.orgCache = orgCache;
    }
    
    public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	@Override
    public OrganizationMessage addVisitor(V3xOrgVisitor visitor) throws BusinessException {
        List<V3xOrgVisitor> visitors = new ArrayList<V3xOrgVisitor>(1);
        visitors.add(visitor);
        return this.addVisitors(visitors);
    }

    @Override
    public OrganizationMessage addVisitors(List<V3xOrgVisitor> visitors) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        List<V3xOrgVisitor> sucessVisitors = new ArrayList<V3xOrgVisitor>();
        if(Strings.isEmpty(visitors)) {return message;}
        for (V3xOrgVisitor visitor : visitors) {
        	visitor.setIdIfNew();
            if (Strings.isBlank(visitor.getMobile())) {
            	message.addErrorMsg(visitor, "手机号码不能为空!");
        		continue;
            }else{
            	List<Long> memberIds = orgCache.getMembersByTelnum(visitor.getMobile());
            	if(Strings.isNotEmpty(memberIds)){
            		message.addErrorMsg(visitor, "手机号码已经被内部人员使用!");
            		continue;
            	}
            	
            	Map param = new HashMap();
            	param.put("mobile", visitor.getMobile());
            	param.put("orgAccountId", visitor.getOrgAccountId());
            	List<OrgVisitor> list = (List<OrgVisitor>)orgDao.getOrgVisitor(new FlipInfo(), param);
            	if(Strings.isNotEmpty(list)){
            		message.addErrorMsg(visitor, "已经注册为访客!");
            		continue;
            	}
            }
            
/*			boolean isDuplicated = orgManagerDirect.isPropertyDuplicated(V3xOrgVisitor.class.getSimpleName(), "mobile", visitor.getMobile());
			if (isDuplicated) {
				message.addErrorMsg(visitor,"手机号码已经被访客注册！");
				return message;
			}*/
            List<OrgVisitor> poList = new ArrayList<OrgVisitor>();
            poList.add((OrgVisitor) visitor.toPO());
            
            sucessVisitors.add(visitor);
            //批量插入
            orgDao.insertOrgVisitor(poList);
            message.addSuccessMsg(visitor);
            
            User user = new User();
            user.setAccountId(visitor.getOrgAccountId());
            user.setId(visitor.getId());
            String name = visitor.getName()+"(" +visitor.getMobile() + ")";
            user.setName(name);
            String ip = visitor.getExt_attr_2();
            if(Strings.isNotBlank(ip)) {
            	user.setRemoteAddr(visitor.getExt_attr_2());
            }
            appLogManager.insertLog4Account(user, visitor.getOrgAccountId(), -801, name, visitor.getAccount_name());
        }
        
        return message;
    }
    
    
	@Override
    public OrganizationMessage updateVisitor(V3xOrgVisitor visitor) throws BusinessException {
        List<V3xOrgVisitor> visitors = new ArrayList<V3xOrgVisitor>(1);
        visitors.add(visitor);
        return this.updateVisitors(visitors);
    }

    @Override
    public OrganizationMessage updateVisitors(List<V3xOrgVisitor> visitors) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        if(Strings.isEmpty(visitors)) {return message;}
        
        User user = AppContext.getCurrentUser();
        for (V3xOrgVisitor visitor : visitors) {
        	List<Long> memberIds = orgCache.getMembersByTelnum(visitor.getMobile());
        	if(Strings.isNotEmpty(memberIds)){
        		message.addErrorMsg(visitor, "手机号码已经被内部人员使用!");
        		continue;
        	}
        	V3xOrgVisitor oldVisitor = orgCache.getV3xOrgVisitor(visitor.getId(),true);
            message.addSuccessMsg(visitor);
            orgDao.update((OrgVisitor)visitor.toPO());
            if(oldVisitor != null) {
            	if(user != null && user.isAdmin()) {//管理员的操作
            		if(!oldVisitor.isValid() && visitor.isValid()) {//启用
            			appLogManager.insertLog4Account(user, AppContext.currentAccountId(), -803, user.getName(),visitor.getName()+"(" +visitor.getMobile() + ")");
            		}else if(oldVisitor.isValid() && !visitor.isValid()) {//停用
            			appLogManager.insertLog4Account(user, AppContext.currentAccountId(), -804, user.getName(),visitor.getName()+"(" +visitor.getMobile() + ")");
            		}
            	}else {
            		//访客自己的操作
                    user = new User();
                    user.setAccountId(visitor.getOrgAccountId());
                    user.setId(visitor.getId());
                    String name = visitor.getName()+"(" +visitor.getMobile() + ")";
                    user.setName(name);
                    String ip = visitor.getExt_attr_2();
                    if(Strings.isNotBlank(ip)) {
                    	user.setRemoteAddr(visitor.getExt_attr_2());
                    }
                    
                    if(visitor.getState() == OrgConstants.VISITOR_STATE.DELETE.ordinal()) {//解绑
                    	appLogManager.insertLog4Account(user, visitor.getOrgAccountId(), -802, name);
                    }else {//重新绑定
                    	appLogManager.insertLog4Account(user, visitor.getOrgAccountId(), -801, name, visitor.getAccount_name());
                    }
            		
            	}
            }
        }
        
        return message;
    }

}
