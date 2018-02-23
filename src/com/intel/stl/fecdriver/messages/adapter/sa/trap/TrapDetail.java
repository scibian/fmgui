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

package com.intel.stl.fecdriver.messages.adapter.sa.trap;

import java.nio.ByteBuffer;

import com.intel.stl.api.notice.TrapCapabilityBean;
import com.intel.stl.api.notice.TrapKeyBean;
import com.intel.stl.api.notice.TrapLinkBean;
import com.intel.stl.api.notice.TrapMKeyBean;
import com.intel.stl.api.notice.TrapSwitchPKeyBean;
import com.intel.stl.api.notice.TrapSysguidBean;
import com.intel.stl.api.subnet.GIDBean;
import com.intel.stl.fecdriver.messages.adapter.sa.GID;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm_types.h
 * commit a86e948b247e4d9fd98434e350b00f112ba93c39
 * date 2017-08-16 10:28:01
 *
 * typedef struct {
 *     IB_GID      Gid;
 * } PACK_SUFFIX STL_TRAP_GID;
 *
 * #define STL_TRAP_GID_NOW_IN_SERVICE_DATA STL_TRAP_GID
 * #define STL_TRAP_GID_OUT_OF_SERVICE_DATA STL_TRAP_GID
 * #define STL_TRAP_GID_ADD_MULTICAST_GROUP_DATA STL_TRAP_GID
 * #define STL_TRAP_GID_DEL_MULTICAST_GROUP_DATA STL_TRAP_GID
 * </pre>
 *
 * @param data
 * @return
 */
public class TrapDetail {
    public static GIDBean getGID(byte[] data) {
        GID.Global gid = new GID.Global();
        gid.wrap(data, 0);
        return gid.toObject();
    }

    /**
     * Description:
     *
     * <pre>
     * ref:/ALL_EMB/IbAcess/Common/Inc/stl_sm.h
     *
     * typedef struct {
     *     uint32      Lid;
     * } PACK_SUFFIX STL_TRAP_PORT_CHANGE_STATE_DATA;
     * </pre>
     *
     * @param data
     * @return
     */
    public static int getLid(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, 4);
        return buffer.getInt();
    }

    public static TrapLinkBean getTrapLink(byte[] data) {
        TrapLink link = new TrapLink();
        link.wrap(data, 0);
        return link.toObject();
    }

    public static TrapCapabilityBean getTrapCapability(byte[] data) {
        TrapCapability cap = new TrapCapability();
        cap.wrap(data, 0);
        return cap.toObject();
    }

    /**
     * Description:
     *
     * <pre>
     * ref:/ALL_EMB/IbAcess/Common/Inc/stl_sm.h
     *
     * typedef struct {
     *     uint64      SystemImageGuid;
     *     uint32      Lid;
     * } PACK_SUFFIX STL_TRAP_SYSGUID_CHANGE_DATA;
     * </pre>
     *
     * @param data
     * @return
     */
    public static TrapSysguidBean getTrapSysguid(byte[] data) {
        TrapSysguid sysguid = new TrapSysguid();
        sysguid.wrap(data, 0);
        return sysguid.toObject();
    }

    public static TrapMKeyBean getTrapMKey(byte[] data) {
        TrapMKey mKey = new TrapMKey();
        mKey.wrap(data, 0);
        return mKey.toObject();
    }

    public static TrapKeyBean getTrapKey(byte[] data) {
        TrapKey key = new TrapKey();
        key.wrap(data, 0);
        return key.toObject();
    }

    public static TrapSwitchPKeyBean getTrapSwitchPKey(byte[] data) {
        TrapSwitchPKey key = new TrapSwitchPKey();
        key.wrap(data, 0);
        return key.toObject();
    }

    /**
     *
     * Description:
     *
     * <pre>
     * ref:/ALL_EMB/IbAcess/Common/Inc/stl_sm.h
     *
     * LinkWidth of at least one port of switch at <ReportingLID> has changed
     * typedef struct {
     *     uint32  ReportingLID;
     * } PACK_SUFFIX STL_SMA_TRAP_DATA_LINK_WIDTH;
     * </pre>
     *
     * @param data
     * @return
     */
    public static int getReportingLid(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, 4);
        return buffer.getInt();
    }
}
