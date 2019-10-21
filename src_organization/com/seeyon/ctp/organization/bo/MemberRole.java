/**
 * 
 */
package com.seeyon.ctp.organization.bo;

import java.io.Serializable;

/**
 * 人员所担任的角色对象
 * 
 * @author tanmf
 *
 */
public class MemberRole implements Serializable{
    //人员
    private long memberId;
    
    //角色
    private V3xOrgRole role;
    
    //如果是部门角色，才有值，单位角色是null
    private V3xOrgDepartment department;
    
    //单位
    private long accountId;
    
    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public V3xOrgRole getRole() {
        return role;
    }

    public void setRole(V3xOrgRole role) {
        this.role = role;
    }

    public V3xOrgDepartment getDepartment() {
        return department;
    }

    public void setDepartment(V3xOrgDepartment department) {
        this.department = department;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (accountId ^ (accountId >>> 32));
        result = prime * result + ((department == null) ? 0 : department.hashCode());
        result = prime * result + (int) (memberId ^ (memberId >>> 32));
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MemberRole other = (MemberRole) obj;
        if (accountId != other.accountId)
            return false;
        if (department == null) {
            if (other.department != null)
                return false;
        } else if (!department.equals(other.department))
            return false;
        if (memberId != other.memberId)
            return false;
        if (role == null) {
            if (other.role != null)
                return false;
        } else if (!role.equals(other.role))
            return false;
        return true;
    }
    
    
    
    
}
