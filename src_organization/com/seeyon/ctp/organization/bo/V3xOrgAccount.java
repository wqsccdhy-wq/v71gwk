package com.seeyon.ctp.organization.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;

/**
 * The persistent class for the _org_account database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xOrgAccount extends V3xOrgUnit implements Serializable {

    private static final long serialVersionUID = -2327812443752403806L;

    /** 是否可访问 */
    private Boolean           isCanAccess       = Boolean.TRUE;
    /** 是否可访问或不可访问的单位id列表 */
    private List<Long>        accessIds         = new UniqueList<Long>();
    /** 机构访问分级方位类型默认三个全选中 */
    private List<Integer>     accessScopeLevels = new ArrayList<Integer>();

    public V3xOrgAccount() {
        super();
    }

    /**
     * 浅度克隆一个OrgAccount,不克隆缓存相关的数据
     * @param account
     */
    public V3xOrgAccount(V3xOrgAccount account) {
        super((V3xOrgUnit)account);
    }

    public V3xOrgAccount(OrgUnit orgUnit) {
        super(orgUnit);
    }

    public String getEntityType() {
		if(this.externalType == OrgConstants.ExternalType.Interconnect4.ordinal()){
			return OrgConstants.ORGENT_TYPE.BusinessAccount.name();
		}else{
			return OrgConstants.ORGENT_TYPE.Account.name();
		}
    }

    /**
     * 给选人界面用的，不要轻易修改
     * @throws BusinessException 
     */
    public void toJsonString(StringBuilder o,OrgManager orgManager,OrgCache orgCache) throws BusinessException {
        o.append("{");
        o.append(TOXML_PROPERTY_id).append(":\"").append(this.getId()).append("\"");
        o.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(this.getName())).append("\"");
        o.append(",P:\"").append(this.getSuperior()).append("\"");
        o.append(",L:").append(this.getLevelScope());
        o.append(",S:\"").append(Strings.escapeJavascript(this.getShortName())).append("\"");
        o.append(",R:").append(this.isGroup());
        o.append(",M:").append(orgManager.getMemberNumsMapWithConcurrent().get(this.getId()));
        o.append(",").append(TOXML_PROPERTY_externalType).append(":\"").append(this.getExternalType()).append("\"");
        o.append("}");
    }
    
    public Long getId(){
        return super.getId();
    }
    
    public String getName(){
        return super.getName();
    }

    public Boolean getIsCanAccess() {
        return isCanAccess;
    }

    public void setIsCanAccess(Boolean isCanAccess) {
        this.isCanAccess = isCanAccess;
    }

    /**
     * 此方法仅用于单位保存时做为存储用的临时存储属性<br>
     * 该方法不做缓存也不要调用这个方法获取单位可以访问的id列表<br>
     * 请使用工具类OrgHelper.getAccessIdsByUnitId(Long unitId)方法<br>
     * @return
     * @throws BusinessException
     */
    public List<Long> getAccessIds() {
//        List<Long> accessIds = new UniqueList<Long>();
//        List<V3xOrgAccount> accessAccounts = OrgHelper.getOrgManager().accessableAccountsByUnitId(this.id);
//        for (V3xOrgAccount bo : accessAccounts) {
//            accessIds.add(bo.getId());
//        }
//        return accessIds;
        return accessIds;
    }

    public void setAccessIds(List<Long> accessIds) {
        this.accessIds = accessIds;
    }

    public List<Integer> getAccessScopeLevels() {
        return accessScopeLevels;
    }

    public void setAccessScopeLevels(List<Integer> accessScopeLevels) {
        this.accessScopeLevels = accessScopeLevels;
    }
    
    public boolean isRoot(){
        if("true".equals(SystemProperties.getInstance().getProperty("org.isGroupVer"))){//多组织
        	if(this.isGroup()){
        		return true;
        	}
        }else if(OrgConstants.ACCOUNTID.equals(this.id)){//单组织
        	return true;
        }
        return false;
    }
}
