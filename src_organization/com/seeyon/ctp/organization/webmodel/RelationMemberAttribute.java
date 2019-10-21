package com.seeyon.ctp.organization.webmodel;

/**
 * 人员的表单关联属性对象
 * @author wf
 *
 */
public class RelationMemberAttribute {
	private String code;  //字段编号
	private String desc;  //字段描述
	private String i18n;  //字段名称（国际化）
	private String fieldType; // 字段类型
	private String FormFieldCtrl; //对应表单控件
	private Long enumId;//如果属性是枚举类型的，对应的枚举id
	private Integer fieldLenth;//字段值最大长度
	private Integer digitNum = 0;//如果是数字类型，最长小数位
	
	public RelationMemberAttribute(String code, String desc, String i18n, String fieldType, String FormFieldCtrl,Long enumId,Integer fieldLenth,Integer digitNum){
		this.code = code;
		this.desc = desc;
		this.i18n = i18n;
		this.fieldType = fieldType;
		this.FormFieldCtrl = FormFieldCtrl;
		this.enumId = enumId;
		this.fieldLenth = fieldLenth;
		this.digitNum = digitNum;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getI18n() {
		return i18n;
	}
	public void setI18n(String i18n) {
		this.i18n = i18n;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFormFieldCtrl() {
		return FormFieldCtrl;
	}
	public void setFormFieldCtrl(String formFieldCtrl) {
		FormFieldCtrl = formFieldCtrl;
	}
	public Long getEnumId() {
		return enumId;
	}
	public void setEnumId(Long enumId) {
		this.enumId = enumId;
	}
	public Integer getFieldLenth() {
		return fieldLenth;
	}
	public void setFieldLenth(Integer fieldLenth) {
		this.fieldLenth = fieldLenth;
	}
	public Integer getDigitNum() {
		return digitNum;
	}
	public void setDigitNum(Integer digitNum) {
		this.digitNum = digitNum;
	}

}
