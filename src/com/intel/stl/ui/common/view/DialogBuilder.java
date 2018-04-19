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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.main.view.FVMainFrame;

public class DialogBuilder {
    private String title;

    private JDialog dialog;

    private JPanel buttonsPanel;

    private JTextArea text;

    private JScrollPane jspane;

    private JLabel dialogIconLbl;

    private final Color bgColor = UIConstants.INTEL_WHITE;

    private JButton okBtn, cancelBtn;

    private int btnPressed = JOptionPane.NO_OPTION;

    //
    // Constructor to create dialog with one button
    //
    public DialogBuilder(java.awt.Component owner, String title, boolean modal,
            String btn0) {
        this(owner, title, modal, btn0, null);
    }

    // This is constructor for a modeless dialog
    // which should always be shown on top of its parent window.
    public DialogBuilder(String btn0) {
        dialog = new JDialog();

        createButtonsPanel(btn0, null);
        initComponents();

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModalityType(java.awt.Dialog.ModalityType.MODELESS);
        dialog.setIconImage(UIImages.LOGO_24.getImage());
        dialog.setPreferredSize(new Dimension(500, 300));
        dialog.pack();
    }

    public DialogBuilder(java.awt.Component owner, String title, boolean modal,
            String btn0, String btn1) {
        this.title = title;

        // Figure out the parent.
        if (owner instanceof JFrame) {
            dialog = new JDialog((JFrame) owner, title, modal);
            new MovingTogether((JFrame) owner, dialog);
        } else if (owner instanceof JDialog) {
            dialog = new JDialog((JDialog) owner, title, modal);
        } else {
            dialog = new JDialog();
            dialog.setTitle(title);
            dialog.setModal(modal);
            dialog.setIconImage(UIImages.LOGO_24.getImage());
            // System.out.println("PARENT IS NEITHER FRAME NOR DIALOG: " +
            // owner);
        }

        createButtonsPanel(btn0, btn1);
        initComponents();

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // TODO: think about modality type - important for multi-subnet view
        // scanario
        // APPLICATION_MODAL
        // DOCUMENT_MODAL
        // MODELESS
        // TOOLKIT_MODAL

        dialog.setPreferredSize(new Dimension(500, 300));
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
    }

    //
    // Constructor for password dialog
    //
    public DialogBuilder(java.awt.Component owner, String title, boolean modal,
            java.awt.Component showThisInDialog, String btn0, String btn1) {
        this.title = title;

        // Figure out the parent.
        if (owner instanceof JFrame) {
            dialog = new JDialog((JFrame) owner, title, modal);
            new MovingTogether((JFrame) owner, dialog);

            if (modal) {
                dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
            }
        } else if (owner instanceof JDialog) {
            dialog = new JDialog((JDialog) owner, title, modal);
        } else {
            dialog = new JDialog();
            dialog.setTitle(title);
            dialog.setModal(modal);
            dialog.setIconImage(UIImages.LOGO_24.getImage());
        }

        createButtonsPanel(btn0, btn1);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(showThisInDialog, BorderLayout.CENTER);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        content.add(buttonsPanel, BorderLayout.SOUTH);
        dialog.getContentPane().add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
    }

    public void initComponents() {

        Container container = dialog.getContentPane();
        container.setBackground(bgColor);
        container.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        dialogIconLbl = new JLabel();
        gc.insets = new Insets(20, 20, 20, 20);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        gc.weighty = 0;
        gc.gridwidth = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.NORTHWEST;
        container.add(dialogIconLbl, gc);

        jspane =
                new JScrollPane(getTextArea(),
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jspane.setBorder(BorderFactory.createEmptyBorder());
        jspane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        JViewport viewport = jspane.getViewport();
        viewport.setScrollMode(JViewport.BLIT_SCROLL_MODE);
        gc.insets = new Insets(20, 0, 20, 10);
        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.BOTH;
        container.add(jspane, gc);

        gc.insets = new Insets(0, 0, 5, 5);
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        gc.weighty = 0;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        container.add(buttonsPanel, gc);

    }

    protected void createButtonsPanel(String btn0, String btn1) {
        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder());
        buttonsPanel.setOpaque(false);

        if (btn1 != null) {
            // this is cancel button
            cancelBtn = ComponentFactory.getIntelCancelButton(btn1);
            cancelBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelAction();
                }
            });
            buttonsPanel.add(cancelBtn);
        }

        if (btn0 != null) {
            // this is ok button
            okBtn = ComponentFactory.getIntelActionButton(btn0);
            okBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // clear text
                    if (text != null) {
                        text.setText(null);
                    }
                    btnPressed = JOptionPane.YES_OPTION;
                    dialog.dispose();
                }
            });
            buttonsPanel.add(okBtn);
        }

        // If there are two buttons, make them same width.
        if (btn0 != null && btn1 != null) {
            JButton btnGroup[] = { okBtn, cancelBtn };
            ComponentFactory.makeSameWidthButtons(btnGroup);
        }

    }

    public void show() {
        refresh();

        // The following code ensures that the parent frame will be shown behind
        // the dialog in case when the dialog is shown while its parent
        // is in minimized state.
        if (!dialog.isVisible()) {

            if (dialog.getParent() != null
                    && dialog.getParent() instanceof JFrame) {
                JFrame parent = (JFrame) dialog.getParent();

                if (parent.getState() != Frame.NORMAL) {
                    parent.setState(Frame.NORMAL);
                }
                parent.setVisible(true);
                parent.toFront();
            }

            dialog.setLocationRelativeTo(dialog.getParent());
            dialog.setVisible(true);
        }

    }

    /**
     * 
     * <i>Description:</i> parent may move or has changed name, this method
     * ensure we recent to parent's current location, and have updated title
     * 
     */
    protected void refresh() {
        Container parent = dialog.getParent();
        if (parent != null) {
            String prefix = null;
            if (parent instanceof FVMainFrame) {
                prefix = ((FVMainFrame) parent).getSubnetName();
            } else if (parent instanceof Frame) {
                prefix = ((Frame) parent).getTitle();
            } else if (parent instanceof Dialog) {
                prefix = ((Dialog) parent).getTitle();
            }
            if (prefix != null) {
                dialog.setTitle(prefix + " " + title);
            }
        }
    }

    public int getButtonPressed() {
        return btnPressed;
    }

    public void cancelAction() {
        // User pressed cancel button
        btnPressed = JOptionPane.NO_OPTION;
        dialog.setVisible(false);
    }

    private JTextArea getTextArea() {
        text = new JTextArea();
        text.setEditable(false);
        PlainDocument plaindocument = new PlainDocument();
        text.setDocument(plaindocument);
        text.revalidate();
        text.setMargin(new Insets(0, 10, 0, 0));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        text.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        text.setRows(0);

        return text;

    }

    private int lastCaretPosition = 0;

    public void appendText(String str) {
        int currentCaretPosition = lastCaretPosition;

        if (text.getText().isEmpty()) {
            text.append(str);
        } else {
            text.append("\n" + str);
            // Advance caret position by one character
            currentCaretPosition += 1;
        }

        // Preserve caret position after appending text - to use on next append
        lastCaretPosition = text.getText().length();

        // Set caret to the value from last append (now stored in
        // currentCaretPosition local variable)
        text.setCaretPosition(currentCaretPosition);

        // DEBUG: remove
        // text.getCaret().setVisible(true);

        // All of the following code is to position text in the viewable
        // area of the viewport for this text area:
        Rectangle viewableArea;
        try {
            viewableArea = text.modelToView(text.getCaretPosition());
            text.scrollRectToVisible(viewableArea);

            // This code moves the newly appended text into the viewport's
            // viewable window.
            // If appended text is larger than viewable
            // area, the first line of appended text will show up at the top
            // line of the text area.
            // If appended text is smaller than viewable area, the first line
            // of appended text will show up somewhere in the viewable
            // rectangle, with last line of appended text being at the very
            // bottom of the viewable area.
            jspane.getViewport().setViewPosition(
                    new Point(viewableArea.x, viewableArea.y));

        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void setTitle(String title) {
        dialog.setTitle(title);
    }

    public JDialog getDialog() {
        return dialog;
    }

    public void setImageIcon(ImageIcon dlgIcon) {
        dialogIconLbl.setIcon(dlgIcon);
    }

    public void setText(String str) {
        text.setText(str);
    }

}

//
// Class to move the dialog in parent's coordinate space
// when parent gets moved around on the screen.
//
class MovingTogether extends ComponentAdapter {
    private Window parent, dialog;

    private int xDiff, yDiff;

    public MovingTogether(JFrame parent, JDialog dialog) {
        if (null != parent) {
            // Make sure we did not get here with null parent
            this.parent = parent;
            this.dialog = dialog;

            parent.addComponentListener(this);
            dialog.addComponentListener(this);
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        Window win = (Window) e.getComponent();
        if (win == parent && parent.isVisible()) {
            // Parent frame got moved. Make sure we move the dialog along with
            // the parent.
            dialog.setLocation(parent.getLocationOnScreen().x + xDiff,
                    parent.getLocationOnScreen().y + yDiff);
        } else if (parent.isVisible()) {
            // Dialog is the source of the event, it probably got moved.
            // Save the new delta(s) between parent and modal dialog
            // coordinates.
            Point location = dialog.getLocation();
            xDiff = location.x - parent.getLocationOnScreen().x;
            yDiff = location.y - parent.getLocationOnScreen().y;
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
        Window win = (Window) e.getComponent();
        if (win == parent) {
            // System.out.println("Parent is shown");
        } else if (parent.isVisible()) {
            Point location = dialog.getLocationOnScreen();
            // calculate x/y deltas with the parent dialog
            xDiff = location.x - parent.getLocationOnScreen().x;
            yDiff = location.y - parent.getLocationOnScreen().y;
        }
    }
}
