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

package com.intel.stl.api.performance.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.intel.stl.api.IRandomable;
import com.intel.stl.api.performance.CategoryBucketBean;
import com.intel.stl.api.performance.CategoryStatBean;
import com.intel.stl.api.performance.FocusPortsRspBean;
import com.intel.stl.api.performance.GroupInfoBean;
import com.intel.stl.api.performance.ImageInfoBean;
import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.api.performance.UtilStatsBean;
import com.intel.stl.api.performance.VFFocusPortsRspBean;
import com.intel.stl.api.performance.VFInfoBean;

public class Randomizer implements IRandomable {
    private final Random random;

    private boolean isActive;

    private long allBandwidth;

    private long allPacketRate;

    private int allPmaFailedPorts;

    private int allTopoFailedPorts;

    private long allCongestion;

    private long allSignalIntegrity;

    private long allSmaCongestion;

    private long allSecurity;

    private long allRouting;

    private long bandwidth;

    private long packetRate;

    private int pmaFailedPorts;

    private int topoFailedPorts;

    private long congestion;

    private long signalIntegrity;

    private long smaCongestion;

    private long security;

    private long routing;

    public Randomizer() {
        random = new Random();
        isActive = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.IRandomable#setSeed(long)
     */
    @Override
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.IRandomable#setRandom(boolean)
     */
    @Override
    public void setRandom(boolean b) {
        isActive = b;
    }

    private final List<ImageInfoBean> recentImageInfos =
            new ArrayList<ImageInfoBean>(4);

    public void randomImageInfo(ImageInfoBean imageInfo, int nodes) {
        if (!isActive) {
            return;
        }

        int index = recentImageInfos.indexOf(imageInfo);
        if (index >= 0) {
            ImageInfoBean last = recentImageInfos.get(index);
            imageInfo.setNumNoRespNodes(last.getNumNoRespNodes());
            imageInfo.setNumSkippedNodes(last.getNumSkippedNodes());
            imageInfo.setNumNoRespPorts(last.getNumNoRespPorts());
            imageInfo.setNumSkippedPorts(last.getNumSkippedPorts());
            return;
        }

        if (recentImageInfos.size() >= 4) {
            recentImageInfos.remove(0);
        }
        recentImageInfos.add(imageInfo);

        imageInfo.setNumNoRespNodes(random.nextInt(nodes) / 10);
        nodes -= imageInfo.getNumNoRespNodes();
        imageInfo.setNumSkippedNodes(random.nextInt(nodes) / 10);

        long ports = imageInfo.getNumHFIPorts() + imageInfo.getNumSwitchPorts();
        imageInfo.setNumNoRespPorts((long) (random.nextDouble() * ports / 10));
        ports -= imageInfo.getNumNoRespPorts();
        imageInfo.setNumSkippedPorts((long) (random.nextDouble() * ports / 10));

    }

    private final List<GroupInfoBean> recentGroupInfos =
            new ArrayList<GroupInfoBean>(4);

    public void randomGroupInfo(GroupInfoBean info) {
        if (!isActive) {
            return;
        }

        int index = recentGroupInfos.indexOf(info);
        if (index >= 0) {
            GroupInfoBean last = recentGroupInfos.get(index);
            info.setInternalUtilStats(last.getInternalUtilStats());
            info.setInternalCategoryStats(last.getInternalCategoryStats());
            return;
        }

        if (recentGroupInfos.size() >= 4) {
            recentGroupInfos.remove(0);
        }
        recentGroupInfos.add(info);

        boolean isAll = info.getGroupName().equals("All");

        UtilStatsBean internalUtil = info.getInternalUtilStats();
        if (isAll) {
            bandwidth = Math.max(bandwidth,
                    internalUtil.getTotalMBps() + random.nextInt(1000));
            allBandwidth = bandwidth;
            packetRate = Math.max(packetRate,
                    internalUtil.getTotalKPps() + random.nextInt(1000));
            allPacketRate = packetRate;
            if (Math.random() > 0.8) {
                allPmaFailedPorts = pmaFailedPorts = Math.max(pmaFailedPorts,
                        internalUtil.getPmaNoRespPorts() + random.nextInt(10));
                allTopoFailedPorts = topoFailedPorts = Math.max(topoFailedPorts,
                        internalUtil.getTopoIncompPorts() + random.nextInt(10));
            } else {
                allPmaFailedPorts = pmaFailedPorts = 0;
                allTopoFailedPorts = topoFailedPorts = 0;
            }
        } else {
            bandwidth = (long) (allBandwidth * random.nextDouble());
            packetRate = (long) (allPacketRate * random.nextDouble());
            pmaFailedPorts = (int) (allPmaFailedPorts * random.nextDouble());
            topoFailedPorts = (int) (allTopoFailedPorts * random.nextDouble());
        }
        internalUtil.setTotalMBps(bandwidth);
        internalUtil.setTotalKPps(packetRate);
        internalUtil.setPmaNoRespPorts(pmaFailedPorts);
        internalUtil.setTopoIncompPorts(topoFailedPorts);

        randomHistogram(internalUtil.getBwBucketsAsArray());

        CategoryStatBean errStat = info.getInternalCategoryStats();
        if (isAll) {
            congestion = Math.max(congestion,
                    errStat.getCategoryMaximums().getCongestion()
                            + random.nextInt(99));
            allCongestion = congestion;

            signalIntegrity = Math.max(signalIntegrity,
                    errStat.getCategoryMaximums().getIntegrityErrors()
                            + random.nextInt(99));
            allSignalIntegrity = signalIntegrity;

            smaCongestion = Math.max(smaCongestion,
                    errStat.getCategoryMaximums().getSmaCongestion()
                            + random.nextInt(99));
            allSmaCongestion = smaCongestion;

            security = Math.max(security,
                    errStat.getCategoryMaximums().getSecurityErrors()
                            + random.nextInt(99));
            allSecurity = security;

            routing = Math.max(routing,
                    errStat.getCategoryMaximums().getRoutingErrors()
                            + random.nextInt(99));
            allRouting = routing;
        } else {
            congestion = (int) (allCongestion * random.nextDouble());
            signalIntegrity = (int) (allSignalIntegrity * random.nextDouble());
            smaCongestion = (int) (allSmaCongestion * random.nextDouble());
            security = (int) (allSecurity * random.nextDouble());
            routing = (int) (allRouting * random.nextDouble());
        }
        errStat.getCategoryMaximums().setCongestion(congestion);
        errStat.getCategoryMaximums().setIntegrityErrors(signalIntegrity);
        errStat.getCategoryMaximums().setSmaCongestion(smaCongestion);
        errStat.getCategoryMaximums().setSecurityErrors(security);
        errStat.getCategoryMaximums().setRoutingErrors(routing);

        CategoryBucketBean[] ports = errStat.getPorts();
        int[] congestions = new int[ports.length];
        int[] integrities = new int[ports.length];
        int[] smaCongestions = new int[ports.length];
        int[] securities = new int[ports.length];
        int[] routings = new int[ports.length];
        for (int i = 0; i < ports.length; i++) {
            congestions[i] = ports[i].getCongestion();
            integrities[i] = ports[i].getIntegrityErrors();
            smaCongestions[i] = ports[i].getSmaCongestion();
            securities[i] = ports[i].getSecurityErrors();
            routings[i] = ports[i].getRoutingErrors();
        }
        randomHistogram(congestions);
        randomHistogram(integrities);
        randomHistogram(smaCongestions);
        randomHistogram(securities);
        randomHistogram(routings);
        for (int i = 0; i < ports.length; i++) {
            ports[i].setCongestion(congestions[i]);
            ports[i].setIntegrityErrors(integrities[i]);
            ports[i].setSmaCongestion(smaCongestions[i]);
            ports[i].setSecurityErrors(securities[i]);
            ports[i].setRoutingErrors(routings[i]);
        }
    }

    private final List<VFInfoBean> recentVFInfos = new ArrayList<VFInfoBean>(4);

    public void randomVFInfo(VFInfoBean info) {
        if (!isActive) {
            return;
        }

        int index = recentVFInfos.indexOf(info);
        if (index >= 0) {
            VFInfoBean last = recentVFInfos.get(index);
            info.setInternalUtilStats(last.getInternalUtilStats());
            info.setInternalCategoryStats(last.getInternalCategoryStats());
            return;
        }

        if (recentVFInfos.size() >= 4) {
            recentVFInfos.remove(0);
        }
        recentVFInfos.add(info);

        UtilStatsBean internalUtil = info.getInternalUtilStats();
        long bandwidth = internalUtil.getTotalMBps() + random.nextInt(1000);
        long packetRate = internalUtil.getTotalKPps() + random.nextInt(1000);
        internalUtil.setTotalMBps(bandwidth);
        internalUtil.setTotalKPps(packetRate);
        if (Math.random() > 0.8) {
            internalUtil.setPmaNoRespPorts(
                    internalUtil.getPmaNoRespPorts() + random.nextInt(10));
            internalUtil.setTopoIncompPorts(
                    internalUtil.getTopoIncompPorts() + random.nextInt(10));
        }

        randomHistogram(internalUtil.getBwBucketsAsArray());

        CategoryStatBean errStat = info.getInternalCategoryStats();
        long value = errStat.getCategoryMaximums().getCongestion()
                + random.nextInt(99);
        errStat.getCategoryMaximums().setCongestion(value);

        value = errStat.getCategoryMaximums().getIntegrityErrors()
                + random.nextInt(99);
        errStat.getCategoryMaximums().setIntegrityErrors(value);

        value = errStat.getCategoryMaximums().getSmaCongestion()
                + random.nextInt(99);
        errStat.getCategoryMaximums().setSmaCongestion(value);

        value = errStat.getCategoryMaximums().getSecurityErrors()
                + random.nextInt(99);
        errStat.getCategoryMaximums().setSecurityErrors(value);

        value = errStat.getCategoryMaximums().getRoutingErrors()
                + random.nextInt(99);
        errStat.getCategoryMaximums().setRoutingErrors(value);

        CategoryBucketBean[] ports = errStat.getPorts();
        int[] congestions = new int[ports.length];
        int[] integrities = new int[ports.length];
        int[] smaCongestions = new int[ports.length];
        int[] securities = new int[ports.length];
        int[] routings = new int[ports.length];
        for (int i = 0; i < ports.length; i++) {
            congestions[i] = ports[i].getCongestion();
            integrities[i] = ports[i].getIntegrityErrors();
            smaCongestions[i] = ports[i].getSmaCongestion();
            securities[i] = ports[i].getSecurityErrors();
            routings[i] = ports[i].getRoutingErrors();
        }
        randomHistogram(congestions);
        randomHistogram(integrities);
        randomHistogram(smaCongestions);
        randomHistogram(securities);
        randomHistogram(routings);
        for (int i = 0; i < ports.length; i++) {
            ports[i].setCongestion(congestions[i]);
            ports[i].setIntegrityErrors(integrities[i]);
            ports[i].setSmaCongestion(smaCongestions[i]);
            ports[i].setSecurityErrors(securities[i]);
            ports[i].setRoutingErrors(routings[i]);
        }
    }

    protected void randomHistogram(int[] counts) {
        int delta = 0;
        for (int i = 0; i < counts.length - 1; i++) {
            double tmp = (int) (counts[i] * random.nextDouble()
                    - delta * random.nextDouble());
            counts[i] -= (int) tmp;
            delta += (int) tmp;
        }
        counts[counts.length - 1] += delta;
    }

    protected void randomHistogram(Integer[] counts) {
        int delta = 0;
        for (int i = 0; i < counts.length - 1; i++) {
            double tmp = (int) (counts[i] * random.nextDouble()
                    - delta * random.nextDouble());
            counts[i] -= (int) tmp;
            delta += (int) tmp;
        }
        counts[counts.length - 1] += delta;
    }

    public void randomFocusPorts(List<FocusPortsRspBean> focusPorts) {
        if (!isActive) {
            return;
        }

        int[] values = new int[focusPorts.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = (int) (Math.random() * 100);
        }
        Arrays.sort(values);
        for (int i = 0; i < values.length; i++) {
            focusPorts.get(i).setValue(values[values.length - 1 - i]);
            int flag = random.nextInt(10);
            if (flag > 3) {
                flag = 0;
            }
            focusPorts.get(i).setLocalStatus((byte) flag);
        }
    }

    public void randomVFFocusPorts(List<VFFocusPortsRspBean> focusPort) {
        if (!isActive) {
            return;
        }

        int[] values = new int[focusPort.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = (int) (Math.random() * 100);
        }
        Arrays.sort(values);
        for (int i = 0; i < values.length; i++) {
            focusPort.get(i).setValue(values[values.length - 1 - i]);
            int flag = random.nextInt(10);
            if (flag > 3) {
                flag = 0;
            }
            focusPort.get(i).setLocalStatus((byte) flag);
        }
    }

    Map<Point, Long[]> counters = new HashMap<Point, Long[]>();

    public synchronized void randomPortCounters(PortCountersBean counter) {
        Point id = new Point(counter.getNodeLid(), counter.getPortNumber());
        Long[] last = counters.get(id);
        if (last == null) {
            counters.put(id, new Long[] { counter.getPortRcvData(),
                    counter.getPortXmitData() });
            return;
        }
        last[0] = getRandomTraffic(last[0]);
        counter.setPortRcvData(last[0]);
        last[1] = getRandomTraffic(last[1]);
        counter.setPortXmitData(last[1]);
    }

    private long getRandomTraffic(long val) {
        if (val < (0x01 << 30)) {
            val = 0x01 << 30;
        }
        long res = val + (long) Math.ceil(random.nextDouble() * (0x01L << 34));
        return res;
    }
}
