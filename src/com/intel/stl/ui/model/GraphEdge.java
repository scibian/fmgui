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

package com.intel.stl.ui.model;

import java.util.Map;
import java.util.TreeMap;

public class GraphEdge extends GraphCell {
    private static final long serialVersionUID = 6368674272408088705L;

    private int fromLid;

    private byte fromType;

    private int toLid;

    private byte toType;

    private Map<Integer, Integer> links;

    /**
     * Description:
     * 
     */
    public GraphEdge() {
        super();
    }

    public GraphEdge(int fromLid, byte fromType, int toLid, byte toType,
            Map<Integer, Integer> links) {
        super();
        this.fromLid = fromLid;
        this.fromType = fromType;
        this.toLid = toLid;
        this.toType = toType;
        this.links = links;
        this.name = createName();
    }

    /**
     * Description:
     * 
     * @param fromLid
     * @param toLid
     * @param links
     */
    public GraphEdge(String name, int fromLid, byte fromType, int toLid,
            byte toType, Map<Integer, Integer> links) {
        super(name);
        this.fromLid = fromLid;
        this.fromType = fromType;
        this.toLid = toLid;
        this.toType = toType;
        this.links = links;
    }

    public GraphEdge normalize() {
        if (fromLid < toLid) {
            Map<Integer, Integer> reversedLinks =
                    new TreeMap<Integer, Integer>();
            for (Integer key : links.keySet()) {
                reversedLinks.put(links.get(key), key);
            }
            return new GraphEdge(toLid, toType, fromLid, fromType,
                    reversedLinks);
        } else {
            return this;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.model.GraphCell#isVertex()
     */
    @Override
    public boolean isVertex() {
        return false;
    }

    protected String createName() {
        return fromLid + "-" + toLid;
    }

    /**
     * @return the fromLid
     */
    public int getFromLid() {
        return fromLid;
    }

    /**
     * @param fromLid
     *            the fromLid to set
     */
    public void setFromLid(int fromLid) {
        this.fromLid = fromLid;
    }

    /**
     * @return the fromType
     */
    public byte getFromType() {
        return fromType;
    }

    /**
     * @param fromType
     *            the fromType to set
     */
    public void setFromType(byte fromType) {
        this.fromType = fromType;
    }

    /**
     * @return the toLid
     */
    public int getToLid() {
        return toLid;
    }

    /**
     * @param toLid
     *            the toLid to set
     */
    public void setToLid(int toLid) {
        this.toLid = toLid;
    }

    /**
     * @return the toType
     */
    public byte getToType() {
        return toType;
    }

    /**
     * @param toType
     *            the toType to set
     */
    public void setToType(byte toType) {
        this.toType = toType;
    }

    /**
     * @return the links
     */
    public Map<Integer, Integer> getLinks() {
        return links;
    }

    /**
     * @param links
     *            the links to set
     */
    public void setLinks(Map<Integer, Integer> links) {
        this.links = links;
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
        result = prime * result + fromLid;
        result = prime * result + fromType;
        result = prime * result + ((links == null) ? 0 : links.hashCode());
        result = prime * result + toLid;
        result = prime * result + toType;
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
        GraphEdge other = (GraphEdge) obj;
        if (fromLid != other.fromLid) {
            return false;
        }
        if (fromType != other.fromType) {
            return false;
        }
        if (links == null) {
            if (other.links != null) {
                return false;
            }
        } else if (!links.equals(other.links)) {
            return false;
        }
        if (toLid != other.toLid) {
            return false;
        }
        if (toType != other.toType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return fromLid + "-" + toLid + ":" + links.size() + " " + links;
    }
}
