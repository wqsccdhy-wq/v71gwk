
package com.seeyon.ctp.permission.dao;

import com.seeyon.ctp.datasource.CtpDynamicDataSource;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.permission.po.PrivPermission;

public class LicensePerMissionCacheImpl extends AbstractSystemInitializer implements LicensePerMissionCache {
    
    private final static Log log = LogFactory.getLog(LicensePerMissionCacheImpl.class);
    
    // key - Id
    private CacheMap<Long, PrivPermission> privPermissionCache = null;
    
    
    private LicensePerMissionDao licensePerMissionDao;

    public void setLicensePerMissionDao(LicensePerMissionDao licensePerMissionDao){
        this.licensePerMissionDao = licensePerMissionDao;
    }
    @Override
    public void initialize() {
        CacheAccessable factory = CacheFactory.getInstance(LicensePerMissionCache.class); 

        
        privPermissionCache = factory.createMap("PrivPermission");
        //如果其他节点已经正常启动，启动时不需要重新load一遍数据，会自动从其他节点同步数据过来。
        if(!CacheFactory.isSkipFillData()){
            try{
                CtpDynamicDataSource.setDataSourceKey(DataSourceName.BASE.getSource());
                List<PrivPermission>  list = licensePerMissionDao.getAllPerMissionPO();
                for (PrivPermission privPermission : list) {
                    privPermissionCache.put(privPermission.getId(), privPermission);
                }
            }finally {
                CtpDynamicDataSource.clearDataSourceKey();
            }

        }
                
        log.info("加载单位许可数信息");
    }
    
    
    private  CacheMap<Long, PrivPermission> getCacheMap(){
        
        
        return privPermissionCache;
    } 

    
    /*******************  以下是缓存更新的代码  ******************/
    
    public  void cacheUpdate(PrivPermission privPermission) {
    	CacheMap<Long, PrivPermission> cache = getCacheMap();
    	cache.put(privPermission.getId(), privPermission);
        
    }

    public  void cacheUpdate(List<PrivPermission> privPermissions) {
    	CacheMap<Long, PrivPermission> cache = getCacheMap();   	
    	for (PrivPermission privPermission : privPermissions) {
    		cache.put(privPermission.getId(), privPermission);
		}
    }
    
    

    public  void cacheRemove() {
    	CacheMap<Long, PrivPermission> cache = getCacheMap();   
        cache.clear();       
        
    }

   
	@Override
	public List<PrivPermission> getAllPerMissionPO() {
		List<PrivPermission> list = new ArrayList<PrivPermission>();
		CacheMap<Long, PrivPermission> cache = getCacheMap();   
		Set<Long> set = cache.keySet();
		for (Long l : set) {
			list.add(cache.get(l));
		}
		// TODO Auto-generated method stub
		return list;
	}

	@Override
	public PrivPermission getPerMissionPO(Long accId, Integer lictype) {
		CacheMap<Long, PrivPermission> cache = getCacheMap();
		Set<Long> set = cache.keySet();
		for (Long l : set) {
			if(cache.get(l).getOrgAccountId().equals(accId)&&cache.get(l).getLictype().equals(lictype)){
				return cache.get(l);
			}
		}
		// TODO Auto-generated method stub
		return new PrivPermission();
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
	}

}
