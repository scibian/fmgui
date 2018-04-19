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

package com.intel.stl.ui.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

public class ExComboBoxModel<E> extends DefaultComboBoxModel<E> {
    private static final long serialVersionUID = 386462244488142481L;

    private final List<E> disabledItems = new ArrayList<E>();

    private final Set<Integer> disabledItemIds = new HashSet<Integer>();

    private final boolean adjustSelection;

    /**
     * Description:
     *
     */
    public ExComboBoxModel(boolean adjustSelection) {
        super();
        this.adjustSelection = adjustSelection;
    }

    /**
     * Description:
     *
     * @param items
     */
    public ExComboBoxModel(E[] items, boolean adjustSelection) {
        super(items);
        this.adjustSelection = adjustSelection;
    }

    /**
     * Description:
     *
     * @param v
     */
    public ExComboBoxModel(Vector<E> v, boolean adjustSelection) {
        super(v);
        this.adjustSelection = adjustSelection;
    }

    @SuppressWarnings("unchecked")
    public void setDisabledItem(E... items) {
        disabledItems.clear();
        disabledItemIds.clear();
        if (items == null || items.length == 0) {
            return;
        }

        disabledItems.addAll(Arrays.asList(items));
        chacheDisabledIndices();
        adjustSelection();
    }

    public void addDisabledItem(E item) {
        if (disabledItems.contains(item)) {
            return;
        }

        disabledItems.add(item);
        int index = getIndexOf(item);
        disabledItemIds.add(index);
        adjustSelection();
    }

    public void removeDisabledItem(E item) {
        if (disabledItems.remove(item)) {
            int index = getIndexOf(item);
            disabledItemIds.remove(index);
        }
    }

    public E getFirstAvailableItem() {
        for (int i = 0; i < getSize(); i++) {
            if (!disabledItemIds.contains(i)) {
                return getElementAt(i);
            }
        }
        return null;
    }

    protected void adjustSelection() {
        if (!adjustSelection) {
            return;
        }

        Object selected = getSelectedItem();
        if (selected == null) {
            return;
        }

        // shift to next avaliable one. if reach the end, use the first
        // available one
        E firstAvaiable = null;
        for (E item : disabledItems) {
            if (selected.equals(item)) {
                boolean foundDisabledItem = false;
                for (int i = 0; i < getSize(); i++) {
                    E element = getElementAt(i);

                    if (firstAvaiable == null && !disabledItemIds.contains(i)) {
                        firstAvaiable = element;
                    }

                    if (!foundDisabledItem) {
                        foundDisabledItem =
                                element != null && element.equals(item);
                    } else if (!disabledItemIds.contains(i)) {
                        setSelectedItem(element);
                        return;
                    }
                }
                setSelectedItem(firstAvaiable);
                return;
            }
        }
    }

    protected void chacheDisabledIndices() {
        disabledItemIds.clear();
        for (int i = 0; i < getSize(); i++) {
            E element = getElementAt(i);
            for (E item : disabledItems) {
                if (element != null && element.equals(item)) {
                    disabledItemIds.add(i);
                }
            }
        }
    }

    public boolean isDisabled(int index) {
        return disabledItemIds.contains(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.DefaultComboBoxModel#removeElementAt(int)
     */
    @Override
    public void removeElementAt(int index) {
        E element = getElementAt(index);
        disabledItems.remove(element);
        super.removeElementAt(index);
        chacheDisabledIndices();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.DefaultComboBoxModel#removeElement(java.lang.Object)
     */
    @Override
    public void removeElement(Object anObject) {
        disabledItems.remove(anObject);
        super.removeElement(anObject);
        chacheDisabledIndices();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.DefaultComboBoxModel#removeAllElements()
     */
    @Override
    public void removeAllElements() {
        disabledItems.clear();
        disabledItemIds.clear();
        super.removeAllElements();
    }

    /**
     * <i>Description:</i>This method is created to allow Squish process to
     * access this member.
     *
     * @return disabledItems list.
     */
    public List<E> getDisabledItems() {
        return disabledItems;
    }
}
