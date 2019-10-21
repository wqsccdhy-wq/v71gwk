/**
 * 
 */
package com.seeyon.ctp.organization;

import java.util.HashMap;
import java.util.Map;


/**
 * @author tanmf
 *
 */
public final class OrgConstants {
    /**
     * 组织模型7大元素的定义
     */
    public static enum ORGENT_TYPE{
        Account,
        Department,
        Team,
        Member,
        Role,
        Post,
        Level,
        /** 政务版--职级 **/
        DutyLevel,
        
        Department_Role,
        Department_Post,
        Unit,
        //V-Join单位标签
        JoinAccountTag,
        //guest账号
        Guest,
        //多单位多部门 （用于公文）
        AccountAndDepartment,
        //人员信息标签（人员信息枚举主数据）
        MemberMetadataTag,
        //多维组织线（单位）
        BusinessAccount,
        //多维组织部门
        BusinessDepartment,
        //多维组织部门
        BusinessRole,
        //访客
        Visitor
    }
    
    /**
     * 关系表的Key定义，长度不要超过50，不要有特殊字符
     */
    public static enum RelationshipType {
        /** 人员主岗*/
        Member_Post,
        /** 人员角色 */
        Member_Role,
        /** 组成员 */
        Team_Member,
        /** 组公开范围 */
        Team_PubScope,
        /** 标准岗位映射关系 */
        Banchmark_Post,
        /** 标准角色映射关系 */
        Banchmark_Role,
        /** 部门下的岗位 */
        Department_Post,
        /** 外部人员工作范围 */
        External_Workscope,
        /** 单位的访问范围 */
        Account_AccessScope,
        /** v-join平台人员工作范围 */
        External_Access,
        /** 业务线部门成员 */
        BusinessDepartment_Member,
    }

    /**
     * 关系：目标字段名
     */
    public static enum RelationshipObjectiveName{
        objective0Id,
        objective1Id,
        objective2Id,
        objective3Id,
        objective4Id,
        objective5Id,
        objective6Id,
        objective7Id,
    }

    /**
     * 人员岗位关系的类型，Relationship.type
     */
    public static enum MemberPostType {
        /** 主岗 */
        Main,
        /** 副岗 */
        Second,
        /** 兼职  */
        Concurrent,
    }
    
    /**
     * v-join平台和v5平台人员的互访范围
     */
    public static enum ExternalAccessType {
        /** v-join 平台人员可以访问的内部组织 */
        Access,
        /** v-join 平台人员可以被访问的内部组织 */
        BeAccessed,
        /** v-join 平台组织内的访问设置，（单向设置:vj单位-vj单位  ,双向设置：vj人员-vj人员，vj人员-vj单位）目前只有单向设置的情况，以后可能会有双向设置的情况**/
        VjoinAccess,
    }

    /**
     * 组成员类型，Relationship.type
     */
    public static enum TeamMemberType {
        /** 成员 */
        Member,
        /** 主管 */
        Leader,
        /** 领导 */
        SuperVisor,
        /** 关联人员 */
        Relative,
    }

    /**
     * 机构类型
     */
    public static enum UnitType{
        Account,
        Department
    }
    
    /**
     * V5/V-Join，元素类型
     */
    public static enum ExternalType{
    	Inner, //v5 单位、部门、角色、岗位、人员(内部人员和编外人员)
    	Interconnect1,//部门：V-Join外部机构 ， 角色：V-Join外部机构角色，岗位：V-Join人员岗位，人员：V-Join人员
    	Interconnect2,//部门：V-Join外部单位，  角色：V-Join外部单位角色，人员：guest账号
    	Interconnect3,//单位：V-Join虚拟单位，  角色：V-Join虚拟单位角色
    	Interconnect4 //单位：业务组织单位                部门：业务组织部门
    }
    

    /**
     * 实体对象的状态，主要用于同步
     */
    public static enum ORGENT_STATUS {
        /** 这个状态值不能使用，仅仅是因为升级上来占0这个位置的*/
        NULL,
        /** 正常 */
        NORMAL,
        /** 申请停用 */
        DISABLED,
        /** 申请删除 */
        DELETED,
        /** 申请调离 */
        TRANSFERED
    }

    /**
     * 新建时默认密码
     */
    public static final String DEFAULT_PASSWORD          = "123456";

    /**
     * 修改密码时，用户回显的，比照客户时候修改了密码
     */
    public static final String DEFAULT_INTERNAL_PASSWORD = "~`@%^*#?";

    /**
     * 组类型
     */
    public static enum TEAM_TYPE {
        /** 这个状态值不能使用，仅仅是因为升级上来占0这个位置的*/
        NULL,
        /** 个人组 */
        PERSONAL,
        /** 系统组 */
        SYSTEM,
        /** 项目组 */
        PROJECT,
        /** WebIM讨论组 */
        DISCUSS,
        /** 协同讨论组 */
        COLTEAM,
    }
    public static enum TEAM_SCOPE {
        /** 这个状态值不能使用，仅仅是因为升级上来占0这个位置的*/
        NULL, 
        /** 公开组 */
        OPEN,
        /** 私有组 */
        PERSONAL,
        
    }

    /**
     *  角色的关联类型，单位角色/部门角色
     */
    public static enum ROLE_BOND {
        /** 集团角色*/
        GROUP,
        /** 单位角色 */
        ACCOUNT,
        /** 部门角色 */
        DEPARTMENT,
        /** 这个状态值不能使用，仅仅是因为升级上来占3这个位置的*/
        NULL1,
        /** 这个状态值不能使用，仅仅是因为升级上来占4这个位置的*/
        NULL2,
        /** 业务生成器角色 */
        BUSINESS,
        /** SSO菜单集成 */
        SSO,
        /** 报表空间  */
        REPORTSPACE
    }

    /**
     * 系统预置后台角色，不包含任何应用角色，<b>名称不允许有下划线</b>
     */
    public static enum Role_SYSTEM_NAME {
        /** 系统管理员 */
        SystemAdmin,
        /** 审计管理员 */
        AuditAdmin,
        /** 集团管理员 */
        GroupAdmin,
        /** 单位后台管理员 */
        AccountAdministrator,
        /** 超级管理员 */
        SuperAdmin,
        //增加如下枚举是为了保证以下角色全集团唯一，但他们不属于系统预置后台角色
        /** 集团文档空间管理员，为文档发送到集团空间使用*/
        GroupManager,
        /** 集团公告管理员*/
        GroupBulletinAdmin,
        /** 集团公告审核员*/
        GroupBulletinAuditor,
        /** 集团新闻管理员*/
        GroupNewsAdmin,
        /** 集团新闻审核员*/
        GroupNewsAuditor,
        /** 集团调查管理员*/
        GroupSurveyAdmin,
        /** 集团调查审核员*/
        GroupSurveyAuditor,
        /** 集团讨论审核员*/
        GroupDiscussAdmin,
        /**Guest特殊账号*/
        GuestAccount,//在数据库中有对应的角色数据，每个guest账号都会和这个角色建立关系
        /**业务线管理员*/
        BusinessOrganizationManager
    }
    /**
     * 应用预置角色，<b>名称不允许有下划线</b>
     */
    public static enum Role_NAME {
    	/** 这个状态值不能使用，仅仅是因为升级上来占0这个位置的*/
        NULL,
	    /** 集团文档空间管理员，为文档发送到集团空间使用*/
	    GroupManager,
		/** 单位文档空间管理员，为文档发送到单位空间使用，并且可以在知识管理中进行博客管理 */
        AccountAdmin,		
        /** 部门管理员 */
        DepAdmin,
        /**
         * 单位会议联络员
         */
        UnitMeetContact,
        /**
         * 部门会议联络员
         */
        DepMeetContact,
        /**
         * 单位督办联络员
         */
        UnitSupervision,
        /**
         * 部门督办联络员
         */
        DeptSupervision,
        /**
         * 督察人员角色
         */
        SuperviseStaff,
        /** 部门主管 */
        DepManager,
        /** 部门分管领导 */
        DepLeader,
        /** 部門公文收發員 */
        Departmentexchange,	
		/** 编外人员*/
		ExternalStaff,
		/** 普通人员*/
		GeneralStaff,
		/** 协同模板管理员*/
		TtempletManager,
		/** 考勤管理员 */
		AttendanceAdmin,
        /** HR管理员 */
        HrAdmin,
        /** 工资管理员 */
        SalaryAdmin,
        /** 表单管理员 */
        FormAdmin,
		/** 车辆管理员 */
		CarsAdmin,
		/** 图书管理员 */
		BooksAdmin,
		/** 办公用品管理员 */
		StocksAdmin,
        /** 会议室管理员*/
        MeetingRoomAdmin,
		/** 公文管理员*/
		EdocManagement,
        /** 單位公文收發員 */
        Accountexchange,
        /** 发文拟文*/
        SendEdoc,
        /** 签报拟文*/
        SignEdoc,
        /** 收文登记*/
        RecEdoc,
		/** 绩效管理员*/
        PerformanceAdmin,
		/** 集团公告管理员*/
		GroupBulletinAdmin,
		/** 集团公告审核员*/
		GroupBulletinAuditor,
		/** 集团新闻管理员*/
		GroupNewsAdmin,
		/** 集团新闻审核员*/
		GroupNewsAuditor,
		/** 集团调查管理员*/
		GroupSurveyAdmin,
		/** 集团调查审核员*/
		GroupSurveyAuditor,
		/** 集团讨论管理员*/
		GroupDiscussAdmin,
		/** 单位公告管理员*/
		UnitBulletinAdmin,
		/** 单位公告审核员*/
		UnitBulletinAuditor,
		/** 单位新闻管理员*/
		UnitNewsAdmin,
		/** 单位新闻审核员*/
		UnitNewsAuditor,
		/** 单位调查管理员*/
		UnitSurveyAdmin,
		/** 单位调查审核员*/
		UnitSurveyAuditor,
		/** 单位讨论管理员*/
		UnitDiscussAdmin,
		/** 个人博客*/
        MemberBlog,
		/** 知识管理集团库管理员 */
        DocGroupAdmin,
		/** 知识管理单位库管理员 */
        DocUnitAdmin,
        /** 单位主管 */
        AccountManager,  
        /** 系统管理员 */
        SystemAdmin,
        /** 审计管理员 */
        AuditAdmin,
        /** 集团管理员 */
        GroupAdmin,
        /** 单位后台管理员 */
        AccountAdministrator,
        /** 超级管理员 */
        SuperAdmin,
        /** 部门空间角色 */
        DeptSpace,
        /** 归档公文修改 */
        EdocModfiy,
        /** 信息上报人 */
        InfoReporter,
        /** 期刊审核人 */
        MagazineAudit,
        /** 信息报送管理员 */
        InfoManager,
        /** G6 角色 收文登记 */
        RegisterEdoc,
        /** G6 角色 收文分发 */
        FenfaEdoc,
        /** 驾驶员 */
        CarsDriver,
        /** 办公设备管理员 */
        AssetsAdmin,
        /** 报表管理员 */
        ReportAdmin,
        /** REST 管理员 */
        RESTManager,
        /** 单位分管领导 */
        AccountLeader,
        /** 大秀管理员*/
        ShowAdmin,
        /**A82\81 流程绩效用户授权*/
        WfanalysisAuth,
        /**A82\81 行为绩效用户授权*/
        BehavioranalysisAuth,
        
        /** 集团用车管理员*/
        GroupSpecialTrainAdmin,
        /** 单位用车管理员*/
        AccountSpecialTrainAdmin,
        /** 部门用车管理员*/
        DepartmentSpecialTrainAdmin,
        /**员工福利管理员*/
        EmployeeBenefitAdmin,
        /** vjoin人员（v-join平台人员的默认角色）*/
        VjoinStaff,
        /** 机构负责人(v-join平台预制的角色)*/
        VjoinUnitManager,
        /** 单位负责人(v-join平台预制的角色)*/
        VjoinAccountManager,
        /** 子机构管理员(v-join平台预制的角色)*/
        VjoinSubManager,
        /** 流程知识中心*/
        ProcessAssets,
        /**7.0报表管理员---非帆软报表管理员*/
        VReportAdmin,
        /** 携程管理员 */
        CtripAdmin,
        /**业务设计师 角色**/
        BusinessDesigner,
        /** 登录前门户的Guest账号  虚拟角色，暂时只是用于Rbac权限控制**/
        GuestDefault,
        /** 大屏Guest账号  虚拟角色，暂时只是用于Rbac权限控制**/
        GuestScreen,
		/**收文退件*/
        RecEdocBack,
        /**单位公文收文员*/
        AccountGovdocRec,
        /**单位公文发文员*/
        AccountGovdocSend,
        /**单位公文统计*/
        AccountGovdocStat,
        /**快速发文*/
        EdocQuickSend,
        /**公文列表*/
        EdocList,
        /**部门公文收文员*/
        DepartmentGovdocRec,
        /**部门公文发文员*/
        DepartmentGovdocSend,
        /**代领导批示*/
        LeaderPishi,
        /**业务线管理员*/
        BusinessOrganizationManager
    }

    /**
     * 角色分类
     */
    public static enum Role_Category {
        Role,
    }

    /**
     * 单位是否可以访问，保存在关系表中
     * 用于关系表object5Id存储，对应key--Account_AccessScope
     */
    public static enum Account_AccessScope_Type {
        /** 不可以访问 */
        NOT_ACCESS,
        /** 可以访问 */
        CAN_ACCESS,
        /** 分级设置 */
        LEVEL
    }
    
    /** 只勾选了上级不可见 */
    public static final Long Account_AccessScope_Level_0 = 0L;
    /** 只勾选了平级不可见 */
    public static final Long Account_AccessScope_Level_1 = 1L;
    /** 只勾选了下级不可见 */
    public static final Long Account_AccessScope_Level_2 = 2L;
    /** 勾选了上级不可见和平级不可见 */
    public static final Long Account_AccessScope_Level_3 = 3L;
    /** 勾选了上级不可见和下级不可见 */
    public static final Long Account_AccessScope_Level_4 = 4L;
    /** 勾选了平级不可见和下级不可见 */
    public static final Long Account_AccessScope_Level_5 = 5L;
    /** 上级平级下级不可见都勾选 */
    public static final Long Account_AccessScope_Level_6 = 6L;
    
    /**
     * 单位访问的分类
     * 保存在org_unit表中的扩展字段
     * 使用元数据对应的key来保存EXT_ATTR_12-----accessCategory
     */
    public static enum Permission_Type {
        /** 统一设置 */
        TOTAL,
        /** 分级设置 */
        LEVEL,
        /** 自由设置 */
        FREE
    }
    
    /**排序号方式-插入*/
    public static final String SORTID_TYPE_INSERT = "0";
    /**排序号方式-重复*/
    public static final String SORTID_TYPE_REPEAT = "1";
    /** 集团版--常量集团ID */
    public static final Long GROUPID = -1730833917365171641L;
    /** 企业版--企业单位ID */
    public static final Long ACCOUNTID = 670869647114347L;
    /** 系统管理员预置ID */
    public static final Long SYSTEM_ADMIN_ID = -7273032013234748168L;
    /** 审计管理员预置ID */
    public static final Long AUDIT_ADMIN_ID = -4401606663639775639L;
    /** 集团管理员预置ID */
    public static final Long GROUP_ADMIN_ID = 5725175934914479521L;
    /** 平台管理员预置ID */
    public static final Long PLATFORM_ADMIN_ID = -7273032013234748399L;
    /** 超级管理员预制ID */
    public static final Long SUPER_ADMIN_ID = 6725175934914479521L;
    /** 单位管理员角色ID */
    public static final Long ACCOUNT_ADMIN_ROLE_ID = -2989205846588111483L;
    /** 工作地预制Id **/
    public static final Long WORKLOCAL_ID = 8264671846789452738L;
    /** 系统登录前通用guest账号Id */
    public static final Long GUEST_ID = -6964000252392685202L;
    /**Guest账号预制角色ID*/
    public static final Long GUEST_ROLE_ID = -7861977929759174835L;
    /**Vjoin子机构管理员预制角色ID*/
    public static final Long VJOIN_SUBMANAGER_ROLE_ID = -8495309803700534657L;
    /** 多维组织管理员角色ID */
    public static final Long BUSINESS_ORGANIZATION_ROLE_ID = -6735188174936650506L;
    /**Guest账号预制的名称*/
    public static final String DEFAULT_GUEST_NAME = "seeyon-guest";
    /**访客管理*/
    public static final String ORG_VISITOR_CONFIG_CATEGORY = "org_visitor";
    /**访客管理-应用开关*/
    public static final String ORG_VISITOR_CONFIG_ITEM_APP_SWITCH = "app_switch";
    
    public static enum MEMBER_TYPE{
        /** 不使用，占位0用 */
        NULL,
        /** 正式 */
        FORMAL,
        /** 非正式 */
        INFORMAL,
    }
    
    /**
     * 人员状态，在职离职
     */
    public static enum MEMBER_STATE{
        /** 不使用，占位0用 */
        NULL,
        /** 在职 */
        ONBOARD,
        /** 离职 */
        RESIGN;
    }
    
    /**
     * 访客状态
     */
    public static enum VISITOR_STATE{
        /** 不使用，占位0用 */
        NULL,
        /** 正常 */
        NORMAL,
        /** 删除 */
        DELETE,
        /** 禁用*/
        FORBIDDEN;
    }
    public enum AppLogAction {
    
        /** 组织信息管理_批量导入部門*/
        Organization_BatchAddDepartment(834),
        /** 组织信息管理_批量导入部門角色*/
        Organization_BatchAddDepartmentRole(835);
    
        private int key;

        AppLogAction(int key) {
            this.key = key;
        }

        public int getKey() {
            return this.key;
        }

        public int key() {
            return this.key;
        }
    }
    
    public static Map<String,String> defaultVjoinMenu = new HashMap<String,String>() {
        {
            put("menu_-4751259066364441679","协同事项");//协同工作-->协同事项
            put("menu_-4140425781984149261","新建事项");//新建事项
            put("menu_-8306075607604378311","待发事项");//待发事项
            put("menu_3908880089061278395","已发事项"); //已发事项
            put("menu_-74745459456393211","待办事项"); //待办事项
            put("menu_-1170093474986179706","已办事项");//已办事项
            put("menu_-2480427965080909155","查询统计");//表单应用-->查询统计
            put("menu_-7080776067937645441","表单查询");//表单查询
            put("menu_-1760548293269241145","表单统计");//表单统计
            put("menu_-339788257495134854","知识分享"); //知识社区-->知识分享
            put("menu_-8395666445450196178","我的收藏");//我的收藏
            put("menu_-5739332168962522608","文档中心");//文档中心
            put("menu_9027674737881466566","信息公开"); //文化建设-->信息公开
            put("menu_-124936515553112307","新闻"); //新闻
            put("menu_-4161126842094894391","公告");//公告
            put("menu_-1089588988266921304","讨论");//讨论
            put("menu_2345991731884477998","数据信息");//业务生成器-->数据信息
            put("menu_-4325582076455204361","基础数据");//基础数据
            put("menu_-5548038733928905841","信息管理");//信息管理
        }
    };
}
