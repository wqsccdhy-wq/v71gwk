package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;

/**
 * 集群其它节点的人员状态改变（增删改）事件，仅供内部使用。
 * 
 * @author wangwenyou
 * 
 */
public class ClusterMemberEvent extends Event {
	public enum Action {
		Add, Update, Delete
	}

	private Action action;


	private long id;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2387628658791695804L;

	public ClusterMemberEvent(Object source) {
		super(source);
	}

	public Action getAction() {
		return action;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
}
