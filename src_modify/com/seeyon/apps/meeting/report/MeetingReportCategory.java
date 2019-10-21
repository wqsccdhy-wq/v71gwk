/**
 * Author: het
 * Date:: 2018年11月6日:
 *
 * Copyright (C) 2018 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.apps.meeting.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;

import com.google.common.collect.Lists;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.ORGENT_TYPE;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.event.AddAccountEvent;
import com.seeyon.ctp.organization.event.AddAdminMemberEvent;
import com.seeyon.ctp.organization.event.DeleteAccountEvent;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.report.engine.api.bean.DataSourceType;
import com.seeyon.ctp.report.engine.api.interfaces.AbstractReportAppCategory;
import com.seeyon.ctp.report.engine.api.manager.ReportApi;
import com.seeyon.ctp.report.engine.po.ReportDesign;
import com.seeyon.ctp.util.RoleType;
import com.seeyon.ctp.util.annotation.ListenEvent;

/**
 * <p>会议报表分类定义</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: seeyon.com</p>
 */
public class MeetingReportCategory extends AbstractReportAppCategory {
	
	private static final Log logger = CtpLogFactory.getLog(MeetingReportCategory.class);
	
	/**系统报表ID--会议室明细查询*/
	public static Long SYSTEM_REPORTID_MEETING 					= 4085567871552603725L;
	/**系统报表ID--会议室申请统计*/
	public static Long SYSTEM_REPORTID_MEETING_ROOM 			= 6179881880964910897L;
	/**系统报表ID--会议室管理员管理的会议室申请统计*/
	public static Long SYSTEM_REPORTID_MEETING_ROOM_4_ROOMADMIN = 3702223525420024918L;
	/**系统报表ID--本单位会议室使用明细*/
	public static Long SYSTEM_REPORTID_ROOMLIST_ACCOUNT 		= 8603796114228461663L;
	/**系统报表ID--我管理的会议室使用明细*/
	public static Long SYSTEM_REPORTID_ROOMLIST_ADMIN 			= -1763432283535487673L;
	
	public static DataSourceType MEETING_SOURCETYPE = new DataSourceType("meeting", "会议");
	
	private ReportApi reportApi;
	private OrgManager orgManager;
	
	public void setReportApi(ReportApi reportApi) {
		this.reportApi = reportApi;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	@ListenEvent(event = AddAdminMemberEvent.class)
	public void onAddAccountAdmin(AddAdminMemberEvent evt) throws BusinessException {
		/*Map<String, Object> auth1 = getAuthInfo(SYSTEM_REPORTID_MEETING,ORGENT_TYPE.Member.ordinal(),evt.getMember().getId());
		Map<String, Object> auth2 = getAuthInfo(SYSTEM_REPORTID_MEETING_ROOM,ORGENT_TYPE.Member.ordinal(),evt.getMember().getId());
		Map<String, Object> auth3 = getAuthInfo(SYSTEM_REPORTID_ROOMLIST_ACCOUNT,ORGENT_TYPE.Member.ordinal(),evt.getMember().getId());
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> authList = Lists.newArrayList(auth1,auth2,auth3);
		reportApi.saveDesignAuths(authList);*/
		
		logger.info("预置会议系统报表授权成功！"+evt.getMember().getId());
	}
	
	@SuppressWarnings("unchecked")
	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws BusinessException {
		/*List<V3xOrgRole> roleList = orgManager.getRoleByCode(RoleType.MeetingRoomAdmin.name(), evt.getAccount().getId());
		if(CollectionUtils.isNotEmpty(roleList)){
			Map<String, Object> auth1  = getAuthInfo(SYSTEM_REPORTID_MEETING_ROOM_4_ROOMADMIN,ORGENT_TYPE.Role.ordinal(),roleList.get(0).getId());
			Map<String, Object> auth2 = getAuthInfo(SYSTEM_REPORTID_ROOMLIST_ADMIN,ORGENT_TYPE.Role.ordinal(),roleList.get(0).getId());
			reportApi.saveDesignAuths(Lists.newArrayList(auth1,auth2));
			logger.info("预置会议系统报表会议管理员授权成功！"+roleList.get(0).getId());
		}*/
	}
	
	@ListenEvent(event = DeleteAccountEvent.class)
	public void onDeleteAccount(DeleteAccountEvent evt) throws BusinessException {
		
	}
	
	@Override
	public void addSystemReportAuths(List<ReportDesign> designs) throws BusinessException {
		/*if(CollectionUtils.isEmpty(designs)){
			return;
		}
		List<Long> roleDesignIds = new ArrayList<Long>();
		List<Long> memberDesignIds = new ArrayList<Long>();
		for(ReportDesign design : designs){
			if(!ApplicationCategoryEnum.meeting.name().equals(design.getCategory())){
				continue;
			}
			long designId = design.getId().longValue();
			if(SYSTEM_REPORTID_MEETING_ROOM_4_ROOMADMIN.longValue() == designId 
					|| SYSTEM_REPORTID_ROOMLIST_ADMIN.longValue() == designId){
				roleDesignIds.add(design.getId());
			}else if( SYSTEM_REPORTID_MEETING.longValue() == designId
					|| SYSTEM_REPORTID_MEETING_ROOM.longValue() == designId
					|| SYSTEM_REPORTID_ROOMLIST_ACCOUNT.longValue() == designId ){
				memberDesignIds.add(design.getId());
			}
		}
		
		List<Map<String, Object>> auths = Lists.newArrayList();
		List<V3xOrgAccount> accounts = orgManager.getAllAccounts();
		for(V3xOrgAccount acc : accounts){
			if(!acc.isGroup()){
				if(CollectionUtils.isNotEmpty(roleDesignIds)){
					List<V3xOrgRole> roles = orgManager.getRoleByCode(RoleType.MeetingRoomAdmin.name(), acc.getId());
					for(V3xOrgRole role : roles){
						for(Long designId : roleDesignIds){
							auths.add(getAuthInfo(designId,ORGENT_TYPE.Role.ordinal(),role.getId()));
						}
					}
				}
				
				V3xOrgMember accAdmin = orgManager.getAdministrator(acc.getId());
				if(CollectionUtils.isNotEmpty(memberDesignIds) && null != accAdmin){//从日志看有为空的单位管理员
					for(Long designId : memberDesignIds){
						auths.add(getAuthInfo(designId,ORGENT_TYPE.Member.ordinal(),accAdmin.getId()));
					}
				}
			}
		}
		reportApi.saveDesignAuths(auths);*/
		logger.info("升级预置会议报表授权成功！");
	}
	
	
	@Override
	public String getAppCategory() {
		return ApplicationCategoryEnum.meeting.name();
	}

	@Override
	public List<DataSourceType> getReportAppSourceType() {
		return Lists.newArrayList(MEETING_SOURCETYPE);
	}

	@Override
	public List<String> getAppCategoryShowDataSource(DataSourceType sourceType) {
		return Lists.newArrayList(ApplicationCategoryEnum.meeting.name());
	}

	@Override
	public Boolean hasReportAuth(String appCategoryId, User user) {
		//appCategoryId按照单位ID划分
		boolean hasAuth = false;
		try {
			if(user.getLoginAccount().toString().equals(appCategoryId)){
				if(user.isAdmin()){
					hasAuth = true;
				}else if(orgManager.isRole(user.getId(), user.getLoginAccount(), OrgConstants.Role_NAME.MeetingRoomAdmin.name())){
					hasAuth = true;
				}else{
					logger.info(String.format("拒绝访问，当前用户%s即不是单位/集团/系统管理员，又不是会议室管理员，无法制作报表!", user.getId().toString()));
				}
			}else{
				logger.info(String.format("拒绝访问，当前用户%s所在单位%S与请求归属单位%S不一致!", user.getId().toString(),appCategoryId,user.getLoginAccount().toString()));
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return hasAuth;
	}

	@Override
	public Map<String, Object> getAppProperties() {
		Map<String, Object> map = super.getAppProperties();
		map.put(TPL_BUTTON_PRINT, false);
		map.put(TPL_BUTTON_SEND_COL, false);
		return map;
	}
}
