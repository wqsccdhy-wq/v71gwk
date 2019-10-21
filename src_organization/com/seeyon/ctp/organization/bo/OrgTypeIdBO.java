package com.seeyon.ctp.organization.bo;


import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.util.Strings;

import java.io.Serializable;

public class OrgTypeIdBO implements Serializable{
    
    String id;
    String type;
    String include = "0";//0:包含子部门   1：不包含子部门
    
    public OrgTypeIdBO(){
        
    }
    
    public OrgTypeIdBO(String id, String type) {
        super();
        this.id = id;
        this.type = type;
    }
    
    public OrgTypeIdBO(Long id, String type) {
        super();
        this.id = id.toString();
        this.type = type;
    }

    public String getId() {
		return id;
	}
    //如果不是部门下的岗位，则可以转换为long
    public Long getLId(){
    	if(!this.type.equals(OrgConstants.ORGENT_TYPE.Department_Post.name())){
    		return Long.valueOf(id);
    	}
    	return null;
    }

	public void setId(String id) {
		this.id = id;
	}
	
	public void setId(Long id) {
		this.id = id.toString();
	}

	public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
	public String getInclude() {
		return include;
	}
	public void setInclude(String include) {
		this.include = include;
	}
	/**
	 * 获取部门下的岗位的部门id
	 * @return
	 */
	public Long getDepartmentId() {
		if(this.type.equals(OrgConstants.ORGENT_TYPE.Department_Post.name())){
			if(Strings.isNotBlank(id)){
				String[] ids = id.split(V3xOrgEntity.ROLE_ID_DELIMITER);
				return Long.valueOf(ids[0]);
			}
		}
		return null;
	}

	/**
	 * 获取部门下的岗位的岗位id
	 * @return
	 */
	public Long getPostId() {
		if(this.type.equals(OrgConstants.ORGENT_TYPE.Department_Post.name())){
			if(Strings.isNotBlank(id)){
				String[] ids = id.split(V3xOrgEntity.ROLE_ID_DELIMITER);
				if(ids.length>1)
				return Long.valueOf(ids[1]);
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		OrgTypeIdBO other = (OrgTypeIdBO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
    
    
    
}
