package com.seeyon.ctp.organization.event;

import com.seeyon.ctp.event.Event;
import com.seeyon.ctp.organization.bo.V3xOrgPost;

public class UpdatePostEvent extends Event {
	private static final long serialVersionUID = -8810065022677531758L;
	private V3xOrgPost post;
	private V3xOrgPost oldPost;

	public V3xOrgPost getPost() {
		return post;
	}

	public void setPost(V3xOrgPost post) {
		this.post = post;
	}

	public UpdatePostEvent(Object source) {
		super(source);
	}

    public V3xOrgPost getOldPost() {
        return oldPost;
    }

    public void setOldPost(V3xOrgPost oldPost) {
        this.oldPost = oldPost;
    }

}