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

package com.intel.stl.ui.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.intel.stl.ui.performance.ChartArgument;

public class PinDescription {
    private static final String DELIMITER = ";";

    public enum PinID {
        STATUS,
        HEALTH,
        WORST,
        SUBNET_BW, // bandwidth
        SUBNET_PR, // packet rate
        SUBNET_CG, // congestion
        SUBNET_SC, // SMA Congenstion
        SUBNET_SI, // Signal Integrity
        SUBNET_BB, // bubble
        SUBNET_SE, // security
        SUBNET_RT, // routing
        PERF_BW, // bandwidth
        PERF_PR, // packet rate
        PERF_CG, // congestion
        PERF_SC, // SMA Congenstion
        PERF_SI, // Signal Integrity
        PERF_BB, // bubble
        PERF_SE, // security
        PERF_RT, // routing
        PERF_PORT,
        PERF_PORT_ERR,
        PERF_NODE
    };

    private final PinID id;

    private final String title;

    private int height;

    private PinArgument argument;

    /**
     * Description:
     *
     * @param id
     */
    public PinDescription(PinID id, String title) {
        this(id, title, new PinArgument());
    }

    public PinDescription(PinID id, String title, PinArgument argument) {
        super();
        this.id = id;
        this.title = title;
        this.argument = argument;
    }

    /**
     * @param argument
     *            the argument to set
     */
    public void setArgument(PinArgument argument) {
        this.argument = argument;
    }

    /**
     * @return the id
     */
    public PinID getID() {
        return id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the argument
     */
    public PinArgument getArgument() {
        return argument;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     *
     * <i>Description:</i> human readable description of the pin. Can be used as
     * the tool tip for a pin card
     *
     * @return
     */
    public String getDescription() {
        if (argument.isEmpty()) {
            return title;
        }

        if (argument instanceof ChartArgument) {
            return ((ChartArgument<?>) argument).getFullName();
        } else {
            return getMetaDescription();
        }
    }

    public String getMetaDescription() {
        if (argument.isEmpty()) {
            return null;
        }

        StringBuffer sb = new StringBuffer("<html><body><table>");
        for (String key : argument.stringPropertyNames()) {
            sb.append("<tr><td><b>" + key + "</b></td><td>"
                    + argument.getProperty(key) + "</td></tr>");
        }
        sb.append("</table></body></html>");

        return sb.toString();
    }

    public String persistent() {
        if (title.contains(DELIMITER)) {
            throw new RuntimeException("Title '" + title
                    + "' constains reserved delimiter '" + DELIMITER + "'");
        }
        StringBuffer sb = new StringBuffer();
        sb.append(id.name() + DELIMITER);
        sb.append(title + DELIMITER);
        sb.append(Integer.toString(height) + DELIMITER);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            argument.store(out, "Argument");
        } catch (IOException e) {
            e.printStackTrace();
        }
        sb.append(out.toString());
        return sb.toString();
    }

    public static PinDescription restore(String str) {
        String[] segs = str.split(DELIMITER, 4);
        if (segs.length != 4) {
            throw new IllegalArgumentException(
                    "Invalid PinDescription persistent string '" + str + "'");
        }
        PinID id = PinID.valueOf(segs[0]);
        String title = segs[1];
        PinDescription res = new PinDescription(id, title);
        int height = Integer.parseInt(segs[2]);
        res.setHeight(height);
        PinArgument argument = new PinArgument();
        ByteArrayInputStream in = new ByteArrayInputStream(segs[3].getBytes());
        try {
            argument.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // NOTE, we convert generic PinArgument argument to specific argument.
        // This will solve the issues when we need to do comparison, equals or
        // hascode calculation etc. Some of operations may include class name
        // that will cause troubles to us. So it's better to do conversion here!
        // If you have other type of argument, please add your conversion!
        ChartArgument<?> chartArg = null;
        try {
            chartArg = ChartArgument.asChartArgument(argument);
        } catch (IllegalArgumentException e) {
        }
        if (chartArg != null) {
            res.setArgument(chartArg);
        } else {
            res.setArgument(argument);
        }

        return res;
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
        result = prime * result
                + ((argument == null) ? 0 : argument.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        PinDescription other = (PinDescription) obj;
        if (argument == null) {
            if (other.argument != null) {
                return false;
            }
        } else if (!argument.equals(other.argument)) {
            return false;
        }
        if (id != other.id) {
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
        return "PinDescription [id=" + id + ", title=" + title + ", argument="
                + argument + "]";
    }

}
