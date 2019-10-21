package com.seeyon.ctp.organization.selectpeople.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义选人界面的数据对象
 * @author wf
 *
 */
public class SelectPeopleObject {
	/**
	 * 数据的唯一性主键   如 id、表单流程节点 code:Sender 等
	 */
	String K;
	/**
	 * 显示名称，注意考虑国际化
	 */
	String N;
	/**
	 * 子对象
	 */
	List<SelectPeopleObjectSub> children;
	
	public String getK() {
		return K;
	}
	public void setK(String k) {
		K = k;
	}
	public String getN() {
		return N;
	}
	public void setN(String n) {
		N = n;
	}
	public List<SelectPeopleObjectSub> getChildren() {
		return children;
	}
	public void setChildren(List<SelectPeopleObjectSub> children) {
		this.children = children;
	}
	public void addChild(SelectPeopleObjectSub children) {
		if(this.children == null){
			this.children = new ArrayList<SelectPeopleObjectSub>();
		}
		this.children.add(children);
	}
	
	public void addAllChild(List<SelectPeopleObjectSub> childrens) {
		if(this.children == null){
			this.children = new ArrayList<SelectPeopleObjectSub>();
		}
		this.children.addAll(childrens);
	}
	
}
