package com.seeyon.apps.addressbook.webmodel;

import java.util.ArrayList;
import java.util.List;

public class AddressBookObject {
	public AddressBookObject(){
		this.children = new ArrayList();
	}
	// id
	private Long id;
	// 名称
	private String name;
	// 类型
	private String type;
	//总数
	private Integer total;
	//子对象
	private List children;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Object> getChildren() {
		return children;
	}
	public void setChildren(List children) {
		this.children = children;
	}
	public void addChild(Object children) {
		if(this.children == null){
			this.children = new ArrayList<Object>();
		}
		this.children.add(children);
	}
	
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public void addAllChild(List childrenList) {
		if(childrenList==null) return;
		if(this.children == null){
			this.children = new ArrayList<Object>();
		}
		this.children.addAll(childrenList);
	}

}
