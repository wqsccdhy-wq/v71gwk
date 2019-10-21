package com.seeyon.ctp.organization.bo;

import java.util.Comparator;

import com.seeyon.ctp.organization.po.OrgRelationship;

public class CompareSortRelationship {

    private static Comparator<V3xOrgRelationship> compareSortEntity = new Comparator<V3xOrgRelationship>(){

        public int compare(V3xOrgRelationship ent1, V3xOrgRelationship ent2) {
        	if(ent1.getSortId() == null || ent2.getSortId() == null){
        		return 1;
        	}
        	
            long id1 = ent1.getSortId();
            long id2 = ent2.getSortId();
            return id1 > id2 ? 1 : (id1 < id2 ? -1 : 0);
        }
    	
    };
    
    private static Comparator<OrgRelationship> compareSortEntity2 = new Comparator<OrgRelationship>(){

        public int compare(OrgRelationship ent1, OrgRelationship ent2) {
        	if(ent1.getSortId() == null || ent2.getSortId() == null){
        		return 1;
        	}
        	
            long id1 = ent1.getSortId();
            long id2 = ent2.getSortId();
            return id1 > id2 ? 1 : (id1 < id2 ? -1 : 0);
        }
    	
    };

    /**
     * 按照V3xOrgRelationship.sort排序
     * @return
     */
    public static Comparator<V3xOrgRelationship> getInstance() {
        return compareSortEntity;
    }
    
    /**
     * 按照OrgRelationship.sort排序
     * @return
     */
    public static Comparator<OrgRelationship> getInstance2() {
    	return compareSortEntity2;
    }

}
