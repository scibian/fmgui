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

import java.awt.Frame;
import java.awt.Window;

import javax.help.HelpSet;
import javax.help.Presentation;
import javax.help.WindowPresentation;
import javax.swing.JDialog;

public class HelpMainWindow extends WindowPresentation {

    /**
     * Description:
     *
     * @param hs
     */
    private HelpMainWindow(HelpSet hs) {
        super(hs);
    }

    static public Presentation getPresentation(HelpSet hs, String name) {
        return new HelpMainWindow(hs);
    }

    @Override
    public void setActivationWindow(Window window) {
        if (window == null) {
            JDialog dialog = null;
            Frame[] frames = Frame.getFrames();
            if (frames != null && frames.length > 0) {
                dialog = new JDialog(Frame.getFrames()[frames.length - 1],
                        true);
            }
            super.setActivationWindow(dialog);
        } else if (window instanceof Frame) {
            JDialog dialog = new JDialog((Frame) window, true);
            super.setActivationWindow(dialog);
        } else {
            super.setActivationWindow(window);
        }
    }
}
