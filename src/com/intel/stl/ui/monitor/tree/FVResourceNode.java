/**
 * Copyright (c) 2015, Intel Corporation
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Intel Corporation nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intel.stl.ui.monitor.tree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.intel.stl.api.StringUtils;
import com.intel.stl.ui.monitor.TreeNodeType;

/**
 *
 * The class provides an implementation of the TreeNode interface
 */
public class FVResourceNode implements TreeNode {

    /*
     * Type of this node, which is used by a renderer to set appropriate icon
     * for the node
     */
    private TreeNodeType mType;

    /*
     * Title for a node
     */
    private String mTitle;

    /*
     * Vector holding the children of a node
     */
    private Vector<FVResourceNode> mChildren = new Vector<FVResourceNode>();

    /*
     * The parent node
     */
    private FVResourceNode mParent;

    /**
     * Id for the node. For HCA/SW, it will be lid, for a Port, it will be
     * portNumber. For others, it should be type id.
     */
    private int id;

    private final long guid;

    private final String guidStr;

    public FVResourceNode(String pTitle, TreeNodeType pType, int id) {
        this(pTitle, pType, id, -1);
    }

    public FVResourceNode(String pTitle, TreeNodeType pType, int id,
            long guid) {
        mTitle = pTitle;
        mType = pType;
        this.id = id;
        this.guid = guid;
        guidStr = StringUtils.longHexString(guid);
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the guid
     */
    public long getGuid() {
        return guid;
    }

    /**
     * @return the guidStr
     */
    public String getGuidStr() {
        return guidStr;
    }

    public void addChild(FVResourceNode child) {
        mChildren.add(child);
        child.setParent(this);
    }

    public void addChild(int index, FVResourceNode child) {
        mChildren.add(index, child);
        child.setParent(this);
    }

    public FVResourceNode removeChild(int index) {
        FVResourceNode node = mChildren.remove(index);
        if (node != null) {
            node.setParent(null);
        }
        return node;
    }

    public void setParent(FVResourceNode pParent) {
        mParent = pParent;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    @Override
    public FVResourceNode getChildAt(int pChildIndex) {
        return mChildren.elementAt(pChildIndex);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    @Override
    public int getChildCount() {
        return mChildren.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeNode#getParent()
     */
    @Override
    public FVResourceNode getParent() {
        return mParent;
    }

    public FVResourceNode getRoot() {
        FVResourceNode res = this;
        while (res.getParent() != null) {
            res = res.getParent();
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    @Override
    public int getIndex(TreeNode pNode) {
        return mChildren.indexOf(pNode);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    @Override
    public boolean getAllowsChildren() {

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    @Override
    public boolean isLeaf() {
        return (mChildren.size() == 0);
    }

    public boolean isTypeMatched(TreeNodeType type) {
        if (mType == type) {
            return true;
        } else if (type == TreeNodeType.NODE && (mType == TreeNodeType.HFI
                || mType == TreeNodeType.SWITCH)) {
            return true;
        }
        return false;
    }

    public boolean isNode() {
        return mType == TreeNodeType.HFI || mType == TreeNodeType.SWITCH;
    }

    public boolean isPort() {
        return mType == TreeNodeType.ACTIVE_PORT
                || mType == TreeNodeType.INACTIVE_PORT;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.tree.TreeNode#children()
     */
    @Override
    public Enumeration<FVResourceNode> children() {
        return mChildren.elements();
    }

    /**
     * @return the mChildren
     */
    public Vector<FVResourceNode> getChildren() {
        return mChildren;
    }

    public TreePath getPath() {
        List<FVResourceNode> path = new ArrayList<FVResourceNode>();
        FVResourceNode tmp = this;
        path.add(tmp);
        while ((tmp = tmp.getParent()) != null) {
            path.add(0, tmp);
        }
        return new TreePath(path.toArray(new FVResourceNode[0]));
    }

    /**
     * The node object should override this method to provide a text that will
     * be displayed for the node in the tree.
     */
    @Override
    public String toString() {
        return mTitle;
    }

    public String getName() {
        return mTitle;
    }

    public TreeNodeType getType() {
        return mType;
    }

    public void setType(TreeNodeType pType) {
        mType = pType;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public FVResourceNode copy() {
        FVResourceNode res = null;
        if (isNode()) {
            res = new FVResourceNode(mTitle, mType, id, guid);
        } else {
            res = new FVResourceNode(mTitle, mType, id);
        }
        res.mParent = mParent;
        res.mChildren = new Vector<FVResourceNode>();
        for (FVResourceNode child : mChildren) {
            res.addChild(child.copy());
        }
        return res;
    }

    public FVResourceNode filter(INodeVisbilityIndicator indicator) {
        FVResourceNode res = null;
        if (isNode()) {
            res = new FVResourceNode(mTitle, mType, id, guid);
        } else {
            res = new FVResourceNode(mTitle, mType, id);
        }
        res.mParent = mParent;
        res.mChildren = new Vector<FVResourceNode>();
        for (int i = 0; i < mChildren.size(); i++) {
            FVResourceNode child = mChildren.get(i);
            if (indicator.isVisible(child)) {
                res.addChild(child.filter(indicator));
            }
        }
        return res;
    }

    public void dump(PrintStream out, String prefix) {
        out.println(
                prefix + mTitle + " " + mType + "[" + id + ":" + guid + "]");
        for (FVResourceNode child : mChildren) {
            child.dump(out, prefix + "  ");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (guid ^ (guid >>> 32));
        result = prime * result + ((mTitle == null) ? 0 : mTitle.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FVResourceNode)) {
            return false;
        }
        FVResourceNode other = (FVResourceNode) obj;
        if (guid != other.guid) {
            return false;
        }
        if (mTitle == null) {
            if (other.mTitle != null) {
                return false;
            }
        } else if (!mTitle.equals(other.mTitle)) {
            return false;
        }
        return true;
    }

    /**
     *
     * <i>Description:</i>
     *
     * @param node
     * @return
     */
    public boolean hasSamePath(FVResourceNode anotherNode) {
        if (anotherNode == null) {
            return false;
        }

        TreePath path = getPath();
        TreePath anotherPath = anotherNode.getPath();
        return path.equals(anotherPath);
    }

} // class FVResourceNode
