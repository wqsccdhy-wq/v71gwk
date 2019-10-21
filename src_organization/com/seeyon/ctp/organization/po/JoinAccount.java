package com.seeyon.ctp.organization.po;

import java.util.HashMap;
import java.util.Map;

import com.seeyon.ctp.common.po.BasePO;

/**
 * v-join平台，外单位自定义属性对象
 * @author wf
 *
 */
public class JoinAccount extends BasePO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * v-join 虚拟单位
	 */
	private Long orgAccountId;
	/**
	 * v-join 平台  外单位Id;
	 */
	private Long departmentId;
	/**
	 * 创建时间
	 */
	private java.util.Date createDate;
	/**
	 * 更新时间
	 */
	private java.util.Date updateDate;

	/**
	 * 预留字段 文本类型
	 */
	private java.lang.String extAttr1;
	private java.lang.String extAttr2;
	private java.lang.String extAttr3;
	private java.lang.String extAttr4;
	private java.lang.String extAttr5;
	private java.lang.String extAttr6;
	private java.lang.String extAttr7;
	private java.lang.String extAttr8;
	private java.lang.String extAttr9;
	private java.lang.String extAttr10;

	/**
	 * 预留字段 数字型
	 */
	private java.lang.Double extAttr11;
	private java.lang.Double extAttr12;
	private java.lang.Double extAttr13;
	private java.lang.Double extAttr14;
	private java.lang.Double extAttr15;
	private java.lang.Double extAttr16;
	private java.lang.Double extAttr17;
	private java.lang.Double extAttr18;
	private java.lang.Double extAttr19;
	private java.lang.Double extAttr20;

	/**
	 * 预留字段 日期型
	 */
	private java.util.Date extAttr21;
	private java.util.Date extAttr22;
	private java.util.Date extAttr23;
	private java.util.Date extAttr24;
	private java.util.Date extAttr25;
	private java.util.Date extAttr26;
	private java.util.Date extAttr27;
	private java.util.Date extAttr28;
	private java.util.Date extAttr29;
	private java.util.Date extAttr30;
	
	
	/**
	 * 预留字段 枚举类型  （对应的枚举类型id）
	 */
	private String extAttr31;
	private String extAttr32;
	private String extAttr33;
	private String extAttr34;
	private String extAttr35;
	private String extAttr36;
	private String extAttr37;
	private String extAttr38;
	private String extAttr39;
	private String extAttr40;
	/**
	 * 返回所有 key: EXT_ATTR_1...30
	 * @return
	 */
	public Map<String, Object> getExtAttrMap(){
	    Map<String, Object> m = new HashMap<String, Object>();
	    m.put("EXT_ATTR_1", this.getExtAttr1());
	    m.put("EXT_ATTR_2", this.getExtAttr2());
	    m.put("EXT_ATTR_3", this.getExtAttr3());
	    m.put("EXT_ATTR_4", this.getExtAttr4());
	    m.put("EXT_ATTR_5", this.getExtAttr5());
	    m.put("EXT_ATTR_6", this.getExtAttr6());
	    m.put("EXT_ATTR_7", this.getExtAttr7());
	    m.put("EXT_ATTR_8", this.getExtAttr8());
	    m.put("EXT_ATTR_9", this.getExtAttr9());
	    m.put("EXT_ATTR_10", this.getExtAttr10());
	    m.put("EXT_ATTR_11", this.getExtAttr11());
	    m.put("EXT_ATTR_12", this.getExtAttr12());
	    m.put("EXT_ATTR_13", this.getExtAttr13());
	    m.put("EXT_ATTR_14", this.getExtAttr14());
	    m.put("EXT_ATTR_15", this.getExtAttr15());
	    m.put("EXT_ATTR_16", this.getExtAttr16());
	    m.put("EXT_ATTR_17", this.getExtAttr17());
	    m.put("EXT_ATTR_18", this.getExtAttr18());
	    m.put("EXT_ATTR_19", this.getExtAttr19());
	    m.put("EXT_ATTR_20", this.getExtAttr20());
	    m.put("EXT_ATTR_21", this.getExtAttr21());
	    m.put("EXT_ATTR_22", this.getExtAttr22());
	    m.put("EXT_ATTR_23", this.getExtAttr23());
	    m.put("EXT_ATTR_24", this.getExtAttr24());
	    m.put("EXT_ATTR_25", this.getExtAttr25());
	    m.put("EXT_ATTR_26", this.getExtAttr26());
	    m.put("EXT_ATTR_27", this.getExtAttr27());
	    m.put("EXT_ATTR_28", this.getExtAttr28());
	    m.put("EXT_ATTR_29", this.getExtAttr29());
	    m.put("EXT_ATTR_30", this.getExtAttr30());
	    m.put("EXT_ATTR_31", this.getExtAttr31());
	    m.put("EXT_ATTR_32", this.getExtAttr32());
	    m.put("EXT_ATTR_33", this.getExtAttr33());
	    m.put("EXT_ATTR_34", this.getExtAttr34());
	    m.put("EXT_ATTR_35", this.getExtAttr35());
	    m.put("EXT_ATTR_36", this.getExtAttr36());
	    m.put("EXT_ATTR_37", this.getExtAttr37());
	    m.put("EXT_ATTR_38", this.getExtAttr38());
	    m.put("EXT_ATTR_39", this.getExtAttr39());
	    m.put("EXT_ATTR_40", this.getExtAttr40());
	    
	    return m;
	}
	
	public Long getOrgAccountId() {
		return orgAccountId;
	}
	public void setOrgAccountId(Long orgAccountId) {
		this.orgAccountId = orgAccountId;
	}
	public Long getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	public java.util.Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}
	public java.util.Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(java.util.Date updateDate) {
		this.updateDate = updateDate;
	}
	public java.lang.String getExtAttr1() {
		return extAttr1;
	}
	public void setExtAttr1(java.lang.String extAttr1) {
		this.extAttr1 = extAttr1;
	}
	public java.lang.String getExtAttr2() {
		return extAttr2;
	}
	public void setExtAttr2(java.lang.String extAttr2) {
		this.extAttr2 = extAttr2;
	}
	public java.lang.String getExtAttr3() {
		return extAttr3;
	}
	public void setExtAttr3(java.lang.String extAttr3) {
		this.extAttr3 = extAttr3;
	}
	public java.lang.String getExtAttr4() {
		return extAttr4;
	}
	public void setExtAttr4(java.lang.String extAttr4) {
		this.extAttr4 = extAttr4;
	}
	public java.lang.String getExtAttr5() {
		return extAttr5;
	}
	public void setExtAttr5(java.lang.String extAttr5) {
		this.extAttr5 = extAttr5;
	}
	public java.lang.String getExtAttr6() {
		return extAttr6;
	}
	public void setExtAttr6(java.lang.String extAttr6) {
		this.extAttr6 = extAttr6;
	}
	public java.lang.String getExtAttr7() {
		return extAttr7;
	}
	public void setExtAttr7(java.lang.String extAttr7) {
		this.extAttr7 = extAttr7;
	}
	public java.lang.String getExtAttr8() {
		return extAttr8;
	}
	public void setExtAttr8(java.lang.String extAttr8) {
		this.extAttr8 = extAttr8;
	}
	public java.lang.String getExtAttr9() {
		return extAttr9;
	}
	public void setExtAttr9(java.lang.String extAttr9) {
		this.extAttr9 = extAttr9;
	}
	public java.lang.String getExtAttr10() {
		return extAttr10;
	}
	public void setExtAttr10(java.lang.String extAttr10) {
		this.extAttr10 = extAttr10;
	}
	public java.lang.Double getExtAttr11() {
		return extAttr11;
	}
	public void setExtAttr11(java.lang.Double extAttr11) {
		this.extAttr11 = extAttr11;
	}
	public java.lang.Double getExtAttr12() {
		return extAttr12;
	}
	public void setExtAttr12(java.lang.Double extAttr12) {
		this.extAttr12 = extAttr12;
	}
	public java.lang.Double getExtAttr13() {
		return extAttr13;
	}
	public void setExtAttr13(java.lang.Double extAttr13) {
		this.extAttr13 = extAttr13;
	}
	public java.lang.Double getExtAttr14() {
		return extAttr14;
	}
	public void setExtAttr14(java.lang.Double extAttr14) {
		this.extAttr14 = extAttr14;
	}
	public java.lang.Double getExtAttr15() {
		return extAttr15;
	}
	public void setExtAttr15(java.lang.Double extAttr15) {
		this.extAttr15 = extAttr15;
	}
	public java.lang.Double getExtAttr16() {
		return extAttr16;
	}
	public void setExtAttr16(java.lang.Double extAttr16) {
		this.extAttr16 = extAttr16;
	}
	public java.lang.Double getExtAttr17() {
		return extAttr17;
	}
	public void setExtAttr17(java.lang.Double extAttr17) {
		this.extAttr17 = extAttr17;
	}
	public java.lang.Double getExtAttr18() {
		return extAttr18;
	}
	public void setExtAttr18(java.lang.Double extAttr18) {
		this.extAttr18 = extAttr18;
	}
	public java.lang.Double getExtAttr19() {
		return extAttr19;
	}
	public void setExtAttr19(java.lang.Double extAttr19) {
		this.extAttr19 = extAttr19;
	}
	public java.lang.Double getExtAttr20() {
		return extAttr20;
	}
	public void setExtAttr20(java.lang.Double extAttr20) {
		this.extAttr20 = extAttr20;
	}
	public java.util.Date getExtAttr21() {
		return extAttr21;
	}
	public void setExtAttr21(java.util.Date extAttr21) {
		this.extAttr21 = extAttr21;
	}
	public java.util.Date getExtAttr22() {
		return extAttr22;
	}
	public void setExtAttr22(java.util.Date extAttr22) {
		this.extAttr22 = extAttr22;
	}
	public java.util.Date getExtAttr23() {
		return extAttr23;
	}
	public void setExtAttr23(java.util.Date extAttr23) {
		this.extAttr23 = extAttr23;
	}
	public java.util.Date getExtAttr24() {
		return extAttr24;
	}
	public void setExtAttr24(java.util.Date extAttr24) {
		this.extAttr24 = extAttr24;
	}
	public java.util.Date getExtAttr25() {
		return extAttr25;
	}
	public void setExtAttr25(java.util.Date extAttr25) {
		this.extAttr25 = extAttr25;
	}
	public java.util.Date getExtAttr26() {
		return extAttr26;
	}
	public void setExtAttr26(java.util.Date extAttr26) {
		this.extAttr26 = extAttr26;
	}
	public java.util.Date getExtAttr27() {
		return extAttr27;
	}
	public void setExtAttr27(java.util.Date extAttr27) {
		this.extAttr27 = extAttr27;
	}
	public java.util.Date getExtAttr28() {
		return extAttr28;
	}
	public void setExtAttr28(java.util.Date extAttr28) {
		this.extAttr28 = extAttr28;
	}
	public java.util.Date getExtAttr29() {
		return extAttr29;
	}
	public void setExtAttr29(java.util.Date extAttr29) {
		this.extAttr29 = extAttr29;
	}
	public java.util.Date getExtAttr30() {
		return extAttr30;
	}
	public void setExtAttr30(java.util.Date extAttr30) {
		this.extAttr30 = extAttr30;
	}
	public String getExtAttr31() {
		return extAttr31;
	}
	public void setExtAttr31(String extAttr31) {
		this.extAttr31 = extAttr31;
	}
	public String getExtAttr32() {
		return extAttr32;
	}
	public void setExtAttr32(String extAttr32) {
		this.extAttr32 = extAttr32;
	}
	public String getExtAttr33() {
		return extAttr33;
	}
	public void setExtAttr33(String extAttr33) {
		this.extAttr33 = extAttr33;
	}
	public String getExtAttr34() {
		return extAttr34;
	}
	public void setExtAttr34(String extAttr34) {
		this.extAttr34 = extAttr34;
	}
	public String getExtAttr35() {
		return extAttr35;
	}
	public void setExtAttr35(String extAttr35) {
		this.extAttr35 = extAttr35;
	}
	public String getExtAttr36() {
		return extAttr36;
	}
	public void setExtAttr36(String extAttr36) {
		this.extAttr36 = extAttr36;
	}
	public String getExtAttr37() {
		return extAttr37;
	}
	public void setExtAttr37(String extAttr37) {
		this.extAttr37 = extAttr37;
	}
	public String getExtAttr38() {
		return extAttr38;
	}
	public void setExtAttr38(String extAttr38) {
		this.extAttr38 = extAttr38;
	}
	public String getExtAttr39() {
		return extAttr39;
	}
	public void setExtAttr39(String extAttr39) {
		this.extAttr39 = extAttr39;
	}
	public String getExtAttr40() {
		return extAttr40;
	}
	public void setExtAttr40(String extAttr40) {
		this.extAttr40 = extAttr40;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}