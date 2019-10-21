/**
 * 
 */
package com.seeyon.ctp.organization.memberleave.bo;

/**
 * @author tanmf
 *
 */
public class MemberLeavePending {

    private String key;
    
    private String label;
    
    private int count;
    
    private Long agentMemberId;
    
    private boolean isMustSetAgentMember;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count == null ? 0 : count;
    }

    public Long getAgentMemberId() {
        return agentMemberId;
    }

    public void setAgentMemberId(Long agentMemberId) {
        this.agentMemberId = agentMemberId;
    }

    public boolean isMustSetAgentMember() {
        return isMustSetAgentMember;
    }

    public void setMustSetAgentMember(boolean isMustSetAgentMember) {
        this.isMustSetAgentMember = isMustSetAgentMember;
    }
    
}
