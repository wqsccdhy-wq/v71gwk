package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.Constants.login_useragent_from;
import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgMember;

/**
 * 人员修改密码事件
 * @author lilong
 *
 */
public class ChangePwdEvent extends Event {

    /**
     * 
     */
    private static final long serialVersionUID = 8694539077207227889L;
    private V3xOrgMember member;
    private final login_useragent_from userAgentFromEnum;
    /**
     * 修改密码是由哪一个登录发起的。
     * @return 修改密码的用户登录的来源。
     */
	public login_useragent_from getUserAgentFromEnum() {
		return userAgentFromEnum;
	}

	public ChangePwdEvent(Object source) {
        super(source);
        User user = AppContext.getCurrentUser();
        if(user!=null){
        	userAgentFromEnum = user.getUserAgentFromEnum();
        }else{
        	userAgentFromEnum = login_useragent_from.other;
        }
    }

    public V3xOrgMember getMember() {
        return member;
    }

    public void setMember(V3xOrgMember member) {
        this.member = member;
    }

}
