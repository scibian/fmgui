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

package com.intel.stl.ui.wizards.view.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.view.IntelComboBoxUI;

/**
 * Editor for the severity column of the Event Wizard table
 */
public class EventSeverityEditor extends DefaultCellEditor {

    private static final long serialVersionUID = -1390427182618639277L;

    private int selectedIndex;

    private final ITableListener tableModel;

    private final JComboBox<String> cbox;

    public EventSeverityEditor(JComboBox<String> cbox, ITableListener tblModel) {

        super(cbox);
        this.cbox = cbox;
        this.tableModel = tblModel;
        initComponents();
    }

    protected void initComponents() {
        cbox.setUI(new IntelComboBoxUI());
        cbox.setEditable(false);

        cbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>) e.getSource();
                selectedIndex = cb.getSelectedIndex();

                List<EventRule> eventRules = tableModel.getEventRules();
                if (tableModel.getSelectedRow() > -1) {
                    eventRules.get(tableModel.getSelectedRow())
                            .setEventSeverity(
                                    NoticeSeverity
                                            .getNoticeSeverity(selectedIndex));
                }

                tableModel.updateTable(eventRules);
            }
        });
    }

    /**
     * @return the selectedIndex
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

}
