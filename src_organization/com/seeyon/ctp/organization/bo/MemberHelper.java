package com.seeyon.ctp.organization.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.seeyon.ctp.util.Strings;

public class MemberHelper {
	
	/**
	 * 判断人员副岗列表中是否包含当前部门
	 */
	public static boolean isSndPostContainDept(V3xOrgMember member, Long deptId) {
		List<MemberPost> second_post = member.getSecond_post();
		if(deptId != null){
			for(MemberPost memberPost : second_post){
				if(deptId.equals(memberPost.getDepId())){
					return true;
				}
			}			
		}
		return false;		
	}
	
	/**
	 * 判断人员副岗列表中是否包含当前岗位
	 */
	public static boolean isSndPostContainPost(V3xOrgMember member, Long postId){
		List<MemberPost> second_post = member.getSecond_post();
		if(postId != null){
			for(MemberPost memberPost : second_post){
				if(postId.equals(memberPost.getPostId())){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断人员副岗列表中是否包含当前部门下的岗位
	 */
	public static boolean isSndPostContain(V3xOrgMember member, Long deptId, Long postId){
		if(deptId!=null&&postId!=null){
			List<MemberPost> second_post = member.getSecond_post();
			for(MemberPost memberPost : second_post){
				if(deptId.equals(memberPost.getDepId())&&postId.equals(memberPost.getPostId())){
					return true;
				}
			}			
		}
		return false;
	}

	
	/**
	 * 檢測我是否在制定的部門(s)中兼職，如果在，則返回兼職部門id
	 * 
	 * @param member
	 * @param deptsId
	 * @param postId
	 * @return
	 */
	public static List<Long> getSndPostDepartId(V3xOrgMember member, Collection<Long> deptsId, Long postId){
		List<Long> resul = new ArrayList<Long>();
		if(deptsId != null && postId != null){
			List<MemberPost> second_post = member.getSecond_post();
			for(MemberPost memberPost : second_post){
				if(deptsId.contains(memberPost.getDepId())&&postId.equals(memberPost.getPostId())){
					resul.add(memberPost.getDepId());
				}
			}			
		}
		
		return resul;
	}
	
	/**
     * 判断人员副岗列表中是否包含当前部门
     */
    public static boolean isSndPostContainDept(V3xOrgMember member, Set<Long> deptIds) {
        List<MemberPost> second_post = member.getSecond_post();
        if(Strings.isNotEmpty(deptIds)){
            for(MemberPost memberPost : second_post){
                if(deptIds.contains(memberPost.getDepId())){
                    return true;
                }
            }           
        }
        return false;       
    }
	
}
