/**
 * $Author: $
 * $Rev: $
 * $Date:: 2012-06-05 15:14:56#$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.organization.bo;

import java.util.Comparator;

/**
 * <p>Title: 组织模型组织path对比工具类</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: seeyon.com</p>
 * 
 * @since CTP2.0
 * @author lilong
 */
public class CompareUnitPath{

	private static Comparator<V3xOrgUnit> compareSortEntity = new Comparator<V3xOrgUnit>(){
		
		public int compare(V3xOrgUnit unit1, V3xOrgUnit unit2) {
			String path1 = ((V3xOrgUnit) unit1).getPath();
			String path2 = ((V3xOrgUnit) unit2).getPath();
	        if (path1.length() == path2.length()) {
	            return path1.compareTo(path2);
	        }
	        else {
	            // 不同级，父<子
	            return path1.length() < path2.length() ? -1 : 1;
	        }
		}
	};
	
	private static Comparator<V3xOrgUnit> compareSortEntity2 = new Comparator<V3xOrgUnit>(){
		public int compare(V3xOrgUnit unit1, V3xOrgUnit unit2) {
			String path1 = ((V3xOrgUnit) unit1).getPath();
			String path2 = ((V3xOrgUnit) unit2).getPath();
			Long sortId1 = ((V3xOrgUnit) unit1).getSortId();
			Long sortId2 = ((V3xOrgUnit) unit2).getSortId();
			
			if (path1.length() == path2.length()) {
				return sortId1 > sortId2 ? 1 : (sortId1 < sortId2 ? -1 : 0);
			}
			else {
				// 不同级，父<子
				return path1.length() < path2.length() ? -1 : 1;
			}
		}
	};

    /**
     * 先按照path长度排，再按照path顺序排
     * @return
     */
    public static Comparator<V3xOrgUnit> getInstance() {
        return compareSortEntity;
    }
    
    /**
     * 先按照path长度排，再按照sortId顺序排
     * @return
     */
    public static Comparator<V3xOrgUnit> getInstance2() {
    	return compareSortEntity2;
    }

}
