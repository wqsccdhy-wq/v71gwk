package com.seeyon.ctp.organization.services;

import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;

public interface OrganizationServices {

    /**
     * 增加单位
     * @param account 单位实体
     * @throws BusinessException
     */
    public void addAccount(V3xOrgAccount account) throws BusinessException;

    /**
     * 增加部门
     * @param dept 部门实体
     * @param parentId 父组织id
     * @throws BusinessException
     */
    public void addDepartment(V3xOrgDepartment dept, Long parentId) throws BusinessException;

    /**
     * 增加人员
     * @param member 人员实体
     * @throws BusinessException
     */
    public void addMember(V3xOrgMember member) throws BusinessException;

    /**
     * 增加岗位
     * @param post 岗位实体
     * @throws BusinessException
     */
    public void addPost(V3xOrgPost post) throws BusinessException;

    /**
     * 增加职务级别
     * @param level 职务级别实体
     * @throws BusinessException
     */
    public void addLevel(V3xOrgLevel level) throws BusinessException;

    /**
     * 根据单位id删除单位
     * @param accountId 单位id
     * @throws BusinessException
     */
    public void delAccount(Long accountId) throws BusinessException;

    /**
     * 根据部门id删除部门
     * @param deptId 部门id
     * @throws BusinessException
     */
    public void delDepartment(Long deptId) throws BusinessException;

    /**
     * 根据人员id删除人员
     * @param memberId 人员id
     * @throws BusinessException
     */
    public void delMember(Long memberId) throws BusinessException;

    /**
     * 根据岗位id删除岗位
     * @param postId 岗位id
     * @throws BusinessException
     */
    public void delPost(Long postId) throws BusinessException;

    /**
     * 根据职务id删除职务
     * @param levelId 职务id
     * @throws BusinessException
     */
    public void delLevel(Long levelId) throws BusinessException;

    /**
     * 更新单位实体
     * @param account
     * @throws BusinessException
     */
    public void updateAccount(V3xOrgAccount account) throws BusinessException;

    /**
     * 更新部门实体
     * @param dept 部门实体
     * @param parentId 父组织的id(部门或单位)
     * @throws BusinessException
     */
    public void updateDepartment(V3xOrgDepartment dept, Long parentId) throws BusinessException;

    /**
     * 更新部门实体
     * @param dept 部门实体
     * @throws BusinessException
     */
    public void updateDepartment(V3xOrgDepartment dept) throws BusinessException;

    /**
     * 更新人员
     * @param member 人员实体
     * @throws BusinessException
     */
    public void updateMember(V3xOrgMember member) throws BusinessException;

    /**
     * 更新岗位
     * @param post 岗位实体
     * @throws BusinessException
     */
    public void updatePost(V3xOrgPost post) throws BusinessException;

    /**
     * 更新职务级别
     * @param level 职务实体
     * @throws BusinessException
     */
    public void updateLevel(V3xOrgLevel level) throws BusinessException;

    /*
     * 增加无组织用户
     */
    public void addUnOrgMember(V3xOrgMember member) throws BusinessException;

    /*
     * 更新无组织用户
     */
    public void updateUnOrgMember(V3xOrgMember member) throws BusinessException;

    /**
     * 增加兼职岗位信息
     * @param currentPosts
     * @throws BusinessException
     */
    public void addUserCurrentPost(List<MemberPost> currentPosts) throws BusinessException;

    /**
     * 根据人员id删除某人的兼职
     * @param userId
     * @throws BusinessException
     */
    public void delUserCurrentPost(Long userId) throws BusinessException;

    /**
     * 删除全部兼职
     * @throws BusinessException
     */
    public void clearAllCurrentPosts() throws BusinessException;

    public OrgManager getOrgManager();

    public OrgManagerDirect getOrgManagerDirect();

    /**
     * 检查跨单位调动人员是否存在代办事项
     * 
     * @param memberId 人员
     * @return boolean
     * @throws BusinessException
     */
    public boolean modifyMemberAccountCheck(Long memberId) throws BusinessException;

    /**
     * 批量同步人员
     * @param members 人员列表
     * @param rollback 人员校验出错是否回滚
     * @param isNeedSecondPost 是否更新人员副岗
     * @param accountId 同步单位ID
     * @throws BusinessException
     */
    public Map<Long, String> synchMember(List<V3xOrgMember> members, boolean rollback, boolean isNeedSecondPost,
            Long accountId) throws Exception;

    /**
     * 跨单位调整部门
     * @param deptId 调整部门ID
     * @param accountId 调入单位ID
     * @throws BusinessException
     */
    public List<String[]> moveDept(Long deptId, Long accountId) throws BusinessException;

    /**
     * 跨单位调整人员所属部门
     * @param memberId 调整人员ID
     * @param deptId 调入部门ID
     * @throws BusinessException
     */
    public void moveMember(Long memberId, Long deptId) throws BusinessException;

    /**
     * 添加组
     * @param team
     * @throws BusinessException
     */
    public void addTeam(V3xOrgTeam team) throws BusinessException;

    /**
     * 更新组
     * @param team
     * @throws BusinessException
     */
    public void updateTeam(V3xOrgTeam team) throws BusinessException;

}
