package com.seeyon.apps.addressbook.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.addressbook.dao.AddressBookCustomerFieldInfoDao;
import com.seeyon.apps.addressbook.po.AddressBook;
import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;


@ProcessInDataSource(name = DataSourceName.BASE)
public class AddressBookCustomerFieldInfoManagerImpl extends AbstractSystemInitializer implements AddressBookCustomerFieldInfoManager {

    private static final Log               log                 = LogFactory.getLog(AddressBookCustomerFieldInfoManagerImpl.class);
    private static final int maxSize = 1000000;
    
    private AddressBookCustomerFieldInfoDao addressBookCustomerFieldInfoDao;
    private AddressBookManager addressBookManager;
    private OrgManager orgManager;
    private OrgCache orgCache;
    private CacheMap<Long, AddressBook> AddressBookInfoCache = null;

	public void setAddressBookCustomerFieldInfoDao(
			AddressBookCustomerFieldInfoDao addressBookCustomerFieldInfoDao) {
		this.addressBookCustomerFieldInfoDao = addressBookCustomerFieldInfoDao;
	}

	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	

	public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}

	@Override
	public FlipInfo getAll(FlipInfo fi, Map params)throws BusinessException {
		return addressBookCustomerFieldInfoDao.getAll(fi, params);
	}
	
	@Override
	public AddressBook getByMemberId(Long memberId)throws BusinessException {
		return AddressBookInfoCache.get(memberId);
	}
	
    
	@Override
    public void addAddressBook(AddressBook addressBook) throws BusinessException {
		addressBookCustomerFieldInfoDao.save(addressBook);
		  AddressBookInfoCache.put(addressBook.getMemberId(), addressBook);
    }
    
    @Override
    public void updateAddressBook(AddressBook addressBook) throws BusinessException {
    	addressBookCustomerFieldInfoDao.update(addressBook);
    	AddressBookInfoCache.put(addressBook.getMemberId(), addressBook);
    } 
    
    @Override
    public void updateAddressBookEmpty(String[] disabledLabels) throws BusinessException{
    	addressBookCustomerFieldInfoDao.updateAddressBookEmpty(disabledLabels);
    	try {
    		Method method;
    		for (Long memberId : AddressBookInfoCache.keySet()) {
    			AddressBook addressBook= AddressBookInfoCache.get(memberId);
    			for(int i=0;i<disabledLabels.length;i++){
    				method=addressBookManager.getSetMethod(disabledLabels[i]);
    				if(null==method){
    					throw new BusinessException("自定义通讯录字段: "+disabledLabels[i]+"不存在！");
    				}
    				method.invoke(addressBook, new Object[] { null });
    			}
    		}
    	} catch (Exception e) {
    	} 
    } 
    
    @Override
	public void addAddressBooks(List<AddressBook> addressBooks) {
    	List<AddressBook> result = new UniqueList<AddressBook>();
        for (AddressBook addressBook : addressBooks) {
        	if(!addressBook.isEmpty()){
        		result.add(addressBook);
        		AddressBookInfoCache.put(addressBook.getMemberId(), addressBook);
        	}
        }
		DBAgent.saveAll(result);
	}
    
    @Override
	public List<AddressBook> getAllAddressbookinfos(Long accountId) {
    	List<AddressBook> result = new UniqueList<AddressBook>();
    	if(accountId == null){
    		return new ArrayList<AddressBook>(AddressBookInfoCache.values());
    	}
    	List<Long> memberIds = orgCache.getAllMembers(accountId);
    	for(Long memberId : AddressBookInfoCache.keySet()){
    		if(memberIds.contains(memberId)){
    			result.add(AddressBookInfoCache.get(memberId));
    		}
    	}
    	return result;
	}
    
    @Override
    public int getSortOrder() {
        return -999;
    }
    
	@SuppressWarnings("unchecked")
	public void initialize() {
        long start = System.currentTimeMillis();
        CacheAccessable factory = CacheFactory.getInstance(AddressBookCustomerFieldInfoManager.class);
        AddressBookInfoCache = factory.createMap("AddressBookInfoCache");
        //如果其他节点已经正常启动，启动时不需要重新load一遍数据，会自动从其他节点同步数据过来。
        if(!CacheFactory.isSkipFillData()){
        	FlipInfo fi = new FlipInfo();
        	fi.setSize(maxSize);
        	List<AddressBook> addressBookSetList = new ArrayList<AddressBook>();
        	addressBookCustomerFieldInfoDao.getAll(fi, null);
        	if(null!=fi.getData()){
        		addressBookSetList=fi.getData();
        	}
        	if (Strings.isNotEmpty(addressBookSetList)) {
        		for (AddressBook addressBook : addressBookSetList) {
        			AddressBookInfoCache.put(addressBook.getMemberId(), addressBook);
        		}
        	}
        }
        log.info("加载所有人员自定义通讯录信息. 耗时: " + (System.currentTimeMillis() - start) + " MS");
    }

}