package com.seeyon.ctp.organization.selectpeople.manager;

import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_Email;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_Mobile;
import static com.seeyon.ctp.organization.bo.V3xOrgEntity.TOXML_PROPERTY_NAME;

import java.util.List;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.Strings;

public abstract class AbstractSelectPeoplePanel implements SelectPeoplePanel {
    
    public InitCacheType getInitCacheType(){
        return InitCacheType.NoCache;
    }
    
    public boolean isCustom(){
        return false;
    }
    
    public String getAdditionalJsonString(long memberId, long accountId, String extParameters) throws BusinessException{
        return null;
    }
    
    public Object[] getName(String id, Long accountId){
        return null;
    }
    
    /**
     * 如果不是一个单位的
     * 输出格式：
     * <pre>
     * ,E[
     *    {
     *     K: "", //id
     *     N: "", //name
     *     DN: "" //department.name
     *    },
     *     K: "", //id
     *     N: "", //name
     *     DN: "" //department.name
     *    }
     * ]
     * </pre>
     * 否则什么都不输出
     * @param o
     * @param member
     * @param loginAccountId
     * @param needMobileMail
     */
    protected static void makeE(StringBuilder o, V3xOrgMember member, Long loginAccountId, boolean needMobileMail){
        if(loginAccountId != member.getOrgAccountId().longValue()){ //不是一个单位的
            o.append(",E:");
            makeE0(o, member, loginAccountId, needMobileMail);
        }
    }
    
    protected static void makeE(StringBuilder o, List<V3xOrgMember> members, Long loginAccountId, boolean needMobileMail){
        o.append(",E:[");
        
        int i = 0;
        for (V3xOrgMember member : members) {
            boolean result = makeE0(o, member, loginAccountId, needMobileMail);
            if(i++ != members.size() && result){
                o.append(",");
            }
        }
        
        o.append("]");
    }
    
    private static boolean makeE0(StringBuilder o, V3xOrgMember member, Long loginAccountId, boolean needMobileMail){
        try{
            OrgManager orgManager = (OrgManager)AppContext.getBean("orgManager");
            
            if(loginAccountId != member.getOrgAccountId().longValue()){ //不是一个单位的
                V3xOrgDepartment d = (V3xOrgDepartment)orgManager.getGlobalEntity(OrgConstants.ORGENT_TYPE.Department.name(), member.getOrgDepartmentId());
                
                o.append("{");
                o.append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(member.getName())).append("\"");
                o.append(",A:\"").append(member.getOrgAccountId()).append("\"");
                if(d != null){
                    o.append(",DN:\"").append(Strings.escapeJavascript(d.getName())).append("\"");
                }
                
                if(needMobileMail && Strings.isNotBlank(member.getEmailAddress())){
                    o.append(",").append(TOXML_PROPERTY_Email).append(":\"").append(Strings.escapeJavascript(member.getEmailAddress())).append("\"");
                }
                if(needMobileMail && Strings.isNotBlank(member.getTelNumber())){
                    o.append(",").append(TOXML_PROPERTY_Mobile).append(":\"").append(Strings.escapeJavascript(member.getTelNumber())).append("\"");
                }
                
                o.append("}");
                
                return true;
            }
        }
        catch (Exception e) {
        }
        
        return false;
     }
    
}
