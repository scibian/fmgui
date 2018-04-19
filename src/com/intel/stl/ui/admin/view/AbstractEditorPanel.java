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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.intel.stl.ui.admin.IItemEditorListener;
import com.intel.stl.ui.admin.InvalidEditException;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.SafeNameField;

public abstract class AbstractEditorPanel<E> extends JPanel {
    private static final long serialVersionUID = 6210030398358856720L;

    private JPanel namePanel;

    private JFormattedTextField nameField;

    private JButton helpBtn;

    private JPanel ctrPanel;

    private JButton saveBtn;

    private JButton resetBtn;

    private DocumentListener nameListener;

    private IItemEditorListener edtListener;

    public AbstractEditorPanel() {
        super();
        initComponent();
    }

    protected void initComponent() {
        setLayout(new BorderLayout(5, 5));

        JPanel panel = getNamePanel();
        add(panel, BorderLayout.NORTH);

        JComponent mainComp = getMainComponent();
        add(mainComp, BorderLayout.CENTER);

        panel = getControlPanel();
        add(panel, BorderLayout.SOUTH);
    }

    protected JPanel getNamePanel() {
        if (namePanel == null) {
            namePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
            JLabel nameLabel = ComponentFactory
                    .getH3Label(STLConstants.K2111_NAME.getValue(), Font.BOLD);
            namePanel.add(nameLabel);
            nameField = new SafeNameField(false);
            nameField.setColumns(32);
            namePanel.add(nameField);

            helpBtn = ComponentFactory
                    .getImageButton(UIImages.HELP_ICON.getImageIcon());
            helpBtn.setToolTipText(STLConstants.K0037_HELP.getValue());
            namePanel.add(helpBtn);

        }
        return namePanel;
    }

    public void enableHelp(boolean b) {
        if (helpBtn != null) {
            helpBtn.setEnabled(b);
        }
    }

    public JButton getHelpButton() {
        return helpBtn;
    }

    protected abstract JComponent getMainComponent();

    protected JPanel getControlPanel() {
        if (ctrPanel == null) {
            ctrPanel = new JPanel();
            ctrPanel.setOpaque(false);
            installButtons(ctrPanel);
        }
        return ctrPanel;
    }

    protected void installButtons(JPanel panel) {
        panel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        saveBtn = ComponentFactory
                .getIntelActionButton(STLConstants.K3010_SAVE.getValue());
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (edtListener != null) {
                    edtListener.onSave();
                }
            }
        });
        panel.add(saveBtn);

        resetBtn = ComponentFactory
                .getIntelActionButton(STLConstants.K1006_RESET.getValue());
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (edtListener != null) {
                    edtListener.onReset();
                }
            }
        });
        panel.add(resetBtn);
    }

    protected DocumentListener getNameListener() {
        if (nameListener == null) {
            nameListener = new DocumentListener() {

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * javax.swing.event.DocumentListener#insertUpdate(javax.swing
                 * .event.DocumentEvent)
                 */
                @Override
                public void insertUpdate(DocumentEvent e) {
                    Document doc = e.getDocument();
                    try {
                        updateName(doc.getText(0, doc.getLength()));
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * javax.swing.event.DocumentListener#removeUpdate(javax.swing
                 * .event.DocumentEvent)
                 */
                @Override
                public void removeUpdate(DocumentEvent e) {
                    Document doc = e.getDocument();
                    try {
                        updateName(doc.getText(0, doc.getLength()));
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * javax.swing.event.DocumentListener#changedUpdate(javax.swing
                 * .event.DocumentEvent)
                 */
                @Override
                public void changedUpdate(DocumentEvent e) {
                }

            };
        }
        return nameListener;
    }

    /**
     * <i>Description:</i>
     *
     * @param text
     */
    protected void updateName(String text) {
        if (edtListener != null) {
            edtListener.nameChanged(text);
        }
    }

    public void setEditorListener(IItemEditorListener listener) {
        edtListener = listener;
    }

    public void setItem(final Item<E> item, final Item<E>[] items) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                // start name listening after we set the item properly
                nameField.getDocument()
                        .removeDocumentListener(getNameListener());
                setItemName(item.getName());
                String[] appNames = new String[items.length];
                for (int i = 0; i < appNames.length; i++) {
                    appNames[i] = items[i].getName();
                }
                showItemObject(item.getObj(), appNames, item.isEditable());

                nameField.setEnabled(item.isEditable());
                saveBtn.setEnabled(item.isEditable());
                resetBtn.setEnabled(item.isEditable());
                nameField.requestFocusInWindow();
                nameField.getDocument().addDocumentListener(getNameListener());
            }
        });
    }

    public void selectItemName() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                nameField.selectAll();
            }
        });
    }

    protected void setItemName(String name) {
        nameField.setText(name);
    }

    protected abstract void showItemObject(E obj, String[] itemNames,
            boolean isEditable);

    public abstract void itemNameChanged(String oldName, String newName);

    public void clear() {
        nameField.getDocument().removeDocumentListener(getNameListener());
        setItemName(null);
        nameField.setEnabled(false);
        saveBtn.setEnabled(false);
        resetBtn.setEnabled(false);
    }

    protected String getCurrentName() {
        return nameField.getText();
    }

    /**
     *
     * <i>Description:</i> update an item with current content
     *
     * @param item
     */
    public void updateItem(Item<E> item) throws InvalidEditException {
        if (!isEditValid()) {
            throw new InvalidEditException(
                    UILabels.STL81053_INVALID_EDIT.getDescription());
        }
        item.setName(getCurrentName());
        updateItemObject(item.getObj());
    }

    protected abstract void updateItemObject(E obj);

    protected boolean isEditValid() {
        return nameField.isEditValid();
    }
}
