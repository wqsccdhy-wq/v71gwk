package com.seeyon.apps.addressbook.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.apps.addressbook.po.AddressBookMember;
import com.seeyon.ctp.common.dao.BaseHibernateDao;
import com.seeyon.ctp.util.SQLWildcardUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.v3x.common.web.login.CurrentUser;

public class AddressBookMemberDaoImpl extends BaseHibernateDao<AddressBookMember> implements  AddressBookMemberDao{
	
	private transient static final Log LOG = LogFactory
	.getLog(AddressBookMemberDaoImpl.class);

	/**
	 * 查出该用户创建的所有外部联系人
	 * 
	 * @param creatorId
	 *            用户ID
	 * @return 外部联系人列表
	 */
	public List findMembersByCreatorId(final Long creatorId) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
			throws HibernateException {
		StringBuilder sHql = new StringBuilder();
		sHql.append("select mem from com.seeyon.apps.addressbook.po.AddressBookMember mem");
		sHql.append(" where mem.creatorId = :creator_id order by mem.createdTime");
		Query query = session.createQuery(sHql.toString());
//		query.setLong(0, creatorId);
//		query.setParameter("creatorId", creatorId, Hibernate.LONG);
		query.setLong("creator_id", creatorId);
		return query.list();
			}
		});
//		List<AddressBookMember> memberList = null;
//		StringBuilder hql = null;
//	
//		hql = new StringBuilder();
//		hql.append(" from com.seeyon.apps.addressbook.po.AddressBookMember as mem ");
//		hql.append(" where mem.creatorId = :cid ");
		
//		Object[] params = new Object[1];
//		params[0] = creatorId;
//		memberList = this.find(hql.toString(), params);
//		memberList = this.getHibernateTemplate().find(hql.toString(), creatorId);
//		memberList = this.getHibernateTemplate().findByNamedParam(hql.toString(), "cid", creatorId);
//		memberList = this.find(hql.toString(), null);
		
//		Criteria criteria = getSession().createCriteria(AddressBookMember.class);
//		criteria.add(Expression.eq("creatorId",creatorId));
//		criteria.addOrder(Order.desc("createdTime"));
//		memberList = criteria.list();
//		
//		if ((memberList != null) && (!memberList.isEmpty())) {
//			return memberList;
//		}
//		
//		return null;
	}
	
	/**
	 * 删除外部联系人
	 * @param memberIds 外部联系人列表
	 */
	public void deleteMembersByIds(final List<Long> memberIds) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuilder sHql = new StringBuilder();
				sHql.append("delete AddressBookMember");
				sHql.append(" where id in (:memberIds)");
				Query query = session.createQuery(sHql.toString());
				query.setParameterList("memberIds", memberIds);
				return query.executeUpdate();
			}
		});
	}
	
	public List findMembersByTeamId(final Long teamId){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Long userId = CurrentUser.get().getId();
				StringBuilder sHql = new StringBuilder();
				sHql.append("select mem from com.seeyon.apps.addressbook.po.AddressBookMember mem");
				sHql.append(" where mem.category = :teamId and mem.creatorId=:userId");
				Query query = session.createQuery(sHql.toString());
				query.setLong("teamId", teamId);
				query.setLong("userId", userId);
				return query.list();
			}
		});
	}
	
	/**
	 * 按姓名查找员工
	 */
	public List findOrgMembersByName(final String name){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuilder sHql = new StringBuilder();
				sHql.append("select orgMem from com.seeyon.v3x.organization.domain.V3xOrgMember orgMem");
				sHql.append(" where orgMem.name like :name");
				Query query = session.createQuery(sHql.toString());
				query.setString("name", "%"+SQLWildcardUtil.escape(name)+"%");
				return query.list();
			}
		});
	}
	
	/**
	 * 按姓名查找外部联系人
	 */
	public List findMemberByName(final String name){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Long userId = CurrentUser.get().getId();
				StringBuilder sHql = new StringBuilder();
				sHql.append("select mem from com.seeyon.apps.addressbook.po.AddressBookMember mem");
				sHql.append(" where mem.creatorId=:userId and mem.name like :name ");
				Query query = session.createQuery(sHql.toString());
				query.setLong("userId", userId);
				query.setString("name", "%"+SQLWildcardUtil.escape(name)+"%");
				return query.list();
			}
		});
	}
	
	/**
	 * 按手机号码查找外部联系人
	 */
	public List findMemberByTel(final String tel){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Long userId = CurrentUser.get().getId();
				StringBuilder sHql = new StringBuilder();
				sHql.append("select mem from com.seeyon.apps.addressbook.po.AddressBookMember mem");
				sHql.append(" where mem.creatorId=:userId and mem.mobilePhone like :tel ");
				Query query = session.createQuery(sHql.toString());
				query.setLong("userId", userId);
				query.setString("tel", "%"+SQLWildcardUtil.escape(tel)+"%");
				return query.list();
			}
		});
	}
	
	/**
	 * 按职务级别查找员工
	 */
	public List findOrgMemberByLevelName(final String levelName){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuilder sHql = new StringBuilder();
				sHql.append("select orgMem from com.seeyon.v3x.organization.domain.V3xOrgMember orgMem");
				sHql.append(" ,com.seeyon.v3x.organization.domain.V3xOrgLevel orgLevel");
				sHql.append(" where orgMem.orgLevelId = orgLevel.id");
				sHql.append(" and orgLevel.name like :levelName");
				Query query = session.createQuery(sHql.toString());
				query.setString("levelName", "%"+SQLWildcardUtil.escape(levelName)+"%");
				return query.list();
			}
		});
	}
	
	/**
	 * 按职务级别查找外部联系人
	 */
	public List findMemberByLevelName(final String levelName){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Long userId = CurrentUser.get().getId();
				StringBuilder sHql = new StringBuilder();
				sHql.append("select mem from com.seeyon.apps.addressbook.po.AddressBookMember mem");
				sHql.append(" where mem.creatorId = :userId");
				if(Strings.isNotBlank(levelName)){
					sHql.append(" and mem.companyLevel like :levelName");
				}
				Query query = session.createQuery(sHql.toString());
				query.setLong("userId", userId);
				if(Strings.isNotBlank(levelName)){
					query.setString("levelName", "%"+SQLWildcardUtil.escape(levelName)+"%");
				}
				return query.list();
			}
		});
	}
	
	/**
	 * 判断是否有相同的邮件地址
	 */
//	public boolean hasSameMail(String mail, String memberId) {
//		List<AddressBookMember> memberList = null;
//		Criteria criteria = getSession().createCriteria(AddressBookMember.class);
//		criteria.add(Expression.eq("email", mail)).add(Expression.eq("id", Long.parseLong(memberId)));
//		memberList = criteria.list();
//		if (null != memberList && !memberList.isEmpty()){
//					return true;
//		}
//		return false;
//	}
	
	public boolean hasSameMail(String mail, String memberId) {
		Session session = super.getSession();
		List<AddressBookMember> memberList = null;
		try{
			Criteria criteria = session.createCriteria(AddressBookMember.class);
			criteria.add(Expression.eq("email", mail.trim()));
			memberList = criteria.list();
		}catch(Exception ex){
			LOG.error("" , ex);
		}finally{
			super.releaseSession(session);
		}
		if(null != memberList && !memberList.isEmpty()){
			//if(memberList.get(0).getId() != Long.parseLong(memberId))
			if(memberList.get(0).getCreatorId() != Long.parseLong(memberId))
				return true;
		}
		return false;
	}
    /**
     * 按姓名和所属组查找外部联系人
     */
    public List findMemberByNameAndTeam(final String Name,final Long categoryId,final Long createrId){
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException {
                StringBuilder sHql = new StringBuilder();
                sHql.append("select mem from com.seeyon.apps.addressbook.po.AddressBookMember mem");
                sHql.append(" where mem.name = :userName");
                sHql.append(" and mem.category =:categoryId");
                sHql.append(" and mem.creatorId =:creatorId");
                Query query = session.createQuery(sHql.toString());
                query.setString("userName", Name);
                query.setLong("categoryId", categoryId);
                query.setLong("creatorId", createrId);
                return query.list();
            }
        });
    }
}
