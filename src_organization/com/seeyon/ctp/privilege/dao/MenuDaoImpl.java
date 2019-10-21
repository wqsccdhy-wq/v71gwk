/**
 * $Author: sunzhemin $
 * $Rev: 48827 $
 * $Date:: 2015-04-20 14:31:06#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Maps;
import com.seeyon.ctp.common.dao.AbstractHibernateDao;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.privilege.bo.PrivMenuBO;
import com.seeyon.ctp.privilege.enums.AppResourceCategoryEnums;
import com.seeyon.ctp.privilege.exceptions.PrivilegeExistException;
import com.seeyon.ctp.privilege.po.PrivMenu;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.SQLWildcardUtil;
import com.seeyon.ctp.util.StringUtil;

/**
 * <p>Title: 菜单的DAO实现类</p>
 * <p>Description: 本程序实现对数据库中的菜单的管理</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 */
public class MenuDaoImpl extends AbstractHibernateDao<PrivMenu> implements MenuDao {
	private final static Log logger              = LogFactory.getLog(MenuDaoImpl.class);

	private PrivilegeCache privilegeCache;	//权限缓存接口
    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.MenuDao#selectById(java.lang.Long)
     */
    @Override
    public PrivMenu selectById(Long menuId) {
        return DBAgent.get(entityClass, menuId);
    }

    public void setPrivilegeCache(PrivilegeCache privilegeCache) {
		this.privilegeCache = privilegeCache;
	}

	/* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.MenuDao#selectList(com.seeyon.ctp.privilege.po.PrivMenu)
     */
    @Override
    public List<PrivMenu> selectList(PrivMenu menu) {
        return selectList(menu, null);
    }

    @Override
    public List<PrivMenu> selectList(PrivMenu menu, FlipInfo fi) {
        return selectList(menu, fi, "=");
    }

    @Override
    public List<PrivMenu> selectListByPath(PrivMenu menu, FlipInfo fi) {
        return selectList(menu, fi, "like");
    }

    @Override
    public List<PrivMenu> selectUnModifiable(){
    	 List<PrivMenu> result = new ArrayList<PrivMenu>();
    	   StringBuilder sql = new StringBuilder();
           Map<String, Object> p = new ConcurrentHashMap<String, Object>();
           sql.append(" from PrivMenu pm ");
           sql.append(" where pm.ext5 = 0 ");
           result = DBAgent.find(sql.toString(), p);
       
       return result;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<PrivMenu> selectDisable(){
    	 List<PrivMenu> result = new ArrayList<PrivMenu>();
    	   StringBuilder sql = new StringBuilder();
           Map<String, Object> p = new ConcurrentHashMap<String, Object>();
           sql.append(" from PrivMenu pm ");
           sql.append(" where pm.ext15=0");
           result = DBAgent.find(sql.toString(), p);
           
       return result;
    }
    
    
    @Override
    public List<PrivMenu> selectSubList(PrivMenu menu) {
        PrivMenu menuTemp = new PrivMenu();
        menuTemp.setPath(menu.getPath());
        String parentLevel = menu.getExt2();
        String level = String.valueOf(Integer.parseInt(parentLevel) + 1);
        menuTemp.setExt2(level);
        return selectList(menuTemp, null, "like");
    }

    @SuppressWarnings("unchecked")
    public List<PrivMenu> selectList(PrivMenu menu, FlipInfo fi, String condition4path) {
        List<PrivMenu> result = null;
        // 为空的情况返回所有
        if (menu == null) {
            result = DBAgent.loadAll(entityClass);
        } else {
            StringBuilder sql = new StringBuilder();
            Map<String, Object> p = new ConcurrentHashMap<String, Object>();
            sql.append("from PrivMenu p");
            sql.append(" where 1=1");
            Long id = menu.getId();
            String name = menu.getName();
            String path = menu.getPath();
            String level = menu.getExt2();
            String version = menu.getExt3();
            Integer type = menu.getExt4();
            if (id != null && id != 0) {
                // 查找菜单下级菜单的情况
                if ("like".equals(condition4path)) {
                    sql.append(" and id <> :id");
                } else {
                    // 根据ID查找的情况
                    sql.append(" and id = :id");
                }
                p.put("id", id);
            }
            if (name != null) {
                sql.append(" and name like :name");
                p.put("name", "%" + SQLWildcardUtil.escape(name) + "%");
            }
            if (path != null) {
                // 查找菜单下级菜单的情况
                if ("like".equals(condition4path)) {
                    sql.append(" and path like :path");
                    p.put("path", SQLWildcardUtil.escape(path) + "%");
                } else {
                    sql.append(" and path = :path");
                    p.put("path", path);
                }
            }
            if (level != null) {
                sql.append(" and ext2 = :ext2");
                p.put("ext2", level);
            }
            if (!StringUtil.checkNull(version)) {
                sql.append(" and ext3 = :ext3");
                p.put("ext3", version);
            } else {
                sql.append(" and (ext3 is null or ext3 = '')");
            }
            if (type != null) {
                // null的情况默认为前台应用类型
                if (type == AppResourceCategoryEnums.ForegroundApplication.getKey()) {
                    sql.append(" and (ext4 = :ext4 or ext4 is null)");
                } else {
                    sql.append(" and ext4 = :ext4");
                }
                p.put("ext4", type);
            }
            sql.append(" ORDER BY p.sortid ASC");
            result = DBAgent.find(sql.toString(), p, fi);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.MenuDao#updateMenu(com.seeyon.ctp.privilege.po.PrivMenu)
     */
    @Override
    public Long updateMenu(PrivMenu menu) throws PrivilegeExistException, BusinessException {
        Long result = -1l;
        if (menu != null && menu.getId() != null) {
            //
            if (menu instanceof PrivMenuBO) {
                StringBuilder sql = new StringBuilder();
                Map<String, Object> p = new ConcurrentHashMap<String, Object>();
                sql.append("update PrivMenu set id = :id");
                p.put("id", menu.getId());
                String name = menu.getName();
                String icon = menu.getIcon();
                String target = menu.getTarget();
                Integer sortid = menu.getSortid();
                Long ext20 = menu.getExt20();
                if (name != null) {
                    sql.append(", name = :name");
                    p.put("name", name);
                }
                if (icon != null) {
                    sql.append(", icon = :icon");
                    p.put("icon", icon);
                }
                if (target != null) {
                    sql.append(", target = :target");
                    p.put("target", target);
                }
                if (sortid != null) {
                    sql.append(", sortid = :sortid");
                    p.put("sortid", sortid);
                }
                if (ext20 != null) {
                    sql.append(", ext20 = :ext20");
                    p.put("ext20", ext20);
                }
                sql.append(" where id = :oldId");
                p.put("oldId", menu.getId());
                DBAgent.bulkUpdate(sql.toString(), p);
            } else {
                // 检查要更新的菜单是否存在
                if (!selectExist(menu)) {
                    throw new PrivilegeExistException();
                }
                DBAgent.update(menu);
            }
            result = menu.getId();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.MenuDao#updateMenuPatchAll(java.util.List)
     */
    @Override
    public void updateMenuPatchAll(List<PrivMenu> menus) throws BusinessException {
        if (menus != null) {
            // 检查ID是否存在
            PrivMenu menu = null;
            for (Iterator<PrivMenu> iterator = menus.iterator(); iterator.hasNext();) {
                menu = iterator.next();
                if (menu != null && !selectExist(menu)) {
                    throw new PrivilegeExistException();
                }
            }
            try {
                DBAgent.updateAll(menus);
            } catch (Exception e) {
                throw new BusinessException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.MenuDao#insertMenu(com.seeyon.ctp.privilege.po.PrivMenu)
     */
    @Override
    public Long insertMenu(PrivMenu menu) throws PrivilegeExistException, BusinessException {
        long result = -1L;
        if (menu != null && menu.getId() != null) {
            if (menu instanceof PrivMenuBO) {
                PrivMenuBO privMenuBO = (PrivMenuBO) menu;
                menu = privMenuBO.toPO();
            }
            // 检查要保存的菜单是否已经存在
            if (selectExist(menu)) {
            	  DBAgent.update(menu);
            	  result = menu.getId();
            }else{
            	  menu.setIdIfNew();
                  result = (Long) DBAgent.save(menu);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.MenuDao#insertMenuPatchAll(java.util.List)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List insertMenuPatchAll(List<PrivMenu> menus) throws BusinessException {
        List result = null;
        if (menus == null || menus.size() == 0) {
            return result;
        }
        // 检查ID是否重复
        PrivMenu menu = null;
        for (Iterator<PrivMenu> iterator = menus.iterator(); iterator.hasNext();) {
            menu = iterator.next();
            if (menu != null && selectExist(menu)) {
                throw new PrivilegeExistException();
            }
        }
        try {
            result = DBAgent.saveAll(menus);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.seeyon.ctp.privilege.dao.MenuDao#deleteMenu(java.lang.Object)
     */
	@Override
	public boolean deleteMenu(Object menu) throws BusinessException {
		try {
			if (menu instanceof Long[]) {
				Long[] menuIds = (Long[]) menu;
				List<PrivMenu> menus = new ArrayList<PrivMenu>();
				PrivMenu menuPO = null;
				for (int i = 0; i < menuIds.length; i++) {
					//menuPO = DBAgent.get(PrivMenu.class, menuIds[i]);
					PrivMenuBO menuBO = privilegeCache.getMenuById(menuIds[i]);
					if(menuBO==null){
					    continue;//null防护
                    }
					menuPO = new PrivMenu(menuBO);
					if (null != menuPO) {
					}
					if((menuPO.getType()!=null && menuPO.getType() == 0) || menuPO.getId().equals(-2480427965080909155L)){//预制的菜单（再判断一个常用的菜单：‘表单应用’菜单的id，以防类型也被修改）
						logger.info("menuId :"+menuPO.getId() + "线程ID:" + Thread.currentThread().getName()+",系统预置菜单要被删除了！！！！！！！！！。");
			            Throwable e = new Throwable();
			            logger.info(e.getLocalizedMessage(), e);
						throw new BusinessException("menuId :"+menuPO.getId() + "线程ID:" + Thread.currentThread().getName()+",系统预置菜单要被删除了！！！！！！！！！。");
					}
				}
				DBAgent.deleteAll(menus);
			} else if (menu instanceof PrivMenu){
				PrivMenu menuPO = (PrivMenu)menu;
				if((menuPO.getType()!=null && menuPO.getType() == 0) || menuPO.getId().equals(-2480427965080909155L)){//预制的菜单（再判断一个常用的菜单：‘表单应用’菜单的id，以防类型也被修改）
					logger.info("menuId :"+menuPO.getId() + "线程ID:" + Thread.currentThread().getName()+",系统预置菜单要被删除了！！！！！！！！！。");
		            Throwable e = new Throwable();
		            logger.info(e.getLocalizedMessage(), e);
					throw new BusinessException("menuId :"+menuPO.getId() + "线程ID:" + Thread.currentThread().getName()+",系统预置菜单要被删除了！！！！！！！！！。");
				}
				delete(menu);
			}
		} catch (Exception e) {
			logger.error("",e);
			throw new BusinessException(e);
		}
		return true;
	}

    /**
     * 检查菜单的是否已经存在数据库中
     * @param menu 菜单对象
     * @return 菜单是否已经存在数据库中
     */
    private boolean selectExist(PrivMenu menu) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(id) from PrivMenu p where id = :id");
        Map<String, Long> p = new ConcurrentHashMap<String, Long>();
        p.put("id", menu.getId());
        return DBAgent.count(sql.toString(), p) > 0;
    }

    @Override
    public String selectMaxPath(String parentPath, Integer level) {
        String result = null;
        StringBuilder sql = new StringBuilder();
        sql.append("select max(path) from PrivMenu where 1=1");
        Map<String, Object> p = new ConcurrentHashMap<String, Object>();
        if (parentPath != null) {
            sql.append(" and path like :path");
            p.put("path", SQLWildcardUtil.escape(parentPath) + "%");
        }
        if (level != null) {
            sql.append(" and ext2 = :ext2");
            p.put("ext2", String.valueOf(level));
        }
        @SuppressWarnings("rawtypes")
        List resultLsit = DBAgent.find(sql.toString(), p);
        if (resultLsit.size() > 0&&resultLsit.get(0)!=null) {
            result = String.valueOf(resultLsit.get(0));
        }
        return result;
    }

    @Override
    public Long findParentMenu(PrivMenu menu) {
        Long result = 0l;
        Integer pathLength=privilegeCache.getMenuPathLength();
        if (menu != null) {
            String path = menu.getPath();
            if (!StringUtil.checkNull(path)) {
                PrivMenu menuSearch = new PrivMenu();
                String parentPath = path.substring(0, path.length() - pathLength);
                menuSearch.setPath(parentPath);
                menuSearch.setExt3(menu.getExt3());
                List<PrivMenu> results = selectList(menuSearch);
                if (results != null && results.size() == 1) {
                    result = results.get(0).getId();
                }
            }
        }
        return result;
    }

	@Override
	public void updateMenu(Map<String, Object> map) {
        StringBuilder sql = new StringBuilder("update PrivMenu set ");
        Map<String, Object> params = Maps.newHashMap();
		if(map.containsKey("sortid")){
			sql.append(" sortid=:sortid, ");
			params.put("sortid",(Integer) map.get("sortid"));
		}
		if(map.containsKey("path")){
			sql.append(" path=:path, ");
			params.put("path",(String) map.get("path"));
		}
		if(map.containsKey("target")){
			sql.append(" target=:target, ");
			params.put("target",(String) map.get("target"));
		}
		if(map.containsKey("updatedate")){
			sql.append(" updatedate=:updatedate, ");
			params.put("updatedate",(Date) map.get("updatedate"));
		}
		if(map.containsKey("updateuserid")){
			sql.append(" updateuserid=:updateuserid, ");
			params.put("updateuserid",(Long) map.get("updateuserid"));
		}
		if(params.size() == 0){
			throw new IllegalArgumentException("更新字段不能为空！");
		}
		sql.append(" id=:id ");
        sql.append(" where id = :id");
        params.put("id",(Long) map.get("id"));
        DBAgent.bulkUpdate(sql.toString(), params);
    
	}
}
