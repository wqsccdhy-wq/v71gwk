package com.seeyon.ctp.ociporg.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.seeyon.ctp.ociporg.dao.OcipOrgRoleDao;
import com.seeyon.ctp.organization.po.OrgLevel;
import com.seeyon.ctp.organization.po.OrgPost;
import com.seeyon.ctp.util.DBAgent;

public class OcipOrgRoleDaoImpl implements OcipOrgRoleDao {

	@Override
	public OrgPost getPostByName(String name, Long accountid, String id) {

		OrgPost orgPost = null;
		if (!Strings.isNullOrEmpty(id)) {
			orgPost = getPostById(Long.valueOf(id));
			if (orgPost != null) {
				return orgPost;
			}
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("accountid", accountid);
		String hql = "FROM OrgPost where name=:name and orgAccountId=:accountid";
		List<OrgPost> find = DBAgent.find(hql, params);
		if (find != null && !find.isEmpty()) {
			orgPost = find.get(0);
		}
		return orgPost;
	}

	@Override
	public OrgLevel getLevelByName(String name, Long accountid, String id) {

		OrgLevel orgLevel = null;
		if (!Strings.isNullOrEmpty(id)) {
			orgLevel = getLevelById(Long.valueOf(id));
			if (orgLevel != null) {
				return orgLevel;
			}
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("accountid", accountid);
		String hql = "FROM OrgLevel where name=:name and orgAccountId=:accountid";
		List<OrgLevel> find = DBAgent.find(hql, params);
		if (find != null && !find.isEmpty()) {
			orgLevel = find.get(0);
		}

		return orgLevel;
	}

	@Override
	public OrgPost getPostById(Long id) {
		return DBAgent.get(OrgPost.class, id);
	}

	@Override
	public OrgLevel getLevelById(Long id) {
		return DBAgent.get(OrgLevel.class, id);
	}

}
