package com.seeyon.ctp.common.usermapper.dao;

import java.util.List;

import org.springframework.util.StringUtils;

import com.seeyon.ctp.common.po.usermapper.CtpOrgUserMapper;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;

public interface UserMapperDao {
	
	public void saveUserMapper(CtpOrgUserMapper usermapper);
	
	public void updateUserMapper(CtpOrgUserMapper usermapper);
	
	public void deleteUserMapper(CtpOrgUserMapper usermapper);
	
	public CtpOrgUserMapper getLoginName(String exLoginName,String type);
	
	public List<CtpOrgUserMapper> getExLoginNames(String loginName,String type);
	
	public List<CtpOrgUserMapper> getAll();
	
	public List<CtpOrgUserMapper> getAll(String type);
	
	public void mapper(String loginName, String type, List<CtpOrgUserMapper> mappers);
	
	public void map(String loginName, String exLoginName, String exPassword
			, String type, String userId, String exUserId, Long accountId);
	
	public void clearType(String type);
	public void clearTypeLogin(String type,String login,OrgManagerDirect om);
	
	public CtpOrgUserMapper getById(long id);
	public List<CtpOrgUserMapper> getAllAndExId(String type,String exId);
	public CtpOrgUserMapper getUserMapperByExId(String exLoginName, String exId);
	public List<CtpOrgUserMapper> getAllAndExUserId(String type, String exUserId);
	public List<CtpOrgUserMapper> getExLoginNamesByMemberId(Long memberId,String type);
	public List<CtpOrgUserMapper> getMapByMemberIdEXLoginName(Long memberId,String exLoginName,String type);
	public List<CtpOrgUserMapper> getMapByMemberIdEXId(Long memberId,String exLoginId, String type);
	/**
	 * 當前登錄名作為登錄名或者外部登錄名，判斷是否在系統中已經做了綁定
	 * @param LoginName
	 * @return
	 */
	public boolean isbind(String loginName);
	/**
	 * 根據memberId，判斷此人是否進行了ad綁定
	 * @param memberId
	 * @return
	 */
	public boolean isbind(Long memberId);
}//end class
