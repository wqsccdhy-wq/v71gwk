package com.seeyon.apps.addressbook.po;

import com.seeyon.ctp.common.po.BasePO;

public class AddressBookSetScope extends BasePO {

    private static final long serialVersionUID = 3946903521350709214L;

    private Long              addressbookSetId;                       //通讯录设置ID
    private Integer           addressbookSetType;                     //通讯录设置类型
    private Long              userId;                                 //组织模型实体ID
    private String            userType;                               //组织模型实体类型

    public AddressBookSetScope() {

    }

    public AddressBookSetScope(Long addressbookSetId, Integer addressbookSetType, Long userId, String userType) {
        super();
        this.setIdIfNew();
        this.addressbookSetId = addressbookSetId;
        this.addressbookSetType = addressbookSetType;
        this.userId = userId;
        this.userType = userType;
    }

    public Long getAddressbookSetId() {
        return addressbookSetId;
    }

    public void setAddressbookSetId(Long addressbookSetId) {
        this.addressbookSetId = addressbookSetId;
    }

    public Integer getAddressbookSetType() {
        return addressbookSetType;
    }

    public void setAddressbookSetType(Integer addressbookSetType) {
        this.addressbookSetType = addressbookSetType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
