package com.seeyon.ctp.organization.manager;

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

import org.apache.commons.logging.Log;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.RelationshipType;
import com.seeyon.ctp.organization.bo.CompareSortEntity;
import com.seeyon.ctp.organization.bo.CompareSortMemberPost;
import com.seeyon.ctp.organization.bo.CompareUnitPath;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.organization.dao.OrgDao;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.po.OrgRole;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;


public class BusinessOrgManagerDirectImpl implements BusinessOrgManagerDirect {

	private final static Log     logger       = CtpLogFactory.getLog(OrgManagerImpl.class);
	
	private OrgDao orgDao;
	private OrgManager orgManager;
	private OrgCache orgCache;
	private OrgManagerDirect orgManagerDirect;

	public void setOrgDao(OrgDao orgDao) {
		this.orgDao = orgDao;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	@Override
	public OrganizationMessage addAccount(V3xOrgAccount account) throws BusinessException {
        if(null == account) {
        	logger.error("创建业务组织失败，传入的单位实体为空！");
            throw new BusinessException(ResourceUtil.getString("org.business.addaccount.fail"));
        }

        OrganizationMessage message = new OrganizationMessage();
        account.setIdIfNew();
        account.setExternalType(OrgConstants.ExternalType.Interconnect4.ordinal());
        account.setIsInternal(false);
        String path = OrgHelper.getPathByPid4Add(V3xOrgUnit.class, account.getSuperior());
        account.setPath(path);
        account.setType(OrgConstants.UnitType.Account);
        //1、校验数据
        boolean isDuplicated = orgDao.isPropertyDuplicated(V3xOrgAccount.class, "name", account.getName(),account.getId(),account.getId(),OrgConstants.ExternalType.Interconnect4.ordinal(), account.getCreaterId());
        if (isDuplicated) {
            message.addErrorMsg(account, ResourceUtil.getString("org.business.name.repeat"));
            return message;
        }
        //判断单位编码是否重复
        isDuplicated = orgDao.isPropertyDuplicated(V3xOrgAccount.class, "code", account.getCode(),account.getId(),account.getId(),OrgConstants.ExternalType.Interconnect4.ordinal(), account.getCreaterId());
        if (isDuplicated) {
            message.addErrorMsg(account, ResourceUtil.getString("org.business.code.repeat"));
            return message;
        }
        
        //sortId处理
        if(OrgConstants.SORTID_TYPE_INSERT.equals(account.getSortIdType())
                && orgDao.isPropertyDuplicated(V3xOrgAccount.class, "sortId", account.getSortId(),account.getId(),account.getId(),OrgConstants.ExternalType.Interconnect4.ordinal(), AppContext.currentUserId())) {//插入
            orgDao.insertRepeatSortNum(V3xOrgAccount.class, null, account.getSortId(),null,OrgConstants.ExternalType.Interconnect4.ordinal(),account.getId(),account.getCreaterId());
        }
        //2、实例化
        List<OrgUnit> poList = new ArrayList<OrgUnit>(1);
        poList.add((OrgUnit) account.toPO());
        
        orgDao.insertOrgUnit(poList);
        message.addSuccessMsg(account);
        return message;
	}
	
	@Override
	public OrganizationMessage updateAccount(V3xOrgAccount account) throws BusinessException {
		OrganizationMessage message = new OrganizationMessage();
        account.setExternalType(OrgConstants.ExternalType.Interconnect4.ordinal());
        account.setIsInternal(false);
        account.setType(OrgConstants.UnitType.Account);
        //1、校验数据
        boolean isDuplicated = orgDao.isPropertyDuplicated(V3xOrgAccount.class, "name", account.getName(),account.getId(),account.getId(),OrgConstants.ExternalType.Interconnect4.ordinal(), account.getCreaterId());
        if (isDuplicated) {
            message.addErrorMsg(account, ResourceUtil.getString("org.business.name.repeat"));
            return message;
        }
        //判断单位编码是否重复
        isDuplicated = orgDao.isPropertyDuplicated(V3xOrgAccount.class, "code", account.getCode(),account.getId(),account.getId(),OrgConstants.ExternalType.Interconnect4.ordinal(), account.getCreaterId());
        if (isDuplicated) {
            message.addErrorMsg(account, ResourceUtil.getString("org.business.code.repeat"));
            return message;
        }
        
        //sortId处理
        if(OrgConstants.SORTID_TYPE_INSERT.equals(account.getSortIdType())
                && orgDao.isPropertyDuplicated(V3xOrgAccount.class, "sortId", account.getSortId(),account.getId(),account.getId(),OrgConstants.ExternalType.Interconnect4.ordinal(), AppContext.currentUserId())) {//插入
            orgDao.insertRepeatSortNum(V3xOrgAccount.class, null, account.getSortId(),null,OrgConstants.ExternalType.Interconnect4.ordinal(),account.getId(),account.getCreaterId());
        }
        // 实例化数据
        orgDao.update((OrgUnit)OrgHelper.boTopo(account));
        message.addSuccessMsg(account);
        return message;
	}
	
	@Override
	public OrganizationMessage deleteAccount(V3xOrgAccount account) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        
		List<V3xOrgDepartment> deptlist = orgManagerDirect.getAllDepartments(account.getId(), null, null, null, null, null);
		if(Strings.isNotEmpty(deptlist)){
			message.addErrorMsg(account, ResourceUtil.getString("org.business.deletemsg2"));
			return message;
		}
		
        //实例化
        account.setIsDeleted(true);
        account.setEnabled(false);
        account.setUpdateTime(new Date());
        orgDao.update((OrgUnit) account.toPO());
        
        //删除组织下的角色
        List<OrgRole> list = orgDao.getAllRolePO(account.getId(), null, null, null);
        for (int i = 0; i < list.size(); i++) {
            OrgRole role = list.get(i);
            role.setEnable(false);
            role.setDeleted(true);
        }
        orgDao.updates(list);
        //删除组织授权关系
        //orgDao.deleteOrgRelationshipPO(OrgConstants.RelationshipType.Business_Access.name(), account.getId(), account.getId(), null);

        //删除某单位内在关系表中得所有关系
        orgDao.deleteOrgRelationshipPOByAccountId(account.getId());
        message.addSuccessMsg(account);
        return message;
	}
	
	@Override
	public List<V3xOrgAccount> getAccountList(Long createrId,Boolean enable)throws BusinessException {
		List<V3xOrgAccount> result = new ArrayList<V3xOrgAccount>();
		List<OrgUnit> list =  (List<OrgUnit>) orgDao.getAllBusinessUnitPO(createrId, enable, null, null, null);
		if(Strings.isEmpty(list)){
			return result;
		}
		for(OrgUnit unit : list){
			result.add((V3xOrgAccount)OrgHelper.poTobo(unit));
		}
		Collections.sort(result, CompareSortEntity.getInstance());
		return result;
	}
	
    @Override
    public OrganizationMessage addDepartment(V3xOrgDepartment dept) throws BusinessException {
        List<V3xOrgDepartment> depts = new ArrayList<V3xOrgDepartment>();
        depts.add(dept);
        return this.addDepartments(depts);
    }

    @Override
    public OrganizationMessage addDepartments(List<V3xOrgDepartment> depts) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        List<OrgUnit> poList = new ArrayList<OrgUnit>();
        for (V3xOrgDepartment dept : depts) {
        	dept.setExternalType(OrgConstants.ExternalType.Interconnect4.ordinal());
        	dept.setIsInternal(false);
            if (Strings.isNotBlank(dept.getCode())) {
                //判断部门编码是否重复
            	boolean isDuplicated = orgDao.isPropertyDuplicated(V3xOrgDepartment.class, "code", dept.getCode(),dept.getOrgAccountId(),dept.getId(),OrgConstants.ExternalType.Interconnect4.ordinal());
                if (isDuplicated) {
                    message.addErrorMsg(dept, ResourceUtil.getString("org.business.code.repeat"));
                    return message;
                }
                
                if(dept.getCode().length()>50){
                	message.addErrorMsg(dept, ResourceUtil.getString("org.business.code.toolarge"));
                	return message;
                }
            }
            
            
            V3xOrgUnit parent = orgManager.getUnitById(dept.getSuperior());
            
            if (parent == null || !parent.isValid()) {
                message.addErrorMsg(dept, OrganizationMessage.MessageStatus.DEPARTMENT_PARENTDEPT_DISABLED);
                return message;
            }
            
            //校验同级部门名称是否重复
            List<V3xOrgDepartment>  brother = this.getChildDepartments(dept.getSuperior(), true);
            boolean isduplicated = isExistRepeatProperty(brother, "name", dept.getName(), dept);
            if (isduplicated && OrgConstants.ExternalType.Interconnect4.ordinal() == dept.getExternalType()) {
            	message.addErrorMsg(dept, ResourceUtil.getString("org.business.dept.name.repeat"));
            	return message;
            }
            
            if(null == dept.getSortId()) {
                dept.setSortId(Long.valueOf(orgDao.getMaxSortId(V3xOrgAccount.class, dept.getOrgAccountId(), OrgConstants.ExternalType.Interconnect4.ordinal()) + 1));
            }
            
            String path = OrgHelper.getPathByPid4Add(V3xOrgUnit.class, dept.getSuperior(),null);
            dept.setPath(path);
            dept.setIdIfNew();
            dept.setType(OrgConstants.UnitType.Department);
            message.addSuccessMsg(dept);
            poList.add((OrgUnit) dept.toPO());
            
            this.orgCache.cacheUpdate(dept);
        }

        //实例化
        orgDao.insertOrgUnit(poList);

        return message;
    }
    
    @Override
    public OrganizationMessage updateDepartment(V3xOrgDepartment dept) throws BusinessException {
        List<V3xOrgDepartment> departments = new ArrayList<V3xOrgDepartment>(1);
        departments.add(dept);
        return this.updateDepartments(departments);
    }

    @Override
    public OrganizationMessage updateDepartments(List<V3xOrgDepartment> depts) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        
        for (V3xOrgDepartment dept : depts) {
            //如果修改了上级单位再重新计算Path
        	List<V3xOrgUnit> childUnits = new ArrayList<V3xOrgUnit>();
        	
            if (Strings.isNotBlank(dept.getCode())) {
                //判断部门编码是否重复
                boolean isDuplicated = orgDao.isPropertyDuplicated(V3xOrgDepartment.class, "code", dept.getCode(),dept.getOrgAccountId(),dept.getId(),OrgConstants.ExternalType.Interconnect4.ordinal());
                if (isDuplicated) {
                    message.addErrorMsg(dept, OrganizationMessage.MessageStatus.DEPARTMENT_REPEAT_CODE);
                    return message;
                }
                
                if(dept.getCode().length()>50){
                	message.addErrorMsg(dept, ResourceUtil.getString("org.business.code.toolarge"));
                	return message;
                }
            }
            
            V3xOrgDepartment oldDept = orgManager.getEntityById(V3xOrgDepartment.class, dept.getId());
            //如果更改了启用标志为启用，则要判断上级部门是否启用
            if (dept.getEnabled()) {
                if (dept.getSuperior() == -1L || !orgManager.getUnitById(dept.getSuperior()).getEnabled()) {
                    message.addErrorMsg(dept, OrganizationMessage.MessageStatus.DEPARTMENT_PARENTDEPT_DISABLED);
                    return message;
                }
            } else {
                //部门下有有效的自部门，不允许停用
                childUnits = orgCache.getChildUnitsByPid(V3xOrgUnit.class, dept.getId());
                Collections.sort(childUnits, CompareUnitPath.getInstance());
                for (V3xOrgUnit v3xOrgDepartment : childUnits) {
                    if (v3xOrgDepartment.isValid()) {
                        message.addErrorMsg(dept, OrganizationMessage.MessageStatus.DEPARTMENT_EXIST_DEPARTMENT_ENABLE);
                        return message;
                    }
                }
            }
            
        	if(orgManager.getUnitById(dept.getSuperior()).getPath().contains(orgManager.getDepartmentById(dept.getId()).getPath())){
        	    //message.addErrorMsg(dept, "");
        		message.addErrorMsg(dept, OrganizationMessage.MessageStatus.DEPARTMENT_PARENTDEPT_ISCHILD);
        		return message;
            }

            if (!Strings.equals(dept.getSuperior(), oldDept.getSuperior()) || (!oldDept.isValid() && dept.isValid())) {
                String path = OrgHelper.getPathByPid4Add(V3xOrgUnit.class, dept.getSuperior(),null);
                dept.setPath(path);
            } else {
                dept.setPath(oldDept.getPath());
            }
            
            //校验同级部门名称是否重复
            List<V3xOrgDepartment> brother;
            brother = this.getChildDepartments(dept.getSuperior(), true);
            boolean isduplicated = isExistRepeatProperty(brother, "name", dept.getName(), dept);
            
            if (isduplicated) {
            	message.addErrorMsg(dept, OrganizationMessage.MessageStatus.DEPARTMENT_REPEAT_NAME);
            	return message;
            }
            
            dept.setCreateTime(oldDept.getCreateTime());
            orgDao.update((OrgUnit) dept.toPO());
            message.addSuccessMsg(dept);
            
            for (V3xOrgUnit c : childUnits) {
                c.setPath(c.getPath().replaceFirst(oldDept.getPath(), dept.getPath()));
                c.setOrgAccountId(dept.getOrgAccountId());
                orgDao.update((OrgUnit) OrgHelper.boTopo(c));
            }
        }
        return message;
    }
    
    @Override
    public OrganizationMessage deleteDepartment(V3xOrgDepartment dept) throws BusinessException {
        if (dept == null) {
            throw new BusinessException(ResourceUtil.getString("org.business.dept.null"));
        }
        List<V3xOrgDepartment> deps = new ArrayList<V3xOrgDepartment>(1);
        deps.add(dept);
        return this.deleteDepartments(deps);
    }

    @Override
    public OrganizationMessage deleteDepartments(List<V3xOrgDepartment> depts) throws BusinessException {
        OrganizationMessage message = new OrganizationMessage();
        List<V3xOrgDepartment> deleteLists = new UniqueList<V3xOrgDepartment>();
        //1、校验 数据
        for (V3xOrgDepartment dept : depts) {
            // 增加删除部门下子部门的方法
            List<V3xOrgDepartment> childs = getChildDepartments(dept.getId(), false);
            for (V3xOrgDepartment c : childs) {
                deleteLists.add(c);
            }
            deleteLists.add(dept);
            message.addSuccessMsg(dept);
        }
        
        List<V3xOrgRelationship> deleteRelationShips = new ArrayList<V3xOrgRelationship>();
    	for(V3xOrgDepartment dept : deleteLists){
    		//删除部门下的人员关系数据
			deleteRelationShips.addAll(orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.BusinessDepartment_Member, dept.getId(), (Long)null, null));
    		
            //删除这个部门角色下的所有角色关系数据
			EnumMap<RelationshipObjectiveName, Object> objectiveIds = new EnumMap<RelationshipObjectiveName, Object>(RelationshipObjectiveName.class);
			objectiveIds.put(OrgConstants.RelationshipObjectiveName.objective0Id, dept.getId());
			deleteRelationShips.addAll(orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, (Long)null, (Long)null, objectiveIds));

    		dept.setEnabled(false);
    		dept.setIsDeleted(true);
    		orgDao.update((OrgUnit) dept.toPO());
    	}
        //删除关系数据
        orgManagerDirect.deleteOrgRelationships(deleteRelationShips);
        
        return message;
    }
    
    private boolean isExistRepeatProperty(List<? extends V3xOrgEntity> ents, String propertyName, Object value,
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
            logger.error("实体中不存在" + propertyName + "属性的get方法。", e);
        }
        return false;
    }
    
    private Object getEntityProperty(V3xOrgEntity entity, String property) throws Exception {
        return OrgHelper.getProperty(entity, property);
    }
    
    @Override
    public List<V3xOrgDepartment> getChildDepartments(Long parentDepId, boolean firtLayer) throws BusinessException {
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
    
    @Override
    public List<V3xOrgMember> getAllMembers(Long accountId) throws BusinessException{
    	List<V3xOrgMember> members = new UniqueList<V3xOrgMember>();
    	
		List<V3xOrgRelationship> list = orgManager.getV3xOrgRelationship(RelationshipType.BusinessDepartment_Member, null, accountId,null);
		for(V3xOrgRelationship rel : list){
			Long deptId = rel.getSourceId();
			V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
			if(dept == null || !dept.isValid()) {
				continue;
			}
			String type = rel.getObjective5Id();
			Long id = rel.getObjective0Id();
			V3xOrgEntity entity = orgManager.getEntity(type, id);
			if(entity == null || !entity.isValid()){
				continue;
			}
			if(OrgConstants.ORGENT_TYPE.Member.name().equals(entity.getEntityType())){
				members.add((V3xOrgMember)entity);
			}else if(OrgConstants.ORGENT_TYPE.Post.name().equals(entity.getEntityType())){
				members.addAll(orgManager.getMembersByPost(id));
			}else if(OrgConstants.ORGENT_TYPE.Department.name().equals(entity.getEntityType())){
				boolean firstLayer = false;
				if("1".equals(rel.getObjective7Id())){
					firstLayer = true;
				}
				members.addAll(orgManager.getMembersByDepartment(id, firstLayer));
			}
		}
		
		Collections.sort(members, CompareSortEntity.getInstance());
		return members;
    }
    
    
    @Override
    public List<V3xOrgMember> getMembersByDepartment(Long deptId, boolean firtLayer) throws BusinessException{
    	List<V3xOrgMember> result = new UniqueList<V3xOrgMember>();
    	
    	List<V3xOrgDepartment> depts = new ArrayList<V3xOrgDepartment>();
    	V3xOrgDepartment department = orgManager.getDepartmentById(deptId);
    	if(department != null && department.isValid()){
    		depts.add(department);
    		if(!firtLayer){
    			depts.addAll(this.getChildDepartments(deptId, firtLayer));
    		}
    	}
    	
    	for(V3xOrgDepartment dept : depts){
    		List<V3xOrgRelationship> list = orgManager.getV3xOrgRelationship(RelationshipType.BusinessDepartment_Member, dept.getId(), dept.getOrgAccountId(),null);
    		for(V3xOrgRelationship rel : list){
    			String type = rel.getObjective5Id();
    			Long id = rel.getObjective0Id();
    			V3xOrgEntity entity = orgManager.getEntity(type, id);
    			if(entity == null || !entity.isValid()){
    				continue;
    			}
    			if(OrgConstants.ORGENT_TYPE.Member.name().equals(entity.getEntityType())){
    				result.add((V3xOrgMember)entity);
    			}else if(OrgConstants.ORGENT_TYPE.Post.name().equals(entity.getEntityType())){
    				result.addAll(orgManager.getMembersByPost(id));
    			}else if(OrgConstants.ORGENT_TYPE.Department.name().equals(entity.getEntityType())){
    				boolean firstLayer = false;
    				if("1".equals(rel.getObjective7Id())){
    					firstLayer = true;
    				}
    				result.addAll(orgManager.getMembersByDepartment(id, firstLayer));
    			}
    		}
    	}
    	Collections.sort(result, CompareSortEntity.getInstance());
    	return result;
    }
    
    @Override
    public List<MemberPost> getMemberPostByDepartment(Long deptId, boolean firtLayer) throws BusinessException{
    	List<MemberPost> memberPostList = new UniqueList<MemberPost>();
    	
    	List<V3xOrgDepartment> depts = new ArrayList<V3xOrgDepartment>();
    	V3xOrgDepartment department = orgManager.getDepartmentById(deptId);
    	if(department != null && department.isValid()){
    		depts.add(department);
    		if(!firtLayer){
    			depts.addAll(this.getChildDepartments(deptId, firtLayer));
    		}
    	}
    	
    	for(V3xOrgDepartment dept : depts){
    		List<V3xOrgRelationship> list = orgManager.getV3xOrgRelationship(RelationshipType.BusinessDepartment_Member, dept.getId(), dept.getOrgAccountId(),null);
    		for(V3xOrgRelationship rel : list){
    			String type = rel.getObjective5Id();
    			Long id = rel.getObjective0Id();
    			V3xOrgEntity entity = orgManager.getEntity(type, id);
    			if(entity == null || !entity.isValid()){
    				continue;
    			}
    			if(OrgConstants.ORGENT_TYPE.Member.name().equals(entity.getEntityType())){
    				V3xOrgMember member = orgManager.getMemberById(id);
    				if(member != null && member.isValid()) {
    					memberPostList.add(MemberPost.createMainPost(member));
    				}
	            	
    			}else if(OrgConstants.ORGENT_TYPE.Post.name().equals(entity.getEntityType())){
    				memberPostList.addAll(orgManager.getMemberPostByPost(id));
    			}else if(OrgConstants.ORGENT_TYPE.Department.name().equals(entity.getEntityType())){
    				boolean firstLayer = false;
    				if("1".equals(rel.getObjective7Id())){
    					firstLayer = true;
    				}
    				memberPostList.addAll(orgManager.getMemberPostByDepartment(id, firstLayer));
    			}
    		}
    	}
    	Collections.sort(memberPostList, CompareSortMemberPost.getInstance());
    	return memberPostList;
    }
    
    @Override
    public Set<Long> getAccessBusinessMember(Long memberId,Long businessId) throws BusinessException {
    	Set<Long> accessBusinessMembersIds = new HashSet<Long>();//业务线内可见的人
    	V3xOrgAccount business = orgManager.getAccountById(businessId);
    	if(business == null){
    		return accessBusinessMembersIds;
    	}
    	
    	if(!canAccess(memberId,businessId)) {
    		return accessBusinessMembersIds;
    	}
    	
    	List<V3xOrgDepartment> myDepts = new UniqueList<V3xOrgDepartment>();
    	List<V3xOrgDepartment> otherDepts = this.getChildDepartments(businessId, false);
    	
    	List<V3xOrgDepartment>  depts = new UniqueList<V3xOrgDepartment>();
    	depts.addAll(otherDepts);
    	
    	V3xOrgMember member = orgManager.getMemberById(memberId);
    	
    	for (V3xOrgDepartment dept : depts) {
    		if(myDepts.contains(dept)) continue;
    		List<V3xOrgMember> deptMembers = this.getMembersByDepartment(dept.getId(), true);
    		if(deptMembers.contains(member)){//当前人员在这个业务线部门下，那就能看到这个业务线部门及其及部门下的所有人员。
    			List<V3xOrgDepartment> childDepts = this.getChildDepartments(dept.getId(), false);
    			myDepts.add(dept);
    			myDepts.addAll(childDepts);
    			
    			otherDepts.remove(dept);
    			otherDepts.removeAll(childDepts);
    			
    		}
    	}
    	
    	for (V3xOrgDepartment dept : myDepts) {
    		List<V3xOrgMember> deptMembers = this.getMembersByDepartment(dept.getId(), true);
    		for(V3xOrgMember m : deptMembers){
    			accessBusinessMembersIds.add(m.getId());
    		}
    	}
    	
    	for (V3xOrgDepartment dept : otherDepts) {
    		List<V3xOrgMember> deptMembers = this.getMembersByDepartment(dept.getId(), true);
    		List<V3xOrgMember> accessMember = OrgHelper.checkLevelScope(memberId, deptMembers);
    		for(V3xOrgMember m : accessMember){
    			Long id = m.getId();
    			if(!accessBusinessMembersIds.contains(id)){
    				accessBusinessMembersIds.add(m.getId());
    			}
    		}
    	}

        return accessBusinessMembersIds;
    }
    
	@Override
	public List<V3xOrgAccount> getAccessBusiness(Long memberId, Long accountId) throws BusinessException{
		List<V3xOrgAccount> result = new UniqueList<V3xOrgAccount>();
    	Long createrId = null;
    	V3xOrgAccount currentAccount = orgManager.getAccountById(accountId);
    	if(currentAccount.getIsGroup()){
    		createrId = OrgConstants.GROUP_ADMIN_ID;
    	}else{
    		V3xOrgMember member = orgManager.getAdministrator(accountId);
    		if(member != null){
    			createrId = member.getId();
    		}
    	}
    	
    	List<V3xOrgAccount> businessList = new ArrayList<V3xOrgAccount>();
    	if(createrId != null){
    		businessList = this.getAccountList(createrId, true);
    	}
    	
		Collections.sort(businessList, CompareSortEntity.getInstance());
    	for(V3xOrgAccount business : businessList){
    		boolean canShow = false;
            if(!business.getIsPublic()){
            	List<V3xOrgMember> members = this.getAllMembers(business.getId());
            	for(V3xOrgMember member : members){
            		if(member.getId().equals(memberId)){
            			canShow = true;
            			break;
            		}
            	}
            	
            	if(!canShow){
            		List<V3xOrgRelationship> reflist = orgManager.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, null, business.getId(), null);
            		for (V3xOrgRelationship v3xOrgRelationship : reflist) {
        				Long roleId = v3xOrgRelationship.getObjective1Id();
        				if(OrgConstants.BUSINESS_ORGANIZATION_ROLE_ID.equals(roleId)) {
        					continue;
        				}
            			Long roleMemberId = v3xOrgRelationship.getSourceId();
                		if(roleMemberId.equals(memberId)){
                			canShow = true;
                			break;
                		}
            		}
            	}
            }else{
            	canShow = true;
            }

            if(canShow){
            	result.add(business);
            }
    		
    	}
    	
    	return result;
	}
	
	@Override
	public boolean canAccess(Long memberId, Long businessId) throws BusinessException{
		boolean canAccess = false;
		V3xOrgAccount business = orgManager.getAccountById(businessId);
		if(business.getCreaterId().equals(memberId) || orgManager.isRole(memberId, businessId, OrgConstants.Role_NAME.BusinessOrganizationManager.name())) {
			return true;
		}
        if(!business.getIsPublic()){
        	List<V3xOrgMember> members = this.getAllMembers(business.getId());
        	for(V3xOrgMember member : members){
        		if(member.getId().equals(memberId)){
        			canAccess = true;
        			break;
        		}
        	}
        	
        	if(!canAccess){
        		List<V3xOrgRelationship> reflist = orgManager.getV3xOrgRelationship(OrgConstants.RelationshipType.Member_Role, null, business.getId(), null);
        		for (V3xOrgRelationship v3xOrgRelationship : reflist) {
    				Long roleId = v3xOrgRelationship.getObjective1Id();
    				if(OrgConstants.BUSINESS_ORGANIZATION_ROLE_ID.equals(roleId)) {
    					continue;
    				}
        			Long roleMemberId = v3xOrgRelationship.getSourceId();
            		if(roleMemberId.equals(memberId)){
            			canAccess = true;
            			break;
            		}
        		}
        	}
        }else{
        	canAccess = true;
        }
    	return canAccess;
	}
	
    @Override
    public V3xOrgRole getRoleByCode(String code, Long accountId) throws BusinessException {
        List<V3xOrgRole> list = orgCache.getAllV3xOrgEntity(V3xOrgRole.class, accountId,null);
        for (Iterator<V3xOrgRole> it = list.iterator(); it.hasNext();) {
            V3xOrgRole role = it.next();
            if (role != null && role.isValid() && code.equals(role.getCode())){
                return role;
            }
        }
        return null;
    }
    
    @Override
    public List<V3xOrgDepartment> getBusinessDeptsByMemberId(Long memberId,Long businessId,boolean firtLayer) throws BusinessException {
    	List<V3xOrgDepartment> memberDepts = new UniqueList<V3xOrgDepartment>();
    	V3xOrgMember member = orgManager.getMemberById(memberId);
    	if(businessId != null) {
    		List<V3xOrgDepartment> allDepts = this.getChildDepartments(businessId, false);
    		List<V3xOrgDepartment>  depts = new UniqueList<V3xOrgDepartment>();
    		depts.addAll(allDepts);
    		for (V3xOrgDepartment dept : depts) {
    			if(memberDepts.contains(dept)) continue;
    			List<V3xOrgMember> deptMembers = this.getMembersByDepartment(dept.getId(), true);
    			if(deptMembers.contains(member)){
    				memberDepts.add(dept);
    				if(!firtLayer) {
    					List<V3xOrgDepartment> childDepts = getChildDepartments(dept.getId(), firtLayer);
    					memberDepts.addAll(childDepts);
    				}
    			}
    		}
    	}else {//如果没有指定业务线，获取所有所属的部门
    		List<V3xOrgRelationship> list = orgCache.getV3xOrgRelationship(RelationshipType.BusinessDepartment_Member);
    		Map<Long,V3xOrgDepartment> deptMap = new HashMap<Long,V3xOrgDepartment>();
    		V3xOrgDepartment businessDept;
    		for(V3xOrgRelationship rel : list){
    			Long businessDeptId = rel.getSourceId();
    			if(deptMap.containsKey(businessDeptId)) {
    				businessDept = deptMap.get(businessDeptId);
    			}else {
    				businessDept = orgManager.getDepartmentById(businessDeptId);
    				deptMap.put(businessDeptId, businessDept);
    			}
    			if(businessDept == null || !businessDept.isValid()) continue;
    			businessId = rel.getOrgAccountId();
    			
				if(memberDepts.contains(businessDept)){
					continue;
				}
				
    			String type = rel.getObjective5Id();
    			Long id = rel.getObjective0Id();
    			V3xOrgEntity entity = orgManager.getEntity(type, id);
    			if(entity == null || !entity.isValid()){
    				continue;
    			}
    			
    			if(OrgConstants.ORGENT_TYPE.Member.name().equals(entity.getEntityType())){
    				if(member.getId().equals(id)){
    					memberDepts.add(businessDept);
    				}
    			}else if(OrgConstants.ORGENT_TYPE.Post.name().equals(entity.getEntityType())){
    				List<V3xOrgMember> postMembers = orgManager.getMembersByPost(id);
    				if(postMembers.contains(member)){
    					memberDepts.add(businessDept);
    				}
    			}else if(OrgConstants.ORGENT_TYPE.Department.name().equals(entity.getEntityType())){
    				boolean firstLayer = false;
    				if("1".equals(rel.getObjective7Id())){
    					firstLayer = true;
    				}
    				List<V3xOrgMember> deptMembers = orgManager.getMembersByDepartment(id, firstLayer);
    				if(deptMembers.contains(member)){
    					memberDepts.add(businessDept);
    				}
    			}
    			
    			if(!firtLayer) {
    				memberDepts.addAll(this.getChildDepartments(businessDeptId, false));
    			}
    		}
    	}
    	return memberDepts;
    }
    
}
