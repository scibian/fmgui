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

package com.intel.stl.ui.common.view;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;

/**
 * Extended JFormattedTextFiled that will show error hint on the TextField
 */
public class ExFormattedTextField extends JFormattedTextField {
    private static final long serialVersionUID = 6265122315036017331L;

    private Border orgBorder;

    private String orgTooltip;

    private String validationTooltip;

    /**
     * Description:
     *
     * @param formatter
     */
    public ExFormattedTextField(AbstractFormatter formatter) {
        super(formatter);
        init();
    }

    /**
     * Description:
     *
     * @param format
     */
    public ExFormattedTextField(Format format) {
        super(format);
        init();
    }

    protected void init() {
        AbstractFormatter formatter = getFormatter();
        if (formatter != null && formatter instanceof DefaultFormatter) {
            ((DefaultFormatter) formatter).setOverwriteMode(false);
        }

        // keep focus when we have invalid value
        setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JFormattedTextField ftf = (JFormattedTextField) input;
                return ftf.isEditValid();
            }
        });

        addPropertyChangeListener("editValid", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() == Boolean.TRUE) {
                    setBackground(UIConstants.INTEL_WHITE);
                    setBorder(orgBorder);
                    setToolTipText(orgTooltip);
                } else {
                    setBackground(UIConstants.INTEL_LIGHT_RED);

                    if (orgBorder == null) {
                        orgBorder = getBorder();
                    }
                    setBorder(BorderFactory
                            .createLineBorder(UIConstants.INTEL_RED, 2));

                    if (validationTooltip != null) {
                        if (orgTooltip == null) {
                            orgTooltip = getToolTipText();
                        }
                        setToolTipText(validationTooltip);
                        // show tooltip immediately
                        ToolTipManager.sharedInstance()
                                .mouseMoved(new MouseEvent(
                                        ExFormattedTextField.this, 0, 0, 0, 0,
                                        0, 0, false));
                    }
                }
            }
        });

        Util.makeUndoable(this);
    }

    /**
     * @param validationTooltip
     *            the validationTooltip to set
     */
    public void setValidationTooltip(String validationTooltip) {
        this.validationTooltip = validationTooltip;
    }

    public void setFixedFormatter(AbstractFormatter formatter) {
        setFormatterFactory(new DefaultFormatterFactory(formatter));
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (!b) {
            setBackground(UIConstants.INTEL_BACKGROUND_GRAY);
        } else {
            setBackground(UIConstants.INTEL_WHITE);
        }
    }

}
