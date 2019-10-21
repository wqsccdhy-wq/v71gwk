package com.seeyon.apps.addressbook.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import com.seeyon.apps.addressbook.po.AddressBookTeam;
import com.seeyon.ctp.common.dao.BaseHibernateDao;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.po.OrgTeam;
import com.seeyon.ctp.util.Strings;

public class AddressBookTeamDaoImpl extends BaseHibernateDao<AddressBookTeam>implements AddressBookTeamDao{
	
	private transient static final Log LOG = LogFactory
	.getLog(AddressBookTeamDaoImpl.class);
	
	/**
	 * 查出该用户创建的所有类别
	 * @param creatorId 用户ID
	 * @return 类别列表
	 */
	public List findTeamsByCreatorId(final Long creatorId) {
/*		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
			throws HibernateException {
		StringBuilder sHql = new StringBuilder();
		sHql.append("select team from com.seeyon.v3x.addressbook.domain.AddressBookTeam team");
		sHql.append(" where team.creatorId = :creatorId");
		Query query = session.createQuery(sHql.toString());
		query.setLong("creatorId", creatorId);
		return query.list();
	}
});*/
		Session session = super.getSession();
		List<AddressBookTeam> teamList = null;
		try{
			Criteria criteria = session.createCriteria(AddressBookTeam.class);
			criteria.add(Expression.eq("creatorId",creatorId));
			criteria.addOrder(Order.desc("createdTime"));
			teamList = criteria.list();
		}catch(Exception ex){
			LOG.error(" ", ex);
		}finally{
			super.releaseSession(session);
		}	
		if ((teamList != null) && (!teamList.isEmpty())) {
			return teamList;
		}
		
		return null;
	}
	
	public AddressBookTeam getTeamById(Long teamId){
		return this.get(teamId);
	}
	
	/**
	 * 判断是否有相同的类别名称
	 */
	public boolean hasSameCategory(String name, Long createId) {
		Session session = super.getSession();
		List<AddressBookTeam> teamList = new ArrayList<AddressBookTeam>();
		try{
			Criteria criteria = session.createCriteria(AddressBookTeam.class);
			criteria.add(Expression.eq("creatorId", createId)).add(Expression.eq("name", name.trim()));
			teamList = toTypeSafeList(criteria.list());
		}catch(Exception ex){
			LOG.error("" , ex);
		}finally{
			super.releaseSession(session);
		}
		if (null != teamList && !teamList.isEmpty())
			return true;
		else
			return false;
	}
	
	/**
	 * 判断是否有相同的个人组
	 */
	public boolean hasSameOwnTeam(String name, Long ownerId, Long accountId){
		Session session = super.getSession();
		List<OrgTeam> teamList = new ArrayList<OrgTeam>();
		try{
			Criteria criteria = session.createCriteria(OrgTeam.class);
			criteria.add(Expression.eq("name", name.trim())).add(Expression.eq("deleted", false)).add(Expression.eq("ownerId", ownerId)).add(Expression.eq("orgAccountId", accountId));
			teamList = convertToSafeList(criteria.list());
		}catch(Exception ex){
			LOG.error("" , ex);
		}finally{
			super.releaseSession(session);
		}
		if(Strings.isNotEmpty(teamList)){
			return true;
		}else
			return false;
	}
	
	/**
	 * 判断是否有相同的讨论组
	 */
	public boolean hasSameDiscussTeam(String name, Long ownerId, Long accountId){
		Session session = super.getSession();
		List<V3xOrgTeam> teamList = new ArrayList<V3xOrgTeam>();
		try{
			Criteria criteria = session.createCriteria(V3xOrgTeam.class);
			//TODO fenghao 2012-11-05 criteria.add(Expression.eq("name", name.trim())).add(Expression.eq("type", V3xOrgEntity.TEAM_TYPE_DISCUSS)).add(Expression.eq("isDeleted", false)).add(Expression.eq("ownerId", ownerId)).add(Expression.eq("orgAccountId", accountId));
			teamList = toSafeList(criteria.list());
		}catch(Exception ex){
			LOG.error("" , ex);
		}finally{
			super.releaseSession(session);
		}
		
		if(null != teamList && !teamList.isEmpty()){
			return true;
		}else{
			return false;
		}
	}
    public List<AddressBookTeam> toTypeSafeList(List list)
    {
        return list;
    }
    public List<V3xOrgTeam> toSafeList(List list){
    	return list;
    }
    //增加转换方法
    private List<OrgTeam> convertToSafeList(List list){
    	return list;
    }
}
