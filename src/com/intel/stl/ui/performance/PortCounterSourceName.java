/**
 * Copyright (c) 2016, Intel Corporation
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

public class PortCounterSourceName extends PortSourceName {

    private String fieldName;

    /**
     * Description:
     *
     * @param vfName
     * @param nodeDesc
     * @param lid
     * @param portNum
     */
    public PortCounterSourceName(String vfName, String nodeDesc, int lid,
            short portNum) {
        this(vfName, nodeDesc, lid, portNum, null);
    }

    /**
     * Description:
     *
     * @param nodeLid
     * @param portNumber
     */
    public PortCounterSourceName(int nodeLid, short portNumber) {
        this(null, null, nodeLid, portNumber, null);
    }

    /**
     * Description:
     *
     * @param vfName
     * @param nodeDesc
     * @param lid
     * @param portNum
     * @param fieldName2
     */
    public PortCounterSourceName(String vfName, String nodeDesc, int lid,
            short portNum, String fieldName) {
        super(vfName, nodeDesc, lid, portNum);
        this.fieldName = fieldName;
        if (fieldName != null) {
            sourceName = sourceName + DELIMITER + fieldName;
        }
    }

    /**
     * Description:
     *
     * @param nodeLid
     * @param portNumber
     * @param field
     */
    public PortCounterSourceName(int nodeLid, short portNumber, String field) {
        this(null, null, nodeLid, portNumber, field);
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.ISource#copy()
     */
    @Override
    public ISource copy() {
        return new PortCounterSourceName(vfName, nodeDesc, lid, portNum,
                fieldName);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((fieldName == null) ? 0 : fieldName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PortCounterSourceName other = (PortCounterSourceName) obj;
        String fieldName = getFieldName();
        String otherName = other.getFieldName();
        if (fieldName == null) {
            if (otherName != null) {
                return false;
            }
        } else if (!fieldName.equals(otherName)) {
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
        return "PortCounterSourceName [vfName=" + vfName + ", nodeDesc="
                + nodeDesc + ", lid=" + lid + ", portNum=" + portNum
                + ", fieldName=" + fieldName + "]";
    }
}
