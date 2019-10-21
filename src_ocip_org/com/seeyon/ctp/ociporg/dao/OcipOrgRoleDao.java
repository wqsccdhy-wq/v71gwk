package com.seeyon.ctp.ociporg.dao;

import com.seeyon.ctp.organization.po.OrgLevel;
import com.seeyon.ctp.organization.po.OrgPost;

public interface OcipOrgRoleDao {

	public OrgPost getPostById(Long id);

	public OrgPost getPostByName(String name, Long accountid, String id);

	public OrgLevel getLevelById(Long id);

	public OrgLevel getLevelByName(String name, Long accountid, String id);

}
