package com.seeyon.ctp.organization.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StopWatch;

import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.Strings;

/**
 * 组织模型错误数据修复，清理
 * @author wf
 *
 */
public class OrgDataClear {
	
	private final static Log orgLog = LogFactory.getLog("org");
	
	/**
	 * 数据修复，清理（组织数据表-数据库操作，用在初始化组织模型数据之前）
	 */
    static void clearOrgDataNoCache(){
    	StopWatch watch = new StopWatch();
        watch.start();
        orgLog.info("-----------组织模型数据修复开始------------");
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
        int count = 0;
        String sql = "";
    	JDBCAgent jdbc = new JDBCAgent();
    	try {
	    		{
		    		//人员状态修复
		    		sql = "select id,name,code from org_member where IS_ENABLE=1 and IS_INTERNAL=1 and IS_ADMIN=0 and (IS_DELETED=1 or ORG_DEPARTMENT_ID=-1 or ORG_POST_ID=-1 or ORG_LEVEL_ID=-1 )";
		    		jdbc.execute(sql);
		    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
		    		orgLog.info("开始人员状态修复： " + result.size() + " 条，详细信息如下：");
		    		if(Strings.isNotEmpty(result)){
		    			for (Map<String, Object> dealMap : result) {
		    				Long id = Long.valueOf(dealMap.get("id").toString());
		    				String name = dealMap.get("name") == null ? "" : dealMap.get("name").toString();
		    				String code = dealMap.get("code") == null ? "" : dealMap.get("code").toString();
		    				orgLog.info("id： " + id + ",name: "+name + "code: " + code);
		    			}
		    		}
		    		sql = "update org_member set IS_ENABLE=0 where IS_ENABLE=1 and IS_INTERNAL=1 and IS_ADMIN=0 and (IS_DELETED=1 or ORG_DEPARTMENT_ID=-1 or ORG_POST_ID=-1 or ORG_LEVEL_ID=-1 )";
		    		count = jdbc.execute(sql);
		    		orgLog.info("完成人员状态修复： " + count + " 条");
	    		}
    		
	    		{
	    			//单位、部门状态修复
	    			sql = "select id,name,code from org_unit where IS_DELETED=1 and IS_ENABLE=1";
	    			jdbc.execute(sql);
		    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
		    		orgLog.info("开始单位、部门状态修复： " + result.size() + " 条，详细信息如下：");
		    		if(Strings.isNotEmpty(result)){
		    			for (Map<String, Object> dealMap : result) {
		    				Long id = Long.valueOf(dealMap.get("id").toString());
		    				String name = dealMap.get("name") == null ? "" : dealMap.get("name").toString();
		    				String code = dealMap.get("code") == null ? "" : dealMap.get("code").toString();
		    				orgLog.info("id： " + id + ",name: "+name + "code: " + code);
		    			}
		    		}
	    			sql = "update org_unit set IS_ENABLE=0 where IS_DELETED=1 and IS_ENABLE=1";
	    			count = jdbc.execute(sql);
	    			orgLog.info("完成单位、部门状态修复： " + count + " 条");
	    		}
    		
	    		{
	    			//清理多余的岗位关系数据
	    			sql = "select id,source_id,objective0_id,objective1_id,objective2_id,objective5_id,sort_id,org_account_id from org_relationship where type='Member_Post' and source_id not in(select id from org_member)";
	    			jdbc.execute(sql);
		    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
		    		orgLog.info("开始清理多余的岗位关系数据： " + result.size() + " 条，详细信息如下：");
		    		if(Strings.isNotEmpty(result)){
		    			for (Map<String, Object> dealMap : result) {
		    				Long id = Long.valueOf(dealMap.get("id").toString());
		    				Long source_id = dealMap.get("source_id") == null ? null : Long.valueOf(dealMap.get("source_id").toString());
		    				Long objective0_id = dealMap.get("objective0_id") == null ? null : Long.valueOf(dealMap.get("objective0_id").toString());
		    				Long objective1_id = dealMap.get("objective1_id") == null ? null : Long.valueOf(dealMap.get("objective1_id").toString());
		    				Long objective2_id = dealMap.get("objective2_id") == null ? null : Long.valueOf(dealMap.get("objective2_id").toString());
		    				String objective5_id = dealMap.get("objective5_id") == null ? "" : dealMap.get("objective5_id").toString();
		    				Integer sort_id = dealMap.get("sort_id") == null ? null : Integer.valueOf(dealMap.get("sort_id").toString());
		    				Long org_account_id = dealMap.get("org_account_id") == null ? null : Long.valueOf(dealMap.get("org_account_id").toString());
		    				orgLog.info("id： " + id + ",source_id: "+source_id + "objective0_id: " + objective0_id + "objective1_id: " + objective1_id + "objective2_id: " + objective2_id + "objective5_id: " + objective5_id + "sort_id: " + sort_id + "org_account_id: " + org_account_id);
		    			}
		    		}
	    			sql = "delete from org_relationship where type='Member_Post' and source_id not in(select id from org_member)";
	    			count = jdbc.execute(sql);
	    			orgLog.info("完成清理多余的岗位关系数据： " + count + " 条");
	    		}
    		
	    		{
	    			//清除多余的角色关系数据
	    			sql = "select id,source_id,objective0_id,objective1_id,objective5_id,org_account_id from org_relationship where type='Member_Role' and objective5_id='Member' and source_id not in(select id from org_member)";
	    			jdbc.execute(sql);
		    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
		    		orgLog.info("开始清除多余的角色关系数据： " + result.size() + " 条，详细信息如下：");
		    		if(Strings.isNotEmpty(result)){
		    			for (Map<String, Object> dealMap : result) {
		    				Long id = Long.valueOf(dealMap.get("id").toString());
		    				Long source_id = dealMap.get("source_id") == null ? null : Long.valueOf(dealMap.get("source_id").toString());
		    				Long objective0_id = dealMap.get("objective0_id") == null ? null : Long.valueOf(dealMap.get("objective0_id").toString());
		    				Long objective1_id = dealMap.get("objective1_id") == null ? null : Long.valueOf(dealMap.get("objective1_id").toString());
		    				String objective5_id = dealMap.get("objective5_id") == null ? null : dealMap.get("objective5_id").toString();
		    				Long org_account_id = dealMap.get("org_account_id") == null ? null : Long.valueOf(dealMap.get("org_account_id").toString());
		    				orgLog.info("id： " + id + ",source_id: "+source_id + "objective0_id: " + objective0_id + "objective1_id: " + objective1_id + "objective5_id: " + objective5_id + "org_account_id: " + org_account_id);
		    			}
		    		}
	    			sql = "delete from org_relationship where type='Member_Role' and objective5_id='Member' and source_id not in(select id from org_member)";
	    			count = jdbc.execute(sql);
	    			orgLog.info("完成清除多余的角色关系数据： " + count + " 条");
	    		}
    		
    		//修复部门上下级关系，保证上级部门存在
	        //角色映射关系
    		
    		//int result = jdbc.executeBatch(sqls);
			//orgLog.info("数据修复总条数： " + result);
		} catch (Exception e) {
			orgLog.error("执行组织模型数据修复失败！",e);
		}finally{
			jdbc.close();
		}
    	
    	//设置关系表中主副岗的排序号，保证与member表主岗排序号一致。
    	jdbc = new JDBCAgent();
    	try {
    		List<String> sqls = new ArrayList<String>();
    		Map<Long,Integer> memberSortMap = new HashMap<Long,Integer>();//存放人员排序号的map
    		sql = "select id,sort_id from org_member where is_internal=1 and is_admin=0";
    		jdbc.execute(sql);
    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
    		if(Strings.isNotEmpty(result)){
    			for (Map<String, Object> memberMap : result) {
    				Long id = Long.valueOf(memberMap.get("id").toString());
    				Integer sortId = Integer.valueOf(memberMap.get("sort_id").toString());
    				memberSortMap.put(id, sortId);
    			}
    			
    			sql = "select id,source_id,sort_id from ORG_RELATIONSHIP where type='Member_Post' and objective5_id!='Concurrent' ";
    			jdbc.execute(sql);
    			result = (List<Map<String, Object>>) jdbc.resultSetToList();
    			if(Strings.isNotEmpty(result)){
    				for (Map<String, Object> relMap : result) {
    					Long id = Long.valueOf(relMap.get("id").toString());
    					Long source_id = Long.valueOf(relMap.get("source_id").toString());
        				Integer sortId = Integer.valueOf(relMap.get("sort_id").toString());
        				if(memberSortMap.containsKey(source_id) && !Strings.equals(memberSortMap.get(source_id),sortId)){
        					sqls.add("update org_relationship set sort_id="+memberSortMap.get(source_id)+" where id="+id);
        				}
    				}
    			}
    			
    			if(Strings.isNotEmpty(sqls)){
    				jdbc.executeBatch(sqls);
    			}
    		}
    		orgLog.info("修改关系表中主副岗的排序号： " + sqls.size() + " 条");

		} catch (Exception e) {
			orgLog.error("修改关系表中主副岗的排序号失败！",e);
		}finally{
			jdbc.close();
		}
    	
    	//TODO
/*    	//修复部门的path，保证同一个单位下的部门 path都是单位path开头的
    	jdbc = new JDBCAgent();
    	try {
    		Map<Long,String> accountPathMap = new HashMap<Long, String>();
    		sql = "select id,path from org_unit where type='Account' and id!=-1730833917365171641 order by path";
			jdbc.execute(sql);
    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
    		if(Strings.isNotEmpty(result)){
    			for (Map<String, Object> dealMap : result) {
    				Long accountId = Long.valueOf(dealMap.get("id").toString());
    				String path = dealMap.get("path").toString();
    				accountPathMap.put(accountId, path);
    			}
    		}
    		
    		for(Long accountId : accountPathMap.keySet()){
    			String path = accountPathMap.get(accountId);
    			sql = "select id,path from org_unit where type='Department' and org_account_id=" + accountId +" and path not like '"+path+"%' order by path";
    		}

		} catch (Exception e) {
			orgLog.error("修复部门的path失败！",e);
		}finally{
			jdbc.close();
		}*/
    	
    	
    	//删除不是本单位的副岗的关系数据
    	jdbc = new JDBCAgent();
    	try {
    		List<String> sqls = new ArrayList<String>();
    		Map<Long,Long> memberAccountMap = new HashMap<Long,Long>();//存放人员单位的map
    		sql = "select id,org_account_id from org_member where is_internal=1 and is_admin=0 and org_account_id!=-1 and org_account_id is not null";
    		jdbc.execute(sql);
    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
    		if(Strings.isNotEmpty(result)){
    			for (Map<String, Object> memberMap : result) {
    				Long id = Long.valueOf(memberMap.get("id").toString());
    				Long org_account_id = Long.valueOf(memberMap.get("org_account_id").toString());
    				memberAccountMap.put(id, org_account_id);
    			}
    			
    			sql = "select id,source_id,org_account_id from ORG_RELATIONSHIP where type='Member_Post' and objective5_id='Second' ";
    			jdbc.execute(sql);
    			result = (List<Map<String, Object>>) jdbc.resultSetToList();
    			if(Strings.isNotEmpty(result)){
    				for (Map<String, Object> relMap : result) {
    					Long id = Long.valueOf(relMap.get("id").toString());
    					Long source_id = Long.valueOf(relMap.get("source_id").toString());
    					Long org_account_id = Long.valueOf(relMap.get("org_account_id").toString());
        				if(memberAccountMap.containsKey(source_id) && !Strings.equals(memberAccountMap.get(source_id), org_account_id)){
        					sqls.add("delete from org_relationship where id="+id);
        				}
    				}
    			}
    			
    			if(Strings.isNotEmpty(sqls)){
    				jdbc.executeBatch(sqls);
    			}
    		}
    		orgLog.info("删除不是本单位的副岗的关系数据： " + sqls.size() + " 条");

		} catch (Exception e) {
			orgLog.error("删除不是本单位的副岗的关系数据失败！",e);
		}finally{
			jdbc.close();
		}
    	
    	//人员的岗位关系数据问题，部门-岗位-职级和所在单位不一致。（一般是人员跨单位调整后的数据问题）人员主表和关系表都清理
    	jdbc = new JDBCAgent();
    	try {
			Map<Long,Set<Long>> deptMap = new HashMap<Long,Set<Long>>();//单位下的部门
			Map<Long,Long> deptMap0 = new HashMap<Long,Long>();//部门所属的单位
			
			Map<Long,Set<Long>> postMap = new HashMap<Long,Set<Long>>();//单位下的岗位
			Map<Long,Long> postMap0 = new HashMap<Long,Long>();//岗位所属的单位
			
			Map<Long,Set<Long>> levelMap = new HashMap<Long,Set<Long>>();//单位下的职级
			Map<Long,Long> levelMap0 = new HashMap<Long,Long>();//职级所属的单位
			
			sql = "select id,org_account_id from org_unit where type='Department' order by org_account_id,path";
			jdbc.execute(sql);
    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
    		if(Strings.isNotEmpty(result)){
    			for (Map<String, Object> dealMap : result) {
    				Long org_account_id = Long.valueOf(dealMap.get("org_account_id").toString());
    				Long deptId = Long.valueOf(dealMap.get("id").toString());
    				Set<Long> deptSet = deptMap.get(org_account_id);
    				if(deptSet == null){
    					deptSet = new HashSet<Long>();
    					deptMap.put(org_account_id, deptSet);
    				}
    				deptSet.add(deptId);
    				deptMap0.put(deptId, org_account_id);
    			}
    		}
    		
    		sql = "select id,org_account_id from org_post order by org_account_id";
			jdbc.execute(sql);
    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
    		if(Strings.isNotEmpty(result)){
    			for (Map<String, Object> dealMap : result) {
    				Long org_account_id = Long.valueOf(dealMap.get("org_account_id").toString());
    				Long postId = Long.valueOf(dealMap.get("id").toString());
    				Set<Long> postSet = postMap.get(org_account_id);
    				if(postSet == null){
    					postSet = new HashSet<Long>();
    					postMap.put(org_account_id, postSet);
    				}
    				postSet.add(postId);
    				postMap0.put(postId, org_account_id);
    			}
    		}
    		
    		sql = "select id,org_account_id from org_level order by org_account_id";
			jdbc.execute(sql);
    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
    		if(Strings.isNotEmpty(result)){
    			for (Map<String, Object> dealMap : result) {
    				Long org_account_id = Long.valueOf(dealMap.get("org_account_id").toString());
    				Long levelId = Long.valueOf(dealMap.get("id").toString());
    				Set<Long> levelSet = levelMap.get(org_account_id);
    				if(levelSet == null){
    					levelSet = new HashSet<Long>();
    					levelMap.put(org_account_id, levelSet);
    				}
    				levelSet.add(levelId);
    				levelMap0.put(levelId, org_account_id);
    			}
    		}
    		
    		//清理人员主表
    		orgLog.info("开始清理人员主表的岗位数据，详细信息如下：");
    		List<String> sqls = new ArrayList<String>();
    		sql = "select id,name,org_department_id,org_post_id,org_level_id,org_account_id from org_member where IS_ENABLE=1 and IS_INTERNAL=1 and IS_ADMIN=0";
    		jdbc.execute(sql);
    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
    		if(Strings.isNotEmpty(result)){
    			for (Map<String, Object> memberMap : result) {
    				Long id = Long.valueOf(memberMap.get("id").toString());
    				String name = memberMap.get("name") == null ? "" : memberMap.get("name").toString();
    				Long org_department_id = memberMap.get("org_department_id") == null ? null : Long.valueOf(memberMap.get("org_department_id").toString());
    				Long org_post_id = memberMap.get("org_post_id") == null ? null : Long.valueOf(memberMap.get("org_post_id").toString());
    				Long org_level_id = memberMap.get("org_level_id") == null ? null : Long.valueOf(memberMap.get("org_level_id").toString());
    				Long org_account_id = memberMap.get("org_account_id") == null ? null : Long.valueOf(memberMap.get("org_account_id").toString());
    				if(org_department_id == null || org_department_id == -1 || org_account_id == null || org_account_id == -1){
    					continue;
    				}
    				
    				if(deptMap0.get(org_department_id) != null && (deptMap.get(org_account_id) == null || !deptMap.get(org_account_id).contains(org_department_id))){//人员的部门和单位不在同一个单位下
    					//修改人员的所属单位为部门的所属单位。
    					String sql0 = "update org_member set org_account_id="+deptMap0.get(org_department_id);
    					//如果岗位和部门不是同一个单位的，设置岗位id为-1
    					if(org_post_id != null && org_post_id !=-1 && !deptMap0.get(org_department_id).equals(postMap0.get(org_post_id))){
    						sql0 = sql0 + ",org_post_id=-1";
    					}
    					//如果职级和部门不是同一个单位的，设置职级id为-1
    					if(org_level_id != null && org_level_id !=-1 && !deptMap0.get(org_department_id).equals(levelMap0.get(org_level_id))){
    						sql0 = sql0 + ",org_level_id=-1";
    					}
    					sql0 = sql0 + " where id="+ id;
    					sqls.add(sql0);
    					orgLog.info("sql: " + sql0 + "; < id= " + id + ", name= " + name + ", old_org_account_id= "+org_account_id + ", old_org_post_id= "+org_post_id + ", old_org_level_id= "+org_level_id + " >");
    				}
    			}
    		}
    		if(Strings.isNotEmpty(sqls)){
    			jdbc.executeBatch(sqls);
    		}
    		orgLog.info("共修复" + sqls.size()+ "条！");
    		
    		//清理人员岗位关系表
    		orgLog.info("开始清理人员岗位关系表数据，详细信息如下：");
    		sqls = new ArrayList<String>();
    		sql = "select id,source_id,objective0_id,objective1_id,objective2_id,org_account_id from org_relationship where type='Member_Post' and source_id in("
    			   +"select id from org_member where IS_ENABLE=1 and IS_INTERNAL=1 and IS_ADMIN=0)";
    		jdbc.execute(sql);
    		result = (List<Map<String, Object>>) jdbc.resultSetToList();
    		if(Strings.isNotEmpty(result)){
    			for (Map<String, Object> relMap : result) {
    				Long id = Long.valueOf(relMap.get("id").toString());
    				Long source_id = relMap.get("source_id") == null ? null : Long.valueOf(relMap.get("source_id").toString());
    				Long objective0_id = relMap.get("objective0_id") == null ? null : Long.valueOf(relMap.get("objective0_id").toString());
    				Long objective1_id = relMap.get("objective1_id") == null ? null : Long.valueOf(relMap.get("objective1_id").toString());
    				Long objective2_id = relMap.get("objective2_id") == null ? null : Long.valueOf(relMap.get("objective2_id").toString());
    				Long org_account_id = relMap.get("org_account_id") == null ? null : Long.valueOf(relMap.get("org_account_id").toString());
    				if(objective0_id == null || objective0_id == -1 || org_account_id == null || org_account_id == -1){
    					continue;
    				}
    				
    				if(deptMap0.get(objective0_id) != null && (deptMap.get(org_account_id) == null || !deptMap.get(org_account_id).contains(objective0_id))){//人员岗位关系的部门和单位不在同一个单位下
    					//修改岗位关系的所属单位为部门的所属单位。
    					String sql0 = "update org_relationship set org_account_id="+deptMap0.get(objective0_id);
    					//如果岗位和部门不是同一个单位的，设置岗位id为-1
    					if(objective1_id != null && objective1_id !=-1 && !deptMap0.get(objective0_id).equals(postMap0.get(objective1_id))){
    						sql0 = sql0 + ",objective1_id=-1";
    					}
    					//如果职级和部门不是同一个单位的，设置职级id为-1
    					if(objective2_id != null && objective2_id !=-1 && !deptMap0.get(objective0_id).equals(levelMap0.get(objective2_id))){
    						sql0 = sql0 + ",objective2_id=-1";
    					}
    					sql0 = sql0 + " where id="+ id;
    					sqls.add(sql0);
    					orgLog.info("sql: " + sql0 + "; < id= " + id + ", source_id= " + source_id + ", old_org_account_id= "+org_account_id + ", old_objective1_id= "+objective1_id + ", old_objective2_id= "+objective2_id + " >");
    				}
    			}
    		}
    		if(Strings.isNotEmpty(sqls)){
    			jdbc.executeBatch(sqls);
    		}
    		orgLog.info("共修复" + sqls.size()+ "条！");

		} catch (Exception e) {
			orgLog.error("修改人员的岗位数据失败！",e);
		}finally{
			jdbc.close();
		}
    	
    	//修复重复的个人组的名称，保证校验通过
        jdbc = new JDBCAgent();
    	try {
    		List<String> sqls = new ArrayList<String>();
    		sql = "update org_team set name='tempTeam' where name is null";
    		jdbc.execute(sql);
    		count = 0;
    		for(int i=1001;i<=1030;i++){//循环30次，对重复数据做区分处理，1001-1030
    			sqls = new ArrayList<String>();
    			sql = "select max(id) id,name from org_team where type=1 group by OWNER_id,name having count(*)>1";
    			jdbc.execute(sql);
    			result = (List<Map<String, Object>>) jdbc.resultSetToList();
    			if(Strings.isEmpty(result)){
    				break;
    			}
    			
    			for (Map<String, Object> teamMap : result) {
    				Long id = Long.valueOf(teamMap.get("id").toString());
    				String name = teamMap.get("name").toString();
    				name = name + "-" + i;
    				sqls.add("update org_team set name='" + name + "' where id = "+id);
    			}
    			jdbc.executeBatch(sqls);
    			count = count + sqls.size();
    		}
    		orgLog.info("修改重复个人组名称： " + count + " 条");
    		
    		sql = "update org_team set type=2 where type=5";
    		count = jdbc.execute(sql);
    		orgLog.info("修改协同讨论组为系统组： " + count + " 条");
		} catch (Exception e) {
			orgLog.error("修改组信息失败！",e);
		}finally{
			jdbc.close();
		}
    	
    	
    	//删除旧的多维组织管理员设置方式下的数据
        jdbc = new JDBCAgent();
    	try {
    		sql = "delete from org_relationship where type='Business_Access'";
    		jdbc.execute(sql);
    		orgLog.info("删除旧的多维组织管理员设置方式下的数据结束 ");
		} catch (Exception e) {
			orgLog.error("删除旧的多维组织管理员设置方式下的数据失败！",e);
		}finally{
			jdbc.close();
		}
    	
		watch.stop();
		orgLog.info("-----------组织模型数据修复完成，耗时：" + watch.getTotalTimeMillis() + "毫秒------------");
    }
    
	/**
	 * 数据修复，清理（登录表-数据库操作，用在初始化登录信息之前）
	 */
    public static void clearPrincipalDataNoCache(){
    	StopWatch watch = new StopWatch();
        watch.start();
        orgLog.info("-----------登录数据修复开始-----------");
    	JDBCAgent jdbc = new JDBCAgent();
    	try {
    		//删除登录表中对应人员不存在的数据
    		String sql = "select id,login_name,member_id from org_principal where MEMBER_ID not in(select id from org_member where IS_DELETED=0)";
			jdbc.execute(sql);
			List<Map<String, Object>> result = (List<Map<String, Object>>) jdbc.resultSetToList();
			orgLog.info("开始登录数据修复开始数据： " + result.size() + " 条，详细信息如下：");
    		if(Strings.isNotEmpty(result)){
    			for (Map<String, Object> dealMap : result) {
    				Long id = Long.valueOf(dealMap.get("id").toString());
    				String objective5_id = dealMap.get("login_name") == null ? null : dealMap.get("login_name").toString();
    				Long member_id = dealMap.get("member_id") == null ? null : Long.valueOf(dealMap.get("member_id").toString());
    				orgLog.info("id： " + id + ",objective5_id: "+objective5_id + "member_id: " + member_id);
    			}
    		}
    		
    		sql = "delete from org_principal where MEMBER_ID not in(select id from org_member where IS_DELETED=0)";
    		int count = jdbc.execute(sql);
    		//删除重复的登录名记录信息
    		orgLog.info("完成登录数据修复开始数据：" + count + " 条");
		} catch (Exception e) {
			orgLog.error("执行组织模型数据修复失败！",e);
		}finally{
			jdbc.close();
		}
    	
		watch.stop();
		orgLog.info("-----------登录数据修复完成，耗时：" + watch.getTotalTimeMillis() + "毫秒-----------");
    }
}
