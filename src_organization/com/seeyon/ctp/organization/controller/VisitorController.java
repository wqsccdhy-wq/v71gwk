package com.seeyon.ctp.organization.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.config.manager.ConfigManager;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.po.config.ConfigItem;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.OrgConstants.Role_NAME;
import com.seeyon.ctp.organization.manager.VisitorManager;
import com.seeyon.ctp.organization.po.OrgVisitor;
import com.seeyon.ctp.util.DateUtil;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.authenticate.domain.User;

@CheckRoleAccess(roleTypes={Role_NAME.GroupAdmin,Role_NAME.AccountAdministrator})
public class VisitorController extends BaseController {
	protected AppLogManager		appLogManager;
	protected VisitorManager	visitorManager;
	protected ConfigManager		configManager;
    
	public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }
	
	public void setVisitorManager(VisitorManager visitorManager) {
		this.visitorManager = visitorManager;
	}
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	
	/**
	 * 访客管理
	 * @param request
	 * @param response
	 * @return
	 * @throws BusinessException
	 */
	public ModelAndView showVisitorframe(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
        ModelAndView mav = new ModelAndView("apps/organization/visitor/visitorHome");
        visitor(mav);
        return mav;
    }
    
	/**
	 * 访客应用
	 * @param request
	 * @param response
	 * @return
	 * @throws BusinessException
	 */
	public ModelAndView visitorApp(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
        ModelAndView mav = new ModelAndView("apps/organization/visitor/visitorApp");
        visitor(mav);
        return mav;
    }
	
	/**
	 * 保存--访客应用
	 * @param request
	 * @param response
	 * @return
	 * @throws BusinessException
	 */
	public ModelAndView saveApps(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		ModelAndView mav = new ModelAndView("apps/organization/visitor/visitorApp");
        List<ConfigItem> configItemList = new ArrayList<ConfigItem>();
        String visitorApps = request.getParameter("visitorapps");
        User user = AppContext.getCurrentUser();
        if(Strings.isNotBlank(visitorApps)){
        	 String names[] = visitorApps.split(",");
             // 遍历页面的访客应用选项CheckBox   有就更新，没有就新增
             for (String name : names) {
             	String cat = name.substring(0,name.indexOf(":"));
         		ConfigItem cf = configManager.getConfigItem(OrgConstants.ORG_VISITOR_CONFIG_CATEGORY, OrgConstants.ORG_VISITOR_CONFIG_ITEM_APP_SWITCH, AppContext.getCurrentUser().getAccountId());
     			// 已存在配置，更新
     			if(cf != null){
     				cf.setModifyDate(DateUtil.currentTimestamp());
     				String cv = cf.getConfigValue();
     				if(Strings.isNotBlank(cv) && !cv.contains(cat)){
     					cf.setConfigValue(cv+","+cat);
     				} else {
     					cf.setConfigValue("meeting");
     				}
     				cf.setOrgAccountId(AppContext.getCurrentUser().getAccountId());
     				configManager.updateConfigItem(cf);
     				configItemList.add(cf);
     				appLogManager.insertLog4Account(user, user.getAccountId(), -805, ResourceUtil.getString("visitor.tap.app.meetingMGT"));
     			} else {
     				// 不存在配置，添加
     				ConfigItem cfi = new ConfigItem();
     				cfi.setId(UUIDLong.longUUID());
     				cfi.setConfigCategory(OrgConstants.ORG_VISITOR_CONFIG_CATEGORY);
     				cfi.setConfigItem(OrgConstants.ORG_VISITOR_CONFIG_ITEM_APP_SWITCH);
     				cfi.setConfigValue("meeting");
     				cfi.setOrgAccountId(AppContext.getCurrentUser().getAccountId());
     				cfi.setCreateDate(DateUtil.currentTimestamp());
     				configManager.addConfigItem(cfi);
     				configItemList.add(configManager.getConfigItem(OrgConstants.ORG_VISITOR_CONFIG_CATEGORY, OrgConstants.ORG_VISITOR_CONFIG_ITEM_APP_SWITCH, AppContext.getCurrentUser().getAccountId()));
     				appLogManager.insertLog4Account(user, user.getAccountId(), -805, ResourceUtil.getString("visitor.tap.app.meetingMGT"));
     			}
         	}
        } else {
        	ConfigItem cf = configManager.getConfigItem(OrgConstants.ORG_VISITOR_CONFIG_CATEGORY, OrgConstants.ORG_VISITOR_CONFIG_ITEM_APP_SWITCH, AppContext.getCurrentUser().getAccountId());
 			if(cf != null){
 				if(cf != null){
     				cf.setModifyDate(DateUtil.currentTimestamp());
     				cf.setConfigValue(null);
     				configManager.updateConfigItem(cf);
     				configItemList.add(cf);
     				appLogManager.insertLog4Account(user, user.getAccountId(), -806, ResourceUtil.getString("visitor.tap.app.meetingMGT"));
     			}
 			}
        }
        mav.addObject("appsmgtList", configItemList);
        
        return null;
	}
	
	private ModelAndView visitor(ModelAndView mav){
		List<ConfigItem> configItemList = new ArrayList<ConfigItem>();
		ConfigItem cf = configManager.getConfigItem(OrgConstants.ORG_VISITOR_CONFIG_CATEGORY, OrgConstants.ORG_VISITOR_CONFIG_ITEM_APP_SWITCH, AppContext.getCurrentUser().getAccountId());
		// 已存在配置，更新
		if(cf != null){
			configItemList.add(cf);
		} else {
			// 不存在配置，添加
			ConfigItem cfi = new ConfigItem();
			cfi.setId(UUIDLong.longUUID());
			cfi.setConfigCategory(OrgConstants.ORG_VISITOR_CONFIG_CATEGORY);
			cfi.setConfigItem(OrgConstants.ORG_VISITOR_CONFIG_ITEM_APP_SWITCH);
			cfi.setOrgAccountId(AppContext.getCurrentUser().getAccountId());
			cfi.setCreateDate(DateUtil.currentTimestamp());
			configManager.addConfigItem(cfi);
			configItemList.add(configManager.getConfigItem(OrgConstants.ORG_VISITOR_CONFIG_CATEGORY, OrgConstants.ORG_VISITOR_CONFIG_ITEM_APP_SWITCH, AppContext.getCurrentUser().getAccountId()));
		}
        mav.addObject("appsmgtList", configItemList);
        mav.addObject("orgAccountId", AppContext.getCurrentUser().getAccountId());
        return mav;
	}
	
	/**
	 * 访客信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView visitorInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("apps/organization/visitor/visitorInfo");
        Map<String, Object> params = new HashMap<String, Object>();
        FlipInfo flipInfo = new FlipInfo();
        params.put("orgAccountId", request.getParameter("orgAccountId"));
        flipInfo.setPage(1);
        flipInfo.setSize(20);
        List<OrgVisitor> orgVisitorList = new ArrayList<OrgVisitor>();
        params.put("orgAccountId", AppContext.getCurrentUser().getAccountId());
        orgVisitorList = visitorManager.visitorInfo(flipInfo, params).getData();
        flipInfo.setData(orgVisitorList);
        mav.addObject("visitorTable", flipInfo);
        return mav;
    }
	
	/**
	 * 隐私保密协议页面
	 * @param request
	 * @param response
	 * @return
	 * @throws BusinessException
	 */
	public ModelAndView showAgreement(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
        ModelAndView mav = new ModelAndView("apps/organization/visitor/wechatLicenseAgreement");
        return mav;
	}
	
	/**
	 * 访客日志
	 * @param request
	 * @param response
	 * @return
	 * @throws BusinessException
	 */
	public ModelAndView visitorAppLog(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
        ModelAndView mav = new ModelAndView("apps/organization/visitor/visitorAppLog");
        return mav;
	}
	
}
