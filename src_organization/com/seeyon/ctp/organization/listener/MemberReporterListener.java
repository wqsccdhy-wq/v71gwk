package com.seeyon.ctp.organization.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.event.DeleteMemberEvent;
import com.seeyon.ctp.organization.event.UpdateMemberEvent;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.util.annotation.ListenEvent;

/**
 * 人员，离职、停用、删除后，清空汇报人是此人的‘汇报人’信息
 * @author lilong
 * @version CTP2.0
 * @date 2013-01-25
 *
 */
public class MemberReporterListener {
	private final static Log   logger = LogFactory.getLog(MemberReporterListener.class);

    @ListenEvent(event = UpdateMemberEvent.class, async = true)
    public void clearDisableMemberReporter(UpdateMemberEvent event) {
        V3xOrgMember member = event.getMember();
        if (!member.isValid()) {
            try {
    			clearMemberReporter(member);
    		} catch (BusinessException e) {
    			logger.error("更新人员信息，清空人员汇报人失败！");
    		}
        }
    }

    @ListenEvent(event = DeleteMemberEvent.class, async = true)
    public void clearDelMemberReporter(DeleteMemberEvent event) {
        V3xOrgMember member = event.getMember();
        try {
			clearMemberReporter(member);
		} catch (BusinessException e) {
			logger.error("更新人员信息，清空人员汇报人失败！");
		}
    }
    
    
    
    private void clearMemberReporter(V3xOrgMember member) throws BusinessException{
    	 List<OrgMember> reportToMembers = new ArrayList<OrgMember>();
    	 OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
    	 OrgDao orgDao = (OrgDao) AppContext.getBean("orgDao");
    	 List<V3xOrgEntity> entityList = orgManager.getEntityNoRelation(V3xOrgMember.class.getSimpleName(),"extAttr37",member.getId(),null,false,false,null);
    	 for(int i=0;i<entityList.size();i++){
    		 Long memberId = entityList.get(i).getId();
    		 V3xOrgMember m = orgManager.getMemberById(memberId);
    		 m.setReporter(null);
			 reportToMembers.add((OrgMember) m.toPO());
				
    	 }
    	 orgDao.update(reportToMembers);
    }

}
