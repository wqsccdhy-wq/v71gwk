package com.seeyon.apps.addressbook.webmodel;

/**
 * 人员的对象
 * 包含主、副、兼单位部门岗位信息
 * @author wf
 *
 */
public class AddressBookMember {
	
	public AddressBookMember(){
		this.T = "Member";
	}
	/**
	 * id
	 */
	private Long I;
	/**
	 * 名称
	 */
	private String N;
	/**
	 * 所属单位id
	 */
	private Long AId;
	/**
	 * 所属部门id
	 */
	private Long DId;
	/**
	 * 所属部门名称
	 */
	private String DN;
	/**
	 * 所属岗位id
	 */
	private Long PId;
	/**
	 * 所属岗位名称
	 */
	private String PN;
	/**
	 * 类型 
	 * @return
	 */
	private String T;
	/**
	 * 人员头像
	 */
	private String Img;
	
	/**
	 * 手机号码
	 */
	private String tNm;
	/**
	 * 办公电话（单位电话）
	 */
	private String oNm;
	
	/**
	 * @return the oNm
	 */
	public String getoNm() {
		return oNm;
	}
	/**
	 * @param oNm the oNm to set
	 */
	public void setoNm(String oNm) {
		this.oNm = oNm;
	}
	public Long getI() {
		return I;
	}
	public void setI(Long i) {
		I = i;
	}
	public String getN() {
		return N;
	}
	public void setN(String n) {
		N = n;
	}
	public Long getAId() {
		return AId;
	}
	public void setAId(Long aId) {
		AId = aId;
	}
	public Long getDId() {
		return DId;
	}
	public void setDId(Long dId) {
		DId = dId;
	}
	public String getDN() {
		return DN;
	}
	public void setDN(String dN) {
		DN = dN;
	}
	public Long getPId() {
		return PId;
	}
	public void setPId(Long pId) {
		PId = pId;
	}
	public String getPN() {
		return PN;
	}
	public void setPN(String pN) {
		PN = pN;
	}
	public String getT() {
		return T;
	}
	public void setT(String t) {
		T = t;
	}
	public String getImg() {
		return Img;
	}
	public void setImg(String img) {
		Img = img;
	}
	public String gettNm() {
		return tNm;
	}
	public void settNm(String tNm) {
		this.tNm = tNm;
	}
	

}
