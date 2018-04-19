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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;

public interface ICertsAssistant {

    /**
     * 
     * <i>Description:</i> returns a SSLEngine for the SubnetDescription using
     * the current HostInfo
     * 
     * @return a SSLEngine
     */
    SSLEngine getSSLEngine(SubnetDescription subnet) throws Exception;

    /**
     * 
     * <i>Description:</i> returns a SSLEngine for the SubnetDescription using
     * the current HostInfo
     * 
     * @return a SSLEngine
     */
    SSLEngine getSSLEngine(HostInfo host) throws Exception;

    /**
     * 
     * <i>Description:</i> returns a KeyManagerFactory for the
     * SubnetDescription.
     * 
     * @return a KeyManagerFactory
     */
    KeyManagerFactory getKeyManagerFactory(SubnetDescription subnet);

    /**
     * 
     * <i>Description:</i> returns a TrustManagerFactory for the
     * SubnetDescription.
     * 
     * @return a TrustManagerFactory
     */
    TrustManagerFactory getTrustManagerFactory(SubnetDescription subnet);

    /**
     * 
     * <i>Description:</i> clears all factories associated with this subnet
     * 
     */
    void clearSubnetFactories(SubnetDescription subnet);
}
