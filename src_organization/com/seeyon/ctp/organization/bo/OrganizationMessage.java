package com.seeyon.ctp.organization.bo;

import java.util.ArrayList;
import java.util.List;


/**
 * 组织模型实体操作返回消息类。
 * 
 * @author wusb
 * @date 2010-11-19
 */
public class OrganizationMessage {
    private boolean isSuccess = true; // 是否成功
    private List<OrgMessage> successMsgs = new ArrayList<OrgMessage>();// 成功的实体信息
    private List<OrgMessage> errorMsgs = new ArrayList<OrgMessage>();// 失败的实体信息
    private List<OrgMessage> errorMsgInfos = new ArrayList<OrgMessage>();// 失败的实体信息

    public void addSuccessMsg(V3xOrgEntity ent) {
        successMsgs.add(new OrgMessage(ent,
                OrganizationMessage.MessageStatus.SUCCESS));
    }
    
    public void addAllSuccessMsg(List<OrgMessage> messageList) {
    	successMsgs.addAll(messageList);
    }

    public void addErrorMsg(V3xOrgEntity ent, MessageStatus code) {
        isSuccess = false;
        errorMsgs.add(new OrgMessage(ent, code));
    }
    
    public void addAllErrorMsg(List<OrgMessage> messageList) {
    	errorMsgs.addAll(messageList);
    }
    
    public void addErrorMsg(V3xOrgEntity ent, String errorMsgInfo) {
        isSuccess = false;
        errorMsgInfos.add(new OrgMessage(ent, errorMsgInfo));
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public List<OrgMessage> getSuccessMsgs() {
        return successMsgs;
    }

    public List<OrgMessage> getErrorMsgs() {
        return errorMsgs;
    }
    
    public List<OrgMessage> getErrorMsgInfos() {
        return errorMsgInfos;
    }

    public class OrgMessage {
        private MessageStatus code; // 状态码
        private V3xOrgEntity ent; // 实体
        private String msgInfo; //状态信息

        private OrgMessage(V3xOrgEntity ent, MessageStatus code) {
            this.ent = ent;
            this.code = code;
        }
        
        private OrgMessage(V3xOrgEntity ent, String msgInfo) {
            this.ent = ent;
            this.msgInfo = msgInfo;
        }

        public MessageStatus getCode() {
            return code;
        }

        public V3xOrgEntity getEnt() {
            return ent;
        }
        
        public String getMsgInfo(){
        	return msgInfo;
        }

    }

    public enum MessageStatus {
        SUCCESS, // 完全成功
        ACCOUNT_REPEAT_NAME, // 单位名称重复
        ACCOUNT_REPEAT_SHORT_NAME, // 单位名称重复
        ACCOUNT_REPEAT_CODE, // 单位编码重复
        ACCOUNT_REPEAT_ADMIN_NAME, // 单位管理员名称重复
        ACCOUNT_EXIST_ENTITY, // 单位下存在未删除的组织模型实体
        /**单位下存在未删除的部门*/
        ACCOUNT_EXIST_DEPARTMENT,
        /**单位下存在未删除的单位自建角色 */
        ACCOUNT_EXIST_ROLE,
        /**单位下存在未删除的岗位 */
        ACCOUNT_EXIST_POST,
        /**单位下存在未删除的职务级别 */
        ACCOUNT_EXIST_LEVEL,
        /**单位下存在未删除的子单位 */
        ACCOUNT_EXIST_CHILDACCOUNT,
        /**单位下存在未删除的组*/
        ACCOUNT_EXIST_TEAM,
        /**单位下存在未删除的人员 */
        ACCOUNT_EXIST_MEMBER,
        DEPARTMENT_REPEAT_NAME, // 部门名称重复
        DEPARTMENT_EXIST_MEMBER, // 部门存在成员
        DEPARTMENT_EXIST_TEAM, // 部门存在组
        DEPARTMENT_PARENTID_NULL, // 父部门ID为空
        DEPARTMENT_PARENTDEPT_DISABLED, // 父部门禁用
        DEPARTMENT_PARENTDEPT_SAME, // 与父部门相同（检查父部门是否是自己）
        DEPARTMENT_PARENTDEPT_ISCHILD, // 父部门是自己的子部门
        POST_REPEAT_NAME, // 岗位名称重复
        POST_EXIST_MEMBER, // 岗位存在成员
        LEVEL_EXIST_MEMBER, // 职务存在成员
        LEVEL_EXIST_MAPPING, // 存在职务级别的映射
        MEMBER_DEPARTMENT_DISABLED, // 人员所在部门不可用
        MEMBER_POST_DISABLED, // 人员所在岗位不可用
        MEMBER_LEVEL_DISABLED, // 人员所在职务不可用
        MEMBER_REPEAT_POST, // 人员副岗和主岗重复
        MEMBER_EXIST_SIGNET, // 人员存在授权印章
        MEMBER_NOT_EXIST, // 人员不存在（常用在修改和删除时）
        PRINCIPAL_REPEAT_NAME, // 人员帐号名称重复
        DUTYLEVEL_EXIST_MEMBER, // 政务版--职级存在成员
        REPEAT_PATH, //PATH重复
        PRINCIPAL_NOT_EXIST, //登录名不存在
        ROLE_NOT_EXIST, //角色不存在
        POST_EXIST_BENCHMARK, //基准岗位已引用
        OUT_PER_NUM, //添加的人员数量大于单位剩余的可注册数量，不允许添加人员！
        LEVEL_REPEAT_NAME, // 存在职务级别的映射
        ACCOUNT_EXIST_MEMBER_ENABLE, //单位内存在有效人员不允许停用
        ACCOUNT_EXIST_DEPARTMENT_ENABLE, // 单位存在有效的部门不允许停用
        ACCOUNT_EXIST_ROLE_ENABLE, // 单位存在有效角色不允许停用
        ACCOUNT_EXIST_POST_ENABLE,//单位存在有效岗位不允许停用
        ACCOUNT_EXIST_LEVEL_ENABLE,//单位存在有效职务不允许停用
        ACCOUNT_EXIST_CHILDACCOUNT_ENABLE,//单位存在有效子单位不允许停用
        ACCOUNT_EXIST_TEAM_ENABLE,//单位存在有效组不允许停用
        /** 人员编码重复 */
        MEMBER_REPEAT_CODE,
        /** 部门编码重复 */
        DEPARTMENT_REPEAT_CODE,
        /** 人员有公共信息管理员或者审核员等身份，或其他为清理事项，删除失败 */
        MEMBER_CANNOT_DELETE,
        /** 单位自定义登录页地址不能重复 */
        ACCOUNT_CUSTOM_LOGIN_URL_DUPLICATED,
        /** 部门下存在启用子部门，不允许停用 */
        DEPARTMENT_EXIST_DEPARTMENT_ENABLE,
        ACCOUNT_VALID_SUPERACCOUNT_DISABLE,//上级单位不可用
        /** 部门下存在启用的外部子单位，不允许停用 */
        DEPARTMENT_EXIST_EXTDEPARTMENT_ENABLE,
        MEMBER_EXTERNALACCOUNT_DISABLED, // 人员所在外单位不可用
    }
}