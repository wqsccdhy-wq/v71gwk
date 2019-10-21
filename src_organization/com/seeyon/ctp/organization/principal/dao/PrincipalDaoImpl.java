/**
 * 
 */
package com.seeyon.ctp.organization.principal.dao;

import java.util.Date;
import java.util.List;

import com.seeyon.ctp.organization.po.OrgPrincipal;
import com.seeyon.ctp.util.DBAgent;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 *
 * 2010-11-16
 */
public class PrincipalDaoImpl implements PrincipalDao {

	public List<OrgPrincipal> selectAll(){
		return DBAgent.loadAll(OrgPrincipal.class);
	}
	
	public void insertBatch(List<OrgPrincipal> prps) {
	    DBAgent.saveAll(prps);
	}
	
	@Override
	public void updateBatch(List<OrgPrincipal> prps) {
	    DBAgent.updateAll(prps);
	}
	
	public void update(OrgPrincipal orgPrincipal){
	    orgPrincipal.setUpdateTime(new Date());
	    DBAgent.updateNoMerge(orgPrincipal);
	}
	
	public void delete(long principalId){
	    OrgPrincipal p = new OrgPrincipal(principalId);
	    DBAgent.delete(p);
	}
	
}
