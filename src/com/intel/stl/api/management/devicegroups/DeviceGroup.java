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

package com.intel.stl.api.management.devicegroups;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.intel.stl.api.management.NumberNode;
import com.intel.stl.api.management.XMLConstants;

@XmlRootElement(name = XMLConstants.DEVICE_GROUP)
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceGroup {
    @XmlElement(name = XMLConstants.NAME)
    private String name;

    @XmlElements({
            @XmlElement(name = XMLConstants.SI_GUID,
                    type = SystemImageGUID.class),
            @XmlElement(name = XMLConstants.NODE_GUID, type = NodeGUID.class),
            @XmlElement(name = XMLConstants.PORT_GUID, type = PortGUID.class) })
    private List<NumberNode> ids;

    @XmlElement(name = XMLConstants.NODE_DESC, type = NodeDesc.class)
    private List<NodeDesc> nodeDescs;

    @XmlElement(name = XMLConstants.NODE_TYPE, type = String.class)
    @XmlJavaTypeAdapter(NodeTypeAdapter.class)
    private List<NodeTypeAttr> nodeTypes;

    @XmlElement(name = XMLConstants.SELECT, type = String.class)
    @XmlJavaTypeAdapter(DGSelectAdapter.class)
    private List<DGSelect> selects;

    @XmlElement(name = XMLConstants.INCLUDE_GROUP, type = IncludeGroup.class)
    private List<IncludeGroup> includeGroups;

    public DeviceGroup() {
        super();
    }

    /**
     * Description:
     * 
     * @param name
     */
    public DeviceGroup(String name) {
        super();
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the ids
     */
    public List<NumberNode> getIDs() {
        return ids;
    }

    public void addID(NumberNode id) {
        if (id == null) {
            throw new IllegalArgumentException("null ID is not allowed.");
        }

        if (ids == null) {
            ids = new ArrayList<NumberNode>();
        }
        ids.add(id);
    }

    /**
     * @return the nodeDesc
     */
    public List<NodeDesc> getNodeDesc() {
        return nodeDescs;
    }

    public void addNodeDesc(NodeDesc desc) {
        if (desc == null) {
            throw new IllegalArgumentException("null NodeDesc is not allowed.");
        }

        if (nodeDescs == null) {
            nodeDescs = new ArrayList<NodeDesc>();
        }
        nodeDescs.add(desc);
    }

    /**
     * @return the nodeTypes
     */
    public List<NodeTypeAttr> getNodeTypes() {
        return nodeTypes;
    }

    public void addNodeType(NodeTypeAttr type) {
        if (type == null) {
            throw new IllegalArgumentException("null NodeType is not allowed.");
        }

        if (nodeTypes == null) {
            nodeTypes = new ArrayList<NodeTypeAttr>();
        }
        nodeTypes.add(type);
    }

    /**
     * @return the selects
     */
    public List<DGSelect> getSelects() {
        return selects;
    }

    public void addSelect(DGSelect select) {
        if (select == null) {
            throw new IllegalArgumentException("null Select is not allowed.");
        }

        if (selects == null) {
            selects = new ArrayList<DGSelect>();
        }
        selects.add(select);
    }

    /**
     * @return the includeGroups
     */
    public List<IncludeGroup> getIncludeGroups() {
        return includeGroups;
    }

    public void addIncludeGroup(IncludeGroup group) {
        if (group == null) {
            throw new IllegalArgumentException(
                    "null IncludeGroup is not allowed.");
        } else if (group.getValue().equals(name)) {
            throw new IllegalArgumentException("Can not reference to itself.");
        }

        if (includeGroups == null) {
            includeGroups = new ArrayList<IncludeGroup>();
        }
        includeGroups.add(group);
    }

    public void addIncludeGroup(String group) {
        if (group == null) {
            throw new IllegalArgumentException(
                    "null IncludeGroup is not allowed.");
        } else if (group.equals(name)) {
            throw new IllegalArgumentException("Can not reference to itself.");
        }

        if (includeGroups == null) {
            includeGroups = new ArrayList<IncludeGroup>();
        }
        includeGroups.add(new IncludeGroup(group));
    }

    public void removeIncludeGroup(String name) {
        if (includeGroups != null) {
            includeGroups.remove(new IncludeGroup(name));
        }
    }

    public void insertIncludeGroup(int index, String name) {
        if (includeGroups != null) {
            includeGroups.add(index, new IncludeGroup(name));
        }
    }

    public int indexOfIncludeGroup(String name) {
        IncludeGroup group = new IncludeGroup(name);
        if (includeGroups != null) {
            return includeGroups.indexOf(group);
        }
        return -1;
    }

    public boolean doesIncludeGroup(String name) {
        IncludeGroup group = new IncludeGroup(name);
        if (includeGroups != null) {
            return includeGroups.contains(group);
        }
        return false;
    }

    public void clear() {
        if (ids != null) {
            ids.clear();
        }

        if (nodeDescs != null) {
            nodeDescs.clear();
        }

        if (nodeTypes != null) {
            nodeTypes.clear();
        }

        if (selects != null) {
            selects.clear();
        }

        if (includeGroups != null) {
            includeGroups.clear();
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
        result = prime * result + ((ids == null) ? 0 : ids.hashCode());
        result =
                prime
                        * result
                        + ((includeGroups == null) ? 0 : includeGroups
                                .hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result =
                prime * result
                        + ((nodeDescs == null) ? 0 : nodeDescs.hashCode());
        result =
                prime * result
                        + ((nodeTypes == null) ? 0 : nodeTypes.hashCode());
        result = prime * result + ((selects == null) ? 0 : selects.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        DeviceGroup other = (DeviceGroup) obj;
        if (ids == null) {
            if (other.ids != null) {
                return false;
            }
        } else if (!ids.equals(other.ids)) {
            return false;
        }
        if (includeGroups == null) {
            if (other.includeGroups != null) {
                return false;
            }
        } else if (!includeGroups.equals(other.includeGroups)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (nodeDescs == null) {
            if (other.nodeDescs != null) {
                return false;
            }
        } else if (!nodeDescs.equals(other.nodeDescs)) {
            return false;
        }
        if (nodeTypes == null) {
            if (other.nodeTypes != null) {
                return false;
            }
        } else if (!nodeTypes.equals(other.nodeTypes)) {
            return false;
        }
        if (selects == null) {
            if (other.selects != null) {
                return false;
            }
        } else if (!selects.equals(other.selects)) {
            return false;
        }
        return true;
    }

    public DeviceGroup copy() {
        DeviceGroup res = new DeviceGroup(name);
        if (ids != null) {
            res.ids = new ArrayList<NumberNode>(ids.size());
            for (NumberNode id : ids) {
                res.ids.add(id.copy());
            }
        }
        if (nodeDescs != null) {
            res.nodeDescs = new ArrayList<NodeDesc>(nodeDescs.size());
            for (NodeDesc nd : nodeDescs) {
                res.nodeDescs.add(nd.copy());
            }
        }
        if (nodeTypes != null) {
            res.nodeTypes = new ArrayList<NodeTypeAttr>(nodeTypes);
        }
        if (selects != null) {
            res.selects = new ArrayList<DGSelect>(selects);
        }
        if (includeGroups != null) {
            res.includeGroups =
                    new ArrayList<IncludeGroup>(includeGroups.size());
            for (IncludeGroup ig : includeGroups) {
                res.includeGroups.add(ig.copy());
            }
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DeviceGroup [name=" + name + ", ids=" + ids + ", nodeDescs="
                + nodeDescs + ", nodeTypes=" + nodeTypes + ", selects="
                + selects + ", includeGroups=" + includeGroups + "]";
    }

}
