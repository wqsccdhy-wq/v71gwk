package com.seeyon.ctp.organization.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrgTypeIdBO;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;

public class OuterWorkerAuthUtil {
    private static final Log log = LogFactory.getLog(OuterWorkerAuthUtil.class);
    /**
     * 得到外单位人员可以访问的  部门（内部部门+自己）。
     * @param memberId
     * @param departmentId
     * @param accountId
     * @param orgManager
     * @return
     * @throws BusinessException
     */
    public static Collection<V3xOrgDepartment> getCanAccessDep(Long memberId,Long departmentId,Long accountId,OrgManager orgManager) throws BusinessException{
    	return getCanAccessDep(memberId, departmentId, accountId, orgManager, false);
    }
    /**
     * 得到外单位人员可以访问的 部门(内部+自己)。
     * @param memberId
     * @param departmentId
     * @param accountId
     * @param orgManager
     * @return
     * @throws BusinessException
     */
    public static List<V3xOrgDepartment> getCanAccessDep(Long memberId,Long departmentId,Long accountId,OrgManager orgManager, boolean forSeletePeople) throws BusinessException{
        List<V3xOrgEntity> canReadList = orgManager.getExternalMemberWorkScope(memberId,false);
        List<V3xOrgDepartment> depList = new UniqueList<V3xOrgDepartment>();
        Map<Long,V3xOrgDepartment> depMap = new HashMap<Long,V3xOrgDepartment>();
        if(canReadList != null && !canReadList.isEmpty()){
            for(V3xOrgEntity access : canReadList){
                //可访问人员
                if(access instanceof V3xOrgMember){
                    V3xOrgMember m = (V3xOrgMember) access;
                    V3xOrgDepartment d = orgManager.getDepartmentById(m.getOrgDepartmentId());
                    if(d != null){
                        if(d.getOrgAccountId().equals(accountId)){
                            depMap.put(d.getId(), d);
                        } else{ //被访问的人在当前单位是兼职，找他的兼职部门
                            Set<Long> secondDepts = orgManager.getConcurentPostsByMemberId(accountId, m.getId()).keySet();
                            if(!secondDepts.isEmpty()){
                                V3xOrgDepartment d2 = orgManager.getDepartmentById(secondDepts.iterator().next());
                                if(d2 != null){
                                    depMap.put(d2.getId(), d2);
                                }
                            }
                        }
                    }
                }else if(access instanceof V3xOrgDepartment){
                    V3xOrgDepartment d = (V3xOrgDepartment) access;
                    List<V3xOrgDepartment> childDep = orgManager.getChildDepartments(d.getId(), false);//不要其他的外部门
                    for(V3xOrgDepartment child : childDep){
                        if(child.getIsInternal())
                            depMap.put(child.getId(), child);
                    }
                    depMap.put(d.getId(), d);
                }else if (access instanceof V3xOrgAccount){
                    if(accountId.longValue() == access.getId()){
                        List<V3xOrgDepartment> depts = orgManager.getChildDepartments(access.getId(), false, true);
                        for(V3xOrgDepartment d : depts){
                            depMap.put(d.getId(), d);
                        }
                    }
                }
            }
        }
        if(!forSeletePeople){
        	//组成员
        	List<V3xOrgTeam> outWorkerTeam = orgManager.getTeamsByMember(memberId,accountId);
        	if(outWorkerTeam != null && !outWorkerTeam.isEmpty()){
        		for(V3xOrgTeam team : outWorkerTeam){
        			List<OrgTypeIdBO> teamMember = team.getAllMembers();
        			for(OrgTypeIdBO teamMemberId :teamMember){
        				V3xOrgEntity m = orgManager.getEntityById(OrgHelper.getV3xClass(teamMemberId.getType()), teamMemberId.getLId());
        				if(m != null && !m.getIsDeleted() && m.getOrgAccountId().longValue() == accountId){
        					if(m instanceof V3xOrgMember){
        						V3xOrgMember member = (V3xOrgMember) m;
        						V3xOrgDepartment d = orgManager.getDepartmentById(member.getOrgDepartmentId());
        						if(d.getIsInternal())
        							depMap.put(d.getId(), d);
        					}
        				}
        			}
        		}
        	}
        }
        //本部门
        V3xOrgDepartment outDep = orgManager.getDepartmentById(departmentId);
        depMap.put(outDep.getId(), outDep);
        /*//跨靠部门---没有隶属关系
        if(!canAccessAllAccount){
            List<V3xOrgDepartment> depts = orgManager.getAllDepartments(accountId);
            for(V3xOrgDepartment d : depts){
                if(outDep.getOrgAccountId().longValue() == d.getOrgAccountId() && d.getParentPath().equals(outDep.getParentPath())){
                    depMap.put(d.getId(), d);
                }
            }
        }*/
        if(depMap.size()>0){
            depList = new UniqueList<V3xOrgDepartment>(depMap.values());
        }
        Collections.sort(depList, CompareSortEntity.getInstance());
        return depList;
    }
    
    
    public static Collection<V3xOrgDepartment> getCanAccessDepexclusion (Long memberId,Long departmentId,Long accountId,OrgManager orgManager) throws BusinessException{
        List<V3xOrgEntity> canReadList = orgManager.getExternalMemberWorkScope(memberId,false);
        List<V3xOrgDepartment> depList = new UniqueList<V3xOrgDepartment>();
        Map<Long,V3xOrgDepartment> depMap = new HashMap<Long,V3xOrgDepartment>();
        if(canReadList != null && !canReadList.isEmpty()){
            for(V3xOrgEntity access : canReadList){
                //可访问人员
                if(access instanceof V3xOrgMember){
                    V3xOrgMember m = (V3xOrgMember) access;
                    V3xOrgDepartment d = orgManager.getDepartmentById(m.getOrgDepartmentId());
                    if(d != null){
                        if(d.getOrgAccountId().equals(accountId)){
                            depMap.put(d.getId(), d);
                        } else{ //被访问的人在当前单位是兼职，找他的兼职部门
                            Set<Long> secondDepts = orgManager.getConcurentPostsByMemberId(accountId, m.getId()).keySet();
                            if(!secondDepts.isEmpty()){
                                V3xOrgDepartment d2 = orgManager.getDepartmentById(secondDepts.iterator().next());
                                if(d2 != null){
                                    depMap.put(d2.getId(), d2);
                                }
                            }
                        }
                    }
                }else if(access instanceof V3xOrgDepartment){
                    V3xOrgDepartment d = (V3xOrgDepartment) access;
                    List<V3xOrgDepartment> childDep = orgManager.getChildDepartments(d.getId(), false);//不要其他的外部门
                    for(V3xOrgDepartment child : childDep){
                        if(child.getIsInternal())
                            depMap.put(child.getId(), child);
                    }
                    depMap.put(d.getId(), d);
                }else if (access instanceof V3xOrgAccount){
                    if(accountId.longValue() == access.getId()){
                        List<V3xOrgDepartment> depts = orgManager.getChildDepartments(access.getId(), false, true);
                        for(V3xOrgDepartment d : depts){
                            depMap.put(d.getId(), d);
                        }
                    }
                }
            }
        }
        //本部门
        V3xOrgDepartment outDep = orgManager.getDepartmentById(departmentId);
        depMap.put(outDep.getId(), outDep);
        if(depMap.size()>0){
            depList = new UniqueList<V3xOrgDepartment>(depMap.values());
        }
        Collections.sort(depList, CompareSortEntity.getInstance());
        return depList;
    }
    
    /**
     * 得到外单位人员可以访问的 人员。
     * @param memberId
     * @param departmentId
     * @param accountId
     * @param orgManager
     * @return
     * @throws BusinessException
     */
    public static Collection<V3xOrgMember> getCanAccessMembers(Long memberId,Long departmentId,Long accountId,OrgManager orgManager) throws BusinessException{
        List<V3xOrgEntity> canReadList = orgManager.getExternalMemberWorkScope(memberId,false);
        List<V3xOrgDepartment> depList = new UniqueList<V3xOrgDepartment>();
        Map<Long,V3xOrgDepartment> depMap = new HashMap<Long,V3xOrgDepartment>();
        List<V3xOrgMember> memberList = new UniqueList<V3xOrgMember>();
        Map<Long,V3xOrgMember> memberMap = new HashMap<Long,V3xOrgMember>();
        if(canReadList != null && !canReadList.isEmpty()){
            for(V3xOrgEntity access : canReadList){
                //可访问人员
                if(access instanceof V3xOrgMember){
                    V3xOrgMember m = (V3xOrgMember) access;
                    if(!m.isValid()){
                    	continue;
                    }
                    V3xOrgDepartment d = orgManager.getDepartmentById(m.getOrgDepartmentId());
                    if(d != null){
                        if(d.getOrgAccountId().equals(accountId)){
                            memberMap.put(m.getId(), m);
                        } else{ //被访问的人在当前单位是兼职，找他的兼职部门
                            Set<Long> secondDepts = orgManager.getConcurentPostsByMemberId(accountId, m.getId()).keySet();
                            if(!secondDepts.isEmpty()){
                                V3xOrgDepartment d2 = orgManager.getDepartmentById(secondDepts.iterator().next());
                                if(d2 != null){
                                	memberMap.put(m.getId(), m);
                                }
                            }
                        }
                    }
                }else if(access instanceof V3xOrgDepartment){
                    V3xOrgDepartment d = (V3xOrgDepartment) access;
                    List<V3xOrgDepartment> childDep = orgManager.getChildDepartments(d.getId(), false);//不要其他的外部门
                    for(V3xOrgDepartment child : childDep){
                        if(child.getIsInternal())
                            depMap.put(child.getId(), child);
                    }
                    depMap.put(d.getId(), d);
                }else if (access instanceof V3xOrgAccount){
                    if(accountId.longValue() == access.getId()){
                        List<V3xOrgDepartment> depts = orgManager.getChildDepartments(access.getId(), false, true);
                        for(V3xOrgDepartment d : depts){
                            depMap.put(d.getId(), d);
                        }
                    }
                }
            }
        }
        

        //本部门
        V3xOrgDepartment outDep = orgManager.getDepartmentById(departmentId);
        if(outDep != null) {
        	depMap.put(outDep.getId(), outDep);
        }
        /*//跨靠部门---没有隶属关系
        if(!canAccessAllAccount){
            List<V3xOrgDepartment> depts = orgManager.getAllDepartments(accountId);
            for(V3xOrgDepartment d : depts){
                if(outDep.getOrgAccountId().longValue() == d.getOrgAccountId() && d.getParentPath().equals(outDep.getParentPath())){
                    depMap.put(d.getId(), d);
                }
            }
        }*/
        if(depMap.size()>0){
            depList = new UniqueList<V3xOrgDepartment>(depMap.values());
        }
        
        if(memberMap.size()>0){
        	memberList = new UniqueList<V3xOrgMember>(memberMap.values());
        }
        for(V3xOrgDepartment v3xOrgDepartment : depList){
        	List<V3xOrgMember> membersByDepartment = orgManager.getMembersByDepartment(v3xOrgDepartment.getId(), false);
        	memberList.addAll(membersByDepartment);
        }
        //外部人员在组内，组内的人员不显示在组织架构中。
        //组成员
/*        List<V3xOrgTeam> outWorkerTeam = orgManager.getTeamsByMember(memberId,accountId);
        if(outWorkerTeam != null && !outWorkerTeam.isEmpty()){
            for(V3xOrgTeam team : outWorkerTeam){
                List<OrgTypeIdBO> teamMember = team.getAllMembers();
                for(OrgTypeIdBO teamMemberId :teamMember){
                    V3xOrgEntity m = orgManager.getEntityById(OrgHelper.getV3xClass(teamMemberId.getType()), teamMemberId.getId());
                    if(m != null && !m.getIsDeleted() && m.getOrgAccountId().longValue() == accountId){
                        if(m instanceof V3xOrgMember){
                            V3xOrgMember member = (V3xOrgMember) m;
                            V3xOrgDepartment d = orgManager.getDepartmentById(member.getOrgDepartmentId());
	                        if(d.getIsInternal()) {
	                        	//depMap.put(d.getId(), d);
	                        	memberList.add(member);
	                        }
                        }
                    }
                }
            }
        }*/
        return memberList;
    }
    
    
    /**
     * 获取内部人员能范围的外部部门
     * @param memberId
     * @param accountId
     * @param orgManager
     * @return
     * @throws BusinessException
     */
    public static List<V3xOrgDepartment> getAccessOuterDep(Long memberId,Long accountId,OrgManager orgManager) throws BusinessException{
    	List<V3xOrgDepartment> result = new UniqueList<V3xOrgDepartment>();
    	V3xOrgMember member = orgManager.getMemberById(memberId);
    	if(member == null || !member.isValid() || !member.getIsInternal()){
    		return result;
    	}
    	
    	List<V3xOrgDepartment> allOuterDepList = orgManager.getChildDepartments(accountId, false, false);
    	for(V3xOrgDepartment out : allOuterDepList){
    		if(canAccessOuterDep(memberId,member.getOrgDepartmentId(),accountId,out,orgManager)){
    			result.add(out);
    		}
    	}
		return result;
    }
    /**
     * 内部人员是否可以访问本外部门
     * @param memberId 人员ID
     * @param departmentId 部门ID
     * @param accountId 单位ID
     * @param outDepartment 外部门
     * @param orgManager
     * @return
     * @throws BusinessException
     */
    public static boolean canAccessOuterDep(Long memberId,Long departmentId,Long accountId,V3xOrgDepartment outDepartment,OrgManager orgManager) throws BusinessException{
        //判断是否是跨靠部门-本部门
        V3xOrgDepartment userDep = orgManager.getDepartmentById(departmentId);
        try {
            if(outDepartment == null || userDep == null) return false;
            List<Long> depIds = orgManager.getUserDomainIDs(memberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
            List<Long> accountIds = orgManager.getUserDomainIDs(memberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
            List<V3xOrgMember> members = orgManager.getExtMembersByDepartment(outDepartment.getId(), false);
            for(V3xOrgMember m : members){
                //权限部门
                Long longAccountId = accountId;
                List<V3xOrgEntity> canReadList = orgManager.getExternalMemberWorkScope(m.getId(),false);
                for(V3xOrgEntity entity : canReadList){
                    if(entity.getId().longValue() == memberId 
                            || entity.getId().longValue() == departmentId 
                            || entity.getId().longValue() == accountId 
                            || entity.getId().longValue()== longAccountId 
                            || depIds.contains(entity.getId())  
                            || accountIds.contains(entity.getId())){
                        return true;
                    }else{
                        Map<Long, List<MemberPost>> map = orgManager.getConcurentPostsByMemberId(longAccountId, memberId);
                        if(map!=null && map.containsKey(entity.getId())){
                            return true;
                        }
                    }
                }
                //组
                List<V3xOrgTeam> teams = orgManager.getTeamsByMember(m.getId(), m.getOrgAccountId());
                for(V3xOrgTeam t : teams){
                    List<OrgTypeIdBO> m1 = t.getAllMembers();
                    for(OrgTypeIdBO mm : m1){
                        if( Strings.equals(mm.getId(), memberId)){
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断是否可以访问外部门异常",e);
        }
        return false;
    }

    /**
     * 获取外部人员部门列表结构
     */
    public static List<WebV3xOrgDepartment> getOuterDeptList(ModelAndView mav, User user, Long currentAccountId, OrgManager orgManager) throws Exception {
        Collection<V3xOrgDepartment> canReadList = OuterWorkerAuthUtil.getCanAccessDep(user.getId(), user.getDepartmentId(), user.getAccountId(), orgManager);
        mav.addObject("external", canReadList);

        Map<Long, WebV3xOrgDepartment> webDeptList = new HashMap<Long, WebV3xOrgDepartment>();

        for (V3xOrgDepartment dept : canReadList) {
            if (dept != null) {
                WebV3xOrgDepartment webDept = new WebV3xOrgDepartment();
                webDept.setV3xOrgDepartment(dept);
                OuterWorkerAuthUtil.findParentDept(webDeptList, webDept, dept.getId(), currentAccountId, orgManager);
                webDeptList.put(dept.getId(), webDept);
            }
        }

        return new ArrayList<WebV3xOrgDepartment>(webDeptList.values());
    }
    
    /**
     * 获取外部人员部门列表结构,只显示工作范围内的部门
     */
    public static List<WebV3xOrgDepartment> getOuterSubDeptList(ModelAndView mav, User user, Long currentAccountId, OrgManager orgManager) throws Exception {
        Collection<V3xOrgDepartment> canReadList = OuterWorkerAuthUtil.getCanAccessDepexclusion(user.getId(), user.getDepartmentId(), user.getAccountId(), orgManager);
        mav.addObject("external", canReadList);

        Map<Long, WebV3xOrgDepartment> webDeptList = new HashMap<Long, WebV3xOrgDepartment>();
        String deptIds="";
        for (V3xOrgDepartment dept : canReadList) {
        	if (dept != null) {
        		deptIds+=dept.getId();
        	}
        }

        for (V3xOrgDepartment dept : canReadList) {
            if (dept != null) {
                WebV3xOrgDepartment webDept = new WebV3xOrgDepartment();
                webDept.setV3xOrgDepartment(dept);
                V3xOrgDepartment parentDept = orgManager.getParentDepartment(dept.getId());
                if (parentDept != null) {
                	// 设置父节点
                	if(deptIds.indexOf(parentDept.getId().toString())>=0){
                		webDept.setParentId(parentDept.getId());
                		webDept.setParentName(parentDept.getName());
                	}
                }
                webDeptList.put(dept.getId(), webDept);
            }
        }

        return new ArrayList<WebV3xOrgDepartment>(webDeptList.values());
    }

    /**
     * 组织部门列表结构
     */
    public static void findParentDept(Map<Long, WebV3xOrgDepartment> webDeptList, WebV3xOrgDepartment webDept, Long deptId, Long accountId, OrgManager orgManager) throws Exception {
        V3xOrgDepartment parentDept = orgManager.getParentDepartment(deptId);
        if (parentDept != null) {
            // 设置父节点
            webDept.setParentId(parentDept.getId());
            webDept.setParentName(parentDept.getName());

            // 添加父节点
            WebV3xOrgDepartment webParentDept = new WebV3xOrgDepartment();
            webParentDept.setV3xOrgDepartment(parentDept);

            OuterWorkerAuthUtil.findParentDept(webDeptList, webParentDept, parentDept.getId(), accountId, orgManager);

            webDeptList.put(parentDept.getId(), webParentDept);
        }
    }

}