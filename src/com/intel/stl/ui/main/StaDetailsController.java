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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;

import javax.swing.Timer;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.main.view.StaDetailsPanel;
import com.intel.stl.ui.model.NodeTypeViz;
import com.intel.stl.ui.model.StateShortTypeViz;

/**
 * per feedback we got, we do not show Router info here. we intentionally change
 * it on UI side rather than backend because we need to support it again in the
 * future
 */
public class StaDetailsController {
    private final NodeTypeViz[] nodeTypes;

    private final String name;

    private final DefaultCategoryDataset failedDataset;

    private final DefaultCategoryDataset skippedDataset;

    private final DefaultPieDataset typeDataset;

    private final StaDetailsPanel view;

    private Timer viewClearTimer;

    public StaDetailsController(String name, StaDetailsPanel view) {
        this.view = view;

        this.name = name;
        view.setNameLabel(name);

        failedDataset = new DefaultCategoryDataset();
        failedDataset.addValue(0, name, StateShortTypeViz.NORESP.name());
        view.setFailedDataset(failedDataset);

        skippedDataset = new DefaultCategoryDataset();
        skippedDataset.addValue(0, name, StateShortTypeViz.SKIPPED.name());
        view.setSkipedDataset(skippedDataset);

        typeDataset = new DefaultPieDataset();
        nodeTypes = view.getTypes();
        Color[] colors = new Color[nodeTypes.length];
        for (int i = 0; i < colors.length; i++) {
            typeDataset.setValue(nodeTypes[i].getName(), 0);
            colors[i] = nodeTypes[i].getColor();
        }
        view.setTypeDataset(typeDataset, colors);
    }

    /**
     * @return the view
     */
    public StaDetailsPanel getView() {
        return view;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the failedDataset
     */
    public DefaultCategoryDataset getFailedDataset() {
        return failedDataset;
    }

    /**
     * @return the skippedDataset
     */
    public DefaultCategoryDataset getSkippedDataset() {
        return skippedDataset;
    }

    /**
     * @return the typeDataset
     */
    public DefaultPieDataset getTypeDataset() {
        return typeDataset;
    }

    public void setStates(final long total, final long failed,
            final long skipped) {
        clearTimer();
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                failedDataset.setValue((double) failed / total, name,
                        StateShortTypeViz.NORESP.name());
                skippedDataset.setValue((double) skipped / total, name,
                        StateShortTypeViz.SKIPPED.name());
                String failedStr = UIConstants.INTEGER.format(failed);
                String skippedStr = UIConstants.INTEGER.format(skipped);

                view.setFailed(failedStr);
                view.setSkipped(skippedStr);
            }
        });
    }

    public void setTypes(int total, EnumMap<NodeType, Integer> types) {
        EnumMap<NodeType, Long> wrapper =
                new EnumMap<NodeType, Long>(NodeType.class);
        for (NodeType nodeType : types.keySet()) {
            wrapper.put(nodeType, (long) types.get(nodeType));
        }
        setTypes(total, wrapper);
    }

    public void setTypes(final long total, EnumMap<NodeType, Long> types) {
        clearTimer();
        final long[] counts = NodeTypeViz.getDistributionValues(types);

        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                String totalNumber = UIConstants.INTEGER.format(total);
                view.setTotalNumber(totalNumber);
                for (NodeTypeViz nodeType : nodeTypes) {
                    long count = counts[nodeType.ordinal()];
                    typeDataset.setValue(nodeType.getName(), count);

                    String number = UIConstants.INTEGER.format(count);
                    String label = getTypeString(count, nodeType);
                    view.setTypeInfo(nodeType, number, label);
                }
            }
        });
    }

    protected String getTypeString(long count, NodeTypeViz nodeType) {
        String label =
                count == 1 ? nodeType.getName() : nodeType.getPluralName();
        return label;
    }

    protected void clearTimer() {
        if (viewClearTimer != null) {
            if (viewClearTimer.isRunning()) {
                viewClearTimer.stop();
            }
            viewClearTimer = null;
        }
    }

    public void clear() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                failedDataset.clear();
                skippedDataset.clear();
                typeDataset.clear();
            }
        });
        if (viewClearTimer == null) {
            viewClearTimer =
                    new Timer(UIConstants.UPDATE_TIME, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (viewClearTimer != null) {
                                view.clear();
                            }
                        }
                    });
            viewClearTimer.setRepeats(false);
        }
        viewClearTimer.restart();
    }
}
