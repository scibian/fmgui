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

public class PortCounterChartArgument
        extends PortChartArgument<PortCounterSourceName> {

    private static final long serialVersionUID = -3012846244021056051L;

    public static final String FIELD_NAME = "Field Name";

    public void setFieldName(String fieldName) {
        put(FIELD_NAME, fieldName);
    }

    public String getFieldName() {
        return getProperty(FIELD_NAME);
    }

    /**
     * <i>Description:</i>
     *
     * @param vfName
     * @param nodeDesc
     * @param lid
     * @param portNum
     * @param fieldName
     */
    private void setSource(String vfName, String nodeDesc, int lid,
            short portNum, String fieldName) {
        setSource(vfName, nodeDesc, lid, portNum);
        setFieldName(fieldName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.PortChartArgument#setSources(com.intel.stl.
     * ui.performance.PortSourceName[])
     */
    @Override
    public void setSources(PortCounterSourceName[] sources) {
        setSource(sources[0].getVfName(), sources[0].getNodeDesc(),
                sources[0].getLid(), sources[0].getPortNum(),
                sources[0].getFieldName());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.ChartArgument#getSources()
     */
    @Override
    public PortCounterSourceName[] getSources() {
        PortCounterSourceName source = new PortCounterSourceName(getVfName(),
                getNodeDesc(), getLid(), getPortNum(), getFieldName());
        return new PortCounterSourceName[] { source };
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
        PortCounterChartArgument other = (PortCounterChartArgument) obj;
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

}
