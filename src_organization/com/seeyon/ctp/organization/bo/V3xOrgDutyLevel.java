package com.seeyon.ctp.organization.bo;
import java.io.Serializable;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.util.ObjectToXMLUtil;


/**
 * 政务版——职级（目前只在单位下管理，没有组织的职级）
 * 
 */
public class V3xOrgDutyLevel extends V3xOrgEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer levelId;
	
	/**
	 * 复制传入的实体的属性值到Level的实例。
	 * @param ent 为空不做任何操作，类型不匹配抛出IllegalArgumentException异常。
	 * @throws BusinessException 
	 */
	public V3xOrgDutyLevel(V3xOrgDutyLevel ent) {
		this();
		//TODO
	}
	
	
	public V3xOrgDutyLevel() {
//	    this.fromPO(po);
	    //TODO
    }
	
    public V3xOrgEntity fromPO(BasePO po) {
        // TODO Auto-generated method stub
        return this;
    }

    public BasePO toPO() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getEntityType()
    {
        return OrgConstants.ORGENT_TYPE.DutyLevel.name();
    }

	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append(ObjectToXMLUtil.makeBeanNodeBegin(this.getClass()));
		
		sb.append(ObjectToXMLUtil.NEW_LINE);

		sb.append(ObjectToXMLUtil.makeProperties("id", this.getId()));
		sb.append(ObjectToXMLUtil.makeProperties(TOXML_PROPERTY_ENTITY_TYPE, "L"));
		sb.append(ObjectToXMLUtil.makeProperties(TOXML_PROPERTY_NAME, this.getName()));
				
		sb.append(ObjectToXMLUtil.NEW_LINE);

		sb.append(ObjectToXMLUtil.makeBeanNodeEnd());
		sb.append(ObjectToXMLUtil.NEW_LINE);

		return sb.toString();
	}
	
//	/**
//	 * 给选人界面用的，不要轻易修改
//	 */
//	public void toJsonString(StringBuilder o) {
//		o.append("{");
//		o.append(TOXML_PROPERTY_id).append(":\"").append(this.getId()).append("\"");
//		o.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(this.getName())).append("\"");
//		o.append(",S:").append(this.getLevelId());
//		o.append(",H:").append(this.makeLiushuihao());
//		String l = "-1";
//		if(this.getGroupLevelId() != null){
//			l = String.valueOf(this.getGroupLevelId());
//		}
//		o.append(",G:").append("\""+l+"\"");
//		
//		if(Strings.isNotBlank(this.code)){
//			o.append(",").append(TOXML_PROPERTY_Code).append(":\"").append(Strings.escapeJavascript(this.code)).append("\"");
//		}
//		o.append("}");
//	}
	
	private int liushuihao = -1;
	
	private static int liushui = 0;
	public synchronized int makeLiushuihao(){
		if(this.liushuihao == -1){
			liushuihao = liushui++;
		}
		
		return liushuihao;
	}

	public boolean isValid() {
		return enabled && !isDeleted;
	}


	public Integer getLevelId() {
		return levelId;
	}


	public void setLevelId(Integer levelId) {
		this.levelId = levelId;
	}
	
}