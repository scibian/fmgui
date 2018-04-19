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
package com.intel.stl.fecdriver.messages.adapter.pa;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.performance.ErrBucketBean;
import com.intel.stl.api.performance.ErrStatBean;
import com.intel.stl.api.performance.ErrSummaryBean;
import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.performance.PAConstants;
import com.intel.stl.api.performance.UtilStatsBean;
import com.intel.stl.api.performance.VFInfoBean;
import com.intel.stl.common.StringUtils;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pa.h v1.55<br>
 * ref: /ALL_EMB/IbAccess/Common/Inc/ib_pa.h
 *
 * <pre>
 *  typedef struct _STL_PA_VF_INFO_DATA {
 * [64] 	char					vfName[STL_PM_VFNAMELEN];	// \0 terminated
 * [72] 	uint64					reserved;
 * [88] 	STL_PA_IMAGE_ID_DATA	imageId;
 * [92] 	uint32					numPorts;
 * [180] STL_PA_PM_UTIL_STATS	internalUtilStats;
 * [340] STL_PMERRSTAT_T			internalErrors;
 *  	// these are added at the end to allow for forward and backward
 *  	// compatibility.
 * [341] uint8					maxInternalRate;
 * [342] uint8					minInternalRate;
 * [346] uint32					maxInternalMBps;
 *  } PACK_SUFFIX STL_PA_VF_INFO_DATA;
 *
 *  typedef struct _STL_PA_Image_ID_Data {
 * [8] 	uint64					imageNumber;
 * [12] 	int32					imageOffset;
 * [16] 	uint32					reserved;
 *  } PACK_SUFFIX STL_PA_IMAGE_ID_DATA;
 *
 *  // Utilization statistical summary (88 bytes)
 *  typedef struct _STL_PA_PM_Util_Stats {
 *  [0]  uint64                  totalMBps;  // MB per sec
 *  [8]  uint64                  totalKPps;  // K pkts per sec
 *  [16] uint32                  avgMBps;
 *  [20] uint32                  minMBps;
 *  [24] uint32                  maxMBps;
 *  [28] uint32                  numBWBuckets;
 *  [32] uint32                  BWBuckets[STL_PM_UTIL_BUCKETS];
 *  [72] uint32                  avgKPps;
 *  [76] uint32                  minKPps;
 *  [80] uint32                  maxKPps;
 *  [82] uint16                  pmaFailedPorts;
 *  [84] uint16                  topoFailedPorts;
 *  } PACK_SUFFIX STL_PA_PM_UTIL_STATS;
 *
 *  // 40 + 24*5 = 160 bytes
 *  typedef struct STL_PMERRSTATSTRUCT {
 *   STL_PA_PM_ERROR_SUMMARY     errorMaximums;
 *   STL_PMERRBUCKET_T   ports[STL_PM_ERR_BUCKETS];
 *  } PACK_SUFFIX STL_PMERRSTAT_T;
 *
 *  // 24 bytes
 *  typedef struct STL_PMERRBUCKETSTRUCT {
 *  [0]   uint32                  integrityErrors;
 *  [4]   uint32                  congestionErrors;
 *  [8]   uint32                  smaCongestionErrors;
 *  [12]   uint32                  bubbleErrors;
 *  [16]   uint32                  securityErrors;
 *  [20]   uint32                  routingErrors;
 *  } PACK_SUFFIX STL_PMERRBUCKET_T;
 *
 *  // Error statistical summary (40 bytes)
 *  typedef struct _STL_PA_PM_Error_Summary {
 *  [0]    uint32                  integrityErrors;
 *  [4]    uint32                  congestionErrors;
 *  [8]    uint32                  smaCongestionErrors;
 *  [12]    uint32                  bubbleErrors;
 *  [16]    uint32                  securityErrors;
 *  [20]    uint32                  routingErrors;
 *
 *  [24]    uint16                  utilizationPct10;         // in units of 10%
 *  [26]    uint16                  discardsPct10;            // in units of 10%
 *  [28]    uint16                  reserved[6];
 *  } PACK_SUFFIX STL_PA_PM_ERROR_SUMMARY;
 *
 *  #define STL_PM_VFNAMELEN		64
 *
 *  #define STL_PM_UTIL_GRAN_PERCENT 10 // granularity of utilization buckets
 *  #define STL_PM_UTIL_BUCKETS (100 / STL_PM_UTIL_GRAN_PERCENT)
 *
 *  #define PM_ERR_GRAN_PERCENT 25  // granularity of error buckets
 *  #define PM_ERR_BUCKETS ((100/PM_ERR_GRAN_PERCENT)+1) // extra bucket is for those over threshold
 *
 * </pre>
 *
 */
public class VFInfo extends SimpleDatagram<VFInfoBean> {
    public VFInfo() {
        super(346);
    }

    public void setVfName(String name) {
        StringUtils.setString(name, buffer, 0, PAConstants.STL_PM_GROUPNAMELEN);
    }

    public void setImageNumber(long num) {
        buffer.putLong(72, num);
    }

    public void setImageOffset(int offset) {
        buffer.putInt(80, offset);
    }

    private UtilStatsBean getUtilStatsBean(int position) {
        buffer.position(position);
        UtilStatsBean bean = new UtilStatsBean();
        bean.setTotalMBps(buffer.getLong());
        bean.setTotalKPps(buffer.getLong()); // K pkts per sec
        bean.setAvgMBps(buffer.getInt());
        bean.setMinMBps(buffer.getInt());
        bean.setMaxMBps(buffer.getInt());
        bean.setNumBWBuckets(buffer.getInt());
        List<Integer> buckets =
                new ArrayList<Integer>(PAConstants.STL_PM_UTIL_BUCKETS);
        for (int i = 0; i < PAConstants.STL_PM_UTIL_BUCKETS; i++) {
            buckets.add(buffer.getInt());
        }
        bean.setBwBuckets(buckets);
        bean.setAvgKPps(buffer.getInt());
        bean.setMinKPps(buffer.getInt());
        bean.setMaxKPps(buffer.getInt());
        bean.setPmaFailedPorts(buffer.getShort());
        bean.setTopoFailedPorts(buffer.getShort());
        return bean;
    }

    private ErrStatBean getErrStatBean(int position) {
        buffer.position(position);
        ErrSummaryBean esBean = new ErrSummaryBean();
        esBean.setIntegrityErrors(buffer.getInt());
        esBean.setCongestionErrors(buffer.getInt());
        esBean.setSmaCongestionErrors(buffer.getInt());
        esBean.setBubbleErrors(buffer.getInt());
        esBean.setSecurityErrors(buffer.getInt());
        esBean.setRoutingErrors(buffer.getInt());
        esBean.setUtilizationPct10(buffer.getShort() & 0xffff);
        esBean.setDiscardsPct10(buffer.getShort() & 0xffff);
        buffer.getInt(); // reserved
        buffer.getInt(); // reserved
        buffer.getInt(); // reserved
        ErrBucketBean[] ebBeans = new ErrBucketBean[PAConstants.PM_ERR_BUCKETS];
        for (int i = 0; i < ebBeans.length; i++) {
            ebBeans[i] = new ErrBucketBean();
            ebBeans[i].setIntegrityErrors(buffer.getInt());
            ebBeans[i].setCongestionErrors(buffer.getInt());
            ebBeans[i].setSmaCongestionErrors(buffer.getInt());
            ebBeans[i].setBubbleErrors(buffer.getInt());
            ebBeans[i].setSecurityErrors(buffer.getInt());
            ebBeans[i].setRoutingErrors(buffer.getInt());
        }

        ErrStatBean bean = new ErrStatBean(esBean, ebBeans);
        return bean;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public VFInfoBean toObject() {
        buffer.clear();

        VFInfoBean bean = new VFInfoBean();
        bean.setVfName(StringUtils.toString(buffer.array(),
                buffer.arrayOffset(), PAConstants.STL_PM_VFNAMELEN));
        buffer.position(72);
        ImageIdBean imageId =
                new ImageIdBean(buffer.getLong(), buffer.getInt());
        bean.setImageId(imageId);
        buffer.position(88);
        bean.setNumPorts(buffer.getInt());
        UtilStatsBean utilStat = getUtilStatsBean(92);
        bean.setInternalUtilStats(utilStat);
        ErrStatBean errStatBean = getErrStatBean(180);
        bean.setInternalErrors(errStatBean);
        buffer.position(340);
        bean.setMaxInternalRate(buffer.get());
        bean.setMinInternalRate(buffer.get());
        bean.setMaxInternalMBps(buffer.getInt());
        return bean;
    }

}
