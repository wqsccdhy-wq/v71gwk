package com.seeyon.ctp.organization.selectpeople.manager;

import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_NAME;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_id;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_parentId;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;

import com.seeyon.apps.addressbook.manager.AddressBookManager;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.common.metadata.bo.MetadataColumnBO;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.Strings;

public class SelectPeoplePanel4MemberMetadataTagImpl extends AbstractSelectPeoplePanel {
    private static final Log      LOGGER                     = CtpLogFactory.getLog(SelectPeoplePanel4MemberMetadataTagImpl.class);

    private OrgManager            orgManager;
    private EnumManager           enumManagerNew;
    private AddressBookManager addressBookManager;

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setEnumManagerNew(EnumManager enumManagerNew) {
        this.enumManagerNew = enumManagerNew;
    }
	
	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}

	@Override
    public String getType() {
        return V3xOrgEntity.ORGENT_TYPE_MEMBER_METADATATAG;
    }

    @Override
    public Date getLastModifyTimestamp(Long accountId) throws BusinessException {
        return null;
    }

    @Override
    public String getJsonString(long memberId, long accountId, String extParameters) throws BusinessException {
        StringBuilder a = new StringBuilder();
        a.append("[");

        V3xOrgAccount account = orgManager.getAccountById(accountId);
        if (account != null && account.getIsInternal()) {
            a.append("{");
            a.append(TOXML_PROPERTY_id).append(":\"").append(account.getId()).append("\"").append(",");
            a.append(TOXML_PROPERTY_parentId).append(":\"-1\"").append(",");
            a.append(TOXML_PROPERTY_NAME).append(":\"").append(ResourceUtil.getString("org.member.metadatatag.label")).append("\"");
            a.append("}");
            
            List<MetadataColumnBO> metadataColumnList=addressBookManager.getCustomerAddressBookList();

            for (MetadataColumnBO bo : metadataColumnList) {
            	Integer type = bo.getType();
            	if(type == 3 && bo.getIsShowinWorkflow() == 1){//枚举
            		Long enumId = bo.getEnumId();
            		if (enumId == null) {
            			continue;
            		}
/*            		List<CtpEnumItem> itemList = new ArrayList<CtpEnumItem>();
            		if ("first".equals(enumLevel)) {//第一级枚举值
            			itemList = enumManagerNew.getCtpEnumItem(enumId, 0);
            		} else if ("last".equals(enumLevel)) {//末级枚举值
            			itemList = enumManagerNew.getLastCtpEnumItem(enumId);
            		}*/
            		
            		List<CtpEnumItem> itemList = enumManagerNew.getEmumItemByEmumId(enumId);
            		Collections.sort(itemList, new Comparator<CtpEnumItem>() {
            			public int compare(CtpEnumItem item1, CtpEnumItem item2) {
        					Long sort1 = item1.getSortnumber();
        					Long sort2 = item2.getSortnumber();
        					return sort1 > sort2 ? 1 : (sort1 < sort2 ? -1 : 0);
            			}
            		});
            		if (Strings.isNotEmpty(itemList)) {
            			a.append(",");
            			a.append("{");
            			a.append(TOXML_PROPERTY_id).append(":\"").append(bo.getId()).append("\"").append(",");
            			a.append(TOXML_PROPERTY_parentId).append(":\"").append(account.getId()).append("\"").append(",");
            			a.append(TOXML_PROPERTY_NAME).append(":\"").append(bo.getLabel()).append("\"");
            			a.append("}");
            			
            			for (CtpEnumItem ctpEnumItem : itemList) {
            				a.append(",");
            				a.append("{");
            				a.append(TOXML_PROPERTY_id).append(":\"").append(ctpEnumItem.getId()).append("\"").append(",");
            				if(ctpEnumItem.getLevelNum() == 0){
            					a.append(TOXML_PROPERTY_parentId).append(":\"").append(bo.getId()).append("\"").append(",");
            				}else{
            					a.append(TOXML_PROPERTY_parentId).append(":\"").append(ctpEnumItem.getParentId()).append("\"").append(",");
            				}
            				a.append(TOXML_PROPERTY_NAME).append(":\"").append(ctpEnumItem.getLabel()).append("\"");
            				a.append("}");
            			}
            		}
            	}
            }
        }

        a.append("]");

        return a.toString();
    }

}
