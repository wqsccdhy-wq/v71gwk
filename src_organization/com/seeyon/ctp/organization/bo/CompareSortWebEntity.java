package com.seeyon.ctp.organization.bo;

import java.util.Comparator;

import com.seeyon.ctp.organization.webmodel.WebV3xOrgSecondPost;

public class CompareSortWebEntity implements Comparator<WebV3xOrgSecondPost> {
    
    private static CompareSortWebEntity compareSortWebEntity = null;

    public static CompareSortWebEntity getInstance() {
        if (compareSortWebEntity == null) {
            compareSortWebEntity = new CompareSortWebEntity();
        }
        return compareSortWebEntity;
    }

    public int compare(WebV3xOrgSecondPost ent1, WebV3xOrgSecondPost ent2) {
        long id1 = ent1.getSortId();
        long id2 = ent2.getSortId();
        return id1 > id2 ? 1 : (id1 < id2 ? -1 : 0);
    }
}
