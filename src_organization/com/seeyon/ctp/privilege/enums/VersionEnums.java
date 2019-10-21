package com.seeyon.ctp.privilege.enums;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.code.EnumsCode;

public enum VersionEnums implements EnumsCode {
	
	a6("0","0"),// A6版 
	enterprise("1","1"), // A8企业版 
	entgroup("2","2"),// A8集团版
	government("3","3"),// 政务版又名G6-V5单组织版
	governmentgroup("4","4"),// 政务多组织版 又名G6-V5多组织版
	ufidanc("5","5"),// UFIDA-NC协同套件
	ufidau8("6","6"),// UFIDA-U8协同套件
	a6_s("7","7"),// A6-S版
	u8oa("0_U8S","8"),//U8OA标准版
	u8oa_enterprise("1_U8E","9"),//U8OA企业版
	ncoa("2_NC","10");
	
	private String key;
	private String text;
	
	VersionEnums(String key,String text){
		this.key=key;
		this.text = text;
	}
	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return String.valueOf(key);
	}
	public String getKey() {
		return key;
	}

	public String getText() {
		// TODO Auto-generated method stub
		return text.toString();
	}
	
	public static String getTextForKey(){
	 String productId=AppContext.getSystemProperty("system.ProductId");
        String productLine=AppContext.getSystemProperty("system.productLine");
        if(productLine!=null){
        	productId+="_"+productLine;
        }
        
        /**
         * 为了向下兼容放弃了switch (key) 
         */
        if(VersionEnums.a6.getKey().equals(productId)){
			return a6.getValue();
		}else if(VersionEnums.enterprise.getKey().equals(productId)){
			return enterprise.getValue();
		}else if(VersionEnums.entgroup.getKey().equals(productId)){
			return entgroup.getValue();
		}else if(VersionEnums.government.getKey().equals(productId)){
			return government.getValue();
		}else if(VersionEnums.governmentgroup.getKey().equals(productId)){
			return governmentgroup.getValue();
		}else if(VersionEnums.ufidanc.getKey().equals(productId)){
			return ufidanc.getValue();
		}else if(VersionEnums.ufidau8.getKey().equals(productId)){
			return ufidau8.getValue();
		}else if(VersionEnums.a6_s.getKey().equals(productId)){
			return a6_s.getValue();
		}else if(VersionEnums.u8oa.getKey().equals(productId)){
			return u8oa.getValue();
		}else if(VersionEnums.u8oa_enterprise.getKey().equals(productId)){
			return u8oa_enterprise.getValue();
		}else if(VersionEnums.ncoa.getKey().equals(productId)){
			return ncoa.getValue();
		}else{
			return "";
		}
	}

}
