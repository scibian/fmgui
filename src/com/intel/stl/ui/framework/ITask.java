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

/*
 * A task that performs work for a controller in the MVC framework
 */
public interface ITask {

    /**
     * 
     * <i>Description:</i> executes this task through a Swing worker
     * 
     * @param owner
     *            the controller that owns this task
     */
    void execute(IController owner);

    /**
     * 
     * <i>Description:</i> returns the submission status of this task
     * 
     * @return boolean
     */
    boolean isSubmitted();

    /**
     * 
     * <i>Description:</i> returns the completion status of this task
     * 
     * @return boolean
     */
    boolean isDone();

    /**
     * 
     * <i>Description:</i> returns the cancellation status of this task
     * 
     * @return boolean
     */
    boolean isCancelled();

    /**
     * 
     * <i>Description:</i> cancels this task
     * 
     * @param mayInterruptIfRunning
     *            flag to cancel this task even if it is running
     */
    void cancel(boolean mayInterruptIfRunning);

    /**
     * 
     * <i>Description:</i> adds a PropertyChangeListener for this task
     * 
     * @param listener
     *            the listener interested in property changes
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * 
     * <i>Description:</i> removes a PropertyChangeListener
     * 
     * @param listener
     *            the listener no longer interested in property changes
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * 
     * <i>Description:</i> fires a PropertyChange event
     * 
     * @param propertyName
     *            the name of the property changing
     * @param oldValue
     *            the old value the property had
     * @param newValue
     *            the new value the property has
     */
    void firePropertyChange(String propertyName, Object oldValue,
            Object newValue);

}
