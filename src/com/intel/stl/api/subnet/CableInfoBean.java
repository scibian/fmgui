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

package com.intel.stl.api.subnet;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cable Info Record from SA populated by the connect manager. QSFP interpreted.
 */
public class CableInfoBean implements Serializable {
    // we need to handle unsigned values
    private static final long serialVersionUID = 1L;

    protected static Logger log = LoggerFactory.getLogger(CableInfoBean.class);

    private byte id;

    private PowerClassType powerClass;

    // extIdent;
    private byte pwrClassLow;

    private byte extIdentOther;

    private boolean txCDRSupported;

    private boolean rxCDRSupported;

    private byte pwrClassHigh;

    private byte connector;

    private byte[] specComp;// 8 bytes;

    private byte encode;

    private byte bitRateLow;

    private byte extRateComp;

    private byte lenSmf;

    private int om3Length;

    private int om2Length;

    private byte lenOm1;

    private byte lenOm4;

    private int om4Length;

    // devTech;
    private byte xmitTech;

    private byte devTechOther;

    private String vendorName;// 16 bytes;

    private byte extMod;

    private byte[] vendorOui;// 3 bytes;

    private String vendorPn;// 16 bytes;

    private String vendorRev;// 2 bytes;

    private byte[] waveAtten;// 2 bytes;

    private byte[] waveTol;// 2 bytes;

    private int maxCaseTemp;

    private byte ccBase;

    private byte linkCodes;

    // rxtxOptEquemp;
    private boolean txInpEqAutoAdp;

    private boolean txInpEqFixProg;

    private boolean rxOutpEmphFixProg;

    private boolean rxOutpAmplFixProg;

    // rxtxOptCdrsquel;
    private boolean txCDROnOffCtrl;

    private boolean rxCDROnOffCtrl;

    private boolean txCdrLol;

    private boolean rxCdrLol;

    private boolean rxSquelDis;

    private boolean rxOutDis;

    private boolean txSquelDis;

    private boolean txSquelchImp;

    // memtxOptPagesquel;
    private boolean memPage02Provided;

    private boolean memPage01Provided;

    private boolean rateSel;

    private boolean txDis;

    private boolean txFault;

    private boolean txSquelOmapav;

    private boolean txLos;

    private String vendorSN;// 16 bytes;

    private String dateCode;// 8 bytes;

    private Date date;

    private byte diagMonType;

    private byte optionsEnh;

    private byte bitRateHigh;

    private byte ccExt;

    private byte[] vendor;// 26 bytes;

    private byte opaCertCable;

    private boolean certCableFlag;

    private byte vendor2;

    private byte opaCertDataRate;

    private byte[] vendor3;// 3 bytes;

    private int reachClass;

    private CertifiedRateType certDataRate;

    public CableInfoBean() {
        super();
    }

    /**
     * @return the id
     */
    public byte getId() {
        return id;
    }

    /**
     * @return the extId
     */
    public PowerClassType getPowerClass() {
        return powerClass;
    }

    /**
     * @return the connector
     */
    public byte getConnector() {
        return connector;
    }

    /**
     * @return the bitRateLow
     */
    public byte getBitRateLow() {
        return bitRateLow;
    }

    /**
     * @return the bitRateHigh
     */
    public byte getBitRateHigh() {
        return bitRateHigh;
    }

    /**
     * @return the om3Length
     */
    public int getOm3Length() {
        return om3Length;
    }

    /**
     * @return the om2Length
     */
    public int getOm2Length() {
        return om2Length;
    }

    /**
     * @return the copperLength
     */
    public int getOm4Length() {
        return om4Length;
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
     * @return the maxCaseTemp
     */
    public int getMaxCaseTemp() {
        return maxCaseTemp;
    }

    /**
     * @return the ccBase
     */
    public byte getCcBase() {
        return ccBase;
    }

    /**
     * @return the linkCodes
     */
    public byte getLinkCodes() {
        return linkCodes;
    }

    /**
     * @return the txInpEqAutoAdp
     */
    public boolean getTxInpEqAutoAdp() {
        return txInpEqAutoAdp;
    }

    /**
     * @return the txInpEqFixProg
     */
    public boolean getTxInpEqFixProg() {
        return txInpEqFixProg;
    }

    /**
     * @return the rxOutpEmphFixProg
     */
    public boolean getRxOutpEmphFixProg() {
        return rxOutpEmphFixProg;
    }

    /**
     * @return the rxOutpAmplFixProg
     */
    public boolean getRxOutpAmplFixProg() {
        return rxOutpAmplFixProg;
    }

    /**
     * @return the txCDROnOffCtrl
     */
    public boolean getTxCDROnOffCtrl() {
        return txCDROnOffCtrl;
    }

    /**
     * @return the rxCDROnOffCtrl
     */
    public boolean getRxCDROnOffCtrl() {
        return rxCDROnOffCtrl;
    }

    public boolean getTxCdrLol() {
        return txCdrLol;
    }

    public boolean getRxCdrLol() {
        return rxCdrLol;
    }

    /**
     * @return the txSquelchImp
     */
    public boolean isTxSquelchImp() {
        return txSquelchImp;
    }

    /**
     * @return the memPage02Provided
     */
    public boolean isMemPage02Provided() {
        return memPage02Provided;
    }

    /**
     * @return the memPage01Provided
     */
    public boolean isMemPage01Provided() {
        return memPage01Provided;
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

    public Date getDateCodeAsDate() {
        return date;
    }

    /**
     * @return the ccExt
     */
    public byte getCcExt() {
        return ccExt;
    }

    /**
     * @return the certCableFlag
     */
    public boolean getCertCableFlag() {
        return certCableFlag;
    }

    /**
     * @return the reachClass
     */
    public int getReachClass() {
        return reachClass;
    }

    /**
     * @return the certDataRate
     */
    public CertifiedRateType getCertDataRate() {
        return certDataRate;
    }

    public byte getPwrClassLow() {
        return pwrClassLow;
    }

    public byte getExtIdentOther() {
        return extIdentOther;
    }

    public byte getPwrClassHigh() {
        return pwrClassHigh;
    }

    public byte[] getSpecComp() {
        return specComp;
    }

    public byte getEncode() {
        return encode;
    }

    public byte getExtRateComp() {
        return extRateComp;
    }

    public byte getLenSmf() {
        return lenSmf;
    }

    public byte getLenOm1() {
        return lenOm1;
    }

    public byte getXmitTech() {
        return xmitTech;
    }

    public byte getDevTechOther() {
        return devTechOther;
    }

    public byte getExtMod() {
        return extMod;
    }

    public byte[] getWaveAtten() {
        return waveAtten;
    }

    public byte[] getWaveTol() {
        return waveTol;
    }

    public boolean getRxSquelDis() {
        return rxSquelDis;
    }

    public boolean getRxOutDis() {
        return rxOutDis;
    }

    public boolean getTxSquelDis() {
        return txSquelDis;
    }

    public boolean getMemPage02Provided() {
        return memPage02Provided;
    }

    public boolean getMemPage01Provided() {
        return memPage01Provided;
    }

    public boolean getRateSel() {
        return rateSel;
    }

    public boolean getTxDis() {
        return txDis;
    }

    public boolean getTxFault() {
        return txFault;
    }

    public boolean getTxSquelOmapav() {
        return txSquelOmapav;
    }

    public boolean getTxLos() {
        return txLos;
    }

    public Date getDate() {
        return date;
    }

    public byte getDiagMonType() {
        return diagMonType;
    }

    public byte getOptionsEnh() {
        return optionsEnh;
    }

    public byte[] getVendor() {
        return vendor;
    }

    public byte getOpaCertCable() {
        return opaCertCable;
    }

    public byte getVendor2() {
        return vendor2;
    }

    public byte getOpaCertDataRate() {
        return opaCertDataRate;
    }

    public byte[] getVendor3() {
        return vendor3;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(byte id) {
        this.id = id;
    }

    /**
     * @param extID
     *            the extId to set
     */
    public void setPowerClass(PowerClassType extID) {
        this.powerClass = extID;
    }

    /**
     * @return the txCDRSupported
     */
    public boolean getTxCDRSupported() {
        return txCDRSupported;
    }

    /**
     * @param txCDRSupported
     *            the txCDRSupported to set
     */
    public void setTxCDRSupported(boolean txCDRSupported) {
        this.txCDRSupported = txCDRSupported;
    }

    /**
     * @return the rxCDRSupported
     */
    public boolean getRxCDRSupported() {
        return rxCDRSupported;
    }

    /**
     * @param rxCDRSupported
     *            the rxCDRSupported to set
     */
    public void setRxCDRSupported(boolean rxCDRSupported) {
        this.rxCDRSupported = rxCDRSupported;
    }

    /**
     * @param connector
     *            the connector to set
     */
    public void setConnector(byte connector) {
        this.connector = connector;
    }

    /**
     * @param bitRateLow
     *            the bitRateLow to set
     */
    public void setBitRateLow(byte bitRateLow) {
        this.bitRateLow = bitRateLow;
    }

    /**
     * @param bitRateHigh
     *            the bitRateHigh to set
     */
    public void setBitRateHigh(byte bitRateHigh) {
        this.bitRateHigh = bitRateHigh;
    }

    /**
     * @param om3Length
     *            the om3Length to set
     */
    public void setOm3Length(int om3Length) {
        this.om3Length = om3Length;
    }

    /**
     * @param om2Length
     *            the om2Length to set
     */
    public void setOm2Length(int om2Length) {
        this.om2Length = om2Length;
    }

    public byte getLenOm4() {
        return lenOm4;
    }

    public void setLenOm4(byte lenOm4) {
        this.lenOm4 = lenOm4;
    }

    /**
     * @param copperLength
     *            the copperLength to set
     */
    public void setOm4Length(int om4Length) {
        this.om4Length = om4Length;
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
     * @param maxCaseTemp
     *            the maxCaseTemp to set
     */
    public void setMaxCaseTemp(int maxCaseTemp) {
        this.maxCaseTemp = maxCaseTemp;
    }

    /**
     * @param ccBase
     *            the ccBase to set
     */
    public void setCcBase(byte ccBase) {
        this.ccBase = ccBase;
    }

    /**
     * @param linkCodes
     *            the linkCodes to set
     */
    public void setLinkCodes(byte linkCodes) {
        this.linkCodes = linkCodes;
    }

    /**
     * @param txInpEqAutoAdp
     *            the txInpEqAutoAdp to set
     */
    public void setTxInpEqAutoAdp(boolean txInpEqAutoAdp) {
        this.txInpEqAutoAdp = txInpEqAutoAdp;
    }

    /**
     * @param txInpEqFixProg
     *            the txInpEqFixProg to set
     */
    public void setTxInpEqFixProg(boolean txInpEqFixProg) {
        this.txInpEqFixProg = txInpEqFixProg;
    }

    /**
     * @param rxOutpEmphFixProg
     *            the rxOutpEmphFixProg to set
     */
    public void setRxOutpEmphFixProg(boolean rxOutpEmphFixProg) {
        this.rxOutpEmphFixProg = rxOutpEmphFixProg;
    }

    /**
     * @param rxOutpAmplFixProg
     *            the rxOutpAmplFixProg to set
     */
    public void setRxOutpAmplFixProg(boolean rxOutpAmplFixProg) {
        this.rxOutpAmplFixProg = rxOutpAmplFixProg;
    }

    /**
     * @param txCDROnOffCtrl
     *            the txCDROnOffCtrl to set
     */
    public void setTxCDROnOffCtrl(boolean txCDROnOffCtrl) {
        this.txCDROnOffCtrl = txCDROnOffCtrl;
    }

    /**
     * @param rxCDROnOffCtrl
     *            the rxCDROnOffCtrl to set
     */
    public void setRxCDROnOffCtrl(boolean rxCDROnOffCtrl) {
        this.rxCDROnOffCtrl = rxCDROnOffCtrl;
    }

    public void setTxCdrLol(boolean txCdrLol) {
        this.txCdrLol = txCdrLol;
    }

    public void setRxCdrLol(boolean rxCdrLol) {
        this.rxCdrLol = rxCdrLol;
    }

    /**
     * @param txSquelchImp
     *            the txSquelchImp to set
     */
    public void setTxSquelchImp(boolean txSquelchImp) {
        this.txSquelchImp = txSquelchImp;
    }

    /**
     * @param memPage02Provided
     *            the memPage02Provided to set
     */
    public void setMemPage02Provided(boolean memPage02Provided) {
        this.memPage02Provided = memPage02Provided;
    }

    /**
     * @param memPage01Provided
     *            the memPage01Provided to set
     */
    public void setMemPage01Provided(boolean memPage01Provided) {
        this.memPage01Provided = memPage01Provided;
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
     * @param ccExt
     *            the ccExt to set
     */
    public void setCcExt(byte ccExt) {
        this.ccExt = ccExt;
    }

    /**
     * <i>Description:</i>
     *
     * @param certCableFlag
     */
    public void setCertCableFlag(boolean certCableFlag) {
        this.certCableFlag = certCableFlag;

    }

    /**
     * @param reachClass
     *            the reachClass to set
     */
    public void setReachClass(int reachClass) {
        this.reachClass = reachClass;
    }

    /**
     * @param certDataRate
     *            the certDataRate to set
     */
    public void setCertDataRate(CertifiedRateType certDataRate) {
        this.certDataRate = certDataRate;
    }

    public void setPwrClassLow(byte pwrClassLow) {
        this.pwrClassLow = pwrClassLow;
    }

    public void setExtIdentOther(byte extIdentOther) {
        this.extIdentOther = extIdentOther;
    }

    public void setPwrClassHigh(byte pwrClassHigh) {
        this.pwrClassHigh = pwrClassHigh;
    }

    public void setSpecComp(byte[] specComp) {
        this.specComp = specComp;
    }

    public void setEncode(byte encode) {
        this.encode = encode;
    }

    public void setExtRateComp(byte extRateComp) {
        this.extRateComp = extRateComp;
    }

    public void setLenSmf(byte lenSmf) {
        this.lenSmf = lenSmf;
    }

    public void setOm3Length(Integer om3Length) {
        this.om3Length = om3Length;
    }

    public void setOm2Length(Integer om2Length) {
        this.om2Length = om2Length;
    }

    public void setLenOm1(byte lenOm1) {
        this.lenOm1 = lenOm1;
    }

    public void setOm4Length(Integer om4Length) {
        this.om4Length = om4Length;
    }

    public void setXmitTech(byte xmitTech) {
        this.xmitTech = xmitTech;
    }

    public void setDevTechOther(byte devTechOther) {
        this.devTechOther = devTechOther;
    }

    public void setExtMod(byte extMod) {
        this.extMod = extMod;
    }

    public void setWaveAtten(byte[] waveAtten) {
        this.waveAtten = waveAtten;
    }

    public void setWaveTol(byte[] waveTol) {
        this.waveTol = waveTol;
    }

    public void setMaxCaseTemp(Integer maxCaseTemp) {
        this.maxCaseTemp = maxCaseTemp;
    }

    public void setRxSquelDis(boolean rxSquelDis) {
        this.rxSquelDis = rxSquelDis;
    }

    public void setRxOutDis(boolean rxOutDis) {
        this.rxOutDis = rxOutDis;
    }

    public void setTxSquelDis(boolean txSquelDis) {
        this.txSquelDis = txSquelDis;
    }

    public void setRateSel(boolean rateSel) {
        this.rateSel = rateSel;
    }

    public void setTxDis(boolean txDis) {
        this.txDis = txDis;
    }

    public void setTxFault(boolean txFault) {
        this.txFault = txFault;
    }

    public void setTxSquelOmapav(boolean txSquelOmapav) {
        this.txSquelOmapav = txSquelOmapav;
    }

    public void setTxLos(boolean txLos) {
        this.txLos = txLos;
    }

    public void setDiagMonType(byte diagMonType) {
        this.diagMonType = diagMonType;
    }

    public void setOptionsEnh(byte optionsEnh) {
        this.optionsEnh = optionsEnh;
    }

    public void setVendor(byte[] vendor) {
        this.vendor = vendor;
    }

    public void setOpaCertCable(byte opaCertCable) {
        this.opaCertCable = opaCertCable;
    }

    public void setVendor2(byte vendor2) {
        this.vendor2 = vendor2;
    }

    public void setOpaCertDataRate(byte opaCertDataRate) {
        this.opaCertDataRate = opaCertDataRate;
    }

    public void setVendor3(byte[] vendor3) {
        this.vendor3 = vendor3;
    }

    public void setReachClass(Integer reachClass) {
        this.reachClass = reachClass;
    }

    /**
     *
     * <i>Description:</i>Unit in Gbps.
     *
     * @param codeLow
     * @param codeHigh
     * @return
     */
    public Integer stlCableInfoBitRate(byte codeLow, byte codeHigh) {
        int result;
        if (codeLow == (byte) 0xFF) {
            result = (codeHigh / 4);
        } else {
            result = (codeLow / 10);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CableInfoBean [id=" + id + ", extId=" + powerClass
                + ", txCDRSupported=" + txCDRSupported + ", rxCDRSupported="
                + rxCDRSupported + ", xmitTech=" + xmitTech + ", connector="
                + connector + ", bitRateLow=" + bitRateLow + ", bitRateHigh="
                + bitRateHigh + ", om3Length=" + om3Length + ", om2Length="
                + om2Length + ", om4Length=" + om4Length + ", vendorName="
                + vendorName + ", vendorOui=" + Arrays.toString(vendorOui)
                + ", vendorPn=" + vendorPn + ", vendorRev=" + vendorRev
                + ", maxCaseTemp=" + maxCaseTemp + ", ccBase=" + ccBase
                + ", txInpEqAutoAdp=" + txInpEqAutoAdp + ", txInpEqFixProg="
                + txInpEqFixProg + ", rxOutpEmphFixProg=" + rxOutpEmphFixProg
                + ", rxOutpAmplFixProg=" + rxOutpAmplFixProg
                + ", txCDROnOffCtrl=" + txCDROnOffCtrl + ", rxCDROnOffCtrl="
                + rxCDROnOffCtrl + ", txSquelchImp=" + txSquelchImp
                + ", memPage02Provided=" + memPage02Provided
                + ", memPage01Provided=" + memPage01Provided + ", vendorSN="
                + vendorSN + ", dateCode=" + dateCode + ", date=" + date
                + ", ccExt=" + ccExt + ", certCableFlag=" + certCableFlag
                + ", reachClass=" + reachClass + ", certDataRate="
                + certDataRate + "]";
    }

}
