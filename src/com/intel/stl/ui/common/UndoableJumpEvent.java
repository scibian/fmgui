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

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.event.JumpToEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.UndoableSelection;

public class UndoableJumpEvent extends UndoableSelection<JumpToEvent> {
    private static final long serialVersionUID = 3434708668219629420L;

    private final MBassador<IAppEvent> eventBus;

    /**
     * Description:
     * 
     * @param oldSelection
     * @param newSelection
     * @param eventBus
     */
    public UndoableJumpEvent(MBassador<IAppEvent> eventBus,
            JumpToEvent oldSelection, JumpToEvent newSelection) {
        super(oldSelection, newSelection);
        this.eventBus = eventBus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.main.UndoableSelection#execute(java.lang.Object)
     */
    @Override
    protected void execute(JumpToEvent selection) {
        eventBus.publish(selection);
    }

}
