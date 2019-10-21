package com.seeyon.ctp.organization.bo;

import java.util.Comparator;

public class CompareTypeEntity implements Comparator<V3xOrgTeam> {

    private static CompareTypeEntity compareSortEntity = null;

    public static CompareTypeEntity getInstance() {
        if (compareSortEntity == null) {
            compareSortEntity = new CompareTypeEntity();
        }
        return compareSortEntity;
    }

    public int compare(V3xOrgTeam ent1, V3xOrgTeam ent2) {
        int result = 0;
        int id1 = ent1.getType();
        int id2 = ent2.getType();
        result = id1 > id2 ? 1 : (id1 < id2 ? -1 : 0);
        return result;
    }

}
