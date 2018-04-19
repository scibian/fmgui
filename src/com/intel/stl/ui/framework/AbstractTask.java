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

package com.intel.stl.ui.framework;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.intel.stl.ui.main.Context;

public abstract class AbstractTask<M, T, V> implements ITask {

    protected final M model;

    private final BackgroundWorker<T, V> worker;

    private final PropertyChangeSupport support;

    private IController controller;

    private final AtomicBoolean submitted = new AtomicBoolean(false);

    public AbstractTask(M model) {
        this.model = model;
        this.worker = new BackgroundWorker<T, V>() {

            @Override
            protected T doInBackground() throws Exception {
                return processInBackground(context);
            }

            @Override
            protected void done() {
                T result;
                try {
                    result = get();
                    onTaskSuccess(result);
                    owner.onTaskSuccess();
                } catch (InterruptedException e) {
                    onTaskFailure(e);
                    owner.onTaskFailure(e);
                } catch (ExecutionException e) {
                    onTaskFailure(e.getCause());
                    owner.onTaskFailure(e.getCause());
                } finally {
                    onFinally();
                }
            }

            @Override
            protected void process(List<V> results) {
                processIntermediateResults(results);
            }
        };
        this.support = worker.getPropertyChangeSupport();
    }

    public M getModel() {
        return model;
    }

    @Override
    public void execute(IController owner) {
        if (submitted.compareAndSet(false, true)) {
            this.controller = owner;
            worker.setOwner(controller);
            this.worker.execute();
        }
    }

    public abstract T processInBackground(Context context) throws Exception;

    public abstract void onTaskSuccess(T result);

    public abstract void onTaskFailure(Throwable caught);

    public abstract void onFinally();

    /**
     * 
     * <i>Description:</i> processes intermediate results published by the task;
     * 
     * @param intermediateResults
     *            the intermediate results published by the process in the
     *            background
     */
    protected abstract void processIntermediateResults(
            List<V> intermediateResults);

    @Override
    public boolean isSubmitted() {
        return submitted.get();
    }

    @Override
    public boolean isDone() {
        return worker.isDone();
    }

    @Override
    public boolean isCancelled() {
        return worker.isCancelled();
    }

    @Override
    public void cancel(boolean mayInterruptIfRunning) {
        worker.cancel(mayInterruptIfRunning);
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        worker.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        worker.removePropertyChangeListener(listener);
    }

    @SafeVarargs
    protected final void publish(V... chunks) {
        worker.publishIntermediateResults(chunks);
    }

    protected final void setProgress(int progress) {
        worker.setTaskProgress(progress);
    }

    protected final IController getController() {
        return controller;
    }
}
