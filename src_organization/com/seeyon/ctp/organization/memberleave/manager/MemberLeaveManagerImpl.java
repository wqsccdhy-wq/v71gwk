/**
 * 
 */
/**
 * Id: MemberLeaveHelper.java, v1.0 2011-12-1 wangchw wangchw Exp
 * Copyright (c) 2011 Seeyon, Ltd. All rights reserved
 */
package com.seeyon.ctp.organization.memberleave.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.Constants.LoginOfflineOperation;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.po.applog.AppLog;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.dubbo.RefreshInterfacesAfterUpdate;
import com.seeyon.ctp.login.online.OnlineRecorder;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.memberleave.bo.MemberLeaveDetail;
import com.seeyon.ctp.organization.memberleave.bo.MemberLeavePending;
import com.seeyon.ctp.organization.memberleave.bo.MemberLeavePendingData;
import com.seeyon.ctp.organization.po.OrgTeam;
import com.seeyon.ctp.organization.webmodel.WebMemberLeaveAppLog;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

/**
 * @Project/Product: A8（A8）
 * @Description: 人员离职管理
 * @Copyright: Copyright (c) 2011 of Seeyon, Ltd.
 * @author: wangchw
 * @author: tanmf
 * @time: 2011-12-1 下午06:01:53
 * @version: v1.0
 */
@ProcessInDataSource(name = DataSourceName.BASE)
public class MemberLeaveManagerImpl extends AbstractSystemInitializer implements MemberLeaveManager {
	
	private final static Log     logger       = CtpLogFactory.getLog(MemberLeaveManagerImpl.class);
    /**
     * 应用日志
     */
    private AppLogManager                  appLogManager;
    /**
     * 组织模型
     */
    private OrgManager                     orgManager;

    private OrgManagerDirect               orgManagerDirect;
    
    private OrgDao orgDao;
    
    private OrgCache orgCache;

    //不需要做缓存
    private List<MemberLeaveClearItemInterface>    HandItems = new ArrayList<MemberLeaveClearItemInterface>();
    private Map<String, MemberLeaveDataInterface> PendingDatas  = new HashMap<String, MemberLeaveDataInterface>();
    
    public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }

	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}

	public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}

    public void initialize() {

        fillMemberLeaveClearItemInterface();
        fillMemberLeaveDataInterface();
    }

    @RefreshInterfacesAfterUpdate(inface = MemberLeaveClearItemInterface.class)
    public void fillMemberLeaveClearItemInterface() {
        Map<String, MemberLeaveClearItemInterface> _panels = AppContext.getBeansOfType(MemberLeaveClearItemInterface.class);
        HandItems.addAll(_panels.values());
        Collections.sort(HandItems, MemberLeaveHandItemInterfaceComparatorInstance);
    }

    @RefreshInterfacesAfterUpdate(inface = MemberLeaveDataInterface.class)
    public void fillMemberLeaveDataInterface() {
        Map<String, MemberLeaveDataInterface> _datas = AppContext.getBeansOfType(MemberLeaveDataInterface.class);
        for (MemberLeaveDataInterface d : _datas.values()) {
            if (d.isEnabled()) {
                this.PendingDatas.put(d.getAppKey(), d);
            }
        }
    }
    
    private static final MemberLeaveDataInterfaceComparator MemberLeaveDataInterfaceComparatorInstance = new MemberLeaveDataInterfaceComparator();
    
    private static class MemberLeaveDataInterfaceComparator implements Comparator<MemberLeaveDataInterface> {
        public int compare(MemberLeaveDataInterface o1, MemberLeaveDataInterface o2) {
            int thisVal = o1.getSortId();
            int anotherVal = o2.getSortId();
            return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
        }
    }
    
    private static final MemberLeaveHandItemInterfaceComparator MemberLeaveHandItemInterfaceComparatorInstance = new MemberLeaveHandItemInterfaceComparator();
    private static class MemberLeaveHandItemInterfaceComparator implements Comparator<MemberLeaveClearItemInterface> {
        public int compare(MemberLeaveClearItemInterface o1, MemberLeaveClearItemInterface o2) {
            Integer thisVal = o1.getSortId();
            Integer anotherVal = o2.getSortId();
            
            if(thisVal == null){
                return 1;
            }
            if(anotherVal == null){
                return -1;
            }
            
            return thisVal.compareTo(anotherVal);
        }
    }
    
    public List<MemberLeavePending> getMemberLeavePending(long leaveMemberId) throws BusinessException{
        List<MemberLeavePending> result = new ArrayList<MemberLeavePending>();
        
        List<MemberLeaveDataInterface> dis = new ArrayList<MemberLeaveDataInterface>(this.PendingDatas.values());
        Collections.sort(dis, MemberLeaveDataInterfaceComparatorInstance);
        
        for (MemberLeaveDataInterface pendingBean : dis) {
            MemberLeavePending pending = new MemberLeavePending();
            
            pending.setKey(pendingBean.getAppKey());
            pending.setCount(pendingBean.getCount(leaveMemberId));
            pending.setLabel(pendingBean.getLabel());
            pending.setAgentMemberId(pendingBean.getAgentMemberId(leaveMemberId));
            pending.setMustSetAgentMember(pendingBean.isMustSetAgentMember(leaveMemberId));
            
            result.add(pending);
        }
        
        return result;
    }
    
    public FlipInfo listPendingData(FlipInfo fi, Map<String, String> params0) throws BusinessException{
        String key = (String)params0.get("key");
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("memberId", Long.parseLong(params0.get("memberId")));
        
        if(Strings.isNotBlank(params0.get("senderName"))){
            params.put("senderName", params0.get("senderName"));
        }
        if(Strings.isNotBlank(params0.get("condition")) && "sendDate".equals((String)params0.get("condition"))){
            params.put("sendDate", params0.get("sendDate"));
            params.put("textfield", params0.get("textfield"));
            params.put("textfield1", params0.get("textfield1"));
        }
        if(Strings.isNotBlank(params0.get("subject"))){
            params.put("subject", params0.get("subject"));
        }
        
        MemberLeaveDataInterface mdi = this.PendingDatas.get(key);
        List<MemberLeavePendingData> list = mdi.list(fi, params);
        
        fi.setData(list);
        
        return fi;
    }
    
    public List<MemberLeaveDetail> getMemberLeaveHandItem(long leaveMemberId,int category) throws BusinessException{
        List<MemberLeaveDetail> result = new ArrayList<MemberLeaveDetail>();
        
        for (MemberLeaveClearItemInterface m : this.HandItems) {
        	try {
        		if(m.getCategory().ordinal() == category){
        			List<MemberLeaveDetail> items = m.getItems(leaveMemberId);
        			
        			if(items == null || items.isEmpty()){
        				continue;
        			}
        			
        			result.addAll(items);
        		}
			} catch (Exception e) {
				logger.error("离职接口实现异常：" + m.getClass().getSimpleName() ,e);
			}
        }
        
        return result;
    }
    //防止越权
    @CheckRoleAccess(roleTypes={Role_NAME.AccountAdministrator, Role_NAME.DepAdmin,Role_NAME.HrAdmin})
    public boolean save4Leave(long leaveMemberId, Map<String, Long> agentMember) throws BusinessException{
        User user = AppContext.getCurrentUser();
        
        V3xOrgMember leaveMember = orgManager.getMemberById(leaveMemberId);
        
        //将离职用户踢下下线
        OnlineRecorder.moveToOffline(leaveMember.getLoginName(), LoginOfflineOperation.adminKickoff);
        
        //将账号设置成离职状态，一定要放在“离职工作交接”之前，否则被清掉了
        
        Long noupdateState=(null==agentMember.get("noupdateState"))?-1:Long.valueOf(agentMember.get("noupdateState").toString());
        if(noupdateState!=1L){
        	leaveMember.setEnabled(false);
        	leaveMember.setState(OrgConstants.MEMBER_STATE.RESIGN.ordinal());
        }
        
        OrganizationMessage returnMessage = this.orgManagerDirect.updateMember(leaveMember);
        OrgHelper.throwBusinessExceptionTools(returnMessage);
        
        //离职工作交接
        for (MemberLeaveDataInterface memberLeaveDataInterface : this.PendingDatas.values()) {
            String appKey = memberLeaveDataInterface.getAppKey();
            Long agentMemberId = agentMember.get(appKey);
            
            Long oldAgentMemberId = memberLeaveDataInterface.getAgentMemberId(leaveMemberId);
            if(Strings.equals(oldAgentMemberId, agentMemberId)){
                //ignore
            }
            else if(agentMemberId == null){
                memberLeaveDataInterface.removeAgent(leaveMemberId);
            }
            else{
                memberLeaveDataInterface.doHandle(leaveMemberId, agentMemberId);
            }
        }

        //应用日志
        appLogManager.insertLog4Account(user, user.isGroupAdmin() ? leaveMember.getOrgAccountId() : AppContext.currentAccountId(), AppLogAction.Organization_MemberLeave, user.getName(), leaveMember.getName());
        
        return true;
    }
    
    //防止越权
    @AjaxAccess
    @CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator, Role_NAME.DepAdmin,Role_NAME.HrAdmin})
    @Override
    public void updateMemberToLeave(long leaveMemberId) throws BusinessException{
        User user = AppContext.getCurrentUser();
        
        V3xOrgMember leaveMember = orgManager.getMemberById(leaveMemberId);
        leaveMember.setEnabled(false);
        leaveMember.setState(OrgConstants.MEMBER_STATE.RESIGN.ordinal());
        
        //将离职用户踢下下线
        OnlineRecorder.moveToOffline(leaveMember.getLoginName(), LoginOfflineOperation.adminKickoff);
        
        OrganizationMessage returnMessage = this.orgManagerDirect.updateMember(leaveMember);
        OrgHelper.throwBusinessExceptionTools(returnMessage);

        //应用日志
        appLogManager.insertLog4Account(user, user.isGroupAdmin() ? leaveMember.getOrgAccountId() : AppContext.currentAccountId(), AppLogAction.Organization_MemberLeave, user.getName(), leaveMember.getName());
    }
    
    @Override
    public boolean save4LeaveFromTrigger(long leaveMemberId, Map<String, Long> agentMember) throws BusinessException{
        User user = AppContext.getCurrentUser();
        V3xOrgMember leaveMember = orgManager.getMemberById(leaveMemberId);
        
        //将离职用户踢下下线
        OnlineRecorder.moveToOffline(leaveMember.getLoginName(), LoginOfflineOperation.adminKickoff);
        
        //离职工作交接
        for (MemberLeaveDataInterface memberLeaveDataInterface : this.PendingDatas.values()) {
            String appKey = memberLeaveDataInterface.getAppKey();
            Long agentMemberId = agentMember.get(appKey);
            
            Long oldAgentMemberId = memberLeaveDataInterface.getAgentMemberId(leaveMemberId);
            if(Strings.equals(oldAgentMemberId, agentMemberId)){
                //ignore
            }
            else if(agentMemberId == null){
                memberLeaveDataInterface.removeAgent(leaveMemberId);
            }
            else{
                memberLeaveDataInterface.doHandle(leaveMemberId, agentMemberId);
            }
        }

        //应用日志
        appLogManager.insertLog4Account(user, user.isGroupAdmin() ? leaveMember.getOrgAccountId() : AppContext.currentAccountId(), AppLogAction.Organization_MemberLeave, user.getName(), leaveMember.getName());
        return true;
    }
    
    public void transMemberReturn(long leaveMemberId) throws BusinessException{
        for (MemberLeaveDataInterface memberLeaveDataInterface : this.PendingDatas.values()) {
            memberLeaveDataInterface.removeAgent(leaveMemberId);
        }
    }

	@Override
	public FlipInfo showLeaveInfo(FlipInfo fi, Map params) throws BusinessException {
		List<MemberLeaveDetail> leaveInfoList = new ArrayList<MemberLeaveDetail>();
		List<MemberLeaveDetail> result = new ArrayList<MemberLeaveDetail>();
		if (params.containsKey("memberId") && Strings.isNotBlank(String.valueOf(params.get("memberId")))) {
			long memberId = Long.valueOf(params.get("memberId").toString());
			int category = Integer.valueOf(params.get("category").toString());
			leaveInfoList = getMemberLeaveHandItem(memberId,category);
			
			if(params.containsKey("condition") && Strings.isNotBlank(String.valueOf(params.get("condition"))) && !"undefined".equals(String.valueOf(params.get("condition")))
					&& params.containsKey("value") && Strings.isNotBlank(String.valueOf(params.get("value"))) && !"undefined".equals(String.valueOf(params.get("value")))){
				String condition=params.get("condition").toString();
				String value=params.get("value").toString();
				String[] valueArr = value.replace("[", "").replace("]", "").replace("\"", "").split(",");
				
				for(MemberLeaveDetail info : leaveInfoList){
					if("accountName".equals(condition)){
						//[北京分公司, Account|-1685869084055551370]
						if(null!=valueArr && valueArr.length>0){
							value=valueArr[0];
							if(info.getAccountName().equals(value)){
								result.add(info);
							}
						}else{
							result.add(info);
						}
					}
/*					else if(condition.indexOf("type")==0){
						if(info.getType().equals(value)){
							result.add(info);
						}
					}*/
					else if(condition.indexOf("type")==0){
						if(info.getType().indexOf(value)!=-1){
							result.add(info);
						}
					}else if("content".equals(condition)){
						if(info.getContent().indexOf(value)!=-1){
							result.add(info);
						}
					}else if("title".equals(condition)){
						if(info.getTitle().indexOf(value)!=-1){
							result.add(info);
						}
					}else if("roleType".equals(condition)){
						if(info.getRoleType() == Integer.valueOf(value)){
							result.add(info);
						}
					}
				}
				DBAgent.memoryPaging(result, fi);
			}else{
				DBAgent.memoryPaging(leaveInfoList, fi);
			}
		}
		return fi;

	}
	
    @Override
    @AjaxAccess
    public FlipInfo showTeamList(FlipInfo fi, Map params)throws BusinessException {
        Long accountId = Long.valueOf(params.get("accountId").toString());

        Map<String, Object> param = new HashMap<String, Object>();
        List<OrgTeam> list = new ArrayList<OrgTeam>();
    	// 按人员名查询组
    	long memberId = Long.valueOf(params.get("memberId").toString());
    	
    	Object condition = params.get("condition");
    	Object value = params.get("value");
    	
		param.put("Member", memberId);
		list = orgDao.getAllTeamPO(null, null, null, param, null);
        List rellist = new ArrayList();
        for (OrgTeam object : list) {
            HashMap<String, String> m = new HashMap<String, String>();
            ParamUtil.beanToMap((OrgTeam)object, m, true);

            V3xOrgUnit unit = orgManager.getUnitById(Long.valueOf(String.valueOf(m.get("ownerId"))));
            if(null == unit) {
                continue;
            }

            //根据查询条件过滤权限属性
            if(orgManager.isEmptyTeamScope((V3xOrgTeam)OrgHelper.poTobo(object))){
            	m.put("scope", "2");
            }else{
            	m.put("scope", "1");
            }
            
            if(condition != null && value != null 
            	&& Strings.isNotBlank(condition.toString()) && Strings.isNotBlank(value.toString())
            	&& !"undefined".equals(condition)){
            	if("name".equals(condition)){
            		String name = ((OrgTeam)object).getName();
            		if(name.indexOf(value.toString()) != -1){
            			rellist.add(m);
            		}
            	}else if("accountName".equals(condition)){
    				String[] valueArr = params.get("value").toString().replace("[", "").replace("]", "").replace("\"", "").split(",");
					value=valueArr[1];
					if(((OrgTeam)object).getOrgAccountId().equals(Long.valueOf(value.toString()))){
						rellist.add(m);
					}
            	}
            }else{
            	rellist.add(m);
            }
            

        }

        DBAgent.memoryPaging(rellist, fi);

        Map<Long,List<V3xOrgMember>> leaderMap = getTeamsLeader(accountId);

        List result = new ArrayList();
        for (Object m : fi.getData()) {
            V3xOrgTeam team = orgManager.getTeamById(Long.valueOf(((HashMap) m).get("id").toString()));
            if (orgManager.isEmptyTeamScope(team)) {
                ((HashMap) m).put("scope", "2");
            } else {
                ((HashMap) m).put("scope", "1");
            }
            List teamLeader = leaderMap.get(team.getId());

            if (teamLeader!=null && teamLeader.size() > 0) {
                String names = OrgHelper.showOrgEntities(teamLeader, "id", "entityType", null);
                ((HashMap) m).put("teamLeader", names);
            } else {
                ((HashMap) m).put("teamLeader", "");
            }
            
            ((HashMap) m).put("accountName", orgManager.getAccountById(team.getOrgAccountId()).getName());
            result.add(m);
        }
        fi.setData(result);

        return fi;
    }
    
    private Map<Long,List<V3xOrgMember>> getTeamsLeader(Long accountId) throws BusinessException {
    	Map<Long,List<V3xOrgMember>> map = new HashMap<Long, List<V3xOrgMember>>();
    	EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
        enummap.put(OrgConstants.RelationshipObjectiveName.objective5Id, OrgConstants.TeamMemberType.Leader.name());
    	List<V3xOrgRelationship> rellist = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Team_Member, null, accountId, enummap);
    	for (V3xOrgRelationship orgRelationship : rellist) {
    		Long teamId = Long.valueOf(orgRelationship.getSourceId());
    		if(!map.containsKey(teamId)){
    			map.put(teamId, new UniqueList<V3xOrgMember>());
    		}
    		List<V3xOrgMember> memberList = map.get(teamId);
    		memberList.add(orgManager.getMemberById(orgRelationship.getObjective0Id()));
		}
    	return map;
    }
	
	@Override
	@AjaxAccess
	@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator, Role_NAME.DepAdmin,Role_NAME.HrAdmin})
    public void dealLeave(int index, String oldMemberId, String newMemberId, Object data) throws BusinessException {
		Long newMember = null;
		if(Strings.isNotBlank(newMemberId)){
			newMember = Long.valueOf(newMemberId);
		}
			
		switch (index) {
		  	case 1:
		  		dealLeave1(Long.valueOf(oldMemberId), (Map)data);
		  		break;
		  	case 2:
		  		break;
		  	case 3:
		  		break;
		  	case 4:
		  		dealLeaveCommon(Long.valueOf(oldMemberId), newMember ,(List<String>)data);
		  		break;
		  	case 5:
		  		dealLeaveCommon(Long.valueOf(oldMemberId), newMember ,(List<String>)data);
		  		break;
		  	case 6:
		  		dealLeaveCommon(Long.valueOf(oldMemberId), newMember ,(List<String>)data);
		  		break;
		  	case 7:
		  		dealLeaveCommon(Long.valueOf(oldMemberId), newMember ,(List<String>)data);
		  		break;
		}
    }
	
    private void dealLeave1(Long memberId, Map map) throws BusinessException {
        Map<String, Long> agentMember = new HashMap<String, Long>();
        for (Object key : map.keySet()) {
            if(String.valueOf(key).startsWith("AgentId_")){
                String value = String.valueOf(map.get(key));
                
                if(Strings.isNotBlank(value)){
                    agentMember.put(String.valueOf(key).substring(8), Long.parseLong(value));
                }
            }
        }
        
        //离职工作交接
        for (MemberLeaveDataInterface memberLeaveDataInterface : this.PendingDatas.values()) {
            String appKey = memberLeaveDataInterface.getAppKey();
            Long agentMemberId = agentMember.get(appKey);
            
            Long oldAgentMemberId = memberLeaveDataInterface.getAgentMemberId(memberId);
            if(Strings.equals(oldAgentMemberId, agentMemberId)){
                //ignore
            }
            else if(agentMemberId == null){
                memberLeaveDataInterface.removeAgent(memberId);
            }
            else{
                memberLeaveDataInterface.doHandle(memberId, agentMemberId);
            }
        }
    } 
    
    private void dealLeaveCommon(Long oldMemberId, Long newMemberId,List<String> ids) throws BusinessException {
    	Map<String,List<String>> mainData = new HashMap<String,List<String>>();
    	Map<String,List<String>> subData = new HashMap<String,List<String>>();
    	for(String item : ids){
    		String interfaceName = item.substring(item.lastIndexOf("_") + 1);
    		String subInterfaceName = "";
    		if(interfaceName.indexOf(",")!=-1) {
    			String[] interfaceNames = interfaceName.split(",");
    			interfaceName = interfaceNames[0];
    			subInterfaceName = interfaceNames[1];
    		}
    		String id = item.substring(0,item.lastIndexOf("_"));
    		
    		List d = mainData.get(interfaceName);
    		if(d == null){
    			d = new ArrayList<String>();
    			mainData.put(interfaceName,d);
    		}
    		d.add(id);
    		
    		if(Strings.isNotBlank(subInterfaceName)) {
        		List d2 = subData.get(subInterfaceName);
        		if(d2 == null){
        			d2 = new ArrayList<String>();
        			subData.put(subInterfaceName,d2);
        		}
        		d2.add(id);
    		}
    	}
    	
        for (MemberLeaveClearItemInterface m : this.HandItems) {
        	String className = m.getClass().getSimpleName();
        	int index = className.indexOf("$");
        	if(index > 0){ //Cap4MemberLeaveClearItemBussinessImpl$$EnhancerBySpringCGLIB$$a08e8d5b
        		className = className.substring(0,index);
        	}
        	
        	if(mainData.containsKey(className)){
        		List<String> id = mainData.get(className);
        		((AbstractMemberLeaveClearItem)m).updateAuthority(oldMemberId, newMemberId, id);        		
        	}
        	
        	if(subData.containsKey(className)){
        		List<String> id = subData.get(className);
        		((AbstractMemberLeaveClearItem)m).updateAuthority2(oldMemberId, newMemberId, id);        		
        	}
        }

    }
    
    @Override
    @AjaxAccess
    public FlipInfo queryAppLogs(FlipInfo fi, Map params) throws BusinessException {
    	Object memberId = params.get("memberId");
    	if(memberId == null){
    		return fi;
    	}
    	
    	List<AppLog> list = appLogManager.getAllAppLogsList(800, 890,null, null, null, null, null);
        if (Strings.isEmpty(list)) {
            return fi;
        }

        List<WebMemberLeaveAppLog> webAppLogList = new ArrayList<WebMemberLeaveAppLog>();
		Map<Long, String> accountNames = orgManager.getAllAccountShortNames();
		
    	for(AppLog log : list){
    		String leaveMemberId = log.getParam5();
    		if(memberId.equals(leaveMemberId)){
    			webAppLogList.add(getAllWebAppLog(log,accountNames));
    		}
    	}
    	
    	DBAgent.memoryPaging(webAppLogList, fi);
    	return fi;
    }
    
	/**
	 * 对查询得到的数据进行封装,用于前台列表显示
	 */
	private WebMemberLeaveAppLog getAllWebAppLog(AppLog appLog,Map<Long, String> accountNames) throws BusinessException {
		Long accountId = appLog.getActionAccountId();
		Long memberId = appLog.getActionUserId();

		String accountName = accountNames.get(accountId);
		
		String memberName = "";
		V3xOrgMember member = orgManager.getMemberById(memberId);
		if(member!=null) {
			memberName = member.getName();
			if(member.getIsAdmin()){
				memberName = OrgHelper.showMemberNameOnly(memberId);
				V3xOrgAccount account = orgManager.getAccountById(member.getOrgAccountId());
				if(orgManager.isAdministrator(member.getLoginName(), account)){
					memberName = account.getName()+memberName;
				}
			}
		}
		WebMemberLeaveAppLog webAppLog = new WebMemberLeaveAppLog();
		webAppLog.setId(appLog.getId());
		webAppLog.setAccount(accountName);
		webAppLog.setUser(memberName);
		webAppLog.setActionType(appLog.getActionType());
		webAppLog.setIpAddress(appLog.getIP());
		webAppLog.setActionDesc(appLog.getActionDesc());
		webAppLog.setModelName(appLog.getModuleName());
		webAppLog.setDepment("");
		webAppLog.setActionTime(appLog.getActionDate());
		webAppLog.setAppLog(appLog);
		return webAppLog;
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
	@Override
	@AjaxAccess
	public void saveLog(Long oldMemberId, Long newMemberId, String authNames, String categoryName) throws BusinessException{
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
				V3xOrgMember oldMember = orgManager.getMemberById(oldMemberId);
				if(oldMember != null){
					oldMemberName = oldMember.getName();
				}
			}
			
			if(newMemberId != null){
				V3xOrgMember newMember = orgManager.getMemberById(newMemberId);
				if(newMember != null){
					newMemberName = newMember.getName();
				}
			}
			String categoryShowName = ResourceUtil.getString("memberleave.Category."+categoryName);
			
			appLogManager.insertLog(user, actionId, actionUserName, oldMemberName, 
					categoryShowName, newMemberName, authNames, oldMemberId.toString());
		} catch (Exception e) {
			logger.error("交接日志记录异常", e);
		}
	}
	

}
