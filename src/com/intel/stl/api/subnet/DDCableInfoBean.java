/**
 * Copyright (c) 2017, Intel Corporation
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

package com.intel.stl.api.subnet;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.Utils;

/**
 * Cable Info Record from SA populated by the connect manager. QSFP interpreted.
 *
 * Reference: QSFP-DD-mgmt-snapshot-11-13-17
 *
 * NOTE: we only implement the fields we are interested in
 */
public class DDCableInfoBean implements Serializable {
    private static final long serialVersionUID = 1L;

    protected static Logger log =
            LoggerFactory.getLogger(DDCableInfoBean.class);

    private byte id;

    private String vendorName;// 16 bytes;

    private byte[] vendorOui;// 3 bytes;

    private String vendorPn;// 16 bytes;

    private String vendorRev;// 2 bytes;

    private String vendorSN;// 16 bytes;

    private String dateCode;// 8 bytes;

    private Date date;

    /**
     * this attribute is power in W, i.e. it's the value from FE times 0.25 W
     */
    private double maxPower; // 201 Maximum power consumption in multiples of
                             // 0.25 W rounded up to the next whole multiple of
                             // 0.25 W

    // prompted to support unsigned int
    private short cableLength; // 202 (In units of 'meters').

    private byte connector; // 203

    private byte xmitTech; // 212

    public DDCableInfoBean() {
        super();
    }

    /**
     * @return the id
     */
    public byte getId() {
        return id;
    }

    /**
     * @return the vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * @return the vendorOui
     */
    public byte[] getVendorOui() {
        return vendorOui;
    }

    /**
     * @return the vendorPn
     */
    public String getVendorPn() {
        return vendorPn;
    }

    /**
     * @return the vendorRev
     */
    public String getVendorRev() {
        return vendorRev;
    }

    /**
     * @return the vendorSN
     */
    public String getVendorSN() {
        return vendorSN;
    }

    /**
     * @return the dataCode
     */
    public String getDateCode() {
        return dateCode;
    }

    public Date getDate() {
        return date;
    }

    /**
     * @return the maxPower
     */
    public double getMaxPower() {
        return maxPower;
    }

    /**
     * @return the cableLength
     */
    public short getCableLength() {
        return cableLength;
    }

    /**
     * @return the connector
     */
    public byte getConnector() {
        return connector;
    }

    /**
     * @return the xmitTech
     */
    public byte getXmitTech() {
        return xmitTech;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(byte id) {
        this.id = id;
    }

    /**
     * @param vendorName
     *            the vendorName to set
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * @param vendorOui
     *            the vendorOui to set
     */
    public void setVendorOui(byte[] vendorOui) {
        this.vendorOui = vendorOui;
    }

    /**
     * @param vendorPn
     *            the vendorPn to set
     */
    public void setVendorPn(String vendorPn) {
        this.vendorPn = vendorPn;
    }

    /**
     * @param vendorRev
     *            the vendorRev to set
     */
    public void setVendorRev(String vendorRev) {
        this.vendorRev = vendorRev;
    }

    /**
     * @param vendorSN
     *            the vendorSN to set
     */
    public void setVendorSN(String vendorSN) {
        this.vendorSN = vendorSN;
    }

    /**
     * @param dateCode
     *            the dateCode to set
     */
    public void setDateCode(String year, String month, String day, String lot) {
        dateCode = year + "/" + month + "/" + day + "-" + lot;
        SimpleDateFormat formatter = null;
        if (lot == null || lot.isEmpty()) {
            formatter = new SimpleDateFormat("yy/MM/dd-");
        } else {
            formatter = new SimpleDateFormat("yy/MM/dd-hh");
        }
        try {
            date = formatter.parse(dateCode);
        } catch (ParseException e) {
            date = null;
            log.warn("Parsing exception for date code string", e);
        }
    }

    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @param maxPower
     *            the maxPower to set
     */
    public void setMaxPower(byte maxPower) {
        this.maxPower = Utils.unsignedByte(maxPower) * 0.25;
    }

    /**
     * @param cableLength
     *            the cableLength to set
     */
    public void setCableLength(byte cableLength) {
        this.cableLength = Utils.unsignedByte(cableLength);
    }

    /**
     * @param connector
     *            the connector to set
     */
    public void setConnector(byte connector) {
        this.connector = connector;
    }

    /**
     * @param xmitTech
     *            the xmitTech to set
     */
    public void setXmitTech(byte xmitTech) {
        this.xmitTech = xmitTech;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DDCableInfoBean [id=" + id + ", vendorName=" + vendorName
                + ", vendorOui=" + Arrays.toString(vendorOui) + ", vendorPn="
                + vendorPn + ", vendorRev=" + vendorRev + ", vendorSN="
                + vendorSN + ", dateCode=" + dateCode + ", date=" + date
                + ", maxPower=" + maxPower + ", cableLength=" + cableLength
                + ", connector=" + connector + ", xmitTech=" + xmitTech + "]";
    }

}
