package com.seeyon.apps.addressbook.dao;


import java.util.List;

import com.seeyon.apps.addressbook.po.AddressBookTeam;
import com.seeyon.ctp.common.dao.CTPBaseHibernateDao;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;


public interface AddressBookTeamDao extends CTPBaseHibernateDao<AddressBookTeam> {

	/**
	 * 查出该用户创建的所有类别
	 * @param creatorId 用户ID
	 * @return 类别列表
	 */
	public List findTeamsByCreatorId(final Long creatorId) ;
	
	public AddressBookTeam getTeamById(Long teamId);
	
	/**
	 * 判断是否有相同的类别名称
	 */
	public boolean hasSameCategory(String name, Long createId);
	
	/**
	 * 判断是否有相同的个人组
	 */
	public boolean hasSameOwnTeam(String name, Long ownerId, Long accountId);
	
	/**
	 * 判断是否有相同的讨论组
	 */
	public boolean hasSameDiscussTeam(String name, Long ownerId, Long accountId);
	

	public List<AddressBookTeam> toTypeSafeList(List list);
    
	public List<V3xOrgTeam> toSafeList(List list);
}
