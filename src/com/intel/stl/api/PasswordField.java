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

package com.intel.stl.api;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class PasswordField {

    private static String CLEAR_THREAD_NAME = "pf-thread";

    private static int DEFAULT_LIFE_TIME = 3000; // 3 sec

    private final int lifetime;

    protected final AtomicLong expiration = new AtomicLong();

    private final AtomicReference<char[]> pwd = new AtomicReference<char[]>(
            null);

    protected Thread clearThread;

    public PasswordField() {
        this(DEFAULT_LIFE_TIME);
    }

    public PasswordField(int lifetime) {
        this.lifetime = lifetime;
    }

    public void setPassword(char[] password) {
        char[] currpwd = pwd.get();
        // Set the value atomically; if another thread is updating the
        // AtomicReference, it might take two or three iterations to update the
        // value, no locking.
        while (!pwd.compareAndSet(currpwd, password)) {
            currpwd = pwd.get();
        }
        if (currpwd != null) {
            for (int i = 0; i < currpwd.length; i++) {
                currpwd[i] = '\0';
            }
        }
        if (pwd.get() == null) {
            stopClearThread();
        } else {
            startClearThread();
            resetExpiration();
        }
    }

    public char[] getPassword() {
        resetExpiration();
        char[] work = pwd.get();
        return (work == null ? null : Arrays.copyOf(work, work.length));
    }

    private void startClearThread() {
        if (clearThread == null) {
            clearThread = createThread(new Runnable() {

                @Override
                public void run() {
                    do {
                        try {
                            waitForExpiration();
                        } catch (InterruptedException e) {
                            break;
                        }
                    } while (clearThread != null
                            && !clearThread.isInterrupted()
                            && System.currentTimeMillis() < expiration.get());
                    if (clearThread != null && !clearThread.isInterrupted()) {
                        setPassword(null);
                    }
                }

            });
            clearThread.start();
        }
    }

    protected void stopClearThread() {
        if (clearThread != null) {
            synchronized (clearThread) {
                clearThread.interrupt();
            }
            clearThread = null;
        }
    }

    private void resetExpiration() {
        if (clearThread != null) {
            synchronized (clearThread) {
                expiration.set(System.currentTimeMillis() + lifetime);
                clearThread.notify();
            }
        }
    }

    protected void waitForExpiration() throws InterruptedException {
        synchronized (clearThread) {
            clearThread.wait(expiration.get() - System.currentTimeMillis());
        }
    }

    protected Thread createThread(Runnable runnable) {
        Thread newThread = new Thread(runnable);
        newThread.setName(CLEAR_THREAD_NAME);
        return newThread;
    }
}
