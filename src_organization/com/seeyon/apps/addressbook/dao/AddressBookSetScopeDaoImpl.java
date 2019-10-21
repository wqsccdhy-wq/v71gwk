package com.seeyon.apps.addressbook.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.seeyon.apps.addressbook.constants.AddressbookConstants;
import com.seeyon.apps.addressbook.po.AddressBookSet;
import com.seeyon.apps.addressbook.po.AddressBookSetScope;
import com.seeyon.ctp.common.dao.BaseHibernateDao;
import com.seeyon.ctp.util.Strings;

public class AddressBookSetScopeDaoImpl extends BaseHibernateDao<AddressBookSetScope> implements AddressBookSetScopeDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<AddressBookSetScope> findScopeByAddressBookSetId(Long addressBookSetId) {
        String hql = " from " + AddressBookSetScope.class.getName() + " as scope where scope.addressbookSetId=?";
        return this.find(hql, -1, -1, null, addressBookSetId);
    }

    @Override
    public List<AddressBookSetScope> saveScope(AddressBookSet bean, boolean isNew) {
        if (!isNew) {
            this.delete(new Object[][] { { "addressbookSetId", bean.getId() } });
        }

        List<AddressBookSetScope> scopes = new ArrayList<AddressBookSetScope>();

        String viewScopeIds = bean.getViewScopeIds();
        String[][] viewScopes = Strings.getSelectPeopleElements(viewScopeIds);
        if (viewScopes != null && viewScopes.length > 0) {
            for (int i = 0; i < viewScopes.length; i++) {
                AddressBookSetScope scope = new AddressBookSetScope(bean.getId(), AddressbookConstants.ADDRESSBOOK_SETTYPE_VIEWSCOPE, NumberUtils.toLong(viewScopes[i][1]), viewScopes[i][0]);
                scopes.add(scope);
            }
        }

        String keyInfoIds = bean.getKeyInfoIds();
        String[][] keyInfos = Strings.getSelectPeopleElements(keyInfoIds);
        if (keyInfos != null && keyInfos.length > 0) {
            for (int i = 0; i < keyInfos.length; i++) {
                AddressBookSetScope scope = new AddressBookSetScope(bean.getId(), AddressbookConstants.ADDRESSBOOK_SETTYPE_KEYINFO, NumberUtils.toLong(keyInfos[i][1]), keyInfos[i][0]);
                scopes.add(scope);
            }
        }

        String exportPrintIds = bean.getExportPrintIds();
        String[][] exportPrints = Strings.getSelectPeopleElements(exportPrintIds);
        if (exportPrints != null && exportPrints.length > 0) {
            for (int i = 0; i < exportPrints.length; i++) {
                AddressBookSetScope scope = new AddressBookSetScope(bean.getId(), AddressbookConstants.ADDRESSBOOK_SETTYPE_EXPORTPRINT, NumberUtils.toLong(exportPrints[i][1]), exportPrints[i][0]);
                scopes.add(scope);
            }
        }

        if (CollectionUtils.isNotEmpty(scopes)) {
            this.savePatchAll(scopes);
        }

        return scopes;
    }

}
