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
package com.intel.stl.fecdriver.messages.command;

import com.intel.stl.fecdriver.messages.adapter.sa.GID;

/**
 */
public class InputLid extends InputArgument {
    private final GID<?> sourceGid;

    private final int lid;

    public InputLid(int lid) {
        this(null, lid);
    }

    public InputLid(GID<?> sourceGid, int lid) {
        super();
        this.sourceGid = sourceGid;
        this.lid = lid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vieo.fv.message.command.argument.InputArgument#getType()
     */
    @Override
    public InputType getType() {
        return InputType.InputTypeLid;
    }

    /**
     * @return the sourceGid
     */
    @Override
    public GID<?> getSourceGid() {
        return sourceGid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vieo.fv.message.command.argument.InputArgument#getLid()
     */
    @Override
    public int getLid() {
        return lid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InputLid [sourceGid=" + sourceGid + ", lid=" + lid + "]";
    }

}
