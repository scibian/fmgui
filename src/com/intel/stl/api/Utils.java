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

package com.intel.stl.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManagerFactory;

import com.intel.stl.api.configuration.LinkSpeedMask;
import com.intel.stl.api.configuration.LinkWidthMask;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.common.SshSession;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

public class Utils {

    private static final String SSL_PROTOCOL = "TLSv1.2";

    private static final String TRUST_MANAGEMENT_ALGORITHM = "SunX509";

    private static final String SECURITY_PROVIDER = "SunJSSE";

    private static final String KEYSTORE_TYPE = "JKS";

    public static boolean isExpectedSpeed(PortInfoBean port,
            LinkSpeedMask expectedSpeed) {
        short activeSpeedBit = port.getLinkSpeedActive();
        return activeSpeedBit == expectedSpeed.getSpeedMask();
    }

    public static boolean isExpectedWidth(PortInfoBean port,
            LinkWidthMask expectedWidth) {
        short activeWidthBit = port.getLinkWidthActive();
        return activeWidthBit == expectedWidth.getWidthMask();
    }

    public static boolean isExpectedWidthDowngrade(PortInfoBean port,
            LinkWidthMask expectedWidth) {
        short txActiveWidthDownBit = port.getLinkWidthDownTxActive();
        short rxActiveWidthDownBit = port.getLinkWidthDownRxActive();
        return txActiveWidthDownBit == expectedWidth.getWidthMask()
                && rxActiveWidthDownBit == expectedWidth.getWidthMask();
    }

    public static boolean isSlowPort(PortInfoBean port) {
        return isDegradedPort(port) || !isExpectedSpeed(port)
                || !isExpectedWidth(port) || !isExpectedWidthDowngrade(port);
    }

    public static boolean isExpectedSpeed(PortInfoBean port) {
        short activeSpeedBit = port.getLinkSpeedActive();
        float activeSpeed =
                LinkSpeedMask.getLinkSpeedMask(activeSpeedBit).getSpeedInGb();
        float expectedSpeed = getEnabledSpeed(port);
        return (activeSpeed >= expectedSpeed);
    }

    private static float getEnabledSpeed(PortInfoBean port) {
        float maxSupportedSpeed = 0;
        List<LinkSpeedMask> supportedSpeeds =
                LinkSpeedMask.getSpeedMasks(port.getLinkSpeedSupported());
        for (LinkSpeedMask speed : supportedSpeeds) {
            if (speed.getSpeedInGb() > maxSupportedSpeed) {
                maxSupportedSpeed = speed.getSpeedInGb();
            }
        }
        return maxSupportedSpeed;
    }

    public static boolean isExpectedWidth(PortInfoBean port) {
        short activeWidthBit = port.getLinkWidthActive();
        int activeWidth =
                LinkWidthMask.getLinkWidthMask(activeWidthBit).getWidth();
        int expectedWidth = getEnabledWidth(port);
        return (activeWidth >= expectedWidth);
    }

    private static int getEnabledWidth(PortInfoBean port) {
        int maxSupportedWidth = 0;
        List<LinkWidthMask> supportedWidths =
                LinkWidthMask.getWidthMasks(port.getLinkWidthSupported());
        for (LinkWidthMask speed : supportedWidths) {
            if (speed.getWidth() > maxSupportedWidth) {
                maxSupportedWidth = speed.getWidth();
            }
        }
        return maxSupportedWidth;
    }

    public static boolean isExpectedWidthDowngrade(PortInfoBean port) {
        short txActiveWidthDownBit = port.getLinkWidthDownTxActive();
        int txActiveWidthDown =
                LinkWidthMask.getLinkWidthMask(txActiveWidthDownBit).getWidth();
        short rxActiveWidthDownBit = port.getLinkWidthDownRxActive();
        int rxActiveWidthDown =
                LinkWidthMask.getLinkWidthMask(rxActiveWidthDownBit).getWidth();
        int expectedWidthDown = getEnabledWidthDowngrade(port);
        return (txActiveWidthDown >= expectedWidthDown)
                && (rxActiveWidthDown >= expectedWidthDown);
    }

    private static int getEnabledWidthDowngrade(PortInfoBean port) {
        int maxSupportedWidthDown = 0;
        List<LinkWidthMask> supportedWidthDowns =
                LinkWidthMask.getWidthMasks(port.getLinkWidthDownSupported());
        for (LinkWidthMask speed : supportedWidthDowns) {
            if (speed.getWidth() > maxSupportedWidthDown) {
                maxSupportedWidthDown = speed.getWidth();
            }
        }
        return maxSupportedWidthDown;
    }

    public static boolean isDegradedPort(PortInfoBean port) {
        short activeWidthBit = port.getLinkWidthActive();
        short txActiveWidthDownBit = port.getLinkWidthDownTxActive();
        short rxActiveWidthDownBit = port.getLinkWidthDownRxActive();
        return activeWidthBit != txActiveWidthDownBit
                || activeWidthBit != rxActiveWidthDownBit;
    }

    /**
     *
     * <i>Description:</i>
     *
     * @param id
     *            HoQLife id
     * @return HoqLife in ms
     */
    public static final double getHoQLife(byte id) {
        if (id >= 0x14) {
            return Double.POSITIVE_INFINITY;
        } else {
            return (0x04 << id) / 1024.0;
        }
    }

    public static final Date convertFromUnixTime(long unixTime) {
        return new Date(unixTime * 1000);
    }

    public static short unsignedByte(byte val) {
        return (short) (val & 0xff);
    }

    public static int unsignedShort(short val) {
        return val & 0xffff;
    }

    public static long unsignedInt(int val) {
        return val & 0xffffffffL;
    }

    public static long toLong(String str) {
        if (str.startsWith("0x") || str.startsWith("0X")) {
            str = str.substring(2, str.length());
        }
        return new BigInteger(str, 16).longValue();
    }

    public static File getFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            ClassLoader classLoader = Utils.class.getClassLoader();
            file = new File(classLoader.getResource(fileName).getFile());
        }
        return file;
    }

    public static SSLEngine createSSLEngine(String host, int port,
            KeyManagerFactory kmf, TrustManagerFactory tmf) throws Exception {
        if (kmf == null || tmf == null) {
            Exception e =
                    new SSLHandshakeException("Couldn't create SSLEngine");
            throw e;
        }

        SSLContext context = SSLContext.getInstance(SSL_PROTOCOL);
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(),
                new SecureRandom());
        SSLEngine engine = context.createSSLEngine(host, port);
        engine.setUseClientMode(true);
        return engine;
    }

    public static KeyManagerFactory createKeyManagerFactory(String client,
            char[] pwd) throws FMException {

        File cert = new File(client);
        InputStream stream = null;
        KeyManagerFactory kmf;
        try {
            kmf = KeyManagerFactory.getInstance(TRUST_MANAGEMENT_ALGORITHM,
                    SECURITY_PROVIDER);
            KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
            stream = new FileInputStream(cert);
            ks.load(stream, pwd);
            kmf.init(ks, pwd);
        } catch (Exception e) {
            throw new FMKeyStoreException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return kmf;
    }

    public static TrustManagerFactory createTrustManagerFactory(String trustCA,
            char[] pwd) throws FMException {

        File trustcert = new File(trustCA);
        InputStream truststream = null;
        TrustManagerFactory tmf;
        try {
            tmf = TrustManagerFactory.getInstance(TRUST_MANAGEMENT_ALGORITHM);
            KeyStore trustks = KeyStore.getInstance(KEYSTORE_TYPE);
            truststream = new FileInputStream(trustcert);
            trustks.load(truststream, pwd);
            tmf.init(trustks);
        } catch (Exception e) {
            throw new FMTrustStoreException(e);
        } finally {
            if (truststream != null) {
                try {
                    truststream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tmf;
    }

    public static String getKnownHosts() {
        String knownHost = "~/.ssh/known_hosts";
        if (knownHost.startsWith("~")) {
            knownHost = knownHost.replace("~", System.getProperty("user.home"));
        }

        File file = new File(knownHost);
        if (file.exists()) {
            return knownHost;
        }

        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (file.createNewFile()) {
                return knownHost;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSch createJSch() throws JSchException {
        JSch jsch = new JSch();
        String knownHosts = Utils.getKnownHosts();
        if (knownHosts != null) {
            jsch.setKnownHosts(knownHosts);
        }
        return jsch;
    }

    public static String listToConcatenatedString(List<String> stringList,
            String delimiter) {
        StringBuffer sb = new StringBuffer();
        if (stringList != null) {
            for (String str : stringList) {
                if (str != null) {
                    if (str.contains(delimiter)) {
                        throw new IllegalArgumentException("Source name '" + str
                                + "' contains DELIMITER '" + delimiter + "'");
                    }

                    if (sb.length() == 0) {
                        sb.append(str);
                    } else {
                        sb.append(delimiter + str);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static List<String> concatenatedStringToList(String concatString,
            String delimiter) {
        if ((concatString == null) || concatString.isEmpty()) {
            return new ArrayList<String>();
        } else {
            return Arrays.asList(concatString.split(delimiter));
        }
    }

    public static int[] getFMVersion(SshSession session) throws Exception {
        List<String> out = session.exec("opaconfig -V", 10000);
        if (!out.isEmpty()) {
            String version = out.get(0);
            if (version.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
                String[] segs = version.split("\\.");
                int[] res = new int[segs.length];
                for (int i = 0; i < segs.length; i++) {
                    res[i] = Integer.valueOf(segs[i]);
                }
                return res;
            }
        }
        return null;
    }
}
