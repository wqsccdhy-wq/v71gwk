package com.seeyon.apps.addressbook.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.apps.addressbook.po.AddressBook;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;

public class AddressBookCustomerFieldInfoDaoImpl implements AddressBookCustomerFieldInfoDao {

	@Override
	public FlipInfo getAll(FlipInfo fi, Map params) {
		 StringBuilder hql = new StringBuilder();
		 hql.append(" from AddressBook where 1=1 ");
		 HashMap<String,Object> pMap = new HashMap<String,Object>();
		 if(null!=params){
			 if(params.containsKey("memberId")){
				 Long memberId=Long.valueOf(params.get("memberId").toString());
				 hql.append(" and  memberId = :memberId ");
				 pMap.put("memberId",memberId);
			 }
	 
		 }
	     DBAgent.find(hql.toString(),pMap,fi);
	     return fi;
	}
	
	@Override
	public AddressBook getByMemberId(Long memberId) {
		 StringBuilder hql = new StringBuilder();
		 hql.append(" from AddressBook where 1=1 ");
		 HashMap<String,Object> pMap = new HashMap<String,Object>();
		 hql.append(" and  memberId = :memberId ");
		 pMap.put("memberId",memberId);
	     List<AddressBook> list=(List<AddressBook>)DBAgent.find(hql.toString(),pMap);
	     if(null!=list && list.size()>0){
	    	 return (AddressBook)list.get(0);
	     }
	     return null;
	}
	
	@Override
	public AddressBook getById(Long id) throws BusinessException {
		return DBAgent.get(AddressBook.class, id);
	}
	
    @Override
    public void save(AddressBook addressBook) {
        DBAgent.save(addressBook);
    }

    @Override
    public void update(AddressBook addressBook) {
        DBAgent.update(addressBook);
    }
    
    @Override
	public void updateAddressBookEmpty(String[] disabledLabels) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		hql.append("update AddressBook set ");
		//EXT_ATTR_1 -->  extAttr1  
        for(int i=0;i<disabledLabels.length;i++){
        	hql.append(disabledLabels[i].toLowerCase().replace("_", "").replace("a", "A")+"=:lable"+i+",");
        	map.put("lable"+i, null);
        }
		DBAgent.bulkUpdate(hql.substring(0,hql.length()-1).toString(), map);
	}
}
