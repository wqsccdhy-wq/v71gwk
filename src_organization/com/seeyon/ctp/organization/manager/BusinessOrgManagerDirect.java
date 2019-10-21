package com.seeyon.ctp.organization.manager;

import java.util.List;
import java.util.Set;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRole;


public interface BusinessOrgManagerDirect {
    /**
     * 获取业务组织列表
     * @param createrId
     * @param enable
     * @return
     * @throws BusinessException
     */
	List<V3xOrgAccount> getAccountList(Long createrId, Boolean enable) throws BusinessException;
	
	/**
	 * 新建业务组织
	 * @param account
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addAccount(V3xOrgAccount account) throws BusinessException;

	/**
	 * 更新业务组织
	 * @param account
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updateAccount(V3xOrgAccount account) throws BusinessException;

	/**
	 * 删除业务组织
	 * @param account
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage deleteAccount(V3xOrgAccount account) throws BusinessException;

	/**
	 * 新建业务组织部门
	 * @param dept
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addDepartment(V3xOrgDepartment dept)throws BusinessException;

	/**
	 * 批量新建业务组织部门
	 * @param dept
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage addDepartments(List<V3xOrgDepartment> depts) throws BusinessException;

	/**
	 * 更新业务组织部门
	 * @param dept
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updateDepartment(V3xOrgDepartment dept) throws BusinessException;

	/**
	 * 批量更新业务组织部门
	 * @param depts
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage updateDepartments(List<V3xOrgDepartment> depts) throws BusinessException;

	/**
	 * 删除业务组织部门
	 * @param dept
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage deleteDepartment(V3xOrgDepartment dept) throws BusinessException;

	/**
	 * 批量删除业务组织部门
	 * @param depts
	 * @return
	 * @throws BusinessException
	 */
	OrganizationMessage deleteDepartments(List<V3xOrgDepartment> depts) throws BusinessException;

	/**
	 * 获取单位/部门下的自部门
	 * @param parentDepId
	 * @param firtLayer
	 * @param externalType
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgDepartment> getChildDepartments(Long parentDepId,boolean firtLayer) throws BusinessException;

	/**
	 * 获取部分下的人员
	 * @param deptId 部门id
	 * @param firtLayer  true只查询本部门  false查询所有子部门
	 * @return
	 * @throws BusinessException 
	 */
	List<V3xOrgMember> getMembersByDepartment(Long deptId, boolean firtLayer) throws BusinessException;

	/**
	 * 获取组织下所有人员
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgMember> getAllMembers(Long accountId) throws BusinessException;

	List<MemberPost> getMemberPostByDepartment(Long deptId, boolean firtLayer) throws BusinessException;

	/**
	 * 获取指定人员能看到的业务线下的所有人
	 * @param memberId
	 * @param businessId
	 * @return
	 * @throws BusinessException
	 */
	Set<Long> getAccessBusinessMember(Long memberId, Long businessId) throws BusinessException;

	/**
	 * 获取人员可见的单位下的业务线
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	List<V3xOrgAccount> getAccessBusiness(Long memberId, Long accountId) throws BusinessException;

	/**
	 * 根据编号获取角色
	 * @param code
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	V3xOrgRole getRoleByCode(String code, Long accountId) throws BusinessException;

	/**
	 *获取人员在指定业务线下 存在于哪些部门
	 * @param memberId  指定人员
	 * @param businessId 业务线id   null:获取所有所属的部门,不建议使用
	 * @param firtLayer 是否包含子部门  false: 包含； true:不包含
	 * @return
	 */
	List<V3xOrgDepartment> getBusinessDeptsByMemberId(Long memberId, Long businessId, boolean firtLayer) throws BusinessException;

	/**
	 *   指定人员是否可以访问指定的业务线
	 * @param memberId
	 * @param businessId
	 * @return
	 * @throws BusinessException
	 */
	boolean canAccess(Long memberId, Long businessId) throws BusinessException;
}