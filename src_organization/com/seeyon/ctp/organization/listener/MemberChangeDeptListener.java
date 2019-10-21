package com.seeyon.ctp.organization.listener;

import java.util.EnumMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.MemberRole;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.event.MemberUpdateDeptEvent;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.annotation.ListenEvent;

/**
 * 人员修改部门，清空所有部门角色监听
 * @author MrBean
 *
 */
public class MemberChangeDeptListener {
    private final static Log   logger = LogFactory.getLog(MemberChangeDeptListener.class);
    
    @ListenEvent(event = MemberUpdateDeptEvent.class, async = true)
    public void cleanOldDeptRolesForMember(MemberUpdateDeptEvent event) {
    	Long oldDeptId = event.getOldDepartmentId();
    	Long newDeptId = event.getNewDepartmentId();
        OrgManager orgManager = OrgHelper.getOrgManager();
        OrgDao orgDao = (OrgDao) AppContext.getBean("orgDao");
        try {
        	V3xOrgUnit oldDept = orgManager.getUnitById(oldDeptId);
        	V3xOrgUnit newDept = orgManager.getUnitById(newDeptId);
        	if(oldDept == null || newDept == null){
        		return;
        	}
        	if(oldDept.getOrgAccountId().equals(newDept.getOrgAccountId())){
        		return;
        	}
            List<MemberRole> roles = orgManager.getMemberRoles(event.getMember().getId(), event.getOldDepartmentId());
            for (MemberRole memberRole : roles) {
            	if(memberRole.getDepartment()==null){
            		continue;
            	}
                EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds = new EnumMap<OrgConstants.RelationshipObjectiveName, Object>(
                        OrgConstants.RelationshipObjectiveName.class);
                objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective0Id, memberRole.getDepartment().getId());
                orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Role.name(), memberRole.getMemberId(), memberRole.getAccountId(), objectiveIds);
            }
        } catch (BusinessException e) {
            logger.error(e);
        }
        
    }
}
