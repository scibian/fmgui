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

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.intel.stl.api.notice.NoticeAttrBean;
import com.intel.stl.api.notice.NoticeBean;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "noticeType",
        discriminatorType = DiscriminatorType.INTEGER)
@Table(name = "NOTICES", indexes = { @Index(name = "IDX_NOTICE_STATUS",
        columnList = "noticeStatus") })
public abstract class NoticeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private NoticeId id = new NoticeId();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subnetId", insertable = false, updatable = false)
    private SubnetRecord subnet;

    @Column(name = "noticeType", insertable = false, updatable = false)
    private int noticeType;

    @Column(length = 32)
    @Enumerated(STRING)
    private NoticeStatus noticeStatus;

    @Embedded
    private NoticeBean notice;

    public NoticeRecord() {
    }

    public NoticeRecord(NoticeBean notice) {
        setNoticeFields(notice);
        this.notice = notice;
    }

    public NoticeId getId() {
        return id;
    }

    public void setId(NoticeId id) {
        this.id = id;
    }

    public SubnetRecord getSubnet() {
        return subnet;
    }

    public void setSubnet(SubnetRecord subnet) {
        this.id.setFabricId(subnet.getId());
        this.subnet = subnet;
    }

    /**
     * 
     * Description: returns the notice discriminator value.
     * 
     * @return either 1 (Generic) or 2 (Vendor) - See specific concrete classes
     *         extending this abstract class
     */
    public int getNoticeType() {
        return noticeType;
    }

    /**
     * 
     * Description: sets the notice type. Please note that this value is set
     * internally by Hibernate according to the concrete implementation being
     * saved, so there is no point on setting it.
     * 
     * @param noticeType
     */
    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }

    /**
     * @return the noticeStatus
     */
    public NoticeStatus getNoticeStatus() {
        return noticeStatus;
    }

    /**
     * @param noticeStatus
     *            the noticeStatus to set
     */
    public void setNoticeStatus(NoticeStatus noticeStatus) {
        this.noticeStatus = noticeStatus;
    }

    public NoticeBean getNotice() {
        NoticeAttrBean attributes = getNoticeAttributes();
        // The following fields were transient, so, need to
        // be filled with NoticeRecord info.
        notice.setAttributes(attributes);
        notice.setId(id.getNoticeId());
        return notice;
    }

    public void setNotice(NoticeBean notice) {
        setNoticeFields(notice);
        this.notice = notice;
    }

    public abstract NoticeAttrBean getNoticeAttributes();

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
        NoticeRecord other = (NoticeRecord) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    private void setNoticeFields(NoticeBean notice) {
        NoticeAttrBean attributes = notice.getAttributes();
        if (attributes == null) {
            // TODO Create message for this
            throw new IllegalArgumentException(
                    "No NoticeAttrBean attached to NoticeBean");
        }
        this.id.setNoticeId(notice.getId());
    }
}
