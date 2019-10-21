/**
 * $Author$
 * $Rev$
 * $Date::                     $:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */

package com.seeyon.ctp.privilege.manager;

import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;

/**
 * @author renwei
 * @edit futao
 */
@ProcessInDataSource(name = DataSourceName.BASE)
public class MenuCacheManagerImpl implements MenuCacheManager {
//    private final static Log logger = LogFactory.getLog(MenuCacheManagerImpl.class);
    private PrivilegeMenuManager privilegeMenuManager;

    /*private OrgManager orgManager;
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    private static final String UNDERLINE = "_";
    *//**
     * 人员与业务生成器菜单缓存 key:人员ID_单位ID，value:有权限的菜单Map
     *//*
    private Map<String, Map<Long, PrivMenuBO>> member2MenusMap = new ConcurrentHashMap<String, Map<Long, PrivMenuBO>>();
    private Map<String, Long> memberBizDate = new ConcurrentHashMap<String, Long>();
    private Map<String, Long> memberOrgDate = new ConcurrentHashMap<String, Long>();
    
    private static final CacheAccessable cacheFactory = CacheFactory.getInstance(MenuCacheManager.class);
    private CacheObject<Long> BizLastModity = cacheFactory.createObject("BizLastModity");*/
    
    @SuppressWarnings("unchecked")
    public void initialize() {
       /* try {
            List<V3xOrgMember> allMembers = orgManager.getAllMembers(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
            final long orgLastModify = this.orgManager.getModifiedTimeStamp(null).getTime();
            
            final Long bizLastModifyTime = System.currentTimeMillis();
           
            final List<V3xOrgMember>[] splitMembers = Strings.splitList(allMembers, 700);
            
            BizLastModity.set(bizLastModifyTime);
            
            //采用异步加载人员菜单缓存
            new Thread(new Runnable() {
                public void run() {
                    CTPExecutor.execute(Arrays.asList(splitMembers), splitMembers.length, new CTPExecutor.Task<List<V3xOrgMember>>() {
                        public void execute(List<V3xOrgMember> members) {
                            Long startTime = System.currentTimeMillis();
                            
                            for (V3xOrgMember member : members) {
                                Long memberId = member.getId();
                                Long accountId = member.getOrgAccountId(); //主单位ID
                                
                                try {
                                    addMenus(memberId, accountId, bizLastModifyTime, orgLastModify);
                                }
                                catch (BusinessException e) {
                                    logger.error("启动时加载菜单缓存异常！", e);
                                }
                                
                                //兼职单位列表,不包含人员所在单位
//                              List<V3xOrgAccount> concurrentAccounts = orgManager.getConcurrentAccounts(memberId);
//                              for (V3xOrgAccount account : concurrentAccounts) {
//                                  addMenus(memberId, account.getId(), startTime, orgLastModify);
//                              }
                            }
                            
                            logger.info(" -- 加载人员菜单缓存[" + members.size() + "], 耗时" + (System.currentTimeMillis() - startTime) + "ms");
                        }
                    });
                }
            }).start();
            
            //停留一会儿，以多加载一些人
            if(allMembers.size() > 1000){
                try {
                    Thread.sleep(10 * 1000);
                }
                catch (Throwable e) {
                }
            }
            
        }
        catch (BusinessException e) {
            logger.error("启动时加载菜单缓存异常！", e);
        }*/
    }
    
    public void updateBiz(){
    	/*try{
    		User currentUser = AppContext.getCurrentUser();  
    		String ctxKey = "Menu-" + currentUser.getId() + "-" + currentUser.getLoginAccount();
    		AppContext.putThreadContext(ctxKey, null);
	    	Map<Long, PrivMenuBO> resources = menuManager.getByMember(currentUser.getId(), currentUser.getLoginAccount());
	    	List<String> resList = new ArrayList<String>();
	    	for (PrivMenuBO res : resources.values()) {
	    		String code = res.getResourceCode();
	    		if(code == null){
	    			continue;
	    		}
	    		resList.add(code); 
	    	}
	    	UserHelper.setResourceJsonStr(JSONUtil.toJSONString(resList)); 
    	}catch(Throwable e){
    		logger.error("刷新resourcecode异常！", e);
    	}
        BizLastModity.set(System.currentTimeMillis());*/
    	privilegeMenuManager.updateBiz();
    }
    
    /**
     * 根据人员ID_单位ID获取菜单list
     * @param memberId
     * @return
     * @throws BusinessException 
     */
    public Map<Long, PrivMenuBO> getMenus(Long memberId, Long accountId) throws BusinessException{
  /*      String key = memberId.toString() + UNDERLINE + accountId.toString();
        
        long bizLastModify = this.BizLastModity.get();
        long orgLastModify = this.orgManager.getModifiedTimeStamp(null).getTime();
        
        if(!Strings.equals(bizLastModify, memberBizDate.get(key)) || !Strings.equals(orgLastModify, memberOrgDate.get(key))){
            long startTime = System.currentTimeMillis();
            Map<Long, PrivMenuBO> menus = addMenus(memberId, accountId, bizLastModify, orgLastModify);
            logger.info("重新加载人员菜单缓存：" + memberId+","+ (System.currentTimeMillis() - startTime) + "ms");
            
            return menus;
        }
        else{
            return member2MenusMap.get(key);
        }*/
    	return privilegeMenuManager.getMenus(memberId, accountId);
    }
    
	public Map<Long, PrivMenuBO> reSetMM1Menus(Long memberId, Long accountId) throws BusinessException {
/*		String key = memberId.toString() + UNDERLINE + accountId.toString();
		long bizLastModify = this.BizLastModity.get();
		long orgLastModify = this.orgManager.getModifiedTimeStamp(null).getTime();
		boolean hasM1Plugin = AppContext.hasPlugin("mm1");
		if (hasM1Plugin) {
			long startTime = System.currentTimeMillis();
			Map<Long, PrivMenuBO> menus = addMenus(memberId, accountId, bizLastModify, orgLastModify);		
			logger.info("重新加载人员菜单缓存：" + memberId+"," + (System.currentTimeMillis() - startTime) + "ms");
			return menus;	
		}
	
		return member2MenusMap.get(key);*/
		
		return privilegeMenuManager.reSetMM1Menus(memberId, accountId);

	}
    


    public void destroy() {
        
    }

	public void setPrivilegeMenuManager(PrivilegeMenuManager privilegeMenuManager) {
		this.privilegeMenuManager = privilegeMenuManager;
	}

    
}
