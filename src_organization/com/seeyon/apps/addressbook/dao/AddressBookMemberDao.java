
package com.seeyon.apps.addressbook.dao;

import java.util.List;

import com.seeyon.apps.addressbook.po.AddressBookMember;
import com.seeyon.ctp.common.dao.CTPBaseHibernateDao;


public interface AddressBookMemberDao extends CTPBaseHibernateDao<AddressBookMember> {


	/**
	 * 查出该用户创建的所有外部联系人
	 * 
	 * @param creatorId
	 *            用户ID
	 * @return 外部联系人列表
	 */
	public List findMembersByCreatorId(final Long creatorId);
	
	/**
	 * 删除外部联系人
	 * @param memberIds 外部联系人列表
	 */
	public void deleteMembersByIds(final List<Long> memberIds) ;
	
	public List findMembersByTeamId(final Long teamId);
	
	/**
	 * 按姓名查找员工
	 */
	public List findOrgMembersByName(final String name);
	
	/**
	 * 按姓名查找外部联系人
	 */
	public List findMemberByName(final String name);
	
	/**
	 * 按手机号码查找外部联系人
	 */
	public List findMemberByTel(final String tel);
	
	/**
	 * 按职务级别查找员工
	 */
	public List findOrgMemberByLevelName(final String levelName);
	
	/**
	 * 按职务级别查找外部联系人
	 */
	public List findMemberByLevelName(final String levelName);
	
	/**
	 * 判断是否有相同的邮件地址
	 */
//	public boolean hasSameMail(String mail, String memberId);
	
	public boolean hasSameMail(String mail, String memberId) ;
    /**
     * 按姓名和所属组查找外部联系人
     */
    public List findMemberByNameAndTeam(final String name,final Long categoryId,final Long createrId);
}
