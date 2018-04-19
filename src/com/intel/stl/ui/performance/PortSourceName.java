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

package com.intel.stl.ui.performance;

public class PortSourceName implements ISource {
    public final static String DELIMITER = ":";

    protected String vfName;

    protected String nodeDesc;

    protected int lid;

    protected short portNum;

    protected String sourceName;

    public PortSourceName(String sourceName) {
        this.sourceName = sourceName;
        fillSource(sourceName);
    }

    public PortSourceName(int lid, short portNum) {
        this(null, null, lid, portNum);
    }

    /**
     * Description:
     * 
     * @param lid
     * @param portNum
     */
    public PortSourceName(String nodeDesc, int lid, short portNum) {
        this(null, nodeDesc, lid, portNum);
    }

    public PortSourceName(String vfName, String nodeDesc, int lid, short portNum) {
        this.vfName = vfName;
        this.nodeDesc = nodeDesc;
        this.lid = lid;
        this.portNum = portNum;
        sourceName = getSourceName(vfName, nodeDesc, lid, portNum);
    }

    protected void fillSource(String name) {
        String[] segs = name.split(DELIMITER);
        if (segs.length == 3) {
            nodeDesc = segs[0];
            lid = Integer.parseInt(segs[1]);
            portNum = Short.parseShort(segs[2]);
        } else if (segs.length == 4) {
            vfName = segs[0];
            nodeDesc = segs[1];
            lid = Integer.parseInt(segs[2]);
            portNum = Short.parseShort(segs[3]);
        } else {
            throw new IllegalArgumentException(
                    "Invalid port source name format '" + name + "'");
        }
    }

    public String getVfName() {
        return vfName;
    }

    public String getNodeDesc() {
        return nodeDesc;
    }

    /**
     * @return the lid
     */
    public int getLid() {
        return lid;
    }

    /**
     * @return the portNum
     */
    public short getPortNum() {
        return portNum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.performance.ISource#sourceName()
     */
    @Override
    public String sourceName() {
        return sourceName;
    }

    public String getPrettyName() {
        return nodeDesc + DELIMITER + portNum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.performance.ISource#copy()
     */
    @Override
    public ISource copy() {
        return new PortSourceName(vfName, nodeDesc, lid, portNum);
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
        result = prime * result + lid;
        result = prime * result + portNum;
        result = prime * result + ((vfName == null) ? 0 : vfName.hashCode());
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
        PortSourceName other = (PortSourceName) obj;
        if (lid != other.lid) {
            return false;
        }
        if (portNum != other.portNum) {
            return false;
        }
        if (vfName == null) {
            if (other.vfName != null) {
                return false;
            }
        } else if (!vfName.equals(other.vfName)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PortSourceName [vfName=" + vfName + ", nodeDesc=" + nodeDesc
                + ", lid=" + lid + ", portNum=" + portNum + "]";
    }

    public static String getSourceName(String vfName, String nodeDesc, int lid,
            short portNum) {
        StringBuffer sb = new StringBuffer();
        if (vfName != null) {
            sb.append(vfName + DELIMITER);
        }
        if (nodeDesc != null) {
            sb.append(nodeDesc + DELIMITER);
        }
        sb.append(lid + DELIMITER + portNum);
        return sb.toString();
    }

    public static String getSourceName(String nodeDesc, int lid, short portNum) {
        return getSourceName(null, nodeDesc, lid, portNum);
    }

    public static String getSourceName(int lid, short portNum) {
        return getSourceName(null, null, lid, portNum);
    }
}
