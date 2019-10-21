package com.seeyon.ctp.organization.bo;
import java.util.Comparator;

public class CompareTimeEntity  implements Comparator<V3xOrgEntity>{
	
    private static CompareTimeEntity compareTimeEntity = null;   
    
    public static CompareTimeEntity getInstance(){   
        if(compareTimeEntity == null){   
        	compareTimeEntity = new CompareTimeEntity();   
        }   
        return compareTimeEntity;
    }

	public int compare(V3xOrgEntity ent1, V3xOrgEntity ent2) {
        int result = 0;
        long id1 = ((V3xOrgEntity)ent1).getCreateTime().getTime();
        long id2 = ((V3xOrgEntity)ent2).getCreateTime().getTime();
        result = id1 > id2 ? 1 : (id1<id2 ? -1 : 0);
        return result;
	}
}

