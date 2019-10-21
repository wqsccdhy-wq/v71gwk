package com.seeyon.ctp.organization.bo;

import java.io.Serializable;

import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.po.OrgRole;
import com.seeyon.ctp.util.Strings;

/**
 * The persistent class for the v3x_org_role database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xOrgRole extends V3xOrgEntity implements Serializable {

    private static final long serialVersionUID = -1532623165109321396L;
    private int               bond             = OrgConstants.ROLE_BOND.ACCOUNT.ordinal();
    private int               type             = V3xOrgEntity.ROLETYPE_FIXROLE;//1.预置角色  2 集团同步的单位角色  3 自建角色
    private Boolean           isBenchmark      = false;                                    //默认不是基准角色
    private String            category         = "1";
    private String            showName         = "";

    /**
     * 复制传入的实体的属性值到Role的实例。
     * @param orgRole
     */
    public V3xOrgRole(V3xOrgRole orgRole) {
        this.id = orgRole.getId();
        this.isBenchmark = orgRole.getIsBenchmark();
        this.name = orgRole.getName();
        this.description = orgRole.getDescription();
        this.orgAccountId = orgRole.getOrgAccountId();
        this.sortId = orgRole.getSortId();
        this.createTime = orgRole.getCreateTime();
        this.enabled = orgRole.getEnabled();
        this.updateTime = orgRole.getUpdateTime();
        this.status = orgRole.getStatus();
        this.isDeleted = orgRole.getIsDeleted();
        this.category = orgRole.getCategory();
        this.type = orgRole.getType();
        this.code = orgRole.getCode();
        this.bond = orgRole.getBond();
        this.externalType = orgRole.getExternalType();
    }

    public V3xOrgRole() {
    }

    public V3xOrgRole(OrgRole orgRole) {
        this.fromPO(orgRole);
    }

    public V3xOrgEntity fromPO(BasePO po) {
        OrgRole orgRole = (OrgRole) po;
        this.id = orgRole.getId();
        this.isBenchmark = orgRole.isBenchmark();
        this.name = orgRole.getName();
        this.description = orgRole.getDescription();
        this.orgAccountId = orgRole.getOrgAccountId();
        this.sortId = orgRole.getSortId();
        this.createTime = orgRole.getCreateTime();
        this.enabled = orgRole.isEnable();
        this.updateTime = orgRole.getUpdateTime();
        this.status = orgRole.getStatus();
        this.isDeleted = orgRole.isDeleted();
        this.category = orgRole.getCategory();
        this.type = orgRole.getType();
        this.code = orgRole.getCode();
        this.bond = orgRole.getBond();
        this.externalType = orgRole.getExternalType();
        this.showName = showName(orgRole.getName());
        return this;
    }

    public BasePO toPO() {
        OrgRole o = new OrgRole();
        o.setId(this.id);
        o.setBenchmark(this.isBenchmark);
        o.setName(this.name);
        o.setDescription(this.description);
        o.setOrgAccountId(this.orgAccountId);
        o.setSortId(this.sortId.longValue());
        o.setCreateTime(this.createTime);
        o.setEnable(this.enabled);
        o.setUpdateTime(this.updateTime);
        o.setStatus(this.status);
        o.setDeleted(this.isDeleted);
        o.setCategory(this.category);
        o.setType(this.type);
        o.setCode(this.code);
        o.setBond(this.bond);
        o.setExternalType(this.externalType);
        return o;
    }

    public String getEntityType() {
        return OrgConstants.ORGENT_TYPE.Role.name();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBond() {
        return bond;
    }

    public void setBond(int bond) {
        this.bond = bond;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public Boolean getIsBenchmark() {
        return isBenchmark;
    }

    public void setIsBenchmark(Boolean isBenchmark) {
        this.isBenchmark = isBenchmark;
    }
    
    /**
     * 得到显示名称
     * 
     * @return
     */
    public String getShowName(){
        return showName(this.getName());
    }
    
    private String showName(String name){
    	//预制角色
		OrgConstants.Role_NAME RoleName =null;
		try{
			RoleName = OrgConstants.Role_NAME.valueOf(name);	            
        }
        catch(Exception e){
            //ignore
        }
		  	
        if(RoleName!=null){
            return ResourceUtil.getString("sys.role.rolename." + name);
        }
        else{
        	String customRoleName =  ResourceUtil.getString("sys.role.rolename." + name);
        	if(Strings.isNotBlank(customRoleName) && !customRoleName.equals("sys.role.rolename." + name)){
        		return customRoleName;
        	}
            return name;
        }
    }

    public void toJsonString(StringBuilder o) {
        OrgConstants.Role_NAME roleName = null;
        try {
            roleName =  OrgConstants.Role_NAME.valueOf(this.getCode());
        }
        catch (Exception e) {
            //ignore
        }
        
        o.append("{");
        
        if(roleName != null && this.getBond() == OrgConstants.ROLE_BOND.DEPARTMENT.ordinal()){
            o.append(TOXML_PROPERTY_id).append(":\"").append(this.getCode()).append("\"");
        }
        else{
            o.append(TOXML_PROPERTY_id).append(":\"").append(this.getId()).append("\"");
        }

        o.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(getShowName())) .append("\"");
        o.append(",T:").append(this.getType());
        o.append(",B:").append(this.getBond());
        o.append("}");
    }
    /**
     * 修改OA-99598
     * 在流程表单中，对于单位角色和部门角色的导入都应该是code，如果单位角色导出的是Id
     * 那么在导入到另一个系统中的话，选择单位的节点就不可用了。
     * 为了避免对原有功能的影响，添加这个方法
     * 创建人:zhiyanqiang	
     * 创建时间：2016年8月10日 上午11:02:06    
     * @param o 
     */
    public void toJsonString4FormAccount(StringBuilder o)
    {

    	OrgConstants.Role_NAME roleName = null;
        try {
            roleName =  OrgConstants.Role_NAME.valueOf(this.getCode());
        }
        catch (Exception e) {
            //ignore
        }
        
        o.append("{");
        
        if(roleName != null && (this.getBond() == OrgConstants.ROLE_BOND.DEPARTMENT.ordinal() || this.getBond()==OrgConstants.ROLE_BOND.ACCOUNT.ordinal())){
            o.append(TOXML_PROPERTY_id).append(":\"").append(this.getCode()).append("\"");
        }
        else{
            o.append(TOXML_PROPERTY_id).append(":\"").append(this.getId()).append("\"");
        }

        o.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(getShowName())) .append("\"");
        o.append(",T:").append(this.getType());
        o.append(",B:").append(this.getBond());
        o.append("}");
   
    }
    public boolean isValid() {
        return enabled && !isDeleted;
    }

	public void toVjoinJsonString(StringBuilder o) {
		OrgConstants.Role_NAME roleName = null;
        try {
            roleName =  OrgConstants.Role_NAME.valueOf(this.getCode());
        }
        catch (Exception e) {
            //ignore
        }
        
        o.append("{");
        
        if(roleName != null && this.getBond() == OrgConstants.ROLE_BOND.DEPARTMENT.ordinal()){
            o.append(TOXML_PROPERTY_id).append(":\"").append(this.getCode()).append("\"");
        }
        else{
            o.append(TOXML_PROPERTY_id).append(":\"SVjoin").append(this.getId()).append("\"");
        }
        
        o.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(getShowName())) .append("("+ ResourceUtil.getString("org.outer") +")\"");
        o.append(",T:").append(this.getType());
        o.append(",B:").append(this.getBond());
        o.append("}");
	}

}