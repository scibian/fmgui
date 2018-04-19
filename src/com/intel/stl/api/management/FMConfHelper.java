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

package com.intel.stl.api.management;

import static com.intel.stl.api.subnet.HostType.ESM;
import static com.intel.stl.api.subnet.HostType.HSM;
import static com.intel.stl.api.subnet.HostType.UNKNOWN;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.Utils;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.HostType;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.common.STLMessages;
import com.intel.stl.common.SshSession;
import com.intel.stl.fecdriver.network.ssh.JSchChannelType;
import com.intel.stl.fecdriver.network.ssh.SshKeyType;
import com.intel.stl.fecdriver.network.ssh.impl.JSchSession;
import com.intel.stl.fecdriver.network.ssh.impl.JSchSessionFactory;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * This class helps us get and deploy FM conf file. It also can help us restart
 * FM remotely.To avoid confliction on conf change, the only way to get a helper
 * is through the {@link #getInstance()} method that will guarantee only one
 * helper per subnet. Therefore we can safely do sync control within the helper.
 * Note the sync here is at low level. It prevents us from getting and deploying
 * at the same time. At business logic level, we may have multiple thread read
 * in the file in memory and do different modifications, and turns out one
 * application has multiple versions of FM conf. This kind of sync is controlled
 * at higher level.
 *
 */
public class FMConfHelper {
    private final static Logger log =
            LoggerFactory.getLogger(FMConfHelper.class);

    public final static String OLD_HSM_CFGPATH = "/etc/sysconfig/";

    public final static String HSM_CFGPATH = "/etc/opa-fm/";

    public final static String ESM_CFGPATH = "/firmware/";

    private static Map<SubnetDescription, FMConfHelper> helperMap =
            new HashMap<SubnetDescription, FMConfHelper>();

    protected final static String CONF = "opafm.xml";

    protected final static int NUM_BACKUPS = 16;

    protected final SubnetDescription subnet;

    protected int[] fmVersion;

    protected File tmpConfFile;

    public static FMConfHelper getInstance(SubnetDescription subnet) {
        FMConfHelper helper = helperMap.get(subnet);
        if (helper == null) {
            helper = new FMConfHelper(subnet);
            helperMap.put(subnet, helper);
        }
        return helper;
    }

    /**
     * Description:
     *
     * @param subnet
     */
    protected FMConfHelper(SubnetDescription subnet) {
        super();
        this.subnet = subnet;
    }

    public String getHost() {
        return subnet.getCurrentFE().getHost();
    }

    public synchronized void reset() {
        tmpConfFile = null;
    }

    /**
     *
     * <i>Description:</i> return the local copy of FM opafm.xml file.
     *
     * @return the local file of the opafm.xml
     */
    public synchronized File getConfFile() {
        return tmpConfFile;
    }

    /**
     *
     * <i>Description:</i> apply the local opafm.xml to SM node. This will do
     * the following: 1) create connection with SM node with username/password
     * 2) make a copy of FM opafm.xml 3) replace FM opafm.xml with local
     * opafm.xml
     */
    public synchronized void deployConf(char[] password) throws Exception {
        if (tmpConfFile == null) {
            return;
        }
        HostInfo hostInfo = subnet.getCurrentFE();
        HostType hostType = hostInfo.getHostType();
        Session session = createSession(hostInfo, password);
        String host = hostInfo.getHost();
        String userName = hostInfo.getSshUserName();
        session.connect();
        try {
            switch (hostType) {
                case HSM:
                    deployHSM(host, userName, session);
                    break;
                case ESM:
                    deployESM(session);
                    break;
                case UNKNOWN:
                    break;
            }
        } finally {
            session.disconnect();
        }

    }

    private void deployHSM(String host, String userName, Session session)
            throws JSchException, SftpException, IOException {
        // upload file
        uploadFile(tmpConfFile.getAbsolutePath(), getConfigLocation(HSM),
                tmpConfFile.getName(), session);
        // rename file
        String cmd = getShellScript();
        log.info("execute command @ " + host + " \"" + cmd + "\"");
        Channel channel = session.openChannel("exec");
        try {
            ((ChannelExec) channel).setCommand(cmd);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();
            waitForExecution(in, channel);
            subnet.setCurrentUser(userName);
        } finally {
            channel.disconnect();
        }
    }

    private void deployESM(Session session)
            throws SftpException, JSchException {
        // upload file (there is no way to make a copy of the original
        // configuration file in ESM, so we override it)
        uploadFile(tmpConfFile.getAbsolutePath(), getConfigLocation(ESM), CONF,
                session);
    }

    protected Session createSession(HostInfo hostInfo, char[] password)
            throws JSchException {
        String host = hostInfo.getHost();
        String userName = hostInfo.getSshUserName();
        int port = hostInfo.getSshPortNum();

        JSch jsch = Utils.createJSch();
        Session session = jsch.getSession(userName, host, port);
        session.setPassword(new String(password));
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        return session;
    }

    private void uploadFile(String localFile, String remoteFolder,
            String remoteFile, Session session)
                    throws SftpException, JSchException {
        // upload file
        Channel channel =
                session.openChannel(JSchChannelType.SFTP_CHANNEL.getValue());
        String host = session.getHost();
        channel.connect();
        try {
            ChannelSftp channelSftp = (ChannelSftp) channel;
            // channelSftp.cd(hostType.getConfigLocation());
            log.info("Upload {} to {}:{}{}", localFile, host, remoteFolder,
                    remoteFile);
            channelSftp.cd(remoteFolder);
            channelSftp.put(localFile, remoteFile);
        } finally {
            channel.disconnect();
        }

    }

    public synchronized void restartFM() {
        // TODO: restart FM. This is should be called after a user choose to
        // restart via FM GUI. Some user may prefer to do it manually. And we
        // also should info our user FM will temporarily down, etc.
    }

    protected String getShellScript() {
        return "cd " + getConfigLocation(HSM) + "; cp " + CONF + " " + CONF
                + ".`date +%Y%m%d%H%M%S-%3N`" + ".fv; mv "
                + tmpConfFile.getName() + " " + CONF + "; chmod 755 " + CONF
                + "; rm -f `ls -t opafm.xml.??????????????-???.fv | sed 1,"
                + NUM_BACKUPS + "d`";
    }

    protected void waitForExecution(InputStream in, Channel channel)
            throws IOException {
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
            }
            if (channel.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                log.info("Command exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(200);
            } catch (Exception ee) {
            }
        }
    }

    public boolean checkConfigFilePresense() {
        if (tmpConfFile != null && tmpConfFile.exists()) {
            return true;
        }
        return false;
    }

    public void fetchConfigFile(char[] password) throws Exception {
        HostInfo hostInfo = subnet.getCurrentFE();
        String hostName = hostInfo.getHost();

        String userName = hostInfo.getSshUserName();

        JSchSession jschSession =
                JSchSessionFactory.getSession(subnet, false, password,
                        SshKeyType.MANAGEMENT_KEY.getKey(subnet.getSubnetId()));

        // check FM Version
        SshSession ss = jschSession.getSshSession();
        if (ss != null) {
            try {
                fmVersion = Utils.getFMVersion(ss);
            } catch (Exception e) {
            }
        }

        File tmpFile = File.createTempFile("~FV", null);
        tmpFile.deleteOnExit();
        if (!jschSession.isConnected()) {
            jschSession.shutdown();
            jschSession = JSchSessionFactory.getSession(subnet, false, password,
                    SshKeyType.MANAGEMENT_KEY.getKey(subnet.getSubnetId()));
        }
        ChannelSftp sftpChannel = jschSession.getSFtpChannel();
        sftpChannel.connect();
        HostType hostType = determineHostType(sftpChannel);
        hostInfo.setHostType(hostType);
        try {
            sftpChannel.get(CONF, tmpFile.getAbsolutePath());
            log.info("Download {}:{}{} to {} (size={})", hostName,
                    getConfigLocation(hostType), CONF,
                    tmpFile.getAbsolutePath(), tmpFile.length());
            subnet.setCurrentUser(userName);
            tmpConfFile = tmpFile;
        } catch (SftpException e) {
            throw new SftpException(e.id,
                    STLMessages.STL61020_SFTP_FAILURE.getDescription(
                            getConfigLocation(hostType) + CONF,
                            StringUtils.getErrorMessage(e)),
                    e.getCause());
        } finally {
            sftpChannel.disconnect();
        }
    }

    /*
     * This method is called by the ManagementApi to terminate any open
     * connections.
     */
    public void cancelFetchConfigFile(SubnetDescription subnet) {
        // See if there are any in-progress or open connections and close them
        JSchSession subnetSession = JSchSessionFactory.getSessionFromMap(
                SshKeyType.MANAGEMENT_KEY.getKey(subnet.getSubnetId()));
        if (subnetSession != null) {

            Channel sftp = null;
            try {
                sftp = subnetSession.getSFtpChannel();
                if (sftp != null) {
                    sftp.disconnect();
                }
            } catch (JSchException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                // Close session if user canceled the login
                subnetSession.disconnect();
            }

        } else {
            log.info(
                    "cancelFetchConfigFile(): subnetSession is not in the map yet");
        }

    }

    protected HostType determineHostType(ChannelSftp sftpChannel) {
        HostType hostType = UNKNOWN;
        HostType[] hostTypes = HostType.values();
        for (int i = 0; i < hostTypes.length; i++) {
            try {
                sftpChannel.cd(getConfigLocation(hostTypes[i]));
                hostType = hostTypes[i];
                break;
            } catch (SftpException e) {
            }
        }
        return hostType;
    }

    protected String getConfigLocation(HostType type) {
        if (type == HostType.ESM) {
            return ESM_CFGPATH;
        } else if (type == HostType.HSM) {
            if (fmVersion == null
                    || (fmVersion[0] == 10 && fmVersion[1] <= 3)) {
                return OLD_HSM_CFGPATH;
            } else {
                return HSM_CFGPATH;
            }
        } else {
            // unknown
            return "";
        }
    }
}
