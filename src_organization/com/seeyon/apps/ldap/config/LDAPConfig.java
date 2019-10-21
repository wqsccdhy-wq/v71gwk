package com.seeyon.apps.ldap.config;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.config.IConfigPublicKey;
import com.seeyon.ctp.common.config.manager.ConfigManager;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.po.config.ConfigItem;
import com.seeyon.ctp.util.TextEncoder;

/**
 * LDAP/AD封装属性供ROOT下使用
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-11-5
 */
public class LDAPConfig {
    private Log                        log                = LogFactory.getLog(LDAPConfig.class);

    //    private static SystemProperties sys = SystemProperties.getInstance();
    private LDAPProperties             sys                = LDAPProperties.getInstance();
    private static String              URL_HEAD_LDAP      = "ldap://";

    private int                        enable             = 0;                                                                //0不启用1不启用

    private String                     ldapUrl            = "";

    private String                     admin              = "";

    private String                     passWord           = "";

    private String                     authenication      = "";

    private String                     baseDn;

    private String                     type;

    private String                     adSslUrl           = "";

    //SSL的端口解开，允许客户修改
    //如果是389默认改成636，如果是其他则保持用户设置
    private static String              SSLPORT            = "636";
    private static String              NORMALPORT         = "389";

    public static final String         LDAPMEMBER         = "ldap.member";

    public static final String         ADMEMBER           = "ad.member";

    public static final String         ADFLAG             = "ad";

    public static final String         LDAPFlag           = "ldap";

    private static Map<String, String> localAuthMembers   = new ConcurrentHashMap<String, String>();

    static LDAPConfig                  instance           = null;
    private boolean                    isEnableLdap       = false;

    private boolean                    isEnableSSL        = false;                                                            //是否支持SSL加密

    public static final String         LDAP_RESOURCE_NAME = "com.seeyon.v3x.plugin.ldap.resource.i18n.LDAPSynchronResources";

    private String                     ldapType;

    private String                     A8ServerDomainName;                                                                    //A8服务器域名

    private String                     ip;

    private String                     adDomainName;
    
    private boolean                    ldapCanOauserLogon = false;
    
    private Date modifyDate = new Date();

    public String getAdDomainName() {
        return adDomainName;
    }

    public void setAdDomainName(String adDomainName) {
        this.adDomainName = adDomainName;
    }

    private String principal;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getA8ServerDomainName() {
        return A8ServerDomainName;
    }

    public void setA8ServerDomainName(String a8ServerDomainName) {
        A8ServerDomainName = a8ServerDomainName;
    }

    public boolean isDisabledModifyPassWord() {
        return isDisabledModifyPassWord;
    }

    public void setDisabledModifyPassWord(boolean isDisabledModifyPassWord) {
        this.isDisabledModifyPassWord = isDisabledModifyPassWord;
    }


    public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	private boolean isDisabledModifyPassWord = false;

	public static LDAPConfig getInstance(Log log) {   	
        if (instance == null) {
            instance = new LDAPConfig();
            instance.init();
            instance.setLog(log);
        }

        return instance;

    }

    public static LDAPConfig getInstance() {
    	ConfigManager configManager = (ConfigManager)AppContext.getBean("configManager");
    	ConfigItem ldapItem = configManager.getConfigItem(IConfigPublicKey.LDAP_SET, LDAPProperties.LDAP_ENABLED);
    	boolean refresh=false;
    	if(null==ldapItem){
    		refresh = true;
    	}else if(instance == null){
    		refresh = true;
    	}else{
    		if("0".equals(ldapItem.getConfigValue())){
    			instance.setEnableLdap(false);
    			refresh = false;
    		}else{
    			Date date = ldapItem.getModifyDate();
    			Long modifyDate = Long.valueOf((null==date)?0:date.getTime());
    			Long oldModifyDate = Long.valueOf(((instance.getModifyDate())==null)?-1:instance.getModifyDate().getTime());
    			if(modifyDate.compareTo(oldModifyDate)!=0){
    				refresh = true;
    			}
    		}
    	}
    	
    	if(refresh){
     		 instance = new LDAPConfig();
      		 instance.init();
      		 instance.reload();
    	}
        return instance;
    }

    public static LDAPConfig createInstance(Log log) {
        LDAPConfig lc = getInstance();
        lc.setLog(log);

        return lc;
    }

    public static LDAPConfig createInstance() {
        instance = null;
        instance = new LDAPConfig();
        instance.init();

        return instance;
    }

    public void init() {
        try {
            if (sys == null) {
                return;
            }
            String property = sys.getProperty("ldap.enabled");
            if (property == null || "".equals(property)) {
                return;
            }
            this.enable = Integer.parseInt(property);
            if (enable == 1) {
                isEnableLdap = true;
                ldapUrl = createUrlString(sys.getProperty("ldap.url"), Integer.parseInt(sys.getProperty("ldap.port")));
                if (sys.getProperty("ldap.ad.ssl.enabled") != null) {
                    isEnableSSL = "1".equals(sys.getProperty("ldap.ad.ssl.enabled"));
                }
                
                ldapCanOauserLogon =false;
                if (sys.getProperty("ldap.can.oauserlogon") != null) {
                	ldapCanOauserLogon = "1".equals(sys.getProperty("ldap.can.oauserlogon"));
                }
                
                //启用ssl设置才更新sslport
                if (isEnableSSL) {
                    SSLPORT = sys.getProperty("ldap.port");
                }
                adSslUrl = createSslUrlString(sys.getProperty("ldap.url"));
                admin = sys.getProperty("ldap.admin");
                passWord = sys.getProperty("ldap.password");
                //bug:37246 start by MENG
                passWord = TextEncoder.decode(passWord);
                //bug:37246 end
                authenication = sys.getProperty("ldap.authenication");
                baseDn = sys.getProperty("ldap.basedn");
                String hostName = sys.getProperty("ad.hostName");
                String domainName = sys.getProperty("ad.domain.name");
                if (hostName != null && domainName != null) {

                    A8ServerDomainName = hostName + "." + domainName;
                    principal = "HTTP/" + hostName + "." + domainName.toUpperCase() + "@" + domainName.toUpperCase();
                    adDomainName = sys.getProperty("ad.domain.name");
                }
                ip = sys.getProperty("ldap.url");

                createType(sys.getProperty("ldap.ad.enabled"), this.enable);
                isDisabledModifyPassWord = "1".equals(SystemProperties.getInstance().getProperty("ldap.disable.modify.password")) ? true : false;
                log.debug("LDAPConfig: " + isEnableLdap);
            }

        } catch (Exception e) {
            log.error("LDAP/AD启动出错", e);
        }
    }
    
    
    private void reload() {
        try {
            String property = getLdapSetValue("ldap.enabled");
            if (property == null || "".equals(property)) {
                return;
            }
            ConfigManager configManager = (ConfigManager)AppContext.getBean("configManager");
            ConfigItem ldapItem = configManager.getConfigItem("ldap_set", "ldap.enabled");
            this.modifyDate = ldapItem.getModifyDate();
            this.enable = Integer.parseInt(property);
            if (enable == 1) {
                isEnableLdap = true;
                ldapUrl = createUrlString(getLdapSetValue("ldap.url"), Integer.parseInt(getLdapSetValue("ldap.port")));
                if (getLdapSetValue("ldap.ad.ssl.enabled") != null) {
                    isEnableSSL = "1".equals(getLdapSetValue("ldap.ad.ssl.enabled"));
                }
                
                ldapCanOauserLogon =false;
                if (getLdapSetValue("ldap.can.oauserlogon") != null) {
                	ldapCanOauserLogon = "1".equals(getLdapSetValue("ldap.can.oauserlogon"));
                }
                
                //启用ssl设置才更新sslport
                if (isEnableSSL) {
                    SSLPORT = getLdapSetValue("ldap.port");
                }
                adSslUrl = createSslUrlString(getLdapSetValue("ldap.url"));
                admin = getLdapSetValue("ldap.admin");
                passWord = getLdapSetValue("ldap.password");
                //bug:37246 start by MENG
                passWord = TextEncoder.decode(passWord);
                //bug:37246 end
                authenication = getLdapSetValue("ldap.authenication");
                baseDn = getLdapSetValue("ldap.basedn");
                String hostName = getLdapSetValue("ad.hostName");
                String domainName = getLdapSetValue("ad.domain.name");
                if (hostName != null && domainName != null) {
                    A8ServerDomainName = hostName + "." + domainName;
                    principal = "HTTP/" + hostName + "." + domainName.toUpperCase() + "@" + domainName.toUpperCase();
                    adDomainName = getLdapSetValue("ad.domain.name");
                }
                ip = getLdapSetValue("ldap.url");

                if (this.enable == 1) {
                    String ldapOrAd = getLdapSetValue("ldap.ad.enabled");
                    if (ldapOrAd != null && !"".equals(ldapOrAd)) {
                        if (ADFLAG.equals(ldapOrAd)) {
                            type = ADMEMBER;
                        } else {
                            type = LDAPMEMBER + "." + getLdapSetValue(LDAPProperties.LDAP_SERVER_TYPE);
                        }
                    } else {
                        type = LDAPMEMBER + "." + getLdapSetValue(LDAPProperties.LDAP_SERVER_TYPE);
                    }
                }
                isDisabledModifyPassWord = "1".equals(SystemProperties.getInstance().getProperty("ldap.disable.modify.password")) ? true : false;
                log.debug("LDAPConfig: " + isEnableLdap);
            }

        } catch (Exception e) {
            log.error("LDAP/AD启动出错", e);
        }
    }
    
    private String getLdapSetValue(String configItem){
    	ConfigManager configManager = (ConfigManager)AppContext.getBean("configManager");
        ConfigItem ldapItem = configManager.getConfigItem("ldap_set", configItem);
        String property = "";
        if(null!=ldapItem){
        	property = ldapItem.getConfigValue();
        }
        return property;
    }

    private void createType(String isType, int enabled) {
        if (enabled == 1) {
            String ldapOrAd = isType;
            if (ldapOrAd != null && !"".equals(ldapOrAd)) {
                if (ADFLAG.equals(ldapOrAd)) {
                    type = ADMEMBER;
                } else {
                    type = LDAPMEMBER + "." + sys.getProperty(LDAPProperties.LDAP_SERVER_TYPE);
                }
            } else {
                type = LDAPMEMBER + "." + sys.getProperty(LDAPProperties.LDAP_SERVER_TYPE);
            }
        }
    }

    private void createLocalAuthMem(String membersList) {
        if (membersList == null || "".equals(membersList)) {
            return;
        } else {
            // if (membersList.indexOf("，") != -1)
            // {
            // membersList = StringUtils.replace(membersList, "，", ",");
            // }
            String[] memberArray = membersList.split(",");

            for (int i = 0; i < memberArray.length; i++) {
                localAuthMembers.put(memberArray[i], memberArray[i]);
            }
        }
    }

    public String createUrlString(String host, int port) {
        if (StringUtils.isNotEmpty(host)) {
            StringBuilder sb = new StringBuilder();
            if (!host.startsWith(URL_HEAD_LDAP)) {
                sb.append(URL_HEAD_LDAP);
            }
            sb.append(host);
            sb.append(":");
            sb.append(port);
            return sb.toString();
        } else {
            return URL_HEAD_LDAP + "127.0.0.1:" + port;
        }

    }

    private String createSslUrlString(String host) {
        if (host != null && !"".equals(host)) {
            StringBuilder sb = new StringBuilder();
            if (!host.startsWith(URL_HEAD_LDAP)) {
                sb.append(URL_HEAD_LDAP);
            }
            sb.append(host);
            sb.append(":");
            sb.append(SSLPORT);
            return sb.toString();
        } else {
            return URL_HEAD_LDAP + "127.0.0.1:" + SSLPORT;
        }

    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getAuthenication() {
        return authenication;
    }

    public void setAuthenication(String authenication) {
        this.authenication = authenication;
    }

    public String getPassWord() {
        passWord = TextEncoder.decode(passWord);
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(String baseDn) {
        this.baseDn = baseDn;
    }

    public String getType() {
        return type;
    }

    public static Map<String, String> getLocalAuthMembers() {
        return localAuthMembers;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public int getEnable() {
        return enable;
    }

    public String getAdSslUrl() {
        return adSslUrl;
    }

    public boolean getIsEnableLdap() {
        return isEnableLdap;
    }

    public void setEnableLdap(boolean isEnableLdap) {
        this.isEnableLdap = isEnableLdap;
    }

    public String getLdapType() {
        return ldapType;
    }

    public void setLdapType(String ldapType) {
        this.ldapType = ldapType;
    }

    public boolean getIsEnableSSL() {
        return isEnableSSL;
    }

    public void setEnableSSL(boolean isEnableSSL) {
        this.isEnableSSL = isEnableSSL;
    }

    public static String getSSLPORT() {
        return SSLPORT;
    }

    public static void setSSLPORT(String sSLPORT) {
        SSLPORT = sSLPORT;
    }

	public boolean getLdapCanOauserLogon() {
		return ldapCanOauserLogon;
	}

	public void setLdapCanOauserLogon(boolean ldapCanOauserLogon) {
		this.ldapCanOauserLogon = ldapCanOauserLogon;
	}
	
	public LDAPProperties getSys() {
		return sys;
	}

	public void setSys(LDAPProperties sys) {
		this.sys = sys;
	}

	public static String getNORMALPORT() {
		return NORMALPORT;
	}

	public static void setNORMALPORT(String nORMALPORT) {
		NORMALPORT = nORMALPORT;
	}

}
