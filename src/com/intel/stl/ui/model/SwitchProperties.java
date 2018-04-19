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

package com.intel.stl.ui.model;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.subnet.LFTRecordBean;
import com.intel.stl.api.subnet.MFTRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.api.subnet.SwitchRecordBean;
import com.intel.stl.ui.common.STLConstants;

/**
 * @deprecated use {@link com.intel.stl.ui.model.DevicePropertyCategory}
 */
@Deprecated
public class SwitchProperties extends NodeProperties {

    private SwitchRecordBean swRec = null;

    private NodeRecordBean nodeRec = null;

    private List<LFTRecordBean> lftRecs = null;

    private List<MFTRecordBean> mftRecs = null;

    private List<String> deviceGrp = null;

    boolean hasData = false;

    public SwitchProperties(SwitchRecordBean swR, NodeRecordBean nodeR,
            List<LFTRecordBean> lftR, List<MFTRecordBean> mftR, List<String> grp) {
        super(nodeR, grp);
        swRec = swR;
        lftRecs = lftR;
        mftRecs = mftR;
        nodeRec = nodeR;
        deviceGrp = grp;
        // NodeRec = nodeRec;

        if ((swRec != null) && (nodeRec != null) && (lftRecs != null)
                && (mftRecs != null) && (swRec.getSwitchInfo() != null)
                && (deviceGrp != null)) {
            hasData = true;
        }
    }

    public String getIPChassisName() {
        String retVal = STLConstants.K0383_NA.getValue();

        return retVal;
    }

    public String getNumEntries() {
        if (hasData) {
            return Integer.toString(swRec.getSwitchInfo()
                    .getPartitionEnforcementCap());
        } else {
            return "";
        }
    }

    public String getEnhancedPort0Support() {
        String retVal = "";
        if (hasData) {
            if (swRec.getSwitchInfo().isEnhancedPort0()) {
                retVal = STLConstants.K0385_TRUE.getValue();
            } else {
                retVal = STLConstants.K0386_FALSE.getValue();
            }
        }

        return retVal;
    }

    public List<String[]> getLIDsAndForwardedPorts() {
        List<String[]> retVal = new ArrayList<String[]>();

        if (hasData) {
            for (LFTRecordBean lft : lftRecs) {
                int lid = lft.getBlockNum() * SAConstants.FDB_DATA_LENGTH;
                byte[] retPts = lft.getLinearFdbData();
                for (int i = 0; i < retPts.length; i++, lid++) {
                    if (retPts[i] != -1) {
                        retVal.add(new String[] {
                                StringUtils.intHexString(lid),
                                Short.toString((short) (retPts[i] & 0xff)) });
                    }
                }
            }
        }

        return retVal;

    }

    public List<String[]> getLIDsAndMultiForwardedPorts() {
        List<String[]> retVal = new ArrayList<String[]>();

        if (hasData) {
            for (MFTRecordBean mft : mftRecs) {
                int lid =
                        mft.getBlockNum()
                                * SAConstants.STL_NUM_MFT_ELEMENTS_BLOCK
                                + SAConstants.LID_MCAST_START;
                long[] retMasks = mft.getMftTable();
                for (int i = 0; i < retMasks.length; i++, lid++) {
                    if (retMasks[i] > 0) {
                        retVal.add(new String[] {
                                StringUtils.intHexString(lid),
                                StringUtils.longHexString(retMasks[i]) });
                    }
                }
            }
        }
        return retVal;
    }

}
