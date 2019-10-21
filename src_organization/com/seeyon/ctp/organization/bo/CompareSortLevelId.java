/**
 * 
 */
package com.seeyon.ctp.organization.bo;

import java.util.Comparator;

/**
 * 职务级别按照序号排序
 * 
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * @date 2012-12-27 
 */
public class CompareSortLevelId implements Comparator<V3xOrgLevel> {

    private static CompareSortLevelId instance = null;
    
    @Override
    public int compare(V3xOrgLevel level1, V3xOrgLevel level2) {
        return level1.getLevelId().compareTo(level2.getLevelId());
    }

    public static CompareSortLevelId getInstance() {
        if (instance == null) {
            instance = new CompareSortLevelId();
        }
        return instance;
    }

}
