package com.seeyon.ctp.organization.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.filemanager.V3XFile;
import com.seeyon.ctp.organization.inexportutil.ResultObject;
import com.seeyon.ctp.util.FlipInfo;

public interface MemberManager {

    /**
     * 按照单位展现
     * @param fi
     * @param params
     * @return
     * @throws BusinessException
     */
    FlipInfo showByAccount(final FlipInfo fi, final Map params) throws BusinessException;
    
    /**
     * 部门管理员管理的人员列表
     * @param fi
     * @param params
     * @return
     * @throws BusinessException
     */
    FlipInfo show4DeptAdmin(FlipInfo fi, final Map params) throws BusinessException;
    
    /**
     * 按照部门展现
     * @param fi
     * @param params
     * @return
     * @throws BusinessException
     */
    FlipInfo showByDepartment(final FlipInfo fi, final Map params) throws BusinessException;
    
    /**
     * 查看人员信息
     * @param memberId
     * @return
     * @throws BusinessException
     */
    HashMap viewOne(Long memberId) throws BusinessException;
    
    /**
     * 创建人员
     * @param map
     * @return
     * @throws BusinessException
     */
    Object createMember(String accountId, Map map) throws BusinessException;
    
    /**
     * 更新人员信息
     * @param map
     * @return
     * @throws BusinessException
     */
    Object updateMember(Map map) throws BusinessException;
    
    /**
     * 人员删除操作
     * @param ids
     * @return
     * @throws BusinessException
     */
    Object deleteMembers(Long[] ids) throws BusinessException;
    
    /**
     * 人员调出操作
     * @param ids
     * @return 
     * @throws BusinessException
     */
    Object cancelMember(Long[] ids) throws BusinessException;
    
    /**
     * 为人员调出判断部门主管或管理员ajax判断提供的manager方法
     * @param memberIds
     * @return 
     * @throws BusinessException
     */
    HashMap<String,String> checkMember4DeptRole(String memberIds) throws BusinessException;
    
    /**
     * 展现外部人员列表
     * @param fi
     * @param params
     * @return
     * @throws BusinessException
     */
    FlipInfo showExtMember(FlipInfo fi, Map params) throws BusinessException;
    
    /**
     * 保存一个外部人员
     * @param map
     * @throws BusinessException
     */
    Object createExtMember(String accountId, Map map) throws BusinessException;
    
    /**
     * 单位管理员查询所有单位角色，但要排除这个单位的单位管理员角色
     * @param fi
     * @param param
     * @return
     * @throws BusinessException
     */
    FlipInfo findRolesWithoutAdmin(FlipInfo fi, Map param) throws BusinessException;
    
    /**
     * 单位管理员新建外部人员时，显示的角色列表
     * 显示普通人员、外部人员、单位和集团自建角色
     * @param fi
     * @param param
     * @return
     * @throws BusinessException
     */
    FlipInfo findRoles4ExtMember(FlipInfo fi, Map param) throws BusinessException;
    
    /**
     * 用于如果人员部门有变化则清空原来部门的角色，只回归其其他角色
     * @param memberId
     * @return
     * @throws BusinessException
     */
    HashMap<String, String> noDeptRoles(Long memberId) throws BusinessException;
    
    /**
     * 获取当前单位可以删除的所有角色集合，仅用于删除这个人能被赋予的所有角色
     * @return
     * @throws BusinessException
     */
    public Set<Long> canDelRoles(Long accountId) throws BusinessException;
    
    /**
     * 显示该人员的所拥有的全部角色列表，仅用于查看不用做维护
     * @return
     * @throws BusinessException
     */
    FlipInfo showMemberAllRoles(FlipInfo fi, Map param) throws BusinessException;
    
    /**
     * 根据实体ID，判断人员所选的其他实体已经是否被授权了角色
     * @param entityIds
     * @return
     * @throws BusinessException
     */
    boolean checkNoRoles(String entityIds) throws BusinessException;
    /**
     * 上传zip压缩文件,返回结果列表
     * @param zipFileName 压缩文件名
     * @param v3xFile v3xfile
     * @param accountId 单位id
     * @param override 是否覆盖
     * @throws BusinessException
     */
    public List<ResultObject> uploadMemberPicAttachment(String zipFileName,V3XFile v3xFile,Long accountId,Boolean override) throws BusinessException;
}
