package com.seeyon.apps.addressbook.dao;

import java.util.List;

import com.seeyon.apps.addressbook.po.AddressBookSet;
import com.seeyon.apps.addressbook.po.AddressBookSetScope;
import com.seeyon.ctp.common.dao.CTPBaseHibernateDao;

public interface AddressBookSetScopeDao extends CTPBaseHibernateDao<AddressBookSetScope> {

    public List<AddressBookSetScope> findScopeByAddressBookSetId(Long addressBookSetId);

    public List<AddressBookSetScope> saveScope(AddressBookSet bean, boolean isNew);

}
