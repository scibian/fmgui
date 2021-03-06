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

package com.intel.stl.ui.performance.item;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.performance.PortCounterSourceName;
import com.intel.stl.ui.performance.observer.IDataObserver;
import com.intel.stl.ui.performance.observer.IFieldObserver;
import com.intel.stl.ui.performance.observer.PCPacketObserver;
import com.intel.stl.ui.performance.observer.VFPCPacketObserver;
import com.intel.stl.ui.performance.provider.DataProviderName;
import com.intel.stl.ui.performance.provider.PortCounterFieldProvider;
import com.intel.stl.ui.performance.provider.VFPortCounterFieldProvider;

public class PCPacketItem extends PortCounterFieldItem {

    public PCPacketItem() {
        this(DEFAULT_DATA_POINTS);
    }

    public PCPacketItem(int maxDataPoints) {
        super(STLConstants.K0881_PC_PACKET.getValue(),
                STLConstants.K0881_PC_PACKET.getValue(),
                STLConstants.K0881_PC_PACKET.getValue(), maxDataPoints);
    }

    public PCPacketItem(PCPacketItem item) {
        super(item);
    }

    @Override
    protected void copyPreparation(
            AbstractPerformanceItem<PortCounterSourceName> item) {
        PortCounterFieldProvider provider = new PortCounterFieldProvider();
        PCPacketObserver packetObserver = new PCPacketObserver(this);
        registerDataProvider(DataProviderName.PORT, provider, packetObserver);

        VFPortCounterFieldProvider vfProvider =
                new VFPortCounterFieldProvider();
        VFPCPacketObserver packetVfObserver = new VFPCPacketObserver(this);
        registerDataProvider(DataProviderName.VF_PORT, vfProvider,
                packetVfObserver);
    }

    @Override
    protected void copyDataObserver(
            AbstractPerformanceItem<PortCounterSourceName> item) {
        String field = null;
        if (item.getSources() != null) {
            field = item.getSources()[0].getFieldName();
        }
        IDataObserver<?> curObserver = item.getCurrentObserver();

        if (curObserver != null) {
            DataType type = curObserver.getType();
            for (IDataObserver<?> observer : observers.values()) {
                observer.setType(type);
                if (field != null) {
                    if (observer instanceof PCPacketObserver) {
                        ((PCPacketObserver) observer).setFieldName(field);
                    } else {
                        ((VFPCPacketObserver) observer).setFieldName(field);
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.item.IPerformanceItem#copy()
     */
    @Override
    public IPerformanceItem<PortCounterSourceName> copy() {
        return new PCPacketItem(this);
    }

    public synchronized void setFieldName(String name) {
        for (PortCounterSourceName sn : sourceNames) {
            sn.setFieldName(name);
        }

        for (IDataObserver<?> observer : observers.values()) {
            if (observer instanceof IFieldObserver) {
                ((IFieldObserver) observer).setFieldName(name);
            }
        }

        HistoryType currentType = getHistoryType();
        if (currentType != null && currentType != HistoryType.CURRENT) {
            // force re-init current history type
            setHistoryType(currentType, true);
        }
    }

    @Override
    public void setSources(PortCounterSourceName[] sourceNames) {
        super.setSources(sourceNames);
        if (sameSources(sourceNames)) {
            onRefresh(null);
            return;
        } else {
            setFieldName(sourceNames[0].getFieldName());
        }
    }
}
