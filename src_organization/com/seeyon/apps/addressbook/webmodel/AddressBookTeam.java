package com.seeyon.apps.addressbook.webmodel;

/**
 * 组对象
 * @author wf
 *
 */
public class AddressBookTeam {
	public AddressBookTeam(){
		this.T = "Team";
	}
	/**
	 * id
	 */
	private Long I;
	/**
	 * 所属单位id
	 */
	private Long AId;
	/**
	 * 名称
	 */
	private String N;
	/**
	 * 总人数
	 */
	private Integer Nm;
	/**
	 * 类型
	 */
	private String T;
	public Long getI() {
		return I;
	}
	public void setI(Long i) {
		I = i;
	}
	public Long getAId() {
		return AId;
	}
	public void setAId(Long aId) {
		AId = aId;
	}
	public String getN() {
		return N;
	}
	public void setN(String n) {
		N = n;
	}
	public Integer getNm() {
		return Nm;
	}
	public void setNm(Integer nm) {
		Nm = nm;
	}
	public String getT() {
		return T;
	}
	public void setT(String t) {
		T = t;
	}
	
}
