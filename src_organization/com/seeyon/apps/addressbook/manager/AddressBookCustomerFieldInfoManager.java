package com.seeyon.apps.addressbook.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.apps.addressbook.po.AddressBook;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.FlipInfo;

/**
 * 自定义通讯录字段信息
 * @author wff
 *
 */
public interface AddressBookCustomerFieldInfoManager {

    /**
     * 通讯录字段设置列表
     * @param fi
     * @param params
     * @return
     * @throws BusinessException
     */
    FlipInfo getAll(final FlipInfo fi, final Map params) throws BusinessException;

	/**
	 * 添加一条设置信息
	 * @param map
	 * @throws BusinessException
	 */
	void addAddressBook(AddressBook addressBook) throws BusinessException;
	
	/**
	 * 
	 * 修改一条设置信息
	 * @param map
	 * @throws BusinessException
	 */
	void updateAddressBook(AddressBook addressBook) throws BusinessException;

	/**
	 * 批量导入添加通讯录信息
	 * @param sddressBookCustomerFieldInfos
	 */
	void addAddressBooks(List<AddressBook> addressBooks);

	/**
	 * 根据人员id查找
	 * @param memberId
	 * @return
	 * @throws BusinessException
	 */
	AddressBook getByMemberId(Long memberId) throws BusinessException;

	/**
	 * 清空已经被删除的字段的信息
	 * @param disabledLabels
	 * @throws BusinessException
	 */
	void updateAddressBookEmpty(String[] disabledLabels) throws BusinessException;

	/**
	 * 根据单位id，获取所有单位下的人员元数据信息（包含兼职人员）
	 * @param accountId null:全集团
	 * @return
	 */
	List<AddressBook> getAllAddressbookinfos(Long accountId);


}