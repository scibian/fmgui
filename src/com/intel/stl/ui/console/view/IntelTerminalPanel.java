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

package com.intel.stl.ui.console.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.intel.stl.ui.common.ConsolePromptType;
import com.intel.stl.ui.common.IHelp;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.console.IConsole;
import com.wittams.gritty.BackBuffer;
import com.wittams.gritty.ScrollBuffer;
import com.wittams.gritty.Style;
import com.wittams.gritty.StyleState;
import com.wittams.gritty.swing.TermPanel;

public class IntelTerminalPanel extends TermPanel {

    private static final long serialVersionUID = 7434759697031493119L;

    protected KeyListener intelKeyHandler;

    protected boolean keyHandlerEnable = true;

    private final IHelp consoleHelpListener;

    private final IConsole consoleController;

    /**
     * Description:
     * 
     * @param backBuffer
     * 
     * @param scrollBuffer
     * 
     * @param styleState
     */
    public IntelTerminalPanel(BackBuffer backBuffer, ScrollBuffer scrollBuffer,
            StyleState styleState, IHelp consoleHelpListener,
            IConsole consoleStateController) {
        super(backBuffer, scrollBuffer, styleState);
        this.consoleHelpListener = consoleHelpListener;
        this.consoleController = consoleStateController;
    }

    @Override
    public void consumeRun(int x, int y, Style style, char[] buf, int start,
            int len) {
        super.consumeRun(x, y, style, buf, start, len);
        int old = start % getColumnCount();

        try {
            String cmd = null;
            if (consoleController.isInitialized()) {

                // Get the prompt and command from the buffer
                int begin = start - old;
                int end = len + old;
                cmd = new String(buf, begin, end);

                String prompt = getPrompt(cmd);
                if ((cmd != null) && (prompt != null)) {
                    if (cmd.contains(prompt)) {
                        String command = Util.extractCommand(cmd, prompt);
                        if(command != null && !command.isEmpty()){
	                        consoleHelpListener.parseCommand(command);
	                        consoleHelpListener.updateSelection(command);
	                        consoleController.setLastCommand(command);
                        } 
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processKeyEvent(final KeyEvent e) {

        if (keyHandlerEnable && (intelKeyHandler != null)) {
            final int id = e.getID();
            if (id == KeyEvent.KEY_PRESSED) {
                intelKeyHandler.keyPressed(e);
            } else if (id == KeyEvent.KEY_RELEASED) {
                /* keyReleased(e); */
            } else if (id == KeyEvent.KEY_TYPED) {
                intelKeyHandler.keyTyped(e);
            }
            e.consume();
        }
    }

    @Override
    public void setKeyHandler(KeyListener keyHandler) {
        intelKeyHandler = keyHandler;
    }

    public void enableKeyHandler(boolean enable) {
        keyHandlerEnable = enable;
    }

    private String getPrompt(String cmd) {
        if (cmd.contains(ConsolePromptType.ESM.getPrompt())) {
            return ConsolePromptType.ESM.getPrompt();
        } else if (cmd.contains(ConsolePromptType.HSM_ROOT.getPrompt())) {
            return ConsolePromptType.HSM_ROOT.getPrompt();
        } else if (cmd.contains(ConsolePromptType.HSM_USER.getPrompt())) {
            return ConsolePromptType.HSM_USER.getPrompt();
        }

        return null;
    }
}
