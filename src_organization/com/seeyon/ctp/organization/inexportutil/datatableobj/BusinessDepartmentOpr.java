package com.seeyon.ctp.organization.inexportutil.datatableobj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import com.seeyon.apps.businessorganization.manager.BusinessDepartmentManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.metadata.manager.MetadataManager;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.inexportutil.DataObject;
import com.seeyon.ctp.organization.inexportutil.DataUtil;
import com.seeyon.ctp.organization.inexportutil.ResultObject;
import com.seeyon.ctp.organization.inexportutil.inf.IImexPort;
import com.seeyon.ctp.organization.inexportutil.msg.MsgContants;
import com.seeyon.ctp.organization.inexportutil.pojo.ImpExpDepartment;
import com.seeyon.ctp.organization.inexportutil.pojo.ImpExpPojo;
import com.seeyon.ctp.organization.manager.BusinessOrgManagerDirect;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
//import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.services.OrganizationServices;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.Strings;

public class BusinessDepartmentOpr extends AbstractImpOpr implements IImexPort {   
    protected EnumManager  enumManager;
    private  Integer level = null;
    private Integer count = 0;
    private Set<Long> unsuccessDept = new HashSet<Long>();
    private  ImpExpDepartment pre = null;
    private List<String> roleNameList = new ArrayList<String>();
    private static String regex=".*['\"%|,/\\\\]{1,}.*";
    protected EnumManager getEnumManager(){
        if(enumManager == null){
            enumManager = (EnumManager)AppContext.getBean("enumManagerNew");
        }
        
        return enumManager;
    }
    
    public String[] getFixedField(HttpServletRequest request){
        
        //导出excel文件的国际化
        String dep_Name = ResourceUtil.getString("org.dept.name");
        String dep_grade = ResourceUtil.getString("org.dept.level");
        String dep_desc = ResourceUtil.getString("common.description.label");
        String createDate = ResourceUtil.getString("org.account_form.createdtime.label");
        String updateDate = ResourceUtil.getString("org.account_form.updatetime.label");
        String state_Enabled = ResourceUtil.getString("org.account_form.enable.use");
        String accountId = ResourceUtil.getString("org.account.lable");
        
        
        String []fieldname={
            "name:"+dep_Name+":name",
            "code:"+dep_grade+":code",
            "enable:"+state_Enabled+":enable",
            "create_time:"+createDate+":create",
            "update_time:"+updateDate+":update",
            "desciption:"+dep_desc+":ciption",
            "org_account_id:"+accountId+":accountid"
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
    	BusinessOrgManagerDirect businessOrgManagerDirect = (BusinessOrgManagerDirect) AppContext.getBean("businessOrgManagerDirect");
    	OrgManagerDirect orgManagerDirect = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
        List<V3xOrgEntity> departments = orgManagerDirect.getEntityNoRelationDirect(V3xOrgDepartment.class.getSimpleName(), null, null, null,((V3xOrgDepartment) volst.get(0)).getOrgAccountId());
        Map<Long,V3xOrgDepartment> idMap = new HashMap<Long, V3xOrgDepartment>();//单位下的内部部门，包含停用
        Map<String,Long> codeMap = new HashMap<String, Long>();//单位下的内部部门，包含停用
        for(int i = 0; i<departments.size(); i++){
        	V3xOrgDepartment dept = (V3xOrgDepartment)departments.get(i);
        	if(!dept.getIsInternal()){
        		continue;
        	}
        	idMap.put(dept.getId(), dept);
        	if(Strings.isNotBlank(dept.getCode())){
        		codeMap.put(dept.getCode(), dept.getId());
        	}
        }
        
        for (int i = 0; i < volst.size(); i++) {
        	//校验编码重复
        	V3xOrgDepartment voa = (V3xOrgDepartment) volst.get(i);
        	if (idMap.containsKey(voa.getId())) {
        		if(!idMap.get(voa.getId()).getEnabled()){
        			throw new Exception(ResourceUtil.getString("import.department.validate.check2"));
        		}
        	}
        	
        	if (Strings.isNotBlank(voa.getCode())) {
        		if (codeMap.containsKey(voa.getCode())) {
        			if(!voa.getId().equals(codeMap.get(voa.getCode()))){
        				throw new Exception(ResourceUtil.getString("import.department.validate.check1"));
        			}
        		}
        	}
        	
            if (Strings.isNotEmpty(voa.getName())) {
                String reg = "^.*[\'\\/|><:*?&%$].*$";
                if(voa.getName().matches(reg)) {
                    throw new Exception(ResourceUtil.getString("import.validate.check22", voa.getName(), ResourceUtil.getString("org.dept.name")));
                }
            }
        	
        }
            
    }

    public V3xOrgEntity getVO() {
        // TODO Auto-generated method stub
        return new V3xOrgDepartment();
    }

    public List assignVO(OrgManager od,MetadataManager metadataManager,Long accountid,List<List<String>> accountList,List volst) throws Exception{
        List returnlst = new ArrayList();       
        for(int i = 2 ; i < accountList.size() ; i++){
            V3xOrgDepartment voa = new V3xOrgDepartment();
            List valuelst = accountList.get(i);
            Method med [] =  voa.getClass().getMethods();
            if(DataUtil.isNotNullValue(valuelst)){
            for(int j=0;j<med.length;j++){
                Method mdd = med [j];
                if(mdd.getName().indexOf("set") != -1){
                    for(int m=0;m<volst.size();m++){
                        DataObject dao = (DataObject)volst.get(m);
                        if(mdd.getName().toLowerCase().indexOf(DataUtil.submark(dao.getFieldName()).toLowerCase()) == 3){
                            if(dao.getColumnnum() != -1){
                                Class cl[] = mdd.getParameterTypes();
                                if("java.lang.Integer".equals(cl[0].getName())){
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
                                    if(DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())){
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
        
    private V3xOrgDepartment doRemove(V3xOrgDepartment voa,List inList){
        for(int j=0;j<inList.size();j++){
            V3xOrgDepartment v3oavo = (V3xOrgDepartment)inList.get(j);
            if(v3oavo.getName().equals(voa.getName())){
                return v3oavo;
            }
        }
        return null;
    }
    
    public Map devVO(OrgManager od,List volst) throws Exception{
        List v3xorgaccountvolst = od.getAllPosts(((V3xOrgDepartment)volst.get(0)).getOrgAccountId());
        List newlst = new ArrayList();
        //重复的
        List duplst = new ArrayList();
        newlst.addAll(volst);
        //这段有时间再改进
        
        int i=0;
        V3xOrgDepartment ftempobj;
        while (i < newlst.size()) {
            V3xOrgDepartment voa = (V3xOrgDepartment) newlst.get(i);
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
        ImpExpDepartment p = (ImpExpDepartment) pojo;
        return p.getAccountName();
    }
    
    protected ImpExpPojo transToPojo(List<String> org, V3xOrgAccount voa,String onlytag) throws Exception {
        ImpExpDepartment iep = new ImpExpDepartment();
        String tempPredeptName = "";
        int size = org.size()-1;
        if(size<=1 || count<1){
            count++;
            return null;
        }
        Integer thisLevel = 1;
        logger.info("org.size()=" + size);
        //设定单行长度导入时是可以变化的
        if (size < 4) {
        	throw new Exception(ResourceUtil.getString(MsgContants.ORG_IO_MSG_ERROR_MUST_DEPTLEVELNUM));
        }
        //考虑客户自己修改了文件模板?
        if(level==null){
            for(int i=0;i<size;i++){
                String temp = org.get(i);
                if(temp == null){
                    break;
                }
                String name = temp.trim();
                if("部门编码".equals(name) || "部門編碼".equals(name) || "Code".equals(name)){
                    level =i;
                    break;
                }
            }
            if(level==null){
                return null;
            }
            for(int j=level+4;j<size;j++){
                roleNameList.add(org.get(j).trim());
            }
            return null;
        }
        
        //部门代码
        if (StringUtils.hasText((String) org.get(level))) {
            String code =org.get(level);
            iep.setCode(code.length()>50?code.substring(0, 50):code);
        }else if("code".equals(onlytag)){
        	throw new Exception(ResourceUtil.getString("import.department.validate.check6"));
        }
        
        //部门级次
        int MUST_DEPTLEVELNUM = 0;
        if (StringUtils.hasText((String) org.get(level+1))) {
            try{
                thisLevel =Integer.valueOf(org.get(level+1));
                if(thisLevel<1){
                    MUST_DEPTLEVELNUM=1;
                }
                iep.setLevel(thisLevel);
            }catch(NumberFormatException e){
                MUST_DEPTLEVELNUM=1;
            }
        } else{
            MUST_DEPTLEVELNUM=1;
        }
        if(MUST_DEPTLEVELNUM!=0){
            throw new Exception(ResourceUtil.getString(MsgContants.ORG_IO_MSG_ERROR_MUST_DEPTLEVELNUM));
        }
        
        //排序号
        int MUST_DEPTSORTNUM = 0;
        if (StringUtils.hasText((String) org.get(level+2))) {
            try{
                Integer sortId =Integer.valueOf(org.get(level+2));
                if(sortId<0){
                    MUST_DEPTSORTNUM =1;
                }
                iep.setSortId(sortId.toString());
            }catch(NumberFormatException e){
                MUST_DEPTSORTNUM =1;
            }
        } else{
            MUST_DEPTSORTNUM =1;
        }
        if(MUST_DEPTSORTNUM!=0){
            throw new Exception(ResourceUtil.getString(MsgContants.ORG_IO_MSG_ERROR_MUST_DEPTSORTNUM));
        }
        
        //描述
        if (StringUtils.hasText((String) org.get(level+3))){
            iep.setDescription(org.get(level+3).trim());
            if(iep.getDescription().length()>200) {//描述太长
                throw new Exception(ResourceUtil.getString("imp.role.descriptiontoolong"));
            }
        }
        
        //部门名称
        if (!StringUtils.hasText((String) org.get(thisLevel-1))){
        	String errorName = org.get(0) + "...";
            org.set(0, ("".equals(iep.getName())|| iep.getName()==null)?"":iep.getName());
            throw new Exception("Error|"+errorName+"|"+ResourceUtil.getString("import.department.validate.check5"));
        }
        iep.setName((String) org.get(thisLevel-1));
        //當前名稱
        StringBuilder sbThisName = new StringBuilder();
        StringBuilder sbThisNameNoplus = new StringBuilder();
        //前一個部門名稱
        StringBuilder sbPreName = new StringBuilder();
        
        for(int lev=0;lev<thisLevel-1;lev++){
            if (!StringUtils.hasText((String) org.get(lev))){
                org.set(0, ("".equals(iep.getName())|| iep.getName()==null)?"null":iep.getName());
                throw new Exception(ResourceUtil.getString(MsgContants.ORG_IO_MSG_ERROR_RIGHT_DEPTNAME));
            }
            sbThisName.append(org.get(lev)+",");
            sbThisNameNoplus.append(org.get(lev));
        }
        tempPredeptName = sbThisName.toString();
        iep.setWholeName(tempPredeptName+iep.getName());
        if(Strings.isNotBlank(iep.getName())) {
            if(iep.getName().length()>100) {//部门名称太长
                throw new Exception(ResourceUtil.getString("imp.role.deptnametoolong", iep.getName()));
            }
            if((sbThisNameNoplus+iep.getName()).matches(regex)){//包含特殊字符
                throw new Exception(ResourceUtil.getString("imp.role.deptnamecontainillegal", iep.getWholeName()));
            }
        }
        if(pre!=null){
            String[] deptNames = pre.getWholeName().split(",");
            for(int lev=0;lev<thisLevel-1&&lev<deptNames.length;lev++){
                sbPreName.append(deptNames[lev]+",");
            }
            if(!sbPreName.toString().equals(tempPredeptName)){
                throw new Exception("《"+iep.getWholeName()+"》"+ResourceUtil.getString("dept.name.notsameserial")+"《"+sbPreName.toString()+"》");
            }
            if(iep.getWholeName().equals(pre.getWholeName())){
                throw new Exception(iep.getWholeName()+ResourceUtil.getString("dept.name.repeat"));
            }
        }
        if(pre == null && iep.getLevel()!=1){
            throw new Exception("Error|"+iep.getName()+"|"+ResourceUtil.getString("import.department.validate.check4"));
        }
        
        //所属单位
        iep.setAccountName(voa.getName());
        
        if(pre!=null){
            ImpExpDepartment temp =pre;
            while(temp!=null){
                if((temp.getLevel()-iep.getLevel())==-1){
                    iep.setPre(temp);
                    break;
                }
                temp=temp.getPre();
            }
        }
        pre = iep;
        
        //部门角色
        Map<Long,List<String>> roleMembers =new HashMap<Long,List<String>>();
        List<String> noExistRoles = new ArrayList<String>();
        List<String> dupExistRoles = new ArrayList<String>();
        List<V3xOrgRole> rolelist = OrgHelper.getOrgManager().getAllDepRoles(voa.getOrgAccountId());
        Map<String,V3xOrgRole> roleNameToRole = new HashMap<String,V3xOrgRole>();
        for(V3xOrgRole r:rolelist){
            roleNameToRole.put(r.getShowName(), r);
        }
        for(int rolesize=0;rolesize<size-level-4;rolesize++){
            if(rolesize<roleNameList.size()){
                String roleName =roleNameList.get(rolesize);
                V3xOrgRole tempRole = roleNameToRole.get(roleName);
                String memberNames = org.get(level+4+rolesize).trim();
                String[] nameArray = memberNames.split("、");
                if(tempRole==null){
                    noExistRoles.add(roleName);
                }else{
                    if(roleMembers.get(tempRole.getId())!=null){
                        dupExistRoles.add(roleName);
                    }else{
                        roleMembers.put(tempRole.getId(), Arrays.asList(nameArray));
                    }
                }
            }else{
                logger.error("rolesize 超长过列头长度了"+rolesize+" : "+size);
            }
        }
        iep.setRoleMembers(roleMembers);
        iep.setDupExistRole(dupExistRoles);
        iep.setNotExistRole(noExistRoles);
        return iep;
    }

    protected V3xOrgEntity existEntity(OrganizationServices organizationServices, ImpExpPojo pojo, V3xOrgAccount voa, String onlytag)
            throws Exception {
        ImpExpDepartment iep = (ImpExpDepartment) pojo;
        if("name".equals(onlytag) && Strings.isNotBlank(iep.getWholeName())){
        	if(departmentFullpathMap.containsKey(iep.getWholeName().replace(",", "/"))){
        		return departmentFullpathMap.get(iep.getWholeName().replace(",", "/"));
        	}
        	
        	if(disableDepartmentFullpathMap.containsKey(iep.getWholeName().replace(",", "/"))){
        		return disableDepartmentFullpathMap.get(iep.getWholeName().replace(",", "/"));
        	}
        }else if("code".equals(onlytag) && Strings.isNotBlank(iep.getCode())){
        	if(departmentCodeMap.containsKey(iep.getCode())){
        		return departmentCodeMap.get(iep.getCode());
        	}
        	
        	if(disableDepartmentCodeMap.containsKey(iep.getCode())){
        		return disableDepartmentCodeMap.get(iep.getCode());
        	}
        }
        return null;
    }
    
    protected V3xOrgEntity copyToEntity(OrganizationServices organizationServices, MetadataManager metadataManager,
            ImpExpPojo pojo, V3xOrgEntity ent, V3xOrgAccount voa) throws Exception {
        return copyToDepartment(organizationServices, metadataManager, (ImpExpDepartment) pojo, (V3xOrgDepartment) ent, voa);
    }

    protected V3xOrgEntity copyToDepartment(OrganizationServices organizationServices, MetadataManager metadataManager,
            ImpExpDepartment pojo, V3xOrgDepartment ent, V3xOrgAccount voa) throws Exception {
        if (pojo == null)
            throw new Exception("null ImpExpPost object to cover to V3xOrgDepartment object");

        V3xOrgDepartment vod = null;
        if (ent != null) {
            vod = organizationServices.getOrgManager().getDepartmentById(ent.getId());
        }
        if (vod == null) {
            vod = new V3xOrgDepartment();
            vod.setIdIfNew();
        }
        pojo.setId(vod.getId());
        vod.setName(pojo.getName());
        vod.setCode(pojo.getCode());
        vod.setSortId(Long.valueOf(pojo.getSortId()));
        vod.setDescription(pojo.getDescription());
        vod.setOrgAccountId(voa.getId());
        ImpExpDepartment pre = (ImpExpDepartment) pojo.getPre();
        Long superiorId = pre!=null ? (pojo.getPre().getId() == null ? null : Long.valueOf(pojo.getPre().getId())) : voa.getId();
        vod.setSuperior(superiorId);

        return vod;
    }
    
    protected void add(OrganizationServices organizationServices, V3xOrgEntity ent,ImpExpPojo pj) throws Exception {
        OrgManagerDirect omd = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
        BusinessOrgManagerDirect businessOrgManagerDirect = (BusinessOrgManagerDirect) AppContext.getBean("businessOrgManagerDirect");
        V3xOrgDepartment dept = (V3xOrgDepartment)ent;
        if(ent.getSortId()==null){
            ent.setSortId(Long.valueOf(omd.getMaxSortNum(V3xOrgDepartment.class.getSimpleName(), ent.getOrgAccountId()) + 1L));
        }
        logger.info("add department=" + ent.getName());
        OrganizationMessage message = null;
        if(!unsuccessDept.contains(dept.getSuperior())){
            try{
                message = businessOrgManagerDirect.addDepartment((V3xOrgDepartment) ent);
                if(!message.isSuccess()){
            		Map result = OrgHelper.getBusinessExceptionMessage(message);
            		throw new BusinessException(result.get("msg").toString());
                }
            }catch(Exception e){
                logger.warn("error",e);
                unsuccessDept.add(ent.getId());
                throw new Exception("创建部门失败,原因：" + e.getMessage());
            }
        }else{
            unsuccessDept.add(ent.getId());
            throw new Exception("上级部门不存在");
        }
        if(message!=null && !message.isSuccess()){
            unsuccessDept.add(ent.getId());
            //OrgHelper.throwBusinessExceptionTools(message);
    		Map result = OrgHelper.getBusinessExceptionMessage(message);
    		throw new BusinessException(result.get("msg").toString());
        }else if(message!=null && message.isSuccess()){
        	dealRole(organizationServices, ent, pj);
        }
        logger.info("ok add department=" + ent.getName());
    }

    protected void update(OrganizationServices organizationServices, V3xOrgEntity ent,ImpExpPojo pj) throws Exception {
        //OrgManagerDirect omd = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
    	BusinessOrgManagerDirect businessOrgManagerDirect = (BusinessOrgManagerDirect) AppContext.getBean("businessOrgManagerDirect");
        V3xOrgDepartment dept = (V3xOrgDepartment)ent;
        logger.info("update department=" + ent.getName());
        OrganizationMessage message = null;
        if(!unsuccessDept.contains(dept.getSuperior())){
            try{
            	message = businessOrgManagerDirect.updateDepartment((V3xOrgDepartment) ent);
                if(!message.isSuccess()){
            		Map result = OrgHelper.getBusinessExceptionMessage(message);
            		throw new BusinessException(result.get("msg").toString());
                }
            }catch(Exception e){
                logger.warn("error",e);
                unsuccessDept.add(ent.getId());
                throw new Exception("更新部门失败,原因：" + e.getMessage());
            }
        }else{
            unsuccessDept.add(ent.getId());
            throw new Exception("上级部门不存在");
        }
        if(message!=null && !message.isSuccess()){
            unsuccessDept.add(ent.getId());
            //OrgHelper.throwBusinessExceptionTools(message);
    		Map result = OrgHelper.getBusinessExceptionMessage(message);
    		throw new BusinessException(result.get("msg").toString());
        }else if(message!=null && message.isSuccess()){
        	dealRole(organizationServices, ent, pj);
        }
        logger.info("ok update department=" + ent.getName());
    }
    
    
    private void dealRole(OrganizationServices organizationServices, V3xOrgEntity ent,ImpExpPojo pj) throws Exception {
        BusinessDepartmentManager bdm = (BusinessDepartmentManager) AppContext.getBean("businessDepartmentManager");
        
        logger.info("update role department=" + ent.getName());
        V3xOrgDepartment temp = (V3xOrgDepartment) ent;
        //omd.updateDepartment(temp);
        ImpExpDepartment iDept = (ImpExpDepartment)pj;
        
        Map<Long,List<String>> roleMembers = iDept.getRoleMembers();
        //不存在的角色
        List<String> noExistRoles = iDept.getNotExistRole();
        //重名的角色
        List<String> dupExistRoles = iDept.getDupExistRole();
        
        List<V3xOrgMember> memberlist = OrgHelper.getOrgManager().getMembersByDepartment(pj.getId(), false);
        //修改为获取全集团的人  万新控股集团有限公_快速需求上报单__YE201800744
        List<V3xOrgMember> memberListAccount = OrgHelper.getOrgManager().getAllMembers(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
        Map<String,Integer> memberSameNameDept = new HashMap<String,Integer>();
        Map<String,Integer> memberSameNameAcc = new HashMap<String,Integer>();
        //部门人员的map
        memberMap(memberlist,memberSameNameDept);
        //单位人员的map
        memberMap(memberListAccount,memberSameNameAcc);
        
        Map<String,String> dept= new HashMap<String,String>();//roleMembers.size()
        List<String> errorlist = new ArrayList<String>();
        //显示部门角色
        List<V3xOrgRole> rolelist = OrgHelper.getOrgManager().getAllDepRoles(temp.getOrgAccountId());
        Map<String,Long> roleName2Id = new HashMap<String,Long>();
        Set<String> dupRoleNames = new HashSet<String>();
        Map<Long,V3xOrgRole> roleIdToRole = new HashMap<Long,V3xOrgRole>();
        for(V3xOrgRole r:rolelist){
            if(roleName2Id.get(r.getShowName())==null){
                roleIdToRole.put(r.getId(), r);
                roleName2Id.put(r.getShowName(), r.getId());
            }else{
                roleIdToRole.remove(roleName2Id.get(r.getShowName()));
                dupRoleNames.add(r.getShowName());
            }
        }
        Iterator iter = roleMembers.entrySet().iterator();
        //出现在导入列表中的role
        Set<Long> roleInSet = new HashSet<Long>();
        //遍历传入的角色人员对应map
        while (iter.hasNext()) {
            StringBuilder sb = new StringBuilder();
            Map.Entry entry = (Map.Entry) iter.next(); 
            Long roleId =(Long) entry.getKey();
            List<String> rolesMembers = (List<String>)entry.getValue();
            V3xOrgRole r = roleIdToRole.get(roleId);
            int index =rolelist.indexOf(r);
            if(r!=null){
                if(rolesMembers == null){
                    continue;
                }
                roleInSet.add(r.getId());
                //部门分管领导角色用单位下的人员做判断
                if(r.getCode().equals(OrgConstants.Role_NAME.DepLeader.name())){
                    validate(errorlist, sb, r, rolesMembers, memberListAccount, memberSameNameAcc);
                //其他角色用部门下的人员做判断
                }else{
                    validate(errorlist, sb, r, rolesMembers, memberListAccount, memberSameNameAcc);
                }
                if(sb.toString().length()>20){
                    dept.put("deptrole"+index, sb.toString());
                }else{
                    //未填写的角色维持原状
                    List<V3xOrgMember> oldRoleMember = OrgHelper.getOrgManager().getMembersByRole(temp.getId(), rolelist.get(index).getId());
                    makeDeptMap(dept,oldRoleMember,index);
                }
            }
            
        }
        for(int i=0;i<rolelist.size();i++){
            Long roleId =rolelist.get(i).getId();
            if(!roleInSet.contains(roleId)){
                List<V3xOrgMember> oldRoleMember = OrgHelper.getOrgManager().getMembersByRole(temp.getId(), roleId);
                makeDeptMap(dept,oldRoleMember,i);
            }
        }
        
        bdm.dealDeptRole(dept, temp, rolelist);
        
        //报错信息生成 start
        {
            StringBuilder sb1  =new StringBuilder();
            String notEx = "";
            for(String role : noExistRoles){
                if(!Strings.isBlank(role)){
                    sb1.append(role+"、");
                }
            }
            notEx=sb1.toString();
            
            StringBuilder sb2  =new StringBuilder();
            String dupEx = "";
            for(String role : dupExistRoles){
                sb2.append(role+"、");
            }
            dupEx=sb2.toString();
            
            StringBuilder sb3  =new StringBuilder();
            String dupinEx = "";
            for(String role : dupRoleNames){
                sb3.append(role+"、");
            }
            dupinEx=sb3.toString();
            
            if(!errorlist.isEmpty() || notEx.length()>1 || dupEx.length()>1 || dupinEx.length()>1){
                StringBuilder sb  =new StringBuilder();
                String err = "";
                for(String s :errorlist){
                    sb.append(s);
                }
                err =sb.toString();
                throw new Exception(err
                        +(notEx.length()>1?notEx.substring(0, notEx.length()-1)+ResourceUtil.getString("imp.role.rolenotexist"):" ")
                        +(dupEx.length()>1?dupEx.substring(0, dupEx.length()-1)+ResourceUtil.getString("imp.role.roleduplicate"):" ")
                        +(dupinEx.length()>1?dupinEx.substring(0, dupinEx.length()-1)+ResourceUtil.getString("imp.role.roleinduplicate"):" ")
                        );
            }
        }
            //报错信息生成 end
        logger.info("ok role department=" + ent.getName());
    }

    protected String msg4AddNoDouble(ImpExpPojo pj) {
        return ResourceUtil.getString("org.io.error.doublesamefile.dept.name") + pj.getName();
    }
    
	protected String msg4AddNoDoubleCode(ImpExpPojo pj){//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_OK)
		return "导入文件中已经包含相部门代码的部门信息 ，部门编号"+((ImpExpDepartment)pj).getCode();
	}
    
    @Override
    protected void addNoDouble(ImpExpPojo pj, Map<String, Object> stringMap, List pjs, Map mapReport) {
        if (pj == null)
            return;

        ImpExpDepartment pd = (ImpExpDepartment)pj;
        if (stringMap != null) {
            if (stringMap.containsKey(pd.getWholeName())) {
                ResultObject ro = this.newResultObject(pj,
                        this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_IGNORED), this.msg4AddNoDouble(pj));
                this.addReport(ro, IImexPort.RESULT_IGNORE, mapReport);
                return;
            }
            
			if(stringMap.containsKey("code:"+pd.getCode())){
				ResultObject ro=this.newResultObject(pj
						   , this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_IGNORED)
						   , this.msg4AddNoDoubleCode(pd));
				this.addReport(ro, IImexPort.RESULT_IGNORE, mapReport);
				return ;
			}

            stringMap.put(pd.getWholeName(), pj);
            if(Strings.isNotBlank(pd.getCode())){
            	stringMap.put("code:"+pd.getCode(), pd);
            }
        }

        if (pjs != null)
            pjs.add(pj);

        return;
    }
    
    private void memberMap(List<V3xOrgMember> memberlist, Map<String,Integer> memberSameNameMap){
        for(V3xOrgMember m:memberlist){
            if(memberSameNameMap.get(m.getName())!=null){
                memberSameNameMap.put(m.getName(), memberSameNameMap.get(m.getName())+1);
            }else{
                memberSameNameMap.put(m.getName(), 1);
            }
            String fullName =m.getName()+"("+m.getLoginName()+")";
            if(memberSameNameMap.get(fullName)!=null){
                memberSameNameMap.put(fullName, memberSameNameMap.get(fullName)+1);
            }else{
                memberSameNameMap.put(fullName, 1);
            }
        }
    }
    
    private void validate(List<String> errorlist,StringBuilder sb,V3xOrgRole r,List<String> rolesMembers,List<V3xOrgMember> memberList,Map<String,Integer> memberSameName){
        Map<String,Integer> sameMember = new HashMap<String,Integer>();
        //OA-72210 Excel中人员重复，导入第一个人员成功，这个现在还是未成功 
        Map<String,Integer> sameMemberFirst = new HashMap<String,Integer>();
        for(String name:rolesMembers){
            if(sameMember.get(name)==null){
                sameMember.put(name, 1);
            }else{
                sameMember.put(name, sameMember.get(name)+1);
            }
        }
        for(String name:rolesMembers){
            if(name.length()==0){
                continue;
            }
            
            if(memberList.isEmpty()&&name.length()>0){
                errorlist.add(ResourceUtil.getString("imp.role.membernotexist", name, r.getShowName()));
                continue;
            }
            if(sameMember.get(name)!=null && sameMember.get(name)>1){
                if(sameMemberFirst.get(name)!=null){
                    errorlist.add(ResourceUtil.getString("imp.role.memberduplicate", r.getShowName(),name));
                    continue;
                }else{
                    sameMemberFirst.put(name,1);
                }
            }
            if(memberSameName.get(name)!=null && memberSameName.get(name)>1){
                errorlist.add(ResourceUtil.getString("imp.role.memberduplicate.inapp", r.getShowName(),name));
                continue;
            }
            if(memberSameName.get(name)==null || (memberSameName.get(name)!=null && memberSameName.get(name)<1)){
                errorlist.add(ResourceUtil.getString("imp.role.membernotexist", name, r.getShowName()));
                continue;
            }
            for(V3xOrgMember m:memberList){
                if(m.getName().equals(name)||(m.getName()+"("+m.getLoginName()+")").equals(name)){
                    if(sb.indexOf(m.getId().toString())==-1){
                        sb.append("Member|").append(m.getId()).append(",");
                    }
                }
            }
        }
    }
    private void makeDeptMap(Map dept,List<V3xOrgMember> oldRoleMember,int index){
        String value = "";
        StringBuilder sb1 = new StringBuilder();
        for(V3xOrgMember m : oldRoleMember){
            sb1.append("Member|").append(m.getId()).append(",");
        }
        value = sb1.toString();
        dept.put("deptrole"+index, value);
    }
}
