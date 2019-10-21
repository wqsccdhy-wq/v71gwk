package com.seeyon.ctp.organization.bo;
import java.io.Serializable;

import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.po.OrgLevel;
import com.seeyon.ctp.util.Strings;

/**
 * The persistent class for the v3x_org_level database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xOrgLevel extends V3xOrgEntity implements Serializable {
	
    private static final long serialVersionUID = 4681183992409719079L;
    private Integer levelId;
	private Long groupLevelId;
	
	/**
	 * 复制传入的实体的属性值到Level的实例。
	 * @param orgLevel
	 */
	public V3xOrgLevel(V3xOrgLevel orgLevel) {
        this.id = orgLevel.getId();
        this.name = orgLevel.getName();
        this.sortId = orgLevel.getSortId();
        this.createTime = orgLevel.getCreateTime();
        this.description = orgLevel.getDescription();
        this.enabled = orgLevel.getEnabled();
        this.updateTime = orgLevel.getUpdateTime();
        this.status = orgLevel.getStatus();
        this.orgAccountId = orgLevel.getOrgAccountId();
        this.isDeleted = orgLevel.getIsDeleted();
        this.groupLevelId = orgLevel.getGroupLevelId();
        this.levelId = orgLevel.getLevelId().intValue();
        this.code = orgLevel.getCode();
        
        this.liushuihao = orgLevel.makeLiushuihao();
	}
	
    public Long getGroupLevelId() {
		return groupLevelId;
	}


	public void setGroupLevelId(Long groupLevelId) {
		this.groupLevelId = groupLevelId;
	}


	public Integer getLevelId() {
		return levelId;
	}


	public void setLevelId(Integer levelId) {
		this.levelId = levelId;
	}

	
	
	public V3xOrgLevel() {
	    super();
    }
	
    public V3xOrgLevel(OrgLevel orgLevel) {
        this.fromPO(orgLevel);
    }
    
    public V3xOrgEntity fromPO(BasePO po) {
        OrgLevel orgLevel = (OrgLevel)po;
        
        this.id = orgLevel.getId();
        this.name = orgLevel.getName();
        this.sortId = orgLevel.getSortId();
        this.createTime = orgLevel.getCreateTime();
        this.description = orgLevel.getDescription();
        this.enabled = orgLevel.isEnable();
        this.updateTime = orgLevel.getUpdateTime();
        this.status = orgLevel.getStatus();
        this.orgAccountId = orgLevel.getOrgAccountId();
        this.isDeleted = orgLevel.isDeleted();
        this.groupLevelId = orgLevel.getGroupLevelId();
        this.levelId = orgLevel.getLevelId().intValue();
        this.code = orgLevel.getCode();
        
        return this;
    }

    public BasePO toPO() {
        OrgLevel o = new OrgLevel();
        o.setId(this.id);
        o.setName(this.name);
        o.setSortId(this.sortId.longValue());
        o.setCreateTime(this.createTime);
        o.setDescription(this.description);
        o.setEnable(this.enabled);
        o.setUpdateTime(this.updateTime);
        o.setStatus(this.status);
        o.setOrgAccountId(this.orgAccountId);
        o.setDeleted(this.isDeleted);
        o.setGroupLevelId(this.groupLevelId);
        o.setLevelId(this.levelId.longValue());
        o.setCode(this.code);
        return o;
    }




    public String getEntityType()
    {
        return OrgConstants.ORGENT_TYPE.Level.name();
    }
	
	/**
	 * 给选人界面用的，不要轻易修改
	 */
	public void toJsonString(StringBuilder o) {
		o.append("{");
		o.append(TOXML_PROPERTY_id).append(":\"").append(this.getId()).append("\"");
		o.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(this.getName())).append("\"");
		o.append(",S:").append(this.getLevelId());
		o.append(",H:").append(this.makeLiushuihao());
		String l = "-1";
		if(this.getGroupLevelId() != null){
			l = String.valueOf(this.getGroupLevelId());
		}
		o.append(",G:").append("\""+l+"\"");
		
		if(Strings.isNotBlank(this.code)){
			o.append(",").append(TOXML_PROPERTY_Code).append(":\"").append(Strings.escapeJavascript(this.code)).append("\"");
		}
		o.append("}");
	}
	
	private transient int liushuihao = 0;
	private static Object lock = new Object();
	private static int liushui = 0;
	public int makeLiushuihao(){
	    if(this.liushuihao == 0){
	        synchronized(lock){
    	        if(this.liushuihao == 0){
    	            liushuihao = ++liushui;
		}
	        }
	        return liushuihao;
	    }
		return liushuihao;
	}

	@Override
	public boolean isValid() {
		return enabled && !isDeleted;
	}
	
}