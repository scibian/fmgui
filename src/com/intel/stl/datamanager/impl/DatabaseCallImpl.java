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

package com.intel.stl.datamanager.impl;

import static com.intel.stl.common.STLMessages.STL30014_DATABASE_CALL_EXECUTION_EXCEPTION;
import static com.intel.stl.common.STLMessages.STL30015_DATABASE_CALL_INTERRUPTION_EXCEPTION;
import static com.intel.stl.common.STLMessages.STL30020_ENTITY_MANAGER_CLOSED;
import static com.intel.stl.common.STLMessages.STL30021_DATABASE_SERVER_NOT_INITIALIZED;
import static com.intel.stl.common.STLMessages.STL30068_TRANSACTION_ACTIVE;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.FMException;
import com.intel.stl.api.StringUtils;
import com.intel.stl.datamanager.DatabaseCall;
import com.intel.stl.datamanager.Result;
import com.intel.stl.dbengine.DatabaseContext;
import com.intel.stl.dbengine.DatabaseServer;

public abstract class DatabaseCallImpl<R> implements DatabaseCall<R> {

    private static Logger log = LoggerFactory.getLogger(DatabaseCallImpl.class);

    private DatabaseServer server;

    private Exception clientTrace;

    private Future<R> future;

    @Override
    public R call() throws Exception {
        if (server == null) {
            DatabaseException dbe =
                    new DatabaseException(
                            STL30021_DATABASE_SERVER_NOT_INITIALIZED);
            throw dbe;
        }
        boolean exceptionProcessed = false;
        String contextName = getContextName();
        DatabaseContext ctx = server.getDatabaseContext(contextName);
        try {
            R result = execute(ctx);
            return result;
        } catch (HibernateException he) {
            exceptionProcessed = true;
            checkTransaction(ctx);
            // According to Hibernate documentation, any exception coming from
            // the EntityManager will not leave it in a consistent state, so
            // it should be discarded and a new EntityManager created
            server.addDatabaseContext(contextName);
            ctx = server.getDatabaseContext(contextName);
            addClientTrace(he);
            throw he;
        } catch (Exception e) {
            exceptionProcessed = true;
            checkTransaction(ctx);
            addClientTrace(e);
            throw e;
        } finally {
            EntityManager em = ctx.getEntityManager();
            if (!exceptionProcessed) {
                EntityTransaction tx = em.getTransaction();
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                    DatabaseCallException dce =
                            new DatabaseCallException(
                                    STL30068_TRANSACTION_ACTIVE
                                            .getDescription());
                    throw dce;
                }
            }
            em.clear();
            if (!em.isOpen()) {
                server.addDatabaseContext(contextName);
                DatabaseCallException dce =
                        new DatabaseCallException(
                                STL30020_ENTITY_MANAGER_CLOSED.getDescription());
                throw dce;
            }
        }
    }

    @Override
    public void setFuture(Future<R> future) {
        this.future = future;
    }

    @Override
    public void setDatabaseServer(DatabaseServer server) {
        this.server = server;
    }

    @Override
    public void setClientTrace(Exception e) {
        this.clientTrace = e;
    }

    @Override
    public R getResult() {
        R result = null;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            throw getResultInterruptedError(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DatabaseException) {
                DatabaseException dbe = (DatabaseException) cause;
                throw dbe;
            }
            throw getResultError(cause);
        }
        return result;
    }

    @Override
    public <E extends FMException> R getResult(Class<E> allowedException)
            throws E {
        R result = null;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            throw getResultInterruptedError(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DatabaseException) {
                throw (DatabaseException) cause;
            } else if (cause instanceof FMException) {
                if (allowedException.isInstance(cause)) {
                    throw allowedException.cast(cause);
                }
            }
            throw getResultError(cause);
        }
        return result;
    }

    @Override
    public <E extends FMException, F extends FMException> R getResult(
            Class<E> allowedException1, Class<F> allowedException2) throws E, F {
        R result = null;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            throw getResultInterruptedError(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DatabaseException) {
                throw (DatabaseException) cause;
            } else if (cause instanceof FMException) {
                if (allowedException1.isInstance(cause)) {
                    throw allowedException1.cast(cause);
                }
                if (allowedException2.isInstance(cause)) {
                    throw allowedException2.cast(cause);
                }
            }
            throw getResultError(cause);
        }
        return result;
    }

    @Override
    public Result<R> getResultAsync() {
        Result<R> result = new ResultImpl<R>(future);
        return result;
    }

    @Override
    public abstract R execute(DatabaseContext dbCtx) throws Exception;

    protected String getContextName() {
        return Thread.currentThread().getName();
    }

    private void checkTransaction(DatabaseContext dbCtx) {
        EntityManager em = dbCtx.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }

    private DatabaseException getResultError(Throwable cause) {
        DatabaseException dbe =
                new DatabaseException(
                        STL30014_DATABASE_CALL_EXECUTION_EXCEPTION, cause,
                        cause.getClass().getSimpleName(),
                        StringUtils.getErrorMessage(cause));
        log.error(StringUtils.getErrorMessage(dbe), cause);
        return dbe;
    }

    private DatabaseException getResultInterruptedError(InterruptedException e) {
        DatabaseException dbe =
                new DatabaseException(
                        STL30015_DATABASE_CALL_INTERRUPTION_EXCEPTION, e);
        log.debug(StringUtils.getErrorMessage(dbe), e);
        return dbe;
    }

    private void addClientTrace(Throwable t) {
        if (clientTrace != null) {
            t.addSuppressed(clientTrace);
        }
    }
}
