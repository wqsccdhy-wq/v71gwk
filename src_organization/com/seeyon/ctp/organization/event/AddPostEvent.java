package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgPost;

public class AddPostEvent extends Event {
	private static final long serialVersionUID = 440610076099027974L;
	private V3xOrgPost post;

	public V3xOrgPost getPost() {
		return post;
	}

	public void setPost(V3xOrgPost post) {
		this.post = post;
	}

	public AddPostEvent(Object source) {
		super(source);
	}

}