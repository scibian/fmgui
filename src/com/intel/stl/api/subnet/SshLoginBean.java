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

package com.intel.stl.api.subnet;

import com.intel.stl.api.CertsDescription;

public class SshLoginBean {

    private long id;

    private String subnetName;

    private String SshUserName;

    private String host;

    private int port;

    private CertsDescription certs;

    public SshLoginBean(long id, String subnetName, String SshUserName,
            String host, int port, CertsDescription certs) {
        this.id = id;
        this.subnetName = subnetName;
        this.SshUserName = SshUserName;
        this.host = host;
        this.port = port;
        this.certs = certs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubnetName() {
        return subnetName;
    }

    public void setSubnetName(String name) {
        this.subnetName = name;
    }

    public String getSshUserName() {
        return SshUserName;
    }

    public void setSshUserName(String sshUserName) {
        SshUserName = sshUserName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public CertsDescription getCerts() {
        return certs;
    }

    public void setCerts(CertsDescription certs) {
        this.certs = certs;
    }

    @Override
    public String toString() {

        return "SshLoginBean [id=" + id + ", subnetName=" + subnetName
                + ", SshUserName=" + SshUserName + ", host=" + host + ", port="
                + port + ", certs.keyStoreFile=" + certs.getKeyStoreFile()
                + ", certs.trustStoreFile" + certs.getTrustStoreFile() + "]";
    }
}
