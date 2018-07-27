/**
 * Copyright (c) 2017, Intel Corporation
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

package com.intel.stl.fecdriver.messages.adapter.sa;

import com.intel.stl.api.subnet.DDCableInfoBean;
import com.intel.stl.common.StringUtils;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * @formatter:on
 * <pre>
 * <vendorName>    Vendor name                             ASCII           P0 B129
 * <vendorOUI>     Vendor OUI                              Unsigned int    P0 B145
 * <vendorPN>      Vendor part number                      ASCII           P0 B148
 * <vendorRev>     Vendor rev                              ASCII           P0 B164
 * <vendorSN>      Vendor serial number                    ASCII           P0 B166
 * <dateCodeYY>    Vendor manufacturing date code year     ASCII           P0 B182
 * <dateCodeMM>    Vendor manufacturing date code month    ASCII           P0 B184
 * <dateCodeDD>    Vendor manufacturing date code day      ASCII           P0 B186
 * <dateCodeSP>    Vendor manufacturing lot code           ASCII           P0 B188
 * <powerClassLow> Power class, <= 3.5W                    Encoding        P0 B201
 * <cableLength>   Cable assembly length                   Unsigned int    P0 B202
 * <mediaConnType> Connector type for connection           Encoding        P0 B203
 *                 to bulk media
 * <cableType>     Cable type (optics/passive/active Cu)   Encoding        P0 B212
 *
 * <pre>
 * @formatter:off
 */
public class CableInfoDD extends SimpleDatagram<DDCableInfoBean> {

    public CableInfoDD() {
        super(128);
    }

    @Override
    public DDCableInfoBean toObject() {
        DDCableInfoBean bean = new DDCableInfoBean();

        bean.setId(buffer.get());
        byte[] byteVals = new byte[16];
        buffer.get(byteVals);
        bean.setVendorName(StringUtils.toString(byteVals, 0, 16));
        byteVals = new byte[3];
        buffer.get(byteVals);
        bean.setVendorOui(byteVals);
        byteVals = new byte[16];
        buffer.get(byteVals);
        bean.setVendorPn(StringUtils.toString(byteVals, 0, 16));
        byteVals = new byte[2];
        buffer.get(byteVals);
        bean.setVendorRev(StringUtils.toString(byteVals, 0, 2));
        byteVals = new byte[16];
        buffer.get(byteVals);
        bean.setVendorSN(StringUtils.toString(byteVals, 0, 16));
        byteVals = new byte[8];
        buffer.get(byteVals);
        String year = StringUtils.toString(byteVals, 0, 2);
        String month = StringUtils.toString(byteVals, 2, 2);
        String day = StringUtils.toString(byteVals, 4, 2);
        String lot = StringUtils.toString(byteVals, 6, 2);
        bean.setDateCode(year, month, day, lot);
        buffer.position(73);
        bean.setMaxPower(buffer.get());
        bean.setCableLength(buffer.get());
        bean.setConnector(buffer.get());
        buffer.position(84);
        bean.setXmitTech(buffer.get());
        return bean;
    }

}
