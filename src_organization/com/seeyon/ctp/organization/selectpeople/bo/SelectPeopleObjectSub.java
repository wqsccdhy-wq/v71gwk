package com.seeyon.ctp.organization.selectpeople.bo;

/**
 * 自定义选人界面子数据对象
 * @author wf
 *
 */
public class SelectPeopleObjectSub {

	/**
	 * 子数据的数据类型
	 */
	String T;
	/**
	 * 子数据的唯一性主键   如 id、角色code:DeptManager 等
	 */
	String K;
	/**
	 * 显示名称
	 */
	String N;
	public String getT() {
		return T;
	}
	public void setT(String t) {
		T = t;
	}
	public String getK() {
		return K;
	}
	public void setK(String k) {
		K = k;
	}
	public String getN() {
		return N;
	}
	public void setN(String n) {
		N = n;
	}
	
}
