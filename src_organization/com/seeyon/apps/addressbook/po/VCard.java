package com.seeyon.apps.addressbook.po;

public class VCard {
	  public String name = "";//      卢呈祥
	  public String nName = ""; //    卢;呈祥
	  public String comment = "";  // 
	  public String title = "";  //    职务    （ 例 ：初级，高级）
	  public String organisation = "";//      用友致远软件技术有限公司;研发二部
	  public String address = ""; //家庭住址	
	  public String phone = "";   //商务电话
	  public String fax = "";     //商务传真
	  public String homeP = "";   //住宅电话
	  public String mobilePhone = "";     //移动电话

	  public String email = "";
	  
	 public String getVCard() {
	        String charset = ";CHARSET=utf-8";
		    String vCard = "BEGIN:VCARD\n"
		      + "FN"+charset+":" + name + "\n"
		      + "N"+charset+":" + nName + "\n"   
		      + "NOTE;CHARSET=utf-8;ENCODING=QUOTED-PRINTABLE"+charset+":" + comment + "\n"  
		      + "TITLE"+charset+":" + title + "\n"
		      + "ORG"+charset+":" + organisation + "\n"
		      + "ADR;POSTAL;WORK"+charset+":;" + address + "\n"
		      + "TEL;Work"+charset+":" + phone + "\n"
		      
		      + "TEL;HOME;VOICE"+charset+":" + homeP + "\n"
		      + "TEL;CELL;VOICE"+charset+":" + mobilePhone   + "\n"
		      
		      + "TEL;WORK;FAX"+charset+":" + fax + "\n"
		      
		      + "EMAIL;PREF;INTERNET"+charset+":" + email + "\n"
		     
		      + "VERSION:2.1\n"
		      + "END:VCARD\n";

		    return vCard;
		  }

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	

	public String getHomeP() {
		return homeP;
	}

	public void setHomeP(String homeP) {
		this.homeP = homeP;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNName() {
		return nName;
	}

	public void setNName(String name) {
		nName = name;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	 
	
	 
	 
	 
}
