package com.seeyon.ctp.organization.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.Constants.LoginOfflineOperation;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.login.online.OnlineRecorder;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgMember;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator})
@ProcessInDataSource(name = DataSourceName.BASE)
public class GuestManagerImpl implements GuestManager{
	
	private OrgManager orgManager;
	private OrgManagerDirect orgManagerDirect;
	private OrgDao orgDao;
	private AppLogManager       appLogManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	@Override
	@AjaxAccess
	public boolean isGuestEnable() throws BusinessException{
		boolean enable = false;
		V3xOrgMember guest = orgManager.getMemberById(OrgConstants.GUEST_ID);
		if (null == guest) {
            return false;
        }
		enable = guest.getEnabled();
		return enable;
	}
	
	@Override
	@AjaxAccess
	public boolean changeEnable() throws BusinessException{
		User user = AppContext.getCurrentUser();
		if((Boolean)SysFlag.sys_isGroupVer.getFlag() && !AppContext.isGroupAdmin()){
			throw new BusinessException("修改登录前账号，权限不足！");
		}
		V3xOrgMember guest = orgManager.getMemberById(OrgConstants.GUEST_ID);
		boolean enable = guest.getEnabled();
		guest.setEnabled(!enable);
		OrganizationMessage message = orgManagerDirect.updateGuest(guest);
		if(enable){//启用-->停用
			appLogManager.insertLog4Account(user, AppContext.currentAccountId(),807, user.getName(), guest.getName());
		}else{//停用-->启用
			appLogManager.insertLog4Account(user, AppContext.currentAccountId(),806, user.getName(), guest.getName());
		}
		if(!message.isSuccess()) {
			return false;
		}
		return true;
	}
	
    @Override
    @AjaxAccess
    public FlipInfo guestList(FlipInfo fi, Map params) throws BusinessException {
        String condition = "externalType";
        Object value = OrgConstants.ExternalType.Interconnect2.ordinal();

        Long accountId = AppContext.currentAccountId();
        orgDao.getAllMemberPOByAccountId(accountId, null, false, null, condition, value, fi);
        
        List<OrgMember> data = fi.getData();
        List<V3xOrgMember> members = (List<V3xOrgMember>) OrgHelper.listPoTolistBo(data);
        List<WebV3xOrgMember> result = new ArrayList<WebV3xOrgMember>();
        for (V3xOrgMember member : members) {
        	if(!member.isGuest() || OrgConstants.GUEST_ID.equals(member.getId())){
        		continue;
        	}
            WebV3xOrgMember o = new WebV3xOrgMember();
            o.setName(member.getName());
            o.setLoginName(member.getLoginName());
            o.setV3xOrgMember(member);
            o.setEnableName(member.getEnabled()?ResourceUtil.getString("org.member_form.start.rep"):ResourceUtil.getString("org.member_form.end.rep"));
            o.setId(member.getId());
            o.setDescription(member.getDescription());
            result.add(o);
        }
        fi.setData(result);
        return fi;
    }
    
    @Override
    @AjaxAccess
    public Long createGuest(Map map) throws BusinessException {
        Long accountId = AppContext.currentAccountId();
        User user = AppContext.getCurrentUser();
        
        V3xOrgMember member = new V3xOrgMember();
        ParamUtil.mapToBean(map, member, false);
        member.setId(UUIDLong.longUUID());
        member.setOrgAccountId(accountId);
        V3xOrgPrincipal p = new V3xOrgPrincipal(
                member.getId(),
                map.get("loginName").toString(),
                map.get("password").toString());
        member.setV3xOrgPrincipal(p);
        
        OrganizationMessage returnMessage = orgManagerDirect.addGuest(member);
        OrgHelper.throwBusinessExceptionTools(returnMessage);
        appLogManager.insertLog4Account(user, AppContext.currentAccountId(),808, user.getName(), member.getName());
        return returnMessage.getSuccessMsgs().get(0).getEnt().getId();
    }
    
    @Override
    @AjaxAccess
    public Long updateGuest(Map map) throws BusinessException {
        User user = AppContext.getCurrentUser();
        V3xOrgMember updateMember = new V3xOrgMember();
        ParamUtil.mapToBean(map, updateMember, false);
        V3xOrgMember oldMember = orgManager.getMemberById(updateMember.getId());
        
        V3xOrgPrincipal oldPrincipal = oldMember.getV3xOrgPrincipal();
        boolean passwordchange = (null != map.get("isChangePWD") && Boolean.valueOf(map.get("isChangePWD").toString()))
                || null == oldPrincipal || !oldPrincipal.getLoginName().equals(map.get("loginName").toString())
                || !OrgConstants.DEFAULT_INTERNAL_PASSWORD.equals(map.get("password").toString());
        if (passwordchange) {
            //loginName
            V3xOrgPrincipal p = new V3xOrgPrincipal(updateMember.getId(), map.get("loginName").toString(), map.get(
                    "password").toString());
            updateMember.setV3xOrgPrincipal(p);
        }
        
        OrganizationMessage returnMessage = orgManagerDirect.updateGuest(updateMember);
        OrgHelper.throwBusinessExceptionTools(returnMessage);
        appLogManager.insertLog4Account(user, AppContext.currentAccountId(),809, user.getName(), updateMember.getName());
        
        if (passwordchange) {
        	OnlineRecorder.moveToOffline(oldMember.getLoginName(), LoginOfflineOperation.adminKickoff);
        }
        
        return returnMessage.getSuccessMsgs().get(0).getEnt().getId();
    }
    
    @Override
    @AjaxAccess
    public Boolean deleteGuests(Long[] ids) throws BusinessException {
    	User user = AppContext.getCurrentUser();
        List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
        String names = "";
        for (Long s : ids) {
        	V3xOrgMember guest = orgManager.getMemberById(s);
            members.add(guest);
            if(Strings.isBlank(names)){
            	names = guest.getName();
            }else{
            	names = names + "、" + guest.getName();
            }
        }
        OrganizationMessage returnMessage = orgManagerDirect.deleteGuests(members);
        OrgHelper.throwBusinessExceptionTools(returnMessage);
        appLogManager.insertLog4Account(user, AppContext.currentAccountId(),810, user.getName(), names);

        return true;
    }
    
	@Override
	@AjaxAccess
    public HashMap viewOne(Long memberId) throws BusinessException {
        HashMap map = new HashMap();
        V3xOrgMember member = orgManager.getMemberById(memberId);
        ParamUtil.beanToMap(member, map, false);
        return map;
    }
	
	@Override
	public String getDefaultLoginName(){
		return OrgConstants.DEFAULT_GUEST_NAME;
	}
	
	@Override
	public String getDefaultLoginPassword(){
		return OrgConstants.DEFAULT_PASSWORD;
	}

}
