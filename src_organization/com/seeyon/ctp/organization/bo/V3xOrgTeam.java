package com.seeyon.ctp.organization.bo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.po.OrgTeam;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;

/**
 * The persistent class for the v3x_org_team database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xOrgTeam extends V3xOrgEntity implements Serializable {

    private static final long serialVersionUID = -7290690162493785003L;

    private static final Log  log              = LogFactory.getLog(V3xOrgTeam.class);
	
	private Long depId  = V3xOrgEntity.DEFAULT_NULL_ID;
	private int type = OrgConstants.TEAM_TYPE.SYSTEM.ordinal();
	private Long ownerId =   V3xOrgEntity.DEFAULT_NULL_ID;
	private Long createId =   V3xOrgEntity.DEFAULT_NULL_ID;
	
	

	//以下成员不在组里面持久化
//	private HashMap<Long, V3xOrgMember> members = new HashMap<Long, V3xOrgMember>();
	private List<OrgTypeIdBO> members = new UniqueList<OrgTypeIdBO>();
	private List<OrgTypeIdBO> leaders = new UniqueList<OrgTypeIdBO>(); 
	private List<OrgTypeIdBO> supervisors = new UniqueList<OrgTypeIdBO>();
	private List<OrgTypeIdBO> relatives = new UniqueList<OrgTypeIdBO>();
	
	/**
	 * 复制传入的实体的属性值到Team的实例。
	 * 
	 * @param orgTeam
	 */
    public V3xOrgTeam(V3xOrgTeam orgTeam) {
        this();
        this.id = orgTeam.getId();
        this.ownerId = orgTeam.getOwnerId();
        this.name = orgTeam.getName();
        this.sortId = orgTeam.getSortId();
        this.orgAccountId = orgTeam.getOrgAccountId();
        this.createTime = orgTeam.getCreateTime();
        this.description = orgTeam.getDescription();
        this.enabled = orgTeam.getEnabled();
        this.updateTime = orgTeam.getUpdateTime();
        this.status = orgTeam.getStatus();
        this.isDeleted = orgTeam.getIsDeleted();
        this.type = orgTeam.getType();
        this.code = orgTeam.getCode();
        this.createId = orgTeam.getCreateId();
        
        // copyProperties复制了集合的引用，需要处理
        members = new UniqueList<OrgTypeIdBO>(members);
        leaders = new UniqueList<OrgTypeIdBO>(leaders);
        supervisors = new UniqueList<OrgTypeIdBO>(supervisors);
        relatives = new UniqueList<OrgTypeIdBO>(relatives);
    }	

    public V3xOrgTeam() {
    }
    
    public V3xOrgTeam(OrgTeam orgTeam) {
        this.fromPO(orgTeam);
    }
    
    public V3xOrgEntity fromPO(BasePO po) {
        OrgTeam orgTeam = (OrgTeam)po;
        
        this.id = orgTeam.getId();
        this.ownerId = orgTeam.getOwnerId();
        this.name = orgTeam.getName();
        this.sortId = orgTeam.getSortId();
        this.orgAccountId = orgTeam.getOrgAccountId();
        this.createTime = orgTeam.getCreateTime();
        this.description = orgTeam.getDescription();
        this.enabled = orgTeam.isEnable();
        this.updateTime = orgTeam.getUpdateTime();
        this.status = orgTeam.getStatus();
        this.isDeleted = orgTeam.isDeleted();
        this.type = orgTeam.getType();
        this.code = orgTeam.getCode();
        this.createId = orgTeam.getCreaterId();
        
        return this;
    }

    public BasePO toPO() {
        OrgTeam o = new OrgTeam();
        o.setId(this.id);
        o.setOwnerId(this.ownerId);
        o.setName(this.name);
        o.setSortId(this.sortId.longValue());
        o.setOrgAccountId(this.orgAccountId);
        o.setCreateTime(this.createTime);
        o.setDescription(this.description);
        o.setEnable(this.enabled);
        o.setUpdateTime(this.updateTime);
        o.setStatus(this.status);
        o.setDeleted(this.isDeleted);
        o.setType(this.type);
        o.setCode(this.code);
        o.setCreaterId(this.createId);
        return o;
    }

    public Long getCreateId() {
		return createId;
	}

	public void setCreateId(Long createId) {
		this.createId = createId;
	}
	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public Long getDepId() {
		return this.depId;
	}
	public void setDepId(Long depId) {
		this.depId = depId;
	}

    public String getEntityType() {
        return OrgConstants.ORGENT_TYPE.Team.name();
    }

    public List<OrgTypeIdBO> getMemberList(int memType) {
        if (OrgConstants.TeamMemberType.Member.ordinal() == memType) {
            return new ArrayList<OrgTypeIdBO>(members);
        }

        if (OrgConstants.TeamMemberType.Leader.ordinal() == memType) {
            return new ArrayList<OrgTypeIdBO>(leaders);
        }
        if (OrgConstants.TeamMemberType.SuperVisor.ordinal() == memType) {
            return new ArrayList<OrgTypeIdBO>(supervisors);
        }
        if (OrgConstants.TeamMemberType.Relative.ordinal() == memType) {
            return new ArrayList<OrgTypeIdBO>(relatives);
        }
        return Collections.emptyList();
    }

	public List<OrgTypeIdBO> getAllMembers() {
		List<OrgTypeIdBO> list = new ArrayList<OrgTypeIdBO>();
		list.addAll(leaders);
		list.addAll(members);
		return list;
	}

	public List<OrgTypeIdBO> getAllRelatives() {
		List<OrgTypeIdBO> list = new ArrayList<OrgTypeIdBO>();
		list.addAll(supervisors);
		list.addAll(relatives);
		return list;
	}

	public List<OrgTypeIdBO> getMembers() {
		return new ArrayList<OrgTypeIdBO>(members);
	}


	public void setMembers(List<OrgTypeIdBO> members) {
		this.members = members;
	}


	public Long getOwnerId() {
		return ownerId;
	}


	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}


	public int getType() {
		return type;
	}

	public void setType(int teamType) {
		this.type = teamType;
	}

	public List<OrgTypeIdBO> getLeaders() {
		return new ArrayList<OrgTypeIdBO>(leaders);
	}


	public void setLeaders(List<OrgTypeIdBO> leaders) {
		this.leaders = leaders;
	}


	public List<OrgTypeIdBO> getRelatives() {
		return new ArrayList<OrgTypeIdBO>(relatives);
	}


	public void setRelatives(List<OrgTypeIdBO> relatives) {
		this.relatives = relatives;
	}


	public List<OrgTypeIdBO> getSupervisors() {
		return new ArrayList<OrgTypeIdBO>(supervisors);
	}


	public void setSupervisors(List<OrgTypeIdBO> supervisors) {
		this.supervisors = supervisors;
	}

	/**
	 * 将成员添加成为组的不同类型的成员。 组下面有组长、组的领导、组员、组的相关人员四类人员。
	 * 其标志定义在V3xOrgEntity中，分别是ORGREL_TYPE_TEAM_LEADER,ORGREL_TYPE_TEAM_SUPERV,ORGREL_TYPE_TEAM_MEMBER,ORGREL_TYPE_TEAM_RELATIVE
	 * members: 人员列表 teamId: 组ID orgRelType: 四类人员的标志
	 * 
	 */
    public boolean addTeamMember(List<OrgTypeIdBO> memberIds, int orgRelType) {
        if (OrgConstants.TeamMemberType.Member.ordinal() == orgRelType) {
            return members.addAll(memberIds);
        }

        if (OrgConstants.TeamMemberType.Leader.ordinal() == orgRelType) {
            return leaders.addAll(memberIds);
        }

        if (OrgConstants.TeamMemberType.SuperVisor.ordinal() == orgRelType) {
            return supervisors.addAll(memberIds);
        }

        if (OrgConstants.TeamMemberType.Relative.ordinal() == orgRelType) {
            return relatives.addAll(memberIds);
        }

        return false;
    }
	
    public boolean addTeamMember(OrgTypeIdBO memberId, int orgRelType) {
        if (OrgConstants.TeamMemberType.Member.ordinal() == orgRelType) {
            return members.add(memberId);
        }

        if (OrgConstants.TeamMemberType.Leader.ordinal() == orgRelType) {
            return leaders.add(memberId);
        }

        if (OrgConstants.TeamMemberType.SuperVisor.ordinal() == orgRelType) {
            return supervisors.add(memberId);
        }

        if (OrgConstants.TeamMemberType.Relative.ordinal() == orgRelType) {
            return relatives.add(memberId);
        }

        return false;
    }
	
	/**
	 * 给选人界面用的，不要轻易修改
	 * 
	 * {D:"4671434749625321681",M:["3617689184970950159","-4311948622092547980","4095909756522350674","8437858001606167119"],K:"-8201352672941817077",T:1,N:"22"}
	 * @throws BusinessException 
	 */
	public void toJsonString(StringBuilder o, OrgManager orgManager, long loginAccountId) throws BusinessException {
		o.append("{");
		o.append(TOXML_PROPERTY_id).append(":'").append(this.getId()).append("'");
		o.append(",").append(TOXML_PROPERTY_NAME).append(":'").append(Strings.escapeJavascript(this.getName())).append("'");
		
		//为了节省流量, 系统组不传了, 前端兼容
		if(this.getType() != OrgConstants.TEAM_TYPE.SYSTEM.ordinal()){
		    o.append(",T:").append(this.getType());
		}
		
		if(!Strings.equals(this.getOwnerId(), loginAccountId)){
		    o.append(",O:'").append(Strings.escapeNULL(this.getOwnerId(), -1L)).append("'");
		}
		
		if(!Strings.equals(this.getOrgAccountId(), loginAccountId)){
		    o.append(",A:'").append(this.getOrgAccountId()).append("'");
		}
		
		List<OrgTypeIdBO> l;
		List<OrgTypeIdBO> ls = new ArrayList<OrgTypeIdBO>();
		l = this.getLeaders();
		if(l != null && !l.isEmpty()){ //主管
			o.append(",L:[");
			member(orgManager,o, l,ls);
			o.append("]");
		}
		
		l = this.getMembers();
		if(l != null && !l.isEmpty()){ //成员
			o.append(",M:[");
			member(orgManager,o, l,ls);
			o.append("]");
		}
		l = this.getSupervisors();
		if(l != null && !l.isEmpty()){ //组的领导
			o.append(",S:[");
			member(orgManager,o, l,ls);
			o.append("]");
		}
		l = this.getRelatives();
		if(l != null && !l.isEmpty()){ //关联人员
			o.append(",RM:[");
			member(orgManager,o, l,ls);
			o.append("]");
		}
		//外部单位数据
		o.append(",E:[]");
		
		o.append("}");
	}
	
	private void member(OrgManager orgManager,StringBuilder o, List<OrgTypeIdBO> l,List<OrgTypeIdBO> ls) throws BusinessException{
		int i = 0;
		Map<Long,String> map = new HashMap<Long,String>();
		for (OrgTypeIdBO m : l) {
			if(i++ != 0){
				o.append(",");
			}
			//Department|-567909894368|部门1
			//Department_Post|-567909894368_3846897087970|部门1-岗位1(北京分公司)
			String type = m.getType();
			String id = m.getId();
			Long accountId = null;
			String name = "";
			if(!V3xOrgEntity.ORGREL_TYPE_DEP_POST.equals(type)){
				List<V3xOrgEntity> entitys = orgManager.getEntities(type+"|"+id);
				V3xOrgEntity entity = entitys.get(0);
				name = entity.name;
				accountId = entity.getOrgAccountId();
			}else{
				int index = id.indexOf(V3xOrgEntity.ROLE_ID_DELIMITER);
				String deptId = id.substring(0,index);
				String postId = id.substring(index+1);
				List<V3xOrgEntity> deptEntity = orgManager.getEntities(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT+"|"+deptId);
				List<V3xOrgEntity> postEntity = orgManager.getEntities(V3xOrgEntity.ORGENT_TYPE_POST+"|"+postId);
				if(Strings.isNotEmpty(deptEntity) && Strings.isNotEmpty(postEntity)){
					V3xOrgDepartment dept = (V3xOrgDepartment)deptEntity.get(0);
					V3xOrgPost post = (V3xOrgPost)postEntity.get(0);
					name = dept.name+"-"+post.name;
					accountId = dept.getOrgAccountId();
				}
			}
			
			if(accountId!=null){
				if(!this.getOrgAccountId().equals(accountId)){//不是一个单位的
					if(map.get(accountId)==null){
						V3xOrgAccount account = orgManager.getAccountById(accountId);
						map.put(accountId, account.getShortName());
					}
					name = name+"("+map.get(accountId)+")";
				}
			}
			if(Strings.isNotBlank(name)){
				o.append("'").append(m.getType()+"|"+accountId+"_"+m.getId()+"|"+Strings.escapeJavascript(name)).append("'"); 
			}
		}
		ls.addAll(l);
	}

	public boolean isValid() {
		return enabled && !isDeleted;
	}

	/**
	 * 判断组成员中是否包含指定人员。
	 * @param memberId 人员
	 * @return 包含则返回true，否则返回false。
	 */
	@Deprecated
    public boolean contains(Long memberId) {
/*        if (members.contains(memberId)) {
            return true;
        }
        if (leaders.contains(memberId)) {
            return true;
        }
        if (supervisors.contains(memberId)) {
            return true;
        }
        if (relatives.contains(memberId)) {
            return true;
        }*/
        
        return false;
    }
}