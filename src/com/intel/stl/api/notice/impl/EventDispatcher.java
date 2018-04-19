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

package com.intel.stl.api.notice.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.intel.stl.api.notice.EventDescription;
import com.intel.stl.api.notice.IEventListener;

public class EventDispatcher extends Thread {
    private final static Logger log = LoggerFactory
            .getLogger(EventDispatcher.class);

    private final Object mutex = new Object();

    private boolean stop = false;

    private final List<EventDescription> events =
            new ArrayList<EventDescription>();

    private final List<IEventListener<EventDescription>> eventListeners =
            new CopyOnWriteArrayList<IEventListener<EventDescription>>();

    private Map<String, String> loggingContextMap;

    public void addEvents(List<EventDescription> newEvents) {
        if (newEvents == null || newEvents.isEmpty()) {
            return;
        }

        try {
            synchronized (events) {
                events.addAll(newEvents);
            }
        } finally {
            synchronized (mutex) {
                mutex.notify();
            }
        }

    }

    /**
     * @return the stop
     */
    public boolean isStop() {
        return stop;
    }

    /**
     * @param stop
     *            the stop to set
     */
    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void addEventListener(IEventListener<EventDescription> listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(IEventListener<EventDescription> listener) {
        eventListeners.remove(listener);
    }

    public void cleanup() {
        stop = true;
        eventListeners.clear();
        synchronized (events) {
            events.clear();
        }
        synchronized (mutex) {
            mutex.notify();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (loggingContextMap != null) {
            MDC.setContextMap(loggingContextMap);
        }
        log.info("Notice EventDispather '" + getName() + "' started");
        stop = false;
        while (!stop) {
            EventDescription[] toProcess = null;
            synchronized (events) {
                toProcess = events.toArray(new EventDescription[0]);
                events.clear();
            }
            if (toProcess != null && toProcess.length > 0) {
                for (EventDescription event : toProcess) {
                    log.info("Notify event: " + event);
                }
                for (IEventListener<EventDescription> listener : eventListeners) {
                    listener.onNewEvent(toProcess);
                }
            } else {
                synchronized (mutex) {
                    try {
                        if (!stop) {
                            mutex.wait();
                        }
                    } catch (InterruptedException e) {
                        log.warn("Notice EventDispatcher interrupted!");
                    }
                }
            }
        }
        log.info("Notice EventDispather '" + getName() + "' stopped");
    }

    public void setLoggingContextMap(Map<String, String> loggingContextMap) {
        this.loggingContextMap = loggingContextMap;
    }

}
