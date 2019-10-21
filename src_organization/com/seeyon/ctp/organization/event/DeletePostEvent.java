package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgPost;

public class DeletePostEvent extends Event {
	private static final long serialVersionUID = -8319467962914611306L;
	private V3xOrgPost post;

	public V3xOrgPost getPost() {
		return post;
	}

	public void setPost(V3xOrgPost post) {
		this.post = post;
	}

	public DeletePostEvent(Object source) {
		super(source);
	}

}