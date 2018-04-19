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

package com.intel.stl.ui.configuration.view;

import java.awt.Component;

import javax.swing.JComponent;

import com.intel.stl.api.configuration.ResourceCategory;
import com.intel.stl.ui.configuration.HoQLifeBarChartController;
import com.intel.stl.ui.configuration.LFTHistogramController;
import com.intel.stl.ui.configuration.MTUByVLBarChartController;
import com.intel.stl.ui.configuration.MultiColumnCategoryController;
import com.intel.stl.ui.configuration.PropertyCategoryController;
import com.intel.stl.ui.configuration.PropertyGroupController;
import com.intel.stl.ui.configuration.SC2SLMTBarChartController;
import com.intel.stl.ui.configuration.SC2VLNTMTBarChartController;
import com.intel.stl.ui.configuration.SC2VLTMTBarChartController;
import com.intel.stl.ui.configuration.VLStallCountByVLBarChartController;
import com.intel.stl.ui.framework.AbstractView;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.model.DevicePropertyGroup;

public class DevicePropertyGroupPanel extends
        AbstractView<DevicePropertyGroup, PropertyGroupController> {
    private static final long serialVersionUID = 4539967617107147387L;

    private GroupPanel mainPanel;

    protected PropertyVizStyle style;

    /**
     * Description:
     * 
     * @param style
     */
    public DevicePropertyGroupPanel(PropertyVizStyle style) {
        super();
        this.style = style;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.framework.AbstractView#modelUpdateFailed(com.intel.stl
     * .ui.framework.AbstractModel, java.lang.Throwable)
     */
    @Override
    public void modelUpdateFailed(DevicePropertyGroup model, Throwable caught) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.framework.AbstractView#modelChanged(com.intel.stl.ui
     * .framework.AbstractModel)
     */
    @Override
    public void modelChanged(DevicePropertyGroup model) {
        mainPanel.setModel(model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.framework.AbstractView#getMainComponent()
     */
    @Override
    public JComponent getMainComponent() {
        if (mainPanel == null) {
            mainPanel = new GroupPanel(style);
        }
        return mainPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.framework.AbstractView#initComponents()
     */
    @Override
    public void initComponents() {
    }

    class GroupPanel extends
            PropertyGroupPanel<DevicePropertyCategory, DevicePropertyGroup> {
        private static final long serialVersionUID = 6635431613502243647L;

        /**
         * Description:
         * 
         * @param style
         */
        public GroupPanel(PropertyVizStyle style) {
            super(style);
        }

        @Override
        protected Component createCategoryPanel(
                DevicePropertyCategory category, PropertyVizStyle style) {
            Component categoryPanel;
            if (category.size() < 26) {
                if (category.getCategory() == ResourceCategory.LFT_HISTOGRAM) {
                    categoryPanel = new LFTHistogramPanel();
                    new LFTHistogramController(category,
                            (LFTHistogramPanel) categoryPanel, null);
                } else if (category.getCategory() == ResourceCategory.SC2SLMT_CHART) {
                    categoryPanel = new SC2SLMTBarChartPanel();
                    new SC2SLMTBarChartController(category,
                            (SC2SLMTBarChartPanel) categoryPanel, null);
                } else if (category.getCategory() == ResourceCategory.SC2VLTMT_CHART) {
                    categoryPanel = new SC2VLTMTBarChartPanel();
                    new SC2VLTMTBarChartController(category,
                            (SC2VLTMTBarChartPanel) categoryPanel, null);
                } else if (category.getCategory() == ResourceCategory.SC2VLNTMT_CHART) {
                    categoryPanel = new SC2VLNTMTBarChartPanel();
                    new SC2VLNTMTBarChartController(category,
                            (SC2VLNTMTBarChartPanel) categoryPanel, null);
                } else if (category.getCategory() == ResourceCategory.MTU_CHART) {
                    categoryPanel = new MTUByVLBarChartPanel();
                    new MTUByVLBarChartController(category,
                            (MTUByVLBarChartPanel) categoryPanel, null);
                } else if (category.getCategory() == ResourceCategory.HOQLIFE_CHART) {
                    categoryPanel = new HoQLifeBarChartPanel();
                    new HoQLifeBarChartController(category,
                            (HoQLifeBarChartPanel) categoryPanel, null);
                } else if (category.getCategory() == ResourceCategory.VL_STALL_CHART) {
                    categoryPanel = new VLStallCountByVLBarChartPanel();
                    new VLStallCountByVLBarChartController(category,
                            (VLStallCountByVLBarChartPanel) categoryPanel, null);
                } else {
                    categoryPanel = new DevicePropertyCategoryPanel(style);
                    new PropertyCategoryController(category,
                            (DevicePropertyCategoryPanel) categoryPanel, null);
                }
            } else {
                if (category.size() < 51) {
                    categoryPanel = new MultiColumnCategoryPanel(2, style);
                } else {
                    categoryPanel = new PagingCategoryPanel(2, 16, 5, style);
                }
                new MultiColumnCategoryController(category,
                        (MultiColumnCategoryPanel) categoryPanel, null);
            }
            return categoryPanel;
        }

    }

    /**
     * <i>Description:</i>
     * 
     * @param b
     */
    public void enableHelp(boolean b) {
        if (mainPanel == null) {
            // this shouldn't happen
            throw new RuntimeException("No panel initialized");
        }
        mainPanel.enableHelp(b);
    }

    /**
     * <i>Description:</i>
     * 
     * @return
     */
    public Component getHelpButton() {
        if (mainPanel == null) {
            // this shouldn't happen
            throw new RuntimeException("No panel initialized");
        }
        return mainPanel.getHelpButton();
    }
}
