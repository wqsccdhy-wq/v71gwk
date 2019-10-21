package com.seeyon.ctp.organization.dao;

import java.util.Map;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.po.JoinAccount;
import com.seeyon.ctp.util.FlipInfo;

/**
 * v-join 外单位自定义属性
 * @author wf
 *
 */
public interface JoinAccountCustomerFieldInfoDao {

	/**
	 * 查询所有符合条件的单位属性
	 * @param fi
	 * @param params
	 * @return
	 */
	FlipInfo getAll(FlipInfo fi, Map params);

	/**
	 * 根据id查找对象
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	JoinAccount getById(Long id) throws BusinessException;

	/**
	 * 保存
	 */
	void save(JoinAccount joinAccount);

	/**
	 * 更新
	 */
	void update(JoinAccount joinAccount);
	
	/**
	 * 根据外单位id查找
	 * @param departmentId
	 */
	JoinAccount getByDepartmentId(Long departmentId);

	/**
	 * 清空被删除字段中的信息
	 * @param disabledLabels
	 */
	void updateJoinAccountEmpty(String[] disabledLabels, Long orgAccountId);

}
