package com.seeyon.ctp.organization.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.seeyon.ctp.organization.bo.V3xOrgEntity;

public class OrgTreeNode implements Serializable {
    private static final long serialVersionUID = 1L;
    //private static final String root ="0000";
    private String parentId;
    private String selfId;
    protected String nodeName;
    protected V3xOrgEntity obj;
    protected OrgTreeNode parentNode;
    protected List<OrgTreeNode> childList;

    public OrgTreeNode() {
        initChildList();
    }

    public OrgTreeNode(OrgTreeNode parentNode) {
        this.setParentNode(parentNode);
        this.setParentId(parentNode.getParentId());
        initChildList();
    }

    public boolean isLeaf() {
        if (childList == null) {
            return true;
        } else {
            if (childList.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /* 插入一个child节点到当前节点中 */
    public void addChildNode(OrgTreeNode treeNode) {
        initChildList();
        childList.add(treeNode);
    }

    public void initChildList() {
        if (childList == null)
            childList = new ArrayList<OrgTreeNode>();
    }

    /* 返回当前节点的父辈节点集合 */
    public List<OrgTreeNode> getElders() {
        List<OrgTreeNode> elderList = new ArrayList<OrgTreeNode>();
        OrgTreeNode parentNode = this.getParentNode();
        if (parentNode == null) {
            return elderList;
        } else {
            elderList.add(parentNode);
            elderList.addAll(parentNode.getElders());
            return elderList;
        }
    }

    /* 返回当前节点的晚辈集合 */
    public List<OrgTreeNode> getJuniors() {
        List<OrgTreeNode> juniorList = new ArrayList<OrgTreeNode>();
        List<OrgTreeNode> childList = this.getChildList();
        if (childList == null) {
            return juniorList;
        } else {
            int childNumber = childList.size();
            for (int i = 0; i < childNumber; i++) {
                OrgTreeNode junior = childList.get(i);
                juniorList.add(junior);
                juniorList.addAll(junior.getJuniors());
            }
            return juniorList;
        }
    }

    /* 返回当前节点的孩子集合 */
    public List<OrgTreeNode> getChildList() {
        return childList;
    }

/*     删除节点和它下面的晚辈 
    public void deleteNode() {
        OrgTreeNode parentNode = this.getParentNode();
        String id = this.getSelfId();

        if (parentNode != null) {
            parentNode.deleteChildNode(id);
        }
    }

     删除当前节点的某个子节点级联删除 
    public void deleteChildNode(String childId) {
        List<OrgTreeNode> childList = this.getChildList();
        int childNumber = childList.size();
        for (int i = 0; i < childNumber; i++) {
            OrgTreeNode child = childList.get(i);
            if (child.getSelfId().equals(childId)) {
                childList.remove(i);
                return;
            }
        }
    }*/

    /* 子树中动态的插入一个新的节点*/
    public boolean insertJuniorNode(OrgTreeNode treeNode) {
        String juniorParentId = treeNode.getParentId();
        if (this.selfId.equals(juniorParentId)) {
            addChildNode(treeNode);
            return true;
        } else {
            List<OrgTreeNode> childList = this.getChildList();
            int childNumber = childList.size();
            boolean insertFlag;

            for (int i = 0; i < childNumber; i++) {
                OrgTreeNode childNode = childList.get(i);
                insertFlag = childNode.insertJuniorNode(treeNode);
                if (insertFlag == true)
                    return true;
            }
            return false;
        }
    }

    /* 子树中找某个节点 */
    public OrgTreeNode findTreeNodeById(String id) {
        if (this.selfId.equals(id))
            return this;
        if (childList.isEmpty() || childList == null) {
            return null;
        } else {
            int childNumber = childList.size();
            for (int i = 0; i < childNumber; i++) {
                OrgTreeNode child = childList.get(i);
                OrgTreeNode resultNode = child.findTreeNodeById(id);
                if (resultNode != null) {
                    return resultNode;
                }
            }
            return null;
        }
    }
    
    /* 遍历子树，先序遍历 */
    public List<OrgTreeNode> traversal(){
        List<OrgTreeNode> l = new ArrayList<OrgTreeNode>();
        traverse(l,true);
        return l;
        
    }

    private List<OrgTreeNode> traverse(List<OrgTreeNode> list,boolean firstEnter) {
        if(selfId.length()>8 && !firstEnter){
            list.add(this);
        }
        if (childList == null || childList.isEmpty()){
            return list;
        }
        int childNumber = childList.size();
        List<OrgTreeNode> tempChildList = new ArrayList<OrgTreeNode>(childList);
        Collections.sort(tempChildList, new Comparator<OrgTreeNode>() {
            public int compare(OrgTreeNode s1, OrgTreeNode s2) {
                return s1.getObj().getSortId().compareTo(s2.getObj().getSortId());
            }
        });
        for (int i = 0; i < childNumber; i++) {
            OrgTreeNode child = tempChildList.get(i);
            child.traverse(list,false);
        }
        return list;
    }

    public void setChildList(List<OrgTreeNode> childList) {
        this.childList = childList;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public OrgTreeNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(OrgTreeNode parentNode) {
        this.parentNode = parentNode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public V3xOrgEntity getObj() {
        return obj;
    }

    public void setObj(V3xOrgEntity obj) {
        this.obj = obj;
    }
}
