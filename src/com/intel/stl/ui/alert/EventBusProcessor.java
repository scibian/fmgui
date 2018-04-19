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

package com.intel.stl.ui.alert;

import java.util.Collection;
import java.util.List;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.api.configuration.EventType;
import com.intel.stl.api.notice.EventDescription;
import com.intel.stl.ui.framework.AbstractEvent;
import com.intel.stl.ui.framework.IAppEvent;

public abstract class EventBusProcessor<E extends AbstractEvent> extends
        EventProcesser {
    private final static boolean DEBUG = false;

    private final MBassador<IAppEvent> eventBus;

    private final Collection<E> eventsToSend;

    private final BusWorker worker;

    private boolean toStop;

    /**
     * Description:
     * 
     * @param eventBus
     */
    public EventBusProcessor(MBassador<IAppEvent> eventBus) {
        super();
        this.eventBus = eventBus;
        setTargetTypes(getTargetTypes());
        eventsToSend = createBusEventCollection();

        worker = new BusWorker();
        startWorker(worker);
    }

    protected void startWorker(Runnable worker) {
        Thread thread = new Thread(worker);
        thread.setDaemon(true);
        thread.start();
    }

    protected abstract EventType[] getTargetTypes();

    protected abstract Collection<E> createBusEventCollection();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.alert.EventProcesser#processEvent(com.intel.stl.api.
     * notice.EventDescription)
     */
    @Override
    protected void processEvents(List<EventDescription> evts) {
        if (toStop) {
            return;
        }

        E event = toBusEvent(evts);
        synchronized (eventsToSend) {
            eventsToSend.add(event);
        }
    }

    protected abstract E toBusEvent(List<EventDescription> evts);

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.alert.EventProcesser#finishProcess()
     */
    @Override
    protected void finishProcess() {
        if (toStop) {
            return;
        }

        synchronized (worker) {
            worker.notify();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.alert.EventProcesser#clearup()
     */
    @Override
    public void cleanup() {
        synchronized (eventsToSend) {
            eventsToSend.clear();
        }
        synchronized (worker) {
            worker.toStop();
            worker.notify();
        }
    }

    class BusWorker implements Runnable {

        public void toStop() {
            toStop = true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            while (!toStop) {
                Object[] events = null;
                synchronized (eventsToSend) {
                    events = eventsToSend.toArray();
                    eventsToSend.clear();
                }
                if (events != null && events.length > 0) {
                    if (DEBUG) {
                        System.out.println("Processing " + events.length
                                + " events...");
                    }
                    for (Object event : events) {
                        if (DEBUG) {
                            System.out.println("Publish  " + event);
                        }
                        eventBus.publish((E) event);
                    }
                } else {
                    synchronized (this) {
                        try {
                            if (!toStop) {
                                wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }
}
