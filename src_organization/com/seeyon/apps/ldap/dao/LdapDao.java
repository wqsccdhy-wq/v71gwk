package com.seeyon.apps.ldap.dao;

import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.domain.EntryValueBean;

/**
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-11-6
 */
public interface LdapDao {
    /**
     * 取得LDAP连接实例
     */
    public DirContext getContext() throws Exception;

    /**
     * LDAP/AD用户账号是否存在
     * 
     * @param dn
     *            条目
     * @return boolean true or false
     */
    public boolean isUserExist(String dn) throws Exception;

    /**
     * 修改用户密码
     * 
     * @param rdn
     *            条目
     * @param oldPassWord
     *            旧密码
     * @param newPassword
     *            新密码
     * @return void
     */
    public void modifyUserPassWord(String rdn, String oldPassWord, String newPassword) throws Exception;

    public Attributes findUser(String uid) throws Exception;

    /**
     * 修改用户密码
     * 
     * @param dn
     *            条目
     * @return String 登录账号
     */
    public String getLoginName(String dn) throws Exception;

    /**
     * 用户登录认证
     * 
     * @param username
     *            用户账号
     * @param password
     *            用户密码
     * @return boolean true or false
     */
    public boolean auth(String username, String password);

    public void setLDAPConfig(LDAPConfig val);

    public LDAPConfig getLDAPConfig();

    public void userTreeView(String baseDn, List<EntryValueBean> list) throws Exception;

    public List<EntryValueBean> ouTreeView(String baseDn, boolean isRoot) throws Exception;

    public String[] getuserAttribute(String uid) throws Exception;

    public boolean createNode(String dn, String[] parameter) throws Exception;
    
    /**
     * 获取父节点下的直接子节点（包含ou和cn）
     * 本节点：SearchControls.OBJECT_SCOPE  
     * 单层，直接子节点：SearchControls.ONELEVEL_SCOPE 
     * 遍历，全部子节点 SearchControls.SUBTREE_SCOPE
     * @param parentDn 父节点
     * @return 
     * @throws Exception
     */
    public List<EntryValueBean> getSubNode(String parentDn,String parentId, String type) throws Exception;
    

	public List<EntryValueBean> getOrgSubNode(String parentDn, String parentId) throws Exception;


    /**
     * 查询ad中的人员
     * @param baseDn
     * @param key
     * @return
     * @throws Exception
     */
    public List<EntryValueBean> searchCn(String baseDn, String key) throws Exception;
}
