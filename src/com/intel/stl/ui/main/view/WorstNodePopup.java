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

package com.intel.stl.ui.main.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.Timer;

import org.jdesktop.swingx.JXHyperlink;

import com.intel.stl.api.StringUtils;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.event.JumpDestination;
import com.intel.stl.ui.model.NodeScore;

public class WorstNodePopup extends JPanel implements MouseListener {
    private static final long serialVersionUID = 5839148578351334245L;

    private JLabel header;

    private JPanel contentPanel;

    private JLabel lid;

    private JLabel score;

    private JLabel event;

    private JXHyperlink[] jumpBtns;

    private IWorstNodesListener listener;

    private NodeScore nodeScore;

    private Popup popup;

    private Timer hideTimer;

    /**
     * Description:
     * 
     * @param score
     */
    public WorstNodePopup() {
        super();
        initComponent();
        addMouseListener(this);
    }

    /**
     * @param listener
     *            the listener to set
     */
    public void setListener(IWorstNodesListener listener) {
        this.listener = listener;
    }

    protected void initComponent() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(UIConstants.INTEL_BLUE));

        header = createHeader();
        add(header, BorderLayout.NORTH);

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(UIConstants.INTEL_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 2, 2, 2);
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 1;
        gc.weightx = 0;
        JLabel label =
                ComponentFactory.getH5Label(STLConstants.K0026_LID.getValue(),
                        Font.BOLD);
        label.setHorizontalAlignment(JLabel.RIGHT);
        contentPanel.add(label, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx = 1;
        lid = ComponentFactory.getH5Label("", Font.PLAIN);
        contentPanel.add(lid, gc);

        gc.gridwidth = 1;
        gc.weightx = 0;
        label =
                ComponentFactory.getH5Label(
                        STLConstants.K0108_SCORE.getValue(), Font.BOLD);
        label.setHorizontalAlignment(JLabel.RIGHT);
        contentPanel.add(label, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx = 1;
        score = ComponentFactory.getH5Label("", Font.BOLD);
        contentPanel.add(score, gc);

        gc.gridwidth = 1;
        gc.weightx = 0;
        label =
                ComponentFactory.getH5Label(
                        STLConstants.K0674_EVENT_TYPE.getValue(), Font.BOLD);
        label.setHorizontalAlignment(JLabel.RIGHT);
        contentPanel.add(label, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx = 1;
        event = ComponentFactory.getH5Label("", Font.PLAIN);
        contentPanel.add(event, gc);

        gc.gridwidth = 1;
        gc.gridheight = JumpDestination.values().length;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.NORTH;
        label =
                ComponentFactory.getH5Label(
                        STLConstants.K1055_INSPECT.getValue(), Font.BOLD);
        label.setHorizontalAlignment(JLabel.RIGHT);
        contentPanel.add(label, gc);

        gc.weightx = 1;
        gc.gridheight = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.anchor = GridBagConstraints.CENTER;
        jumpBtns = new JXHyperlink[JumpDestination.values().length];
        for (int i = 0; i < jumpBtns.length; i++) {
            final JumpDestination dest = JumpDestination.values()[i];
            jumpBtns[i] = new JXHyperlink(new AbstractAction(dest.getName()) {
                private static final long serialVersionUID =
                        4612692450375442721L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (nodeScore != null && listener != null) {
                        listener.jumpTo(nodeScore.getLid(),
                                nodeScore.getType(), dest);
                        hidePopup();
                    }
                }

            });
            contentPanel.add(jumpBtns[i], gc);
        }
        add(contentPanel, BorderLayout.CENTER);
    }

    protected JLabel createHeader() {
        JLabel header = ComponentFactory.getH4Label("", Font.BOLD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
                UIConstants.INTEL_ORANGE));
        return header;
    }

    public void setNode(NodeScore nodeScore) {
        if (nodeScore == null) {
            return;
        }

        this.nodeScore = nodeScore;
        header.setText(nodeScore.getName());
        header.setIcon(nodeScore.getIcon());

        lid.setText(StringUtils.intHexString(nodeScore.getLid()));

        score.setText(UIConstants.DECIMAL.format(nodeScore.getScore()));
        score.setForeground(nodeScore.getColor());

        event.setText(nodeScore.getEventType().name());

        for (JXHyperlink btn : jumpBtns) {
            btn.setEnabled(nodeScore.isActive());
        }

        revalidate();
    }

    /**
     * @param popup
     *            the popup to set
     */
    public synchronized void setPopup(Popup popup) {
        // just in case it happens
        hidePopup();
        this.popup = popup;
    }

    protected synchronized void hidePopup() {
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (hideTimer != null) {
            if (hideTimer.isRunning()) {
                hideTimer.stop();
            }
            hideTimer = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (popup != null && !getVisibleRect().contains(e.getPoint())) {
            if (hideTimer == null) {
                ActionListener listener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (hideTimer != null) {
                            hidePopup();
                        }
                    }
                };
                hideTimer = new Timer(UIConstants.UPDATE_TIME, listener);
                hideTimer.setRepeats(false);
            }
            hideTimer.restart();
        }
    }
}
