package com.seeyon.apps.ldap.manager;

import java.util.List;

import com.seeyon.apps.ldap.domain.EntryValueBean;
import com.seeyon.apps.ldap.domain.V3xLdapRdn;
import com.seeyon.apps.ldap.domain.V3xLdapSwitchBean;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.annotation.AjaxAccess;

/**
 * LDAP/AD业务实现类
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @author lilong
 * @version 2008-11-5
 * @version CTP2.0
 * @date 2012-12-11
 */
public interface LdapBindingMgr {
    /**
     * 批量绑定A8用户账号
     * @param orgManagerDirect 
     * @param list ldif或ldf文件内容
     * @return void
     */
    public void batchBinding(OrgManager orgManager, List<String> list, List<V3xOrgMember> memberList, int option)
            throws Exception;

    /**
     * 手工单用户账号绑定
     * @param memberId 人员ID
     * @param loginName 用户账号
     * @param binding 绑定的LDAP/AD相对条目
     * @param enabled 用户账号是否可用
     * @return String 结果
     */
    public String[] handBinding(long memberId, String loginName, String binding, boolean enabled) throws Exception;

    /**
     * 修改用户密码
     * @param dn 条目
     * @param oldPassWord 旧密码
     * @param newPassword 新密码
     * @return void
     */
    public void modifyUserPassWord(String dn, String oldPassWord, String newPassword) throws Exception;

    /**
     * 绑定前清空
     * @param orgManagerDirect
     * @param memberList
     *        登录管理员管理的所有用户List
     * @return void
     */
    public void deleteAllBinding(OrgManagerDirect orgManagerDirect, List<V3xOrgMember> memberList) throws Exception;

    /**
     * 查询单位绑定目录下的人员
     * @param list
     * @param baseDN
     * @throws Exception
     */
    public void userTreeView(List<EntryValueBean> list, String baseDN) throws Exception;

    public List<EntryValueBean> ouTreeView(boolean isRoot) throws Exception;

    /**
     * 从LDAP上查询
     * @param dn 
     * @return String[] 从LDAP上查询出用户帐号，姓名，密码，手机号码等信息
     */
    public String[] getUserAttributes(String dn) throws Exception;

    public void saveOrUpdateLdapSet(V3xOrgAccount account, V3xLdapRdn value) throws Exception;

    public V3xLdapRdn findLdapSet(Long orgAccountId) throws Exception;

    public boolean createNode(V3xOrgMember member, String selectOU) throws Exception;

    public V3xLdapSwitchBean viewLdapSwitch() throws Exception;

    public V3xLdapSwitchBean saveLdapSwitch(V3xLdapSwitchBean ldapSwitchBean) throws Exception;

    public void deleteLdapSet(Long orgAccountId) throws Exception;

    public String getDefaultOU(long accoutId);
    
    public V3xLdapSwitchBean createLdapProperties(V3xLdapSwitchBean ldapSwitchBean) throws Exception;
    
    public V3xLdapSwitchBean readLdapPropertiesFromDataBase() throws Exception;
    
    public List<EntryValueBean> subTreeView(String parentDn, String parentId, String type) throws Exception;

    @AjaxAccess
	public List<EntryValueBean> getSearchCn(String baseDn, String key)throws Exception;
    
    public List<EntryValueBean> subOrgTreeView(String parentDn,String parentId) throws Exception ;

	/**
	 * 如果已经绑定了ldap，根据ldap的登录名取对应的oa登录名
	 * 允许使用oa账号登录的，直接返回。
	 * @param loginName
	 * @return
	 */
	public String getLoginName(String username);
}
