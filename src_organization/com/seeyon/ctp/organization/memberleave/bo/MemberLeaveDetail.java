/**
 * 
 */
package com.seeyon.ctp.organization.memberleave.bo;

/**
 * @author tanmf
 *
 */
public class MemberLeaveDetail {
	//各应用拼接起来的唯一标识，保证离职替换接收人时，授权数据替换准确。
	//如开发一部部门主管，可以记录为：unitId_roleId_处理数据接口（从哪个接口返回的数据，就在哪个接口里处理） --> 43794739479347_25503874083058_MemberLeaveHandItemInterfaceOrgRoleImpl
	private String id; 

    private String accountName;
    
    private String type;
    
    private String content;
    
    private String title;
	//下面几个是个人中心过分类过滤用的属性
	private Long accountId;

	private Long deptId;

	private String deptName;

	private String deptFullName;

	private int roleType;

	private String roleCode;

	private String roleName;//部门时title前面多东西

	private Boolean enabled = true;
	
	private String dealInterfaceClassName;//处理交接的接口实现，如果是多个用“,”隔开 连接
	
	private String webId;
	
	public String getWebId() {
		return  webId;
	}

	public void setWebId(String webId) {
		this.webId = webId;
	}


	public String getId() {
		return  id;
	}

	public void setId(String id) {
		this.id = id;
		this.webId = id + "_" + this.dealInterfaceClassName;
	}
	
	public String getDealInterfaceClassName() {
		return dealInterfaceClassName;
	}

	public void setDealInterfaceClassName(String dealInterfaceClassName) {
		this.dealInterfaceClassName = dealInterfaceClassName;
		this.webId = this.id + "_" + dealInterfaceClassName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptFullName() {
		return deptFullName;
	}

	public void setDeptFullName(String deptFullName) {
		this.deptFullName = deptFullName;
	}

	public int getRoleType() {
		return roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

}
