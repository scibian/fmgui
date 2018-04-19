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

package com.intel.stl.ui.publisher;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.ICancelIndicator;

/**
 * A task manager that only runs one task at one time. If there is a unfinished
 * previous task, it will cancel it first.
 */
public class SingleTaskManager {
    private static final Logger log = LoggerFactory.getLogger(SingleTaskManager.class);

    private static final boolean DEBUG = false;

    private SwingWorker<?, ?> worker;

    private boolean mayInterruptIfRunning = true;

    /**
     * @return the mayInterruptIfRunning
     */
    public boolean isMayInterruptIfRunning() {
        return mayInterruptIfRunning;
    }

    /**
     * @param mayInterruptIfRunning
     *            the mayInterruptIfRunning to set
     */
    public void setMayInterruptIfRunning(boolean mayInterruptIfRunning) {
        this.mayInterruptIfRunning = mayInterruptIfRunning;
    }

    public synchronized <V> void submit(final CancellableCall<V> caller,
            final ICallback<V> callback) {
        if (worker != null && !worker.isDone()) {
            worker.cancel(mayInterruptIfRunning);
        }

        worker = new SwingWorker<V, Void>() {
            @Override
            protected V doInBackground() throws Exception {
                if (isCancelled()) {
                    log.info("Cancelled task caller " + caller + " callback "
                            + callback);
                    return null;
                }

                if (caller != null) {
                    if (DEBUG) {
                        System.out.println("Start caller " + caller
                                + " at background "
                                + Thread.currentThread().getName()
                                + "with callback " + callback);
                    }
                    V res = caller.call();
                    if (DEBUG) {
                        System.out.println("End caller " + caller);
                    }
                    return res;
                } else {
                    return null;
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done() {
                try {
                    V result = get();
                    if (callback != null) {
                        if (DEBUG) {
                            System.out.println("Start callback " + callback
                                    + " at frontground "
                                    + Thread.currentThread().getName());
                        }
                        callback.onDone(result);
                        if (DEBUG) {
                            System.out.println("End callback " + callback);
                        }
                    }
                } catch (InterruptedException e) {
                    if (DEBUG) {
                        System.out.println("Interrupted caller " + caller
                                + " callback " + callback);
                    }
                } catch (CancellationException e) {
                    log.info("Cancelled task caller " + caller + " callback "
                            + callback);
                    if (DEBUG) {
                        System.out.println("Cancelled task caller " + caller
                                + " callback " + callback);
                    }
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (!(cause instanceof CancellationException)) {
                        e.printStackTrace();
                        e.getCause().printStackTrace();
                        if (callback != null) {
                            callback.onError(e.getCause());
                        }
                    }
                } finally {
                    if (callback != null) {
                        callback.onFinally();
                    }
                }
            }
        };

        final ICancelIndicator indicator = caller.getCancelIndicator();
        caller.setCancelIndicator(new ICancelIndicator() {
            @Override
            public boolean isCancelled() {
                return worker.isCancelled()
                        || (indicator != null && indicator.isCancelled());
            }
        });
        worker.execute();
    }

}
