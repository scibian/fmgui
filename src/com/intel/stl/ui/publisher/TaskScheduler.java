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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.FMException;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.failure.BaseFailureEvaluator;
import com.intel.stl.api.failure.BaseTaskFailure;
import com.intel.stl.api.failure.FailureManager;
import com.intel.stl.api.failure.FatalException;
import com.intel.stl.api.failure.IFailureManagement;
import com.intel.stl.api.failure.ITaskFailure;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.performance.PerformanceRequestCancelledException;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.publisher.subscriber.EventSubscriber;
import com.intel.stl.ui.publisher.subscriber.FocusPortCounterSubscriber;
import com.intel.stl.ui.publisher.subscriber.GroupInfoSubscriber;
import com.intel.stl.ui.publisher.subscriber.IRegisterTask;
import com.intel.stl.ui.publisher.subscriber.ImageInfoSubscriber;
import com.intel.stl.ui.publisher.subscriber.PortCounterSubscriber;
import com.intel.stl.ui.publisher.subscriber.Subscriber;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;
import com.intel.stl.ui.publisher.subscriber.VFFocusPortSubscriber;
import com.intel.stl.ui.publisher.subscriber.VFInfoSubscriber;
import com.intel.stl.ui.publisher.subscriber.VFPortCounterSubscriber;

public class TaskScheduler implements IRegisterTask {

    private static Logger log = LoggerFactory.getLogger(TaskScheduler.class);

    private final static long SHUTDOWN_TIME = 2000;

    private final static int POOL_SIZE = 2;

    private static final String TSB_THREAD_PREFIX = "tsbthread-";

    private static final String TSS_THREAD_PREFIX = "tssthread-";

    private final String name;

    private final Context context;

    private final IPerformanceApi perfApi;

    private ScheduledExecutorService scheduledService;

    private final ExecutorService backgroundService;

    private final IFailureManagement failureMgr;

    private final BaseFailureEvaluator failureEvaluator;

    private int refreshRate; // seconds

    private boolean shutdownInProgress = false;

    // List of external entities to be notified when the refresh rate changes.

    private final List<IRefreshRateListener> refreshRateChangeListeners =
            new ArrayList<IRefreshRateListener>();

    private final HashMap<SubscriberType, Subscriber<?>> subscriberPool =
            new HashMap<SubscriberType, Subscriber<?>>();

    public TaskScheduler(Context context, int refreshRate) {
        this(context, POOL_SIZE, refreshRate);
    }

    public TaskScheduler(Context context, int poolSize, int refreshRate) {
        this.context = context;
        this.perfApi = context.getPerformanceApi();
        this.name = context.getSubnetDescription().getName();
        this.refreshRate = refreshRate;

        ThreadFactory tssFactory = new ServiceThreadFactory(TSS_THREAD_PREFIX);
        scheduledService =
                Executors.newScheduledThreadPool(poolSize, tssFactory);
        ThreadFactory tsbFactory = new ServiceThreadFactory(TSB_THREAD_PREFIX);
        backgroundService =
                Executors.newFixedThreadPool(poolSize * 2, tsbFactory);

        failureEvaluator = new BaseFailureEvaluator();
        failureEvaluator.setRecoverableErrors(RuntimeException.class,
                TimeoutException.class);
        failureEvaluator.setUnrecoverableErrors(IOException.class);

        failureMgr = FailureManager.getManager();

        // Initialize the subscribers
        subscriberPool.put(SubscriberType.PORT_COUNTER,
                new PortCounterSubscriber(this, perfApi));
        subscriberPool.put(SubscriberType.VF_PORT_COUNTER,
                new VFPortCounterSubscriber(this, perfApi));
        subscriberPool.put(SubscriberType.GROUP_INFO, new GroupInfoSubscriber(
                this, perfApi));
        subscriberPool.put(SubscriberType.IMAGE_INFO, new ImageInfoSubscriber(
                this, perfApi));
        subscriberPool.put(SubscriberType.VF_INFO, new VFInfoSubscriber(this,
                perfApi));
        subscriberPool.put(SubscriberType.FOCUS_PORTS,
                new FocusPortCounterSubscriber(this, perfApi));
        subscriberPool.put(SubscriberType.VF_FOCUS_PORTS,
                new VFFocusPortSubscriber(this, perfApi));
        subscriberPool.put(SubscriberType.EVENT, new EventSubscriber(this,
                context.getEvtCal()));

        log.info("Refresh Rate = " + refreshRate);
    }

    /**
     * @return the perfApi
     */
    public IPerformanceApi getPerformanceApi() {
        return perfApi;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.publisher.subscriber.IRegisterTask#getSubscriber(com
     * .intel.stl.ui.publisher.subscriber.SubscriberType)
     */
    @Override
    // public <E> Subscriber<E> getSubscriber(SubscriberType subscriberType) {
    public Subscriber<?> getSubscriber(SubscriberType subscriberType) {
        Subscriber<?> res = subscriberPool.get(subscriberType);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException("Couldn't find Subscriber '"
                    + subscriberType + "'");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.publisher.subscriber.IRegisterTask#getRefreshRate()
     */
    @Override
    public int getRefreshRate() {
        return refreshRate;
    } /*
       * (non-Javadoc)
       * 
       * @see
       * com.intel.stl.ui.publisher.subscriber.IRegisterTask#updateRefreshRate
       * (int)
       */

    @Override
    public void updateRefreshRate(int refreshRate) {

        // Update the refresh rate
        this.refreshRate = refreshRate;
        log.info("Refresh Rate changed to: " + refreshRate);

        ScheduledExecutorService oldScheduledService = scheduledService;
        // create an new ExecutorService, the new registered tasks will be
        // scheduled on it
        scheduledService = Executors.newScheduledThreadPool(POOL_SIZE);
        // call subscriber to move its tasks to the new created ExecutorService
        for (Subscriber<?> subscriber : subscriberPool.values()) {
            subscriber.rescheduleTasks();
        }

        // Notify registered listeners of rate change.
        for (IRefreshRateListener listener : this.refreshRateChangeListeners) {
            listener.onRefreshRateChange(this.refreshRate);
        }

        // it should be safe to shutdown the old ExecutorService without impact
        // on UI
        try {
            shutdownServiceNow(oldScheduledService);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.publisher.subscriber.IRegisterTask#submit(java.lang.
     * Runnable)
     */
    @Override
    public Future<?> submitToBackground(Runnable task) {
        Future<?> future = null;
        try {
            future = backgroundService.submit(task);
        } catch (RejectedExecutionException ree) {
            if (!shutdownInProgress) {
                throw ree;
            }
        }
        return future;
    }

    @Override
    public <E> Future<E> submitToBackground(Callable<E> task) {
        Future<E> future = null;
        try {
            future = backgroundService.submit(task);
        } catch (RejectedExecutionException ree) {
            if (!shutdownInProgress) {
                throw ree;
            }
        }
        return future;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.publisher.subscriber.IRegisterTask#submitTask(java.util
     * .List, com.intel.stl.ui.publisher.Task,
     * com.intel.stl.ui.publisher.ICallback, java.util.concurrent.Callable)
     */
    @Override
    public <E> Task<E> scheduleTask(List<Task<E>> tasks, Task<E> task,
            ICallback<E> callback, Callable<E> caller) {

        return registerTask(tasks, task, callback, caller);
    }

    protected <E> Task<E> registerTask(List<Task<E>> tasks, Task<E> task,
            ICallback<E> callback, final Callable<E> caller) {
        if (tasks == null) {
            return null;
        }

        synchronized (tasks) {
            int index = tasks.indexOf(task);
            if (index >= 0) {
                task = tasks.get(index);
                task.addCallback(callback);
                log.debug("Register Task " + task);
                log.debug("Add callback to already running task " + task);
            } else {
                task.addCallback(callback);
                task.setCaller(caller);
                final Task<E> taskFinal = task;
                log.debug("Schedule task " + task + " with rate " + refreshRate
                        + " sec.");
                ScheduledFuture<?> future =
                        scheduledService.scheduleAtFixedRate(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    E result = caller.call();
                                    taskFinal.onDone(result);
                                } catch (PerformanceRequestCancelledException e) {
                                    log.info(e.getMessage());
                                    try {
                                        taskFinal.onError(e);
                                    } finally {
                                        handleFailure(taskFinal, e);
                                    }
                                } catch (Exception e) {
                                    log.error(
                                            "Scheduled task had an error: {}",
                                            StringUtils.getErrorMessage(e), e);
                                    try {
                                        taskFinal.onError(e);
                                    } finally {
                                        handleFailure(taskFinal, e);
                                    }
                                }
                                // Util.showErrorMessage(context.getOwner(),
                                // "TTTTTTTTTTTTTTTT");
                            }
                        }, 0, refreshRate, TimeUnit.SECONDS);
                task.setFuture(future);
                tasks.add(task);
            }
            return task;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.publisher.subscriber.IRegisterTask#removeTask(java.util
     * .List, com.intel.stl.ui.publisher.Task,
     * com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    public <E> void removeTask(List<Task<E>> tasks, Task<E> task,
            ICallback<E> callback) {

        deregisterTask(tasks, task, callback);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.publisher.subscriber.IRegisterTask#removeTaskArray(java
     * .util.List, java.util.List, com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    public <E> void removeTask(List<Task<E>> taskList, List<Task<E>> tasks,
            ICallback<E[]> callbacks) {

        deregisterTask(taskList, tasks, callbacks);

    }

    protected <E> void deregisterTask(List<Task<E>> tasks, Task<E> target,
            ICallback<E> callback) {
        if (tasks == null) {
            return;
        }

        synchronized (tasks) {
            log.debug("Deregister Task " + target);
            int index = tasks.indexOf(target);
            if (index >= 0) {
                Task<E> realTask = tasks.get(index);
                realTask.removeCallback(callback);
                if (realTask.isEmpty()) {
                    log.debug("Stop Task " + target);
                    try {
                        realTask.getFuture().cancel(true);
                    } finally {
                        tasks.remove(index);
                    }
                }
            }
        }
    }

    protected <E> void deregisterTask(final List<Task<E>> tasks,
            List<Task<E>> targets, final ICallback<E[]> callback) {
        if (tasks == null) {
            return;
        }

        synchronized (tasks) {
            for (int i = targets.size() - 1; i >= 0; i--) {
                Task<E> target = targets.get(i);
                log.debug("Deregister Task " + target);
                int index = tasks.indexOf(target);
                if (index >= 0) {
                    Task<E> realTask = tasks.get(index);
                    realTask.removeSubCallbacks(callback);
                    if (realTask.isEmpty()) {
                        log.debug("Stop Task " + target);
                        try {
                            realTask.getFuture().cancel(true);
                        } finally {
                            tasks.remove(index);
                        }
                    }
                }
            }
        }
    }

    protected <E> void handleFailure(final Task<E> task, final Exception e) {
        Throwable error = e;
        if (e instanceof FMException) {
            Throwable tmp = e.getCause();
            if (tmp != null) {
                error = tmp;
            }
        }
        ITaskFailure<Void> taskFailure =
                new BaseTaskFailure<Void>(task, failureEvaluator) {

                    @Override
                    public Callable<Void> getTask() {
                        // we do NOT retry!
                        return null;
                    }

                    @Override
                    public void onFatal() {
                        log.error("Fatal Failure - Stop task! " + task);
                        FatalException fe = new FatalException(e);
                        // We need to be careful what we show on each callback
                        // on the error. The current approach is displaying
                        // a error message here, and then each callback takes
                        // local responsibility to do things like cleaning up.
                        task.onError(fe);
                        Util.showErrorMessage(context.getOwner(),
                                UILabels.STL40013_FATAL_FAILURE
                                        .getDescription(task.getDescription()));
                        // this exception will stop the schedule
                        throw fe;
                    }

                };
        failureMgr.submit(taskFailure, error);
    }

    public void clear() {
        // Traverse the subscribers in the subscriber pool, cancel the tasks
        // and clear out the task list
        for (Subscriber<?> subscriber : subscriberPool.values()) {
            subscriber.cancelTasks();
        } // for
    }

    /**
     * Description:
     * 
     * @throws InterruptedException
     * 
     */
    public void shutdown() throws InterruptedException {
        shutdownInProgress = true;
        try {
            clear();
        } finally {
            try {
                failureMgr.cleanup();
            } finally {
                try {
                    shutdownService(scheduledService);
                } finally {
                    shutdownService(backgroundService);
                }
            }
        }

        log.info("Shutdown " + getClass().getName() + " for subnet '" + name
                + "'");
    }

    public void suspendServiceDuringFailover() throws InterruptedException {
        shutdownService(scheduledService);
    }

    private void shutdownService(ExecutorService service)
            throws InterruptedException {

        // Shut down the service
        service.shutdown();

        // Wait for termination to complete
        if (!service.awaitTermination(SHUTDOWN_TIME, TimeUnit.MILLISECONDS)) {
            log.warn("Executor did not terminate in the specified time.");
            List<Runnable> droppedTasks = service.shutdownNow();
            log.warn("Executor was abruptly shut down. " + droppedTasks.size()
                    + " tasks will not be executed.");
        }
    }

    private void shutdownServiceNow(ExecutorService service)
            throws InterruptedException {

        // Shut down the service immediately!
        service.shutdownNow();

        // Wait just in case it doesn't 'actually' shut down right away
        if (!service.awaitTermination(SHUTDOWN_TIME, TimeUnit.MILLISECONDS)) {
            log.warn("Executor did not terminate in the specified time.");
            List<Runnable> droppedTasks = service.shutdownNow();
            log.warn("Executor was abruptly shut down. " + droppedTasks.size()
                    + " tasks will not be executed.");
        }
    }

    // Register refresh rate listeners.
    public void addListener(IRefreshRateListener listener) {
        refreshRateChangeListeners.add(listener);
    }

    // De-register refresh rate listeners.
    public void removeListener(IRefreshRateListener listener) {
        refreshRateChangeListeners.remove(listener);
    }

    private class ServiceThreadFactory implements ThreadFactory {

        private final String prefix;

        private final AtomicInteger threadCount = new AtomicInteger(1);

        public ServiceThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            String threadName = prefix + threadCount.getAndIncrement();
            return new Thread(r, threadName);
        }

    }
}
