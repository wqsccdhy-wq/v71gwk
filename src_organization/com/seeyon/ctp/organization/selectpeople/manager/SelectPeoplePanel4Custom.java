package com.seeyon.ctp.organization.selectpeople.manager;


import java.util.Date;
import java.util.List;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.selectpeople.bo.SelectPeopleObject;
import com.seeyon.ctp.organization.selectpeople.bo.SelectPeopleObjectSub;
import com.seeyon.ctp.util.Strings;

/**
 * 自定义的选人界面抽象类
 * @author wf
 *
 */
public abstract class SelectPeoplePanel4Custom extends AbstractSelectPeoplePanel {    
    private OrgManager orgManager;

    public OrgManager getOrgManager() {
    	if(orgManager == null){
    		orgManager = (OrgManager)AppContext.getBean("orgManager");
    	}
        return orgManager;
    }
    //list1（左上）的展现方式
    public static enum Area1Type{
        //列表
        LIST,
        //树
        TREE,
    }
    
    //list2(左下)的数据选择方式
    public static enum Area2SelectMode{
        //可作为单独元素选择到右侧已选区域
    	SINGLE,
        //与区域1的元素 拼接在一起作为整体供选择
    	UNION,
    }
    
    @Override
    public Date getLastModifyTimestamp(Long accountId) throws BusinessException {
    	
        return getOrgManager().getModifiedTimeStamp(accountId);
    }
    
    /**
     * 自定义页签的名称，注意国际化
     * @return
     */
    public abstract String getName();
    
    /**
     * 自定义生成选人页签。
     * @return
     */
    public boolean isCustom(){
    	return true;
    }
    
	/**
	 * 自定义选人页签，是否显示区域2,默认不显示
	 * @return
	 */
    public boolean isShowArea2(){
    	return false;
    }
    
    /**
     * 自定义页签区域1的展示方式
     * 两种方式：
     * 1.列表（默认方式）
     * 2.树机构（暂不支持，有场景再扩展）
     * 使用枚举  PanelStyle
     * @return
     */
    public String getArea1ShowType(){
    	return Area1Type.LIST.name();
    }
    
    /**
     * 区域2的数据选择方式， 
     * 提供2种方式：
     * 1.可作为单独元素选择到右侧已选区域（默认方式）
     * 2.与区域1的元素 拼接在一起作为整体供选择，这种情况下已选元素的数据类型就是当前控件的类型
     * 使用枚举 Area2SelectMode
     */
    public String getArea2SelectMode(){
    	return Area2SelectMode.SINGLE.name();
    }
    
    /**
     * 当area2SelectMode为union，即选择区域2的内容时 同时会将区域1和区域2的内容做连接后 选到已选区域。
     * 这里获取的是 区域1内容和区域2内容值的连接符 如：‘_’
     * 如：区域1内容： 用车人         值：UseMember
     *    区域2内容：部门主管     值：DeptManager
     * 选择后的数据为：  用车人部门主管    值:UseMember_DeptManager
     * @return
     */
    public String getSp(){
    	return "";
    }
    
    /**
     * 自定义页签返回给选人界面的数据
     * @param memberId 当前人员id
     * @param accountId 切换单位id
     * @param extParameters 扩展参数
     * @return
     * @throws BusinessException
     */
    public abstract List<SelectPeopleObject> getData(Long memberId, Long accountId,String extParameters) throws BusinessException;
    
    @Override
    public String getJsonString(long memberId, long accountId, String extParameters) throws BusinessException {
    	List<SelectPeopleObject> data = this.getData(memberId, accountId, extParameters);
        StringBuilder a = new StringBuilder();
        a.append("[");
        int i = 0;

        for (SelectPeopleObject obj : data) {
            if (i++ != 0) {
                a.append(",");
            }
            a.append("{");
            a.append("K").append(":\"").append(Strings.escapeJavascript(obj.getK())).append("\"");
            a.append(",").append("N").append(":\"").append(Strings.escapeJavascript(obj.getN())).append("\"");
            
            StringBuilder b = new StringBuilder();
            b.append("[");
            int j = 0;
            List<SelectPeopleObjectSub> subList = obj.getChildren();
            if(Strings.isNotEmpty(subList)){
            	for(SelectPeopleObjectSub sub : subList){
                    if (j++ != 0) {
                        b.append(",");
                    }
            		b.append("{");
            		b.append("K").append(":\"").append(Strings.escapeJavascript(sub.getK())).append("\"");
            		b.append(",").append("N").append(":\"").append(Strings.escapeJavascript(sub.getN())) .append("\"");
            		b.append(",").append("T").append(":\"").append(Strings.escapeJavascript(sub.getT())) .append("\"");
            		b.append("}");
            	}
            }
            b.append("]");
            a.append(",R : ").append(b);
            a.append("}");
        }

        a.append("]");

        return a.toString();
    }
    
    public String getCustomPanelProperties(){
    	String panelType = this.getType();
    	String panelName =  this.getName();
    	boolean isShowArea2 = this.isShowArea2();
    	String area1ShowType = this.getArea1ShowType();
    	String area2SelectMode = this.getArea2SelectMode();
    	String sp =  this.getSp();
    	String customProperties = panelType+"|"+panelName+"|"+isShowArea2+"|"+area1ShowType+"|"+area2SelectMode+"|"+sp;
    	return customProperties;
    }
}
