
package com.seeyon.ctp.organization.manager;

import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgVisitor;


public interface VisitorManagerDirect {

	OrganizationMessage addVisitor(V3xOrgVisitor visitor) throws BusinessException;

	OrganizationMessage addVisitors(List<V3xOrgVisitor> visitors) throws BusinessException;

	OrganizationMessage updateVisitor(V3xOrgVisitor visitor) throws BusinessException;

	OrganizationMessage updateVisitors(List<V3xOrgVisitor> visitors) throws BusinessException;

}
