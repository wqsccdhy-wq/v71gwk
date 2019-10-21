/**
 *
 */
package com.seeyon.ctp.organization.selectpeople.manager;

import static com.seeyon.ctp.organization.bo.V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.ORGENT_TYPE_LEVEL;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.ORGENT_TYPE_MEMBER;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.ORGENT_TYPE_POST;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_Email;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_Mobile;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_NAME;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_id;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_isInternal;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.VIRTUAL_ACCOUNT_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.dubbo.RefreshInterfacesAfterUpdate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.addressbook.constants.AddressbookConstants;
import com.seeyon.apps.addressbook.manager.AddressBookManager;
import com.seeyon.apps.addressbook.po.AddressBookSet;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemInitializer;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.ORGENT_TYPE;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.CompareSortLevelId;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrgTypeIdBO;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.JoinOrgManagerDirect;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.manager.OuterWorkerAuthUtil;
import com.seeyon.ctp.organization.selectpeople.manager.SelectPeoplePanel.InitCacheType;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.annotation.AjaxAccess;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-10-13
 */
@ProcessInDataSource(name = DataSourceName.BASE)
public class SelectPeopleManagerImpl implements SelectPeopleManager, SystemInitializer {
	private static final Log log = LogFactory.getLog(SelectPeopleManagerImpl.class);

    private OrgManager           orgManager;
    private OrgCache             orgCache;
    private OrgManagerDirect     orgManagerDirect;
    private JoinOrgManagerDirect joinOrgManagerDirect;
    private AddressBookManager         addressBookManager;

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setOrgCache(OrgCache orgCache) {
        this.orgCache = orgCache;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }

    public void setJoinOrgManagerDirect(JoinOrgManagerDirect joinOrgManagerDirect) {
        this.joinOrgManagerDirect = joinOrgManagerDirect;
    }
    
    public AddressBookManager getAddressBookManager() {
		return addressBookManager;
	}

	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}



	//不做缓存
    private Map<String, Date> orgDate = new Hashtable<String, Date>();
	private Map<String, String> orgString = new Hashtable<String, String>();

	private Map<String, SelectPeoplePanel> panels = new HashMap<String, SelectPeoplePanel>();

	@Override
    @RefreshInterfacesAfterUpdate(inface = SelectPeoplePanel.class)
    public void initialize(){
	    List<SelectPeoplePanel> initCachePanels_G = new ArrayList<SelectPeoplePanel>();
	    List<SelectPeoplePanel> initCachePanels_A = new ArrayList<SelectPeoplePanel>();

        Map<String, SelectPeoplePanel> panels0 = AppContext.getBeansOfType(SelectPeoplePanel.class);
        for (String n : panels0.keySet()) {
            SelectPeoplePanel panel = panels0.get(n);
            this.panels.put(panel.getType(), panel);

            if(panel.getInitCacheType() == InitCacheType.Init_Global){
                initCachePanels_G.add(panel);
            }
            else if(panel.getInitCacheType() == InitCacheType.Init_Account){
                initCachePanels_A.add(panel);
            }
        }

		try {
			long startTime = System.currentTimeMillis();

			List<V3xOrgAccount> allAccounts = this.orgManager.getAllAccounts();
			Date time = new Date(0);

			//不区分单位的缓存
			this.getAllOrgEnt_Account(time);

			for (SelectPeoplePanel p : initCachePanels_G) {
                this.getExtendPanel(p, time, -1L, VIRTUAL_ACCOUNT_ID,"");
            }


			for (V3xOrgAccount account : allAccounts) {
				if(account.isGroup()){
					continue;
				}

				long accountId = account.getId();

				this.getAllOrgEnt_Department(time, accountId);
				this.getAllOrgEnt_Level(time, accountId);
				this.getAllOrgEnt_Member(time, accountId);
				this.getAllOrgEnt_Post(time, accountId);
				this.getConcurent(time, accountId);

				for (SelectPeoplePanel p : initCachePanels_A) {
                    this.getExtendPanel(p, time, -1L, accountId, "");
                }
			}

			log.info("初始化选人界面数据！" + (System.currentTimeMillis() - startTime) + " MS");
		}
		catch (Exception e) {
			log.error("", e);
		}
	}

    public void destroy(){

    }

	@Override
    public String getOrgModel(String timestamp, long loginAccountId, long memberId, String extParameters) throws BusinessException {
        Map<String, Date> map = new HashMap<String, Date>();
        if(StringUtils.isNotBlank(timestamp)){
            StringTokenizer timestamps = new StringTokenizer(timestamp, "=;");

            while (timestamps.hasMoreTokens()) {
                String key = timestamps.nextToken();
                Date value = new Date(new Long(timestamps.nextToken()));

                map.put(key, value);
            }
        }

        Date timestampDate = map.get("HeadModifyState");
        if(timestampDate == null){
            timestampDate = new Date(0L);
        }

        AppContext.putThreadContext("extParameters", extParameters);

		V3xOrgMember member = this.orgManager.getMemberById(memberId);

		V3xOrgAccount loginAccount = this.orgManager.getAccountById(loginAccountId);
		boolean isGroupAccount = loginAccount.isGroup();
		Long vJoinAllowAccountId = OrgHelper.getVJoinAllowAccount();

		StringBuilder result = new StringBuilder();
		result.append("{");
		// 加载单位
		String allAccount = getAllOrgEnt_Account(timestampDate);
		if (allAccount != null) {
			result.append(ORGENT_TYPE_ACCOUNT).append(" : ").append(allAccount).append(", ");
		}

		// 加载部门
		if(!isGroupAccount){
			String allDepartment = getAllOrgEnt_Department(timestampDate, loginAccountId);
			if (allDepartment != null) {
				result.append(ORGENT_TYPE_DEPARTMENT).append(" : ").append(allDepartment).append(", ");
			}
		}

		// 加载人员
		if(!isGroupAccount){
			String allMember = getAllOrgEnt_Member(timestampDate, loginAccountId);
			if (allMember != null) {
				result.append(ORGENT_TYPE.Member).append(" : ").append(allMember).append(", ");
			}
		}

		// 加载岗位
		String allPost = getAllOrgEnt_Post(timestampDate, loginAccountId);
		if (allPost != null) {
			result.append(ORGENT_TYPE.Post).append(" : ").append(allPost).append(", ");
		}
		
		// 加载岗位
		String allGuest = getAllOrgEnt_Guest(timestampDate, loginAccountId);
		if (allGuest != null) {
			result.append("Guest").append(" : ").append(allGuest).append(", ");
		}

		// 加载职务级别
		String allLevel = getAllOrgEnt_Level(timestampDate, loginAccountId);
		if (allLevel != null) {
			result.append(ORGENT_TYPE.Level).append(" : ").append(allLevel).append(", ");
		}

		// 加载组
		String allTeam = getAllOrgEnt_Team(timestampDate, loginAccountId, memberId);
		if (allTeam != null) {
			result.append(ORGENT_TYPE.Team).append(" : ").append(allTeam).append(", ");
		}

		//兼职	
		String concurent = getConcurent(timestampDate, loginAccountId);
		if(concurent != null){
			result.append("Concurent").append(" : ").append(concurent).append(", ");
		}

		String customPanels = "";
		for (String type : this.panels.keySet()) {
		    SelectPeoplePanel panel = this.panels.get(type);

	        Date d = map.get(panel.getType());
	        if(d == null){
	            d = new Date(0L);
	        }

		    String panelType = panel.getType();
		    if(panel.isCustom()){
		    	String customProperties = ((SelectPeoplePanel4Custom)panel).getCustomPanelProperties();
		    	customPanels = Strings.isBlank(customPanels) ? customProperties : customPanels+","+customProperties;
		    }
		    String data = this.getExtendPanel(panel, d, memberId, loginAccountId,extParameters);
		    if(data != null){
                result.append(panelType).append(" : ").append(data).append(", ");
            }
		    
		    String data2 = this.getExtendPanelAdditionalJson(panel, d, memberId, loginAccountId,extParameters);
		    if(data2 != null){
                result.append(panelType + "_Additional").append(" : ").append(data2).append(", ");
            }
        }
		result.append("customPanels").append(" : ").append("[\""+customPanels+"\"]").append(", ");

		//编外人员，取出我的工作范围
		if(member != null && member.isV5External()){
			String emw = getExternalMemberWorkScope(timestampDate, member.getId(), member.getOrgAccountId());
			if(Strings.isNotBlank(emw)){
				result.append("ExternalMemberWorkScope : ").append(emw).append(", ");
			}
		}
		
		//取出我能访问的内部部门
		List<V3xOrgDepartment> innerDepts = new UniqueList<V3xOrgDepartment>();
		//取出我能访问的vjoin部门
		List<V3xOrgDepartment> vjoinDepts = new UniqueList<V3xOrgDepartment>();
		
		//vjoin人员可以访问的vjoin外部单位（自己所在的外部外部单位+所在外部单位可以访问的其他外部单位）
		List<V3xOrgDepartment> vjMemberAccessVjAccounts = new UniqueList<V3xOrgDepartment>();
		
		//vjoin人员
		if(member != null && member.isVJoinExternal() && vJoinAllowAccountId != null){
			//取出我的可以访问的内部组织
			String vmw = getVjoinMemberWorkScope(timestampDate, member.getId(), vJoinAllowAccountId);
			if(Strings.isNotBlank(vmw)){
				result.append("VjoinMemberWorkScope : ").append(vmw).append(", ");
			}
			
			boolean isVjoinSubAdmin = orgManager.isRole(member.getId(), null, OrgConstants.Role_NAME.VjoinSubManager.name(), null);
			if(!member.getIsAdmin() && !isVjoinSubAdmin){
				List<V3xOrgDepartment> tempDepts = joinOrgManagerDirect.getAccessInnerDepts(member.getId(),Long.valueOf(vJoinAllowAccountId));
				for(V3xOrgDepartment dept : tempDepts){
					Long deptId = dept.getId();
					if(!innerDepts.contains(dept)){
						innerDepts.addAll(orgManager.getAllParentDepartments(deptId));
						innerDepts.add(dept);
					}
				}
				
				//能够访问的所有外部单位：自己所在的外部单位+可以访问的其他外部单位
				vjMemberAccessVjAccounts.add(orgManager.getDepartmentById(member.getOrgDepartmentId()));
				vjMemberAccessVjAccounts.addAll(joinOrgManagerDirect.getVjoinAccessDepartments(memberId));
				for(V3xOrgDepartment d : vjMemberAccessVjAccounts){
					vjoinDepts.addAll(joinOrgManagerDirect.getAllParentDepartments(d.getId()));
					vjoinDepts.add(d);
				}
			}else if(isVjoinSubAdmin){//子机构管理员
				List<V3xOrgDepartment> allInnerDept = orgManager.getAllInternalDepartments(vJoinAllowAccountId);
				innerDepts.addAll(allInnerDept);
				
				//子机构下的外部单位
				vjMemberAccessVjAccounts.addAll(joinOrgManagerDirect.getChildDepartments(member.getOrgDepartmentId(), false, OrgConstants.ExternalType.Interconnect2.ordinal()));
				
				List<V3xOrgDepartment> allSubVJoinDept = joinOrgManagerDirect.getChildDepartments(member.getOrgDepartmentId(), false, null);
				vjoinDepts.add(orgManager.getDepartmentById(member.getOrgDepartmentId()));
				vjoinDepts.addAll(allSubVJoinDept);
			}
		}
		
		//内部人员
		if(member != null && member.getIsInternal() && vJoinAllowAccountId != null){
			//取出我能访问的vjoin人员
			String iav = getInnerMemberAccessVjoinMember(timestampDate, member.getId(), vJoinAllowAccountId);
			if(Strings.isNotBlank(iav)){
				result.append("InnerMemberAccessVjoinMember : ").append(iav).append(", ");
			}
			
			//取出我能访问的vjoin部门
			vjoinDepts = joinOrgManagerDirect.getAccessVjoinDepts(memberId, null);

		}
		
		if(!Strings.isEmpty(innerDepts)){
			result.append("AccessInnerDepts : ").append(getAccessDept(timestampDate, member.getId(), vJoinAllowAccountId,innerDepts)).append(", ");
		}
		
		if(!Strings.isEmpty(vjoinDepts)){
			result.append("AccessVjoinDepts : ").append(getAccessDept(timestampDate, member.getId(), vJoinAllowAccountId,vjoinDepts)).append(", ");
		}
		
		if(!Strings.isEmpty(vjMemberAccessVjAccounts)){
			result.append("VjMemberAccessVjAccounts : ").append(getAccessDept(timestampDate, member.getId(), vJoinAllowAccountId,vjMemberAccessVjAccounts)).append(", ");
		}
		
		String extMembers = null;
		String vjoinMembers = null;

		/*
		 * 系统管理员、审计管理员能够访问任何单位的任何外部人员；
         * 管理员，能访问本单位的所有的外部人员
		 */
		if(orgManager.isSystemAdminById(memberId) || orgManager.isAuditAdminById(memberId)){
			//ignore
		} else if (orgManager.isAdministratorById(memberId, loginAccountId) 
				|| (AppContext.getCurrentUser().isAdministrator() && loginAccount.getExternalType().equals(OrgConstants.ExternalType.Interconnect3.ordinal()))) {
		    extMembers = getExtMemberScopeOfInternal(timestampDate, member.getId(), member.getOrgAccountId(),true);
		} else if(member != null && member.getIsInternal()){
			//内部人员，能访问哪些外部人员
			extMembers = getExtMemberScopeOfInternal(timestampDate, member.getId(), member.getOrgAccountId(),false);
			//内部人员，能访问哪些vjoin人员
			vjoinMembers = getVjoinMemberScopeOfInternal(timestampDate, member.getId(), member.getOrgAccountId());
		}

		if(Strings.isNotBlank(extMembers)){
			result.append("ExtMemberScopeOfInternal : ").append(extMembers).append(", ");
		}
		
		if(Strings.isNotBlank(vjoinMembers)){
			result.append("VjoinMemberScopeOfInternal : ").append(vjoinMembers).append(", ");
		}

		result.append("timestamp").append(" : \"").append(getModifiedTimeStamps(loginAccountId)).append("\"");

		result.append("}");

		AppContext.removeThreadContext("extParameters");

		return result.toString();
	}

	@Override
    public String getQueryOrgModel(String name, Boolean isNeedCheckLevelScope,Long accountId) throws BusinessException {
	    if(Strings.isBlank(name)) {
	        return "{}";//如果是空，直接返回
	    }
	    User user = AppContext.getCurrentUser();
	    String queryName = name.toLowerCase();
	    StringBuilder sb = new StringBuilder();
        sb.append("{");

        //外部人员
        if(user != null && !user.isInternal()){
            List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
            List<V3xOrgEntity> entities = orgManager.getExternalMemberWorkScope(user.getId(), false);

            for (V3xOrgEntity entity : entities) {
                List<V3xOrgMember> ms = this.orgManager.getMembersByType(entity.getEntityType(), entity.getId());

                for (V3xOrgMember member : ms) {
                    if (member.isValid()) {
                        members.add(member);
                    }
                }
            }
            this.prase2Member(members, sb, queryName, user, false);
            sb.append("}");
            return sb.toString();
        }

	    if(null == isNeedCheckLevelScope) isNeedCheckLevelScope = Boolean.TRUE;

        List<V3xOrgMember> allMembers = new ArrayList<V3xOrgMember>();
        if(accountId==null){

            //List<V3xOrgMember> members = orgManager.getAllMembers(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
        	List<V3xOrgEntity> members = (List<V3xOrgEntity>)orgManager.getEntityList(V3xOrgMember.class.getSimpleName(),"name",name,accountId);
            Map<Long,V3xOrgMember> membersMap = new HashMap<Long,V3xOrgMember>();
            // 查所有
            List<MemberPost> conMembers = orgManager.getAllConcurrentPostByAccount(null);
            List<V3xOrgAccount> accessableAccounts = orgManager.accessableAccounts(user.getId());
            Set<Long> accIds = new HashSet<Long>();

            for (V3xOrgAccount account : accessableAccounts) {
                if(account.isGroup()) continue;
                accIds.add(account.getId());
            }
            for(V3xOrgEntity m : members){
                membersMap.put(m.getId(), (V3xOrgMember)m);
                if(accIds.contains(m.getOrgAccountId())){
                    allMembers.add((V3xOrgMember)m);
                }
            }
            for(MemberPost mp : conMembers){
                if(accIds.contains(mp.getOrgAccountId())){
                    V3xOrgMember m = membersMap.get(mp.getMemberId());
                    if(m != null){
                        allMembers.add(m);
                    }
                }
            }

        } else {
            V3xOrgAccount account = orgManager.getAccountById(accountId);
            if (account.isGroup()) return "{}";//如果是空，直接返回;
            //List<V3xOrgMember> members = orgManager.getAllMembers(account.getId());
            List<V3xOrgEntity> members = (List<V3xOrgEntity>)orgManager.getEntityList(V3xOrgMember.class.getSimpleName(),"name",name,accountId);
            if (CollectionUtils.isNotEmpty(members)) {
            	for(V3xOrgEntity  m : members){
            		allMembers.add((V3xOrgMember)m);
            	}
            }
            
            List<MemberPost> conMembers = orgManager.getAllConcurrentPostByAccount(accountId);
            for(MemberPost mp : conMembers){
                V3xOrgMember m = orgManager.getMemberById(mp.getMemberId());
                if(m != null){
                	if(m.getName().toLowerCase().indexOf(queryName)>=0){
                		allMembers.add(m);
                	}
                    
                }
            }
        }
        this.prase2Member(allMembers, sb, queryName, user, isNeedCheckLevelScope);
        sb.append("}");
        return sb.toString();

	}
	
	@Override
	@AjaxAccess
    public String getQueryOrgModelByType(String name, Boolean isNeedCheckLevelScope,Long accountId,String type) throws BusinessException {
	    if(Strings.isBlank(name)) {
	        return "{}";//如果是空，直接返回
	    }
	    
	    if(OrgConstants.ORGENT_TYPE.Member.name().contentEquals(type)) {
	    	return getQueryOrgModel(name,isNeedCheckLevelScope,accountId);
	    }else if(OrgConstants.ORGENT_TYPE.Department.name().contentEquals(type)) {
	    	return getDepartmentModel(name,accountId,type);
	    }else if("JoinAccount".contentEquals(type)) {//vjoin 外部单位
	    	return getJoinAccountModel(name,type);
	    }else if("JoinMember".contentEquals(type)) {//vjoin人员
	    	return getJoinMemberModel(name,type);
	    }
	    return "{}";
	}
	
	//选人界面选中单位时的，搜索部门。按照名称搜索，不进行权限校验（权限在前端校验）
	private String getDepartmentModel(String name,Long accountId,String type) throws BusinessException {
	    if(Strings.isBlank(name) || accountId==null) {
	        return "{}";//如果是空，直接返回
	    }
	    
	    V3xOrgAccount account = orgManager.getAccountById(accountId);
	    if (account.isGroup()) return "{}";//如果是空，直接返回;
	    
	    String queryName = name.toLowerCase();
	    StringBuilder sb = new StringBuilder();
        sb.append("{");

        List<V3xOrgEntity> allDepartment = new ArrayList<V3xOrgEntity>();
        List<V3xOrgEntity> depts = (List<V3xOrgEntity>)orgManager.getEntityList(V3xOrgDepartment.class.getSimpleName(),"name",name,accountId);
        if (CollectionUtils.isNotEmpty(depts)) {
        	for(V3xOrgEntity  d : depts){
        		allDepartment.add(d);
        	}
        }
        
        this.prase2Entity(allDepartment, sb, queryName,type);
        sb.append("}");
        return sb.toString();

	}
	
	private String getJoinAccountModel(String name,String type) throws BusinessException {
	    if(Strings.isBlank(name)) {
	        return "{}";//如果是空，直接返回
	    }
	    
	    String queryName = name.toLowerCase();
	    StringBuilder sb = new StringBuilder();
        sb.append("{");

        List<V3xOrgEntity> allDepartment = new ArrayList<V3xOrgEntity>();
        Long accountId = joinOrgManagerDirect.getDefaultVjoinAccount(null);
        List<V3xOrgEntity> depts = orgManager.getEntityList(V3xOrgDepartment.class.getSimpleName(),"name",name,accountId);
        if (CollectionUtils.isNotEmpty(depts)) {
        	for(V3xOrgEntity  d : depts){
        		if(d.getExternalType() == OrgConstants.ExternalType.Interconnect1.ordinal() || d.getExternalType() == OrgConstants.ExternalType.Interconnect2.ordinal()) {
        			allDepartment.add(d);
        		}
        	}
        }
        
        this.prase2Entity(allDepartment, sb, queryName,type);
        sb.append("}");
        return sb.toString();
	}
	
	private String getJoinMemberModel(String name,String type) throws BusinessException {
	    if(Strings.isBlank(name)) {
	        return "{}";//如果是空，直接返回
	    }
	    
	    String queryName = name.toLowerCase();
	    StringBuilder sb = new StringBuilder();
        sb.append("{");

        List<V3xOrgEntity> allMember = new ArrayList<V3xOrgEntity>();
        Long accountId = joinOrgManagerDirect.getDefaultVjoinAccount(null);
        List<V3xOrgEntity> members = orgManager.getEntityList(V3xOrgMember.class.getSimpleName(),"name",name,accountId);
        if (CollectionUtils.isNotEmpty(members)) {
        	for(V3xOrgEntity  m : members){
        		if(m.getExternalType() == OrgConstants.ExternalType.Interconnect1.ordinal()) {
        			allMember.add(m);
        		}
        	}
        }
        
        this.prase2Entity(allMember, sb, queryName, type);
        sb.append("}");
        return sb.toString();
	}

    @Override
    public String getQueryOrgModel(String name, Boolean isNeedCheckLevelScope) throws BusinessException {
        return getQueryOrgModel(name, isNeedCheckLevelScope, null);
    }

    @Override
    public String getQueryOrgModel(String name) throws BusinessException{
        return getQueryOrgModel(name,(Long)null);
    }
	@Override
    public String getQueryOrgModel(String name,Long accountId) throws BusinessException {
	    if(Strings.isBlank(name)) {
	        return "{}";
	    }
		User user = AppContext.getCurrentUser();
		StringBuilder sb = new StringBuilder();
		sb.append("{");

		String queryName = name.toLowerCase();

		List<V3xOrgMember> allMembers = new ArrayList<V3xOrgMember>();
		if(accountId==null){
            List<V3xOrgAccount> accessableAccounts = orgManager.accessableAccounts(user.getId());
            for (V3xOrgAccount account : accessableAccounts) {
                if(account.isGroup()) continue;
                //List<V3xOrgMember> members = orgManager.getAllMembers(account.getId());
                List<V3xOrgEntity> members = (List<V3xOrgEntity>)orgManager.getEntityList(V3xOrgMember.class.getSimpleName(),"name",name,account.getId());
                if (CollectionUtils.isNotEmpty(members)) {
                	for(V3xOrgEntity  m : members){
                		allMembers.add((V3xOrgMember)m);
                	}
                }
            }
        } else {
            V3xOrgAccount account = orgManager.getAccountById(accountId);
            if (account.isGroup()) return "{}";//如果是空，直接返回;
            //List<V3xOrgMember> members = orgManager.getAllMembers(account.getId());
            List<V3xOrgEntity> members = (List<V3xOrgEntity>)orgManager.getEntityList(V3xOrgMember.class.getSimpleName(),"name",name,accountId);
            if (CollectionUtils.isNotEmpty(members)) {
            	for(V3xOrgEntity  m : members){
            		allMembers.add((V3xOrgMember)m);
            	}
            }
            
            List<MemberPost> conMembers = orgManager.getAllConcurrentPostByAccount(accountId);
            for(MemberPost mp : conMembers){
                V3xOrgMember m = orgManager.getMemberById(mp.getMemberId());
                if(m != null){
                	if(m.getName().toLowerCase().indexOf(queryName)>=0){
                		allMembers.add(m);
                	}
                    
                }
            }
        }

		this.prase2Member(allMembers, sb, queryName, user, true);

		sb.append("}");

		return sb.toString();
	}

	/**
	 * 加载组织模型的时间戳 timestamp
	 * 格式为
	 *
	 * @param entType
	 * @return
	 */
	private String getModifiedTimeStamps(long loginAccountId) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		sb.append("HeadModifyState=" + orgManager.getModifiedTimeStamp(loginAccountId).getTime());

		for (String type : this.panels.keySet()) {
            SelectPeoplePanel panel = this.panels.get(type);
            Date d = panel.getLastModifyTimestamp(loginAccountId);

            if(d != null){
                sb.append(";").append(panel.getType()).append("=").append(d.getTime());
            }
        }

		return sb.toString();
	}

	private String getAllOrgEnt_Account(Date time) throws BusinessException{
	    String key = ORGENT_TYPE_ACCOUNT + "_" + VIRTUAL_ACCOUNT_ID;

	    Date lastM = orgDate.get(key);

	    if (orgManager.isModified(lastM, VIRTUAL_ACCOUNT_ID)){
    		StringBuilder a = new StringBuilder();
    		a.append("[");

    		int i = 0;
    		List<V3xOrgAccount> allAccounts = orgManager.getAllAccounts();
    		List<V3xOrgAccount> joinAccounts = joinOrgManagerDirect.getAllAccounts();
            allAccounts.addAll(joinAccounts);
            
    		for (V3xOrgAccount account : allAccounts) {
    			if(i++ != 0){
    				a.append(",");
    			}
    			account.toJsonString(a,orgManager,orgCache);
    		}
    		a.append("]");

    		lastM = orgManager.getModifiedTimeStamp(VIRTUAL_ACCOUNT_ID);

    		this.orgString.put(key, a.toString());
    		this.orgDate.put(key, lastM);
	    }

	    // 前段数据时间戳与后段不一直，加载
	    if (!time.equals(lastM)) {
	        return this.orgString.get(key);
	    }

	    return null;
	}

	/**
	 * 加载部门信息
	 *
	 * @param loginAccountId
	 * @return
	 */
	private String getAllOrgEnt_Department(Date time, long loginAccountId) throws BusinessException {
		String key;// = ORGENT_TYPE_DEPARTMENT + "_" +loginAccountId;
        User user = AppContext.getCurrentUser();
        if(user == null){
        	return null;
        }
        V3xOrgMember member = orgManager.getMemberById(user.getId());
		if (member.isV5External()) { // 编外人员
			key = ORGENT_TYPE_DEPARTMENT + "_" + loginAccountId + "_" + user.getId();
		}else if(member.isVJoinExternal()){//vjoin 人员
			key = ORGENT_TYPE_DEPARTMENT + "_vjoin_" + loginAccountId + "_" + user.getId();
		}else { // 内部人员
			key = ORGENT_TYPE_DEPARTMENT + "_" + loginAccountId;
		}

        Date spLastM  = orgDate.get(key);
        Date orglastM = orgManager.getModifiedTimeStamp(loginAccountId);

        if (!Strings.equals(spLastM, orglastM)){
    		StringBuilder a = new StringBuilder();
    		a.append("[");

    		int i = 0;
    		List<V3xOrgDepartment> deps;
    		if (member.isV5External()) { // 外部人员
    			deps = OuterWorkerAuthUtil.getCanAccessDep(user.getId(), user.getDepartmentId(), user.getAccountId(), orgManager, true);
    		}else if(member.isVJoinExternal()){//vjoin 人员
    			
				deps = orgCache.getAllV3xOrgEntityNoClone(V3xOrgDepartment.class, loginAccountId);
				Collections.sort(deps, CompareSortEntity.getInstance());
				
/*				if(member.getIsAdmin()){
					deps = orgCache.getAllV3xOrgEntityNoClone(V3xOrgDepartment.class, loginAccountId);
					Collections.sort(deps, CompareSortEntity.getInstance());
				}else{
					deps = joinOrgManagerDirect.getAccessInnerDepts(user.getId(),Long.valueOf(loginAccountId));
				}*/
    		}else { // 内部人员
    			deps = orgCache.getAllV3xOrgEntityNoClone(V3xOrgDepartment.class, loginAccountId);
    			Collections.sort(deps, CompareSortEntity.getInstance());
    		}

    		Map<Long, List<V3xOrgPost>> accountPosts = orgManager.getAccountDeptPosts(loginAccountId);

    		for (V3xOrgDepartment department : deps) {
    		    if(!department.isValid()){
    		        continue;
    		    }
    			if(i++ != 0){
    				a.append(",");
    			}
    			if(accountPosts!=null){
    			    department.toJsonString(a, orgManager, accountPosts);
    			}else{
    			    department.toJsonString(a, orgManager);
    			}
    		}
    		a.append("]");

    		this.orgString.put(key, a.toString());
    		this.orgDate.put(key, orglastM);
		}

        // 前段数据时间戳与后段不一直，加载
        if (!time.equals(orglastM)) {
            return this.orgString.get(key);
        }

        return null;
	}

	private Object lockMember = new Object();
	
	/**
	 * 加载所有人员
	 *
	 * @param loginAccountId
	 * @return [{D:0,K:"3000740460148573035",N:"徐石",P:0,L:7},{D:1,K:"-2509390303980075869",N:"张三",P:1,L:8}]
	 */
	private String getAllOrgEnt_Member(Date time, long loginAccountId) throws BusinessException {
	    String key = ORGENT_TYPE_MEMBER + "_" +loginAccountId;

	    synchronized(lockMember){
            Date spLastM  = orgDate.get(key);
            Date orglastM = orgManager.getModifiedTimeStamp(loginAccountId);
    
            if (!Strings.equals(spLastM, orglastM)){
        		boolean needMobile = true; //TODO mobileMessageManager.isValidateMobileMessage();
    
        		List<MemberPost> secondPost = orgManager.getSecondPostByAccount(loginAccountId);
        		Map<Long, List<MemberPost>> secondPostMap = new HashMap<Long, List<MemberPost>>();
        		for (MemberPost s : secondPost) {
        		    Strings.addToMap(secondPostMap, s.getMemberId(), s);
                }
    
        		StringBuilder a = new StringBuilder();
        		a.append("[");
        		List<V3xOrgMember> members = orgCache.getAllV3xOrgEntityNoClone(V3xOrgMember.class, null);
        		Collections.sort(members, CompareSortEntity.getInstance());
    
        		int i = 0;
        		AddressBookSet addressBookSet = addressBookManager.getAddressbookSetByAccountId(loginAccountId);
        		if(members != null){
        			for (V3xOrgMember member : members) {
        			    if(!member.isValid()){
                            continue;
                        }
                        
        			    if(!Strings.equals(member.getOrgAccountId(), loginAccountId)){
        			        continue;
        			    }
        			    
        				if(i++ != 0){
        					a.append(",");
        				}
        				member.toJsonString(a, orgCache, needMobile, secondPostMap.get(member.getId()),addressBookSet);
        			}
        		}
    
        		a.append("]");
    
                this.orgString.put(key, a.toString());
                this.orgDate.put(key, orglastM);
            }
            
            // 前段数据时间戳与后段不一直，加载
            if (!time.equals(orglastM)) {
                return this.orgString.get(key);
            }
	    }

        return null;
	}

	/**
	 * 加载所有岗位
	 *
	 * @param myLoginDepartId
	 * @return
	 */
	private String getAllOrgEnt_Post(Date time, long loginAccountId) throws BusinessException {
		String key = ORGENT_TYPE_POST + "_" +loginAccountId;

        Date spLastM  = orgDate.get(key);
        Date orglastM = orgManager.getModifiedTimeStamp(loginAccountId);

        if (!Strings.equals(spLastM, orglastM)){
    		StringBuilder a = new StringBuilder();
    		a.append("[");

    		int i = 0;
    		List<V3xOrgPost> posts = orgCache.getAllV3xOrgEntityNoClone(V3xOrgPost.class, loginAccountId);
    		Collections.sort(posts, CompareSortEntity.getInstance());
    		for (V3xOrgPost post : posts) {
    		    if(!post.isValid()){
    		        continue;
    		    }
    		    
    			if(i++ != 0){
    				a.append(",");
    			}
    			post.toJsonString(a);
    		}
    		a.append("]");

            this.orgString.put(key, a.toString());
            this.orgDate.put(key, orglastM);
        }

        // 前段数据时间戳与后段不一直，加载
        if (!time.equals(orglastM)) {
            return this.orgString.get(key);
        }

        return null;
	}
	
	
	/**
	 * 加载所有特殊账号
	 *
	 * @param myLoginDepartId
	 * @return
	 */
	private String getAllOrgEnt_Guest(Date time, long loginAccountId) throws BusinessException {
		String key = "Guest" + "_" +loginAccountId;

        Date spLastM  = orgDate.get(key);
        Date orglastM = orgManager.getModifiedTimeStamp(loginAccountId);

        if (!Strings.equals(spLastM, orglastM)){
    		StringBuilder a = new StringBuilder();
    		a.append("[");

    		int i = 0;
    		List<V3xOrgMember> guests = new UniqueList<V3xOrgMember>();
    		//登录前guest账号总是显示。
    		V3xOrgMember defaultGuest = orgManager.getMemberById(OrgConstants.GUEST_ID);
    		if(defaultGuest != null && defaultGuest.isValid()){
    			guests.add(defaultGuest);
    		}
    		List<V3xOrgMember> specials= orgCache.getAllV3xOrgEntityNoClone(V3xOrgMember.class, loginAccountId,OrgConstants.ExternalType.Interconnect2.ordinal());
    		Collections.sort(specials, new Comparator<V3xOrgMember>() {
    			public int compare(V3xOrgMember m1, V3xOrgMember m2) {
                      return m1.getCreateTime().compareTo(m2.getCreateTime());
    			}
    		});
    		guests.addAll(specials);
    		
    		for (V3xOrgMember guest : guests) {
    		    if(!guest.isValid() || !guest.isGuest()){
    		        continue;
    		    }
    		    
    			if(i++ != 0){
    				a.append(",");
    			}
    			guest.toGuestJsonString(a);
    		}
    		a.append("]");

            this.orgString.put(key, a.toString());
            this.orgDate.put(key, orglastM);
        }

        // 前段数据时间戳与后段不一直，加载
        if (!time.equals(orglastM)) {
            return this.orgString.get(key);
        }

        return null;
	}

	/**
	 * 加载所有职务级别
	 *
	 * @param myLoginDepartId
	 * @return
	 */
	private String getAllOrgEnt_Level(Date time, long loginAccountId) throws BusinessException {
		String key = ORGENT_TYPE_LEVEL + "_" +loginAccountId;

        Date spLastM  = orgDate.get(key);
        Date orglastM = orgManager.getModifiedTimeStamp(loginAccountId);

        if (!Strings.equals(spLastM, orglastM)){
    		StringBuilder a = new StringBuilder();
    		a.append("[");

    		int i = 0;
    		List<V3xOrgLevel> levels = orgCache.getAllV3xOrgEntityNoClone(V3xOrgLevel.class, loginAccountId);
    		Collections.sort(levels, CompareSortLevelId.getInstance());
    		for (V3xOrgLevel level : levels) {
    		    if(!level.isValid()){
                    continue;
                }
    		    
    			if(i++ != 0){
    				a.append(",");
    			}
    			level.toJsonString(a);
    		}
    		a.append("]");

            this.orgString.put(key, a.toString());
            this.orgDate.put(key, orglastM);
        }

        // 前段数据时间戳与后段不一直，加载
        if (!time.equals(orglastM)) {
            return this.orgString.get(key);
        }

        return null;
	}

	/**
	 * 加载所有组
	 *
	 * @param myLoginDepartId
	 * @return
	 */
	private String getAllOrgEnt_Team(Date time, long loginAccountId, long memberId) throws BusinessException {
		if(!orgManager.isModified(time, loginAccountId)) { // 前段数据时间戳与后段一直，不用加载
		    return null;
		}

		StringBuilder a = new StringBuilder();
		a.append("[");

		List<V3xOrgTeam> teams = orgManager.getTeamsByMember(memberId, loginAccountId);

		Collections.sort(teams, new Comparator<V3xOrgTeam>() {
			public int compare(V3xOrgTeam c1, V3xOrgTeam c2) {
				//type: 1个人组; 3项目组; 2系统组; 4讨论组
				int type1 = c1.getType();
				int type2 = c2.getType();

                if (type1 == 1) type1 = -2;
                if (type1 == 3) type1 = -1;
                if (type2 == 1) type2 = -2;
                if (type2 == 3) type2 = -1;

				if(type1 == type2){
                  if ((c1.getSortId() == null) && (c2.getSortId() == null)) {
                    return 0;
				}
                  if (c1.getSortId() == null) {
                    return 1;
                  }
                  if (c2.getSortId() == null) {
                    return -1;
				}
                  Long id1 = c1.getSortId().longValue();
                  Long id2 = c2.getSortId().longValue();
                  if(id1==id2){
                	  return c1.getId().compareTo(c2.getId());
                  }else{
                	  return id1.compareTo(id2);
                  }
                }
                return type1 < type2 ? -1 : 1;
			}
		});

		int i = 0;
		for (V3xOrgTeam team : teams) {
			if(team.isValid()){
				if(i++ != 0){
					a.append(",");
				}

				team.toJsonString(a, this.orgManager, loginAccountId);
			}
		}
		a.append("]");

		return a.toString();
	}

	/**
	 * 兼职人员
	 *
	 * @param time
	 * @param loginAccountId
	 * @return
	 */
	public String getConcurent(Date time, long loginAccountId) throws BusinessException{
		String key = "Concurent_" +loginAccountId;

        Date spLastM  = orgDate.get(key);
        Date orglastM = orgManager.getModifiedTimeStamp(loginAccountId);

        if (!Strings.equals(spLastM, orglastM)){
    		boolean needMobile = true;//TODO mobileMessageManager.isValidateMobileMessage();

    		StringBuilder o = new StringBuilder();
    		o.append("{");

    		int i = 0;

    		Map<Long, List<MemberPost>> concurents = this.orgManager.getConcurentPosts(loginAccountId);
    		AddressBookSet addressBookSet = addressBookManager.getAddressbookSetByAccountId(loginAccountId);
    		if(concurents != null && !concurents.isEmpty()){
    			Set<Map.Entry<Long, List<MemberPost>>> set = concurents.entrySet();
    			for (Map.Entry<Long, List<MemberPost>> entry : set) {
    				if(i++ != 0){
    					o.append(",");
    				}

    				o.append("\"" + entry.getKey() + "\":");

    				o.append("[");

    				List<MemberPost> concurrentPosts = entry.getValue();
    				Long sortId = 0L;
                    Map<Long,Long> m = new HashMap<Long,Long>();
                    Set<Long> s = new HashSet<Long>();
                    for (MemberPost c : concurrentPosts) {
                        if (s.contains(c.getMemberId())) {
                            sortId = m.get(c.getMemberId());
                            if (sortId > c.getSortId()) {
                                sortId = c.getSortId();
                            }
                            m.put(c.getMemberId(), sortId);
                        } else {
                            s.add(c.getMemberId());
                            m.put(c.getMemberId(), c.getSortId());
                        }
                    }
    				//同一个部门下的兼职人员，排序号用同一个，都用最小的那个。保证在选人界面一个部门下只显示一个人的信息。如一个人兼职到研发部，有两个岗位信息，就显示成一条：（兼）开发经理（兼）架构师
					int j = 0;
    				for (MemberPost c : concurrentPosts) {
    				    long id = c.getMemberId();

    				    V3xOrgMember member = this.orgManager.getMemberById(id);
    				    if(member == null || !member.isValid()){
    				        continue;
    				    }

    					if(j++ != 0){
    						o.append(",");
    					}
    					o.append("{");

    					o.append(TOXML_PROPERTY_id).append(":\"").append(id).append("\"");
    					o.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(member.getName())).append("\"");
    					if(c.getPostId() != null){
    						o.append(",P:\"").append(String.valueOf(c.getPostId())).append("\""); //兼职岗位id
    					}
    					sortId = m.get(c.getMemberId());
    					o.append(",S:").append(sortId); //在兼职单位的排序号

    					o.append(",A:\"").append(member.getOrgAccountId()).append("\""); //原单位id
    					if(c.getLevelId() != null){
    						o.append(",L:\"").append(String.valueOf(c.getLevelId())).append("\""); //在兼职单位的职务级别
    					}

    					V3xOrgDepartment d = this.orgManager.getDepartmentById(member.getOrgDepartmentId());;

    					if(d != null){
    						o.append(",DN:\"").append(Strings.escapeJavascript(d.getName())).append("\""); //原部门名称
    					}

    			        String emailAddress = member.getEmailAddress();
    			        String telNumber = member.getTelNumber();

    					if(Strings.isNotBlank(emailAddress)){
    						o.append(",").append(TOXML_PROPERTY_Email).append(":\"").append(Strings.escapeJavascript(emailAddress)).append("\"");
    					}
    					if(needMobile && Strings.isNotBlank(telNumber)){
    		                if (!addressBookManager.checkPhone(AppContext.currentUserId(), member.getId(), loginAccountId, addressBookSet)) {
    		                    o.append(",").append(TOXML_PROPERTY_Mobile).append(":\"").append(AddressbookConstants.ADDRESSBOOK_INFO_REPLACE).append("\"");
    		                }else{
    		                	o.append(",").append(TOXML_PROPERTY_Mobile).append(":\"").append(Strings.escapeJavascript(telNumber)).append("\"");
    		                }
    					}

    					o.append("}");
    				}

    				o.append("]");
    			}
    		}

    		o.append("}");

            this.orgString.put(key, o.toString());
            this.orgDate.put(key, orglastM);
        }

        // 前段数据时间戳与后段不一直，加载
        if (!time.equals(orglastM)) {
            return this.orgString.get(key);
        }

        return null;
	}

    private String getExtendPanel(SelectPeoplePanel panel, Date time, long memberId, final long loginAccountId0, String extParameters) throws BusinessException {
    	V3xOrgMember member = orgManager.getMemberById(memberId);
        long loginAccountId = (panel.getInitCacheType() == InitCacheType.Init_Global) ? VIRTUAL_ACCOUNT_ID : loginAccountId0;
        String local = AppContext.getLocale().toString();
        String type = panel.getType();
        String key = type + "_" + loginAccountId + "_" + local;
        if("JoinDepartment".equals(type)){
             key = type + "_" + loginAccountId + "_" +memberId +"_"+local;
        }
        
        if("BusinessDepartment".equals(type) && !member.getIsInternal()) {
        	key = type + "_" + loginAccountId + "_" +memberId +"_"+local;
        }

        Date lastM1 =  panel.getLastModifyTimestamp(loginAccountId);

        //不用缓存，每次都是新的
        if(lastM1 == null){
            return panel.getJsonString(memberId, loginAccountId, extParameters);
        }

        Date lastM = orgDate.get(key);

        if(!Strings.equals(lastM, lastM1)){
            String a = panel.getJsonString(memberId, loginAccountId, extParameters);

            this.orgString.put(key, a);
            this.orgDate.put(key, lastM1);
        }

        // 前段数据时间戳与后段不一直，加载
        if (!time.equals(lastM1)) {
            return this.orgString.get(key);
        }

        return null;
    }
    
    private String getExtendPanelAdditionalJson(SelectPeoplePanel panel, Date time, long memberId, final long loginAccountId0, String extParameters) throws BusinessException {
        long loginAccountId = (panel.getInitCacheType() == InitCacheType.Init_Global) ? VIRTUAL_ACCOUNT_ID : loginAccountId0;
        String local = AppContext.getLocale().toString();
        String type = panel.getType();
        String key = type + "_" + loginAccountId + "_" +memberId + "_" + local + "_Additional";

        Date lastM1 =  panel.getLastModifyTimestamp(loginAccountId);

        //不用缓存，每次都是新的
        if(lastM1 == null){
            return panel.getAdditionalJsonString(memberId, loginAccountId, extParameters);
        }

        Date lastM = orgDate.get(key);

        if(!Strings.equals(lastM, lastM1)){
            String a = panel.getAdditionalJsonString(memberId, loginAccountId, extParameters);
            if(a == null){
            	return null;
            }
            this.orgString.put(key, a);
            this.orgDate.put(key, lastM1);
        }

        // 前段数据时间戳与后段不一直，加载
        if (!time.equals(lastM1)) {
            return this.orgString.get(key);
        }

        return null;
    }

	/**
	 * 系统管理员，可以访问所有单位的外部人员
	 * @param time
	 * @param memberId
	 * @param accountId
	 * @return
	 */
	private String getAllAccountsExtMember(Date time, long accountId) throws BusinessException{
		if(!orgManager.isModified(time, accountId)){
		    return null;
		}

		Map<Long, List<Long>> ms = new HashMap<Long, List<Long>>();
		List<V3xOrgMember> ws = orgManager.getAllAccountsExtMember(false);

		StringBuilder a = new StringBuilder();
		a.append("{");

		if(ws != null && !ws.isEmpty()){
			for (V3xOrgMember l : ws) {
				Strings.addToMap(ms, l.getOrgDepartmentId(), l.getId());
			}

			int i = 0;
			for (Map.Entry<Long, List<Long>> l : ms.entrySet()) {
				if(i++ != 0){
					a.append(",");
				}

				a.append("\"").append(l.getKey()).append("\":[");
				int j = 0;
				for (Long eMemberId : l.getValue()) {
					if(j++ != 0){
						a.append(",");
					}
					a.append("\"").append(eMemberId).append("\"");
				}
				a.append("]");
			}
		}

		a.append("}");

		return a.toString();
	}

	/**
	 * 内部人员能访问哪些外部人员
	 * @param time
	 * @param memberId
	 * @param accountId
	 * @return
	 */
	private String getExtMemberScopeOfInternal(Date time, long memberId, long accountId, boolean isAdministrator) throws BusinessException{
	    // OA-64509 去除内部访问内部人员范围的时间戳
//		if(!orgManager.isModified(time, accountId)){
//		    return null;
//		}
		Map<Long, List<Long>> ms = new HashMap<Long, List<Long>>();
		List<V3xOrgMember> ws = null;
		if(isAdministrator) {
		    ws = orgManager.getAllExtMembers(accountId);
		}else {
		    ws = orgManager.getMemberWorkScopeForExternal(memberId, false);
		}

		StringBuilder a = new StringBuilder();
		a.append("{");

		if(ws != null && !ws.isEmpty()){
			for (V3xOrgMember l : ws) {
				Strings.addToMap(ms, l.getOrgDepartmentId(), l.getId());
			}

			int i = 0;
			for (Map.Entry<Long, List<Long>> l : ms.entrySet()) {
				if(i++ != 0){
					a.append(",");
				}

				a.append("\"").append(l.getKey()).append("\":[");
				int j = 0;
				for (Long eMemberId : l.getValue()) {
					if(j++ != 0){
						a.append(",");
					}
					a.append("\"").append(eMemberId).append("\"");
				}
				a.append("]");
			}
		}

		a.append("}");

		return a.toString();
	}
	
	/**
	 * 内部人员能访问哪些vjoin人员
	 * @param time
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	private String getVjoinMemberScopeOfInternal(Date time, long memberId, long accountId) throws BusinessException{
		Map<Long, List<Long>> ms = new HashMap<Long, List<Long>>();
		List<V3xOrgMember> ws = joinOrgManagerDirect.getAccessExternalMembers(memberId);

		StringBuilder a = new StringBuilder();
		a.append("{");

		if(ws != null && !ws.isEmpty()){
			for (V3xOrgMember l : ws) {
				Strings.addToMap(ms, l.getOrgAccountId(), l.getId());
			}

			int i = 0;
			for (Map.Entry<Long, List<Long>> l : ms.entrySet()) {
				if(i++ != 0){
					a.append(",");
				}

				a.append("\"").append(l.getKey()).append("\":[");
				int j = 0;
				for (Long eMemberId : l.getValue()) {
					if(j++ != 0){
						a.append(",");
					}
					a.append("\"").append(eMemberId).append("\"");
				}
				a.append("]");
			}
		}

		a.append("}");

		return a.toString();
	}

	/**
	 * 外部人员，能访问哪些内部人员
	 *
	 * @param time
	 * @param memberId
	 * @param accountId
	 * @return
	 */
	private String getExternalMemberWorkScope(Date time, long memberId, long accountId) throws BusinessException{
		if(!orgManager.isModified(time, accountId)){
		    return null;
		}

		List<V3xOrgEntity> ws = orgManager.getExternalMemberWorkScope(memberId, false);

		StringBuilder a = new StringBuilder();
		a.append("[");

		if(ws != null && !ws.isEmpty()){
			int i = 0;
			for (V3xOrgEntity l : ws) {
				if(l instanceof V3xOrgAccount){//单位
					return "[\"A\"]";
				}
				else if(l instanceof V3xOrgDepartment){
					if(i++ != 0){
						a.append(",");
					}

					a.append("\"D").append(((V3xOrgDepartment)l).makeLiushuihao()).append("\"");
				}
				else if(l instanceof V3xOrgMember){
					if(i++ != 0){
						a.append(",");
					}

					a.append("\"M").append(((V3xOrgMember)l).getId()).append("\"");
				}
			}
		}

		a.append("]");

		return a.toString();
	}
	
	/**
	 * vjoin 人员可以访问那些内部组织
	 * 部门---》部门
	 * 人员，岗位，职务级别，组---》人员
	 * @param time
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	private String getVjoinMemberWorkScope(Date time, long memberId, long accountId) throws BusinessException{
		if(!orgManager.isModified(time, accountId)){
		    return null;
		}
		List<V3xOrgMember> members = new UniqueList<V3xOrgMember>();
		if(orgManager.isRole(memberId, null, OrgConstants.Role_NAME.VjoinSubManager.name(), null)){//子机构管理员
			StringBuilder a = new StringBuilder();
			a.append("[");
			members = orgManager.getAllMembers(OrgHelper.getVJoinAllowAccount());
			if(Strings.isNotEmpty(members)){
				int i = 0;
				for (V3xOrgEntity l : members) {
					if(i++ != 0){
						a.append(",");
					}
					
					a.append("\"M").append(((V3xOrgMember)l).getId()).append("\"");
				}
			}
			
			a.append("]");
			return a.toString();
		}
		
		
     	List<Long> relationShipIds = orgCache.getAccessMemberOrgRelationshipIds(memberId);
     	List<V3xOrgDepartment> depts = new UniqueList<V3xOrgDepartment>();
     	for (Long id : relationShipIds) {
     		V3xOrgRelationship orgRelationship = orgCache.getV3xOrgRelationshipById(id);
     		String entityType = orgRelationship.getObjective6Id();
     		String include = orgRelationship.getObjective7Id();
 			V3xOrgEntity m = orgManager.getEntityById(OrgHelper.getV3xClass(entityType), orgRelationship.getObjective0Id());
    		if (m != null && m.isValid()) {
    			if (m instanceof V3xOrgMember) {
    				members.add((V3xOrgMember) m);
    			} else if (m instanceof V3xOrgDepartment) {
    				members.addAll(orgManager.getMembersByDepartment(m.getId(), "1".equals(include)));
    				depts.add((V3xOrgDepartment)m);
    				if(!"1".equals(include)){
    					depts.addAll(orgManager.getChildDepartments(m.getId(), false));
    				}
    			} else if (m instanceof V3xOrgPost) {
    				members.addAll(orgManager.getMembersByPost(m.getId()));
    			} else if (m instanceof V3xOrgLevel) {
    				members.addAll(orgManager.getMembersByLevel(m.getId()));
    			}else if (m instanceof V3xOrgTeam) {
    				members.addAll(orgManager.getMembersByTeam(m.getId(), new HashSet<Long>()));
    			}else if (m instanceof V3xOrgAccount) {
//    				members.addAll(orgManager.getAllMembers(m.getId()));
//    				depts.addAll(orgManager.getChildDeptsByAccountId(m.getId(), false));
    				return "[\"A\"]";
    			}
    		}
 		}

		StringBuilder a = new StringBuilder();
		a.append("[");
		
		if(Strings.isNotEmpty(depts) || Strings.isNotEmpty(members)){
			int i = 0;
			for (V3xOrgEntity l : depts) {
				if(i++ != 0){
					a.append(",");
				}
				a.append("\"D").append(((V3xOrgDepartment)l).makeLiushuihao()).append("\"");
			}
			
			for (V3xOrgEntity l : members) {
				if(i++ != 0){
					a.append(",");
				}

				a.append("\"M").append(((V3xOrgMember)l).getId()).append("\"");
			}
		}

		a.append("]");

		return a.toString();
	}
	
	/**
	 * 内部人员可以访问的vjoin人员
	 * @param time
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	private String getInnerMemberAccessVjoinMember(Date time, long memberId, long accountId) throws BusinessException{
     	List<V3xOrgMember> members = joinOrgManagerDirect.getAccessExternalMembers(memberId);

		StringBuilder a = new StringBuilder();
		a.append("[");
		
		if(Strings.isNotEmpty(members)){
			int i = 0;
			
			for (V3xOrgMember m : members) {
				if(i++ != 0){
					a.append(",");
				}

				a.append("\"M").append(m.getId()).append("\"");
			}
		}

		a.append("]");

		return a.toString();
	}
	
	private String getAccessDept(Date time, long memberId, long accountId, List<V3xOrgDepartment> depts) throws BusinessException{
		StringBuilder a = new StringBuilder();
		a.append("[");
		
		if(Strings.isNotEmpty(depts)){
			int i = 0;
			
			for (V3xOrgDepartment d : depts) {
				if(i++ != 0){
					a.append(",");
				}

				a.append("\"D").append(d.getId()).append("\"");
			}
		}

		a.append("]");

		return a.toString();
	}

    public Map<String, Object> saveAsTeam(String name, String memberIdStr) throws BusinessException{
        Map<String, Object> result = new HashMap<String, Object>();

        User user = AppContext.getCurrentUser();

        V3xOrgMember member = orgManager.getMemberById(user.getId());
        long accountId = member.getOrgAccountId();
        Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgTeam.class.getSimpleName(), accountId);

        V3xOrgTeam team = new V3xOrgTeam();
        team.setIdIfNew();
        team.setName(name);
        team.setType(OrgConstants.TEAM_TYPE.PERSONAL.ordinal());
        team.setOwnerId(user.getId());
        team.setOrgAccountId(user.getLoginAccount());
        team.setSortId(Long.valueOf(maxSortNum + 1L));

        String[] memberIds = memberIdStr.split("[,]");
        for (String m : memberIds) {
            team.addTeamMember(new OrgTypeIdBO(Long.parseLong(m),OrgConstants.ORGENT_TYPE.Member.name()), OrgConstants.TeamMemberType.Member.ordinal());
        }

        OrgHelper.notUpdateModifyTimestampOfCurrentThread();
        
        /**
         * 重名比较原则
         * 1.同一个单位（集团）下的组比较
         * 2.除了个人组，其他组之间名称不能重复
         * 3.不同人的个人组名称可以重复
         */
      	List<V3xOrgTeam> allEnts = orgManager.getAllTeams(team.getOrgAccountId());
      	for(V3xOrgTeam ent : allEnts){
      		int type = ent.getType();
  			//个人组只和自己创建的个人组之间进行比较
  			if(OrgConstants.TEAM_TYPE.PERSONAL.ordinal() == type && name.equals(ent.getName()) && team.getOwnerId().equals(ent.getOwnerId())){
  				result.put("RepeatName", true);
  				return result;
  			}
      	}
        
        this.orgManagerDirect.addTeam(team);

        result.put("TeamId", team.getId());

        return result;
    }

    public String parseElements(String originalDataValue){
        return "[WLCCYBD-V5]" + Strings.escapeNULL(OrgHelper.parseElements(originalDataValue), "" );
    }

    private void prase2Member(List<V3xOrgMember>allMembers, StringBuilder sb , String queryName, User user, boolean isNeedCheckLevelScope) throws BusinessException{
        if (CollectionUtils.isNotEmpty(allMembers)) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(ORGENT_TYPE_MEMBER).append(" : ").append("[");
            boolean needMobile = true;//TODO mobileMessageManager.isValidateMobileMessage();
            int i = 0;
            Map<Long,AddressBookSet> map = new HashMap<Long, AddressBookSet>();
            for (V3xOrgMember member : allMembers) {
                if(null == member.getName()) continue;//OA-49815
                if (member.getName().toLowerCase().indexOf(queryName) != -1) {
                    if (isNeedCheckLevelScope && !user.isAdmin() && !OrgHelper.checkLevelScope(user.getId(), member.getId())) {
                        continue;
                    }
                    if (i++ != 0) {
                        sb2.append(",");
                    }
                    sb2.append("{");
                    sb2.append(TOXML_PROPERTY_id).append(":\"").append(member.getId()).append("\"");
                    sb2.append(",S:").append(member.getSortId());
                    sb2.append(",P:\"").append(member.getOrgPostId()).append("\"");
                    V3xOrgPost thePost = orgManager.getPostById(member.getOrgPostId());
                    sb2.append(",PM:\"").append(thePost==null?"":Strings.escapeJavascript(thePost.getName())).append("\"");
                    sb2.append(",L:\"").append(member.getOrgLevelId()).append("\"");
                    sb2.append(",D:\"").append(member.getOrgDepartmentId()).append("\"");
                    String deptName = "";
                    String deptNameF = "";
                    V3xOrgDepartment dept = orgManager.getDepartmentById(member.getOrgDepartmentId());
                    if (dept != null) {
                        deptName = dept.getName();
                        deptNameF = OrgHelper.deptPName(dept.getId());
                    }
                    sb2.append(",DM:\"").append(Strings.escapeJavascript(deptName)).append("\"");
                    sb2.append(",DF:\"").append(Strings.escapeJavascript(deptNameF)).append("\"");
                    sb2.append(",A:\"").append(member.getOrgAccountId()).append("\"");

                    if (!member.getIsInternal()) {
                        sb2.append(",").append(TOXML_PROPERTY_isInternal).append(":0");
                    }
                    sb2.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(member.getName())).append("\"");

                    String emailAddress = member.getEmailAddress();
                    String telNumber = member.getTelNumber();

                    if (Strings.isNotBlank(emailAddress)) {
                        sb2.append(",").append(TOXML_PROPERTY_Email).append(":\"").append(Strings.escapeJavascript(emailAddress)).append("\"");
                    }
                    if (needMobile && Strings.isNotBlank(telNumber)) {
                    	AddressBookSet addressBookSet = new AddressBookSet();
                    	if(map.containsKey(member.getOrgAccountId())){
                    		addressBookSet = map.get(member.getOrgAccountId());
                    	}else{
                    		addressBookSet = addressBookManager.getAddressbookSetByAccountId(member.getOrgAccountId());
                    		map.put(member.getOrgAccountId(), addressBookSet);
                    	}
		                if (!addressBookManager.checkPhone(AppContext.currentUserId(), member.getId(), member.getOrgAccountId(), addressBookSet)) {
		                    sb2.append(",").append(TOXML_PROPERTY_Mobile).append(":\"").append(AddressbookConstants.ADDRESSBOOK_INFO_REPLACE).append("\"");
		                }else{
		                	sb2.append(",").append(TOXML_PROPERTY_Mobile).append(":\"").append(Strings.escapeJavascript(telNumber)).append("\"");
		                }
                    }
                    if (!Strings.isEmpty(member.getSecond_post())) {
                        sb2.append(",F:[");
                        int j = 0;
                        for (MemberPost memberPost : member.getSecond_post()) {
                            V3xOrgDepartment dep = orgManager.getDepartmentById(memberPost.getDepId());
                            if (dep != null) {
                                V3xOrgPost post = orgManager.getPostById(memberPost.getPostId());
                                if (post != null) {
                                    if (j++ != 0) {
                                        sb2.append(",");
                                    }
                                    sb2.append("[");
                                    sb2.append(dep.getId());
                                    sb2.append(",");
                                    sb2.append(post.getId());
                                    sb2.append("]");
                                }
                            }
                        }
                        sb2.append("]");
                    }
                    sb2.append("}");
                }
            }
            sb2.append("]");
            sb.append(sb2);
        }
    }
    
    //只返回id，实体对象从前端获取
    private void prase2Entity(List<V3xOrgEntity> allEntitys, StringBuilder sb ,String queryName, String type) throws BusinessException{
        if (CollectionUtils.isNotEmpty(allEntitys)) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(type).append(" : ").append("[");
            int i = 0;
            for (V3xOrgEntity dept : allEntitys) {
                if(null == dept.getName()) continue;
                if (dept.getName().toLowerCase().indexOf(queryName) != -1) {
                    if (i++ != 0) {
                        sb2.append(",");
                    }
                    sb2.append("{");
                    sb2.append(TOXML_PROPERTY_id).append(":\"").append(dept.getId()).append("\"");
                    sb2.append("}");
                }
            }
            sb2.append("]");
            sb.append(sb2);
        }
    }
    
    public SelectPeoplePanel getPanel(String panel) {
        return this.panels.get(panel);
    }

}