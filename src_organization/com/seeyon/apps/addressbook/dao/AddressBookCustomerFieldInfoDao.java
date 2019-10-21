package com.seeyon.apps.addressbook.dao;

import java.util.Map;

import com.seeyon.apps.addressbook.po.AddressBook;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.FlipInfo;
/**
 * 自定义通讯录人员信息
 * @author wff
 *
 */
public interface AddressBookCustomerFieldInfoDao {

	/**
	 * 查询所有符合条件的通讯录信息
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
	AddressBook getById(Long id) throws BusinessException;

	/**
	 * 保存
	 * @param addressBookFieldSet
	 */
	void save(AddressBook addressBook);

	/**
	 * 更新
	 * @param addressBookFieldSet
	 */
	void update(AddressBook addressBook);

	/**
	 * 根据人员id查找
	 * @param memberId
	 * @return
	 */
	AddressBook getByMemberId(Long memberId);

	/**
	 * 清空被删除字段中的信息
	 * @param disabledLabels
	 */
	void updateAddressBookEmpty(String[] disabledLabels);

	

}
