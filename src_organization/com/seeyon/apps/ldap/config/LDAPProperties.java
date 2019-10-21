package com.seeyon.apps.ldap.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.seeyon.apps.ldap.manager.LdapBindingMgr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.ldap.domain.V3xLdapSwitchBean;
import com.seeyon.apps.ldap.manager.LdapBindingMgrImp;
import com.seeyon.apps.ldap.util.LdapConstants;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.config.IConfigPublicKey;
import com.seeyon.ctp.common.config.manager.ConfigManager;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.po.config.ConfigItem;
import com.seeyon.ctp.util.Strings;

/**
 * LDAP/AD封装Properties操作
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2009-1-21
 */
public class LDAPProperties {
    private static Log              log                       = LogFactory.getLog(LDAPProperties.class);

    private static LDAPProperties   instance;

    private Properties              ldapProperties            = new Properties();

    private static final String     LDAP_SWITCH_FOLDER        = "/conf";

    private static final String     LDAP_SWITCH_FILE          = "/ldap.properties";
    /**
     * ldap/ad 目录服务地址
     */
    public static final String      LDAP_URL                  = "ldap.url";
    /**
     * ldap/ad 端口
     */
    public static final String      LDAP_PORT                 = "ldap.port";
    /**
     * ldap/ad 目录服务基准DN
     */
    public static final String      LDAP_BASEDN               = "ldap.basedn";
    /**
     * ldap/ad 管理员帐号
     */
    public static final String      LDAP_ADMIN                = "ldap.admin";
    /**
     * ldap/ad 密码
     */
    public static final String      LDAP_PASSWORD             = "ldap.password";
    /**
     * ldap/ad 是否启用目录服务
     */
    public static final String      LDAP_ENABLED              = "ldap.enabled";
    /**
     * ldap/ad 目录服务类型选择
     */
    public static final String      LDAP_AD_ENABLED           = "ldap.ad.enabled";
    /**
     * ad是否支持SSL
     */
    public static final String      LDAP_SSL_ENABLED          = "ldap.ad.ssl.enabled";
    /**
     * ldap/ad 本地认证用户
     */
    //    public static final String LDAP_LOCALAUTH="ldap.localAuth";
    /**
     * ldap/ad 暂时保留，认证方式：simple
     */
    public static final String      LDAP_AUTHENICATION        = "ldap.authenication";

    public static final String      AD_HOST_NAME              = "ad.hostName";

    public static final String      AD_DOMAIN_NAME            = "ad.domain.name";

    public static final String      AD_PRINCIPAL              = "ad.principal";
    
    /**
     * 从数据库/文件中读取ldap的标示
     */
    public static final String      LDAP_SET_FROM              = "ldap.set.from";
    public static final String      FROM_FILE                  = "file";
    public static final String      FROM_DATABASE              = "database";

    public static final String      LDAP_CAN_OAUSERLOGON       = "ldap.can.oauserlogon";
    /**
     * ldap/ad 认证方式：simple
     */
    public static final String      LDAP_SIMPLE               = "simple";
    public static final String      LDAP_SERVER_TYPE          = "ldap.server.type";
    public static final String      LDAP_PASSWORD_MODIFY      = "ldap.disable.modify.password";
    //private static SystemProperties sys                       = SystemProperties.getInstance();
    //    private boolean isSystemSwitch=false;
    /**
     * A8基础目录
     */
    private static String           a8BaseFolder              = null;
    /**
     * A8基础目录的配置key
     */
    public static final String      Config_A8_base_folder_key = "A8.base.folder";

    private LDAPProperties() {
        init();
    }

    //如果数据库中有配置信息，直接读数据库，如果没有再读文件
    //保留
    private void init() {
        if (!SystemEnvironment.hasPlugin(LdapConstants.LDAP_PLUGIN_ID)) {
            return;
        }
        if(getSetFrom().equals(LDAPProperties.FROM_FILE)){
        	String baseFolder = SystemEnvironment.getBaseFolder().replaceAll("\\\\", "/");
        	String fileFolderPath = baseFolder + LDAP_SWITCH_FOLDER;
        	String filePath = baseFolder + LDAP_SWITCH_FOLDER + LDAP_SWITCH_FILE;
        	File fileDir = new File(fileFolderPath);
        	synchronized (this) {
        		File file = new File(filePath);
        		LdapBindingMgrImp ldapBindingMgr = (LdapBindingMgrImp) AppContext.getBean("ldapBindingMgr");
        		if (!fileDir.exists() || !file.exists()) {
                    try {
						ldapBindingMgr.createLdapProperties(null);
					} catch (IOException e) {
						log.error("创建ldap 默认设置失败");
					}
                    setPropertiesFromDataBase(ldapProperties);
        		}else{
        			FileInputStream fileInput = null;
        			try {
        				//如果配置在文件里，首次从文件里读取，并且存放在数据库中，以后都 在数据库中读。
        				fileInput = new FileInputStream(file);
        				
        				ldapProperties.load(fileInput);
        				String ldap_password_modify = SystemProperties.getInstance().getProperty(LDAP_PASSWORD_MODIFY, "0");
        				ldapProperties.put(LDAP_PASSWORD_MODIFY,ldap_password_modify);
        				fileInput.close();
        				
        				V3xLdapSwitchBean ldapSwitchBean = initldapSwitchBean(ldapProperties);
                        ldapBindingMgr.createLdapProperties(ldapSwitchBean);
                        
        			} catch (FileNotFoundException e) {
        				log.info(e.getMessage(), e);
        			} catch (IOException e) {
        				log.info(e.getMessage(), e);
        			}
        		}
        	}
        	log.debug(this.getClass().getName() + " init: " + filePath);
        }else{
        	setPropertiesFromDataBase(ldapProperties);
        }
    }

    public static LDAPProperties getInstance() {
        if (instance == null) {
            instance = new LDAPProperties();
        }
        return instance;
    }

    public static void loadProperties() {
        instance = null;
        if (instance == null) {
            instance = new LDAPProperties();
        }
    }

    /**
     * 根据ldap/adkey得到配置值
     * 
     * @param key
     * @return String
     */
    public String getProperty(String key) {
        return ldapProperties.getProperty(key);
    }

    /**
     * 得到A8基础目录C:\Program Files\UFseeyon\A8\Group\base
     * @return
     */
    public String getA8BaseFolder() {
        if (a8BaseFolder == null || "".equals(a8BaseFolder)) {
            String filepath = SystemProperties.getInstance().getProperty(Config_A8_base_folder_key);
            a8BaseFolder = getCanonicalPath(filepath, true);
        }

        return a8BaseFolder;
    }

    private String getCanonicalPath(String filepath, boolean isCreate) {
        if (filepath == null || "".equals(filepath)) {
            return null;
        }

        File f = new File(filepath);
        try {
            File fc = f.getCanonicalFile();

            if (isCreate) {
                fc.mkdirs();
            }

            return fc.getAbsolutePath();
        } catch (IOException e) {
        }

        return filepath;
    }
    
    private String getSetFrom(){
    	ConfigManager configManager = (ConfigManager) AppContext.getBean("configManager");
    	ConfigItem configItem = configManager.getConfigItem(IConfigPublicKey.LDAP_SET, LDAPProperties.LDAP_SET_FROM);
    	if(null != configItem && configItem.getConfigValue().equals(LDAPProperties.FROM_DATABASE)){
    		return LDAPProperties.FROM_DATABASE;
    	}
    	return LDAPProperties.FROM_FILE;
    }
    
    private V3xLdapSwitchBean initldapSwitchBean(Properties ldapProperties){
    	V3xLdapSwitchBean bean = new V3xLdapSwitchBean();
    	bean.setLdapSetFrom(FROM_DATABASE);
    	bean.setLdapUrl(ldapProperties.getProperty(LDAP_URL));
    	bean.setDomainName(ldapProperties.getProperty(AD_DOMAIN_NAME));
    	bean.setHostName(ldapProperties.getProperty(AD_HOST_NAME));
    	bean.setLdapAdEnabled(ldapProperties.getProperty(LDAP_AD_ENABLED));
    	bean.setLdapAdmin(ldapProperties.getProperty(LDAP_ADMIN));
    	bean.setLdapBasedn(ldapProperties.getProperty(LDAP_BASEDN));
    	bean.setLdapEnabled(ldapProperties.getProperty(LDAP_ENABLED));
    	bean.setLdapPassword(ldapProperties.getProperty(LDAP_PASSWORD));
    	bean.setLdapPort(ldapProperties.getProperty(LDAP_PORT));
    	bean.setLdapServerType(ldapProperties.getProperty(LDAP_SERVER_TYPE));
    	bean.setLdapSSLEnabled(ldapProperties.getProperty(LDAP_SSL_ENABLED));
    	bean.setLdapUrl(ldapProperties.getProperty(LDAP_URL));
    	bean.setPrincipal(ldapProperties.getProperty(AD_PRINCIPAL)); 
    	bean.setLdapCanOauserLogon(ldapProperties.getProperty(LDAP_CAN_OAUSERLOGON)); 
    	return bean;
    }
    
    private void initldapProperties(V3xLdapSwitchBean bean,Properties ldapProperties){
    	ldapProperties.setProperty(LDAP_SET_FROM, FROM_DATABASE);
    	ldapProperties.setProperty(LDAP_URL,Strings.isBlank(bean.getLdapUrl())?"":bean.getLdapUrl());
    	ldapProperties.setProperty(AD_DOMAIN_NAME,Strings.isBlank(bean.getDomainName())?"":bean.getDomainName());
    	ldapProperties.setProperty(AD_HOST_NAME,Strings.isBlank(bean.getHostName())?"":bean.getHostName());
    	ldapProperties.setProperty(LDAP_AD_ENABLED,Strings.isBlank(bean.getLdapAdEnabled())?"":bean.getLdapAdEnabled());
    	ldapProperties.setProperty(LDAP_ADMIN,Strings.isBlank(bean.getLdapAdmin())?"":bean.getLdapAdmin());
    	ldapProperties.setProperty(LDAP_BASEDN,Strings.isBlank(bean.getLdapBasedn())?"":bean.getLdapBasedn());
    	ldapProperties.setProperty(LDAP_ENABLED,Strings.isBlank(bean.getLdapEnabled())?"":bean.getLdapEnabled());
    	ldapProperties.setProperty(LDAP_PASSWORD,Strings.isBlank(bean.getLdapPassword())?"":bean.getLdapPassword());
    	ldapProperties.setProperty(LDAP_PORT,Strings.isBlank(bean.getLdapPort())?"":bean.getLdapPort());
    	ldapProperties.setProperty(LDAP_SERVER_TYPE,Strings.isBlank(bean.getLdapServerType())?"":bean.getLdapServerType());
    	ldapProperties.setProperty(LDAP_SSL_ENABLED,Strings.isBlank(bean.getLdapSSLEnabled())?"":bean.getLdapSSLEnabled());
    	ldapProperties.setProperty(LDAP_URL,Strings.isBlank(bean.getLdapUrl())?"":bean.getLdapUrl());
    	ldapProperties.setProperty(AD_PRINCIPAL,Strings.isBlank(bean.getPrincipal())?"":bean.getPrincipal()); 
    	ldapProperties.setProperty(LDAP_CAN_OAUSERLOGON,Strings.isBlank(bean.getLdapCanOauserLogon())?"0":bean.getLdapCanOauserLogon()); 
    	ldapProperties.setProperty(LDAP_AUTHENICATION,LDAP_SIMPLE); 
		String ldap_password_modify = SystemProperties.getInstance().getProperty(LDAP_PASSWORD_MODIFY, "0");
		ldapProperties.put(LDAP_PASSWORD_MODIFY,ldap_password_modify);
    }
    
   private void setPropertiesFromDataBase(Properties ldapProperties){
	        LdapBindingMgr ldapBindingMgr = (LdapBindingMgr) AppContext.getBean("ldapBindingMgr");
            try {
                V3xLdapSwitchBean v3xLdapSwitchBean = ldapBindingMgr.readLdapPropertiesFromDataBase();
                initldapProperties(v3xLdapSwitchBean,ldapProperties);
            } catch (Exception e) {
                log.error("", e);
            }
    }

}
