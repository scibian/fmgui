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

public class AbstractModel implements IModel {
    //
    // private final List<IModelListener> listeners;
    //
    // public AbstractModel() {
    // this.listeners = new ArrayList<IModelListener>();
    // }
    //
    // public void addModelListener(final IModelListener listener) {
    // if (!listeners.contains(listener)) {
    // listeners.add(listener);
    // notifyModelChanged(listener);
    // }
    // }
    //
    // public boolean removeModelListener(final IModelListener listener) {
    // return listeners.remove(listener);
    // }
    //
    // @Override
    // public void notifyModelChanged() {
    // for (final IModelListener listener : listeners) {
    // notifyModelChanged(listener);
    // }
    // }
    //
    // @Override
    // public void notifyModelUpdateFailed(Throwable caught) {
    // for (final IModelListener listener : listeners) {
    // notifyModelUpdateFailed(listener, caught);
    // }
    // }
    //
    // protected void notifyModelChanged(final IModelListener listener) {
    // listener.modelChanged(this);
    // }
    //
    // protected void notifyModelUpdateFailed(final IModelListener listener,
    // Throwable caught) {
    // listener.modelUpdateFailed(this, caught);
    // }
    //
}
