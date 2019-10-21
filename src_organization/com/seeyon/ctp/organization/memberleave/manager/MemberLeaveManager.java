/**
 * 
 */
package com.seeyon.ctp.organization.memberleave.manager;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.memberleave.bo.MemberLeaveDetail;
import com.seeyon.ctp.organization.memberleave.bo.MemberLeavePending;
import com.seeyon.ctp.util.FlipInfo;

/**
 * @author tanmf
 *
 */
public interface MemberLeaveManager {

    public List<MemberLeavePending> getMemberLeavePending(long leaveMemberId) throws BusinessException;
    
    public List<MemberLeaveDetail> getMemberLeaveHandItem(long leaveMemberId,int category) throws BusinessException;
    
    public FlipInfo listPendingData(FlipInfo fi, Map<String, String> params) throws BusinessException;
    
    public boolean save4Leave(long leaveMemberId, Map<String, Long> agentMember) throws BusinessException;
    
    /**
     * 从离职到在职，把代理交接取消掉
     * 
     * @param eventObject
     */
    public void transMemberReturn(long leaveMemberId) throws BusinessException;
    
    public FlipInfo showLeaveInfo(FlipInfo fi, Map params) throws BusinessException;

    /**
     * 给触发用的离职交接接口，不更新人员信息，只添加待办交接人
     * @param leaveMemberId
     * @param agentMember
     * @return
     * @throws BusinessException
     */
	boolean save4LeaveFromTrigger(long leaveMemberId,Map<String, Long> agentMember) throws BusinessException;

	/**
	 * 离职交接数据保存
	 * @param index 处理：1-未处理事项 2-流程节点 3-组人员 4-色权限 5-工作管理权限 6-业务管理权限 
	 * @param memberId
	 * @param map
	 * @throws Exception
	 */
	void dealLeave(int index, String oldMemberId, String newMemberId, Object data) throws Exception;

	/**
	 * 取到离职人员所在的组
	 * @param fi
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	FlipInfo showTeamList(FlipInfo fi, Map params) throws BusinessException;

	/**
	 * 更新人员为离职状态
	 * @param leaveMemberId
	 * @return
	 * @throws BusinessException
	 */
	void updateMemberToLeave(long leaveMemberId) throws BusinessException;

	/**
	 * 离职人员的工作交接日志
	 * @param fi
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	FlipInfo queryAppLogs(FlipInfo fi, Map params) throws BusinessException;

	/**
	 * 交接日志
	 * @param oldMemberId
	 * @param newMemberId
	 * @param authNames
	 * @param categoryName
	 * @throws BusinessException
	 */
	void saveLog(Long oldMemberId, Long newMemberId, String authNames, String categoryName) throws BusinessException;
}
