/**
 * 
 */
package com.seeyon.ctp.organization.principal;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AbstractSystemInitializer;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.common.config.IConfigPublicKey;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.security.MessageEncoder;
import com.seeyon.ctp.common.security.SecurityHelper;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.organization.dao.OrgDataClear;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.po.OrgPrincipal;
import com.seeyon.ctp.organization.principal.dao.PrincipalDao;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UniqueList;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 *
 * 2010-11-15
 */
@ProcessInDataSource(name = DataSourceName.BASE)
public class PrincipalManagerImpl extends AbstractSystemInitializer implements PrincipalManager {
    private final static Log logger = LogFactory.getLog(PrincipalManagerImpl.class);
    
    /*
     * key:loginName
     */
    private CacheMap<String, OrgPrincipal> principalBeans = null;
    /*
     * key:memberId; value:loginName
     */
    private CacheMap<Long, String> principalBeansId2LoginName = null;
    
    private PrincipalDao principalDao;
    
    private SystemConfig systemConfig;
    
    private OrgManager orgManager;
    
    public void setPrincipalDao(PrincipalDao principalDao) {
        this.principalDao = principalDao;
    }
    
    public void setSystemConfig(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

    
    @Override
    public int getSortOrder() {
        return -999;
    }
    
	public void initialize(){
        CacheAccessable cacheFactory = CacheFactory.getInstance(PrincipalManager.class);
        principalBeans = cacheFactory.createMap("PrincipalBeans");
        principalBeansId2LoginName = cacheFactory.createMap("PrincipalBeansId2LoginName");
        
        //如果其他节点已经正常启动，启动时不需要重新load一遍数据，会自动从其他节点同步数据过来。
        if(!CacheFactory.isSkipFillData()){
        	//数据修复
        	OrgDataClear.clearPrincipalDataNoCache();
        	
        	List<OrgPrincipal> principals = this.principalDao.selectAll();
        	Map<String, OrgPrincipal> _temp1 = new HashMap<String, OrgPrincipal>();
        	Map<Long, String> _temp2 = new HashMap<Long, String>();
        	for (OrgPrincipal orgPrincipal : principals) {
        		_temp1.put(orgPrincipal.getLoginName(), orgPrincipal);
        		_temp2.put(orgPrincipal.getMemberId(), orgPrincipal.getLoginName());
        	}
        	this.principalBeans.putAll(_temp1);
        	this.principalBeansId2LoginName.putAll(_temp2);
        }
        
        logger.info("加载OrgPrincipal完成");
    }
    
    public boolean isExist(String loginName) {
        return principalBeans.contains(loginName);
    }

    public boolean isExist(long memberId) {
        try {
            OrgPrincipal bean = getPrincipalBeanByMemberId(memberId);
            return bean != null;
        }
        catch (NoSuchPrincipalException e) {
            //ignore
        }
        
        return false;
    }

    public long getMemberIdByLoginName(String loginName) throws NoSuchPrincipalException {
        OrgPrincipal bean = principalBeans.get(loginName);
        if(bean == null){
            throw new NoSuchPrincipalException(loginName);
        }
        return bean.getMemberId();
    }
    
    public String getLoginNameByMemberId(long memberId) throws NoSuchPrincipalException {
        return this.principalBeansId2LoginName.get(memberId);
    }

    private OrgPrincipal getPrincipalBeanByMemberId(long memberId) throws NoSuchPrincipalException {
        String loginName = this.principalBeansId2LoginName.get(memberId);
        if(null != loginName) {
            OrgPrincipal orgPrincipal = this.principalBeans.get(loginName);
            if(null != orgPrincipal) {
                return orgPrincipal;
            }
        }
        throw new NoSuchPrincipalException(String.valueOf(memberId));
    }

    public OrganizationMessage insert(V3xOrgPrincipal principal){
        List<V3xOrgPrincipal> principals = new ArrayList<V3xOrgPrincipal>(1);
        principals.add(principal);
        
        return insertBatch(principals);
    }
    
    public OrganizationMessage insertBatch(List<V3xOrgPrincipal> principals){
        return insertBatch(principals, null);
    }

    public OrganizationMessage update(V3xOrgPrincipal principal) {
        List<V3xOrgPrincipal> principals = new ArrayList<V3xOrgPrincipal>(1);
        principals.add(principal);
        
        return updateBatch(principals);
    }

    public OrganizationMessage updateBatch(List<V3xOrgPrincipal> principals) {
        OrganizationMessage message = new OrganizationMessage();
        if(principals == null || principals.isEmpty()){
            return message;
        }
        
        for (int k = 0; k < principals.size(); k++) {
            //1、校验 数据（如果检查出错误数据，就从newEnts中移除掉，不进行操作。）
            boolean isUpdate = false;
            
            V3xOrgPrincipal principal = principals.get(k);
            
            long memberId = principal.getMemberId();
            String loginName = principal.getLoginName();
            String password = principal.getPassword();
            
            OrgPrincipal bean = null;
            try{
                bean = this.getPrincipalBeanByMemberId(memberId);
            }
            catch (NoSuchPrincipalException e) {
                message.addErrorMsg(null, OrganizationMessage.MessageStatus.MEMBER_NOT_EXIST);
                continue;
            }
            
            String oldLoginName = bean.getLoginName();
            if(!loginName.equals(oldLoginName)){//表示更换了用户 名
                if(this.isExist(loginName)){ //新用户名已经被其它账号使用了
                    message.addErrorMsg(null, OrganizationMessage.MessageStatus.PRINCIPAL_REPEAT_NAME);
                    continue;
                }
                else{
                    //设置新账号值
                    bean.setLoginName(loginName);
                    isUpdate = true;
                    //从内存中删除老的账号
                    this.principalBeans.remove(oldLoginName);
                    this.principalBeansId2LoginName.remove(memberId);
                }
            }
            
            message.addSuccessMsg(null);
            
            //如果密码修改了
            if(!OrgConstants.DEFAULT_INTERNAL_PASSWORD.equals(password)){
                //加密人员账号信息
                try {
                    MessageEncoder encode = getMessageEncoder();
                    bean.setCredentialValue(encode.encode(loginName, password));
                }
                catch (Exception e) {
                    logger.error("error set member's password code!", e);
                }
                
                bean.setExpirationDate(this.makeExpirationDate(bean));
                isUpdate = true;
            }
            
            if(isUpdate){
                bean.setEnable(true);//OA-48148
                this.principalDao.update(bean);
                this.principalBeans.put(loginName, bean);
                this.principalBeansId2LoginName.put(bean.getMemberId(), bean.getLoginName());
            }
        }
        
        return message;
    }
    
    /**
     * 计算密码的超期时间，如果当前登录者在操作密码，标示是自己的修改密码，期限要延后，否则期限就是此刻。
     * 
     * @param bean
     * @return
     */
    private Date makeExpirationDate(OrgPrincipal bean) {
        Date expirationDate = null;
        User u = AppContext.getCurrentUser();
/*        //这个密码是我自己改的，把超期时间延后
        //在登录页,找回密码处修改，也把超期时间延后
        if ((null != u && (Strings.equals(bean.getMemberId(), u.getId()) || u.isSystemAdmin() || u.isAuditAdmin())) || u==null) {
            expirationDate = this.getExpirationDate();
        } else {
            //如果管理员修改时间当即设置为超期
            expirationDate = new Date();
        }*/
        
        if(u!=null){
        	try {
        		if(!Strings.equals(bean.getMemberId(), u.getId()) && (u.isGroupAdmin() || u.isAdministrator() || orgManager.isHRAdmin())){
        			expirationDate = new Date(); //如果管理员修个人密码当即设置为超期
        		}else{
        			expirationDate = this.getExpirationDate();
        		}
			} catch (BusinessException e) {
				logger.error("判断当前人员角色信息失败!");
			}
        }else{
        	expirationDate = this.getExpirationDate();
        }
        return expirationDate;
    }
    
    public void delete(long memberId) {
        try {
            OrgPrincipal bean = getPrincipalBeanByMemberId(memberId);
            if(bean != null){
                this.principalDao.delete(bean.getId());
                //移除缓存
                principalBeans.remove(bean.getLoginName());
                principalBeansId2LoginName.remove(bean.getMemberId());
            }
        }
        catch (NoSuchPrincipalException e) {
            return;
        }
    }

    public Date getPwdExpirationDate(String loginName) {
        OrgPrincipal bean = principalBeans.get(loginName);
        if(bean != null){
            return bean.getExpirationDate();
        }
        return null;
    }
    
    public Date getCredentialUpdateDate(String loginName) {
        OrgPrincipal bean = principalBeans.get(loginName);
        if(bean != null){
            return bean.getUpdateTime();
        }
        return null;
    }

    public boolean authenticate(String loginName, String password) {
        OrgPrincipal bean = principalBeans.get(loginName);
        if(bean == null){
            return false;
        }
        String credentialValue=bean.getCredentialValue();
        boolean result = false;
        MessageEncoder encode=null;
        try {
        	if(credentialValue.indexOf("$SM3$") >= 0){
        		 encode = new MessageEncoder("SM3","BC");
        	}else{
        		 encode = new MessageEncoder();
        	}
        	 String pwdC = encode.encode(loginName, password);
             result = pwdC.equals(credentialValue);
             if(!result && logger.isDebugEnabled()){
            	 logger.debug("Password"+pwdC+"!="+credentialValue);
             }
        }
        catch (Exception e) {
            logger.warn("", e);
        }
        
        return result;
    }

    public boolean changePassword(String loginName, String password, boolean isExpirationDate)
            throws NoSuchPrincipalException {
        OrgPrincipal bean = principalBeans.get(loginName);
        if(bean == null){
            throw new NoSuchPrincipalException(loginName);
        }

        //加密人员账号信息
        boolean isSM3= false;
        MessageEncoder encode=null;
        try {
        	isSM3 = SecurityHelper.isSM3CryptPassword();
        	if(isSM3){
        		 encode = new MessageEncoder("SM3","BC");
        	}else{
        		 encode = new MessageEncoder();
        	}
            password = encode.encode(loginName, password);
        }
        catch (Exception e) {
            logger.error("error set member's password code!",e);
        }
        
        Date expirationDate = new Date();
        if(isExpirationDate){
            expirationDate = new Date();
        }
        else{
            expirationDate = getExpirationDate();
        }
        
        bean.setCredentialValue(password);
        bean.setExpirationDate(expirationDate);
        bean.setUpdateTime(new Date());
        
        this.principalDao.update(bean);
        
        //3、更新缓存
        principalBeans.notifyUpdate(loginName);
        
        return true;
    }
    
    public List<String> getAllLoginNames(){
        Set<String> set = principalBeans.keySet();
        List<String> list=new ArrayList<String>(set);
        return list;
    }

    public Map<String, Long> getLoginNameMemberIdMap() {
        Map<String, Long> map = new HashMap<String, Long>();
        for (OrgPrincipal bean: principalBeans.values()) {
            map.put(bean.getLoginName(), bean.getMemberId());
        }
        return map;
    }

    public Map<Long,String>  getMemberIdLoginNameMap() {
        Map<Long,String> map = new HashMap<Long,String>();
        for (OrgPrincipal bean: principalBeans.values()) {
            map.put(bean.getMemberId(),bean.getLoginName());
        }
        return map;
    }
    
    public String getPassword(long memberId) throws NoSuchPrincipalException{
        OrgPrincipal bean = this.getPrincipalBeanByMemberId(memberId);
        if(bean == null){
            return null;
        }
        
        return bean.getCredentialValue();
    }
    
   private Date getExpirationDate(){
        //密码过期期限
        int pwdExpirationTime = 0;
        String pwdExpirationTimeCfi = systemConfig.get(IConfigPublicKey.PWD_EXPIRATION_TIME);
        
        if(pwdExpirationTimeCfi != null){
            pwdExpirationTime = Integer.parseInt(pwdExpirationTimeCfi);
        }
        
        Date  pwdExpirationDate = null;
        if(pwdExpirationTime>0){
            //大于零时，表示设定了过期期限
            pwdExpirationDate = Datetimes.addDate(new Date(),pwdExpirationTime);
        }   
        return pwdExpirationDate;
    }
	@Override
	public void updateBatchExpirationDate(int days) {
		if (days == 0)
			return;
		List<OrgPrincipal> principals = principalDao.selectAll();

		if (principals == null || principals.isEmpty()) {
			return;
		}
		Date pwdExpirationDate = null;
		List<OrgPrincipal> updatePrincipals = new UniqueList<OrgPrincipal>();
		for (int k = 0; k < principals.size(); k++) {
			OrgPrincipal principal = principals.get(k);
			if (principal.getExpirationDate() == null) {
				continue;
			}
			pwdExpirationDate = Datetimes.addDate(principal.getExpirationDate(), days);
			principal.setExpirationDate(pwdExpirationDate);
			updatePrincipals.add(principal);
			this.principalBeans.put(principal.getLoginName(), principal);
		}
		
		this.principalDao.updateBatch(updatePrincipals);

	}
	
	private MessageEncoder getMessageEncoder() throws NoSuchAlgorithmException, NoSuchProviderException {
		boolean isSM3= false;
		MessageEncoder encode=null;
		isSM3 = SecurityHelper.isSM3CryptPassword();
		if(isSM3){
			 encode = new MessageEncoder("SM3","BC");
		}else{
			 encode = new MessageEncoder();
		}
		return encode;
	}

	@Override
	public OrganizationMessage insertBatch(List<V3xOrgPrincipal> principals, String resource) {
		OrganizationMessage message = new OrganizationMessage();
        if(principals == null || principals.isEmpty()){
            return message;
        }
        //收集符合条件的保存实体
        List<OrgPrincipal> prps = new ArrayList<OrgPrincipal>();
        
        for (int k = 0; k < principals.size(); k++) {
            //1、校验 数据（如果检查出错误数据，就从newEnts中移除掉，不进行操作。）
            V3xOrgPrincipal principal = principals.get(k);
            long memberId = principal.getMemberId();
            String loginName = principal.getLoginName();
            String password = principal.getPassword();
            
            if(this.isExist(loginName)){
                message.addErrorMsg(null, OrganizationMessage.MessageStatus.POST_REPEAT_NAME);
                continue;
            }
            
            if(this.isExist(memberId)){
                message.addErrorMsg(null, OrganizationMessage.MessageStatus.POST_REPEAT_NAME);
                continue;
            }
            
            message.addSuccessMsg(null);
            
            // 持久化Principal     
            OrgPrincipal prp = new OrgPrincipal();
            prp.setIdIfNew();
            prp.setMemberId(memberId);
            prp.setClassName(null);
            prp.setLoginName(loginName);
            prp.setEnable(true);
            prp.setCreateTime(new Date());
            prp.setUpdateTime(new Date());
            //Fix OA-19622 lilong 管理员新建出来的人员，第一次没有超期时间
            prp.setExpirationDate(null);
            
            prps.add(prp);
            
            
            //加密人员账号信息
            try {
            	/**
            	 * wangqing 2019010-17
            	 * 从OCIP导入的人员密码已经加密过，不用再加密
            	 */
            	MessageEncoder encode = getMessageEncoder();
            	if (Strings.isNotEmpty(resource)) {
            		 //加密人员账号信息
                    prp.setCredentialValue(password); 
				}else {
					 //加密人员账号信息
                    prp.setCredentialValue(encode.encode(loginName, password)); 
				}
               
            }
            catch (Exception e) {
                //ignore
            }
        }
        
        //2、保存符合条件的实体
        this.principalDao.insertBatch(prps);
        
        //3、添加到缓存
        Map<String, OrgPrincipal> _temp = new HashMap<String, OrgPrincipal>();
        Map<Long, String> _temp2 = new HashMap<Long, String>();
        for (OrgPrincipal orgPrincipal : prps) {
            _temp.put(orgPrincipal.getLoginName(), orgPrincipal);
            _temp2.put(orgPrincipal.getMemberId(), orgPrincipal.getLoginName());
        }
        
        this.principalBeans.putAll(_temp);
        this.principalBeansId2LoginName.putAll(_temp2);
        
        return message;
	}
}
