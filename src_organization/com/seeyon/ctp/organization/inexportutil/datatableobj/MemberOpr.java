package com.seeyon.ctp.organization.inexportutil.datatableobj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.seeyon.apps.addressbook.manager.AddressBookCustomerFieldInfoManager;
import com.seeyon.apps.addressbook.manager.AddressBookManager;
import com.seeyon.apps.addressbook.po.AddressBook;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.CustomizeConstants;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.customize.manager.CustomizeManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.metadata.bo.MetadataColumnBO;
import com.seeyon.ctp.common.metadata.manager.MetadataManager;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumBean;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.OrganizationMessage;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgEntity;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgPrincipal;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.inexportutil.DataObject;
import com.seeyon.ctp.organization.inexportutil.DataStringUtil;
import com.seeyon.ctp.organization.inexportutil.DataUtil;
import com.seeyon.ctp.organization.inexportutil.ResultObject;
import com.seeyon.ctp.organization.inexportutil.inf.IImexPort;
import com.seeyon.ctp.organization.inexportutil.msg.MsgContants;
import com.seeyon.ctp.organization.inexportutil.pojo.ImpExpMember;
import com.seeyon.ctp.organization.inexportutil.pojo.ImpExpPojo;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.services.OrganizationServices;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.UniqueList;

/**
 * 人员导入 
 * @author kyt
 * @author lilong
 *
 */
public class MemberOpr extends AbstractImpOpr implements IImexPort {
	private static final String LOGINNAME = "loginname";
	private static final Log log = LogFactory.getLog(MemberOpr.class);
	// 导入人员时识别的性别集合（只要为男、M、MALE都视为男性）
	private static Set<String> males = new HashSet<String>();
	private static Set<String> females = new HashSet<String>();
	private static Map<String,String> primaryLanguange = new HashMap<String,String>();
    
	static{
		males.add(ResourceUtil.getString("org.memberext_form.base_fieldset.sexe.man"));
		females.add(ResourceUtil.getString("org.memberext_form.base_fieldset.sexe.woman"));
		males.add("M");females.add("F");
		males.add("MALE");females.add("FEMALE");
	}
	
	static{
		int productId = com.seeyon.ctp.common.constants.ProductEditionEnum.getCurrentProductEditionEnum().getKey();
		if(productId==3 || productId==4){
		}else{
			primaryLanguange.put("en", "en");
			primaryLanguange.put("English", "en");
		}
		
		primaryLanguange.put("中文（简体）", "zh_CN");
		primaryLanguange.put("zh_CN", "zh_CN");
		
		primaryLanguange.put("中文（繁體）", "zh_TW");
		primaryLanguange.put("zh_TW", "zh_TW");
	}
	
    public String[] getFixedField(HttpServletRequest request) {
        String state_Enabled = ResourceUtil.getString("org.account_form.enable.use");
        String member_name = ResourceUtil.getString("org.member_form.name.label");
        String member_loginName = ResourceUtil.getString("org.member_form.loginName.label");
        String member_primaryLanguange = ResourceUtil.getString("org.member_form.primaryLanguange");
        String member_state = ResourceUtil.getString("org.state.lable");
        String member_code = ResourceUtil.getString("org.member_form.code");
        String member_sortId = ResourceUtil.getString("org.member_form.sort");
        String member_deptName = ResourceUtil.getString("org.member_form.deptName.label");
        String member_primaryPost = ResourceUtil.getString("org.member_form.primaryPost.label");
        String member_levelName = ResourceUtil.getString("org.member_form.levelName.label");
        String member_type = ResourceUtil.getString("org.member_form.type");
        String member_tel = ResourceUtil.getString("org.member_form.tel");
        String member_account = ResourceUtil.getString("org.member_form.account");
        String member_description = ResourceUtil.getString("org.member_form.description");
        String company_createDate = ResourceUtil.getString("org.account_form.createdtime.label");
        String company_updateDate = ResourceUtil.getString("org.account_form.updatetime.label");

        String islogin = ResourceUtil.getString("org.member_form.islogin.label");
        String isvirtual = ResourceUtil.getString("org.member_form.isvirtual.label");
        String isassign = ResourceUtil.getString("org.member_form.isassigned.label");
        String isadmin = ResourceUtil.getString("org.member_form.isAdmin.label");
        String isinternal = ResourceUtil.getString("org.member_form.isinternal.label");
        String agentname = ResourceUtil.getString("org.member_form.agentname.label");
        String agentid = ResourceUtil.getString("org.member_form.agentid.label");
        String agenttime = ResourceUtil.getString("org.member_form.agenttime.label");
//        String member_location = ResourceUtil.getString("member.location");
//        String member_hiredate = ResourceUtil.getString("member.hiredate");
//        String member_reporter = ResourceUtil.getString("member.report2");

        String[] fieldname = { "loginname:" + member_loginName + ":loginname", "name:" + member_name + ":name",
                "code:" + member_code + ":code", "primary_languange:" + member_primaryLanguange + ":primary",
                "is_loginable:" + islogin + ":loginable", "is_virtual:" + isvirtual + ":virtual",
                "is_assigned:" + isassign + ":assigned", "is_admin:" + isadmin + ":admin",
                "sort_id:" + member_sortId + ":sort", "state:" + member_state + ":state",
                "type:" + member_type + ":type", "is_internal:" + isinternal + ":internal",
                "enabled:" + state_Enabled + ":enabled", "create_time:" + company_createDate + ":create",
                "update_time:" + company_updateDate + ":update", "tel_number:" + member_tel + ":tel",
                "agent_id:" + agentname + ":agentid", "agent_to_id:" + agentid + ":agenttoid",
                "agent_time:" + agenttime + ":agenttime", "description:" + member_description + ":cription",
                "org_department_id:" + member_deptName + ":departmentid",
                "org_level_id:" + member_levelName + ":levelid", "org_account_id:" + member_account + ":accountid",
                "org_post_id:" + member_primaryPost + ":postid"

        };
        return fieldname;
    }
    
    public List matchLanguagefield(List statrlst, HttpServletRequest request) throws Exception {
        DataObject logindao = new DataObject();
        logindao.setFieldName(LOGINNAME);
        logindao.setLength(100);
        logindao.setTableName(((DataObject) statrlst.get(0)).getTableName());
        statrlst.add(0, logindao);
        for (int i = 0; i < statrlst.size(); i++) {
            DataObject dao = (DataObject) statrlst.get(i);
            boolean flag = false;
            String[] fieldname = getFixedField(request);
            for (int j = 0; j < fieldname.length; j++) {
                String field[] = fieldname[j].split(":");
                if (dao.getFieldName().equalsIgnoreCase(field[0])) {
                    dao.setMatchCHNName(field[1]);
                    dao.setMatchENGName(field[2]);
                    flag = true;
                }
            }
            if (!flag) {
                dao.setMatchCHNName("");
            }
        }
        return statrlst;
    }

    public void validateData(List volst) throws Exception {
    	
    	AddressBookManager addressBookManager = (AddressBookManager) AppContext.getBean("addressBookManager");
    	List<MetadataColumnBO> metadataColumnList=addressBookManager.getCustomerAddressBookList();
    	OrgManagerDirect orgManagerDirect = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
    	OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
    	EnumManager enumManagerNew = (EnumManager) AppContext.getBean("enumManagerNew"); 
    	if(Strings.isEmpty(volst)) return;
    	
    	Long accountId = ((V3xOrgMember) volst.get(0)).getOrgAccountId();
    	
    	List<V3xOrgMember> currentAccountMembers = orgManager.getAllMembers(accountId);
    	Map<String,Long> memberNameMap = new HashMap<String, Long>();
    	Map<String,Long> memberNameWithLoginNameMap = new HashMap<String, Long>();
    	for(V3xOrgMember m : currentAccountMembers){
    		memberNameMap.put(m.getName(), m.getId());
    		memberNameWithLoginNameMap.put(m.getName()+"("+ m.getLoginName() +")", m.getId());
    	}
    	
    	List<V3xOrgDepartment> currentAccountDepartments = orgManager.getChildDepartments(accountId, false);
    	Map<String,Long> deptNameMap = new HashMap<String, Long>();
    	Map<String,Long> deptNameWithCodeMap = new HashMap<String, Long>();
    	for(V3xOrgDepartment d : currentAccountDepartments){
    		deptNameMap.put(d.getName(), d.getId());
    		if(Strings.isNotBlank(d.getCode())){
    			deptNameWithCodeMap.put(d.getName()+"("+ d.getCode() +")", d.getId());
    		}
    	}
        
        for (int i = 0; i < volst.size(); i++) {
            V3xOrgMember voa = (V3xOrgMember) volst.get(i);
            if (Strings.isNotBlank(voa.getCode())) {
                //判断人员编码是否重复
                boolean isDuplicated = orgManagerDirect.isPropertyDuplicated(V3xOrgMember.class.getSimpleName(), "code",
                        voa.getCode(), null, voa.getId());
                if (isDuplicated) {
                    throw new Exception(ResourceUtil.getString("MessageStatus.MEMBER_REPEAT_CODE"));
                }
            }
            
            if (Strings.isNotEmpty(voa.getName())) {
                if (voa.getName().length() > 40) {
                    throw new Exception(ResourceUtil.getString("import.validate.check1", voa.getName()));
                }
                
                String reg = "^.*[\'\\/|><:*?&%$].*$";
                if(voa.getName().matches(reg)) {
                    throw new Exception(ResourceUtil.getString("import.validate.check22", voa.getName(),ResourceUtil.getString("org.member_form.name.label")));
                }
            }
            if (Strings.isNotEmpty(voa.getLoginName())) {
                if (voa.getLoginName().length() > 50) {
                    throw new Exception(ResourceUtil.getString("import.validate.check2", voa.getName()));
                }
                
                String reg = "^.*[\'\\/|><:*?&%$].*$";
                if(voa.getLoginName().matches(reg)) {
                    throw new Exception(ResourceUtil.getString("import.validate.check16", voa.getName()));
                }
            }
            if (Strings.isNotEmpty(voa.getCode())) {
                if (voa.getCode().length() > 100) {
                    throw new Exception(ResourceUtil.getString("import.validate.check3", voa.getName()));
                }
            }
            if (Strings.isNotEmpty(voa.getTelNumber())) {
                if (voa.getTelNumber().length() > 100) {
                    throw new Exception(ResourceUtil.getString("import.validate.check4", voa.getName()));
                }
                
                String reg = "^[0-9]*$";
                if(!voa.getTelNumber().matches(reg)) {
                	throw new Exception(ResourceUtil.getString("import.validate.check22", voa.getName(),ResourceUtil.getString("org.member_form.tel")));
                }
            }
            if (Strings.isNotEmpty(voa.getEmailAddress())) {
                if (voa.getEmailAddress().length() > 100) {
                    throw new Exception(ResourceUtil.getString("import.validate.check5", voa.getName()));
                }
                
                if (!voa.getEmailAddress().matches("^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")) {
                    throw new Exception(ResourceUtil.getString("import.validate.check17", voa.getName()));
                }
            }
            
            if (Strings.isNotEmpty(voa.getPrimaryLanguange())) {
                if (voa.getPrimaryLanguange().length() > 10) {
                    throw new Exception(ResourceUtil.getString("import.validate.check6", voa.getName()));
                }
                
                if(!primaryLanguange.containsKey(voa.getPrimaryLanguange().trim())){
                	throw new Exception(ResourceUtil.getString("import.validate.check7", voa.getName()));
                }
                
                voa.setPrimaryLanguange(primaryLanguange.get(voa.getPrimaryLanguange().trim()));
            }
            
/*            if (null != voa.getPostAddress()) {
                if (voa.getPostAddress().length() > 70) {
                    throw new Exception("数据 " + voa.getName() + " 的 通信地址 ,长度太长!");
                }
            }*/

            //通讯录字段
            int cLen=0;
            List<String> customerAddressBooklist=voa.getCustomerAddressBooklist();
            MetadataColumnBO metadataColumn;
            if(null!=customerAddressBooklist && customerAddressBooklist.size()>0){
            	cLen=customerAddressBooklist.size();
            }
            for(int j=0;j<metadataColumnList.size();j++){
            	metadataColumn=metadataColumnList.get(j);
            	if(j<cLen){
            		String value=customerAddressBooklist.get(j);
            		if(!"".equals(value)){
            			if(metadataColumn.getType()==0){
                            if (value.length() > 100) {
                                throw new Exception(ResourceUtil.getString("import.validate.check15", voa.getName(),metadataColumn.getLabel()));
                            }
            			}else if(metadataColumn.getType()==1){
        				    Pattern pattern = Pattern.compile("([0-9]{0,8})([.][0-9]{1,4})?"); 
        				    if(!pattern.matcher(value).matches()) {
        				    	throw new Exception(ResourceUtil.getString("import.validate.check8", voa.getName(),metadataColumn.getLabel()));
        				    }
        				    
                            if (value.length() > 13) {
                                throw new Exception(ResourceUtil.getString("import.validate.check15", voa.getName(),metadataColumn.getLabel()));
                            }
            			}else if(metadataColumn.getType()==2){
            				Date dateValue;
            				try{
	                            if ((value.indexOf("-") == 2)) {
	                                 dateValue = Datetimes.parse(value, "yy-MM-dd");
	                            } else if (value.indexOf("年") == 2) {
	                            	dateValue = Datetimes.parse(value, ResourceUtil.getString("common.datetime.patternorg.small"));
	                            } else {
	                            	dateValue = Datetimes.parse(value, ResourceUtil.getString("common.datetime.patternorg"));
	                            }            
                            } catch (Exception e) {
                            	throw new Exception(ResourceUtil.getString("import.validate.check10", voa.getName(),metadataColumn.getLabel()));
                            }
                            
            				if(null!=dateValue){
            					if(dateValue.before(Datetimes.parse("1753-01-01","yyyy-MM-dd")) || dateValue.after(Datetimes.parse("9999-12-31","yyyy-MM-dd"))) {
            						throw new Exception(ResourceUtil.getString("import.validate.check11", voa.getName(),metadataColumn.getLabel()));
            					}
            				}else{
            					throw new Exception(ResourceUtil.getString("import.validate.check21", voa.getName(),metadataColumn.getLabel()));
            				}
            			}else if(metadataColumn.getType()==3){//枚举
            				Long menuId = metadataColumn.getEnumId();
            				//CtpEnumBean enumBean = enumManagerNew.getEnum(menuId);
            				CtpEnumItem item = enumManagerNew.getItemByEnumId(menuId, value);
            				if(item!=null){
            					enumManagerNew.updateEnumItemRef(item.getId());
            					customerAddressBooklist.set(j, item.getId().toString());
            				}else{
            					//throw new Exception(ResourceUtil.getString(enumBean.getName()) +"下不存在选项：" + value);
            					throw new Exception(ResourceUtil.getString("import.validate.check23",metadataColumn.getLabel(),value));
            				}
            			}else if(metadataColumn.getType()==4){//选人
            				Long memberId = null;
            				if(memberNameMap.containsKey(value)){
            					memberId = memberNameMap.get(value);
            				}else if(memberNameWithLoginNameMap.containsKey(value)){
            					memberId = memberNameWithLoginNameMap.get(value);
            				}
            				if(memberId!=null){
            					customerAddressBooklist.set(j, memberId.toString());
            				}else{
            					throw new Exception(ResourceUtil.getString("import.validate.check24",metadataColumn.getLabel(),value));
            				}
            			}else if(metadataColumn.getType()==5){//选部门
            				Long deptId = null;
            				if(deptNameMap.containsKey(value)){
            					deptId = deptNameMap.get(value);
            				}else if(deptNameWithCodeMap.containsKey(value)){
            					deptId = deptNameWithCodeMap.get(value);
            				}
            				if(deptId!=null){
            					customerAddressBooklist.set(j, deptId.toString());
            				}else{
            					throw new Exception(ResourceUtil.getString("import.validate.check25",metadataColumn.getLabel(),value));
            				}
            			}
            		}
            	}
            }
            
            if (null != voa.getBirthday()) {//OA-48359 SQL Server 2008中的datetime字段范围1753-01-01到9999-12-31
                Date birthday = voa.getBirthday();
                if(birthday.before(Datetimes.parse("1753-01-01","yyyy-MM-dd")) || birthday.after(Datetimes.parse("9999-12-31","yyyy-MM-dd"))) {
                    throw new Exception(ResourceUtil.getString("import.validate.check11", voa.getName(),birthday));
                }
            }
            
            if (null != voa.getHiredate()) {//OA-48359 SQL Server 2008中的datetime字段范围1753-01-01到9999-12-31
                Date hiredate = voa.getHiredate();
                if(hiredate.before(Datetimes.parse("1753-01-01","yyyy-MM-dd")) || hiredate.after(Datetimes.parse("9999-12-31","yyyy-MM-dd"))) {
                    throw new Exception(ResourceUtil.getString("import.validate.check11", voa.getName(),hiredate));
                }
            }
        }
    }

    public V3xOrgEntity getVO() {
        // TODO Auto-generated method stub
        return new V3xOrgMember();
    }

    public List assignVO(OrgManager od, MetadataManager metadataManager, Long accountid,
            List<List<String>> accountList, List volst) throws Exception {
        List returnlst = new ArrayList();
        List<V3xOrgDepartment> deptlst = od.getAllDepartments(accountid);
        List<V3xOrgLevel> levellst = od.getAllLevels(accountid);
        List<V3xOrgPost> postlst = od.getAllPosts(accountid);
        for (int i = 2; i < accountList.size(); i++) {
            //log.info("accountList i="+i);//tanglh
            V3xOrgMember voa = new V3xOrgMember();
            List valuelst = accountList.get(i);//tanglh
            //log.info("valuelst.size()"+valuelst.size());
            Method med[] = voa.getClass().getMethods();
            if (DataUtil.isNotNullValue(valuelst)) {
                for (int j = 0; j < med.length; j++) {
                    Method mdd = med[j];
                    if (mdd.getName().indexOf("set") != -1) {
                        //log.info("mdd.getName()="+mdd.getName());
                        for (int m = 0; m < volst.size(); m++) {
                            DataObject dao = (DataObject) volst.get(m);
                            if (mdd.getName().toLowerCase().indexOf(DataUtil.submark(dao.getFieldName()).toLowerCase()) == 3) {
                                if (dao.getColumnnum() != -1) {
                                    Class cl[] = mdd.getParameterTypes();
                                    if ("java.lang.Integer".equals(cl[0].getName())) {
                                        if (DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())) {
                                            mdd.invoke(voa, new Object[] { new Integer(valuelst.get(dao.getColumnnum())
                                                    .toString()) });
                                        } else {
                                            mdd.invoke(voa, new Object[] { Integer.valueOf(0) });
                                        }
                                    } else if ("java.util.Date".equals(cl[0].getName())) {
                                        if ("".equals(valuelst.get(dao.getColumnnum()).toString())) {
                                            mdd.invoke(voa, new Object[] { Datetimes.getTodayFirstTime() });
                                        } else if (valuelst.get(dao.getColumnnum()).toString().trim().length() == 10) {
                                            mdd.invoke(
                                                    voa,
                                                    new Object[] { Datetimes.parse(valuelst.get(dao.getColumnnum())
                                                            .toString().trim()
                                                            + " 00:00:00") });
                                        } else {
                                            mdd.invoke(voa, new Object[] { Datetimes.parse(valuelst.get(
                                                    dao.getColumnnum()).toString()) });
                                        }
                                    } else if ("java.lang.Boolean".equals(cl[0].getName())) {
                                        mdd.invoke(voa, new Object[] { Boolean.valueOf(valuelst.get(dao.getColumnnum())
                                                .toString()) });
                                    } else if ("java.lang.Long".equals(cl[0].getName())) {
                                        if (DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())) {
                                            mdd.invoke(voa, new Object[] { new Long(valuelst.get(dao.getColumnnum())
                                                    .toString()) });
                                        } else if (!DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())) {
                                            if (DataUtil.submark(dao.getFieldName()).toLowerCase()
                                                    .indexOf("departmentid") != -1) {
                                                //log.info("departmentid");
                                                mdd.invoke(
                                                        voa,
                                                        new Object[] { getCorrectDept(deptlst,
                                                                valuelst.get(dao.getColumnnum()).toString()) });
                                            } else if (DataUtil.submark(dao.getFieldName()).toLowerCase()
                                                    .indexOf("levelid") != -1) {
                                                //log.info("levelid");
                                                mdd.invoke(
                                                        voa,
                                                        new Object[] { getCorrectLevel(levellst,
                                                                valuelst.get(dao.getColumnnum()).toString()) });
                                            } else if (DataUtil.submark(dao.getFieldName()).toLowerCase()
                                                    .indexOf("postid") != -1) {
                                                //log.info("postid");
                                                mdd.invoke(
                                                        voa,
                                                        new Object[] { getCorrectPost(postlst,
                                                                valuelst.get(dao.getColumnnum()).toString()) });
                                            } else {
                                                mdd.invoke(voa, new Object[] { Long.valueOf(0) });
                                            }
                                        } else {
                                            mdd.invoke(voa, new Object[] { Long.valueOf(0) });
                                        }
                                    } else if ("int".equals(cl[0].getName())) {
                                        if (DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())) {
                                            mdd.invoke(
                                                    voa,
                                                    new Object[] { Integer.valueOf(
                                                            valuelst.get(dao.getColumnnum()).toString()).intValue() });
                                        } else {
                                            mdd.invoke(voa, new Object[] { 0 });
                                        }
                                    } else if ("java.lang.Byte".equals(cl[0].getName())) {
                                        if (DataUtil.isNumeric(valuelst.get(dao.getColumnnum()).toString())) {
                                            mdd.invoke(
                                                    voa,
                                                    new Object[] { Byte.valueOf(
                                                            valuelst.get(dao.getColumnnum()).toString()).intValue() });
                                        } else {
                                            mdd.invoke(voa, new Object[] { Byte.valueOf("1") });
                                        }
                                    } else if ("com.seeyon.v3x.organization.domain.V3xOrgAccount".equals(cl[0].getName())) {
                                        V3xOrgAccount vox = new V3xOrgAccount();
                                        vox.setName(valuelst.get(dao.getColumnnum()).toString());
                                        mdd.invoke(voa, new Object[] { vox });
                                    } else {
                                        try {
                                            mdd.invoke(voa, new Object[] { valuelst.get(dao.getColumnnum()) });
                                        } catch (Exception e) {
                                            log.info("error", e);//tanglh
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //log.info("voa.getId()"+voa.getId());
                returnlst.add(voa);
            }
        }
        return returnlst;
    }

    @Deprecated
    public List creatUpdateSql(List volst) throws Exception {
        List returnlst = new ArrayList();
        for (int i = 0; i < volst.size(); i++) {
            V3xOrgMember voa = (V3xOrgMember) volst.get(i);
            if (voa == null) {
                continue;
            }
            if (!StringUtils.hasText(voa.getName()))//voa.getName()必填
            {
                continue;
            }
            //if()

            //log.info("voa.getId()="+voa.getId());
            StringBuilder sb = new StringBuilder();
            sb.append(" UPDATE v3x_org_member SET ");

            sb.append("name='" + (voa.getName() == null ? "" : voa.getName()) + "'"); //name
            sb.append(voa.getCode() == null ? "" : "   , code='" + voa.getCode() + "'"); //code
            //			sb.append(voa.getPrimaryLanguange()==null?"":"  , primary_languange='"+voa.getPrimaryLanguange()+"'");					//secondname
            if (voa.getEnabled()) {
                sb.append("  , enabled='" + 1 + "'");
            } else {
                sb.append(" , enabled='" + 0 + "'"); //enable
            }
            sb.append(voa.getSortId() == null ? "" : " ,sort_id='" + voa.getSortId() + "'"); //sortid
            sb.append(" ,state='" + voa.getState() + "'"); //levelscope
            sb.append(" ,type='" + voa.getType() + "'"); //levelscope
            if (voa.getIsInternal()) {
                sb.append(" ,is_internal='" + 1 + "'"); //isroot
            } else {
                sb.append(" , is_internal='" + 0 + "'");
            }
            if (voa.getIsLoginable()) {
                sb.append("  , is_loginable='" + 1 + "'"); //isroot
            } else {
                sb.append("  ,  is_loginable='" + 0 + "'");
            }
            if (voa.getIsVirtual()) {
                sb.append("  , is_virtual='" + 1 + "'"); //isroot
            } else {
                sb.append("   ,  is_virtual='" + 0 + "'");
            }
            if (voa.getIsAssigned()) {
                sb.append(" ,is_assigned='" + 1 + "'"); //isroot
            } else {
                sb.append("  , is_assigned='" + 0 + "'");
            }
            if (voa.getIsAdmin()) {
                sb.append("  , is_admin='" + 1 + "'"); //isroot
            } else {
                sb.append("  ,  is_admin='" + 0 + "'");
            }
            sb.append(voa.getCreateTime() == null ? " " : DataStringUtil.createDateTimeString("  ,create_time=",
                    voa.getCreateTime(), null));//tanglh
            //voa.getCreateTime()==null?"":" , create_time=TO_TIMESTAMP('"+Datetimes.formatDatetime(voa.getCreateTime())+"','YYYY-MM-DD HH24:MI:SS.FF')");					//createtime
            sb.append(voa.getUpdateTime() == null ? " " : DataStringUtil.createDateTimeString("  ,update_time=",
                    voa.getUpdateTime(), null));//tanglh
            //voa.getUpdateTime()==null?"":" , update_time=TO_TIMESTAMP('"+voa.getUpdateTime()+"','YYYY-MM-DD HH24:MI:SS.FF')");					//updatetime
            sb.append(voa.getDescription() == null ? "" : "  ,description='" + voa.getDescription() + "'"); //description

            sb.append(voa.getOrgDepartmentId() == null ? "" : "  ,org_department_id='" + voa.getOrgDepartmentId() + "'"); //alias
            sb.append(voa.getOrgLevelId() == null ? "" : "  ,org_level_id='" + voa.getOrgLevelId() + "'"); //shortname
            sb.append(voa.getOrgAccountId() == null ? "" : "   ,org_account_id='" + voa.getOrgAccountId() + "'"); //groupshortname
            sb.append(voa.getOrgPostId() == null ? "" : "   ,org_post_id='" + voa.getOrgPostId() + "'"); //adminname
            //			sb.append("  ,agent_id='"+voa.getAgentId()+"'");						//adminid
            //			sb.append("  , agent_to_id='"+voa.getAgentToId()+"'");					//adminemail
            //			sb.append(voa.getAgentTime()==null?" ":DataStringUtil.createDateTimeString(
            //					"   ,agent_time=", voa.getAgentTime(), null));//tanglh
            //voa.getAgentTime()==null?"":" AND agent_time=TO_TIMESTAMP('"+voa.getAgentTime()+"','YYYY-MM-DD HH24:MI:SS.FF')");
            sb.append(voa.getTelNumber() == null ? "" : " AND tel_number='" + voa.getTelNumber() + "'");//superior
            if (voa.getIsDeleted()) {
                sb.append("  ,is_deleted='" + 1 + "'"); //isdeleted
            } else {
                sb.append("  ,is_deleted='" + 0 + "'");
            }
            sb.append(voa.getEmailAddress() == null ? "" : "  ,EMAIL_ADDRESS='" + voa.getEmailAddress() + "'");
            sb.append("   where id='" + voa.getId() + "'");
            returnlst.add(sb);
        }
        return returnlst;
    }

    private V3xOrgMember doRemove(V3xOrgMember voa, List inList) {
        for (int j = 0; j < inList.size(); j++) {
            V3xOrgMember v3oavo = (V3xOrgMember) inList.get(j);
            if (v3oavo.getName().equals(voa.getName())) {
                return v3oavo;
            }
        }
        return null;
    }

    public Map devVO(OrgManager od, List volst) throws Exception {
        List v3xorgaccountvolst = od.getAllMembers(((V3xOrgMember) volst.get(0)).getOrgAccountId());
        List newlst = new ArrayList();
        //重复的
        List duplst = new ArrayList();
        newlst.addAll(volst);
        //这段有时间再改进

        int i = 0;
        V3xOrgMember ftempobj;
        while (i < newlst.size()) {//在这里判断登录名
            V3xOrgMember voa = (V3xOrgMember) newlst.get(i);
            ftempobj = doRemove(voa, v3xorgaccountvolst);

            if (ftempobj != null) {
                duplst.add(ftempobj);
                newlst.remove(i);
            } else {
                i++;
            }
        }

        Map mp = new HashMap();
        //重复的
        mp.put("dup", duplst);
        //剩下的
        mp.put("new", newlst);
        return mp;
    }	
	
    private Long getCorrectDept(List<V3xOrgDepartment> deptlst, String name) {
        if (name == null) {
            name = "";
        }
        for (V3xOrgDepartment vod : deptlst) {
            if (vod.getName().equals(name.trim())) {
                return vod.getId();
            }
        }
        return Long.valueOf(0);
    }

    private Long getCorrectLevel(List<V3xOrgLevel> levellst, String name) {
        if (name == null) {
            name = "";
        }
        for (V3xOrgLevel vod : levellst) {
            if (vod.getName().equals(name.trim())) {
                return vod.getId();
            }
        }
        return Long.valueOf(0);
    }

    private Long getCorrectPost(List<V3xOrgPost> postlst, String name) {
        if (name == null) {
            name = "";
        }
        for (V3xOrgPost vod : postlst) {
            if (vod.getName().equals(name.trim())) {
                return vod.getId();
            }
        }
        return Long.valueOf(0);
    }	
	
	protected String getAccountName(ImpExpPojo pojo){
		ImpExpMember p=(ImpExpMember)pojo;
		return p.getAccountName();
	}	
	
    protected ImpExpPojo transToPojo(List<String> org, V3xOrgAccount voa,String onlytag) throws Exception {
        ImpExpMember iep = new ImpExpMember();
        int size = org.size();
        if (org.size() < 7) {
            throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_FILEDATA));
        }

        if (!StringUtils.hasText((String) org.get(0))) {
            throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_MEMBERNAME));
        }
        iep.setName(catchNoCammerString(org.get(0).trim()));//tanglh ","

        if (!StringUtils.hasText((String) org.get(1))) {
            throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_LOGINNAME));
        }
        iep.setLoginName(catchNoCammerString(org.get(1).trim()));

        if (!StringUtils.hasText((String) org.get(2))) {
            //OA-26875 由于人员code和部门code要做唯一限制，因此这里导入的如果是空的自动给一个空串
            iep.setCode("");
        } else {
            iep.setCode(catchNoCammerString(org.get(2).trim())); //tanglh
        }
        //人员排序号
        if (!StringUtils.hasText((String) org.get(3))) {
            throw new Exception(ResourceUtil.getString(MsgContants.ORG_IO_MSG_ERROR_MUST_SORTIDNOTEMPTY));
        }
        Long sortId = -1L;
        try{
            sortId = Long.valueOf(org.get(3).trim());
        }catch(NumberFormatException e){
            throw new Exception(ResourceUtil.getString(MsgContants.ORG_IO_MSG_ERROR_MUST_SORTID));
        }
        if(sortId<=0 || sortId>999999999){
            throw new Exception(ResourceUtil.getString("org.io.error.must.sortIdm"));
        }
        iep.setSortId(org.get(3).trim());

        if (!StringUtils.hasText((String) org.get(4))) {
            throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_ACCOUNT));
        }
        iep.setAccountName(org.get(4).trim());

        if (!StringUtils.hasText((String) org.get(5))) {
            iep.setDept(IImexPort.NULL_ENTITY_TAG);
        } else {
            iep.setDept(org.get(5).trim());
        }

        if (!StringUtils.hasText((String) org.get(6))) {
            //throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_PPOST));
            iep.setPpost(IImexPort.NULL_ENTITY_TAG);
        } else {
            iep.setPpost(org.get(6).trim());
        }
        
        //副岗
        try {
            if (StringUtils.hasText((String) org.get(7))) {
                iep.setSpost(org.get(7).trim());
            }
        } catch (Exception e) {

        }
        try {
            if (!StringUtils.hasText((String) org.get(8))) {
                //throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_MUST_LEV));
                iep.setLevel(IImexPort.NULL_ENTITY_TAG);
            } else {
                iep.setLevel(org.get(8).trim());
            }
        }catch (Exception e) {

        }

        try {
            if (size>9 && StringUtils.hasText((String) org.get(9))) {
                iep.setTelNumber(org.get(9).trim().replace(",", ""));//
            }
        } catch (Exception e) {

        }

        try {
            if (size>10 && StringUtils.hasText((String) org.get(10))) {
                iep.setEMail(org.get(10).trim());//
            }
        } catch (Exception e) {

        }

        // 性别
        try {
            if (size>11 && StringUtils.hasText((String) org.get(11))) {
                iep.setGender(org.get(11).trim());//
            }
        } catch (Exception e) {

        }

        // 生日
        try {
            if (size>12 && StringUtils.hasText((String) org.get(12))) {
                iep.setBirthday(org.get(12).trim());//
            }
        } catch (Exception e) {

        }

        // 办公电话
        try {
            if (size>13 &&StringUtils.hasText((String) org.get(13))) {
                iep.setOfficeNumber(org.get(13).trim());//
            }
        } catch (Exception e) {

        }
        
        // 首选语言
        try {
            if (size>14 &&StringUtils.hasText((String) org.get(14))) {
                iep.setPrimaryLanguange(org.get(14).trim());
            }
        } catch (Exception e) {

        }
        
/*        // 通信地址
        try {
            if (size>15 &&StringUtils.hasText((String) org.get(15))) {
                iep.setCommunication(org.get(15).trim());
            }
        } catch (Exception e) {

        }*/
        
        // 工作地
        try {
	    	if (size>15 && StringUtils.hasText((String) org.get(15))) {
	    		EnumManager enumManagerNew = (EnumManager) AppContext.getBean("enumManagerNew"); 
	    		String localStr = org.get(15).trim();
	    		String localIds = enumManagerNew.parseToIds(localStr,OrgConstants.WORKLOCAL_ID);
	    		if(Strings.isBlank(localIds) || localStr.split("-").length!=localIds.split(",").length){
	    			throw new Exception(ResourceUtil.getString("org.io.error.local.notexist",iep.getName(),localStr));
	    		}
	    		iep.setLocation(org.get(15).trim());//
	    	}        
    	} catch (Exception e) {
        	throw e;
        }

        // 入职时间
        try {
        	if (size>16 && StringUtils.hasText((String) org.get(16))) {
        		iep.setHiredate(org.get(16).trim());//
        	}
        } catch (Exception e) {
        	
        }

        // 汇报人
        try {
	    	if (size>17) {
	    		if(StringUtils.hasText((String) org.get(17)) && Strings.isNotBlank(org.get(17))){
	    			OrgManagerDirect orgManagerDirect = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
	    			String reporterName = org.get(17).trim();
	    			//格式为: XXX(YYY) 先把这个按照姓名来找，如果找不到就当成 姓名(登录名)这种格式找.
	    			List<V3xOrgMember>  members = orgManagerDirect.getMemberByName(reporterName, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, false);
	    			if(null == members || members.size()==0){
	    				if(Strings.isNotBlank(reporterName) && reporterName.lastIndexOf("(")>0 && reporterName.lastIndexOf(")")==reporterName.length()-1){
	    					String name=reporterName.substring(0,reporterName.lastIndexOf("("));
	    					String loginName=reporterName.substring(reporterName.lastIndexOf("(")+1,reporterName.lastIndexOf(")"));
	    					List<V3xOrgMember>  mList = orgManagerDirect.getMemberByName(name, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, false);
	    					try{
	    						V3xOrgMember m = orgManagerDirect.getMemberByLoginName(loginName, false);
	    						String reporterName1 = "";
	    						for(V3xOrgMember member : mList){
	    							if(Strings.equals(member.getId(), m.getId())){
	    								reporterName1=String.valueOf(m.getId());
	    								reporterName=reporterName1;
	    							}
	    						}
	    						if(Strings.isBlank(reporterName1)|| null == mList|| mList.size()==0){
	    							throw new Exception(ResourceUtil.getString("org.io.error.reporter.notexist",iep.getName(),reporterName));
	    						}
	    					}catch(BusinessException e){
	    						throw new Exception(ResourceUtil.getString("org.io.error.reporter.notexist",iep.getName(),reporterName));
	    					}
	    				}else{
	    					throw new Exception(ResourceUtil.getString("org.io.error.reporter.notexist",iep.getName(),reporterName));
	    				}
	    			}else{
	    				reporterName=String.valueOf(members.get(0).getId());
	    				
	    			}
	    			iep.setReportTo(reporterName);//
	    		}
	    	}
        } catch (Exception e) {
        	throw e;
        }

        
        List<String> customerAddressBooklist=new ArrayList<String>();
        //自定义的通讯录字段
        int len=org.size();
        if(len>18){
        	for(int i=18;i<len;i++){
        		if(StringUtils.hasText((String) org.get(i))){
        			customerAddressBooklist.add(org.get(i).trim());
        		}else{
        			customerAddressBooklist.add("");
        		}
        	}
        	
        }
        
        try {
        	iep.setCustomerAddressBooklist(customerAddressBooklist);
        } catch (Exception e) {

        }

        return iep;
    }
	
    protected V3xOrgEntity existEntity(OrganizationServices organizationServices, ImpExpPojo pojo, V3xOrgAccount voa, String onlytag)
            throws Exception {

        ImpExpMember iep = (ImpExpMember) pojo;
        V3xOrgMember member = memberLoginNameMap.get(iep.getLoginName());

        return member;
    }
	
    protected V3xOrgEntity copyToEntity(OrganizationServices organizationServices, MetadataManager metadataManager,
            ImpExpPojo pojo, V3xOrgEntity ent, V3xOrgAccount voa) throws Exception {

        //增加导入的数据校验
        return copyToMember(organizationServices, metadataManager, (ImpExpMember) pojo, (V3xOrgMember) ent, voa);
    }

    protected V3xOrgEntity copyToMember(OrganizationServices organizationServices, MetadataManager metadataManager,
            ImpExpMember pojo, V3xOrgMember ent, V3xOrgAccount voa) throws Exception {
        if (pojo == null) {
            throw new Exception("null ImpExpMember object to cover to V3xOrgMember object");
        }
        V3xOrgMember vop = null;
        boolean isnew = false;
        if (ent != null) {
            vop = organizationServices.getOrgManager().getMemberById(ent.getId());
        }
        if (ent == null) {
            vop = new V3xOrgMember();
            isnew = true;
        }
		
		vop.setName(pojo.getName());//
		vop.setCode(pojo.getCode());//
		vop.setSortId(Long.valueOf(pojo.getSortId()));//排序号
		vop.setOrgAccountId(voa.getId());
		if(isnew) {
		    vop.setIdIfNew();
		    V3xOrgPrincipal p = new V3xOrgPrincipal(vop.getId(),pojo.getLoginName(),organizationServices.getOrgManager().getInitPWD());
		    vop.setV3xOrgPrincipal(p);
		}
		//拓展信息
		if(ent!=null){
			//如果人员已经绑定了手机号码和邮箱，管理员修改了手机号码和邮箱后，解除绑定 
			CustomizeManager customizeManager = (CustomizeManager) AppContext.getBean("customizeManager");    
			if(null==pojo.getTelNumber() || null ==vop.getTelNumber() || 
					(null!=pojo.getTelNumber() && null!=vop.getTelNumber() && !pojo.getTelNumber().equals(vop.getTelNumber()))){
				customizeManager.saveOrUpdateCustomize(ent.getId(), CustomizeConstants.BIND_PHONENUMBER, "false");
			}
			
			if(null==pojo.getEMail() || null ==vop.getEmailAddress() || 
					(null!=pojo.getEMail() && null!=vop.getEmailAddress() && !pojo.getEMail().equals(vop.getEmailAddress()))){
				customizeManager.saveOrUpdateCustomize(ent.getId(), CustomizeConstants.BIND_EMAIL, "false");
			}
		}
        
		vop.setProperty("telnumber", pojo.getTelNumber());//电话
        vop.setProperty("emailaddress", pojo.getEMail());//邮箱
		
		vop.setProperty("officenumber", pojo.getOfficeNumber());//办公电话
		
        
		//首选语言
        if(!Strings.isBlank(pojo.getPrimaryLanguange())){
        	vop.setPrimaryLanguange(pojo.getPrimaryLanguange());
        }
        
/*        //通信地址
        if(!Strings.isBlank(pojo.getCommunication())){
        	vop.setPostAddress(pojo.getCommunication());
        }*/
        
		//工作地入职时间汇报人  
		if(!Strings.isBlank(pojo.getLocation())){
			EnumManager enumManagerNew = (EnumManager) AppContext.getBean("enumManagerNew"); 
			vop.setProperty("location", enumManagerNew.parseToIds(pojo.getLocation(),OrgConstants.WORKLOCAL_ID));
		}
		
		if (pojo.getHiredate().length() > 0) {
            try {
                Date hiredate;
                if ((pojo.getHiredate().indexOf("-") == 2)) {
                	hiredate = Datetimes.parse(pojo.getHiredate(), "yy-MM-dd");
                } else if (pojo.getHiredate().indexOf("年") == 2) {
                	hiredate = Datetimes.parse(pojo.getHiredate(), ResourceUtil.getString("common.datetime.patternorg.small"));
                } else {
                	hiredate = Datetimes.parse(pojo.getHiredate(), ResourceUtil.getString("common.datetime.patternorg"));
                }
                vop.setProperty("hiredate", hiredate);
            } catch (Exception e) {
                // 忽略出生日期解析错误
            }
        }
		if(!Strings.isBlank(pojo.getReportTo())){
			vop.setProperty("reporter", Long.valueOf(pojo.getReportTo()));
		}else{
			vop.setProperty("reporter", null);
		}
		
		
        String gender = pojo.getGender().toUpperCase();
        if (males.contains(gender)) {
            vop.setProperty("gender", 1);
        } else if (females.contains(gender)) {
            vop.setProperty("gender", 2);
        }
        if (pojo.getBirthday().length() > 0) {
            try {
                Date birthday;
                if ((pojo.getBirthday().indexOf("-") == 2)) {
                    birthday = Datetimes.parse(pojo.getBirthday(), "yy-MM-dd");
                } else if (pojo.getBirthday().indexOf("年") == 2) {
                    birthday = Datetimes.parse(pojo.getBirthday(), ResourceUtil.getString("common.datetime.patternorg.small"));
                } else {
                    birthday = Datetimes.parse(pojo.getBirthday(), ResourceUtil.getString("common.datetime.patternorg"));
                }
                vop.setProperty("birthday", birthday);
            } catch (Exception e) {
                // 忽略出生日期解析错误
            }
        }

        if (IImexPort.NULL_ENTITY_TAG.equals(pojo.getDept())) {
            vop.setOrgDepartmentId(V3xOrgEntity.DEFAULT_NULL_ID);
            vop.setEnabled(false);;
        } else {
            String[] depnc = this.getCodeFromNameCodeString(pojo.getDept(),V3xOrgDepartment.class.getSimpleName(),voa.getId());
            V3xOrgDepartment dep = this.getNeedDepartment(organizationServices, depnc, voa);
            if (dep != null) {
                long depid = dep.getId();

                if (isnew)
                    vop.setIsInternal(dep.getIsInternal());//根据部门设置
                vop.setOrgDepartmentId(depid);
            } else {
                vop.setOrgDepartmentId(V3xOrgEntity.DEFAULT_NULL_ID);
            }
        }
		
        if (IImexPort.NULL_ENTITY_TAG.equals(pojo.getPpost()) || pojo.getPpost()== null || !vop.getIsInternal()) {
            vop.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
            if(vop.getIsInternal()){
            	vop.setEnabled(false);
            }
        } else {
            String[] postnc = this.getCodeFromNameCodeString(pojo.getPpost());
            try {
                long postid = this.getNeedPost(organizationServices, postnc, voa).getId();
                vop.setOrgPostId(postid);
            } catch (Exception e) {
                if (vop.getIsInternal())
                    throw e;
            }
        }
        
        if(isnew && (pojo.getSpost() == null || Strings.isBlank(pojo.getSpost()))) {
            //副岗给空
            vop.setSecond_post(new ArrayList<MemberPost>(0));
        } else {
            //修改为导入的副岗
            //如果主岗是空的，不设置副岗
            if(pojo.getSpost() != null && Strings.isNotBlank(pojo.getSpost()) && 
                    !IImexPort.NULL_ENTITY_TAG.equals(pojo.getPpost()) && pojo.getPpost()!= null && Strings.isNotBlank(pojo.getPpost())){
                List<MemberPost> secPosts = new ArrayList<MemberPost>();
                List<String> errorList = new ArrayList<String>();
                StringBuilder sb2  =new StringBuilder();
                List<MemberPost> memberPosts = transToMemberPost(vop,pojo.getSpost(),errorList);
                if(errorList.size()>0){
                    for(String error:errorList){
                        sb2.append(error+";");
                    }
                    throw new Exception(sb2.toString());
                }
                for (MemberPost memberPost : memberPosts) {
                    secPosts.add(memberPost);
                }
                vop.setSecond_post(secPosts);
            }
/*            else if(isnew){
                //副岗给空
                vop.setSecond_post(new ArrayList<MemberPost>(0));
            }else{
                //保持原副岗
                List<MemberPost> secPosts = new ArrayList<MemberPost>();
                List<MemberPost> memberPosts = organizationServices.getOrgManager().getMemberPosts(vop.getOrgAccountId(), vop.getId());
                for (MemberPost memberPost : memberPosts) {
                    if(OrgConstants.MemberPostType.Second == memberPost.getType()) {
                        secPosts.add(memberPost);
                    }
                }
                vop.setSecond_post(secPosts);
            }*/
            else{
                //不填副岗就给空
                vop.setSecond_post(new ArrayList<MemberPost>(0));
            }
        }

        if (IImexPort.NULL_ENTITY_TAG.equals(pojo.getLevel()) || pojo.getLevel()==null || !vop.getIsInternal()) {
            vop.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
            if(vop.getIsInternal()){
            	vop.setEnabled(false);
            }
        } else {
            String[] levnc = this.getCodeFromNameCodeString(pojo.getLevel());
            try {
                long levid = this.getNeedLevel(organizationServices, levnc, voa).getId();
                vop.setOrgLevelId(levid);
            } catch (Exception e) {
                if (vop.getIsInternal())
                    throw e;
            }
        }
        
      //通讯录自定义字段
        vop.setCustomerAddressBooklist(pojo.getCustomerAddressBooklist());
		
		return vop;
	}
	
    private List<MemberPost> transToMemberPost(V3xOrgMember vom, String spost ,List<String> errorList) throws BusinessException {
        OrgManager om = (OrgManager) AppContext.getBean("orgManager");
        String[] deptAndPosts = spost.split(",");
        //单位下所有部门
        List<V3xOrgDepartment>  depts = om.getAllDepartments(vom.getOrgAccountId());
        Set<String> accountDeptsName = new HashSet<String>();
        Map<String,V3xOrgDepartment> deptName2Post = new HashMap<String,V3xOrgDepartment>();
        for(V3xOrgDepartment dept : depts){
            accountDeptsName.add(dept.getName());
            accountDeptsName.add(dept.getName()+"("+dept.getCode()+")");
            deptName2Post.put(dept.getName(), dept);
            deptName2Post.put(dept.getName()+"("+dept.getCode()+")", dept);
        }
        //所有岗位
        List<V3xOrgPost>  accountPost = om.getAllPosts(vom.getOrgAccountId());
        Set<String> accountPostName = new HashSet<String>();
        Map<String,V3xOrgPost> postName2Post = new HashMap<String,V3xOrgPost>();
        for(V3xOrgPost post : accountPost){
            accountPostName.add(post.getName());
            postName2Post.put(post.getName(), post);
        }
        //副岗信息不是必选，但可以多选
        List<MemberPost> secondPostList = new ArrayList<MemberPost>();
        //TODO 未来有性能问题
        for(String deptAndPost : deptAndPosts){
            String[] d2p = deptAndPost.split("》");
            if(d2p.length!=2){
                errorList.add(ResourceUtil.getString("org.io.error.secondpost.deptformaterror")+ deptAndPost);
                continue;
            }
            String deptName = d2p[0];
            String postName = d2p[1];
            List<V3xOrgDepartment> matchDepts = new UniqueList<V3xOrgDepartment>();
            Map<Long,List<V3xOrgPost>> matchPosts = new HashMap<Long,List<V3xOrgPost>>();
            if(accountDeptsName.contains(deptName)){
                matchDepts.add(deptName2Post.get(deptName));
                List<V3xOrgPost> temPosts = new ArrayList<V3xOrgPost>();
                if(accountPostName.contains(postName)){
                    temPosts.add(postName2Post.get(postName));
                }
                matchPosts.put(deptName2Post.get(deptName).getId(), temPosts);
            }
            for(V3xOrgDepartment dept:depts){
                if(dept.getName().equals(deptName) || (dept.getName()+"("+dept.getCode()+")").equals(deptName)){
                    matchDepts.add(dept);
                    List<V3xOrgPost> temPosts = new ArrayList<V3xOrgPost>();
                    if(accountPostName.contains(postName)){
                        temPosts.add(postName2Post.get(postName));
                    }
                    matchPosts.put(dept.getId(), temPosts);
                }
            }
            if(matchDepts.size()<1){
                errorList.add(ResourceUtil.getString("org.io.error.secondpost.deptnamenotexists", deptName));
                continue;
            }else if(matchDepts.size()>1){
                errorList.add(ResourceUtil.getString("org.io.error.secondpost.deptnameduplicate", deptName));
                continue;
            }else{
                if(matchPosts.get(matchDepts.get(0).getId()).size()<1){
                    errorList.add(ResourceUtil.getString("org.io.error.secondpost.deptpostnotexists", deptName, postName));
                    continue;
                }else if(matchPosts.get(matchDepts.get(0).getId()).size()>1){
                    errorList.add(ResourceUtil.getString("org.io.error.secondpost.deptpostduplicate", deptName, postName));
                    continue;
                }
            }
            if(matchDepts.size()==1 && matchPosts.get(matchDepts.get(0).getId()).size()==1){
                try {
                    secondPostList.add(MemberPost.createSecondPost(vom.getId(),
                            matchDepts.get(0).getId(),
                            matchPosts.get(matchDepts.get(0).getId()).get(0).getId(), 
                            vom.getOrgAccountId(), 
                            Integer.valueOf(om.getPostById(vom.getOrgPostId()).getSortId().toString())));
                } catch (NumberFormatException e) {
                    logger.error("格式化错误："+om.getPostById(vom.getOrgPostId()).getSortId(),e);
                }
            }
        }
        return secondPostList;
    }

    protected void add(OrganizationServices organizationServices, V3xOrgEntity ent,ImpExpPojo pj) throws BusinessException {
        OrgManagerDirect omd = (OrgManagerDirect) AppContext.getBean("orgManagerDirect");
        if(ent.getSortId()==null){
            ent.setSortId(Long.valueOf(omd.getMaxSortNum(V3xOrgMember.class.getSimpleName(), ent.getOrgAccountId()) + 1L));
        }
        logger.info("add member=" + ent.getName());
        OrganizationMessage messages = omd.addMember((V3xOrgMember) ent);
        logger.info("ok add member=" + ent.getName());
    }

    protected void update(OrganizationServices organizationServices, V3xOrgEntity ent, ImpExpPojo pj) throws BusinessException {
        V3xOrgMember m = (V3xOrgMember) ent;
        if (m.getIsInternal()) {
            organizationServices.updateMember(m);
            //更新自定义的通讯录字段信息--在OrgManagerDirect中统一处理。
            //updateAddressBookinfo(m);
        } else {
            organizationServices.updateUnOrgMember(m);
        }
    }
	
	protected String msg4AddNoDouble(ImpExpPojo pj){//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_OK)
		return this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME)
		        +((ImpExpMember)pj).getLoginName();
		//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME)
	}
	
	protected String msg4AddNoDoubleCode(ImpExpPojo pj){//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_OK)
		return this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_CODE)
		        +((ImpExpMember)pj).getCode();
	}
	
	protected void addNoDouble(ImpExpPojo pj,Map<String,Object> stringMap,List pjs,Map mapReport){
		if(pj==null) {
			return ;
		}
		
		//boolean ok=true;
		ImpExpMember pm=(ImpExpMember)pj;
		if(stringMap!=null){
			if(stringMap.containsKey(pm.getLoginName())){
				ResultObject ro=this.newResultObject(pj
						   , this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_IGNORED)
						   , this.msg4AddNoDouble(pm));
				this.addReport(ro, IImexPort.RESULT_IGNORE, mapReport);
				return ;
			}
			
			if(stringMap.containsKey("code:"+pm.getCode())){
				ResultObject ro=this.newResultObject(pj
						   , this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_IGNORED)
						   , this.msg4AddNoDoubleCode(pm));
				this.addReport(ro, IImexPort.RESULT_IGNORE, mapReport);
				return ;
			}
			
			stringMap.put(pm.getLoginName(), pm);
			if(Strings.isNotBlank(pm.getCode())){
				stringMap.put("code:"+pm.getCode(), pm);
			}
		}
		
		if(pjs!=null) {
			pjs.add(pj);
		}
		
		return ;
	}
	
	protected String inCurrentAccount(V3xOrgEntity ent,V3xOrgAccount voa
            ,OrganizationServices  organizationServices){
        if (ent == null || voa == null) {
            return null;
        }

        long voaid = voa.getId();
        long entaid = ent.getOrgAccountId();

        if (voaid == entaid) {
            return null;
        }
		
		StringBuilder reason=new StringBuilder();
		try{
			V3xOrgMember m=(V3xOrgMember)ent;
			V3xOrgAccount oa=organizationServices.getOrgManager().getAccountById(entaid);
			String oan=
				oa==null?null:oa.getName();
			reason.append(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_NAME_LOGINNAME));
			//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_NAME_LOGINNAME)
			reason.append(m.getLoginName());
			if(StringUtils.hasText(oan)){
				reason.append(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ALERT_INACCOUNT)+oan);
				//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ALERT_INACCOUNT)
			}else
				reason.append(this.getMsgProvider().getMsg
						(MsgContants.ORG_IO_MSG_ALERT_INOTHERACCOUNT));
			//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ALERT_INOTHERACCOUNT)
			reason.append(this.getMsgProvider()
					.getMsg(MsgContants.ORG_IO_MSG_NAME_REG));//this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_NAME_REG)
		}catch(Exception e){
			
		}
		
		return reason.toString();
	}
	
    protected String getOKMsg4Add(V3xOrgEntity ent, OrganizationServices organizationServices) {
        return super.getOKMsg4Add(ent, organizationServices) + getOKMsg4Member(ent);
    }

    protected String getOKMsg4Update(V3xOrgEntity ent, OrganizationServices organizationServices) {

        return super.getOKMsg4Update(ent, organizationServices) + getOKMsg4Member(ent);
    }

    protected String getOKMsg4Member(V3xOrgEntity ent) {
        String msg = "";
        try {
            V3xOrgMember m = (V3xOrgMember) ent;
            msg += "  " + this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_NAME_LOGINNAME) + "：" + m.getLoginName();
        } catch (Exception e) {

        }
        return msg;
    }

    @Override
    protected Map add(OrganizationServices organizationServices, MetadataManager metadataManager, List ents,
            Map mapReport,Map<Long,ImpExpPojo> pjl) throws Exception {
        return commit(organizationServices, ents, mapReport, true);
    }

    protected Map update(OrganizationServices organizationServices, MetadataManager metadataManager, List ents,
            Map mapReport) throws Exception {
        return commit(organizationServices, ents, mapReport, false);
    }

    private Map commit(OrganizationServices organizationServices, List ents, Map mapReport, boolean isAdd)
            throws Exception {
        if (ents == null || mapReport == null) {
            return mapReport;
        }
        if (ents.size() == 0) {
            return mapReport;
        }

        OrderedMap memberMap = new ListOrderedMap();
        for (Object o : ents) {
            V3xOrgMember m = (V3xOrgMember) o;
            memberMap.put(m.getId(), m);
        }
        V3xOrgMember m = (V3xOrgMember) ents.get(0);
        Map<Long, String> r;
        try {
            r = organizationServices.synchMember(ents, false, false, m.getOrgAccountId());
        } catch (BusinessException be) {
            ResultObject ro = null;
            ro = this.newResultObject(OrgHelper.showMemberNameOnly(AppContext.getCurrentUser().getId()), this
                    .getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_FAILED), be.getLocalizedMessage());
            mapReport = this.addReport(ro, IImexPort.RESULT_ERROR, mapReport);
            return mapReport;
        }

        for (OrderedMapIterator it = memberMap.orderedMapIterator(); it.hasNext();) {
            Long id = (Long) it.next();
            V3xOrgMember member = (V3xOrgMember) memberMap.get(id);
            String value = r.get(id);
            if (value != null) {
                if ("1".equals(value.substring(0, 1))) {
                    log.error("保存人员出错：" + id + " " + value);
                    ResultObject ro = null;
                    if ("1|error add member for the same loginname already existed!".equals(value)) {
                        ro = this.newResultObject(member, this.getMsgProvider()
                                .getMsg(MsgContants.ORG_IO_MSG_OP_FAILED), ResourceUtil.getString("import.validate.check12"));
                    }else if("1|MEMBER_REPEAT_POST".equals(value)){
                        ro = this.newResultObject(member, this.getMsgProvider()
                                .getMsg(MsgContants.ORG_IO_MSG_OP_FAILED), ResourceUtil.getString("MessageStatus.MEMBER_REPEAT_POST"));
                    } else if(value.startsWith("1|添加人员出错:")){
                        ro = this.newResultObject(member, this.getMsgProvider()
                                .getMsg(MsgContants.ORG_IO_MSG_OP_FAILED),
                                this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_EXCEPTION) + value.substring(value.indexOf(":")+1));
                    }else {
                        ro = this.newResultObject(member, this.getMsgProvider()
                                .getMsg(MsgContants.ORG_IO_MSG_OP_FAILED),
                                this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_EXCEPTION) + value);
                    }
                    mapReport = this.addReport(ro, IImexPort.RESULT_ERROR, mapReport);

                } else {
                    if (isAdd) {
                        ResultObject ro = this.newResultObject(member,
                                this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_OK),
                                this.getOKMsg4Add(member, organizationServices));
                        mapReport = this.addReport(ro, IImexPort.RESULT_ADD, mapReport);
                    } else {
                        ResultObject ro = this.newResultObject(member,
                                this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_OP_OK),
                                this.getOKMsg4Update(member, organizationServices));
                        mapReport = this.addReport(ro, IImexPort.RESULT_UPDATE, mapReport);
                    }
                }
            }
        }
        return mapReport;
    }
    
    private void updateAddressBookinfo(V3xOrgMember m) throws BusinessException{
    	//更新自定义的通讯录字段
        int cLen=0;
        List<String> customerAddressBooklist = m.getCustomerAddressBooklist();
        MetadataColumnBO metadataColumn;
        if(null!=customerAddressBooklist && customerAddressBooklist.size()>0){
        	cLen=customerAddressBooklist.size();
        }
        AddressBookCustomerFieldInfoManager addressBookCustomerFieldInfoManager = (AddressBookCustomerFieldInfoManager) AppContext.getBean("addressBookCustomerFieldInfoManager");
        //自定义的通讯录字段
        AddressBook addressBook = addressBookCustomerFieldInfoManager.getByMemberId(m.getId());
        boolean isExist=true;
        if(null==addressBook){//如果沒有，新增
        	addressBook=new AddressBook();
        	addressBook.setId(UUIDLong.longUUID());
        	addressBook.setMemberId(m.getId());
        	addressBook.setCreateDate(new Date());
        	addressBook.setUpdateDate(new Date());
    		isExist=false;
        }else{//更新
        	addressBook.setUpdateDate(new Date());
        }
        AddressBookManager addressBookManager = (AddressBookManager) AppContext.getBean("addressBookManager");
        List<MetadataColumnBO> metadataColumnList=addressBookManager.getCustomerAddressBookList();
        
        for(int i=0;i<metadataColumnList.size();i++){
        	metadataColumn=metadataColumnList.get(i);
        	String columnName=metadataColumn.getColumnName();
        	Method method=addressBookManager.getSetMethod(columnName);
			if(null==method){
				 throw new BusinessException("自定义通讯录字段: "+metadataColumn.getLabel()+"不存在！");
			}
 			String value="";
 			if(i<cLen){
 				value=customerAddressBooklist.get(i);
 			}
        	try {
        		 if(metadataColumn.getType()==0){
				 String saveValue=(null==value || Strings.isBlank(value))?"":String.valueOf(value);
					method.invoke(addressBook, new Object[] { saveValue });
				 }
				 if(metadataColumn.getType()==1){
					 Double saveValue=(null==value || Strings.isBlank(value))?null:Double.valueOf(String.valueOf(value));
					 method.invoke(addressBook, new Object[] { saveValue });       
				 }
				 if(metadataColumn.getType()==2){
					 String saveValue=(null==value || Strings.isBlank(value))?"":String.valueOf(value);
					 method.invoke(addressBook, new Object[] {"".equals(saveValue)?null: Datetimes.parse(saveValue, "yyyy-MM-dd") });         
				 }
				 } catch (Exception e) {
				 }   
        }
        
        if(isExist){
        	addressBookCustomerFieldInfoManager.updateAddressBook(addressBook);
        }else{
        	addressBookCustomerFieldInfoManager.addAddressBook(addressBook);
        }
    	
    }
    protected V3xOrgDepartment getNeedDepartment(OrganizationServices organizationServices, String[] ncvalue,
            V3xOrgAccount voa) throws Exception {
        try {
            if (StringUtils.hasText(ncvalue[1])){
                V3xOrgDepartment dep =  departmentCodeMap.get(ncvalue[1]);//先按码匹配
                if (dep != null)
                    return dep;
            }
        }
        catch (Exception e) {
            logger.error("error", e);
        }
        try {//如果不是部门名称（编码）这种格式，兼容判断是否是全路径格式
			if(Strings.isNotBlank(ncvalue[2]) && ncvalue[2].indexOf("/") != -1){//如果是全路径部门名称,并且不止一级，其中含"/"
				V3xOrgDepartment dep = this.departmentFullpathMap.get(ncvalue[2]);
				if(dep != null)
				   return dep;
			}
		} catch (Exception e) {
			 logger.error("error", e);
		}
        try {
            V3xOrgDepartment dep = this.departmentNameMap.get(ncvalue[0]);
            if (dep != null)
                return dep;
            // return this.getRightDeptByName(organizationServices, ncvalue[0], voa);//按名平配，因为重名，会出现匹配错误部门
        } catch (Exception e) {
            logger.error("error", e);
            //throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_DEP));
        }

        throw new Exception(this.getMsgProvider().getMsg(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_DEP));
    }
}