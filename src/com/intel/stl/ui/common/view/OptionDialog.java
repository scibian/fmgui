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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.intel.stl.api.IAssistant;
import com.intel.stl.ui.common.Util;

public abstract class OptionDialog extends JDialog implements IAssistant {
    private static final long serialVersionUID = 4480551678945585493L;

    private final Object waitObj;

    protected JProgressBar progressBar;

    protected JPanel ctrPanel;

    // -------------- Option Buttons ---------------//
    private JButton okButton;

    private JButton cancelButton;

    protected int option;

    /**
     * 
     * Description:
     * 
     * @param owner
     * @param title
     */
    public OptionDialog(Component owner, String title, int optionType) {
        super(SwingUtilities.getWindowAncestor(owner), title);
        initComponent(optionType);
        waitObj = new Object();
    }

    protected void initComponent(int optionType) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        JComponent mainPanel = getMainComponent();
        panel.add(mainPanel, BorderLayout.CENTER);
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
        // progressBar.setVisible(false);
        panel.add(progressBar, BorderLayout.SOUTH);
        getContentPane().add(panel, BorderLayout.CENTER);

        ctrPanel = new JPanel();
        installButtons(ctrPanel, optionType);
        getContentPane().add(ctrPanel, BorderLayout.SOUTH);

        setSize();
        setLocationRelativeTo(getOwner());
    }

    /**
     * <i>Description:</i>
     * 
     */
    protected void setSize() {
        pack();
    }

    /**
     * <i>Description:</i>
     * 
     * @param ctrPanel2
     */
    protected void installButtons(JPanel ctrPanel, int optionType) {
        ctrPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        if (optionType == JOptionPane.OK_OPTION
                || optionType == JOptionPane.OK_CANCEL_OPTION) {
            okButton = new JButton("Ok");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onOk();
                    option = JOptionPane.OK_OPTION;
                    synchronized (waitObj) {
                        waitObj.notify();
                    }
                }
            });
            ctrPanel.add(okButton);
        }

        if (optionType == JOptionPane.CANCEL_OPTION) {
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onCancel();
                    option = JOptionPane.CANCEL_OPTION;
                    synchronized (waitObj) {
                        waitObj.notify();
                    }
                }
            });
            ctrPanel.add(cancelButton);
        }
    }

    /**
     * <i>Description:</i>
     * 
     */
    public void onCancel() {
    }

    /**
     * <i>Description:</i>
     * 
     */
    public void onOk() {
    }

    public void enableOk(boolean b) {
        okButton.setEnabled(b);
    }

    public void enableCancel(boolean b) {
        cancelButton.setEnabled(b);
    }

    /**
     * <i>Description:</i>
     * 
     * @return
     */
    protected abstract JComponent getMainComponent();

    public void showDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setVisible(true);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.IAssistant#getOption(java.lang.Throwable)
     */
    @Override
    public int getOption(Throwable error) {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Can not call from EDT!");
        }
        option = -1;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // progressBar.setVisible(false);
                progressBar.setString(null);
                setVisible(true);
            }
        });

        waitForInput();
        return option;
    }

    protected void waitForInput() {
        synchronized (waitObj) {
            while (option == -1) {
                try {
                    waitObj.wait();
                } catch (InterruptedException e) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.ILoginAssistant#startProgress()
     */
    @Override
    public void startProgress() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);
                repaint();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.ILoginAssistant#stopProgress()
     */
    @Override
    public void stopProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setIndeterminate(false);
                progressBar.setValue(100);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.ILoginAssistant#reportProgress(java.lang.String)
     */
    @Override
    public void reportProgress(final String note) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setString(note);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.ILoginAssistant#close()
     */
    @Override
    public void close() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                onClose();
                setVisible(false);
            }
        });
    }

    /**
     * 
     * <i>Description:</i> any work we intend to do before we close this dialog
     * 
     */
    protected abstract void onClose();
}
