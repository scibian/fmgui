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

package com.intel.stl.ui.configuration;

import static com.intel.stl.ui.common.STLConstants.K0385_TRUE;
import static com.intel.stl.ui.common.STLConstants.K0386_FALSE;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_CC_BASE;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_CC_EXT;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_CERT_CABLE_FLAG;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_CERT_DATA_RATE;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_CONNECTOR;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_COPPER_LEN;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_DATE_CODE;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_DEVICE_TECH;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_ID;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_LENGTH;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_MAXCASE_TEMP;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_MEM_PAGE01_PROV;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_MEM_PAGE02_PROV;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_NOMINAL_BR;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_OM2_LEN;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_OM3_LEN;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_POWER_CLASS;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_REACH_CLASS;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_RX_CDR_ON_OFF_CTRL;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_RX_CDR_SUP;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_RX_OUTP_AMPL_FIX_PROG;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_RX_OUTP_EMPH_FIX_PROG;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_TX_CDR_ON_OFF_CTRL;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_TX_CDR_SUP;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_TX_INP_EQ_AUTO_ADP;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_TX_INP_EQ_FIX_PROG;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_TX_SQUELCH_IMP;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_VENDOR_NAME;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_VENDOR_OUI;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_VENDOR_PN;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_VENDOR_REV;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_VENDOR_SN;

import com.intel.stl.api.subnet.CableInfoBean;
import com.intel.stl.api.subnet.CableRecordBean;
import com.intel.stl.api.subnet.CableType;
import com.intel.stl.api.subnet.CertifiedRateType;
import com.intel.stl.api.subnet.DDCableInfoBean;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.PowerClassType;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.model.CableTypeViz;
import com.intel.stl.ui.model.CertifiedRateTypeViz;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.model.PortTypeViz;
import com.intel.stl.ui.model.PowerClassTypeViz;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

/**
 * Reference: /All_EMB/IbPrint/stl_sma.c.1.103 for the QSFP interpretation.
 */
public class CableInfoProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        FVResourceNode node = context.getResourceNode();
        PortRecordBean portBean = context.getPort();
        ISubnetApi subnetApi = context.getContext().getSubnetApi();
        // For node type Switch and port 0, don't process and display
        // N/A screen.
        if (portBean == null || portBean.getPortNum() == 0
                && node.getParent().getType() == TreeNodeType.SWITCH) {
            getCableInfo(category, null);
            return;
        }

        CableRecordBean cableRecordBean = subnetApi
                .getCable(portBean.getEndPortLID(), portBean.getPortNum());

        if ((cableRecordBean == null)
                || (cableRecordBean.getPortType() != PortTypeViz.STANDARD
                        .getPortType().getValue())) {
            getCableInfo(category, null);
            return;
        }

        if (cableRecordBean.getDdCableInfo() != null) {
            getDDCableInfo(category, cableRecordBean.getDdCableInfo());
        } else {
            getCableInfo(category, cableRecordBean.getCableInfo());
        }

    }

    public void getCableInfo(DevicePropertyCategory category,
            CableInfoBean bean) {

        String na = STLConstants.K0039_NOT_AVAILABLE.getValue();
        if (bean != null) {
            byte id = bean.getId();
            addProperty(category, CABLE_ID, hex(id));

            PowerClassType powerClass = bean.getPowerClass();
            if (powerClass != null) {
                try {
                    addProperty(category, CABLE_POWER_CLASS, PowerClassTypeViz
                            .getPowerClassTypeVizFor(powerClass).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                addProperty(category, CABLE_POWER_CLASS, na);
            }

            boolean txCDRSupported = bean.getTxCDRSupported();
            addProperty(category, CABLE_TX_CDR_SUP, txCDRSupported
                    ? K0385_TRUE.getValue() : K0386_FALSE.getValue());

            boolean rxCDRSupported = bean.getTxCDRSupported();
            addProperty(category, CABLE_RX_CDR_SUP, rxCDRSupported
                    ? K0385_TRUE.getValue() : K0386_FALSE.getValue());
            byte connector = bean.getConnector();
            addProperty(category, CABLE_CONNECTOR, hex(connector));

            byte bitRateLow = bean.getBitRateLow();
            byte bitRateHigh = bean.getBitRateHigh();
            Integer nominalBr =
                    bean.stlCableInfoBitRate(bitRateLow, bitRateHigh);
            if (nominalBr != null) {
                addProperty(category, CABLE_NOMINAL_BR, nominalBr.toString()
                        + " " + STLConstants.K1152_CABLE_GB.getValue());
            } else {
                addProperty(category, CABLE_NOMINAL_BR, na);
            }
            int om2Length = bean.getOm2Length();
            addProperty(category, CABLE_OM2_LEN, Integer.toString(om2Length)
                    + " " + STLConstants.K1154_CABLE_M.getValue());

            int om3Length = bean.getOm3Length();
            addProperty(category, CABLE_OM3_LEN, Integer.toString(om3Length)
                    + " " + STLConstants.K1154_CABLE_M.getValue());

            int om4Length = bean.getOm4Length();
            addProperty(category, CABLE_COPPER_LEN, Integer.toString(om4Length)
                    + " " + STLConstants.K1154_CABLE_M.getValue());

            byte xmitTech = bean.getXmitTech();
            byte codeConnector = bean.getConnector();
            try {
                {
                    CableTypeViz cableType = CableTypeViz.getCableTypeVizFor(
                            CableType.getCableType(xmitTech, codeConnector));
                    if (cableType != null) {
                        addProperty(category, CABLE_DEVICE_TECH,
                                cableType.getName());
                    } else {
                        addProperty(category, CABLE_DEVICE_TECH, na);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                addProperty(category, CABLE_DEVICE_TECH, na);
            }

            String vendorName = bean.getVendorName();
            if (vendorName != null) {
                addProperty(category, CABLE_VENDOR_NAME, vendorName);
            } else {
                addProperty(category, CABLE_VENDOR_NAME, na);
            }
            byte[] vendorOui = bean.getVendorOui();
            if (vendorOui != null) {
                StringBuilder sb = new StringBuilder(vendorOui.length * 2);
                sb.append("0x");
                for (byte b : vendorOui) {
                    sb.append(String.format("%02x", b));
                }
                addProperty(category, CABLE_VENDOR_OUI, sb.toString());
            } else {
                addProperty(category, CABLE_VENDOR_OUI, na);
            }

            String vendorPN = bean.getVendorPn();
            if (vendorPN != null) {
                addProperty(category, CABLE_VENDOR_PN, vendorPN);
            } else {
                addProperty(category, CABLE_VENDOR_PN, na);
            }
            String vendorRev = bean.getVendorRev();
            if (vendorRev != null) {
                addProperty(category, CABLE_VENDOR_REV, vendorRev);
            } else {
                addProperty(category, CABLE_VENDOR_REV, na);
            }
            int maxCaseTemp = bean.getMaxCaseTemp();
            if (maxCaseTemp > 0) {
                addProperty(category, CABLE_MAXCASE_TEMP,
                        Integer.toString(maxCaseTemp) + " "
                                + STLConstants.K1158_CABLE_C.getValue());
            } else {
                addProperty(category, CABLE_MAXCASE_TEMP,
                        STLConstants.K1159_NOT_INDICATED.getValue());
            }
            byte ccBase = bean.getCcBase();
            addProperty(category, CABLE_CC_BASE, hex(ccBase));

            boolean txInpEqAutoAdp = bean.getTxInpEqAutoAdp();
            if (txInpEqAutoAdp) {
                addProperty(category, CABLE_TX_INP_EQ_AUTO_ADP,
                        K0385_TRUE.getValue());
            } else {
                addProperty(category, CABLE_TX_INP_EQ_AUTO_ADP,
                        K0386_FALSE.getValue());
            }

            boolean txInpEqFixProg = bean.getTxInpEqFixProg();
            if (txInpEqFixProg) {
                addProperty(category, CABLE_TX_INP_EQ_FIX_PROG,
                        K0385_TRUE.getValue());
            } else {
                addProperty(category, CABLE_TX_INP_EQ_FIX_PROG,
                        K0386_FALSE.getValue());
            }

            boolean rxOutpEmphFixProg = bean.getRxOutpEmphFixProg();
            if (rxOutpEmphFixProg) {
                addProperty(category, CABLE_RX_OUTP_EMPH_FIX_PROG,
                        K0385_TRUE.getValue());
            } else {
                addProperty(category, CABLE_RX_OUTP_EMPH_FIX_PROG,
                        K0386_FALSE.getValue());
            }

            boolean rxOutpAmplFixProg = bean.getRxOutpAmplFixProg();
            if (rxOutpAmplFixProg) {
                addProperty(category, CABLE_RX_OUTP_AMPL_FIX_PROG,
                        K0385_TRUE.getValue());
            } else {
                addProperty(category, CABLE_RX_OUTP_AMPL_FIX_PROG,
                        K0386_FALSE.getValue());
            }

            boolean txCDROnOffCtrl = bean.getTxCDROnOffCtrl();
            if (txCDROnOffCtrl) {
                addProperty(category, CABLE_TX_CDR_ON_OFF_CTRL,
                        K0385_TRUE.getValue());
            } else {
                addProperty(category, CABLE_TX_CDR_ON_OFF_CTRL,
                        K0386_FALSE.getValue());
            }

            boolean rxCDROnOffCtrl = bean.getRxCDROnOffCtrl();
            if (rxCDROnOffCtrl) {
                addProperty(category, CABLE_RX_CDR_ON_OFF_CTRL,
                        K0385_TRUE.getValue());
            } else {
                addProperty(category, CABLE_RX_CDR_ON_OFF_CTRL,
                        K0386_FALSE.getValue());
            }

            boolean txSquelchImp = bean.isTxSquelchImp();
            if (txSquelchImp) {
                addProperty(category, CABLE_TX_SQUELCH_IMP,
                        K0385_TRUE.getValue());
            } else {
                addProperty(category, CABLE_TX_SQUELCH_IMP,
                        K0386_FALSE.getValue());
            }

            boolean memPage02Provided = bean.isMemPage02Provided();
            if (memPage02Provided) {
                addProperty(category, CABLE_MEM_PAGE02_PROV,
                        K0385_TRUE.getValue());
            } else {
                addProperty(category, CABLE_MEM_PAGE02_PROV,
                        K0386_FALSE.getValue());
            }

            boolean memPage01Provided = bean.isMemPage01Provided();
            if (memPage01Provided) {
                addProperty(category, CABLE_MEM_PAGE01_PROV,
                        K0385_TRUE.getValue());
            } else {
                addProperty(category, CABLE_MEM_PAGE01_PROV,
                        K0386_FALSE.getValue());
            }
            String vendorSn = bean.getVendorSN();
            if (vendorSn != null) {
                addProperty(category, CABLE_VENDOR_SN, vendorSn);
            } else {
                addProperty(category, CABLE_VENDOR_SN, na);
            }
            String dateCode = bean.getDateCode();
            if (dateCode != null) {
                addProperty(category, CABLE_DATE_CODE, "20" + dateCode);
            } else {
                String beanDateCode = bean.getDateCode();
                if (beanDateCode != null) {
                    dateCode = STLConstants.K1116_INVALID.getValue();
                } else {
                    dateCode = na;

                }
                addProperty(category, CABLE_DATE_CODE, dateCode);
            }

            byte ccExt = bean.getCcExt();
            addProperty(category, CABLE_CC_EXT, hex(ccExt));

            boolean ccFlag = bean.getCertCableFlag();
            addProperty(category, CABLE_CERT_CABLE_FLAG,
                    ccFlag ? STLConstants.K1155_CABLE_Y.getValue()
                            : STLConstants.K1156_CABLE_N.getValue());

            int reachClass = bean.getReachClass();
            addProperty(category, CABLE_REACH_CLASS,
                    Integer.toString(reachClass));

            CertifiedRateType certDataRate = bean.getCertDataRate();
            if (certDataRate != null) {
                try {
                    addProperty(category, CABLE_CERT_DATA_RATE,
                            CertifiedRateTypeViz
                                    .getCertifiedRateTypeVizFor(certDataRate)
                                    .getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                addProperty(category, CABLE_CERT_DATA_RATE, na);
            }

        } else {
            getNullCableInfo(category);
        }
    }

    private void getNullCableInfo(DevicePropertyCategory category) {
        String na = STLConstants.K0039_NOT_AVAILABLE.getValue();
        addProperty(category, CABLE_ID, na);
        addProperty(category, CABLE_POWER_CLASS, na);
        addProperty(category, CABLE_TX_CDR_SUP, na);
        addProperty(category, CABLE_RX_CDR_SUP, na);
        addProperty(category, CABLE_CONNECTOR, na);
        addProperty(category, CABLE_NOMINAL_BR, na);
        addProperty(category, CABLE_OM2_LEN, na);
        addProperty(category, CABLE_OM3_LEN, na);
        addProperty(category, CABLE_COPPER_LEN, na);
        addProperty(category, CABLE_DEVICE_TECH, na);
        addProperty(category, CABLE_VENDOR_NAME, na);
        addProperty(category, CABLE_VENDOR_OUI, na);
        addProperty(category, CABLE_VENDOR_PN, na);
        addProperty(category, CABLE_VENDOR_REV, na);
        addProperty(category, CABLE_MAXCASE_TEMP, na);
        addProperty(category, CABLE_CC_BASE, na);
        addProperty(category, CABLE_TX_INP_EQ_AUTO_ADP, na);
        addProperty(category, CABLE_TX_INP_EQ_FIX_PROG, na);
        addProperty(category, CABLE_RX_OUTP_EMPH_FIX_PROG, na);
        addProperty(category, CABLE_RX_OUTP_AMPL_FIX_PROG, na);
        addProperty(category, CABLE_TX_CDR_ON_OFF_CTRL, na);
        addProperty(category, CABLE_RX_CDR_ON_OFF_CTRL, na);
        addProperty(category, CABLE_TX_SQUELCH_IMP, na);
        addProperty(category, CABLE_MEM_PAGE02_PROV, na);
        addProperty(category, CABLE_MEM_PAGE01_PROV, na);
        addProperty(category, CABLE_VENDOR_SN, na);
        addProperty(category, CABLE_DATE_CODE, na);
        addProperty(category, CABLE_CC_EXT, na);
        addProperty(category, CABLE_CERT_CABLE_FLAG, na);
        addProperty(category, CABLE_REACH_CLASS, na);
        addProperty(category, CABLE_CERT_DATA_RATE, na);
    }

    public void getDDCableInfo(DevicePropertyCategory category,
            DDCableInfoBean bean) {

        String na = STLConstants.K0039_NOT_AVAILABLE.getValue();
        if (bean != null) {
            byte id = bean.getId();
            addProperty(category, CABLE_ID, hex(id));

            String vendorName = bean.getVendorName();
            if (vendorName != null) {
                addProperty(category, CABLE_VENDOR_NAME, vendorName);
            } else {
                addProperty(category, CABLE_VENDOR_NAME, na);
            }
            byte[] vendorOui = bean.getVendorOui();
            if (vendorOui != null) {
                StringBuilder sb = new StringBuilder(vendorOui.length * 2);
                sb.append("0x");
                for (byte b : vendorOui) {
                    sb.append(String.format("%02x", b));
                }
                addProperty(category, CABLE_VENDOR_OUI, sb.toString());
            } else {
                addProperty(category, CABLE_VENDOR_OUI, na);
            }

            String vendorPN = bean.getVendorPn();
            if (vendorPN != null) {
                addProperty(category, CABLE_VENDOR_PN, vendorPN);
            } else {
                addProperty(category, CABLE_VENDOR_PN, na);
            }
            String vendorRev = bean.getVendorRev();
            if (vendorRev != null) {
                addProperty(category, CABLE_VENDOR_REV, vendorRev);
            } else {
                addProperty(category, CABLE_VENDOR_REV, na);
            }
            String vendorSn = bean.getVendorSN();
            if (vendorSn != null) {
                addProperty(category, CABLE_VENDOR_SN, vendorSn);
            } else {
                addProperty(category, CABLE_VENDOR_SN, na);
            }
            String dateCode = bean.getDateCode();
            if (dateCode != null) {
                addProperty(category, CABLE_DATE_CODE, "20" + dateCode);
            } else {
                String beanDateCode = bean.getDateCode();
                if (beanDateCode != null) {
                    dateCode = STLConstants.K1116_INVALID.getValue();
                } else {
                    dateCode = na;

                }
                addProperty(category, CABLE_DATE_CODE, dateCode);
            }
            double maxPwr = bean.getMaxPower();
            if (maxPwr > 0) {
                addProperty(category, CABLE_POWER_CLASS,
                        String.format("%.2f %s", maxPwr,
                                STLConstants.K1656_W_MAX.getValue()));
            } else {
                addProperty(category, CABLE_POWER_CLASS, na);
            }
            byte xmitTech = bean.getXmitTech();
            byte codeConnector = bean.getConnector();
            try {
                CableTypeViz cableType = CableTypeViz.getCableTypeVizFor(
                        CableType.getCableType(xmitTech, codeConnector));
                if (cableType != null) {
                    addProperty(category, CABLE_DEVICE_TECH,
                            cableType.getName());
                } else {
                    addProperty(category, CABLE_DEVICE_TECH, na);
                }
            } catch (Exception e) {
                e.printStackTrace();
                addProperty(category, CABLE_DEVICE_TECH, na);
            }
            short cableLength = bean.getCableLength();
            addProperty(category, CABLE_LENGTH, Short.toString(cableLength)
                    + " " + STLConstants.K1154_CABLE_M.getValue());
        } else {
            getNullDDCableInfo(category);
        }
    }

    private void getNullDDCableInfo(DevicePropertyCategory category) {
        String na = STLConstants.K0039_NOT_AVAILABLE.getValue();
        addProperty(category, CABLE_ID, na);
        addProperty(category, CABLE_POWER_CLASS, na);
        addProperty(category, CABLE_VENDOR_NAME, na);
        addProperty(category, CABLE_VENDOR_OUI, na);
        addProperty(category, CABLE_VENDOR_PN, na);
        addProperty(category, CABLE_VENDOR_REV, na);
        addProperty(category, CABLE_VENDOR_SN, na);
        addProperty(category, CABLE_DATE_CODE, na);
        addProperty(category, CABLE_MAXCASE_TEMP, na);
        addProperty(category, CABLE_LENGTH, na);
        addProperty(category, CABLE_DEVICE_TECH, na);
    }
}
