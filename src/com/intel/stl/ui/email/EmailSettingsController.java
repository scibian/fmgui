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

package com.intel.stl.ui.email;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.intel.stl.api.configuration.MailProperties;
import com.intel.stl.api.notice.IEmailEventListener;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.email.view.EmailSettingsView;
import com.intel.stl.ui.main.ISubnetManager;
import com.intel.stl.ui.main.view.FVMainFrame;

public class EmailSettingsController
        implements IEmailController, IEmailEventListener<NoticeBean> {

    private static EmailSettingsController instance = null;

    private final EmailSettingsView view;

    private final ISubnetManager subnetMgr;

    private String smtpServerName = "";

    private String smtpPortNumber =
            new Integer(UIConstants.DEFAULT_SMTP_PORT).toString();

    private String fromAddress = "";

    private final String toTestAddress = "";

    private boolean isEmailNotificationsEnabled = false;

    private EmailSettingsController(EmailSettingsView view,
            ISubnetManager subnetMgr) {
        this.view = view;
        this.view.setEmailSettingsListener(this);
        this.subnetMgr = subnetMgr;

        subnetMgr.getConfigurationApi().addEmailEventListener(this);

        // Obtain the current values for SMTP properties from DB
        getSmtpSettingsFromDb();

        // Set SMPT settings in the view
        setSmtpSettingsInView();

    }

    /**
     * <i>Description:</i>
     *
     */
    private void setSmtpSettingsInView() {
        view.setSmtpServerNameStr(smtpServerName);
        view.setSmtpServerPortStr(smtpPortNumber);
        view.setFromAddrStr(fromAddress);
        view.setEnableEmailChkbox(isEmailNotificationsEnabled);
    }

    /**
     * <i>Description:</i>
     *
     */
    private void getSmtpSettingsFromDb() {
        MailProperties mailProperties =
                subnetMgr.getConfigurationApi().getMailProperties();
        smtpServerName = mailProperties.getSmtpServer();
        int port = mailProperties.getSmtpPort();
        if (port < 0) {
            port = UIConstants.DEFAULT_SMTP_PORT;
        }
        smtpPortNumber = new Integer(port).toString();
        fromAddress = mailProperties.getFromAddr();
        isEmailNotificationsEnabled =
                mailProperties.getEmailNotificationsEnabled();
    }

    /**
     * <i>Description:</i> If there is NO instance of EmailSettingsController,
     * create one with the current FVMainFrame as owner.
     *
     * If there is an instance of EmailSettingsController, update its owner to
     * the FVMainFrame passed as the parameter. This is to allow for proper
     * parenting and appearance of the dialog.
     *
     */
    public static EmailSettingsController getInstance(FVMainFrame owner,
            ISubnetManager subMgr) {
        if (instance == null) {
            instance = new EmailSettingsController(new EmailSettingsView(owner),
                    subMgr);
        } else {
            instance.updateOwner(owner);
        }

        return instance;
    }

    /**
     * <i>Description:</i>
     *
     * @param owner
     */
    private void updateOwner(FVMainFrame owner) {
        view.setOwner(owner);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.email.IEmailControl#onReset()
     */
    @Override
    public void onReset() {
        // Set the current SMTP values in the view
        setSmtpSettingsInView();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.email.IEmailControl#onOK()
     */
    @Override
    public void onOK() {
        // Should retrieve the values presently set in the view and
        // save them in the controller (here).
        smtpServerName = view.getSmtpServerNameStr();
        smtpPortNumber = view.getSmtpServerPortStr();
        fromAddress = view.getFromAddrStr();
        isEmailNotificationsEnabled = view.getEnableEmail();

        // Save to the database.
        MailProperties mailProperties = new MailProperties();
        mailProperties.setSmtpServer(smtpServerName);
        mailProperties.setFromAddr(fromAddress);
        mailProperties.setSmtpPort(new Integer(smtpPortNumber));
        mailProperties
                .setEmailNotificationsEnabled(isEmailNotificationsEnabled);
        subnetMgr.getConfigurationApi().updateMailProperties(mailProperties);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.email.IEmailControl#onTest()
     */
    @Override
    public void onTest() {
        view.showTesting(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            /*
             * (non-Javadoc)
             *
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected Void doInBackground() throws Exception {
                // Should retrieve the values presently set in the view and use
                // these values to send the test email
                String testSmtpHostName = view.getSmtpServerNameStr();
                String testSmtpPortNum = view.getSmtpServerPortStr();
                String testToAddr = view.getToAddrStr();
                String testFromAddr = view.getFromAddrStr();

                // Test connection with above values...
                MailProperties mailProperties = new MailProperties();
                mailProperties.setSmtpServer(testSmtpHostName);
                mailProperties.setFromAddr(testFromAddr);
                mailProperties.setSmtpPort(new Integer(testSmtpPortNum));
                String subject =
                        UILabels.STL92001_TEST_EMAIL_SUBJECT.getDescription();
                String body = "";
                subnetMgr.getConfigurationApi().sendTestMail(mailProperties,
                        testToAddr, subject, body);
                return null;
            }

            /*
             * (non-Javadoc)
             *
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                    Util.showError(view, e);
                } finally {
                    view.showTesting(false);
                }
            }

        };
        worker.execute();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.email.IEmailControl#showEmailSettingsDlg()
     */
    @Override
    public void showEmailSettingsDlg(FVMainFrame owner) {
        view.setOwner(owner);
        view.setLocationRelativeTo(owner);
        view.setVisible(true);
        view.toFront();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.email.IEmailControl#hideEmailSettingsDlg()
     */
    @Override
    public void hideEmailSettingsDlg() {
        this.view.setVisible(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.notice.IEmailEventListener#onNewEvent(java.lang.Object
     * [])
     */
    @Override
    public void onNewEvent(NoticeBean[] noticeList) {
        for (NoticeBean bean : noticeList) {
            Util.showErrorMessage(view, new String(bean.getData()));
        }
    }

    @Override
    public boolean isEmailValid(String email) {
        return subnetMgr.isEmailValid(email);
    }
}
