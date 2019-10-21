
package com.seeyon.ctp.organization.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.Constants.LoginOfflineOperation;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.init.MclclzUtil;
import com.seeyon.ctp.common.metadata.bo.MetadataCategoryBO;
import com.seeyon.ctp.common.metadata.bo.MetadataColumnBO;
import com.seeyon.ctp.common.metadata.enums.MetadataConstants;
import com.seeyon.ctp.common.metadata.manager.MetadataCategoryManager;
import com.seeyon.ctp.common.metadata.manager.MetadataColumnManager;
import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.event.EventDispatcher;
import com.seeyon.ctp.login.online.OnlineRecorder;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.RelationshipType;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.CompareSortMemberPost;
import com.seeyon.ctp.organization.bo.CompareSortRelationship;
import com.seeyon.ctp.organization.bo.CompareUnitPath;
import com.seeyon.ctp.organization.bo.EntityIdTypeDsBO;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.event.AddJoinMemberEvent;
import com.seeyon.ctp.organization.po.JoinAccount;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.organization.po.OrgPost;
import com.seeyon.ctp.organization.po.OrgRelationship;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.organization.principal.NoSuchPrincipalException;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.permission.bo.LicenseConst;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;
import com.seeyon.ctp.privilege.bo.PrivTreeNodeBO;
import com.seeyon.ctp.privilege.dao.RoleMenuDao;
import com.seeyon.ctp.privilege.manager.PrivilegeMenuManager;
import com.seeyon.ctp.privilege.po.PrivRoleMenu;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.UniqueList;
import com.seeyon.ctp.util.json.JSONUtil;

public class JoinOrgManagerDirectImpl implements JoinOrgManagerDirect {

    private final static Log   log = LogFactory.getLog(JoinOrgManagerDirectImpl.class);
    
    private static final Class<?> c = MclclzUtil.ioiekc("com.seeyon.ctp.product.ProductInfo");

    private OrgDao           orgDao;
    private OrgCache         orgCache;
    private OrgManager       orgManager;
    private OrgManagerDirect orgManagerDirect;
    private PrincipalManager principalManager;
    private MetadataColumnManager metadataColumnManager;
    private MetadataCategoryManager metadataCategoryManager;
    private JoinAccountCustomerFieldInfoManager joinAccountCustomerFieldInfoManager;
    private RoleMenuDao  roleMenuDao;
    private PrivilegeMenuManager      privilegeMenuManager;
    private EnumManager enumManagerNew;

    public void setOrgDao(OrgDao orgDao) {
        this.orgDao = orgDao;
    }

    public void setOrgCache(OrgCache orgCache) {
        this.orgCache = orgCache;
    }
    
    public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setMetadataColumnManager(MetadataColumnManager metadataColumnManager) {
		this.metadataColumnManager = metadataColumnManager;
	}

	public void setMetadataCategoryManager(MetadataCategoryManager metadataCategoryManager) {
		this.metadataCategoryManager = metadataCategoryManager;
	}
	
	public void setJoinAccountCustomerFieldInfoManager(JoinAccountCustomerFieldInfoManager joinAccountCustomerFieldInfoManager) {
		this.joinAccountCustomerFieldInfoManager = joinAccountCustomerFieldInfoManager;
	}

	public void setRoleMenuDao(RoleMenuDao roleMenuDao) {
		this.roleMenuDao = roleMenuDao;
	}

	public void setPrivilegeMenuManager(PrivilegeMenuManager privilegeMenuManager) {
		this.privilegeMenuManager = privilegeMenuManager;
	}

	public void setEnumManagerNew(EnumManager enumManagerNew) {
		this.enumManagerNew = enumManagerNew;
	}

	@Override
    public OrganizationMessage addAccount(V3xOrgAccount account) throws BusinessException {
		OrganizationMessage message = new OrganizationMessage();
        if(null == account) {
        	message.addErrorMsg(null, "创建单位失败，传入的单位实体为空！");
        	return message;
        }
        account.setIdIfNew();
        String path = OrgHelper.getPathByPid4Add(V3xOrgUnit.class, account.getSuperior());
        account.setPath(path);
        account.setType(OrgConstants.UnitType.Account);
        //排序号默认给成1，不在前台显示
        account.setSortId(1L);
        //修改單位可見範圍處理代碼，步驟先刪除關係再新建
        account.setLevelScope(-1);
        //2、实例化
        List<OrgUnit> poList = new ArrayList<OrgUnit>(1);
        poList.add((OrgUnit) account.toPO());
        orgDao.insertOrgUnit(poList);
        return message;
    }
	
	@Override
    public OrganizationMessage updateAccount(V3xOrgAccount account) throws BusinessException {
		OrganizationMessage message = new OrganizationMessage();
        if(null == account) {
        	message.addErrorMsg(null, "修改单位失败，传入的单位实体为空！");
        	return message;
        }
        //实例化
        orgDao.update((OrgUnit)OrgHelper.boTopo(account));
        return message;
    }
	
    @Override
    public OrganizationMessage addAccount(V3xOrgAccount account, V3xOrgMember adminMember) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        // 校验登录名是否已存在
        if (principalManager.isExist(adminMember.getV3xOrgPrincipal().getLoginName())) {
            //message.addErrorMsg(adminMember, ResourceUtil.getString("MessageStatus.PRINCIPAL_REPEAT_NAME"));
        	message.addErrorMsg(adminMember, "登录名重复");
            return message;
        }

        message = this.addAccount(account);
        if(!message.isSuccess()){
            return message;
        }

        //实例化单位管理员人员信息
        adminMember.setIsAdmin(true);
        adminMember.setOrgAccountId(account.getId());
        List<OrgMember> _relMember = new ArrayList<OrgMember>(1);
        _relMember.add((OrgMember) adminMember.toPO());
        orgDao.insertOrgMember(_relMember);
        //实例化管理员账号
        principalManager.insert(adminMember.getV3xOrgPrincipal());
        //TODO 这里ID临时写死，由于整个系统只有一个单位管理员
        addRole2Entity(-2989205846588111483L, account.getId(), adminMember);
        message.addSuccessMsg(account);
        return message;
    }
    
    private void addRole2Entity(Long roleId, Long unitId, V3xOrgEntity entity) throws BusinessException {
        V3xOrgRelationship relBO = new V3xOrgRelationship();
        relBO.setId(UUIDLong.longUUID());
        relBO.setKey(OrgConstants.RelationshipType.Member_Role.name());
        relBO.setSourceId(entity.getId());
        V3xOrgUnit unit = orgManager.getUnitById(unitId);
        relBO.setObjective0Id(unitId);
        relBO.setObjective1Id(roleId);
        relBO.setObjective5Id(entity.getEntityType());//保存Member/Department/Post/Level等
        relBO.setOrgAccountId(unit.getOrgAccountId());
        List<OrgRelationship> relPOs = new ArrayList<OrgRelationship>(1);
        relPOs.add((OrgRelationship) relBO.toPO());
        /** 先将这些实体与这些角色关系先删除 */
       
        EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds = new EnumMap<OrgConstants.RelationshipObjectiveName, Object>(
                OrgConstants.RelationshipObjectiveName.class);
        objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective1Id, roleId);
        objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective0Id, unitId);
        
        orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Role.name(), entity.getId(), unit.getOrgAccountId(),
                objectiveIds);
        /** 插入关系表实体与角色之间的关系 */
        orgDao.insertOrgRelationship(relPOs);
    }
    
    /**
     * 校验重名（内部组织和外部组织一起判断，全局不能重复）
     * @param entityClassName
     * @param property
     * @param value
     * @return
     * @throws BusinessException
     */
    
    @Override
    public boolean isPropertyDuplicated(String entityClassName, String property, Object value, Long accountId)
            throws BusinessException {
        return orgDao.isPropertyDuplicated(OrgHelper.getEntityTypeBySimpleName(entityClassName), property, value, accountId);
    }
    
    private boolean isPropertyDuplicated(String entityClassName, String property, Object value, Long accountId, Long entId)
            throws BusinessException {
        return orgDao.isPropertyDuplicated(OrgHelper.getEntityTypeBySimpleName(entityClassName), property, value, accountId, entId, OrgConstants.ExternalType.Interconnect1.ordinal());
    }
    
    private <T extends V3xOrgEntity> boolean isPropertyDuplicated(Class<T> entityClassName, String property, Object value, Long accountId){
        return orgDao.isPropertyDuplicated(entityClassName, property, value, accountId);
    }
    
    @Override
    public OrganizationMessage addDepartment(V3xOrgDepartment dept) throws BusinessException {
        List<V3xOrgDepartment> depts = new ArrayList<V3xOrgDepartment>();
        depts.add(dept);
        return this.addDepartments(depts);
    }
    
    @Override
    public OrganizationMessage addDepartment(V3xOrgDepartment dept,V3xOrgMember subManager) throws BusinessException {
    	if(subManager == null){
    		return addDepartment(dept);
    	}
        OrganizationMessage message = new OrganizationMessage();
        // 校验登录名是否已存在
        if (principalManager.isExist(subManager.getV3xOrgPrincipal().getLoginName())) {
        	message.addErrorMsg(subManager, "管理员登录名重复");
            return message;
        }

        message = this.addDepartment(dept);
        if(!message.isSuccess()){
            return message;
        }

        //实例化子机构管理员人员信息
        subManager.setIsAdmin(true);
        subManager.setEnabled(dept.getEnabled());
        subManager.setIsInternal(false);
        subManager.setExternalType(OrgConstants.ExternalType.Interconnect1.ordinal());
        subManager.setOrgDepartmentId(dept.getId());
        subManager.setOrgAccountId(dept.getOrgAccountId());
        List<OrgMember> _relMember = new ArrayList<OrgMember>(1);
        _relMember.add((OrgMember) subManager.toPO());
        orgDao.insertOrgMember(_relMember);
        //实例化管理员账号
        principalManager.insert(subManager.getV3xOrgPrincipal());
        //TODO 这里ID临时写死，由于整个系统只有一个子机构官立员
        addRole2Entity(OrgConstants.VJOIN_SUBMANAGER_ROLE_ID, dept.getId(), subManager);
        message.addSuccessMsg(dept);
        return message;
    }

    @Override
    public OrganizationMessage addDepartments(List<V3xOrgDepartment> depts) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        List<OrgUnit> poList = new ArrayList<OrgUnit>();
        int type = depts.get(0).getExternalType();
        List<MetadataColumnBO> metadataColumnList=this.getCustomerAccountProperties(depts.get(0).getOrgAccountId());
        List<JoinAccount> joinAccountCustomerFieldInfos = new ArrayList<JoinAccount>();
        List<V3xOrgDepartment> deptList = new ArrayList<V3xOrgDepartment>();
        for (V3xOrgDepartment dept : depts) {
            if(type == OrgConstants.ExternalType.Interconnect1.ordinal()){
              //校验特殊字符
                String reg = "^.*[(/)|(\\\\)|(,)|(\\|)|(')|(\")|(<)|(>)].*$";
                
                if (Strings.isNotBlank(dept.getName())) {
                    if (dept.getName().length() > 100) {
                        message.addErrorMsg(dept, "机构名称太长!");
                        return message;
                    }else if(dept.getName().matches(reg)){
                        message.addErrorMsg(dept, "机构名称包含特殊字符!");
                        return message;
                    }
                }
                
                if (Strings.isNotBlank(dept.getCode())) {
                    if (dept.getCode().length() > 20) {
                        message.addErrorMsg(dept, "单位编号太长!");
                        return message;
                    }else if(dept.getCode().matches(reg)){
                        message.addErrorMsg(dept, "单位编号包含特殊字符!");
                        return message;
                    }
                }
                
                if (Strings.isNotBlank(dept.getDescription())) {
                    if (dept.getDescription().length() > 200) {
                        message.addErrorMsg(dept, "备注太长!");
                        return message;
                    }
                }
            } else if(type == OrgConstants.ExternalType.Interconnect2.ordinal()){
              //校验特殊字符
                String reg = "^.*[(/)|(\\\\)|(,)|(\\|)|(')|(\")|(<)|(>)].*$";
                
                if (Strings.isNotBlank(dept.getName())) {
                    if (dept.getName().length() > 100) {
                        message.addErrorMsg(dept, "单位名称太长!");
                        return message;
                    }else if(dept.getName().matches(reg)){
                        message.addErrorMsg(dept, "单位名称包含特殊字符!");
                        return message;
                    }
                }
                
                if (Strings.isNotBlank(dept.getCode())) {
                    if (dept.getCode().length() > 20) {
                        message.addErrorMsg(dept, "单位编号太长!");
                        return message;
                    }else if(dept.getCode().matches(reg)){
                        message.addErrorMsg(dept, "单位编号包含特殊字符!");
                        return message;
                    }
                }
                
                if (Strings.isNotBlank(dept.getDescription())) {
                    if (dept.getDescription().length() > 500) {
                        message.addErrorMsg(dept, "备注太长!");
                        return message;
                    }
                }
                
                String address =  dept.getProperty("address") == null ? "" :  dept.getProperty("address").toString();
                if (Strings.isNotBlank(address)) {
                    if (address.length() > 70) {
                        message.addErrorMsg(dept, "单位地址太长!");
                        return message;
                    }else if(address.matches(reg)){
                        message.addErrorMsg(dept, "单位地址包含特殊字符!");
                        return message;
                    }
                }
            }
            if (Strings.isNotBlank(dept.getCode())) {
                //判断部门编码是否重复
                boolean isDuplicated = isPropertyDuplicated(V3xOrgDepartment.class.getSimpleName(), "code",
                        dept.getCode(), dept.getOrgAccountId());
                if (isDuplicated) {
                    message.addErrorMsg(dept, ResourceUtil.getString("Vjoin.DEPARTMENT_REPEAT_CODE"));
                    return message;
                }
            }
            //校验同级部门名称是否重复
            List<V3xOrgDepartment> brother = this.getChildUnits(dept.getSuperior(), true);
            boolean isduplicated = isExistRepeatProperty(brother, "name", dept.getName(), dept);
            if (isduplicated) {
                if(dept.getExternalType().equals(OrgConstants.ExternalType.Interconnect1.ordinal())){
            		message.addErrorMsg(dept, ResourceUtil.getString("Vjoin.UNIT_REPEAT_NAME",dept.getName()));
            	}else{
            		message.addErrorMsg(dept, ResourceUtil.getString("Vjoin.ACCOUNT_REPEAT_NAME",dept.getName()));
            	}
                return message;
            } 
            
            if(null == dept.getSortId() || dept.getSortId() == 0) {
                dept.setSortId(Long.valueOf(getMaxSortNum(V3xOrgDepartment.class.getSimpleName(), dept.getOrgAccountId(),dept.getExternalType())) + 1);
            }
            
            String path = OrgHelper.getPathByPid4Add(V3xOrgUnit.class, dept.getSuperior(),null);
            dept.setPath(path);
            dept.setIdIfNew();
            dept.setType(OrgConstants.UnitType.Department);
            message.addSuccessMsg(dept);
            poList.add((OrgUnit) dept.toPO());
            
            if(type==OrgConstants.ExternalType.Interconnect2.ordinal()){
            	int cLen=0;
            	List<String> customerProperties = dept.getCustomerProperties();
            	MetadataColumnBO metadataColumn;
            	JoinAccount joinAccount = new JoinAccount();
            	joinAccount.setId(UUIDLong.longUUID());
            	joinAccount.setOrgAccountId(dept.getOrgAccountId());
            	joinAccount.setDepartmentId(dept.getId());
            	joinAccount.setCreateDate(new Date());
            	joinAccount.setUpdateDate(new Date());
            	if(null!=customerProperties && customerProperties.size()>0){
            		cLen=customerProperties.size();
            	}
            	
            	if(!Strings.isEmpty(metadataColumnList)){
            		for(int i=0;i<metadataColumnList.size();i++){
            			metadataColumn=metadataColumnList.get(i);
            			String columnName=metadataColumn.getColumnName();
            			String value="";
            			if(i<cLen){
            				value=customerProperties.get(i);
            			}
            			
            			if(!"".equals(value)){
            				try {
            					Method method=joinAccountCustomerFieldInfoManager.getSetMethod(columnName);
            					if(null==method){
            						throw new BusinessException("自定义字段: "+metadataColumn.getLabel()+"不存在！");
            					}
            					if(metadataColumn.getType()==0){
            						method.invoke(joinAccount, new Object[] { value });       
            					}
            					if(metadataColumn.getType()==1){
            						method.invoke(joinAccount, new Object[] { Double.valueOf(value) });       
            					}
            					if(metadataColumn.getType()==2){
            						method.invoke(joinAccount, new Object[] { Datetimes.parse(value, "yyyy-MM-dd") });       
            					}
            					if(metadataColumn.getType()==3){
            						String items = "";
            						String itemArr[] = value.split(",");
            						for(String item : itemArr){
            							try {
            								enumManagerNew.updateEnumItemRef(Long.valueOf(item));
            								if(Strings.isBlank(items)){
            									items = item;
            								}else{
            									items = items + "," + item;
            								}
										} catch (Exception e) {
											log.error("外单位属性枚举值有误： "+item);
										}
            						}
            						method.invoke(joinAccount, new Object[] { items });       
            					}
            				} catch (Exception e) {
            					log.error("保存外单位自定义属性信息失败！");
            					throw new BusinessException("保存外单位自定义属性信息失败！");
            				}
            			}
            		}
            	}
            	
            	joinAccountCustomerFieldInfos.add(joinAccount);
            }
            
            deptList.add(dept);
        }
        //实例化部门
        orgDao.insertOrgUnit(poList);
        orgCache.cacheUpdate(deptList);
        //保存外单位属性
        if(type==OrgConstants.ExternalType.Interconnect2.ordinal()){
        	joinAccountCustomerFieldInfoManager.addJoinAccounts(joinAccountCustomerFieldInfos);
        }

        return message;
    }
    
    @Override
    public List<V3xOrgDepartment> getChildDepartments(Long parentDepId, boolean firtLayer) throws BusinessException {
    	return getChildDepartments(parentDepId,firtLayer,null);
    }
    
    @Override
    public List<V3xOrgDepartment> getChildDepartments(Long parentDepId, boolean firtLayer,Integer externalType) throws BusinessException {
        List<V3xOrgDepartment> resultList = new UniqueList<V3xOrgDepartment>();

        V3xOrgUnit parentDep = this.orgCache.getV3xOrgEntityNoClone(V3xOrgUnit.class, parentDepId);
        if(parentDep == null || !parentDep.isValid()){
            return resultList;
        }

        String subDeptType=null;
        if(firtLayer){
        	subDeptType = OrgCache.SUBDEPT_OUTER_FIRST;
        }
        List<Long> deptIdsList = this.orgCache.getSubDeptList(parentDepId,subDeptType);
        List<V3xOrgDepartment> deptList = new UniqueList<V3xOrgDepartment>();
        for(Long deptId : deptIdsList){
        	V3xOrgDepartment dept = orgCache.getV3xOrgEntity(V3xOrgDepartment.class, deptId);
        	if(externalType==null || dept.getExternalType().equals(externalType)){
    			deptList.add(dept);
        	}
        }
        Collections.sort(deptList, CompareSortEntity.getInstance());
        return deptList;
    }
    
    @Override
    public List<V3xOrgDepartment> getChildUnits(Long parentDepId, boolean firtLayer) throws BusinessException {
        List<V3xOrgDepartment> resultList = new UniqueList<V3xOrgDepartment>();

        V3xOrgUnit parentDep = this.orgCache.getV3xOrgEntityNoClone(V3xOrgUnit.class, parentDepId);
        if(parentDep == null || !parentDep.isValid()){
            return resultList;
        }

        String subDeptType=null;
        if(firtLayer){
        	subDeptType = OrgCache.SUBDEPT_OUTER_FIRST;
        }
        List<Long> deptIdsList = this.orgCache.getSubDeptList(parentDepId,subDeptType);
        List<V3xOrgDepartment> deptList = new UniqueList<V3xOrgDepartment>();
        for(Long deptId : deptIdsList){
        	V3xOrgDepartment dept = orgCache.getV3xOrgEntity(V3xOrgDepartment.class, deptId);
        	deptList.add(dept);
        }
        Collections.sort(deptList, CompareSortEntity.getInstance());
        return deptList;
    }
    
    /*--------------工具方法----------------*/
    // 检查本集合中是否存在相同的属性值的对象
    @Override
    public boolean isExistRepeatProperty(List<? extends V3xOrgEntity> ents, String propertyName, Object value,
            V3xOrgEntity entity) throws BusinessException {
        try {
            for (V3xOrgEntity ent : ents) {
                if (!ent.getId().equals(entity.getId())) {
                    Object val = getEntityProperty(ent, propertyName);
                    if (val.equals(value)){
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            log.error("实体中不存在" + propertyName + "属性的get方法。", e);
        }
        
        return false;
    }
    
    protected Object getEntityProperty(V3xOrgEntity entity, String property) throws Exception {
        return OrgHelper.getProperty(entity, property);
    }
    
    @Override
    public Integer getMaxSortNum(String entityClassName, Long accountId, int externalType) throws BusinessException {
        return orgDao.getMaxSortId(OrgHelper.getEntityTypeBySimpleName(entityClassName), accountId,externalType);
    }
    
    @Override
    public OrganizationMessage deleteDepartment(V3xOrgDepartment dept) throws BusinessException {
    	OrganizationMessage result = new OrganizationMessage();
        if (dept == null) {
        	result.addErrorMsg(null, "实体对象为空！");
        	return result;
        }
        List<V3xOrgDepartment> deps = new ArrayList<V3xOrgDepartment>(1);
        deps.add(dept);
        return this.deleteDepartments(deps);
    }

    @Override
    public OrganizationMessage deleteDepartments(List<V3xOrgDepartment> depts) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        //1、校验 数据
        for (V3xOrgDepartment dept : depts) {
            //检查部门下是否存在成员
            if (isExistMemberByDept(dept)) {
            	message.addErrorMsg(dept,ResourceUtil.getString("MessageStatus.ACCOUNT_EXIST_MEMBER_ENABLE",dept.getName()));
                continue;
            }
            
            /**
             * 暂时没有组，先屏蔽todo
             */
            //检查部门下是否存在组
/*            if (isExistTeamByDept(dept)) {
            	//throw new BusinessException("部门下面存在组，不允许删除！");
                message.addErrorMsg(dept, ResourceUtil.getString("MessageStatus.DEPARTMENT_EXIST_TEAM);
                continue;
            }*/
            // 判断是否有有末级外单位
            List<V3xOrgDepartment> childsAccount = this.getChildDepartments(dept.getId(), false,OrgConstants.ExternalType.Interconnect2.ordinal());
            if(Strings.isNotEmpty(childsAccount)){
            	message.addErrorMsg(dept,ResourceUtil.getString("MessageStatus.DEPARTMENT_EXIST_EXTDEPARTMENT_ENABLE"));
            	continue;
            }
            //删除子机构
            List<V3xOrgDepartment> childsUnit = this.getChildDepartments(dept.getId(), false,OrgConstants.ExternalType.Interconnect1.ordinal());
            for (V3xOrgDepartment c : childsUnit) {
                this.updateEntity2Deleted(c);
                orgDao.update((OrgUnit) c.toPO());
                //删除子机构的管理员账号
                V3xOrgMember admin = this.getSubAdmin(c.getId());
                if(admin != null && !admin.getIsDeleted()){
                	this.deleteMember(admin);
                }
            }
            //2、删除符合条件的实体
            this.updateEntity2Deleted(dept);
            orgDao.update((OrgUnit) dept.toPO());
            V3xOrgMember admin = this.getSubAdmin(dept.getId());
            if(admin != null && !admin.getIsDeleted()){
            	this.deleteMember(admin);
            }
            message.addSuccessMsg(dept);
        }
        return message;
    }
    
    // 检查部门下是否存在成员
    protected boolean isExistMemberByDept(V3xOrgDepartment dept) throws BusinessException {
        List<V3xOrgMember> members = this.getMembersByDepartment(dept.getId(), false);
        boolean isAllMemberUnEnabled = true;
        for (V3xOrgMember mem : members) {
            if (mem.isValid()) {
                isAllMemberUnEnabled = false;
                break;
            }
        }
        if (!(ListUtils.EMPTY_LIST.equals(members) || isAllMemberUnEnabled)) {
            return true;
        }
        return false;
    }
    
    @Override
    public List<V3xOrgMember> getMembersByDepartment(Long departmentId, boolean firtLayer) throws BusinessException {
        V3xOrgDepartment dep = orgManager.getDepartmentById(departmentId);
        if (dep == null) {
            return Collections.emptyList();
        }
        
        List<V3xOrgMember> memberList = new UniqueList<V3xOrgMember>();

        List<Long> deptIds = new UniqueList<Long>();
        if (!firtLayer){
            deptIds = this.orgCache.getSubDeptList(departmentId);
        }

        deptIds.add(departmentId);
        List<V3xOrgRelationship> rels = orgCache.getDepartmentRelastionships(deptIds,OrgConstants.MemberPostType.Main);
        
        for (V3xOrgRelationship rel : rels) {     
            V3xOrgMember member = orgCache.getV3xOrgEntityNoClone(V3xOrgMember.class, rel.getSourceId());
            if (member != null && member.isValid() && !member.getIsAdmin()) {
                V3xOrgMember newMember = OrgHelper.cloneEntityImmutableDecorator(member);
                newMember.setSortId(rel.getSortId());
                memberList.add(newMember);
            }
        }
        
        Collections.sort(memberList, CompareSortEntity.getInstance());
        
        return memberList;
    }
    
    @Override
    public List<MemberPost> getMemberPostByDepartment(Long departmentId, boolean firtLayer) throws BusinessException {
        V3xOrgDepartment dep = orgManager.getDepartmentById(departmentId);
        if (dep == null) {
            return Collections.emptyList();
        }
        
        List<MemberPost> memberPostList = new UniqueList<MemberPost>();

        List<Long> deptIds = new UniqueList<Long>();
        if (!firtLayer){
            deptIds = this.orgCache.getSubDeptList(departmentId);
        }

        deptIds.add(departmentId);
        List<V3xOrgRelationship> rels = orgCache.getDepartmentRelastionships(deptIds,OrgConstants.MemberPostType.Main);
        
        for (V3xOrgRelationship rel : rels) {
            V3xOrgMember member = orgCache.getV3xOrgEntityNoClone(V3xOrgMember.class, rel.getSourceId());
            if (member != null && member.isValid() && !member.getIsAdmin()) {
            	MemberPost memberPost  = new MemberPost(rel);
            	memberPostList.add(memberPost);
            }
        }
        
        Collections.sort(memberPostList, CompareSortMemberPost.getInstance());
        return memberPostList;
    }
    
    @Override
    public List<V3xOrgMember> getMembersByPost(Long postId) throws BusinessException {
        return this.getMembersByPost(postId, null);
    }
    
    @Override
    public List<MemberPost> getMemberPostByPost(Long postId) throws BusinessException {
        return this.getMemberPostByPost(postId, null);
    }
    
    @Override
    public List<V3xOrgMember> getMembersByPost(Long postId, Long deptId) throws BusinessException {
        V3xOrgPost post = orgCache.getV3xOrgEntityNoClone(V3xOrgPost.class, postId);

        List<V3xOrgMember> memberList = new UniqueList<V3xOrgMember>();

        if (post == null || !post.isValid()){
            return memberList;
        }
        Long orgAccountId = post.getOrgAccountId();

        List<Long> postIds = new UniqueList<Long>();
        postIds.add(postId);

    	EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
        enummap.put(OrgConstants.RelationshipObjectiveName.objective1Id, postIds);

        List<V3xOrgRelationship> ents = orgCache.getV3xOrgRelationship(RelationshipType.Member_Post, (Long)null, orgAccountId, enummap);
        Collections.sort(ents, CompareSortRelationship.getInstance());

        Set<Long> childDeptIds = new HashSet<Long>();
        childDeptIds.add(deptId);
        if(deptId != null){
        	List<V3xOrgDepartment> childDepts = this.getChildDepartments(deptId, false, OrgConstants.ExternalType.Interconnect2.ordinal());
        	for(V3xOrgDepartment d : childDepts){
        		childDeptIds.add(d.getId());
        	}
        }
        
        for (V3xOrgRelationship ent : ents) {
        	if(ent.getObjective0Id() == null || ent.getObjective1Id() == null){
        		continue;
        	}
        	
        	if(deptId != null && !childDeptIds.contains(ent.getObjective0Id())){
        		continue;
        	}

            V3xOrgMember mem = (V3xOrgMember) orgCache.getV3xOrgEntityNoClone(V3xOrgMember.class, ent.getSourceId());
            if (mem != null && mem.isValid()) {
                memberList.add(OrgHelper.cloneEntityImmutableDecorator(mem));
            }
        }
        return memberList;
    }
    
    @Override
    public List<MemberPost> getMemberPostByPost(Long postId, Long deptId) throws BusinessException {
        V3xOrgPost post = orgCache.getV3xOrgEntityNoClone(V3xOrgPost.class, postId);

        List<MemberPost> memberPostList = new UniqueList<MemberPost>();

        if (post == null || !post.isValid()){
            return memberPostList;
        }
        Long orgAccountId = post.getOrgAccountId();

        List<Long> postIds = new UniqueList<Long>();
        postIds.add(postId);

    	EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
        enummap.put(OrgConstants.RelationshipObjectiveName.objective1Id, postIds);

        List<V3xOrgRelationship> ents = orgCache.getV3xOrgRelationship(RelationshipType.Member_Post, (Long)null, orgAccountId, enummap);
        Collections.sort(ents, CompareSortRelationship.getInstance());

        Set<Long> childDeptIds = new HashSet<Long>();
        childDeptIds.add(deptId);
        if(deptId != null){
        	List<V3xOrgDepartment> childDepts = this.getChildDepartments(deptId, false, OrgConstants.ExternalType.Interconnect2.ordinal());
        	for(V3xOrgDepartment d : childDepts){
        		childDeptIds.add(d.getId());
        	}
        }
        
        for (V3xOrgRelationship ent : ents) {
        	if(ent.getObjective0Id() == null || ent.getObjective1Id() == null){
        		continue;
        	}
        	
        	if(deptId != null && !childDeptIds.contains(ent.getObjective0Id())){
        		continue;
        	}

            V3xOrgMember mem = (V3xOrgMember) orgCache.getV3xOrgEntityNoClone(V3xOrgMember.class, ent.getSourceId());
            if (mem != null && mem.isValid()) {
            	MemberPost cntPost = new MemberPost(ent);
            	memberPostList.add(cntPost);
            }
        }
        Collections.sort(memberPostList, CompareSortMemberPost.getInstance());
        return memberPostList;
    }
    
    /**
     * 统一更新删除实体时三个状态同时修改
     * <code>isDeleted</code>
     * <code>enabled</code>
     * <code>updateTime</code>
     * @param entity
     */
    private void updateEntity2Deleted(V3xOrgEntity entity) {
        entity.setIsDeleted(true);
        entity.setEnabled(false);
        entity.setUpdateTime(new Date());
    }
    
    @Override
    public OrganizationMessage updateDepartment(V3xOrgDepartment dept) throws BusinessException {
        List<V3xOrgDepartment> departments = new ArrayList<V3xOrgDepartment>(1);
        departments.add(dept);
        return this.updateDepartments(departments);
    }
    
    @Override
    public OrganizationMessage updateDepartment(V3xOrgDepartment dept,V3xOrgMember subManager) throws BusinessException {
    	OrganizationMessage message = new OrganizationMessage();
    	V3xOrgMember admin = this.getSubAdmin(dept.getId());
    	if(admin == null & subManager == null){
    		return updateDepartment(dept);
    	}
    	
        if(subManager != null){//删除管理员
        	if(admin == null){//原来没有管理员，现在有--新增
                if (principalManager.isExist(subManager.getV3xOrgPrincipal().getLoginName())) {
                	message.addErrorMsg(subManager, "管理员登录名重复");
                    return message;
                }
        	}else{//原来有，现在也有--更新
				try {
					String oldLoginName = principalManager.getLoginNameByMemberId(admin.getId());
					//修改管理员
					if(!subManager.getV3xOrgPrincipal().getLoginName().equals(oldLoginName)) {
						if (principalManager.isExist(subManager.getV3xOrgPrincipal().getLoginName())) {
							message.addErrorMsg(subManager, "管理员登录名重复");
							return message;
						}
					}
				} catch (NoSuchPrincipalException e) {
					log.error("判断登录名重复失败！",e);
				}
        	}
        }
        
        List<V3xOrgDepartment> departments = new ArrayList<V3xOrgDepartment>(1);
        departments.add(dept);
        message =  this.updateDepartments(departments);
        if(!message.isSuccess()){
            return message;
        }
        
        if(subManager == null){//删除管理员
        	//删除单位管理员用户和账号
        	this.deleteMember(admin);
        }else{
        	if(admin == null){//原来没有管理员，现在有--新增
        		subManager.setIsAdmin(true);
                subManager.setIsInternal(false);
                subManager.setExternalType(OrgConstants.ExternalType.Interconnect1.ordinal());
                subManager.setOrgDepartmentId(dept.getId());
                subManager.setEnabled(dept.getEnabled());
                subManager.setOrgAccountId(dept.getOrgAccountId());
                List<OrgMember> _relMember = new ArrayList<OrgMember>(1);
                _relMember.add((OrgMember) subManager.toPO());
                orgDao.insertOrgMember(_relMember);
                //实例化管理员账号
                principalManager.insert(subManager.getV3xOrgPrincipal());
                //TODO 这里ID临时写死，由于整个系统只有一个子机构
                addRole2Entity(OrgConstants.VJOIN_SUBMANAGER_ROLE_ID, dept.getId(), subManager);
        	}else{//原来有，现在也有--更新
        		this.updateMember(subManager);
        	}
        }
        message.addSuccessMsg(dept);
        return message;
        
    }

    @Override
    public OrganizationMessage updateDepartments(List<V3xOrgDepartment> depts) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        int type = depts.get(0).getExternalType();
        List<MetadataColumnBO> metadataColumnList=this.getCustomerAccountProperties(depts.get(0).getOrgAccountId());
        
        for (V3xOrgDepartment dept : depts) {
            if (Strings.isNotBlank(dept.getCode())) {
                //判断部门编码是否重复
                boolean isDuplicated = isPropertyDuplicated(V3xOrgDepartment.class.getSimpleName(), "code",
                        dept.getCode(), dept.getOrgAccountId(), dept.getId());
                if (isDuplicated) {
                    message.addErrorMsg(dept, ResourceUtil.getString("Vjoin.DEPARTMENT_REPEAT_CODE"));
                    return message;
                }
            }
            V3xOrgDepartment oldDept = orgManager.getEntityById(V3xOrgDepartment.class, dept.getId());
            //如果更改了启用标志为启用，则要判断上级部门是否启用
            if (dept.getEnabled()) {
                if (dept.getSuperior() == -1L || !orgManager.getUnitById(dept.getSuperior()).getEnabled()) {
                    message.addErrorMsg(dept, ResourceUtil.getString("MessageStatus.VJOINDEPARTMENT_PARENTDEPT_DISABLED"));
                    continue;
                }
            } else {
                //如果更改了启用标志停用，要停用所有的下级部门            	
            	boolean canDisable = true;
            	List<V3xOrgDepartment> child = this.getChildUnits(dept.getId(),false);
                for (V3xOrgDepartment v3xOrgDepartment : child) {
                    if (v3xOrgDepartment.isValid()) {
                    	if(v3xOrgDepartment.getExternalType().equals(OrgConstants.ExternalType.Interconnect1.ordinal())){
                    		message.addErrorMsg(dept, ResourceUtil.getString("MessageStatus.DEPARTMENT_EXIST_DEPARTMENT_ENABLE"));
                    	}else{
                    		message.addErrorMsg(dept, ResourceUtil.getString("MessageStatus.DEPARTMENT_EXIST_EXTDEPARTMENT_ENABLE"));
                    	}
                        canDisable = false;
                        break;
                    }
                }
                
                if(canDisable){
                	for (V3xOrgDepartment v3xOrgDepartment : child) {
                		v3xOrgDepartment.setEnabled(false);
                	}
                }else{
                	continue;
                }
                
                //如果更改了启用标志为停用，则部门下存在人员时不允许
                if (isExistMemberByDept(dept)) {
                    message.addErrorMsg(dept, "外部单位下存在外部人员，不允许停用。操作失败！");
                    continue;
                }
            }
        	if(orgManager.getUnitById(dept.getSuperior()).getPath().contains(orgManager.getDepartmentById(dept.getId()).getPath())){
        		message.addErrorMsg(dept, ResourceUtil.getString("MessageStatus.DEPARTMENT_PARENTDEPT_ISCHILD"));
        		continue;
            }
        	
            //如果修改了上级单位再重新计算Path
        	List<V3xOrgUnit> childUnits = new ArrayList<V3xOrgUnit>();
            if (!Strings.equals(dept.getSuperior(), oldDept.getSuperior())
                    || (!oldDept.isValid() && dept.isValid())) {
                String path = OrgHelper.getPathByPid4Add(V3xOrgUnit.class, dept.getSuperior(),null);
                dept.setPath(path);
                childUnits = orgCache.getChildUnitsByPid(V3xOrgUnit.class, dept.getId());
                
            } else {
                dept.setPath(oldDept.getPath());
            }
            
            //校验同级部门名称是否重复
            List<V3xOrgDepartment> brother = this.getChildUnits(dept.getSuperior(), true);
            boolean isduplicated = isExistRepeatProperty(brother, "name", dept.getName(), dept);
            if (isduplicated) {
            	if(dept.getExternalType().equals(OrgConstants.ExternalType.Interconnect1.ordinal())){
            		message.addErrorMsg(dept, ResourceUtil.getString("Vjoin.UNIT_REPEAT_NAME",dept.getName()));
            	}else{
            		message.addErrorMsg(dept, ResourceUtil.getString("Vjoin.ACCOUNT_REPEAT_NAME",dept.getName()));
            	}
            	continue;
            }
            
            if(type==OrgConstants.ExternalType.Interconnect2.ordinal()){
                JoinAccount joinAccount = joinAccountCustomerFieldInfoManager.getByDepartmentId(dept.getId());
                boolean isExist=true;
            	int cLen=0;
            	List<String> customerProperties = dept.getCustomerProperties();
            	MetadataColumnBO metadataColumn;
            	if(joinAccount==null){
            		joinAccount = new JoinAccount();
            		joinAccount.setId(UUIDLong.longUUID());
            		joinAccount.setOrgAccountId(dept.getOrgAccountId());
            		joinAccount.setDepartmentId(dept.getId());
            		joinAccount.setCreateDate(new Date());
            		joinAccount.setUpdateDate(new Date());
            	}else{
            		joinAccount.setUpdateDate(new Date());
            	}
            	if(null!=customerProperties && customerProperties.size()>0){
            		cLen=customerProperties.size();
            	}
            	
            	if(!Strings.isEmpty(metadataColumnList)){
            		for(int i=0;i<metadataColumnList.size();i++){
            			metadataColumn=metadataColumnList.get(i);
            			String columnName=metadataColumn.getColumnName();
            			String value="";
            			if(i<cLen){
            				value=customerProperties.get(i);
            			}
            			
        				try {
        					Method method=joinAccountCustomerFieldInfoManager.getSetMethod(columnName);
        					if(null==method){
        						throw new BusinessException("自定义字段: "+metadataColumn.getLabel()+"不存在！");
        					}
        					if(metadataColumn.getType()==0){
        						String saveValue=(null==value || "".equals(value))?"":String.valueOf(value);
        						method.invoke(joinAccount, new Object[] { saveValue });       
        					}
        					if(metadataColumn.getType()==1){
        						Double saveValue=(null==value || "".equals(value))?null:Double.valueOf(String.valueOf(value));
        						method.invoke(joinAccount, new Object[] { saveValue });       
        					}
        					if(metadataColumn.getType()==2){
        						String saveValue=(null==value || "".equals(value))?"":String.valueOf(value);
        						method.invoke(joinAccount, new Object[] { Strings.isBlank(saveValue)?"":Datetimes.parse(saveValue, "yyyy-MM-dd") });       
        					}
        					if(metadataColumn.getType()==3){
        						String saveValue=(null==value || "".equals(value))?"":String.valueOf(value);
        						String items = "";
        						String itemArr[] = saveValue.split(",");
        						for(String item : itemArr){
        							try {
        								enumManagerNew.updateEnumItemRef(Long.valueOf(item));
        								if(Strings.isBlank(items)){
        									items = item;
        								}else{
        									items = items + "," + item;
        								}
									} catch (Exception e) {
										log.error("外单位属性枚举值有误： "+item);
									}
        						}
        						method.invoke(joinAccount, new Object[] { items });     
        					}
        				} catch (Exception e) {
        					log.error("保存外单位自定义属性信息失败！");
        					throw new BusinessException("保存外单位自定义属性信息失败！");
        				}
            		}
            	}
            	
                if(isExist){
                	joinAccountCustomerFieldInfoManager.updateJoinAccount(joinAccount);
                }else{
                	joinAccountCustomerFieldInfoManager.addJoinAccount(joinAccount);
                }
            }
            
            dept.setCreateTime(oldDept.getCreateTime());
            orgDao.update((OrgUnit) dept.toPO());
            
            message.addSuccessMsg(dept);
            
            for (V3xOrgUnit c : childUnits) {
                c.setPath(c.getPath().replaceFirst(oldDept.getPath(), dept.getPath()));
                orgDao.update((OrgUnit) OrgHelper.boTopo(c));
            }
        }
        return message;
    }
    
    @Override
    public OrganizationMessage addPost(V3xOrgPost post) throws BusinessException {
    	OrganizationMessage result = new OrganizationMessage();
        if (post == null){
        	result.addErrorMsg(null, "实体对象为空！");
        	return result;
        }
        List<V3xOrgPost> deps = new ArrayList<V3xOrgPost>(1);
        deps.add(post);
        return this.addPosts(deps);
    }

    @Override
    public OrganizationMessage addPosts(List<V3xOrgPost> posts) throws BusinessException {
    	OrganizationMessage result = new OrganizationMessage();
        if (null == posts) {
        	result.addErrorMsg(null, "岗位实体对象列表为空！");
        	return result;
        }
        OrganizationMessage message = new OrganizationMessage();
        List<OrgPost> poList = new ArrayList<OrgPost>();
        
        for (V3xOrgPost post : posts) {
            //判断名称是否重复
            boolean isduplicated = isPropertyDuplicated(V3xOrgPost.class, "name", post.getName(), post.getOrgAccountId());
            if (isduplicated) {
                message.addErrorMsg(post, ResourceUtil.getString("MessageStatus.POST_REPEAT_NAME",post.getName()));
                continue;
            }

            /**默认初始化一些属性，外部传入可以不必考虑这些初始化属性*/
            post.setIdIfNew();
            post.setStatus(OrgConstants.ORGENT_STATUS.NORMAL.ordinal());
            if(null == post.getSortId()) {
                post.setSortId(Long.valueOf(getMaxSortNum(V3xOrgPost.class.getSimpleName(), post.getOrgAccountId(),post.getExternalType())) + 1);
            }
            /*********/
            poList.add((OrgPost) post.toPO());
            message.addSuccessMsg(post);
        }
        // 2、实例化岗位
        orgDao.insertOrgPost(poList);

        return message;
    }
    
    @Override
    public OrganizationMessage updatePost(V3xOrgPost post) throws BusinessException {
        List<V3xOrgPost> posts = new ArrayList<V3xOrgPost>();
        posts.add(post);
        return this.updatePosts(posts);
    }
    
    @Override
    public OrganizationMessage updatePosts(List<V3xOrgPost> posts) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        for (V3xOrgPost post : posts) {
            V3xOrgPost oldPost = orgManager.getPostById(post.getId());
            //如果是集团基准岗，则同步修改各单位绑定或引用的基准岗
            // 判断名称重复
            if(!post.getName().equals(orgManager.getPostById(post.getId()).getName())){
                boolean isduplicated = isPropertyDuplicated(V3xOrgPost.class, "name", post.getName(), post.getOrgAccountId());
                if (isduplicated) {
                    message.addErrorMsg(post, ResourceUtil.getString("MessageStatus.POST_REPEAT_NAME",post.getName()));
                    continue;
                }
            }
            
            //岗位下如果有人员,则不能停用
        	List<V3xOrgMember> _tempMemberList = orgManager.getMembersByPost(post.getId());
            if (!post.getEnabled() && oldPost.getEnabled() && _tempMemberList != null && _tempMemberList.size() > 0) {
                message.addErrorMsg(post, ResourceUtil.getString("MessageStatus.POST_EXIST_MEMBER"));
                continue;
            }
            post.setCreateTime(oldPost.getCreateTime());
            orgDao.update((OrgPost) post.toPO());
            message.addSuccessMsg(post);
        }
        return message;
    }
    
    @Override
    public OrganizationMessage deletePost(V3xOrgPost post) throws BusinessException {
    	OrganizationMessage result = new OrganizationMessage();
        if (post == null){
        	result.addErrorMsg(null, "实体对象为空！");
        	return result;
        }
        List<V3xOrgPost> posts = new ArrayList<V3xOrgPost>(1);
        posts.add(post);
        return this.deletePosts(posts);
    }
    
    @Override
    public OrganizationMessage deletePosts(List<V3xOrgPost> posts) throws BusinessException {
    	OrganizationMessage message = new OrganizationMessage();
        if (posts == null) {
        	message.addErrorMsg(null, "实体对象列表为空！");
        	return message;
        }
        Set<V3xOrgPost> posts2delete = new HashSet<V3xOrgPost>();
        //TODO 1、校验岗位，集团基准岗等，同时删除关系
        for (V3xOrgPost post : posts) {
            //岗位下如果有人员,则不能删除
            List<V3xOrgMember> _tempMemberList = orgManager.getMembersByPost(post.getId());
            if(_tempMemberList != null && _tempMemberList.size() > 0){
                message.addErrorMsg(post, ResourceUtil.getString("MessageStatus.POST_EXIST_MEMBER"));
                continue;
            }
            posts2delete.add(post);
        }
        for (V3xOrgPost post : posts2delete) {
            this.updateEntity2Deleted(post);
            // 2、实例化
            orgDao.update((OrgPost) post.toPO());
            message.addSuccessMsg(post);
            
        }
        return message;
    }
    
    @Override
    public OrganizationMessage addMember(V3xOrgMember member) throws BusinessException {
        List<V3xOrgMember> members = new ArrayList<V3xOrgMember>(1);
        members.add(member);
        return this.addMembers(members);
    }
    
    @Override
    public OrganizationMessage addMembers(List<V3xOrgMember> members) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        if(Strings.isEmpty(members)) {return message;}
        int size = members.size();
        List<OrgMember> poList = new ArrayList<OrgMember>(size);
        List<OrgRelationship> rels = new ArrayList<OrgRelationship>(size);
        List<V3xOrgPrincipal> principals = new ArrayList<V3xOrgPrincipal>(size);
        List<V3xOrgMember> sucessMembers = new ArrayList<V3xOrgMember>(size);
        //许可数校验
        int membernums = 0;
        for (V3xOrgMember v3xOrgMember : members) {
            if(v3xOrgMember.isValid()){
                membernums++;
            }
        }
        
        int permissionType = (Integer) MclclzUtil.invoke(c, "getVJoinPermissionType");
        if(permissionType == LicenseConst.PERMISSION_TYPE_RES){
            int hasSize = this.getAllMembers(this.getDefaultVjoinAccount(null)).size();
            int maxRegister = (Integer) MclclzUtil.invoke(c, "getVJoinMaxRegisterSize");
            if(membernums + hasSize > maxRegister){
                message.addErrorMsg(null, "外部人员已达到最大可注册数量，不允许添加人员！ ");
                return message;
            }
        }
        
        //Todo
        Long accountId = members.get(0).getOrgAccountId();
        for (V3xOrgMember member : members) {
            member.setIdIfNew();
            member.setIsInternal(false);
            member.setExternalType(OrgConstants.ExternalType.Interconnect1.ordinal());
            //2013-04-02需求变更人员编码要保证唯一
            if (Strings.isNotBlank(member.getCode())) {
                //判断人员编码是否重复
                boolean isDuplicated = isPropertyDuplicated(V3xOrgMember.class.getSimpleName(), "code", member.getCode(),member.getOrgAccountId());
                if (isDuplicated) {
                    message.addErrorMsg(member, ResourceUtil.getString("MessageStatus.MEMBER_REPEAT_CODE"));
                    return message;
                }
            }
            if(null == member.getV3xOrgPrincipal() || Strings.isBlank(member.getV3xOrgPrincipal().getLoginName())) {
                message.addErrorMsg(member, ResourceUtil.getString("MessageStatus.PRINCIPAL_NOT_EXIST"));
                continue;
            }
            // 校验登录名是否已存在
            if (principalManager.isExist(member.getV3xOrgPrincipal().getLoginName())) {
            	//message.addErrorMsg(member, ResourceUtil.getString("MessageStatus.PRINCIPAL_REPEAT_NAME"));
            	message.addErrorMsg(member, "登录名重复");
                continue;
            }
            if(null == member.getSortId()) {
                member.setSortId(Long.valueOf(getMaxSortNum(V3xOrgMember.class.getSimpleName(), member.getOrgAccountId(),OrgConstants.ExternalType.Interconnect1.ordinal())) + 1);
            }
            poList.add((OrgMember) member.toPO());
            //实例化账号
            principals.add(member.getV3xOrgPrincipal());
            //关系表维护，主岗关系
            MemberPost mainPost = MemberPost.createMainPost(member);
            rels.add((OrgRelationship) mainPost.toRelationship().toPO());
            
            sucessMembers.add(member);
            message.addSuccessMsg(member);
        }
        if (poList == null || poList.size() == 0) {
            return message;
        }
        //批量插入人员信息表
        orgDao.insertOrgMember(poList);
        defaultMemberRole4Add(sucessMembers,accountId);
        orgDao.insertOrgRelationship(rels);
        principalManager.insertBatch(principals);
        
        for (V3xOrgMember v3xOrgMember : sucessMembers) {
            // 触发事件
            AddJoinMemberEvent event = new AddJoinMemberEvent(this);
            if(sucessMembers.size() > 1) {
                event.setBatch(true);
            }
            event.setMember(v3xOrgMember);
            EventDispatcher.fireEvent(event);
        }
        
        return message;
    }
    
    @Override
    public OrganizationMessage updateMember(V3xOrgMember member) throws BusinessException {
        List<V3xOrgMember> members = new ArrayList<V3xOrgMember>(1);
        members.add(member);
        return this.updateMembers(members);
    }

    @Override
    public OrganizationMessage updateMembers(List<V3xOrgMember> members) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        if(members.isEmpty()) {
            return message;
        }
        
        //许可数校验
        int membernums = 0;
        for (V3xOrgMember v3xOrgMember : members) {
            if (!v3xOrgMember.getIsAdmin() && v3xOrgMember.isValid() == true 
                    && orgManager.getMemberById(v3xOrgMember.getId()).isValid() == false) {
                membernums++;
            }
        }
        
        
        int permissionType = (Integer) MclclzUtil.invoke(c, "getVJoinPermissionType");
        if(permissionType == LicenseConst.PERMISSION_TYPE_RES){
            int hasSize = this.getAllMembers(this.getDefaultVjoinAccount(null)).size();
            int maxRegister = (Integer) MclclzUtil.invoke(c, "getVJoinMaxRegisterSize");
            if(membernums + hasSize > maxRegister){
                message.addErrorMsg(null, "更新的人员数量大于单位剩余的可注册数量，不允许更新人员！");
                return message;
            }
        }
        
        List<OrgRelationship> rels = new ArrayList<OrgRelationship>(members.size());
        
        EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds = new EnumMap<OrgConstants.RelationshipObjectiveName, Object>(
                OrgConstants.RelationshipObjectiveName.class);
        objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective5Id, OrgConstants.MemberPostType.Main.name());
        
        for (V3xOrgMember member : members) {
            //2013-04-02需求变更人员编码要保证唯一
            if (Strings.isNotBlank(member.getCode()) && member.isValid()) {
                //判断人员编码是否重复
                boolean isDuplicated = isPropertyDuplicated(V3xOrgMember.class.getSimpleName(), "code",member.getCode(), member.getOrgAccountId(), member.getId());
                if (isDuplicated) {
                    message.addErrorMsg(member, ResourceUtil.getString("MessageStatus.MEMBER_REPEAT_CODE"));
                    continue;
                }
            }
            //检验人员所在部门、岗位、职务不可用直接不能启用 //OA-59629
            if(!member.getIsAdmin() && member.getEnabled()) {
                V3xOrgDepartment dept = orgManager.getDepartmentById(member.getOrgDepartmentId());
                if(null == dept || !dept.isValid()) {
                    message.addErrorMsg(member, ResourceUtil.getString("MessageStatus.MEMBER_DEPARTMENT_DISABLED"));
                    continue;
                }
            }
            //人员无效（人员删除、离职），删除账号；未分配账号保留
            if(member.getIsDeleted() || !member.getIsLoginable() || member.getState().intValue() == OrgConstants.MEMBER_STATE.RESIGN.ordinal()){
                //Fix OA-11374 增加判断，有可能人员离职帐号就已经被删除了，此时再删除会引起事务报错
                if(principalManager.isExist(member.getId())) {
                    principalManager.delete(member.getId());
                }
            } else{
                V3xOrgPrincipal principal = member.getV3xOrgPrincipal();
                if(null != principal) {
                    if(!principalManager.isExist(member.getId())){ //之前没有账号，给补一个
                        if(principalManager.isExist(principal.getLoginName())){
                            //message.addErrorMsg(member, ResourceUtil.getString("MessageStatus.PRINCIPAL_REPEAT_NAME"));
                        	message.addErrorMsg(member, "登录名重复");
                            continue;
                        }
                        principalManager.insert(principal);
                    }
                    else{
                        //判断登陆名重名
                        String oldPrincipal = null;
                        try {
                            oldPrincipal = principalManager.getLoginNameByMemberId(member.getId());
                        }
                        catch (NoSuchPrincipalException e) {
                            //ignore
                        }

                        if(!principal.getLoginName().equals(oldPrincipal)) {
                            if(principalManager.isExist(principal.getLoginName())) {
                                //message.addErrorMsg(member, ResourceUtil.getString("MessageStatus.PRINCIPAL_REPEAT_NAME"));
                            	message.addErrorMsg(member, "登录名重复");
                                continue;
                            }
                        }
                        
                        principalManager.update(member.getV3xOrgPrincipal());
                    }
                }
            }
            //关系表维护，主岗关系
            MemberPost mainPost = MemberPost.createMainPost(member);
            rels.add((OrgRelationship) mainPost.toRelationship().toPO());
            
            V3xOrgMember oldMember = orgManager.getEntityById(V3xOrgMember.class, member.getId());
            //实例化
            member.setCreateTime(oldMember.getCreateTime());
            orgDao.update((OrgMember) member.toPO());
            
          //修改主岗方法 steven
            orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Post.name(), member.getId(), member.getOrgAccountId(), null);
            orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Post.name(), member.getId(), null, objectiveIds);
            message.addSuccessMsg(member);
        }
        orgDao.insertOrgRelationship(rels);

        return message;
    }
    
    @Override
    public OrganizationMessage deleteMember(V3xOrgMember member) throws BusinessException {
        List<V3xOrgMember> members = new ArrayList<V3xOrgMember>(1);
        members.add(member);
        return this.deleteMembers(members);
    }

    @Override
    public OrganizationMessage deleteMembers(List<V3xOrgMember> members) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        for (V3xOrgMember member : members) {
            if(member == null){
                continue;
            }
            String oldLoginName = member.getLoginName();
            
            //信息表逻辑删除
            this.updateEntity2Deleted(member);
            member.setIsLoginable(false);
            //实例化
            orgDao.update((OrgMember) member.toPO());
            //账号表物理删除
            //20121228lilong增加判断，如果是离职人员帐号已经被删除了这里再次删除人员时会有报错导致事务回滚
            if(principalManager.isExist(member.getId())) {
                principalManager.delete(member.getId());
            }
            //清除这个人在关系表中的记录
            orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Post.name(), member.getId(), null, null);
            orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Member_Role.name(), member.getId(), null, null);
            
            //删除互访权限 todo
            this.deleteAccessRelation(member.getId(), member.getOrgAccountId());
            
            //将用户踢下下线
            OnlineRecorder.moveToOffline(oldLoginName, LoginOfflineOperation.adminKickoff);
            message.addSuccessMsg(member);
        }
        return message;
    }
    
    @Override
    public List<V3xOrgAccount> getAllAccounts() throws BusinessException{
        List<V3xOrgAccount> allAccounts = orgCache.getAllAccounts(OrgConstants.ExternalType.Interconnect3.ordinal());
        Collections.sort(allAccounts, CompareSortEntity.getInstance());
        return allAccounts;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<V3xOrgDepartment> getAllDepartments(Long accountID, Boolean enable,String condition, Object feildvalue) throws BusinessException {
    	accountID = getDefaultVjoinAccount(accountID);
    	if(accountID == null){
    		return null;
    	}
        return (List<V3xOrgDepartment>) OrgHelper.listPoTolistBo(orgDao.getAllUnitPO(OrgConstants.UnitType.Department,
                accountID, enable, false, condition, feildvalue,null));
    }
    
    @Override
    public List<V3xOrgDepartment> getAllDepartments(Long accountId,Integer externalType) throws BusinessException {
    	accountId = this.getDefaultVjoinAccount(accountId);
    	if(accountId == null){
    		return null;
    	}
        List<V3xOrgDepartment> list = orgCache.getAllV3xOrgEntity(V3xOrgDepartment.class, accountId,externalType);
        List<V3xOrgDepartment> deptList = new ArrayList<V3xOrgDepartment>();
        for (Iterator<V3xOrgDepartment> it = list.iterator(); it.hasNext();) {
        	V3xOrgDepartment dept = (V3xOrgDepartment) it.next();
            if (dept.isValid()) {
            	deptList.add(dept);
            }
        }
        Collections.sort(deptList, CompareSortEntity.getInstance());
        return deptList;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<V3xOrgRole> getAllRoles(Long accountID,String condition, Object feildvalue) throws BusinessException {
    	accountID = getDefaultVjoinAccount(accountID);
    	if(accountID == null){
    		return null;
    	}
        return (List<V3xOrgRole>) OrgHelper.listPoTolistBo(orgDao.getAllRolePO(accountID, null, condition, feildvalue,null));
    }
    
    @Override
    public List<V3xOrgRole> getAllRoles(Long accountId) throws BusinessException {
    	accountId = this.getDefaultVjoinAccount(accountId);
    	if(accountId == null){
    		return null;
    	}
        List<V3xOrgRole> list = orgCache.getAllV3xOrgEntity(V3xOrgRole.class, accountId,null);
        List<V3xOrgRole> roleList = new ArrayList<V3xOrgRole>();
        for (Iterator<V3xOrgRole> it = list.iterator(); it.hasNext();) {
        	V3xOrgRole role = (V3xOrgRole) it.next();
        	if(role.getExternalType().equals(OrgConstants.ExternalType.Inner.ordinal())){
        		continue;
        	}
            if (role.isValid()) {
            	roleList.add(role);
            }
        }
        Collections.sort(roleList, CompareSortEntity.getInstance());
        return roleList;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<V3xOrgPost> getAllPosts(Long accountID,Boolean enable,String condition, Object feildvalue) throws BusinessException {
    	accountID = getDefaultVjoinAccount(accountID);
    	if(accountID == null){
    		return null;
    	}
        return (List<V3xOrgPost>) OrgHelper.listPoTolistBo(orgDao.getAllPostPO(accountID, enable, condition, feildvalue, null));
    }
    
    @Override
    public List<V3xOrgPost> getAllPosts(Long accountID) throws BusinessException {
    	accountID = getDefaultVjoinAccount(accountID);
    	if(accountID == null){
    		return null;
    	}
        List<V3xOrgPost> list = orgCache.getAllV3xOrgEntity(V3xOrgPost.class, accountID,OrgConstants.ExternalType.Interconnect1.ordinal());
        List<V3xOrgPost> postList = new ArrayList<V3xOrgPost>();
        for (Iterator<V3xOrgPost> it = list.iterator(); it.hasNext();) {
            V3xOrgPost post = (V3xOrgPost) it.next();
            if (post.isValid()) {
                postList.add(post);
            }
        }
        Collections.sort(postList, CompareSortEntity.getInstance());
        return postList;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<V3xOrgMember> getAllMembers(Long accountId, Boolean enable,String condition,Object feildvalue) throws BusinessException{
    	accountId = getDefaultVjoinAccount(accountId);
    	if(accountId == null){
    		return null;
    	}
        List<OrgMember> orgMembers = orgDao.getAllMemberPOByAccountId(accountId, null,false, enable, condition,feildvalue, null);
        return (List<V3xOrgMember>) OrgHelper.listPoTolistBo(orgMembers);
    }
    
    @Override
    public List<V3xOrgMember> getAllMembers(Long accountId) throws BusinessException {
    	accountId = this.getDefaultVjoinAccount(accountId);
    	if(accountId == null){
    		return null;
    	}
        List<V3xOrgMember> list = orgCache.getAllV3xOrgEntity(V3xOrgMember.class, accountId,OrgConstants.ExternalType.Interconnect1.ordinal());
        List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
        for (Iterator<V3xOrgMember> it = list.iterator(); it.hasNext();) {
        	V3xOrgMember member = (V3xOrgMember) it.next();
            if (member.isValid() && !member.getIsAdmin()) {
            	memberList.add(member);
            }
        }
        Collections.sort(memberList, CompareSortEntity.getInstance());
        return memberList;
    }
    
    @Override
    public void deleteAccessRelation(Long memberId, Long orgAccountId) throws BusinessException {
        orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.External_Access.name(), memberId, orgAccountId, null);
    }
    
    @Override
    public void deleteAccessRelationByType(Long memberId, Long orgAccountId,OrgConstants.ExternalAccessType externalAccessType) throws BusinessException {
        EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
        objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective5Id, externalAccessType.name());
        orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.External_Access.name(), memberId, orgAccountId, objectiveIds);
    }
    
    @Override
    public Map<String,String> getAccessMember(V3xOrgEntity vjoinEntity,String externalAccessType) throws BusinessException {
		Map map = new HashMap<String, String>();
		String ids="";
		String names="";
    	EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
    	   enummap.put(OrgConstants.RelationshipObjectiveName.objective5Id, externalAccessType);
    	List<V3xOrgRelationship> rellist = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.External_Access, vjoinEntity.getId(), vjoinEntity.getOrgAccountId(), enummap);
    	Collections.sort(rellist, CompareSortRelationship.getInstance());
    	for (V3xOrgRelationship orgRelationship : rellist) {
			List<EntityIdTypeDsBO> postlist = new ArrayList<EntityIdTypeDsBO>();
			V3xOrgEntity entity = orgManager.getEntityById(OrgHelper.getV3xClass(orgRelationship.getObjective6Id()), orgRelationship.getObjective0Id());
			if(entity == null){
				continue;
			}
			
			EntityIdTypeDsBO en = new EntityIdTypeDsBO();
			en.setId(entity.getId());
			en.setEntity(entity);
			en.setDsc(orgRelationship.getObjective6Id());
			en.setDscType(orgRelationship.getObjective7Id());
			postlist.add(en);
			if(Strings.isBlank(ids)){
				ids = OrgHelper.parseElementsExt(postlist, "id", "dsc","dscType");
				names = OrgHelper.showOrgEntitiesExt(postlist, "id", "dsc","dscType", null);
			}else{
				ids = ids +","+OrgHelper.parseElementsExt(postlist, "id", "dsc","dscType");
				names = names +","+OrgHelper.showOrgEntitiesExt(postlist, "id", "dsc","dscType", null);
			}
		}
    	map.put("value", ids);
    	map.put("text", names);
    	
    	return map;
    }
    
    @Override
	public void dealExternalAccess(Long entityId,Map<String,String> accessMap,boolean isAdd) throws BusinessException{
		V3xOrgEntity entity = orgManager.getEntityOnlyById(entityId);
		if(entity == null) {
			//再按照类型取数据，有可能是停用的数据。
			//这里先只处理人员
			V3xOrgMember member = orgManager.getMemberById(entityId);
			if(member!=null && !member.getIsDeleted() && !member.getEnabled()){//只取停用人员
				entity = member;
			}else{
				return;
			}
		}
		//添加互访范围设置
		List<V3xOrgRelationship> relList = new ArrayList<V3xOrgRelationship>();
		if(entity instanceof V3xOrgMember){//人员的互访权限
			//如果这个人已经存在，先删除这个人的内外互访范围设置
			if(!isAdd){
				if(accessMap.containsKey(OrgConstants.ExternalAccessType.Access.name()) && accessMap.containsKey(OrgConstants.ExternalAccessType.BeAccessed.name())){
					deleteAccessRelation(entityId, entity.getOrgAccountId());
				}else{
					if(accessMap.containsKey(OrgConstants.ExternalAccessType.Access.name())){
						deleteAccessRelationByType(entityId, entity.getOrgAccountId(),OrgConstants.ExternalAccessType.Access);
					}
					
					if(accessMap.containsKey(OrgConstants.ExternalAccessType.BeAccessed.name())){
						deleteAccessRelationByType(entityId, entity.getOrgAccountId(),OrgConstants.ExternalAccessType.BeAccessed);
					}
				}
			}
			
			//可见内部联系人
			Long i=0L;
			String access = accessMap.get(OrgConstants.ExternalAccessType.Access.name());
			if(Strings.isNotBlank(access)){
				String[] entityIds = access.split(",");
				i=0L;
				for (String strTemp : entityIds) {
					String[] typeAndId = strTemp.split("[|]");
					String include = "0";//默认包含子部门
					if(typeAndId.length>=3){
						include = typeAndId[2];
					}
					V3xOrgRelationship rel = new V3xOrgRelationship();
					rel.setKey(OrgConstants.RelationshipType.External_Access.name());
					rel.setSortId(i++);
					rel.setSourceId(entity.getId());
					rel.setObjective0Id(Long.valueOf(typeAndId[1]));
					rel.setOrgAccountId(entity.getOrgAccountId());
					rel.setObjective5Id(OrgConstants.ExternalAccessType.Access.name());
					rel.setObjective6Id(typeAndId[0]);
					rel.setObjective7Id(include);
					if(isAdd){
						EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
						enummap.put(OrgConstants.RelationshipObjectiveName.objective0Id, rel.getObjective0Id());
						enummap.put(OrgConstants.RelationshipObjectiveName.objective5Id, OrgConstants.ExternalAccessType.Access.name());
						enummap.put(OrgConstants.RelationshipObjectiveName.objective6Id, rel.getObjective6Id());
						List<V3xOrgRelationship> rellist = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.External_Access, entity.getId(), entity.getOrgAccountId(), enummap);
						if(Strings.isNotEmpty(rellist)) continue;
					}
					relList.add(rel);
				}
			}
			
			//公开给内部联系人
			i=0L;
			String beAccessed = accessMap.get(OrgConstants.ExternalAccessType.BeAccessed.name());
			if(Strings.isNotBlank(beAccessed)){
				String[] entityIds = beAccessed.split(",");
				for (String strTemp : entityIds) {
					String[] typeAndId = strTemp.split("[|]");
					String include = "0";//默认包含子部门
					if(typeAndId.length>=3){
						include = typeAndId[2];
					}
					V3xOrgRelationship rel = new V3xOrgRelationship();
					rel.setKey(OrgConstants.RelationshipType.External_Access.name());
					rel.setSortId(i++);
					rel.setSourceId(entity.getId());
					rel.setObjective0Id(Long.valueOf(typeAndId[1]));
					rel.setOrgAccountId(entity.getOrgAccountId());
					rel.setObjective5Id(OrgConstants.ExternalAccessType.BeAccessed.name());
					rel.setObjective6Id(typeAndId[0]);
					rel.setObjective7Id(include);
					if(isAdd){
						EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
						enummap.put(OrgConstants.RelationshipObjectiveName.objective0Id, rel.getObjective0Id());
						enummap.put(OrgConstants.RelationshipObjectiveName.objective5Id, OrgConstants.ExternalAccessType.BeAccessed.name());
						enummap.put(OrgConstants.RelationshipObjectiveName.objective6Id, rel.getObjective6Id());
						List<V3xOrgRelationship> rellist = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.External_Access, entity.getId(), entity.getOrgAccountId(), enummap);
						if(Strings.isNotEmpty(rellist)) continue;
					}
					relList.add(rel);
				}
			}
		}
		
		if(entity instanceof V3xOrgDepartment){//外部单位的权限
			deleteAccessRelation(entityId, entity.getOrgAccountId());
			//可见外部单位
			Long i=0L;
			String access = accessMap.get(OrgConstants.ExternalAccessType.VjoinAccess.name());
			if(Strings.isNotBlank(access)){
				String[] entityIds = access.split(",");
				i=0L;
				for (String strTemp : entityIds) {
					String[] typeAndId = strTemp.split("[|]");
					V3xOrgRelationship rel = new V3xOrgRelationship();
					rel.setKey(OrgConstants.RelationshipType.External_Access.name());
					rel.setSortId(i++);
					rel.setSourceId(entity.getId());
					rel.setObjective0Id(Long.valueOf(typeAndId[1]));
					rel.setOrgAccountId(entity.getOrgAccountId());
					rel.setObjective5Id(OrgConstants.ExternalAccessType.VjoinAccess.name());
					rel.setObjective6Id(typeAndId[0]);
					relList.add(rel);
				}
			}
		}

        if(Strings.isNotEmpty(relList)){
        	orgManagerDirect.addOrgRelationships(relList);
        }
	}
    
    @Override
    public List<V3xOrgMember> getAccessInnerMembers(Long memberId) throws BusinessException{
    	List<V3xOrgMember> members = new UniqueList<V3xOrgMember>();
    	V3xOrgMember member = orgManager.getMemberById(memberId);
    	if(member!=null && member.isValid()){
	     	List<Long> relationShipIds = orgCache.getAccessMemberOrgRelationshipIds(memberId);
	     	for (Long id : relationShipIds) {
	     		V3xOrgRelationship orgRelationship = orgCache.getV3xOrgRelationshipById(id);
	     		String entityType = orgRelationship.getObjective6Id();
	     		String include = orgRelationship.getObjective7Id();
	 			V3xOrgEntity m = orgManager.getEntityById(OrgHelper.getV3xClass(entityType), orgRelationship.getObjective0Id());
        		if (m != null && m.isValid()) {
        			if (m instanceof V3xOrgMember) {
        				members.add((V3xOrgMember) m);
        			} else if (m instanceof V3xOrgDepartment) {
        				members.addAll(this.getMembersByDepartment(m.getId(), "1".equals(include)));
        			} else if (m instanceof V3xOrgPost) {
        				members.addAll(orgManager.getMembersByPost(m.getId()));
        			} else if (m instanceof V3xOrgLevel) {
        				members.addAll(orgManager.getMembersByLevel(m.getId()));
        			}else if (m instanceof V3xOrgTeam) {
        				members.addAll(orgManager.getMembersByTeam(m.getId(), new HashSet<Long>()));
        			}else if (m instanceof V3xOrgAccount) {
        				members.addAll(orgManager.getAllMembers(m.getId()));
        				break;
        			}
        		}
	 		}
    	}
    	return members;
    }
    
    @Override
    public List<V3xOrgDepartment> getVjoinAccessDepartments(Long memberId) throws BusinessException{
    	List<V3xOrgDepartment> result = new UniqueList<V3xOrgDepartment>();
    	V3xOrgMember member = orgManager.getMemberById(memberId);
    	if(member!=null && member.isValid()){
    		V3xOrgDepartment currentDept = orgManager.getDepartmentById(member.getOrgDepartmentId());
    		result.add(currentDept);
	     	List<Long> relationShipIds = orgCache.getVjoinAccessEntityOrgRelationshipIds(currentDept.getId());
	     	for (Long id : relationShipIds) {
	     		V3xOrgRelationship orgRelationship = orgCache.getV3xOrgRelationshipById(id);
	     		String entityType = orgRelationship.getObjective6Id();
	 			V3xOrgEntity m = orgManager.getEntityById(OrgHelper.getV3xClass(entityType), orgRelationship.getObjective0Id());
        		if (m != null && m.isValid() && m instanceof V3xOrgDepartment) {
        			result.add((V3xOrgDepartment)m);
        		}
	 		}
    	}
    	Collections.sort(result, CompareSortEntity.getInstance());
    	return result;
    } 
    
    @Override
    public List<V3xOrgMember> getAccessExternalMembers(Long memberId) throws BusinessException{
    	List<V3xOrgMember> members = new UniqueList<V3xOrgMember>();
    	V3xOrgMember member = orgManager.getMemberById(memberId);
    	if(member!=null && member.isValid()){
    		//目前外部人员只能选择内部的 单位，部门，岗位，职务级别，个人
    		List<Long> domainIds = orgManager.getUserDomainIDs(memberId,OrgConstants.ORGENT_TYPE.Account.name(),
                    OrgConstants.ORGENT_TYPE.Department.name(), OrgConstants.ORGENT_TYPE.Post.name(),
                    OrgConstants.ORGENT_TYPE.Level.name(),OrgConstants.ORGENT_TYPE.Member.name());
	     	Set<Long> ids = orgCache.getAllBeAccessOrgRelationshipIds();
	     	for (Long id : ids) {
	     		V3xOrgRelationship relationship = orgCache.getV3xOrgRelationshipById(id);
	     		Long vJoinMemberId = relationship.getSourceId();
	     		Long entityId = relationship.getObjective0Id();
	     		if(domainIds.contains(entityId)){
	     			V3xOrgMember vJoinMember = orgManager.getMemberById(vJoinMemberId);
	     			if(vJoinMember!=null && vJoinMember.isValid()){
	     				members.add(vJoinMember);
	     			}
	     		}
	 		}
    	}
    	return members;
    }
    
    @Override
    public List<V3xOrgRole> getDepartmentRolesByAccount(Long accountID,Integer externalType) throws BusinessException {
        List<V3xOrgRole> list = orgCache.getAllV3xOrgEntity(V3xOrgRole.class, accountID,externalType);
        List<V3xOrgRole> roleList = new UniqueList<V3xOrgRole>();
        for (Iterator<V3xOrgRole> it = list.iterator(); it.hasNext();) {
            V3xOrgRole role = it.next();
            if (role != null && role.isValid()&&(role.getBond()==OrgConstants.ROLE_BOND.DEPARTMENT.ordinal())){
                roleList.add(role);
            }
        }
        Collections.sort(roleList, CompareSortEntity.getInstance());
        return roleList;
    }
    
    @Override
    public List<MetadataColumnBO> getCustomerAccountProperties(Long orgAccountId) throws BusinessException{
    	//查看通讯录分类的id
    	Long categoryId=0L;
    	Map<String, Object> categoryParams =new HashMap<String, Object>();
    	categoryParams.put("moduleType", MetadataConstants.APPLICATIONCATEGORY_JOINACCOUNT);
    	FlipInfo categoryFi = metadataCategoryManager.findMetadataCategoryList(new FlipInfo(), categoryParams);
    	List<MetadataCategoryBO> l=categoryFi.getData();
    	if(null!=l && l.size()>0){
    		 categoryId=l.get(0).getId();
    	}
    	
        //v-join 平台对应的 外单位扩展属性
        FlipInfo fi=new FlipInfo();
        fi.setSize(100);
        Map<String, Object> sqlParams =new HashMap<String, Object>();
        sqlParams.put("isEnable", 1);
        sqlParams.put("categoryId", categoryId);
        List<MetadataColumnBO> metadataColumnBOList =new ArrayList<MetadataColumnBO>();
        FlipInfo addressBookFlipInfo=metadataColumnManager.findCtpMetadataColumnList(fi, sqlParams);
        if(null!=addressBookFlipInfo){
        	List<MetadataColumnBO> list = addressBookFlipInfo.getData();
        	for(MetadataColumnBO bo : list){
        		if(bo.getCreateUser().equals(orgAccountId)){
        			metadataColumnBOList.add(bo);
        		}
        	}
        }
        return metadataColumnBOList;
    }
    
    @Override
    public V3xOrgRole getRoleByCode(String code, Long accountId) throws BusinessException {
    	accountId = getDefaultVjoinAccount(accountId);
        List<V3xOrgRole> list = orgCache.getAllV3xOrgEntity(V3xOrgRole.class, accountId,null);
        for (Iterator<V3xOrgRole> it = list.iterator(); it.hasNext();) {
            V3xOrgRole role = it.next();
            if (role != null && role.isValid() && code.equals(role.getCode())){
                return role;
            }
        }
        return null;
    }
    
    
    private void defaultMemberRole4Add(List<V3xOrgMember> memberList,Long accountId) throws BusinessException {
        if(null == memberList || memberList.size() == 0){
            return;
        }
        List<OrgRelationship> relPOs = new ArrayList<OrgRelationship>(memberList.size());
        
        V3xOrgRole defultRole = this.getRoleByCode(OrgConstants.Role_NAME.VjoinStaff.name(), accountId);
        if(defultRole==null){
        	return ;
        }
        
        for (V3xOrgMember member : memberList) {
            V3xOrgRelationship relBO = new V3xOrgRelationship();
            relBO.setId(UUIDLong.longUUID());
            relBO.setKey(RelationshipType.Member_Role.name());
            relBO.setSourceId(member.getId());
            relBO.setObjective0Id(accountId);
            relBO.setObjective1Id(defultRole.getId());
            relBO.setObjective5Id(OrgConstants.ORGENT_TYPE.Member.name());
            relBO.setOrgAccountId(accountId);
            
            relPOs.add((OrgRelationship) relBO.toPO());
        }
        
        orgDao.insertOrgRelationship(relPOs);
    }
    
    @Override
    public void updateRoleResource(List nodes, Long accountId) throws BusinessException {
        PrivTreeNodeBO node = null;
        String nodeId = null;
        PrivRoleMenu roleRes = null;
        Long menuId = null;

        List<PrivRoleMenu> roleReources = new ArrayList<PrivRoleMenu>();
        if(nodes!=null&&nodes.size()>0){
        	if(nodes.get(0) instanceof Map){
        		nodes = ParamUtil.mapsToBeans(nodes, PrivTreeNodeBO.class, true);
        	}
        }
        
        V3xOrgRole role = this.getRoleByCode(OrgConstants.Role_NAME.VjoinStaff.name(), accountId);
        Long roleId = role.getId();
        if(role == null){
        	return;
        }
        // 先删除已存在的关系
        roleRes = new PrivRoleMenu();
        roleRes.setRoleid(roleId);
        roleMenuDao.deleteRoleMenu(roleRes);

        // 新建角色资源关系
        StringBuilder ressourcename = new StringBuilder("");

        for (int i = 0; i < nodes.size(); i++) {
            node = (PrivTreeNodeBO) nodes.get(i);
            nodeId = node.getIdKey();
             if (nodeId.indexOf(PrivTreeNodeBO.menuflag) != -1) {
                // 勾选菜单时自动将入口资源勾选
            	 if(OrgConstants.defaultVjoinMenu.containsKey(node.getIdKey())){
            		 menuId = Long.parseLong(node.getIdKey().replace(PrivTreeNodeBO.menuflag, ""));
            		 PrivMenuBO menuBO = privilegeMenuManager.findById(menuId);
            		 if (menuBO != null) {
            			 roleRes = new PrivRoleMenu();
            			 roleRes.setNewId();
            			 roleRes.setResourceid(menuBO.getEnterResourceId());
            			 roleRes.setRoleid(roleId);
            			 roleRes.setMenuid(menuBO.getId());
            			 roleReources.add(roleRes);
            			 ressourcename.append(menuBO.getName());
            			 ressourcename.append(",");
            		 }
            	 }
            }
        }
        roleMenuDao.insertRoleMenuPatchAll(roleReources);
        //应用日志

        privilegeMenuManager.updateMemberMenuLastDateByRoleId(roleId, accountId,null);
        privilegeMenuManager.updateBiz();
    }
    
	public List<PrivTreeNodeBO> getRoleResource(Long accountId) throws Exception {
		// 后台管理资源列表
		List<PrivTreeNodeBO> treeNodes4Back = new ArrayList<PrivTreeNodeBO>();
		// 前台应用资源列表
		List<PrivTreeNodeBO> treeNodes4Front = new ArrayList<PrivTreeNodeBO>();
		
        V3xOrgRole role = this.getRoleByCode(OrgConstants.Role_NAME.VjoinStaff.name(), accountId);
        Long roleId = role.getId();
        if(role == null){
        	return treeNodes4Front;
        }
		privilegeMenuManager.getTreeNodes(null, null, roleId.toString(), null, null,
				"1", null, treeNodes4Back, treeNodes4Front,true);
		return treeNodes4Front;

	}
	
	@Override
	public OrganizationMessage modifyPwd(Long memberId,String nowPassword, String oldPassword) throws BusinessException {
		OrganizationMessage message = new OrganizationMessage();
		PrincipalManager principalManager = (PrincipalManager) AppContext.getBean("principalManager");
		V3xOrgMember member = orgManager.getMemberById(memberId);
		if(member == null || !member.isValid()){
			message.addErrorMsg(null, "不是有效的人员!");
			return message;
		}
		if(!orgManager.isOldPasswordCorrect(member.getLoginName(), oldPassword)){
			message.addErrorMsg(member, "原密码错误!");
		    return message;
		}
		
		try {
			V3xOrgPrincipal newOrgPrincipal = new V3xOrgPrincipal(member.getId(), member.getLoginName(), nowPassword);
			member.setV3xOrgPrincipal(newOrgPrincipal);

			OrganizationMessage om = principalManager.update(newOrgPrincipal);
			if (Strings.isNotEmpty(om.getErrorMsgs())) {
				message.addAllErrorMsg(om.getErrorMsgs());
				return message;
			}
		} catch (Exception e) { 
			log.error(e.getMessage());
		}
		message.addSuccessMsg(member);
		return message;
	}
	
	
    @Override
    public OrganizationMessage modifyPwd4Admin(Long accountId,String loginName, String nowPassword, String oldPassword) throws BusinessException {
    	OrganizationMessage message = new OrganizationMessage();
        V3xOrgMember admin = orgManager.getAdministrator(accountId);
        if(!admin.getLoginName().equals(loginName)) {
            // 校验登录名是否已存在
            if (principalManager.isExist(loginName)) {
            	message.addErrorMsg(null, ResourceUtil.getString("MessageStatus.ACCOUNT_REPEAT_ADMIN_NAME"));
            	return message;
            }
        }
        
        if(!orgManager.isOldPasswordCorrect(admin.getLoginName(), oldPassword)){
        	message.addErrorMsg(admin, "原密码错误!");
        	return message;
        }
        
        V3xOrgPrincipal adminPri = admin.getV3xOrgPrincipal();
        adminPri.setLoginName(loginName);
        adminPri.setMemberId(admin.getId());
        adminPri.setPassword(nowPassword);
        admin.setV3xOrgPrincipal(adminPri);
        
		OrganizationMessage om = updateMember(admin);
		if (Strings.isNotEmpty(om.getErrorMsgs())) {
			message.addAllErrorMsg(om.getErrorMsgs());
			return message;
		}
		message.addSuccessMsg(admin);
		return message;
    }
    
    @Override
    public List<V3xOrgDepartment> getAccessInnerDepts(Long memberId,Long accountId) throws BusinessException{
    	List<V3xOrgDepartment> depts = new UniqueList<V3xOrgDepartment>();
    	if(accountId == null) {
    		return depts;
    	}
    	V3xOrgAccount account = orgManager.getAccountById(accountId);
    	if(account!=null && account.isValid() && account.getExternalType().equals(OrgConstants.ExternalType.Inner.ordinal())){
    		Set<Long> deptIds = new HashSet<Long>();
    		V3xOrgMember member = orgManager.getMemberById(memberId);
    		if(member!=null && member.isValid()){
    			List<Long> relationShipIds = orgCache.getAccessMemberOrgRelationshipIds(memberId);
    			for (Long id : relationShipIds) {
    				V3xOrgRelationship orgRelationship = orgCache.getV3xOrgRelationshipById(id);
    				String entityType = orgRelationship.getObjective6Id();
    				String include = orgRelationship.getObjective7Id();
    				V3xOrgEntity m = orgManager.getEntityById(OrgHelper.getV3xClass(entityType), orgRelationship.getObjective0Id());
    				if (m != null && m.isValid()) {
    					if (m instanceof V3xOrgMember) {
    						Long deptId = ((V3xOrgMember) m).getOrgDepartmentId();
    						V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
    						if(dept.getOrgAccountId().equals(accountId)){
    							if(dept!=null && dept.isValid()){
    								depts.add(dept);
    								deptIds.add(dept.getId());
    							}
    						}else{
    							Map<Long, List<MemberPost>> map = orgManager.getConcurentPostsByMemberId(accountId, m.getId());
    							for(Long l : map.keySet()){
    								V3xOrgDepartment cDept = orgManager.getDepartmentById(l);
    								depts.add(cDept);
    								deptIds.add(cDept.getId());
    							}
    						}
    						
    					} else if (m instanceof V3xOrgDepartment) {
    						depts.add((V3xOrgDepartment)m);
    						deptIds.add(m.getId());
    						if(!"1".equals(include)){//是否包含子部门
    							List<V3xOrgDepartment> childDepts = orgManager.getChildDepartments(m.getId(), false);
    							depts.addAll(childDepts);
    							for(V3xOrgDepartment c : childDepts){
    								deptIds.add(c.getId());
    							}
    						}
    					} else if (m instanceof V3xOrgPost) {
    						Long postId = m.getId();
    						EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
    						enummap.put(OrgConstants.RelationshipObjectiveName.objective1Id, postId);
    						List<V3xOrgRelationship> postRelationList = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Post, null, accountId, enummap);
    						for (V3xOrgRelationship rel : postRelationList) {
    							Long deptId = rel.getObjective0Id();
    							if(!deptIds.contains(deptId)){
    								V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
    								if(dept!=null && dept.isValid()){
    									depts.add(dept);
    									deptIds.add(deptId);
    								}
    							}
    						}
    					} else if (m instanceof V3xOrgLevel) {
    						Long levelId = m.getId();
    						EnumMap<RelationshipObjectiveName, Object> enummap = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
    						enummap.put(OrgConstants.RelationshipObjectiveName.objective2Id, levelId);
    						List<V3xOrgRelationship> postRelationList = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Post, null, accountId, enummap);
    						for (V3xOrgRelationship rel : postRelationList) {
    							Long deptId = rel.getObjective0Id();
    							if(!deptIds.contains(deptId)){
    								V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
    								if(dept!=null && dept.isValid()){
    									depts.add(dept);
    									deptIds.add(deptId);
    								}
    							}
    						}
    					}else if (m instanceof V3xOrgTeam) {
    						Long teamId = m.getId();
    						List<V3xOrgMember> members = orgManager.getMembersByTeam(teamId);
    						for(V3xOrgMember tm : members){
    							Long deptId = tm.getOrgDepartmentId();
    							if(!deptIds.contains(deptId)){
    								V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
    								if(dept!=null && dept.isValid()){
    									depts.add(dept);
    									deptIds.add(deptId);
    								}
    							} 
    						}
    					}else if (m instanceof V3xOrgAccount) {
    						depts = orgManager.getChildDeptsByAccountId(accountId,false);
    						break;
    					}
    				}
    			}
    		}
    		Collections.sort(depts, CompareSortEntity.getInstance());
    	}
    	return depts;
    }
    
    @Override
    public List<V3xOrgDepartment> getAccessVjoinDepts(Long memberId,Long accountId) throws BusinessException{
    	accountId = this.getDefaultVjoinAccount(accountId);
    	List<V3xOrgDepartment> depts = new UniqueList<V3xOrgDepartment>();
    	V3xOrgAccount account = orgManager.getAccountById(accountId);
    	Set<Long> deptIds = new HashSet<Long>();
    	if(accountId == null || !account.isValid() || account.getExternalType().equals(OrgConstants.ExternalType.Inner.ordinal())) {
    		return depts;
    	}
    	List<V3xOrgMember> members = this.getAccessExternalMembers(memberId);
    	for(V3xOrgMember member : members){
    		if(member!=null && member.isValid() && member.isVJoinExternal() && accountId.equals(member.getOrgAccountId())){
    			Long deptId = member.getOrgDepartmentId();
    			if(!deptIds.contains(deptId)){
    				V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
    				if(dept!=null && dept.isValid() && dept.getExternalType()!=OrgConstants.ExternalType.Inner.ordinal()){
    					deptIds.add(deptId);
    					depts.add(dept);
    					List<V3xOrgDepartment> fDeps = this.getAllParentDepartments(deptId);
    					depts.addAll(fDeps);
    					for(V3xOrgDepartment f : fDeps){
    						deptIds.add(f.getId());
    					}
    				}
    			}
    		}
    	}
    	return depts;
    }
    
    @Override
    public List<V3xOrgDepartment> getAllParentDepartments(Long depId) throws BusinessException {
        return getAllParentDepartments0(depId, true);
    }
    
    private List<V3xOrgDepartment> getAllParentDepartments0(Long depId, boolean isNeedSort) throws BusinessException {
        List<V3xOrgDepartment> fDeps = new ArrayList<V3xOrgDepartment>();
        V3xOrgDepartment dept = orgManager.getDepartmentById(depId);
        if (dept == null) {
            log.warn("orgCache中没有该部门id:"+ depId);
            return fDeps;
        }
        
        for(;;){
            Long pId = dept.getSuperior();
            if(pId == null){
                break;
            }
            dept = orgManager.getDepartmentById(pId);
            if(dept == null || !dept.isValid() || dept.getType() == OrgConstants.UnitType.Account || dept.getExternalType()==OrgConstants.ExternalType.Inner.ordinal()){
                break;
            }
            fDeps.add(dept);
        }

        if(isNeedSort){
            Collections.sort(fDeps, CompareUnitPath.getInstance());
        }
        
        return fDeps;
    }
    
    @Override
    public Long getDefaultVjoinAccount(Long accountId) throws BusinessException{
    	if(accountId == null){
    		List<V3xOrgAccount> accounts = this.getAllAccounts();
    		if(!Strings.isEmpty(accounts)){
    			accountId = accounts.get(0).getId();
    		}
    	}
    	return accountId;
    }
    
    /**
     * 根据外单位属性的枚举值，得到这些外单位下的人员
     * @param enumId
     * @throws BusinessException 
     */
    @Override
    public List<V3xOrgMember> getMembersByEnumId(Long enumItemId) throws BusinessException{
    	List<V3xOrgMember> result = new UniqueList<V3xOrgMember>();
    	Long accountId = getDefaultVjoinAccount(null);
    	CtpEnumItem item = enumManagerNew.getCtpEnumItem(enumItemId);
    	if(item == null){
    		return null;
    	}
    	Long enumId = item.getRefEnumid();
    	List<MetadataColumnBO> list = getCustomerAccountProperties(accountId);
    	String columnNames = null;
    	for(MetadataColumnBO bo : list){
    		if(bo.getType() !=3){
    			continue;
    		}
            String rule = bo.getRule();
            Map<String, String> map = new HashMap<String, String>();
            try {
            	map = (Map<String, String>) JSONUtil.parseJSONString(rule);
			} catch (Exception e) {
				log.error(bo.getLabel()+" 引用的枚举无效");
				continue;
			}
            
            Long eId = Long.valueOf(map.get("codeId"));
            if(!enumId.equals(eId)){
            	continue;
            }
            if(Strings.isBlank(columnNames)){
            	columnNames = bo.getColumnName();
            }else{
            	columnNames = columnNames + "," + bo.getColumnName();
            }
    	}
    	if(Strings.isNotBlank(columnNames)){
	    	String[] columnNameArr = columnNames.split(",");
	    	List<JoinAccount> joinAccountList = joinAccountCustomerFieldInfoManager.getAllJoinAccount(accountId);
	    	List<Long> deptIds = new UniqueList<Long>();
	    	
	    	for(String columnName: columnNameArr){
	    		Method method = joinAccountCustomerFieldInfoManager.getGetMethod(columnName);
	    		for(JoinAccount joinAccount : joinAccountList){
					try {
						Object value = method.invoke(joinAccount, new Object[] {});
						if(value != null){
							if(value.toString().indexOf(enumItemId.toString())>=0){
								deptIds.add(joinAccount.getDepartmentId());
							}
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
	    		
	    		}
	    	
	    		for(Long deptId : deptIds){
	    			result.addAll(this.getMembersByDepartment(deptId, false));
	    		}
	    	
	    	}
    	}
    	return result;
    }
    
    @Override
    public List<V3xOrgMember> getMembersByDepartmentRoleOfUp(long departmentId, String roleNameOrId) throws BusinessException{
        V3xOrgDepartment department = orgCache.getV3xOrgEntity(V3xOrgDepartment.class, departmentId);
        if(department == null || !department.isValid()){
            return new ArrayList<V3xOrgMember>(0);
        }

        V3xOrgRole role = getRoleByNameOrId(roleNameOrId,departmentId);
        if(role == null || !role.isValid()){
            return new ArrayList<V3xOrgMember>(0);
        }

        //如果是机构角色，并且department对应的是外部单位，则找这个外部单位的直接上级机构，对应的机构角色下的人
        if(role.getExternalType().equals(OrgConstants.ExternalType.Interconnect1.ordinal()) 
        		&& department.getExternalType().equals(OrgConstants.ExternalType.Interconnect2.ordinal())){
        	departmentId = department.getSuperior();
        }
        List<V3xOrgMember> result1 = this.getMembersByRoleNameOrId(departmentId, role.getId().toString());
        if (result1 != null && !result1.isEmpty()) {
            return result1;
        }
        
    	List<V3xOrgDepartment> allParentDept = this.getAllParentDepartments(departmentId);
    	for (int i = allParentDept.size(); i > 0; i--) {
    		V3xOrgDepartment parentDept = allParentDept.get(i - 1);
    		
    		List<V3xOrgMember> result0 = this.getMembersByRoleNameOrId(parentDept.getId(), role.getId().toString());
    		if (result0 != null && !result0.isEmpty()) {
    			return result0;
    		}
    	}


        return new ArrayList<V3xOrgMember>(0);
    }
    
    @Override
    public List<MemberPost> getMemberPostByDepartmentRoleOfUp(long departmentId, String roleNameOrId) throws BusinessException{
        V3xOrgDepartment department = orgCache.getV3xOrgEntity(V3xOrgDepartment.class, departmentId);
        if(department == null || !department.isValid()){
            return new ArrayList<MemberPost>(0);
        }

        V3xOrgRole role = getRoleByNameOrId(roleNameOrId,departmentId);
        if(role == null || !role.isValid()){
            return new ArrayList<MemberPost>(0);
        }

        //如果是机构角色，并且department对应的是外部单位，则找这个外部单位的直接上级机构，对应的机构角色下的人
        if(role.getExternalType().equals(OrgConstants.ExternalType.Interconnect1.ordinal()) 
        		&& department.getExternalType().equals(OrgConstants.ExternalType.Interconnect2.ordinal())){
        	departmentId = department.getSuperior();
        }
        List<MemberPost> result1 = this.getMemberPostByRoleNameOrId(departmentId, role.getId().toString());
        if (result1 != null && !result1.isEmpty()) {
            return result1;
        }
        
    	List<V3xOrgDepartment> allParentDept = this.getAllParentDepartments(departmentId);
    	for (int i = allParentDept.size(); i > 0; i--) {
    		V3xOrgDepartment parentDept = allParentDept.get(i - 1);
    		
    		List<MemberPost> result0 = this.getMemberPostByRoleNameOrId(parentDept.getId(), role.getId().toString());
    		if (result0 != null && !result0.isEmpty()) {
    			return result0;
    		}
    	}


        return new ArrayList<MemberPost>(0);
    }
    
    @Override
    public V3xOrgRole getRoleByNameOrId(String roleId, Long unitId) throws BusinessException {
        V3xOrgUnit unitById = orgManager.getUnitById(unitId);
    	if(null != unitById && null != unitById.getOrgAccountId()){
    		unitId = unitById.getOrgAccountId();
    	}
    	//先按照名称查
    	V3xOrgRole role = this.getRoleByName(roleId, unitId);
    	if(null!=role){
    		roleId = role.getId().toString();
    	}
    	
    	//再按照
    	//如果不是数字，直接返回null
    	try {
    		Long.parseLong(roleId);
		} catch (Exception e) {
			log.info("找不到对应的角色 ："+roleId);
			return null;
		}
    	return orgManager.getRoleById(Long.parseLong(roleId));
    }
    
    @Override
    public List<V3xOrgMember> getMembersByRoleNameOrId(Long unitId, String roleId) throws BusinessException {
        
        V3xOrgRole role = this.getRoleByNameOrId(roleId, unitId);
        if (role == null) {
            return Collections.emptyList();
        }
        
        V3xOrgUnit unit = orgManager.getUnitById(unitId);
        //如果是机构角色，并且department对应的是外部单位，则找这个外部单位的直接上级机构，对应的机构角色下的人
        if(unit!=null && role.getExternalType().equals(OrgConstants.ExternalType.Interconnect1.ordinal()) 
        		&& unit.getExternalType().equals(OrgConstants.ExternalType.Interconnect2.ordinal())){
        	unitId = unit.getSuperior();
        }

        List<V3xOrgRelationship> relList = orgCache.getRoleEntityRelastionships(role.getId(), unitId, null);
        if(relList.isEmpty()) {
            return new ArrayList<V3xOrgMember>(); 
        }
        
        Collections.sort(relList, CompareSortRelationship.getInstance());

        List<V3xOrgMember> cntList = new UniqueList<V3xOrgMember>();
        for (V3xOrgRelationship rel : relList) {
            V3xOrgMember mem = (V3xOrgMember) orgCache.getV3xOrgEntityNoClone(V3xOrgMember.class, rel.getSourceId());
            if (mem != null && mem.isValid()) {
            	cntList.add(OrgHelper.cloneEntityImmutableDecorator(mem));
            }
        }

        return cntList;
    }
    
    @Override
    public List<MemberPost> getMemberPostByRoleNameOrId(Long unitId, String roleId) throws BusinessException {
        V3xOrgRole role = this.getRoleByNameOrId(roleId, unitId);
        if (role == null) {
            return Collections.emptyList();
        }
        
        V3xOrgUnit unit = orgManager.getUnitById(unitId);
        //如果是机构角色，并且department对应的是外部单位，则找这个外部单位的直接上级机构，对应的机构角色下的人
        if(unit!=null && role.getExternalType().equals(OrgConstants.ExternalType.Interconnect1.ordinal()) 
        		&& unit.getExternalType().equals(OrgConstants.ExternalType.Interconnect2.ordinal())){
        	unitId = unit.getSuperior();
        }

        List<V3xOrgRelationship> relList = orgCache.getRoleEntityRelastionships(role.getId(), unitId, null);
        if(relList.isEmpty()) {
            return new ArrayList<MemberPost>(); 
        }
        
        Collections.sort(relList, CompareSortRelationship.getInstance());

        List<MemberPost> memberPostList = new UniqueList<MemberPost>();
        for (V3xOrgRelationship rel : relList) {
            V3xOrgMember mem = (V3xOrgMember) orgCache.getV3xOrgEntityNoClone(V3xOrgMember.class, rel.getSourceId());
            if (mem != null && mem.isValid()) {
            	MemberPost memberPost  = new MemberPost(rel);
            	memberPostList.add(memberPost);
            }
        }

        return memberPostList;
    }
    
    @Override
    public V3xOrgRole getRoleByName(String roleName, Long accountId) throws BusinessException {
    	return getRoleByName(roleName, accountId, null);
    }
    @Override
    public V3xOrgRole getRoleByName(String roleName, Long accountId, Integer externalType) throws BusinessException {
    	List<V3xOrgRole> list = new UniqueList<V3xOrgRole>();
    	if(externalType != null){
    		list = orgCache.getAllV3xOrgEntityNoClone(V3xOrgRole.class, accountId,externalType);
    	}else{
    		list.addAll(orgCache.getAllV3xOrgEntityNoClone(V3xOrgRole.class, accountId,OrgConstants.ExternalType.Interconnect1.ordinal()));
    		list.addAll(orgCache.getAllV3xOrgEntityNoClone(V3xOrgRole.class, accountId,OrgConstants.ExternalType.Interconnect2.ordinal()));
    		list.addAll(orgCache.getAllV3xOrgEntityNoClone(V3xOrgRole.class, accountId,OrgConstants.ExternalType.Interconnect3.ordinal()));
    	}
        for (V3xOrgRole role : list) {
            if(accountId != null && !role.getOrgAccountId().equals(accountId)){
                continue;
            }
            if (roleName.equals(role.getName()) || roleName.equals(role.getCode())){
                return OrgHelper.cloneEntity(role);
            }
        }

        return null;
    }

    
    @Override
    public List<V3xOrgDepartment> getDepartmentsByVjoinRole(Long memberId, String roleName,Integer externalType) throws BusinessException {
        List<V3xOrgDepartment> resultList = new ArrayList<V3xOrgDepartment>();

        List<V3xOrgAccount> accounts = this.getAllAccounts();
        if (Strings.isEmpty(accounts)) {
            return resultList;
        }

        //取V-Join虚拟单位角色
        V3xOrgRole role = this.getRoleByName(roleName, accounts.get(0).getId(), externalType);
        if (role == null) {
            return resultList;
        }

        List<Long> deptIds = orgManager.getDomainByRole(role.getId(), memberId);
        for (Long depId : deptIds) {
            V3xOrgDepartment dept;
            try {
                dept = orgCache.getV3xOrgEntity(V3xOrgDepartment.class, depId);
                if (dept != null && dept.isValid()) {
                    resultList.add(dept);
                }
            } catch (ClassCastException e) {
                log.error("该人员的部门角色关系数据有误，捕获异常不影响登录");
                continue;
            }
        }

        return resultList;
    }
    
    @Override
    public List<V3xOrgDepartment> getDepartmentsByVjoinManager(Long memberId, String roleName) throws BusinessException {
        Integer externalType = OrgConstants.ExternalType.Interconnect1.ordinal();
        if (Role_NAME.VjoinUnitManager.name().equals(roleName)) {
            externalType = OrgConstants.ExternalType.Interconnect1.ordinal();
        } else if (Role_NAME.VjoinAccountManager.name().equals(roleName)) {
            externalType = OrgConstants.ExternalType.Interconnect2.ordinal();
        }
        
        return getDepartmentsByVjoinRole(memberId, roleName, externalType);
    }
    
    @Override
    public <T extends V3xOrgEntity> List<T> getEntitiesByName(Class<T> clazz, String name, Long accountId) throws BusinessException {
        if(Strings.isBlank(name)){
            return Collections.EMPTY_LIST;
        }
        
        String key = clazz.getSimpleName() + name + accountId;
        
        List enList = (List)AppContext.getThreadContext(key);
        if(enList != null) {
            return enList;
        }
        else{
            enList = new ArrayList();
            AppContext.putThreadContext(key, enList);
        }
        
        List<? extends V3xOrgEntity> allEntList = orgCache.getAllV3xOrgEntityNoClone(clazz, accountId,null);
        for (V3xOrgEntity e : allEntList) {
    		if(Strings.equals(e.getName(), name)){
    			if(!e.getExternalType().equals(OrgConstants.ExternalType.Inner)){
    				enList.add(OrgHelper.cloneEntity(e));
    			}
        	}
        }
        return enList;
    }
    
    @Override
    public void deleteAllAccessRelation(List<Long> oldAccountIds, List<Long> newAccountIds) throws BusinessException{
    	Set<Long> delIds = new HashSet<Long>();
    	for(Long oldId : oldAccountIds){
    		if(!newAccountIds.contains(oldId)){
    			delIds.add(oldId);
    		}
    	}
    	
    	if(Strings.isEmpty(delIds)){
    		return ;
    	}
    	
    	List<V3xOrgRelationship> rels = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.External_Access);
    	
    	List<OrgRelationship> delRels = new UniqueList<OrgRelationship>();
    	for(V3xOrgRelationship rel : rels){
    		V3xOrgEntity entity = orgManager.getEntity(rel.getObjective6Id(), rel.getObjective0Id());
    		if(delIds.contains(entity.getOrgAccountId())){
    			delRels.add((OrgRelationship)rel.toPO());
    		}
    	}
    	orgDao.deleteOrgRelationshipPOs(delRels);
    	
    }
    
    @Override
    public V3xOrgMember getSubAdmin(Long unitId) throws BusinessException {
        List<V3xOrgRelationship> relList = orgCache.getRoleEntityRelastionships(OrgConstants.VJOIN_SUBMANAGER_ROLE_ID, unitId, null);
        if(relList.isEmpty()) {
            return null;
        }
        
    	V3xOrgRelationship rel = relList.get(0);
        V3xOrgMember admin = orgManager.getMemberById(rel.getSourceId());
        if (admin != null && !admin.getIsDeleted()) {
            return admin;
        }
        
        return null;

    }

}
