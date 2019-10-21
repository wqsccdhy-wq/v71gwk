package com.seeyon.ctp.organization.principal.dao;

import java.util.List;

import com.seeyon.ctp.organization.po.OrgPrincipal;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 *
 * 2010-11-16
 */
public interface PrincipalDao {

	/**
	 * 取得所有有效的账号
	 * @return
	 */
	public List<OrgPrincipal> selectAll();
	
	/**
	 * 批量持久化账号
	 * @param prps
	 * @param userRoles
	 */
	public void insertBatch(List<OrgPrincipal> prps);
	
	/**
	 * 修改账号信息
	 * @param orgPrincipal
	 */
	public void update(OrgPrincipal orgPrincipal);
	/**
	 * 批量修改账号信息
	 * @param orgPrincipal
	 */
	void updateBatch(List<OrgPrincipal> prps);

	/**
	 * 删除一个登录身份
	 * 
	 * @param meberId
	 */
	public void delete(long principalId);

	
}