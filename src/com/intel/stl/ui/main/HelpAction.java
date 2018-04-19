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

package com.intel.stl.ui.main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JMenuItem;

public class HelpAction {

    public static boolean DYNAMIC_SIZE = false;

    /**
     *
     */
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -1868097381130706771L;

    // Help ID strings
    private final String DEFAULT = "GUID-4B372184-0331-4BEA-9958-578F7E030F39";

    private final String SUBNET_SUMMARY =
            "GUID-A3DCE301-7E40-445A-81F0-B2CADEAEAF0A";

    private final String SUBNET_NAME =
            "GUID-F3CB56AD-4135-4F26-A1B2-07AB07D3A3C3";

    private final String STATUS = "GUID-96555ADB-F524-41F4-A803-FFB0942DB3FC";

    private final String HEALTH_TREND =
            "GUID-5F45E9FD-D356-4631-BC5B-8854FD28CC0E";

    private final String WORST_NODES =
            "GUID-1686A817-3EC8-4C32-B6E8-90B804418596";

    private final String SUBNET_PERFORMANCE =
            "GUID-0F8B013E-084E-4F34-8D8F-1E6679EB85FE";

    private final String UNIT_GROUP =
            "GUID-13874FF6-49CF-4EE9-A937-40254947787B";

    private final String ERROR_GROUP =
            "GUID-25D1F25A-CC7E-4F46-BDB5-9B10F2624065";

    private final String TREND = "GUID-0A2A33E6-DDBD-4323-9721-40B53A60EAC5";

    private final String TOP_N = "GUID-4C9DB627-99EA-4475-B0FD-677ACEF0D89D";

    private final String PERF_SUBNET_SUMMARY =
            "GUID-4A0DC71A-2224-41C4-8BF8-47DDB59AD963";

    private final String STATISTICS =
            "GUID-D67D3699-28D5-4488-A235-3040CF9350AF";

    private final String EVENTS = "GUID-79008C3C-15F7-45FE-B795-DE29C8C5B9E9";

    private final String GENERAL_SUMMARY =
            "GUID-2B9BFA98-8AAB-4620-9F23-92E06346A11A";

    private final String PERF_TREND =
            "GUID-0A2A33E6-DDBD-4323-9721-40B53A60EAC5";

    private final String HISTOGRAM =
            "GUID-9AF58D84-4FCE-49B4-BCA3-03C91FB1FD34";

    private final String PERF_TOP_N =
            "GUID-4C9DB627-99EA-4475-B0FD-677ACEF0D89D";

    private final String PORT_PERF =
            "GUID-F4DBA2C1-FFC5-43CA-A499-388060815F2D";

    private final String PORT_RCV_PKTS =
            "GUID-F5CFD46A-C11B-429D-A416-E1186319ADF6";

    private final String PORT_TRAN_PKTS =
            "GUID-A8C4761A-08D5-4440-A94B-BBFEDC156499";

    private final String NODE_PERF =
            "GUID-70C75041-ECD6-4300-A93F-F1E121B6D98C";

    private final String NODE_RCV_PKTS =
            "GUID-F5CFD46A-C11B-429D-A416-E1186319ADF6";

    private final String NODE_TRAN_PKTS =
            "GUID-A8C4761A-08D5-4440-A94B-BBFEDC156499";

    private final String COUNTERS = "GUID-A352C507-C5F4-48A5-8BA9-736581803B8C";

    private final String PORT_COUNTERS =
            "GUID-8CC288BF-70AB-4523-B32D-FA8C4F725AC0";

    // Port Counters
    // Receive Counters
    private final String RCV_DATA = "GUID-F5CFD46A-C11B-429D-A416-E1186319ADF6";

    private final String RCV_PKTS = "GUID-F5CFD46A-C11B-429D-A416-E1186319ADF6";

    private final String MULTICAST_RCV_PKTS =
            "GUID-3C147939-C18A-4346-9D99-74BAB5D3DFA6";

    private final String RCV_ERRS = "GUID-3B4561AD-E1EF-4F39-A39E-268C0DD9FD3A";

    private final String RCV_CONSTRAINT_ERRS =
            "GUID-9A652048-0F32-4D6E-B64C-B742BFF3E6F3";

    private final String RCV_SW_REL_ERRS =
            "GUID-E4169146-0B33-4EFE-97AE-36384D85E630";

    private final String RCV_REM_PHY_ERRS =
            "GUID-EFCE8B3A-98FF-4386-8C44-FB7E6F01A6F3";

    private final String RCV_FECN = "GUID-186A280F-E732-4365-9A7B-AF633DD777C1";

    private final String RCV_BECN = "GUID-D94D064B-D4EC-4BAB-90F9-64F536058776";

    private final String RCV_BUBBLE =
            "GUID-89804D08-63B7-44C8-BF86-D00F53763999";

    // Transmit Counters
    private final String XMIT_DATA =
            "GUID-A8C4761A-08D5-4440-A94B-BBFEDC156499";

    private final String XMIT_PKTS =
            "GUID-A8C4761A-08D5-4440-A94B-BBFEDC156499";

    private final String MULTICAST_XMIT_PKTS =
            "GUID-DDED73D4-4C43-44BE-8FAE-4214F4E1D142";

    private final String XMIT_DISCARDS =
            "GUID-148C47A5-5D6A-49BD-BBA4-90D0BC10F1E0";

    private final String XMIT_CONSTRAINT_ERRS =
            "GUID-FC39FD8D-5C5E-4FC2-A50F-C59939E363BA";

    private final String XMIT_WAIT =
            "GUID-BCFEC34D-924F-4E4E-80E2-F2A1D8BE537F";

    private final String XMIT_TIME_CONG =
            "GUID-AE9E07FE-AC99-49F2-86B0-A8118E235B2B";

    private final String XMIT_WASTED_BW =
            "GUID-FCD750F4-45F2-49B7-9A09-8C41BDC2C7EC";

    private final String XMIT_WAIT_DATA =
            "GUID-73FCC98D-2678-432C-A36D-DE8CF6341719";

    // Other Counters
    private final String LOCAL_LINK_INTEG_ERRS =
            "GUID-4D0309E3-9A47-4F35-A973-2840BA27D81A";

    private final String FM_CONFIG_ERRS =
            "GUID-7B75E70B-A43C-4F56-B470-0B59587B8BE9";

    private final String EXC_BUFFER_OVER =
            "GUID-51A4E172-EABF-42C0-9673-42809AEF05D0";

    private final String SW_PORT_CONG =
            "GUID-0E409862-6375-413F-8289-6621B20AC338";

    private final String MARK_FECN =
            "GUID-29E050E6-8029-4B8E-A5EC-37F336E37B36";

    private final String LINK_ERR_REC =
            "GUID-113DCE79-CAFC-4028-A2CB-6FDB863844F0";

    private final String LINK_DOWN =
            "GUID-B226FB6B-5E55-41E3-89FD-32637E7F1D78";

    private final String UNCORR_ERRS =
            "GUID-4EAD9828-C6C9-4FD7-B4BC-2C76DE2E7A54";

    private final String LINK_QUAL =
            "GUID-AD014247-7DA0-4C8C-BB14-4C650FF82865";

    // Topology page
    private final String NAME_OF_SUBNET =
            "GUID-30A92411-3312-45AA-A09F-56AD35F29567";

    private final String TOPOLOGY_NODE =
            "GUID-0C3FFAC9-B71D-44B0-A74F-60D7AF20C683";

    private final String OVERALL_SUMMARY =
            "GUID-CD12ECBC-7E28-4B82-9419-BF16256A012F";

    private final String TOPOLOGY_SUMMARY =
            "GUID-CD12ECBC-7E28-4B82-9419-BF16256A012F";

    private final String LINKS = "GUID-4D0C7193-5343-4D40-AB29-8A1A0D4684FD";

    private final String ROUTES = "GUID-2F669B07-F890-44FA-9771-1C3E427FEDF2";

    private final String DEVICE_GROUP =
            "GUID-14B1543F-C9DC-4B01-B2E0-BCAE320CB212";

    private final String MFT = "GUID-B3A46E21-3C2B-4B86-9ED0-0DA29AEAF597";

    private final String LFT = "GUID-509C627F-D08E-4104-8D86-9EB3430D426D";

    private final String PERF_NODE_PORTS_TABLE =
            "GUID-C5A734FF-34F0-4A54-A40F-FA80703B10FB";

    // HFI Node level properties
    private final String NODE_GENERAL =
            "GUID-BCC5875D-76FA-414C-A0DC-DFC3EB1640B9";// ResourceCategory.NODE_INFO

    private final String SC2SL = "GUID-3A2A68C1-3DD3-4C91-ABE7-2B3F96EDADAE";

    // Switch Node level properties
    private final String SWITCH_INFORMATION =
            "GUID-4E451160-BD28-48E7-AA1E-5C8117B39B49";

    private final String ROUTING_INFORMATION =
            "GUID-AF9B8778-17F8-4BEA-8979-C9119DE9AC2C";

    // Port level properties
    private final String PORT_DEVICE_INFO =
            "GUID-80DD968E-049F-49FA-9BA8-107446DF2F71";// ResourceCategory.PORT_INFO

    private final String PORT_LINK =
            "GUID-37EC9203-4C4F-4E82-A4C5-ED151C5379FE";

    private final String PORT_LINK_CONN =
            "GUID-E3841E8C-4026-4050-9A25-2E2670327BC5";

    private final String PORT_CAPABILITY =
            "GUID-0C771533-6402-4BC7-93E4-35D5F4203CC9";

    private final String VL = "GUID-6B2C80A1-89CF-4DCC-AE7D-5B8B11F4B9DC";

    private final String DIAGNOSTICS =
            "GUID-5FB5A987-8A67-4B2F-AD09-51C694C0ED9B";

    private final String PARTITION =
            "GUID-83CC80FB-B0C4-4D03-AD39-00BD7210B688";

    private final String MANAGEMENT =
            "GUID-19E75083-19D2-4837-AC64-F7654F04842C";

    private final String FLIT_CONTROL =
            "GUID-9ABCBE5B-D5FE-49B8-8EF3-6FA0E7808CE0";

    private final String PORT_ERROR_ACTIONS =
            "GUID-040685F8-87FF-4856-A4B4-5645C5D73DB4";

    private final String MISCELLANEOUS =
            "GUID-AB8249B4-8904-40EF-B056-6570A1207D6A";

    private final String MTU_VL = "GUID-445726B9-7F54-4971-A0F6-055F9364B1D0";

    private final String HOQLIFE_VL =
            "GUID-E0F13187-54DD-4EA5-AE69-A9A8F901F3FA";

    private final String STALLCOUNT_VL =
            "GUID-494FB010-B0EA-4B77-BB41-02D7B5419B88";

    private final String QSFP = "GUID-70F1021D-77A6-4AAA-B5CF-9405711066A1";

    private final String SC2VLT = "GUID-E1B4725F-B178-47C3-A257-890E1915BD08";

    private final String SC2VLNT = "GUID-9E5155FF-35AD-4BA8-8C52-12FCE06CF80C";

    private final String LINK_DOWN_ERROR =
            "GUID-67A138BD-D88A-4C60-93DD-CB4EA5D16717";

    // setup wizard
    private final String SETUP_WIZARD =
            "GUID-4B372184-0331-4BEA-9958-578F7E030F39";

    // Admin page
    private final String ADMIN_APP =
            "GUID-6F61C532-13BA-4144-9E1B-D80C50F5AF4F";

    private final String ADMIN_DG = "GUID-621E93B2-383E-4EA1-8BA8-E18825C65006";

    private final String ADMIN_VF = "GUID-331F206B-089E-4271-B768-D3BD4846B6A0";

    private final String ADMIN_CONSOLE =
            "GUID-48853F77-50C1-4650-9205-104455B51088";

    private final String ADMIN_CONSOLE_TERMINAL =
            "GUID-48853F77-50C1-4650-9205-104455B51088";

    private final String ADMIN_LOG_VIEWER =
            "GUID-A7A4DFCC-C7F3-4657-8023-0D852D35DD9D";

    // Java Help system navigator type enum.
    private final JavaHelpNavType view;

    private static final String FILE_NAME =
            "GUID-02097852-C3C6-4C04-B7B5-3DADCB0EFF62.hs";

    private OnlineHelpBroker helpBroker;

    private HelpSet helpSet;

    private static HelpAction instance = null;

    public static HelpAction getInstance() {
        if (instance == null) {
            instance = new HelpAction();
        }
        return instance;
    }

    protected HelpAction() {
        view = JavaHelpNavType.TOC;
        initHelpSystem();
    }

    private void initHelpSystem() {
        if (helpBroker != null && helpSet != null) {
            return;
        }

        helpSet = initHelpSet();
        if (helpSet != null) {
            helpSet.setKeyData(HelpSet.implRegistry, HelpSet.helpBrokerClass,
                    OnlineHelpBroker.class.getName());
            helpBroker = (OnlineHelpBroker) helpSet.createHelpBroker();
            helpBroker.setCurrentView(view.toString());

            if (DYNAMIC_SIZE) {
                int width = (int) (Toolkit.getDefaultToolkit().getScreenSize()
                        .getWidth() * .4f);
                int height = (int) (Toolkit.getDefaultToolkit().getScreenSize()
                        .getHeight() * .8f);
                helpBroker.setSize(new Dimension(width, height));
            }
        }
    }

    protected HelpSet initHelpSet() {

        URL hsURL = HelpSet.findHelpSet(null, FILE_NAME);

        HelpSet helpSet = null;
        try {
            helpSet = new HelpSet(null, hsURL);
        } catch (HelpSetException e) {
            e.printStackTrace();
        }

        return helpSet;
    }

    public void enableHelpMenu(JMenuItem menu) {
        helpBroker.enableHelpOnButton(menu, DEFAULT, helpSet);
    }

    public HelpBroker getHelpBroker() {
        return helpBroker;
    };

    public HelpSet getHelpSet() {
        return helpSet;
    }

    public String getDefault() {
        return DEFAULT;
    }

    /**
     * @return the subnetSummary
     */
    public String getSubnetSummary() {
        return SUBNET_SUMMARY;
    }

    /**
     * @return the subnetName
     */
    public String getSubnetStatisticsName() {
        return SUBNET_NAME;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return STATUS;
    }

    /**
     * @return the healthTrend
     */
    public String getHealthTrend() {
        return HEALTH_TREND;
    }

    /**
     * @return the worstNodes
     */
    public String getWorstNodes() {
        return WORST_NODES;
    }

    /**
     * @return the subnetPerformance
     */
    public String getSubnetPerformance() {
        return SUBNET_PERFORMANCE;
    }

    public String getUnitGroup() {
        return UNIT_GROUP;
    }

    public String getErrorGroup() {
        return ERROR_GROUP;
    }

    /**
     * @return the trend
     */
    public String getTrend() {
        return TREND;
    }

    /**
     * @return the topN
     */
    public String getTopN() {
        return TOP_N;
    }

    /**
     * @return the perfSubnetSummary
     */
    public String getPerfSubnetSummary() {
        return PERF_SUBNET_SUMMARY;
    }

    /**
     * @return the statistics
     */
    public String getPerformanceStatistics() {
        return STATISTICS;
    }

    /**
     * @return the events
     */
    public String getEvents() {
        return EVENTS;
    }

    /**
     * @return the generalSummary
     */
    public String getGeneralSummary() {
        return GENERAL_SUMMARY;
    }

    /**
     * @return the perfTrend
     */
    public String getPerfTrend() {
        return PERF_TREND;
    }

    /**
     * @return the histogram
     */
    public String getHistogram() {
        return HISTOGRAM;
    }

    /**
     * @return the perfTopN
     */
    public String getPerfTopN() {
        return PERF_TOP_N;
    }

    /**
     * @return the performance
     */
    public String getPortPerf() {
        return PORT_PERF;
    }

    /**
     * @return the receivedPackets
     */
    public String getPortRcvPkts() {
        return PORT_RCV_PKTS;
    }

    /**
     * @return the transmittedPackets
     */
    public String getPortTranPkts() {
        return PORT_TRAN_PKTS;
    }

    /**
     * @return the counters
     */
    public String getCounters() {
        return COUNTERS;
    }

    public String getPortCounters() {
        return PORT_COUNTERS;
    }

    public String getRcvData() {
        return RCV_DATA;
    }

    public String getRcvPkts() {
        return RCV_PKTS;
    }

    public String getMulticastRcvPkts() {
        return MULTICAST_RCV_PKTS;
    }

    public String getRcvErrs() {
        return RCV_ERRS;
    }

    public String getRcvConstraintErrs() {
        return RCV_CONSTRAINT_ERRS;
    }

    public String getRcvSwRelErrs() {
        return RCV_SW_REL_ERRS;
    }

    public String getRcvRemPhyErrs() {
        return RCV_REM_PHY_ERRS;
    }

    public String getRcvFECN() {
        return RCV_FECN;
    }

    public String getRcvBECN() {
        return RCV_BECN;
    }

    public String getRcvBubble() {
        return RCV_BUBBLE;
    }

    public String getXmitData() {
        return XMIT_DATA;
    }

    public String getXmitPkts() {
        return XMIT_PKTS;
    }

    public String getMulticastXmitPkts() {
        return MULTICAST_XMIT_PKTS;
    }

    public String getXmitDiscards() {
        return XMIT_DISCARDS;
    }

    public String getXmitConstraintErrs() {
        return XMIT_CONSTRAINT_ERRS;
    }

    public String getXmitWait() {
        return XMIT_WAIT;
    }

    public String getXmitTimeCong() {
        return XMIT_TIME_CONG;
    }

    public String getXmitWastedBw() {
        return XMIT_WASTED_BW;
    }

    public String getXmitWaitData() {
        return XMIT_WAIT_DATA;
    }

    public String getLocalLinkIntegErrs() {
        return LOCAL_LINK_INTEG_ERRS;
    }

    public String getFMConfigErrs() {
        return FM_CONFIG_ERRS;
    }

    public String getExcBufferOver() {
        return EXC_BUFFER_OVER;
    }

    public String getSwPortCong() {
        return SW_PORT_CONG;
    }

    public String getMarkFECN() {
        return MARK_FECN;
    }

    public String getLinkErrRec() {
        return LINK_ERR_REC;
    }

    public String getLinkDown() {
        return LINK_DOWN;
    }

    public String getUncorrErrs() {
        return UNCORR_ERRS;
    }

    public String getLinkQual() {
        return LINK_QUAL;
    }

    /**
     * @return the NODE_PERFORMANCE
     */
    public String getNodePerf() {
        return NODE_PERF;
    }

    /**
     * @return the NODE_RECEIVED_PACKETS
     */
    public String getNodeRcvPkts() {
        return NODE_RCV_PKTS;
    }

    /**
     * @return the nODE_TRANSMITTED_PACKETS
     */
    public String getNodeTranPkts() {
        return NODE_TRAN_PKTS;
    }

    /**
     * @return the nameOfSubnet
     */
    public String getNameOfSubnet() {
        return NAME_OF_SUBNET;
    }

    /**
     * @return the tOPOLOGY_NODE
     */
    public String getTopologyNode() {
        return TOPOLOGY_NODE;
    }

    /**
     * @return the overallSummary
     */
    public String getOverallSummary() {
        return OVERALL_SUMMARY;
    }

    /**
     * @return the topologySummary
     */
    public String getTopologySummary() {
        return TOPOLOGY_SUMMARY;
    }

    /**
     * @return the links
     */
    public String getLinks() {
        return LINKS;
    }

    /**
     * @return the rOUTES
     */
    public String getRoutes() {
        return ROUTES;
    }

    /**
     * @return the switchInformation
     */
    public String getSwitchInformation() {
        return SWITCH_INFORMATION;
    }

    /**
     * @return the routingInformation
     */
    public String getRoutingInformation() {
        return ROUTING_INFORMATION;
    }

    /**
     * @return the deviceGroup
     */
    public String getDeviceGroup() {
        return DEVICE_GROUP;
    }

    /**
     * @return the mft
     */
    public String getMft() {
        return MFT;
    }

    /**
     * @return the lft
     */
    public String getLft() {
        return LFT;
    }

    /**
     * @return the PERF_NODE_PORTS_TABLE
     */
    public String getPerfNodePortsTable() {
        return PERF_NODE_PORTS_TABLE;
    }

    public String getNodeGeneral() {
        return NODE_GENERAL;
    }

    /**
     * @return the mTU_VL
     */
    public String getMTUByVL() {
        return MTU_VL;
    }

    /**
     * @return the hOQLIFE_VL
     */
    public String getHoQLifeByVL() {
        return HOQLIFE_VL;
    }

    /**
     * @return the sTALLCOUNT_VL
     */
    public String getStallCountByVL() {
        return STALLCOUNT_VL;
    }

    /**
     * @return the qSFP
     */
    public String getQSFP() {
        return QSFP;
    }

    /**
     * @return the sC2SL
     */
    public String getSC2SL() {
        return SC2SL;
    }

    /**
     * @return the sC2VLT
     */
    public String getSC2VLT() {
        return SC2VLT;
    }

    /**
     * @return the sC2VNLT
     */
    public String getSC2VLNT() {
        return SC2VLNT;
    }

    /**
     * @return the lINK_DOWN_ERROR
     */
    public String getLinkDownError() {
        return LINK_DOWN_ERROR;
    }

    /**
     * @return the pORT_ERROR_ACTIONS
     */
    public String getPortErrorActions() {
        return PORT_ERROR_ACTIONS;
    }

    /**
     * @return the mISCELLANEOUS
     */
    public String getMisc() {
        return MISCELLANEOUS;
    }

    /**
     * @return the pORT_DEVICE_INFO
     */
    public String getPortDevInfo() {
        return PORT_DEVICE_INFO;
    }

    /**
     * @return the pORT_LINK
     */
    public String getPortLink() {
        return PORT_LINK;
    }

    /**
     * @return the pORT_LINK_CONN
     */
    public String getPortLinkConn() {
        return PORT_LINK_CONN;
    }

    /**
     * @return the pORT_CAPABILITY
     */
    public String getPortCap() {
        return PORT_CAPABILITY;
    }

    /**
     * @return the vL
     */
    public String getVL() {
        return VL;
    }

    /**
     * @return the dIAGNOSTICS
     */
    public String getDiagnostics() {
        return DIAGNOSTICS;
    }

    /**
     * @return the pARTITION
     */
    public String getPartition() {
        return PARTITION;
    }

    /**
     * @return the mANAGEMENT
     */
    public String getManagement() {
        return MANAGEMENT;
    }

    /**
     * @return the fLIT_CONTROL
     */
    public String getFlitControl() {
        return FLIT_CONTROL;
    }

    /**
     * @return the sETUP_WIZARD
     */
    public String getSetupWizard() {
        return SETUP_WIZARD;
    }

    /**
     * @return the aDMIN_APP
     */
    public String getAdminApp() {
        return ADMIN_APP;
    }

    /**
     * @return the aDMIN_DG
     */
    public String getAdminDg() {
        return ADMIN_DG;
    }

    /**
     * @return the aDMIN_VF
     */
    public String getAdminVf() {
        return ADMIN_VF;
    }

    /**
     * @return the aDMIN_CONSOLE
     */
    public String getAdminConsole() {
        return ADMIN_CONSOLE;
    }

    /**
     * @return the aDMIN_CONSOLE_TERMINAL
     */
    public String getAdminConsoleTerminal() {
        return ADMIN_CONSOLE_TERMINAL;
    }

    public String getAdminLogViewer() {
        return ADMIN_LOG_VIEWER;
    }
}
