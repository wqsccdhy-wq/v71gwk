package com.seeyon.apps.addressbook.webmodel;

/**
 * 人员卡片对象
 * 包含要求的固定字段和所有的自定义字段
 * @author wf
 *
 */
public class AddressBookPeopleCard {
	/**
	 * id
	 */
	private Long I;
	/**
	 * 名称
	 */
	private String N;
	/**
	 * 主岗单位id
	 */
	private Long AId;
	/**
	 * 主岗部门id
	 */
	private Long DId;
	/**
	 * 主岗部门名称
	 */
	private String DN;
	/**
	 * 全路径名称
	 */
	private String ADN;
	/**
	 * 主岗岗位id
	 */
	private Long PId;
	/**
	 * 主岗岗位名称
	 */
	private String PN;
	/**
	 * 主岗职务级别id
	 */
	private Long LId;
	/**
	 * 主岗职务级别名称
	 */
	private String LN;
	/**
	 * 人员头像
	 */
	private String Img;
	/**
	 * 工作电话
	 */
	private String ONm;
	/**
	 * 手机
	 */
	private String TNm;
	/**
	 * 邮箱
	 */
	private String EM;
	
	/**
	 * 汇报人id
	 */
	private Long RPId;
	
	/**
	 * 汇报人名称（只有内部人员有）
	 */
	private String RP;
	
	/**
	 * 1:內部人員  0 ：外部人員
	 */
	private String Inner; 
	
	/**
	 * 人员外部标识
	 * 0:内部人员
	 * 1:外部人员
	 */
	private Integer Ext;
	
	/**
	 * 外部机构名称
	 */
	private String VjUN;
	
	/**
	 * 外部单位名称
	 */
	private String VjAN;
	
	/**
	 *自定义的字段
	 *格式为  上班楼栋:N,工位号:1001,乘坐班车:是
	 */
	private String CIf;
	private String jCIf;//json格式的自定义字段值
	
	/**
	 * 工作地
	 */
	private String wl;
	
	/**
	 * 微信
	 */
	private String wc;
	
	/**
	 * 微博
	 */
	private String wb;
	
	/**
	 * 家庭住址
	 */
	private String add;
	
	/**
	 * 邮政编码
	 */
	private String pl;
	
	/**
	 * json格式的副岗
	 */
	private String sp;
	
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
	public Long getLId() {
		return LId;
	}
	public void setLId(Long lId) {
		LId = lId;
	}
	public String getLN() {
		return LN;
	}
	public void setLN(String lN) {
		LN = lN;
	}
	public String getImg() {
		return Img;
	}
	public void setImg(String img) {
		Img = img;
	}
	public String getTNm() {
		return TNm;
	}
	public void setTNm(String tNm) {
		TNm = tNm;
	}
	public String getONm() {
		return ONm;
	}
	public void setONm(String oNm) {
		ONm = oNm;
	}
	public String getEM() {
		return EM;
	}
	public void setEM(String eM) {
		EM = eM;
	}
	public String getCIf() {
		return CIf;
	}
	public void setCIf(String cIf) {
		CIf = cIf;
	}
	public String getADN() {
		return ADN;
	}
	public void setADN(String aDN) {
		ADN = aDN;
	}
	public String getInner() {
		return Inner;
	}
	public void setInner(String inner) {
		Inner = inner;
	}
    public String getVjUN() {
        return VjUN;
    }
    public void setVjUN(String vjUN) {
        VjUN = vjUN;
    }
    public String getVjAN() {
        return VjAN;
    }
    public void setVjAN(String vjAN) {
        VjAN = vjAN;
    }
    public Integer getExt() {
        return Ext;
    }
    public void setExt(Integer ext) {
        Ext = ext;
    }
    public String getjCIf() {
        return jCIf;
    }
    public void setjCIf(String jCIf) {
        this.jCIf = jCIf;
    }
	public String getRP() {
		return RP;
	}
	public void setRP(String rP) {
		RP = rP;
	}
	public Long getRPId() {
		return RPId;
	}
	public void setRPId(Long rPId) {
		RPId = rPId;
	}
	public String getWl() {
		return wl;
	}
	public void setWl(String wl) {
		this.wl = wl;
	}
	public String getWc() {
		return wc;
	}
	public void setWc(String wc) {
		this.wc = wc;
	}
	public String getWb() {
		return wb;
	}
	public void setWb(String wb) {
		this.wb = wb;
	}
	public String getAdd() {
		return add;
	}
	public void setAdd(String add) {
		this.add = add;
	}
	public String getPl() {
		return pl;
	}
	public void setPl(String pl) {
		this.pl = pl;
	}
	public String getSp() {
		return sp;
	}
	public void setSp(String sp) {
		this.sp = sp;
	}
	
}
