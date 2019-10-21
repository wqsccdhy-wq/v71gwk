package com.seeyon.ctp.organization.manager;

import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.webmodel.RelationMemberAttribute;


public interface OrgRelationDataManager {
	/**
	 * 获取表单可以使用的人员关联属性
	 * @return
	 */
	public List<RelationMemberAttribute> getMemberRelationAttribute() throws BusinessException;
	
	/**
	 * 根据人员属性编号，获取人员属性的值
	 * @param memberId
	 * @param attributeCode
	 * @return
	 */
	public Object getMemberInfoByRelationAttribute(Long memberId, String attributeCode) throws BusinessException;
}