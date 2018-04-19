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
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.Border;

import com.intel.stl.ui.common.PinArgument;
import com.intel.stl.ui.common.PinDescription;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;

/**
 * A decoration to any UI component to make it work on a PinBoard. This class
 * adds tittle, controller buttons (up, down and close) and an information
 * button that displays detailed information about the pin.
 */
public class DecoratedPinCardView extends JPanel implements MouseListener,
        MouseMotionListener {
    private static final long serialVersionUID = -6977548379083364984L;

    private static final boolean DEBUG = false;

    private static final String MAIN = "Main";

    private static final String INFO = "Info";

    private enum Resize {
        NONE,
        READY,
        START,
        DRAG
    };

    private JPanel titlePanel;

    private JLabel titleLabel;

    private JButton infoBtn;

    private JButton upBtn;

    private JButton downBtn;

    private JButton closeBtn;

    private JPanel sourcePanel;

    private JPanel mainPanel;

    private JPanel infoPanel;

    private final PinDescription pinDesc;

    private Resize resize = Resize.NONE;

    private int startHeight, startResizeY;

    private boolean isResizing = false;

    private final Border normalBorder;

    private final Border resizeBorder;

    public DecoratedPinCardView(Component comp, String title, PinDescription pin) {
        this.pinDesc = pin;
        setLayout(new BorderLayout());
        setOpaque(false);
        normalBorder =
                BorderFactory.createMatteBorder(3, 1, 2, 1,
                        UIConstants.INTEL_BORDER_GRAY);
        resizeBorder =
                BorderFactory.createLineBorder(UIConstants.INTEL_BLUE, 2);
        setBorder(normalBorder);

        JPanel titlePanel = getTitltPanel();
        add(titlePanel, BorderLayout.NORTH);

        mainPanel = new JPanel(new CardLayout());
        mainPanel.setOpaque(false);

        Map<String, String> sourceDesc =
                pinDesc.getArgument().getSourceDescription();
        if (sourceDesc == null) {
            mainPanel.add(comp, MAIN);
        } else {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);
            JPanel sourcePanel = getSourcePanel(sourceDesc);
            panel.add(sourcePanel, BorderLayout.NORTH);

            panel.add(comp);
            mainPanel.add(panel, MAIN);
        }

        JPanel infoPanel = getInfoPanel();
        JScrollPane pane = new JScrollPane(infoPanel);
        pane.getViewport().setBackground(UIConstants.INTEL_WHITE);
        mainPanel.add(pane, INFO);
        add(mainPanel, BorderLayout.CENTER);

        setTitle(title);
        setDescription(pin.getDescription());

        Dimension compDim = comp.getPreferredSize();
        Dimension prefDim =
                new Dimension(compDim.width, compDim.height
                        + titlePanel.getPreferredSize().height);
        setPreferredSize(prefDim);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    protected JPanel getTitltPanel() {
        if (titlePanel == null) {
            titlePanel = new JPanel(new BorderLayout());
            titlePanel.setOpaque(false);
            titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
                    UIConstants.INTEL_ORANGE));

            titleLabel = ComponentFactory.getH4Label("", Font.PLAIN);
            titlePanel.add(titleLabel, BorderLayout.CENTER);

            JToolBar toolbar = new JToolBar();
            toolbar.setFloatable(false);
            toolbar.setBackground(UIConstants.INTEL_WHITE);
            installButtons(toolbar);
            titlePanel.add(toolbar, BorderLayout.EAST);
        }
        return titlePanel;
    }

    protected void installButtons(JToolBar toolbar) {
        if (!pinDesc.getArgument().isEmpty()) {
            infoBtn =
                    ComponentFactory.getImageButton(UIImages.INFORMATION_ICON
                            .getImageIcon());
            infoBtn.setSelected(false);
            infoBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showInfo();
                }
            });
            toolbar.add(infoBtn);
        }

        upBtn = new JButton(UIImages.GO_UP.getImageIcon());
        upBtn.setOpaque(false);
        upBtn.setToolTipText(STLConstants.K4001_GO_UP.getValue());
        upBtn.setEnabled(false);
        toolbar.add(upBtn);

        downBtn = new JButton(UIImages.GO_DOWN.getImageIcon());
        downBtn.setOpaque(false);
        downBtn.setToolTipText(STLConstants.K4002_GO_DOWN.getValue());
        downBtn.setEnabled(false);
        toolbar.add(downBtn);

        closeBtn = new JButton(UIImages.CLOSE_RED.getImageIcon());
        closeBtn.setOpaque(false);
        closeBtn.setToolTipText(STLConstants.K4003_UNPIN.getValue());
        closeBtn.setRolloverIcon(UIImages.CLOSE_RED.getImageIcon());
        toolbar.add(closeBtn);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setTitle(String title, Icon icon) {
        titleLabel.setText(title);
        titleLabel.setIcon(icon);
    }

    public void setDescription(String description) {
        titleLabel.setToolTipText(description);
    }

    protected JPanel getSourcePanel(Map<String, String> sourceDesc) {
        if (sourcePanel == null) {
            sourcePanel = new JPanel();
            LayoutManager layout = new BoxLayout(sourcePanel, BoxLayout.Y_AXIS);
            sourcePanel.setLayout(layout);
            sourcePanel.setOpaque(false);
            sourcePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(1, 5, 0, 5), BorderFactory
                            .createLineBorder(UIConstants.INTEL_BORDER_GRAY)));

            for (String key : sourceDesc.keySet()) {
                JPanel panel =
                        new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
                // panel.setOpaque(false);
                JLabel label = ComponentFactory.getH6Label(key, Font.BOLD);
                label.setHorizontalAlignment(JLabel.RIGHT);
                panel.add(label);

                label =
                        ComponentFactory.getH6Label(sourceDesc.get(key),
                                Font.PLAIN);
                // label.setToolTipText(sourceDesc.get(key));
                panel.add(label);
                sourcePanel.add(panel);
            }
        }
        return sourcePanel;
    }

    public void enableUpButton(boolean b) {
        upBtn.setEnabled(b);
    }

    public void enableDownButton(boolean b) {
        downBtn.setEnabled(b);
    }

    public void setUpAction(ActionListener listener) {
        upBtn.addActionListener(listener);
    }

    public void setDownAction(ActionListener listener) {
        downBtn.addActionListener(listener);
    }

    public void setCloseAction(ActionListener listener) {
        closeBtn.addActionListener(listener);
    }

    protected JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel(new CardLayout());
            mainPanel.setOpaque(false);
        }
        return mainPanel;
    }

    protected JPanel getInfoPanel() {
        if (infoPanel == null) {
            infoPanel = new JPanel(new GridBagLayout());
            infoPanel.setOpaque(false);

            PinArgument props = pinDesc.getArgument();
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(2, 2, 2, 2);
            if (props.isEmpty()) {
                gc.weightx = 1;
                gc.weighty = 1;
                gc.fill = GridBagConstraints.BOTH;
                JLabel label =
                        ComponentFactory.getH4Label(
                                STLConstants.K0039_NOT_AVAILABLE.getValue(),
                                Font.BOLD);
                infoPanel.add(label, gc);
            } else {
                for (String name : props.stringPropertyNames()) {
                    gc.gridwidth = 1;
                    JLabel label = ComponentFactory.getH4Label(name, Font.BOLD);
                    label.setHorizontalAlignment(JLabel.RIGHT);
                    infoPanel.add(label, gc);

                    gc.gridwidth = GridBagConstraints.REMAINDER;
                    label =
                            ComponentFactory.getH4Label(
                                    props.getProperty(name), Font.PLAIN);
                    label.setHorizontalAlignment(JLabel.LEADING);
                    infoPanel.add(label, gc);
                }
            }
            gc.weighty = 1;
            gc.fill = GridBagConstraints.BOTH;
            infoPanel.add(Box.createGlue(), gc);
        }
        return infoPanel;
    }

    protected void showInfo() {
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        if (infoBtn.isSelected()) {
            layout.show(mainPanel, MAIN);
            infoBtn.setSelected(false);
        } else {
            layout.show(mainPanel, INFO);
            infoBtn.setSelected(true);
        }
        repaint();
    }

    public int getComponentHeight() {
        return getSize().height - titlePanel.getSize().height;
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
        if (resize == Resize.READY) {
            resize = Resize.START;
            startResizeY = e.getYOnScreen();
            startHeight = getPreferredSize().height;
            setBorder(resizeBorder);
        }
        if (DEBUG) {
            System.out.println(System.identityHashCode(this) + " mousePressed "
                    + resize);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (resize == Resize.START) {
            int newHeight = e.getYOnScreen() - startResizeY + startHeight;
            adjustHeight(newHeight);
            setBorder(normalBorder);
        }
        resize = Resize.NONE;
        setCursor(Cursor.getDefaultCursor());
        if (DEBUG) {
            System.out.println(System.identityHashCode(this)
                    + " mouseReleased " + resize);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (DEBUG) {
            System.out.println(System.identityHashCode(this) + " mouseEntered "
                    + resize);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (resize != Resize.START) {
            resize = Resize.NONE;
            setCursor(Cursor.getDefaultCursor());
            if (getBorder() != normalBorder) {
                setBorder(normalBorder);
            }
        }
        if (DEBUG) {
            System.out.println(System.identityHashCode(this) + " mouseExited "
                    + resize);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
     * )
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (resize != Resize.START) {
            if (DEBUG) {
                System.out.println(System.identityHashCode(this)
                        + " mouseDragged " + resize);
            }
            return;
        }

        if (!isResizing) {
            isResizing = true;
            int newHeight = e.getYOnScreen() - startResizeY + startHeight;
            adjustHeight(newHeight);
            isResizing = false;
        }
        if (DEBUG) {
            System.out.println(System.identityHashCode(this) + " mouseDragged "
                    + resize);
        }
    }

    protected void adjustHeight(int newHeight) {
        if (DEBUG) {
            System.out.println(System.identityHashCode(this) + " adjustHeight "
                    + newHeight);
        }
        Dimension dim = getPreferredSize();
        dim.height = newHeight;
        setPreferredSize(dim);
        getParent().revalidate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (resize != Resize.NONE) {
            if (DEBUG) {
                System.out.println(System.identityHashCode(this)
                        + " mouseMoved " + resize);
            }
            return;
        }

        if (DEBUG) {
            System.out.println(e.getPoint() + " " + getHeight() + " "
                    + System.identityHashCode(e.getSource()));
        }
        int delta = e.getPoint().y - getHeight();
        if (delta > -10 && delta < 10) {
            setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            resize = Resize.READY;
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
        if (DEBUG) {
            System.out.println(System.identityHashCode(this) + " mouseMoved "
                    + resize);
        }
    }

}
