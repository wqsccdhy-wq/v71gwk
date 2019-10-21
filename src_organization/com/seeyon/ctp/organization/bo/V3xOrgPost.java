/**
 * $Author: $
 * $Rev: $
 * $Date:: 2012-06-05 15:14:56#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */

package com.seeyon.ctp.organization.bo;
import java.io.Serializable;

import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.po.OrgPost;
import com.seeyon.ctp.util.ObjectToXMLUtil;
import com.seeyon.ctp.util.Strings;


/**
 * <p>Title: 组织模型岗位BO对象</p>
 * <p>Description:
 * </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * @since CTP2.0
 * @author lilong
 */
public class V3xOrgPost extends V3xOrgEntity implements Serializable {
    
    private static final long serialVersionUID = -8147796287389079418L;

    private Long typeId = V3xOrgEntity.DEFAULT_NULL_ID;
	/**
	 * 复制传入的实体的属性值到Post的实例。
	 * @param orgPost
	 */
	public V3xOrgPost(V3xOrgPost orgPost) {
        this.id = orgPost.getId();
        this.isDeleted = orgPost.getIsDeleted();
        this.name = orgPost.getName();
        this.description = orgPost.getDescription();
        this.createTime = orgPost.getCreateTime();
        this.sortId = orgPost.getSortId();
        this.enabled = orgPost.getEnabled();
        this.updateTime = orgPost.getUpdateTime();
        this.status = orgPost.getStatus();
        this.orgAccountId = orgPost.getOrgAccountId();
        this.typeId = orgPost.getTypeId();
        this.code = orgPost.getCode();
        this.externalType = orgPost.getExternalType();
        
        this.liushuihao = orgPost.makeLiushuihao();
	}
	
	public V3xOrgPost() {
	}
	
	public V3xOrgPost(OrgPost orgPost) {
	    this.fromPO(orgPost);
	}
	
    public V3xOrgEntity fromPO(BasePO po) {
        OrgPost orgPost = (OrgPost)po;
        this.id = orgPost.getId();
        this.isDeleted = orgPost.isDeleted();
        this.name = orgPost.getName();
        this.description = orgPost.getDescription();
        this.createTime = orgPost.getCreateTime();
        this.sortId = orgPost.getSortId();
        this.enabled = orgPost.isEnable();
        this.updateTime = orgPost.getUpdateTime();
        this.status = orgPost.getStatus();
        this.orgAccountId = orgPost.getOrgAccountId();
        this.typeId = orgPost.getType().longValue();
        this.code = orgPost.getCode();
        this.externalType = orgPost.getExternalType();
        
        return this;
    }

    public BasePO toPO() {
        OrgPost o = new OrgPost();
        o.setId(this.id);
        o.setDeleted(this.isDeleted);
        o.setName(this.name);
        o.setDescription(this.description);
        o.setCreateTime(this.createTime);
        o.setSortId(this.sortId.longValue());
        o.setEnable(this.enabled);
        o.setUpdateTime(this.updateTime);
        o.setStatus(this.status);
        o.setOrgAccountId(this.orgAccountId);
        o.setType(this.typeId);
        o.setCode(this.code);
        o.setExternalType(this.externalType);
        return o;
    }

	public Long getTypeId() {
		return this.typeId;
	}
	public void setTypeId(Long type) {
		this.typeId = type;
	}

    public String getEntityType()
    {
        return OrgConstants.ORGENT_TYPE.Post.name();
    }

	/**
	 * Post(id, name, description)
	 */
	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append(ObjectToXMLUtil.makeBeanNodeBegin(this.getClass()));
		
		sb.append(ObjectToXMLUtil.NEW_LINE);

		sb.append(ObjectToXMLUtil.makeProperties("id", this.getId()));
		sb.append(ObjectToXMLUtil.makeProperties(TOXML_PROPERTY_ENTITY_TYPE, "P"));
		sb.append(ObjectToXMLUtil.makeProperties(TOXML_PROPERTY_NAME, this.getName()));
		sb.append(ObjectToXMLUtil.makeProperties("T", this.getTypeId()));
				
		sb.append(ObjectToXMLUtil.NEW_LINE);

		sb.append(ObjectToXMLUtil.makeBeanNodeEnd());
		sb.append(ObjectToXMLUtil.NEW_LINE);

		return sb.toString();
	}
	
	/**
	 * 给选人界面用的，不要轻易修改
	 */
	public void toJsonString(StringBuilder o) {
		o.append("{");
		o.append(TOXML_PROPERTY_id).append(":'").append(this.getId()).append("'");
		o.append(",").append(TOXML_PROPERTY_NAME).append(":'").append(Strings.escapeJavascript(this.getName())).append("'");
		o.append(",").append(TOXML_PROPERTY_externalType).append(":'").append(this.getExternalType()).append("'");
		o.append(",T:").append(this.getTypeId());
		o.append(",H:").append(this.makeLiushuihao());
		
		if(Strings.isNotBlank(this.code)){
			o.append(",").append(TOXML_PROPERTY_Code).append(":'").append(Strings.escapeJavascript(this.getCode())).append("'");
		}
		o.append("}");
	}
	
    private transient int liushuihao = 0;
	
    private static int maxliushui = 0;
    private static Object lock = new Object();
    public int makeLiushuihao() {
        if (this.liushuihao == 0) {
            synchronized (lock) {
                if (this.liushuihao == 0) {
		            liushuihao = ++maxliushui;
				}
				return liushuihao;
			}
        }
        return liushuihao;
    }

	public boolean isValid() {
		return enabled && !isDeleted;
	}

}