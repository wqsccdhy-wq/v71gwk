/**
 *
 */
package com.seeyon.ctp.organization.memberleave.bo;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.ctp.organization.dao.OrgHelper;

/**
 * @author tanmf
 *
 */
public class MemberLeavePendingData implements Serializable {

    private static final long serialVersionUID = -9054581752553182906L;

    private String subject;

    private long senderId;

    private Date sendDate;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getSenderName(){
        return OrgHelper.showMemberName(senderId);
    }

}
