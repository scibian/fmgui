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

package com.intel.stl.ui.console;

import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.main.Context;

// Added this comment to correct PR 126675 comment above
public interface IConsoleEventListener {

    public void setContext(Context context, IProgressObserver observer);

    public void addNewConsole(final LoginBean loginBean, boolean showDialog,
            final String command);

    public void initializeConsole(LoginBean loginBean, String command)
            throws NumberFormatException;

    public void initializeConsoleThread(int consoleId, LoginBean loginBean,
            String command);

    public void updatePersonalizedTab(LoginBean loginBean, String command);

    public void onConnect(boolean connected, String command);

    public void onConnectFail(ConsoleTerminalController console, int reason,
            Exception e);

    public void cleanup();

    public void setTabListener(ITabListener tabListener);

    public void removeConsole(int id);
    
    public ConsoleTerminalController getConsoleController (Integer index);

    /**
     * 
     * <i>Description:</i>
     * 
     * @param connectedOnly
     *            if true we only count connected consoles
     * @return
     */
    public int getNumConsoles(boolean connectedOnly);

    public IConsoleLogin getConsoleLogin();

    public void onUnlockThread(int consoleId, char[] pw);

    public void closeSession(ConsoleTerminalController console);

    public void terminalStopped(int consoleId);

    public boolean isConsoleAllowed(ConsoleTerminalController console);
}
