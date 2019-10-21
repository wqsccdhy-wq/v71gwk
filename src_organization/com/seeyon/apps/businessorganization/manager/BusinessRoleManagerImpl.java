package com.seeyon.apps.businessorganization.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.manager.BusinessOrgManagerDirect;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.po.OrgRole;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.StringUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;

@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator,Role_NAME.BusinessOrganizationManager})
@ProcessInDataSource(name = DataSourceName.BASE)
public class BusinessRoleManagerImpl implements BusinessRoleManager {
	private final static Log logger = LogFactory.getLog(BusinessRoleManagerImpl.class);
	protected OrgCache orgCache;
    private BusinessOrgManagerDirect businessOrgManagerDirect;
    private OrgDao orgDao;
    private OrgManager orgManager;
    protected PrincipalManager principalManager;
    protected AppLogManager       appLogManager;
    private OrgManagerDirect orgManagerDirect;
	
    public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}
	public void setBusinessOrgManagerDirect(
			BusinessOrgManagerDirect businessOrgManagerDirect) {
		this.businessOrgManagerDirect = businessOrgManagerDirect;
	}
	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	
    @SuppressWarnings("unchecked")
    @Override
    @AjaxAccess
    public FlipInfo getRoleList(FlipInfo fi, Map param) throws BusinessException {
        Long accountId = Long.parseLong(param.get("accountId").toString());
        if (param.get("name") != null) {
            String searchRoleName = String.valueOf(param.get("name"));
            param.put("name", processRoleName(searchRoleName, accountId));
        }
        List<V3xOrgRole> roleList = new ArrayList<V3xOrgRole>();
        // 查询条件为空的情况直接从缓存返回
        if (param.size() == 1) {
            roleList = orgCache.getAllV3xOrgEntity(V3xOrgRole.class, accountId);
        } else {
            // 存在查询提交时从数据库返回
            List<OrgRole> list = orgDao.getAllRolePO(accountId, null, param, null);
            for (int i = 0; i < list.size(); i++) {
                OrgRole role = list.get(i);
                if (null != role) {
                    roleList.add(new V3xOrgRole(role));
                }
            }
        }
        // 排序
        Collections.sort(roleList, CompareSortEntity.getInstance());
        // 分页
        DBAgent.memoryPaging(roleList, fi);
        return fi;
    }
    
    /**
     * 前台列表的角色名称显示的是国际化名称，查询时需进行转化
     * @param searchRoleName 查询时输入的角色名
     * @return 数据库中保存的角色名
     */
    private List<String> processRoleName(String searchRoleName, Long accountId) {
        List<String> result = new ArrayList<String>();
        if (Strings.isNotBlank(searchRoleName)) {
            List<OrgRole> list = orgDao.getAllRolePO(accountId, null, null, null);
            if (list != null) {
                String roleNameI81n = null;
                String roleName = null;
                for (OrgRole orgRole : list) {
                    roleName = orgRole.getName();
                    roleNameI81n = ResourceUtil.getString("sys.role.rolename." + roleName);
                    if (!StringUtil.checkNull(roleNameI81n) && roleNameI81n.indexOf(searchRoleName) != -1) {
                        result.add(roleName);
                    } else {
                        result.add(searchRoleName);
                    }
                }
            }
        }
        return result;
    }
    
    @Override
    @AjaxAccess
    public int getMaxSortId(Long accountId) {
        return orgDao.getMaxSortId(V3xOrgRole.class, accountId) + 1;
    }
	
    @Override
    @AjaxAccess
    public Map createRole(String accountId, Map role) throws BusinessException {
    	Map result = new HashMap();
    	result.put("SUCCESS", "true");
    	Long accId = Long.parseLong(accountId);
		V3xOrgRole newrole = new V3xOrgRole();
		ParamUtil.mapToBean(role, newrole, false);
		newrole.setIdIfNew();
		newrole.setOrgAccountId(accId);
		newrole.setExternalType(OrgConstants.ExternalType.Interconnect4.ordinal());
		newrole.setBond(OrgConstants.ROLE_BOND.DEPARTMENT.ordinal());
		newrole.setType(V3xOrgEntity.ROLETYPE_USERROLE);
		
        //判断角色名称是否重复
        List<OrgRole> list = orgDao.getAllRolePO(newrole.getOrgAccountId(), null, null, null);
        if (list != null) {
            String roleNameI81n = null;
            String roleName = null;
            for (OrgRole orgRole : list) {
            	if(orgRole.getId().equals(newrole.getId())) {
            		continue;
            	}
                roleName = orgRole.getName();
                roleNameI81n = ResourceUtil.getString("sys.role.rolename." + roleName);
                if(roleName.equals(newrole.getName()) || roleNameI81n.equals(newrole.getName()) ) {
                	result.put("SUCCESS", "false");
                	result.put("msg", ResourceUtil.getString("role.repeat.name") + "，" + ResourceUtil.getString("MessageStatus.ERROR"));
                    return result;                
                }
            }
        }
        
        //判断编码是否重复
    	boolean isDuplicated = orgDao.isPropertyDuplicated(V3xOrgRole.class, "code", newrole.getCode(),newrole.getOrgAccountId(),newrole.getId(),OrgConstants.ExternalType.Interconnect4.ordinal());
        if (isDuplicated) {
        	result.put("SUCCESS", "false");
        	result.put("msg", ResourceUtil.getString("org.business.role.code.repeat"));
            return result;
        }
        
        List<OrgRole> orgRolePO = new ArrayList<OrgRole>();
        OrgRole roleNew = (OrgRole) newrole.toPO();
        roleNew.setIdIfNew();
        orgRolePO.add(roleNew);
        orgDao.insertOrgRole(orgRolePO);
        V3xOrgAccount account = orgManager.getAccountById(accId);
        appLogManager.insertLog(AppContext.getCurrentUser(), 868 , AppContext.getCurrentUser().getName(), account.getName(), roleNew.getName());
        return result;
    }
    
    @Override
    @AjaxAccess
    public Map updateRole(String accountId,Map map) throws BusinessException {
    	Map result = new HashMap();
    	result.put("SUCCESS", "true");
    	Long accId = Long.parseLong(accountId);
    	Long roleId = Long.parseLong(map.get("id").toString());
    	V3xOrgRole role = orgManager.getRoleById(roleId);
    	if(role == null){
        	result.put("SUCCESS", "false");
        	result.put("msg", "角色不存在，请刷新再试!");
            return result;
    	}
		ParamUtil.mapToBean(map, role, false);
    	
		
        //判断角色名称是否重复
        List<OrgRole> list = orgDao.getAllRolePO(role.getOrgAccountId(), null, null, null);
        if (list != null) {
            String roleNameI81n = null;
            String roleName = null;
            for (OrgRole orgRole : list) {
            	if(orgRole.getId().equals(role.getId())) {
            		continue;
            	}
                roleName = orgRole.getName();
                roleNameI81n = ResourceUtil.getString("sys.role.rolename." + roleName);
                if(roleName.equals(role.getName()) || roleNameI81n.equals(role.getName()) ) {
                	result.put("SUCCESS", "false");
                	result.put("msg", ResourceUtil.getString("role.repeat.name")+"，"+ResourceUtil.getString("MessageStatus.ERROR"));
                    return result;                
                }
            }
        }
        
        //判断编码是否重复
    	boolean isDuplicated = orgDao.isPropertyDuplicated(V3xOrgRole.class, "code", role.getCode(),role.getOrgAccountId(),role.getId(),OrgConstants.ExternalType.Interconnect4.ordinal());
        if (isDuplicated) {
        	result.put("SUCCESS", "false");
        	result.put("msg", ResourceUtil.getString("org.business.role.code.repeat"));
            return result;
        }
        
        //如果角色已经被使用，不允许停用
        if(!role.getEnabled()){
            //如果角色被使用，则不允许删除
            List<V3xOrgMember> rellistrole = orgManager.getMembersByRole(null, role.getId());
            if(rellistrole.size()>0){
            	result.put("SUCCESS", "false");
            	result.put("msg", ResourceUtil.getString("role.message.use")+"，"+ResourceUtil.getString("MessageStatus.ERROR"));
                return result;
            }
        }
        
        OrgRole roleUpdate = (OrgRole) role.toPO();
        orgDao.update(roleUpdate);
        
        V3xOrgAccount account = orgManager.getAccountById(accId);
        appLogManager.insertLog(AppContext.getCurrentUser(), 869 , AppContext.getCurrentUser().getName(), account.getName(), roleUpdate.getName());
        return result;
    }
    
    @Override
    @AjaxAccess
    public Map deleteRole(Long[] roles) throws BusinessException {
    	Map result = new HashMap();
    	result.put("SUCCESS", "true");
        if (roles != null && roles.length>0) {
            User user = AppContext.getCurrentUser();
            List<String[]> appLogs = new ArrayList<String[]>();
            List<V3xOrgRole> noDeleteRole = new UniqueList<V3xOrgRole>();
            for (Long id : roles) {
            	V3xOrgRole role = orgManager.getRoleById(id);
                if(role==null){
                	return result;
                }
                //如果角色被使用，则不允许删除
            	List<V3xOrgMember> rellistrole = orgManager.getMembersByRole(null, id);
            	if(rellistrole.size()>0){
            		noDeleteRole.add(role);
        		}
            }

            if(noDeleteRole.size()>0){
            	int i=0;
            	String roleNames = "";
            	for(V3xOrgRole nRole : noDeleteRole){
            		if(i==0){
            			roleNames = nRole.getShowName();
            		}else{
            			roleNames = roleNames + "、"+nRole.getShowName();
            		}
            		i++;
            	}
            	result.put("SUCCESS", "false");
            	result.put("msg", roleNames+ResourceUtil.getString("role.message.use")+"，"+ResourceUtil.getString("MessageStatus.ERROR"));
                return result;
            }

            List<V3xOrgRelationship> deleteRelationShips = new ArrayList<V3xOrgRelationship>();
            for (Long id : roles) {
                V3xOrgRole role = orgManager.getRoleById(id);
            	orgManagerDirect.deleteRole(role);
            	
            	V3xOrgAccount account = orgManager.getAccountById(role.getOrgAccountId());
                appLogManager.insertLog(AppContext.getCurrentUser(), 870 , AppContext.getCurrentUser().getName(), account.getName(), role.getName());
            	
    			EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
    			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective1Id, role.getId());
    			deleteRelationShips.addAll(orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, (Long)null, (Long)null, objectiveIds));
            }
            
            //删除角色关系数据
            orgManagerDirect.deleteOrgRelationships(deleteRelationShips);


        }
    	return result;
    }
    
    @Override
    @AjaxAccess
    public Map viewRole(Long roleId) throws BusinessException{
    	Map<String, Object> roleMap = new HashMap<String, Object>();
		V3xOrgRole role = orgManager.getRoleById(roleId);
		if(role != null){
			roleMap.put("id", String.valueOf(role.getId()));
			roleMap.put("name", role.getShowName());
			roleMap.put("code", role.getCode());
			roleMap.put("sortId", role.getSortId());
			roleMap.put("enable", role.getEnabled());
			roleMap.put("description", role.getDescription());
			roleMap.put("accountId", role.getOrgAccountId());
			roleMap.put("bond", role.getBond());
			roleMap.put("type", role.getType());
		}
		return roleMap;
    }
}
