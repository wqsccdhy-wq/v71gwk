package com.seeyon.ctp.organization.listener;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.event.MemberAccountChangeEvent;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.annotation.ListenEvent;

public class MemberChangeAccListener {

    private final static Log logger = LogFactory.getLog(MemberChangeAccListener.class);

    @ListenEvent(event = MemberAccountChangeEvent.class, async = true)
    public void cleanOldDeptRolesForMember(MemberAccountChangeEvent event) {
    	
        OrgDao orgDao = (OrgDao) AppContext.getBean("orgDao");
        OrgCache orgCache = (OrgCache) AppContext.getBean("orgCache");
        OrgManagerDirect orgManagerDirect = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
        // OA-60681 人员调出，清理掉在原单位的所有角色
        //从缓存里查询角色，避免因事务没提交导致的多删了缓存
        try {
        	List<V3xOrgRelationship> rels = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, event.getMember().getId(), event.getOldAccount(), null);
			orgManagerDirect.deleteOrgRelationships(rels);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
		}
    }
}
