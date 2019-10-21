package com.seeyon.ctp.organization.event;
/**
 * 人员跨单位调整事件。
 * @author wangwenyou
 *
 */
public class MemberAccountChangeEvent extends UpdateMemberEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7655160668133823455L;
	private long srcAccount;
	private long targetAccount;
	public MemberAccountChangeEvent(Object source) {
		super(source);
	}
	/**
	 * 调整前的单位Id。
	 * @return
	 */
	public long getOldAccount() {
		return srcAccount;
	}
	public void setOldAccount(long srcAccount) {
		this.srcAccount = srcAccount;
	}
	/**
	 * 调整后的单位Id。
	 * @return
	 */
	public long getAccount() {
		return targetAccount;
	}
	public void setAccount(long targetAccount) {
		this.targetAccount = targetAccount;
	}
}
