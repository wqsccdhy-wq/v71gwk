/**
 * $Id: AddressBookManager.java,v 1.16 2011/02/24 05:57:58 renhy Exp $
`* 
 * Licensed to the UFIDA
 */
package com.seeyon.apps.addressbook.manager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.apps.addressbook.po.AddressBookMember;
import com.seeyon.apps.addressbook.po.AddressBookSet;
import com.seeyon.apps.addressbook.po.AddressBookTeam;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.metadata.bo.MetadataColumnBO;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgAccount;

/**
 *
 * <p/> Title: 个人组/类别<外部接口>
 * </p>
 * <p/> Description: 个人组/类别<外部接口>
 * </p>
 * <p/> Date: 2007-5-25
 * </p>
 * @author paul(qdlake@gmail.com)
 */
public interface AddressBookManager {
	
	public final static int TYPE_DISCUSS = 4; //讨论组
	public final static int TYPE_EMAIL = 3; //外部联系人-邮件
	public final static int TYPE_CATEGORY = 2; //外部联系人-类别
	public final static int TYPE_OWNTEAM = 1; //个人组
	
	/**
	 * 添加外部联系人
	 * @param member 外部联系人
	 */
	public void addMember(AddressBookMember member);
	public void updateMember(AddressBookMember member);
	public AddressBookMember getMember(Long memberId);
	/**
	 * 该用户创建的所有外部联系人
	 * @param creatorId 用户ID
	 * @return 外部联系人列表
	 */
	public List<AddressBookMember> getMembersByCreatorId(Long creatorId);
	public List<AddressBookMember> getMembersByTeamId(Long teamId);
	public void removeCategoryMembersByIds(Long creatorId, List<Long> memberIds);
	public void removeMembersByIds(Long creatorId, List<Long> memberIds);
	/**
	 * 该用户创建的所有类别
	 * @param creatorId 用户ID
	 * @return 类别列表
	 */
	public List<AddressBookTeam> getTeamsByCreatorId(Long creatorId);
	
	/**
	 * 添加个人组/类别
	 * @param team
	 */
	public void addTeam(AddressBookTeam team);
	public AddressBookTeam getTeam(Long teamId);
	
	/**
	 * 修改类别
	 * @param team
	 */
	public void updateTeam(AddressBookTeam team);
	
	/**
	 * 删除类别，以及关联成员
	 * @param teamId 类别id
	 */
	public void removeTeamById(Long teamId);
	
	/**
	 * 按名称查找员工
	 * @param name 员工名称
	 * @return
	 */
	public List getOrgMemByName(String name);
	public List getMemberByName(String name); //外部联系人
	
	/**
	 * 按手机号码查找员工
	 * @param tel 员工手机号码
	 * @return
	 */
	public List getMemberByTel(String tel);
	
	/**
	 * 按职务级别查找员工
	 * @param levelName 职务级别
	 * @return
	 */
	public List getOrgMemberByLevelName(String levelName);
	public List getMemberByLevelName(String levelName);//外部联系人
	
	/**
	 * 根据类型（邮件、类别）判断是否存在
	 */
	public boolean isExist(int type, String name, Long createId, Long accountId, String memberId); //外部联系人
	
	public String doImport(File file,String categoryId, String memberId)throws Exception;

	public String doCsvImport(File file, String categoryId,
			String memberId) throws Exception;
    
    public boolean isExistSameUserName(AddressBookMember member,Long createrId);

    public AddressBookSet getAddressbookSetByAccountId(Long accountId);

    public void saveAddressbookSet(AddressBookSet bean, boolean isNew);

    public boolean checkLevelScope(V3xOrgMember user, V3xOrgMember member, Long accountId, AddressBookSet addressBookSet);

    public boolean checkLevelScope(V3xOrgMember user, V3xOrgMember member, Long accountId, AddressBookSet addressBookSet, Map<Long, List<V3xOrgDepartment>> deptsMap, Map<Long, Set<Long>> deptIdsMap);

    /**
     * 职务检测
     * @param userId 当前用户ID
     * @param memberId 要检测人员ID
     * @param accountId 根据哪个单位的设置检测
     * @return
     */
    public boolean checkLevel(Long userId, Long memberId, Long accountId);

    public boolean checkLevel(Long userId, Long memberId, Long accountId, AddressBookSet addressBookSet);

    /**
     * 手机号检测
     * @param userId 当前用户ID
     * @param memberId 要检测人员ID
     * @param accountId 根据哪个单位的设置检测
     * @return
     */
    public boolean checkPhone(Long userId, Long memberId, Long accountId);

    public boolean checkPhone(Long userId, Long memberId, Long accountId, AddressBookSet addressBookSet);

    /**
     * 组装部门人员数据
     * @param deptId
     * @param accountId
     * @param members
     * @return
     * @throws Exception
     */
    public List<V3xOrgMember> listDeptMembers(Long deptId, Long accountId, List<V3xOrgMember> members) throws Exception;
	List<MetadataColumnBO> getCustomerAddressBookList()
			throws BusinessException;
	List<MetadataColumnBO> getCurrentAccountEnableCustomerFields(AddressBookSet addressbookSet)
			throws BusinessException;
	Method getGetMethod(String fieldName);
	Method getSetMethod(String fieldName);
	/**
	 * 获取人员的关联人员信息
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws Exception
	 */
	HashMap getRelationInfoByMemberId(String memberId, String accountId) throws Exception;
	/**
	 * 单位树
	 * @return
	 * @throws BusinessException
	 */
	List<WebV3xOrgAccount> getAccountTree(Map params) throws BusinessException;
	/**
	 * 根据id查询联系人列表
	 * @param accountId
	 * @param searchContent
	 * @param key
	 * @return
	 * @throws Exception
	 */
	String getContactsByAccountId(String accountId, String searchContent,String key) throws Exception;
	/**
	 * 获取当前登录人员，部门下能看到的人员的总数
	 * @param deptId
	 * @param firtLayer  是否包含子部门 true:不包含 false: 包含
	 * @return
	 * @throws Exception
	 */
	int getDeptMemberSize(Long deptId, boolean firtLayer) throws Exception;
	/**
	 * 获取我能看到的业务线
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	String getBusinessJson(Long accountId) throws BusinessException;
	/**
	 * 获取单位/部门信息
	 * @param unitId
	 * @return
	 * @throws BusinessException
	 */
	Map getUnitInfo(Long unitId) throws BusinessException;

}