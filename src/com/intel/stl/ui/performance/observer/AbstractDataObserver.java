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

package com.intel.stl.ui.performance.observer;

import com.intel.stl.api.performance.ErrStatBean;
import com.intel.stl.api.performance.GroupInfoBean;
import com.intel.stl.api.performance.UtilStatsBean;
import com.intel.stl.api.performance.VFInfoBean;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.performance.item.IPerformanceItem;

public abstract class AbstractDataObserver<E, I extends IPerformanceItem<?>>
        implements IDataObserver<E> {
    protected I controller;

    protected volatile DataType type;

    public AbstractDataObserver(I controller) {
        this(controller, DataType.ALL);
    }

    /**
     * Description:
     * 
     * @param controller
     * @param type
     */
    public AbstractDataObserver(I controller, DataType type) {
        super();
        this.controller = controller;
        this.type = type;
    }

    /**
     * @param type
     *            the type to set
     */
    @Override
    public void setType(DataType type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    @Override
    public DataType getType() {
        return type;
    }

    protected UtilStatsBean[] getUtilStatsBeans(GroupInfoBean bean,
            DataType type) {
        switch (type) {
            case INTERNAL:
                return new UtilStatsBean[] { bean.getInternalUtilStats() };
            case TRANSMIT:
                return new UtilStatsBean[] { bean.getSendUtilStats() };
            case RECEIVE:
                return new UtilStatsBean[] { bean.getRecvUtilStats() };
            case EXTERNAL:
                return new UtilStatsBean[] { bean.getSendUtilStats(),
                        bean.getRecvUtilStats() };
            case ALL:
                return new UtilStatsBean[] { bean.getInternalUtilStats(),
                        bean.getSendUtilStats(), bean.getRecvUtilStats() };
        }
        throw new UnsupportedOperationException("Unknown Type " + type);
    }

    protected ErrStatBean[] getErrStatBeans(GroupInfoBean bean, DataType type) {
        switch (type) {
            case INTERNAL:
                return new ErrStatBean[] { bean.getInternalErrors() };
            case EXTERNAL:
                return new ErrStatBean[] { bean.getExternalErrors() };
            case ALL:
                return new ErrStatBean[] { bean.getInternalErrors(),
                        bean.getExternalErrors() };
            case TRANSMIT:
            case RECEIVE:
                throw new IllegalArgumentException("Unsupported Type " + type);
        }
        throw new UnsupportedOperationException("Unknown Type " + type);
    }

    protected UtilStatsBean[] getUtilStatsBeans(VFInfoBean bean, DataType type) {
        switch (type) {
            case ALL:
            case INTERNAL:
                return new UtilStatsBean[] { bean.getInternalUtilStats() };
            case EXTERNAL:
            case TRANSMIT:
            case RECEIVE:
                throw new IllegalArgumentException("Unsupported Type " + type);
        }
        throw new UnsupportedOperationException("Unknown Type " + type);
    }

    protected ErrStatBean[] getErrStatBeans(VFInfoBean bean, DataType type) {
        switch (type) {
            case INTERNAL:
            case ALL:
                return new ErrStatBean[] { bean.getInternalErrors() };
            case TRANSMIT:
            case RECEIVE:
            case EXTERNAL:
                throw new IllegalArgumentException("Unsupported Type " + type);
        }
        throw new UnsupportedOperationException("Unknown Type " + type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.performance.observer.IDataObserver#reset()
     */
    @Override
    public void reset() {
        // do nothing
    }
}
