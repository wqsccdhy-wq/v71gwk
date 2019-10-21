package com.seeyon.ctp.ociporg.manager.impl;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.seeyon.ctp.ociporg.manager.AbsOcipOrgManager;
import com.seeyon.ctp.ociporg.manager.OrgDepartmentTempManager;
import com.seeyon.ctp.ociporg.manager.OrgUnitTempManager;
import com.seeyon.ctp.ociporg.po.OrgDepartmentTemp;
import com.seeyon.ctp.ociporg.po.OrgUnitTemp;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.OrganizationMessage.MessageStatus;
import com.seeyon.ctp.organization.bo.OrganizationMessage.OrgMessage;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.util.FlipInfo;

public class OcipDepartmentManagerImpl extends AbsOcipOrgManager<OrgDepartmentTemp> {

	private static final Logger LOGGER = Logger.getLogger(OcipDepartmentManagerImpl.class);

	private OrgDepartmentTempManager orgDepartmentTempManager;
	
	private OrgUnitTempManager orgUnitTempManager;
	
	//private DepartmentManager departmentManager;

	@Override
	public void importOrg(String resourceId, FlipInfo flipInfo) {
		Short isFlag = new Short("0");
		List<Short> gradeList = orgDepartmentTempManager.findOrgDepartmentTemByGrade(resourceId);
		if (CollectionUtils.isEmpty(gradeList)) {
			return;
		}

		Collections.sort(gradeList);
		
		/**
		 * TODO  测试
		 */
		//一级部门 电子政务处
		/*OrgDepartmentTemp orgDepartmentTemp = orgDepartmentTempManager.findOrgDepartmentTempById("-5012969178853192900");
		importEntry(orgDepartmentTemp , resourceId);
		
		//二级部门  电子政务处系统维护人员
		OrgDepartmentTemp orgDepartmentTemp2 = orgDepartmentTempManager.findOrgDepartmentTempById("4940432715428151713");
		importEntry(orgDepartmentTemp2 , resourceId);*/
		
		for (Short grade : gradeList) {
			Long allCount = orgDepartmentTempManager.getCount(grade, isFlag, resourceId);
			if (allCount < 1) {
				String info = "resourceId:" + resourceId + "|没有需要导入的部门：grade：" + grade + "|isFlag:" + isFlag;
				LOGGER.info(info);
				System.out.println(info);
				continue;
			}

			String info = "resourceId:" + resourceId + "|grade为:" + grade + " 的部门一共有:" + allCount + "条数据需要导入!!!";
			LOGGER.info(info);
			System.out.println(info);
			Long count = (allCount / 20l) + 1;
			for (long i = 0; i < count; i++) {
				List<OrgDepartmentTemp> list = orgDepartmentTempManager.findOrgDepartmentTempByGrade(grade, isFlag, resourceId, flipInfo);
				if (!CollectionUtils.isEmpty(list)) {
					for (OrgDepartmentTemp orgDepartmentTemp : list) {
						importEntry(orgDepartmentTemp, resourceId);
					}
				}
			}
		}
		

	}

	@Override
	public void importEntry(OrgDepartmentTemp t, String resourceId) {
		long beginTime = System.currentTimeMillis();
		MessageStatus messageStatus = null;
		Short result = new Short("1");
		String id = t.getId();
		String name = t.getName();
		boolean success = false;
		String messageInfo = "";
		try {
			V3xOrgDepartment newdept = initDep(t);
			OrganizationMessage message = orgManagerDirect.addDepartment(newdept);
			success = message.isSuccess();
			if (success) {
				result = new Short("1");
				List<OrgMessage> successMsgs = message.getSuccessMsgs();
				OrgMessage orgMessage = successMsgs.get(0);
				messageStatus = orgMessage.getCode();
			} else {
				result = new Short("2");
				List<OrgMessage> errorMsgs = message.getErrorMsgs();
				OrgMessage orgMessage = errorMsgs.get(0);
				messageStatus = orgMessage.getCode();
			}
			t.setIsFlag(result);
			long endTime = System.currentTimeMillis();
			long time = (endTime - beginTime) / 1000;
			String info = "生成部门结果,部门:" + name + "|id:" + id + "|result:" + result + "|messageStatus:" + messageStatus + "|耗时:" + time + "秒";
			LOGGER.info(info);
			System.out.println(info);

		} catch (Exception e) {
			messageInfo = e.getMessage();
			success = false;
			t.setIsFlag(new Short("3"));
			LOGGER.error("生成部门异常,部门:" + name + "|id:" + id, e);
		}finally {
			try {
				orgDepartmentTempManager.updatOrgDepartmentTemp(t);
				if (messageStatus!=null) {
					addLog(messageStatus.toString(), resourceId, id, name, "DEPARTMENT", success);
				}else {
					addLog(messageInfo, resourceId, id, name, "DEPARTMENT", success);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
	
	private V3xOrgDepartment initDep(OrgDepartmentTemp t) {
		V3xOrgDepartment department = new V3xOrgDepartment();
		//Map<String , Object> map = new HashMap<String, Object>();
		String unitId = t.getUnitId();
		OrgUnitTemp unitTemp = orgUnitTempManager.findOrgUnitTempById(unitId);
		String g6UnitId = unitId;
		if (unitTemp != null) {
			g6UnitId = unitTemp.getObjectId();
		}
		department.setOrgAccountId(Long.valueOf(g6UnitId));
		department.setId(Long.valueOf(t.getObjectId()));
		/*String code = t.getCode();
		if (Strings.isNullOrEmpty(code)) {
			department.setCode(t.getId());
		}else {
			department.setCode(code);
		}*/
		department.setCode(t.getId());
		department.setName(t.getName());
		department.setCreateDeptSpace(false);
		
		//String superDepartment = "";
		Short grade = t.getGrade();
		//一级部门
		if (1 == grade) {
			department.setSuperior(Long.valueOf(g6UnitId));
		}else {
			department.setSuperior(Long.valueOf(t.getParentId()));
		}
		
		//map.put("superDepartment", superDepartment);//上级部门
		Integer isEnable = t.getIsEnable();
		Integer delFlag = t.getDelFlag();
		if (isEnable == 0) {
			department.setEnabled(true);
		} else {
			department.setEnabled(false);
		}
		
		if (delFlag == 0) {
			department.setIsDeleted(false);
		} else {
			department.setIsDeleted(true);
		}
		department.setSortId(Long.valueOf(t.getSortId()));
		department.setSortIdType("0");//0序号可重复，1序号不可重复
		department.setResourceId(t.getResourceId());
		return department;
	}


	public OrgDepartmentTempManager getOrgDepartmentTempManager() {
		return orgDepartmentTempManager;
	}

	public void setOrgDepartmentTempManager(OrgDepartmentTempManager orgDepartmentTempManager) {
		this.orgDepartmentTempManager = orgDepartmentTempManager;
	}

	public OrgUnitTempManager getOrgUnitTempManager() {
		return orgUnitTempManager;
	}

	public void setOrgUnitTempManager(OrgUnitTempManager orgUnitTempManager) {
		this.orgUnitTempManager = orgUnitTempManager;
	}

	
}
