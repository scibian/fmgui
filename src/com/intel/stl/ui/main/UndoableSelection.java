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

package com.intel.stl.ui.main;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UndoableSelection<S> extends AbstractUndoableEdit {
    private static final long serialVersionUID = 8781728280185363577L;

    private static final Logger log = LoggerFactory
            .getLogger(UndoableSelection.class);

    protected final S oldSelection, newSelection;

    private boolean isSignificant = true;

    /**
     * Description:
     * 
     * @param oldSelection
     * @param newSelection
     */
    public UndoableSelection(S oldSelection, S newSelection) {
        super();
        this.oldSelection = oldSelection;
        this.newSelection = newSelection;
    }

    public boolean isValid() {
        return oldSelection != newSelection
                && (newSelection == null || !newSelection.equals(oldSelection));
    }

    /**
     * @param isSignificant
     *            the isSignificant to set
     */
    public void setSignificant(boolean isSignificant) {
        this.isSignificant = isSignificant;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.AbstractUndoableEdit#isSignificant()
     */
    @Override
    public boolean isSignificant() {
        return isSignificant;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.AbstractUndoableEdit#undo()
     */
    @Override
    public synchronized void undo() throws CannotUndoException {
        super.undo();
        if (oldSelection != null) {
            log.info("UNDO " + oldSelection);
            execute(oldSelection);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.AbstractUndoableEdit#redo()
     */
    @Override
    public synchronized void redo() throws CannotRedoException {
        super.redo();
        if (newSelection != null) {
            log.info("REDO " + newSelection);
            execute(newSelection);
        }
    }

    protected abstract void execute(S selection);

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.AbstractUndoableEdit#canUndo()
     */
    @Override
    public boolean canUndo() {
        boolean res = super.canUndo() && canExecute(oldSelection);
        log.info("CAN_UNDO " + res);
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.AbstractUndoableEdit#canRedo()
     */
    @Override
    public boolean canRedo() {
        boolean res = super.canRedo() && canExecute(newSelection);
        log.info("CAN_REDO " + res);
        return res;
    }

    protected boolean canExecute(S selection) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UndoableSelection [isSignificant=" + isSignificant
                + ",\n  oldSelection=" + oldSelection + ",\n  newSelection="
                + newSelection + "]";
    }

}
