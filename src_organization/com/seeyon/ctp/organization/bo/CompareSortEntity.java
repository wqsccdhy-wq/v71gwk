package com.seeyon.ctp.organization.bo;

import java.util.Comparator;

import com.fasterxml.jackson.core.sym.Name1;

public class CompareSortEntity implements Comparator<V3xOrgEntity> {

    private static CompareSortEntity compareSortEntity = null;

    public static CompareSortEntity getInstance() {
        if (compareSortEntity == null) {
            compareSortEntity = new CompareSortEntity();
        }
        return compareSortEntity;
    }

    public int compare(V3xOrgEntity ent1, V3xOrgEntity ent2) {
    	if(ent1 == null || ent2 == null){
    		return -1;
    	}
    	
    	if(ent1 instanceof V3xOrgAccount){//如果是单位，集团总是排最前。(集团预置数据排序号是1，可能会与新建的单位排序一致。)
    		V3xOrgAccount account = (V3xOrgAccount)ent1;
    		if(account.isGroup()){
    			return -1;
    		}
    	}
    	
    	Long id1 = ent1.getSortId();
        Long id2 = ent2.getSortId();
        if (id1 == null || id2 == null) {
            return -1;
        }
        if(id1 == id2){
            return ent1.getId().compareTo(ent2.getId());
        }else{
            return id1.compareTo(id2);
        }
    }
}
