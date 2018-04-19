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

import java.io.Serializable;

public class CertsDescription implements Serializable {
    private static final long serialVersionUID = 1923500609559782312L;

    private String keyStoreFile;

    private String trustStoreFile;

    private transient char[] keyStorePwd;

    private transient char[] trustStorePwd;

    public CertsDescription() {
    }

    /**
     * Description:
     * 
     * @param keyStoreFile
     * @param trustStoreFile
     */
    public CertsDescription(String keyStoreFile, String trustStoreFile) {
        this.keyStoreFile = keyStoreFile;
        this.trustStoreFile = trustStoreFile;
    }

    public void setKeyStore(String file, char[] pwd) {
        keyStoreFile = file;
        keyStorePwd = pwd;
    }

    public void setTrustStore(String file, char[] pwd) {
        trustStoreFile = file;
        trustStorePwd = pwd;
    }

    public void setKeyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    /**
     * @return the keyStoreFile
     */
    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    /**
     * @param keyStorePwd
     *            the keyStorePwd to set
     */
    public void setKeyStorePwd(char[] keyStorePwd) {
        this.keyStorePwd = keyStorePwd;
    }

    /**
     * @return the keyStorePwd
     */
    public char[] getKeyStorePwd() {
        return keyStorePwd;
    }

    public void setTrustStoreFile(String trustStoreFile) {
        this.trustStoreFile = trustStoreFile;
    }

    /**
     * @return the trustStoreFile
     */
    public String getTrustStoreFile() {
        return trustStoreFile;
    }

    /**
     * @param trustStorePwd
     *            the trustStorePwd to set
     */
    public void setTrustStorePwd(char[] trustStorePwd) {
        this.trustStorePwd = trustStorePwd;
    }

    /**
     * @return the trustStorePwd
     */
    public char[] getTrustStorePwd() {
        return trustStorePwd;
    }

    public void clearKeyPwd() {
        clearPwd(keyStorePwd);
        keyStorePwd = null;
    }

    public void clearTrustPwd() {
        clearPwd(trustStorePwd);
        trustStorePwd = null;
    }

    public void clearPwds() {
        clearKeyPwd();
        clearTrustPwd();
    }

    public boolean hasPwds() {
        boolean hasPwds =
                keyStorePwd != null && keyStorePwd.length > 0
                        && trustStorePwd != null && trustStorePwd.length > 0;
        return hasPwds;
    }

    private void clearPwd(char[] pwd) {
        if (pwd != null) {
            for (int i = 0; i < pwd.length; i++) {
                pwd[i] = '\0';
            }
        }
    }

    /**
     * <i>Description:</i>
     * 
     * @return
     */
    public boolean isEmpty() {
        return keyStoreFile == null || keyStoreFile.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
                prime
                        * result
                        + ((keyStoreFile == null) ? 0 : keyStoreFile.hashCode());
        result =
                prime
                        * result
                        + ((trustStoreFile == null) ? 0 : trustStoreFile
                                .hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CertsDescription other = (CertsDescription) obj;
        if (keyStoreFile == null) {
            if (other.keyStoreFile != null) {
                return false;
            }
        } else if (!keyStoreFile.equals(other.keyStoreFile)) {
            return false;
        }
        if (trustStoreFile == null) {
            if (other.trustStoreFile != null) {
                return false;
            }
        } else if (!trustStoreFile.equals(other.trustStoreFile)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CertsDescription [keyStoreFile=" + keyStoreFile
                + ", trustStoreFile=" + trustStoreFile + "]";
    }

}
