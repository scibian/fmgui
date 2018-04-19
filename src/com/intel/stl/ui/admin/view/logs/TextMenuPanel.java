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

package com.intel.stl.ui.admin.view.logs;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import com.intel.stl.ui.admin.impl.logs.ITextMenuListener;
import com.intel.stl.ui.admin.impl.logs.TextEvent;
import com.intel.stl.ui.admin.impl.logs.TextEventType;
import com.intel.stl.ui.common.view.TextMenuItem;
import com.intel.stl.ui.common.view.TextPopupUtil;
import com.intel.stl.ui.common.view.TextPopupUtil.ITextMenuAction;

/**
 * JPanel to hold the popup menu and listen for mouse events
 */
public class TextMenuPanel extends JPanel implements MouseListener {

    private static final long serialVersionUID = 3208426280088071528L;

    private final JPopupMenu menuText;

    private ITextMenuListener textListener;

    private String selectedText;

    private String clipboardContents;

    public TextMenuPanel(List<TextEventType> eventTypes) {
        super();
        menuText = createPopupMenu(eventTypes);
    }

    public void setTextMenuListener(ITextMenuListener listener) {
        textListener = listener;
    }

    protected JPopupMenu createPopupMenu(List<TextEventType> eventTypes) {

        // Use the text popup utility to create a popup menu with specific
        // actions for each menu option
        return TextPopupUtil.createPopupMenu(eventTypes, new ITextMenuAction() {

            @Override
            public Action createTextMenuAction(final byte eventId) {

                Action action = null;

                switch (TextEventType.getType(eventId)) {
                    case COPY:
                        action = new DefaultEditorKit.CopyAction();
                        break;

                    case PASTE:
                        action = new DefaultEditorKit.PasteAction();
                        break;

                    case HIGHLIGHT:
                        action = new AbstractAction() {
                            private static final long serialVersionUID =
                                    3808622111777476707L;

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // Fire the event for this menu selection
                                fireTextEvent(new TextEvent(
                                        TextEventType.getType(eventId),
                                        selectedText));
                            }
                        };
                        break;
                }

                return action;
            }
        });
    }

    protected void fireTextEvent(TextEvent event) {
        textListener.doAction(event);
    }

    protected void show(MouseEvent e) {
        menuText.show(e.getComponent(), e.getX(), e.getY());
    }

    public void setMenuEnable(TextEventType type, boolean b) {

        int i = 0;
        boolean found = false;
        Component components[] = menuText.getComponents();
        while (!found && i < components.length) {
            if ((components[i] instanceof TextMenuItem)
                    && (((TextMenuItem) components[i]).getTextEventType()
                            .equals(type))) {
                ((TextMenuItem) components[i]).setEnabled(b);
                found = true;
            }
            i++;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            show(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        JTextComponent component = (JTextComponent) e.getSource();

        if (e.getClickCount() > 1) {
            // If the source is a text field highlight all
            if (component instanceof JTextField) {
                JTextField txtfld = (JTextField) component;
                int endPosition = txtfld.getText().length();
                txtfld.select(endPosition, endPosition);
            }
        } else {

            // If the source is a text field highlight all
            if (component instanceof JTextField) {
                ((JTextField) component).selectAll();

            } else if (component instanceof JTextArea) {

                // Preserved for future development
                // Un-highlight selection; i.e. change the highlight to match
                // the rest of the highlighted text
                /*-
                 *  String key = textListener.getSelectedKey();
                    int start = textListener.getSelectionStart();
                    int end = textListener.getSelectionEnd();
                    textListener.unHighlightSelection(key, start, end);
                    textListener.setCurrentSelection("", 0, 0);
                 */

            }

            // Enable the menu if there's something to paste
            try {
                clipboardContents = (String) Toolkit.getDefaultToolkit()
                        .getSystemClipboard().getData(DataFlavor.stringFlavor);
                setMenuEnable(TextEventType.PASTE,
                        !clipboardContents.isEmpty());

            } catch (HeadlessException | UnsupportedFlavorException
                    | IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        JTextComponent component = (JTextComponent) e.getSource();
        selectedText = component.getSelectedText();

        int startPosition = component.getSelectionStart();
        int endPosition = component.getSelectionEnd();

        // Enable the menu if something is highlighted
        boolean menuEnabled = startPosition != endPosition;
        setMenuEnable(TextEventType.COPY, menuEnabled);
        setMenuEnable(TextEventType.HIGHLIGHT, menuEnabled);

        try {
            clipboardContents = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
            setMenuEnable(TextEventType.PASTE, !clipboardContents.isEmpty());
        } catch (HeadlessException | UnsupportedFlavorException
                | IOException e1) {
            e1.printStackTrace();
        }

        // under Linux and Mac isPopupTrigger is trigger by mousePressed
        // rather than mouseReleased
        if (e.isPopupTrigger()) {
            show(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
