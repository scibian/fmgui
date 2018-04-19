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

package com.intel.stl.ui.common;

import java.util.List;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.common.view.ChartsView;
import com.intel.stl.ui.common.view.IChartsCardListener;
import com.intel.stl.ui.event.JumpDestination;
import com.intel.stl.ui.event.JumpToEvent;
import com.intel.stl.ui.event.PortsSelectedEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.model.DatasetDescription;
import com.intel.stl.ui.model.PortEntry;
import com.intel.stl.ui.performance.ChartArgument;

public class ChartsCard extends
        BaseCardController<IChartsCardListener, ChartsView> implements
        IChartsCardListener {
    private IPinDelegator pinDelegator;

    private UndoHandler undoHandler;

    private JumpToEvent origin;

    private String currentChart;

    public ChartsCard(ChartsView view, MBassador<IAppEvent> eventBus,
            List<DatasetDescription> datasets) {
        super(view, eventBus);
        view.setDatasets(datasets);
        if (datasets != null && !datasets.isEmpty()) {
            currentChart = datasets.get(0).getName();
        }
        view.setChart(currentChart);
    }

    /**
     * @param undoHandler
     *            the undoHandler to set
     */
    public void setUndoHandler(UndoHandler undoHandler, JumpToEvent source) {
        this.undoHandler = undoHandler;
        this.origin = source;
    }

    /**
     * @param origin
     *            the origin to set
     */
    public void setOrigin(JumpToEvent origin) {
        this.origin = origin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.main.view.IChartsCardListener#onSelectChart(int)
     */
    @Override
    public void onSelectChart(String name) {
        String oldChart = currentChart;
        currentChart = name;
        view.setChart(currentChart);

        if (undoHandler != null && !undoHandler.isInProgress()) {
            UndoableChartSelection undoSel =
                    new UndoableChartSelection(this, oldChart, name);
            undoHandler.addUndoAction(undoSel);
        }
    }

    public void selectChart(String name) {
        view.selectChart(name);
    }

    /**
     * @return the currentChart
     */
    public String getCurrentChart() {
        return currentChart;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseCardController#getCardListener()
     */
    @Override
    public IChartsCardListener getCardListener() {
        return this;
    }

    @Override
    public void jumpTo(Object content, JumpDestination destination) {
        if (content instanceof PortEntry) {
            PortEntry pe = (PortEntry) content;
            PortsSelectedEvent event =
                    new PortsSelectedEvent(pe.getNodeLid(), pe.getPortNum(),
                            this, destination.getName());
            eventBus.publish(event);

            if (undoHandler != null && !undoHandler.isInProgress()) {
                UndoableJumpEvent undoSel =
                        new UndoableJumpEvent(eventBus, origin, event);
                undoHandler.addUndoAction(undoSel);
            }
        }
    }

    /**
     * @param pinDelegator
     *            the pinDelegator to set
     */
    public void setPinDelegator(IPinDelegator pinDelegator) {
        this.pinDelegator = pinDelegator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseCardController#onPin()
     */
    @Override
    public void onPin() {
        if (pinDelegator != null) {
            PinArgument arg = getChartProperties();
            pinDelegator.addPin(currentChart, arg);
        }
    }

    protected PinArgument getChartProperties() {
        PinArgument res = new PinArgument();
        res.put(ChartArgument.NAME, currentChart);
        return res;
    }

}
