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

import java.util.Properties;

import com.intel.stl.ui.common.PinArgument;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.model.HistoryType;

public abstract class ChartArgument<S extends ISource> extends PinArgument {
    private static final long serialVersionUID = 5414961409003011918L;

    /**
     * Chart name
     */
    public static final String NAME = "Name";

    public static final String FULL_NAME = "Full Name";

    /**
     * Chart data provider, such as DeviceGroup or VirtualFabric.
     *
     * @see com.intel.stl.ui.performance.provider.IDataProvider
     */
    public static final String PROVIDER = "Provider";

    public static final String DATA_TYPE = "Data Type";

    public static final String HISTORY_TYPE = "History Type";

    /**
     * Description:
     *
     */
    public ChartArgument() {
        super();
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public String getName() {
        return getProperty(NAME);
    }

    public void setFullName(String fullName) {
        put(FULL_NAME, fullName);
    }

    public String getFullName() {
        return getProperty(FULL_NAME);
    }

    public void setProvider(String name) {
        put(PROVIDER, name);
    }

    public String getProvider() {
        return getProperty(PROVIDER);
    }

    public void setDataType(DataType type) {
        put(DATA_TYPE, type.name());
    }

    public DataType getDataType() {
        String str = getProperty(DATA_TYPE);
        if (str != null) {
            return DataType.valueOf(str);
        } else {
            return null;
        }
    }

    public void setHistoryType(HistoryType type) {
        put(HISTORY_TYPE, type.name());
    }

    public HistoryType getHistoryTpe() {
        String str = getProperty(HISTORY_TYPE);
        if (str != null) {
            return HistoryType.valueOf(str);
        } else {
            return null;
        }
    }

    /**
     *
     * <i>Description:</i> the source name will be used for PerformanceItems. In
     * future we may extend source name to an object rather than a String to
     * give us enough flexibility
     *
     * @return
     */
    public abstract S[] getSources();

    public abstract void setSources(S[] sources);

    private static final String DELIMITER = ",";

    protected String toString(String[] sourceNames) {
        StringBuffer sb = new StringBuffer();
        for (String source : sourceNames) {
            if (source.contains(DELIMITER)) {
                throw new IllegalArgumentException("Source name '" + source
                        + "' contains DELIMITER '" + DELIMITER + "'");
            }

            if (sb.length() == 0) {
                sb.append(source);
            } else {
                sb.append(DELIMITER + source);
            }
        }
        return sb.toString();
    }

    protected String[] toArray(String str) {
        if (str == null) {
            return new String[0];
        } else {
            return str.split(DELIMITER);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        String name = getName();
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("rawtypes")
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
        ChartArgument other = (ChartArgument) obj;
        String name = getName();
        String otherName = other.getName();
        if (name == null) {
            if (otherName != null) {
                return false;
            }
        } else if (!name.equals(otherName)) {
            return false;
        }
        return true;
    }

    public static ChartArgument<?> asChartArgument(Properties props) {
        if (props instanceof ChartArgument) {
            return (ChartArgument<?>) props;
        }

        if (props.getProperty(GroupChartArgument.GROUPS) != null
                || props.getProperty(GroupChartArgument.VFS) != null) {
            GroupChartArgument gca = new GroupChartArgument();
            for (String name : props.stringPropertyNames()) {
                gca.put(name, props.getProperty(name));
            }
            return gca;
        } else {
            if (props.getProperty(PortChartArgument.NODE_LID) != null) {
                if (props.getProperty(
                        PortCounterChartArgument.FIELD_NAME) == null) {

                    PortChartArgument<PortSourceName> pca =
                            new PortChartArgument<PortSourceName>();
                    for (String name : props.stringPropertyNames()) {
                        pca.put(name, props.getProperty(name));
                    }
                    return pca;
                } else {
                    PortCounterChartArgument pcca =
                            new PortCounterChartArgument();
                    for (String name : props.stringPropertyNames()) {
                        pcca.put(name, props.getProperty(name));
                    }
                    return pcca;
                }
            }
        }
        throw new IllegalArgumentException("Unknow properties " + props);
    }
}
