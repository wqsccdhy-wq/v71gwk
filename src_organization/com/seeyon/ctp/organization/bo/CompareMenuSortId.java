/**
 * 
 */
package com.seeyon.ctp.organization.bo;

import java.util.Comparator;

import com.seeyon.ctp.privilege.bo.PrivMenuBO;

/**
 * 职务级别按照序号排序
 * 
 * @author <a href="tanmf@seeyon.com">Tanmf</a>   
 * @date 2012-12-27 
 */
public class CompareMenuSortId implements Comparator<PrivMenuBO>  {

    private static CompareMenuSortId instance = null;  
    
    @Override
    public int compare(PrivMenuBO menu1, PrivMenuBO menu2) {
        return menu1.getSortid().compareTo(menu2.getSortid());
    }

    public static CompareMenuSortId getInstance() {
        if (instance == null) {
            instance = new CompareMenuSortId();
        }
        return instance;
    }

}
