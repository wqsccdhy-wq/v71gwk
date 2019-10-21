package com.seeyon.ctp.organization.inexportutil.datatableobj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.metadata.manager.MetadataManager;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.inexportutil.ResultObject;
import com.seeyon.ctp.organization.inexportutil.inf.IImexPort;
import com.seeyon.ctp.organization.inexportutil.msg.MsgContants;
import com.seeyon.ctp.organization.inexportutil.msg.MsgProvider;
import com.seeyon.ctp.organization.inexportutil.msg.MsgProviderBuilder;
import com.seeyon.ctp.organization.inexportutil.pojo.ImpExpPojo;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.organization.services.OrganizationServices;
import com.seeyon.ctp.util.Strings;

public abstract class AbstractImpOpr implements IImexPort {
    protected final static Log              logger      = LogFactory.getLog(AbstractImpOpr.class);

    String                                  resource    = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";

    Locale                                  locale      = null;

    protected MsgProvider                   msgProvider = null;
    private List<V3xOrgDepartment>          allDepartments;                                                                  //部门缓存
    private List<V3xOrgPost>                allPosts;                                                                         //岗位缓存
    private List<V3xOrgLevel>               allLevels;                                                                       // 职务缓存
    protected Map<String, V3xOrgDepartment> departmentNameMap;                                                               // 部门名称缓存，名称匹配，重名无法保证匹配正确的部门
    protected Map<String, V3xOrgDepartment> disableDepartmentNameMap;                                                        // 停用部门名称缓存，名称匹配，重名无法保证匹配正确的部门
    protected Map<String, V3xOrgDepartment> extDepartmentNameMap;                                                            // 外部单位名称缓存，名称匹配，重名无法保证匹配正确的部门
    protected Map<String, V3xOrgDepartment> departmentCodeMap;                                                               // 部门编码缓存，编码匹配，重名无法保证匹配正确的部门
    protected Map<String, V3xOrgDepartment> disableDepartmentCodeMap;                                                        // 停用部门编码缓存，编码匹配，重名无法保证匹配正确的部门
    protected Map<String, V3xOrgDepartment> extDepartmentCodeMap;                                                            // 外部单位编码缓存，编码匹配，重名无法保证匹配正确的部门
    protected Map<String, V3xOrgPost>       postNameMap;                                                                     // 岗位名称缓存，名称匹配
    protected Map<String, V3xOrgLevel>      levelNameMap;                                                                    // 职务名称缓存，名称匹配
    protected Map<String, V3xOrgMember>     memberLoginNameMap;                                                              // 人员登录名缓存，判断相同登录名人员是否存在
    protected Map<String, V3xOrgDepartment>  departmentFullpathMap;
    protected Map<String, V3xOrgDepartment>  disableDepartmentFullpathMap;
    //List<String>  ->  pojo
    abstract protected String getAccountName(ImpExpPojo pojo);

    abstract protected ImpExpPojo transToPojo(List<String> org, V3xOrgAccount voa,String onlytag) throws Exception;

    abstract protected String msg4AddNoDouble(ImpExpPojo pj);

    /**
     * 检查ImpExpPojo对象，有问题抛出Exception，message为错误信息
     */
    protected void pojoCheck(OrganizationServices organizationServices, MetadataManager metadataManager, ImpExpPojo pojo)
            throws Exception {
    }

    protected List transFromOrg(OrganizationServices organizationServices, MetadataManager metadataManager,
            List<List<String>> orgl, V3xOrgAccount account, Map mapReport,String onlytag) {
        logger.info("transFromOrg");
        List rl = new ArrayList();
        Map<String, Object> stringMap = new HashMap<String, Object>(256);
        if (orgl == null || mapReport == null)
            return rl;
        int i = 0;
        for (List<String> ol : orgl) {
            ImpExpPojo pj = null;
            try {
                if (ol == null || ol.isEmpty())
                    continue;

                pj = transToPojo(ol, account,onlytag);
            } catch (Exception e) {
            	String name = ol.get(0);
                String msg = e.getMessage();
                if(msg.startsWith("Error|")){
                	name = msg.split("\\|")[1];
                	msg = msg.split("\\|")[2];
                }
                ResultObject ro = this.newResultObject(name,
                        this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_FAILED), this.getMsgProvider()
                                .getMsg(MsgContants.ORG_IO_MSG_ERROR_EXCEPTION) + msg);
                mapReport = this.addReport(ro, IImexPort.RESULT_ERROR, mapReport);
                
            }
            if (pj != null) {
                if (!account.isGroup()) {
                    String acName = getAccountName(pj);
                    if (!this.passByAccount(acName, account)) {
                        ResultObject ro = this.newResultObject(pj,
                                this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_IGNORED),
                                ResourceUtil.getString("import.not.belongUnit") + account.getName());
                        mapReport = this.addReport(ro, IImexPort.RESULT_IGNORE, mapReport);
                        continue;
                    }
                }
                // 检查ImpExpPojo对象。此功能本可以放在transToPojo()中，但拿不到metadataManager，故新加pojoCheck()
                try {
                    pojoCheck(organizationServices, metadataManager, pj);
                } catch (Exception e) {
                    if (StringUtils.hasText(ol.get(0))) {
                        ResultObject ro = this.newResultObject(ol.get(0),
                                this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_FAILED), this.getMsgProvider()
                                        .getMsg(MsgContants.ORG_IO_MSG_ERROR_EXCEPTION) + e.getMessage());
                        mapReport = this.addReport(ro, IImexPort.RESULT_ERROR, mapReport);
                    }
                    continue;
                }
                this.addNoDouble(pj, stringMap, rl, mapReport);
            }
        }

        return rl;
    }

    protected void addNoDouble(ImpExpPojo pj, Map<String, Object> stringMap, List pjs, Map mapReport) {
        if (pj == null)
            return;

        //boolean ok=true;
        if (stringMap != null) {
            if (stringMap.containsKey(pj.getName())) {
                ResultObject ro = this.newResultObject(pj,
                        this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_IGNORED), this.msg4AddNoDouble(pj));
                this.addReport(ro, IImexPort.RESULT_IGNORE, mapReport);
                return;
            }

            stringMap.put(pj.getName(), pj);
        }

        if (pjs != null)
            pjs.add(pj);

        return;
    }

    //pojo  ->  entity
    abstract protected V3xOrgEntity existEntity(OrganizationServices organizationServices, ImpExpPojo pojo,
            V3xOrgAccount voa,String onlytag) throws Exception;

    abstract protected V3xOrgEntity copyToEntity(OrganizationServices organizationServices,
            MetadataManager metadataManager, ImpExpPojo pojo, V3xOrgEntity ent, V3xOrgAccount voa) throws Exception;

    protected String inCurrentAccount(V3xOrgEntity ent, V3xOrgAccount voa, OrganizationServices organizationServices) {
        return null;
    }

    protected Map transToDes(OrganizationServices organizationServices, MetadataManager metadataManager, List ml,
            V3xOrgAccount voa, boolean ignoreWhenUpdate, Map mapReport, String onlytag) throws BusinessException {
        Map map = new HashMap();

        List addl = new ArrayList();
        map.put(IImexPort.RESULT_ADD, addl);

        List updatel = new ArrayList();
        map.put(IImexPort.RESULT_UPDATE, updatel);

        if (ml == null || ml.isEmpty())
            return map;

        // 初始化缓存的本单位全部部门、职务级别和岗位
        initCache(organizationServices, voa);
        for (int i = 0; i < ml.size(); i++) {
            ImpExpPojo pojo = (ImpExpPojo) ml.get(i);
            if (pojo == null)
                continue;

            try {
                V3xOrgEntity ent = existEntity(organizationServices, pojo, voa, onlytag);
                String action = this.doUpdate4Entity(ent, ignoreWhenUpdate, pojo);

                if (IImexPort.RESULT_IGNORE.equals(action)) {
                    String reason = inCurrentAccount(ent, voa, organizationServices);
                    if (StringUtils.hasText(reason)) {
                        ResultObject ro = this.newResultObject(pojo,
                                this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_IGNORED), reason);
                        mapReport = this.addReport(ro, action, mapReport);

                        continue;
                    } else {
                        ResultObject ro = this.newResultObject(pojo,
                                this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_IGNORED), this.getMsgProvider()
                                        .getMsg(MsgContants.ORG_IO_MSG_ALERT_IGNORED4DOUBLE));
                        mapReport = this.addReport(ro, action, mapReport);
                        if("ImpExpDepartment".equals(pojo.getClass().getSimpleName())){
                        	ent = copyToEntity(organizationServices, metadataManager, pojo, ent, voa);
                        }
                        continue;
                    }
                }
                if (IImexPort.RESULT_UPDATE.equals(action)) {
                    String reason = inCurrentAccount(ent, voa, organizationServices);
                    if (StringUtils.hasText(reason)) {
                        ResultObject ro = this.newResultObject(pojo,
                                this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_IGNORED), reason);
                        mapReport = this.addReport(ro, action, mapReport);

                        continue;
                    }
                }
                if (IImexPort.RESULT_NOTEXIST.equals(action)) {
                    ResultObject ro = this.newResultObject(pojo,
                            this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_FAILED), ResourceUtil.getString("imp.dept.notexist"));
                    mapReport = this.addReport(ro, action, mapReport);
                    continue;
                }

                ent = copyToEntity(organizationServices, metadataManager, pojo, ent, voa);

                //校验数据
                List volst = new ArrayList(1);
                volst.add(ent);
                this.validateData(volst);

                List l = (List) map.get(action);
                if (l == null)
                    throw new Exception("error action to do");

                l.add(ent);
            } catch (Exception e) {
                ResultObject ro = this.newResultObject(pojo,
                        this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_FAILED),
                        this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_EXCEPTION) + e.getMessage());
                mapReport = this.addReport(ro, IImexPort.RESULT_ERROR, mapReport);
            }
        }

        return map;
    }

    private void initCache(OrganizationServices organizationServices, V3xOrgAccount voa) throws BusinessException {
        OrgManager om = (OrgManager) AppContext.getBean("orgManager");
        OrgDao od = (OrgDao) AppContext.getBean("orgDao");
        OrgManagerDirect orgManagerDirect = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
        
        boolean isBusinessOrg = voa.getExternalType() == OrgConstants.ExternalType.Interconnect4.ordinal();
        
        List<OrgMember> allOrgMembers = od.getAllMemberPOByAccountId(null, null, null, null, null);
        List<V3xOrgMember> allMembers = new ArrayList<V3xOrgMember>();
        for (OrgMember o:allOrgMembers) {
            allMembers.add(new V3xOrgMember(o));
        }
        
        allDepartments = om.getAllDepartments(voa.getId());
        List<V3xOrgEntity> disableDepartments = om.getEntityNoRelation(V3xOrgDepartment.class.getSimpleName(), null, null,voa.getId(), false,false,false);
        allPosts = om.getAllPosts(voa.getId());
        allLevels = om.getAllLevels(voa.getId());
        memberLoginNameMap = new HashMap<String, V3xOrgMember>();
        for (V3xOrgMember member : allMembers) {
            // 避免内存实体被取出后直接修改，new一个实例。
            memberLoginNameMap.put(member.getLoginName(), new V3xOrgMember(member));
        }
        departmentNameMap = new HashMap<String, V3xOrgDepartment>();
        departmentCodeMap = new HashMap<String, V3xOrgDepartment>();
        
        extDepartmentNameMap = new HashMap<String, V3xOrgDepartment>();
        extDepartmentCodeMap = new HashMap<String, V3xOrgDepartment>();
        
        departmentFullpathMap = new HashMap<String, V3xOrgDepartment>();
        for (V3xOrgDepartment dept : allDepartments) {
        	if(!dept.getIsInternal() && !isBusinessOrg){
        		extDepartmentNameMap.put(dept.getName(), dept);
                if(Strings.isNotBlank(dept.getCode())){
                	extDepartmentCodeMap.put(dept.getCode(), dept);
                }
        		continue;
        	}
        	//获取其全部门路径，上传时使用
        	String deptFullName = OrgHelper.showDepartmentFullPath(dept.getId());
        	departmentFullpathMap.put(deptFullName, dept);
            departmentNameMap.put(dept.getName(), dept);
            if(Strings.isNotBlank(dept.getCode())){
                departmentCodeMap.put(dept.getCode(), dept);
            }
        }
        
        disableDepartmentFullpathMap = new HashMap<String, V3xOrgDepartment>();
        disableDepartmentNameMap = new HashMap<String, V3xOrgDepartment>();
        disableDepartmentCodeMap = new HashMap<String, V3xOrgDepartment>();
        for (V3xOrgEntity entity : disableDepartments) {
        	V3xOrgDepartment dept = (V3xOrgDepartment)entity;
        	if(!dept.getIsInternal()  && !isBusinessOrg){
        		continue;
        	}
        	
        	String deptFullName = OrgHelper.showDepartmentFullPath(dept.getId());
        	disableDepartmentFullpathMap.put(deptFullName, dept);
        	disableDepartmentNameMap.put(dept.getName(), dept);
            if(Strings.isNotBlank(dept.getCode())){
            	disableDepartmentCodeMap.put(dept.getCode(), dept);
            }
        }
        
        postNameMap = new HashMap<String, V3xOrgPost>();
        for (V3xOrgPost post : allPosts) {
            postNameMap.put(post.getName(), post);
        }
        levelNameMap = new HashMap<String, V3xOrgLevel>();
        for (V3xOrgLevel level : allLevels) {
            levelNameMap.put(level.getName(), level);
        }
    }

    abstract protected void add(OrganizationServices organizationServices, V3xOrgEntity ent,ImpExpPojo pj) throws Exception;

    abstract protected void update(OrganizationServices organizationServices, V3xOrgEntity ent, ImpExpPojo pj) throws Exception;

    protected String getOKMsg4Add(V3xOrgEntity ent, OrganizationServices organizationServices) {
        /*"已成功添加"
         * this.getMsgProvider()
                      .getMsg(MsgContants.ORG_IO_MSG_OK_ADD);
         */
        return this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OK_ADD);//
    }

    protected String getOKMsg4Update(V3xOrgEntity ent, OrganizationServices organizationServices) {
        /*"已成功更新"
         * this.getMsgProvider()
                      .getMsg(MsgContants.ORG_IO_MSG_OK_UPDATE);
         */
        return this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OK_UPDATE);
    }

    protected Map add(OrganizationServices organizationServices, MetadataManager metadataManager, V3xOrgEntity ent,
            Map mapReport,ImpExpPojo pj) throws Exception {
        try {
            add(organizationServices, ent,pj);

            ResultObject ro = this.newResultObject(ent, this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_OK),
                    this.getOKMsg4Add(ent, organizationServices));
            mapReport = this.addReport(ro, IImexPort.RESULT_ADD, mapReport);
        } catch (Exception e) {
            logger.error("error", e);
            ResultObject ro = this.newResultObject(ent, this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_FAILED),
                    this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_EXCEPTION) + e.getMessage());
            mapReport = this.addReport(ro, IImexPort.RESULT_ERROR, mapReport);
        }

        return mapReport;
    }

    protected Map update(OrganizationServices organizationServices, MetadataManager metadataManager, V3xOrgEntity ent,
            Map mapReport, ImpExpPojo pj) throws Exception {
        try {
            update(organizationServices, ent, pj);

            ResultObject ro = this.newResultObject(ent, this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_OK),
                    this.getOKMsg4Update(ent, organizationServices));
            mapReport = this.addReport(ro, IImexPort.RESULT_UPDATE, mapReport);
        } catch (Exception e) {
            logger.error("error", e);
            ResultObject ro = this.newResultObject(ent, this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_FAILED),
                    this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_EXCEPTION) + e.getMessage());
            mapReport = this.addReport(ro, IImexPort.RESULT_ERROR, mapReport);
        }

        return mapReport;
    }

    protected Map add(OrganizationServices organizationServices, MetadataManager metadataManager, List ents,
            Map mapReport,Map<Long,ImpExpPojo> pjl) throws Exception {
        if (ents == null || mapReport == null)
            return mapReport;

        for (int i = 0; i < ents.size(); i++) {
            V3xOrgEntity ent = (V3xOrgEntity) ents.get(i);
            if (ent == null)
                continue;
            mapReport = add(organizationServices, metadataManager, ent, mapReport,pjl.get(ent.getId()));
        }

        return mapReport;
    }

    protected Map update(OrganizationServices organizationServices, MetadataManager metadataManager, List ents,
            Map mapReport, Map<Long,ImpExpPojo> pjl) throws Exception {
        if (ents == null || mapReport == null)
            return mapReport;

        for (int i = 0; i < ents.size(); i++) {
            V3xOrgEntity ent = (V3xOrgEntity) ents.get(i);
            if (ent == null){
                continue;
            }
            mapReport = update(organizationServices, metadataManager, ent, mapReport, pjl.get(ent.getId()));
        }

        return mapReport;
    }

    protected Map op(OrganizationServices organizationServices, MetadataManager metadataManager, Map ents, Map mapReport, Map<Long,ImpExpPojo> pjl)
            throws Exception {
        if (ents == null)
            return mapReport;

        mapReport = add(organizationServices, metadataManager, (List) ents.get(IImexPort.RESULT_ADD), mapReport,pjl);

        mapReport = update(organizationServices, metadataManager, (List) ents.get(IImexPort.RESULT_UPDATE), mapReport, pjl);

        return mapReport;
    }

    protected boolean passByAccount(String name, V3xOrgAccount account) {
        if (account == null || !StringUtils.hasText(account.getName()))
            return true;
        if (!StringUtils.hasText(name))
            return false;

        return name.trim().equals(account.getName().trim()) ? true : false;
    }

    protected String doUpdate4Entity(Object ent, boolean ignoreWhenUpdate) {
       return this.doUpdate4Entity(ent, ignoreWhenUpdate, null);
    }
    
    protected String doUpdate4Entity(Object ent, boolean ignoreWhenUpdate,ImpExpPojo type) {
        if (ignoreWhenUpdate) {
            return ent == null ? IImexPort.RESULT_ADD : IImexPort.RESULT_IGNORE;
        }

        return ent == null ? IImexPort.RESULT_ADD : IImexPort.RESULT_UPDATE;
    }

    protected ResultObject newResultObject(V3xOrgEntity ent, String success, String des) {
        if (ent == null)
            return null;
        return newResultObject(ent.getName(), success, des);
    }

    protected ResultObject newResultObject(ImpExpPojo pj, String success, String des) {
        if (pj == null)
            return null;
        return newResultObject(pj.getName(), success, des);
    }

    protected ResultObject newResultObject(String name, String success, String des) {
       /* if (!StringUtils.hasText(name))
            return null;*/

        ResultObject ro = new ResultObject();

        ro.setName(name);
        if (StringUtils.hasText(success))
            ro.setSuccess(success);
        if (StringUtils.hasText(des))
            ro.setDescription(des);

        return ro;
    }

    protected Map addReport(ResultObject ro, String action, Map mapReport) {
        if (ro == null || mapReport == null)
            return mapReport;

        List l = (List) mapReport.get(action);
        if (l == null)
            return mapReport;

        l.add(ro);

        return mapReport;
    }

    public Map importOrg(OrganizationServices organizationServices, MetadataManager metadataManager,
            List<List<String>> fromList, V3xOrgAccount voa, boolean ignoreWhenUpdate,String onlytag) throws Exception {
        if (organizationServices == null)
            throw new Exception("no organizationServices!");
        if (metadataManager == null)
            throw new Exception("no metadataManager!");

        Map map = new HashMap();

        List addl = new ArrayList();
        List updatel = new ArrayList();
        List igl = new ArrayList();
        List errorl = new ArrayList();
        List notexist = new ArrayList();

        map.put(IImexPort.RESULT_ADD, addl);
        map.put(IImexPort.RESULT_UPDATE, updatel);
        map.put(IImexPort.RESULT_IGNORE, igl);
        map.put(IImexPort.RESULT_ERROR, errorl);
        map.put(IImexPort.RESULT_NOTEXIST, notexist);

        List ml = this.transFromOrg(organizationServices, metadataManager, fromList, voa, map, onlytag);
        Map desMap = this.transToDes(organizationServices, metadataManager, ml, voa, ignoreWhenUpdate, map, onlytag);
        Map<Long,ImpExpPojo> pojos = new HashMap<Long,ImpExpPojo>();
        for(Object a : ml){
            ImpExpPojo p = (ImpExpPojo)a;
            pojos.put(p.getId(), p);
        }
        map = op(organizationServices, metadataManager, desMap, map, pojos);

        return map;
    }

    protected String[] getCodeFromNameCodeString(String nameStr) {
    	return getCodeFromNameCodeString(nameStr,null,null);
    }
    
    protected String[] getCodeFromNameCodeString(String nameStr,String entityClassName,Long accountId) {
        String[] map = new String[3];
        String name = "";
        String code = "";

        map[0] = name;
        map[1] = code;

        if (!StringUtils.hasText(nameStr))
            return map;

        int epos = nameStr.lastIndexOf(")");
        int spos = nameStr.lastIndexOf("(");

        if (epos < 0 || spos < 0 || epos <= spos) {
            name = nameStr;
        } else {
        	//判断是不是名称中本身就包含括号
        	OrgManager om = (OrgManager) AppContext.getBean("orgManager");
        	if(Strings.isNotBlank(entityClassName)){
        		List entitys = null;
        		try {
        			if((V3xOrgDepartment.class.getSimpleName()).equals(entityClassName)){
        				entitys = om.getDepartmentsByName(nameStr,accountId);
        			}else{
        				entitys = om.getEntityList(entityClassName, "name", nameStr, accountId,false,true);
        			}
				} catch (BusinessException e) {
				}
        		if(Strings.isEmpty(entitys)){//没有查到名称中包含括号的实体
        			name = nameStr.substring(0, spos).trim();
            		code = nameStr.substring(spos + 1, epos).trim();
        		}else{
        			name = nameStr;
        		}
        	}else{
        		name = nameStr.substring(0, spos).trim();
        		code = nameStr.substring(spos + 1, epos).trim();
        	}
        }

        map[0] = name;
        map[1] = code;
        map[2] = nameStr;

        return map;
    }

    protected V3xOrgEntity getRightEntity(OrganizationServices organizationServices, Class clazz, String propName,
            String value, V3xOrgAccount voa) throws Exception {
        //V3xOrgEntity ent=null;
        /*		List l=organizationServices.getOrgManagerDirect()
        		                           .getEntityList(clazz.getSimpleName(), propName, value, voa.getId());
        		if(l==null || l.isEmpty())
        			throw new Exception("no right "+clazz.getSimpleName()+"  object");*/
        Object o = organizationServices.getOrgManager().getEntityNoRelation(clazz.getSimpleName(), propName, value,
                voa.getId());
        if (o == null)
            throw new Exception("no right " + clazz.getSimpleName() + "  object");
        return (V3xOrgEntity) o;//l.get(0);
    }

    //getdept
    protected V3xOrgDepartment getRightDept(OrganizationServices organizationServices, String propName, String value,
            V3xOrgAccount voa) throws Exception {
        return (V3xOrgDepartment) getRightEntity(organizationServices, V3xOrgDepartment.class, propName, value, voa);
    }

    protected V3xOrgDepartment getRightDeptByCode(OrganizationServices organizationServices, String value,
            V3xOrgAccount voa) throws Exception {
        return (V3xOrgDepartment) getRightEntity(organizationServices, V3xOrgDepartment.class, "code", value, voa);
    }

    protected V3xOrgDepartment getRightDeptByName(OrganizationServices organizationServices, String value,
            V3xOrgAccount voa) throws Exception {
        return (V3xOrgDepartment) getRightEntity(organizationServices, V3xOrgDepartment.class, "name", value, voa);
    }

    protected V3xOrgDepartment getNeedDepartment(OrganizationServices organizationServices, String[] ncvalue,
            V3xOrgAccount voa) throws Exception {
        try {
            if (StringUtils.hasText(ncvalue[1])){
                V3xOrgDepartment dep =  departmentCodeMap.get(ncvalue[1]);//先按码匹配
                if (dep != null)
                    return dep;
            }
        }
        catch (Exception e) {
            logger.error("error", e);
        }

        try {
            V3xOrgDepartment dep = this.departmentNameMap.get(ncvalue[0]);
            if (dep != null)
                return dep;
            // return this.getRightDeptByName(organizationServices, ncvalue[0], voa);//按名平配，因为重名，会出现匹配错误部门
        } catch (Exception e) {
            logger.error("error", e);
            //throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_DEP));
        }
        //return this.getRightDeptByCode(organizationServices, ncvalue[1], voa);

        /*
        List<V3xOrgDepartment> ps = null;
        try {
            ps = allDepartments;//organizationServices.getOrgManagerDirect().getAllDepartments(voa.getId());
        } catch (Exception e) {
            logger.error("error", e);
        }
        if (ps != null) {
            if (StringUtils.hasText(ncvalue[1])) {
                for (V3xOrgDepartment d : ps) {
                    if (d == null)
                        continue;

                    if (ncvalue[1].equals(d.getCode()))
                        return d;
                }
            }

            if (StringUtils.hasText(ncvalue[0]) || StringUtils.hasText(ncvalue[2])) {
                for (V3xOrgDepartment d : ps) {
                    if (d == null)
                        continue;
                    logger.info("d=" + d.getName());
                    if (ncvalue[0].equals(d.getName()) || ncvalue[2].equals(d.getName()))
                        return d;
                }
            }
        }
        */

        throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_DEP));
    }

    //getPost
    protected V3xOrgPost getRightPost(OrganizationServices organizationServices, String propName, String value,
            V3xOrgAccount voa) throws Exception {
        return (V3xOrgPost) getRightEntity(organizationServices, V3xOrgPost.class, propName, value, voa);
    }

    protected V3xOrgPost getRightPostByCode(OrganizationServices organizationServices, String value, V3xOrgAccount voa)
            throws Exception {
        return (V3xOrgPost) getRightEntity(organizationServices, V3xOrgPost.class, "code", value, voa);
    }

    protected V3xOrgPost getRightPostByName(OrganizationServices organizationServices, String value, V3xOrgAccount voa)
            throws Exception {
        return (V3xOrgPost) getRightEntity(organizationServices, V3xOrgPost.class, "name", value, voa);
    }

    // 匹配主岗
    protected V3xOrgPost getNeedPost(OrganizationServices organizationServices, String[] ncvalue, V3xOrgAccount voa)
            throws Exception {
    	V3xOrgPost post = null;
    	if(ncvalue.length==3){
    		post = this.postNameMap.get(ncvalue[2]);
    	}
    	if(post == null){
    		post = this.postNameMap.get(ncvalue[0]);
    	}
        if (post != null)
            return post;
        throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_PPOST));
    }

    //getLevel
    protected V3xOrgLevel getRightLevel(OrganizationServices organizationServices, String propName, String value,
            V3xOrgAccount voa) throws Exception {
        return (V3xOrgLevel) getRightEntity(organizationServices, V3xOrgLevel.class, propName, value, voa);
    }

    protected V3xOrgLevel getRightLevelByCode(OrganizationServices organizationServices, String value, V3xOrgAccount voa)
            throws Exception {
        return (V3xOrgLevel) getRightEntity(organizationServices, V3xOrgLevel.class, "code", value, voa);
    }

    protected V3xOrgLevel getRightLevelByName(OrganizationServices organizationServices, String value, V3xOrgAccount voa)
            throws Exception {
        return (V3xOrgLevel) getRightEntity(organizationServices, V3xOrgLevel.class, "name", value, voa);
    }

    protected V3xOrgLevel getNeedLevel(OrganizationServices organizationServices, String[] ncvalue, V3xOrgAccount voa)
            throws Exception {
        /*		try{
        			return this.getRightLevelByName(organizationServices, ncvalue[0], voa);
        		}catch(Exception e){
        			logger.error("error", e);
        			
        		}
        		
        		try{
        			List<V3xOrgLevel>  ps= allLevels;//organizationServices.getOrgManagerDirect().getAllLevels(voa.getId(),false);
        			if(ps!=null){
        				for(V3xOrgLevel p:ps){
        					if(p==null)
        						continue;
        					
        					if(ncvalue[0].equals(p.getName()))
        						return p;
        				}
        			}
        		}catch(Exception e){
        			logger.error("error", e);
        		}*/
        V3xOrgLevel level = this.levelNameMap.get(ncvalue[0]);
        if (level != null)
            return level;
        throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_LEV));
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    protected MsgProvider getMsgProvider() {
        if (this.msgProvider == null) {
            this.msgProvider = (MsgProviderBuilder.getInstance().createMsgProvider(this.getLocale()));
        }

        return this.msgProvider;
    }

    protected String catchNoCammerString(String ln) throws Exception {
        String[] sa = ln.split(",");
        StringBuilder sb = new StringBuilder();
        for (String str : sa) {
            if (StringUtils.hasText(str))
                sb.append(str);
        }

        return sb.toString();
    }

    protected boolean regTest(String value, String patten) {
        try {
            return value.matches(patten);
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean regTestChNum(String value) {
        return regTest(value, "^[\\w-]+$");
    }

    protected boolean regTestMail(String value) {
        //"^[-!#$%&'*+\\./0-9=?A-Z^_`a-z{|}~]+@[-!#$%&'*+\\/0-9=?A-Z^_`a-z{|}~]+.[-!#$%&'*+\\./0-9=?A-Z^_`a-z{|}~]+$"
        //"^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
        return regTest(value,
                "^[-!#$%&'*+\\./0-9=?A-Z^_`a-z{|}~]+@[-!#$%&'*+\\/0-9=?A-Z^_`a-z{|}~]+.[-!#$%&'*+\\./0-9=?A-Z^_`a-z{|}~]+$");
    }
}//end class
