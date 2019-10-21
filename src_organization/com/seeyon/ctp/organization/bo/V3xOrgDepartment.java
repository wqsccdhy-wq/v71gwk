package com.seeyon.ctp.organization.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.util.Strings;

/**
 * The persistent class for the v3x_org_department database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xOrgDepartment extends V3xOrgUnit implements Serializable {
	
//	protected SpaceManager  spaceManager;
//    protected SpaceManager getSpaceManager(){
//        if(spaceManager == null){
//        	spaceManager = (SpaceManager)AppContext.getBean("spaceManager");
//        }
//        
//        return spaceManager;
//    }
    private boolean isCreateDeptSpace = false;
    private int level;
    //存放外单位属性自定义字段的list
  	private List<String> customerProperties=new ArrayList<String>();

    /**
     * 这两个属性都是为了portal创建部门空间，其他模块不要调用
     */
    /** 部门管理员 */
    private String DepAdmin = null;
    /** 部门主管 */
    private String DepManager = null;
    public String getDepManager() {
        return DepManager;
    }

    public void setDepManager(String depManager) {
        DepManager = depManager;
    }
	public String getDepAdmin() {
		return DepAdmin;
	}

	public void setDepAdmin(String depAdmin) {
		DepAdmin = depAdmin;
	}
	private static final long serialVersionUID = -6361706889762469209L;
	
	private final static Log   logger    = LogFactory.getLog(V3xOrgDepartment.class);

	/**
	 * 复制传入的实体的属性值到Department的实例。
	 */
	public V3xOrgDepartment(V3xOrgDepartment ent) {
		super((V3xOrgUnit)ent);
		this.liushuihao = ent.makeLiushuihao();
	}

	public V3xOrgDepartment() {
	    super();
	    this.setType(OrgConstants.UnitType.Department);
    }
	
	public V3xOrgDepartment(OrgUnit orgUnit) {
	    super(orgUnit);
	}

	public String getEntityType() {
		if(this.externalType == OrgConstants.ExternalType.Interconnect4.ordinal()){
			return OrgConstants.ORGENT_TYPE.BusinessDepartment.name();
		}else{
			return OrgConstants.ORGENT_TYPE.Department.name();
		}
	}
	
//    public boolean isCreateDeptSpace() {
//    	isCreateDeptSpace = getSpaceManager().isCreateDepartmentSpace(this.id);
//		return isCreateDeptSpace;
//	}
    public boolean CreateDeptSpace() {
    	Object isCreateDeptSpace = this.getProperty("isCreateDeptSpace");
    	if(isCreateDeptSpace == null){
    		return false;
    	}else{
    		return "1".equals(isCreateDeptSpace.toString()) ? true : false;
    	}
	}

	public void setCreateDeptSpace(boolean isCreateDeptSpace) {
		this.setProperty("isCreateDeptSpace", isCreateDeptSpace ? 1L : 0L);
		this.isCreateDeptSpace = this.CreateDeptSpace();
	}

	/**
	 * 部门层级
	 * 需要通过path算一次取出来，添加这个属性只是为了rest接口批量添加部门是存传过来的部门层级
	 * @return
	 */
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public List<String> getCustomerProperties() {
		return customerProperties;
	}

	public void setCustomerProperties(List<String> customerProperties) {
		this.customerProperties = customerProperties;
	}

	private List<Long> getPosts(){
        try {
            List<V3xOrgPost> posts = OrgHelper.getOrgManager().getDepartmentPost(this.getId());
            return (List<Long>)OrgHelper.getEntityIds(posts);
        }
        catch (Exception e) {
            logger.error("", e);
        }
        
        return Collections.EMPTY_LIST;
    }

	private List<V3xOrgPost> getV3xPosts(){
        try {
            List<V3xOrgPost> posts = OrgHelper.getOrgManager().getDepartmentPost(this.getId());
            return posts;
        }
        catch (Exception e) {
            logger.error("", e);
        }
        
        return Collections.EMPTY_LIST;
    }	
	
	   public String getWholeName() throws BusinessException{
		   try {
			   if(Strings.isBlank(this.path)){
				   return this.name;
			   }
			   StringBuilder sb = new StringBuilder();
			   String wholeName = "";
			   V3xOrgAccount currentAccont = OrgHelper.getOrgManager().getAccountById(this.getOrgAccountId());
			   String accountPath =currentAccont.getPath();
			   
			   int deptDeep = (this.getPath().length()-accountPath.length())/4;
			   if(deptDeep<0){
				   return this.name;
			   }
			   
			   V3xOrgDepartment tempDept = this;
			   //部门名称(各级别)
			   String[] deptsName = new String[deptDeep];
			   for (int len =deptDeep; len>0; len--) {
				   if(len<deptDeep && tempDept!=null){
					   V3xOrgDepartment parentDept= OrgHelper.getOrgManager().getDepartmentById(tempDept.getSuperior());
					   if(parentDept == null){
						   logger.info("上级部门丢失："+ this.name);
						   return this.name;
					   }
					   deptsName[len-1]= parentDept==null ? "" : parentDept.getName();
					   tempDept=parentDept;
				   }else{
					   deptsName[len-1]=this.getName();
				   }
			   }
			   for(int i=0;i<deptDeep;i++){
				   sb.append(deptsName[i]).append(',');
			   }
			   wholeName = sb.toString();
			   return wholeName.substring(0, wholeName.length()-1);
			} catch (Exception e) {
				logger.error("",e);
			}
		   return this.name;
	    }

	/**
	 * 给选人界面用的，不要轻易修改
	 */
	public void toJsonString(StringBuilder o, OrgManager orgManager) {
	    this.toJsonString(o, orgManager, null);
	}
	public void toJsonString(StringBuilder o, OrgManager orgManager, Map<Long, List<V3xOrgPost>> accountPosts) {
		o.append("{");
		o.append(TOXML_PROPERTY_id).append(":'").append(this.getId()).append("'");
		o.append(",P:'").append(this.getPath()).append("'");
		o.append(",H:").append(this.makeLiushuihao());
		
		if(!this.getIsInternal()){
			o.append(",").append(TOXML_PROPERTY_isInternal).append(":").append(0);
		}
		
		o.append(",").append(TOXML_PROPERTY_externalType).append(":'").append(this.getExternalType()).append("'");
		o.append(",").append(TOXML_PROPERTY_NAME).append(":'").append(Strings.escapeJavascript(this.getName())).append("'");
		
		List<V3xOrgPost> posts = new ArrayList<V3xOrgPost>();
		if(accountPosts!=null){
		    posts = accountPosts.get(this.getId());
		}
        
        if(posts != null && !posts.isEmpty()){
            int i = 0;
            o.append(",S:[");
            for (V3xOrgPost post : posts) {
                if(post == null || !post.isValid()){
                    continue;
                }
                if(i++ != 0){
                    o.append(",");
                }
                o.append(post.makeLiushuihao());
            }
            o.append("]");
        }
		
		o.append("}");
	}
	
	private transient int liushuihao = 0;
	
	private static int maxliushui = 0;
	private static Object lock = new Object();
	public int makeLiushuihao(){
	    if(this.liushuihao == 0){
	        synchronized(lock){
	            if(this.liushuihao == 0){
	                liushuihao = ++maxliushui;
		}
	            return liushuihao;
	        }
	    }
		return liushuihao;
	}
	
	/**
	 * 是否包含子部门
	 * @param includeExternal 是否考虑外部部门
	 * @return
	 * @throws BusinessException
	 */
	public boolean hasChildren(boolean includeExternal) throws BusinessException{
		return OrgHelper.getOrgManager().hasChildren(this.getId(), includeExternal);
	}
	
}