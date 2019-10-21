package com.seeyon.ctp.portal.manager;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.seeyon.apps.news.api.NewsApi;
import com.seeyon.apps.project.api.ProjectApi;
import com.seeyon.apps.project.bo.ProjectBO;
import com.seeyon.apps.taskmanage.util.ProductEditionUtil;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.common.cglib.CglibCopier;
import com.seeyon.ctp.common.constants.CustomizeConstants;
import com.seeyon.ctp.common.constants.ProductEditionEnum;
import com.seeyon.ctp.common.customize.manager.CustomizeManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.po.customize.CtpCustomize;
import com.seeyon.ctp.common.task.AsynchronousBatchTask;
import com.seeyon.ctp.login.bo.MenuBO;
import com.seeyon.ctp.menu.manager.PortalMenuManager;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.portal.api.PortalSetInterface;
import com.seeyon.ctp.portal.api.PortalSetInterfaceManager;
import com.seeyon.ctp.portal.dao.PortalNavDao;
import com.seeyon.ctp.portal.dao.PortalSetDao;
import com.seeyon.ctp.portal.dao.PortalSetSecurityDao;
import com.seeyon.ctp.portal.link.dao.LinkSpaceDao;
import com.seeyon.ctp.portal.link.manager.LinkSpaceManager;
import com.seeyon.ctp.portal.nav.bo.PortalMenuBo;
import com.seeyon.ctp.portal.nav.bo.PortalNavBo;
import com.seeyon.ctp.portal.po.PortalCustomizeMenu;
import com.seeyon.ctp.portal.po.PortalGlobalConfig;
import com.seeyon.ctp.portal.po.PortalLinkSpace;
import com.seeyon.ctp.portal.po.PortalLinkSpaceAcl;
import com.seeyon.ctp.portal.po.PortalNav;
import com.seeyon.ctp.portal.po.PortalSet;
import com.seeyon.ctp.portal.po.PortalSpaceFix;
import com.seeyon.ctp.portal.po.PortalSpacePage;
import com.seeyon.ctp.portal.po.PortalTheme;
import com.seeyon.ctp.portal.portlet.ImagePortletLayout;
import com.seeyon.ctp.portal.portlet.PortletConstants;
import com.seeyon.ctp.portal.portlet.manager.BasePortletCategory;
import com.seeyon.ctp.portal.space.bo.DefaultSpaceSetting;
import com.seeyon.ctp.portal.space.bo.MenuTreeNode;
import com.seeyon.ctp.portal.space.bo.SpaceBO;
import com.seeyon.ctp.portal.space.dao.SpaceDao;
import com.seeyon.ctp.portal.space.manager.SpaceManager;
import com.seeyon.ctp.portal.sso.thirdpartyintegration.ThirdpartySpace;
import com.seeyon.ctp.portal.sso.thirdpartyintegration.manager.ThirdpartySpaceManager;
import com.seeyon.ctp.portal.util.Constants;
import com.seeyon.ctp.portal.util.Constants.SpaceState;
import com.seeyon.ctp.portal.util.Constants.SpaceType;
import com.seeyon.ctp.portal.util.G6PortalNavCmparator;
import com.seeyon.ctp.portal.util.PortalCommonUtil;
import com.seeyon.ctp.portal.util.PortalConstants;
import com.seeyon.ctp.portal.util.PortalNavType;
import com.seeyon.ctp.portal.util.SpaceFixUtil;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;
import com.seeyon.ctp.privilege.manager.PrivilegeManager;
import com.seeyon.ctp.privilege.manager.PrivilegeMenuManager;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.EnumUtil;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.json.JSONUtil;


public class PortalNavManagerImpl extends AsynchronousBatchTask<Map<String,Object>> implements PortalNavManager{
	private final static Log logger= LogFactory.getLog(PortalNavManagerImpl.class);
	private PortalNavDao portalNavDao;
	private PortalSetDao portalSetDao;
	private OrgManager   orgManager;
	private SpaceDao 	 spaceDao;
	private PortalCacheManager portalCacheManager;
	private PrivilegeManager privilegeManager;
	private ProjectApi projectApi;
	private LinkSpaceDao linkSpaceDao;
	private PortalMenuManager portalMenuManager;
	private GlobalConfigManager globalConfigManager;
	private SpaceManager spaceManager;
	private PortalManager portalManager;
	private LinkSpaceManager linkSpaceManager;
	private CustomizeManager customizeManager;
	private PortalCustomizeMenuManager portalCustomizeMenuManager;
	private PrivilegeMenuManager privilegeMenuManager;

	public PortalSetSecurityManager getPortalSetSecurityManager() {
		return portalSetSecurityManager;
	}

	public void setPortalSetSecurityManager(PortalSetSecurityManager portalSetSecurityManager) {
		this.portalSetSecurityManager = portalSetSecurityManager;
	}

	private PortalSetSecurityManager portalSetSecurityManager;

	public PortalSetSecurityDao getPortalSetSecurityDao() {
		return portalSetSecurityDao;
	}

	public void setPortalSetSecurityDao(PortalSetSecurityDao portalSetSecurityDao) {
		this.portalSetSecurityDao = portalSetSecurityDao;
	}

	private PortalSetSecurityDao portalSetSecurityDao;
	private NewsApi newsApi;
	

	public void setNewsApi(NewsApi newsApi) {
		this.newsApi = newsApi;
	}

	public PortalCustomizeMenuManager getPortalCustomizeMenuManager() {
		return portalCustomizeMenuManager;
	}

	public void setPortalCustomizeMenuManager(PortalCustomizeMenuManager portalCustomizeMenuManager) {
		this.portalCustomizeMenuManager = portalCustomizeMenuManager;
	}

	public void setCustomizeManager(CustomizeManager customizeManager) {
		this.customizeManager = customizeManager;
	}

	public void setPortalManager(PortalManager portalManager) {
		this.portalManager = portalManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setGlobalConfigManager(GlobalConfigManager globalConfigManager) {
		this.globalConfigManager = globalConfigManager;
	}

	public void setPortalMenuManager(PortalMenuManager portalMenuManager) {
		this.portalMenuManager = portalMenuManager;
	}

	public void setLinkSpaceDao(LinkSpaceDao linkSpaceDao) {
		this.linkSpaceDao = linkSpaceDao;
	}

	public void setProjectApi(ProjectApi projectApi) {
		this.projectApi = projectApi;
	}

	public void setPrivilegeManager(PrivilegeManager privilegeManager) {
		this.privilegeManager = privilegeManager;
	}

	public void setPortalCacheManager(PortalCacheManager portalCacheManager) {
		this.portalCacheManager = portalCacheManager;
	}

	public void setSpaceDao(SpaceDao spaceDao) {
		this.spaceDao = spaceDao;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setPortalSetDao(PortalSetDao portalSetDao) {
		this.portalSetDao = portalSetDao;
	}

	public void setPortalNavDao(PortalNavDao portalNavDao) {
		this.portalNavDao = portalNavDao;
	}
	
	private CacheMap<String,PortalNav> navCache;
	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(PortalNavManagerImpl.class);

	private void putNavCache(List<PortalNav> list){
		if(list!=null && list.size()>0){
			Map<String,PortalNav> map=new HashMap<String, PortalNav>();
			for(PortalNav nav:list){
				try{
						//单个记录加容错兼容，防止出现单个数据无效造成所有的数据都加载失败的情况
					    String key=nav.getNavType()+"_"+nav.getPortalId()+"_"+nav.getEntityId()+"_"+nav.getAccountId();
						String json=nav.getNavValue();
						if(Strings.isNotBlank(json)) {
							if("menu".equals(nav.getNavType())) {
								Type type=new com.seeyon.ctp.util.TypeReference<ArrayList<PortalMenuBo>>(){}.getType();
								List<PortalMenuBo> menus=JSONUtil.parseJSONString(json, type);
								nav.setPortalMenuBoList(menus);
							}else {
								Type type=new com.seeyon.ctp.util.TypeReference<ArrayList<PortalNavBo>>(){}.getType();
								List<PortalNavBo> navs=JSONUtil.parseJSONString(json, type);
								nav.setPortalNavBoList(navs);
							}
						}
						map.put(key, nav);
				}
				catch (Exception e){
					logger.error("navId--->"+nav.getId());//记录有问题的数据
					logger.error("导航数据加载入缓存出错！",e);
				}
			}
			navCache.putAll(map);
		}
	}
	@Override
	public void initNavCache(){
		navCache = cacheFactory.createMap("navCache");
		SpaceNavLastModity=new ConcurrentHashMap<String, Long>();
		PortalNavLastModity=new ConcurrentHashMap<String, Long>();
		navBoCache=new ConcurrentHashMap<String, List<PortalNavBo>>();
	}
	@Override
	public void initNavCacheData(){
		try {
			List<PortalNav> list=this.portalNavDao.findAll();
			putNavCache(list);
		} catch (Exception e) {
			logger.error("初始化导航数据错误!",e);
		}
	}
	//获取需要追加导航的entityIDs
	private List<Long> getAppendEntityIds(String auth) throws BusinessException{
		List<V3xOrgMember> members = OrgHelper.getMembersByElements(auth);
		List<Long> entityIds=new ArrayList<Long>();
		for(V3xOrgMember member : members) {
			entityIds.add(member.getId());
		}
		boolean isGroupVer = (Boolean) (SysFlag.sys_isGroupVer.getFlag());
		long accountId = AppContext.currentAccountId();
		if(accountId==OrgConstants.GROUPID) {
			List<V3xOrgAccount> allAccount = orgManager.getAllAccounts();
			for(V3xOrgAccount account : allAccount) {
				entityIds.add(account.getId());
			}
		}else if(!isGroupVer){
			entityIds.add(OrgConstants.GROUPID);
			entityIds.add(accountId);
		}else {
			entityIds.add(accountId);
		}
		return entityIds;
	}
	//获取需要追加的门户ids,如果portalId非空则表示指定追加到此门户
	private List<Long> getAppendPortalIds(Long portalId,Long accountId) throws BusinessException{
		List<Long> ids=new ArrayList<Long>();
		if(portalId!=null) {
			ids.add(portalId);
			return ids;
		}
		boolean isGroupVer = (Boolean)(SysFlag.sys_isGroupVer.getFlag());
		if(!isGroupVer) {//非集团版只有一个主门户id为1
			ids.add(1l);
			return ids;
		}
		//只追加到主门户上
		List<PortalGlobalConfig> masterPortals=null;
		//如果集团创建门户 则追加所有的主门户
		if(OrgConstants.GROUPID.longValue()==accountId.longValue()){
			masterPortals=globalConfigManager.find(PortalConstants.MASTER_PORTAL, null);
		}else{//如果单位创建门户 只追加单位当前主门户
			masterPortals=globalConfigManager.find(PortalConstants.MASTER_PORTAL, accountId);
		}
		for(PortalGlobalConfig masterPortal : masterPortals){
			String portalIdStr=masterPortal.getValue();
			if(Strings.isNotBlank(portalIdStr)){
				ids.add(Long.parseLong(portalIdStr));
			}
		}
		return ids;
	}
	@Override
	public void appendPortal(PortalSet portal,String auth) throws BusinessException{
//		if(portal==null){
//			return ;
//		}
//		//新建门户的时候自动追加到主门户导航, 除移动门户,大屏,登录前,报表
//		if(1!=portal.getPcPortal() || 2==portal.getPortalType() || 3==portal.getPortalType()|| 4==portal.getPortalType()) {
//			return ;//不是pc门户不追加导航
//		}
//		Map<String,Object> map= new HashMap<String, Object>();
//		map.put("type", "portal");
//		map.put("portal", portal);
//		map.put("auth", auth);
//		super.addTask(map);
	}
	@Override
	public void appendSpace(PortalSpaceFix space,Long portalId,String auth) throws BusinessException{
//		if(space==null || portalId==null){
//			return ;
//		}
//		Map<String,Object> map= new HashMap<String, Object>();
//		map.put("type", "space");
//		map.put("space", space);
//		map.put("portalId", portalId);
//		map.put("auth", auth);
//		super.addTask(map);
	}
	
	private void appendBizMenuTask(List<Long> memberIds,PrivMenuBO menu) throws BusinessException {
		logger.info("appendBizMenuTask");
		List<PortalNav> navList=portalNavDao.findBy(memberIds,"menu");
		if(navList!=null){
			Map<String,PortalNav> map=new HashMap<String, PortalNav>();
			List<PortalNav> updateList=new ArrayList<PortalNav>();
			for(PortalNav n:navList){
				String key=n.getNavType()+"_"+n.getPortalId()+"_"+n.getEntityId()+"_"+n.getAccountId();
				//如果没配置过
				if(n!=null && n.getNavValue()!=null){
					String json=n.getNavValue();
					List<PortalMenuBo> list= n.getPortalMenuBoList();
					if(null==list || list.isEmpty()){
						list= JSON.parseObject(json, new TypeReference<ArrayList<PortalMenuBo>>() {});
					}
					PortalMenuBo bo=new PortalMenuBo();
					bo.setChecked(1);
					bo.setMenuId(menu.getId().toString());
					bo.setSortId(list.size()+2);
					list.add(bo);
					n.setNavValue(JSONUtil.toJSONString(list));
					n.setPortalMenuBoList(list);
					map.put(key, n);
					updateList.add(n);
				}
			}
			if(menu!=null&&memberIds!=null){
				//记录cap4门户菜单自动追加操作，方便问题定位于排查
				logger.info("业务菜单【"+menu.getName()+"】自动追加到门户菜单,授权人数【"+memberIds.size()+"】");
			}
			this.navCache.putAll(map);
			portalNavDao.updateAll(updateList);
		}
	}
	@Override
	public void appendBizMenu(List<Long> memberIds,PrivMenuBO menu)throws BusinessException{
		if(memberIds==null) {
			return ;
		}
		if(memberIds.size()>2000) {
			int count=memberIds.size()/2000;
			int m=memberIds.size()%2000;
			if(m>0) {
				count++;
			}
			List<List<Long>> list=new ArrayList<List<Long>>();
			for(int i=0;i<count;i++) {
				if(i==count-1) {
					list.add(memberIds.subList(i*2000, memberIds.size()-1));
				}else {
					list.add(memberIds.subList(i*2000, ((i+1)*2000)-1));
				}
			}
			for(List<Long> ids:list) {
				appendBizMenuTask(ids, menu);
			}
		}else {
			appendBizMenuTask(memberIds, menu);
		}
		
	}
	private void checkIsDelete(String portalId,List<PortalNavBo> navs) throws BusinessException {
		boolean isMasterPortal = portalManager.isMasterPortal(portalId);
		List<PortalNavBo> old = null;
		if(AppContext.isAdmin()) {
			old = getAdminDefaultNav(AppContext.getCurrentUser(),portalId,null, isMasterPortal);
		}else {
			old = getCurrentUserDefaultNav(portalId, AppContext.getCurrentUser());
		}
		Map<String,PortalNavBo> map = new HashMap<String, PortalNavBo>();
		for(PortalNavBo nav:navs) {
			map.put(nav.getId(), nav);
		}
		for(PortalNavBo nav:old) {
			//navs里没有则表示删除掉了
			if(!map.containsKey(nav.getId())) {
				nav.setIsDelete("1");
				navs.add(nav);
			}
		}
	}
	@Override
	@AjaxAccess
	public void savePortalNav(List<Map<String,String>> navs,String portalId)throws BusinessException{
		if(navs==null){
			navs = new ArrayList<Map<String,String>>();
		}
		User user = AppContext.getCurrentUser();
		long entityId = PortalCommonUtil.getEntityId(user);
		long accountId = PortalCommonUtil.getAccountId(user);
		long portalLongId = Strings.isBlank(portalId)?null:Long.parseLong(portalId);
		ArrayList<PortalNavBo> list = new ArrayList<PortalNavBo>();
		int sort=1;
		boolean isFrontManager = false;
		for(Map<String,String> nav : navs){
			Set<String> keys=nav.keySet();
			if(keys.contains("frontManager")) {
				isFrontManager = true;
				continue;
			}
			for(String key:keys){
				String[] temp = key.split("_");
				if(temp!=null && temp.length>1){
					String navId=temp[1];
					String navType = temp[0];
					if("space".equals(navType)) {
						Long spaceId = parseLong(navId);
						if(spaceId!=null) {
							PortalSpaceFix fix = portalCacheManager.getPageFixIdCacheByKey(spaceId);
							if(fix!=null && fix.getParentId()!=null) {
								navId = fix.getParentId().toString();
							}
						}
					}
					PortalNavBo bo=new PortalNavBo();
					bo.setId(navId);
					bo.setNavType(navType);
					bo.setNavName(nav.get(key));
					bo.setSort(sort);
					sort++;
					list.add(bo);
				}
			}
		}
		if(isFrontManager) {
			PortalSet portal = portalCacheManager.getPortalSetFromCache(parseLong(portalId));
			if(portal!=null) {
				entityId = portal.getEntityId();
				accountId = portal.getAccountId();
			}
		}
		PortalNav portalNav= portalNavDao.getDefaultNav(entityId, accountId, portalId, "nav");
		boolean update= true;
		if(null==portalNav){
			portalNav= new PortalNav();
			portalNav.setNewId();
			update= false;
		}
		checkIsDelete(portalId, list);
		portalNav.setPortalId(portalLongId);
		portalNav.setEntityId(entityId);
		portalNav.setAccountId(accountId);
		portalNav.setNavType("nav");
		portalNav.setNavValue(JSONUtil.toJSONString(list));
		portalNav.setPortalNavBoList(list);
		if(update){
			portalNavDao.update(portalNav);
		}else{
			portalNavDao.save(portalNav);
		}
		portalNavDao.deleteBy(portalNav.getId(), portalLongId, entityId, accountId, "nav");
		String key=portalNav.getNavType()+"_"+portalId+"_"+entityId+"_"+accountId;
		navCache.put(key, portalNav);
		//兼容7.0以前的导航数据,如果前端用户自己设置导航后删掉之前的个性化数据
		if(!user.isAdmin()) {
			CtpCustomize spaceCustom = customizeManager.getCustomizeInfoWithCache(user.getId(), CustomizeConstants.SPACE_ORDER);
			if(spaceCustom!=null) {
				customizeManager.deleteCustomizeById(spaceCustom.getId());
			}
		}
	}

	@Override
	public List<PortalNav> findBy(Long portalId, Long entityId, Long accountId,String type) throws BusinessException {
		return portalNavDao.findBy(portalId, entityId, accountId,type);
	}

//	private List<Map<String, String>> appendDefaultSpace1(List<Map<String, String>> list,String portalId) throws BusinessException{
//		if(list==null){
//			list=new ArrayList<Map<String,String>>();
//		}
//		PortalSpaceFix fix=this.getDefaultSpace(portalId);
//		if(fix==null){
//			return list;
//		}
//		String k="space_"+fix.getId();
//		String v=ResourceUtil.getString(fix.getSpacename());
//		Map<String,String> navMap=new HashMap<String, String>();
//		navMap.put(k, v);
//		List<Map<String, String>> result=new ArrayList<Map<String,String>>();
//		result.add(navMap);
//		result.addAll(list);
//		return result;
//	}
	private List<PortalNavBo> setDefaultSpaceToFirst(List<PortalNavBo> navs,Long defaultSpaceId){
		if(navs==null || defaultSpaceId==null) {
			return navs;
		}
		List<PortalNavBo> list=new ArrayList<PortalNavBo>();
		for(PortalNavBo bo:navs) {
			if(defaultSpaceId.toString().equals(bo.getId())) {
				list.add(0, bo);
			}else {
				list.add(bo);
			}
		}
		return list;
	}
	private List<PortalNavBo> checkAdminPriv(User user,List<PortalNavBo> list,String portalId) throws BusinessException{
		List<PortalNavBo> result=new ArrayList<PortalNavBo>();
		if(list==null) {
			return result;
		}
		List<PortalNavBo> spaces = getSpaceForNav(null);//过滤权限用
		 //可访问的PortalLinkSpace
		List<PortalLinkSpace> linkSpaceList = linkSpaceManager.findLinkSpacesCanAccess(user.getId());
		List<PortalSet> portals = getPortalNav();
		List<PrivMenuBO> menus = portalManager.getAllLevel1Menus(Long.valueOf(portalId));
		Map<String,String> idAndNames=getAdminIdSet(spaces, portals, menus,linkSpaceList);
		for(PortalNavBo bo:list) {
			if(idAndNames.containsKey(bo.getId()) || PortalNavType.linkProject.name().equals(bo.getNavType()) ||  PortalNavType.linkSystem.name().equals(bo.getNavType()) ||  (bo.getNavType() != null && bo.getNavType().startsWith(PortalNavType.thirdPartyPortal.name()))) {
				String navName = idAndNames.get(bo.getId());
				if(Strings.isNotBlank(navName) && !navName.equals(bo.getNavName())) {//回填已选的时候看看导航里的内容名称有没有变动有变动改一下
					bo.setNavName(navName);
				}
				result.add(bo);
			}
		}
		return result;
	}
	private Map<String,String> getAdminIdSet(List<PortalNavBo> spaces,List<PortalSet> portals,List<PrivMenuBO> menus,List<PortalLinkSpace> linkSpaceList) {
		Map<String,String> map=new HashMap<String,String>();
		if(spaces!=null && !spaces.isEmpty()) {
			for(PortalNavBo space:spaces) {
				map.put(space.getId(),ResourceUtil.getString(space.getNavName()));
			}
		}
		if(portals!=null && !portals.isEmpty()) {
			for(PortalSet portal:portals) {
				map.put(portal.getId().toString(),ResourceUtil.getString(portal.getPortalName()));
			}
		}
		if(menus!=null && !menus.isEmpty()) {
			for(PrivMenuBO menu:menus) {
				map.put(menu.getId().toString(),ResourceUtil.getString(menu.getName()));
			}
		}	
		if(Strings.isNotEmpty(linkSpaceList)){
			for (PortalLinkSpace portalLinkSpace : linkSpaceList) {
				map.put(portalLinkSpace.getId().toString(),portalLinkSpace.getSpaceName());
			}
		}
		return map;
	}
	private List<PortalNavBo> getAdminDefaultNav(User user,String portalIdStr,Long defaultSpaceId,boolean isMasterPortal) throws BusinessException{
		List<PortalNavBo> result=this.getSpaceForNav(portalIdStr);
		if(defaultSpaceId!=null) {
			result=setDefaultSpaceToFirst(result,defaultSpaceId);
		}
		if(result==null) {
			result=new ArrayList<PortalNavBo>();
		}
		if(isMasterPortal) {//如果是主门户,追加上关联系统空间和门户
			List<PortalNavBo> linkSpaces = getLinkSpaces();
			if(linkSpaces!=null) {
				result.addAll(linkSpaces);
			}
			putPortals(result, user);
		}
		return result;
	}
	@Override
	public List<PortalNavBo> getBackFillData(Long portalId,Long defaultSpaceId) throws BusinessException {
		User user=AppContext.getCurrentUser();
		String portalIdStr= portalId==null?"":portalId.toString();
		List<PortalNavBo> result= new ArrayList<PortalNavBo>();
		boolean isMasterPortal = false;
		PortalSet portal = null;
		if(portalId!=null){
			portal = portalCacheManager.getPortalSetFromCache(portalId);
			isMasterPortal = portalManager.isMasterPortal(portal);
		}
		if(user.isAdmin()) {
			List<PortalNavBo> list=null;
			PortalNav navData=this.getNavByCache(portal, "nav");
			if(navData!=null) {
				list = navData.getPortalNavBoList();
			}
			if(list==null) {
				result=getAdminDefaultNav(user, portalIdStr, defaultSpaceId, isMasterPortal);
			}else{
				if(isMasterPortal) {
					//将默认空间追加到第一位
					int defaultSpaceIndex= -1;
					for(int i=0;i<list.size();i++){
						PortalNavBo bo=list.get(i);
						if(defaultSpaceId !=null && bo.getId().equals(defaultSpaceId.toString())){
							defaultSpaceIndex= i;
						}else{
							result.add(list.get(i));
						}
					}
					if(defaultSpaceIndex>=0){
						result.add(0, list.get(defaultSpaceIndex));
					}else{//追加到第1位
						if(defaultSpaceId != null) {
							PortalSpaceFix fix= portalCacheManager.getPageFixIdCacheByKey(defaultSpaceId);
							PortalSpacePage spacePage= portalCacheManager.getPageCacheByKey(fix.getPath());
							PortalNavBo bo=new PortalNavBo();
							bo.setId(fix.getId().toString());
							bo.setNavName(ResourceUtil.getString(fix.getSpacename()));
							bo.setNavType("space");
							bo.setSort(0);
							String spaceIcon= fix.getSpaceIcon();
							//去掉图标的/seeyon
							if(Strings.isNotBlank(spaceIcon) && spaceIcon.indexOf("fileUpload.do")>-1 && spaceIcon.startsWith("/seeyon")) {
								spaceIcon=spaceIcon.replace("/seeyon", "");
							}
							bo.setIcon(spaceIcon);
							bo.setDecoration(spacePage.getDefaultLayoutDecorator());
							bo.setUrl(fix.getPath());
							bo.setSpaceType(fix.getType().toString());
							bo.setOpenType("mainfrm");
							result.add(0, bo);
						}
					} 
				}else {
					result.addAll(list);
				}
			}
			result = addNewNavForAdmin(user,portalIdStr, result,isMasterPortal);
			result = checkAdminPriv(user, result,portalIdStr);
		}else {
			//回填已选的时候containMenu =1,needSpaceGroup=0,needPortalGroup=0,needTileSecondMenu=0
			result= this.getCurrentUserNav(portalIdStr,"1", "0", "0","0");
			try{
				//回填时针对自定义菜单特殊处理
				if(result!=null){
					for(PortalNavBo navBo:result){
						if(navBo!=null){
							if("menu".equals(navBo.getNavType())){
								//portalCustomizeMenuManager.get
								Long menuid = parseLong(navBo.getId());
								if(menuid!=null){
									PortalCustomizeMenu byId = portalCustomizeMenuManager.getById(menuid);
									if(byId!=null){
										//自定义菜单的情况
										String navName=navBo.getNavName();
										if("portal".equals(byId.getModuleFrom())){
											//综合业务菜单不展示自定义标识
											navBo.setNavName("["+ResourceUtil.getString("portalcustomizemenu.custom")+"]"+navName);
										}
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e){
				//异常捕获起来防止引起问题
				logger.error("自定义菜单转换处理时出错了！");
			}
		}
		return result;
	}

	@Override
	@AjaxAccess
	public List<PortalSet> getPortalNav() throws BusinessException {
		User user=AppContext.getCurrentUser();
		boolean isGroupVer = (Boolean)(SysFlag.sys_isGroupVer.getFlag());
		List<PortalSet> result=new ArrayList<PortalSet>();
		PortalSet masterPortal=null;
		//1.取主门户
		if(user.isGroupAdmin() || !isGroupVer){
			masterPortal = portalCacheManager.getPortalSetFromCache(1l);
		}else{
			String portalIdStr = portalCacheManager.getUserAcessMainPortalIdFromCache(user);
			if(Strings.isNotBlank(portalIdStr)) {
				masterPortal = portalCacheManager.getPortalSetFromCache(Long.parseLong(portalIdStr));
			}else {//else为防护,一般不会进去查库
				masterPortal = portalSetDao.getAccountDefaultSet(user.getLoginAccount());
			}
		}
		if(masterPortal!=null && masterPortal.getId()!=null){
			result.add(masterPortal);
		}
		//2.取其他门户 
		//集团管理员 : 显示系统门户 + 集团管理员自定义门户  
		//单位管理员 : 显示系统门户 + 集团管理员自定义门户 + 单位管理员自定义门户 + 业务门户
		List<Long> domainIds = getDomainIds(user);		
		List<PortalSet> portals=portalSetDao.getPortalsForNav(domainIds);
		if(portals!=null && portals.size()>0){
			result.addAll(sortPortals(portals));
		}
		return result;
	}
	private List<Long> getDomainIds(User user) throws BusinessException{
		V3xOrgMember member = orgManager.getMemberById(user.getId());
        if(member.getIsInternal()){
            return orgManager.getAllUserDomainIDs(member.getId());
        }else{
            return orgManager.getUserDomainIDs(member.getId(), user.getLoginAccount(), V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,V3xOrgEntity.ORGENT_TYPE_TEAM);
        }
	}
 	private List<PortalSet> sortPortals(List<PortalSet> portals) throws BusinessException{
		List<PortalSet> result=new ArrayList<PortalSet>();
		if(portals==null || portals.size()==0){
			return result;
		}
		List<PortalSet> group=new ArrayList<PortalSet>();
		List<PortalSet> unit=new ArrayList<PortalSet>();
		List<PortalSet> otherUnit=new ArrayList<PortalSet>();
		List<PortalSet> other=new ArrayList<PortalSet>();
		long groupId=OrgConstants.GROUPID;
		long unitId=AppContext.currentAccountId();
		for(PortalSet portalSet : portals){
			PortalSet portal = null;
			try {
				portal = (PortalSet)portalSet.clone();
				portal.setId(portalSet.getId());
			} catch (Exception e) {
				this.logger.error("clone portalSet error!");
			}
			long entityId=portal.getEntityId();
			long accountId=portal.getAccountId();
			V3xOrgAccount account = this.orgManager.getAccountById(accountId);
			if(portal.getPortalType()==5){//如果业务门户排到最后
				other.add(portal);
			}else{
				if(entityId==groupId || entityId==0l){//集团创建的和预制的 排到最前
					portal.setPortalName(portal.getPortalName());
					group.add(portal); 
				}else if(unitId==accountId){//单位创建的 排集团后边
					unit.add(portal);
				}else{
					portal.setPortalName(portal.getPortalName()+"("+account.getShortName()+")");
					otherUnit.add(portal);
				}
			}
		}
		result.addAll(group);
		result.addAll(unit);
		result.addAll(otherUnit);
		result.addAll(other);
		return result;
	}
 	@Override
 	public PortalSpaceFix getDefaultSpace(DefaultSpaceSetting defaultSpace) throws BusinessException{
		User user=AppContext.getCurrentUser();
		PortalSpaceFix fix=null;
		if(defaultSpace==null){
			fix= portalCacheManager.getDefaulPortalSpaceFixFromCache(Constants.SpaceType.Default_personal.ordinal(), user.getLoginAccount());
		}else{
			String spaceId=defaultSpace.getDefaultSpace();
			if(Strings.isBlank(spaceId)){
				String spaceType=defaultSpace.getSpaceType();
				if(DefaultSpaceSetting.Default_Space_Personal_Type.equals(spaceType)){
					if(user.isInternal()) {
						fix= portalCacheManager.getDefaulPortalSpaceFixFromCache(Constants.SpaceType.Default_personal.ordinal(), user.getLoginAccount());
					}else {
						fix = portalCacheManager.getDefaulPortalSpaceFixFromCache(Constants.SpaceType.Default_out_personal.ordinal(), user.getLoginAccount());
					}
				}else if(DefaultSpaceSetting.Default_Space_Group_Type.equals(spaceType)){
					fix= portalCacheManager.getDefaulPortalSpaceFixFromCache(Constants.SpaceType.group.ordinal(), OrgConstants.GROUPID);
				}else if(DefaultSpaceSetting.Default_Space_Corporation_Type.equals(spaceType)){
					fix= portalCacheManager.getDefaulPortalSpaceFixFromCache(Constants.SpaceType.corporation.ordinal(), user.getLoginAccount());
				}
			}else{
				fix= portalCacheManager.getPageFixIdCacheByKey(Long.parseLong(spaceId));
			}
		}
		return fix;
 	}
 	private List<PortalNavBo> sortSpaces(List<PortalSpaceFix> spaces,List<ThirdpartySpace> thirdPartySpacesList) throws BusinessException{
 		List<PortalNavBo> result=new ArrayList<PortalNavBo>();
 		if(spaces == null || spaces.size()==0){
 			return result;
 		}
 		List<PortalNavBo> person=new ArrayList<PortalNavBo>();
 		List<PortalNavBo> group=new ArrayList<PortalNavBo>();
 		List<PortalNavBo> unit=new ArrayList<PortalNavBo>();
 		List<PortalNavBo> other=new ArrayList<PortalNavBo>();
 		long groupId=OrgConstants.GROUPID;
		long unitId=AppContext.currentAccountId();
		int sort=1;
 		for(PortalSpaceFix space:spaces){
 			int type=space.getType();
 			long accountId=space.getAccountId();
 			//列表中过滤其他单位的这几类空间 个人空间50,领导空间910,单位空间2,外部人员空间16,部门空间1,
 			if((5==type || 0==type || 9==type || 10==type || 2==type || 16==type || 1==type) 
 					&& unitId != accountId) {
				continue;
			}
 			V3xOrgAccount account = this.orgManager.getAccountById(accountId);
 			PortalNavBo bo=new PortalNavBo();
 			String navName=ResourceUtil.getString(space.getSpacename());
 			if(unitId!=accountId && space.getType()!=SpaceType.group.ordinal() && space.getType()!=SpaceType.public_custom_group.ordinal()) {
 				navName += "("+account.getShortName()+")";
 			}
 			bo.setNavName(navName);
			bo.setNavType("space");
			bo.setId(space.getId().toString());
			bo.setOpenType("mainfrm");
			bo.setIcon(space.getSpaceIcon());
			bo.setSort(sort);
			bo.setUrl(space.getPath());
			PortalSpacePage page=portalCacheManager.getPageCacheByKey(space.getPath());
			bo.setDecoration(page.getDefaultLayoutDecorator());
			bo.setSpaceType(space.getType().toString());
			sort++;
 			long entityId=space.getEntityId();
 			//个人、领导、外部人员、自定义个人、--(部门、自定义团队||4==type||1==type)
 			if(Constants.isPersonalStyleSpace(type)){
 				person.add(bo);
 			}else if(entityId == groupId){
 				group.add(bo);
 			}else if(unitId == entityId){
 				unit.add(bo);
 			}else{
 				other.add(bo);
 			}
 		}
 		result.addAll(person);
 		result.addAll(group);
		result.addAll(unit);
		result.addAll(other);
		//第三方空间排到最后
		if(thirdPartySpacesList!=null && thirdPartySpacesList.size()>0){
			for(ThirdpartySpace space:thirdPartySpacesList){
				PortalNavBo bo=new PortalNavBo();
	 			bo.setNavName(ResourceUtil.getString(space.getName()));
				bo.setNavType("space");
				bo.setId(space.getId());
				String openType = space.getOpenType();
				if ("workspace".equals(openType)) {
                    openType = "mainfrm";
                } else {
                    openType = "newWindow";
                }
				bo.setOpenType(openType);
				bo.setIcon("menu_thridpartyspace.png");
				bo.setSort(sort);
				String pageUrl = space.getPageURL(AppContext.currentUserId(), AppContext.currentAccountId());
				String spacePath= "/seeyon/thirdpartyController.do?method=show&id=" + space.getId() + "&pageUrl=" + pageUrl;
				bo.setUrl(spacePath);
				sort++;
				result.add(bo);
			}
		}
 		return result;
 	}
 	@Override
	@AjaxAccess
	public List<PortalNavBo> getSpaceForNav(String portalId)throws BusinessException{
		User user=AppContext.getCurrentUser();
		Long portalLongId=Strings.isBlank(portalId)?null:Long.parseLong(portalId);
		boolean isMasterPortal=false;
		int portalType=0;
		if(portalLongId!=null){
			PortalSet portal = portalCacheManager.getPortalSetFromCache(portalLongId);
			isMasterPortal = portalManager.isMasterPortal(portal);
			portalType=portal.getPortalType();
		}
		List<ThirdpartySpace> thirdPartySpacesList = null;
		if(isMasterPortal || portalLongId == null){//OA-148929 ,空间卡片下没有第三方空间
			try {
				thirdPartySpacesList=ThirdpartySpaceManager.getInstance().getAccessSpaces(orgManager, AppContext.currentUserId(), null, null);
			} catch (Exception e) {
				this.logger.error("导航中查询第三方空间异常!",e);
			}
		}
		List<PortalSpaceFix> list=null;
		if(!user.isAdmin()){
			list=spaceDao.getSpaceForNav(portalLongId, isMasterPortal,portalType, this.getDomainIds(user));
			V3xOrgMember member = orgManager.getMemberById(user.getId());
			if(!member.getIsInternal() && !member.isScreenGuest() && !member.isDefaultGuest() && (isMasterPortal || portalLongId==null)){
	            PortalSpaceFix outerSpace = spaceManager.transCreateDefaultOuterSpace(member.getOrgAccountId());
	            if(null != outerSpace && SpaceState.normal.ordinal() == outerSpace.getState()){
	            	if (CollectionUtils.isEmpty(list)) {
	            		list= new ArrayList<PortalSpaceFix>();
	            	}
	            	list.add(outerSpace);
	            }
	        }
		}else{
			list=spaceDao.getSpaceForNav(portalLongId, isMasterPortal,portalType, new ArrayList<Long>());
		}
		return sortSpaces(list,thirdPartySpacesList);
	}
 	private Set<String> getAllIdSet(List<PortalSet> portals,List<SpaceBO> spaces,List<PrivMenuBO> menus,
 			Map<String,PortalSet> portalMap,Map<String,SpaceBO> spaceMap,Map<String,PrivMenuBO> menuMap){
 		Set<String> set=new HashSet<String>();
 		if(portals!=null && portals.size()>0){
 			for(PortalSet portal:portals){
 				set.add(portal.getId().toString());
 				if(portalMap!=null) {
 					portalMap.put(portal.getId().toString(), portal);
 				}
 			}
 		}
 		if(spaces!=null && spaces.size()>0){
 			for(SpaceBO space:spaces){
 				if(Strings.isNotBlank(space.getParentId())){//如果编辑过空间后产生子的数据
 					set.add(space.getParentId());
 					if(spaceMap!=null) {
 						spaceMap.put(space.getParentId(), space);
 					}
 				}
 				set.add(space.getSpaceId());
 				if(spaceMap!=null) {
 					spaceMap.put(space.getSpaceId(), space);
 				}
 			}
 		}
 		if(menus!=null && menus.size()>0){
 			for(PrivMenuBO menu:menus){
 				if(menu!=null){
 					set.add(menu.getId().toString());
 					if(menuMap!=null) {
 						menuMap.put(menu.getId().toString(), menu);
 					}
 				}
 			}
 		}
 		return set;
 	}
 	private String getBasePortalUrl(){
 		return "/seeyon/main.do?method=main&subPortal=true&portalId=";
 	}
 	private String getThemeSpaceBaseUrl() {
 		return "/seeyon/portal/spaceController.do?method=showThemSpace&showState=show&portalId=";
 	}
 	private String[] getDefaultSpaceIconAndNameByType(String sType,String spaceName,String spaceAccountId,Long accountId,SpaceBO space){
    	if(Strings.isBlank(sType)){
    		return new String[]{"",spaceName};
    	}
    	Integer type=Integer.parseInt(sType);
    	//Long currentSpaceAccountId=Strings.isBlank(spaceAccountId)?-1l:Long.parseLong(spaceAccountId);
    	SpaceType s_Type = EnumUtil.getEnumByOrdinal(SpaceType.class, type);
        SpaceType spaceType = Constants.parseDefaultSpaceType(s_Type);
        String spaceIcon = "";
        switch(spaceType){
            case personal:
                spaceIcon = "menu_personal.png";
                break;
            case leader:
                spaceIcon = "menu_leader.png";
                break;
            case outer:
                spaceIcon = "menu_outer.png";
                break;
            case personal_custom:
                spaceIcon = "menu_personal_custom.png";
                break;
            case department:
                spaceIcon = "menu_department.png";
                break;
            case custom:
                /*if(!currentSpaceAccountId.equals(accountId)){
                    spaceName += "(" + Functions.getAccountShortName(currentSpaceAccountId) + ")";//这个名称在别的地方加上单位简称了，这里再加就重复了，如有疑问找王朝文。
                }*/
                spaceIcon = "menu_custom.png";
                break;
            case corporation:
                spaceIcon = "menu_corporation.png";
                break;
            case public_custom:
                /*if(!currentSpaceAccountId.equals(accountId)){
                    spaceName += "(" + Functions.getAccountShortName(currentSpaceAccountId) + ")";//这个名称在别的地方加上单位简称了，这里再加就重复了，如有疑问找王朝文。
                }*/
                spaceIcon = "menu_public_custom.png";
                break;
            case group:
                spaceIcon = "menu_group.png";
                break;
            case public_custom_group:
                spaceIcon = "menu_public_custom_group.png";
                break;
            case v_report:
            	spaceIcon = "menu_report.png";
            	break;
            case related_system:
            	if(space.getSpaceIcon()==null || space.getSpaceIcon().indexOf("fileUpload.do")==-1) {
            		spaceIcon = "related.png";
            	}
            	break;
            case big_screen:
            	spaceIcon = "bigScreen.png";
               break;
            case before_login:
            	spaceIcon = "beforeLogin.png";
            	break;
            default:
                break;

        }
        return new String[]{spaceIcon,spaceName};
    }
 	private PortalNavBo newGroup(List<PortalNavBo> data,String type){
 		if("space".equals(type)){
 			PortalNavBo bo=new PortalNavBo();
 			bo.setId("-2");
 			bo.setIcon("space");
 			bo.setNavType(PortalNavType.spaceGroup.name());
 			bo.setNavName(ResourceUtil.getString("portal.nav.group.space"));
 			bo.setList(data);
 			bo.setSort(-2);
 			return bo;
 		}else if("portal".equals(type)){
 			PortalNavBo bo=new PortalNavBo();
 			bo.setId("-1");
 			bo.setIcon("Subportal");
 			bo.setNavType("portalGroup");
 			bo.setNavName(ResourceUtil.getString("portal.nav.group.portal"));
 			bo.setList(data);
 			bo.setSort(-1);
 			return bo;
 		}else{
 			return null;
 		}
 	}
 	private String getPortalIcon(PortalSet portal) throws BusinessException{
 		if(portal==null){
 			return "";
 		}
 		String icon=portal.getImgId();
 		if(Strings.isNotBlank(icon)) {
 			icon = PortalCommonUtil.getNavIcon(icon);
 			return icon;
 		}
 		switch (portal.getPortalType()) {
		case 0:
			//主门户
			if(portalManager.isMasterPortal(portal)){
				icon="Mainportal.png";
			}else{//子门户
				icon="Subportal.png";
			}
			break;
		case 1:
			break;
		case 2:
			icon="BigScreenPortal.png";
			break;
		case 3:
			icon="BeforeLoginPortal.png";
			break;
		case 4:
			icon="Reportportal.png";
			break;
		case 5:
			icon="Businessportal.png";
			break;
		default:
			break;
		}
 		return icon;
 	}
	private List<PortalNavBo> converPortalNavBo(String portalId,List<PortalNavBo> list,String containMenu,
			String needSpaceGroup,String needPortalGroup,
			Set<String> ids,Map<String,PortalSet> portalMap,
			Map<String,SpaceBO> spaceMap,Map<String,PrivMenuBO> menuMap,
			boolean isDefault,String needTileSecondMenu) throws BusinessException {
		
		User user=AppContext.getCurrentUser();
		List<PortalNavBo> result=new ArrayList<PortalNavBo>();
		if(list==null || list.size()==0){
			return result;
		}
		Map<Long,MenuBO> portalCustomMenuMap= portalCacheManager.getPortalCustomizeMenuFirstLevelMap(list);
		//兼容老版本导航用
		CtpCustomize custom = this.customizeManager.getCustomizeInfoWithCache(user.getId(), CustomizeConstants.SPACE_ORDER);
		Long portalLongId=Strings.isBlank(portalId)?0:Long.parseLong(portalId);
		PortalSet portal = portalCacheManager.getPortalSetFromCache(portalLongId);
		boolean isMasterPortal = portalManager.isMasterPortal(portal);
		Map<String,MenuBO> menuBOMap=getMenusMap();
		List<PortalNavBo> spaceGroup=new ArrayList<PortalNavBo>();
		List<PortalNavBo> portalGroup=new ArrayList<PortalNavBo>();
		List<PortalNavBo> menuNav=new ArrayList<PortalNavBo>();//放菜单,用于判断导航有几个菜单
        List<PrivMenuBO> allBussinessMenuList = null;
        Set<String> navIdSet= new HashSet<String>();
		for(int i=0;i<list.size();i++){
			PortalNavBo nav=new PortalNavBo();
			CglibCopier.copy(list.get(i), nav);
			String headerspaceId= nav.getHeaderspaceId();
			if(Strings.isNotBlank(headerspaceId) && Strings.isDigits(headerspaceId)){
				if(navIdSet.contains(headerspaceId)){
					continue;
				}
				navIdSet.add(headerspaceId);
			}
			
			String navType = nav.getNavType();
			String thirdPortalType = "";
			if(navType.indexOf("|") > -1) {
				String [] temp=navType.split("\\|");
				navType = temp[0];
				thirdPortalType = temp[1];
			}
			boolean systemMenuCheck=(!ids.contains(nav.getId()) && !"thirdPartyPortal".equals(navType));
			boolean portalcustomizeMenuCheck=false;
			if(Strings.isDigits(nav.getId())){
				Long idLong=parseLong(nav.getId());
				if(idLong!=null){
					MenuBO menuBO= portalCustomMenuMap.get(Long.valueOf(nav.getId()));
					if(null!=menuBO){
						portalcustomizeMenuCheck= true;
					}
				}
			}
			//判断权限
			if(systemMenuCheck&&(!portalcustomizeMenuCheck)){
				continue;
			}
			if(Strings.isNotBlank(headerspaceId) && Strings.isDigits(headerspaceId)){
				if(!ids.contains(headerspaceId)){
					nav.setIsDelete("0");
				}else{
					PortalSpaceFix headerSpace = portalCacheManager.getPageFixIdCacheByKey(Long.parseLong(headerspaceId));
					String extAttributes= headerSpace.getExtAttributes();
					SpaceFixUtil fixUtil = new SpaceFixUtil(extAttributes);
	                
					Boolean isAllowpushed= fixUtil.isAllowpushed();
					if(!isAllowpushed){//需要将领导空间推送给个人用户，则需要将领导空间放在第一位
						nav.setIsDelete("0");
					}
				}
				
				
			}
			//如果isDelete==1跳过
			if("1".equals(nav.getIsDelete())) {
				continue;
			}
			if(!"1".equals(containMenu)){//如果不需要菜单的时候过滤掉
				if("menu".equals(navType)){
					continue;
				}
			}
			if("portal".equals(navType)){
				PortalSet set=portalMap.get(nav.getId());
				boolean isCurrentAccountId=true;
				if(set == null) {
					continue;
				}else {
					//判断此门户是否归属本单位，不是本单位的需要添加单位简称标志
					Long accountId = set.getAccountId();
					Long loginAccountId = AppContext.getCurrentUser().getLoginAccount();
					if(loginAccountId.longValue()!=accountId.longValue()){
						isCurrentAccountId=false;
					}
				}
				if(set.getPortalType()==0 || set.getPortalType()==5){//PC门户，其它不显示出来
					String portalName=ResourceUtil.getString(set.getPortalName());
					if(!isCurrentAccountId){
						V3xOrgAccount account = orgManager.getAccountById(set.getAccountId());
						if(null!=account&&(!account.isGroup())){
							//非本单位并且非集团时添加简称
							portalName=portalName+"("+account.getShortName()+")";
						}
					}
					nav.setNavName(portalName);
					nav.setOpenType("newWindow");//导航里只可以配置子门户
					nav.setUrl(getBasePortalUrl()+set.getId());
					nav.setIcon(PortalCommonUtil.convertNavIcon(getPortalIcon(set)));
					if("1".equals(needPortalGroup)){
						//如果需要给门户分组显示
						portalGroup.add(nav);
						continue;
					}
					result.add(nav);
				}
			}else if("menu".equals(navType)){
				PrivMenuBO menu=menuMap.get(nav.getId());
				if(menu!=null){
					nav.setNavName(ResourceUtil.getString(menu.getName()));
					nav.setOpenType(menu.getTarget());//mainfrm or newWindow
					nav.setIcon(PortalCommonUtil.convertNavIcon(menu.getIcon()));
					nav.setMenu(menuBOMap.get(nav.getId()));
					if(Strings.isNotBlank(menu.getUrl())) {
						nav.setUrl("/seeyon"+menu.getUrl());
					}
				}
				else {
					//自定义菜单的情况
					MenuBO menuBo= portalCustomMenuMap.get(Long.valueOf(nav.getId()));
					if(null!=menuBo){
						nav=new PortalNavBo();
						nav.setNavName(ResourceUtil.getString(menuBo.getName()));
						nav.setOpenType(menuBo.getTarget());//mainfrm or newWindow
						nav.setIcon(PortalCommonUtil.convertNavIcon(menuBo.getIcon()));
						nav.setMenu(menuBo);
						nav.setId(String.valueOf(menuBo.getId()));
						nav.setNavType("menu");
		                if(Strings.isNotBlank(menuBo.getUrl())) {
		                	nav.setUrl("/seeyon"+menuBo.getUrl());
		                }
					}else{
						continue;
					}
				}
				result.add(nav);
				menuNav.add(nav);
			}else if("thirdPartyPortal".equals(navType)){
				if(Strings.isBlank(thirdPortalType)) {
					continue;
				}
				nav.setOpenType("newWindow");//mainfrm or newWindow
				if(Strings.isBlank(nav.getIcon())) {
					nav.setIcon("Businessportal.png");
				}
				//nav.setNavType(PortalNavType.linkSystem.name());//告诉前端用linnkSystem的打开方式打开第三方
				PortalSetInterface thirdPortal = PortalSetInterfaceManager.getPortalSet(thirdPortalType);
				nav.setUrl(thirdPortal.getUrl(nav.getId()));
				nav.setIcon(PortalCommonUtil.convertNavIcon(nav.getIcon()));
				result.add(nav);
			}else{
				SpaceBO space=spaceMap.get(nav.getId());
				if(isDefault && "26".equals(space.getSpaceType()) && custom==null) {
					continue;//如果取得默认空间,则导航中不显示项目空间
				}
				if(space!=null){
					String spaceName=ResourceUtil.getString(space.getSpaceName());
					String[] nameAndIcon=this.getDefaultSpaceIconAndNameByType(space.getSpaceType(),spaceName, space.getAccountId(), user.getLoginAccount(),space);
					nav.setId(space.getSpaceId());
					nav.setNavName(nameAndIcon[1]);
					String spaceIcon=space.getSpaceIcon();
					if(String.valueOf(SpaceType.related_system.ordinal()).equals(space.getSpaceType()) && (Strings.isNotBlank(space.getSpaceIcon()) && space.getSpaceIcon().indexOf("fileUpload.do")==-1)) {
						spaceIcon = nameAndIcon[0];
					}
					if(Strings.isBlank(spaceIcon)) {
						spaceIcon = nameAndIcon[0];
					}
					//去掉图标的/seeyon
					if(Strings.isNotBlank(spaceIcon) && spaceIcon.indexOf("fileUpload.do")>-1 && spaceIcon.startsWith("/seeyon")) {
						spaceIcon=spaceIcon.replace("/seeyon", "");
					}
					nav.setIcon(Strings.isBlank(spaceIcon)?space.getSpaceIcon():spaceIcon);
					nav.setDecoration(space.getDecoration());
					if("linkProject".equals(navType)){
						nav.setUrl("/seeyon"+space.getSpacePath());
					}else{
						nav.setUrl(space.getSpacePath());
					}
					nav.setSpaceType(space.getSpaceType());
					String spaceBelongPortalId="";

					if(!String.valueOf(Constants.SpaceType.thirdparty.ordinal()).equals(space.getSpaceType())
							&&!String.valueOf(Constants.SpaceType.related_system.ordinal()).equals(space.getSpaceType())){
						Long pid=portalCacheManager.getSpaceBelongPortalCache(Long.parseLong(space.getSpaceId()));
						spaceBelongPortalId=pid==null?"":pid.toString();
						if(isMasterPortal){
							PortalSet systemPortal = portalCacheManager.getPortalSetFromCache(pid);
							if(systemPortal!=null && (systemPortal.getId().longValue()==1 || (systemPortal.getParentId()!=null && systemPortal.getParentId().longValue()==1))) {
								spaceBelongPortalId= "1";
							}
						}
						//如果是当前门户下空间
						if(spaceBelongPortalId.equals(portalId) || (isMasterPortal && "1".equals(spaceBelongPortalId)) ||
								"linkProject".equals(navType) || "mainfrm".equals(nav.getOpenType())){
							nav.setOpenType("mainfrm");
						}else{
							nav.setOpenType("newWindow");
							if(String.valueOf(SpaceType.v_report.ordinal()).equals(space.getSpaceType())) {//如果是报表空间url和普通的逻辑不一样
								nav.setUrl(getThemeSpaceBaseUrl()+pid+"&spaceId="+space.getSpaceId());
							}else {
								if(!isMasterPortal) {//如果子门户
									//查询空间所属门户是否为主门户,如果是主门户给个标识
									Long id=Strings.isNotBlank(spaceBelongPortalId)?Long.parseLong(spaceBelongPortalId):null;
									if(id!=null) {
										PortalSet set=portalCacheManager.getPortalSetFromCache(id);
										if(id.longValue()==1l || (set.getParentId()!=null && set.getParentId().longValue()==1l)) {
											nav.setMasterPortalSpace(true);
										}
									}
								}
								nav.setUrl(getBasePortalUrl()+pid+"&spaceId="+space.getSpaceId());
							}
						}
					}else{
						//第三方空间
						String openType = space.getOpenType();
						if ("workspace".equals(openType)) {
							openType = "mainfrm";
						} else {
							openType = "newWindow";
						}
						nav.setOpenType(openType);
					}
					//兼容用12做项目空间type的数据
					if(String.valueOf(SpaceType.related_project.ordinal()).equals(space.getSpaceType()) ||
							String.valueOf(SpaceType.related_project_space.ordinal()).equals(space.getSpaceType())	) {
						nav.setNavType(PortalNavType.linkProject.name());
					}
					if("1".equals(needSpaceGroup) && (spaceBelongPortalId.equals(portalId) || 
							(isMasterPortal && "1".equals(spaceBelongPortalId)) 
							|| (isMasterPortal && (String.valueOf(SpaceType.thirdparty.ordinal()).equals(space.getSpaceType()) 
									|| String.valueOf(SpaceType.related_system.ordinal()).equals(space.getSpaceType()) 
									|| String.valueOf(SpaceType.related_project.ordinal()).equals(space.getSpaceType())
									|| String.valueOf(SpaceType.related_project_space.ordinal()).equals(space.getSpaceType()))))){
						//如果需要给当前门户空间分组显示
						spaceGroup.add(nav);
						continue;
					}
					nav.setIcon(PortalCommonUtil.convertNavIcon(nav.getIcon()));
					result.add(nav);
				}
			}
		}
		List<PortalNavBo> rs=new ArrayList<PortalNavBo>();
		if(spaceGroup.size()>0){
//			sortBoList(spaceGroup);
			rs.add(newGroup(spaceGroup, "space"));
		}
		if(portalGroup.size()>0){
//			sortBoList(portalGroup);
			rs.add(newGroup(portalGroup, "portal"));
		}
		//if(menuNav.size()==1 && "1".equals(needTileSecondMenu)) {
		//	List<PortalNavBo> menuList=getSecondMenus(menuNav, menuIndex);
		//	result.remove(menuIndex);
		//	result.addAll(menuIndex,menuList);
		//}
		//G6政务 需要排序和修改名字
		if (ProductEditionUtil.isG6Verson(ProductEditionEnum.getCurrentProductEditionEnum().getKey() + "")) {
			Collections.sort(result,new G6PortalNavCmparator());
		}
		rs.addAll(result);
		return rs;
	}
	private List<PortalNavBo> getSecondMenus(List<PortalNavBo> navs,int index) {
		PortalNavBo nav=navs.get(0);
		MenuBO menu=nav.getMenu();
		List<MenuBO> seconds=menu.getItems();
		List<PortalNavBo> menuList=new ArrayList<PortalNavBo>();
		for(MenuBO s:seconds) {
			PortalNavBo bo=new PortalNavBo();
			bo.setNavName(ResourceUtil.getString(s.getName()));
			bo.setOpenType(s.getTarget());//mainfrm or newWindow
			bo.setIcon(s.getIcon());
			bo.setMenu(s);
			bo.setSort(index+1);
			bo.setNavType(PortalNavType.menu.name());
			bo.setId(s.getId().toString());
			bo.setUrl("/seeyon"+s.getUrl());
			menuList.add(bo);
		}
		return menuList;
	}
	private  PortalNav getMobileNavByCache(String portalId,String navType){
		User user=AppContext.getCurrentUser();
		PortalNav nav=null;
		String key=null;
		if(user!=null){
			//个人
			key=navType+"_"+portalId+"_"+user.getId()+"_"+user.getLoginAccount();
			nav=navCache.get(key);
		}
		if(nav==null){
			PortalSet portalSetFromCache = portalCacheManager.getPortalSetFromCache(Long.valueOf(portalId));
			if(portalSetFromCache!=null){
				Long accountId = portalSetFromCache.getAccountId();
				key=navType+"_"+portalId+"_"+accountId+"_"+accountId;
				nav=navCache.get(key);
			}
		}
		return nav;
	}
	private PortalNav getNavByCache(String portalId,String navType){
		User user=AppContext.getCurrentUser();
		//个人
		String key=navType+"_"+portalId+"_"+user.getId()+"_"+user.getLoginAccount();
		PortalNav nav=navCache.get(key);
		if(nav==null){
			//单位
			key=navType+"_"+portalId+"_"+user.getLoginAccount()+"_"+user.getLoginAccount();
			nav=navCache.get(key);
			if(nav==null){
				//集团
				key=navType+"_"+portalId+"_"+OrgConstants.GROUPID+"_"+OrgConstants.GROUPID;
				nav=navCache.get(key);
				String masterPortal=portalCacheManager.getUserAcessMainPortalIdFromCache(user);
				//如果是主门户再挖一下门户id为1的
				if(masterPortal.equals(portalId)){
					key=navType+"_"+"1_"+OrgConstants.GROUPID+"_"+OrgConstants.GROUPID;
					nav=navCache.get(key);
				}
			}
		}
		return nav;
	}
	private PortalNav getNavByCache(PortalSet portal,String navType) throws BusinessException{
		if(!portalManager.isMasterPortal(portal)) {
			return getSubPortalNavByCache(portal,navType);
		}else {
			return getNavByCache(portal.getId().toString(),navType);
		}
	}
	private PortalNav getSubPortalNavByCache(PortalSet portal,String navType) {
		String key=navType+"_"+portal.getId()+"_"+portal.getEntityId()+"_"+portal.getAccountId();
		return navCache.get(key);
	}
	private List<PortalNavBo> spaceBo2NavBo(List<SpaceBO> spaces){
		List<PortalNavBo> list = new ArrayList<PortalNavBo>();
		if(spaces==null || spaces.isEmpty()) {
			return list;
		}
		for(SpaceBO space:spaces) {
			if("26".equals(space.getSpaceType())){
				//项目空间不自动添加进入导航
				continue;
			}
			PortalNavBo nav=spaceBo2NavBo(space);
			list.add(nav);
		}
		return list;
	}
	private PortalNavBo spaceBo2NavBo(SpaceBO space){
		PortalNavBo nav = new PortalNavBo();
		if(space==null) {
			return nav;
		}
		nav.setId(space.getSpaceId());
		nav.setNavName(ResourceUtil.getString(space.getSpaceName()));
		if(String.valueOf(SpaceType.related_system.ordinal()).equals(space.getSpaceType())) {
			nav.setNavType("linkSystem");
		}else if(String.valueOf(SpaceType.related_project_space.ordinal()).equals(space.getSpaceType())){
			nav.setNavType("linkProject");
		}else {
			nav.setNavType("space");
		}
		return nav;
	}
	private Map<String,Long> SpaceNavLastModity;
	private Map<String,Long> PortalNavLastModity;
	private Map<String,List<PortalNavBo>> navBoCache;
	//只给前端用户用
	private List<PortalNavBo> getCurrentUserDefaultNav(String portalId,User user)throws BusinessException{
		String key=portalId+"_"+user.getId()+"_"+user.getLoginAccount()+"_"+user.getLocale();
		long spaceLastModity=portalCacheManager.getSpaceLastModityTime();
		long spaceNavLastModity=SpaceNavLastModity.get(key)==null?0l:SpaceNavLastModity.get(key);
		long portalLastModity=portalCacheManager.getPortalLastModityTime();
		long portalNavLastModity=PortalNavLastModity.get(key)==null?0l:PortalNavLastModity.get(key);
		List<PortalNavBo> boList = null;
		if(spaceNavLastModity!=spaceLastModity || portalNavLastModity!=portalLastModity){
			PortalSet portal=portalCacheManager.getPortalSetFromCache(Long.parseLong(portalId));
			List <SpaceBO> spaces = portalCacheManager.getSpaceBoList(user, "pc", portalId);
			//与管理员默认排序保持一致
			List<PortalSpaceFix> fixList=new ArrayList<PortalSpaceFix>();
			List<ThirdpartySpace> thirdPartySpacesList = null;
			boolean isMasterPortal=portalManager.isMasterPortal(portal);
			if(isMasterPortal){
				try {
					thirdPartySpacesList = ThirdpartySpaceManager.getInstance().getAccessSpaces(orgManager, AppContext.currentUserId(), null, null);
				} catch (Exception e) {
					this.logger.error("导航中查询第三方空间异常!",e);
				}
			}
			for(SpaceBO space:spaces) {
				if ("26".equals(space.getSpaceType())) {
					//项目空间不自动添加进入导航
					continue;
				}
				if(space.getSpacePath()!=null&&space.getSpacePath().contains("/thirdpartyController.do")){
					//第三方空间已经有处理
					continue;
				}
				if (space != null) {
					PortalSpaceFix pageFix = portalCacheManager.getPageFixPathCacheByKey(space.getSpacePath());
					if (pageFix != null) {
						fixList.add(pageFix);
					}
				}
			}
			boList=sortSpaces(fixList,thirdPartySpacesList);
			//
			//只有主门户默认的时候需要有门户
			if(portalManager.isMasterPortal(portal)) {
				putPortals(boList, user);
			}
			navBoCache.put(key, boList);
			SpaceNavLastModity.put(key, spaceLastModity);
			PortalNavLastModity.put(key, portalLastModity);
		}else{
			boList=navBoCache.get(key);
		}
		return boList;
	}
	private void putPortals(List<PortalNavBo> boList,User user) throws BusinessException{
        int sort=boList.size()+1;
        List<PortalSet> portals=portalSetDao.getGroupAndUnitPortalNoMaster(OrgConstants.GROUPID, user.getLoginAccount(), this.getDomainIds(user));
        if(portals!=null){
            for(PortalSet set:portals){
                if(set.getPortalType()==0 || set.getPortalType()==5){
                    PortalNavBo bo=new PortalNavBo();
                    bo.setNavName(ResourceUtil.getString(set.getPortalName()));
                    bo.setNavType(PortalNavType.portal.name());
                    bo.setId(set.getId().toString());
                    bo.setOpenType("newWindow");
                    bo.setIcon(Strings.isBlank(set.getImgId())?"portal_set_default.png":set.getImgId());
                    bo.setSort(sort);
                    bo.setUrl(getBasePortalUrl()+set.getId());
                    sort++;
                    boList.add(bo);
                }
            }
        }
	}
	private Map<String,MenuBO> getMenusMap()throws BusinessException {
		User user=AppContext.getCurrentUser();
		List<MenuBO> list=portalMenuManager.getMenusOfMember(user);
		Map<String,MenuBO> map=new ConcurrentHashMap<String,MenuBO>();
		if(list!=null){
			for(MenuBO bo:list){
				map.put(bo.getId().toString(), bo);
			}
		}
		return map;
	}
	//设置权限过滤用的ids,和门户,空间,菜单map对象
	private Set<String> putPrivIds(User user,boolean isDefault,Map<String,PortalSet> portalMap,Map<String,SpaceBO> spaceMap,Map<String,PrivMenuBO> menuMap) throws BusinessException {
		List<PortalSet> portals = portalCacheManager.getCurrentUserPortalList();
		List<SpaceBO> spaces = portalCacheManager.getSpaceBoList(user, "pc");
		List<PrivMenuBO> menus = null;
		//if(!isDefault) {//默认情况下不包含菜单,所以少查询一下
		//	menus = privilegeManager.getMenusOfMember(user.getId(), user.getLoginAccount());
		//}
		menus = privilegeManager.getMenusOfMember(user.getId(), user.getLoginAccount());
		return getAllIdSet(portals, spaces, menus,portalMap,spaceMap,menuMap);
	}
	private Map<String,PortalNavBo> toMap(List<PortalNavBo> list){
		Map<String,PortalNavBo> map = new HashMap<String, PortalNavBo>();
		if(list==null || list.isEmpty()) {
			return map;
		}
		for(PortalNavBo bo:list) {
			map.put(bo.getId(), bo);
		}
		return map;
	}
	private List<PortalNavBo> addNewNav(User user,String portalId,List<PortalNavBo> list) throws BusinessException{
		List<PortalNavBo> result=new ArrayList<PortalNavBo>();
		if(list==null || list.isEmpty()) {
			return result;
		}
		result.addAll(list);
		Map<String,PortalNavBo> map = toMap(list);
		List<PortalNavBo> navs = getCurrentUserDefaultNav(portalId, user);
		for(PortalNavBo bo : navs) {
			if("space".equals(bo.getNavType())) {//过滤掉个性化的空间
				Long spaceId = parseLong(bo.getId());
				if(spaceId!=null) {
					PortalSpaceFix fix = portalCacheManager.getPageFixIdCacheByKey(spaceId);
					if(fix!=null && fix.getParentId()!=null && map.containsKey(fix.getParentId().toString())) {
						continue;
					}
				}
			}
			if(!map.containsKey(bo.getId())) {
				bo.setIsDelete("0");
				result.add(bo);
			}
		}
		return result;
	}
	//管理员追加新增的门户,空间  只有管理员设置过导航以后才会走这里
	private List<PortalNavBo> addNewNavForAdmin(User user,String portalId,List<PortalNavBo> list,boolean isMasterPortal) throws BusinessException{
		List<PortalNavBo> result=new ArrayList<PortalNavBo>();
		if(list==null || list.isEmpty()) {
			return result;
		}
		result.addAll(list);
		List<PortalNavBo> navs = getSpaceForNav(portalId);
//		List<PortalNavBo> linkSpaces = getLinkSpaces();
//		if(linkSpaces!=null && !linkSpaces.isEmpty()) {
//			navs.addAll(linkSpaces);
//		}
		if(isMasterPortal) {
			putPortals(navs, user);
		}
		Map<String,PortalNavBo> map = toMap(list);
		for(PortalNavBo bo:navs) {
			if(Strings.isNotBlank(bo.getId()) && !map.containsKey(bo.getId()) && !bo.getId().equals(portalId)) {
				bo.setIsDelete("0");
				result.add(bo);
			}
		}
		return result;
	}
	@Override
	@AjaxAccess
	public List<PortalNavBo> getCurrentUserNav(String portalId,String containMenu,String needSpaceGroup,
			String needPortalGroup,String needTileSecondMenu) throws BusinessException {
		User user=AppContext.getCurrentUser();
		Long portalLongId = Strings.isBlank(portalId)?null:Long.parseLong(portalId);
		PortalSet portalSet= portalCacheManager.getPortalSetFromCache(portalLongId);
		if(null==portalSet){
			return null;
		}
		boolean isSubPortal= !portalManager.isMasterPortal(portalSet);
		
		boolean isDefault=false;
		boolean needAddNew = false;
		PortalNav navData=getNavByCache(portalSet,"nav");
		List<PortalNavBo> list=null;
		if(navData!=null){
			list = navData.getPortalNavBoList();
			String key="nav_"+portalId+"_"+user.getId()+"_"+user.getLoginAccount();
			if(navCache.get(key) == null && !isSubPortal) {
				isDefault=true;
			}
			needAddNew = true;
		}
		if(list==null || list.size()==0){
			list=getCurrentUserDefaultNav(portalId,user);
			isDefault=true;
		}
		
		DefaultSpaceSetting accountSetting = DefaultSpaceSetting.getDefaultSpaceSettingForAccount(AppContext.currentAccountId(),portalId);
		
		Map<String,PortalSet> portalMap=new HashMap<String, PortalSet>();
		Map<String,SpaceBO> spaceMap=new HashMap<String, SpaceBO>();
		Map<String,PrivMenuBO> menuMap=new HashMap<String, PrivMenuBO>();
		Set<String> ids=putPrivIds(user, isDefault, portalMap, spaceMap, menuMap);//设置权限过滤用的ids,和门户,空间,菜单map对象
		if((!isDefault || needAddNew) && Strings.isNotBlank(portalId)) {//补一下新授权的空间和门户
			list = addNewNav(user, portalId, list);
		}
		list= converPortalNavBo(portalId,list,containMenu,needSpaceGroup,needPortalGroup,ids,portalMap,spaceMap,menuMap,isDefault,needTileSecondMenu);
		String canChangeDefaultSpace = "";
		if(accountSetting!=null){
			canChangeDefaultSpace = accountSetting.getAllowChangeDefaultSpace();
		}
		
		if(Strings.isNotBlank(canChangeDefaultSpace) && !isSubPortal){
			if("1".equals(needSpaceGroup)){
				if(list!=null && !list.isEmpty()){
					PortalNavBo bo=list.get(0);//分组以后空间分组在第一位
					List<PortalNavBo> spaceList=bo.getList();
					spaceList= sortNav(ids,spaceList, accountSetting,canChangeDefaultSpace,"nav",portalId,spaceMap,isDefault);
					bo.setList(spaceList);
					list.set(0, bo);
				}
			}else{
				//从新排序,如果默认控制不允许修改,则把默认空间提到第一位
				list= sortNav(ids,list, accountSetting,canChangeDefaultSpace,"nav",portalId,spaceMap,isDefault);
			}
		}
		boolean isBackFill="0".equals(needTileSecondMenu);//是否在回填,目前只有回填的时候needTileSecondMenu为0
		//长导航的时候追加所有菜单到后边,原来有个参数containMenu长导航的时候传1
		//如果是主门户并且containMenu为1(长导航的时候containMenu为1)把门户菜单追加到导航后边
		//backFill为0的
		if(!isSubPortal && "1".equals(containMenu) && !isBackFill) {
			appendPortalMenusToNav(list,portalId);
		}
		if(list!=null&&list.size()>0){
			int unitIndex=-1;//记录单位空间位置
			int groupIndex=-1;//记录集团空间位置
			int menuSize=0;//记录导航中的菜单类的数量
			int menuIndex=-1;//单个菜单时记录菜单的位置
			PortalNavBo menuNav=null;
			for(int j=0;j<list.size();j++){
				PortalNavBo navBo=list.get(j);
				if(navBo!=null){
					String navName = navBo.getNavName();
					String navType=navBo.getNavType();
					if("menu".equals(navType)){
						menuSize++;
						menuIndex=j;
						menuNav=navBo;
					}
					if(Strings.isNotBlank(navName)){
						navBo.setNavName(navName);
						if(isDefault){
							if(String.valueOf(SpaceType.corporation.ordinal()).equals(navBo.getSpaceType())){
								unitIndex=j;
							}
							if(String.valueOf(SpaceType.group.ordinal()).equals(navBo.getSpaceType())){
								groupIndex=j;
							}
						}
					}
				}
			}
			try{
				if(navData==null&&isDefault && unitIndex!=-1 && groupIndex!=-1 && (unitIndex<groupIndex)&&(!("0".equals(canChangeDefaultSpace)))){
					PortalSpaceFix defaultSpace = this.getDefaultSpace(accountSetting);
					boolean isCorporationSpaceDefault=false;
					isCorporationSpaceDefault=(defaultSpace!=null&&defaultSpace.getType()!=null&&(defaultSpace.getType()==SpaceType.corporation.ordinal()));
					if(!isCorporationSpaceDefault){
						//默认情况下，单位在集团前面的话需要调整顺序，集团在单位前面（仅在个人、单位管理员和集团管理员都没有调整过顺序的情况下才执行，并且单位空间非默认空间）
						PortalNavBo unitNavBo=list.get(unitIndex);
						PortalNavBo groupNavBo=list.get(groupIndex);
						PortalNavBo[] navBoArray=new PortalNavBo[list.size()];
						navBoArray=list.toArray(navBoArray);
						navBoArray[unitIndex]=groupNavBo;
						navBoArray[groupIndex]=unitNavBo;
						list = Arrays.asList(navBoArray);
					}

				}
			}
			catch (Exception e){
				logger.error("单位空间,集团空间排序调整出错!",e);
			}
			if(menuSize==1&&"1".equals(needTileSecondMenu)&&menuNav!=null){
				//仅有一个菜单时需要进行拆分
				List<PortalNavBo> navList=new ArrayList<PortalNavBo>(1);
				navList.add(menuNav);
				List<PortalNavBo> menuList=getSecondMenus(navList, menuIndex);
				list.remove(menuIndex);
				list.addAll(menuList);
			}
		}
		//if(menuNav.size()==1 && "1".equals(needTileSecondMenu)) {
		//	List<PortalNavBo> menuList=getSecondMenus(menuNav, menuIndex);
		//	result.remove(menuIndex);
		//	result.addAll(menuIndex,menuList);
		//}
		return list;
	}

	@Override
	@AjaxAccess
	public Map<String, Object> getCurrentUserNavPage(String portalId, String containMenu, String needSpaceGroup, String needPortalGroup, String needTileSecondMenu, Integer pageSize, Integer pageNum) throws BusinessException {
		Map<String,Object> dataMap=new HashMap<String, Object>(2);
		List<PortalNavBo> portalAllNavs =getCurrentUserNav(portalId,containMenu,needSpaceGroup,needPortalGroup,needTileSecondMenu);//所有的导航
		FlipInfo flipInfo=new FlipInfo();
		if(pageSize==null){
			pageSize=20;
		}
		if(pageNum==null){
			pageNum=1;
		}
		flipInfo.setSize(pageSize);
		flipInfo.setPage(pageNum);
		DBAgent.memoryPaging(portalAllNavs, flipInfo);
		Integer pages = flipInfo.getPages();//总页数
		List data = flipInfo.getData();
		boolean hasMore=true;
		if(pages <=pageNum){
			hasMore=false;
		}
		dataMap.put("hasMore",hasMore);
		if(pageNum> pages){
			data=null;
		}
		dataMap.put("dataNav",data);
		return dataMap;
	}

	//追加门户菜单到导航中
	private void appendPortalMenusToNav(List<PortalNavBo> navs,String portalId) throws BusinessException{
		List<MenuBO> menus = portalManager.getPortalMenus(portalId);
		if(menus==null) {
			logger.info("该门户下缺少菜单,portalId为:"+(Strings.isBlank(portalId)?"空":portalId));
			return ;
		}
		MenuBO agentMenuBo= portalManager.generateAgentMenuBo();
		if(null!=agentMenuBo){
			menus.add(0,agentMenuBo);
		}
		//if(menus.size()==1 && null == agentMenuBo) {
		//	menus = menus.get(0).getItems();
		//}
		Set<String> navIdsSet=new HashSet<String>(navs.size());
		for(PortalNavBo navBo:navs){
			navIdsSet.add(navBo.getId());
		}
		for(MenuBO menu:menus) {
			Long menuId = menu.getId();
			if(navIdsSet.contains(menuId.toString())){
				continue;//已经存在的去重处理
			}
			PortalNavBo nav=new PortalNavBo();
			nav.setId(menuId.toString());
			nav.setOpenType(menu.getTarget());
			nav.setNavType("menu");
			nav.setNavName(ResourceUtil.getString(menu.getName()));
			nav.setOpenType(menu.getTarget());//mainfrm or newWindow
			nav.setIcon(menu.getIcon());
			nav.setMenu(menu);
			if(Strings.isNotBlank(menu.getUrl())) {
				nav.setUrl(menu.getUrl());
			}
			navs.add(nav);
		}
	}
	//硬把默认空间怼到第一位
	private List<PortalNavBo> sortNav(Set<String> ids,List<PortalNavBo> list,DefaultSpaceSetting defaultSpace,String canChangeDefaultSpace,
			String navType,String portalId,Map<String,SpaceBO> spaceMap,boolean isDefault) throws BusinessException{
		List<PortalNavBo> result=new ArrayList<PortalNavBo>();
		if(list!=null){
			PortalSpaceFix defaultSpaceFix =this.getDefaultSpace(defaultSpace);
			boolean hasDefaultSpaceRight= true;
			if(defaultSpaceFix!=null){
				SpaceBO personalSpace= spaceMap.get(defaultSpaceFix.getId().toString());
				if(null!=personalSpace){
					defaultSpaceFix= portalCacheManager.getPageFixPathCacheByKey(personalSpace.getSpacePath());
				}
			}
			if(defaultSpaceFix==null || !ids.contains(defaultSpaceFix.getId().toString())){//默认空间也要经过权限控制才行fix bug OA-141325
				hasDefaultSpaceRight= false;
			} 
			int index=0;
			boolean beFind=false;
			boolean firstIsLeaderSpace=false;
			boolean needReplacePersonalSpaceWithLeaderSpace= false;
			int pushedIndex= -1;//领导空间索引位置
			int personIndex= -1;//个人空间索引位置
			int extIndex= -1;//编外人员空间
			boolean putLeaderSpaceFirst= false;
			for(int i=0;i<list.size();i++){
				PortalNavBo bo=list.get(i);
				if("1".equals(bo.getIsDelete())) {
					continue;//如果已移除跳过
				}
				if(i==0) {
					if(String.valueOf(SpaceType.default_leader.ordinal()).equals(bo.getSpaceType()) 
							|| String.valueOf(SpaceType.leader.ordinal()).equals(bo.getSpaceType())) {
						firstIsLeaderSpace=true;
					}
				}
				PortalSpaceFix space= null;
				Long longId = parseLong(bo.getId());
				if(longId != null) {
					space = portalCacheManager.getPageFixIdCacheByKey(longId);
				}
				//是否可以在导航数据中找到默认空间
				if(hasDefaultSpaceRight && 
						(bo.getId().equals(defaultSpaceFix.getId().toString()) || 
						(space!=null && space.getParentId()!=null && defaultSpaceFix.getId().longValue()==space.getParentId().longValue())
						)){
					index=i;
					beFind=true;
				}
				if(null!=space){
					if(space.getType() == SpaceType.default_leader.ordinal()
							|| space.getType() == SpaceType.leader.ordinal()) {//找到领导空间，看下是否要将个人空间替换为领导空间
						String extAttributes= space.getExtAttributes();
						SpaceFixUtil fixUtil = new SpaceFixUtil(extAttributes);
		                
						Boolean isAllowpushed= fixUtil.isAllowpushed();
						if(isAllowpushed){//需要将领导空间推送给个人用户，则需要将领导空间放在第一位
							needReplacePersonalSpaceWithLeaderSpace= true;
							pushedIndex= i;
						}
					}
					if(space.getType()== SpaceType.Default_personal.ordinal() 
							|| space.getType()== SpaceType.personal.ordinal()
							){//个人空间
						personIndex= i;
					}
					if(space.getType()== SpaceType.outer.ordinal() || space.getType()== SpaceType.Default_out_personal.ordinal()){//编外人员空间
						extIndex= i;
					}
				}
				
			}
			if(extIndex>-1){//有外部人员空间
				if(needReplacePersonalSpaceWithLeaderSpace && pushedIndex>-1 && isDefault) {
					list.set(extIndex, list.get(pushedIndex));
					list.remove(pushedIndex);
					result.addAll(list);
				}
			}else{
				//领导空间推送的时候如果有个性化的用户会直接把领导空间拼接到导航第一位
				//如果没有个性化则替换导航中的个人空间(个人空间可能不在第一位)
				if(needReplacePersonalSpaceWithLeaderSpace && personIndex>-1 && pushedIndex>-1 && isDefault){
					list.set(personIndex, list.get(pushedIndex));
					list.remove(pushedIndex);
					result.addAll(list);
					putLeaderSpaceFirst= true;
				}else{//默认空间处理逻辑
					//如果在list列表中找到默认空间,并且默认空间位置不在第一个,
					if(beFind && index>0){
						if("0".equals(canChangeDefaultSpace)){//并且管理员禁止修改默认空间,则把默认空间提到第一个
							result.add(list.get(index));
							result.addAll(list.subList(0,index));
							result.addAll(list.subList(index+1,list.size()));
						}else if("1".equals(canChangeDefaultSpace) && !isDefaultSpaceConfiged(navType,portalId)){//并且管理员允许修改默认空间,且个人没有设置过默认空间，则把默认空间提到第一个
							result.add(list.get(index));
							result.addAll(list.subList(0,index));
							result.addAll(list.subList(index+1,list.size()));
						}
					}
					
					//如果list列表中没找到默认空间,并且管理员禁止,并且第一位不是领导空间
					if(!needReplacePersonalSpaceWithLeaderSpace && hasDefaultSpaceRight && !beFind && !"1".equals(canChangeDefaultSpace) &&!firstIsLeaderSpace){
						PortalSpacePage spacePage= portalCacheManager.getPageCacheByKey(defaultSpaceFix.getPath());
						PortalNavBo bo=new PortalNavBo();
						bo.setId(defaultSpaceFix.getId().toString());
						bo.setNavName(ResourceUtil.getString(defaultSpaceFix.getSpacename()));
						bo.setNavType("space");
						bo.setSort(0);
						String spaceIcon= defaultSpaceFix.getSpaceIcon();
						//去掉图标的/seeyon
						if(Strings.isNotBlank(spaceIcon) && spaceIcon.indexOf("fileUpload.do")>-1 && spaceIcon.startsWith("/seeyon")) {
							spaceIcon=spaceIcon.replace("/seeyon", "");
						}
						bo.setIcon(spaceIcon);
						bo.setDecoration(spacePage.getDefaultLayoutDecorator());
						bo.setUrl(defaultSpaceFix.getPath());
						bo.setSpaceType(defaultSpaceFix.getType().toString());
						bo.setOpenType("mainfrm");
						result.add(bo);
						result.addAll(list); 
					}
				}
			}
			if(needReplacePersonalSpaceWithLeaderSpace && (firstIsLeaderSpace || putLeaderSpaceFirst) ){
				PortalNavBo defaultPortalNavBo= result.size()==0?list.get(0):result.get(0);
				if("9".equals(defaultPortalNavBo.getSpaceType()) || "10".equals(defaultPortalNavBo.getSpaceType())){
					defaultPortalNavBo.setDefaultSpace(true);
				}
			}
		}
		return result.size()==0?list:result;
	}
	
	private boolean isDefaultSpaceConfiged(String navType,String portalId) {
		User user=AppContext.getCurrentUser();
		//个人
		String key= navType+"_"+portalId+"_"+user.getId()+"_"+user.getLoginAccount();
		PortalNav nav=navCache.get(key);
		if(nav==null){
			return false;
		}
		return true;
	}

	@Override
	@AjaxAccess
	public List<PortalNavBo> getCurrentUserRelatedProjectSpace() throws BusinessException {
		List<PortalNavBo> relatedProjectSpaces = new ArrayList<PortalNavBo>();
		List<ProjectBO> relatedProjectList = null;
		User user=AppContext.getCurrentUser();
		if(projectApi == null || !AppContext.hasPlugin("project")){
			relatedProjectList = new ArrayList<ProjectBO>();
		}
		if(user.isAdmin()){
			relatedProjectList = projectApi.findProjectsByAccountId(user.getLoginAccount());
		}else{
			relatedProjectList = projectApi.findProjectsByMemberId(user.getId());
		}
		if (CollectionUtils.isNotEmpty(relatedProjectList)) {
            for (ProjectBO rpj : relatedProjectList) {
                PortalNavBo bo=new PortalNavBo(String.valueOf(rpj.getId()),Strings.toHTML(rpj.getProjectName()),PortalNavType.linkProject.name(),"mainfrm","/seeyon/project/project.do?method=projectSpace&from=fromSpace&projectId=" + rpj.getId(),"menu_relateproject.png",0,null,String.valueOf(SpaceType.related_project_space.ordinal()));
                relatedProjectSpaces.add(bo);
            }
        }
		return relatedProjectSpaces;
	}
	@Override
	@AjaxAccess
	public List<PortalNavBo> getLinkSpaces() throws BusinessException{
		List<PortalLinkSpace> list=this.linkSpaceDao.getLinkSpaces();
		List<PortalNavBo> result=new ArrayList<PortalNavBo>();
		if(list==null || list.size()==0){
			return result;
		}
		User user=AppContext.getCurrentUser();
		for(PortalLinkSpace pls:list){
			if(user.isAdmin()){
				PortalNavBo bo=new PortalNavBo(String.valueOf(pls.getId()),pls.getSpaceName(),PortalNavType.linkSystem.name(),"mainfrm","/seeyon/portal/linkSystemController.do?method=linkConnect&linkId=" + pls.getId(),"menu_linksystem.png",0,null,String.valueOf(SpaceType.related_system.ordinal()));
				result.add(bo);
			}else{
				Set<PortalLinkSpaceAcl> set=pls.getLinkSpaceAcls();
				boolean hasSec=false;
				if(set!=null && set.size()>0){
					List<Long> domains = getDomainIds(user);
					B:
					for(PortalLinkSpaceAcl acl:set){
						if(domains.contains(acl.getUserId())){
							hasSec=true;
							break B;
						}
					}
				}
				if(hasSec){
					PortalNavBo bo=new PortalNavBo(String.valueOf(pls.getId()),pls.getSpaceName(),"linkSystem","mainfrm","/seeyon/portal/linkSystemController.do?method=linkConnect&linkId=" + pls.getId(),"menu_linksystem.png",0,null,String.valueOf(SpaceType.related_system.ordinal()));
					result.add(bo);
				}
			}
		}
		return result;
	}

	@Override
	@AjaxAccess
	public MenuBO getMenuByLevel1Id(String menuId) throws BusinessException {
		User user=AppContext.getCurrentUser();
		List<MenuBO> list=portalMenuManager.getMenusOfMember(user);
		if(list!=null){
			for(MenuBO bo:list){
				if(bo.getId().toString().equals(menuId)){
					return bo;
				}
			}
		}
		return null;
	}
	@Override
	@AjaxAccess
	public void savePortalMenus(String menuJson,String portalId,String isManager) throws BusinessException{
		User user=AppContext.getCurrentUser();
		long entityId=user.getId();
		long accountId=user.getLoginAccount();
		if(user.isAdmin() || "1".equals(isManager)){
			entityId=user.getLoginAccount();
		}
		savePortalMenus(menuJson,portalId,entityId,accountId);
	}
	@Override
	@SuppressWarnings("unchecked")
	public void savePortalMenus(String menuJson,String portalId,Long entityId,Long accountId) throws BusinessException{
		if(Strings.isBlank(portalId)){
			return ;
		}
		if(Strings.isNotBlank(menuJson)){
			List<Map<String,Object>> menus = JSONUtil.parseJSONString(menuJson, List.class);
			if(menus!=null && menus.size()>0){
				List<PortalMenuBo> list=new ArrayList<PortalMenuBo>();
				Set<String> menuIdSelectedSet=new HashSet<String>(menus.size());
				for(int i=0; i<menus.size();i++){
	                Map<String,Object> menu = menus.get(i);
	                String menuId = ((String)menu.get("id")).split("_")[1];
	                int isChecked = ((Boolean)menu.get("checked")) == true?1:0;
	                String sort = String.valueOf(menu.get("sort"));
	                PortalMenuBo portalMenu = new PortalMenuBo();
	                portalMenu.setMenuId(menuId);
	                portalMenu.setSortId(Integer.parseInt(sort));
	                portalMenu.setChecked(isChecked);
	                list.add(portalMenu);
	                menuIdSelectedSet.add(menuId);
	            }
	            //性能优化针对自定义菜单没有权限的也做记录处理，避免登录时再做校验
				List<PortalCustomizeMenu> customizeMenuList =portalCustomizeMenuManager.getAllCanSeeMenusInMem();
				if(customizeMenuList!=null&&customizeMenuList.size()>0){
					for(PortalCustomizeMenu customizeMenu:customizeMenuList){
						Long cusomizeMenuId = customizeMenu.getId();
						if(cusomizeMenuId!=null&&(!menuIdSelectedSet.contains(cusomizeMenuId.toString()))){
							PortalMenuBo portalMenu = new PortalMenuBo();
							portalMenu.setMenuId(cusomizeMenuId.toString());
							portalMenu.setSortId(-1);
							portalMenu.setChecked(-1);
							list.add(portalMenu);
						}
					}
				}
				//
				if(list.size()>0){
					PortalNav portalNav= this.getDefaultNav(entityId, accountId, portalId, "menu");
					boolean update= true;
					if(null==portalNav){
						portalNav= new PortalNav();
						portalNav.setIdIfNew();
						update= false;
					}
					portalNav.setEntityId(entityId);
					portalNav.setAccountId(accountId);
					portalNav.setPortalId(Long.parseLong(portalId));
					portalNav.setNavType("menu");
					portalNav.setNavValue(JSONUtil.toJSONString(list));
					portalNav.setPortalMenuBoList(list);
					if(update){
						portalNavDao.update(portalNav);
					}else{
						portalNavDao.save(portalNav);
					}
					String key=portalNav.getNavType()+"_"+portalId+"_"+entityId+"_"+accountId;
					this.navCache.put(key, portalNav);
				}
			}
		}
	}
	private List<PortalMenuBo> toMenuBo(PortalNav nav){
		if(nav!=null && Strings.isNotBlank(nav.getNavValue())){
			String json=nav.getNavValue();
			List<PortalMenuBo> list= nav.getPortalMenuBoList();
			if(null==list || list.isEmpty()){
				list=JSON.parseObject(json, new TypeReference<ArrayList<PortalMenuBo>>() {});
				nav.setPortalMenuBoList(list);
			}
			return list;
		}
		return null;
	}
	//查询门户菜单
	@Override
	public List<PortalMenuBo> getPortalMenus(Long portalId) throws BusinessException {
		if(portalId==null){
			return null;
		}
		PortalNav nav=getNavByCache(portalCacheManager.getPortalSetFromCache(portalId), "menu");
		if(nav!=null) {
			return nav.getPortalMenuBoList();
		}
		return null;
	}
	//回填菜单
	@Override
	public List<MenuTreeNode> getPortalMenuTree(Long portalId) throws BusinessException {
		List<PortalMenuBo> portalSpaceMenus = getPortalMenus(portalId);
		List<MenuTreeNode> allMenus = portalMenuManager.getAllUseAbleMenus();
		if (CollectionUtils.isNotEmpty(portalSpaceMenus)) {
			Map<String, PortalMenuBo> menuMap = new HashMap<String, PortalMenuBo>();
			for (PortalMenuBo menu : portalSpaceMenus) {
				String menuId = "menu_" + menu.getMenuId();
				menuMap.put(menuId, menu);
			}
			for (MenuTreeNode node : allMenus) {
				PortalMenuBo menu = menuMap.get(node.getIdKey());
				if (menu != null) {
					node.setSort(String.valueOf(menu.getSortId()));
					node.setChecked(menu.getChecked() == 1 ? true : false);
				}
			}
		}else {
			for (MenuTreeNode node : allMenus) {
				node.setChecked(true);
			}
		}
		Comparator<MenuTreeNode> comparator = new Comparator<MenuTreeNode>() {
			@Override
			public int compare(final MenuTreeNode o1, final MenuTreeNode o2) {
				if (Strings.isBlank(o1.getSort()) || Strings.isBlank(o2.getSort())) {
					return 0;
				} else {
					return Integer.parseInt(o1.getSort()) - Integer.parseInt(o2.getSort());
				}
			}
		};
		Collections.sort(allMenus, comparator);
		MenuTreeNode rootNode = new MenuTreeNode();
		rootNode.setIdKey("menu_0");
		String rootName = ResourceUtil.getString("menuManager.menuTree.root.label");
		rootNode.setNameKey(rootName);
		rootNode.setpIdKey(null);
		rootNode.setUrlKey(null);
		rootNode.setIconKey(null);
		allMenus.add(rootNode);
		return allMenus;
	}

	@Override
	public void pushLeaderNav(PortalSpaceFix space, List<V3xOrgMember> members, Long accountId) throws BusinessException {
		//获取当前单位的主门户,领导空间只会在主门户下
        String portalId=this.portalCacheManager.getUserAcessMainPortalIdFromCache(AppContext.getCurrentUser());
        if(Strings.isNotBlank(portalId)) {
        	//List<PortalNavBo> defaultNav=getCurrentUserDefaultNav(portalId);
        	long portalLongId=Long.parseLong(portalId);
        	List<PortalNav> list=findBy(portalLongId, null, accountId, "nav");
        	Map<Long,PortalNav> map=new HashMap<Long, PortalNav>();
        	List<PortalNav> updateNavs=new ArrayList<PortalNav>();
        	Map<String,PortalNav> cacheMap=new HashMap<String, PortalNav>();
        	if(list!=null && !list.isEmpty()) {
        		for(PortalNav nav:list) {
        			map.put(nav.getEntityId(), nav);
        		}
        	}
        	for(V3xOrgMember member:members) {//给单位下所有做个导航配置的人员追加领导空间
        		long memberId=member.getId();
        		if(map.containsKey(memberId)){
        			PortalNav n=map.get(memberId);
        			String json=n.getNavValue();
        			List<PortalNavBo> boList= n.getPortalNavBoList();
					if(null==boList || boList.isEmpty()){
						boList= JSON.parseObject(json, new TypeReference<ArrayList<PortalNavBo>>() {});
					}
        			ArrayList<PortalNavBo> result=addLeaderSpace(space, boList);
        			n.setNavValue(JSONUtil.toJSONString(result));
        			n.setPortalNavBoList(result);
        			String key=n.getNavType()+"_"+portalId+"_"+memberId+"_"+accountId;
        			cacheMap.put(key,n);
        			updateNavs.add(n);
        		}
        	}
        	
        	boolean update= true;
        	PortalNav accountPortalNav= this.getDefaultNav(accountId, accountId, portalId, "nav");
        	if(null==accountPortalNav){
        		accountPortalNav= new PortalNav();
        		accountPortalNav.setNewId();
        		update= false;
        	}
        	
			/*ArrayList<PortalNavBo> result=addLeaderSpace(space, new ArrayList<PortalNavBo>(defaultNav));
			accountPortalNav.setPortalId(Strings.isBlank(portalId)?null:Long.parseLong(portalId));
			accountPortalNav.setEntityId(accountId);
			accountPortalNav.setAccountId(accountId);
			accountPortalNav.setNavType("nav");
			accountPortalNav.setNavValue(JSONUtil.toJSONString(result));
			String key=accountPortalNav.getNavType()+"_"+portalId+"_"+accountId+"_"+accountId;
			accountPortalNav.setPortalNavBoList(result);
			cacheMap.put(key,accountPortalNav);
			
			if(update){
				DBAgent.update(accountPortalNav);
			}else{
				DBAgent.save(accountPortalNav);
			}*/
        	
        	if(updateNavs!=null && !updateNavs.isEmpty()) {
        		DBAgent.saveAll(updateNavs);
        	}
        	//更新缓存
        	navCache.putAll(cacheMap);
        }
	}
	private Long parseLong(String id) {
		try {
			return Long.parseLong(id);
		} catch (Exception e) {
			return null;
		}
	}
	//找到个人空间去掉,然后把领导空间放到第一位
	private ArrayList<PortalNavBo> addLeaderSpace(PortalSpaceFix space,List<PortalNavBo> boList){
		ArrayList<PortalNavBo> list=new ArrayList<PortalNavBo>();
		PortalNavBo bo=new PortalNavBo();
		bo.setId(space.getId().toString());
		bo.setNavName(ResourceUtil.getString(space.getSpacename()));
		bo.setNavType(PortalNavType.space.name());
		bo.setSort(0);
		list.add(bo);
		for(PortalNavBo nav:boList) {
			String id=nav.getId();
			if("space".equals(nav.getNavType()) && Strings.isNotBlank(id)) {
				Long fixId=parseLong(id);
				if(fixId!=null) {
					PortalSpaceFix fix=portalCacheManager.getPageFixIdCacheByKey(fixId);
					if(fix!=null) {
						int spaceType=fix.getType();
						if(Constants.SpaceType.leader.ordinal() == spaceType || Constants.SpaceType.default_leader.ordinal() == spaceType) {
							//如果导航里已存在则去掉从新推
							if(fixId.longValue()==space.getId().longValue() || (fix.getParentId()!=null && fix.getParentId().longValue()==space.getId().longValue())) {
								continue;
							}
						}
						//如果是个人空间或者是外部人员空间则去掉  如果是当前推送的领导空间去重(第一位已经放了一个)
						if(Constants.SpaceType.Default_personal.ordinal() == spaceType || Constants.SpaceType.personal.ordinal() == spaceType
								|| Constants.SpaceType.Default_out_personal.ordinal() == spaceType || Constants.SpaceType.outer.ordinal() == spaceType) {
							nav.setIsDelete("1");
							nav.setHeaderspaceId(space.getId().toString());
						}
					}
				}
			}
			list.add(nav);
		}
		return list;
	}

	@Override
	public List<PortalNav> getPortalNavs(Long accountId, Long portalId) throws BusinessException {
		List<PortalNav> allPortalNavs= portalNavDao.getPortalNavs(accountId,portalId);
		return allPortalNavs;
	}

	@Override
	public PortalNav getDefaultNav(Long entityId,Long accountId, String portalId,String navType) throws BusinessException {
		PortalNav portalNav= portalNavDao.getDefaultNav(entityId, accountId, portalId, navType);
		return portalNav;
	}

	public LinkSpaceManager getLinkSpaceManager() {
		return linkSpaceManager;
	}

	public void setLinkSpaceManager(LinkSpaceManager linkSpaceManager) {
		this.linkSpaceManager = linkSpaceManager;
	}

	@Override
	public void appendThirdPartyToMasterPortalNav(String portalType,PortalNavBo nav, List<Long> memberIds) throws BusinessException {
		if(memberIds==null || memberIds.isEmpty() || nav==null || Strings.isBlank(nav.getId()) || Strings.isBlank(nav.getNavName())) {
			throw new BusinessException("参数错误!");
		}
		nav.setNavType(PortalNavType.thirdPartyPortal.name()+"|"+portalType);
		Map<String,Object> map= new HashMap<String, Object>();
		map.put("type", "thirdPortal");
		map.put("nav", nav);
		map.put("memberIds", memberIds);
		super.addTask(map);
	}
	private boolean findNav(List<PortalNavBo> list,String id) {
		boolean isFind=false;
		if(Strings.isBlank(id)) {
			return isFind;
		}
		if(list!=null && !list.isEmpty()) {
			for(PortalNavBo bo : list) {
				if(id.equals(bo.getId())) {
					isFind = true;
					break;
				}
			}
		}
		return isFind;
	}
	private PortalNav newPortalNav(Long userId,Long accountId,String portalId,String navType,List<PortalNavBo> navs) {
		PortalNav nav=new PortalNav();
		Long portalLongId = Strings.isNotBlank(portalId)?Long.parseLong(portalId):null;
		nav.setNewId();
		nav.setPortalId(portalLongId);
		nav.setNavType(navType);
		nav.setEntityId(userId);
		nav.setAccountId(accountId);
		nav.setNavValue(JSONUtil.toJSONString(navs));
		nav.setPortalNavBoList(navs);
		return nav;
	}
	private User memberToUser(V3xOrgMember member,Long accountId) {
		User user=new User();
		user.setId(member.getId());
		user.setAccountId(member.getOrgAccountId());
		user.setLoginAccount(accountId);
		user.setAdministrator(false);
		user.setSystemAdmin(false);
		user.setGroupAdmin(false);
		user.setAuditAdmin(false);
		user.setSuperAdmin(false);
		user.setPlatformAdmin(false);
		return user;
	}
	@Override
	public void removeMasterPortalThirdPartyNav(String navId, List<Long> memberIds) throws BusinessException {
		if(memberIds==null || memberIds.isEmpty() || Strings.isBlank(navId)) {
			throw new BusinessException("参数错误!");
		}
		Map<String,Object> map= new HashMap<String, Object>();
		map.put("type", "removeThirdPortal");
		map.put("memberIds", memberIds);
		map.put("navId", navId);
		super.addTask(map);
	}
	@Override
	public String getFirstPersonalSpacePath(String portalId) throws BusinessException {
		List<PortalNavBo> list = this.getCurrentUserNav(portalId, "0", "0", "0", "0");
		Long spaceId = null;
		L:
		if(list!=null && !list.isEmpty()) {
			for(PortalNavBo bo:list) {
				String navType = bo.getNavType();
				Long id = parseLong(bo.getId());
				if("space".equals(navType) && id != null) {
					PortalSpaceFix fix = portalCacheManager.getPageFixIdCacheByKey(id);
					if(fix!=null && Constants.isPersonalStyleSpace(fix.getType())) {
						spaceId = fix.getId();
						break L;
					}
				}
			}
		}
		List<SpaceBO> spaces = portalCacheManager.getSpaceBoList(AppContext.getCurrentUser(), "pc",portalId);
		if(spaces!=null && !spaces.isEmpty()) {
			for(SpaceBO space : spaces) {
				if(spaceId == null) {
					int spaceType = Integer.parseInt(space.getSpaceType());
					if(Constants.isPersonalStyleSpace(spaceType)) {
						return space.getSpacePath();
					}
				}else {
					if(spaceId.toString().equals(space.getSpaceId()) || spaceId.toString().equals(space.getParentId())) {
						return space.getSpacePath();
					}
				}
			}
			
		}
		return null;
	}

	@Override
	public void restoreMenu(String portalId) throws BusinessException {
		restoreMenuOrNav(portalId,"menu");
	}

	@Override
	public void restoreNav(String portalId) throws BusinessException {
		restoreMenuOrNav(portalId,"nav");
		User user = AppContext.getCurrentUser();
		String key=portalId+"_"+user.getId()+"_"+user.getLoginAccount()+"_"+user.getLocale();
		//String accountKey=portalId+"_"+administrator.getId()+"_"+user.getLoginAccount()+"_"+user.getLocale();
		//List<PortalNavBo> accountNavBoList = navBoCache.get(accountKey);//管理员的导航内容
		//navBoCache.put(key,accountNavBoList);
		PortalNavLastModity.put(key,new Date().getTime());
	}
	private void saveOrAppendMobilePortalNav(String portalId,String mobileNavType,List<Map<String, String>> navs,String from) throws BusinessException{
		if(navs==null){
			navs = new ArrayList<Map<String,String>>();
		}
		User user = AppContext.getCurrentUser();
		boolean hasAddNav=false;
		long entityId = PortalCommonUtil.getEntityId(user);
		long accountId = PortalCommonUtil.getAccountId(user);
		long portalLongId = Strings.isBlank(portalId)?null:Long.parseLong(portalId);
		PortalSet portal = portalCacheManager.getPortalSetFromCache(parseLong(portalId));
		if(portal!=null) {
			//仅门户管理员，单位管理员，集团管理员可以设置导航内容直接取门户对应的内容就可以
			entityId = portal.getEntityId();
			accountId = portal.getAccountId();
		}
		ArrayList<PortalNavBo> list = new ArrayList<PortalNavBo>();
		for(int index=0;index<navs.size();index++){
			Map<String,String> map=navs.get(index);
			String allIds = map.get("allIds");
			if("maddNav|common|PortletCategory".equals(allIds)){
				hasAddNav=true;
			}
			String id=null;
			if(Strings.isNotBlank(allIds)){
				String[] IdArray = allIds.split("\\|");
				if(IdArray.length==3){
					id=IdArray[0];
				}
			}
			String navName = map.get("navName");
			String navType= map.get("navType");
			String icon=map.get("icon");
			String origName=map.get("origName");
			String url=map.get("url");
			String mobileNavModifyFlag=map.get("mobileNavModifyFlag");
			if(id!=null){
				PortalNavBo bo=new PortalNavBo();
				bo.setId(id);
				bo.setNavType(navType);
				bo.setNavName(navName);
				bo.setSort(index);
				bo.setAllIds(allIds);
				bo.setIcon(icon);
				if(Strings.isNotBlank(url)&&allIds.indexOf("singleURL|thirdPartyCategory")>-1){
					//仅指定url时保存
					bo.setUrl(url);
				}
				if(Strings.isNotBlank(origName)&&Strings.isNotBlank(navName)){
					if(!navName.equals(origName)){
						//原始名称与修改名称不相同，做了修改
						bo.setMobileNavModifyFlag(true);
					}else {
						bo.setMobileNavModifyFlag(false);
					}
				}
				if("true".equals(mobileNavModifyFlag)){
					bo.setMobileNavModifyFlag(true);
				}
				list.add(bo);
			}
		}
		if(!"append".equals(from)){
			PortalNav nav = getUserMobileNavCommon(portalId, mobileNavType,"config");//全部的
			if(nav!=null&&nav.getNoRightPortalNavBoList()!=null){
				list.addAll(nav.getNoRightPortalNavBoList());
			}
		}
		PortalNav portalNav= new PortalNav();
		portalNav.setIdIfNew();
		portalNav.setPortalId(portalLongId);
		portalNav.setEntityId(entityId);
		portalNav.setAccountId(accountId);
		portalNav.setNavType(mobileNavType);
		portalNav.setNavValue(JSONUtil.toJSONString(list));
		portalNav.setPortalNavBoList(list);
		portalNavDao.deleteBy(-1L, portalLongId, entityId, accountId, mobileNavType);
		portalNavDao.save(portalNav);
		String key=portalNav.getNavType()+"_"+portalId+"_"+entityId+"_"+accountId;
		navCache.put(key, portalNav);
		if("mBottomNav".equals(mobileNavType)){
			//底导航判断是否存在加号导航如果没有加号导航对应的加号导航数据也需要删除
			if(!hasAddNav){
				portalNavDao.deleteBy(-1L, portalLongId, entityId, accountId,"mAddNav");
				String addNavkey="mAddNav"+"_"+portalId+"_"+entityId+"_"+accountId;
				navCache.remove(addNavkey);
			}
		}
	}
	@Override
	public void saveMobilePortalNav(String portalId,String mobileNavType,List<Map<String, String>> navs) throws BusinessException {
		saveOrAppendMobilePortalNav(portalId,mobileNavType,navs,"config");
	}
    private void appendAllSpaceInPortalToNav(String portalId) throws BusinessException{
		PortalSet portalSetFromCache = portalCacheManager.getPortalSetFromCache(Long.valueOf(portalId));
		List<Map<String,String>> navsMap=new ArrayList<Map<String, String>>();
		ArrayList<PortalNavBo> portalNavBoList = new ArrayList<PortalNavBo>();
		Set<String> appendPortalSet=new HashSet<String>();
		List<PortalNavBo> allPortalNavBo=new ArrayList<PortalNavBo>();
		if(portalSetFromCache!=null){
			//门户下空间
			List<PortalSpaceFix> mobileSpacesList = spaceManager.getMobileSpace4Admin(portalSetFromCache.getAccountId(), "mobile", portalId);
			if(mobileSpacesList!=null&&mobileSpacesList.size()>0){
				for(PortalSpaceFix fix:mobileSpacesList){
					Long spaceId=fix.getId();
					String allIds = spaceId + "|" + portalId + "|SpaceAppCategory";
					PortalNavBo spaceNavBo=new PortalNavBo();
					spaceNavBo.setAllIds(allIds);
					spaceNavBo.setNavType("space");
					spaceNavBo.setNavName(ResourceUtil.getString(fix.getSpacename()));
					spaceNavBo.setId(spaceId.toString());
					spaceNavBo.setIcon("vp-space");
					portalNavBoList.add(spaceNavBo);
				}
			}
		}
		if(portalSetFromCache!=null){
			String item= "Mobile_"+PortalConstants.MASTER_PORTAL;
			Long accountIdPass = portalSetFromCache.getAccountId();
			Long entityId= accountIdPass;
			Long accountId= accountIdPass;
			String mainPortalIdStr= portalCacheManager.getPortalGlobalConfigFromCache(item, entityId, accountId);
			if(portalId.equals(mainPortalIdStr)){
				List<PortalSet> portalSetAllAppendList=new ArrayList<PortalSet>();
				List<PortalSet> portalSetRealAppendList=new ArrayList<PortalSet>();
				//2.主门户追加本单位下所有移动子门户
				Map<String,Object> myAccountMap = new HashMap<String, Object>();
				StringBuffer myAccountSb = new StringBuffer();
				myAccountSb.append(" from PortalSet where id!=:id and accountId =:accountId and (portalType=:typeA or portalType=:typeB) order by sortId");
				myAccountMap.put("accountId",accountId);
				myAccountMap.put("id",Long.valueOf(mainPortalIdStr));
				myAccountMap.put("typeA",1);
				myAccountMap.put("typeB",5);
				List<PortalSet> portalSetMyAccountList = DBAgent.find(myAccountSb.toString(), myAccountMap);
				portalSetAllAppendList.addAll(portalSetMyAccountList);
				//3.主门户追加其他单位授权给本单位的移动门户（含授权给本单位下人和部门、岗位及职务）
				Map<String,Object> params = new HashMap<String, Object>();
				StringBuffer sb = new StringBuffer();
				sb.append(" from PortalSet where accountId !=:accountId and (portalType=:typeA or portalType=:typeB) order by sortId");
				params.put("accountId",accountId);
				params.put("typeA",1);
				params.put("typeB",5);
				List<PortalSet> notMyAccountList = DBAgent.find(sb.toString(), params);
				portalSetAllAppendList.addAll(notMyAccountList);

				for(PortalSet set:portalSetAllAppendList){
					Integer portalType = set.getPortalType();
					boolean isAppend=false;
					if(portalType.intValue()==5){
						Integer mobilePortal = set.getMobilePortal();
						if(mobilePortal!=null&&mobilePortal.intValue()==1){
							isAppend=true;
						}
					}
					else {
						  isAppend=true;
					}
					if(isAppend){
						Long setId = set.getId();
						if(set.getAccountId().longValue()==OrgConstants.GROUPID.longValue()){
							//集团的直接追加
							portalSetRealAppendList.add(set);
							continue;
						}else if(set.getAccountId().longValue()==accountIdPass.longValue()){
							//本单位的子门户直接添加
							portalSetRealAppendList.add(set);
							continue;
						}
						List<String> list = portalSetSecurityManager.getSecurityByPortalId(String.valueOf(setId));
						if( null!=list && !list.isEmpty() && list.size()>=2 ){
							String canShareValue= list.get(0);
							if(Strings.isNotBlank(canShareValue)){
								//使用授权的追加
								List<V3xOrgEntity> entityList = orgManager.getEntities(canShareValue);
								for(V3xOrgEntity entity:entityList){
									Long orgAccountId = entity.getOrgAccountId();
									if(accountIdPass.longValue()==orgAccountId.longValue()){
										//使用授权包含本单位的追加
										portalSetRealAppendList.add(set);
										break;
									}
								}
							}
						}
					}
				}
				for(PortalSet realAppendSet:portalSetRealAppendList){
					List<PortalNavBo> portalNavBos = appendMobilePortalToMainPortal(realAppendSet, "Account|" + portalSetFromCache.getAccountId(), true);
					if(portalNavBos!=null){
						allPortalNavBo.addAll(portalNavBos);
					}
				}
				portalNavBoList.addAll(allPortalNavBo);
			}
		}
		for(PortalNavBo bo:portalNavBoList){
			HashMap<String,String> map=new HashMap<String, String>();
			String allIds = bo.getAllIds();
			map.put("allIds", allIds);
			map.put("navName",bo.getNavName());
			map.put("navType",bo.getNavType());
			map.put("id",bo.getId());
			map.put("icon",bo.getIcon());
			if(!appendPortalSet.contains(allIds)){
				navsMap.add(map);
				appendPortalSet.add(allIds);
			}
		}
		if(navsMap.size()>0){
			saveOrAppendMobilePortalNav(portalId,"mTopOrMiddleNav",navsMap,"append");
		}
	}
	private PortalNav getUserMobileNavCommon(String portalId, String mobileNavType,String fromType) throws BusinessException{
		PortalNav navByCache = getMobileNavByCache(portalId, mobileNavType);
		if("mTopOrMiddleNav".equals(mobileNavType)&&Strings.isNotBlank(portalId)&&Strings.isDigits(portalId)&&navByCache==null){
			//中上导航需要默认将改门户下的空间都添加到导航中,需要将授权给全集团的门户自动追加到信息门户中
			try{
				appendAllSpaceInPortalToNav(portalId);
				navByCache=getMobileNavByCache(portalId,mobileNavType);//补偿完成后再获取一下
			}
			catch (Exception e){
				logger.error("自动补偿中上导航时出错了!",e);
			}
		}
		if(navByCache==null){
			return null;
		}
		String navValue = navByCache.getNavValue();
		Type type=new com.seeyon.ctp.util.TypeReference<ArrayList<PortalNavBo>>(){}.getType();
		List<PortalNavBo> navs=JSONUtil.parseJSONString(navValue, type);
		Comparator<PortalNavBo> comparator = new Comparator<PortalNavBo>() {
			@Override
			public int compare(final PortalNavBo o1, final PortalNavBo o2) {
				return o1.getSort()-o2.getSort();
			}
		};
		Collections.sort(navs, comparator);
		List<PortalNavBo> canUseNavList=new ArrayList<PortalNavBo>();
		Set<String> canUseNavSet=new HashSet<String>();
		List<PortalNavBo> noUseNavList=new ArrayList<PortalNavBo>();
		Set<String> noUseSet=new HashSet<String>();
		for(PortalNavBo bo:navs){
			String allIds = bo.getAllIds();
			if(Strings.isNotBlank(allIds)){
				if("index".equals(bo.getNavType())&&allIds.endsWith("index")){
					//首页底导航特殊处理
					bo.setDisplayName(ResourceUtil.getString("portal.comp.page.first"));
					if(!bo.isMobileNavModifyFlag()){
						bo.setNavName(ResourceUtil.getString("portal.comp.page.first"));
					}
					bo.setNavType("index");
					String spaceId="index";
					String mTopOrMiddleNavFirstSpaceId=null;
					if(!AppContext.isAdmin()){
						if("mBottomNav".equals(mobileNavType)){
							//底导航获取首页时对首页的处理
							boolean hasMiddleOrTopNav=false;
							String themeIdStr = portalCacheManager.getDefaultThemeByCache(portalId,true);
							if(Strings.isBlank(themeIdStr)){
								if (!AppContext.getCurrentUser().isV5Member()) {//V-Join
									themeIdStr= "2100000010";
								} else {
									themeIdStr= "2100000001";
								}
							}
							Long themeId = Long.parseLong(themeIdStr);
							PortalTheme theme =portalCacheManager.getPortalTheme(themeId,Long.valueOf(portalId));//查询主题信息
							if(null==theme){
								themeId= 2100000001L;
								theme= portalCacheManager.getPortalTheme(themeId);
							}
							Long tid = theme.getTid();
							String datatplJsArr = portalCacheManager.getPortaLayoutDataTpls(tid);
							if(datatplJsArr!=null&&(datatplJsArr.contains("navBar")||datatplJsArr.contains("topNav"))){
								hasMiddleOrTopNav=true;
							}
							if(hasMiddleOrTopNav==true){
								PortalNav userMobileNavCommon = getUserMobileNavCommon(portalId, "mTopOrMiddleNav", "front");//中上导航
								if(userMobileNavCommon!=null){
									List<PortalNavBo> navBoList = userMobileNavCommon.getPortalNavBoList();
									if(navBoList!=null&&navBoList.size()>0){
										//有中上导航取的是门户上中导航中排最左边有权限的本门户空间
										for(PortalNavBo portalNavBo:navBoList){
											String navType = portalNavBo.getNavType();
											if("space".equals(navType)){
												String tempAllIds = portalNavBo.getAllIds();
												if(tempAllIds.contains(portalId)){
													mTopOrMiddleNavFirstSpaceId=portalNavBo.getId();//本门户下第一个有权限的空间
													break;
												}
											}
										}
									}
								}

							}
						}
						List<SpaceBO> spaces = portalCacheManager.getSpaceBoList(AppContext.getCurrentUser(),"mobile", portalId);
						if(mTopOrMiddleNavFirstSpaceId!=null){
							//管理员设置的中上导航中的内容可能是空间模板
							for(SpaceBO spaceBO:spaces){
								if(spaceBO!=null&&mTopOrMiddleNavFirstSpaceId.equals(spaceBO.getParentId())){
									mTopOrMiddleNavFirstSpaceId=spaceBO.getSpaceId();
									break;
								}
							}
							//中上导航存在并且有第一个设置的导航
							bo.setId(mTopOrMiddleNavFirstSpaceId);
							bo.setUrl("spaceId|"+mTopOrMiddleNavFirstSpaceId);
						}
						else {
							//普通用户需要获取当前门户下的第一个有权限的空间
							if(spaces!=null&&spaces.size()>0){
								SpaceBO spaceBO = spaces.get(0);
								spaceId=spaceBO.getSpaceId();
								bo.setId(spaceId);
								bo.setUrl("spaceId|"+spaceId);
							}
						}
					}
					bo.setAllIds(spaceId+"|"+portalId+"|index");//格式为spaceId|portalId|index
					bo.setOpenType(PortletConstants.UrlType.space.name());//打开的类型
					if(!canUseNavSet.contains(bo.getAllIds())){
						canUseNavList.add(bo);
						canUseNavSet.add(bo.getAllIds());
					}
				}
				if("portal".equals(bo.getNavType())){
					//校验对应的门户是否已经删除
					String id = bo.getId();
					if(Strings.isNotBlank(id)&&Strings.isDigits(id)){
						PortalSet portalSetFromCache = portalCacheManager.getPortalSetFromCache(Long.valueOf(id));
						if(portalSetFromCache==null||portalSetFromCache.getIsdelete().intValue()==1){
							continue;
						}
						if(Strings.isNotBlank(portalId)&&Strings.isDigits(portalId)){
							PortalSet fromPortal=portalCacheManager.getPortalSetFromCache(Long.valueOf(portalId));
							if(fromPortal!=null&&fromPortal.getAccountId()!=null){
								String item= "Mobile_"+PortalConstants.MASTER_PORTAL;
								Long accountId = fromPortal.getAccountId();
								String mainPortalIdStr= portalCacheManager.getPortalGlobalConfigFromCache(item, accountId, accountId);//配置portal对应的移动主门户
								Long navAccountId = portalSetFromCache.getAccountId();
								if(!portalId.equals(mainPortalIdStr)&&(navAccountId !=null)){
									//配置的门户对应的主门户
									String navMainPortalIdStr= portalCacheManager.getPortalGlobalConfigFromCache(item, navAccountId, navAccountId);//配置portal对应的移动主门户
									if(id.equals(navMainPortalIdStr)&&(navAccountId.longValue()!=accountId.longValue())){
										logger.warn("子门户"+portalId+";导航"+id+"是其他单位的主门户，过滤掉不显示。"+navAccountId+";"+accountId);
										continue;
									}
								}
							}
						}
					}
				}
				if(!"append".equals(fromType)){
					//非配置态的时候
					ImagePortletLayout imagePortletLayoutReal = getImagePortletLayoutReal(allIds);
					if(imagePortletLayoutReal!=null){
						//前端展示需要权限过滤
						String displayName = imagePortletLayoutReal.getDisplayName();
						bo.setDisplayName(displayName);//原始名称
						bo.setOpenType(imagePortletLayoutReal.getPortletUrlType());//打开的类型
						String imagePortletLayoutRealId = imagePortletLayoutReal.getId();
						if(Strings.isNotBlank(imagePortletLayoutRealId)&&imagePortletLayoutRealId.indexOf("singleURL|thirdPartyCategory")<0){
							//指定URL时url中已经保存了url的内容
							bo.setUrl(imagePortletLayoutReal.getMobileUrl());
						}
						if(!bo.isMobileNavModifyFlag()){
							//未修改过的话导航名称取原始名称
							if(Strings.isNotBlank(displayName)){
								bo.setNavName(displayName);
							}else {
								continue;
							}
						}
						if(!canUseNavSet.contains(bo.getAllIds())){
							canUseNavList.add(bo);
							canUseNavSet.add(bo.getAllIds());
						}

					}else {
						//无权限的记录
						if(!bo.getId().equals("1")&&(!noUseSet.contains(bo.getAllIds()))){
							noUseNavList.add(bo);
							noUseSet.add(bo.getAllIds());
						}
					}
				}
				else {
					//配置态无权限过滤
					canUseNavList.add(bo);
				}
			}
		}
		navByCache.setPortalNavBoList(canUseNavList);
		navByCache.setNoRightPortalNavBoList(noUseNavList);
		return navByCache;
	}
	@Override
	public PortalNav getCurrentUserMobileNav(String portalId, String mobileNavType) throws BusinessException{
		return getUserMobileNavCommon(portalId,mobileNavType,"config");
	}

	@Override
	public PortalNav getCurrentUserMobileNavFront(String portalId, String mobileNavType) throws BusinessException {
		return getUserMobileNavCommon(portalId,mobileNavType,"front");
	}
	@Override
	public void appendMobileSpaceToMobileNav(Long spaceId, String spaceName, Long portlId) throws BusinessException {
		PortalNavBo spaceNavBo=new PortalNavBo();
		spaceNavBo.setAllIds(spaceId+"|"+portlId+"|SpaceAppCategory");
		spaceNavBo.setNavType("space");
		spaceNavBo.setNavName(ResourceUtil.getString(spaceName));
		spaceNavBo.setId(spaceId.toString());
		spaceNavBo.setIcon("vp-space");
		checkAndAppendNav(portlId.toString(),spaceNavBo);
	}
    private void checkAndAppendNav(String toPortalId,PortalNavBo appendNavBo) throws BusinessException{
		try{
			PortalNav mTopOrMiddleNav = getUserMobileNavCommon(toPortalId, "mTopOrMiddleNav","append");//获取当前门户的中上导航
			List<PortalNavBo> portalNavBoList=null;
			if(mTopOrMiddleNav!=null){
				portalNavBoList= mTopOrMiddleNav.getPortalNavBoList();//已经存在的导航
			}
			if(portalNavBoList!=null&&portalNavBoList.size()>0){
				//校验一下导航中是否已经存在了已有的，已经存在的情况不添加
				boolean isAdd=true;
				for(PortalNavBo bo:portalNavBoList){
					String allIds = bo.getAllIds();
					String appendAllIds=appendNavBo.getAllIds();
					if(appendAllIds.equals(allIds)){
						isAdd=false;
						break;
					}
				}
				if(isAdd){
					portalNavBoList.add(appendNavBo);
				}
			}
			else {
				portalNavBoList=new ArrayList<PortalNavBo>();
				portalNavBoList.add(appendNavBo);
			}
			List<Map<String,String>> navsMap=new ArrayList<Map<String, String>>(portalNavBoList.size());
			for(PortalNavBo bo:portalNavBoList){
				HashMap<String,String> map=new HashMap<String, String>();
				map.put("allIds",bo.getAllIds());
				map.put("navName",bo.getNavName());
				map.put("navType",bo.getNavType());
				map.put("id",bo.getId());
				map.put("icon",bo.getIcon());
				map.put("origName",bo.getDisplayName());
				map.put("mobileNavModifyFlag",bo.isMobileNavModifyFlag()?"true":"false");
				navsMap.add(map);
			}
			saveOrAppendMobilePortalNav(toPortalId,"mTopOrMiddleNav",navsMap,"append");
		}
		catch (Exception e){
			logger.error("自动追加中上导航出错!",e);
		}


	}
	@Override
	public List<PortalNavBo> appendMobilePortalToMainPortal(PortalSet portalSet,String canshare,boolean firstsave) throws BusinessException {
		String portSetId=portalSet.getId().toString();
		Long setAccountId = portalSet.getAccountId();
		String portalName=portalSet.getPortalName();
		String item= "Mobile_"+PortalConstants.MASTER_PORTAL;
		Set<Long> accountIdList=new HashSet<Long>();//待追加的单位的IdList
		accountIdList.add(setAccountId);//自己的添加上
        if(setAccountId.longValue()==OrgConstants.GROUPID.longValue()){
        	//集团的移动门户需要添加到所有单位
			List<V3xOrgAccount> allAccounts = orgManager.getAllAccounts();
			for(V3xOrgAccount account:allAccounts){
				Long accountId = account.getId();
				if(accountId!=null&&(accountId.longValue()!=OrgConstants.GROUPID.longValue())){
					accountIdList.add(accountId);
				}
			}
		}
		if(Strings.isNotBlank(canshare)){
			//使用授权的追加
			List<V3xOrgEntity> entityList = orgManager.getEntities(canshare);
			for(V3xOrgEntity entity:entityList){
				Long orgAccountId = entity.getOrgAccountId();
				if(orgAccountId!=null){
					accountIdList.add(orgAccountId);
				}
				if(orgAccountId.longValue()==OrgConstants.GROUPID.longValue()){
					//使用授权是全单位的情况
					List<V3xOrgAccount> allAccounts = orgManager.getAllAccounts();
					for(V3xOrgAccount account:allAccounts){
						Long accountId = account.getId();
						if(accountId!=null&&(accountId.longValue()!=OrgConstants.GROUPID.longValue())){
							accountIdList.add(accountId);
						}
					}
				}
			}
		}
		List<PortalNavBo> appendList=new ArrayList<PortalNavBo>();
		for(Long accountIdPass:accountIdList){
			Long entityId= accountIdPass;
			Long accountId= accountIdPass;
			String mainPortalIdStr= portalCacheManager.getPortalGlobalConfigFromCache(item, entityId, accountId);
			if(Strings.isNotBlank(mainPortalIdStr)&&(!mainPortalIdStr.equals(portSetId))){
                PortalNavBo portalNavBo=new PortalNavBo();
			    if(portalSet.getPortalType()==999){
					portalNavBo.setId(portSetId + "|mobileBusiness|BusinessAppCategory");
					portalNavBo.setNavType("mobileBusiness");
					portalNavBo.setNavName(ResourceUtil.getString(portalName));
					portalNavBo.setAllIds(portSetId + "|mobileBusiness|BusinessAppCategory");
					portalNavBo.setIcon("vp-businesssolutionsgen");
                }else {
                    String allIdsPass=portSetId+"|portalSet|SpaceAppCategory";
                    portalNavBo.setAllIds(allIdsPass);
                    portalNavBo.setNavType("portal");
                    portalNavBo.setNavName(ResourceUtil.getString(portalName));
                    portalNavBo.setId(portSetId);
                    portalNavBo.setIcon("vp-Subportal");
                }
                if(firstsave){
					appendList.add(portalNavBo);
				}else {
					checkAndAppendNav(mainPortalIdStr,portalNavBo);
				}
			}
		}
		return appendList;
	}
	private ImagePortletLayout getImagePortletLayoutReal(String allIds){
		if(Strings.isBlank(allIds)){
			return null;
		}
		String[] portletArray = allIds.split("\\|");
		String porletId="";
		String subCategoryId="";
		String categoryId="";
		if(portletArray.length==3){
			porletId=portletArray[0];
			subCategoryId=portletArray[1];
			categoryId=portletArray[2];
		}
		Map<String, BasePortletCategory> portletCreaters = AppContext.getBeansOfType(BasePortletCategory.class);
		Map<String, BasePortletCategory> portletCreatersId = new HashMap<String, BasePortletCategory>(portletCreaters.size());
		Set<Map.Entry<String, BasePortletCategory>> entries = portletCreaters.entrySet();
		for (Map.Entry<String, BasePortletCategory> entry : entries) {
			BasePortletCategory basePortletCategory = entry.getValue();
			String tmpCategoryId = basePortletCategory.getCategoryId();
			portletCreatersId.put(tmpCategoryId, basePortletCategory);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("subCategory",subCategoryId);
		params.put("portletId",porletId);
		params.put("spaceType", "m3mobile");
		ImagePortletLayout layout = null;
		BasePortletCategory categoryCreater = portletCreatersId.get(categoryId);
		if (categoryCreater != null) {
			layout = categoryCreater.getPortletProperty(params);
		}
		return layout;
	}
	private void restoreMenuOrNav(String portalId,String type) throws BusinessException{
		User user = AppContext.getCurrentUser();
		long entityId = PortalCommonUtil.getEntityId(user);
		long accountId = PortalCommonUtil.getAccountId(user);
		long portalLongId = Strings.isBlank(portalId)?null:Long.parseLong(portalId);
		String key=type+"_"+portalId+"_"+entityId+"_"+accountId;
		portalNavDao.deleteBy(portalLongId, entityId, accountId,type);
		navCache.remove(key);
	}
	@Override
	protected void doBatch(List<Map<String,Object>> list) {
		for (Map<String, Object> map : list) {
			String type= (String)map.get("type");
			if("portal".equals(type)){
				this.appendPortal(map);
			}else if("space".equals(type)){
				this.appendSpace(map);
			}else if("thirdPortal".equals(type)) {
				this.appendThirdPortalToMasterPortalNav(map);
			}else if("removeThirdPortal".equals(type)) {
				this.removeMasterPortalThirdPartyNav(map);
			}
		}
	}

	private void appendSpace(Map<String, Object> paramMap) {
//		PortalSpaceFix space= (PortalSpaceFix)paramMap.get("space");
//		Long portalId= (Long)paramMap.get("portalId");
//		String auth= (String)paramMap.get("auth");
//		
//		try{
//			List<Long> ids=null;
//			if(portalId==1l) {//如果是集团主门户则要追加到所有的单位主门户下包括集团
//				ids = getAppendPortalIds(null, AppContext.currentAccountId());
//			}else {
//				ids = getAppendPortalIds(portalId, null);
//			}
//			//追加到对应门户
//			List<PortalNav> navList=portalNavDao.findByPortalIds(ids, "nav");
//			if(navList!=null){
//				List<Long> entityIds=getAppendEntityIds(auth);
//				Map<String,PortalNav> map=new HashMap<String, PortalNav>();
//				List<PortalNav> updateList=new ArrayList<PortalNav>();
//				for(PortalNav n:navList){
//					if(entityIds.contains(n.getEntityId())) {
//						String key=n.getNavType()+"_"+n.getPortalId()+"_"+n.getEntityId()+"_"+n.getAccountId();
//						//如果没配置过
//						if(n!=null && n.getNavValue()!=null){
//							String json=n.getNavValue();
//							String idStr="\"id\":\""+space.getId()+"\"";
//							if(json.indexOf(idStr)>-1) {
//								continue;//如果导航中已经包含此空间则跳过
//							}
//							List<PortalNavBo> list= n.getPortalNavBoList();
//							if(null==list || list.isEmpty()){
//								list= JSON.parseObject(json, new TypeReference<ArrayList<PortalNavBo>>() {});
//							}
//							PortalNavBo bo=new PortalNavBo();
//							bo.setId(space.getId().toString());
//							bo.setNavName(space.getSpacename());
//							bo.setNavType(PortalNavType.space.name());
//							bo.setSort(list.size()+1);
//							list.add(bo);
//							n.setNavValue(JSONUtil.toJSONString(list));
//							n.setPortalNavBoList(list);
//							map.put(key, n);
//							updateList.add(n);
//						}
//					}
//				}
//				if(!updateList.isEmpty()) {
//					this.navCache.putAll(map);
//					portalNavDao.updateAll(updateList);
//				}
//			}
//		}catch(Throwable e){
//			logger.error("追加空间到导航发生异常"+space.getId(),e);
//		}
	}

	private void appendPortal(Map<String, Object> paramMap){
//		PortalSet portal= (PortalSet)paramMap.get("portal");
//		String auth= (String)paramMap.get("auth");
//		try{
//			List<Long> ids=getAppendPortalIds(null, portal.getAccountId());
//			if(ids.size()>0){
//				//获取符合条件的导航数据
//				List<PortalNav> navList=this.portalNavDao.findByPortalIds(ids,"nav");
//				if(navList!=null){
//					List<Long> entityIds=getAppendEntityIds(auth);
//					Map<String,PortalNav> map=new HashMap<String, PortalNav>();
//					List<PortalNav> updateList=new ArrayList<PortalNav>();
//					for(PortalNav n:navList){
//						//只追加授权的人
//						if(entityIds.contains(n.getEntityId())) {
//							String key=n.getNavType()+"_"+n.getPortalId()+"_"+n.getEntityId()+"_"+n.getAccountId();
//							//如果没配置过
//							if(n!=null && n.getNavValue()!=null){
//								String json=n.getNavValue();
//								String idStr="\"id\":\""+portal.getId().toString()+"\"";
//								if(json.indexOf(idStr)>-1) {
//									continue;//如果导航中已包含此门户则跳过
//								}
//								List<PortalNavBo> list= n.getPortalNavBoList();
//								if(null==list || list.isEmpty()){
//									list= JSON.parseObject(json, new TypeReference<ArrayList<PortalNavBo>>() {});
//								}
//								PortalNavBo bo=new PortalNavBo();
//								bo.setId(portal.getId().toString());
//								bo.setNavName(portal.getPortalName());
//								bo.setNavType(PortalNavType.portal.name());
//								bo.setSort(list.size()+1);
//								list.add(bo);
//								n.setNavValue(JSONUtil.toJSONString(list));
//								n.setPortalNavBoList(list);
//								map.put(key, n);
//								updateList.add(n);
//							}
//						}
//					}
//					if(!updateList.isEmpty()) {
//						this.navCache.putAll(map);
//						portalNavDao.updateAll(updateList);
//					}
//				}
//			}
//		}catch(Throwable e){
//			logger.error("追加门户到导航发生异常"+portal.getId(),e);
//		}
	}
	private void appendThirdPortalToMasterPortalNav(Map<String, Object> paramMap) {
		try {
			List<Long> memberIds = paramMap.get("memberIds")==null?new ArrayList<Long>():((ArrayList<Long>)paramMap.get("memberIds"));
			PortalNavBo nav = (PortalNavBo)paramMap.get("nav");
			List<PortalNav> insertList=new ArrayList<PortalNav>();
			List<PortalNav> updateList=new ArrayList<PortalNav>();
			Map<String,PortalNav> map=new HashMap<String, PortalNav>();
			for(Long memberId:memberIds) {
				V3xOrgMember member = orgManager.getMemberById(memberId);
				List<MemberPost> posts = orgManager.getMemberPosts(null, memberId);
				if(posts!=null && !posts.isEmpty()) {
					for(MemberPost post:posts) {
						Long accountId = post.getOrgAccountId();
						User user = memberToUser(member, accountId);
						String masterPortalId = portalCacheManager.getUserAcessMainPortalIdFromCache(user);
						String key = "nav_"+masterPortalId+"_"+user.getId()+"_"+accountId;
						PortalNav portalNav = this.navCache.get(key);
						List<PortalNavBo> navs = null;
						if(portalNav==null) {
							navs = this.getCurrentUserDefaultNav(masterPortalId, user);
							boolean isFindNav=findNav(navs, nav.getId());
							if(isFindNav) {
								continue;
							}
							navs.add(nav);
							PortalNav navPo = newPortalNav(user.getId(), accountId, masterPortalId, "nav", navs);
							insertList.add(navPo);
							map.put(key, navPo);
						}else {
							navs = portalNav.getPortalNavBoList();
							boolean isFindNav=findNav(navs, nav.getId());
							if(isFindNav) {
								continue;
							}
							navs.add(nav);
							portalNav.setNavValue(JSONUtil.toJSONString(navs));
							portalNav.setPortalNavBoList(navs);
							updateList.add(portalNav);
							map.put(key, portalNav);
						}
					}
				}
			}
			if(insertList.size()>0) {
				portalNavDao.saveAll(insertList);
			}
			if(updateList.size()>0) {
				portalNavDao.updateAll(updateList);
			}
			if(insertList.size()>0 || updateList.size()>0) {
				this.navCache.putAll(map);
			}
		} catch (Exception e) {
			logger.error("追加第三方门户到导航发生异常!",e);
		}
	}
	private void removeMasterPortalThirdPartyNav(Map<String, Object> paramMap) {
		try {
			List<Long> memberIds = paramMap.get("memberIds")==null?new ArrayList<Long>():((ArrayList<Long>)paramMap.get("memberIds"));
			String navId = paramMap.get("navId")==null?"":paramMap.get("navId").toString();
			List<PortalNav> updateList=new ArrayList<PortalNav>();
			Map<String,PortalNav> map=new HashMap<String, PortalNav>();
			for(Long memberId:memberIds) {
				V3xOrgMember member = orgManager.getMemberById(memberId);
				List<MemberPost> posts = orgManager.getMemberPosts(null, memberId);
				if(posts!=null && !posts.isEmpty()) {
					for(MemberPost post:posts) {
						Long accountId = post.getOrgAccountId();
						User user = memberToUser(member, accountId);
						String masterPortalId = portalCacheManager.getUserAcessMainPortalIdFromCache(user);
						String key = "nav_"+masterPortalId+"_"+user.getId()+"_"+accountId;
						PortalNav portalNav = this.navCache.get(key);
						List<PortalNavBo> navs = null;
						if(portalNav!=null) {
							navs = portalNav.getPortalNavBoList();
							List<PortalNavBo> list=new ArrayList<PortalNavBo>();
							for(PortalNavBo bo:navs) {
								String navType = bo.getNavType();
								if(navType.indexOf("|")>-1) {
									navType = navType.split("\\|")[0];
								}
								if(navId.equals(bo.getId()) && PortalNavType.thirdPartyPortal.name().equals(navType)) {
									//navId==当前bo.id 并且是thirdPartyPortal类型过滤掉
								}else {
									list.add(bo);
								}
							}
							portalNav.setNavValue(JSONUtil.toJSONString(list));
							portalNav.setPortalNavBoList(list);
							updateList.add(portalNav);
							map.put(key, portalNav);
						}
					}
				}
			}
			if(updateList.size()>0) {
				portalNavDao.updateAll(updateList);
				navCache.putAll(map);
			}
		} catch (Exception e) {
			logger.error("移除第三方门户到导航发生异常!",e);
		}
	}

	public PrivilegeMenuManager getPrivilegeMenuManager() {
		return privilegeMenuManager;
	}

	public void setPrivilegeMenuManager(PrivilegeMenuManager privilegeMenuManager) {
		this.privilegeMenuManager = privilegeMenuManager;
	}

	@Override
	public PortalNavBo generatePortalNavBoFromSpaceBO(SpaceBO space) {
		PortalNavBo nav= this.spaceBo2NavBo(space);
		User user= AppContext.getCurrentUser();
		String spaceName=ResourceUtil.getString(space.getSpaceName());
		String[] nameAndIcon=this.getDefaultSpaceIconAndNameByType(space.getSpaceType(),spaceName, space.getAccountId(), user.getLoginAccount(),space);
		nav.setId(space.getSpaceId());
		nav.setNavName(nameAndIcon[1]);
		String spaceIcon=space.getSpaceIcon();
		if(String.valueOf(SpaceType.related_system.ordinal()).equals(space.getSpaceType()) && (Strings.isNotBlank(space.getSpaceIcon()) && space.getSpaceIcon().indexOf("fileUpload.do")==-1)) {
			spaceIcon = nameAndIcon[0];
		}
		if(Strings.isBlank(spaceIcon)) {
			spaceIcon = nameAndIcon[0];
		}
		//去掉图标的/seeyon
		if(Strings.isNotBlank(spaceIcon) && spaceIcon.indexOf("fileUpload.do")>-1 && spaceIcon.startsWith("/seeyon")) {
			spaceIcon=spaceIcon.replace("/seeyon", "");
		}
		nav.setIcon(Strings.isBlank(spaceIcon)?space.getSpaceIcon():spaceIcon);
		nav.setDecoration(space.getDecoration());
		nav.setUrl(space.getSpacePath());
		nav.setSpaceType(space.getSpaceType());
		nav.setOpenType("mainfrm");
		nav.setMasterPortalSpace(false);
		//兼容用12做项目空间type的数据
		if(String.valueOf(SpaceType.related_project.ordinal()).equals(space.getSpaceType()) ||
				String.valueOf(SpaceType.related_project_space.ordinal()).equals(space.getSpaceType())	) {
			nav.setNavType(PortalNavType.linkProject.name());
		}
		nav.setIcon(PortalCommonUtil.convertNavIcon(nav.getIcon()));
		return nav;
	}
	/**
	 * 创建G6版 组织空间导航菜单
	 * @throws BusinessException 
	 */
	@Override
	public List<PortalCustomizeMenu> createG6PortalNav(Long accountId, Long portalId, Long spaceId, Long createMemberId) throws BusinessException {
		Timestamp nowTime = new Timestamp(System.currentTimeMillis());
		List<PortalCustomizeMenu> menuList = new ArrayList<PortalCustomizeMenu>();
		PortalCustomizeMenu parentMmenu = new PortalCustomizeMenu();
		parentMmenu.setIdIfNew();
		parentMmenu.setShowName(ResourceUtil.getString("govdoc.nav.space.menu.name"));
		parentMmenu.setIcon("");
		parentMmenu.setSortNo(10002);
		parentMmenu.setParentId(0L);
		parentMmenu.setPortalId(portalId);
		parentMmenu.setUpdateMember(createMemberId);
		parentMmenu.setCreateTime(nowTime);
		parentMmenu.setAccountId(accountId);
		parentMmenu.setCategoryId("");
		parentMmenu.setSubCategoryId("");
		parentMmenu.setPortletId("");
		parentMmenu.setMenuLevel(1);
		parentMmenu.setModuleFrom("portal");
		parentMmenu.setUpdateTime(nowTime);
		menuList.add(parentMmenu);
		
		PortalCustomizeMenu menu = new PortalCustomizeMenu();
		menu.setIdIfNew();
		menu.setShowName(ResourceUtil.getString("govdoc.nav.bulletin.menu.name"));
		menu.setIcon("vp-cbulletin");
		menu.setSortNo(1);
		menu.setParentId(parentMmenu.getId());
		menu.setPortalId(portalId);
		menu.setUpdateMember(createMemberId);
		menu.setCreateTime(nowTime);
		menu.setAccountId(accountId);
		menu.setPortletId("MyBulletin");
		menu.setCategoryId("PortletCategory");
		menu.setSubCategoryId("publicInformation");
		menu.setMenuLevel(2);
		menu.setModuleFrom("portal");
		menu.setUpdateTime(nowTime);
		menuList.add(menu);
		
		menu = new PortalCustomizeMenu();
		menu.setIdIfNew();
		menu.setShowName(ResourceUtil.getString("govdoc.nav.dwxw.menu.name"));
		menu.setIcon("vp-news");
		menu.setSortNo(2);
		menu.setParentId(parentMmenu.getId());
		menu.setPortalId(portalId);
		menu.setUpdateMember(createMemberId);
		menu.setCreateTime(nowTime);
		menu.setAccountId(accountId);
		//需要查询出来
		/*List<NewsTypeBO> newsTypeList = newsApi.findNewsTypesByAccountId(new FlipInfo(),accountId,"","","");
		for (NewsTypeBO newsTypeBO : newsTypeList) {
			if ("单位要闻".equals(newsTypeBO.getTypeName())) {
				menu.setPortletId("news" + newsTypeBO.getId());
				break;
			}
		}*/
		menu.setCategoryId("publicInfoCategory");
		menu.setSubCategoryId("news");
		menu.setMenuLevel(2);
		menu.setModuleFrom("portal");
		menu.setUpdateTime(nowTime);
		menuList.add(menu);
		
		menu = new PortalCustomizeMenu();
		menu.setIdIfNew();
		menu.setShowName(ResourceUtil.getString("govdoc.nav.dwgk.menu.name"));
		menu.setIcon("vp-news");
		menu.setSortNo(3);
		menu.setParentId(parentMmenu.getId());
		menu.setPortalId(portalId);
		menu.setUpdateMember(createMemberId);
		menu.setCreateTime(nowTime);
		menu.setAccountId(accountId);
		/*for (NewsTypeBO newsTypeBO : newsTypeList) {
			if ("党务公开".equals(newsTypeBO.getTypeName())) {
				menu.setPortletId("news" + newsTypeBO.getId());
				break;
			}
		}*/
		menu.setCategoryId("publicInfoCategory");
		menu.setSubCategoryId("news");
		menu.setMenuLevel(2);
		menu.setModuleFrom("portal");
		menu.setUpdateTime(nowTime);
		menuList.add(menu);
		
		
		menu = new PortalCustomizeMenu();
		menu.setIdIfNew();
		menu.setShowName(ResourceUtil.getString("govdoc.nav.doccenter.menu.name"));
		menu.setIcon("vp-doccenter");
		menu.setSortNo(4);
		menu.setParentId(parentMmenu.getId());
		menu.setPortalId(portalId);
		menu.setUpdateMember(createMemberId);
		menu.setCreateTime(nowTime);
		menu.setAccountId(accountId);
		menu.setPortletId("DocIndex");
		menu.setCategoryId("PortletCategory");
		menu.setSubCategoryId("doc");
		menu.setMenuLevel(2);
		menu.setModuleFrom("portal");
		menu.setUpdateTime(nowTime);
		menuList.add(menu);
		
		menu = new PortalCustomizeMenu();
		menu.setIdIfNew();
		menu.setShowName(ResourceUtil.getString("govdoc.nav.leaderwindow.menu.name"));
		menu.setIcon("vp-edocfawen");
		menu.setSortNo(5);
		menu.setParentId(parentMmenu.getId());
		menu.setPortalId(portalId);
		menu.setUpdateMember(createMemberId);
		menu.setCreateTime(nowTime);
		menu.setAccountId(accountId);
		menu.setPortletId("leaderWindow");
		menu.setCategoryId("PortletCategory");
		menu.setSubCategoryId("common");
		menu.setMenuLevel(2);
		menu.setModuleFrom("portal");
		menu.setUpdateTime(nowTime);
		menuList.add(menu);
		
		portalCustomizeMenuManager.saveG6CustomizeMenus(menuList);
		
		PortalNav portalNav = new PortalNav();
		portalNav.setIdIfNew();
		portalNav.setPortalId(portalId);
		portalNav.setEntityId(accountId);
		portalNav.setAccountId(accountId);
		portalNav.setNavType("nav");
		List<PortalNavBo> list = new ArrayList<PortalNavBo>();
		PortalNavBo navBo = new PortalNavBo();
		navBo.setDefaultSpace(false);
		navBo.setId(spaceId + "");
		navBo.setNavName(ResourceUtil.getString("govdoc.nav.dwkj.menu.name"));
		navBo.setNavType("space");
		navBo.setSort(1);
		list.add(navBo);

		navBo = new PortalNavBo();
		navBo.setDefaultSpace(false);
		navBo.setId(parentMmenu.getId() + "");
		navBo.setNavName(ResourceUtil.getString("govdoc.nav.dwkjcd.menu.name"));
		navBo.setNavType("menu");
		navBo.setSort(2);
		list.add(navBo);
		portalNav.setNavValue(JSONUtil.toJSONString(list));
		portalNav.setPortalNavBoList(list);
		portalNavDao.save(portalNav);
		
		portalNavDao.deleteBy(portalNav.getId(), portalNav.getId(), accountId, accountId, "nav");
		String key=portalNav.getNavType()+"_"+portalId+"_"+accountId+"_"+accountId;
		navCache.put(key, portalNav);
		
		return menuList;
	}
}
