package com.seeyon.ctp.organization.bo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.po.OrgRelationship;
import com.seeyon.ctp.util.UUIDLong;

/**
 * The persistent class for the v3x_org_relationship database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xOrgRelationship implements Serializable {

    private static final long serialVersionUID = -5122371163802509758L;

    private Long id = UUIDLong.longUUID();
    private java.lang.String  key;
    private java.lang.Long    sourceId;
    private java.lang.Long    objective0Id;
    private java.lang.Long    objective1Id;
    private java.lang.Long    objective2Id;
    private java.lang.Long    objective3Id;
    private java.lang.Long    objective4Id;
    private java.lang.String  objective5Id;
    private java.lang.String  objective6Id;
    private java.lang.String  objective7Id;
    private java.lang.Long    sortId = 0L;
    private java.lang.Long    orgAccountId;
    private Date createTime = new Date();
    private Date updateTime= new Date();
    
    public V3xOrgRelationship() {
    }

    public V3xOrgRelationship(OrgRelationship po) {
        this.fromPO(po);
    }

    public V3xOrgRelationship fromPO(BasePO po) {
        OrgRelationship rel = (OrgRelationship) po;
        this.key = rel.getType();
        this.id = rel.getId();
        this.sourceId = rel.getSourceId();
        this.objective0Id = rel.getObjective0Id();
        this.objective1Id = rel.getObjective1Id();
        this.objective2Id = rel.getObjective2Id();
        this.objective3Id = rel.getObjective3Id();
        this.objective4Id = rel.getObjective4Id();
        this.objective5Id = rel.getObjective5Id();
        this.objective6Id = rel.getObjective6Id();
        this.objective7Id = rel.getObjective7Id();
        this.orgAccountId = rel.getOrgAccountId();
        this.sortId = rel.getSortId();
        this.createTime = rel.getCreateTime();
        this.updateTime = rel.getUpdateTime();
        return this;
    }

    public BasePO toPO() {
        OrgRelationship po = new OrgRelationship();
        po.setId(this.id);
        po.setType(this.key);
        po.setSourceId(this.sourceId);
        po.setObjective0Id(this.objective0Id);
        po.setObjective1Id(this.objective1Id);
        po.setObjective2Id(this.objective2Id);
        po.setObjective3Id(this.objective3Id);
        po.setObjective4Id(this.objective4Id);
        po.setObjective5Id(this.objective5Id);
        po.setObjective6Id(this.objective6Id);
        po.setObjective7Id(this.objective7Id);
        po.setSortId(this.sortId);
        po.setOrgAccountId(this.orgAccountId);
        po.setCreateTime(this.createTime);
        po.setUpdateTime(this.updateTime);
        return po;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.lang.String getKey() {
        return key;
    }

    public void setKey(java.lang.String key) {
        this.key = key;
    }

    public java.lang.Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(java.lang.Long sourceId) {
        this.sourceId = sourceId;
    }

    public java.lang.Long getObjective0Id() {
        return objective0Id;
    }

    public void setObjective0Id(java.lang.Long objective0Id) {
        this.objective0Id = objective0Id;
    }

    public java.lang.Long getObjective1Id() {
        return objective1Id;
    }

    public void setObjective1Id(java.lang.Long objective1Id) {
        this.objective1Id = objective1Id;
    }

    public java.lang.Long getObjective2Id() {
        return objective2Id;
    }

    public void setObjective2Id(java.lang.Long objective2Id) {
        this.objective2Id = objective2Id;
    }

    public java.lang.Long getObjective3Id() {
        return objective3Id;
    }

    public void setObjective3Id(java.lang.Long objective3Id) {
        this.objective3Id = objective3Id;
    }

    public java.lang.Long getObjective4Id() {
        return objective4Id;
    }

    public void setObjective4Id(java.lang.Long objective4Id) {
        this.objective4Id = objective4Id;
    }

    public java.lang.String getObjective5Id() {
        return objective5Id;
    }

    public void setObjective5Id(java.lang.String objective5Id) {
        this.objective5Id = objective5Id;
    }

    public java.lang.String getObjective6Id() {
        return objective6Id;
    }

    public void setObjective6Id(java.lang.String objective6Id) {
        this.objective6Id = objective6Id;
    }

    public java.lang.String getObjective7Id() {
        return objective7Id;
    }

    public void setObjective7Id(java.lang.String objective7Id) {
        this.objective7Id = objective7Id;
    }

    public java.lang.Long getSortId() {
        return sortId;
    }

    public void setSortId(java.lang.Long sortId) {
        this.sortId = sortId;
    }

    public java.lang.Long getOrgAccountId() {
        return orgAccountId;
    }

    public void setOrgAccountId(java.lang.Long orgAccountId) {
        this.orgAccountId = orgAccountId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public int hashCode() {
        return this.getId().hashCode();
    }
    
    public static final ToStringStyle v3xToStringStyle = new V3xToStringStyle();

    private static final class V3xToStringStyle extends ToStringStyle {
        private static final long serialVersionUID = -6192155606714372299L;

        private V3xToStringStyle() {
            super();
        }

        public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
            if (value != null) {
                appendFieldStart(buffer, fieldName);
                appendInternal(buffer, fieldName, value, isFullDetail(fullDetail));
                appendFieldEnd(buffer, fieldName);
            }
        }

        private Object readResolve() {
            return BasePO.v3xToStringStyle;
        }
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this, v3xToStringStyle);
    }

}