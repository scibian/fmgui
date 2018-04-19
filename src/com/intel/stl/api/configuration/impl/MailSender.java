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

package com.intel.stl.api.configuration.impl;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.configuration.MailProperties;

public class MailSender {
    private static Logger log = LoggerFactory.getLogger(MailSender.class);

    private static boolean DEBUG = true;

    private static int QUEUE_SIZE = 4096;

    private MailProperties properties;

    private final ExecutorService service;

    private final BlockingQueue<MessageItem> items;

    private Future<?> future;

    private Session session;

    private Transport transport;

    private final Object synchronizeUpdateObject = new Object();

    /**
     * Description:
     * 
     * @param properties
     * @throws MessagingException
     * @throws NoSuchProviderException
     */
    public MailSender(MailProperties properties)
            throws NoSuchProviderException, MessagingException {
        super();
        log.debug("MailSender: constructor called.");
        service = Executors.newSingleThreadExecutor();
        // We use a fixed-size queue to avoid excessive use of
        // memory. We will throw exception if the queue is full.
        items = new LinkedBlockingQueue<MessageItem>(QUEUE_SIZE);
        initSender(properties);
    }

    // Used for a temporary test instance.
    private MailSender() {
        super();
        log.debug("MailSender: temoprary test constructor called.");
        service = null;
        items = null;
    }

    public static void sendTestMail(MailProperties properties,
            String recipient, String messageSubject, String messageBody)
            throws AddressException, MessagingException {
        MailSender mailSender = new MailSender();
        Session session = mailSender.createSession(properties);
        Transport transport = mailSender.createTransport(session);
        MessageItem messageItem =
                mailSender.createMessageItem(properties.getFromAddr(),
                        recipient, messageSubject, messageBody);

        mailSender.sendMailMessage(session, transport, messageItem);
    }

    protected void initSender(MailProperties properties)
            throws NoSuchProviderException, MessagingException {
        if (future != null) {
            future.cancel(true);
        }

        updateTransport(properties);

        Runnable task = createSenderTask();
        future = service.submit(task);
    }

    // Create the key mail sender infrastructure based on the SMTP
    // settings in MailProperties.
    public void updateTransport(MailProperties properties)
            throws NoSuchProviderException, MessagingException {
        if (properties != null) {
            // We synchronize here to prevent sending queued messages
            // during update.
            synchronized (synchronizeUpdateObject) {
                this.properties = properties;
                session = createSession(properties);
                transport = createTransport(session);
            }
        }
    }

    protected Transport createTransport(Session session)
            throws NoSuchProviderException, MessagingException {
        Transport transport = null;
        transport = session.getTransport("smtp");
        transport.connect();
        return transport;
    }

    protected Runnable createSenderTask() throws MessagingException {
        // Provides thread and blocking queue to send mail as
        // notifications become available.
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        // Block here until a notification becomes available.
                        MessageItem item = items.take();
                        try {
                            // We synchronize here in case the user
                            // updates SMTP settings with notifications
                            // already in the blocking queue. This gives
                            // us a chance to update the mail sender before
                            // the actual send operation.
                            synchronized (synchronizeUpdateObject) {
                                sendMailMessage(session, transport, item);
                            }
                        } catch (AddressException e) {
                            e.printStackTrace();
                            log.error("MailSender: createSenderTask exception: "
                                    + e.getMessage());
                        } catch (MessagingException e) {
                            e.printStackTrace();
                            log.error("MailSender: createSenderTask exception: "
                                    + e.getMessage());
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("MailSender: createSenderTask exception: "
                            + e.getMessage());
                } finally {
                    log.info("To close transport (" + transport + ") for "
                            + properties);
                    try {
                        transport.close();
                        log.info("Closed transport (" + transport + ") for "
                                + properties);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        log.error("MailSender: createSenderTask exception: "
                                + e.getMessage());
                    }
                }
            }

        };
        return task;
    }

    protected Session createSession(MailProperties properties) {
        final MailProperties mailProperties = properties;
        Properties props = new Properties();
        props.put("mail.smtp.auth", properties.isAuthEnabled());
        props.put("mail.smtp.starttls.enable", properties.isTlsEnabled());
        props.put("mail.smtp.host", properties.getSmtpServer());
        props.put("mail.smtp.port", properties.getSmtpPort());

        Session session =
                Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailProperties
                                .getUserName(), new String(mailProperties
                                .getPassword()));
                    }
                });
        session.setDebug(DEBUG);
        return session;
    }

    // Package and send the mail message.
    protected void sendMailMessage(Session session, Transport transport,
            MessageItem item) throws AddressException, MessagingException {
        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);

        if ((item.from != null) && (item.from.isEmpty() == false)
                && (item.to != null) && (item.to.isEmpty() == false)) {

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(item.from));

            // Set To: header field of the header.
            InternetAddress[] address = { new InternetAddress(item.to) };
            message.setRecipients(Message.RecipientType.TO, address);

            // Set Subject: header field
            message.setSubject(item.subject);

            // Now set the actual message
            message.setText(item.body);

            // Send message
            transport.sendMessage(message, address);

            if (DEBUG) {
                System.out.println("Send Message " + message + " to "
                        + address[0]);
            }
            log.debug("MailSender: sendMailMessage: " + message + " to "
                    + address[0]);
        }
    }

    public void submitMessage(String to, String subject, String body) {
        MessageItem item =
                new MessageItem(properties.getFromAddr(), to, subject, body);
        // Will throw exception if no space available.
        items.add(item);
    }

    public void shutdown() {
        log.debug("MailSender: shutdown called.");
        if (future != null) {
            future.cancel(true);
        }

        service.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                service.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.warn("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            log.error("MailSender: shutdown exception: " + ie.getMessage());
            // (Re-)Cancel if current thread also interrupted
            service.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public MessageItem createMessageItem(String from, String to,
            String subject, String body) {
        return new MessageItem(from, to, subject, body);
    }

    class MessageItem {
        String from;

        String to;

        String subject;

        String body;

        /**
         * Description:
         * 
         * @param from
         * @param to
         * @param subject
         * @param body
         */
        public MessageItem(String from, String to, String subject, String body) {
            super();
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.body = body;
        }

    }
}
