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

import static com.intel.stl.ui.common.PageWeight.MEDIUM;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ImageIcon;

import com.intel.stl.api.Utils;
import com.intel.stl.ui.common.IHelp;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.console.view.ConsoleTerminalView;
import com.intel.stl.ui.console.view.IntelTerminalPanel;
import com.intel.stl.ui.console.view.IntelTerminalView;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.wittams.gritty.BackBuffer;
import com.wittams.gritty.CharacterUtils;
import com.wittams.gritty.Emulator;
import com.wittams.gritty.ScrollBuffer;
import com.wittams.gritty.StyleState;
import com.wittams.gritty.TerminalWriter;
import com.wittams.gritty.TtyChannel;

/**
 * Controller for the ConsoleTerminalView which creates am SSH connection to a
 * remote system and provides the ability to send message and receive output on
 * a terminal
 */
public class ConsoleTerminalController implements IConsoleListener, IConsole {
    private static String HIS_CMD_START = "his_cmd_start";

    private static String HIS_CMD_DONE = "his_cmd_done";

    // show last 32 history commands, and manipulate the history to omit the
    // command we used here
    private static String HISTORY_CMD = "echo " + HIS_CMD_START
            + ";export TMP_HIS=`(history 1 | cut -c 1-7)`; history -d $TMP_HIS; history 32 | cut -c 8-; echo "
            + HIS_CMD_DONE;

    private static int QUEUE_LIMIT = 100;

    private final IntelTerminalView terminal;

    private Thread processingThread = null;

    // BlockingQueue replaced per PR 129116
    private final BlockingQueue<String> messageQueue;

    private String command = null;

    private LoginBean loginBean;

    private final ConsoleTerminalView consoleTerminalView;

    @SuppressWarnings("unused")
    private String helpID;

    private String pageName;

    private final String pageDescription;

    private IConsoleEventListener consoleEventListener;

    private final IHelp consoleHelpListener;

    private final ConsoleTerminalController cnslTermCtrl = this;

    private IConsoleLogin consoleLogin;

    private ITty tty;

    private final int id;

    private Session lastSession;

    private boolean initialized;

    private String prompt;

    private boolean promptReady;

    private String lastCommand = "";

    // Added this comment to correct PR 126675 comment above
    public ConsoleTerminalController(ConsoleTerminalView view, String pageName,
            String pageDescription, int id, IHelp consoleHelpListener) {

        this.consoleTerminalView = view;
        installHelp();
        this.consoleLogin = consoleTerminalView.getConsoleLogin();
        this.pageName = pageName;
        this.pageDescription = pageDescription;
        this.id = id;
        this.consoleHelpListener = consoleHelpListener;

        terminal = new IntelTerminalView(this,
                consoleHelpListener.getTopicIdList(), cnslTermCtrl);
        view.setTermPanel(terminal);

        messageQueue = new ArrayBlockingQueue<String>(QUEUE_LIMIT);

        // Pass this as the listener to the console
        consoleTerminalView.setConsoleListener(this);
    } // ConsoleTerminalController

    protected void installHelp() {
        String helpId = getHelpID();
        if (helpId != null) {
            consoleTerminalView.enableHelp(true);
            HelpAction helpAction = HelpAction.getInstance();
            helpAction.getHelpBroker().enableHelpOnButton(
                    consoleTerminalView.getHelpButton(), helpId,
                    helpAction.getHelpSet());
        } else {
            consoleTerminalView.enableHelp(false);
        }
    }

    /**
     * @return the lastCommand
     */
    @Override
    public synchronized String getLastCommand() {
        return lastCommand;
    }

    /**
     * @param lastCommand
     *            the lastCommand to set
     */
    @Override
    public synchronized void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public String getHelpID() {
        return HelpAction.getInstance().getAdminConsoleTerminal();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.IPageController#setContext(com.intel.stl.ui.main
     * .Context, com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.IPageController#onRefresh(com.intel.stl.ui.common
     * .IProgressObserver)
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        // setCursor(new Point(0, 0));

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getName()
     */
    @Override
    public String getName() {
        return pageName;
    }

    public void setName(String name) {
        pageName = name;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getDescription()
     */
    @Override
    public String getDescription() {
        return pageDescription;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getView()
     */
    @Override
    public Component getView() {
        return consoleTerminalView;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#cleanup()
     */
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#canExit()
     */
    @Override
    public boolean canExit() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#clear()
     */
    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    public LoginBean getLoginInfo() {
        return loginBean;
    } // getLoginInfo

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#initializeTerminal()
     *
     * Description: This method is called when no other console exists with the
     * same session. In this case, the user has selected the "+" tab to create a
     * new console.
     */
    @Override
    public void initializeTerminal(LoginBean loginBean)
            throws NumberFormatException {

        this.loginBean = loginBean;
        if (loginBean != null) {
            try {
                tty = new IntelTty(loginBean, null);
                terminal.setTty(tty);
                terminal.start();
                processingThread();

            } catch (NumberFormatException e) {
                throw e;
            }
        }
    } // initializeTerminal

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.console.IConsoleListener#initializeTerminal(com.intel
     * .stl.ui.console.LoginBean)
     *
     * Description: This method is called from another console and will share
     * its session. In this case, the user has selected the "Display on New Tab"
     * radio button and issued a command from another console.
     */
    @Override
    public void initializeTerminal(LoginBean loginBean, String command) {
        this.command = command;
        this.loginBean = loginBean;

        tty = new IntelTty(loginBean, null);
        tty.setSession(loginBean.getSession());
        terminal.setTty(tty);
        terminal.start();
        processingThread();
        consoleTerminalView.addCommand(command);
    } // initializeTerminal

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.console.IConsoleListener#addNewConsole(java.lang.String)
     *
     * Description: Directs the listener to create a new console
     */
    @Override
    public void addNewConsole(String command) {
        consoleEventListener.addNewConsole(loginBean, false, command);
    } // addNewConsole

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#shutDownConsole()
     *
     * Description: Releases the blocking queue and shuts down the threads
     */
    @Override
    public void shutDownConsole() {
        addToQueue("exit");
    } // shutDownConsole

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.console.IConsoleListener#updateTab(com.intel.stl.ui.
     * console.LoginBean, java.lang.String)
     *
     * Description: Updates the console tab with the user's name and last issued
     * command
     */
    @Override
    public void updatePersonalizedTab(String command) {
        consoleEventListener.updatePersonalizedTab(loginBean, command);
    } // updatePersonalizedTab

    @Override
    public String toString() {

        String loginBeanStr;

        if (loginBean == null) {
            loginBeanStr = "loginBean is null!";
        } else {
            loginBeanStr = loginBean.toString();
        }

        return super.toString() + loginBeanStr + "]";
    } // toString

    public void setNewConsoleListener(IConsoleEventListener listener) {
        consoleEventListener = listener;
    } // setNewConsoleListener

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#updateInfoPanel()
     *
     * Description: Updates the info panel on the console terminal view with the
     * host name, port, and user name
     */
    @Override
    public void updateInfoPanel(final LoginBean loginBean) {

        Util.runInEDT(new Runnable() {
            @Override
            public void run() {

                consoleTerminalView.setHostName(loginBean.getHostName());
                consoleTerminalView.setPortNum(loginBean.getPortNum());
                consoleTerminalView.setUserName(loginBean.getUserName());
            }
        });

    } // updateInfoPanel

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.console.IConsoleListener#addToQueue(java.lang.String)
     *
     * Description: Places a command on the blocking queue
     */
    @Override
    public void addToQueue(final String command) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                try {
                    if (command != null) {
                        messageQueue.put(command);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    } // addToQueue

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#onLock(boolean)
     */
    @Override
    public void onLock(boolean isSelected) {

        if (isSelected) {
            // Save the last session
            lastSession = tty.getSession();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#onUnlock(java.lang.String)
     */
    @Override
    public void onUnlock(char[] pw) throws JSchException {

        // If this method was called, then the user presumably entered a
        // password and selected the OK button
        boolean success;
        try {
            success = authenticateSession(pw);

            if (success) {
                consoleTerminalView.toggleLock(true);
                consoleLogin.killProgress();
                consoleLogin.hideLogin();
            }
        } catch (JSchException e) {
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#showLoginDialog()
     */
    @Override
    public void showLoginPanel() {

        // Otherwise try to authenticate user login and unlock the
        // terminal
        // Before unlocking the console, display the login panel
        LoginBean loginBean = new LoginBean(lastSession.getUserName(),
                lastSession.getHost(), String.valueOf(lastSession.getPort()));
        consoleLogin.showLogin(loginBean, false, id);

    }

    @Override
    public void hideLoginPanel() {
        consoleLogin.hideLogin();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#authenticateSession()
     */
    @Override
    public boolean authenticateSession(char[] pw) throws JSchException {

        Session session;
        boolean connected = false;

        String userName = lastSession.getUserName();
        String hostName = lastSession.getHost();
        int portNum = lastSession.getPort();

        JSch jsch = Utils.createJSch();
        session = jsch.getSession(userName, hostName, portNum);

        if (session != null) {
            session.setPassword(new String(pw));
            session.connect();
            connected = session.isConnected();
        }

        return connected;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#getConsoleLogin()
     */
    @Override
    public IConsoleLogin getConsoleLogin() {
        consoleLogin = consoleEventListener.getConsoleLogin();
        return consoleLogin;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#onConnect(boolean)
     */
    @Override
    public void onConnect(boolean sessionRunning) {
        boolean connected =
                (sessionRunning && tty.isConnected()) ? true : false;

        if (connected) {
            addToQueue(command);
            loginBean.setSession(tty.getSession());
        }

        consoleEventListener.onConnect(connected, command);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#onConnectFail()
     */
    @Override
    public void onConnectFail(Exception e) {
        consoleEventListener.onConnectFail(this,
                ConsoleDispatchManager.REASON_INIT, e);
    }

    /**
     *
     * <i>Description: Sends commands to the remote host over the output
     * stream</i>
     *
     * @param command
     */
    public void sendCommand(String command) {
        if (terminal != null) {
            if (command != null) {
                try {
                    terminal.sendCommand(command + new String(
                            CharacterUtils.getCode(KeyEvent.VK_ENTER)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    } // sendCommand

    public boolean isConnected() {
        return terminal.isSessionRunning();
    }

    /**
     *
     * <i>Description: Waits for commands on the blocking queue and sends them
     * to the remote host for processing</i>
     *
     */
    public void processingThread() {

        if (terminal != null) {

            processingThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (processingThread != null) {

                        try {

                            command = messageQueue.take();

                            if (command != null) {
                                if (command.compareTo("exit") == 0) {
                                    terminal.stop();
                                    processingThread = null;
                                    command = null;

                                    System.out.println(
                                            "Terminal " + id + " terminated!");
                                } else {
                                    sendCommand(command);
                                }
                            }
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            });
        }

        if ((processingThread != null) && !processingThread.isAlive()) {
            processingThread.start();
        }

    } // processingThread

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#getId()
     */
    @Override
    public int getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#setCursor(java.awt.Point)
     */
    @Override
    public void setCursor(Point position) {
        terminal.setTerminalCursor(position);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#getHelpController()
     */
    @Override
    public IHelp getHelpController() {
        return consoleHelpListener;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#getSession()
     */
    @Override
    public Session getSession() {

        return tty.getSession();
    }

    /**
     * @return the tty
     */
    public ITty getTty() {
        return tty;
    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#getTerminal()
     */
    @Override
    public IntelTerminalView getTerminal() {
        return terminal;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#closeSession()
     */
    @Override
    public void closeSession() {
        consoleEventListener.closeSession(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#terminalStopped()
     */
    @Override
    public void terminalStopped() {
        consoleEventListener.terminalStopped(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleListener#isConsoleAllowed()
     */
    @Override
    public boolean isConsoleAllowed() {

        return consoleEventListener.isConsoleAllowed(this);
    }

    /**
     * Overview: Inner class ConsoleInitializer creates a separate TTY and
     * session, using the current login credentials, to silently send a history
     * command to the remote host and store the result in this console view's
     * command box
     */
    public class ConsoleInitializer implements IConsoleMsgListener {

        private String response = new String();

        private ITty initTty = null;

        private final AtomicBoolean running = new AtomicBoolean();

        private Emulator initEmulator;

        private Thread emulatorThread;

        private boolean done = false;

        /*
         * (non-Javadoc)
         *
         * @see com.intel.stl.ui.console.IConsoleMsgListener#storeCmdResult(int,
         * int, byte[])
         */
        @Override
        public void storeCmdResult(int bytesAvailable, int numBytesRead,
                byte[] buf) {

            String[] cmds;

            if (!done) {
                try {
                    String inputStr = new String(buf, "UTF-8");
                    if (numBytesRead <= inputStr.length()) {
                        response += inputStr.substring(0, numBytesRead);
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    cmds = response.split("\r\n");

                    if (bytesAvailable <= 0) {
                        if (cmds.length >= 2) {
                            done = cmds[cmds.length - 2].equals(HIS_CMD_DONE);
                        } else {
                            response = "";
                        }

                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }

            if (done) {
                initTty.enableMsgListener(false);
                consoleTerminalView.setCmdFieldEnable(false);
                cmds = response.split("\r\n");
                boolean cmdStarted = false;
                for (int i = 0; i < cmds.length - 2; i++) {
                    if (!cmdStarted && cmds[i].equals(HIS_CMD_START)) {
                        cmdStarted = true;
                    } else if (cmdStarted) {
                        consoleTerminalView.addCommand(cmds[i]);
                    }
                }

                if (emulatorThread != null) {
                    emulatorThread.interrupt();
                    emulatorThread = null;
                }
                consoleTerminalView.setCmdFieldEnable(true);
                setInitialized(true);
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.intel.stl.ui.console.IConsoleMsgListener#initializeCommands()
         */
        @Override
        public void initializeCommands(LoginBean loginBean)
                throws NumberFormatException, JSchException {

            boolean connected = false;
            TtyChannel channel = null;

            // Make a copy of the main login bean
            LoginBean bean = new LoginBean(loginBean);
            bean.setPassword(loginBean.getPassword());

            // Create a new session
            JSch jsch = Utils.createJSch();
            Session session = jsch.getSession(bean.getUserName(),
                    bean.getHostName(), Integer.valueOf(bean.getPortNum()));

            // Pass the session to the login bean
            bean.setSession(session);

            // Create a new TTY and start the terminal/emulator
            if (bean != null) {
                initTty = new IntelTty(bean, this);
                channel = new TtyChannel(initTty);

                StyleState styleState = new StyleState();
                BackBuffer backBuffer = new BackBuffer(80, 24, styleState);
                ScrollBuffer scrollBuffer = new ScrollBuffer();

                // Create view components to pass to the emulator
                IntelTerminalPanel termPanel =
                        new IntelTerminalPanel(backBuffer, scrollBuffer,
                                styleState, consoleHelpListener, cnslTermCtrl);
                termPanel.setCursor(0, 0);
                TerminalWriter terminalWriter =
                        new TerminalWriter(termPanel, backBuffer, styleState);
                initEmulator = new IntelEmulator(terminalWriter, channel);

                if (!connected) {
                    emulatorThread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                Thread.currentThread()
                                        .setName(initTty.getName());
                                if (initTty.initialize()) {
                                    Thread.currentThread()
                                            .setName(tty.getName());

                                    running.set(true);
                                    onConnect(running.get());
                                    initEmulator.start();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                running.set(false);
                                initTty.close();
                            }
                        }

                    });
                    emulatorThread.start();
                }
            }
        }

        public void onConnect(boolean connected) {

            if (connected) {

                // Put a blank entry in the combo box
                consoleTerminalView.addCommand("");

                done = false;
                response = "";
                // Turn on the message listener and send the history command
                initTty.enableMsgListener(true);
                try {
                    String commandToSend = new String(HISTORY_CMD + new String(
                            CharacterUtils.getCode(KeyEvent.VK_ENTER)));
                    // wait a little bit to avoid sending command before console
                    // start to respond
                    Thread.sleep(500);
                    initTty.write(commandToSend.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    } // class ConsoleInitializer

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IStateController#isInitialized()
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IStateController#setInitialized(boolean)
     */
    @Override
    public void setInitialized(boolean b) {
        initialized = b;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsole#setPrompt(java.lang.String)
     */
    @Override
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsole#getPrompt()
     */
    @Override
    public String getPrompt() {
        return prompt;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsole#setPromptReady(boolean)
     */
    @Override
    public void setPromptReady(boolean b) {
        promptReady = b;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsole#isPromptReady()
     */
    @Override
    public boolean isPromptReady() {
        return promptReady;
    }

} // class ConsoleTerminalController
