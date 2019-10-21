package com.seeyon.ctp.organization.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.po.JoinAccount;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;

public class JoinAccountCustomerFieldInfoDaoImpl implements JoinAccountCustomerFieldInfoDao {
	@Override
	public FlipInfo getAll(FlipInfo fi, Map params) {
		 StringBuilder hql = new StringBuilder();
		 hql.append(" from JoinAccount where 1=1 ");
		 HashMap<String,Object> pMap = new HashMap<String,Object>();
		 if(null!=params){
			 if(params.containsKey("departmentId")){
				 Long departmentId=Long.valueOf(params.get("departmentId").toString());
				 hql.append(" and  departmentId = :departmentId ");
				 pMap.put("departmentId",departmentId);
			 }
			 if(params.containsKey("orgAccountId")){
				 Long orgAccountId=Long.valueOf(params.get("orgAccountId").toString());
				 hql.append(" and  orgAccountId = :orgAccountId ");
				 pMap.put("orgAccountId",orgAccountId);
			 }
	 
		 }
	     DBAgent.find(hql.toString(),pMap,fi);
	     return fi;
	}
	
	@Override
	public JoinAccount getById(Long id) throws BusinessException {
		return DBAgent.get(JoinAccount.class, id);
	}
	
    @Override
    public void save(JoinAccount joinAccount) {
        DBAgent.save(joinAccount);
    }

    @Override
    public void update(JoinAccount joinAccount) {
        DBAgent.update(joinAccount);
    }
    
	@Override
	public JoinAccount getByDepartmentId(Long departmentId) {
		 StringBuilder hql = new StringBuilder();
		 hql.append(" from JoinAccount where 1=1 ");
		 HashMap<String,Object> pMap = new HashMap<String,Object>();
		 hql.append(" and  departmentId = :departmentId ");
		 pMap.put("memberId",departmentId);
	     List<JoinAccount> list=(List<JoinAccount>)DBAgent.find(hql.toString(),pMap);
	     if(null!=list && list.size()>0){
	    	 return (JoinAccount)list.get(0);
	     }
	     return null;
	}
    
    @Override
	public void updateJoinAccountEmpty(String[] disabledLabels,Long orgAccountId) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> map = new HashMap<String, Object>();
		hql.append("update JoinAccount set ");
		//EXT_ATTR_1 -->  extAttr1  
        for(int i=0;i<disabledLabels.length;i++){
        	hql.append(disabledLabels[i].toLowerCase().replace("_", "").replace("a", "A")+"=:lable"+i+",");
        	map.put("lable"+i, null);
        }
        String e = hql.substring(0,hql.length()-1);
        e = e +" where orgAccountId =:orgAccountId ";
        map.put("orgAccountId", orgAccountId);
		DBAgent.bulkUpdate(e, map);
	}
}
