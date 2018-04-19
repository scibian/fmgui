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

package com.intel.stl.fecdriver.dispatcher;

import java.util.List;

public class ConnectionPoolingPolicy implements IPoolingPolicy<Connection> {

    public static final int MAX_CONNECTIONS_IN_POOL = 10;

    public static final int MIN_CONNECTIONS_IN_POOL = 3;

    private int nextConnection = -1;

    private final int maxPoolSize;

    private final int minPoolSize;

    public ConnectionPoolingPolicy() {
        this(MAX_CONNECTIONS_IN_POOL, MIN_CONNECTIONS_IN_POOL);
    }

    public ConnectionPoolingPolicy(int maxPoolSize, int minPoolSize) {
        this.maxPoolSize = maxPoolSize;
        this.minPoolSize = minPoolSize;
    }

    @Override
    public int calculateNumHandlers(int currNumConns, int currNumSessions) {
        int totConn = Math.min(maxPoolSize, currNumSessions * 3);
        totConn = Math.max(minPoolSize, totConn);
        return (totConn - currNumConns);
    }

    @Override
    public Connection nextHandler(List<Connection> conns) {
        nextConnection++;
        if (nextConnection >= conns.size()) {
            nextConnection = 0;
        }
        return conns.get(nextConnection);
    }

}
