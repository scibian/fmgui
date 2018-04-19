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

import static com.intel.stl.ui.common.STLConstants.K0621_CANCEL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.main.view.IProgressListener;

public class ProgressPanel extends JPanel {
    private static final long serialVersionUID = 1912574664805410774L;

    private JLabel label;

    private JLabel running;

    private JProgressBar progressBar;

    private JButton btnCancel;

    private final int margin = 20;

    private final IProgressListener progressListener;

    /**
     * Description:
     *
     * @param isIndeterminate
     */
    public ProgressPanel(boolean isIndeterminate,
            IProgressListener progressListener) {
        super();
        this.progressListener = progressListener;
        initComponent(isIndeterminate);
    }

    /**
     * Description:
     *
     */
    protected void initComponent(boolean isIndeterminate) {
        setOpaque(true);
        setBackground(new Color(255, 255, 255, 230));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.INTEL_BLUE, 2),
                BorderFactory.createEmptyBorder(margin, margin, margin,
                        margin)));

        setLayout(new BorderLayout(5, 5));
        label = ComponentFactory.getH2Label("", Font.PLAIN);
        add(label, BorderLayout.NORTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(UIConstants.INTEL_WHITE);
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        running = new JLabel();
        running.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panel.add(running, BorderLayout.WEST);

        progressBar = new JProgressBar(0, 100);
        progressBar.setUI(new BasicProgressBarUI());
        progressBar.setBorderPainted(false);
        if (isIndeterminate) {
            progressBar.setIndeterminate(true);
        }
        panel.add(progressBar, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);

        btnCancel =
                ComponentFactory.getIntelActionButton(K0621_CANCEL.getValue());
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (progressListener != null) {
                    progressListener.onCancel();
                }
            }

        });
        add(btnCancel, BorderLayout.EAST);
        // Initial state for the Cancel button
        btnCancel.setVisible(false);
    }

    /**
     *
     * Description:
     *
     * @param value
     *            progress value in range [0, 100]. a value of -1 will set
     *            progress bar to Indeterminate state
     */
    public void setProgress(int value) {
        if (value > 0 && progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        }
        progressBar.setValue(value);
    }

    public double getPercentComplete() {
        return progressBar.getPercentComplete();
    }

    public void setProgressNote(String text) {
        if (text != null) {
            if (!progressBar.isStringPainted()) {
                progressBar.setStringPainted(true);
            }
            progressBar.setString(text);
        } else if (text == null && progressBar.isStringPainted()) {
            progressBar.setStringPainted(false);
        }
    }

    public void setLabel(String text) {
        label.setText(text);
        running.setIcon(text == null ? null : UIImages.RUNNING.getImageIcon());
    }

    public void setCancellable(boolean cancellable) {
        btnCancel.setVisible(cancellable);
    }
}
