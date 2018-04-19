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

    private Byte id;

    private PowerClassType powerClass;

    // extIdent;
    private Byte pwrClassLow;

    private Byte extIdentOther;

    private Boolean txCDRSupported;

    private Boolean rxCDRSupported;

    private Byte pwrClassHigh;

    private Byte connector;

    private Byte[] specComp;// 8 Bytes;

    private Byte encode;

    private Byte bitRateLow;

    private Byte extRateComp;

    private Byte lenSmf;

    private Integer om3Length;

    private Integer om2Length;

    private Byte lenOm1;

    private Byte lenOm4;

    private Integer om4Length;

    // devTech;
    private Byte xmitTech;

    private Byte devTechOther;

    private String vendorName;// 16 bytes;

    private Byte extMod;

    private Byte[] vendorOui;// 3 bytes;

    private String vendorPn;// 16 bytes;

    private String vendorRev;// 2 bytes;

    private Byte[] waveAtten;// 2 Bytes;

    private Byte[] waveTol;// 2 Bytes;

    private Integer maxCaseTemp;

    private Byte ccBase;

    private Byte linkCodes;

    // rxtxOptEquemp;
    private Boolean txInpEqAutoAdp;

    private Boolean txInpEqFixProg;

    private Boolean rxOutpEmphFixProg;

    private Boolean rxOutpAmplFixProg;

    // rxtxOptCdrsquel;
    private Boolean txCDROnOffCtrl;

    private Boolean rxCDROnOffCtrl;

    private Boolean txCdrLol;

    private Boolean rxCdrLol;

    private Boolean rxSquelDis;

    private Boolean rxOutDis;

    private Boolean txSquelDis;

    private Boolean txSquelchImp;

    // memtxOptPagesquel;
    private Boolean memPage02Provided;

    private Boolean memPage01Provided;

    private Boolean rateSel;

    private Boolean txDis;

    private Boolean txFault;

    private Boolean txSquelOmapav;

    private Boolean txLos;

    private String vendorSN;// 16 bytes;

    private String dateCode;// 8 bytes;

    private Date date;

    private Byte diagMonType;

    private Byte optionsEnh;

    private Byte bitRateHigh;

    private Byte ccExt;

    private Byte[] vendor;// 26 Bytes;

    private Byte opaCertCable;

    private Boolean certCableFlag;

    private Byte vendor2;

    private Byte opaCertDataRate;

    private Byte[] vendor3;// 3 Bytes;

    private Integer reachClass;

    private CertifiedRateType certDataRate;

    public CableInfoBean() {
        super();
    }

    /**
     * @return the id
     */
    public Byte getId() {
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
    public Byte getConnector() {
        return connector;
    }

    /**
     * @return the bitRateLow
     */
    public Byte getBitRateLow() {
        return bitRateLow;
    }

    /**
     * @return the bitRateHigh
     */
    public Byte getBitRateHigh() {
        return bitRateHigh;
    }

    /**
     * @return the om3Length
     */
    public Integer getOm3Length() {
        return om3Length;
    }

    /**
     * @return the om2Length
     */
    public Integer getOm2Length() {
        return om2Length;
    }

    /**
     * @return the copperLength
     */
    public Integer getOm4Length() {
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
    public Byte[] getVendorOui() {
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
    public Integer getMaxCaseTemp() {
        return maxCaseTemp;
    }

    /**
     * @return the ccBase
     */
    public Byte getCcBase() {
        return ccBase;
    }

    /**
     * @return the linkCodes
     */
    public Byte getLinkCodes() {
        return linkCodes;
    }

    /**
     * @return the txInpEqAutoAdp
     */
    public Boolean getTxInpEqAutoAdp() {
        return txInpEqAutoAdp;
    }

    /**
     * @return the txInpEqFixProg
     */
    public Boolean getTxInpEqFixProg() {
        return txInpEqFixProg;
    }

    /**
     * @return the rxOutpEmphFixProg
     */
    public Boolean getRxOutpEmphFixProg() {
        return rxOutpEmphFixProg;
    }

    /**
     * @return the rxOutpAmplFixProg
     */
    public Boolean getRxOutpAmplFixProg() {
        return rxOutpAmplFixProg;
    }

    /**
     * @return the txCDROnOffCtrl
     */
    public Boolean getTxCDROnOffCtrl() {
        return txCDROnOffCtrl;
    }

    /**
     * @return the rxCDROnOffCtrl
     */
    public Boolean getRxCDROnOffCtrl() {
        return rxCDROnOffCtrl;
    }

    public Boolean getTxCdrLol() {
        return txCdrLol;
    }

    public Boolean getRxCdrLol() {
        return rxCdrLol;
    }

    /**
     * @return the txSquelchImp
     */
    public Boolean isTxSquelchImp() {
        return txSquelchImp;
    }

    /**
     * @return the memPage02Provided
     */
    public Boolean isMemPage02Provided() {
        return memPage02Provided;
    }

    /**
     * @return the memPage01Provided
     */
    public Boolean isMemPage01Provided() {
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
    public Byte getCcExt() {
        return ccExt;
    }

    /**
     * @return the certCableFlag
     */
    public Boolean getCertCableFlag() {
        return certCableFlag;
    }

    /**
     * @return the reachClass
     */
    public Integer getReachClass() {
        return reachClass;
    }

    /**
     * @return the certDataRate
     */
    public CertifiedRateType getCertDataRate() {
        return certDataRate;
    }

    public Byte getPwrClassLow() {
        return pwrClassLow;
    }

    public Byte getExtIdentOther() {
        return extIdentOther;
    }

    public Byte getPwrClassHigh() {
        return pwrClassHigh;
    }

    public Byte[] getSpecComp() {
        return specComp;
    }

    public Byte getEncode() {
        return encode;
    }

    public Byte getExtRateComp() {
        return extRateComp;
    }

    public Byte getLenSmf() {
        return lenSmf;
    }

    public Byte getLenOm1() {
        return lenOm1;
    }

    public Byte getXmitTech() {
        return xmitTech;
    }

    public Byte getDevTechOther() {
        return devTechOther;
    }

    public Byte getExtMod() {
        return extMod;
    }

    public Byte[] getWaveAtten() {
        return waveAtten;
    }

    public Byte[] getWaveTol() {
        return waveTol;
    }

    public Boolean getRxSquelDis() {
        return rxSquelDis;
    }

    public Boolean getRxOutDis() {
        return rxOutDis;
    }

    public Boolean getTxSquelDis() {
        return txSquelDis;
    }

    public Boolean getMemPage02Provided() {
        return memPage02Provided;
    }

    public Boolean getMemPage01Provided() {
        return memPage01Provided;
    }

    public Boolean getRateSel() {
        return rateSel;
    }

    public Boolean getTxDis() {
        return txDis;
    }

    public Boolean getTxFault() {
        return txFault;
    }

    public Boolean getTxSquelOmapav() {
        return txSquelOmapav;
    }

    public Boolean getTxLos() {
        return txLos;
    }

    public Date getDate() {
        return date;
    }

    public Byte getDiagMonType() {
        return diagMonType;
    }

    public Byte getOptionsEnh() {
        return optionsEnh;
    }

    public Byte[] getVendor() {
        return vendor;
    }

    public Byte getOpaCertCable() {
        return opaCertCable;
    }

    public Byte getVendor2() {
        return vendor2;
    }

    public Byte getOpaCertDataRate() {
        return opaCertDataRate;
    }

    public Byte[] getVendor3() {
        return vendor3;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Byte id) {
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
    public Boolean getTxCDRSupported() {
        return txCDRSupported;
    }

    /**
     * @param txCDRSupported
     *            the txCDRSupported to set
     */
    public void setTxCDRSupported(Boolean txCDRSupported) {
        this.txCDRSupported = txCDRSupported;
    }

    /**
     * @return the rxCDRSupported
     */
    public Boolean getRxCDRSupported() {
        return rxCDRSupported;
    }

    /**
     * @param rxCDRSupported
     *            the rxCDRSupported to set
     */
    public void setRxCDRSupported(Boolean rxCDRSupported) {
        this.rxCDRSupported = rxCDRSupported;
    }

    /**
     * @param connector
     *            the connector to set
     */
    public void setConnector(Byte connector) {
        this.connector = connector;
    }

    /**
     * @param bitRateLow
     *            the bitRateLow to set
     */
    public void setBitRateLow(Byte bitRateLow) {
        this.bitRateLow = bitRateLow;
    }

    /**
     * @param bitRateHigh
     *            the bitRateHigh to set
     */
    public void setBitRateHigh(Byte bitRateHigh) {
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

    public Byte getLenOm4() {
        return lenOm4;
    }

    public void setLenOm4(Byte lenOm4) {
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
    public void setVendorOui(Byte[] vendorOui) {
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
    public void setCcBase(Byte ccBase) {
        this.ccBase = ccBase;
    }

    /**
     * @param linkCodes
     *            the linkCodes to set
     */
    public void setLinkCodes(Byte linkCodes) {
        this.linkCodes = linkCodes;
    }

    /**
     * @param txInpEqAutoAdp
     *            the txInpEqAutoAdp to set
     */
    public void setTxInpEqAutoAdp(Boolean txInpEqAutoAdp) {
        this.txInpEqAutoAdp = txInpEqAutoAdp;
    }

    /**
     * @param txInpEqFixProg
     *            the txInpEqFixProg to set
     */
    public void setTxInpEqFixProg(Boolean txInpEqFixProg) {
        this.txInpEqFixProg = txInpEqFixProg;
    }

    /**
     * @param rxOutpEmphFixProg
     *            the rxOutpEmphFixProg to set
     */
    public void setRxOutpEmphFixProg(Boolean rxOutpEmphFixProg) {
        this.rxOutpEmphFixProg = rxOutpEmphFixProg;
    }

    /**
     * @param rxOutpAmplFixProg
     *            the rxOutpAmplFixProg to set
     */
    public void setRxOutpAmplFixProg(Boolean rxOutpAmplFixProg) {
        this.rxOutpAmplFixProg = rxOutpAmplFixProg;
    }

    /**
     * @param txCDROnOffCtrl
     *            the txCDROnOffCtrl to set
     */
    public void setTxCDROnOffCtrl(Boolean txCDROnOffCtrl) {
        this.txCDROnOffCtrl = txCDROnOffCtrl;
    }

    /**
     * @param rxCDROnOffCtrl
     *            the rxCDROnOffCtrl to set
     */
    public void setRxCDROnOffCtrl(Boolean rxCDROnOffCtrl) {
        this.rxCDROnOffCtrl = rxCDROnOffCtrl;
    }

    public void setTxCdrLol(Boolean txCdrLol) {
        this.txCdrLol = txCdrLol;
    }

    public void setRxCdrLol(Boolean rxCdrLol) {
        this.rxCdrLol = rxCdrLol;
    }

    /**
     * @param txSquelchImp
     *            the txSquelchImp to set
     */
    public void setTxSquelchImp(Boolean txSquelchImp) {
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

        SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd-hh");
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
    public void setCcExt(Byte ccExt) {
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

    public void setPwrClassLow(Byte pwrClassLow) {
        this.pwrClassLow = pwrClassLow;
    }

    public void setExtIdentOther(Byte extIdentOther) {
        this.extIdentOther = extIdentOther;
    }

    public void setPwrClassHigh(Byte pwrClassHigh) {
        this.pwrClassHigh = pwrClassHigh;
    }

    public void setSpecComp(Byte[] specComp) {
        this.specComp = specComp;
    }

    public void setEncode(Byte encode) {
        this.encode = encode;
    }

    public void setExtRateComp(Byte extRateComp) {
        this.extRateComp = extRateComp;
    }

    public void setLenSmf(Byte lenSmf) {
        this.lenSmf = lenSmf;
    }

    public void setOm3Length(Integer om3Length) {
        this.om3Length = om3Length;
    }

    public void setOm2Length(Integer om2Length) {
        this.om2Length = om2Length;
    }

    public void setLenOm1(Byte lenOm1) {
        this.lenOm1 = lenOm1;
    }

    public void setOm4Length(Integer om4Length) {
        this.om4Length = om4Length;
    }

    public void setXmitTech(Byte xmitTech) {
        this.xmitTech = xmitTech;
    }

    public void setDevTechOther(Byte devTechOther) {
        this.devTechOther = devTechOther;
    }

    public void setExtMod(Byte extMod) {
        this.extMod = extMod;
    }

    public void setWaveAtten(Byte[] waveAtten) {
        this.waveAtten = waveAtten;
    }

    public void setWaveTol(Byte[] waveTol) {
        this.waveTol = waveTol;
    }

    public void setMaxCaseTemp(Integer maxCaseTemp) {
        this.maxCaseTemp = maxCaseTemp;
    }

    public void setRxSquelDis(Boolean rxSquelDis) {
        this.rxSquelDis = rxSquelDis;
    }

    public void setRxOutDis(Boolean rxOutDis) {
        this.rxOutDis = rxOutDis;
    }

    public void setTxSquelDis(Boolean txSquelDis) {
        this.txSquelDis = txSquelDis;
    }

    public void setMemPage02Provided(Boolean memPage02Provided) {
        this.memPage02Provided = memPage02Provided;
    }

    public void setMemPage01Provided(Boolean memPage01Provided) {
        this.memPage01Provided = memPage01Provided;
    }

    public void setRateSel(Boolean rateSel) {
        this.rateSel = rateSel;
    }

    public void setTxDis(Boolean txDis) {
        this.txDis = txDis;
    }

    public void setTxFault(Boolean txFault) {
        this.txFault = txFault;
    }

    public void setTxSquelOmapav(Boolean txSquelOmapav) {
        this.txSquelOmapav = txSquelOmapav;
    }

    public void setTxLos(Boolean txLos) {
        this.txLos = txLos;
    }

    public void setDiagMonType(Byte diagMonType) {
        this.diagMonType = diagMonType;
    }

    public void setOptionsEnh(Byte optionsEnh) {
        this.optionsEnh = optionsEnh;
    }

    public void setVendor(Byte[] vendor) {
        this.vendor = vendor;
    }

    public void setOpaCertCable(Byte opaCertCable) {
        this.opaCertCable = opaCertCable;
    }

    public void setCertCableFlag(Boolean certCableFlag) {
        this.certCableFlag = certCableFlag;
    }

    public void setVendor2(Byte vendor2) {
        this.vendor2 = vendor2;
    }

    public void setOpaCertDataRate(Byte opaCertDataRate) {
        this.opaCertDataRate = opaCertDataRate;
    }

    public void setVendor3(Byte[] vendor3) {
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
    public Integer stlCableInfoBitRate(Byte codeLow, Byte codeHigh) {
        int result;
        if (codeLow == (byte) 0xFF) {
            result = (codeHigh / 4);
        } else {
            result = (codeLow / 10);
        }
        return result;
    }

    public CableType stlCableInfoCableType(int codeXmit, Byte codeConnector) {
        CableType cableType = null;
        int connectId = 0;
        if ((codeXmit <= 9) && (codeXmit != 8)) {
            if (codeConnector == SAConstants.CABLEINFO_CONNECTOR_NOSEP) {
                connectId = 1;
            } else {
                connectId = 2;
            }
        }

        cableType = CableType.getCableType(connectId, codeXmit);

        return cableType;
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
