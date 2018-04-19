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
package com.intel.stl.api.performance;

import java.io.Serializable;

/**
 */
public class ImageIdBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private long imageNumber;

    private int imageOffset;

    public ImageIdBean() {
        super();
    }

    public ImageIdBean(long imageNumber, int imageOffset) {
        super();
        this.imageNumber = imageNumber;
        this.imageOffset = imageOffset;
    }

    /**
     * @return the imageNumber
     */
    public long getImageNumber() {
        return imageNumber;
    }

    /**
     * @param imageNumber
     *            the imageNumber to set
     */
    public void setImageNumber(long imageNumber) {
        this.imageNumber = imageNumber;
    }

    /**
     * @return the imageOffset
     */
    public int getImageOffset() {
        return imageOffset;
    }

    /**
     * @param imageOffset
     *            the imageOffset to set
     */
    public void setImageOffset(int imageOffset) {
        this.imageOffset = imageOffset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (imageNumber ^ (imageNumber >>> 32));
        result = prime * result + imageOffset;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ImageIdBean other = (ImageIdBean) obj;
        if (imageNumber != other.imageNumber) {
            return false;
        }
        if (imageOffset != other.imageOffset) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ImageId [imageNumber=0x" + Long.toHexString(imageNumber)
                + ", imageOffset=" + imageOffset + "]";
    }

}
