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

package com.intel.stl.ui.admin.view;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.admin.view.BlankView.BlankEditorPanel;
import com.intel.stl.ui.common.UIConstants;

public class BlankView extends AbstractConfView<Object, BlankEditorPanel> {
    private static final long serialVersionUID = 1264247778661597631L;

    public BlankView(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.view.AbstractConfView#createrEditorPanel()
     */
    @Override
    protected BlankEditorPanel createrEditorPanel() {
        return new BlankEditorPanel();
    }

    public static class BlankEditorPanel extends AbstractEditorPanel<Object> {
        private static final long serialVersionUID = 5486825273954504045L;

        private JPanel mainPanel;

        private JLabel label;

        /*
         * (non-Javadoc)
         *
         * @see com.intel.stl.ui.admin.view.AbstractEditorPanel#getMainPanel()
         */
        @Override
        protected JPanel getMainComponent() {
            if (mainPanel == null) {
                mainPanel = new JPanel();
                mainPanel.setBackground(UIConstants.INTEL_WHITE);
                label = new JLabel();
                mainPanel.add(label);
            }
            return mainPanel;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.intel.stl.ui.admin.view.AbstractEditorPanel#clear()
         */
        @Override
        public void clear() {
            label.setText(null);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.intel.stl.ui.admin.view.AbstractEditorPanel#showItemObject(java
         * .lang.Object, java.lang.String[], boolean)
         */
        @Override
        protected void showItemObject(Object app, String[] appNames,
                boolean isEditable) {
            label.setText(app.toString());
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.intel.stl.ui.admin.view.AbstractEditorPanel#itemNameChanged(java.
         * lang.String)
         */
        @Override
        public void itemNameChanged(String oldName, String newName) {
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.intel.stl.ui.admin.view.AbstractEditorPanel#updateItemObject(
         * java.lang.Object)
         */
        @Override
        protected void updateItemObject(Object obj) {
        }

        /*
         * (non-Javadoc)
         *
         * @see com.intel.stl.ui.admin.view.AbstractEditorPanel#isEditValid()
         */
        @Override
        public boolean isEditValid() {
            return true;
        }

    }
}
