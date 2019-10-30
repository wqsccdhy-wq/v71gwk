/**
 * 
 */
package com.seeyon.ctp.organization.principal;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;

/**
 * 登录身份管理
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 
 *         2010-11-15
 */
public interface PrincipalManager {
    /**
     * 判断此登录名是否<b>存在并有效</b>
     * 
     * @param loginName
     * @return
     */
    public boolean isExist(String loginName);

    /**
     * 判断此<code>Member.id</code>是否<b>存在并有效</b>
     * 
     * @param loginName
     * @return
     */
    public boolean isExist(long memberId);

    /**
     * 根据登录名取得<code>Member.id</code>
     * 
     * @param loginName
     * @return
     * @throws NoSuchPrincipalException
     *             如果用户名不存在
     */
    public long getMemberIdByLoginName(String loginName) throws NoSuchPrincipalException;

    /**
     * 根据<code>Member.id</code>取得登录名。请勿在循环中调用，循环调用请使用getMemberIdLoginNameMap。
     * 
     * @param memberId
     * @return
     * @throws NoSuchPrincipalException
     *             如果用户不存在
     */
    public String getLoginNameByMemberId(long memberId) throws NoSuchPrincipalException;

    /**
     * 取得人员登录名和Id的对应关系Map。
     * 
     * @return key为登录名，value为对应人员Id的Map。
     */
    Map<String, Long> getLoginNameMemberIdMap();

    /**
     * 取得人员Id和登录名的对应关系Map。如果需要批量getLoginNameByMemberId，请使用此方法以提高性能。
     * 
     * @return key为人员Id，Value为对应登录名的Map。
     */
    Map<Long, String> getMemberIdLoginNameMap();

    /**
     * 添加一个登录身份
     * 
     * @param principal
     *            memberId,loginName,password三个字段必须都要有值，password必须是明文
     * @throws ExistPrincipalException
     *             如果用户名已经存在
     */
    public OrganizationMessage insert(V3xOrgPrincipal principal);

    /**
     * 修改一个登录身份
     * 
     * @param principal
     *            memberId,loginName,password三个字段必须都要有值，password必须是明文或默认密码
     */
    public OrganizationMessage update(V3xOrgPrincipal principal);

    /**
     * 修改指定登录名人员的登录密码。因为system和audit-admin不存在Member，不能使用update方法修改密码，所以提供此方法。
     * 
     * @param loginName
     *            登录名，如system、audit-admin。
     * @param password
     *            新密码
     * @param isExpirationDate
     *            是否要计算密码的超期时间，如果当前操作者修改的是自己的密码，就传false；否则传true，表示密码已经超期，登录进来后立即提示修改密码
     * @return 修改成功返回<tt>true</tt>，否则返回<tt>false</tt>。
     * @throws NoSuchPrincipalException
     *             指定用户不存在抛出。
     */
    boolean changePassword(String loginName, String password, boolean isExpirationDate) throws NoSuchPrincipalException;

    /**
     * 批量添加登录身份
     * 
     * @param principals
     *            memberId,loginName,password三个字段必须都要有值，password必须是明文或默认密码
     * @throws ExistPrincipalException
     *             如果用户名已经存在
     */
    public OrganizationMessage insertBatch(List<V3xOrgPrincipal> principals);

    /**
     * 批量添加登录身份
     * 
     * @param principals
     *            memberId,loginName,password三个字段必须都要有值，password必须是明文或默认密码
     * @throws ExistPrincipalException
     *             如果用户名已经存在
     */
    public OrganizationMessage insertBatch(List<V3xOrgPrincipal> principals, String resource);

    /**
     * 批量修改一个登录身份
     * 
     * @param principals
     *            memberId,loginName,password三个字段必须都要有值，password必须是明文或默认密码
     */
    public OrganizationMessage updateBatch(List<V3xOrgPrincipal> principals);

    /**
     * 删除一个登录身份
     * 
     * @param meberId
     */
    public void delete(long memberId);

    /**
     * 得到指定用户的密码过期时间点
     * 
     * @param loginName
     * @return
     */
    public Date getPwdExpirationDate(String loginName);

    /**
     * 得到密码信息最后修改时间点
     * 
     * @param loginName
     * @return
     */
    public Date getCredentialUpdateDate(String loginName);

    /**
     * 认证用户名密码
     * 
     * @param loginName
     * @param password
     *            密码原文
     * @return 验证成功返回<code>true</code>；失败返回<code>false</code>
     */
    public boolean authenticate(String loginName, String password);

    /**
     * 取得所有的人员帐号信息
     * 
     * @return
     */
    public List<String> getAllLoginNames();

    /**
     * 得到指定人员的加密后的密码
     * 
     * @param memberId
     * @return
     * @throws NoSuchPrincipalException
     */
    public String getPassword(long memberId) throws NoSuchPrincipalException;

    /**
     * 批量修改用户的密码过期时间
     * 
     * @param days
     *            //修改后的密码超期时间与之前的密码超期时间相差的天数
     */
    public void updateBatchExpirationDate(int days);

}
