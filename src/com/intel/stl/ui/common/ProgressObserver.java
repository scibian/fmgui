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

package com.intel.stl.ui.common;

import static com.intel.stl.ui.main.FabricController.PROGRESS_AMOUNT_PROPERTY;
import static com.intel.stl.ui.main.FabricController.PROGRESS_NOTE_PROPERTY;

import com.intel.stl.ui.framework.ITask;

public class ProgressObserver implements IProgressObserver {

    private final ITask worker;

    private final ProgressObserver parent;

    private final int estimatedWork;

    private double doneWork;

    public ProgressObserver(ITask worker) {
        this(worker, 100);
    }

    public ProgressObserver(ITask worker, int estimatedWork) {
        this(worker, null, estimatedWork);
    }

    public ProgressObserver(ITask worker, ProgressObserver parent,
            int estimatedWork) {
        if (estimatedWork < 0) {
            throw new IllegalArgumentException("Invalid estimated work value '"
                    + estimatedWork + "'. It must be a positive number");
        }
        this.worker = worker;
        this.parent = parent;
        this.estimatedWork = estimatedWork;
        this.doneWork = 0.0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IProgressObserver#setProgress(double)
     */
    @Override
    public void publishProgress(double percentage) {
        if (percentage < 0.0 || percentage > 1.0) {
            throw new IllegalArgumentException("Invalid percentage '"
                    + percentage + "'");
        }
        double amountDone = percentage * estimatedWork;
        double amountToPublish = amountDone - doneWork;
        reportProgress(amountToPublish);
    }

    protected void reportProgress(double amount) {
        doneWork = doneWork + amount;
        if (parent != null) {
            parent.reportProgress(amount);
            return;
        }
        worker.firePropertyChange(PROGRESS_AMOUNT_PROPERTY, null, new Double(
                amount));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IProgressObserver#setNote(java.lang.String)
     */
    @Override
    public void publishNote(String note) {
        worker.firePropertyChange(PROGRESS_NOTE_PROPERTY, null, note);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IProgressObserver#isCanceled()
     */
    @Override
    public boolean isCancelled() {
        if (worker != null) {
            return worker.isCancelled();
        } else {
            return false;
        }
    }

    @Override
    public void onFinish() {
        publishProgress(1.0);
    }

    @Override
    public IProgressObserver[] createSubObservers(int size) {
        IProgressObserver[] res = new IProgressObserver[size];
        int workToBeDone = estimatedWork - (int) doneWork;
        int step;
        if (workToBeDone > size) {
            step = Math.round((float) workToBeDone / (float) size);

        } else {
            step = 1;
        }
        for (int i = 0; i < size; i++) {
            if (workToBeDone > step) {
                workToBeDone = workToBeDone - step;
            } else {
                step = workToBeDone;
            }
            res[i] = new ProgressObserver(worker, this, step);
        }
        return res;
    }

    public static IProgressObserver emptyObserver() {
        return new ProgressObserver(null);
    }
}
