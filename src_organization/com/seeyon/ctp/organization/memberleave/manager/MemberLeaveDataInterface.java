/**
 * 
 */
package com.seeyon.ctp.organization.memberleave.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.memberleave.bo.MemberLeavePendingData;
import com.seeyon.ctp.util.FlipInfo;

/**
 * @author tanmf
 * 离职交接，需要设置的代理的信息
 */
public interface MemberLeaveDataInterface{
    
    public String getAppKey();
    
    /**
     * 显示key
     * 
     * @return
     */
    public abstract String getLabel();
    
    /**
     * 是否启用
     */
    public boolean isEnabled();
    
    /**
     * 未处理事项数据，如果没有可以返回null
     * 
     * @param memberId 要离职的人员Id
     * @return
     */
    public abstract Integer getCount(long memberId) throws BusinessException;
    
    /**
     * 未处理事项数据列表，如果没有可以返回null
     * 
     * @param fi
     * @param params <pre>
     *               参数：
     *                  memberId(Long):要离职的人员Id；
     *               查询条件：
     *                  senderName(String):流程发起者姓名；
     *                  sendDate(Date):发起时间；
     *                  subject(String):标题
     *               </pre>
     * @return
     */
    public abstract List<MemberLeavePendingData> list(FlipInfo fi, Map<String, Object> params) throws BusinessException;
    
    /**
     * 显示顺序：自由协同、模板协同、公文、公共信息审批
     * 
     * @return
     */
    public abstract int getSortId();
    
    /**
     * 是否必须设置交接人
     * 
     * @param leaveMemberId 要离职的人员Id；
     * @return
     */
    public boolean isMustSetAgentMember(long leaveMemberId) throws BusinessException;

    /**
     * 
     *  <code>
        //获得此人未处理协同的最早时间
        Date startTime = affairManager.getMinStartTimePending(leaveMemberId);
        //代理协同的结束时间
        Date endTime = Timestamp.valueOf("9999-12-31 23:59:59");
        </code>
     * 
     * @param leaveMemberId 要离职的人员Id；
     * @param agentMemberId 工作交接的人，可能为<code>null</code>
     * @throws BusinessException
     * @return
     */
    public abstract boolean doHandle(long leaveMemberId, long agentMemberId) throws BusinessException;
    
    /**
     * 删除代理关系
     * 
     * @param leaveMemberId
     * @return
     */
    public abstract boolean removeAgent(long leaveMemberId);
    
    /**
     * 得到已经设置了的代理信息 ， 没有返回Null
     * 
     * @param leaveMemberId
     * @return
     */
    public abstract Long getAgentMemberId(long leaveMemberId);

}
