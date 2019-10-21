/**
 * $Author:翟锋$
 * $Rev$
 * $Date::                     $:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.common.permission.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.cap.api.bean.CAPFormBean;
import com.seeyon.ctp.cap.api.manager.CAPFormManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ModuleType;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.common.cglib.CglibCopier;
import com.seeyon.ctp.common.config.IConfigPublicKey;
import com.seeyon.ctp.common.config.manager.ConfigManager;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.constants.ProductEditionEnum;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.ctpenumnew.EnumNameEnum;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.permission.bo.NodePolicy;
import com.seeyon.ctp.common.permission.bo.Permission;
import com.seeyon.ctp.common.permission.bo.PermissionOperDefinition;
import com.seeyon.ctp.common.permission.bo.PermissionOperation;
import com.seeyon.ctp.common.permission.enums.PermissionAction;
import com.seeyon.ctp.common.permission.enums.PermissionOperationCategoryEnmus;
import com.seeyon.ctp.common.permission.po.CtpPermissionOperation;
import com.seeyon.ctp.common.permission.vo.PermissionVO;
import com.seeyon.ctp.common.po.config.ConfigItem;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.portal.api.PortalApi;
import com.seeyon.ctp.portal.portlet.ImagePortletLayout;
import com.seeyon.ctp.util.CommonTools;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.XMLCoder;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;


/**
 * @author mujun
 *
 */
public class PermissionManagerImpl implements PermissionManager {
    private static final Log LOGGER = LogFactory.getLog(PermissionManagerImpl.class);
    private final CacheAccessable cacheFactory = CacheFactory.getInstance(PermissionManagerImpl.class);
    
    //节点权限操作
    private Map<String, List<PermissionOperation>> permissionOperations = new ConcurrentHashMap<String, List<PermissionOperation>>();

    //节点权限 KEY:configitem.id VALUE:permission
    private CacheMap<Long,Permission> permissionsCache ;
   
    //没有使用集群Cache,避免频繁集群同步
    private CacheMap<String,String> permissionLabelsCache ;
    

    private ConfigManager configManager;

    private EnumManager enumManagerNew;

    private AppLogManager appLogManager;
    
    private OrgManager orgManager;
    
    private CtpPermissionOperationManager ctpPermissionOperationManager;
    
    private CAPFormManager capFormManager;
    
    private PortalApi portalApi;

	public void setPortalApi(PortalApi portalApi) {
		this.portalApi = portalApi;
	}

	public void setCapFormManager(CAPFormManager capFormManager) {
		this.capFormManager = capFormManager;
	}

	public void setCtpPermissionOperationManager(CtpPermissionOperationManager ctpPermissionOperationManager) {
		this.ctpPermissionOperationManager = ctpPermissionOperationManager;
	}

	public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }

    public void setEnumManagerNew(EnumManager enumManager) {
        this.enumManagerNew = enumManager;
    }

    public void init() {
    	if (permissionsCache != null && permissionLabelsCache != null) {
    		return;
    	}
        if(permissionsCache == null){
            permissionsCache = cacheFactory.createMap("permissionsCache");
        }
        
        if(permissionLabelsCache == null){
        	permissionLabelsCache = cacheFactory.createMap("permissionLabelsCache");
        }
        Map<String,PermissionOperDefinition> definitions = AppContext.getBeansOfType(PermissionOperDefinition.class);
        Collection<PermissionOperDefinition> definition = definitions.values();
        List<PermissionOperation> handleList = new ArrayList<PermissionOperation>();
        List<PermissionOperation> submitList = new ArrayList<PermissionOperation>();
        //这个解析很慢有时候优化一下
        boolean isA81orA6p = false;
        if(ProductEditionEnum.getCurrentProductEditionEnum().getValue().equals(ProductEditionEnum.enterprise.getValue())
        		|| ProductEditionEnum.getCurrentProductEditionEnum().getValue().equals(ProductEditionEnum.a6.getValue()) ) {
        	isA81orA6p = true;
        }
        for(PermissionOperDefinition permissionOperDefinition:definition){
            Map<String,List<Properties>> m = permissionOperDefinition.getOperations();
            Set<String> keySet = m.keySet();
            for(Iterator<String> it = keySet.iterator();it.hasNext();){
                String key = it.next();
                List<Properties> list = m.get(key);
                List<PermissionOperation> al = permissionOperations.get(key);
                if(al==null){
                    al = new ArrayList<PermissionOperation>();
                }
                for(Properties pro:list){
                    PermissionOperation po = new PermissionOperation();
                    String operkey = pro.getProperty("key");
                	if(isA81orA6p && "JointlyIssued".equals(operkey)) {
                 		continue;
                 	}
                    po.setIsSystem(Boolean.TRUE);
                    po.setLabel(pro.getProperty("label"));
                    po.setKey(operkey);
                    po.setCodes(Arrays.asList(pro.getProperty("key")));
                    if(Strings.isNotBlank(pro.getProperty("type"))) {
                        po.setType(pro.getProperty("type"));
                    }
                    if(Strings.isNotBlank(pro.getProperty("id"))) {
                        po.setId(Long.valueOf(pro.getProperty("id")));
                    }
                    if(Strings.isNotBlank(pro.getProperty("icon"))) {
                    	po.setIcon(pro.getProperty("icon"));
                    }
                    if(Strings.isNotBlank(pro.getProperty("color"))) {
                    	po.setColor(pro.getProperty("color"));
                    }
                    if(Strings.isNotBlank(pro.getProperty("submitType"))) {
                        po.setSubmitType(Integer.parseInt(pro.getProperty("submitType")));
                    }
                    if(Strings.isNotBlank(pro.getProperty("pcClick"))) {
                        po.setPcClick(pro.getProperty("pcClick"));
                    }
                    al.add(po);
                }
               permissionOperations.put(key, al);
            }
        }
        //这里只获取协同的definitions
        PermissionOperDefinition collPermissionDefin =  definitions.get("colPermissionOperDefinition");
        if(collPermissionDefin != null){
            Map<String,List<Properties>> collmap = collPermissionDefin.getOperations();
            PermissionOperation po = null;
            for (Map.Entry<String,List<Properties>> entry : collmap.entrySet()) {
                List<Properties> list = entry.getValue();
                for(Properties pro:list){
                    po = new PermissionOperation();
                    po.setIsSystem(Boolean.TRUE);
                    po.setLabel(pro.getProperty("label"));
                    po.setKey(pro.getProperty("key"));
                    if(Strings.isNotBlank(pro.getProperty("type"))) {
                        po.setType(pro.getProperty("type"));
                    }
                    if(Strings.isNotBlank(pro.getProperty("id"))) {
                        po.setId(Long.valueOf(pro.getProperty("id")));
                    }
                    if(Strings.isNotBlank(pro.getProperty("icon"))) {
                        po.setIcon(pro.getProperty("icon"));
                    }
                    if(Strings.isNotBlank(pro.getProperty("color"))) {
                    	po.setColor(pro.getProperty("color"));
                    }
                    if(Strings.isNotBlank(pro.getProperty("submitType"))) {
                        po.setSubmitType(Integer.parseInt(pro.getProperty("submitType")));
                    }
                    if(Strings.isNotBlank(pro.getProperty("pcClick"))) {
                        po.setPcClick(pro.getProperty("pcClick"));
                    }
                    if(po.getSubmitType()!= null &&  po.getSubmitType() == 1){
                        submitList.add(po);
                    }else{
                        handleList.add(po);
                    }
                }
            }
        }
        //后面两个只能查询不能修改
        permissionOperations.put(EnumNameEnum.base_handel.name(), handleList);
        permissionOperations.put(EnumNameEnum.base_submit.name(), submitList);
    }

    
    public void savePermission(Permission permission) throws BusinessException {
        String xml = XMLCoder.encoder(permission.getNodePolicy());
        //如果当前设置的节点权限是默认的则取消该分类对应的默认节点权限
        if ("1".equals(permission.getNodePolicy().getIsDefaultNode().toString())) {
        	cancelIsNotDefaultNode(permission.getCategory(),permission.getOrgAccountId());
        }
        ConfigItem item = new ConfigItem();
        item.setIdIfNew();
        item.setCreateDate(new java.sql.Timestamp(System.currentTimeMillis()));
        item.setModifyDate(new java.sql.Timestamp(System.currentTimeMillis()));
        item.setConfigCategory(permission.getCategory());
        item.setConfigDescription(permission.getDescription());
        item.setConfigItem(permission.getName());
        item.setConfigType(String.valueOf(permission.getType()));
        item.setExtConfigValue(xml);
        item.setOrgAccountId(permission.getOrgAccountId());
        //如果当前排序号发生改变，则修改排序好（规则：排序号为已有排序号（排序号与已存在排序号相同）的时候，当前排序号以后的排序号自动+1）
        setSortAutoOne(permission, item);
        permission.setFlowPermId(item.getId());
        configManager.addConfigItem(item);
        
        permissionsCache.put(item.getId(), permission);
        
    }
    
    // 自定义比较器：按排序号排序  
    static class SortComparator implements Comparator<ConfigItem> {
        public int compare(ConfigItem p1, ConfigItem p2) {// 实现接口中的方法  
            if (p1.getSort() == null) {
                p1.setSort(999);
            }
            if (p2.getSort() == null) {
                p2.setSort(999);
            }
            if (p1.getSort() != null && p2.getSort() != null) {
                return new Double(p1.getSort()).compareTo(new Double(p2.getSort()));
            }
            return 0;

        }
    } 

	public void updatePermission(Permission permission) throws BusinessException {
        ConfigItem configItem = configManager.getConfigItem(permission.getFlowPermId());
        if(configItem == null){
            LOGGER.error("没有找到对应的节点权限配置项:"+permission.getName());
            return ;
        }
        NodePolicy nodePolicy = permission.getNodePolicy();
        String xml = "";
        if(null!=nodePolicy){
            xml = XMLCoder.encoder(nodePolicy);
        }
        configItem.setConfigItem(permission.getName());
        configItem.setConfigDescription(permission.getDescription());
        configItem.setExtConfigValue(xml);
        
        //如果当前排序号发生改变，则修改排序好（规则：排序号为已有排序号（排序号与已存在排序号相同）的时候，当前排序号以后的排序号自动+1）
        setSortAutoOne(permission, configItem);
        
        //设置类型，以方便controller传参
        permission.setCategory(configItem.getConfigCategory());
        configManager.updateConfigItem(configItem);
        permissionsCache.put(configItem.getId(), permission);
    }

    /**
     * 如果当前排序号发生改变，则修改排序好（规则：排序号为已有排序号（排序号与已存在排序号相同）的时候，当前排序号以后的排序号自动+1）
     * @param permission
     * @param configItem
     * @throws BusinessException
     */
	private void setSortAutoOne(Permission permission, ConfigItem configItem) throws BusinessException {
		if (permission.getSort() != null && !permission.getSort().equals(configItem.getSort())) {
        	List<ConfigItem> configItemList  = new ArrayList<ConfigItem>();
        	if (Strings.isNotBlank(permission.getCategory())) {
             	configItemList = configManager.listAllConfigByCategory(permission.getCategory(), configItem.getOrgAccountId());
	        	Collections.sort(configItemList, new SortComparator());
        	}
        	List<Integer> sortList = findPermissionSort(permission.getCategory(), configItem.getOrgAccountId(),configItemList);
        	if (sortList.contains(permission.getSort())) {
        		Integer maxSort = Collections.max(sortList);
        		List<ConfigItem> updateConfigItemList =  new ArrayList<ConfigItem>();
        		int number = 0;
        		for(int i = 0;i<sortList.size();i++) {
        			//设置的置大于查询出来的值，则跳过
        			if (permission.getSort() >sortList.get(i)) {
        				continue;
        			}
        			//设置的值大于最大值，或者设置的值加number不在查询的列表中，则跳过
        			if (permission.getSort() > maxSort || !sortList.contains(permission.getSort()+number)) {
        				break;
        			}
        			number ++;
        			ConfigItem item = configItemList.get(i);
    				if (item.getSort().equals(sortList.get(i))  ) {
    					item.setSort(sortList.get(i)+1);
    					updateConfigItemList.add(item);
    				}
        		}
            	for(ConfigItem item : updateConfigItemList) {
    				configManager.updateConfigItem(item);
    				Permission per = getPermission(item);
    				per.setSort(item.getSort());
    				permissionsCache.put(item.getId(), per);
    			}   
        	}
        }
		configItem.setSort(permission.getSort());
	}
    
	/**
	 * 查询排序号
	 * @param configCategory
	 * @param orgAccountId
	 * @param configItemList
	 * @return
	 * @throws BusinessException
	 */
	public List<Integer> findPermissionSort(String configCategory,Long orgAccountId,List<ConfigItem> configItemList) throws BusinessException {
    	List<Integer> sortList = new ArrayList<Integer>();
        for(ConfigItem configItem : configItemList){
        	//兼容老数据，自定义的节点权限为空
        	if(configItem.getSort() == 999){
        		continue;
        	}
        	sortList.add(configItem.getSort());
        	Collections.sort(sortList);
        }
    	return sortList;
    }
	
	/**
	 * 查询当前节点权限类型最大排序号
	 * @param configCategory
	 * @return
	 * @throws BusinessException
	 */
	public int findPermissionMaxSort(String configCategory) throws BusinessException{
		int maxSort = 0;
		User user = AppContext.getCurrentUser();
	    Long orgAccountId = user.getLoginAccount();
		if (Strings.isNotBlank(configCategory)) {
         	List<ConfigItem> configItemList = configManager.listAllConfigByCategory(configCategory, orgAccountId);
         	List<Integer> sortList =  findPermissionSort(configCategory, orgAccountId, configItemList);
         	maxSort = Collections.max(sortList);
		}
		return maxSort;
	}
	
	/**
	 * 根据传递的参数，设置是否为默认节点
	 * @param id
	 * @param isDefaultNode
	 * @throws BusinessException
	 */
	public void setIsDefaultNode(Long id) throws BusinessException {
		User user = AppContext.getCurrentUser();
		ConfigItem configItem = configManager.getConfigItem(id);
		Permission permission = getPermission(configItem);
		//取消这个分类里的其他默认节点
        cancelIsNotDefaultNode(permission.getCategory(),permission.getOrgAccountId());
        
        NodePolicy nodePolicy = permission.getNodePolicy();
        nodePolicy.setIsDefaultNode(1);
        String xml = "";
            xml = XMLCoder.encoder(nodePolicy);
        configItem.setExtConfigValue(xml);
        
        
        //{0}设置{1}为默认节点
        if (permission.getCategory().contains("edoc")) {
        	appLogManager.insertLog(user, AppLogAction.Edoc_Update_Default_Node, user.getName(),permission.getLabel());
        } else if (permission.getCategory().contains("col")){
        	appLogManager.insertLog(user, AppLogAction.Coll_Update_Default_Node, user.getName(),permission.getLabel());
        }
        
        configManager.updateConfigItem(configItem);
        permissionsCache.put(configItem.getId(), permission);
		
		
	}

	/**
	 * 取消默认节点权限
	 * @param category
	 * @throws BusinessException
	 */
	private void cancelIsNotDefaultNode(String category,Long orgAccountId) throws BusinessException {
		ConfigItem configAction = this.getDefaultConfigItemByConfigCategory(category,orgAccountId);
		if (configAction != null) {
			Permission permissionAction = getPermission(configAction);
	        NodePolicy nodePolicyAction = permissionAction.getNodePolicy();
	        String xmlAction = "";
	        if(nodePolicyAction != null){
	        	nodePolicyAction.setIsDefaultNode(0);
	        	xmlAction = XMLCoder.encoder(nodePolicyAction);
	        }
	        configAction.setExtConfigValue(xmlAction);
	        configManager.updateConfigItem(configAction);
	        permissionsCache.put(configAction.getId(), permissionAction);
		}
	}
	
	/**
	 * 查询默认节点权限 (查询当前单位（协同、公文发文、公文收文、公文签报权限策略）默认的节点权限)
	 * @param configCategory 权限策略 EnumNameEnum
	 * @return ConfigItem
	 * @throws BusinessException
	 */
	private ConfigItem getDefaultConfigItemByConfigCategory(String configCategory,Long orgAccountId) throws BusinessException{
	    List<ConfigItem> configItemList = new ArrayList<ConfigItem>();
		if (Strings.isNotBlank(configCategory)) {
         	configItemList = configManager.listAllConfigByCategory(configCategory, orgAccountId);
		}
		for (ConfigItem item : configItemList) {
            PermissionVO perm = configItemToPermissionVO(item);
            if("1".equals(String.valueOf(perm.getIsDefaultNode()))) {
            	return item;
            }
		}
		return null;
	}
	
	/**
	 * 查询默认节点权限 (查询当前单位（协同、公文发文、公文收文、公文签报权限策略）默认的节点权限)
	 * @param configCategory 权限策略 EnumNameEnum
	 * @return PermissionVO
	 * @throws BusinessException
	 */
	public PermissionVO getDefaultPermissionByConfigCategory(String configCategory,Long orgAccountId) throws BusinessException{
		
		User user = AppContext.getCurrentUser();
	    if (orgAccountId == null) {
	    	orgAccountId = user.getLoginAccount();
	    }
	    if("form_flow_perm_policy".equals(configCategory)){
	    	configCategory = "col_flow_perm_policy";
    	}
		ConfigItem item = getDefaultConfigItemByConfigCategory(configCategory,orgAccountId);
		PermissionVO perm = null;
		if (item == null) {
			if(EnumNameEnum.edoc_send_permission_policy.name().equals(configCategory)){
                item = configManager.getConfigItem(configCategory,"shenpi", orgAccountId);
            }else if(EnumNameEnum.edoc_rec_permission_policy.name().equals(configCategory)){
            	item = configManager.getConfigItem(configCategory,"yuedu", orgAccountId);
            }else if(EnumNameEnum.edoc_qianbao_permission_policy.name().equals(configCategory)){
            	item = configManager.getConfigItem(configCategory,"shenpi", orgAccountId);
            }else if(EnumNameEnum.edoc_new_send_permission_policy.name().equals(configCategory)){
                item = configManager.getConfigItem(configCategory,"shenpi", orgAccountId);
            }else if(EnumNameEnum.edoc_new_rec_permission_policy.name().equals(configCategory)){
            	item = configManager.getConfigItem(configCategory,"shenpi", orgAccountId);
            }else if(EnumNameEnum.edoc_new_change_permission_policy.name().equals(configCategory)){
            	item = configManager.getConfigItem(configCategory,"qianshou", orgAccountId);
            }else if(EnumNameEnum.edoc_new_qianbao_permission_policy.name().equals(configCategory)){
            	item = configManager.getConfigItem(configCategory,"shenpi", orgAccountId);
            }else if(EnumNameEnum.info_send_permission_policy.name().equals(configCategory)){
            	item = configManager.getConfigItem(configCategory,"shenhe", orgAccountId);
            }else {
                item = configManager.getConfigItem(configCategory,"collaboration", orgAccountId);
            }
			LOGGER.info("没有获取到当前单位的默认节点权限：configCategory:"+configCategory+",在系统中找不到！修改成系统初始化的默认权限！");
		}
		perm = configItemToPermissionVO(item);
		return perm;
	}
	
	
	@Override
	public PermissionVO getDefaultPermissionByApp(ApplicationCategoryEnum applicationCategoryEnum, Long orgAccountId) throws BusinessException {

		String category = WFNodePermissionUtil.converAppEnumToCategory(applicationCategoryEnum);
		
		return getDefaultPermissionByConfigCategory(category, orgAccountId);
	}
	@CheckRoleAccess(roleTypes={Role_NAME.AccountAdministrator,Role_NAME.EdocManagement,Role_NAME.InfoManager,Role_NAME.FormAdmin,Role_NAME.BusinessDesigner})
    public void deletePermission(Long id) throws BusinessException {
        configManager.deleteCriteria(id);
        permissionsCache.remove(id);
    }

    
    public String deletePermissions(String[] ids) throws BusinessException {
        if(ids == null || ids.length == 0){
            return ResourceUtil.getString("permission.operation.choose.delete.records");
        }
        for(String id:ids){
        	Permission permis=permissionsCache.get(Long.parseLong(id));
            this.deletePermission(Long.parseLong(id));
            if(EnumNameEnum.info_send_permission_policy.name().equals(permis.getCategory())) {
            	/** (应用设置) 节点权限删除*/
	          	appLogManager.insertLog(AppContext.getCurrentUser(), AppLogAction.Information_permission_Delete,AppContext.getCurrentUser().getName(),permis.getLabel());
            } else if(EnumNameEnum.col_flow_perm_policy.name().equals(permis.getCategory())) {
            	//记录应用日志,删除节点权限--175
            	appLogManager.insertLog(AppContext.getCurrentUser(), 175, AppContext.getCurrentUser().getName(),permis.getLabel());
            } else if(EnumNameEnum.edoc_new_change_permission_policy.name().equals(permis.getCategory())||
            		EnumNameEnum.edoc_new_send_permission_policy.name().equals(permis.getCategory())||
            		EnumNameEnum.edoc_new_qianbao_permission_policy.name().equals(permis.getCategory())||
            		EnumNameEnum.edoc_new_rec_permission_policy.name().equals(permis.getCategory())){
            	appLogManager.insertLog(AppContext.getCurrentUser(), 376, AppContext.getCurrentUser().getName(),permis.getLabel());
            }
        }
        return "";
    }

	public FlipInfo getPermissions(FlipInfo flipInfo,Map params) throws BusinessException {
        List<ConfigItem> list = new ArrayList<ConfigItem>();
        List<PermissionVO> p_list = new ArrayList<PermissionVO>();
        if(params == null){
            return flipInfo;
        }
        User user = AppContext.getCurrentUser();
        Long orgAccountId = user.getLoginAccount();
        String configCategory = (String) params.get("configCategory");
        //是否启用
        String isEnabled = (String) params.get("isEnabled");
        //是否引用
        String isRef = (String) params.get("isRef");
        //名称查询
        String name = (String) params.get("name");

        if(configCategory == null){
            return flipInfo;
        }
        //如果是公文的时候，要查出收文、发文、签报三个集合，并且合并成一个集合
		//G6 6.1 新表单公文在这里直接修改为新的节点权限就可以
        if("edoc".equals(configCategory)){
            List<ConfigItem> edocList = new ArrayList<ConfigItem>();
            //查询发文
            edocList = configManager.listAllConfigByCategory(EnumNameEnum.edoc_new_send_permission_policy.name(), orgAccountId);
            Collections.sort(edocList, new SortComparator());
            list.addAll(edocList);
            //查询收文
            edocList = configManager.listAllConfigByCategory(EnumNameEnum.edoc_new_rec_permission_policy.name(), orgAccountId);
            Collections.sort(edocList, new SortComparator());
            list.addAll(edocList);
            //查询 签报
            edocList = configManager.listAllConfigByCategory(EnumNameEnum.edoc_new_qianbao_permission_policy.name(), orgAccountId);
            Collections.sort(edocList, new SortComparator());
            list.addAll(edocList);
            //查询 交换
            edocList = configManager.listAllConfigByCategory(EnumNameEnum.edoc_new_change_permission_policy.name(), orgAccountId);
            Collections.sort(edocList, new SortComparator());
            list.addAll(edocList);
        }
        /** V51 F18 信息报送  start */
        else if("info".equals(configCategory)) {
            //查询发文
            List<ConfigItem> infoList = configManager.listAllConfigByCategory(EnumNameEnum.info_send_permission_policy.name(), orgAccountId);
            Collections.sort(infoList, new SortComparator());
            list.addAll(infoList);
        }
        /** V51 F18 信息报送  end */
        else{
            list = configManager.listAllConfigByCategory(configCategory, orgAccountId);
            Collections.sort(list, new SortComparator());
        }
        
        if(Strings.isNotEmpty(list)){
     	   for(Iterator<ConfigItem> it = list.iterator();it.hasNext();){
     		   ConfigItem config = it.next();
     		   if(!AppContext.hasPlugin("news") && "newsaudit".equals(config.getConfigItem())){
     			   it.remove();
     		   }
     		   
     		   if(!AppContext.hasPlugin("bulletin") && "bulletionaudit".equals(config.getConfigItem())){
     			 it.remove();
    		   }
     	   }
        }
         
        
        if(isEnabled != null){
            //根据启用状态查询
            for (ConfigItem item : list) {
                PermissionVO perm = configItemToPermissionVO(item);
                if(isEnabled.equals(perm.getIsEnabled().toString())){
                    //V5.1-G6--V51-4-6 登记显示为分发
					/*if(isG6Version()){
						if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "登记".equals(perm.getLabel())){
							perm.setLabel("分发");
						}else if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "Register".equals(perm.getLabel())){
							perm.setLabel("Distribute");
						}else if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "登記".equals(perm.getLabel())){
							perm.setLabel("分發");
						}
					}*/
                    p_list.add(perm);
                }
            }
        }else if(isRef!=null){
            //根据引用状态查询
            for (ConfigItem item : list) {
                PermissionVO perm = configItemToPermissionVO(item);
                if(perm.getIsRef() == null){
                    perm.setIsRef(0);
                }
                if(isRef.equals(perm.getIsRef().toString())){
                    //V5.1-G6--V51-4-6 登记显示为分发
					/*if(isG6Version()){
						if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "登记".equals(perm.getLabel())){
							perm.setLabel("分发");
						}else if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "Register".equals(perm.getLabel())){
							perm.setLabel("Distribute");
						}else if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "登記".equals(perm.getLabel())){
							perm.setLabel("分發");
						}
					}*/
                    p_list.add(perm);
                }
            }
        }else if(name!=null){
            //根据名称查询
            for (ConfigItem item : list) {
                PermissionVO perm = configItemToPermissionVO(item);
                if(perm != null){
                	String label = perm.getLabel();
                    //V5.1-G6--V51-4-6 登记显示为分发
					/*if(isG6Version()){
						if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "登记".equals(perm.getLabel())){
							label="分发";
							perm.setLabel("分发");
						}else if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "Register".equals(perm.getLabel())){
							label="Distribute";
							perm.setLabel("Distribute");
						}else if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "登記".equals(perm.getLabel())){
							label="分發";
							perm.setLabel("分發");
						}
					}*/
                	if(Strings.isNotBlank(label)){
                		if(label.contains(name)){
                            p_list.add(perm);
                        }
                	}
                }
            }
        }else{
            //查询协同全部节点
            for (ConfigItem item : list) {
                PermissionVO perm = configItemToPermissionVO(item);
                //V5.1-G6--V51-4-6 登记显示为分发
				/* if(isG6Version()){
					if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "登记".equals(perm.getLabel())){
						perm.setLabel("分发");
					}else if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "Register".equals(perm.getLabel())){
						perm.setLabel("Distribute");
					}else if(perm.getType()==0 && "edoc_rec_permission_policy".equals(perm.getCategory()) && "登記".equals(perm.getLabel())){
						perm.setLabel("分發");
					}
				}*/
                p_list.add(perm);
            }
        }
        String formAppIdStr = (String) params.get("formAppId");
        String formName = "";
        if(Strings.isNotEmpty(p_list)){
        	List<String> otherFormoperationCodes = new ArrayList<String>();
        	String thisFormOperationCode = "";
        	if (Strings.isNotBlank(formAppIdStr)) {
        		Long formAppId = Long.valueOf(formAppIdStr);
        		//通过接口获取
        		List<CtpPermissionOperation> formAppIdOperation = ctpPermissionOperationManager.findBindFormList();
        		for (CtpPermissionOperation op : formAppIdOperation) {
        			if (!formAppId.equals(op.getFormAppId())) {
        				otherFormoperationCodes.add(String.valueOf(op.getId()));
        			} else {
        				thisFormOperationCode = String.valueOf(op.getId());
        			}
        		}
        		CAPFormBean fBean = capFormManager.getForm(Long.valueOf(formAppIdStr));
        		if(fBean!=null) {
        			formName = fBean.getFormName();
        		}
        	}
        	for(Iterator<PermissionVO> it = p_list.iterator();it.hasNext();){
        		PermissionVO config = it.next();
        		boolean thisFormOp = false;
        		boolean otherFormOp = false;
        		//判断基础\高级\普通操作中是否包含不是该表单的操作(表单绑定操作除外)
        		String advancedStr = config.getAdvancedOperation();
        		if (Strings.isNotBlank(advancedStr)) {
        			String[] advancedArr = advancedStr.split(",");
        			if (Strings.isNotEmpty(otherFormoperationCodes)) {
        				for (String ad : advancedArr) {
        					if (otherFormoperationCodes.contains(ad)) {
        						otherFormOp = true;
        						break;
        					}
        				}
        			}
        			if (Arrays.asList(advancedArr).contains(thisFormOperationCode)) {
        				thisFormOp = true;
        			}
        		}
        		String basicStr = config.getBasicOperation();
        		if (Strings.isNotBlank(basicStr)) {
        			String[] basicArr = basicStr.split(",");
        			if (!otherFormOp && Strings.isNotEmpty(otherFormoperationCodes)) {
        				for (String ad : basicArr) {
        					if (otherFormoperationCodes.contains(ad)) {
        						otherFormOp = true;
        						break;
        					}
        				}
        			}
        			if (!thisFormOp && Arrays.asList(basicArr).contains(thisFormOperationCode)) {
        				thisFormOp = true;
        			}
        		}
        		String commonStr = config.getCommonOperation();
        		if (Strings.isNotBlank(commonStr)) {
        			String[] commonArr = commonStr.split(",");
        			if (!otherFormOp && Strings.isNotEmpty(otherFormoperationCodes)) {
        				for (String ad : commonArr) {
        					if (otherFormoperationCodes.contains(ad)) {
        						otherFormOp = true;
        						break;
        					}
        				}
        			}
        			if (!thisFormOp && Arrays.asList(commonArr).contains(thisFormOperationCode)) {
        				thisFormOp = true;
        			}
        		}
        		if (otherFormOp) {
        			it.remove();
        			continue;
        		}
        		//该人员是否有访问权限(不是自己创建的,并且不是本单位的)、公文的除外
        		if (!"edoc_new_send_permission_policy".equals(configCategory) && !"edoc_new_rec_permission_policy".equals(configCategory)
        				&& !"edoc_new_qianbao_permission_policy".equals(configCategory) && !"edoc_new_change_permission_policy".equals(configCategory) 
        				&& !"edoc".equals(configCategory) && !"info".equals(configCategory) && config.getCreateUserId() != null && !config.getCreateUserId().equals(user.getId()) 
        				&& !orgManager.isAdministratorById(config.getCreateUserId(), user.getLoginAccount())) {
        			it.remove();
        			continue;
        		}
        		if (thisFormOp) {
        			config.setFormName(formName);
        		}
        		// 是否允许被修改\删除
        		if ("edoc_new_send_permission_policy".equals(configCategory) || "edoc_new_rec_permission_policy".equals(configCategory)
        				|| "edoc_new_qianbao_permission_policy".equals(configCategory) || "edoc_new_change_permission_policy".equals(configCategory) 
        				|| "edoc".equals(configCategory) || "info".equals(configCategory) || orgManager.isAdministrator() || (config.getCreateUserId() != null && config.getCreateUserId().equals(user.getId()))) {
        			config.setCanDelete(Boolean.TRUE);
        			config.setCanEdit(Boolean.TRUE);
        		}
        	}
        }
        Object from = params.get("from");
        if (from != null && "formRightSetting".equals(params.get("from"))) {
        	flipInfo.setData(p_list);
		}else {
			DBAgent.memoryPaging(p_list, flipInfo);
		}
        return flipInfo;

    }


    
    public Permission getPermission(String configCategory, String configItem, Long accountId) throws BusinessException {
    	//添加防护处理，如果是公文时，有可能传参 configItem = inform
    	/*if(!EnumNameEnum.col_flow_perm_policy.name().equals(configCategory) && "inform".equals(configItem)){
    		configItem = "zhihui";
    	}
        ConfigItem item = configManager.getConfigItem(configCategory,configItem, accountId);
        if(item == null){
            String changeToPolicy = null;
            if(EnumNameEnum.edoc_send_permission_policy.name().equals(configCategory)){
                changeToPolicy = "shenpi";
            }else if(EnumNameEnum.edoc_rec_permission_policy.name().equals(configCategory)){
                changeToPolicy = "yuedu";
            }else if(EnumNameEnum.edoc_qianbao_permission_policy.name().equals(configCategory)){
                changeToPolicy = "shenpi";
            }else if(EnumNameEnum.edoc_new_send_permission_policy.name().equals(configCategory)){
                changeToPolicy = "shenpi";
            }else if(EnumNameEnum.edoc_new_rec_permission_policy.name().equals(configCategory)){
                changeToPolicy = "shenpi";
            }else if(EnumNameEnum.edoc_new_change_permission_policy.name().equals(configCategory)){
            	changeToPolicy = "qianshou";
    		}else if(EnumNameEnum.edoc_new_qianbao_permission_policy.name().equals(configCategory)){
                changeToPolicy = "shenpi";
            }else{
                changeToPolicy = "collaboration";
            }
            item = configManager.getConfigItem(configCategory,changeToPolicy, accountId);
            
            LOGGER.info("当前流程节点权限：configCategory:"+configCategory+",accountId:"+accountId+",configItem:"+configItem+"在系统中找不到！修改成"+changeToPolicy+"权限！");
            if (item == null) {
                return null;
            }
        }
        if (permissionsCache == null) {
        	init();
        }
        Permission p  = permissionsCache.get(item.getId()) ;
        if(p == null ){
            p = configItemToPermission(item);
            permissionsCache.put(item.getId(), p);
        }
        
        
        Permission permissionNew = this.clone(p);
       
        //做缓存时，无法国际化，这里需要重新国际化一下
        permissionNew.setLabel(getPermissionName(p));
        
     //   permissionsCache.notifyUpdate(item.getId());
        
        return permissionNew;*/
    	return null;
    }

    public Permission clone(Permission p) {
        Permission np = null;
        try {
            np = p.getClass().newInstance();
            CglibCopier.copy(p, np);
        } catch (Exception e) {
            LOGGER.error("",e);
        }
 
        return np;
    }
    
    
    public PermissionVO getPermissionVO(String configCategory, String configItem, Long accountId) throws BusinessException {
    	//添加防护处理，如果是公文时，有可能传参 configItem = inform
    	if(!EnumNameEnum.col_flow_perm_policy.name().equals(configCategory) && "inform".equals(configItem)){
    		configItem = "zhihui";
    	}
        ConfigItem item = configManager.getConfigItem(configCategory,
                configItem, accountId);
        if(item == null){
            if(!EnumNameEnum.col_flow_perm_policy.name().equals(configCategory) && EnumNameEnum.edoc_send_permission_policy.name().equals(configCategory)){
                item = configManager.getConfigItem(configCategory,"shenpi", accountId);
            }else if(!EnumNameEnum.col_flow_perm_policy.name().equals(configCategory) && EnumNameEnum.edoc_rec_permission_policy.name().equals(configCategory)){
            	item = configManager.getConfigItem(configCategory,"yuedu", accountId);
            }else if(!EnumNameEnum.col_flow_perm_policy.name().equals(configCategory) && EnumNameEnum.edoc_qianbao_permission_policy.name().equals(configCategory)){
            	item = configManager.getConfigItem(configCategory,"shenpi", accountId);
            }else{
                item = configManager.getConfigItem(configCategory,"inform", accountId);
            }
            LOGGER.info("当前流程节点权限："+configItem+"在系统中找不到！修改成知会权限！");
        }
        PermissionVO permission = configItemToPermissionVO(item);
        return permission;
    }
    
    public String getPermissionName(String configCategory, String configItem, Long accountId) throws BusinessException {
        String permissionName = "";
        if(Strings.isBlank(configCategory)||Strings.isBlank(configItem)||accountId == null){
            return permissionName;
        }
        Permission permission = this.getPermission(configCategory, configItem, accountId);

        return getPermissionName(permission);
    }
    
    @Override
    public String getPermissionName(Permission permission) throws BusinessException {
        String permissionName ="";
        if(permission == null){
            return "";
        }
        if(Permission.Node_Type_System.equals(permission.getType())){
        	String cacheKey = permission.getCategory()+","+permission.getName()+","+AppContext.getLocale().toString();
        	String label = permissionLabelsCache.get(cacheKey);
            //权限名称
        	if(label == null){
        		label = enumManagerNew.getEnumItemLabel(EnumNameEnum.valueOf(permission.getCategory()), permission.getName());
        		permissionLabelsCache.put(cacheKey,label);
        	}
            //G6版本“登记”节点应该显示为“分发”
			/*            if(isG6Version()){
				if("node.policy.dengji".equals(label)){
					label="edoc.element.fenfa";
				}
			}
			*/            
        	permissionName = ResourceUtil.getString(label);
        }else{
            permissionName = permission.getName();
        }
        return permissionName;
    }
    
    /**
     * po对象转vo对象
     * 将ConfigItem对象转换成Permission对象
     * @param item
     * @return
     * @throws BusinessException
     */
    private Permission configItemToPermission(ConfigItem item)
            throws BusinessException {
        if(null!=item){
            Permission permission = new Permission();
            NodePolicy nodePolicy = new NodePolicy();
            try{
            nodePolicy = (NodePolicy) XMLCoder.decoder(item.getExtConfigValue());
            }catch(Exception e){
                throw new BusinessException("解析节点权限错误：id="+item.getId()+" ConfigCategoryName="+item.getConfigCategoryName(),e);
            }
            if (nodePolicy != null) {
                permission.setNodePolicy(nodePolicy);
            }
            permission.setFlowPermId(item.getId());
            permission.setCategory(item.getConfigCategory());
            permission.setDescription(item.getConfigDescription());
            permission.setOrgAccountId(item.getOrgAccountId());
            permission.setSort(item.getSort());
            if(item.getConfigType()!=null){
                permission.setType(Integer.valueOf(item.getConfigType()));
            }
            permission.setName(item.getConfigItem());
            if(Permission.Node_Type_System.equals(permission.getType())){//权限名称
                String label = enumManagerNew.getEnumItemLabel(EnumNameEnum.valueOf(item.getConfigCategory()), item.getConfigItem());
                permission.setLabel(ResourceUtil.getString(label));
            }else{
                permission.setLabel(item.getConfigItem());
            }
            permission.setCreateDate(item.getCreateDate());
            return permission;
        }else{
            return null;
        }
    }


    
  public PermissionVO getPermission(Long id) throws BusinessException {
    ConfigItem item = configManager.getConfigItem(id);
    PermissionVO permission = null;
    if (item != null) {
      permissionsCache.get(item.getId());
      permission = configItemToPermissionVO(item);
    }
    return permission;
  }

    /**
     * po对象转vo对象
     * 将ConfigItem对象转换成Permission对象
     * @param item
     * @return
     * @throws BusinessException
     */
    public PermissionVO configItemToPermissionVO(ConfigItem item) throws BusinessException {
        if(null!=item){
            PermissionVO permissionVO = new PermissionVO();
            NodePolicy nodePolicy = new NodePolicy();
            try{
                nodePolicy = getPermission(item).getNodePolicy();;
            }catch(Exception e){
                throw new BusinessException("解析节点权限错误：id="+item.getId()+" ConfigCategoryName="+item.getConfigCategoryName(),e);
            }
            permissionVO.setSort(item.getSort());
            permissionVO.setIsDefaultNode(nodePolicy.getIsDefaultNode());
            permissionVO.setFlowPermId(item.getId());
            permissionVO.setCategory(item.getConfigCategory());
            permissionVO.setCategoryName(ResourceUtil.getString("permission."+item.getConfigCategory()));
            permissionVO.setDescription(item.getConfigDescription());
            permissionVO.setOrgAccountId(item.getOrgAccountId());
            permissionVO.setName(item.getConfigItem());
            String configType = item.getConfigType();
            if(Strings.isNotBlank(configType)){
                permissionVO.setType(Integer.valueOf(configType));
                if(Permission.Node_Type_System.toString().equals(configType)){
                    //系统预置 类型
                    permissionVO.setTypeName(ResourceUtil.getString("permission.type.system"));
                    //创建人 系统预置
                    permissionVO.setCreateMemberName(ResourceUtil.getString("permission.type.system"));
                    //权限名称
                    String label = enumManagerNew.getEnumItemLabel(EnumNameEnum.valueOf(item.getConfigCategory()), item.getConfigItem());
                    permissionVO.setLabel(ResourceUtil.getString(label));
                }else{
                    permissionVO.setTypeName(ResourceUtil.getString("permission.type.custome"));
                    permissionVO.setLabel(item.getConfigItem());
                    if (nodePolicy != null) {
                    	if (nodePolicy.getCreateUserId() == null || orgManager.isAdministratorById(nodePolicy.getCreateUserId(), AppContext.getCurrentUser().getLoginAccount())) {//单位管理员
                    		permissionVO.setCreateMemberName(ResourceUtil.getString("sys.role.rolename.AccountAdmin"));
                    	} else {//表单管理员
                    		permissionVO.setCreateMemberName(ResourceUtil.getString("form.formadmin.label"));
                    	}
                    }
                }
            }
            if(nodePolicy!=null){
            	permissionVO.setCreateUserId(nodePolicy.getCreateUserId());
                //批量处理
                permissionVO.setBatch(nodePolicy.getBatch());
                //态度
                permissionVO.setAttitude(nodePolicy.getAttitude());
                //默认态度
                permissionVO.setDefaultAttitude(nodePolicy.getDefaultAttitude());
                //态度对应显示值
                permissionVO.setDatailAttitude(nodePolicy.getDatailAttitude());
                //基本操作
                permissionVO.setBasicOperation(nodePolicy.getBaseAction());
                //高级操作
                permissionVO.setAdvancedOperation(nodePolicy.getAdvancedAction());
                //常用操作
                permissionVO.setCommonOperation(nodePolicy.getCommonAction());
                //是否启用
                Integer isEnabled = nodePolicy.getIsEnabled();
                //不同意设置
                permissionVO.setCustomAction(nodePolicy.getCustomAction());
                permissionVO.setIsEnabled(isEnabled);
                if(isEnabled!=null){
                    //开始节点
                    if(isEnabled.equals(Permission.Node_isActive)){
                        permissionVO.setIsEnabledName(ResourceUtil.getString("permission.status.yes"));
                    }
                    //处理节点
                    if(isEnabled.equals(Permission.Node_isNotActive)){
                        permissionVO.setIsEnabledName(ResourceUtil.getString("permission.status.no"));
                    }
                }else{
                    permissionVO.setIsEnabledName(ResourceUtil.getString("permission.status.no"));
                }
                Integer isRef = nodePolicy.getIsRef();
                permissionVO.setIsRef(isRef);
                if(isRef!=null){
                    //开始节点
                    if(isRef.equals(Permission.Node_isRef)){
                        permissionVO.setIsRefName(ResourceUtil.getString("permission.status.yes"));
                    }
                    //处理节点
                    if(isRef.equals(Permission.Node_unRef)){
                        permissionVO.setIsRefName(ResourceUtil.getString("permission.status.no"));
                    }
                }else{
                    permissionVO.setIsRefName(ResourceUtil.getString("permission.status.no"));
                }
                Integer location = nodePolicy.getLocation();
                permissionVO.setLocation(location);
                if(location!=null){
                    //开始节点
                    if(location.equals(Permission.Node_Location_Start)){
                        permissionVO.setLocationName(ResourceUtil.getString("permission.location.start"));
                    }
                    //处理节点
                    if(location.equals(Permission.Node_Location_Mid)){
                        permissionVO.setLocationName(ResourceUtil.getString("permission.location.mid"));
                    }
                    //结束节点
                    if(location.equals(Permission.Node_Location_End)){
                        permissionVO.setLocationName(ResourceUtil.getString("permission.location.end"));
                    }
                }
                Integer opinionPolicy = nodePolicy.getOpinionPolicy();
                permissionVO.setOpinionPolicy(opinionPolicy);
                permissionVO.setCancelOpinionPolicy(nodePolicy.getCancelOpinionPolicy());
                permissionVO.setDisAgreeOpinionPolicy(nodePolicy.getDisAgreeOpinionPolicy());
                // 指定回退再处理时流转的方式
                Integer submitStyle = nodePolicy.getSubmitStyle();
                permissionVO.setSubmitStyle(submitStyle);
                permissionVO.setPortalValue(nodePolicy.getPortalValue());
            }
            return permissionVO;
        }else{
            return null;
        }
    }

    
    public String isPermissionExsit(Map params) {
        User user = AppContext.getCurrentUser();
        String category = (String) params.get("category");
        String name = (String) params.get("name");//最新的节点权限名称
        String flag = (String) params.get("flag");
        String curName = (String) params.get("curName");//当前节点权限名称 （改之前的名称）
        Long loginAccount = user.getLoginAccount();
        if(Strings.isBlank(category)||Strings.isBlank(name)){
            return "";
        }
        List<ConfigItem> list = configManager.listAllConfigByCategory(category,loginAccount);
        for(ConfigItem cei:list){
            //系统节点
            if("0".equals(cei.getConfigType())){
                //权限名称
                String label = enumManagerNew.getEnumItemLabel(EnumNameEnum.valueOf(category), cei.getConfigItem());
                if(name.equals(ResourceUtil.getString(label))){
                    return ResourceUtil.getString("permission.prompt.exists");
                }
            }else{
                if("edit".equals(flag)){//修改节点权限
                    if(name.equals(cei.getConfigItem())&&!Strings.escapeJavascript(name).equals(Strings.escapeJavascript(curName))){
                        return ResourceUtil.getString("permission.prompt.exists");
                    }
                }else {
                    if(name.equals(cei.getConfigItem())){//新建节点权限 自定义节点
                        return ResourceUtil.getString("permission.prompt.exists");
                    }
                }
            }
        }
        return "";
    }
   
    private Permission getPermission(ConfigItem item) throws BusinessException {
        Permission p = permissionsCache.get(item.getId());
        if (p == null) {
            p = configItemToPermission(item);
            permissionsCache.put(item.getId(), p);
        }

        Permission permissionNew = this.clone(p);

        // 做缓存时，无法国际化，这里需要重新国际化一下
        permissionNew.setLabel(getPermissionName(p));

        return permissionNew;
    }
    
    public List<Permission> getPermissionsByCategory(String category, Long accountId) throws BusinessException {
        List<ConfigItem> list = configManager.listAllConfigByCategory(category, accountId);
       
        Collections.sort(list, new SortComparator());
        List<Permission> fList = new ArrayList<Permission>();
        for (ConfigItem item : list) {
            Permission permission = getPermission(item);
            //没有插件
            if("newsaudit".equals(permission.getName()) && !AppContext.hasPlugin("news")){
            	continue;
            }
            
            if("bulletionaudit".equals(permission.getName()) && !AppContext.hasPlugin("bulletin")){
            	continue;
            }
            if (!"niwen".equals(permission.getName()) && !"dengji".equals(permission.getName())) {
                fList.add(permission);
            }
            	
        }
        return fList;
    }

    
    public List<String> getRequiredOpinionPermissions(String category, Long accountId)
            throws BusinessException {
        ArrayList<String> result = new ArrayList<String>();
        List<Permission> all = getPermissionsByCategory(category, accountId);
        if(all != null && !all.isEmpty()){
            result = new ArrayList<String>();
            for (Permission permission : all) {
                Integer opinion = permission.getNodePolicy().getOpinionPolicy();
                if(opinion != null && opinion.intValue() == 1){
                    result.add(permission.getName());
                }
            }
        }
        return result;
    }

    
    public boolean isActionAllowed(String category, String configItem, String action, Long accountId)
            throws BusinessException{
        List<String> list = getActionList(category, configItem, accountId);
        return (list.contains(action));
    }

    
    public List<String> getActionList(String category, String configItem, Long accountId, PermissionAction action) throws BusinessException {
        ConfigItem item = configManager.getConfigItem(category, configItem, accountId);
        String actionList = "";
        List<String> list = new ArrayList<String>();
        if(null!=item){
            NodePolicy nodePolicy = getPermission(item).getNodePolicy();
            if("basic".equals(action.name()) || "basicOperation".equals(action.name())){
                actionList = nodePolicy.getBaseAction();
            }else if("advanced".equals(action.name()) || "advancedOperation".equals(action.name())){
                actionList = nodePolicy.getAdvancedAction();
            }else if("common".equals(action.name()) || "commonOperation".equals(action.name())){
                actionList = nodePolicy.getCommonAction();
            }
            if(Strings.isNotBlank(actionList)){
                    String[] strs = actionList.split(",");
                    for(String str:strs){
                        list.add(str.trim());
                }
            }
        }
        return list;
    }

    public List<String> getActionList(Permission permission,PermissionAction action){
        List<String> list = new ArrayList<String>();
        if(permission == null || action == null){
        	return list;
        }
        String actionList = "";
        if(PermissionAction.basic.name().equals(action.name())){
            actionList = permission.getNodePolicy().getBaseAction();
        }else if(PermissionAction.advanced.name().equals(action.name())){
            actionList = permission.getNodePolicy().getAdvancedAction();
        }else if(PermissionAction.common.name().equals(action.name())){
            actionList = permission.getNodePolicy().getCommonAction();
        }
        if(Strings.isNotBlank(actionList)){
                String[] strs = actionList.split(",");
                for(String str:strs){
                    list.add(str.trim());
            }
        }
        return list;
    }
    
    public List<String> getBasicActionList(String category, String configItem, Long accountId)throws BusinessException{
        return this.getActionList(category, configItem, accountId, PermissionAction.valueOf("basic"));
    }



    
    public List<String> getCommonActionList(String category, String configItem, Long accountId)
            throws BusinessException {
        return this.getActionList(category, configItem, accountId,PermissionAction.valueOf("common") );
    }

    
    public List<String> getAdvaceActionList(String category, String configItem, Long accountId) throws BusinessException {
        return this.getActionList(category, configItem, accountId, PermissionAction.valueOf("advanced"));
    }

    
    public List<String> getActionList(String category, String configItem, Long accountId) throws BusinessException {
        ConfigItem item = configManager.getConfigItem(category, configItem, accountId);
        String actionList = "";
        List<String> list = new ArrayList<String>();
        if(null != item){
            NodePolicy nodePolicy = getPermission(item).getNodePolicy();
            actionList = nodePolicy.getBaseAction();
            if(Strings.isNotBlank(actionList)){
                String[] strs = actionList.split(",");
                for(String str:strs){
                    list.add(str.trim());
                }
            }
            actionList = nodePolicy.getAdvancedAction();
            if(Strings.isNotBlank(actionList)){
                String[] strs = actionList.split(",");
                for(String str:strs){
                    list.add(str.trim());
                }
            }
            actionList = nodePolicy.getCommonAction();
            if(Strings.isNotBlank(actionList)){
                String[] strs = actionList.split(",");
                for(String str:strs){
                    list.add(str.trim());
                }
            }
        }
        return list;
    }

    
    public List<Map<String, String>> getActionMapList(String category, String configItem, Long accountId)
            throws BusinessException {
        ConfigItem item = configManager.getConfigItem(category, configItem, accountId);
        String actionList = "";
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if(null!=item){
            NodePolicy nodePolicy =  getPermission(item).getNodePolicy();
            actionList = nodePolicy.getBaseAction();
            if(Strings.isNotBlank(actionList)){
                String[] strs = actionList.split(",");
                for(String str:strs){
                    Map<String, String> map = new HashMap<String,String>();
                    str = str.trim();
                    map.put(str, ResourceUtil.getString("permission.operation."+str));
                    list.add(map);
                }
            }
            actionList = nodePolicy.getAdvancedAction();
            if(Strings.isNotBlank(actionList)){
                String[] strs = actionList.split(",");
                for(String str:strs){
                    Map<String, String> map = new HashMap<String,String>();
                    str = str.trim();
                    map.put(str, ResourceUtil.getString("permission.operation."+str));
                    list.add(map);
                }
            }
            actionList = nodePolicy.getCommonAction();
            if(Strings.isNotBlank(actionList)){
                String[] strs = actionList.split(",");
                for(String str:strs){
                    str = str.trim();
                    Map<String, String> map = new HashMap<String,String>();
                    map.put(str, ResourceUtil.getString("permission.operation."+str));
                    list.add(map);
                }
            }
        }
        return list;
    }

    
    public Boolean updatePermissionRef(String configCategory, String configItem, Long accountId) throws BusinessException {
    	configItem = configItem.replaceAll(new String(new char[]{(char)160}), " ");
        Permission permission = this.getPermission(configCategory, configItem, accountId);
        if(permission == null){
            return false;
        }
        
        NodePolicy nodePolicy = permission.getNodePolicy();
        
        boolean isNeedUpdate = Integer.valueOf(0).equals( nodePolicy.getIsRef());
        nodePolicy.setIsRef(1);
        
        if(isNeedUpdate){
            permission.setNodePolicy(nodePolicy);
            this.updatePermission(permission);
        }
        return true;
    }
    
    
    public void updatePermissionRef(Integer category, String flowperm_name, Long accountId) throws BusinessException {
    	 ModuleType type = ModuleType.getEnumByKey(category);
         String configCategory = "";
         if (ModuleType.collaboration.name().equals(type.name()) || ModuleType.form.name().equals(type.name())) {
             configCategory = EnumNameEnum.col_flow_perm_policy.name();
         } else if (ModuleType.edocSend.name().equals(type.name())) {
             configCategory = EnumNameEnum.edoc_send_permission_policy.name();
         } else if (ModuleType.edocRec.name().equals(type.name())) {
             configCategory = EnumNameEnum.edoc_rec_permission_policy.name();
         } else if (ModuleType.edocSign.name().equals(type.name())) {
             configCategory = EnumNameEnum.edoc_qianbao_permission_policy.name();
 		} else if (ModuleType.info.name().equals(type.name())) {
 	     	configCategory = EnumNameEnum.info_send_permission_policy.name();
         }
         String[] boundList = flowperm_name.split(",");
         if (boundList != null && boundList.length > 0) {
             for (int i = 0; i < boundList.length; i++) {
                 this.updatePermissionRef(configCategory, boundList[i],accountId);
             }
         }
    }
    
    public List<PermissionOperation> getPermissionOperation(EnumNameEnum ene) {
        List<PermissionOperation> list = this.permissionOperations.get(ene.name());
        //重新复制一个对象，防止删除时错误删除缓存中数据
        List<PermissionOperation> li = new ArrayList<PermissionOperation>();
        for(PermissionOperation po:list){
        	if(!AppContext.hasPlugin("calendar") && "permission.operation.Transform".equals(po.getLabel())){
        		continue;
        	}
        	if(!AppContext.hasPlugin("doc") && ("Archive".equals(po.getKey()) || "Pigeonhole".equals(po.getKey()))) {
        		continue;
        	}
        	li.add(po);
        }
        return li;
    }

    
    public List<Permission> getPermissionsByStatus(String category, Integer status, Long accountId) throws BusinessException {
        List<ConfigItem> list = configManager.listAllConfigByCategory(category, accountId);
        Collections.sort(list);
        List<Permission> fList = new ArrayList<Permission>();
        for (ConfigItem item : list) {
            Permission permission = getPermission(item);
            if( null!=permission.getNodePolicy() && null!=permission.getNodePolicy().getIsEnabled() && permission.getNodePolicy().getIsEnabled().intValue()!=status.intValue()){
                continue;
            }
            fList.add(permission);
        }
        return fList;
    }

    
    public boolean isSystemPermission(String category, String configItem, Long accountId)
            throws BusinessException {
        List<Permission> list = this.getPermissionsByCategory(category, accountId);
        if(this.isSystem(configItem, list)){
            return true;
        }
        return false;
    }

    private boolean isSystem(String name, List<Permission> list) {
        for(Permission fp: list){
              if(name.equals(fp.getName())){
                  if(fp.getType().intValue() == 0) {
                      return true;
                  }
              }
          }
        return false;
    }

    public List<Permission> getPermissions4WFNodeProperties(String appName,String categoryName,String curPermName,Long accountId,boolean isTemplete,Long formAppId) throws BusinessException{
    	Long accountAdminId = orgManager.getAdministrator(accountId).getId();
    	Long userId = AppContext.currentUserId();
    	
    	String realCategoryName= categoryName;
    	if("form_flow_perm_policy".equals(categoryName)){
    		realCategoryName= "col_flow_perm_policy";
    	}
        List<Permission> newList = new ArrayList<Permission>();
        List<Permission> list = getPermissionsByCategory(realCategoryName,accountId); 
        String nodeType=AppContext.getRawRequest().getParameter("nodeType");
        if (Strings.isNotBlank(nodeType) && "StartNode".equals(nodeType)) {
        	Permission permission = null;
        	if (categoryName.equals(EnumNameEnum.edoc_new_send_permission_policy.name()) || categoryName.equals(EnumNameEnum.edoc_new_qianbao_permission_policy.name())) {
        		permission = this.getPermission(categoryName, "niwen", accountId);
        	}else if (categoryName.equals(EnumNameEnum.edoc_new_rec_permission_policy.name())
        			|| categoryName.equals(EnumNameEnum.edoc_new_change_permission_policy.name())) {
        		permission = this.getPermission(categoryName, "dengji", accountId);
			}else if (categoryName.equals(EnumNameEnum.edoc_new_qianbao_permission_policy.name())) {
        		permission = this.getPermission(categoryName, "niwen", accountId);
			}
        	
        	if (permission != null) {
        		list.clear();
        		list.add(permission);
			}
		}
        List<String> otherFormoperationCodes = new ArrayList<String>();
    	if (formAppId != null) {
    		//通过接口获取
    		List<CtpPermissionOperation> formAppIdOperation = ctpPermissionOperationManager.findBindFormList();
    		for (CtpPermissionOperation op : formAppIdOperation) {
    			if (!formAppId.equals(op.getFormAppId())) {
    				otherFormoperationCodes.add(String.valueOf(op.getId()));
    			}
    		}
    	}
    	
        for (Permission perm : list) {
            //如果是协同 过滤掉 核定、表单审核
            if(!"form_flow_perm_policy".equals(categoryName) && ("vouch".equals(perm.getName())||"formaudit".equals(perm.getName()))){
                continue;
            }
            //如果不是启用的要过滤掉，但是如果当前节点权限处于非启用状态不要过滤
            if(Permission.Node_isNotActive.equals(perm.getIsEnabled())&&!perm.getName().equals(curPermName)){
                continue;
            }
            //如果当前是自由协同，则过滤掉新闻审核和公文审核
            if ("col_flow_perm_policy".equals(categoryName) && !isTemplete && ("newsaudit".equals(perm.getName())||"bulletionaudit".equals(perm.getName()))) {
                continue;
            }
            
            //没有插件
            if("newsaudit".equals(perm.getName()) && !AppContext.hasPlugin("news")){
            	continue;
            }
            
            if("bulletionaudit".equals(perm.getName()) && !AppContext.hasPlugin("bulletin")){
            	continue;
            }
            
            if (!isGovPermission(perm.getCategory())) {//公文节点权限不进行该过滤
            	if (null != formAppId) {
            		boolean isNoPermissions = false;
            		if (perm.getNodePolicy().getCreateUserId() != null) {
            			//不是单位管理员创建的
            			if (!accountAdminId.equals(perm.getNodePolicy().getCreateUserId())) {
            				//不是当前用户创建的
            				if (!userId.equals(perm.getNodePolicy().getCreateUserId())) {
            					isNoPermissions = true;
            				} else {//当前用户创建的,是其他表单节点权限
            					boolean otherFormOp = false;
            					//判断基础\高级\普通操作中是否包含不是该表单的操作(表单绑定操作除外)
            					String advancedStr = perm.getNodePolicy().getAdvancedAction();
            					if (Strings.isNotBlank(advancedStr)) {
            						String[] advancedArr = advancedStr.split(",");
            						if (Strings.isNotEmpty(otherFormoperationCodes)) {
            							for (String ad : advancedArr) {
            								if (otherFormoperationCodes.contains(ad)) {
            									otherFormOp = true;
            									break;
            								}
            							}
            						}
            					}
            					String basicStr = perm.getNodePolicy().getBaseAction();
            					if (Strings.isNotBlank(basicStr)) {
            						String[] basicArr = basicStr.split(",");
            						if (!otherFormOp && Strings.isNotEmpty(otherFormoperationCodes)) {
            							for (String ad : basicArr) {
            								if (otherFormoperationCodes.contains(ad)) {
            									otherFormOp = true;
            									break;
            								}
            							}
            						}
            					}
            					String commonStr = perm.getNodePolicy().getCommonAction();
            					if (Strings.isNotBlank(commonStr)) {
            						String[] commonArr = commonStr.split(",");
            						if (!otherFormOp && Strings.isNotEmpty(otherFormoperationCodes)) {
            							for (String ad : commonArr) {
            								if (otherFormoperationCodes.contains(ad)) {
            									otherFormOp = true;
            									break;
            								}
            							}
            						}
            					}
            					if (otherFormOp) {
            						isNoPermissions = true;
            					}
            				}
            			}
            		}
            		if (isNoPermissions) {
            			continue;
            		}
            	} else {//仅可获取单位管理员创建的节点权限
            		if (perm.getNodePolicy().getCreateUserId() != null && !accountAdminId.equals(perm.getNodePolicy().getCreateUserId())) {
            			continue;
            		}
            	}
            }
            newList.add(perm);
        }
        return newList;
    	
    }
    
    public List<Permission> getPermissions4WFNodeProperties(String appName,String categoryName,String curPermName,Long accountId,boolean isTemplete) throws BusinessException {
    	return getPermissions4WFNodeProperties(appName, categoryName, curPermName, accountId, isTemplete, null);
    }
    /**
     * VJoin新建单位的时候预置节点权限
     * @param accountId
     * @throws BusinessException
     */
    public void saveInitializePermissions4VJoin(long accountId)throws BusinessException{
        //遍历出所有公文和协同的数据，重新赋值accountId
        List<ConfigItem> list = new ArrayList<ConfigItem>();
        list.addAll(configManager.listAllConfigByCategory(EnumNameEnum.col_flow_perm_policy.name()));
        for(ConfigItem item : list){
        	
        	if(!"newCol".equals(item.getConfigItem()) && !"collaboration".equals(item.getConfigItem())){
        		continue;
        	}
        	
            ConfigItem newItem = new ConfigItem();
            newItem.setNewId();
            newItem.setConfigCategory(item.getConfigCategory());
            newItem.setConfigDescription(item.getConfigDescription());
            newItem.setConfigItem(item.getConfigItem());
            newItem.setExtConfigValue(item.getExtConfigValue());
            newItem.setConfigValue(item.getConfigValue());
            newItem.setConfigType(item.getConfigType());
            newItem.setCreateDate(new Timestamp(new Date().getTime()));
            newItem.setModifyDate(new Timestamp(new Date().getTime()));
            if(item.getSort() != null) {
            	newItem.setSort(item.getSort());
            }
            newItem.setOrgAccountId(accountId);
            configManager.addConfigItem(newItem);
        }
    }
    
    public void savePermissionsByAccountId(long accountId)throws BusinessException{
        
       /* LOGGER.info("新增节点权限 for " + accountId);
        
        //遍历出所有公文和协同的数据，重新赋值accountId
        List<ConfigItem> list = new ArrayList<ConfigItem>();
        list.addAll(configManager.listAllConfigByCategory(EnumNameEnum.edoc_new_change_permission_policy.name()));
        list.addAll(configManager.listAllConfigByCategory(EnumNameEnum.edoc_new_send_permission_policy.name()));
        list.addAll(configManager.listAllConfigByCategory(EnumNameEnum.edoc_new_rec_permission_policy.name()));
        list.addAll(configManager.listAllConfigByCategory(EnumNameEnum.edoc_new_qianbao_permission_policy.name()));
        list.addAll(configManager.listAllConfigByCategory(EnumNameEnum.col_flow_perm_policy.name()));
        list.addAll(configManager.listAllConfigByCategory(EnumNameEnum.office_flow_perm_policy.name()));
        
        LOGGER.info("new policy size " + list.size());
        
       // Map<Long,ConfigItem> emap = new HashMap<Long,ConfigItem>();
        for(ConfigItem item : list){
            ConfigItem newItem = new ConfigItem();
            newItem.setNewId();
            newItem.setConfigCategory(item.getConfigCategory());
            newItem.setConfigDescription(item.getConfigDescription());
            newItem.setConfigItem(item.getConfigItem());
            newItem.setExtConfigValue(item.getExtConfigValue());
            newItem.setConfigValue(item.getConfigValue());
            newItem.setConfigType(item.getConfigType());
            newItem.setCreateDate(new Timestamp(new Date().getTime()));
            newItem.setModifyDate(new Timestamp(new Date().getTime()));
            if(item.getSort() != null) {
            	newItem.setSort(item.getSort());
            }
            newItem.setOrgAccountId(accountId);
            configManager.addConfigItem(newItem);
            
			  if(item.getConfigCategory().equals(EnumNameEnum.edoc_send_permission_policy.name())
					||item.getConfigCategory().equals(EnumNameEnum.edoc_rec_permission_policy.name())
					||item.getConfigCategory().equals(EnumNameEnum.edoc_qianbao_permission_policy.name()) ){
			     emap.put(newItem.getId(), item);
			}
        }
		       Map<String, EdocHandlerInterface> superviseHandlers = AppContext.getBeansOfType(EdocHandlerInterface.class);
		if(superviseHandlers!=null){
		    Collection<EdocHandlerInterface> handlers = superviseHandlers.values();
		    if(handlers!=null){
		        for(Iterator<EdocHandlerInterface> it = handlers.iterator();it.hasNext();){
		            EdocHandlerInterface handler = it.next();
		            handler.saveEdocElementPermByAccountId(emap);
		            continue;
		        }
		    }else{
		        LOGGER.error("新建单位，复制节点权限没有找到复制公文元素处理器.handlers为空");
		    }
		}else{
		    LOGGER.error("新建单位，复制节点权限没有找到复制公文元素处理器.");
		}
        
        LOGGER.info("新增节点权限 for " + accountId + " end");*/
    }

    
    public List<PermissionVO> getPermission(String category, Integer status,Long accountId) throws BusinessException {
        List<PermissionVO> listVO = new ArrayList<PermissionVO>();
        List<ConfigItem> list = configManager.listAllConfigByCategory(category, accountId);;
        for (ConfigItem item : list) {
            PermissionVO perm = configItemToPermissionVO(item);
            if(null!=perm && perm.getIsEnabled().intValue()!=status.intValue()){
                continue;
            }
            listVO.add(perm);
        }
        return listVO;
    }

	
	public boolean isContainsAction(Permission permission, String action)
			throws BusinessException {
		String baseAction = permission.getNodePolicy().getBaseAction();
		String commonAction = permission.getNodePolicy().getCommonAction();
		String advancedAction = permission.getNodePolicy().getAdvancedAction();
		if(baseAction.contains(action) || commonAction.contains(action) || advancedAction.contains(action)){
			return true;
		}
		return false;
	}

	
	public boolean isContainsExchangeType(Permission permission) throws BusinessException {
		return this.isContainsAction(permission, "EdocExchangeType");
	}

	/**
	 * 
	 * @param code:操作标识
	 * @param category:协同col_flow_perm_policy
	 * @return
	 * @throws BusinessException 
	 */
	@Override
	public List<String> operationIsRefByPermission (Map<String, String> params) throws BusinessException {
		List<String> IsRefByNameList= new ArrayList<String>();
		String code = params.get("code");
		String category = params.get("category");
		String accountIdStr = params.get("accountId");
		
		if (Strings.isBlank(category)) {
			category = EnumNameEnum.col_flow_perm_policy.name();//默认取协同
		}
		Long accountId = -1L;
		if (Strings.isNotBlank(accountIdStr)) {
			accountId = Long.valueOf(accountIdStr);
		} else {
			accountId = AppContext.currentAccountId();
		}
		
		
		List<ConfigItem> configItemList = configManager.listAllConfigByCategory(category, accountId);
		for (ConfigItem item : configItemList) {
			NodePolicy nodePolicy = new NodePolicy();
            try{
                nodePolicy = getPermission(item).getNodePolicy();;
            }catch(Exception e){
                throw new BusinessException("解析节点权限错误：id="+item.getId()+" ConfigCategoryName="+item.getConfigCategoryName(),e);
            }
            String configType = item.getConfigType();
            String label = "";
            if(Strings.isNotBlank(configType)){
                if(Permission.Node_Type_System.toString().equals(configType)){
                    //权限名称
                    label = enumManagerNew.getEnumItemLabel(EnumNameEnum.valueOf(item.getConfigCategory()), item.getConfigItem());
                }else{
                	label = item.getConfigItem();
                }
            }
            if (nodePolicy != null) {
            	//基本操作
            	String basicOperation = nodePolicy.getBaseAction();
            	if (Strings.isNotBlank(basicOperation)) {
            		List<String> basic = Arrays.asList(basicOperation.split(","));
            		if (basic.contains(code)) {
            			IsRefByNameList.add(label);
            			continue;
            		}
            	}
                //高级操作
                String advancedOperation = nodePolicy.getAdvancedAction();
                if (Strings.isNotBlank(advancedOperation)) {
            		List<String> advanced = Arrays.asList(advancedOperation.split(","));
            		if (advanced.contains(code)) {
            			IsRefByNameList.add(label);
            			continue;
            		}
            	}
                //常用操作
                String commonOperation = nodePolicy.getCommonAction();
                if (Strings.isNotBlank(commonOperation)) {
            		List<String> common = Arrays.asList(commonOperation.split(","));
            		if (common.contains(code)) {
            			IsRefByNameList.add(label);
            			continue;
            		}
            	}
            }
		}
		
		return IsRefByNameList;
		
	}
	
	@Override
	public List<String> attitudeIsRefByPermission (Map<String, String> params) throws BusinessException {
		List<String> IsRefByNameList= new ArrayList<String>();
		String code = params.get("code");
		String category = params.get("category");
		String accountIdStr = params.get("accountId");
		
		if (Strings.isBlank(category)) {
			category = EnumNameEnum.col_flow_perm_policy.name();//默认取协同
		}
		Long accountId = -1L;
		if (Strings.isNotBlank(accountIdStr)) {
			accountId = Long.valueOf(accountIdStr);
		} else {
			accountId = AppContext.currentAccountId();
		}
		
		
		List<ConfigItem> configItemList = configManager.listAllConfigByCategory(category, accountId);
		for (ConfigItem item : configItemList) {
			NodePolicy nodePolicy = new NodePolicy();
            try{
                nodePolicy = getPermission(item).getNodePolicy();;
            }catch(Exception e){
                throw new BusinessException("解析节点权限错误：id="+item.getId()+" ConfigCategoryName="+item.getConfigCategoryName(),e);
            }
            String configType = item.getConfigType();
            String label = "";
            if(Strings.isNotBlank(configType)){
                if(Permission.Node_Type_System.toString().equals(configType)){
                    //权限名称
                    label = enumManagerNew.getEnumItemLabel(EnumNameEnum.valueOf(item.getConfigCategory()), item.getConfigItem());
                }else{
                	label = item.getConfigItem();
                }
            }
            if (nodePolicy != null) {
            	//基本操作
            	String attitude = nodePolicy.getAttitude();
            	if (Strings.isNotBlank(attitude)) {
            		List<String> att = Arrays.asList(attitude.split(","));
            		if (att.contains(code)) {
            			IsRefByNameList.add(label);
            		}
            	}
            }
		}
		
		return IsRefByNameList;
		
	}
	
	/**
	 * 获取节点权限设置操作中已选和备选动作
	 * @param params
	 * @throws BusinessException 
	 */
	@AjaxAccess
	@Override
	@CheckRoleAccess(roleTypes={Role_NAME.AccountAdministrator,Role_NAME.EdocManagement,Role_NAME.InfoManager,Role_NAME.FormAdmin,Role_NAME.BusinessDesigner})
	public Map<String, List<PermissionOperation>> getPermissionOption(Map<String, String> params) throws BusinessException {
		Map<String, List<PermissionOperation>> returnOperation = new HashMap<String, List<PermissionOperation>>();
		
		String currentUserId = params.get("currentUserId");
        String param = params.get("param");
        String notInclude = params.get("notInclude");
        String exists = params.get("exists");
        String isEdoc = params.get("isEdoc");
        String portalValue = params.get("portalValue");
        String formAppId = params.get("formAppId");
        String permissionName = params.get("permissionName");
        
        V3xOrgMember user = new V3xOrgMember();
        if (Strings.isNotBlank(currentUserId)) {
        	user = orgManager.getMemberById(Long.valueOf(currentUserId));
        } else {
        	user = orgManager.getMemberById(AppContext.currentUserId());
        }
        
        //常用、高级操作
        List<PermissionOperation> metadata1 = null;
        //基本操作
        List<PermissionOperation> metadata2 = null;
        //协同权限设置--
        if(EnumNameEnum.col_flow_perm_policy.name().equals(isEdoc)){
        	//协同节点权限操作
        	metadata1 = this.getPermissionOperation(EnumNameEnum.node_control_action);
        	metadata2 = this.getPermissionOperation(EnumNameEnum.col_basic_action);
        	
        	List<PermissionOperation> newColPermissionList = new ArrayList<PermissionOperation>();
        	
        	if (!"inform".equals(permissionName) && !"newCol".equals(permissionName)) {
        		// 增加节点权限数据逻辑
        		List<PermissionOperation> presetPermission = ctpPermissionOperationManager.findPresetPermissionOperationVOList();
        		List<PermissionOperation> customPermission = ctpPermissionOperationManager.findCustomPermissionOperationVOList();
        		
        		
        		if (Strings.isNotEmpty(presetPermission)) {
        			metadata1.addAll(presetPermission);
        			if(!"newCol".equals(permissionName)){
        				metadata2.addAll(presetPermission);
        			}
        		}
        		if (Strings.isNotEmpty(customPermission)) {
        			metadata1.addAll(customPermission);
        			if(!"newCol".equals(permissionName)){
        				metadata2.addAll(customPermission);
        			}
        		}
        	} else if ("newCol".equals(permissionName)) {
        		Map<String, String> addNodeParam = new HashMap<String, String>();
        		addNodeParam.put("actionType", "processAction");
        		addNodeParam.put("actionCode", "AddNode");
        		addNodeParam.put("onlyAction", "true");
        		addNodeParam.put("accountId", String.valueOf(user.getOrgAccountId()));
        		List<CtpPermissionOperation> addNodePermission = ctpPermissionOperationManager.getCtpPermissionOperationByAction(addNodeParam);
        		for (CtpPermissionOperation op : addNodePermission) {
                	PermissionOperation operation = new PermissionOperation();
                	operation.setId(op.getId());
                	operation.setKey(String.valueOf(op.getId()));
                	operation.setLabel(op.getLabel());
                	operation.setType(String.valueOf(op.getCategoryId()));
                	operation.setIsSystem(Boolean.TRUE);
                	operation.setFormAppId(op.getFormAppId());
                	newColPermissionList.add(operation);
                }
        		if (Strings.isNotEmpty(newColPermissionList)) {
        			metadata1.addAll(newColPermissionList);
        		}
        	}
            
            if(!"newCol".equals(permissionName)){
            	//(去掉基础操作配置文件中增加应用磁贴,保证顺序保持一致,显示在文本框之后
            	this.removeItem(metadata2, "AppTile");
	            //分割线
	            PermissionOperation lineOperation = ctpPermissionOperationManager.newLinePeration();
	            metadata1.add(lineOperation);
            	metadata2.add(lineOperation);
            	//文本框
	            PermissionOperation labelOperation = ctpPermissionOperationManager.newLablePeration();
	            metadata1.add(labelOperation);
            	metadata2.add(labelOperation);
            	//应用磁贴
            	PermissionOperation portal = new PermissionOperation();
            	portal.setKey("AppTile");
            	portal.setLabel("permission.operation.AppTile");
            	portal.setType(String.valueOf(PermissionOperationCategoryEnmus.application.getKey()));
            	portal.setIsSystem(Boolean.TRUE);
            	metadata1.add(portal);
            	metadata2.add(portal);
            }
            
            // 应用磁贴选择的具体值
            if (Strings.isNotBlank(portalValue) && portalApi != null) {
            	List<ImagePortletLayout> portalList = portalApi.getImagePorletsByIds(Arrays.asList(portalValue.split(",")));
            	List<PermissionOperation> portalOperation = this.portalToPermission(portalList);
            	if (Strings.isNotEmpty(portalOperation)) {
            		metadata1.addAll(portalOperation);
                    metadata2.addAll(portalOperation);
            	}
            }
            
         //如果是表单审核与核定节点那么就没有-提交 没有修改正文
            if("formaudit".equals(permissionName) || "vouch".equals(permissionName) ){
                if(Strings.isNotEmpty(notInclude)){
                    notInclude = notInclude+",ContinueSubmit,Edit";
                }else{
                    notInclude = "ContinueSubmit,Edit";
                }
            }
            //如果是知会的话就去掉 回退、终止、减签、修改正文、修改附件、加签、当前会签、指定回退、撤销
            if ("inform".equals(permissionName)) {
                if(Strings.isNotEmpty(notInclude)){
                    notInclude = notInclude+",Return,Terminate,RemoveNode,Edit,allowUpdateAttachment,AddNode,JointSign,SpecifiesReturn,Cancel,Sign,SuperviseSet,moreSign";
                }else{
                    notInclude = "Return,Terminate,RemoveNode,Edit,allowUpdateAttachment,AddNode,JointSign,SpecifiesReturn,Cancel,Sign,SuperviseSet,moreSign";
                }
            }
            //新建去掉移交
            if ("newCol".equals(permissionName)) {
            	this.removeItem(metadata1, "Transfer");
            }
            if("newCol".equals(permissionName)){
                metadata2.addAll(metadata1);
                PermissionVO permission = this.getPermission(Long.valueOf(2309L));
                String basic = permission.getBasicOperation();
                List<PermissionOperation> basicList = new ArrayList<PermissionOperation>(); 
                if(Strings.isNotBlank(basic)){
                    List<String> basicOperation = Arrays.asList(basic.split(","));
                    for(PermissionOperation cei:metadata2){
                    	if((basicOperation.contains(cei.getKey()) 
                    			|| newColPermissionList.contains(cei) 
                    			|| "AppTile".equals(cei.getKey())
                    			|| "AddNode".equals(cei.getKey())
                    			|| !cei.getIsSystem()) 
                    			&& !basicList.contains(cei)){
                    		basicList.add(cei);
                    	}
                    }
                }
                metadata2 = basicList;
                metadata1 = Collections.emptyList();
            } else {//基本操作里面不能有发起节点的东西。需要移除。xml里面没有区分
                List<PermissionOperation> removePermission = new ArrayList<PermissionOperation>();
                List<String> removeNames = CommonTools.newArrayList("EditWorkFlow", "Pigeonhole", "RepeatSend");
                for (PermissionOperation po : metadata2) {
                    if (removeNames.contains(po.getKey())) {
                        removePermission.add(po);
                    }
                }
                metadata2.removeAll(removePermission);
            }
        }else if(EnumNameEnum.info_send_permission_policy.name().equals(isEdoc)){
            //信息节点权限操作
            metadata1 = this.getPermissionOperation(EnumNameEnum.info_node_control_action);//高级操作
            metadata2 = this.getPermissionOperation(EnumNameEnum.info_basic_action);//基础操作
        } else {
            //公文节点权限操作
            //阅读权限特殊处理。
			if("yuedu".equals(permissionName) && ("edoc_send_permission_policy".equals(isEdoc)||"edoc_qianbao_permission_policy".equals(isEdoc)
					||"edoc_new_send_permission_policy".equals(isEdoc)||"edoc_new_qianbao_permission_policy".equals(isEdoc))){
				metadata1 = new ArrayList<PermissionOperation>();
				//添加'知会'
				PermissionOperation infom = new PermissionOperation();
				infom.setKey("Infom");
				infom.setLabel("permission.operation.Infom");
				metadata1.add(infom);
				//添加'传阅'
				PermissionOperation passRead = new PermissionOperation();
				passRead.setKey("PassRead");
				passRead.setLabel("permission.operation.PassRead");
				metadata1.add(passRead);
				//添加'转公告'
				PermissionOperation transmitBulletin = new PermissionOperation();
				transmitBulletin.setKey("TransmitBulletin");
				transmitBulletin.setLabel("permission.operation.TransmitBulletin");
				metadata1.add(transmitBulletin);
				//添加'部门归档' v571没有部门归档
/*				PermissionOperation departPigeonhole = new PermissionOperation();
				departPigeonhole.setKey("DepartPigeonhole");
				departPigeonhole.setLabel("permission.operation.DepartPigeonhole");
				metadata1.add(departPigeonhole);*/
				//添加'终止'
				PermissionOperation terminate = new PermissionOperation();
				terminate.setKey("Terminate");
				terminate.setLabel("permission.operation.Terminate");
				metadata1.add(terminate);
				//添加'文单签批'
				PermissionOperation htmlSign = new PermissionOperation();
				htmlSign.setKey("HtmlSign");
				htmlSign.setLabel("permission.operation.HtmlSign");
				metadata1.add(htmlSign);
				//添加'文单签批'
				PermissionOperation zsj = new PermissionOperation();
				zsj.setKey("Transform");
				zsj.setLabel("permission.operation.Transform");
				metadata1.add(zsj);
			}else{
				metadata1 = this.getPermissionOperation(EnumNameEnum.edoc_node_control_action);
				if("yuedu".equals(permissionName) && ("edoc_rec_permission_policy".equals(isEdoc))){
					//移除移交(收文阅读)
					this.removeItem(metadata1, "Transfer");
				}
			}
			//公文中基本操作当是收文、签报时，要去掉‘交换类型’
			metadata2 = this.getPermissionOperation(EnumNameEnum.edoc_basic_action);
			//公文起始节点基础操作只能有固定的备选
			if ((EnumNameEnum.edoc_new_send_permission_policy.name().equals(isEdoc) && "niwen".equals(permissionName))
	        		|| (EnumNameEnum.edoc_new_rec_permission_policy.name().equals(isEdoc) && "dengji".equals(permissionName))
	        		|| (EnumNameEnum.edoc_new_qianbao_permission_policy.name().equals(isEdoc) && "niwen".equals(permissionName))) {
				List<PermissionOperation> allPermissionOperations = new ArrayList<PermissionOperation>();
				allPermissionOperations.addAll(metadata1);
				allPermissionOperations.addAll(metadata2);
				
				metadata2 = new ArrayList<PermissionOperation>();
				metadata2.addAll(this.getStartNodePermission(allPermissionOperations, permissionName));
			}
			if("edoc_rec_permission_policy".equals(isEdoc)||"edoc_qianbao_permission_policy".equals(isEdoc)){
				this.removeItem(metadata2, "EdocExchangeType");
			}
			
			
			//收文节点权限 删除转收文操作
			if("edoc_send_permission_policy".equals(isEdoc)||"edoc_qianbao_permission_policy".equals(isEdoc)||"zhihui".equals(permissionName) || "edoc_new_send_permission_policy".equals(isEdoc)||"edoc_new_qianbao_permission_policy".equals(isEdoc)){
				this.removeItem(metadata1, "TurnRecEdoc");
			}
			
			//发文屏蔽   没有转发文、签收、分办
			if("edoc_new_send_permission_policy".equals(isEdoc)){
				this.removeItem(metadata1, "Zhuanfawen");
				this.removeItem(metadata2, "ReSign");
				this.removeItem(metadata2, "Distribute");
				//单组织需要屏蔽联合发文
				String version = ProductEditionEnum.getCurrentProductEditionEnum().getValue();
				if (version.equals(ProductEditionEnum.government.getValue())) {// 单组织
					this.removeItem(metadata1, "JointlyIssued");
				}
			}
			//收文屏蔽 没有word转pdf操作、分送操作、转发文、签收、分办、wps转ofd、联合发文
			if("edoc_new_rec_permission_policy".equals(isEdoc)){
				this.removeItem(metadata1, "TanstoPDF");
				this.removeItem(metadata2, "FaDistribute");
				this.removeItem(metadata2, "ReSign");
				this.removeItem(metadata2, "Distribute");
				this.removeItem(metadata1, "TransToOfd");
				this.removeItem(metadata1, "JointlyIssued");
			}
			//签报屏蔽  没有分送操作、转发文、签收、分办、wps转ofd、联合发文
			if("edoc_new_qianbao_permission_policy".equals(isEdoc)){
				this.removeItem(metadata2, "FaDistribute");
				this.removeItem(metadata1, "Zhuanfawen");
				this.removeItem(metadata2, "ReSign");
				this.removeItem(metadata2, "Distribute");
				this.removeItem(metadata1, "JointlyIssued");
			}
			//交换屏蔽  没有终止、知会、指定回退、word转pdf、正文套红、撤销、转办、转发文、分送、wps转ofd、联合发文
			if("edoc_new_change_permission_policy".equals(isEdoc)){
				//this.removeItem(metadata1, "Terminate");
				this.removeItem(metadata1, "Infom");
				this.removeItem(metadata1, "SpecifiesReturn");
				this.removeItem(metadata1, "TanstoPDF");
				this.removeItem(metadata1, "EdocTemplate");
				this.removeItem(metadata1, "Cancel");
				this.removeItem(metadata1, "TurnRecEdoc");
				this.removeItem(metadata1, "Zhuanfawen");
				this.removeItem(metadata1, "Zhuanshiwu");
				this.removeItem(metadata2, "FaDistribute");
				this.removeItem(metadata1, "TransToOfd");
				this.removeItem(metadata1, "JointlyIssued");
				this.removeItem(metadata1, "PDFSign");
				this.removeItem(metadata1, "SignChange");
			}
			
			ConfigManager configManager =  (ConfigManager) AppContext.getBean("configManager");
			ConfigItem item = configManager.getConfigItem(IConfigPublicKey.GOVDOC_SWITCH_KEY, "zhuanfawenTactics", AppContext.currentAccountId());
			if(item == null || !"yes".equals(item.getConfigValue())) {
				this.removeItem(metadata1, "Zhuanfawen");
			}
        }
        removePartActionByPlugin(metadata2, metadata1, null);

        List<PermissionOperation> rightList = new ArrayList<PermissionOperation>();
        List<PermissionOperation> leftList = new ArrayList<PermissionOperation>();
        //基础操作\常用操作编辑或者高级操作 编辑时 都需要相互排除
        List<PermissionOperation> metadataList = new ArrayList<PermissionOperation>();
        if(!"".equals(param) && ("common".equals(param) || "advanced".equals(param))){ 
            String[] val = null;
            if(Strings.isNotBlank(notInclude)){
                val = notInclude.split(",");
                String[] exitsData = exists.split(","); 
                leftList = this.getItemList(metadata1,val,leftList,false);
                rightList = this.getItemList(metadata1,exitsData,rightList, true);
                //是否存在分割线
                if (EnumNameEnum.col_flow_perm_policy.name().equals(isEdoc)) {
                	if (!checkCodeInOperation(leftList, "line")) {
                		PermissionOperation lineOperation = ctpPermissionOperationManager.newLinePeration();
                		leftList.add(lineOperation);
                	}
                	if ("common".equals(param) && !checkCodeInOperation(leftList, "label")) {
                		PermissionOperation lineOperation = ctpPermissionOperationManager.newLablePeration();
                		leftList.add(lineOperation);
                	}
                }
                //过滤非当前表单动作
                ExcludeFormPermission(formAppId, leftList);
                metadataList.addAll(leftList);
                returnOperation.put("existMetaData", rightList);
            }else{
            	ExcludeFormPermission(formAppId, metadata1);
            	metadataList.addAll(metadata1);
            }
        }else if(!"".equals(param) && "basic".equals(param)){
            if(Strings.isNotBlank(notInclude)){
                String[] val = notInclude.split(",");
                String[] exitsData = exists.split(","); 
                leftList = this.getItemList(metadata2,val,leftList,false);
                rightList = this.getItemList(metadata2,exitsData,rightList, true);
                
                if(!"newCol".equals(permissionName) && EnumNameEnum.col_flow_perm_policy.name().equals(isEdoc)){
                	if (!checkCodeInOperation(leftList, "line")) {
                		PermissionOperation lineOperation = ctpPermissionOperationManager.newLinePeration();
                		leftList.add(lineOperation);
                	}
                	if (!checkCodeInOperation(leftList, "label")) {
                		PermissionOperation lineOperation = ctpPermissionOperationManager.newLablePeration();
                		leftList.add(lineOperation);
                	}
                }
                
                ExcludeFormPermission(formAppId, leftList);
                
                metadataList.addAll(leftList);
                returnOperation.put("existMetaData", rightList);
            }else {
            	ExcludeFormPermission(formAppId, metadata1);
            	metadataList.addAll(metadata2);
            }
            
        }
        
        if (EnumNameEnum.col_flow_perm_policy.name().equals(isEdoc)) {
        	returnOperation.put("metadata", order4Permission(metadataList, param));
        } else {
        	returnOperation.put("metadata", metadataList);
        }
        return returnOperation;
	}
	
	@Override
	public void removePartActionByPlugin(List<PermissionOperation> basicList, List<PermissionOperation> commonList, List<PermissionOperation> advancedList) {
		if(!AppContext.hasPlugin("doc")){
			remove("Pigeonhole", basicList);
			remove("Archive", basicList);
			
			//公文部门归档
			remove("DepartPigeonhole", commonList);
			remove("DepartPigeonhole", advancedList);	
		}
		
		if(!AppContext.hasPlugin("calendar")){
			remove("Transform", commonList);
			remove("Transform", advancedList);
		}
		if(!AppContext.hasPlugin("bulletin")){//转公告-公文
			remove("TransmitBulletin", commonList);
			remove("TransmitBulletin", advancedList);
		}
		
		boolean wpstransPluginEnable = "true".equals(SystemProperties.getInstance().getProperty("wpstrans.enable")) ? true : false;
		if(!wpstransPluginEnable){	
			remove("TransToOfd", commonList);
		}
	}
	
	@Override
	public List<PermissionOperation> removeItem(List<PermissionOperation> itemList,String key){
        if(itemList != null && itemList.size()>0){
			for(Iterator<PermissionOperation> item = itemList.iterator();item.hasNext();){
				PermissionOperation po = item.next();
				if(po.getKey().equals(key)){
					item.remove();
					break;
				}
			}
        }
        return itemList;
    }
	
	/**
	 * 构造应用磁贴操作
	 * @param portalList
	 * @return
	 */
	private List<PermissionOperation> portalToPermission(List<ImagePortletLayout> portalList) {
    	List<PermissionOperation> permissionList = new ArrayList<PermissionOperation>();
    	
    	for (ImagePortletLayout portal : portalList) {
    		PermissionOperation permission = new PermissionOperation();
    		permission.setIsSystem(Boolean.FALSE);
    		permission.setKey(portal.getId());
    		permission.setLabel(portal.getDisplayName());
    		permission.setType(String.valueOf(PermissionOperationCategoryEnmus.portal.getKey()));
    		
    		String iconName = portal.getImageLayouts().get(0).getImageUrl();
    		if(iconName.indexOf("vp-") == -1){
    			permission.setIcon("vportal vp-" + iconName.replace(".png", "").replace("d_", ""));
    		} else {
    			permission.setIcon(iconName);
    		}
    		permissionList.add(permission);
    	}
    	
    	return permissionList;
    }
	
	private void remove(String key,List<PermissionOperation> list) {
    	if(Strings.isNotEmpty(list)){
    		for(Iterator<PermissionOperation> it = list.iterator();it.hasNext();){
    			PermissionOperation po = it.next();
    			if(key.equals(po.getKey())){
    				it.remove();
    			}
    		}
    	}
    }
	
	/**
     * 过滤非当前表单操作
     * @param formAppIdStr
     * @param leftList
     */
    private void ExcludeFormPermission(String formAppIdStr, List<PermissionOperation> leftList) {
    	if (Strings.isNotBlank(formAppIdStr)) {
    		Long formAppId = Long.valueOf(formAppIdStr);
    		if(leftList != null && leftList.size()>0){
    			for(Iterator<PermissionOperation> item = leftList.iterator();item.hasNext();){
    				PermissionOperation po = item.next();
    				if (po.getFormAppId() != null && !formAppId.equals(po.getFormAppId())) {
    					item.remove();
    				}
    			}
    		}
    		
    	}
    }
    /**
     * 备选中始终需要有分割线,没有时追加
     * @param leftList
     * @throws BusinessException 
     */
    private boolean checkCodeInOperation(List<PermissionOperation> leftList, String code) throws BusinessException {
    	boolean hasCode = false;
    	for (PermissionOperation po : leftList) {
    		if(po.getKey().equals(code)){
    			hasCode = true;
    			break;
    		}
    	}
    	return hasCode;
    }
    
    /**
     * 如果ifExists==true，则从itemList中取为ids值的数据
     * 如果ifExists==false，则从itemList中移除掉为ids值的数据
     * @param itemList
     * @param ids
     * @param ifExists
     * @return
     * @throws BusinessException 
     */
    private List<PermissionOperation> getItemList(List<PermissionOperation> source,String[] keys,
            List<PermissionOperation> target,boolean ifExists) throws BusinessException{
        if(keys==null||keys.length==0){
            return source;
        }
        if(source!=null&&source.size()>0){
                if(ifExists){
                    for(int i=0;i<keys.length;i++){
                    	if (keys[i].contains("label_")) {//输入框
                    		PermissionOperation labelOperation = ctpPermissionOperationManager.newLablePeration();
                    		labelOperation.setKey(keys[i]);
                    		labelOperation.setLabelValue(keys[i].substring(6, keys[i].length()));
                    		labelOperation.setLabel("(" + ResourceUtil.getString("permission.operation.lable") + ")" + keys[i].substring(6, keys[i].length()));
                    		labelOperation.setType(String.valueOf(PermissionOperationCategoryEnmus.label.getKey()));
                    		target.add(labelOperation);
                    	} else  {
                    		for (PermissionOperation item : source) {
                    			if(item.getKey().equals(keys[i])){
                    				target.add(item);
                    				break;
                    			}
                    		}
                    	}
                    }
                }else{
                    for (PermissionOperation item : source) {
                        int flag = 0;
                        for(int i=0;i<keys.length;i++){
                        	if(item.getKey().equals(keys[i])){
                                flag = 0;
                                break;
                            }else{
                                flag++;
                            }
                        }
                        if(flag>0){
                            target.add(item);
                        }
                    }
                }
        }
        return target;
    }
	private boolean isGovPermission(String category) {
		return EnumNameEnum.edoc_new_send_permission_policy.name().equals(category) 
				|| EnumNameEnum.edoc_new_rec_permission_policy.name().equals(category) 
				|| EnumNameEnum.edoc_new_change_permission_policy.name().equals(category)
				|| EnumNameEnum.info_send_permission_policy.name().equals(category)
				|| EnumNameEnum.edoc_new_qianbao_permission_policy.name().equals(category);
	}
	
	/**
	 * 备选操作排序
	 * @param list
	 * @return
	 */
	private List<PermissionOperation> order4Permission(List<PermissionOperation> operationList, String param) {
		List<PermissionOperation> returnOperations = new ArrayList<PermissionOperation>();
		
		//保存顺序的数组,值的顺序不能变
		//"-1":回退,"-2":指定回退,"1003":退回重填,"1004":会签给领导,"1005":加我的下级部门主管
		String[] orderList = null;
		if ("basic".equals(param)) {
			orderList = new String[] {"Print","Opinion","Track","CommonPhrase","UploadAttachment","UploadRelDoc","ContinueSubmit","Comment","Archive","ReMove","-1","-2","1003","1004","1005","1006","1007"};
		} else {
			orderList = new String[] {"Forward","Infom","RemoveNode","Terminate","Cancel","Transfer","AddNode","1004","1005","JointSign","moreSign","-1","-2","1003","Edit","allowUpdateAttachment","SuperviseSet","Sign","Transform","1006","1007"};
		}
		
		//备选操作设置成Map,便于排序操作
		Map<String, PermissionOperation> operationMap = new HashMap<String, PermissionOperation>();
		for (PermissionOperation operation : operationList) {
			operationMap.put(operation.getKey(), operation);
		}
		//固定排序
		for (int i = 0; i < orderList.length; i++) {
			if (operationMap.containsKey(orderList[i])) {
				returnOperations.add(operationMap.get(orderList[i]));
				operationMap.remove(orderList[i]);
			}
		}
		//工具类:分割线\文本框\应用磁贴
		if (operationMap.containsKey("line")) {
			PermissionOperation operation = operationMap.get("line");
			returnOperations.add(operation);
			operationMap.remove(operation.getKey());
		}
		if (operationMap.containsKey("label")) {
			PermissionOperation operation = operationMap.get("label");
			returnOperations.add(operation);
			operationMap.remove(operation.getKey());
		}
		if (operationMap.containsKey("AppTile")) {
			PermissionOperation operation = operationMap.get("AppTile");
			returnOperations.add(operation);
			operationMap.remove(operation.getKey());
		}
		//自定义操作
		for (PermissionOperation operation : operationMap.values()) {
			String codeString = operation.getKey();
			if ("line".equals(codeString) || "label".equals(codeString) || "AppTile".equals(codeString)) {
				continue;
			}
			returnOperations.add(operation);
		}
		
		return returnOperations;
	}
	
	private List<PermissionOperation> getStartNodePermission(List<PermissionOperation> metadata2, String permissionName) {
		List<PermissionOperation> returnOperations = new ArrayList<PermissionOperation>();
		String[] permissionCodes = null;
		if ("dengji".equals(permissionName)) {//登记有转办
			permissionCodes = new String[] {"Print","Cancel","Archive","UploadAttachment","UploadRelDoc","TurnRecEdoc","TransmitBulletin"};
		} else {
			permissionCodes = new String[] {"Print","Cancel","Archive","UploadAttachment","UploadRelDoc","TransmitBulletin"}; 
		}
		
		Map<String, PermissionOperation> operationMap = new HashMap<String, PermissionOperation>();
		for (PermissionOperation operation : metadata2) {
			operationMap.put(operation.getKey(), operation);
		}
		
		for (int i = 0; i < permissionCodes.length; i++) {
			if (operationMap.containsKey(permissionCodes[i])) {
				returnOperations.add(operationMap.get(permissionCodes[i]));
			}
		}
		
		return returnOperations;
	}

	
}
