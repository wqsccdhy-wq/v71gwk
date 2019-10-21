package com.seeyon.apps.ldap.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.ldap.config.LDAPProperties;
import com.seeyon.apps.ldap.dao.AdDaoImp;
import com.seeyon.apps.ldap.dao.LdapDao;
import com.seeyon.apps.ldap.dao.LdapDaoImp;
import com.seeyon.apps.ldap.domain.EntryValueBean;
import com.seeyon.apps.ldap.domain.V3xLdapRdn;
import com.seeyon.apps.ldap.domain.V3xLdapSwitchBean;
import com.seeyon.apps.ldap.util.LDAPTool;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.config.IConfigPublicKey;
import com.seeyon.ctp.common.config.manager.ConfigManager;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceBundleUtil;
import com.seeyon.ctp.common.po.config.ConfigItem;
import com.seeyon.ctp.common.po.usermapper.CtpOrgUserMapper;
import com.seeyon.ctp.common.usermapper.dao.UserMapperDao;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.TextEncoder;
import com.seeyon.v3x.common.web.login.CurrentUser;

/**
 * LDAP/AD业务实现类
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-11-6
 */
public class LdapBindingMgrImp implements LdapBindingMgr {
    private static final Log    log                = LogFactory.getLog(LdapBindingMgrImp.class);

    private static final String PARSEFLAGA1        = "a8";

    private static final String PARSEFLAGA2        = "A8";

    private static final String NOTE               = "#";

    private static final String DNFLAG             = "dn";

    private static final String LDAPFLAG           = "uid";

    private static final String ADFLAG1            = "cn";

    private static final String ADFLAG2            = "CN";
    /**
     * LDAP/AD键值对区分标识
     */
    private static final String ATTRIBUTEFLAG      = ":";

    private static final String DCFLAG1            = "DC=";

    private static final String DCFLAG2            = "dc=";
    /**
     * LDAP/AD配置文件存放目录 base/ldap
     */
    private static final String LDAP_SWITCH_FOLDER = "/conf";

    /**
     * LDAP/AD配置信息文件
     */
    private static final String LDAP_SWITCH_FILE   = "/ldap.properties";

    private UserMapperDao       userMapperDao;

    private LdapDao             ldapDao            = null;
    private OrgManagerDirect    orgManagerDirect   = null;
    private OrgManager          orgManager         = null;
    
    private ConfigManager configManager;


    public OrgManager getOrgManager() {
        return orgManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public UserMapperDao getUserMapperDao() {
        return userMapperDao;
    }

    public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public LdapBindingMgrImp() {
        
    }

    private synchronized void init() {
        if (LDAPConfig.getInstance().getType().indexOf(LDAPConfig.LDAPFlag) != -1) {
            if (LDAPConfig.getInstance().getType().indexOf(LdapServerMap.getOPENLDAP()) != -1) {
                LdapDao ldapDao1 = (LdapDao) AppContext.getBean("openldapDao");
                //ldapDao1.setLog(log);
                ldapDao = (LdapDao) ldapDao1;
            } else {
                LdapDao ldapDao1 = (LdapDao) AppContext.getBean("ldapDao");
                //ldapDao1.setLog(log);
                ldapDao = (LdapDao) ldapDao1;
            }
        } else {
            LdapDao ldapDao1 = (LdapDao) AppContext.getBean("adDao");
            //LdapDao.setLog(log);
            ldapDao = (LdapDao) ldapDao1;
        }
    }

    public void deleteAllBinding(OrgManagerDirect orgManagerDirect, List<V3xOrgMember> memberList) throws Exception {
        if (memberList == null) {
            return;
        }
        for (V3xOrgMember member : memberList) {
            List<CtpOrgUserMapper> list = userMapperDao.getExLoginNames(member.getLoginName(), null);

            for (CtpOrgUserMapper mapper : list) {
                userMapperDao.deleteUserMapper(mapper);
                log.info("删除人员绑定: " + mapper.getLoginName());
            }
        }
    }

    /**
     * 批量绑定A8用户账号
     * 
     * @param orgManagerDirect
     * @param list
     *            ldif或ldf文件内容
     * @param memberList
     *            登录管理员所在单位所有用户
     * @return void
     */
    @Override
    public void batchBinding(OrgManager orgManager, List<String> list, List<V3xOrgMember> memberList, int option)
            throws Exception {
        try {
            Map<String, String> map = null;
            init();
            Class class4Ldap = org.wso2.spring.ws.util.Utils.getTargetClassFromJdkDynamicAopProxy(ldapDao);
            String className = class4Ldap.getSimpleName();
            if(className.toLowerCase().indexOf(AdDaoImp.class.getSimpleName().toLowerCase())==0){
                map = parseADLDIF(list);
            } else {
                map = parseLDIF(list);
            }

            if (map == null || map.size() < 0) {
                return;
            }
            Set<Map.Entry<String, String>> entry = map.entrySet();
            List<CtpOrgUserMapper> usersMapperList = new ArrayList<CtpOrgUserMapper>();
            String[] currentArray = new String[memberList.size()];
            int currentIndex = 0;
            for (Entry<String, String> element : entry) {
                String a8 = element.getValue().trim();
                if (StringUtils.isBlank(a8) || "-1".equals(a8)) {
                    continue;
                }
                String[] temArray = StringUtils.split(element.getKey().trim(), ATTRIBUTEFLAG);
                if (StringUtils.isNotBlank(temArray[1])) {
                    String uidArray = temArray[1].trim();
                    String cnArray = uidArray;
                    // init();
                    log.info("uid||cn: " + uidArray);
                    boolean isExist = false;
                    if (ldapDao.isUserExist(uidArray)) {
                    	isExist = true;
                    }else if(cnArray.indexOf(LDAPFLAG)>=0){
                    	cnArray = cnArray.replace(LDAPFLAG, ADFLAG1);
                    	isExist = ldapDao.isUserExist(cnArray);
                    }
                    log.info("isExist: "+isExist);
                    if (isExist) {
                        String exloginName = ldapDao.getLoginName(cnArray);

                        if (StringUtils.isNotBlank(exloginName)) {
                            String[] longNameArray = StringUtils.split(a8, ATTRIBUTEFLAG);
                            log.info("longNameArray: "+longNameArray);
                            String loginName = "";
                            if (longNameArray != null && longNameArray.length == 2) {
                                loginName = longNameArray[1].trim();
                            }
                            log.info("loginName: "+loginName);
                            V3xOrgMember member = orgManager.getMemberByLoginName(loginName);
                            // 在A8中账号是启用状态并且是单位管理员所在单位用户才可以做绑定
                            if (member != null && member.getEnabled() && memberList.contains(member)) {
                                if (checkisExitExloginNameInDB(exloginName, userMapperDao)) {
                                    continue;
                                }
                                if (checkIsExitExloginName(exloginName, usersMapperList)) {
                                    continue;
                                }

                                List<CtpOrgUserMapper> temp = new ArrayList<CtpOrgUserMapper>();
                                log.info("ExloginName: " + exloginName + " | " + "  A8: " + loginName);
                                String ExUnitCode = cnArray;
                                if(className.toLowerCase().indexOf(LdapDaoImp.class.getSimpleName().toLowerCase())==0){
                                	ExUnitCode = createExUnitCode(cnArray);
                                }
                                log.info("ExUnitCode: "+ExUnitCode);
                                CtpOrgUserMapper userMapper = new CtpOrgUserMapper();

                                currentArray[currentIndex] = loginName;
                                currentIndex++;
                                userMapper.setLoginName(loginName);
                                userMapper.setExLoginName(exloginName);
                                userMapper.setExUnitCode(ExUnitCode);
                                userMapper.setMemberId(member.getId());
                                userMapper.setType(LDAPConfig.getInstance().getType());
                                userMapper.setExPassword("null");
                                userMapper.setExId(member.getOrgAccountId().toString());
                                usersMapperList.add(userMapper);
                                userMapperDao.mapper(loginName, LDAPConfig.getInstance().getType(), temp);
                            }
                        }
                    }
                }
            }
            userMapperDao.mapper("", LDAPConfig.getInstance().getType(), usersMapperList);
            if (option == BingdingEnum.coverAll.key()) {
                this.coverBatchBinding(currentArray);
            }
        } catch (Exception e) {
            log.error("绑定人员账号发生错误：　", e);
            throw new Exception("绑定人员账号发生错误", e);
        }
    }

    /**
     * 手工单用户账号绑定
     * 
     * @param memberId
     *            人员ID
     * @param loginName
     *            用户账号
     * @param binding
     *            绑定的LDAP/AD相对条目
     * @param enabled
     *            用户账号是否可用
     * @return String 结果
     */
    public String[] handBinding(long memberId, String loginName, String binding, boolean enabled) throws Exception {
        if (!org.springframework.util.StringUtils.hasText(loginName)) {
            throw new Exception(LdapBindingMgrImp.class.getName() + " 登录名称为null或空");
        }
        List<String> logList = new ArrayList<String>();

        //        String[] bindingArrays = StringUtils.split(binding, "|");
        if (StringUtils.isBlank(binding) || !enabled) {
            userMapperDao.clearTypeLogin(LDAPConfig.getInstance().getType(), loginName, getOrgManagerDirect());
            if (!enabled) {
                logList.add(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME, "ldap.log.disable"));
            }
            logList.add(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME, "ldap.log.empty", loginName));
        } else {
            List<CtpOrgUserMapper> userMapList = userMapperDao.getExLoginNames(loginName, LDAPConfig.getInstance()
                    .getType());

            if (userMapList != null && userMapList.size() > 0) {
                for (CtpOrgUserMapper mapper : userMapList) {
                    boolean flag = false;
                    //                    for (int i = 0; i < bindingArrays.length; i++)
                    //                    {
                    if (mapper.getExUnitCode().equals(binding)) {
                        flag = true;
                        continue;
                    }

                    //                    }
                    if (!flag) {
                        userMapperDao.deleteUserMapper(mapper);
                        log.info("删除：" + "ExloginName: " + mapper.getExLoginName() + "  A8: " + loginName);
                        logList.add(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME, "ldap.log.deletentry",
                                mapper.getExLoginName(), binding.split("[,]")[0].split("[=]")[1]));
                    }
                }

            }
            // 新加
            this.init();
            //            for (int i = 0; i < bindingArrays.length; i++)
            //            {
            if (ldapDao.isUserExist(binding)) {
                // 其他A8用户没有占用此ldap用户账号才添加
                String exLoginName = ldapDao.getLoginName(binding);
                bindingPerson(exLoginName, loginName, memberId, binding, logList);

            } else {
                log.info("LDAP/AD中无此用户账号" + binding);
                logList.add(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME, "ldap.log.notentry", binding));
            }
            //            }
        }
        return logList.toArray(new String[logList.size()]);
    }

    /**
     * 修改用户密码
     * 
     * @param dn
     *            条目
     * @param oldPassWord
     *            旧密码
     * @param newPassword
     *            新密码
     * @return void
     */
    public void modifyUserPassWord(String dn, String oldPassWord, String newPassword) throws Exception {
        LDAPConfig config = LDAPConfig.getInstance();
        String type = config.getSys().getProperty("ldap.ad.enabled");
        if("ad".equals(type) && !config.getIsEnableSSL()){
        	return;
        }
    	boolean disableModifyLdapPsw = "1".equals(AppContext.getSystemProperty("ldap.disable.modify.password"));
    	if(disableModifyLdapPsw){
    		return;
    	}
    	
        if (StringUtils.isBlank(dn) || StringUtils.isBlank(oldPassWord) || StringUtils.isBlank(newPassword)) {
            throw new Exception("modifyUserPassWord null String");
        }
        try {
            init();
            boolean isAuth = ldapDao.isUserExist(dn);

            if (isAuth) {
                ldapDao.modifyUserPassWord(createDnString(dn), oldPassWord, newPassword);
                log.info(createDnString(dn) + "修改密码成功");
            } else {
                throw new Exception("此用户在LDAP/AD中不存在，修改LDAP/AD密码不成功！");
            }
        } catch (Exception e) {
            log.error("修改LDAP/AD密码不成功！", e);
            throw new Exception("修改LDAP/AD密码不成功！", e);
        }
    }

    private String createExUnitCode(String dn) {
        String baseDn = LDAPConfig.getInstance().getBaseDn();

        if (dn.indexOf(DCFLAG1) != -1) {
            dn = StringUtils.replace(dn, DCFLAG1, DCFLAG2);
        }
        if (baseDn.indexOf(DCFLAG1) != -1) {
            baseDn = StringUtils.replace(baseDn, DCFLAG1, DCFLAG2);
        }

        return StringUtils.replace(dn, "," + baseDn, "");

    }

    private String createDnString(String uerMapper) {
    	uerMapper=uerMapper.replace("\u00A0", " ");
        if (uerMapper.toLowerCase().indexOf(LDAPConfig.getInstance().getBaseDn()) != -1) {
            return uerMapper;
        }
         uerMapper = uerMapper + "," + LDAPConfig.getInstance().getBaseDn();
         String result = "";
         String[] mapperArr = uerMapper.split(",");
         for(String mp :mapperArr){
        	 if(mp.indexOf("=")>0){
        		 result = result + "," + mp;
        	 }else{
        		 result = result + "\\," + mp;
        	 }
         }
         
         if(Strings.isNotBlank(result)){
        	 result = result.substring(1);
         }else{
        	 result = uerMapper;
         }
         
         return result;
    }

    /**
     * 检查ldap用户账号是否存在装载List中
     * 
     * @param exloginName
     *            LDAP/AD账号
     * @param list
     *            装载List
     * @return true 如果存在则返回true
     */
    private boolean checkIsExitExloginName(String exloginName, List<CtpOrgUserMapper> list) {
        if (StringUtils.isBlank(exloginName)) {
            return false;
        }
        for (CtpOrgUserMapper mapper : list) {
            if (mapper.getExLoginName().equals(exloginName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查ldap用户账号是否存在A8数据库中
     * 
     * @param exloginName
     *            LDAP/AD账号
     * @param userMapperDao
     * @return true 如果存在则返回true
     */
    private boolean checkisExitExloginNameInDB(String exloginName, UserMapperDao userMapperDao) {
        if (StringUtils.isBlank(exloginName)) {
            return true;
        }
        CtpOrgUserMapper userMapper = userMapperDao.getLoginName(exloginName, LDAPConfig.getInstance().getType());
        //        CtpOrgUserMapper userMapper = userMapperDao.getExLoginNames(exloginName, LDAPConfig.getInstance().getType());
        if (userMapper == null) {
            return false;
        } else {
            try {
                userMapperDao.deleteUserMapper(userMapper);
                return false;
            } catch (Exception e) {
                log.error(e);
                return true;
            }
        }
    }

    private Map<String, String> parseLDIF(List<String> list) {
        Map<String, String> map = new HashMap<String, String>();

        if (Strings.isEmpty(list)) {
            return map;
        }
        String tem = "";
        int i = 0;
        for (String string : list) {
            if (string.indexOf(NOTE) != -1) {
                continue;
            } else if (string.indexOf(DNFLAG) != -1 && 
            		(string.indexOf(LDAPFLAG) != -1 || string.indexOf(ADFLAG1) != -1 || string.indexOf(ADFLAG2) != -1)) {
                map.put(string, "-1");
                tem = string;
                i++;
            } else if (string.indexOf(PARSEFLAGA1) != -1 || string.indexOf(PARSEFLAGA2) != -1) {
                if (i == 1) {
                    if (!"".equals(tem)) {
                        map.put(tem, string);
                        tem = "";
                    }
                    i = 0;
                }
            } else if (i == 1) {
                i = 0;
                if (!"".equals(tem)) {
                    map.remove(tem);
                    tem = "";
                }
            }
        }
        return map;
    }

    private Map<String, String> parseADLDIF(List<String> list) {
        Map<String, String> map = new HashMap<String, String>();

        if (Strings.isEmpty(list)) {
            return map;
        }
        String tem = "";
        int i = 0;
        for (String string : list) {
            if (string.indexOf(NOTE) != -1) {
                continue;
            } else if (string.indexOf(DNFLAG) != -1 && (string.indexOf(ADFLAG1) != -1 || string.indexOf(ADFLAG2) != -1)) {
                map.put(string, "-1");
                tem = string;
                i++;
            } else if (string.indexOf(PARSEFLAGA1) != -1 || string.indexOf(PARSEFLAGA2) != -1) {
                if (i == 1) {
                    if (!"".equals(tem)) {
                        map.put(tem, string);
                        tem = "";
                    }
                    i = 0;
                }
            } else if (i == 1) {
                i = 0;
                if (!"".equals(tem)) {
                    map.remove(tem);
                    tem = "";
                }
            }
        }
        return map;

    }

    private void coverBatchBinding(String[] currentArray) throws Exception {
        List<CtpOrgUserMapper> list = userMapperDao.getAllAndExId(LDAPConfig.getInstance().getType(),
                String.valueOf(CurrentUser.get().getLoginAccount()));

        for (CtpOrgUserMapper mapper : list) {
            boolean flag = false;
            for (int i = 0; i < currentArray.length; i++) {
                if (mapper.getLoginName().equals(currentArray[i])) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                if (!ldapDao.isUserExist(mapper.getExUnitCode())) {
                    userMapperDao.deleteUserMapper(mapper);
                }
            }
        }
    }

    public void setUserMapperDao(UserMapperDao userMapperDao) {
        this.userMapperDao = userMapperDao;
    }

    public void userTreeView(List<EntryValueBean> list, String baseDN) throws Exception {
        init();
        if(baseDN == null) {
        	String baseDn = "";
        	long accoutId = CurrentUser.get().getLoginAccount();
        	String value = getDefaultOU(accoutId);
        	if (Strings.isNotBlank(value)) {
        		baseDn = value;
        		if (baseDn.equalsIgnoreCase(LDAPConfig.getInstance().getBaseDn())) {
        			baseDn = "";
        		}
        		log.debug("***" + baseDn + "***");
        	}
        	
        	ldapDao.userTreeView(baseDn, list);
        }else {
        	ldapDao.userTreeView(baseDN, list);
        }
    }

    public List<EntryValueBean> ouTreeView(boolean isRoot) throws Exception {
    	log.info("----------ouTreeView strart------------------");
        init();
        log.info("----------init end------------------");
        String baseDn = "";
        if (!isRoot) {
        	 log.info("---------- is not root------------------");
            long accoutId = CurrentUser.get().getLoginAccount();
            // 查询出登录人员单位下的basedn
            String value = getDefaultOU(accoutId);
            if (Strings.isNotBlank(value)) {
                baseDn = value;
                if (baseDn.equalsIgnoreCase(LDAPConfig.getInstance().getBaseDn())) {
                    isRoot = true;
                    baseDn = "";
                }
                log.debug("***" + baseDn + "***");
            } else {
                return null;
            }

        }
        log.info("---------- is root------------------");
        return ldapDao.ouTreeView(baseDn, isRoot);
    }

    public String getDefaultOU(long accoutId) {
        V3xOrgAccount a = null;
        try {
            a = orgManager.getAccountById(accoutId);
        } catch (BusinessException e) {
            log.error("获取单位LDAP目录节点获取单位异常",e);
        }
        if(null != a) {
            return String.valueOf(a.getPOProperties("EXT_ATTR_9"));
        } else {
            return null;
        }
    }

    public String[] getUserAttributes(String dn) throws Exception {
        init();
        //        long accoutId = CurrentUser.get().getLoginAccount();
        // String baseDn="ou=people";
        // String uid=dn+","+baseDn;
        String[] userAttributs4 = ldapDao.getuserAttribute(dn);

        return userAttributs4;
    }

    public void saveOrUpdateLdapSet(V3xOrgAccount account, V3xLdapRdn value) throws Exception {
        //CTP2.0 修改成为保存在组织模型扩展字段中
        Map<String, Object> p = account.getProperties();
        p.put("ldapOu", value.getRootAccountRdn());
        account.setProperties(p);
    }

    public V3xLdapRdn findLdapSet(Long orgAccountId) throws BusinessException {
        V3xOrgAccount a = orgManager.getAccountById(orgAccountId);
        if (null == a) {
            throw new BusinessException("获取单位异常");
        } else {
            String value = String.valueOf(a.getPOProperties("EXT_ATTR_9"));
            if (Strings.isNotBlank(value)) {
                V3xLdapRdn value1 = null;
                if (value != null) {
                    value1 = new V3xLdapRdn();
                    value1.setOrgAccountId(orgAccountId);
                    value1.setRootAccountRdn(value);
                    value1.setLdapType(LDAPConfig.getInstance().getType());
                }
                return value1;
            }
        }
        return null;
    }

    public boolean createNode(V3xOrgMember member, String selectOU) throws Exception {
        String ou = "";
        String dn = "";
        //        V3xOrgProperty value = orgManageDao.findLDAPOrgproperties(CurrentUser.get().getLoginAccount(), LDAPConfig
        //                .getInstance().getType());
        //        if (value != null && (StringUtils.isBlank(selectOU))) {
        //            //            ou = value.getRootAccountRdn();
        //            ou = value.getValue();
        //        } else if (StringUtils.isNotBlank(selectOU)) {
        //            ou = selectOU;
        //        } else {
        //            return false;
        //        }
        
        V3xOrgAccount loginAccount = orgManager.getAccountById(AppContext.getCurrentUser().getLoginAccount());
		Object prop = loginAccount
                .getPOProperties("EXT_ATTR_9");
		String value = prop==null ? null : prop.toString();
        if (Strings.isNotBlank(value) && StringUtils.isBlank(selectOU)) {
            ou = value;
        } else if (Strings.isNotBlank(selectOU)) {
            ou = selectOU;
        } else {
            return false;
        }
        
        
        init();
        Class class4Ldap = org.springframework.aop.support.AopUtils.getTargetClass(ldapDao);
        if (class4Ldap.getSimpleName().equals(AdDaoImp.class.getSimpleName())) {
            dn = ADFLAG1 + "=" + member.getLoginName() + "," + ou;
        } else {
            dn = LDAPFLAG + "=" + member.getLoginName() + "," + ou;
        }
        if (ldapDao.isUserExist(dn)) {
            return false;
        } else {
            String[] parameters = new String[3];

            parameters[0] = member.getLoginName();
            parameters[1] = member.getName();
            parameters[2] = member.getPassword();

            log.debug("***" + dn + "***");
            if (ldapDao.createNode(createDnString(dn), parameters)) {
                //                StringBuilder  sb=new StringBuilder();
                bindingPerson(parameters[0], parameters[0], member.getId(), dn, null);
            } else {
                return false;
            }
        }
        return false;
    }

    private void bindingPerson(String exLoginName, String a8loginName, long memberId, String exUnitCode, List<String> sb) throws BusinessException {
        Long accountId = CurrentUser.get().getLoginAccount();
        V3xOrgMember member = orgManager.getMemberById(memberId);
        if(member == null || !member.isValid()){
            CtpOrgUserMapper uMap = userMapperDao.getLoginName(exLoginName, LDAPConfig.getInstance().getType());
            if(uMap != null){
            	userMapperDao.deleteUserMapper(uMap);
            }
        }else{
            accountId = member.getOrgAccountId();
        }
        
        CtpOrgUserMapper userMapper = null;
        if (userMapperDao.getLoginName(exLoginName, LDAPConfig.getInstance().getType()) == null) {
            userMapper = new CtpOrgUserMapper();

            userMapper.setLoginName(a8loginName);
            userMapper.setMemberId(memberId);
            userMapper.setType(LDAPConfig.getInstance().getType());
            userMapper.setExPassword("null");
            userMapper.setExLoginName(exLoginName);
            userMapper.setExUnitCode(exUnitCode);
            userMapper.setExId(accountId + "");
            userMapperDao.saveUserMapper(userMapper);
            log.info("A8账号 " + a8loginName + " 成功绑定条目： " + exUnitCode);
        } else {
            userMapper = userMapperDao.getUserMapperByExId(exLoginName, accountId + "");

            if (userMapper != null) {
                //同一单位就删除绑定
                userMapperDao.deleteUserMapper(userMapper);

                userMapper = new CtpOrgUserMapper();

                userMapper.setLoginName(a8loginName);
                userMapper.setMemberId(memberId);
                userMapper.setType(LDAPConfig.getInstance().getType());
                userMapper.setExPassword("null");
                userMapper.setExLoginName(exLoginName);
                userMapper.setExUnitCode(exUnitCode);
                userMapper.setExId(accountId + "");
                userMapperDao.saveUserMapper(userMapper);
                log.info("A8账号 " + a8loginName + " 删除之前绑定后绑定条目： " + exUnitCode);
            } else {
                log.info("添加LDAP/AD用户账号已经绑定其他单位A8用户,不能再绑定本单位下A8账号");
                if (sb != null)
                    sb.add(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME, "ldap.log.bindingmuch",
                            exUnitCode));
            }
        }
    }

    // 创建ldap/ad，不再保存文件，保存在数据库中
    public V3xLdapSwitchBean createLdapProperties(V3xLdapSwitchBean ldapSwitchBean) throws IOException {
        if (ldapSwitchBean == null) {
            ldapSwitchBean = new V3xLdapSwitchBean();
            ldapSwitchBean.setLdapUrl(SystemProperties.getInstance()
                    .getProperty(LDAPProperties.LDAP_URL, "128.2.3.123"));
            ldapSwitchBean.setLdapPort(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_PORT, "389"));
            ldapSwitchBean.setLdapBasedn(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_BASEDN,
                    "dc=seeyon,dc=com"));
            ldapSwitchBean.setLdapAdmin(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_ADMIN,
                    "cn=Manager"));
            //bug:37246 start by MENG
            String pwd = SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_PASSWORD, "secret");
            ldapSwitchBean.setLdapPassword(TextEncoder.encode(pwd));
            //bug:37246 end
            ldapSwitchBean.setLdapEnabled(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_ENABLED, "0"));
            ldapSwitchBean.setLdapAdEnabled(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_AD_ENABLED,
                    "ldap"));
            ldapSwitchBean.setLdapServerType(SystemProperties.getInstance().getProperty(
                    LDAPProperties.LDAP_SERVER_TYPE, "sun"));
            ldapSwitchBean.setLdapSSLEnabled(SystemProperties.getInstance().getProperty(
                    LDAPProperties.LDAP_SSL_ENABLED, "0"));

            ldapSwitchBean.setHostName(SystemProperties.getInstance().getProperty(LDAPProperties.AD_HOST_NAME, ""));
            ldapSwitchBean.setDomainName(SystemProperties.getInstance().getProperty(LDAPProperties.AD_DOMAIN_NAME, ""));
            ldapSwitchBean.setPrincipal(SystemProperties.getInstance().getProperty(LDAPProperties.AD_PRINCIPAL, ""));
            ldapSwitchBean.setLdapCanOauserLogon(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_CAN_OAUSERLOGON, "0"));
        }
        ldapSwitchBean.setLdapSetFrom(LDAPProperties.FROM_DATABASE);
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_SET_FROM, LDAPProperties.FROM_DATABASE);
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_URL, ldapSwitchBean.getLdapUrl());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_PORT, ldapSwitchBean.getLdapPort());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_BASEDN, ldapSwitchBean.getLdapBasedn());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_ADMIN, ldapSwitchBean.getLdapAdmin());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_PASSWORD, TextEncoder.encode(ldapSwitchBean.getLdapPassword()));
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_ENABLED, ldapSwitchBean.getLdapEnabled());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_AD_ENABLED, ldapSwitchBean.getLdapAdEnabled());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_AUTHENICATION, LDAPProperties.LDAP_SIMPLE);
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_SERVER_TYPE, ldapSwitchBean.getLdapServerType());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_SSL_ENABLED, ldapSwitchBean.getLdapSSLEnabled());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.AD_HOST_NAME, ldapSwitchBean.getHostName());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.AD_DOMAIN_NAME, ldapSwitchBean.getDomainName());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.AD_PRINCIPAL, ldapSwitchBean.getPrincipal());
        updateLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_CAN_OAUSERLOGON, ldapSwitchBean.getLdapCanOauserLogon());
        return ldapSwitchBean;
    }

    private V3xLdapSwitchBean readLdapProperties(File file) {
        V3xLdapSwitchBean bean = new V3xLdapSwitchBean();
        FileInputStream fileInput = null;
        try {
            fileInput = new FileInputStream(file);
            Properties properties = new Properties();

            properties.load(fileInput);
            bean.setLdapUrl(properties.getProperty(LDAPProperties.LDAP_URL));
            bean.setLdapPort(properties.getProperty(LDAPProperties.LDAP_PORT));
            bean.setLdapBasedn(properties.getProperty(LDAPProperties.LDAP_BASEDN));
            bean.setLdapAdmin(properties.getProperty(LDAPProperties.LDAP_ADMIN));
            bean.setLdapPassword(properties.getProperty(LDAPProperties.LDAP_PASSWORD));
            bean.setLdapEnabled(properties.getProperty(LDAPProperties.LDAP_ENABLED));
            bean.setLdapAdEnabled(properties.getProperty(LDAPProperties.LDAP_AD_ENABLED));
            bean.setLdapServerType(properties.getProperty(LDAPProperties.LDAP_SERVER_TYPE));
            bean.setLdapSSLEnabled(properties.getProperty(LDAPProperties.LDAP_SSL_ENABLED));
            bean.setDomainName(properties.getProperty(LDAPProperties.AD_DOMAIN_NAME));
            bean.setHostName(properties.getProperty(LDAPProperties.AD_HOST_NAME));
            bean.setPrincipal(properties.getProperty(LDAPProperties.AD_PRINCIPAL));
            bean.setLdapCanOauserLogon(properties.getProperty(LDAPProperties.LDAP_CAN_OAUSERLOGON));
            
            this.createLdapProperties(bean);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }finally{
        	if(fileInput != null){
        		try {
					fileInput.close();
				} catch (IOException e) {
					log.error("",e);
				}
        	}
        }
        return bean;
    }
    
    public V3xLdapSwitchBean readLdapPropertiesFromDataBase() throws Exception {
        V3xLdapSwitchBean bean = new V3xLdapSwitchBean();
        bean.setLdapUrl(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_URL));
        bean.setLdapPort(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_PORT));
        bean.setLdapBasedn(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_BASEDN));
        bean.setLdapAdmin(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_ADMIN));
        bean.setLdapPassword(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_PASSWORD));
        bean.setLdapEnabled(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_ENABLED));
        bean.setLdapAdEnabled(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_AD_ENABLED));
        bean.setLdapServerType(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_SERVER_TYPE));
        bean.setLdapSSLEnabled(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_SSL_ENABLED));
        bean.setDomainName(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.AD_DOMAIN_NAME));
        bean.setHostName(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.AD_HOST_NAME));
        bean.setPrincipal(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.AD_PRINCIPAL));
        bean.setLdapCanOauserLogon(getLdapConfigSet(IConfigPublicKey.LDAP_SET,LDAPProperties.LDAP_CAN_OAUSERLOGON));
        return bean;
    }

    /**
     * 保存ldap配置信息
     * 
     * @param ldapSwitchBean
     *         配置信息Bean
     * @return V3xLdapSwitchBean 结果
     */
    public V3xLdapSwitchBean saveLdapSwitch(V3xLdapSwitchBean ldapSwitchBean) throws Exception {
        V3xLdapSwitchBean bean = createLdapProperties(ldapSwitchBean);
        LDAPProperties.loadProperties();
        LDAPConfig.createInstance();
        return bean;
    }

    
    //如果数据库中设置信息，直接从数据库中取，如果没有
    public V3xLdapSwitchBean viewLdapSwitch() throws Exception {
    	V3xLdapSwitchBean bean = null;
    	if(getSetFrom().equals(LDAPProperties.FROM_FILE)){
    		String baseFolder = SystemEnvironment.getBaseFolder().replaceAll("\\\\", "/");
    		String fileFolderPath = baseFolder + LDAP_SWITCH_FOLDER;
    		String filePath = baseFolder + LDAP_SWITCH_FOLDER + LDAP_SWITCH_FILE;
    		File fileDir = new File(fileFolderPath);
    		File file = new File(filePath);
    		if (!fileDir.exists() || !file.exists()) {
    			bean = createLdapProperties(null);
    		}else {
    			bean = this.readLdapProperties(file);
    		}
    	}else{
    		bean = readLdapPropertiesFromDataBase();
    	}

        return bean;
    }

    public void deleteLdapSet(Long orgAccountId) throws Exception {
        //       V3xLdapRdn ldapRdn= ldapSetDao.findByAccountIdAndType(orgAccountId, LDAP_TYPE);
        //       ldapSetDao.delete(ldapRdn);
//        V3xOrgProperty orgproperty = new V3xOrgProperty();
//        orgproperty.setOrgAccountId(orgAccountId);
//        orgproperty.setName(LDAPConfig.getInstance().getType());
        //orgManageDao.removeOrgproperty(orgproperty);
        V3xOrgAccount a = orgManager.getAccountById(orgAccountId);
        if(null == a) {
            throw new BusinessException("获取单位异常");
        } else {
            a.setProperty("ldap", null);
            orgManagerDirect.updateAccount(a);
        }
    }

    public OrgManagerDirect getOrgManagerDirect() {
        return orgManagerDirect;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }
    
    private ConfigItem getNewConfigItem(String item, String value) {
        ConfigItem ldapItem = new ConfigItem();
        ldapItem.setIdIfNew();
        ldapItem.setConfigCategory(IConfigPublicKey.LDAP_SET);
        ldapItem.setConfigItem(item);
        ldapItem.setConfigValue(value);
		Date date=new Date();
		Timestamp stamp=new Timestamp(date.getTime());
		ldapItem.setCreateDate(stamp);
		ldapItem.setModifyDate(stamp);
        return ldapItem;
    }
    
    private void updateLdapConfigSet(String configCategory,String configItem,String configValue){
        ConfigItem ldapItem = configManager.getConfigItem(configCategory, configItem);
        if(null==ldapItem){
        	ldapItem = getNewConfigItem(configItem,configValue);   
        	configManager.addConfigItem(ldapItem);
        }else{
        	ldapItem.setConfigValue(configValue);
        	configManager.updateConfigItem(ldapItem);
        }
    }
    
    private String getLdapConfigSet(String configCategory,String configItem){
        ConfigItem ldapItem = configManager.getConfigItem(configCategory, configItem);
        if(null!=ldapItem){
        	return ldapItem.getConfigValue();
        }
        return "";
    }
    
    private String getSetFrom(){
    	ConfigItem configItem = configManager.getConfigItem(IConfigPublicKey.LDAP_SET, LDAPProperties.LDAP_SET_FROM);
    	if(null != configItem && configItem.getConfigValue().equals(LDAPProperties.FROM_DATABASE)){
    		return LDAPProperties.FROM_DATABASE;
    	}
    	return LDAPProperties.FROM_FILE;
    }
    
    public List<EntryValueBean> subTreeView(String parentDn,String parentId, String type) throws Exception {
        init();
        return ldapDao.getSubNode(parentDn, parentId, type);
    }
    
    @Override
    public List<EntryValueBean> getSearchCn(String baseDn,String key) throws Exception {
        init();
        return ldapDao.searchCn(baseDn, key);
    }

	@Override
	public List<EntryValueBean> subOrgTreeView(String parentDn, String parentId) throws Exception {
		init();
        return ldapDao.getOrgSubNode(parentDn, parentId);
	}
	
	@Override
	public String getLoginName(String username){
    	if (!LDAPTool.canLocalAuth()) {
    		CtpOrgUserMapper ep = userMapperDao.getLoginName(username, LDAPTool.catchLDAPConfig().getType());
    		
            if (ep != null) {
            	username = ep.getLoginName();
            } else {
            	boolean isBind = userMapperDao.isbind(username);
            	if(isBind){
            		log.info(username + " : ldap绑定关系异常！");
            		return "";
            	}else if(!LDAPConfig.getInstance().getLdapCanOauserLogon()){
            	//如果该账号没有绑定，并且默認沒有勾选可以进行a8验证，則验证失败,不再往下走
            		log.info(username + " : 开启ldap，找不到对应的绑定关系！");
            		return "";
            	}
            	//勾选可以进行a8验证，保留登录名作为a8的登录名
            }
    	}
    	return username;
	}
}
