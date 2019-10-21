package com.seeyon.ctp.common.usermapper.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import com.seeyon.apps.ldap.util.LDAPTool;
import com.seeyon.ctp.common.po.usermapper.CtpOrgUserMapper;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.Strings;

public class UserMapperDaoImpl extends com.seeyon.ctp.common.dao.BaseHibernateDao<CtpOrgUserMapper> implements UserMapperDao{
	protected static Log log = LogFactory.getLog(UserMapperDaoImpl.class);
	
	public void saveUserMapper(CtpOrgUserMapper usermapper)
	{
		//A8登录名称对应外部登录名称不能相同
		List<CtpOrgUserMapper> exLogs = getExLoginNames(usermapper.getLoginName(), usermapper
				.getType());
		boolean isExist = false;
		if (exLogs != null && !ListUtils.EMPTY_LIST.equals(exLogs)) {
			for (CtpOrgUserMapper userMapper : exLogs) {
				if (userMapper.getExLoginName().equals(usermapper.getExLoginName())) {
					isExist = true;
					break;
				}
			}
		}
		if (!isExist) {
			this.save(usermapper);
		}
	}
	
	public void updateUserMapper(CtpOrgUserMapper usermapper){
		this.update(usermapper);
	}
	
	public void deleteUserMapper(CtpOrgUserMapper usermapper){
		this.delete(usermapper);
	}
	
	public void clearType(String type){
		if(StringUtils.hasText(type)){
			String sql="delete from v3x_org_user_mapper where type='"+type+"'";
			try{
				this.executeUpdateJDBCSql(sql);//executeUpdateJDBCSql  executeUpdateSql
			}catch(Exception e){
				log.error("", e);
			}
		}
	}
	public void clearTypeLogin(String type,String login,OrgManagerDirect om){
		if(!StringUtils.hasText(login)){
//			clearType(type);
			return;
		}
		
		if(StringUtils.hasText(type)){
			V3xOrgMember m=null;
			try{
				m=om.getMemberByLoginName(login,true);
			}catch(Exception e){
				log.error("", e);
			}
			 String sql="delete from v3x_org_user_mapper where type='"
		           +type+"'  and  login_name='"+login+"'";
		 if(m!=null)
		 {
			 sql="delete from v3x_org_user_mapper where type='"
		           +type+"'  and  member_id="+m.getId()+"";
		 }
			try{
				this.executeUpdateJDBCSql(sql);//executeUpdateJDBCSql  executeUpdateSql
			}catch(Exception e){
				log.error("", e);
			}
		}
	}
	
	public Object executeUpdateSql(final String sql)throws HibernateException, SQLException{
		return this.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				//session.b;
				//Transaction tx = session.beginTransaction();
				Connection con=null;
				
				PreparedStatement stmt=null;
				try{
					con=session.connection();
					boolean auto=con.getAutoCommit();
					if(!auto)
						con.commit();
					stmt=con.prepareStatement(sql);
					stmt.executeUpdate();
					if(!auto)
						con.commit();
				}catch(Exception e){
					log.error("", e);
					if(con!=null){
						con.rollback();
					}
						
				}finally{
					if(stmt!=null)
						stmt.close();
					if(con!=null)
						con.close();
				}
				//tx.commit();
				return null;
			}
		});
	}
	public Object executeUpdateJDBCSql(final String sql)throws Exception{
	    JDBCAgent agent = new JDBCAgent(true);
	    try {
	    	return agent.execute(sql);
	    }catch(Exception e) {
	    	throw e;
	    }finally {
	    	agent.close();
	    }
/*		return JdbcAccessFactory.excute(new JdbcAccessCallback() {
  		public Object doAccess(JdbcConnection c) throws SQLException {
  			PreparedStatement stmt=null;
  			
  			try{
  				stmt=c.prepareStatement(sql);
				stmt.executeUpdate();
				
				if(!c.getAutoCommit())
					c.commit();
  			}catch(Exception e){
  				log.error(sql, e);
  			}finally{
  				if(stmt!=null)
					stmt.close();
  			}
  			return null;
 		}
 
  	});
*/	}
	
	public CtpOrgUserMapper getLoginName(String exLoginName,String type){
		DetachedCriteria criteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
	       criteria.add(Restrictions.eq("exLoginName", exLoginName));
	       criteria.add(Restrictions.eq("type", type));		
		List l = getHibernateTemplate().findByCriteria(criteria);
		if(l==null || l.isEmpty())
			return null;
	        
		return (CtpOrgUserMapper)l.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<CtpOrgUserMapper> getExLoginNames(String loginName,String type){
		DetachedCriteria criteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
		criteria.add(Restrictions.eq("loginName", loginName));
	    if(Strings.isNotBlank(type)){
	    	criteria.add(Restrictions.eq("type", type));
	    }
	    List l = getHibernateTemplate().findByCriteria(criteria); //super.executeCriteria(criteria);
	    return l;
	}
	
	@SuppressWarnings("unchecked")
	public List<CtpOrgUserMapper> getAllMapper(){
		DetachedCriteria criteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
	    List l = getHibernateTemplate().findByCriteria(criteria);
	    return l;
	}
	
	@SuppressWarnings("unchecked")
	public List<CtpOrgUserMapper> getAll(String type){
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
		detachedCriteria.add(Restrictions.eq("type", type));
		return getHibernateTemplate().findByCriteria(detachedCriteria); //super.executeCriteria(detachedCriteria);
	}
	
	public void mapper(String loginName, String type, List<CtpOrgUserMapper> mappers){
		//先删再建
		List<CtpOrgUserMapper> exLogs = getExLoginNames(loginName, type);
		if(exLogs!=null&&!ListUtils.EMPTY_LIST.equals(exLogs)){
			for(CtpOrgUserMapper userMapper:exLogs){
				deleteUserMapper(userMapper);
			}
		}
		for(CtpOrgUserMapper map:mappers){
			saveUserMapper(map);
		}		
	}

	public void map(String loginName, String exLoginName, String exPassword, String type, String userId, String exUserId, Long memberId) {
		// TODO Auto-generated method stub
		List<CtpOrgUserMapper> exLogs = getExLoginNames(loginName, type);
		boolean isExist = false;
		if(exLogs!=null&&!ListUtils.EMPTY_LIST.equals(exLogs)){
			for(CtpOrgUserMapper userMapper:exLogs){
				if(userMapper.getExLoginName().equals(exLoginName)){
					isExist = true;
					break;
				}
			}
		}	
		if(!isExist){
			CtpOrgUserMapper userMapper = new CtpOrgUserMapper();
			userMapper.setExLoginName(exLoginName);
			userMapper.setExId(userId);
			userMapper.setExPassword(exPassword);
			userMapper.setLoginName(loginName);
			userMapper.setType(type);
			userMapper.setExUserId(exUserId);
			userMapper.setMemberId(memberId);
			saveUserMapper(userMapper);
		}
	}
	
	public CtpOrgUserMapper getById(long id){
		return super.get(id);
	}
    @SuppressWarnings("unchecked")
    public List<CtpOrgUserMapper> getAllAndExId(String type, String exId)
    {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
        detachedCriteria.add(Restrictions.eq("type", type));
        detachedCriteria.add(Restrictions.eq("exId", exId));
        return getHibernateTemplate().findByCriteria(detachedCriteria);
    }
    public List<CtpOrgUserMapper> getAllAndExUserId(String type, String exUserId)
    {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
        detachedCriteria.add(Restrictions.eq("type", type));
        detachedCriteria.add(Restrictions.eq("exUserId", exUserId));
        return getHibernateTemplate().findByCriteria(detachedCriteria);
    }
    public CtpOrgUserMapper getUserMapperByExId(String exLoginName, String exId)
    {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
        detachedCriteria.add(Restrictions.eq("exLoginName", exLoginName));
        detachedCriteria.add(Restrictions.eq("exId", exId));
        List list=getHibernateTemplate().findByCriteria(detachedCriteria);
        
        if(list!=null&&!list.isEmpty())
        {
        	return (CtpOrgUserMapper)getHibernateTemplate().findByCriteria(detachedCriteria).get(0);
        }
        else
        {
        	return null; 
        }
    }

	public List<CtpOrgUserMapper> getExLoginNamesByMemberId(Long memberId,
			String type) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
		criteria.add(Restrictions.eq("memberId", memberId));
	    criteria.add(Restrictions.eq("type", type));
	    List l = getHibernateTemplate().findByCriteria(criteria); //super.executeCriteria(criteria);
	    return l;
	}

	public List<CtpOrgUserMapper> getMapByMemberIdEXLoginName(Long memberId,
			String exLoginName, String type) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
		criteria.add(Restrictions.eq("memberId", memberId));
		criteria.add(Restrictions.eq("exLoginName", exLoginName));
	    criteria.add(Restrictions.eq("type", type));
	    List list = getHibernateTemplate().findByCriteria(criteria);
	    return list;
	}
	
	public List<CtpOrgUserMapper> getMapByMemberIdEXId(Long memberId,
			String exLoginId, String type) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
		criteria.add(Restrictions.eq("memberId", memberId));
		criteria.add(Restrictions.eq("exUserId", exLoginId));
	    criteria.add(Restrictions.eq("type", type));
	    List list = getHibernateTemplate().findByCriteria(criteria);
	    return list;
	}
	
	public boolean isbind(String loginName){
		DetachedCriteria criteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
		Criterion arg1 = Restrictions.eq("exLoginName", loginName);     
	    Criterion arg2 = Restrictions.eq("loginName", loginName);         
	    Criterion arg = Restrictions.or(arg1, arg2);
	    criteria.add(arg);
	    Criterion arg3 = Restrictions.eq("type", LDAPTool.catchLDAPConfig().getType()); 
	    criteria.add(arg3);
		List<CtpOrgUserMapper> l = getHibernateTemplate().findByCriteria(criteria);
		if(l==null || l.isEmpty()) {
			return false;
		}

		for(CtpOrgUserMapper orgUserMapper : l){
			if(Strings.isNotBlank(orgUserMapper.getLoginName()) && Strings.isNotBlank(orgUserMapper.getExLoginName()) 
					&& Strings.isNotBlank(orgUserMapper.getExUnitCode())
					&& null!=orgUserMapper.getMemberId()){
				return true;
			}
		}
	        
		return false;
	}
	
	public boolean isbind(Long memberId){
		DetachedCriteria criteria = DetachedCriteria.forClass(CtpOrgUserMapper.class);
	    criteria.add(Restrictions.eq("memberId", memberId));
	    criteria.add(Restrictions.eq("type", LDAPTool.catchLDAPConfig().getType()));
		List l = getHibernateTemplate().findByCriteria(criteria);
		if(l==null || l.isEmpty()) {
			return false;
		}
		if(l.size()>1){
			return false;
		}
		CtpOrgUserMapper orgUserMapper = (CtpOrgUserMapper)l.get(0);
		if(Strings.isBlank(orgUserMapper.getLoginName()) 
				|| Strings.isBlank(orgUserMapper.getExLoginName()) 
				|| Strings.isBlank(orgUserMapper.getExUnitCode())){
			return false;
		}
	        
		return true;
	}
}