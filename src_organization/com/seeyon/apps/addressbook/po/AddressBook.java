package com.seeyon.apps.addressbook.po;

import java.util.HashMap;
import java.util.Map;

import com.seeyon.ctp.common.po.BasePO;
import com.seeyon.ctp.util.Strings;

public class AddressBook extends BasePO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 人员id
	 */
	private Long memberId;
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
	 * 预留字段 枚举型
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
	 * 预留字段 选人
	 */
	private String extAttr41;
	private String extAttr42;
	private String extAttr43;
	private String extAttr44;
	private String extAttr45;
	private String extAttr46;
	private String extAttr47;
	private String extAttr48;
	private String extAttr49;
	private String extAttr50;
	
	/**
	 * 预留字段 选部门
	 */
	private String extAttr51;
	private String extAttr52;
	private String extAttr53;
	private String extAttr54;
	private String extAttr55;
	private String extAttr56;
	private String extAttr57;
	private String extAttr58;
	private String extAttr59;
	private String extAttr60;
	
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
	    m.put("EXT_ATTR_41", this.getExtAttr41());
	    m.put("EXT_ATTR_42", this.getExtAttr42());
	    m.put("EXT_ATTR_43", this.getExtAttr43());
	    m.put("EXT_ATTR_44", this.getExtAttr44());
	    m.put("EXT_ATTR_45", this.getExtAttr45());
	    m.put("EXT_ATTR_46", this.getExtAttr46());
	    m.put("EXT_ATTR_47", this.getExtAttr47());
	    m.put("EXT_ATTR_48", this.getExtAttr48());
	    m.put("EXT_ATTR_49", this.getExtAttr49());
	    m.put("EXT_ATTR_50", this.getExtAttr50());
	    m.put("EXT_ATTR_51", this.getExtAttr51());
	    m.put("EXT_ATTR_52", this.getExtAttr52());
	    m.put("EXT_ATTR_53", this.getExtAttr53());
	    m.put("EXT_ATTR_54", this.getExtAttr54());
	    m.put("EXT_ATTR_55", this.getExtAttr55());
	    m.put("EXT_ATTR_56", this.getExtAttr56());
	    m.put("EXT_ATTR_57", this.getExtAttr57());
	    m.put("EXT_ATTR_58", this.getExtAttr58());
	    m.put("EXT_ATTR_59", this.getExtAttr59());
	    m.put("EXT_ATTR_60", this.getExtAttr60());

	    return m;
	}
	
	public Object getValue(String columName){
		if("EXT_ATTR_1".equals(columName)){
			return this.getExtAttr1();
		}else if("EXT_ATTR_2".equals(columName)){
			return this.getExtAttr2();
		}else if("EXT_ATTR_3".equals(columName)){
			return this.getExtAttr3();
		}else if("EXT_ATTR_4".equals(columName)){
			return this.getExtAttr4();
		}else if("EXT_ATTR_5".equals(columName)){
			return this.getExtAttr5();
		}else if("EXT_ATTR_6".equals(columName)){
			return this.getExtAttr6();
		}else if("EXT_ATTR_7".equals(columName)){
			return this.getExtAttr7();
		}else if("EXT_ATTR_8".equals(columName)){
			return this.getExtAttr8();
		}else if("EXT_ATTR_9".equals(columName)){
			return this.getExtAttr9();
		}else if("EXT_ATTR_10".equals(columName)){
			return this.getExtAttr10();
		}else if("EXT_ATTR_11".equals(columName)){
			return this.getExtAttr11();
		}else if("EXT_ATTR_12".equals(columName)){
			return this.getExtAttr12();
		}else if("EXT_ATTR_13".equals(columName)){
			return this.getExtAttr13();
		}else if("EXT_ATTR_14".equals(columName)){
			return this.getExtAttr14();
		}else if("EXT_ATTR_15".equals(columName)){
			return this.getExtAttr15();
		}else if("EXT_ATTR_16".equals(columName)){
			return this.getExtAttr16();
		}else if("EXT_ATTR_17".equals(columName)){
			return this.getExtAttr17();
		}else if("EXT_ATTR_18".equals(columName)){
			return this.getExtAttr18();
		}else if("EXT_ATTR_19".equals(columName)){
			return this.getExtAttr19();
		}else if("EXT_ATTR_20".equals(columName)){
			return this.getExtAttr20();
		}else if("EXT_ATTR_21".equals(columName)){
			return this.getExtAttr21();
		}else if("EXT_ATTR_22".equals(columName)){
			return this.getExtAttr22();
		}else if("EXT_ATTR_23".equals(columName)){
			return this.getExtAttr23();
		}else if("EXT_ATTR_24".equals(columName)){
			return this.getExtAttr24();
		}else if("EXT_ATTR_25".equals(columName)){
			return this.getExtAttr25();
		}else if("EXT_ATTR_26".equals(columName)){
			return this.getExtAttr26();
		}else if("EXT_ATTR_27".equals(columName)){
			return this.getExtAttr27();
		}else if("EXT_ATTR_28".equals(columName)){
			return this.getExtAttr28();
		}else if("EXT_ATTR_29".equals(columName)){
			return this.getExtAttr29();
		}else if("EXT_ATTR_30".equals(columName)){
			return this.getExtAttr30();
		}else if("EXT_ATTR_31".equals(columName)){
			return this.getExtAttr31();
		}else if("EXT_ATTR_32".equals(columName)){
			return this.getExtAttr32();
		}else if("EXT_ATTR_33".equals(columName)){
			return this.getExtAttr33();
		}else if("EXT_ATTR_34".equals(columName)){
			return this.getExtAttr34();
		}else if("EXT_ATTR_35".equals(columName)){
			return this.getExtAttr35();
		}else if("EXT_ATTR_36".equals(columName)){
			return this.getExtAttr36();
		}else if("EXT_ATTR_37".equals(columName)){
			return this.getExtAttr37();
		}else if("EXT_ATTR_38".equals(columName)){
			return this.getExtAttr38();
		}else if("EXT_ATTR_39".equals(columName)){
			return this.getExtAttr39();
		}else if("EXT_ATTR_40".equals(columName)){
			return this.getExtAttr40();
		}else if("EXT_ATTR_41".equals(columName)){
			return this.getExtAttr41();
		}else if("EXT_ATTR_42".equals(columName)){
			return this.getExtAttr42();
		}else if("EXT_ATTR_43".equals(columName)){
			return this.getExtAttr43();
		}else if("EXT_ATTR_44".equals(columName)){
			return this.getExtAttr44();
		}else if("EXT_ATTR_45".equals(columName)){
			return this.getExtAttr45();
		}else if("EXT_ATTR_46".equals(columName)){
			return this.getExtAttr46();
		}else if("EXT_ATTR_47".equals(columName)){
			return this.getExtAttr47();
		}else if("EXT_ATTR_48".equals(columName)){
			return this.getExtAttr48();
		}else if("EXT_ATTR_49".equals(columName)){
			return this.getExtAttr49();
		}else if("EXT_ATTR_50".equals(columName)){
			return this.getExtAttr50();
		}else if("EXT_ATTR_51".equals(columName)){
			return this.getExtAttr51();
		}else if("EXT_ATTR_52".equals(columName)){
			return this.getExtAttr52();
		}else if("EXT_ATTR_53".equals(columName)){
			return this.getExtAttr53();
		}else if("EXT_ATTR_54".equals(columName)){
			return this.getExtAttr54();
		}else if("EXT_ATTR_55".equals(columName)){
			return this.getExtAttr55();
		}else if("EXT_ATTR_56".equals(columName)){
			return this.getExtAttr56();
		}else if("EXT_ATTR_57".equals(columName)){
			return this.getExtAttr57();
		}else if("EXT_ATTR_58".equals(columName)){
			return this.getExtAttr58();
		}else if("EXT_ATTR_59".equals(columName)){
			return this.getExtAttr59();
		}else if("EXT_ATTR_60".equals(columName)){
			return this.getExtAttr60();
		}else{
			return null;
		}
	}


	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
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

	public String getExtAttr41() {
		return extAttr41;
	}

	public void setExtAttr41(String extAttr41) {
		this.extAttr41 = extAttr41;
	}

	public String getExtAttr42() {
		return extAttr42;
	}

	public void setExtAttr42(String extAttr42) {
		this.extAttr42 = extAttr42;
	}

	public String getExtAttr43() {
		return extAttr43;
	}

	public void setExtAttr43(String extAttr43) {
		this.extAttr43 = extAttr43;
	}

	public String getExtAttr44() {
		return extAttr44;
	}

	public void setExtAttr44(String extAttr44) {
		this.extAttr44 = extAttr44;
	}

	public String getExtAttr45() {
		return extAttr45;
	}

	public void setExtAttr45(String extAttr45) {
		this.extAttr45 = extAttr45;
	}

	public String getExtAttr46() {
		return extAttr46;
	}

	public void setExtAttr46(String extAttr46) {
		this.extAttr46 = extAttr46;
	}

	public String getExtAttr47() {
		return extAttr47;
	}

	public void setExtAttr47(String extAttr47) {
		this.extAttr47 = extAttr47;
	}

	public String getExtAttr48() {
		return extAttr48;
	}

	public void setExtAttr48(String extAttr48) {
		this.extAttr48 = extAttr48;
	}

	public String getExtAttr49() {
		return extAttr49;
	}

	public void setExtAttr49(String extAttr49) {
		this.extAttr49 = extAttr49;
	}

	public String getExtAttr50() {
		return extAttr50;
	}

	public void setExtAttr50(String extAttr50) {
		this.extAttr50 = extAttr50;
	}

	public String getExtAttr51() {
		return extAttr51;
	}

	public void setExtAttr51(String extAttr51) {
		this.extAttr51 = extAttr51;
	}

	public String getExtAttr52() {
		return extAttr52;
	}

	public void setExtAttr52(String extAttr52) {
		this.extAttr52 = extAttr52;
	}

	public String getExtAttr53() {
		return extAttr53;
	}

	public void setExtAttr53(String extAttr53) {
		this.extAttr53 = extAttr53;
	}

	public String getExtAttr54() {
		return extAttr54;
	}

	public void setExtAttr54(String extAttr54) {
		this.extAttr54 = extAttr54;
	}

	public String getExtAttr55() {
		return extAttr55;
	}

	public void setExtAttr55(String extAttr55) {
		this.extAttr55 = extAttr55;
	}

	public String getExtAttr56() {
		return extAttr56;
	}

	public void setExtAttr56(String extAttr56) {
		this.extAttr56 = extAttr56;
	}

	public String getExtAttr57() {
		return extAttr57;
	}

	public void setExtAttr57(String extAttr57) {
		this.extAttr57 = extAttr57;
	}

	public String getExtAttr58() {
		return extAttr58;
	}

	public void setExtAttr58(String extAttr58) {
		this.extAttr58 = extAttr58;
	}

	public String getExtAttr59() {
		return extAttr59;
	}

	public void setExtAttr59(String extAttr59) {
		this.extAttr59 = extAttr59;
	}

	public String getExtAttr60() {
		return extAttr60;
	}

	public void setExtAttr60(String extAttr60) {
		this.extAttr60 = extAttr60;
	}

	public boolean isEmpty(){
		if(Strings.isNotEmpty(extAttr1) || Strings.isNotEmpty(extAttr2) || Strings.isNotEmpty(extAttr3) || Strings.isNotEmpty(extAttr4) || Strings.isNotEmpty(extAttr5)
		    || Strings.isNotEmpty(extAttr6) || Strings.isNotEmpty(extAttr7) || Strings.isNotEmpty(extAttr8) || Strings.isNotEmpty(extAttr9) || Strings.isNotEmpty(extAttr10)
		    || Strings.isNotEmpty(extAttr31) || Strings.isNotEmpty(extAttr32) || Strings.isNotEmpty(extAttr33) || Strings.isNotEmpty(extAttr34) || Strings.isNotEmpty(extAttr35)
			|| Strings.isNotEmpty(extAttr36) || Strings.isNotEmpty(extAttr37) || Strings.isNotEmpty(extAttr38) || Strings.isNotEmpty(extAttr39) || Strings.isNotEmpty(extAttr40)
			|| Strings.isNotEmpty(extAttr41) || Strings.isNotEmpty(extAttr42) || Strings.isNotEmpty(extAttr43) || Strings.isNotEmpty(extAttr44) || Strings.isNotEmpty(extAttr45)
			|| Strings.isNotEmpty(extAttr46) || Strings.isNotEmpty(extAttr47) || Strings.isNotEmpty(extAttr48) || Strings.isNotEmpty(extAttr49) || Strings.isNotEmpty(extAttr50)
			|| Strings.isNotEmpty(extAttr51) || Strings.isNotEmpty(extAttr52) || Strings.isNotEmpty(extAttr53) || Strings.isNotEmpty(extAttr54) || Strings.isNotEmpty(extAttr55)
			|| Strings.isNotEmpty(extAttr56) || Strings.isNotEmpty(extAttr57) || Strings.isNotEmpty(extAttr58) || Strings.isNotEmpty(extAttr59) || Strings.isNotEmpty(extAttr60)
		   ){
			return false;
		}
		
		if(extAttr11 != null || extAttr12 != null || extAttr13 != null || extAttr14 != null || extAttr15 != null
		   || extAttr16 != null || extAttr17 != null || extAttr18 != null || extAttr19 != null || extAttr20 != null){
			return false;
		}
		
		if(extAttr21 != null || extAttr22 != null || extAttr23 != null || extAttr24 != null || extAttr25 != null
			|| extAttr26 != null || extAttr27 != null || extAttr28 != null || extAttr19 != null || extAttr30 != null){
			return false;
		}
		return true;
	}

}