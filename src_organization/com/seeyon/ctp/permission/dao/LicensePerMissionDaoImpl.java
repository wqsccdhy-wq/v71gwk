package com.seeyon.ctp.permission.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.permission.po.PrivPermission;
import com.seeyon.ctp.util.DBAgent;

/**
 * The DAO class for the entities: Department, Externalmember, Level, Member,
 * Metadata, Orgaccount, Orgproperty, Orgrelationship, Post, Team.
 */
public class LicensePerMissionDaoImpl implements LicensePerMissionDao {
	
	private LicensePerMissionCache licensePerMissionCache;

    public void setLicensePerMissionCache(LicensePerMissionCache licensePerMissionCache){
        this.licensePerMissionCache = licensePerMissionCache;
    }
	
	private final static Log log = LogFactory.getLog(LicensePerMissionDaoImpl.class);
	
	public List<PrivPermission> getAllPerMissionPO(){
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT m ");
        hql.append(" FROM " + PrivPermission.class.getSimpleName() + " as m " );
		return DBAgent.find(hql.toString());
	}
	public PrivPermission getPerMissionPO(Long accId,Integer lictype){
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT m ");
        hql.append(" FROM " + PrivPermission.class.getSimpleName() + " as m " );
        hql.append(" WHERE m.orgAccountId=:orgAccountId ");
        hql.append(" AND m.lictype=:lictype ");
        params.put("orgAccountId", accId);
        params.put("lictype", lictype);
        List<PrivPermission> list = DBAgent.find(hql.toString(), params);
        if(list.size()>0){
        	return list.get(0);
        }else{
        	return new PrivPermission();
        }
		
	}

    public void savePerMissionPO(PrivPermission privPermission){
    	DBAgent.saveOrUpdate(privPermission);
    	licensePerMissionCache.cacheUpdate(privPermission);
    }
    public void savePerMissionPO(List<PrivPermission> privPermissions){    	
    	DBAgent.saveAll(privPermissions);
    	licensePerMissionCache.cacheUpdate(privPermissions);
    }
    public void deleteAllPerMissionPO(){
    	DBAgent.bulkUpdate("DELETE FROM "+PrivPermission.class.getSimpleName()+" WHERE 1=1");
    	licensePerMissionCache.cacheRemove();
    }
    
    @Override
    public void saveOrUpdatePerMissionPO(List<PrivPermission> newPrivPermissions) {
    	DBAgent.mergeAll(newPrivPermissions);
    	licensePerMissionCache.cacheUpdate(newPrivPermissions);    	
    }
	
}