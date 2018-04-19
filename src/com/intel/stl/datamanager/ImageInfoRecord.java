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

package com.intel.stl.datamanager;

import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.intel.stl.api.performance.ImageInfoBean;

/**
 * ImageIdBean contains a field called imageNumber; this is a number that goes
 * up as the FM sweeps the fabric, but it's not unique, it restarts when the FM
 * is restarted. So we use sweepStart as the id for ImageInfoRecord (it's a
 * timestamp); however, we will need an index to access by image number, which
 * won't be unique. When querying by this index you might get more than one
 * ImageInfoRecord.
 */
@Entity
@Table(name = "IMAGE_INFOS", indexes = { @Index(name = "IDX_IMAGE_NUM",
        columnList = "imageNumber", unique = false) })
public class ImageInfoRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ImageInfoId id = new ImageInfoId();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subnetId", insertable = false, updatable = false)
    private SubnetRecord subnet;

    @Embedded
    private ImageInfoBean imageInfo;

    public ImageInfoRecord() {
    }

    public ImageInfoRecord(long subnetId, ImageInfoBean imageInfo) {
        this.id.setFabricId(subnetId);
        setImageInfoFields(imageInfo);
        this.imageInfo = imageInfo;
    }

    public ImageInfoId getId() {
        return id;
    }

    public void setId(ImageInfoId id) {
        this.id = id;
    }

    public SubnetRecord getSubnet() {
        return subnet;
    }

    public void setSubnet(SubnetRecord subnet) {
        this.subnet = subnet;
    }

    public ImageInfoBean getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(ImageInfoBean imageInfo) {
        setImageInfoFields(imageInfo);
        this.imageInfo = imageInfo;
    }

    private void setImageInfoFields(ImageInfoBean imageInfo) {
        id.setSweepTimestamp(imageInfo.getSweepStart());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageInfoRecord other = (ImageInfoRecord) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
