package com.seeyon.apps.ldap.event;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.domain.V3xLdapRdn;
import com.seeyon.apps.ldap.manager.LdapBindingMgr;
import com.seeyon.ctp.common.po.usermapper.CtpOrgUserMapper;
import com.seeyon.ctp.common.usermapper.dao.UserMapperDao;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.Strings;

/**
 * 组织模型,HR管理员事件调用类
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2009-1-16
 */
public class OrganizationLdapEvent {
    private static final Log log = LogFactory.getLog(OrganizationLdapEvent.class);

    private LdapBindingMgr   ldapBindingMgr;

    private UserMapperDao    userMapperDao;

    public void deleteLdapSet(Long orgAccountId) {
        try {
            ldapBindingMgr.deleteLdapSet(orgAccountId);
        } catch (Exception e) {
            log.error(e);
        }
    }

    public void newAddLdapPerson(V3xOrgMember member, String selectOU) throws Exception {
        ldapBindingMgr.createNode(member, selectOU);
    }

    public void saveOrUpdateLdapSet(V3xOrgAccount account, V3xLdapRdn value) {
        try {
            ldapBindingMgr.saveOrUpdateLdapSet(account, value);
            log.info(OrganizationLdapEvent.class.getName() + " save ldap set");
        } catch (Exception e) {
            log.error(e);
        }
    }

    public V3xLdapRdn findLdapSet(Long orgAccountId) {
        V3xLdapRdn value = null;
        try {
            value = ldapBindingMgr.findLdapSet(orgAccountId);
        } catch (Exception e) {
            log.error("findLdapSet异常"+e.getLocalizedMessage(), e);
        }
        return value;
    }

    public String getLdapAdLoginName(String a8LoginName) {
        List<CtpOrgUserMapper> userMappers = userMapperDao.getExLoginNames(a8LoginName, LDAPConfig.getInstance()
                .getType());
        String stateNames = "";
        for (CtpOrgUserMapper map : userMappers) {
            stateNames = map.getExLoginName();

        }
        // if (!StringUtils.isBlank(stateNames))
        // {
        // stateNames = stateNames.substring(0, stateNames.length() - 1);
        // }
        return stateNames;

    }

    public String getLdapAdExUnitCode(String a8LoginName) {
        if (StringUtils.isBlank(a8LoginName)) {
            return null;
        }
        List<CtpOrgUserMapper> userMappers = userMapperDao.getExLoginNames(a8LoginName, LDAPConfig.getInstance()
                .getType());
        String stateNames = "";
        for (CtpOrgUserMapper map : userMappers) {
            stateNames = map.getExUnitCode();
        }
        // if (!StringUtils.isBlank(stateNames))
        // {
        // stateNames = stateNames.substring(0, stateNames.length() - 1);
        // }
        return stateNames;

    }

    public String[] addMember(Object member, String entry) throws Exception {
        if (StringUtils.isBlank(entry)) {
            return null;
        }
        V3xOrgMember member1 = (V3xOrgMember) member;
        try {
            return ldapBindingMgr.handBinding(member1.getId(), member1.getLoginName(), entry, member1.getEnabled());
        } catch (Exception e) {
            throw new Exception(OrganizationLdapEvent.class.getName() + " exception addMember! ", e);
        }
    }

    public void changePassword(Object oldMember, Object newMember) throws Exception {
        if (oldMember == null || newMember == null) {
            throw new Exception("ldap/ad null member");
        }
        String oldPassWord = "";
        String newPassword = "";
        String newLoginName = "";
        try {
            if (newMember instanceof V3xOrgMember) {
                newPassword = ((V3xOrgMember) newMember).getPassword();
                newLoginName = ((V3xOrgMember) newMember).getLoginName();
            }
            if (oldMember instanceof V3xOrgMember) {
                oldPassWord = ((V3xOrgMember) oldMember).getPassword();
                String oldLoginName = ((V3xOrgMember) oldMember).getLoginName();

                if (!oldLoginName.equals(newLoginName)) {
                    List<CtpOrgUserMapper> list = userMapperDao.getExLoginNames(oldLoginName, LDAPConfig.getInstance()
                            .getType());
                    for (CtpOrgUserMapper mapper : list) {
                        mapper.setLoginName(newLoginName);
                        userMapperDao.updateUserMapper(mapper);
                    }
                }
                if (StringUtils.isBlank(oldPassWord) || StringUtils.isBlank(newPassword)) {
                    throw new Exception("ldap/ad null password");
                }
                List<CtpOrgUserMapper> list = userMapperDao.getExLoginNames(newLoginName, LDAPConfig.getInstance()
                        .getType());
                if (list == null || list.size() <= 0) {
                    throw new Exception("ldap/ad not binding");
                }
                for (CtpOrgUserMapper mapper : list) {
                    ldapBindingMgr.modifyUserPassWord(mapper.getExUnitCode(), oldPassWord, newPassword);
                }
            }

        } catch (Exception e) {
            throw new Exception(OrganizationLdapEvent.class.getName() + " exception modified ldap/ad pw! ", e);
        }
    }

    public void deleteAllBinding(OrgManagerDirect orgManagerDirect, List<V3xOrgMember> memberList) {
        try {
            ldapBindingMgr.deleteAllBinding(orgManagerDirect, memberList);
        } catch (Exception e) {
            log.error(e);
        }
    }

    public void deleteMember(Object memeber) throws Exception {

    }

    public LdapBindingMgr getLdapBindingMgr() {
        return ldapBindingMgr;
    }

    public void setLdapBindingMgr(LdapBindingMgr ldapBindingMgr) {
        this.ldapBindingMgr = ldapBindingMgr;
    }

    public UserMapperDao getUserMapperDao() {
        return userMapperDao;
    }

    public void setUserMapperDao(UserMapperDao userMapperDao) {
        this.userMapperDao = userMapperDao;
    }

    public String isDefaultOUNull(long accoutId) {
        String value = ldapBindingMgr.getDefaultOU(accoutId);
        if (Strings.isBlank(value)) {
            return null;
        }
        return "value";
    }
}
