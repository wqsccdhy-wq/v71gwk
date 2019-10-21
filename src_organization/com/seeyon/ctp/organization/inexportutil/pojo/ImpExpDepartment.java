package com.seeyon.ctp.organization.inexportutil.pojo;

import java.util.List;
import java.util.Map;

public class ImpExpDepartment  extends ImpExpPojo{
    
    String code;
    
    String accountName;
    
    Integer level;
    
    String wholeName;
    
    ImpExpDepartment pre;
    
    Map<Long,List<String>> roleMembers;
    
    List<String> notExistRole;
    
    List<String> dupExistRole;

    public List<String> getDupExistRole() {
        return dupExistRole;
    }

    public void setDupExistRole(List<String> dupExistRole) {
        this.dupExistRole = dupExistRole;
    }

    public List<String> getNotExistRole() {
        return notExistRole;
    }

    public void setNotExistRole(List<String> notExistRole) {
        this.notExistRole = notExistRole;
    }

    public ImpExpDepartment getPre() {
        return pre;
    }

    public void setPre(ImpExpDepartment pre) {
        this.pre = pre;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getWholeName() {
        return wholeName;
    }

    public void setWholeName(String wholeName) {
        this.wholeName = wholeName;
    }

    public Map<Long, List<String>> getRoleMembers() {
        return roleMembers;
    }

    public void setRoleMembers(Map<Long, List<String>> roleMembers) {
        this.roleMembers = roleMembers;
    }
}
