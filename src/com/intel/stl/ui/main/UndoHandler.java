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

import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.undo.UndoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIImages;

public class UndoHandler {
    private static final Logger log = LoggerFactory
            .getLogger(UndoHandler.class);

    private final ExUndoManager undoMgr;

    private final UndoAction undoAction;

    private final RedoAction redoAction;

    private final AtomicBoolean inProgress = new AtomicBoolean(false);

    public UndoHandler() {
        undoMgr = new ExUndoManager();
        undoAction = new UndoAction();
        redoAction = new RedoAction();
    }

    public void addUndoAction(UndoableSelection<?> edit) {
        if (!edit.isValid()) {
            log.warn("Invalid UndoableSelection " + edit);
            return;
        }

        undoMgr.addEdit(edit);
        log.info("ADD " + edit);
        update();
    }

    /**
     * @return the undoAction
     */
    public UndoAction getUndoAction() {
        return undoAction;
    }

    /**
     * @return the redoAction
     */
    public RedoAction getRedoAction() {
        return redoAction;
    }

    protected void update() {
        undoAction.setEnabled(undoMgr.hasUndo());
        redoAction.setEnabled(undoMgr.hasRedo());
    }

    public synchronized boolean isInProgress() {
        return inProgress.get();
    }

    class UndoAction extends AbstractAction {
        private static final long serialVersionUID = -4557051992945784935L;

        public UndoAction() {
            super(null, UIImages.UNDO.getImageIcon());
            setEnabled(false);
            putValue(Action.SHORT_DESCRIPTION,
                    STLConstants.K1007_UNDO.getValue());
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
         * )
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (UndoHandler.this) {
                inProgress.set(true);
                try {
                    if (undoMgr.canUndo()) {
                        undoMgr.undo();
                    }
                    update();
                } finally {
                    inProgress.set(false);
                }
            }
        }
    }

    class RedoAction extends AbstractAction {
        private static final long serialVersionUID = 7923924627643464462L;

        public RedoAction() {
            super(null, UIImages.REDO.getImageIcon());
            setEnabled(false);
            putValue(Action.SHORT_DESCRIPTION,
                    STLConstants.K1008_REDO.getValue());
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
         * )
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (UndoHandler.this) {
                inProgress.set(true);
                try {
                    if (undoMgr.canRedo()) {
                        undoMgr.redo();
                    }
                    update();
                } finally {
                    inProgress.set(false);
                }
            }
        }
    }

    class ExUndoManager extends UndoManager {
        private static final long serialVersionUID = 7210552571742712837L;

        public boolean hasUndo() {
            return editToBeUndone() != null;
        }

        public boolean hasRedo() {
            return editToBeRedone() != null;
        }
    }
}
