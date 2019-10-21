package com.seeyon.ctp.organization.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.seeyon.ctp.organization.bo.V3xOrgDepartment;

public class OrgTree {

    private OrgTreeNode root;
    private List<OrgTreeNode> tempNodeList;
    private boolean isValidTree = true;
    private int treeDeep = 0;
    private String accountPath;
    private static final int PATHSETP =4;

    public OrgTree() {
        
    }

    public OrgTree(List<OrgTreeNode> treeNodeList,String accountPath) {
        this.tempNodeList = treeNodeList;
        this.accountPath = accountPath;
        generateTree();
    }

    public static OrgTreeNode getOrgTreeNodeById(OrgTreeNode tree, String id) {
        if (tree == null)
            return null;
        OrgTreeNode treeNode = tree.findTreeNodeById(id);
        return treeNode;
    }

    /** 
     * 根据tempNodeList产生一棵树
     */
    private void generateTree() {
        HashMap nodeMap = putNodesIntoMap();
        putChildIntoParent(nodeMap);
    }

    /**
     * 根据tempNodeList生成一个HashMap
     * @return 包含所提供的OrgTreeNode的 HashMap
     */
    protected HashMap putNodesIntoMap() {
        HashMap nodeMap = new HashMap<String, OrgTreeNode>();
        if(tempNodeList.size()>0){
            OrgTreeNode rootNode = new OrgTreeNode();
            rootNode.setSelfId(this.accountPath);
            this.root = rootNode;
            nodeMap.put(rootNode.getSelfId(), this.root);
        }
        Iterator it = tempNodeList.iterator();
        while (it.hasNext()) {
            OrgTreeNode treeNode = (OrgTreeNode) it.next();
            String id = treeNode.getSelfId();
            int deep = (id.length()-this.accountPath.length())/PATHSETP;
            if(deep>this.treeDeep){
                this.treeDeep = deep;
            }
            nodeMap.put(id, treeNode);
        }
        return nodeMap;
    }

    /**
     * 建立TreeNode之间的父子关系
     * @param nodeMap 包含所提供的OrgTreeNode的 HashMap
     */
    protected void putChildIntoParent(HashMap nodeMap) {
        Iterator it = nodeMap.values().iterator();
        while (it.hasNext()) {
            OrgTreeNode treeNode = (OrgTreeNode) it.next();
            String parentId = treeNode.getParentId();
            if (nodeMap.containsKey(parentId)) {
                OrgTreeNode parentNode = (OrgTreeNode) nodeMap.get(parentId);
                if (parentNode == null) {
                    this.isValidTree = false;
                    return;
                } else {
                    parentNode.addChildNode(treeNode);
                }
            }
        }
    }

    /**
     * 初始化tempNodeList
     */
    protected void initTempNodeList() {
        if (this.tempNodeList == null) {
            this.tempNodeList = new ArrayList<OrgTreeNode>();
        }
    }

    /**
     * 向tempNodeList添加节点
     */
    public void addOrgTreeNode(OrgTreeNode treeNode) {
        initTempNodeList();
        this.tempNodeList.add(treeNode);
    }

    /**
     * 向一个树中增加节点
     * @return 是否添加成功 true/false
     */
    public boolean insertOrgTreeNode(OrgTreeNode OrgTreeNode) {
        boolean insertFlag = root.insertJuniorNode(OrgTreeNode);
        return insertFlag;
    }

    /**
     * 把实体转化为OrgTreeNode
     * @param entityList 实体列表
     *@return 包含传入实体的OrgTreeNode的列表
     */
    public static List<OrgTreeNode> changeEnititiesToOrgTreeNodes(List<V3xOrgDepartment> entityList) {
        V3xOrgDepartment orgEntity = new V3xOrgDepartment();
        List<OrgTreeNode> tempNodeList = new ArrayList<OrgTreeNode>();
        OrgTreeNode TreeNode;

        Iterator it = entityList.iterator();
        while (it.hasNext()) {
            orgEntity = (V3xOrgDepartment) it.next();
            TreeNode = new OrgTreeNode();
            TreeNode.setObj(orgEntity);
            TreeNode.setParentId(orgEntity.getParentPath());
            TreeNode.setSelfId(orgEntity.getPath());
            TreeNode.setNodeName(orgEntity.getName());
            tempNodeList.add(TreeNode);
        }
        return tempNodeList;
    }

    public boolean isValidTree() {
        return this.isValidTree;
    }

    public OrgTreeNode getRoot() {
        return root;
    }

    public void setRoot(OrgTreeNode root) {
        this.root = root;
    }

    public List<OrgTreeNode> getTempNodeList() {
        return tempNodeList;
    }

    public void setTempNodeList(List<OrgTreeNode> tempNodeList) {
        this.tempNodeList = tempNodeList;
    }
    public int getTreeDeep() {
        return this.treeDeep;
    }

}
