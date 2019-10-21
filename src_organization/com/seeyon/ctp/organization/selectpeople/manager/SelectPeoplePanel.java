/**
 * 
 */
package com.seeyon.ctp.organization.selectpeople.manager;

import java.util.Date;

import com.seeyon.ctp.common.exceptions.BusinessException;

/**
 * @author tanmf
 *
 */
public interface SelectPeoplePanel {
    //缓存方式
    public static enum InitCacheType{
        //启动不要缓存
        NoCache,
        
        //启动要缓存:全局一个缓存，区分单位
        Init_Account,
        
        //启动要缓存:全局一个缓存，不区分单位
        Init_Global,
    }
    
    /**
     * 是否系统启动时就缓存jason起来；如果每个人返回的数据不一样，就不能缓存
     * @return
     */
    public InitCacheType getInitCacheType();
    
    /**
     * <pre>
     *  var Constants_Account      = "Account";
     *  var Constants_Department   = "Department";
     *  var Constants_Team         = "Team";
     *  var Constants_Post         = "Post";
     *  var Constants_Level        = "Level";
     *  var Constants_Member       = "Member";
     *  var Constants_Role         = "Role";
     *  var Constants_Outworker    = "Outworker";
     *  var Constants_concurentMembers  = "ConcurentMembers";
     *  var Constants_ExchangeAccount   = "ExchangeAccount";
     *  var Constants_OrgTeam           = "OrgTeam";
     *  var Constants_RelatePeople      = "RelatePeople";
     *  var Constants_FormField         = "FormField";
     *  var Constants_Admin             = "Admin";
     *  </pre>
     * @return
     */
    public abstract String getType();
    
    /**
     * 自定义生成选人页签。
     * @return
     */
    public boolean isCustom();
    
    /**
     * 如果沒有緩存，就返回null
     * 
     * @return
     */
    public abstract Date getLastModifyTimestamp(Long accountId) throws BusinessException;
    
    /**
     * 给选人界面用的，不要轻易修改<br>
     * 通用参数：ID：K; 显示名称：N<br>
     * 
     * 举例：
     * <pre>
     * <code>
     *  StringBuilder o = new StringBuilder();
     *  o.append("[");
        
        //TODO
        for (int i = 0; i < 12; i++) {
            if(i > 0){
                o.append(",");
            }
            o.append("{");
            o.append(TOXML_PROPERTY_id).append(":\"").append(i).append("\"");
            o.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript("交换单位" + i)).append("\"");
            o.append("}");
        }
        
        o.append("]");
     * </code>
     * 
     * <code>
     * [
     *     {
     *         K: "6532357562342345112",
     *         N: "名称1",
     *         Other: ...
     *     },
     *     {
     *         K: "1098465323575623423",
     *         N: "名称2",
     *         Other: ...
     *     }
     * ]
     * </code>
     * </pre>
     * @param o
     * @param memberId
     * @param accountId
     */
    public abstract String getJsonString(long memberId, long accountId, String extParameters) throws BusinessException;
    
    /**
     * 附件的返回数据
     * @param memberId
     * @param accountId
     * @param extParameters
     * @return
     * @throws BusinessException
     */
    public String getAdditionalJsonString(long memberId, long accountId, String extParameters) throws BusinessException;
    
    /**
     * 去单个数据的显示名称
     * @param id
     * @param accountId
     * @return Object[]{name, accountId}
     */
    public Object[] getName(String id, Long accountId);
    
}
