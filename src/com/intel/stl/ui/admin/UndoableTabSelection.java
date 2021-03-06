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

package com.intel.stl.ui.admin;

import com.intel.stl.ui.admin.impl.AdminPage;
import com.intel.stl.ui.common.IPageController;
import com.intel.stl.ui.main.UndoableSelection;

public class UndoableTabSelection extends UndoableSelection<IPageController> {
    private static final long serialVersionUID = -246900905809784765L;

    private final AdminPage controller;

    /**
     * Description:
     * 
     * @param oldSelection
     * @param newSelection
     * @param view
     */
    public UndoableTabSelection(AdminPage controller,
            IPageController oldSelection, IPageController newSelection) {
        super(oldSelection, newSelection);
        this.controller = controller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.main.UndoableSelection#execute(java.lang.Object)
     */
    @Override
    protected void execute(IPageController selection) {
        controller.selectPage(selection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.main.UndoableSelection#canExecute(java.lang.Object)
     */
    @Override
    protected boolean canExecute(IPageController selection) {
        if (selection == oldSelection) {
            return newSelection.canExit();
        } else {
            return oldSelection.canExit();
        }
    }

}
