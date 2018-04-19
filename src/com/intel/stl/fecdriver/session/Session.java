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

package com.intel.stl.fecdriver.session;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.intel.stl.api.performance.impl.PAHelper;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.api.subnet.impl.SAHelper;
import com.intel.stl.fecdriver.ICommand;
import com.intel.stl.fecdriver.IResponse;
import com.intel.stl.fecdriver.IStatement;
import com.intel.stl.fecdriver.dispatcher.IRequestDispatcher;

public class Session implements ISession {

    private final List<WeakReference<Statement>> statements =
            new ArrayList<WeakReference<Statement>>();

    private final IRequestDispatcher dispatcher;

    private SAHelper saHelper;

    private PAHelper paHelper;

    public Session(IRequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public synchronized IStatement createStatement() {
        Statement res = new Statement(this);
        addStatement(res);
        return res;
    }

    @Override
    public SAHelper getSAHelper() {
        if (saHelper == null) {
            saHelper = new SAHelper(createStatement());
        }
        return saHelper;
    }

    @Override
    public PAHelper getPAHelper() {
        if (paHelper == null) {
            paHelper = new PAHelper(createStatement());
        }
        return paHelper;
    }

    @Override
    public synchronized void removeStatement(IStatement statement) {
        Iterator<WeakReference<Statement>> it = statements.iterator();
        while (it.hasNext()) {
            WeakReference<Statement> ref = it.next();
            Statement refStatement = ref.get();
            if (refStatement != null && refStatement.equals(statement)) {
                it.remove();
                break;
            }
        }
    }

    @Override
    public void cancelFailover() {
        dispatcher.cancelFailover();
    }

    @Override
    public void close() {
        // Make a shallow copy of the list of statements
        List<WeakReference<Statement>> copy =
                new ArrayList<WeakReference<Statement>>(statements);
        Iterator<WeakReference<Statement>> it = copy.iterator();
        while (it.hasNext()) {
            WeakReference<Statement> ref = it.next();
            try {
                // this will call removeStatement(IStatement), above
                Statement statement = ref.get();
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
            }
        }
        if (saHelper != null) {
            saHelper.close();
        }
        if (paHelper != null) {
            paHelper.close();
        }
        dispatcher.removeSession(this);
    }

    @Override
    public SubnetDescription getSubnetDescription() {
        return dispatcher.getSubnetDescription();
    }

    protected void addStatement(Statement statement) {
        statements.add(new WeakReference<Statement>(statement));
    }

    protected void fireOnRequestTimeout(TimeoutException toe) {
        dispatcher.onRequestTimeout(toe);
    }

    protected <E extends IResponse<F>, F> void submitCmd(ICommand<E, F> cmd) {
        dispatcher.queueCmd(cmd);
    }

}
