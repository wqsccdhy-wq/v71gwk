package com.seeyon.apps.addressbook.dao;

import java.util.List;

import com.seeyon.apps.addressbook.po.AddressBookSet;
import com.seeyon.ctp.common.dao.BaseHibernateDao;

public class AddressBookSetDaoImpl extends BaseHibernateDao<AddressBookSet> implements AddressBookSetDao {

    @Override
    public List<AddressBookSet> findAll() {
        return this.getAll();
    }

    @Override
    public AddressBookSet findByAccountId(Long accountId) {
        return this.findUniqueBy("accountId", accountId);
    }

}
