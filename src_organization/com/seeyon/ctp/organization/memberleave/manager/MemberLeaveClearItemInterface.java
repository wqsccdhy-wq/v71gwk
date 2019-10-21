/**
 * 
 */
package com.seeyon.ctp.organization.memberleave.manager;

import java.util.List;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.memberleave.bo.MemberLeaveDetail;

/**
 * @author tanmf
 *
 */
public interface MemberLeaveClearItemInterface {
	
	public static final String splitTag = "_";
    
    public static enum Category {
    	//角色权限
        Role,
        
        //工作管理权限：空间,文化建设，知识管理, 协同模板分类管理
        Manager,
        
        //业务管理权限：表单，协同，业务生成器
        Business,
        
        //cap4的业务管理权限：表单，协同，业务生成器
        BusinessCap4,
        
        //流程节点
        process,
        
        //报表
        reporter,
        
        //其他：身份验证狗，电子印章
        other
    }
    
    public MemberLeaveClearItemInterface.Category getCategory();

    public List<MemberLeaveDetail> getItems(long memberId) throws BusinessException;

    public Integer getSortId();
    
}
