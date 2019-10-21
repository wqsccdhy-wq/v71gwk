package com.seeyon.ctp.ociporg.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.ociporg.manager.OcipResourceTempManager;
import com.seeyon.ctp.ociporg.manager.OrgUnitTempManager;
import com.seeyon.ctp.ociporg.manager.impl.OcipDepartmentManagerImpl;
import com.seeyon.ctp.ociporg.manager.impl.OcipLevelManagerImpl;
import com.seeyon.ctp.ociporg.manager.impl.OcipMemberManagerImpl;
import com.seeyon.ctp.ociporg.manager.impl.OcipPostManagerImpl;
import com.seeyon.ctp.ociporg.manager.impl.OcipUnitManagerImpl;
import com.seeyon.ctp.ociporg.po.OcipResourceTemp;
import com.seeyon.ctp.util.FlipInfo;

public class OcipOrgController extends BaseController {

	private static final Logger LOGGER = Logger.getLogger(OcipOrgController.class);

	private OrgUnitTempManager orgUnitTempManager;

	private OcipUnitManagerImpl ocipUnitManagerImpl;

	private OcipDepartmentManagerImpl ocipDepartmentManagerImpl;

	private OcipMemberManagerImpl ocipMemberManagerImpl;

	private OcipResourceTempManager ocipResourceTempManager;
	
	private OcipPostManagerImpl ocipPostManagerImpl; 
	
	private OcipLevelManagerImpl ocipLevelManagerImpl;

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Short> list = orgUnitTempManager.findOrgUnitTempByGrade("-6219453952095074147");
		Collections.sort(list);
		System.out.println(list);
		return null;
	}

	public ModelAndView load(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// String unitId = "-6436400663630756582";
		// OrgUnitTemp orgUnitTemp = orgUnitTempManager.findOrgUnitTempById(unitId);

		Short isFlag = new Short("0");
		FlipInfo flipInfo = new FlipInfo();
		List<OcipResourceTemp> resourceTemps = ocipResourceTempManager.findOcipResourceTemp(isFlag);
		if (CollectionUtils.isEmpty(resourceTemps)) {
			return null;
		}
		
		/**
		 * TODO 测试，正式导入时不要打包这部分代码
		 */
		//String resourceId = "-6219453952095074147";
		//ocipUnitManagerImpl.importOrg(resourceId , flipInfo);
		//ocipDepartmentManagerImpl.importOrg(resourceId, flipInfo);
		//ocipPostManagerImpl.importOrg(resourceId, flipInfo);
		//ocipLevelManagerImpl.importOrg(resourceId, flipInfo);
		//ocipMemberManagerImpl.importOrg(resourceId, flipInfo);

		Short ok = new Short("1");
		for (OcipResourceTemp ocipResourceTemp : resourceTemps) {
			String resourceId = ocipResourceTemp.getId();
			ocipUnitManagerImpl.importOrg(resourceId,flipInfo);
			ocipDepartmentManagerImpl.importOrg(resourceId, flipInfo);
			ocipPostManagerImpl.importOrg(resourceId, flipInfo);
			ocipLevelManagerImpl.importOrg(resourceId, flipInfo);
			ocipMemberManagerImpl.importOrg(resourceId, flipInfo);
			
			ocipResourceTemp.setIsFlag(ok);
			ocipResourceTempManager.updatOcipResourceTemp(ocipResourceTemp);
			String sysName = ocipResourceTemp.getSysName();
			System.out.println("系统:" + sysName + " 导入组织机构完成");
		}
		
		System.out.println("导入组织机构完成");

		return null;
	}

	public OrgUnitTempManager getOrgUnitTempManager() {
		return orgUnitTempManager;
	}

	public void setOrgUnitTempManager(OrgUnitTempManager orgUnitTempManager) {
		this.orgUnitTempManager = orgUnitTempManager;
	}

	public OcipUnitManagerImpl getOcipUnitManagerImpl() {
		return ocipUnitManagerImpl;
	}

	public void setOcipUnitManagerImpl(OcipUnitManagerImpl ocipUnitManagerImpl) {
		this.ocipUnitManagerImpl = ocipUnitManagerImpl;
	}

	public OcipResourceTempManager getOcipResourceTempManager() {
		return ocipResourceTempManager;
	}

	public void setOcipResourceTempManager(OcipResourceTempManager ocipResourceTempManager) {
		this.ocipResourceTempManager = ocipResourceTempManager;
	}

	public OcipDepartmentManagerImpl getOcipDepartmentManagerImpl() {
		return ocipDepartmentManagerImpl;
	}

	public void setOcipDepartmentManagerImpl(OcipDepartmentManagerImpl ocipDepartmentManagerImpl) {
		this.ocipDepartmentManagerImpl = ocipDepartmentManagerImpl;
	}

	public OcipMemberManagerImpl getOcipMemberManagerImpl() {
		return ocipMemberManagerImpl;
	}

	public void setOcipMemberManagerImpl(OcipMemberManagerImpl ocipMemberManagerImpl) {
		this.ocipMemberManagerImpl = ocipMemberManagerImpl;
	}

	public OcipPostManagerImpl getOcipPostManagerImpl() {
		return ocipPostManagerImpl;
	}

	public void setOcipPostManagerImpl(OcipPostManagerImpl ocipPostManagerImpl) {
		this.ocipPostManagerImpl = ocipPostManagerImpl;
	}

	public OcipLevelManagerImpl getOcipLevelManagerImpl() {
		return ocipLevelManagerImpl;
	}

	public void setOcipLevelManagerImpl(OcipLevelManagerImpl ocipLevelManagerImpl) {
		this.ocipLevelManagerImpl = ocipLevelManagerImpl;
	}
	
	
	
}
