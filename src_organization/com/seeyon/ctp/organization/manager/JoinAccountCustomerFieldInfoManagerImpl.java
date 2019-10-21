package com.seeyon.ctp.organization.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.dao.JoinAccountCustomerFieldInfoDao;
import com.seeyon.ctp.organization.po.JoinAccount;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;


@ProcessInDataSource(name = DataSourceName.BASE)
public class JoinAccountCustomerFieldInfoManagerImpl extends AbstractSystemInitializer implements JoinAccountCustomerFieldInfoManager {

    private static final Log               log                 = LogFactory.getLog(JoinAccountCustomerFieldInfoManagerImpl.class);
    private static final int maxSize = 1000000;
    
    private JoinAccountCustomerFieldInfoDao joinAccountCustomerFieldInfoDao;
    private CacheMap<Long, JoinAccount> JoinAccountInfoCache = null;

	public JoinAccountCustomerFieldInfoDao getJoinAccountCustomerFieldInfoDao() {
		return joinAccountCustomerFieldInfoDao;
	}

	public void setJoinAccountCustomerFieldInfoDao(
			JoinAccountCustomerFieldInfoDao joinAccountCustomerFieldInfoDao) {
		this.joinAccountCustomerFieldInfoDao = joinAccountCustomerFieldInfoDao;
	}

	@Override
	public FlipInfo getAll(FlipInfo fi, Map params)throws BusinessException {
		return joinAccountCustomerFieldInfoDao.getAll(fi, params);
	}
	
	@Override
	public List<JoinAccount> getAllJoinAccount(Long accountId)throws BusinessException {
		List<JoinAccount> result = new UniqueList<JoinAccount>();
		for(Long id : JoinAccountInfoCache.keySet()){
			JoinAccount joinAccount = JoinAccountInfoCache.get(id);
			if(accountId != null){
				if(!accountId.equals(joinAccount.getOrgAccountId())){
					continue;
				}
			}
			result.add(joinAccount);
		}
		return result;
	}
	
	@Override
	public JoinAccount getByDepartmentId(Long departmentId)throws BusinessException {
		return JoinAccountInfoCache.get(departmentId);
	}
	
    
	@Override
    public void addJoinAccount(JoinAccount joinAccount) throws BusinessException {
		joinAccountCustomerFieldInfoDao.save(joinAccount);
		JoinAccountInfoCache.put(joinAccount.getDepartmentId(), joinAccount);
    }
    
    @Override
    public void updateJoinAccount(JoinAccount joinAccount) throws BusinessException {
    	joinAccountCustomerFieldInfoDao.update(joinAccount);
    	JoinAccountInfoCache.put(joinAccount.getDepartmentId(), joinAccount);
    } 
    
    @Override
    public void updateJoinAccountEmpty(String[] disabledLabels,Long orgAccountId) throws BusinessException{
    	joinAccountCustomerFieldInfoDao.updateJoinAccountEmpty(disabledLabels, orgAccountId);
    	try {
    		Method method;
    		for (Long departmentId : JoinAccountInfoCache.keySet()) {
    			JoinAccount joinAccount= JoinAccountInfoCache.get(departmentId);
    			for(int i=0;i<disabledLabels.length;i++){
    				method=getSetMethod(disabledLabels[i]);
    				if(null==method){
    					throw new BusinessException("自定义外单位属性字段: "+disabledLabels[i]+"不存在！");
    				}
    				method.invoke(joinAccount, new Object[] { null });
    			}
    		}
    	} catch (Exception e) {
    	} 
    } 
    
    @Override
	public void addJoinAccounts(List<JoinAccount> joinAccounts) {
        for (JoinAccount joinAccount : joinAccounts) {
        	JoinAccountInfoCache.put(joinAccount.getDepartmentId(), joinAccount);
        }
		DBAgent.saveAll(joinAccounts);
	}
    
    
	@SuppressWarnings("unchecked")
	@Override
	public void initialize() {
        long start = System.currentTimeMillis();
        CacheAccessable factory = CacheFactory.getInstance(JoinAccountCustomerFieldInfoManager.class);
        JoinAccountInfoCache = factory.createMap("JoinAccountInfoCache");
        //如果其他节点已经正常启动，启动时不需要重新load一遍数据，会自动从其他节点同步数据过来。
        if(!CacheFactory.isSkipFillData()){
        	FlipInfo fi = new FlipInfo();
        	fi.setSize(maxSize);
        	List<JoinAccount> joinAccountSetList = new ArrayList<JoinAccount>();
        	joinAccountCustomerFieldInfoDao.getAll(fi, null);
        	if(null!=fi.getData()){
        		joinAccountSetList=fi.getData();
        	}
        	if (Strings.isNotEmpty(joinAccountSetList)) {
        		for (JoinAccount joinAccount : joinAccountSetList) {
        			JoinAccountInfoCache.put(joinAccount.getDepartmentId(), joinAccount);
        		}
        	}
        }
        log.info("加载所有外单位自定义属性信息. 耗时: " + (System.currentTimeMillis() - start) + " MS");
    }
	
	@Override
	public Method getGetMethod(String fieldName) {
		fieldName=fieldName.replace("EXT_ATTR_", "ExtAttr");
		String method="get"+fieldName;
		try {
			return JoinAccount.class.getMethod(method);
		} catch (Exception e) {
		}
		return null;
	}
	
	@Override
	public Method getSetMethod(String fieldName) {
		fieldName=fieldName.replace("EXT_ATTR_", "extAttr");
		try {
			Class[] parameterTypes = new Class[1];    
			Field field = JoinAccount.class.getDeclaredField(fieldName);       
			parameterTypes[0] = field.getType();       
			
			StringBuilder sb = new StringBuilder();       
			sb.append("set");       
			sb.append(fieldName.substring(0, 1).toUpperCase());       
			sb.append(fieldName.substring(1));       
			
			String method=sb.toString();
			return JoinAccount.class.getMethod(method,parameterTypes);
		} catch (Exception e) {
		}
		return null;
	}

}