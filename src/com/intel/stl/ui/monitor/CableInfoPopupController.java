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

package com.intel.stl.ui.monitor;

import static com.intel.stl.ui.model.DeviceProperty.CABLE_CC_BASE;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_CC_EXT;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_CONNECTOR;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_COPPER_LEN;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_DATE_CODE;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_DEVICE_TECH;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_EXT_MODULE;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_ID;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_LOT_CODE;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_MAXCASE_TEMP;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_MEM_PAGE01_PROV;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_MEM_PAGE02_PROV;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_NOMINAL_BR;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_OM1_LEN;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_OM2_LEN;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_OM3_LEN;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_OPTICAL_WL;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_POWER_CLASS;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_RX_CDR_ON_OFF_CTRL;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_RX_CDR_SUP;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_RX_OUTP_AMPL_FIX_PROG;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_RX_OUTP_EMPH_FIX_PROG;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_SMF_LEN;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_TX_CDR_ON_OFF_CTRL;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_TX_CDR_SUP;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_TX_INP_EQ_AUTO_ADP;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_TX_INP_EQ_FIX_PROG;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_VENDOR_NAME;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_VENDOR_OUI;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_VENDOR_PN;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_VENDOR_REV;
import static com.intel.stl.ui.model.DeviceProperty.CABLE_VENDOR_SN;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.configuration.PropertyCategory;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.configuration.CableInfoProcessor;
import com.intel.stl.ui.configuration.CategoryProcessorContext;
import com.intel.stl.ui.configuration.ResourceCategoryMap;
import com.intel.stl.ui.configuration.view.PropertyGroupPanel;
import com.intel.stl.ui.configuration.view.PropertyVizStyle;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.model.DevicePropertyGroup;
import com.intel.stl.ui.model.DevicePropertyItem;
import com.intel.stl.ui.model.PropertySet;
import com.intel.stl.ui.monitor.view.CableInfoPopupView;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.CancellableCall;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.SingleTaskManager;

/**
 * Controller for the Cable Info popup on the Connectivity Table
 */
public class CableInfoPopupController implements ICableInfoListener {

    private final static Logger log =
            LoggerFactory.getLogger(CableInfoPopupController.class);

    private Context context;

    private DevicePropertyCategory category;

    private final CableInfoProcessor cableInfoProcessor;

    private final SingleTaskManager taskMgr;

    private List<DevicePropertyItem> itemList;

    private final PropertyVizStyle style = new PropertyVizStyle(true, false);

    private final CableInfoPopupView view;

    public CableInfoPopupController(CableInfoPopupView view) {

        this.cableInfoProcessor = new CableInfoProcessor();
        this.taskMgr = new SingleTaskManager();
        this.view = view;
    }

    public void setContext(Context context, IProgressObserver observer) {
        this.context = context;
    }

    protected PropertySet<DevicePropertyGroup> updateModel(
            List<DevicePropertyItem> itemList) {

        PropertySet<DevicePropertyGroup> cableInfoModel = null;

        // Update the simple property category
        PropertyCategory propCategory = new PropertyCategory();
        propCategory.setResourceCategory(
                ResourceCategoryMap.CABLE_INFO.getResourceCategory());
        DevicePropertyCategory cat = new DevicePropertyCategory(propCategory);

        if (itemList == null) {
            final String NA = STLConstants.K0039_NOT_AVAILABLE.getValue();
            cat.addPropertyItem(new DevicePropertyItem(CABLE_ID, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_POWER_CLASS, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_TX_CDR_SUP, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_RX_CDR_SUP, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_CONNECTOR, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_NOMINAL_BR, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_SMF_LEN, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_OM3_LEN, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_OM2_LEN, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_OM1_LEN, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_COPPER_LEN, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_DEVICE_TECH, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_VENDOR_NAME, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_EXT_MODULE, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_VENDOR_OUI, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_VENDOR_PN, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_VENDOR_REV, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_OPTICAL_WL, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_MAXCASE_TEMP, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_CC_BASE, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_CC_BASE, NA));
            cat.addPropertyItem(
                    new DevicePropertyItem(CABLE_TX_INP_EQ_AUTO_ADP, NA));
            cat.addPropertyItem(
                    new DevicePropertyItem(CABLE_TX_INP_EQ_FIX_PROG, NA));
            cat.addPropertyItem(
                    new DevicePropertyItem(CABLE_RX_OUTP_EMPH_FIX_PROG, NA));
            cat.addPropertyItem(
                    new DevicePropertyItem(CABLE_RX_OUTP_AMPL_FIX_PROG, NA));
            cat.addPropertyItem(
                    new DevicePropertyItem(CABLE_TX_CDR_ON_OFF_CTRL, NA));
            cat.addPropertyItem(
                    new DevicePropertyItem(CABLE_RX_CDR_ON_OFF_CTRL, NA));
            cat.addPropertyItem(
                    new DevicePropertyItem(CABLE_MEM_PAGE02_PROV, NA));
            cat.addPropertyItem(
                    new DevicePropertyItem(CABLE_MEM_PAGE01_PROV, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_VENDOR_SN, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_DATE_CODE, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_LOT_CODE, NA));
            cat.addPropertyItem(new DevicePropertyItem(CABLE_CC_EXT, NA));
        } else {
            cat.setList(itemList);
        }

        // Create a property group and add the category
        DevicePropertyGroup propertyGroup = new DevicePropertyGroup();
        propertyGroup.addPropertyCategory(cat);

        // Add the property group to the model
        cableInfoModel = new PropertySet<DevicePropertyGroup>();
        cableInfoModel.addPropertyGroup(propertyGroup);

        // Update the Property Group Panel
        PropertyGroupPanel<DevicePropertyCategory, DevicePropertyGroup> groupPanel =
                new PropertyGroupPanel<DevicePropertyCategory, DevicePropertyGroup>(
                        style);

        // Remove the group panel and update the new one
        for (DevicePropertyGroup group : cableInfoModel.getGroups()) {
            groupPanel.setModel(group);
            groupPanel.enableHelp(false);
        }

        return cableInfoModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.ICableInfoListener#onCableInfoSelection(long,
     * short)
     */
    @Override
    public void onCableInfoSelection(final int lid, final long portGuid,
            final short portNum, final NodeType nodeType) {

        CancellableCall<Void> caller = new CancellableCall<Void>() {
            @Override
            public Void call(ICancelIndicator cancelIndicator)
                    throws Exception {

                // Show popup with all values set to N/A
                PropertySet<DevicePropertyGroup> cableInfoModel =
                        updateModel(null);
                view.updatePopup(cableInfoModel, false);

                CategoryProcessorContext categoryCtx =
                        new CategoryProcessorContext(lid, portNum, context);
                PropertyCategory propCategory = new PropertyCategory();
                propCategory.setResourceCategory(
                        ResourceCategoryMap.CABLE_INFO.getResourceCategory());
                category = new DevicePropertyCategory(propCategory);

                cableInfoProcessor.process(categoryCtx, category);

                return null;
            }
        };

        ICallback<Void> callback = new CallbackAdapter<Void>() {
            /*
             * (non-Javadoc)
             *
             * @see com.intel.stl.ui.publisher.CallbackAdapter#onDone(java .lang
             * .Object )
             */
            @Override
            public void onDone(Void result) {
                if (category != null) {
                    itemList = category.getList();

                    PropertySet<DevicePropertyGroup> cableInfoModel =
                            updateModel(itemList);

                    // Sleep .5sec to eliminate the flicker
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }

                    view.updatePopup(cableInfoModel, true);
                }
            }
        };
        taskMgr.submit(caller, callback);
    }
}
