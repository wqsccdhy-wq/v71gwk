package com.seeyon.apps.addressbook.dao;

import java.util.List;

import com.seeyon.apps.addressbook.po.AddressBookSet;
import com.seeyon.ctp.common.dao.CTPBaseHibernateDao;

public interface AddressBookSetDao extends CTPBaseHibernateDao<AddressBookSet> {

    public List<AddressBookSet> findAll();

    public AddressBookSet findByAccountId(Long accountId);

}
