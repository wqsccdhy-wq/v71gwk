package com.seeyon.ctp.privilege.bo;

import java.util.Comparator;

public class CompareSortMenu implements Comparator<PrivMenuBO> {

    private static CompareSortMenu compareSortEntity = null;

    public static CompareSortMenu getInstance() {
        if (compareSortEntity == null) {
            compareSortEntity = new CompareSortMenu();
        }
        return compareSortEntity;
    }

    public int compare(PrivMenuBO ent1, PrivMenuBO ent2) {
        long id1 = ent1.getSortid();
        long id2 = ent2.getSortid();
        return id1 > id2 ? 1 : (id1 < id2 ? -1 : 0);
    }
}
