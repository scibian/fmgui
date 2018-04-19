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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.HelpController;
import com.intel.stl.ui.console.IConsoleListener;
import com.intel.stl.ui.console.IConsole;
import com.intel.stl.ui.console.ITty;
import com.intel.stl.ui.console.IntelEmulator;
import com.wittams.gritty.BackBuffer;
import com.wittams.gritty.CharacterUtils;
import com.wittams.gritty.Emulator;
import com.wittams.gritty.RequestOrigin;
import com.wittams.gritty.ScrollBuffer;
import com.wittams.gritty.StyleState;
import com.wittams.gritty.TerminalWriter;
import com.wittams.gritty.TtyChannel;
import com.wittams.gritty.swing.ConnectedKeyHandler;

/**
 * This class holds the terminal panel and launches the Emulator task thread to
 * process input
 */
public class IntelTerminalView extends JPanel {

    private static final Logger termLogger = LoggerFactory
            .getLogger(IntelTerminalView.class);

    private static final long serialVersionUID = -8213232075937432833L;

    private final String reportsCommand = "iba_report -o ";

    private final String reportsString = "iba_report -reporttypes ";

    private final StyleState styleState;

    private final BackBuffer backBuffer;

    private final ScrollBuffer scrollBuffer;

    private final IntelTerminalPanel termPanel;

    private final JScrollBar scrollBar;

    private ITty tty;

    private TtyChannel ttyChannel;

    private final TerminalWriter terminalWriter;

    private Emulator emulator;

    private Thread emuThread;

    private final List<String> topicIdList;

    private final AtomicBoolean sessionRunning = new AtomicBoolean();

    private final IConsoleListener consoleListener;

    private String command = new String("");

    public static enum BufferType {
        Back() {
            @Override
            String getValue(IntelTerminalView term) {
                return term.getTermPanel().getBackBuffer().getLines();
            }
        },
        BackStyle() {
            @Override
            String getValue(IntelTerminalView term) {
                return term.getTermPanel().getBackBuffer().getStyleLines();
            }
        },
        Damage() {
            @Override
            String getValue(IntelTerminalView term) {
                return term.getTermPanel().getBackBuffer().getDamageLines();
            }
        },
        Scroll() {
            @Override
            String getValue(IntelTerminalView term) {
                return term.getTermPanel().getScrollBuffer().getLines();
            }
        };

        abstract String getValue(IntelTerminalView term);
    }

    public IntelTerminalView(IConsoleListener consoleListener,
            List<String> topicIdList,
            IConsole consoleController) {

        super(new BorderLayout());

        this.consoleListener = consoleListener;
        this.topicIdList = topicIdList;
        styleState = new StyleState();
        backBuffer = new BackBuffer(80, 24, styleState);
        scrollBuffer = new ScrollBuffer();

        termPanel =
                new IntelTerminalPanel(backBuffer, scrollBuffer, styleState,
                        consoleListener.getHelpController(),
                        consoleController);
        termPanel.setCursor(0, 0);
        terminalWriter = new TerminalWriter(termPanel, backBuffer, styleState);
        scrollBar = new JScrollBar();

        add(termPanel, BorderLayout.CENTER);
        add(scrollBar, BorderLayout.EAST);
        scrollBar.setModel(termPanel.getBoundedRangeModel());
        sessionRunning.set(false);
    }

    public void updateTermPanelDimensions(Dimension mainPanelSize) {

        Dimension charSize = new Dimension();
        charSize.width = termPanel.getPixelWidth() / termPanel.getColumnCount();
        charSize.height = termPanel.getPixelHeight() / termPanel.getRowCount();
        Dimension termSize = new Dimension();
        termSize.width = (mainPanelSize.width * 2) / charSize.width;
        termSize.height = (mainPanelSize.height * 2) / charSize.height;
        termPanel.doResize(termSize, RequestOrigin.User);
    }

    public IntelTerminalPanel getTermPanel() {
        return termPanel;
    }

    public JScrollBar getScrollBar() {
        return scrollBar;
    }

    public void setTty(ITty tty) {
        this.tty = tty;
        ttyChannel = new TtyChannel(tty);

        emulator = new IntelEmulator(terminalWriter, ttyChannel);
        this.termPanel.setEmulator(emulator);
    }

    public void start() {
        if (!sessionRunning.get()) {
            emuThread = new Thread(new EmulatorTask());
            emuThread.start();
        } else {
            termLogger
                    .error("Should not try to start session again at this point... ");
        }
    }

    public void stop() {
        if (sessionRunning.get() && emuThread != null) {
            emuThread.interrupt();
        }
    }

    public boolean isSessionRunning() {
        return sessionRunning.get();
    }

    class EmulatorTask implements Runnable {
        @Override
        public void run() {

            try {
                Thread.currentThread().setName(tty.getName());
                if (tty.initialize()) {
                    Thread.currentThread().setName(tty.getName());
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            termPanel.setKeyHandler(createKeyHandler(emulator));
                            termPanel.requestFocusInWindow();
                        }
                    });

                    sessionRunning.set(true);
                    consoleListener.onConnect(sessionRunning.get());
                    emulator.start();
                }
            } catch (Exception e) {
                consoleListener.onConnectFail(e);
            } finally {
                sessionRunning.set(false);
                consoleListener.terminalStopped();
            }
        }
    }

    protected ConnectedKeyHandler createKeyHandler(final Emulator emulator) {

        // This key handler overrides the keyPressed() and keyTyped() methods
        // to intercept each character typed on the console and send it through
        // the command parser for processing by the help system
        ConnectedKeyHandler keyHandler = new ConnectedKeyHandler(emulator) {

            @Override
            public void keyPressed(final KeyEvent e) {
                try {
                    final char keychar = e.getKeyChar();
                    final byte[] obuffer = new byte[1];
                    final int keycode = e.getKeyCode();
                    final byte[] code = emulator.getCode(keycode);
                    if (code != null) {
                        emulator.sendBytes(code);
                    } else {

                        if ((keychar & 0xff00) == 0) {
                            obuffer[0] = (byte) e.getKeyChar();
                            emulator.sendBytes(obuffer);
                        }
                    }

                    // Process the command for the help system
                    processCommand(keychar, e.getKeyCode(), obuffer);

                } catch (final IOException ex) {
                    termLogger.error("Error sending key to emulator", ex);
                }
            }

            @Override
            public void keyTyped(final KeyEvent e) {
                final char keychar = e.getKeyChar();
                if ((keychar & 0xff00) != 0) {
                    final char[] foo = new char[1];
                    foo[0] = keychar;
                    try {
                        final byte[] bytes = new String(foo).getBytes("EUC-JP");
                        emulator.sendBytes(bytes);

                        // Process the command for the help system
                        processCommand(keychar, e.getKeyCode(), bytes);

                    } catch (final IOException ex) {
                        termLogger.error("Error sending key to emulator", ex);
                    }
                }
            }

        };

        return keyHandler;
    }

    protected void processCommand(char keyChar, int keyCode, byte[] buf) {

        // Send the key character, code, and command buffer to the filter to
        // handle backspacing, carriage return, etc.
        command = filterCommand(keyChar, keyCode, buf);

        // Send valid commands to the help system parser for help system
        // navigation
        consoleListener.getHelpController().parseCommand(command);

        // Update the help system's combo box with the command without
        // parameters
        if (command.split(" ").length > 0) {
            consoleListener.getHelpController().updateSelection(
                    command.split(" ")[0]);
        }
    }

    protected String filterCommand(char keyChar, int keyCode, byte[] buf) {

        // Only append alpha-numeric and special characters to the command
        // string
        if ((CharacterUtils.US < keyChar) && (keyChar < CharacterUtils.DEL)) {
            command += keyChar;
        }

        // Process control characters
        switch (keyCode) {

            case CharacterUtils.BS:
                if (command.length() > 0) {

                    // Special Case for iba_report assists in synchronizing
                    // command string content with backspacing on the command
                    // line
                    if (command.startsWith(reportsString)) {
                        command =
                                command.replaceFirst(reportsString,
                                        reportsCommand);
                    }
                    command = command.substring(0, command.length() - 1);
                }

                break;

            case CharacterUtils.DEL:
                break;

            case CharacterUtils.LF:
                // When the user presses <ENTER>, clear the command
                // and make the help system navigate to the table of contents
                command = "";
                consoleListener.getHelpController().parseCommand(
                        HelpController.TOC);
                consoleListener.getHelpController().updateSelection(command);

                break;

            default:
                break;
        }

        // Special Case for iba_report to ensure that the help system
        // remains at the "Reports Type" section of the page
        if (command.startsWith("iba_report -o ")) {
            command = command.replaceFirst(reportsCommand, reportsString);
        }

        return command;
    }

    public String getBufferText(BufferType type) {
        return type.getValue(this);
    }

    public void setTerminalCursor(Point position) {
        termPanel.setCursor(position.x, position.y);
    }

    public void enableKeyHandler(boolean enable) {
        termPanel.enableKeyHandler(enable);
    }

    /**
     * @return the topicIdList
     */
    public List<String> getTopicIdList() {
        return topicIdList;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(termPanel.getPixelWidth()
                + scrollBar.getPreferredSize().width,
                termPanel.getPixelHeight());
    }

    public void sendCommand(String string) throws IOException {
        emulator.sendBytes(string.getBytes());
    }

    @Override
    public boolean requestFocusInWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                termPanel.requestFocusInWindow();
            }
        });
        return super.requestFocusInWindow();
    }

}
