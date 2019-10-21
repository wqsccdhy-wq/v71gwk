package com.seeyon.ctp.organization.memberleave.manager;

import java.util.List;

import org.apache.commons.logging.Log;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.memberleave.bo.MemberLeaveDetail;
import com.seeyon.ctp.util.Strings;

public abstract class AbstractMemberLeaveClearItem implements MemberLeaveClearItemInterface {
	
	private final static Log     logger       = CtpLogFactory.getLog(AbstractMemberLeaveClearItem.class);
	
	public AppLogManager appLogManager;
	
	private OrgManager orgManager;
	
	public AppLogManager getAppLogManager(){
		if(appLogManager == null){
			appLogManager = (AppLogManager)AppContext.getBean("appLogManager");
		}
		return appLogManager;
	}
	
	public OrgManager getOrgManager(){
		if(orgManager == null){
			orgManager = (OrgManager)AppContext.getBean("orgManager");
		}
		return orgManager;
	}

	/**
	 * 将离职人员的授权替换给接收人
	 * @throws BusinessException 
	 */
	public abstract void updateAuthority(Long oldMemberId, Long newMemberId, List<String> authIds) throws BusinessException;
	
	/**
	 *       处理其他的交接事项，比如 原会议室管理员， 替换 管理的会议室下的管理员 为新的接收人
	 * @throws BusinessException 
	 */
	public void updateAuthority2(Long oldMemberId, Long newMemberId, List<String> authIds) throws BusinessException{
	}
	
	/**
	 * 获取交接权限名称
	 * @param oldMemberId
	 * @param authIds
	 * @param showKey
	 * @return
	 * @throws BusinessException
	 */
	public String getAuthNames(Long oldMemberId, List<String> authIds, String... showKey) throws BusinessException {
		StringBuilder authNames = new StringBuilder();
		try {
			List<MemberLeaveDetail> list = this.getItems(oldMemberId);
			for(String id : authIds){
				String showName = "";
				for(MemberLeaveDetail detail : list){
					if(id.equals(detail.getId())){
						for(String key : showKey){
							String name = "";
							if(key.equals("title")){
								name = detail.getTitle();
							}
							if(key.equals("content")){
								name = detail.getContent();
							}
							if(key.equals("type")){
								name = detail.getType();
							}
							
							if(Strings.isBlank(showName)){
								showName = name;
							}else{
								showName = showName + "-" + name;
							}
						}
						break;
					}
				}
				
				if(authNames.length() > 0){
					authNames.append(",");
				}
				authNames.append(showName);
			}
		} catch (Exception e) {
			logger.error("获取交接权限名称异常", e);
		}
		
		return authNames.toString();
	}
	
	/**
	 * 记录交接日志
	 * {0}将《{1}》的以下{2}交接给《{3}》: {4}。 {5}   
	 * 第五个参数只是用于在ctp_apps_log 表中记录被操作人的id，不会体现在日志描述中     
	 * @param oldMemberId
	 * @param newMemberId
	 * @param authNames
	 * @throws BusinessException
	 */
	public void saveLog(Long oldMemberId, Long newMemberId, String authNames) throws BusinessException{
		if(oldMemberId == null || Strings.isBlank(authNames)){
			return;
		}
		
		try {
			User user = AppContext.getCurrentUser();
			int actionId = 890;
			String actionUserName = user.getName();
			String oldMemberName = "";
			String newMemberName = "";
			if(oldMemberId != null){
				V3xOrgMember oldMember = getOrgManager().getMemberById(oldMemberId);
				if(oldMember != null){
					oldMemberName = oldMember.getName();
				}
			}
			
			if(newMemberId != null){
				V3xOrgMember newMember = getOrgManager().getMemberById(newMemberId);
				if(newMember != null){
					newMemberName = newMember.getName();
				}
			}
			String categoryName = ResourceUtil.getString("memberleave.Category."+this.getCategory().name());
			
			getAppLogManager().insertLog(user, actionId, actionUserName, oldMemberName, 
					categoryName, newMemberName, authNames, oldMemberId.toString());
		} catch (Exception e) {
			logger.error("交接日志记录异常", e);
		}
	}
	
}
