package com.seeyon.ctp.organization.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.RelationshipObjectiveName;
import com.seeyon.ctp.organization.OrgConstants.RelationshipType;
import com.seeyon.ctp.organization.OrgConstants.UnitType;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.bo.V3xOrgUnit;
import com.seeyon.ctp.organization.bo.V3xOrgVisitor;
import com.seeyon.ctp.organization.po.OrgLevel;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.organization.po.OrgPost;
import com.seeyon.ctp.organization.po.OrgRelationship;
import com.seeyon.ctp.organization.po.OrgRole;
import com.seeyon.ctp.organization.po.OrgTeam;
import com.seeyon.ctp.organization.po.OrgUnit;
import com.seeyon.ctp.organization.po.OrgVisitor;
import com.seeyon.ctp.privilege.po.PrivRoleMenu;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.SQLWildcardUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;

/**
 * The DAO class for the entities: Department, Externalmember, Level, Member,
 * Metadata, Orgaccount, Orgproperty, Orgrelationship, Post, Team.
 */
public class OrgDaoImpl implements OrgDao {

	private final static Log log = LogFactory.getLog(OrgDaoImpl.class);
	private final static Long maxNum = 99999L;

	private OrgCache orgCache;

	public void setOrgCache(OrgCache orgCache) {
		this.orgCache = orgCache;
	}


	public List<OrgUnit> getAllUnitPO(OrgConstants.UnitType type,
			Long accountId, Boolean enable, Boolean isInternal,
			String condition, Object feildvalue, FlipInfo flipInfo) {
		return this.getAllUnitPO0(type, accountId, enable, isInternal, condition, feildvalue, flipInfo, false);

	}
	
	@Override
	public List<OrgUnit> getAllUnitPO0(UnitType type, Long accountId,
			Boolean enable, Boolean isInternal, String condition,
			Object feildvalue, FlipInfo flipInfo, boolean isEquals) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append("SELECT m ");
		hql.append(" FROM " + OrgUnit.class.getSimpleName() + " as m ");

		hql.append(" WHERE m.deleted=false ");
		if (type != null) {
			hql.append(" AND m.type=:type ");
			params.put("type", type.name());
		}
		if (accountId != null && !accountId.equals(V3xOrgEntity.VIRTUAL_ACCOUNT_ID)) {
			hql.append(" AND m.orgAccountId=:orgAccountId ");
			params.put("orgAccountId", accountId);
			OrgUnit unit = this.getEntity(OrgUnit.class, accountId);
			if(unit != null && unit.getExternalType() == OrgConstants.ExternalType.Interconnect4.ordinal()) {
				hql.append(" AND m.externalType=:externalType ");
				params.put("externalType", unit.getExternalType());
			}
		}else if(type != null && type == UnitType.Account) {//这种情况只能查询出内部的单位
			hql.append(" AND m.externalType=:externalType ");
			params.put("externalType", OrgConstants.ExternalType.Inner.ordinal());
		}
		if (isInternal != null) {
			hql.append(" AND m.internal=:internal ");
			params.put("internal", isInternal);
		}
		if (enable != null) {
			hql.append(" AND m.enable=:enable ");
			params.put("enable", enable);
		}

		if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
			if (feildvalue instanceof String && !isEquals) {
				hql.append(" AND m.").append(condition).append(" LIKE :feildvalue "+SQLWildcardUtil.setEscapeCharacter());
				feildvalue = "%" + SQLWildcardUtil.escape(String.valueOf(feildvalue)) + "%";
			} else {
				hql.append(" and m.").append(condition).append("=:feildvalue");
			}

			params.put("feildvalue", feildvalue);
		}

		hql.append(" ORDER BY m.sortId ASC");

		return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	@Override
	public List<OrgUnit> getAllBusinessUnitPO(Long createrId,Boolean enable, String condition,Object feildvalue, FlipInfo flipInfo) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append("SELECT m ");
		hql.append(" FROM " + OrgUnit.class.getSimpleName() + " as m ");

		hql.append(" WHERE m.deleted=false and m.externalType=" + OrgConstants.ExternalType.Interconnect4.ordinal());
		if (createrId != null) {
			hql.append(" AND m.createrId=:createrId ");
			params.put("createrId", createrId);
		}
		if (enable != null) {
			hql.append(" AND m.enable=:enable ");
			params.put("enable", enable);
		}

		if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
			if (feildvalue instanceof String) {
				hql.append(" AND m.").append(condition).append(" LIKE :feildvalue "+SQLWildcardUtil.setEscapeCharacter());
				feildvalue = "%" + SQLWildcardUtil.escape(String.valueOf(feildvalue)) + "%";
			} else {
				hql.append(" and m.").append(condition).append("=:feildvalue");
			}

			params.put("feildvalue", feildvalue);
		}

		hql.append(" ORDER BY m.sortId ASC");

		return DBAgent.find(hql.toString(), params, flipInfo);
	}

	public OrgUnit getV3xOrgUnitByPath(String path) {
		List<OrgUnit> unit = getAllUnitPO(null, null, null, null, "path", path,
				null);
		if (unit.size() == 0) {
			return null;
		} else {
			return unit.get(0);
		}
	}
	
	@Override
    public Map<Long, Long> getMemberNumsMapWithOutConcurrent() {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();

        StringBuilder hql = new StringBuilder();
        hql.append("SELECT new map(m.orgAccountId as accountId, count(m.orgAccountId) as nums) from OrgMember as m ");
        hql.append(" WHERE m.enable=true");
        hql.append(" AND m.deleted=false AND m.admin=false AND m.virtual=false AND m.assigned=true");
        hql.append(" GROUP BY m.orgAccountId");

        List rList = DBAgent.find(hql.toString());
        for (Object object : rList) {
            resultMap.put(Long.valueOf(String.valueOf(((Map) object).get("accountId"))),
                    Long.valueOf(String.valueOf(((Map)  object).get("nums"))));
        }
        return resultMap;
    }
	
    @Override
    public List<OrgMember> getAllMemberPOByAccountId(Long accountId, Boolean isInternal, Boolean enable,
            Map<String, Object> param, FlipInfo flipInfo) {
        if(param!=null && param.containsKey("secPostId") && null!=param.get("secPostId")){
        	Long secondPostId = Long.valueOf(param.get("secPostId").toString());
        	param.remove("secPostId");
        	return getAllMemberPOByAccountIdAndSecondPostId(accountId, secondPostId, isInternal, enable, param,flipInfo);
        }
        StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        hql.append("SELECT m ");

        if (null != param && param.containsKey("loginName")) {
            hql.append(" FROM OrgMember as m, OrgPrincipal as p ");
            hql.append(" WHERE m.id=p.memberId ");
        } else {
            hql.append(" FROM OrgMember as m ");
            hql.append(" WHERE 1=1 ");
        }
        
        if (accountId != null) {
            hql.append(" AND m.orgAccountId=:accountId ");
            params.put("accountId", accountId);
        }
        if (isInternal != null) {
            hql.append(" AND m.internal=:internal");
            params.put("internal", isInternal);
        }
        if (enable != null) {
            hql.append(" AND m.enable=:enable ");
            params.put("enable", enable);
        }

        hql.append(" AND m.deleted=false AND m.admin=false AND m.virtual=false AND m.assigned=true");

        if (null != param) {
            Set<Entry<String, Object>> paramSet = param.entrySet();
            for (Entry<String, Object> entry : paramSet) {
                String condition = entry.getKey();
                Object feildvalue = entry.getValue();
                if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
                	if("workLocal".equals(condition)){
                		if("".equals(entry.getValue())){
                			hql.append(" AND (m.extAttr36").append("like :" + condition+" or m.extAttr36 is null)");
                		}else{
                			hql.append(" AND m.extAttr36").append(" like :" + condition);
                		}
                		params.put(condition, feildvalue);
                	}else if("orgDepartmentId_includeChildren".equals(condition)){
                		Long departmentId = Long.valueOf(feildvalue.toString());
            			OrgUnit department = this.getEntity(OrgUnit.class, departmentId);
            			hql.append(" AND m.orgDepartmentId in (select d.id from OrgUnit as d WHERE (d.path like :dPath) AND d.type=:dType AND d.deleted=false AND d.orgAccountId=:dAccountId)");
            			params.put("dPath", department.getPath() + "%");
            			params.put("dType", OrgConstants.UnitType.Department.name());
            			params.put("dAccountId", department.getOrgAccountId());
                	}else if (feildvalue instanceof String) {
                        if ("loginName".equals(condition)) {
                            hql.append(" AND (p.").append(condition).append(" LIKE :" + condition + SQLWildcardUtil.setEscapeCharacter() + ")");
                        } else {
                            hql.append(" AND (m.").append(condition).append(" LIKE :" + condition + SQLWildcardUtil.setEscapeCharacter() + ")");
                        }
                        feildvalue = "%" + SQLWildcardUtil.escape(String.valueOf(feildvalue)) + "%" ;
                        params.put(condition, String.valueOf(feildvalue));
                    } else if (feildvalue != null) {
                        hql.append(" AND m.").append(condition).append("=:" + condition);
                        params.put(condition, feildvalue);
                    }
                }
            }
        }
        hql.append(" ORDER BY m.sortId ASC");

        return DBAgent.find(hql.toString(), params, flipInfo);
    }

    public List<OrgMember> getAllMemberPOByAccountId(Long accountId,
			Integer type, Boolean isInternal, Boolean enable, String condition,
			Object feildvalue, FlipInfo flipInfo) {
    	return getAllMemberPOByAccountId(accountId,type,isInternal,enable,condition,feildvalue,flipInfo,false);
    }
	public List<OrgMember> getAllMemberPOByAccountId(Long accountId,
			Integer type, Boolean isInternal, Boolean enable, String condition,
			Object feildvalue, FlipInfo flipInfo,boolean equal) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append("SELECT m ");

		if ("loginName".equals(condition)) {
			hql.append(" FROM OrgMember as m, OrgPrincipal as p ");
			hql.append(" WHERE m.id=p.memberId AND ");
		} else {
			hql.append(" FROM OrgMember as m");
			hql.append(" WHERE ");
		}

		hql.append(" m.deleted=false AND m.admin=false AND m.virtual=false AND m.assigned=true");

		if (accountId != null) {
			hql.append(" AND m.orgAccountId=:accountId ");
			params.put("accountId", accountId);
		}
		if (type != null) {
			hql.append(" AND m.type=:type");
			params.put("type", type);
		}
		if (isInternal != null) {
			hql.append(" AND m.internal=:internal");
			params.put("internal", isInternal);
		}
		if (enable != null) {
			hql.append(" AND m.enable=:enable");
			params.put("enable", enable);
		}
		if (Strings.isNotBlank(condition) && !"null".equals(condition)) {
			if ("loginName".equals(condition)) {
				hql.append(" AND p.loginName LIKE :feildvalue" + SQLWildcardUtil.setEscapeCharacter());
				feildvalue = "%"
						+ SQLWildcardUtil.escape(String.valueOf(feildvalue))
						+ "%";
				params.put("feildvalue", feildvalue);
			} else {
				if (feildvalue instanceof String && !equal) {
					//查询人员时，oracle下支持大小写不敏感查询，遇到其他需要此类支持的再完善，先只支持这一种情况。
					//BUG_普通_V5_V6.1_一星卡_南京市中医院_IE浏览器，新建自由协调选人的界面，通过小写字母不能检索人员_20170922044627_2017-09-22
					hql.append(" and (").append(SQLWildcardUtil.likeLowerCase(condition))
							.append(" LIKE :feildvalue"+ SQLWildcardUtil.setEscapeCharacter()+")");
					feildvalue = "%"
							+ SQLWildcardUtil
									.escape(String.valueOf(feildvalue).toLowerCase()) + "%";
				} else {
					hql.append(" AND ").append(condition)
							.append("=:feildvalue");
				}
				params.put("feildvalue", feildvalue);
			}
		}

		hql.append(" ORDER BY m.sortId ASC,m.createTime ASC");
		return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	/**
	 * 获取集团下的所有有效人员，包含管理员
	 * @return
	 */
	@Override
	public List<OrgMember> getAllGroupEnableMemberPO() {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append("SELECT m ");
		hql.append(" FROM OrgMember as m");
		hql.append(" WHERE ");

		hql.append(" m.deleted=false AND m.enable=true AND m.virtual=false AND m.assigned=true");

		hql.append(" ORDER BY m.sortId ASC");
		return DBAgent.find(hql.toString());
	}
	
	@Override
	public List<OrgVisitor> getAllVisitorPO() {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("SELECT m ");
		hql.append(" FROM OrgVisitor as m");

		hql.append(" ORDER BY m.create_date ASC");
		return DBAgent.find(hql.toString(),params);
	}
	
	public Integer getAllMemberPONumsByAccountId(Long accountId,
			Integer type, Boolean isInternal, Boolean enable, String condition,
			Object feildvalue) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append("SELECT m ");

		if ("loginName".equals(condition)) {
			hql.append(" FROM OrgMember as m, OrgPrincipal as p ");
			hql.append(" WHERE m.id=p.memberId AND ");
		} else {
			hql.append(" FROM OrgMember as m");
			hql.append(" WHERE ");
		}

		hql.append(" m.deleted=false AND m.admin=false AND m.virtual=false AND m.assigned=true and (m.externalType!=1 and m.externalType!=2 and m.externalType!=3)");

		if (accountId != null) {
			hql.append(" AND m.orgAccountId=:accountId ");
			params.put("accountId", accountId);
		}
		if (type != null) {
			hql.append(" AND m.type=:type");
			params.put("type", type);
		}
		if (isInternal != null) {
			hql.append(" AND m.internal=:internal");
			params.put("internal", isInternal);
		}
		if (enable != null) {
			hql.append(" AND m.enable=:enable");
			params.put("enable", enable);
		}
		if (Strings.isNotBlank(condition) && !"null".equals(condition)) {
			if ("loginName".equals(condition)) {
				hql.append(" AND p.loginName LIKE :feildvalue" + SQLWildcardUtil.setEscapeCharacter());
				feildvalue = "%"
						+ SQLWildcardUtil.escape(String.valueOf(feildvalue))
						+ "%";
				params.put("feildvalue", feildvalue);
			} else {
				if (feildvalue instanceof String) {
					hql.append(" and (").append(condition)
							.append(" LIKE :feildvalue"+ SQLWildcardUtil.setEscapeCharacter()+")");
					feildvalue = "%"
							+ SQLWildcardUtil
									.escape(String.valueOf(feildvalue)) + "%";
				} else {
					hql.append(" AND ").append(condition)
							.append("=:feildvalue");
				}

				params.put("feildvalue", feildvalue);
			}
		}

		hql.append(" ORDER BY m.sortId ASC");
		return DBAgent.count(hql.toString(), params);
	}
	public List<OrgMember> getAllUnAssignedMember(Long accountId,FlipInfo flipInfo){
	    return this.getAllUnAssignedMember(accountId, flipInfo, null);
	}
	public List<OrgMember> getAllUnAssignedMember(Long accountId,
			FlipInfo flipInfo,Map beforeParams) {
        StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        hql.append("SELECT m ");
        hql.append(" FROM OrgMember as m,OrgUnit as u");
        hql.append(" WHERE u.id=m.orgAccountId and ");
        hql.append(" m.deleted=false AND m.virtual=false AND m.assigned=false");

        if (accountId != null) {
            hql.append(" AND m.orgAccountId=:accountId ");
            params.put("accountId", accountId);
        }
         if (beforeParams!=null && Strings.isNotEmpty((String)beforeParams.get("value"))) {
             if (beforeParams.size() > 0 && "name".equals(beforeParams.get("condition"))) {
                 hql.append(" AND m.name like :name " + SQLWildcardUtil.setEscapeCharacter());
                 params.put("name", "%" + SQLWildcardUtil.escape(String.valueOf(beforeParams.get("value"))) + "%");
             }
             if (beforeParams.size() > 0 && "orgAccountIdname".equals(beforeParams.get("condition"))) {
                 hql.append(" AND u.name like :orgAccountIdname " + SQLWildcardUtil.setEscapeCharacter());
                 params.put("orgAccountIdname", "%" + SQLWildcardUtil.escape(String.valueOf(beforeParams.get("value"))) + "%");
                 }
         }
        hql.append(" ORDER BY m.sortId ASC");

        return DBAgent.find(hql.toString(), params, flipInfo);
    }

	public List<OrgMember> getAllMemberPOByDepartmentId(Long departmentId,
			boolean isCludChildDepart, Integer type, Boolean isInternal,
			Boolean enable, String condition, Object feildvalue,
			FlipInfo flipInfo) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append("SELECT m ");

		if ("loginName".equals(condition)) {
			hql.append(" FROM OrgMember as m, OrgPrincipal as p ");
			hql.append(" WHERE m.id=p.memberId AND ");
		} else {
			hql.append(" FROM OrgMember as m");
			hql.append(" WHERE ");
		}

		hql.append(" m.deleted=false AND m.virtual=false AND m.assigned=true");

		// 包含子部门
		if (isCludChildDepart) {
			OrgUnit department = this.getEntity(OrgUnit.class, departmentId);
			hql.append(" AND m.orgDepartmentId in (select d.id from OrgUnit as d WHERE (d.path like :dPath) AND d.type=:dType AND d.deleted=false AND d.orgAccountId=:dAccountId)");
			params.put("dPath", department.getPath() + "%");
			params.put("dType", OrgConstants.UnitType.Department.name());
			params.put("dAccountId", department.getOrgAccountId());
		} else {
			hql.append(" AND m.orgDepartmentId=:departmentId");
			params.put("departmentId", departmentId);
		}

		if (type != null) {
			hql.append(" AND m.type=:type");
			params.put("type", type);
		}
		if (isInternal != null) {
			hql.append(" AND m.internal=:internal");
			params.put("internal", isInternal);
		}
		if (enable != null) {
			hql.append(" AND m.enable=:enable");
			params.put("enable", enable);
		}
		if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
			if ("loginName".equals(condition)) {
				hql.append(" AND p.loginName LIKE :feildvalue" + SQLWildcardUtil.setEscapeCharacter());
				feildvalue = "%"
						+ SQLWildcardUtil.escape(String.valueOf(feildvalue))
						+ "%";
				params.put("feildvalue", feildvalue);
			} else {
				if (feildvalue instanceof String) {
					hql.append(" and (").append(condition)
							.append(" LIKE :feildvalue" + SQLWildcardUtil.setEscapeCharacter() +")");
					feildvalue = "%"
							+ SQLWildcardUtil
									.escape(String.valueOf(feildvalue)) + "%";
				} else {
					hql.append(" AND ").append(condition)
							.append("=:feildvalue");
				}

				params.put("feildvalue", feildvalue);
			}
		}

		hql.append(" ORDER BY m.sortId ASC,m.name ASC");

		return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
    @Override
    public List<OrgMember> getAllMemberPOByDepartmentId(List<Long> departmentIds,
            Boolean isInternal, Boolean enable, Map<String, Object> param, FlipInfo flipInfo) {
        StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        hql.append("SELECT m ");

        if (null != param && param.containsKey("loginName")) {
            hql.append(" FROM OrgMember as m, OrgPrincipal as p ");
            hql.append(" WHERE m.id=p.memberId AND ");
        } else {
            hql.append(" FROM OrgMember as m ");
            hql.append(" WHERE ");
        }

        //不考虑子部门，如果是子部门，将子部门也拼好送进来
        List<List<Long>> list2 = listToArray(departmentIds,999);
        if(list2.isEmpty() || list2.size()<2){
        	hql.append(" m.orgDepartmentId in (:departmentIds)");
            params.put("departmentIds", departmentIds);
        }else{
        	hql.append("(");
        	for(int i=0;i<list2.size();i++){
        		if(i!=list2.size()-1){
        			hql.append(" m.orgDepartmentId in (:departmentIds"+i+") or ");
        			params.put("departmentIds"+i, list2.get(i));
        		}else{
        			hql.append(" m.orgDepartmentId in (:departmentIds"+i+") ");
        			params.put("departmentIds"+i, list2.get(i));
        		}
        	}
        	hql.append(")");
        }
        if (isInternal != null) {
            hql.append(" AND m.internal=:internal");
            params.put("internal", isInternal);
        }
        if (enable != null) {
            hql.append(" AND m.enable=:enable");
            params.put("enable", enable);
        }

        hql.append(" AND m.deleted=false AND m.admin=false AND m.virtual=false AND m.assigned=true ");
        
        if(null != param) {
            Set<Entry<String, Object>> paramSet = param.entrySet();
            for (Entry<String, Object> entry : paramSet) {
                String condition = entry.getKey();
                Object feildvalue = entry.getValue();
                if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
                	if("workLocal".equals(condition)){
                		hql.append(" AND m.extAttr36").append(" like :" + condition);
                		params.put(condition, feildvalue);
                	}else if (feildvalue instanceof String) {
                        if ("loginName".equals(condition)) {
                            hql.append(" AND (p.").append(condition).append(" LIKE :" + condition  + SQLWildcardUtil.setEscapeCharacter() + ")");
                        } else {
                            hql.append(" AND (m.").append(condition).append(" LIKE :" + condition  + SQLWildcardUtil.setEscapeCharacter() + ")");
                        }
                        feildvalue = "%" + SQLWildcardUtil.escape(String.valueOf(feildvalue)) + "%";
                        params.put(condition, String.valueOf(feildvalue));
                    } else if (feildvalue != null) {
                        hql.append(" AND m.").append(condition).append("=:" + condition);
                        params.put(condition, feildvalue);
                    }
                }
            }
        }
        hql.append(" ORDER BY m.sortId ASC");
        return DBAgent.find(hql.toString(), params, flipInfo);
    }

	public List<OrgMember> getAllMemberPOByDepartmentIds(
			List<Long> departmentIds, Integer type, Boolean isInternal,
			Boolean enable, String condition, Object feildvalue,
			FlipInfo flipInfo) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append("SELECT m ");

		if ("loginName".equals(condition)) {
			hql.append(" FROM OrgMember as m, OrgPrincipal as p ");
			hql.append(" WHERE m.id=p.memberId AND ");
		} else {
			hql.append(" FROM OrgMember as m");
			hql.append(" WHERE ");
		}

		hql.append(" m.deleted=false AND m.virtual=false AND m.assigned=true");
		// 不考虑子部门，如果考虑子部门把子部门的id也传进来
		hql.append(" AND m.orgDepartmentId in (:departmentIds)");
		params.put("departmentIds", departmentIds);

		if (type != null) {
			hql.append(" AND m.type=:type");
			params.put("type", type);
		}
		if (isInternal != null) {
			hql.append(" AND m.internal=:internal");
			params.put("internal", isInternal);
		}
		if (enable != null) {
			hql.append(" AND m.enable=:enable");
			params.put("enable", enable);
		}
		if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
			if ("loginName".equals(condition)) {
				hql.append(" AND p.loginName LIKE :feildvalue" + SQLWildcardUtil.setEscapeCharacter());
				feildvalue = "%"
						+ SQLWildcardUtil.escape(String.valueOf(feildvalue))
						+ "%";
				params.put("feildvalue", feildvalue);
			} else {
				if (feildvalue instanceof String) {
					hql.append(" and (").append(condition)
							.append(" LIKE :feildvalue" + SQLWildcardUtil.setEscapeCharacter()+")");
					feildvalue = "%"
							+ SQLWildcardUtil
									.escape(String.valueOf(feildvalue)) + "%";
				} else {
					hql.append(" AND ").append(condition)
							.append("=:feildvalue");
				}

				params.put("feildvalue", feildvalue);
			}
		}

		hql.append(" ORDER BY m.sortId ASC");

		return DBAgent.find(hql.toString(), params, flipInfo);
	}

	public List<OrgRole> getAllRolePO(Long accountId, Boolean enable,
			String condition, Object feildvalue, FlipInfo flipInfo) {
		return getAllRolePO(accountId,enable,condition,feildvalue,flipInfo,false);
	}
	public List<OrgRole> getAllRolePO(Long accountId, Boolean enable,
			String condition, Object feildvalue, FlipInfo flipInfo,boolean equal) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" FROM " + OrgRole.class.getSimpleName());
		hql.append(" WHERE deleted=false");

		if (accountId != null) {
			hql.append(" AND org_Account_Id=:accountId");
			params.put("accountId", accountId);
		}
		if (enable != null) {
			hql.append(" AND enable=:enable");
			params.put("enable", enable);
		}
		if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
			if (feildvalue instanceof String && !equal) {
				hql.append(" AND (").append(condition)
						.append(" LIKE :feildvalue"+ SQLWildcardUtil.setEscapeCharacter()+")");
				feildvalue = "%"
						+ SQLWildcardUtil.escape(String.valueOf(feildvalue))
						+ "%";
			} else {
				hql.append(" AND ").append(condition).append("=:feildvalue");
			}
			params.put("feildvalue", feildvalue);
		}
		
		hql.append(" ORDER BY sortId ASC");

		return DBAgent.find(hql.toString(), params, flipInfo);
	}

	public List<OrgRole> getAllRolePO(Long accountId, Boolean enable,
			Map<String, Object> param, FlipInfo flipInfo) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" FROM " + OrgRole.class.getSimpleName());
		hql.append(" WHERE deleted=false");

		if (accountId != null) {
			hql.append(" AND org_Account_Id=:accountId");
			params.put("accountId", accountId);
		}
		if (enable != null) {
			hql.append(" AND enable=:enable");
			params.put("enable", enable);
		}
		if (param != null) {
			Set<Entry<String, Object>> paramSet = param.entrySet();
			for (Entry<String, Object> entry : paramSet) {
				String condition = entry.getKey();
				Object feildvalue = entry.getValue();
                if (StringUtils.isNotBlank(condition) && !"accountId".equals(condition) && !"null".equals(condition)) {
					// 因为角色名做了国际化处理，所以查询的时候需要转换一次
					if (feildvalue instanceof List && "name".equals(condition)) {
						List names = (List) feildvalue;
						if (names.size() > 0) {
							hql.append(" AND ( 1=0");
							Set nameSet = new HashSet();
							for (int i =0; i<names.size(); i++) {
							    Object name = names.get(i);
							    if(!nameSet.contains(names.get(i))){
							        nameSet.add(name);
							        hql.append(" or name=:name"+i);
							        params.put("name"+i, String.valueOf(name));
							    }
							}
							hql.append(" )");
						}
					} else if (feildvalue instanceof String
							&& !"bond".equals(condition)
							&& !"type".equals(condition)
							&& !"enabled".equals(condition)
							&& !"code".equals(condition)
							&& !"codeEqule".equals(condition)) {
						hql.append(" AND (").append(condition)
								.append(" LIKE :" + condition  + SQLWildcardUtil.setEscapeCharacter() + ")");
						feildvalue = "%"
								+ SQLWildcardUtil.escape(String
										.valueOf(feildvalue)) + "%";
						params.put(condition, String.valueOf(feildvalue));
					} else if (feildvalue != null) {
						if ("enabled".equals(condition)) {
							hql.append(" AND enable=:enable");
							params.put("enable", Boolean.parseBoolean(String
									.valueOf(feildvalue)));
						} else if ("sortId".equals(condition)) {
							hql.append(" AND ").append(condition)
									.append("=:" + condition);
							params.put(condition,
									Long.parseLong(String.valueOf(feildvalue)));
						} else if ("code".equals(condition)) {
							hql.append(" AND code LIKE :code" + SQLWildcardUtil.setEscapeCharacter());
							feildvalue = "%" + SQLWildcardUtil.escape(String.valueOf(feildvalue)) + "%";
							
							params.put("code", String.valueOf(feildvalue));
						} else if ("codeEqule".equals(condition)) {
                            hql.append(" AND code = :codeEqule");
                            feildvalue = String.valueOf(feildvalue);
                            params.put("codeEqule", String.valueOf(feildvalue));
                        } else {
							hql.append(" AND ").append(condition)
									.append("=:" + condition);
							params.put(condition, Integer.parseInt(String
									.valueOf(feildvalue)));
						}
					}
				}
			}
		}
		
		hql.append(" ORDER BY sortId ASC");

		return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	public List<OrgTeam> getAllTeamPO(Long accountId, Integer type,
			Boolean enable, String condition, Object feildvalue,
			FlipInfo flipInfo) {
		return getAllTeamPO(accountId,type,enable,condition,feildvalue,flipInfo,false);
	}

	public List<OrgTeam> getAllTeamPO(Long accountId, Integer type,
			Boolean enable, String condition, Object feildvalue,
			FlipInfo flipInfo,boolean equal) {
		feildvalue = OrgHelper.StrtoBoolean(feildvalue);
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" FROM " + OrgTeam.class.getSimpleName());
		hql.append(" WHERE deleted = false ");
		if (accountId != null) {
			hql.append(" AND org_Account_Id=:accountId");
			params.put("accountId", accountId);
		}

		if (type != null) {
			hql.append(" AND type=:type");
			params.put("type", type);
		}
		if (enable != null) {
			hql.append(" AND enable=:enable");
			params.put("enable", enable);
		}
		if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
			if (feildvalue instanceof String && !equal) {
				hql.append(" AND (").append(condition)
						.append(" LIKE :feildvalue"+ SQLWildcardUtil.setEscapeCharacter()+")");
				feildvalue = "%"
						+ SQLWildcardUtil.escape(String.valueOf(feildvalue))
						+ "%";
			} else {
				hql.append(" AND ").append(condition).append("=:feildvalue");
			}
			params.put("feildvalue", feildvalue);
		}
		hql.append(" ORDER BY sortId ASC");

		return DBAgent.find(hql.toString(), params, flipInfo);
	}

	public List<OrgPost> getAllPostPO(Long accountId, Boolean enable,
			String condition, Object feildvalue, FlipInfo flipInfo) {
		return getAllPostPO(accountId,enable,condition,feildvalue,flipInfo,false);
	}
	
	public List<OrgPost> getAllPostPO(Long accountId, Boolean enable,
			String condition, Object feildvalue, FlipInfo flipInfo,boolean equal) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" FROM " + OrgPost.class.getSimpleName());
		hql.append(" WHERE deleted=false");
		if (accountId != null) {
			hql.append(" AND orgAccountId=:accountId");
			params.put("accountId", accountId);
		}
		if (enable != null) {
			hql.append(" AND enable=:enable");
			params.put("enable", enable);
		}
		if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
			if (feildvalue instanceof String && !equal) {
				hql.append(" AND (").append(condition)
						.append(" LIKE :feildvalue"+ SQLWildcardUtil.setEscapeCharacter()+")");
				feildvalue = "%"
						+ SQLWildcardUtil.escape(String.valueOf(feildvalue))
						+ "%";
			} else {
				hql.append(" AND ").append(condition).append("=:feildvalue");
			}
			params.put("feildvalue", feildvalue);
		}
		hql.append(" ORDER BY sortId ASC");

		return DBAgent.find(hql.toString(), params, flipInfo);
	}

	public boolean isGroupLevelUsed(Long levelId) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" FROM " + OrgLevel.class.getSimpleName());
		hql.append(" WHERE deleted=false");
		hql.append(" AND enable=:enable");
		params.put("enable", true);
		hql.append(" AND groupLevelId=:groupLevelId");
		params.put("groupLevelId", levelId);
		List list = DBAgent.find(hql.toString(), params, null);
		if (list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public List<OrgMember> getAllUnenabledMembers(Long accountId, FlipInfo flipInfo) {
	    StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        hql.append(" FROM " + OrgMember.class.getSimpleName());
        hql.append(" WHERE (deleted = true OR enable = false OR assigned = false)");
        if (accountId != null) {
            hql.append(" AND orgAccountId=:accountId");
            params.put("accountId", accountId);
        }
        hql.append(" ORDER BY sortId ASC");
        return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	@Override
	public List<OrgPost> getAllUnenabledPosts(Long accountId, FlipInfo flipInfo) {
	    StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        hql.append(" FROM " + OrgPost.class.getSimpleName());
        hql.append(" WHERE (deleted=true OR enable=false)");
        if (accountId != null) {
            hql.append(" AND orgAccountId=:accountId");
            params.put("accountId", accountId);
        }
        hql.append(" ORDER BY sortId ASC");
        return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	@Override
	public List<OrgLevel> getAllUnenabledLevels(Long accountId, FlipInfo flipInfo) {
        StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        hql.append(" FROM " + OrgLevel.class.getSimpleName());
        hql.append(" WHERE (deleted = true  OR enable = false)");
        if (accountId != null) {
            hql.append(" AND orgAccountId=:accountId");
            params.put("accountId", accountId);
        }
        hql.append(" ORDER BY levelId ASC ");
        return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	@Override
	public List<OrgUnit> getAllUnenabledDepartments(Long accountId, FlipInfo flipInfo) {
	    StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        hql.append(" FROM " + OrgUnit.class.getSimpleName());
        hql.append(" WHERE (deleted = true OR enable = false) ");
        if (accountId != null) {
            hql.append(" AND orgAccountId=:accountId");
            params.put("accountId", accountId);
        }
        hql.append(" AND type = 'Department' ");
        hql.append(" ORDER BY sortId ASC");
        return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	@Override
	public List<OrgUnit> getAllUnenabledAccounts(Long accountId, FlipInfo flipInfo) {
	    StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        hql.append(" FROM " + OrgUnit.class.getSimpleName());
        hql.append(" WHERE (deleted = true OR enable = false)");
        if (accountId != null) {
            hql.append(" AND orgAccountId=:accountId");
            params.put("accountId", accountId);
        }
        hql.append(" AND type = 'Account' ");
        hql.append(" ORDER BY sortId ASC");
        return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	@Override
	public List<OrgTeam> getAllUnenabledTeams(Long accountId, FlipInfo flipInfo) {
        StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        hql.append(" FROM " + OrgTeam.class.getSimpleName());
        hql.append(" WHERE (deleted = true OR enable = false) ");
        if (accountId != null) {
            hql.append(" AND org_Account_Id=:accountId");
            params.put("accountId", accountId);
        }
        hql.append(" ORDER BY sortId ASC");

        return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	public List<OrgLevel> getAllLevelPO(Long accountId, Boolean enable,
			String condition, Object feildvalue, FlipInfo flipInfo) {
		return getAllLevelPO(accountId,enable,condition,feildvalue,flipInfo,false);
	}
	public List<OrgLevel> getAllLevelPO(Long accountId, Boolean enable,
			String condition, Object feildvalue, FlipInfo flipInfo,boolean equal) {
		feildvalue = OrgHelper.StrtoBoolean(feildvalue);
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" FROM " + OrgLevel.class.getSimpleName());
		hql.append(" WHERE deleted=false");
		if (accountId != null) {
			hql.append(" AND orgAccountId=:accountId");
			params.put("accountId", accountId);
		}
		if (enable != null) {
			hql.append(" AND enable=:enable");
			params.put("enable", enable);
		}
		if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
			if (feildvalue instanceof String && !equal) {
				feildvalue = "%"
						+ SQLWildcardUtil.escape(String.valueOf(feildvalue))
						+ "%";
				hql.append(" AND (").append(condition)
						.append(" LIKE :feildvalue" + SQLWildcardUtil.setEscapeCharacter() +")");
			} else {
				hql.append(" AND ").append(condition).append("=:feildvalue");
			}
			params.put("feildvalue", feildvalue);
		}
		hql.append(" ORDER BY levelId ASC ");

		return DBAgent.find(hql.toString(), params, flipInfo);
	}

	public List<OrgRelationship> getOrgRelationshipPO(
			OrgConstants.RelationshipType type,
			Long sourceId,
			Long accountId,
			EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds,
			FlipInfo flipInfo) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" FROM " + OrgRelationship.class.getSimpleName()
				+ " WHERE 1=1 ");
		if (type != null) {
			hql.append(" AND type=:type");
			params.put("type", type.name());
		}
		if (sourceId != null) {
			hql.append(" AND sourceId=:sourceId");
			params.put("sourceId", sourceId);
		}
		if (accountId != null) {
			hql.append(" AND orgAccountId=:accountId");
			params.put("accountId", accountId);
		}
		if (objectiveIds != null && !objectiveIds.isEmpty()) {
			Set<Map.Entry<OrgConstants.RelationshipObjectiveName, Object>> entries = objectiveIds
					.entrySet();
			for (Map.Entry<OrgConstants.RelationshipObjectiveName, Object> entry : entries) {
				String o = entry.getKey().name();
				if(OrgConstants.RelationshipObjectiveName.objective5Id.name().equals(o)
                        || OrgConstants.RelationshipObjectiveName.objective6Id.name().equals(o)
                        || OrgConstants.RelationshipObjectiveName.objective7Id.name().equals(o)) {
                    hql.append(" AND " + o + " LIKE:" + o + SQLWildcardUtil.setEscapeCharacter());
                    params.put(o, "%" + SQLWildcardUtil.escape(String.valueOf(entry.getValue())) + "%");
                } else {
                    hql.append(" AND " + o + "=:" + o);
                    params.put(o, entry.getValue());
                }
			}
		}

		return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	@Override
    public List<OrgRelationship> getOrgRelationshipPO4ConPost(String memberName, Long postId, Long accountId, Long conAccountId,
            EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds, boolean isSubUnitManage,List<Long> subUnitIds,FlipInfo flipInfo) {
        StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        hql.append(" FROM " + OrgRelationship.class.getSimpleName() + " WHERE 1=1 ");
        hql.append(" AND type=:type");
        params.put("type", OrgConstants.RelationshipType.Member_Post.name());
        
        
        if (memberName != null) {
            hql.append(" AND sourceId in (SELECT id FROM " + OrgMember.class.getSimpleName() + " WHERE name "
                    + "like '"+"%" + SQLWildcardUtil.escape(memberName) + "%"+"'"+ SQLWildcardUtil.setEscapeCharacter()+") ");
        }
        if (postId != null) {
            hql.append(" AND sourceId in (SELECT id FROM "+OrgMember.class.getSimpleName()+" WHERE orgPostId =:postId ) ");
            params.put("postId", postId);
        }
        
        if (accountId != null) {
            hql.append(" AND sourceId in (SELECT id FROM "+OrgMember.class.getSimpleName()+" WHERE orgAccountId=:accountId ) ");
            params.put("accountId", accountId);
        }
        
        if (conAccountId != null) {
            hql.append(" AND orgAccountId=:conAccountId");
            params.put("conAccountId", conAccountId);
        }
        
        if(isSubUnitManage && subUnitIds.size() > 0 ){
        	hql.append(" AND (orgAccountId in(:subUnitId) or sourceId in (SELECT id FROM "+OrgMember.class.getSimpleName()+" WHERE orgAccountId in(:subUnitId)))");
            params.put("subUnitId", subUnitIds);
        }

        if (objectiveIds != null && !objectiveIds.isEmpty()) {
            Set<Map.Entry<OrgConstants.RelationshipObjectiveName, Object>> entries = objectiveIds.entrySet();
            for (Map.Entry<OrgConstants.RelationshipObjectiveName, Object> entry : entries) {
                String o = entry.getKey().name();
                if(OrgConstants.RelationshipObjectiveName.objective5Id.name().equals(o)
                        || OrgConstants.RelationshipObjectiveName.objective6Id.name().equals(o)
                        || OrgConstants.RelationshipObjectiveName.objective7Id.name().equals(o)) {
                    hql.append(" AND " + o + " LIKE:" + o + SQLWildcardUtil.setEscapeCharacter());
                    params.put(o, "%" + SQLWildcardUtil.escape(String.valueOf(entry.getValue())) + "%");
                } else {
                    hql.append(" AND " + o + "=:" + o);
                    params.put(o, entry.getValue());
                }
            }
        }
        hql.append(" ORDER BY createTime DESC , sortId ASC");
        return DBAgent.find(hql.toString(), params, flipInfo);
    }

	public <T extends BasePO> T getEntity(Class<T> entityClass, Long id) {
		return DBAgent.get(entityClass, id);
	}

	public OrgUnit getOrgUnitPO(Long id) {
		return DBAgent.get(OrgUnit.class, id);
	}

	public OrgUnit getOrgUnitPOByPath(String path) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("path", path);
		List lst = DBAgent.find(
				"SELECT m FROM OrgUnit as m where m.path=:path", params);

		if (!lst.isEmpty() && lst.get(0) != null) {
			return (OrgUnit) lst.get(0);
		} else {
			return null;
		}
	}

	public OrgMember getOrgMemberPO(Long id) {
		return DBAgent.get(OrgMember.class, id);
	}

	public OrgRole getOrgRolePO(Long id) {
		return DBAgent.get(OrgRole.class, id);
	}

	public OrgTeam getOrgTeamPO(Long id) {
		return DBAgent.get(OrgTeam.class, id);
	}

	public OrgPost getOrgPostPO(Long id) {
		return DBAgent.get(OrgPost.class, id);
	}

	public OrgLevel getOrgLevelPO(Long id) {
		return DBAgent.get(OrgLevel.class, id);
	}

	public void insertOrgUnit(List<OrgUnit> orgUnit) {
		DBAgent.saveAll(orgUnit);

		this.orgCache.cacheUpdate(OrgHelper.listPoTolistBo(orgUnit));
	}

	public void insertOrgMember(List<OrgMember> orgMember) {
		DBAgent.saveAll(orgMember);

		this.orgCache.cacheUpdate(OrgHelper.listPoTolistBo(orgMember));
	}
	
	public void insertOrgVisitor(List<OrgVisitor> orgVisitor){
		DBAgent.saveAll(orgVisitor);

		this.orgCache.cacheUpdate(OrgHelper.listPoTolistBo(orgVisitor));
	}
	
	@Override
	public void update(OrgVisitor orgVisitor) {
		orgVisitor.setUpdate_date(new Date());
		DBAgent.update(orgVisitor);
		DBAgent.mergeAll(addPoToList(orgVisitor));
		
		V3xOrgVisitor _temp = (V3xOrgVisitor) OrgHelper.poTobo(orgVisitor);
		this.orgCache.cacheUpdate(_temp);
	}

	public void insertOrgRole(List<OrgRole> orgRole) {
		DBAgent.saveAll(orgRole);

		this.orgCache.cacheUpdate(OrgHelper.listPoTolistBo(orgRole));
	}

	public void insertOrgTeam(List<OrgTeam> orgTeam) {
		DBAgent.saveAll(orgTeam);

		this.orgCache.cacheUpdate(OrgHelper.listPoTolistBo(orgTeam));
	}

	public void insertOrgPost(List<OrgPost> orgPost) {
		DBAgent.saveAll(orgPost);

		this.orgCache.cacheUpdate(OrgHelper.listPoTolistBo(orgPost));
	}

	public void insertOrgLevel(List<OrgLevel> orgLevel) {
		DBAgent.saveAll(orgLevel);
		this.orgCache.cacheUpdate(OrgHelper.listPoTolistBo(orgLevel));
	}

	public void insertOrgRelationship(List<OrgRelationship> orgRelationship) {
		DBAgent.saveAll(orgRelationship);

		this.orgCache.cacheUpdateRelationship(orgRelationship);
	}

	public void update(OrgUnit orgUnit) {
		orgUnit.setUpdateTime(new Date());
		//DBAgent.update(orgUnit);
		DBAgent.mergeAll(addPoToList(orgUnit));

		this.orgCache.cacheUpdate(OrgHelper.poTobo(orgUnit));
	}

	public void update(OrgMember orgMember) {
		orgMember.setUpdateTime(new Date());
		//DBAgent.update(orgMember);
		DBAgent.mergeAll(addPoToList(orgMember));

		V3xOrgMember _temp = (V3xOrgMember) OrgHelper.poTobo(orgMember);
		this.orgCache.cacheUpdate(_temp);
	}
	
    public void update(List<OrgMember> orgMembers) {
        for(OrgMember orgMember:orgMembers){
        	orgMember.setUpdateTime(new Date());
        }

        DBAgent.mergeAll(orgMembers);
        for(OrgMember orgMember:orgMembers){
            this.orgCache.cacheUpdate(OrgHelper.poTobo(orgMember));
        }
    }

	public void update(OrgRole orgRole) {
		orgRole.setUpdateTime(new Date());
		//DBAgent.update(orgRole);
		DBAgent.mergeAll(addPoToList(orgRole));
		this.orgCache.cacheUpdate(OrgHelper.poTobo(orgRole));
	}
    public void updates(List<OrgRole> orgRoles) {
        for(OrgRole orgRole:orgRoles){
            orgRole.setUpdateTime(new Date());
        }

        DBAgent.mergeAll(orgRoles);
        for(OrgRole orgRole:orgRoles) {
            this.orgCache.cacheUpdate(OrgHelper.poTobo(orgRole));
        }
    }


	public void update(OrgTeam orgTeam) {
		orgTeam.setUpdateTime(new Date());
		//DBAgent.update(orgTeam);
		DBAgent.mergeAll(addPoToList(orgTeam));

		this.orgCache.cacheUpdate(OrgHelper.poTobo(orgTeam));
	}

	public void update(OrgPost orgPost) {
		orgPost.setUpdateTime(new Date());
		//DBAgent.update(orgPost);
		DBAgent.mergeAll(addPoToList(orgPost));

		this.orgCache.cacheUpdate(OrgHelper.poTobo(orgPost));
	}

	public void update(OrgLevel orgLevel) {
		orgLevel.setUpdateTime(new Date());
		//DBAgent.update(orgLevel);
		DBAgent.mergeAll(addPoToList(orgLevel));

		this.orgCache.cacheUpdate(OrgHelper.poTobo(orgLevel));
	}

	public void deleteOrgRelationshipPO(String type, Long sourceId,
			Long accountId,
			EnumMap<OrgConstants.RelationshipObjectiveName, Object> objectiveIds) {
		if (Strings.isBlank(type)) {
			StringBuilder hql = new StringBuilder();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append(" FROM " + OrgRelationship.class.getSimpleName()
					+ " WHERE 1=1 ");
/*			if (type != null) {
				hql.append(" AND type=:type");
				params.put("type", type);
			}*/
			if (sourceId != null) {
				hql.append(" AND sourceId=:sourceId");
				params.put("sourceId", sourceId);
			}
			if (accountId != null) {
				hql.append(" AND orgAccountId=:accountId");
				params.put("accountId", accountId);
			}
			if (objectiveIds != null && !objectiveIds.isEmpty()) {
				Set<Map.Entry<OrgConstants.RelationshipObjectiveName, Object>> entries = objectiveIds
						.entrySet();
				for (Map.Entry<OrgConstants.RelationshipObjectiveName, Object> entry : entries) {
					String o = entry.getKey().name();
					if (entry.getValue() != null) {
						//如果是数组，则用in，否则用=
	                	if(entry.getValue() instanceof ArrayList){
	                		List<Long> list  = (ArrayList<Long>)entry.getValue();
	                		int maxSize = list.size();
	                		if(Strings.isNotEmpty(list)){
	                			int size = 900;
	                			int count = 0;
	                			int j=0;
	                			hql.append(" AND (" );
	                			while(count<maxSize){
	                				List<Long> ids = new ArrayList<Long>();
	                				int i;
	                				int len = (count+size<maxSize)?count+size:maxSize;
	                				for(i =count;i<len;i++){
	                					ids.add(list.get(i));
	                				}
	                				count = i;
	                				String o0 = o+j;
	                				if(j==0){
	                					hql.append(o + " in(:"+o0+")" );
	                				}else{
	                					hql.append(" or " + o + " in(:"+o0+")" );
	                				}
	                				params.put(o0, ids);
	                				j++;
	                			}
	                			hql.append(")" );
	                		}
	                	}else{
	                		hql.append(" AND " + o + "=:" + o);
	                		params.put(o, entry.getValue());
	                	}
					}
				}
			}
	        List<OrgRelationship> orgRelationships = DBAgent.find("" + hql.toString(), params);
	        DBAgent.bulkUpdate("DELETE" + hql.toString(), params);
	        this.orgCache.cacheRemoveRelationship(orgRelationships);
		}else{
			List<V3xOrgRelationship> rels = orgCache.getV3xOrgRelationship(OrgConstants.RelationshipType.valueOf(type),sourceId,accountId,objectiveIds);
			List<OrgRelationship> deleteRels = new UniqueList<OrgRelationship>();
			for(V3xOrgRelationship rel : rels){
				deleteRels.add((OrgRelationship)rel.toPO());
			}
			this.deleteRelationships(deleteRels);
		}
        
	}
	
    public void deleteOrgRelationshipPOs(List<OrgRelationship> rels) {
        DBAgent.deleteAll(rels);
        this.orgCache.cacheRemoveRelationship(rels);
    }
    @Override
    public <T extends V3xOrgEntity> void insertRepeatSortNum(Class<T> entityClass, Long accountId, Long sortNum, Boolean isInternal) {
    	insertRepeatSortNum(entityClass,accountId,sortNum,isInternal,OrgConstants.ExternalType.Inner.ordinal());
    }
    
    @Override
    public <T extends V3xOrgEntity> void insertRepeatSortNum(Class<T> entityClass, Long accountId, Long sortNum, Boolean isInternal,int externalType,Long selfEntityId) {
    	insertRepeatSortNum(entityClass,accountId,sortNum,isInternal,externalType,selfEntityId,null);
    }
    @Override
    public <T extends V3xOrgEntity> void insertRepeatSortNum(Class<T> entityClass, Long accountId, Long sortNum, Boolean isInternal,int externalType,Long selfEntityId,Long createrId) {
    	List<Long> entityIds = new UniqueList<Long>();
        if (entityClass == V3xOrgAccount.class) {
        	if(OrgConstants.ExternalType.Interconnect4.ordinal() == externalType && createrId != null){
        		DBAgent.bulkUpdate("UPDATE OrgUnit SET sortId=sortId + 1 WHERE type=? AND sortId>=? AND group = false and external_type=? and createrId=?",
        				OrgConstants.UnitType.Account.name(), sortNum,externalType,createrId);
        		List<V3xOrgAccount> accounts = this.orgCache.getAllV3xOrgEntityNoClone(V3xOrgAccount.class, null,externalType);
        		for (V3xOrgAccount bo : accounts) {
        			if(bo.isGroup()) continue;
        			if (bo.getSortId() >= sortNum && createrId.equals(bo.getCreaterId())) {
        				bo.setSortId(bo.getSortId() + 1);
        				entityIds.add(bo.getId());
        			}
        		}
        	}else{
        		DBAgent.bulkUpdate("UPDATE OrgUnit SET sortId=sortId + 1 WHERE type=? AND sortId>=? AND group = false and external_type=?",
        				OrgConstants.UnitType.Account.name(), sortNum,externalType);
        		List<V3xOrgAccount> accounts = this.orgCache.getAllV3xOrgEntityNoClone(V3xOrgAccount.class, accountId,externalType);
        		for (V3xOrgAccount bo : accounts) {
        			if(bo.isGroup()) continue;
        			if (bo.getSortId() >= sortNum) {
        				bo.setSortId(bo.getSortId() + 1);
        				entityIds.add(bo.getId());
        			}
        		}
        	}
        	
        	if(Strings.isNotEmpty(entityIds)){
        		orgCache.cacheUpdateV3xOrgEntityOnlySortId(entityIds,OrgConstants.ORGENT_TYPE.Unit);
        	}
        } else if (entityClass == V3xOrgDepartment.class) {
            DBAgent.bulkUpdate("UPDATE OrgUnit SET sortId=sortId + 1 WHERE type=? AND orgAccountId=? AND sortId>=? and external_type=?",
                    OrgConstants.UnitType.Department.name(), accountId, sortNum,externalType);
            List<V3xOrgDepartment> depts = this.orgCache.getAllV3xOrgEntityNoClone(V3xOrgDepartment.class, accountId,externalType);
            for (V3xOrgDepartment bo : depts) {
                if (bo.getSortId() >= sortNum) {
                    bo.setSortId(bo.getSortId() + 1);
                    entityIds.add(bo.getId());
                }
            }
        	if(Strings.isNotEmpty(entityIds)){
        		orgCache.cacheUpdateV3xOrgEntityOnlySortId(entityIds,OrgConstants.ORGENT_TYPE.Unit);
        	}
        } else if (entityClass == V3xOrgMember.class) {
            if(null == isInternal) {
                isInternal = true;//如果给null默认只自增内部人员
            }
            String hql = "UPDATE OrgMember SET sortId=sortId + 1 WHERE orgAccountId=? AND sortId>=? AND internal=? and external_type=?";
            if(selfEntityId != null){
            	hql = hql + " and id!=? ";
            	DBAgent.bulkUpdate(hql, accountId,
            			sortNum, isInternal,externalType,selfEntityId);
            }else{
            	DBAgent.bulkUpdate(hql, accountId,
            			sortNum, isInternal,externalType);
            }
            List<V3xOrgMember> members = this.orgCache.getAllV3xOrgEntityNoClone(V3xOrgMember.class, accountId,externalType);
            for (V3xOrgMember bo : members) {
            	if(selfEntityId == null || (selfEntityId !=null && selfEntityId!=bo.getId())){
            		if (bo.getSortId() >= sortNum && bo.getIsInternal().equals(isInternal)) {
            			bo.setSortId(bo.getSortId() + 1);
            			entityIds.add(bo.getId());
            		}
            	}
            }
            
        	if(Strings.isNotEmpty(entityIds)){
        		orgCache.cacheUpdateV3xOrgEntityOnlySortId(entityIds,OrgConstants.ORGENT_TYPE.Member);
        	}
            
            if(isInternal || externalType==1) {
            	String hql2="UPDATE OrgRelationship SET sortId=sortId + 1 WHERE type=? AND objective5Id=? AND orgAccountId=? AND sortId>=?";
                if(selfEntityId != null){
                	hql2 = hql2 + " and sourceId!=? ";
                	DBAgent.bulkUpdate(hql2,
                			OrgConstants.RelationshipType.Member_Post.name(), OrgConstants.MemberPostType.Main.name(),
                			accountId, sortNum,selfEntityId);
                }else{
                	DBAgent.bulkUpdate(hql2,
                			OrgConstants.RelationshipType.Member_Post.name(), OrgConstants.MemberPostType.Main.name(),
                			accountId, sortNum);
                }
    
                List<V3xOrgRelationship> rels = this.orgCache.getV3xOrgRelationship(RelationshipType.Member_Post, null, accountId, null);
                List<Long> rs = new ArrayList<Long>(100);
                for (V3xOrgRelationship relBo : rels) {
                	if(selfEntityId == null || (selfEntityId !=null && selfEntityId != relBo.getSourceId())){
                		if (relBo.getSortId() >= sortNum) {
                			relBo.setSortId(relBo.getSortId() + 1);
                			rs.add(relBo.getId());
                		}
                	}
                }
                
                if(!rs.isEmpty()){
                    this.orgCache.cacheUpdateRelationshipOnlySortId(rs);
                }
            }

        } else if (entityClass == V3xOrgPost.class) {
            DBAgent.bulkUpdate("UPDATE OrgPost SET sortId=sortId + 1 WHERE orgAccountId=? AND sortId>=? and sortId < ? and external_type=?", accountId,
                    sortNum,maxNum,externalType);
            List<V3xOrgPost> posts = this.orgCache.getAllV3xOrgEntityNoClone(V3xOrgPost.class, accountId,externalType);
            for (V3xOrgPost bo : posts) {
                if (bo.getSortId() >= sortNum && bo.getSortId().compareTo(maxNum)<0) {
                    bo.setSortId(bo.getSortId() + 1);
                    entityIds.add(bo.getId());
                }
            }
        	if(Strings.isNotEmpty(entityIds)){
        		orgCache.cacheUpdateV3xOrgEntityOnlySortId(entityIds,OrgConstants.ORGENT_TYPE.Post);
        	}
        } else if (entityClass == V3xOrgRole.class) {
            DBAgent.bulkUpdate("UPDATE OrgRole SET sortId=sortId + 1 WHERE orgAccountId=? AND sortId>=? and external_type=?", accountId,
                    sortNum,externalType);
            List<V3xOrgRole> roles = this.orgCache.getAllV3xOrgEntityNoClone(V3xOrgRole.class, accountId);
            for (V3xOrgRole bo : roles) {
                if (bo.getSortId() >= sortNum) {
                    bo.setSortId(bo.getSortId() + 1);
                    entityIds.add(bo.getId());
                }
            }
        	if(Strings.isNotEmpty(entityIds)){
        		orgCache.cacheUpdateV3xOrgEntityOnlySortId(entityIds,OrgConstants.ORGENT_TYPE.Role);
        	}
        } else if (entityClass == V3xOrgTeam.class) {
            DBAgent.bulkUpdate("UPDATE OrgTeam SET sortId=sortId + 1 WHERE orgAccountId=? AND sortId>=?", accountId,
                    sortNum);
            List<V3xOrgTeam> teams = this.orgCache.getAllV3xOrgEntityNoClone(V3xOrgTeam.class, accountId);
            for (V3xOrgTeam bo : teams) {
                if (bo.getSortId() >= sortNum) {
                    bo.setSortId(bo.getSortId() + 1);
                    entityIds.add(bo.getId());
                }
            }
        	if(Strings.isNotEmpty(entityIds)){
        		orgCache.cacheUpdateV3xOrgEntityOnlySortId(entityIds,OrgConstants.ORGENT_TYPE.Team);
        	}
        } else if (entityClass == V3xOrgLevel.class) {
            DBAgent.bulkUpdate("UPDATE OrgLevel SET sortId=sortId + 1 WHERE orgAccountId=? AND sortId>=?", accountId,
                    sortNum);
            List<V3xOrgLevel> levels = this.orgCache.getAllV3xOrgEntityNoClone(V3xOrgLevel.class, accountId);
            for (V3xOrgLevel bo : levels) {
                if (bo.getSortId() >= sortNum) {
                    bo.setSortId(bo.getSortId() + 1);
                    entityIds.add(bo.getId());
                }
            }
        	if(Strings.isNotEmpty(entityIds)){
        		orgCache.cacheUpdateV3xOrgEntityOnlySortId(entityIds,OrgConstants.ORGENT_TYPE.Level);
        	}
        } else {
            DBAgent.bulkUpdate("UPDATE " + entityClass.getSimpleName()
                    + " SET sortId=sortId + 1 WHERE orgAccountId=? AND sortId>=?", accountId, sortNum);
        }

    }
    
    @Override
    public <T extends V3xOrgEntity> void insertRepeatSortNum(Class<T> entityClass, Long accountId, Long sortNum, Boolean isInternal,int externalType) {
      this.insertRepeatSortNum(entityClass, accountId, sortNum, isInternal, externalType,null);
    }
    
	public int getMaxOutternalDeptSortId(Long accountId) {
		Map<String, Object> param = new HashMap<String, Object>();
		String sql = null;
		sql = "SELECT max(sortId) FROM OrgUnit  WHERE type=:type AND deleted=false AND orgAccountId=:accountId AND internal=false";

		param.put("type", OrgConstants.UnitType.Department.name());
		param.put("accountId", accountId);
		List lst = DBAgent.find(sql, param);
		if (!lst.isEmpty() && lst.get(0) != null) {
			return Integer.valueOf(lst.get(0).toString());
		} else {
			return 0;
		}
	}

	public int getExtMemberMaxSortId(Long accountId) {
		Map<String, Object> param = new HashMap<String, Object>();
		String sql = null;
		sql = "SELECT max(sortId) FROM OrgMember WHERE orgAccountId=:accountId AND deleted=false AND admin=false AND assigned=true AND internal=false";
		param.put("accountId", accountId);
		List lst = DBAgent.find(sql, param);
		if (!lst.isEmpty() && lst.get(0) != null) {
			return Integer.valueOf(lst.get(0).toString());
		} else {
			return 0;
		}
	}

	public <T extends V3xOrgEntity> int getMaxSortId(Class<T> entityClass,
			Long accountId) {
		Map<String, Object> param = new HashMap<String, Object>();
		String sql = null;

		if (V3xOrgMember.class == entityClass) {
			sql = "SELECT max(sortId) FROM OrgMember WHERE orgAccountId=:accountId AND deleted=false AND admin=false AND enable=true AND assigned=true AND internal=true";
			param.put("accountId", accountId);
		} else if (V3xOrgAccount.class == entityClass) {
			sql = "SELECT max(sortId) FROM OrgUnit WHERE type=:type AND deleted=false";
			param.put("type", OrgConstants.UnitType.Account.name());
		} else if (V3xOrgDepartment.class == entityClass) {
			sql = "SELECT max(sortId) FROM OrgUnit  WHERE type=:type AND deleted=false AND orgAccountId=:accountId AND internal=true";

			param.put("type", OrgConstants.UnitType.Department.name());
			param.put("accountId", accountId);
		} else if (V3xOrgTeam.class == entityClass) {
			sql = "SELECT max(sortId) FROM OrgTeam WHERE deleted=false AND type!=:type AND orgAccountId=:accountId";

			param.put("type", OrgConstants.TEAM_TYPE.PERSONAL.ordinal());
			param.put("accountId", accountId);
		} else {
			Class<? extends BasePO> poEntityClass = OrgHelper
					.boClassTopoClass(entityClass);
			sql = "SELECT max(sortId) FROM " + poEntityClass.getSimpleName()
					+ " WHERE deleted=false AND orgAccountId=:accountId";

			param.put("accountId", accountId);
		}

		List lst = DBAgent.find(sql, param);
		if (!lst.isEmpty() && lst.get(0) != null) {
			return Integer.valueOf(lst.get(0).toString());
		} else {
			return 0;
		}
	}
	
	@Override
	public <T extends V3xOrgEntity> int getMaxSortId(Class<T> entityClass,Long accountId, int externalType) {
		return getMaxSortId(entityClass, accountId, externalType, null);
	}
	
	@Override
	public <T extends V3xOrgEntity> int getMaxSortId(Class<T> entityClass,Long accountId, int externalType,Long createrId) {
		Map<String, Object> param = new HashMap<String, Object>();
		String sql = null;

		if (V3xOrgMember.class == entityClass) {
			sql = "SELECT max(sortId) FROM OrgMember WHERE orgAccountId=:accountId AND deleted=false AND admin=false AND enable=true AND assigned=true AND externalType=:externalType";
			param.put("accountId", accountId);
			param.put("externalType", externalType);
		} else if (V3xOrgDepartment.class == entityClass) {
			sql = "SELECT max(sortId) FROM OrgUnit  WHERE type=:type AND deleted=false AND orgAccountId=:accountId AND externalType=:externalType";

			param.put("type", OrgConstants.UnitType.Department.name());
			param.put("accountId", accountId);
			param.put("externalType", externalType);
		} else if(V3xOrgAccount.class == entityClass && OrgConstants.ExternalType.Interconnect4.ordinal() == externalType && createrId != null){
			sql = "SELECT max(sortId) FROM OrgUnit  WHERE type=:type AND deleted=false AND createrId=:createrId AND externalType=:externalType";

			param.put("type", OrgConstants.UnitType.Account.name());
			param.put("createrId", createrId);
			param.put("externalType", externalType);
		}else{
			Class<? extends BasePO> poEntityClass = OrgHelper
					.boClassTopoClass(entityClass);
			sql = "SELECT max(sortId) FROM " + poEntityClass.getSimpleName()
					+ " WHERE deleted=false AND orgAccountId=:accountId AND externalType=:externalType";

			param.put("accountId", accountId);
			param.put("externalType", externalType);
		}

		List lst = DBAgent.find(sql, param);
		if (!lst.isEmpty() && lst.get(0) != null) {
			return Integer.valueOf(lst.get(0).toString());
		} else {
			return 0;
		}
	}

	@Override
	public <T extends V3xOrgEntity> boolean isPropertyDuplicated(
			Class<T> entityClass, String property, Object value) {
		return isPropertyDuplicated(entityClass,property,value,OrgConstants.ExternalType.Inner.ordinal());
	}
	@Override
	public <T extends V3xOrgEntity> boolean isPropertyDuplicated(
			Class<T> entityClass, String property, Object value,int externalType) {
		Map<String, Object> param = new HashMap<String, Object>();
		String sql = null;

		if (V3xOrgAccount.class == entityClass) {
			sql = "SELECT count(*) from OrgUnit WHERE type=:type AND deleted=false AND "
					+ property + "=:Porperty and externalType = :externalType";

			param.put("type", OrgConstants.UnitType.Account.name());
			param.put("Porperty", value);
			param.put("externalType", externalType);
		} else if (V3xOrgDepartment.class == entityClass) {
			sql = "SELECT count(*) FROM OrgUnit WHERE type=:type AND deleted=false AND "
					+ property + "=:Porperty and externalType = :externalType";

			param.put("type", OrgConstants.UnitType.Department.name());
			param.put("Porperty", value);
			param.put("externalType", externalType);
		} else if (V3xOrgVisitor.class == entityClass) {
			sql = "SELECT count(*) FROM OrgVisitor WHERE  "
					+ property + "=:Porperty";

			param.put("Porperty", value);
		} else {
			Class<? extends BasePO> poEntityClass = OrgHelper
					.boClassTopoClass(entityClass);

			sql = "SELECT count(*) FROM " + poEntityClass.getSimpleName()
					+ " where deleted=false AND " + property + "=:Porperty and externalType = :externalType";
			param.put("Porperty", value);
			param.put("externalType", externalType);
		}

		List lst = DBAgent.find(sql, param);
		if (!lst.isEmpty() && lst.get(0) != null) {
			return Integer.valueOf(String.valueOf(lst.get(0))) > 0;
		} else {
			return false;
		}
	}
	
	@Override
	public <T extends V3xOrgEntity> boolean isPropertyDuplicated(
            Class<T> entityClass, String property, Object value, Long accountId, Long entId) {
	    return isPropertyDuplicated(entityClass, property, value, accountId, entId, OrgConstants.ExternalType.Inner.ordinal());
    }
	
   @Override
    public <T extends V3xOrgEntity> boolean isPropertyDuplicated(
            Class<T> entityClass, String property, Object value, Long accountId, Long entId, int externalType) {
	   return isPropertyDuplicated(entityClass, property, value, accountId, entId, externalType, null);
   }
	
   @Override
    public <T extends V3xOrgEntity> boolean isPropertyDuplicated(
            Class<T> entityClass, String property, Object value, Long accountId, Long entId, int externalType, Long createrId) {
        Map<String, Object> param = new HashMap<String, Object>();
        String sql = null;

        if (V3xOrgAccount.class == entityClass) {
        	if(OrgConstants.ExternalType.Interconnect4.ordinal() == externalType && createrId != null){
        		sql = "SELECT count(*) FROM OrgUnit WHERE type=:type AND deleted=false AND "
        				+ property + "=:Porperty AND id !=:entId and externalType = :externalType and createrId = :createrId";
        		
        		param.put("type", OrgConstants.UnitType.Account.name());
        		param.put("Porperty", value);
        		param.put("entId", entId);
        		param.put("externalType", externalType);
        		param.put("createrId", createrId);
        	}else{
        		sql = "SELECT count(*) FROM OrgUnit WHERE type=:type AND deleted=false AND "
        				+ property + "=:Porperty AND id !=:entId and externalType = :externalType";
        		
        		param.put("type", OrgConstants.UnitType.Account.name());
        		param.put("Porperty", value);
        		param.put("entId", entId);
        		param.put("externalType", externalType);
        	}
        } else if (V3xOrgDepartment.class == entityClass) {
        	if(OrgConstants.ExternalType.Interconnect4.ordinal() != externalType){
        		sql = "SELECT count(*) FROM OrgUnit WHERE type=:type AND deleted=false "
        				+ " AND internal = (SELECT internal FROM OrgUnit WHERE id =:entId )"
        				+ " AND " + property + "=:Porperty AND id !=:entId ";
        		
        		if(null != accountId) {
        			sql += " AND orgAccountId=:OrgAccountId ";
        			param.put("OrgAccountId", accountId);
        		}
        		
        		if(OrgConstants.ExternalType.Interconnect1.ordinal() == externalType || OrgConstants.ExternalType.Interconnect2.ordinal() == externalType){
        			sql += " and (externalType = :externalType1 or externalType = :externalType2) ";
        			param.put("externalType1", OrgConstants.ExternalType.Interconnect1.ordinal());
        			param.put("externalType2", OrgConstants.ExternalType.Interconnect2.ordinal());
        		}else{
        			sql += " and externalType = :externalType ";
        			param.put("externalType", externalType);
        		}
        		
        		param.put("type", OrgConstants.UnitType.Department.name());
        		param.put("Porperty", value);
        		param.put("entId", entId);
        	}else{
        		sql = "SELECT count(*) FROM OrgUnit WHERE type=:type AND deleted=false "
        				+ " AND " + property + "=:Porperty AND id !=:entId "
		        		+ " and externalType = :externalType ";
        		
        		if(null != accountId) {
        			sql += " AND orgAccountId=:OrgAccountId ";
        			param.put("OrgAccountId", accountId);
        		}
        		
        		param.put("type", OrgConstants.UnitType.Department.name());
        		param.put("Porperty", value);
        		param.put("entId", entId);
        		param.put("externalType", externalType);
        	}
        } else if (V3xOrgMember.class == entityClass) {
            sql = "SELECT count(*) FROM OrgMember WHERE deleted=false AND admin=false";
            if (!"code".equals(property)) {
                sql += " AND internal = (SELECT internal FROM OrgMember WHERE id =:entId )";
            }
            sql+= " AND virtual=false AND assigned=true AND " + property + "=:Porperty AND id !=:entId and externalType = :externalType";
            
            if(null != accountId) {
                sql += " AND orgAccountId=:OrgAccountId ";
                param.put("OrgAccountId", accountId);
            }
            
            param.put("Porperty", value);
            param.put("entId", entId);
            param.put("externalType", externalType);
        } else {
            Class<? extends BasePO> poEntityClass = OrgHelper
                    .boClassTopoClass(entityClass);

            sql = "SELECT count(*) FROM " + poEntityClass.getSimpleName()
                    + " WHERE deleted=false AND " + property
                    + "=:Porperty AND id !=:entId and externalType = :externalType";
            
            if(null != accountId) {
                sql += " AND orgAccountId=:OrgAccountId ";
                param.put("OrgAccountId", accountId);
            }
            
            param.put("Porperty", value);
            param.put("entId", entId);
            param.put("externalType", externalType);
        }

        List lst = DBAgent.find(sql, param);
        if (!lst.isEmpty() && lst.get(0) != null) {
            return Integer.valueOf(String.valueOf(lst.get(0))) > 0;
        } else {
            return false;
        }
    }

	public <T extends V3xOrgEntity> boolean isPropertyDuplicated(
			Class<T> entityClass, String property, Object value, Long accountId) {
		Map<String, Object> param = new HashMap<String, Object>();
		String sql = null;

		if (V3xOrgAccount.class == entityClass) {
			sql = "SELECT count(*) FROM OrgUnit WHERE type=:type AND deleted=false AND "
					+ property + "=:Porperty";

			param.put("type", OrgConstants.UnitType.Account.name());
			param.put("Porperty", value);
		} else if (V3xOrgDepartment.class == entityClass) {
			sql = "SELECT count(*) FROM OrgUnit WHERE type=:type AND deleted=false AND "
					+ property + "=:Porperty AND orgAccountId=:OrgAccountId";

			param.put("type", OrgConstants.UnitType.Department.name());
			param.put("Porperty", value);
			param.put("OrgAccountId", accountId);
		} else {
			Class<? extends BasePO> poEntityClass = OrgHelper
					.boClassTopoClass(entityClass);

			sql = "SELECT count(*) FROM " + poEntityClass.getSimpleName()
					+ " WHERE deleted=false AND " + property
					+ "=:Porperty AND orgAccountId=:OrgAccountId";

			param.put("Porperty", value);
			param.put("OrgAccountId", accountId);
		}

		List lst = DBAgent.find(sql, param);
		if (!lst.isEmpty() && lst.get(0) != null) {
			return Integer.valueOf(String.valueOf(lst.get(0))) > 0;
		} else {
			return false;
		}
	}

	public String getMaxPathByParentPath(String parentPath) {
		String maxPath = "";
		Map<String, Object> param = new HashMap<String, Object>();

		String sql = "SELECT MAX(path)+1 FROM OrgUnit WHERE path LIKE :parentPath AND deleted=false AND enable=true ";
		param.put("parentPath", parentPath + "____");

		List lst = DBAgent.find(sql, param);
		if (!lst.isEmpty() && lst.get(0) != null) {
			maxPath = String.valueOf(lst.get(0));
		}
		return maxPath;
	}

	@Override
	public void deleteOrgRelationshipPOByAccountId(Long accountId) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" FROM " + OrgRelationship.class.getSimpleName()
				+ " WHERE 1=1 ");
		if (accountId != null) {
			hql.append(" AND orgAccountId=:accountId");
			params.put("accountId", accountId);
		} else {
			return;
		}
		List<OrgRelationship> orgRelationships = DBAgent.find(
				"" + hql.toString(), params);
		DBAgent.bulkUpdate("DELETE" + hql.toString(), params);
		this.orgCache.cacheRemoveRelationship(orgRelationships);
	}

	@Override
	public void updateRelationship(OrgRelationship orgRelationshipPO) {
		DBAgent.mergeAll(addPoToList(orgRelationshipPO));
		List<OrgRelationship> orgRelationshipPOs = new ArrayList<OrgRelationship>(
				1);
		orgRelationshipPOs.add(orgRelationshipPO);
		this.orgCache.cacheUpdateRelationship(orgRelationshipPOs);
	}
	
	private List addPoToList(BasePO po){
		List list = new ArrayList();
		list.add(po);
		return list;
	}


	public List<OrgRole> getBaseRole() {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" FROM " + OrgRole.class.getSimpleName()
				+ " WHERE 1=1 ");
		hql.append(" AND orgAccountId=:orgAccountId");
		hql.append(" AND bond<>0");
		params.put("orgAccountId", OrgConstants.GROUPID);
		List<OrgRole> rolelist = DBAgent.find(hql.toString(), params);
		return rolelist;
	}
	public List<PrivRoleMenu> getRoleMenu(OrgRole orgrole) {
		StringBuilder hql_r = new StringBuilder();
		Map<String, Object> params_r = new HashMap<String, Object>();
		hql_r.append(" FROM " + PrivRoleMenu.class.getSimpleName()
				+ " WHERE 1=1 ");
		hql_r.append(" AND roleid=:roleid");
		
		params_r.put("roleid", orgrole.getId());
		List<PrivRoleMenu> roleResourcelist = DBAgent.find(hql_r.toString(), params_r);
		return roleResourcelist;
	}

	@Override
	public void updateRelationships(List<OrgRelationship> orgRelationshipPOs) {
		DBAgent.mergeAll(orgRelationshipPOs);
		this.orgCache.cacheUpdateRelationship(orgRelationshipPOs);
	}
	
	@Override
	public void deleteRelationships(List<OrgRelationship> orgRelationshipPOs) {
		DBAgent.mergeAll(orgRelationshipPOs);
	    DBAgent.deleteAll(orgRelationshipPOs);
	    this.orgCache.cacheRemoveRelationship(orgRelationshipPOs);
	}

	@Override
	public void deleteOrgRelationshipPOById(Long id) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" FROM " + OrgRelationship.class.getSimpleName()
				+ " WHERE 1=1 ");
		if (id != null) {
			hql.append(" AND id=:id");
			params.put("id", id);
		} else {
			return;
		}
		List<OrgRelationship> orgRelationships = DBAgent.find(
				"" + hql.toString(), params);
		DBAgent.bulkUpdate("DELETE" + hql.toString(), params);
		this.orgCache.cacheRemoveRelationship(orgRelationships);
	}

	@Override
	public List<OrgRelationship> getOrgRelationshipPOByMembers(
			RelationshipType type, List<Long> sourceIds, Long accountId,
			EnumMap<RelationshipObjectiveName, Object> objectiveIds,
			FlipInfo flipInfo) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" FROM " + OrgRelationship.class.getSimpleName()
				+ " WHERE 1=1 ");
		if (type != null) {
			hql.append(" AND type=:type");
			params.put("type", type.name());
		}
		if (sourceIds != null && !sourceIds.isEmpty()) {
			hql.append(" AND sourceId in (:sourceId)");
			params.put("sourceId", sourceIds);
		}
		if (accountId != null) {
			hql.append(" AND orgAccountId=:accountId");
			params.put("accountId", accountId);
		}
        if (objectiveIds != null && !objectiveIds.isEmpty()) {
            Set<Map.Entry<OrgConstants.RelationshipObjectiveName, Object>> entries = objectiveIds.entrySet();
            for (Map.Entry<OrgConstants.RelationshipObjectiveName, Object> entry : entries) {
                String o = entry.getKey().name();
                // 增强关系表的字符串模糊查询
                if (OrgConstants.RelationshipObjectiveName.objective5Id.name().equals(o)
                        || OrgConstants.RelationshipObjectiveName.objective6Id.name().equals(o)
                        || OrgConstants.RelationshipObjectiveName.objective7Id.name().equals(o)) {
                    hql.append(" AND " + o + " like:" + o + SQLWildcardUtil.setEscapeCharacter());
                    params.put(o, "%" + SQLWildcardUtil.escape(String.valueOf(entry.getValue())) + "%");
                } else {
                    hql.append(" AND " + o + " =:" + o);
                    params.put(o, entry.getValue());
                }
            }
        }

		return DBAgent.find(hql.toString(), params, flipInfo);
	}
	
	
	@Override
	public List<OrgRelationship> getOrgRelationshipPOByAccountsAndMembers(
			RelationshipType type, List<Long> sourceIds, List<Long> accountIds,
			EnumMap<RelationshipObjectiveName, Object> objectiveIds,
			FlipInfo flipInfo) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" FROM " + OrgRelationship.class.getSimpleName()
				+ " WHERE 1=1 ");
		if (type != null) {
			hql.append(" AND type=:type");
			params.put("type", type.name());
		}
		if (sourceIds != null && !sourceIds.isEmpty()) {
			hql.append(" AND sourceId in (:sourceId)");
			params.put("sourceId", sourceIds);
		}
		if (accountIds != null) {
			hql.append(" AND orgAccountId in(:accountIds)");
			params.put("accountIds", accountIds);
		}
        if (objectiveIds != null && !objectiveIds.isEmpty()) {
            Set<Map.Entry<OrgConstants.RelationshipObjectiveName, Object>> entries = objectiveIds.entrySet();
            for (Map.Entry<OrgConstants.RelationshipObjectiveName, Object> entry : entries) {
                String o = entry.getKey().name();
                // 增强关系表的字符串模糊查询
                if (OrgConstants.RelationshipObjectiveName.objective5Id.name().equals(o)
                        || OrgConstants.RelationshipObjectiveName.objective6Id.name().equals(o)
                        || OrgConstants.RelationshipObjectiveName.objective7Id.name().equals(o)) {
                    hql.append(" AND " + o + " like:" + o + SQLWildcardUtil.setEscapeCharacter());
                    params.put(o, "%" + SQLWildcardUtil.escape(String.valueOf(entry.getValue())) + "%");
                } else {
                	//如果是数组，则用in，否则用=
                	if(entry.getValue() instanceof ArrayList){
                		List<Long> list  = (ArrayList<Long>)entry.getValue();
                		int maxSize = list.size();
                		if(Strings.isNotEmpty(list)){
                			int size = 900;
                			int count = 0;
                			int j=0;
                			hql.append(" AND (" );
                			while(count<maxSize){
                				List<Long> ids = new ArrayList<Long>();
                				int i;
                				int len = (count+size<maxSize)?count+size:maxSize;
                				for(i =count;i<len;i++){
                					ids.add(list.get(i));
                				}
                				count = i;
                				String o0 = o+j;
                				if(j==0){
                					hql.append(o + " in(:"+o0+")" );
                				}else{
                					hql.append(" or " + o + " in(:"+o0+")" );
                				}
                				params.put(o0, ids);
                				j++;
                			}
                			hql.append(")" );
                		}
                	}else{
                		hql.append(" AND " + o + " =:" + o);
                		params.put(o, entry.getValue());
                	}
                }
            }
        }

		return DBAgent.find(hql.toString(), params, flipInfo);
		
	}
	
	@Override
    public List<OrgRelationship> getOrgRelationshipPOByMemberName(String name, FlipInfo flipInfo,String openFrom,List<Long> subUnitIds) {
	    StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        
        hql.append("SELECT r ");

        hql.append(" FROM OrgRelationship as r ");
        hql.append(" WHERE r.type = 'Member_Post' AND r.objective5Id = 'Concurrent' AND r.sourceId in (SELECT m.id from OrgMember as m where m.name like :name "+ SQLWildcardUtil.setEscapeCharacter()+")");
        params.put("name", "%" + SQLWildcardUtil.escape(name) + "%");
        
        if("subUnitManage".equals(openFrom) && subUnitIds.size() > 0 ){
        	hql.append(" AND (r.orgAccountId in(:subUnitId) or r.sourceId in (SELECT id FROM "+OrgMember.class.getSimpleName()+" WHERE orgAccountId in(:subUnitId)))");
            params.put("subUnitId", subUnitIds);
        }
        
	    return DBAgent.find(hql.toString(), params, flipInfo);
	}

	@Override
	public List<OrgTeam> getAllTeamPO(Long accountId, Integer type,
			Boolean enable, Map<String, Object> param, FlipInfo flipInfo) {
		
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append(" FROM " + OrgTeam.class.getSimpleName());
		hql.append(" WHERE deleted=false");
		if (accountId != null) {
			hql.append(" AND org_Account_Id=:accountId");
			params.put("accountId", accountId);
		}

		if (type != null) {
			hql.append(" AND type=:type");
			params.put("type", type);
		}
		if (enable != null) {
			hql.append(" AND enable=:enable");
			params.put("enable", enable);
		}
		List<OrgTeam> list = new ArrayList<OrgTeam>();
		if (param != null) {
			// 按人员名查询组
			if((param.size()==1)&&param.containsKey("Member")){
				Long memberId= Long.valueOf(param.get("Member").toString());
				StringBuilder sql = new StringBuilder();
				sql.append("select t2 from OrgRelationship t1, OrgTeam t2 where t1.sourceId = t2.id and t2.deleted =:deleted and t1.type =:type");
				sql.append(" and t1.objective6Id =:teamMeberType and t1.objective0Id =:teamMemberId ORDER BY t2.sortId ASC ");
				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("deleted", false);
				parameter.put("type", "Team_Member");
				parameter.put("teamMeberType", "Member");
				parameter.put("teamMemberId", memberId);
				list = DBAgent.find(sql.toString(), parameter);
			} else {
				// 查询全部组；按组名、状态、权限属性查询组
				Set<Entry<String, Object>> paramSet = param.entrySet();
	            for (Entry<String, Object> entry : paramSet) {
	                String condition = entry.getKey();
	                Object feildvalue = entry.getValue();
	                feildvalue = OrgHelper.StrtoBoolean(feildvalue);
	                if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
	                    if (feildvalue instanceof String) {
	                        hql.append(" AND (").append(condition).append(" LIKE :feildvalue"+ SQLWildcardUtil.setEscapeCharacter()+")");
	                        feildvalue = "%" + SQLWildcardUtil.escape(String.valueOf(feildvalue)) + "%";
	                    } else {
	                        hql.append(" AND ").append(condition).append("=:feildvalue");
	                    }
	                    params.put("feildvalue", feildvalue);
	                }
	            }
	            hql.append(" ORDER BY sortId ASC");
	    		
	    		list = DBAgent.find(hql.toString(), params, flipInfo);
			}
			
		}
		
		
		return list;
	}
	
	@Override
	public List<OrgMember> getAllMemberPOByAccountIdAndSecondPostId(Long accountId, Long secondPostId, Boolean isInternal, Boolean enable, Map<String, Object> param, FlipInfo flipInfo) {
	    
	    StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        hql.append("SELECT m ");

        if (null != param && param.containsKey("loginName")) {
            hql.append(" FROM OrgMember as m, OrgPrincipal as p ");
            hql.append(" WHERE m.id=p.memberId AND ");
        } else if(null != secondPostId) {
            hql.append(" FROM OrgMember as m, OrgRelationship as orp "
                    + " WHERE orp.sourceId = m.id and orp.type = 'Member_Post' and orp.objective5Id = 'Second' and orp.objective1Id = "
                    + secondPostId);
        } else {
            hql.append(" FROM OrgMember as m ");
            hql.append(" WHERE ");
        }
        
        if (accountId != null) {
            hql.append(" AND m.orgAccountId=:accountId ");
            params.put("accountId", accountId);
        }
        if (isInternal != null) {
            hql.append(" AND m.internal=:internal");
            params.put("internal", isInternal);
        }
        if (enable != null) {
            hql.append(" AND m.enable=:enable ");
            params.put("enable", enable);
        }

        hql.append(" AND m.deleted=false AND m.admin=false AND m.virtual=false AND m.assigned=true ");

        if (null != param) {
            Set<Entry<String, Object>> paramSet = param.entrySet();
            for (Entry<String, Object> entry : paramSet) {
                String condition = entry.getKey();
                Object feildvalue = entry.getValue();
                if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
                	if("workLocal".equals(condition)){
                		hql.append(" AND m.extAttr36").append(" like :" + condition);
                		params.put(condition, feildvalue);
                	}else if("orgDepartmentId_includeChildren".equals(condition)){
                		Long departmentId = Long.valueOf(feildvalue.toString());
            			OrgUnit department = this.getEntity(OrgUnit.class, departmentId);
            			hql.append(" AND m.orgDepartmentId in (select d.id from OrgUnit as d WHERE (d.path like :dPath) AND d.type=:dType AND d.deleted=false AND d.orgAccountId=:dAccountId)");
            			params.put("dPath", department.getPath() + "%");
            			params.put("dType", OrgConstants.UnitType.Department.name());
            			params.put("dAccountId", department.getOrgAccountId());
                	}else if (feildvalue instanceof String) {
                        if ("loginName".equals(condition)) {
                            hql.append(" AND (p.").append(condition).append(" LIKE :" + condition  + SQLWildcardUtil.setEscapeCharacter() + ")");
                        } else {
                            hql.append(" AND (m.").append(condition).append(" LIKE :" + condition  + SQLWildcardUtil.setEscapeCharacter() + ")");
                        }
                        feildvalue = "%" + SQLWildcardUtil.escape(String.valueOf(feildvalue)) + "%";
                        params.put(condition, String.valueOf(feildvalue));
                    } else if (feildvalue != null) {
                        hql.append(" AND m.").append(condition).append("=:" + condition);
                        params.put(condition, feildvalue);
                    }
                }
            }
        }
        
        hql.append(" ORDER BY m.sortId ASC");

        return DBAgent.find(hql.toString(), params, flipInfo);
	    
	    
	}
	
	public List<List<Long>> listToArray(List<Long> departmentIds, int bccSize) {
		List<List<Long>> list2 = new ArrayList<List<Long>>();
		if(departmentIds == null || departmentIds.isEmpty()){
			return list2;
		}
		int size = departmentIds.size();
		if(size<=bccSize){
			list2.add(departmentIds);
			return list2;
		}else{
			int count = size/bccSize;
			int sub = size%bccSize;
			for(int i=0;i<count+1;i++){
				if(i==count){
					List<Long> subList = departmentIds.subList(i * bccSize, i * bccSize + sub);
					list2.add(subList);
				}else{
					List<Long> subList = departmentIds.subList(i*bccSize, (i+1)*bccSize);
					list2.add(subList);					
				}
			}
		}
		return list2;
	}
	
	
	/**
	 * 查询汇报人在此部门下的所有人员
	 * @param deptId
	 * @return
	 */
	public List<OrgMember> getAllMembersByReportToDept(Long deptId) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append("SELECT m ");
		hql.append(" FROM " + OrgMember.class.getSimpleName() + " as m "
				+ "where extAttr37 is not NULL and extAttr37 IN"
				+ "(select DISTINCT(sourceId) from OrgRelationship where type='Member_Post' and  objective0Id =:deptId  )"
				+ " and orgDepartmentId !=:deptId");

		params.put("deptId", deptId);

		hql.append(" ORDER BY m.sortId ASC");

		return DBAgent.find(hql.toString(), params);
	}
	
	/**
	 * 查询汇报人为此人的所有人员
	 * @param deptId
	 * @return
	 */
	public List<OrgMember> getAllMembersByReportToMember(Long[] memberIds) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("SELECT m FROM " + OrgMember.class.getSimpleName() + " as m where extAttr37 is not NULL ");
		
		
		List<Long> strIds = new ArrayList<Long>();
		for(int i=0;i<memberIds.length;i++ ){
			strIds.add(memberIds[i]);
		}
		
		if(Strings.isNotEmpty(strIds)){
			hql.append("and extAttr37 IN (:strIds)");
			params.put("strIds", strIds);
		}else{
			return null;
		}

		hql.append(" ORDER BY m.sortId ASC");

		return DBAgent.find(hql.toString() ,params);
	}
	
	
	@Override
	public List<BasePO> getAllEntityPO(String entityClassName, Long accountId,Date lastDate) {
		return getAllEntityPO(entityClassName,accountId,lastDate,OrgConstants.ExternalType.Inner.ordinal());
	}
	
	@Override
	public List<BasePO> getAllEntityPO(String entityClassName, Long accountId,Date lastDate,int externalType) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("SELECT m from ");
		String h="";
		if(entityClassName.equals(V3xOrgLevel.class.getSimpleName())){
			h = OrgLevel.class.getSimpleName() + " as m where 1=1 ";
		}else if(entityClassName.equals(V3xOrgMember.class.getSimpleName())){
			h = OrgMember.class.getSimpleName() + " as m where m.admin=false and externalType=:externalType";
			params.put("externalType", externalType);
		}else if(entityClassName.equals(V3xOrgPost.class.getSimpleName())){
			h = OrgPost.class.getSimpleName() + " as m where externalType=:externalType ";
			params.put("externalType", externalType);
		}else if(entityClassName.equals(V3xOrgRole.class.getSimpleName())){
			h = OrgRole.class.getSimpleName() + " as m where externalType=:externalType ";
			params.put("externalType", externalType);
		}else if(entityClassName.equals(V3xOrgTeam.class.getSimpleName())){
			h = OrgTeam.class.getSimpleName() + " as m where 1=1 ";
		}else if(entityClassName.equals(V3xOrgAccount.class.getSimpleName())){
			h = OrgUnit.class.getSimpleName() + " as m where m.type=:type and externalType=:externalType";
			params.put("type", OrgConstants.UnitType.Account.name());
			params.put("externalType", externalType);
		}else if(entityClassName.equals(V3xOrgDepartment.class.getSimpleName())){
			h = OrgUnit.class.getSimpleName() + " as m where m.type=:type and externalType=:externalType";
			params.put("type", OrgConstants.UnitType.Department.name());
			params.put("externalType", externalType);
		}

		hql.append(h);
		if (accountId != null && !accountId.equals(V3xOrgEntity.VIRTUAL_ACCOUNT_ID)) {
			hql.append(" AND m.orgAccountId=:orgAccountId ");
			params.put("orgAccountId", accountId);
		}
		
		if(lastDate!=null){
			hql.append(" AND m.updateTime>:lastDate ");
			params.put("lastDate", lastDate);
		}

		hql.append(" ORDER BY m.sortId ASC");

		return DBAgent.find(hql.toString(), params);
	}
	
	@Override
	public List<Long> getAllEntityIds(String entityClassName, Long accountId,Date lastDate,int externalType) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("SELECT m.id from ");
		String h="";
		if(entityClassName.equals(V3xOrgLevel.class.getSimpleName())){
			h = OrgLevel.class.getSimpleName() + " as m where 1=1 ";
		}else if(entityClassName.equals(V3xOrgMember.class.getSimpleName())){
			h = OrgMember.class.getSimpleName() + " as m where m.admin=false and externalType=:externalType";
			params.put("externalType", externalType);
		}else if(entityClassName.equals(V3xOrgPost.class.getSimpleName())){
			h = OrgPost.class.getSimpleName() + " as m where externalType=:externalType ";
			params.put("externalType", externalType);
		}else if(entityClassName.equals(V3xOrgRole.class.getSimpleName())){
			h = OrgRole.class.getSimpleName() + " as m where externalType=:externalType ";
			params.put("externalType", externalType);
		}else if(entityClassName.equals(V3xOrgTeam.class.getSimpleName())){
			h = OrgTeam.class.getSimpleName() + " as m where 1=1 ";
		}else if(entityClassName.equals(V3xOrgAccount.class.getSimpleName())){
			h = OrgUnit.class.getSimpleName() + " as m where m.type=:type and externalType=:externalType";
			params.put("type", OrgConstants.UnitType.Account.name());
			params.put("externalType", externalType);
		}else if(entityClassName.equals(V3xOrgDepartment.class.getSimpleName())){
			h = OrgUnit.class.getSimpleName() + " as m where m.type=:type and externalType=:externalType";
			params.put("type", OrgConstants.UnitType.Department.name());
			params.put("externalType", externalType);
		}

		hql.append(h);
		if (accountId != null && !accountId.equals(V3xOrgEntity.VIRTUAL_ACCOUNT_ID)) {
			hql.append(" AND m.orgAccountId=:orgAccountId ");
			params.put("orgAccountId", accountId);
		}
		
		if(lastDate!=null){
			hql.append(" AND m.updateTime>:lastDate ");
			params.put("lastDate", lastDate);
		}

		return DBAgent.find(hql.toString(), params);
	}
	
	/**
	 * 查询停用和删除的实体
	 * @param entityClassName
	 * @param accountId
	 * @return
	 */
	
	@Override
	public List<BasePO> getDisableEntityPO(String entityClassName, Long accountId,String condition, Object feildvalue) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("SELECT m from ");
		String h="";
		if(entityClassName.equals(V3xOrgLevel.class.getSimpleName())){
			h = OrgLevel.class.getSimpleName() + " as m where (m.deleted=true or (m.deleted=false and m.enable=false)) ";
		}else if(entityClassName.equals(V3xOrgMember.class.getSimpleName())){
			h = OrgMember.class.getSimpleName() + " as m where m.admin=false and (m.deleted=true or (m.deleted=false and m.enable=false))  and externalType=0 ";
		}else if(entityClassName.equals(V3xOrgPost.class.getSimpleName())){
			h = OrgPost.class.getSimpleName() + " as m where (m.deleted=true or (m.deleted=false and m.enable=false))  and externalType=0  ";
		}else if(entityClassName.equals(V3xOrgRole.class.getSimpleName())){
			h = OrgRole.class.getSimpleName() + " as m where (m.deleted=true or (m.deleted=false and m.enable=false))  and externalType=0 ";
		}else if(entityClassName.equals(V3xOrgTeam.class.getSimpleName())){
			h = OrgTeam.class.getSimpleName() + " as m where (m.deleted=true or (m.deleted=false and m.enable=false))  and externalType=0 ";
		}else if(entityClassName.equals(V3xOrgAccount.class.getSimpleName())){
			h = OrgUnit.class.getSimpleName() + " as m where m.type=:type and (m.deleted=true or (m.deleted=false and m.enable=false))  and externalType=0 ";
			params.put("type", OrgConstants.UnitType.Account.name());
		}else if(entityClassName.equals(V3xOrgDepartment.class.getSimpleName())){
			h = OrgUnit.class.getSimpleName() + " as m where m.type=:type and (m.deleted=true or (m.deleted=false and m.enable=false))  and externalType=0";
			params.put("type", OrgConstants.UnitType.Department.name());
		}
	
		hql.append(h);
		if (accountId != null && !accountId.equals(V3xOrgEntity.VIRTUAL_ACCOUNT_ID)) {
			hql.append(" AND m.orgAccountId=:orgAccountId ");
			params.put("orgAccountId", accountId);
		}
		
		if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
			if (feildvalue instanceof String) {
				hql.append(" AND ").append(SQLWildcardUtil.likeLowerCase("m."+condition)).append(" LIKE :feildvalue " + SQLWildcardUtil.setEscapeCharacter());
				feildvalue = "%" + SQLWildcardUtil.escape(String.valueOf(feildvalue)).toLowerCase() + "%";
			} else {
				hql.append(" and m.").append(condition).append("=:feildvalue");
			}

			params.put("feildvalue", feildvalue);
		}
	
		hql.append(" ORDER BY m.sortId ASC");
		
		FlipInfo flipInfo = new FlipInfo();
		flipInfo.setPage(1);
		flipInfo.setSize(10);
		return DBAgent.find(hql.toString(), params,flipInfo);
	}

    public OrgMember getFirstCreateMember() {
        StringBuilder hql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        hql.append(" from OrgMember as m where m.admin=0 and m.externalType=0 order by m.createTime asc");
        FlipInfo flipInfo = new FlipInfo();
        flipInfo.setPage(1);
        flipInfo.setSize(3);
        List<OrgMember> members = DBAgent.find(hql.toString());
        if (Strings.isNotEmpty(members)) {
            return members.get(0);
        } else {
            return null;
        }
    }
    
    @Override
	public List<OrgVisitor> getOrgVisitor(FlipInfo flipInfo, Map param) throws BusinessException {
    	StringBuilder sql = new StringBuilder();
    	Map<String, Object> params = new HashMap<String, Object>();
    	sql.append("select ov from OrgVisitor ov where 1=1 ");
    	boolean allState = false;
    	if(param.containsKey("state") && Integer.valueOf(param.get("state").toString()) == -1) {
    		allState = true;
    	}else {
    		sql.append(" and state !=:deletedState ");
    		params.put("deletedState", OrgConstants.VISITOR_STATE.DELETE.ordinal());
    	}
        if (null != param && param.size()>0) {
            Set<Entry<String, Object>> paramSet = param.entrySet();
        	for (Entry<String, Object> entry : paramSet) {
                String condition = entry.getKey();
                Object feildvalue = entry.getValue();
                if (StringUtils.isNotBlank(condition) && !"null".equals(condition)) {
                	if("orgAccountId".equals(condition)){
                    	sql.append(" AND org_account_id =:orgAccountId");
                    	params.put("orgAccountId", param.get("orgAccountId"));
                    }
                	if("id".equals(condition)){
                		sql.append(" AND id =:id");
                		params.put(condition, feildvalue);
                    }
                	if("name".equals(condition)){
                		sql.append(" AND name LIKE :name"+SQLWildcardUtil.setEscapeCharacter());
            			params.put(condition, "%"+SQLWildcardUtil.escape(String.valueOf(feildvalue))+"%");
            		}
                	if("mobile".equals(condition)){
                		sql.append(" AND mobile = :mobile");
            			params.put(condition, feildvalue);
            		}
                	if("account_name".equals(condition)){
                		sql.append(" AND account_name LIKE :account_name"+SQLWildcardUtil.setEscapeCharacter());
            			params.put(condition, "%"+SQLWildcardUtil.escape(String.valueOf(feildvalue))+"%");
            		}
                	
                	if("regtime".equals(condition)){
                		String qTime = String.valueOf(feildvalue);
                    	if(Strings.isNotBlank(qTime)){
                    		qTime = qTime.substring(0, qTime.length()).replaceAll("\"", "");
                    		String[] querytime = qTime.split(",");
                    		for(int i=0;i<querytime.length;i++){
                    			Date date = Datetimes.parse(querytime[i]);
                    			if(i==0 && date != null){
                    				sql.append(" AND create_date >= :start_time ");
                        			params.put("start_time", date);
                    			}
                    			if(i==1 && date != null){
                    				sql.append(" AND create_date <= :end_time ");
                        			params.put("end_time", date);
                    			}
                    		}
                    	}
                	}
                	if("state".equals(condition) && !allState){
                		sql.append(" AND state =:state ");
            			params.put(condition, feildvalue);
            		}
                	
                }
            }
        }
        sql.append(" order by create_date desc ");
		return DBAgent.find(sql.toString(), params, flipInfo);
    }

}