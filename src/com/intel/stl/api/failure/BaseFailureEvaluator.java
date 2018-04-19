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

package com.intel.stl.api.failure;

public class BaseFailureEvaluator implements IFailureEvaluator {
    private Class<?>[] recoverableErros;

    private Class<?>[] unrecoverableErros;

    /**
     * @param recoverableErros
     *            the recoverableErros to set
     */
    public void setRecoverableErrors(Class<?>... recoverableErros) {
        this.recoverableErros = recoverableErros;
    }

    /**
     * @param unrecoverableErros
     *            the unrecoverableErros to set
     */
    public void setUnrecoverableErrors(Class<?>... unrecoverableErros) {
        this.unrecoverableErros = unrecoverableErros;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.failure.IFailureEvaluator#getType(java.lang.Throwable)
     */
    @Override
    public FailureType getType(Throwable error) {
        if (error == null) {
            return FailureType.IGNORE;
        } else if (hasError(error, unrecoverableErros)) {
            return FailureType.UNRECOVERABLE;
        } else if (hasError(error, recoverableErros)) {
            return FailureType.RECOVERABLE;
        } else {
            return FailureType.IGNORE;
        }
    }

    protected boolean hasError(Throwable target, Class<?>[] ref) {
        if (ref == null) {
            return false;
        }

        Class<?> targetKlass = target.getClass();
        for (Class<?> klass : ref) {
            if (klass != null && klass.isAssignableFrom(targetKlass)) {
                return true;
            }
        }
        return false;
    }
}
