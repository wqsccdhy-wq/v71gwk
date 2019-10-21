package com.seeyon.ctp.privilege.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.dao.AbstractHibernateDao;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.privilege.exceptions.PrivilegeExistException;
import com.seeyon.ctp.privilege.po.PrivMenu;
import com.seeyon.ctp.privilege.po.PrivRoleMenu;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.Strings;

public class RoleMenuDaoImpl extends AbstractHibernateDao<PrivRoleMenu> implements RoleMenuDao {

    private final static Log   log = LogFactory.getLog(RoleMenuDaoImpl.class);
	
    /**
     *  菜单缓存Dao
     */
    PrivilegeCache privilegeCache;
    
	/**
     * 根据角色ID删除角色与资源、菜单的关系数据，如果ext5=0不允许勾选，则不删除
     * @param roleId
     * @return
     */
    private boolean dealRoleMenu(Long roleId) {
        JDBCAgent jdbc = new JDBCAgent();
        try {
        	String sql="delete from priv_role_menu where ROLEID = " + roleId;
            jdbc.execute(sql);
      
        } catch (BusinessException e) {
            log.error("", e);
        } catch (SQLException e) {
            log.error("", e);
        } finally {
            jdbc.close();
        }
        privilegeCache.updateRoleMenu(roleId, null);
        return true;
    }

	@Override
	public boolean deleteRoleMenu(Object roleMenu) throws BusinessException {
		if (roleMenu != null) {
            PrivRoleMenu privRoleMenu = (PrivRoleMenu) roleMenu;
            StringBuilder sql = new StringBuilder();
            Map<String, Object> p = new ConcurrentHashMap<String, Object>();
            sql.append("delete from PrivRoleMenu ");
            sql.append(" where 1 = 1 ");
            if (privRoleMenu.getRoleid() != null) {
            	return dealRoleMenu(privRoleMenu.getRoleid());
            }
            if (privRoleMenu.getProductLine() != null) {
                //版本
                sql.append(" and  productLine= :productLine ");
                p.put("productLine", privRoleMenu.getProductLine());
            }
            if (privRoleMenu.getMenuid() != null) {
                //资源
                sql.append(" and menuid = :menuid ");
                p.put("menuid", privRoleMenu.getMenuid());
            }
            DBAgent.bulkUpdate(sql.toString(), p);
            return true;
        } else {
            return false;
        }
	}

	@Override
	public Long insertRoleMenu(PrivRoleMenu privRoleMenu) throws BusinessException {
		// TODO Auto-generated method stub
		Long result = -1L;
        if (privRoleMenu != null && privRoleMenu.getId() != null) {
            if (selectExist(privRoleMenu)) {
                throw new PrivilegeExistException();
            }
            result = (Long) DBAgent.save(privRoleMenu);
        }
        return result;
	}

	@Override
	public List<Long> insertRoleMenuPatchAll(List<PrivRoleMenu> roleMenu) throws BusinessException {
	        return insertRoleMenuPatchAll(roleMenu,false);
	}
	
	@Override
	public List<Long> insertRoleMenuPatchAll(List<PrivRoleMenu> roleMenu,boolean isIncrement) throws BusinessException {
		// TODO Auto-generated method stub
		 List<Long> result = null;
	        if (roleMenu != null && roleMenu.size() > 0) {
	            // 检查ID是否重复

	            
	            Map<Long, List<PrivRoleMenu>> map = new ConcurrentHashMap<Long, List<PrivRoleMenu>>();
	            JDBCAgent jdbc = new JDBCAgent();
	            try {
					jdbc.batch1Prepare("insert into PRIV_ROLE_MENU(ROLEID, MENUID, ID) values (?, ?, ?)");
					List dataList;
					for (PrivRoleMenu privRoleMenu : roleMenu) {
						dataList = new ArrayList(); 
						dataList.add(privRoleMenu.getRoleid());
						dataList.add(privRoleMenu.getMenuid());
						dataList.add(privRoleMenu.getId());
						jdbc.batch2Add(dataList);
						
						Strings.addToMap(map, privRoleMenu.getRoleid(), privRoleMenu);
					}
					
					jdbc.batch3Execute();
					
		            
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					log.info("insert into priv_role_resource error：",e);
				}finally{
					jdbc.close();
				}
	            
	            // 更新缓存
	            for (List<PrivRoleMenu> p : map.values()) {
	                privilegeCache.updateRoleMenu(null, p,isIncrement);
	            }
	            
	            //result = DBAgent.saveAll(roleResources);
	            
	        }
	        return result;
	}

	/**
     * 判断数据库中是否有记录
     * @param roleResource
     * @return 关系是否已经存在数据库中
     */
    private boolean selectExist(PrivRoleMenu roleMenu) {
        boolean result = false;
        if (roleMenu != null && roleMenu.getId() != null) {
            StringBuilder sql = new StringBuilder();
            sql.append("select count(id) from PrivRoleMenu where id = :id");
            Map<String, Long> p = new ConcurrentHashMap<String, Long>();
            p.put("id", roleMenu.getId());
            return DBAgent.count(sql.toString(), p) > 0;
        }
        return result;
    }
	 @SuppressWarnings("unchecked")
	@Override
	public List<PrivRoleMenu> selectList(PrivRoleMenu privRoleMenu) {
		// TODO Auto-generated method stub
		 List<PrivRoleMenu> result = null;
	        if (privRoleMenu == null) {
	            result = DBAgent.loadAll(entityClass);
	        } else {
	            StringBuilder sql = new StringBuilder();
	            Map<String, Object> p = new ConcurrentHashMap<String, Object>();
	            sql.append(" from PrivRoleMenu ");
	            sql.append(" where 1 = 1 ");
	            if (privRoleMenu.getRoleid() != null) {
	                //角色
	                sql.append(" and roleid = :roleid ");
	                p.put("roleid", privRoleMenu.getRoleid());
	            }
	           /* if (privRoleMenu.getProductLine() != null) {
	                //版本
	                sql.append(" and  productLine= :productLine ");
	                p.put("productLine", privRoleMenu.getProductLine());
	            }*/
	            if (privRoleMenu.getMenuid() != null) {
	                //菜单
	                sql.append(" and menuid = :menuid ");
	                p.put("menuid", privRoleMenu.getMenuid());
	            }
	            result = DBAgent.find(sql.toString(), p);
	        }
	        return result;
	    
	}
    
    
	@Override
	public List<PrivMenu> selectUnModifiable() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrivRoleMenu> selectUnModifiableByRole(Long role) {
		// TODO Auto-generated method stub
		List<PrivRoleMenu> result = new ArrayList<PrivRoleMenu>();
        if (role != null) {
            StringBuilder sql = new StringBuilder();
            Map<String, Object> p = new ConcurrentHashMap<String, Object>();
            sql.append(" from PrivRoleMenu ");
            sql.append(" where (modifiable = false or modifiable is null)");
            sql.append(" and roleid = :roleid ");
            p.put("roleid", role);
            result = DBAgent.find(sql.toString(), p);
        }
        return result;
	}

	/**
     * @param privilegeCache the privilegeCache to set
     */
    public void setPrivilegeCache(PrivilegeCache privilegeCache) {
        this.privilegeCache = privilegeCache;
    }
	 @Override
	public boolean updateRoleMenu(PrivRoleMenu privRoleMenu) throws BusinessException {
		// TODO Auto-generated method stub
		  if (privRoleMenu != null) {
	            if (!selectExist(privRoleMenu)) {
	                throw new PrivilegeExistException();
	            }
	            DBAgent.update(privRoleMenu);
	            return true;
	        } else {
	            return false;
	        }
	}
    
    @Override
	public boolean updateRoleMenuPatchAll(List<PrivRoleMenu> roleMenus) throws BusinessException {
		// TODO Auto-generated method stub
		 if (roleMenus != null && roleMenus.size() > 0) {
	            // 检查ID是否存在数据库中
	            PrivRoleMenu rolereMenu = null;
	            for (Iterator<PrivRoleMenu> iterator = roleMenus.iterator(); iterator.hasNext();) {
	            	rolereMenu = iterator.next();
	                if (rolereMenu != null && rolereMenu.getId() != null && !selectExist(rolereMenu)) {
	                    throw new PrivilegeExistException();
	                }
	                if (rolereMenu == null) {
	                	roleMenus.remove(rolereMenu);
	                }
	            }
	            DBAgent.updateAll(roleMenus);
	            return true;
	        } else {
	            return false;
	        }
	}
}
