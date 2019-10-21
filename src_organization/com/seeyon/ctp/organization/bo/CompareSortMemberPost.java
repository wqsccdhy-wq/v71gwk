package com.seeyon.ctp.organization.bo;

import java.util.Comparator;

public class CompareSortMemberPost implements Comparator<MemberPost> {

    private static CompareSortMemberPost compareSortEntity = null;

    public static CompareSortMemberPost getInstance() {
        if (compareSortEntity == null) {
            compareSortEntity = new CompareSortMemberPost();
        }
        return compareSortEntity;
    }

    public int compare(MemberPost ent1, MemberPost ent2) {
    	if(ent1 == null || ent2 == null){
    		return -1;
    	}
    	
    	Long id1 = ent1.getSortId();
        Long id2 = ent2.getSortId();
        if (id1 == null || id2 == null) {
            return -1;
        }

        return id1.compareTo(id2);
    }
}
