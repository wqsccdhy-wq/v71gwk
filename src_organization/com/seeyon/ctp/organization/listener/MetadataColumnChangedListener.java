package com.seeyon.ctp.organization.listener;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.metadata.enums.ColumnTypeEnum;
import com.seeyon.ctp.common.metadata.event.MetadataColumnChangedEvent;
import com.seeyon.ctp.common.po.metadata.CtpMetadataColumn;
import com.seeyon.ctp.organization.dao.OrgCache;
import com.seeyon.ctp.util.annotation.ListenEvent;

public class MetadataColumnChangedListener {

    @ListenEvent(event = MetadataColumnChangedEvent.class, async = true)
    public void updateModifiedTimeStamp(MetadataColumnChangedEvent event) {
    	OrgCache orgCache = (OrgCache) AppContext.getBean("orgCache");
		/*
		 * CtpMetadataColumn metadataColumn = event.getCtpMetadataColumn();
		 * if(metadataColumn.getType() == ColumnTypeEnum.Enums.ordinal() ||
		 * metadataColumn.getType() == ColumnTypeEnum.Member.ordinal() ||
		 * metadataColumn.getType() == ColumnTypeEnum.Department.ordinal()){ }
		 */
		orgCache.updateModifiedTimeStamp();
    }
}
