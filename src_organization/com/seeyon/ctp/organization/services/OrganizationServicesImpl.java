package com.seeyon.ctp.organization.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceBundleUtil;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.OrganizationMessage.OrgMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.organization.principal.NoSuchPrincipalException;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;

public class OrganizationServicesImpl implements OrganizationServices {

    private static final Log log = LogFactory.getLog(OrganizationServicesImpl.class);
    private OrgManagerDirect orgManagerDirect;
    private OrgManager       orgManager;
    protected PrincipalManager principalManager;
    private OrgDao           orgDao;

    public OrgDao getOrgDao() {
        return orgDao;
    }

    public void setOrgDao(OrgDao orgDao) {
        this.orgDao = orgDao;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    
    public PrincipalManager getPrincipalManager() {
        return principalManager;
    }

    public void setPrincipalManager(PrincipalManager principalManager) {
        this.principalManager = principalManager;
    }

	/*******impl******/
    @Override
    public void addAccount(V3xOrgAccount account) throws BusinessException {
        // TODO Auto-generated method stub
        if (account.getCode() == null) {
            throw new BusinessException("添加单位出错:单位编号为空");
        } else if (account.getName() == null) {
            throw new BusinessException("添加单位出错:单位名称为空");
        } else if (account.getShortName() == null) {
            throw new BusinessException("添加单位出错:单位简称为空");
        } else {
            //单位重名的校验
            List<V3xOrgEntity> accountList0 = orgManager.getEntityList(V3xOrgAccount.class.getSimpleName(), "name", account.getName(),
                    V3xOrgEntity.VIRTUAL_ACCOUNT_ID,false,true);
            if (accountList0 != null && accountList0.size() != 0)
                throw new BusinessException("添加单位出错:已经存在相同名称的单位");
            //单位编码重复的校验
            List<V3xOrgEntity> accountList = orgManager.getEntityList(V3xOrgAccount.class.getSimpleName(), "code", account.getCode(),
                    V3xOrgEntity.VIRTUAL_ACCOUNT_ID,false,true);
            if (accountList != null && accountList.size() != 0)
                throw new BusinessException("添加单位出错:单位编码重复");

            //设置单位的排序号
            Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgAccount.class.getSimpleName(),
                    V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
            account.setSortId(maxSortNum.longValue() + 1L);
            //account.setAccessPermission(V3xOrgEntity.ACCOUNT_ACC_ALL);
            //account.setAdminName(account.getCode()+"-admin");
            //增加单位
            Long accountId = UUIDLong.longUUID();
            account.setId(accountId);
            account.setOrgAccountId(accountId);
            //增加管理员
            String adminNameValue = ResourceBundleUtil.getString(
            		"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
            		"org.account_form.adminName.value", "");            
            V3xOrgMember admin = new V3xOrgMember();
            admin.setId(UUIDLong.longUUID());
            admin.setName(adminNameValue);
            admin.setType(OrgConstants.MEMBER_TYPE.FORMAL.ordinal());
            admin.setIsAdmin(true);
            admin.setOrgAccountId(account.getId());
            admin.setV3xOrgPrincipal(new V3xOrgPrincipal(admin.getId(), account.getCode()+"-admin",orgManager.getInitPWD()));
            orgManagerDirect.addAccount(account,admin);
        }

    }

    @Override
    public void addDepartment(V3xOrgDepartment dept, Long parentId) throws BusinessException {
        // TODO Auto-generated method stub
        if (dept.getCode() == null) {
            throw new BusinessException("添加部门出错:部门编码为空");
        } else if (dept.getName() == null) {
            throw new BusinessException("添加部门出错:部门名称为空");
        } else if (orgManager.getAccountById(dept.getOrgAccountId()) == null) {
            throw new BusinessException("添加部门出错:部门所在单位为空");
        } else if (parentId == null) {
            throw new BusinessException("添加部门出错:上级组织id为空");
        } else {
            //校验父部门
            V3xOrgDepartment parentDept = orgManager.getDepartmentById(parentId);
            V3xOrgAccount account = null;
            if (parentDept == null) {
                account = orgManager.getAccountById(parentId);
                if (account == null) {
                    throw new BusinessException("添加部门出错:上级组织为空");
                }
            }
            //同级部门重名的校验
            List<V3xOrgDepartment> depts = orgManager.getChildDepartments(parentId, true);
            for (V3xOrgDepartment orgDept : depts) {
                if (orgDept.getName().equals(dept.getName())) {
                    throw new BusinessException("添加部门出错:同一级上已经存在相同名称的部门" + ":" + dept.getName()
                            + (parentDept == null ? account.getName() : parentDept.getName()));
                }
            }
            //获得最大排序号
            Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgDepartment.class.getSimpleName(),
                    dept.getOrgAccountId());
            dept.setSortId(maxSortNum.longValue() + 1L);
            dept.setSuperior(parentId);
            //设置部门路径
            //	          String fPath = V3xOrgEntity.ORGACCOUNT_PATH;
            //	          if (!parentId.equals(dept.getOrgAccountId())) {
            //	              V3xOrgDepartment pDep = orgManagerDirect.getDepartmentById(parentId);
            //	              if (pDep == null)
            //	                  throw new BusinessException("父部门的ID错误");
            //	              fPath = pDep.getPath();
            //	          }
            //	          dept.setPath(OrgHelper.getDepartmentPath(fPath,orgManagerDirect.getChildDepartments(parentId, true)));      
            OrganizationMessage message = orgManagerDirect.addDepartment(dept);
            OrgHelper.throwBusinessExceptionTools(message);
        }

    }

    @Override
    public void addMember(V3xOrgMember member) throws BusinessException {
        // TODO Auto-generated method stub
        if (member.getCode() == null) {
            throw new BusinessException("添加人员出错:人员编号为空");
        } else if (member.getName() == null) {
            throw new BusinessException("添加人员出错:人员名称为空");
        } else if (orgManager.getAccountById(member.getOrgAccountId()) == null) {
            throw new BusinessException("添加人员出错:人员所属单位为空或不存在");
        } else {
            //人员密码为空时设置默认密码
            //            if(StringUtils.isEmpty(member.getPassword()))
            //                member.setPassword.DEFAULT_PASSWORD);
            //人员帐号重复验证
            V3xOrgMember orgMem = orgManager.getMemberByLoginName(member.getLoginName());
            if (orgMem != null)
                throw new BusinessException("添加人员出错:人员登录名重复");
            V3xOrgDepartment dept = orgManager.getDepartmentById(member.getOrgDepartmentId());
            V3xOrgPost post = orgManager.getPostById(member.getOrgPostId());
            V3xOrgLevel level = orgManager.getLevelById(member.getOrgLevelId());
            if (dept == null) {
                member.setEnabled(false);
                member.setOrgDepartmentId(V3xOrgEntity.DEFAULT_NULL_ID);
                log.info("set member " + member.getName() + " unable for null department");
            }
            if (post == null) {
                member.setEnabled(false);
                member.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
                log.info("set member " + member.getName() + " unable for null post");
            }
            if (level == null) {
                member.setEnabled(false);
                member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
                log.info("set member " + member.getName() + " unable for null level");
            }
            //获得当前排序号的最大值
            Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgMember.class.getSimpleName(),
                    member.getOrgAccountId());
            member.setSortId(maxSortNum.longValue() + 1L);
            OrganizationMessage message = orgManagerDirect.addMember(member);
            OrgHelper.throwBusinessExceptionTools(message);

        }

    }

    @Override
    public void addPost(V3xOrgPost post) throws BusinessException {
        if (post.getCode() == null) {
            throw new BusinessException("添加岗位出错:岗位编号为空");
        } else if (post.getName() == null) {
            throw new BusinessException("添加岗位出错:岗位名称为空");
        } else if (orgManager.getAccountById(post.getOrgAccountId()) == null) {
            throw new BusinessException("添加岗位出错:岗位所属单位为空或不存在");
        } else {
            //获得当前排序号的最大值
            Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgPost.class.getSimpleName(),
                    post.getOrgAccountId());
            post.setSortId(maxSortNum.longValue() + 1);
            orgManagerDirect.addPost(post);
        }

    }

    @Override
    public void addLevel(V3xOrgLevel level) throws BusinessException {
        if(null == level) {
            throw new BusinessException("添加职级出错:数据为空");
        } else if (level.getCode() == null) {
            throw new BusinessException("添加职级出错:职级编号为空");
        } else if (level.getName() == null) {
            throw new BusinessException("添加职级出错:职级名称为空");
        } else if (null == orgManager.getAccountById(level.getOrgAccountId())) {
            throw new BusinessException("添加职级出错:职级所属单位为空或不存在");
        } else {
            //职务级别不存在序号见缝插针
            //获得当前排序号的最大值
            Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgLevel.class.getSimpleName(),
                    level.getOrgAccountId());
            if (null == level.getLevelId()) {
                level.setLevelId(maxSortNum.intValue() + 1);
//                int i = 1;
//                List<V3xOrgLevel> listLevel = orgManagerDirect.getAllLevels(level.getOrgAccountId(), false);
//                if (null == listLevel || listLevel.isEmpty()) {
//                    level.setLevelId(1);
//                }
//                for (V3xOrgLevel level2 : listLevel) {
//                    if (level2.getLevelId() == i) {
//                        i++;
//                        continue;
//                    }
//                    if (level2.getLevelId() != i) {
//                        level.setLevelId(i);
//                        break;
//                    }
//                }
//                if (level.getLevelId() == null) {
//                    level.setLevelId(i + 1);
//                }
            }
            level.setSortId(maxSortNum.longValue() + 1);
            OrganizationMessage m = orgManagerDirect.addLevel(level);
            OrgHelper.throwBusinessExceptionTools(m);
        }

    }

    @Override
    public void delAccount(Long accountId) throws BusinessException {
        V3xOrgAccount account = orgManager.getAccountById(accountId);
        OrganizationMessage m = orgManagerDirect.deleteAccount(account);
        OrgHelper.throwBusinessExceptionTools(m);
    }

    @Override
    public void delDepartment(Long deptId) throws BusinessException {
        V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
        OrganizationMessage m = orgManagerDirect.deleteDepartment(dept);
        OrgHelper.throwBusinessExceptionTools(m);
    }

    @Override
    public void delMember(Long memberId) throws BusinessException {
        V3xOrgMember member = orgManager.getMemberById(memberId);
        OrganizationMessage m = orgManagerDirect.deleteMember(member);
        OrgHelper.throwBusinessExceptionTools(m);
    }

    @Override
    public void delPost(Long postId) throws BusinessException {
        V3xOrgPost post = orgManager.getPostById(postId);
        OrganizationMessage m = orgManagerDirect.deletePost(post);
        OrgHelper.throwBusinessExceptionTools(m);
    }

    @Override
    public void delLevel(Long levelId) throws BusinessException {
        V3xOrgLevel level = orgManager.getLevelById(levelId);
        OrganizationMessage m = orgManagerDirect.deleteLevel(level);
        OrgHelper.throwBusinessExceptionTools(m);
    }

    @Override
    public void updateAccount(V3xOrgAccount account) throws BusinessException {
        if (account.getCode() == null) {
            throw new BusinessException("修改单位出错:单位编号为空");
        } else if (account.getName() == null) {
            throw new BusinessException("修改单位出错:单位名称为空");
        } else if (account.getShortName() == null) {
            throw new BusinessException("修改单位出错:单位简称为空");
        } else {
            OrganizationMessage m = orgManagerDirect.updateAccount(account);
            OrgHelper.throwBusinessExceptionTools(m);
        }
    }

    @Override
    public void updateDepartment(V3xOrgDepartment dept, Long parentId) throws BusinessException {
        if (dept.getCode() == null) {
            throw new BusinessException("修改部门出错:部门编码为空");
        } else if (dept.getName() == null) {
            throw new BusinessException("修改部门出错:部门名称为空");
        } else if (orgManager.getAccountById(dept.getOrgAccountId()) == null) {
            throw new BusinessException("修改部门出错:部门所在单位为空");
        } else if (parentId == null) {
            throw new BusinessException("修改部门出错:上级组织id为空");
        } else {
            //校验平级部门名称不能相同
            List<V3xOrgDepartment> brother = this.orgManager.getChildDepartments(parentId, true);
            for (V3xOrgDepartment broDept : brother) {
                if (!Strings.equals(broDept.getId(), dept.getId()) && dept.getName().equals(broDept.getName())) {
                    throw new BusinessException("修改部门出错: 同级部门名称重复，操作失败！");
                }
            }
            //校验父部门
            V3xOrgDepartment parentDept = orgManager.getDepartmentById(parentId);
            if (parentDept == null) {
                V3xOrgAccount account = orgManager.getAccountById(parentId);
                if (account == null) {
                    throw new BusinessException("修改部门出错:上级组织为空");
                }
            }
            dept.setSuperior(parentId);
            OrganizationMessage message = orgManagerDirect.updateDepartment(dept);
            OrgHelper.throwBusinessExceptionTools(message);
        }
    }

    @Override
    public void updateDepartment(V3xOrgDepartment dept) throws BusinessException {
        OrganizationMessage m = orgManagerDirect.updateDepartment(dept);
        OrgHelper.throwBusinessExceptionTools(m);
    }

    @Override
    public void updateMember(V3xOrgMember member) throws BusinessException {
        if(member.getCode()==null){
            throw new BusinessException("修改人员出错:人员编号为空");
        }else if(member.getName()==null){
            throw new BusinessException("修改人员出错:人员名称为空");
        }else if(orgManager.getAccountById(member.getOrgAccountId())==null){
            throw new BusinessException("修改人员出错:人员所属单位为空或不存在");
        }else{
            OrganizationMessage m = orgManagerDirect.updateMember(member);
            OrgHelper.throwBusinessExceptionTools(m);
        }
    }

    @Override
    public void updatePost(V3xOrgPost post) throws BusinessException {
        OrganizationMessage m = orgManagerDirect.updatePost(post);
        OrgHelper.throwBusinessExceptionTools(m);
    }

    @Override
    public void updateLevel(V3xOrgLevel level) throws BusinessException {
        OrganizationMessage m = orgManagerDirect.updateLevel(level);
        OrgHelper.throwBusinessExceptionTools(m);
    }

    @Override
    public void addUnOrgMember(V3xOrgMember member) throws BusinessException {
        orgManagerDirect.addUnOrganiseMember(member);
    }

    @Override
    public void updateUnOrgMember(V3xOrgMember member) throws BusinessException {
        orgManagerDirect.updateUnOrganiseMember(member);
    }

    @Override
    public void addUserCurrentPost(List<MemberPost> currentPosts) throws BusinessException {
        if(currentPosts!=null){
            Long zero = Long.parseLong("0000");
            for(MemberPost cntPost:currentPosts){
                V3xOrgRelationship rel = new V3xOrgRelationship();
                rel = cntPost.toRelationship();
                rel.setSortId(zero++);
                orgManagerDirect.addOrgRelationship(rel);
            }           
        }
    }

    @Override
    public void delUserCurrentPost(Long userId) throws BusinessException {
        // TODO Auto-generated method stub
        orgManager.getConcurentPostsByMemberId(null, userId);
        orgManagerDirect.deleteConcurrentPost(null);
    }

    @Override
    public void clearAllCurrentPosts() throws BusinessException {
        orgManager.clearAllCurrentPosts(null);
    }

    @Override
    public OrgManager getOrgManager() {
        return orgManager;
    }

    public OrgManagerDirect getOrgManagerDirect() {
        return orgManagerDirect;
    }

    @Override
    public boolean modifyMemberAccountCheck(Long memberId) throws BusinessException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<Long, String> synchMember(List<V3xOrgMember> members, boolean rollback, boolean isNeedSecondPost,
            Long accountId) throws Exception {
        return new MemberSyncher(rollback, isNeedSecondPost,accountId).synchMember(members);
    }

    @Override
    public List<String[]> moveDept(Long deptId, Long accountId) throws BusinessException {
        List<String[]> moveLogListStr = new ArrayList<String[]>();
//        V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
//        V3xOrgAccount account = orgManager.getAccountById(accountId); 
//        Long oldAccountId= dept.getOrgAccountId();
//        List<V3xOrgMember> deptMems = orgManager.getMembersByDepartment(deptId, false);
//        //List<V3xOrgMember> deptMems = orgManager.getMembersByDepartment(deptId, false, null,oldAccountId);
//        //取所有用户所有的兼职单位
//        Set<Long> accountIdSet = new HashSet<Long>();
//        accountIdSet.add(oldAccountId);
//        for(V3xOrgMember deptMem:deptMems){
//            Map<Long, List<MemberPost>> accountList = orgManager.getConcurentPostsByMemberId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID
//                    ,deptMem.getId());
//            for(List<MemberPost> accountPostList:accountList.values())
//                for(MemberPost concurrent:accountPostList)
//                    accountIdSet.add(concurrent.getOrgAccountId());
//        }
//        if(dept!=null&&account!=null){          
//            log.info(Datetimes.formatDate(new Date())+"开始移动部门:"+dept.getName());
//            V3xOrgAccount orgAccount = orgManager.getAccountById(dept.getOrgAccountId());
//            //删除部门岗位关系
//            orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_DEP_POST,"sourceId",dept.getId());
//            //删除外部人员访问权限
//            orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_EXTERNAL_SCOPE,"objectiveId",dept.getId());
//            //删除部门下的兼职
//            List<V3xOrgRelationship> mp = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_POST,"objectiveId",dept.getId());
//            List<V3xOrgRelationship> deptMp = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"objectiveId",dept.getId());
//            List<V3xOrgRelationship> accountMp = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"orgAccountId",orgAccount.getId());
//            
//            
//            
//            mp.addAll(deptMp);
//            accountMp.removeAll(deptMp);
//            UniqueList<Long> memberIds = new UniqueList<Long>();
//            UniqueList<Long> cntMemberIds = new UniqueList<Long>();
//            for(V3xOrgRelationship rel:mp){
//                memberIds.add(rel.getSourceId());
//            }
//            orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_POST,"objectiveId",dept.getId());
//            orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"objectiveId",dept.getId());
//            if(memberIds.size()>0){
//                orgManager.deleteRelsInList(memberIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE);
//            }
//            cntMemberIds.addAll(memberIds);
            
            //重新设置部门角色关系
//          List<V3xOrgRelationship> deptRoleRels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_DEP_ROLE,"sourceId",dept.getId());
//            List<V3xOrgRelationship> memberDeptRoleRels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,"objectiveId",dept.getId());
//            List<V3xOrgRole> deptRoles= orgManagerDirect.getDepartmentRolesByAccount(accountId);
//            List<V3xOrgRole> oldDeptRoles= orgManagerDirect.getDepartmentRolesByAccount(oldAccountId);
//            if(deptRoles!=null){
//                for(V3xOrgRole role:deptRoles)
//                    dept.addDepRole(role.getId());
//            }
//            //删除部门下的人员部门角色关系
//            orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,"objectiveId",dept.getId(),"orgAccountId",oldAccountId);
//            log.info(Datetimes.formatDate(new Date())+"更新部门关系完成");
//            // 修改部门人员个人组的所属单位
//            List<V3xOrgTeam> allTeams = orgManager.getAllTeams(dept.getOrgAccountId());
//            for (V3xOrgTeam team : allTeams) {
//                V3xOrgMember owner = orgManager.getMemberById(team.getOwnerId());
//                if(owner!=null){
//                    if(deptId.equals(owner.getOrgDepartmentId())){
//                        // 部门人员建立的组
//                        team.setDepId(deptId);
//                        team.setOrgAccountId(accountId);
//                        orgManagerDirect.updateTeam(team); 
//                        List<V3xOrgRelationship> rels = orgManager.getRelationships("sourceId",team.getId());
//                        for (V3xOrgRelationship rel : rels) {
//                            // 从原单位移除
//                            //orgManager.deleteEntity(rel);
//                            // 添加到新单位
//                            //AEIGHT-5577 lilong 没有必要先删除再新增，这个Team_Leader或者Team_Member的关系直接更新单位id即可
//                            rel.setOrgAccountId(accountId); 
//                            orgManager.updateEntity(rel);
//                        }
//                    }
//                }
//            }               
//            //dept.setPosts(new ArrayList<Long>());
////          deptMems = orgManagerDirect.getMembersByDepartment(deptId, false, null,dept.getOrgAccountId());
//            log.info("部门下的人员："+deptMems.size());
//            
//            //删除调整部门下人员的所有组织关联信息(组信息除外，批量删除以200为基数）
//            List<Long> memIds = new ArrayList<Long>();
////          UniqueList<Long> memPostId = new UniqueList<Long>();
//            if(deptMems!=null){
//                int count = 0;
//                for(V3xOrgMember deptMem : deptMems){
//                    if(count>=200){                     
//                        //删除单位角色人员
//                        orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE);
//                        //删除副岗
//                        orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_POST);
//                        //删除兼职
//                        //orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST);
//                        memIds = new ArrayList<Long>();
//                        count = 0;
//                    }
//                    count++;
//                    memIds.add(deptMem.getId());
//                    //删除人员在调入单位的部门角色关系  by wusb at 2010-11-6
//                    orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,"sourceId",deptMem.getId(),"orgAccountId",accountId);
////                  orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"sourceId",deptMem.getId(),"orgAccountId",accountId);
//                }
//                if(memIds!=null&&memIds.size()>0){
//                    //删除单位角色人员
//                    orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE);
//                    //删除副岗
//                    orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_POST);
//                    //删除兼职
//                    //orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST);    
//                }
//            }
//            log.info(Datetimes.formatDate(new Date())+"更新部门人员关系完成");
//            //删除部门与组之间的关系
//            List<V3xOrgEntity> deptTeams = orgManagerDirect.getEntityListNoRelation(V3xOrgTeam.class.getSimpleName(), "depId", deptId, dept.getOrgAccountId());
//            if(deptTeams!=null&&deptTeams.size()>0){
//                for(V3xOrgEntity deptTeam : deptTeams){
//                    ((V3xOrgTeam)deptTeam).setDepId(dept.getOrgAccountId());
//                }
//            }
//            //重新设置部门岗位关系
//            
//            //删除子部门与组之间的关系          
//            List<V3xOrgDepartment> childDepts = orgManagerDirect.getChildDepartments(deptId, false);
//            if(childDepts!=null&&childDepts.size()>0){
//                for(V3xOrgDepartment childDept:childDepts){
//                    List<V3xOrgEntity> childDeptTeams = orgManagerDirect.getEntityListNoRelation(V3xOrgTeam.class.getSimpleName(), "depId", childDept.getId(), dept.getOrgAccountId());
//                    if(childDeptTeams!=null&&childDeptTeams.size()>0){
//                        for(V3xOrgEntity childDeptTeam : childDeptTeams){
//                            ((V3xOrgTeam)childDeptTeam).setDepId(dept.getOrgAccountId());
//                        }
//                    }                       
//                }
//            }
//            //移动部门
//            log.info(Datetimes.formatDate(new Date())+"开始设置部门路径");
//            orgManagerDirect.setDepPath(dept, accountId);
//            dept.setOrgAccountId(accountId);
//            log.info(Datetimes.formatDate(new Date())+"开始持久化部门角色");
//            orgManager.updateEntity(dept);
//            log.info(Datetimes.formatDate(new Date())+"子部门的个数："+childDepts.size());
//            List<Long> allDeptIds = new ArrayList<Long>();
//            allDeptIds.add(deptId);
//            if(childDepts!=null&&childDepts.size()>0){
//                for(V3xOrgDepartment childDept:childDepts){
//                    allDeptIds.add(childDept.getId());
//                    //删除部门岗位关系
//                    orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_DEP_POST,"sourceId",childDept.getId());
//                    //删除外部人员访问权限
//                    orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_EXTERNAL_SCOPE,"objectiveId",childDept.getId());
//                    //删除部门下的兼职
//                    List<V3xOrgRelationship> mpc = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_POST,"objectiveId",childDept.getId());
//                    List<V3xOrgRelationship> cntMpc = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"objectiveId",childDept.getId());
//                    mpc.addAll(cntMpc);
//                    accountMp.removeAll(cntMpc);                    
//                    List<Long> memberIdsC = new ArrayList<Long>();
//                    for(V3xOrgRelationship rel:mpc){
//                        memberIdsC.add(rel.getSourceId());
//                    }
//                    orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_POST,"objectiveId",childDept.getId());
//                    orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"objectiveId",childDept.getId());
//                    if(memberIdsC.size()>0){
//                        orgManager.deleteRelsInList(memberIdsC, V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE);
//                    }   
//                    cntMemberIds.addAll(memberIdsC);
//                    //重新设置部门角色关系
////                  List<V3xOrgRelationship> childDeptRoleRels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_DEP_ROLE,"sourceId",childDept.getId());
//                    List<V3xOrgRelationship> memberDeptRoleRelsChild = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,"objectiveId",childDept.getId());
////                  orgManager.updateEntitys(childDeptRoleRelEnts);
//                    deptRoles = orgManager.getDepartmentRolesByAccount(accountId);
//                    if(deptRoles!=null){
//                        for(V3xOrgRole role:deptRoles)
//                            childDept.addDepRole(role.getId());
//                    }
//                    childDept.setPosts(new ArrayList<Long>());
//                    childDept.setOrgAccountId(accountId);
//                    orgManager.updateEntity(childDept);
//                    
//                    //创建人员部门角色关系  wusb
//                    for(V3xOrgRelationship memberDepRole:memberDeptRoleRelsChild){
//                        if(!isDepLeader(memberDepRole.getBackupId(),oldDeptRoles)){
//                            V3xOrgRelationship rel = new V3xOrgRelationship();
//                            rel.setSourceId(memberDepRole.getSourceId());
//                            rel.setObjectiveId(childDept.getId());
//                            //原单位角色
//                            V3xOrgRole role = orgManager.getRoleById(memberDepRole.getBackupId());
//                            //新单位角色
//                            V3xOrgRole relRole = orgManager.getRoleByName(role.getName(),accountId);
//                            rel.setBackupId(relRole.getId());
//                            rel.setType(V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE);
//                            rel.setOrgAccountId(accountId);
//                            orgManager.updateEntity(rel);
//                        }
//                    }
//                    
//                    //删除子部门人员部门角色关系
//                    orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,"objectiveId",dept.getId(),"orgAccountId",oldAccountId);
//                    
//                }
//            }
//            log.info(Datetimes.formatDate(new Date())+"删除子部门关系完成");
//            //移动人员
//            if(deptMems!=null){     
//                //部门岗位关系
//                UniqueList<String> deptPost = new UniqueList<String>();
//                List<Long> memberIdList = new ArrayList<Long>();
//                for(V3xOrgMember deptMem : deptMems){
//                    deptMem.setOrgAccountId(accountId);
//                    memberIdList.add(deptMem.getId());
//                    //设置岗位
//                    V3xOrgPost memPost = (V3xOrgPost)orgAccount.getEntity(V3xOrgPost.class, deptMem.getOrgPostId());
//                    if(memPost!=null){
//                        List<V3xOrgEntity> accountPost = orgManagerDirect.getEntityListNoRelation(V3xOrgPost.class.getSimpleName(), "name", memPost.getName(), accountId);
//                        if(accountPost!=null&&accountPost.size()>0){
//                            V3xOrgPost post = (V3xOrgPost)accountPost.get(0);
//                            //如果岗位停用则启用岗位
//                            if(!post.getEnabled()){
//                                post.setEnabled(true);
//                                orgManagerDirect.updatePost(post);
//                            }
//                            deptMem.setOrgPostId(post.getId());
//                            //更新部门岗位关系
//                            deptPost.add(deptMem.getOrgDepartmentId()+"&"+post.getId());
//                        }else{
//                            //如果新单位不存在同名岗位则创建岗位
//                            V3xOrgPost newPost = new V3xOrgPost();
//                            newPost.setTypeId(memPost.getTypeId());
//                            newPost.setName(memPost.getName());
//                            newPost.setSortId(V3xOrgEntity.SORT_STEP_NUMBER);
//                            newPost.setOrgAccountId(accountId);
//                            orgManager.updatePost(newPost);
//                            //记录日志
//                            String[] addPostStr = new String[3];
//                            addPostStr[0] = "1";
//                            addPostStr[1] = memPost.getName();
//                            moveLogListStr.add(addPostStr);
//                            //如果是集团基准岗，需要创建关系
//                            List<V3xOrgRelationship> rels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_BENCHMARK_POST,"sourceId",memPost.getId());
//                            if(rels!=null&&rels.size()>0){
//                                //创建基准岗关系
//                                V3xOrgRelationship rel = new V3xOrgRelationship();
//                                rel.setType(V3xOrgEntity.ORGREL_TYPE_BENCHMARK_POST);
//                                rel.setOrgAccountId(accountId);
//                                rel.setSourceId(newPost.getId());
//                                V3xOrgRelationship bmRel = (V3xOrgRelationship)rels.get(0);
//                                rel.setObjectiveId(bmRel.getObjectiveId());
//                                orgManagerDirect.addOrgRelationship(rel);
//                            }
//                            deptMem.setOrgPostId(newPost.getId());
//                            deptPost.add(deptMem.getOrgDepartmentId()+"&"+newPost.getId());
//                        }
//                    }else{
//                        deptMem.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
//                    }
//                    //设置职务级别
//                    V3xOrgLevel memLevel = (V3xOrgLevel)orgAccount.getEntity(V3xOrgLevel.class, deptMem.getOrgLevelId());
//                    if(memLevel!=null){
//                        List<V3xOrgEntity> accountLevel = orgManagerDirect.getEntityListNoRelation(V3xOrgLevel.class.getSimpleName(), "name", memLevel.getName(), accountId);
//                        if(accountLevel!=null&&accountLevel.size()>0){
//                            deptMem.setOrgLevelId(accountLevel.get(0).getId());
//                        }else{
//                            deptMem.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
//                        }
//                    }else{
//                        deptMem.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
//                    }
//                    //清除人员副岗
//                    deptMem.setSecond_post(new ArrayList<MemberPost>());
//                    orgManager.updateEntity(deptMem);
//                    
//                    //清除部门人员在调入单位的兼职
//                    orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"sourceId",deptMem.getId(),"orgAccountId",accountId);
//                    log.info(Datetimes.formatDate(new Date())+"完成移动人员:"+deptMem.getName());
//                }
//                //创建部门岗位关系
//                for(String depP:deptPost){
//                    String[] deptP = depP.split("&");
//                    V3xOrgRelationship rel = new V3xOrgRelationship();
//                    rel.setSourceId(Long.parseLong(deptP[0]));
//                    rel.setObjectiveId(Long.parseLong(deptP[1]));
//                    rel.setType(V3xOrgEntity.ORGREL_TYPE_DEP_POST);
//                    rel.setOrgAccountId(accountId);
//                    orgManager.updateEntity(rel);
//                }
//                
//                //创建人员部门角色关系 wusb 2010-09-01
//                for(V3xOrgRelationship memberDepRole:memberDeptRoleRels){
//                    if(!isDepLeader(memberDepRole.getBackupId(),oldDeptRoles)){
//                        V3xOrgRelationship rel = new V3xOrgRelationship();
//                        rel.setSourceId(memberDepRole.getSourceId());
//                        rel.setObjectiveId(dept.getId());
//                        //原部门角色
//                        V3xOrgRole role = orgManager.getRoleById(memberDepRole.getBackupId());
//                        //新部门角色
//                        V3xOrgRole relRole = orgManager.getRoleByName(role.getName(),accountId);
//                        rel.setBackupId(relRole.getId());
//                        rel.setType(V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE);
//                        rel.setOrgAccountId(accountId);
//                        orgManager.updateEntity(rel);
//                    }
//                }
//                
//                /**
//                 * 删除 
//                 * 单位文档库管理员
//                 * 公文档案库管理员
//                 * 原单位公共信息管理员
//                 * 综合办公各权限管理员
//                 * 删除 工作管理设置人员 
//                 * 印章
//                 * add by wusb 2010-09-01 
//                 */  
////                if(!memberIdList.isEmpty()){
////                    try {
////                        pushNewOrgEntityTemplete4Member(memberIdList);
////                        deleteDocLibManager(memberIdList,accountIdSet);
////                        deletePublicManager(memberIdList,oldAccountId);
////                        deleteWorkManager(memberIdList,allDeptIds,oldAccountId);
////                        deleteOfficeManager(memberIdList,allDeptIds,oldAccountId);
////                        deleteSignetManager(memberIdList,oldAccountId);
////                    } catch (Exception e) {
////                        log.error(e);
//////                      throw new BusinessException(e);
////                    }
////                }
//                
//            }
//            //如果部门下的人员在其他部门不存在兼职，则删除此人员的单位角色
//            for(V3xOrgRelationship rel:accountMp){
//                if(cntMemberIds.contains(rel.getSourceId())){
//                    cntMemberIds.remove(rel.getSourceId());
//                }
//            }
//            if(cntMemberIds.size()>0){
//                orgManagerDirect.deleteRelsInList(cntMemberIds, OrgConstants.RelationshipType.Member_Role.name());
//            }
//        }
//        log.info(Datetimes.formatDate(new Date())+"完成移动部门");
        return moveLogListStr;
    }
    
    //把个人所拥有的模板授权给他  wusb 2010-09-16
//    private void pushNewOrgEntityTemplete4Member(List<Long> memberIds) throws Exception {
//        for(Long memberId:memberIds){
//             templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER,memberId,memberId);
//        }
//    }

    @Override
    public void moveMember(Long memberId, Long deptId) throws BusinessException {
        // TODO Auto-generated method stub
        V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
        V3xOrgMember member = orgManager.getMemberById(memberId);
        if (dept != null && member != null) {
            //是否是本单位移动人员
            if (!member.getOrgAccountId().equals(dept.getOrgAccountId())) {
                //V3xOrgAccount orgAccount = orgManager.getAccountById(member.getOrgAccountId());
                //删除人员的副岗
                member.setSecond_post(new ArrayList<MemberPost>());

                List<Long> sourceIds = new ArrayList<Long>(1);
                sourceIds.add(memberId);

                //删除人员角色关系
                orgManagerDirect.deleteRelsInList(sourceIds, OrgConstants.RelationshipType.Member_Role.name());
                //删除部门角色人员
                //orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE, "sourceId", memberId);
                //删除单位角色人员
                //orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE, "sourceId", memberId);

                //删除人员岗位关系，这样同时也会删除主岗关系，需要后面创建人员时再创建这个关系
                //修改主岗方法 steven
                orgManagerDirect.deleteRelsInList(sourceIds, OrgConstants.RelationshipType.Member_Post.name());
                //删除副岗
                //orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_MEMBER_POST, "sourceId", memberId);
                //删除兼职
                //orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST, "sourceId", memberId);
                //设置岗位
                V3xOrgPost memPost = orgManager.getPostById(member.getOrgPostId());
                //V3xOrgPost memPost = (V3xOrgPost)orgAccount.getEntity(V3xOrgPost.class, member.getOrgPostId());
                if (memPost != null) {
                    List<V3xOrgEntity> accountPost = orgManager.getEntityListNoRelation(
                            V3xOrgPost.class.getSimpleName(), "name", memPost.getName(), dept.getOrgAccountId());
                    if (accountPost != null && accountPost.size() > 0) {
                        V3xOrgPost post = (V3xOrgPost) accountPost.get(0);
                        //如果岗位停用则启用岗位
                        if (!post.getEnabled()) {
                            post.setEnabled(true);
                            orgManagerDirect.updatePost(post);
                        }
                        member.setOrgPostId(post.getId());
                    } else {
                        //如果新单位不存在同名岗位则创建岗位
                        V3xOrgPost newPost = new V3xOrgPost();
                        newPost.setTypeId(memPost.getTypeId());
                        newPost.setName(memPost.getName());
                        newPost.setSortId(V3xOrgEntity.SORT_STEP_NUMBER);
                        newPost.setOrgAccountId(dept.getOrgAccountId());
                        orgManagerDirect.addPost(newPost);
                        member.setOrgPostId(newPost.getId());
                    }
                } else {
                    member.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
                }

                //设置职务级别
                V3xOrgLevel memLevel = orgManager.getLevelById(member.getOrgLevelId());
                //V3xOrgLevel memLevel = (V3xOrgLevel)orgAccount.getEntity(V3xOrgLevel.class, member.getOrgLevelId());
                if (memLevel != null) {
                    List<V3xOrgEntity> accountLevel = orgManager.getEntityListNoRelation(
                            V3xOrgLevel.class.getSimpleName(), "name", memLevel.getName(), dept.getOrgAccountId());
                    if (accountLevel != null && accountLevel.size() > 0) {
                        member.setOrgLevelId(accountLevel.get(0).getId());
                    } else {
                        member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
                    }
                } else {
                    member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
                }
            }
            member.setOrgDepartmentId(deptId);
            member.setOrgAccountId(dept.getOrgAccountId());
            orgManagerDirect.updateMember(member);
        }

    }

    @Override
    public void addTeam(V3xOrgTeam team) throws BusinessException {
        if (team.getName() == null) {
            throw new BusinessException("添加组出错:组名称为空");
        }
        orgManagerDirect.addTeam(team);
    }

    @Override
    public void updateTeam(V3xOrgTeam team) throws BusinessException {
        if (team.getName() == null) {
            throw new BusinessException("修改组出错:组名称为空");
        }
        orgManagerDirect.updateTeam(team);
    }
    
    class MemberSyncher {

        //private Map<Long, V3xOrgEntity>  allRelationship;
        private Map<Long, V3xOrgMember>  allMember;
        //部门岗位关系分类
        private List<V3xOrgRelationship> depPosts;

        private List<V3xOrgMember>       addMemberQueue;
        private List<V3xOrgMember>       updateMemberQueue;
        private List<V3xOrgRelationship> addRelationshipQueue;
        //操作结果
        private Map<Long,String>                      mapReport;

        //取得单位内的组织信息    
        //private OrgCommonDao dao = orgManager.getOrgInstance().getDao();        
        private final long               accountId;
        private final boolean            rollback;
        private final boolean            isNeedSecondPost;

        public MemberSyncher(boolean rollback, boolean isNeedSecondPost, Long accountId) {
            this.rollback = rollback;
            this.isNeedSecondPost = isNeedSecondPost;
            this.accountId = accountId;
        }

        @SuppressWarnings("unchecked")
        public synchronized Map<Long, String> synchMember(List<V3xOrgMember> members) throws Exception {
            //获得单位最大排序号
            int maxNum = orgManagerDirect.getMaxSortNum(V3xOrgMember.class.getSimpleName(), accountId);
            init();
            //校验准备数据
            for (V3xOrgMember member : members) {
                try {
                    check(member);
                    //人员密码为空时设置默认密码
                    if (StringUtils.isEmpty(member.getPassword())) {
                        member.setV3xOrgPrincipal(new V3xOrgPrincipal(member.getId(), member.getLoginName(),
                                OrgConstants.DEFAULT_PASSWORD));
                    }
                    //判断人员是更新还是添加还是放弃
                    V3xOrgEntity ent = allMember.get(member.getId());
                    if (ent != null && !ent.getIsDeleted()) {
                    	//如果登录名变了或者重新设置了密码，就修改。
                    	if( (member.getLoginName() != null && !member.getLoginName().equals(((V3xOrgMember)ent).getLoginName())) ||
                    			StringUtils.isNotEmpty(member.getPassword()) ) {
                    		member.setV3xOrgPrincipal(new V3xOrgPrincipal(member.getId(), member.getLoginName(),
                    				StringUtils.isEmpty(member.getPassword()) ? OrgConstants.DEFAULT_PASSWORD : member.getPassword()));
                    	}
                        update(member);
                    } else if (ent == null) {
                        maxNum++;
                		member.setV3xOrgPrincipal(new V3xOrgPrincipal(member.getId(), member.getLoginName(),
                				StringUtils.isEmpty(member.getPassword()) ? OrgConstants.DEFAULT_PASSWORD : member.getPassword()));
                        add(maxNum, member);
                    }

                } catch (Exception e) {
                    if (rollback) {
                        mapReport.clear();
                        throw e;
                    } else {
                        mapReport.put(member.getId(), "1|" + e.getMessage());
                        log.error("nc同步出错： ", e);
                        continue;
                    }
                }
            }
            //调用direct接口进行实例化保存操作
            save();
            return mapReport;
        }

        private void init() throws BusinessException {
            //操作结果
            mapReport = new HashMap<Long, String>();
            //allRelationship = orgManager.geten
            //allMember = 
            allMember = new HashMap<Long, V3xOrgMember>();
            List<OrgMember> allMembers = orgDao.getAllMemberPOByAccountId(null, null, null, null, null, null, null);
            for (OrgMember po : allMembers) {
                allMember.put(po.getId(), (V3xOrgMember)OrgHelper.poTobo(po));
            }

            //部门岗位关系分类
            depPosts = new ArrayList<V3xOrgRelationship>();
            //实体操作列表
            addMemberQueue = new ArrayList<V3xOrgMember>();
            updateMemberQueue = new ArrayList<V3xOrgMember>();
            addRelationshipQueue = new ArrayList<V3xOrgRelationship>();
        }

        private void save() throws BusinessException {
            //插入人员
            showErrorLog(orgManagerDirect.addMembers(addMemberQueue).getErrorMsgs());
            showErrorLog(orgManagerDirect.updateMembers(updateMemberQueue).getErrorMsgs());
            //此处将接口内抛出的BusinessException全部抛出作为弹出的提示，不记录导入日志
            //OrgHelper.throwBusinessExceptionTools(addMessage);
            //OrgHelper.throwBusinessExceptionTools(updateMessage);
            //重新载入菜单一次 
        }

        private void showErrorLog(List<OrgMessage> errorMessages) {
            if(CollectionUtils.isNotEmpty(errorMessages)){
                for (OrgMessage orgMessage : errorMessages) {
                    log.info(orgMessage.getCode().name()+" "+orgMessage.getEnt().getId()+" "+orgMessage.getEnt().getName());
                    mapReport.put(orgMessage.getEnt().getId(), "1|" + orgMessage.getCode().name());
                }
            }
        }

        private void add(int sortId, V3xOrgMember member) throws BusinessException {
            // 添加
            if (!addMemberQueue.contains(member)) {
            	if(null == member.getSortId() || (null != member.getSortId() && member.getSortId() == 0)){
            		member.setSortId(Long.valueOf(sortId));
            	}
                addMemberQueue.add(member);
                // 添加账号及密码
                // 更新部门岗位关系
                boolean isContain = false;
                Long postId = member.getOrgPostId();
                Long departmentId = member.getOrgDepartmentId();
                if (member.getEnabled() && orgManager.getPostById(postId) != null) {
                    for (V3xOrgRelationship rel : depPosts) {
                        if (departmentId.equals(rel.getSourceId()) && postId.equals(rel.getObjective1Id())) {
                            isContain = true;
                        }
                    }
                }
            }
            mapReport.put(member.getId(), "0");

        }

        private void update(V3xOrgMember member) throws SecurityException, BusinessException {
            //更新
            if (updateMemberQueue.contains(member))
                return;
            //更新部门岗位关系
            boolean isContain = false;
            if (member.getEnabled() && orgManager.getPostById(member.getOrgPostId()) != null) {
                for (V3xOrgRelationship rel : depPosts) {
                    if (member.getOrgDepartmentId().equals(rel.getSourceId())
                            && member.getOrgPostId().equals(rel.getObjective0Id())) {
                        isContain = true;
                    }
                }
            }

            updateMemberQueue.add(member);
            mapReport.put(member.getId(), "0");
        }

        public boolean isChangeState(V3xOrgEntity ent1, V3xOrgMember member) {
            boolean isChange = false;
            return isChange;
        }

        // 检查Member的合法性
        private void check(V3xOrgMember member) throws BusinessException {
            if (Strings.isBlank(member.getName())) {
                throw new BusinessException("添加人员出错:人员姓名为空");
            }
            if (member.getOrgAccountId() == null) {
                throw new BusinessException("添加人员出错:人员单位为空");
            }
            if (!member.getOrgAccountId().equals(accountId)) {
                throw new BusinessException("添加人员出错:人员单位非同步单位");
            }
            //NC组织模型同步问题，开启NC同时启用NC插件，才判断人员编号
            if (AppContext.hasPlugin("nc") || AppContext.hasPlugin("cip")) {
                //NC同步，已经离职的人员A8账号已经删除，補充帳號
                if((null == member.getV3xOrgPrincipal() || Strings.isBlank(member.getLoginName())) 
                        && Strings.isNotBlank(member.getCode())) {
                    V3xOrgPrincipal p = new V3xOrgPrincipal(member.getId(), member.getCode(), orgManager.getInitPWD());
                    member.setV3xOrgPrincipal(p);
                }
            }
            if (principalManager.isExist(member.getLoginName())) {
                try {
                    if (principalManager.getMemberIdByLoginName(member.getLoginName()) != (member.getId())) {
                        throw new BusinessException("添加人员出错:登录名称 " + member.getLoginName() + " " + " 在系统中已经存在");
                    }
                }
                catch (NoSuchPrincipalException e) {
                    //ignore
                }
            }
            // 检查部门
            V3xOrgDepartment dept = orgManager.getDepartmentById(member.getOrgDepartmentId());
            if (dept == null) {
                member.setEnabled(false);
                member.setOrgDepartmentId(V3xOrgEntity.DEFAULT_NULL_ID);
                log.debug("set member " + member.getName() + " unable for null department");
            }
        }
    }
    
}