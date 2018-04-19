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

import java.beans.PropertyChangeSupport;

/**
 * Observer to publish progress by publishInterval or if the task is completed.
 * Also publish note asynchronously through SwingWorker#publish.
 */
public class TimeDrivenProgressObserver {

    private final PropertyChangeSupport support;

    private final int totalNodeCount;

    private double doneWork;

    private int progressToPublish;

    private long lastTimePublished;

    private final long publishInterval = 500;

    public TimeDrivenProgressObserver(PropertyChangeSupport support,
            int totalNodeCount) {

        this.support = support;
        this.totalNodeCount = totalNodeCount;
    }

    /**
     * 
     * <i>Description:</i> Publish progress by publishInterval or if the task is
     * completed.
     * 
     * @param deltaProgress
     */
    public void publishProgress(int deltaProgress) {
        doneWork += deltaProgress;
        progressToPublish += deltaProgress;

        if (isTimeToPublish() || progressToPublish == totalNodeCount) {
            support.firePropertyChange(PROGRESS_AMOUNT_PROPERTY, null,
                    new Double(doneWork));
            doneWork = 0.0;
        }
    }

    private boolean isTimeToPublish() {
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - lastTimePublished;
        if (interval >= publishInterval) {
            lastTimePublished = currentTime;
            return true;
        }
        return false;
    }

    /**
     * 
     * <i>Description:</i>
     * 
     * @param note
     */
    public void publishNote(String note) {
        support.firePropertyChange(PROGRESS_NOTE_PROPERTY, null, note);
    }
}
