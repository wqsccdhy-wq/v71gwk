package com.seeyon.ctp.organization.inexportutil.datatableobj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ctpenumnew.EnumNameEnum;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.metadata.manager.MetadataManager;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.inexportutil.DataObject;
import com.seeyon.ctp.organization.inexportutil.DataUtil;
import com.seeyon.ctp.organization.inexportutil.inf.IImexPort;
import com.seeyon.ctp.organization.inexportutil.msg.MsgContants;
import com.seeyon.ctp.organization.inexportutil.pojo.ImpExpPojo;
import com.seeyon.ctp.organization.inexportutil.pojo.ImpExpPost;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.services.OrganizationServices;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.Strings;
/**
 * 
 * @author kyt
 * @author Bean
 *
 */
public class PostOpr extends AbstractImpOpr implements IImexPort {   
	protected EnumManager  enumManager;
	private static String regexName=".*[\'\\f\\n\\r\\t\\v\\|><:*?%$|]{1,}.*";
	private static String regexCode=".*[-!@#$%><^\";*()\'&_+]{1,}.*";
	private static Long maxNum = 99999L;
    protected EnumManager getEnumManager(){
        if(enumManager == null){
        	enumManager = (EnumManager)AppContext.getBean("enumManagerNew");
        }
        
        return enumManager;
    }
	
	public String[] getFixedField(HttpServletRequest request){
		
//		导出excel文件的国际化
		String state_Enabled = ResourceUtil.getString("org.account_form.enable.use");
		String post_name = ResourceUtil.getString("org.post_form.name");
		String post_type = ResourceUtil.getString("org.post_form.type");
		String post_code = ResourceUtil.getString("org.post_form.type.code");
		String post_sortId = ResourceUtil.getString("org.post_form.type.sort");
		String post_account = ResourceUtil.getString("org.account.lable");
		String post_description = ResourceUtil.getString("org.post_form.description");
		String company_createDate = ResourceUtil.getString("org.account_form.createdtime.label");
		String company_updateDate = ResourceUtil.getString("org.account_form.updatetime.label");
		
		String []fieldname={
			"name:"+post_name+":name",
			"code:"+post_code+":code",
			"enable:"+state_Enabled+":enable",
			"type:"+post_type+":type",
			"sort_id:"+post_sortId+":sort",
			"create_time:"+company_createDate+":create",
			"update_time:"+company_updateDate+":update",
			"desciption:"+post_description+":ciption",
			"org_account_id:"+post_account+":accountid"
	};	
		return fieldname;
	}

	public List matchLanguagefield(List statrlst,HttpServletRequest request) throws Exception {
		for(int i=0;i<statrlst.size();i++){
			DataObject dao = (DataObject)statrlst.get(i);
			boolean flag = false;
			String[] fieldname = getFixedField(request);
			for(int j=0;j<fieldname.length;j++){
				String field[] = fieldname[j].split(":");
				if(dao.getFieldName().equalsIgnoreCase(field[0])){
					dao.setMatchCHNName(field[1]);
					dao.setMatchENGName(field[2]);
					flag = true;
				}
			}
			if(!flag){
				dao.setMatchCHNName("");
			}
		}
		return statrlst;
	}

	public void validateData(List volst) throws Exception {
		for(int i=0;i<volst.size();i++){
			V3xOrgPost voa = (V3xOrgPost)volst.get(i);
			
			if(Strings.isNotBlank(voa.getName())) {
			    if(voa.getName().length()>85) {
			        throw new Exception(ResourceUtil.getString("import.validate.check13",voa.getName(),85));
			    }
			}
			if(Strings.isNotBlank(voa.getCode())) {
				if(voa.getCode().length()>20) {
					throw new Exception(ResourceUtil.getString("import.validate.check15",voa.getName(),voa.getCode()));
				}
			}
			
            //岗位下如果有人员,则不能停用
			if(voa.getId()!=null){
				OrgManager om = (OrgManager) AppContext.getBean("orgManager");
				V3xOrgPost oldPost = om.getPostById(voa.getId());
				List<V3xOrgMember> _tempMemberList = om.getMembersByPost(voa.getId());
				if (!voa.getEnabled() && oldPost.getEnabled() && _tempMemberList != null && _tempMemberList.size() > 0) {
					throw new Exception(ResourceUtil.getString("import.validate.check14",voa.getName()));
				}
			}
            
		}
	}

	public V3xOrgEntity getVO() {
		// TODO Auto-generated method stub
		return new V3xOrgPost();
	}

	public List assignVO(OrgManager od,MetadataManager metadataManager,Long accountid,List<List<String>> accountList,List volst) throws Exception{
		List returnlst = new ArrayList();		
		for(int i = 2 ; i < accountList.size() ; i++){
			V3xOrgPost voa = new V3xOrgPost();
			List valuelst = accountList.get(i);
			Method med [] =  voa.getClass().getMethods();
			if(DataUtil.isNotNullValue(valuelst)){
			for(int j=0;j<med.length;j++){
				Method mdd = med [j];
				if(mdd.getName().indexOf("set") != -1){
					//logger.info("mdd.getName()="+mdd.getName());
					for(int m=0;m<volst.size();m++){
						DataObject dao = (DataObject)volst.get(m);
						if(mdd.getName().toLowerCase().indexOf(DataUtil.submark(dao.getFieldName()).toLowerCase()) == 3){
							if(dao.getColumnnum() != -1){
								Class cl[] = mdd.getParameterTypes();
								if("java.lang.Integer".equals(cl[0].getName())){
									//logger.info("java.lang.Integer");
									if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{new Integer(valuelst.get(dao.getColumnnum()).toString())});
									}else{
										mdd.invoke(voa, new Object[]{Integer.valueOf(0)});
									}
								}else if("java.util.Date".equals(cl[0].getName())){
									if("".equals(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{Datetimes.getTodayFirstTime()});
									}else if(valuelst.get(dao.getColumnnum()).toString().trim().length() == 10){
										mdd.invoke(voa, new Object[]{Datetimes.parse(valuelst.get(dao.getColumnnum()).toString().trim()+" 00:00:00", "yyyy-MM-dd HH:mm:ss")});
									}else{
										mdd.invoke(voa, new Object[]{Datetimes.parse(valuelst.get(dao.getColumnnum()).toString(), "yyyy-MM-dd")});
									}
								}else if("java.lang.Boolean".equals(cl[0].getName())){
									mdd.invoke(voa, new Object[]{Boolean.valueOf(valuelst.get(dao.getColumnnum()).toString())});
								}else if("java.lang.Long".equals(cl[0].getName())){
									//logger.info("java.lang.Long");
									if(DataUtil.submark(dao.getFieldName())
											.toLowerCase()
											.indexOf("type") != -1){//post type
										java.lang.Long  pt=getPostType(metadataManager,
												valuelst.get(dao.getColumnnum()).toString());
										mdd.invoke(
												voa, new Object[]{pt});
										
									}else  if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{Long.valueOf(valuelst.get(dao.getColumnnum()).toString())});
									}else{
										mdd.invoke(voa, new Object[]{Long.valueOf(0)});
									}			
								}else if("int".equals(cl[0].getName())){
									if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(voa, new Object[]{Integer.valueOf(valuelst.get(dao.getColumnnum()).toString()).intValue()});
									}else{
										mdd.invoke(voa, new Object[]{0});
									}			
								}else if("java.lang.Byte".equals(cl[0].getName())){
									if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
										mdd.invoke(
												voa, new Object[]{
														Byte.valueOf(valuelst.get(dao.getColumnnum()).toString()).intValue()});
									}else{
										mdd.invoke(voa, new Object[]{Byte.valueOf("1")});
									}			
								}else if("com.seeyon.v3x.organization.domain.V3xOrgAccount".equals(cl[0].getName())){
									V3xOrgAccount vox = new V3xOrgAccount();
									vox.setName(valuelst.get(dao.getColumnnum()).toString());
									mdd.invoke(voa, new Object[]{vox});		
								}else{
									mdd.invoke(voa, new Object[]{valuelst.get(dao.getColumnnum())});
								}
							}
						}
					}
				}
			}
			returnlst.add(voa);
			}
		}
		return returnlst;
	}
	
	//获取岗位类别列表   
	HashMap<String,String> itemMap=null;
	void initItemMap(MetadataManager metadataManager){
		List<CtpEnumItem> itemList = getEnumManager().getEnumItems(EnumNameEnum.organization_post_types);
		//TODO
//		List<MetadataItem> itemList = metadataManager.getMetadataItems("organization_post_types");
		this.itemMap = new HashMap<String,String>();
		//String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		
		for(CtpEnumItem item : itemList){
			this.itemMap.put(item.getLabel(), item.getValue());
			//logger.info("item.getLabel()="+item.getLabel());
			//logger.info("item.getValue()="+item.getValue());
			String lableC = ResourceUtil.getString(item.getLabel());			
			//logger.info("lableC="+lableC);
			if(Strings.isNotBlank(lableC) && Strings.equals(item.getState(), 1)){
				this.itemMap.put(lableC, item.getValue());
			}
			
//			String lableE = ResourceUtil.getString(resource, Locale.ENGLISH, item.getLabel());
//			//logger.info("lableE="+lableE);
//			if(!lableE.equals("")){
//				this.itemMap.put(lableE, item.getValue());
//			}
		}
	}

    java.lang.Long getPostType(MetadataManager metadataManager, String typename) {
        if (metadataManager == null || !StringUtils.hasText(typename))
            return Long.valueOf(-1);
        ////获取岗位类别列表
        //List<MetadataItem> itemList = metadataManager.getMetadataItems("organization_post_types");
        //		List<CtpEnumItem> itemList = getEnumManager().getEnumItems(EnumNameEnum.organization_post_types);
        //		
        //		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
        //		Set<String> keys=ResourceUtil.getKeys(resource, typename);
        //		Iterator<String> it=keys.iterator();
        //		while(it.hasNext()){
        //			String key=it.next();
        //			CtpEnumItem mis=getEnumManager().getEnumItem(EnumNameEnum.organization_post_types, key);
        //			if(mis==null)
        //				continue;
        //			
        //			CtpEnumItem mi=mis;
        //			try{
        //				return Long.valueOf((
        //						mi.getValue()));
        //			}catch(Exception e){
        //				continue;
        //			}
        //		}
        //		

        try {
            //String v ="1";

            if (itemMap == null)
                this.initItemMap(metadataManager);

            String v = itemMap.get(typename);

            logger.info(typename + " type=" + v);
            if (StringUtils.hasText(v)) {
                return Long.valueOf(v);
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
        return Long.valueOf(-1);
    }
	
	private V3xOrgPost doRemove(V3xOrgPost voa,List inList){
		for(int j=0;j<inList.size();j++){
			V3xOrgPost v3oavo = (V3xOrgPost)inList.get(j);
			if(v3oavo.getName().equals(voa.getName())){
				return v3oavo;
			}
		}
		return null;
	}
	
	public Map devVO(OrgManager od,List volst) throws Exception{
		List v3xorgaccountvolst = od.getAllPosts(((V3xOrgPost)volst.get(0)).getOrgAccountId());
		List newlst = new ArrayList();
		//重复的
		List duplst = new ArrayList();
		newlst.addAll(volst);
		//这段有时间再改进
		
		int i=0;
        V3xOrgPost ftempobj;
        while (i < newlst.size()) {
            V3xOrgPost voa = (V3xOrgPost) newlst.get(i);
            ftempobj = doRemove(voa, v3xorgaccountvolst);

            if (ftempobj != null) {
                duplst.add(ftempobj);
                newlst.remove(i);
            } else {
                i++;
            }
        }
		
		Map mp = new HashMap();
		//重复的
		mp.put("dup", duplst);
		//剩下的
		mp.put("new", newlst);
		return mp;
	}
	
    protected String getAccountName(ImpExpPojo pojo) {
        ImpExpPost p = (ImpExpPost) pojo;
        return p.getAccountName();
    }
	
    protected ImpExpPojo transToPojo(List<String> org, V3xOrgAccount voa,String onlytag) throws Exception {
        ImpExpPost iep = new ImpExpPost();

        logger.info("org.size()=" + org.size());
        if (org.size() < 4) {
            throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_FILEDATA));//ORG_IO_MSG_ERROR_FILEDATA
        }

        if (!StringUtils.hasText((String) org.get(0))){
            throw new Exception(ResourceUtil.getString(MsgContants.ORG_IO_MSG_ERROR_MUST_POSTNAME));//ORG_IO_MSG_ERROR_MUST_POSTNAME
        }
        iep.setName(org.get(0).trim());
        if((iep.getName()).matches(regexName)){//包含特殊字符
            throw new Exception(ResourceUtil.getString("imp.role.namecontainillegal", iep.getName()));
        }
        logger.info(iep.getName());

        if (!StringUtils.hasText((String) org.get(1))) {
            iep.setCode("");//"_default_code""."
        } else{
            iep.setCode(org.get(1).trim());
            if((iep.getCode()).matches(regexCode)){//包含特殊字符
                throw new Exception(ResourceUtil.getString("imp.role.codecontainillegal", iep.getCode()));
            }
        }

        if (!StringUtils.hasText((String) org.get(2))){
        	throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_POSTTYPE));//ORG_IO_MSG_ERROR_MUST_POSTTYPE
        }else{
        	String label = (String) org.get(2);
        	List<CtpEnumItem> itemList = getEnumManager().getEnumItems(EnumNameEnum.organization_post_types);
        	boolean flag = false;
        	if(Strings.isNotEmpty(itemList)){
        		for(CtpEnumItem item : itemList){
        			if((ResourceUtil.getString(item.getLabel()).equals(label) || item.getLabel().equals(label)) && item.getState()==1){
        				flag = true;
        				break;
        			}
        		}
        	}
        	if(!flag){
        		throw new Exception(ResourceUtil.getString("org.io.error.must.post.type"));
        	}
        }
        
        iep.setType(org.get(2).trim());

        if (voa.isGroup()) {//基准岗导入没有“单位”一列
            iep.setAccountName(voa.getName());

            //状态
            iep.setEnabled(org.get(3).trim());
        } else {
            if (!StringUtils.hasText((String) org.get(3)))
                throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_ACCOUNT));
            iep.setAccountName(org.get(3).trim());

            //状态
            iep.setEnabled(org.get(4).trim());
        }
        return iep;
    }

	/*
	 * 进行ImpExpPost特有的检查
	 * 
	 * @see
	 * com.seeyon.v3x.organization.inexportutil.datatableobj.AbstractImpOpr#
	 * pojoCheck(com.seeyon.v3x.organization.services.OrganizationServices,
	 * com.seeyon.v3x.common.metadata.manager.MetadataManager,
	 * com.seeyon.v3x.organization.inexportutil.pojo.ImpExpPojo)
	 */
    @Override
    protected void pojoCheck(OrganizationServices organizationServices, MetadataManager metadataManager, ImpExpPojo pojo)
            throws Exception {
        ImpExpPost postPojo = (ImpExpPost) pojo;
        if (this.getPostType(metadataManager, postPojo.getType()) < 0) {
            throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_POSTTYPE));
        }
    }

    protected V3xOrgEntity existEntity(OrganizationServices organizationServices, ImpExpPojo pojo, V3xOrgAccount voa, String onlytag)
            throws Exception {
        //post  按CODE匹配比较好  "code",iep.getCode()   因为校验重名是好用, "name", iep.getName()
        ImpExpPost iep = (ImpExpPost) pojo;
        OrgManagerDirect omd = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
        List<V3xOrgPost> pms =omd.getAllPosts(voa.getOrgAccountId(), true); 
        V3xOrgPost pc = null;
        for(V3xOrgEntity pm:pms){
            if(iep.getName().equals(pm.getName())){
                pc = (V3xOrgPost) pm;
                return pc;
            }
        }
        
        return pc;
    }
	
    protected V3xOrgEntity copyToEntity(OrganizationServices organizationServices, MetadataManager metadataManager,
            ImpExpPojo pojo, V3xOrgEntity ent, V3xOrgAccount voa) throws Exception {
        return copyToPost(organizationServices, metadataManager, (ImpExpPost) pojo, (V3xOrgPost) ent, voa);
    }

    protected V3xOrgEntity copyToPost(OrganizationServices organizationServices, MetadataManager metadataManager,
            ImpExpPost pojo, V3xOrgPost ent, V3xOrgAccount voa) throws Exception {
        if (pojo == null)
            throw new Exception("null ImpExpPost object to cover to V3xOrgPost object");

        V3xOrgPost vop = null;
        if (ent != null) {
            vop = organizationServices.getOrgManager().getPostById(ent.getId());
        }
        if (vop == null) {
            vop = new V3xOrgPost();
        }

        vop.setName(pojo.getName());
        vop.setCode(pojo.getCode());
        vop.setOrgAccountId(voa.getId());
        String enable = "启用,啟用,Enabled";
        String disable = "停用,停用,Disabled";
        boolean isEnable = false;
        if(enable.indexOf(pojo.getEnabled())>=0){
            isEnable = true;
        }else if(disable.indexOf(pojo.getEnabled())>=0){
            isEnable = false;
        }else{
            throw new Exception(ResourceUtil.getString("org.io.error.must.post.stateincorrect"));
        }
        vop.setEnabled(isEnable);

        long typeid = this.getPostType(metadataManager, pojo.getType());
        logger.info("typeid=" + typeid);
        if (typeid < 0) {
            typeid = 1;
        }
        vop.setTypeId(typeid);

        return vop;
    }
	
    protected void add(OrganizationServices organizationServices, V3xOrgEntity ent,ImpExpPojo pj) throws Exception {
        OrgManagerDirect omd = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
        Long sortNum = Long.valueOf(omd.getMaxSortNum(V3xOrgPost.class.getSimpleName(), ent.getOrgAccountId()) + 1L);
        if(sortNum.compareTo(maxNum)>0){
        	sortNum = maxNum;
        }
        ent.setSortId(sortNum);
        logger.info("add post=" + ent.getName());
        omd.addPost((V3xOrgPost) ent);
        logger.info("ok add post=" + ent.getName());
    }

    protected void update(OrganizationServices organizationServices, V3xOrgEntity ent, ImpExpPojo pj) throws Exception {
        OrgManagerDirect omd = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
        OrgManager om = (OrgManager) AppContext.getBean("orgManager");
        List<V3xOrgRelationship> reflist = om.getV3xOrgRelationship(OrgConstants.RelationshipType.Banchmark_Post, Long.valueOf(ent.getId().toString()), ent.getOrgAccountId(), null);
        if(reflist!=null && reflist.size()>0){
            throw new Exception(ResourceUtil.getString("org.io.error.must.post.cannotBanchmark"));
        }
        logger.info("update post=" + ent.getName());
        omd.updatePost((V3xOrgPost) ent);
        logger.info("ok update post=" + ent.getName());
    }

    protected String msg4AddNoDouble(ImpExpPojo pj) {
        return this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_POSTNAME) + pj.getName();
    }
	
}//end class
