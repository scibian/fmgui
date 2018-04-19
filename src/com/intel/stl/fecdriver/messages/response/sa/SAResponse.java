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
package com.intel.stl.fecdriver.messages.response.sa;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.fecdriver.messages.adapter.ComposedDatagram;
import com.intel.stl.fecdriver.messages.adapter.IDatagram;
import com.intel.stl.fecdriver.messages.adapter.sa.SAData;
import com.intel.stl.fecdriver.messages.response.FVResponse;

/**
 */
public abstract class SAResponse<V, E extends IDatagram<V>> extends
        FVResponse<V> {
    private static boolean DEBUG = false;

    /*
     * (non-Javadoc)
     * 
     * @see com.vieo.fv.message.FVResponse#wrap(byte[], int)
     */
    @Override
    protected IDatagram<?> wrap(byte[] bytes, int offset, int totalLength) {
        SAData saData = new SAData();
        int pos = saData.wrap(bytes, offset);
        int recSize = saData.getSaHeader().getAttributeOffset() * 8;
        int numRecs =
                (isSingleRecord() || recSize == 0) ? 1 : (totalLength - pos)
                        / recSize;
        IDatagram<?>[] records = new IDatagram<?>[numRecs];
        List<V> results = new ArrayList<V>();
        for (int i = 0; i < numRecs; i++) {
            E record = createRecord();
            record.wrap(bytes, pos);
            records[i] = record;
            results.add(record.toObject());
            pos += recSize;
        }
        setResults(results);
        ComposedDatagram<Void> data = new ComposedDatagram<Void>(records);
        saData.setData(data);
        if (DEBUG) {
            System.out.println("#Rec " + numRecs);
            saData.dump("", System.out);
        }
        return saData;
    }

    protected boolean isSingleRecord() {
        return false;
    }

    protected abstract E createRecord();

}
