/**
 * $Author: $
 * $Rev: $
 * $Date:: $
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.apps.addressbook.listener;

import java.util.List;

import com.seeyon.apps.addressbook.manager.AddressBookCustomerFieldInfoManager;
import com.seeyon.apps.addressbook.manager.AddressBookManager;
import com.seeyon.apps.addressbook.po.AddressBookSet;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.metadata.event.MetadataColumnChangedEvent;
import com.seeyon.ctp.common.metadata.event.MetadataColumnDisableEvent;
import com.seeyon.ctp.common.po.metadata.CtpMetadataColumn;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.ListenEvent;

/**
 * @author wff
 *
 */
public class AddressBookSetEventListener {
    private OrgManager orgManager;
    private AddressBookManager addressBookManager;
    private AddressBookCustomerFieldInfoManager addressBookCustomerFieldInfoManager;

    public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public AddressBookManager getAddressBookManager() {
		return addressBookManager;
	}

	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}

	public void setAddressBookCustomerFieldInfoManager(
			AddressBookCustomerFieldInfoManager addressBookCustomerFieldInfoManager) {
		this.addressBookCustomerFieldInfoManager = addressBookCustomerFieldInfoManager;
	}

	@ListenEvent(event = MetadataColumnDisableEvent.class)
    public void onMetadataColumnDisableEvent(MetadataColumnDisableEvent evt) throws Exception {
		//删除通讯录设置里的字段
		if(null!=evt.getCtpMetadataColumn()){
			CtpMetadataColumn ctpMetadataColumn=evt.getCtpMetadataColumn();
			updateAddSet(ctpMetadataColumn.getId());
		}
		//清空自定义通讯录信息表里对应的信息
		if(null!=evt.getDisabledLabels() && evt.getDisabledLabels().length>0){
			String[] disabledLables=evt.getDisabledLabels();
			addressBookCustomerFieldInfoManager.updateAddressBookEmpty(disabledLables);
		}
    }
    
    private void updateAddSet(Long disableFieldId) throws BusinessException {
        List<V3xOrgAccount> accounts=orgManager.getAllAccounts();

        AddressBookSet bean=null;
        for(V3xOrgAccount account : accounts){
        	 bean = addressBookManager.getAddressbookSetByAccountId(account.getId());
        	 if(null==bean){
        		 continue;
        	 }
        	 boolean isNew = false;
        	 String displayColumn = bean.getDisplayColumn();
        	 displayColumn=displayColumn.replace(","+disableFieldId, "");
        	 bean.setDisplayColumn(displayColumn);
        	 
        	 addressBookManager.saveAddressbookSet(bean, isNew);
        }

    }
    
	@ListenEvent(event = MetadataColumnChangedEvent.class)
    public void onMetadataColumnChangedEvent(MetadataColumnChangedEvent evt) throws Exception {
		//删除通讯录设置里的字段
		if(null!=evt.getCtpMetadataColumn()){
			CtpMetadataColumn ctpMetadataColumn=evt.getCtpMetadataColumn();
			if(ctpMetadataColumn.getIsShowinPersoncard() != 1) {
				updateAddSet(ctpMetadataColumn.getId());
			}
		}
    }
}
