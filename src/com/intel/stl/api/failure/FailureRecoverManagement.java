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

package com.intel.stl.api.failure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;

/**
 * This management applies retry strategy to handle failures. Basically, when it
 * sees a failure it will sleep a while and then retry it. If it fails after
 * several tries, it will call {@link ITaskFatal#onFatal()} to treat it as
 * unrecoverable failure.
 * 
 *
 * We intentionally do not put this class under package impl because we will
 * reuse it from UI side which will ignore any classes under impl package
 */
public class FailureRecoverManagement implements IFailureManagement {
    private final static Logger log = LoggerFactory
            .getLogger(FailureRecoverManagement.class);

    private static boolean DEBUG = false;

    private static final long SHUTDOWN_TIME = 1000; // 1 sec

    public final static int DEFAULT_TOLERANCE = 3;

    public final static long DEFAULT_MEM_LENGTH = 3 * 60 * 1000; // 3 minutes

    public final static long DEFAULT_RETRY_INTERVAL = 10 * 1000; // 10 sec.

    private final int tolerance;

    private final long memoryLength;

    private final long retryInterval;

    private final Map<Object, FailureItem> items;

    private final ScheduledExecutorService executor;

    public FailureRecoverManagement() {
        this(DEFAULT_TOLERANCE, DEFAULT_MEM_LENGTH, DEFAULT_RETRY_INTERVAL);
    }

    /**
     * Description:
     * 
     * @param tolerance
     * @param memoryLength
     */
    public FailureRecoverManagement(int tolerance, long memoryLength,
            long retryInterval) {
        super();
        this.tolerance = tolerance;
        this.memoryLength = memoryLength;
        this.retryInterval = retryInterval;
        items = new HashMap<Object, FailureItem>();
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * @return the tolerance
     */
    public int getTolerance() {
        return tolerance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.failure.IFailureManagement#submit(com.intel.stl.api
     * .failure.IFailureItem, java.lang.Throwable)
     */
    @Override
    public synchronized void submit(final ITaskFailure<Void> failure,
            Throwable error) {
        if (failure == null) {
            log.info("Failure is null");
            return;
        }

        checkItems();

        FailureType type = failure.getFailureType(error);
        if (type == FailureType.UNRECOVERABLE) {
            if (DEBUG) {
                System.out.println("Task failure " + failure.getTaskId()
                        + " has exception " + error
                        + " that is a fatal failure. Running onFatal...");
            }
            failure.onFatal();
            items.remove(failure.getTaskId());
        } else if (type == FailureType.RECOVERABLE) {
            FailureItem item = items.get(failure.getTaskId());
            if (item == null) {
                item = new FailureItem();
                items.put(failure.getTaskId(), item);
            }
            int count = item.increaseCount();
            if (DEBUG) {
                System.out.println("Task failure " + failure.getTaskId()
                        + " has count " + count);
            }
            if (count >= tolerance) {
                if (DEBUG) {
                    System.out.println("Task failure " + failure.getTaskId()
                            + " is unrecoverable. Running onFatal...");
                }
                // unrecoverable
                failure.onFatal();
                items.remove(failure.getTaskId());
            } else if (failure.getTask() != null) {
                if (DEBUG) {
                    System.out.println("Task failure " + failure.getTaskId()
                            + " is recoverable. Scheduling retry...");
                }
                // retry
                final Callable<?> task = failure.getTask();
                Runnable realTask = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            task.call();
                        } catch (Exception e) {
                            log.info(
                                    "Retry task with exception "
                                            + StringUtils.getErrorMessage(e), e);
                            submit(failure, e);
                        }
                    }
                };
                executor.schedule(realTask, retryInterval,
                        TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * We intentionally share memory of FailureItems, so when a user uses block
     * and unblocked failure management together, the item counter still work
     * correctly.
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.failure.IFailureManagement#evaluate(com.intel.stl.api
     * .failure.ITaskFailure, java.lang.Throwable)
     */
    @Override
    public <E> E evaluate(ITaskFailure<E> failure, Throwable error) {
        if (failure == null) {
            log.info("Failure is null");
            return null;
        }

        checkItems();

        FailureType type = failure.getFailureType(error);
        if (type == FailureType.UNRECOVERABLE) {
            if (DEBUG) {
                System.out.println("Task failure " + failure.getTaskId()
                        + " has exception " + error
                        + " that is a fatal failure. Running onFatal...");
            }
            failure.onFatal();
            items.remove(failure.getTaskId());
        } else if (type == FailureType.RECOVERABLE) {
            FailureItem item = items.get(failure.getTaskId());
            if (item == null) {
                item = new FailureItem();
                items.put(failure.getTaskId(), item);
            }
            int count = item.increaseCount();
            if (DEBUG) {
                System.out.println("Task failure " + failure.getTaskId()
                        + " has count " + count);
            }
            if (count >= tolerance) {
                if (DEBUG) {
                    System.out.println("Task failure " + failure.getTaskId()
                            + " is unrecoverable. Running onFatal...");
                }
                // unrecoverable
                failure.onFatal();
                items.remove(failure.getTaskId());
            } else if (failure.getTask() != null) {
                if (DEBUG) {
                    System.out.println("Task failure " + failure.getTaskId()
                            + " is recoverable. Retrying...");
                }
                // retry
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e1) {
                }
                Callable<E> task = failure.getTask();
                try {
                    return task.call();
                } catch (Exception e) {
                    log.info(
                            "Retry task with exception "
                                    + StringUtils.getErrorMessage(e), e);
                    return evaluate(failure, e);
                }
            }
        }
        return null;
    }

    /**
     * <i>Description:</i> clear old failures that doesn't happen recently
     * 
     */
    protected void checkItems() {
        Object[] ids = items.keySet().toArray();
        for (Object id : ids) {
            FailureItem item = items.get(id);
            long elapsedTime = item.getElapsedTime();
            if (elapsedTime > memoryLength) {
                log.info("Remove old task failure " + id);
                items.remove(id);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.failure.IFailureManagement#cleanup()
     */
    @Override
    public void cleanup() throws InterruptedException {
        items.clear();
        executor.shutdown();
        if (!executor.awaitTermination(SHUTDOWN_TIME, TimeUnit.MILLISECONDS)) {
            log.info("Executor did not terminate in the specified time.");
            List<Runnable> droppedTasks = executor.shutdownNow();
            log.info("Executor was abruptly shut down. " + droppedTasks.size()
                    + " tasks will not be executed.");
        }
    }

    private static class FailureItem {
        private int count;

        private long timestamp;

        public int increaseCount() {
            count += 1;
            timestamp = System.currentTimeMillis();
            return count;
        }

        public long getElapsedTime() {
            return System.currentTimeMillis() - timestamp;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "FailureItem [count=" + count + ", timestamp=" + timestamp
                    + "]";
        }

    }

}
