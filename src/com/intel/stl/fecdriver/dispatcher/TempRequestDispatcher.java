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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.fecdriver.ICommand;
import com.intel.stl.fecdriver.IResponse;
import com.intel.stl.fecdriver.adapter.IAdapter;
import com.intel.stl.fecdriver.session.ISession;
import com.intel.stl.fecdriver.session.TemporarySession;

public class TempRequestDispatcher extends SubnetRequestDispatcher implements
        ITempRequestDispatcher {
    private static Logger log = LoggerFactory
            .getLogger(TempRequestDispatcher.class);

    private static final String TEMP_SUBNET_NAME = "~tempconnections";

    public TempRequestDispatcher(IAdapter adapter) throws IOException {
        super(new SubnetDescription(TEMP_SUBNET_NAME), adapter, null,
                new NoPoolingPolicy());
    }

    @Override
    public ISession createTemporarySession(HostInfo host,
            IConnectionEventListener listener) {
        log.debug("Creating temporary session to host {}.", host);
        Connection conn = createConnection(host, listener, true);
        addPendingConnection(conn);
        ISession tempSession = new TemporarySession(this, conn);
        sessions.add(tempSession);
        wakeupDispatcher();
        return tempSession;
    }

    @Override
    public void removeSession(ISession session, Connection conn) {
        closeConnection(conn);
        synchronized (connPool) {
            Iterator<Connection> it = connPool.iterator();
            while (it.hasNext()) {
                if (it.next().equals(conn)) {
                    it.remove();
                }
            }
        }
        super.removeSession(session);
    }

    @Override
    public <E extends IResponse<F>, F> void queueCmd(ICommand<E, F> cmd,
            Connection conn) {
        addPendingCommand(cmd, conn);
        wakeupDispatcher();
    }

    @Override
    protected void processConnectionError(Exception ce, Connection conn) {
        // Don't do fail over
        List<ICommand<?, ?>> cmds = new ArrayList<ICommand<?, ?>>();
        cmds.addAll(conn.getPendingCommands());
        closeConnection(conn);
        cancelPendingCmds(cmds, ce);
    }

    @Override
    protected void processRequestError(final Exception re, final Connection conn) {
        // Don't do failure handling
    }
}
