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

package com.intel.stl.fecdriver.messages.adapter.sa;

import com.intel.stl.api.subnet.CableInfoBean;
import com.intel.stl.api.subnet.CertifiedRateType;
import com.intel.stl.api.subnet.PowerClassType;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.common.StringUtils;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm.h.1.157
 *
 * <pre>
 *
 * STL_CABLE_INFO_STD
 *
 *
 *typedef struct {
 *   // Page 0 upper, bytes 128-255
 *   uint8   ident;                  // 128: Identifier
 *   union {
 *       uint8   AsReg8;
 *       struct { IB_BITFIELD5( uint8,   // 129: Extended identifier:
 *           pwr_class_low:  2,          //      Power class low
 *           other:          2,          //      Other settings
 *           tx_cdr_supp:    1,          //      Tx CDR support
 *           rx_cdr_supp:    1,          //      Rx CDR support
 *           pwr_class_high: 2)          //      Power class low
 *       } s;
 *   } ext_ident;
 *   uint8   connector;              // 130: Connector (see STL_CIB_CONNECTOR_TYPE_xxx)
 *   uint8   spec_comp[8];           // 131-138: Elec/optical compliance code
 *   uint8   encode;                 // 139: Encoding algorithm
 *   uint8   bit_rate_low;           // 140: Nominal bit rate low (units 100 Mbps)
 *                                   //      (0xFF see bit_rate_high)
 *   uint8   ext_rate_comp;          // 141: Extended rate compliance code
 *   uint8   len_smf;                // 142: Link len SMF fiber (units km)
 *   uint8   len_om3;                // 143: Link len OM3 fiber (units 2m)
 *   uint8   len_om2;                // 144: Link len OM2 fiber (units 1m)
 *   uint8   len_om1;                // 145: Link len OM1 fiber (units 1m)
 *   uint8   len_om4;                // 146: Link len OM4 copper or fiber (units 1m or 2m)
 *   union {
 *       uint8   AsReg8;
 *       struct { IB_BITFIELD2( uint8,   // 147: Device technology:
 *           xmit_tech:  4,              //      Transmitter technology
 *           other:      4)              //      Other settings
 *       } s;
 *   } dev_tech;
 *   uint8   vendor_name[16];        // 148-163: Vendor name
 *   uint8   ext_mod;                // 164: Extended module code
 *   uint8   vendor_oui[3];          // 165-167: Vendor OUI
 *   uint8   vendor_pn[16];          // 168-183: Vendor part number
 *   uint8   vendor_rev[2];          // 184-185: Vendor revision
 *   uint8   wave_atten[2];          // 186-187: Wave length (value/20 nm) or
 *                                   //          copper attenuation (units dB)
 *   uint8   wave_tol[2];            // 188-189: Wave length tolerance (value/200 nm)
 *   uint8   max_case_temp;          // 190: Max case temperature (degrees C)
 *   uint8   cc_base;                // 191: Checksum addresses 128-190
 *   uint8   link_codes;             // 192: Link codes
 *   union {
 *       uint8   AsReg8;
 *       struct { IB_BITFIELD5( uint8,   // 193: RxTx options: equalization & emphasis
 *           reserved:       4,          //      Reserved
 *           tx_inpeq_autadp_cap:    1,  //      Tx inp equal auto-adaptive capable
 *           tx_inpeq_fixpro_cap:    1,  //      Tx inp equal fixed-prog capable
 *           rx_outemp_fixpro_cap:   1,  //      Rx outp emphasis fixed-prog capable
 *           rx_outamp_fixpro_cap:   1)  //      Rx outp amplitude fixed-prog capable
 *       } s;
 *   } rxtx_opt_equemp;
 *   union {
 *       uint8   AsReg8;
 *       struct { IB_BITFIELD8( uint8,   // 194: RxTx options: CDR, LOL, squelch
 *           tx_cdr_ctrl:    1,          //      Tx CDR On/Off ctrl implemented
 *           rx_cdr_ctrl:    1,          //      Rx CDR On/Off ctrl implemented
 *           tx_cdr_lol:     1,          //      Tx CDR loss of lock flag implemented
 *           rx_cdr_lol:     1,          //      Rx CDR loss of lock flag implemented
 *           rx_squel_dis:   1,          //      Rx squelch disable implemented
 *           rx_out_dis:     1,          //      Rx output disable implemented
 *           tx_squel_dis:   1,          //      Tx squelch disable implemented
 *           tx_squel:       1)          //      Tx squelch implemented
 *       } s;
 *   } rxtx_opt_cdrsquel;
 *   union {
 *       uint8   AsReg8;
 *       struct { IB_BITFIELD8( uint8,   // 195: MemTx options: pages 1 & 2, implementations
 *           page_2:             1,      //      Mem page 2 implemented
 *           page_1:             1,      //      Mem page 1 implemented
 *           rate_sel:           1,      //      Rate select implemented
 *           tx_dis:             1,      //      Tx disable implemented
 *           tx_fault:           1,      //      Tx fault signal implemented
 *           tx_squel_omapav:    1,      //      Tx squelch OMA/Pave
 *           tx_los:             1,      //      Tx loss of signal implemented
 *           reserved:           1)      //      Reserved
 *       } s;
 *   } memtx_opt_pagesquel;
 *   uint8   vendor_sn[16];          // 196-211: Vendor serial number
 *   uint8   date_code[8];           // 212-219: Vendor manufacture date code
 *   uint8   diag_mon_type;          // 220: Diagnostic monitoring type
 *   uint8   options_enh;            // 221: Enhanced options
 *   uint8   bit_rate_high;          // 222: Nominal bit rate high (units 250 Mbps)
 *                                   //      (see also bit_rate_low)
 *   uint8   cc_ext;                 // 223: Checksum addresses 192-222
 *   uint8   vendor[26];             // 224-249: Vendor specific
 *   uint8   opa_cert_cable;         // 250: OPA certified cable (see STL_CIB_CERTIFIED_CABLE)
 *   uint8   vendor2;                // 251: Vendor specific
 *   uint8   opa_cert_data_rate;     // 252: OPA certified data rate
 *   uint8   vendor3[3];             // 253-255: Vendor specific
 * } PACK_SUFFIX STL_CABLE_INFO_STD;
 * </pre>
 *
 *
 * A class to define the structure STL_CABLE_INFO_STD from
 * IbAccess/Common/Inc/stl_sm.h
 */
public class CableInfoStd extends SimpleDatagram<CableInfoBean> {

    public CableInfoStd() {
        super(128);
    }

    public CableInfoBean toLowerObject() {
        CableInfoBean bean = new CableInfoBean();

        bean.setId(buffer.get());
        // extIdent;
        byte byteVal = buffer.get();
        bean.setPwrClassLow((byte) ((byteVal >>> 6) & 0x3));
        bean.setExtIdentOther((byte) ((byteVal >>> 4) & 0x3));
        bean.setTxCDRSupported((byteVal & 0x8) == 0x8);
        bean.setRxCDRSupported((byteVal & 0x4) == 0x4);
        bean.setPwrClassHigh((byte) (byteVal & 0x3));

        bean.setPowerClass(stlCableInfoPowerClassType(bean.getPwrClassLow(),
                bean.getPwrClassHigh()));

        bean.setConnector(buffer.get());
        byte[] byteVals = new byte[8];
        buffer.get(byteVals);
        bean.setSpecComp(convertToByteArray(byteVals));
        bean.setEncode(buffer.get());
        bean.setBitRateLow(buffer.get());
        bean.setExtRateComp(buffer.get());
        bean.setLenSmf(buffer.get());
        bean.setOm3Length((buffer.get() & 0xFF) * 2);
        bean.setOm2Length(buffer.get() & 0xFF);
        bean.setLenOm1(buffer.get());
        bean.setLenOm4(buffer.get());

        // devTech
        byteVal = buffer.get();
        bean.setXmitTech((byte) ((byteVal >>> 4) & 0x0f));
        bean.setDevTechOther((byte) (byteVal & 0x0f));

        boolean isValid =
                isCableLengthValid(bean.getXmitTech(), bean.getConnector());
        bean.setOm4Length(getOM4Length(bean.getLenOm4(), isValid));

        byteVals = new byte[16];
        buffer.get(byteVals);
        bean.setVendorName(StringUtils.toString(byteVals, 0, 16));
        bean.setExtMod(buffer.get());

        byteVals = new byte[3];
        buffer.get(byteVals);
        bean.setVendorOui(convertToByteArray(byteVals));
        byteVals = new byte[16];
        buffer.get(byteVals);
        bean.setVendorPn(StringUtils.toString(byteVals, 0, 16));
        byteVals = new byte[2];
        buffer.get(byteVals);
        bean.setVendorRev(StringUtils.toString(byteVals, 0, 2));
        byteVals = new byte[2];
        buffer.get(byteVals);
        bean.setWaveAtten(convertToByteArray(byteVals));
        byteVals = new byte[2];
        buffer.get(byteVals);
        bean.setWaveTol(convertToByteArray(byteVals));

        bean.setMaxCaseTemp(buffer.get() & 0xFF);
        bean.setCcBase(buffer.get());

        return bean;
    }

    public CableInfoBean toUpperObject() {
        buffer.position(64);
        CableInfoBean bean = new CableInfoBean();
        bean.setLinkCodes(buffer.get());

        // rxtxOptEquemp
        byte byteVal = buffer.get();
        bean.setTxInpEqAutoAdp((byteVal & 0x8) == 0x8);
        bean.setTxInpEqFixProg((byteVal & 0x4) == 0x4);
        bean.setRxOutpEmphFixProg((byteVal & 0x2) == 0x2);
        bean.setRxOutpAmplFixProg((byteVal & 0x1) == 0x1);

        // rxtxOptCdrsquel
        byteVal = buffer.get();
        bean.setTxCDROnOffCtrl((byteVal & 0x80) == 0x80);
        bean.setRxCDROnOffCtrl((byteVal & 0x40) == 0x40);
        bean.setTxCdrLol((byteVal & 0x20) == 0x20);
        bean.setRxCdrLol((byteVal & 0x10) == 0x10);
        bean.setRxSquelDis((byteVal & 0x8) == 0x8);
        bean.setRxOutDis((byteVal & 0x4) == 0x4);
        bean.setTxSquelDis((byteVal & 0x2) == 0x2);
        bean.setTxSquelchImp((byteVal & 0x1) == 0x1);
        // memtxOptPagesquel
        byteVal = buffer.get();
        bean.setMemPage02Provided((byteVal & 0x80) == 0x80);
        bean.setMemPage01Provided((byteVal & 0x40) == 0x40);
        bean.setRateSel((byteVal & 0x20) == 0x20);
        bean.setTxDis((byteVal & 0x10) == 0x10);
        bean.setTxFault((byteVal & 0x8) == 0x8);
        bean.setTxSquelOmapav((byteVal & 0x4) == 0x4);
        bean.setTxLos((byteVal & 0x2) == 0x2);

        byte[] byteVals = new byte[16];
        buffer.get(byteVals);
        bean.setVendorSN(StringUtils.toString(byteVals, 0, 16));
        byteVals = new byte[8];
        buffer.get(byteVals);
        String year = StringUtils.toString(byteVals, 0, 2);
        String month = StringUtils.toString(byteVals, 2, 2);
        String day = StringUtils.toString(byteVals, 4, 2);
        String lot = StringUtils.toString(byteVals, 6, 2);
        bean.setDateCode(year, month, day, lot);

        bean.setDiagMonType(buffer.get());
        bean.setOptionsEnh(buffer.get());
        bean.setBitRateHigh(buffer.get());
        bean.setCcExt(buffer.get());

        byteVals = new byte[26];
        buffer.get(byteVals);
        bean.setVendor(convertToByteArray(byteVals));
        bean.setOpaCertCable(buffer.get());
        bean.setVendor2(buffer.get());
        bean.setOpaCertDataRate(buffer.get());
        byteVals = new byte[3];
        buffer.get(byteVals);
        bean.setVendor3(convertToByteArray(byteVals));

        bean.setCertCableFlag(
                isStlCableInfoCableCertified(bean.getOpaCertCable()));
        bean.setReachClass(bean.getVendor2());
        bean.setCertDataRate(CertifiedRateType
                .getCertifiedRateType(bean.getOpaCertDataRate()));

        return bean;
    }

    private Byte[] convertToByteArray(byte[] bytes) {
        Byte[] byteConv = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            byteConv[i] = Byte.valueOf(bytes[i]);
        }
        return byteConv;
    }

    private boolean isStlCableInfoCableCertified(byte code_cert) {
        if (code_cert == SAConstants.CABLEINFO_OPA_CERTIFIED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCableLengthValid(byte codeXmit, byte connector) {
        if ((codeXmit == 0x08)
                || ((codeXmit <= 0x09)
                        && (connector != SAConstants.CABLEINFO_CONNECTOR_NOSEP))
                || (codeXmit > 0x0F)) {
            return false;
        } else {
            return true;
        }
    }

    private int getOM4Length(byte codeLen, boolean codeValid) {
        if (codeValid) {
            return codeLen & 0xFF;
        } else {
            return (codeLen & 0xFF) * 2;
        }

    }

    private PowerClassType stlCableInfoPowerClassType(int codeLow,
            int codeHigh) {
        PowerClassType type = null;
        type = PowerClassType.getPowerClassType(codeHigh, codeLow);

        return type;
    }
}
