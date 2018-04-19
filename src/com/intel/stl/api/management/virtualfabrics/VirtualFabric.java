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

package com.intel.stl.api.management.virtualfabrics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.intel.stl.api.management.StringNode;
import com.intel.stl.api.management.XMLConstants;

@XmlRootElement(name = XMLConstants.VIRTUAL_FABRIC)
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualFabric {
    // below are the fixed attributes each VF must have

    @XmlElement(name = XMLConstants.NAME)
    private String name;

    @XmlElement(name = XMLConstants.ENABLE, type = Enable.class)
    private Enable enable;

    @XmlElement(name = XMLConstants.SECURITY, type = Security.class)
    private Security security;

    @XmlElement(name = XMLConstants.QOS, type = Qos.class)
    private Qos qos;

    // below are the optional attributes

    @XmlElement(name = XMLConstants.PKEY, type = PKey.class)
    private PKey pkey;

    @XmlElement(name = XMLConstants.MAX_MTU, type = MaxMtu.class)
    private MaxMtu maxMtu;

    @XmlElement(name = XMLConstants.MAX_RATE, type = MaxRate.class)
    private MaxRate maxRate;

    @XmlElement(name = XMLConstants.STANDBY, type = Standby.class)
    private Standby standby;

    @XmlElement(name = XMLConstants.HIGH_PRIORITY, type = HighPriority.class)
    private HighPriority highPriority;

    @XmlElement(name = XMLConstants.BANDWIDTH, type = Bandwidth.class)
    private Bandwidth bandwidth;

    @XmlElement(name = XMLConstants.PKT_LT_MULT, type = PktLifeTimeMult.class)
    private PktLifeTimeMult pktLifeTimeMult;

    @XmlElement(name = XMLConstants.BASE_SL, type = BaseSL.class)
    private BaseSL baseSL;

    @XmlElement(name = XMLConstants.FLOW_CONTR_DISABLE,
            type = FlowControlDisable.class)
    private FlowControlDisable flowControlDisable;

    @XmlElement(name = XMLConstants.PREEMPT_RANK, type = PreemptRank.class)
    private PreemptRank preemptRank;

    @XmlElement(name = XMLConstants.HOQ_LIFE, type = HoqLife.class)
    private HoqLife hoqLife;

    // the following must have at least one element

    @XmlElements({
            @XmlElement(name = XMLConstants.MEMBER, type = Member.class),
            @XmlElement(name = XMLConstants.LIMITED_MEMBER,
                    type = LimitedMember.class) })
    private List<StringNode> members;

    @XmlElement(name = XMLConstants.APPLICATION, type = ApplicationName.class)
    private List<ApplicationName> applications;

    /**
     * Description:
     * 
     */
    public VirtualFabric() {
        this(null);
    }

    /**
     * Description:
     * 
     * @param name
     */
    public VirtualFabric(String name) {
        super();
        this.name = name;
        enable = new Enable(false);
        security = new Security(false);
        qos = new Qos(false);
        addMember(new Member(""));
        addApplication(new ApplicationName(""));
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the enable
     */
    public Enable getEnable() {
        return enable;
    }

    /**
     * @param enable
     *            the enable to set
     */
    public void setEnable(Enable enable) {
        this.enable = enable;
    }

    /**
     * @return the pkey
     */
    public PKey getPKey() {
        return pkey;
    }

    /**
     * @param pkey
     *            the pkey to set
     */
    public void setPKey(PKey pkey) {
        this.pkey = pkey;
    }

    /**
     * @return the security
     */
    public Security getSecurity() {
        return security;
    }

    /**
     * @param security
     *            the security to set
     */
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * @return the qos
     */
    public Qos getQos() {
        return qos;
    }

    /**
     * @param qos
     *            the qos to set
     */
    public void setQos(Qos qos) {
        this.qos = qos;
    }

    /**
     * @return the maxMtu
     */
    public MaxMtu getMaxMtu() {
        return maxMtu;
    }

    /**
     * @param maxMtu
     *            the maxMtu to set
     */
    public void setMaxMtu(MaxMtu maxMtu) {
        this.maxMtu = maxMtu;
    }

    /**
     * @return the maxRate
     */
    public MaxRate getMaxRate() {
        return maxRate;
    }

    /**
     * @param maxRate
     *            the maxRate to set
     */
    public void setMaxRate(MaxRate maxRate) {
        this.maxRate = maxRate;
    }

    /**
     * @return the standby
     */
    public Standby getStandby() {
        return standby;
    }

    /**
     * @param standby
     *            the standby to set
     */
    public void setStandby(Standby standby) {
        this.standby = standby;
    }

    /**
     * @return the highPriority
     */
    public HighPriority getHighPriority() {
        return highPriority;
    }

    /**
     * @param highPriority
     *            the highPriority to set
     */
    public void setHighPriority(HighPriority highPriority) {
        this.highPriority = highPriority;
    }

    /**
     * @return the bandwidth
     */
    public Bandwidth getBandwidth() {
        return bandwidth;
    }

    /**
     * @param bandwidth
     *            the bandwidth to set
     */
    public void setBandwidth(Bandwidth bandwidth) {
        this.bandwidth = bandwidth;
    }

    /**
     * @return the pktLifeTimeMult
     */
    public PktLifeTimeMult getPktLifeTimeMult() {
        return pktLifeTimeMult;
    }

    /**
     * @param pktLifeTimeMult
     *            the pktLifeTimeMult to set
     */
    public void setPktLifeTimeMult(PktLifeTimeMult pktLifeTimeMult) {
        this.pktLifeTimeMult = pktLifeTimeMult;
    }

    /**
     * @return the baseSL
     */
    public BaseSL getBaseSL() {
        return baseSL;
    }

    /**
     * @param baseSL
     *            the baseSL to set
     */
    public void setBaseSL(BaseSL baseSL) {
        this.baseSL = baseSL;
    }

    /**
     * @return the flowControlDisable
     */
    public FlowControlDisable getFlowControlDisable() {
        return flowControlDisable;
    }

    /**
     * @param flowControlDisable
     *            the flowControlDisable to set
     */
    public void setFlowControlDisable(FlowControlDisable flowControlDisable) {
        this.flowControlDisable = flowControlDisable;
    }

    /**
     * @return the preemptRank
     */
    public PreemptRank getPreemptRank() {
        return preemptRank;
    }

    /**
     * @param preemptRank
     *            the preemptRank to set
     */
    public void setPreemptRank(PreemptRank preemptRank) {
        this.preemptRank = preemptRank;
    }

    /**
     * @return the hoqLife
     */
    public HoqLife getHoqLife() {
        return hoqLife;
    }

    /**
     * @param hoqLife
     *            the hoqLife to set
     */
    public void setHoqLife(HoqLife hoqLife) {
        this.hoqLife = hoqLife;
    }

    /**
     * @return the members
     */
    public List<StringNode> getMembers() {
        return members;
    }

    /**
     * @param members
     *            the members to set
     */
    public void addMember(StringNode member) {
        if (members == null) {
            members = new ArrayList<StringNode>();
        }
        members.add(member);
    }

    /**
     * @return the application
     */
    public List<ApplicationName> getApplications() {
        return applications;
    }

    /**
     * @param application
     *            the application to set
     */
    public void addApplication(ApplicationName application) {
        if (applications == null) {
            applications = new ArrayList<ApplicationName>();
        }
        applications.add(application);
    }

    public void clear() {
        pkey = null;
        maxMtu = null;
        maxRate = null;
        standby = null;
        highPriority = null;
        bandwidth = null;
        pktLifeTimeMult = null;
        baseSL = null;
        flowControlDisable = null;
        preemptRank = null;
        hoqLife = null;
        if (members != null) {
            members.clear();
        }
        if (applications != null) {
            applications.clear();
        }
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
        result =
                prime
                        * result
                        + ((applications == null) ? 0 : applications.hashCode());
        result =
                prime * result
                        + ((bandwidth == null) ? 0 : bandwidth.hashCode());
        result = prime * result + ((baseSL == null) ? 0 : baseSL.hashCode());
        result = prime * result + ((enable == null) ? 0 : enable.hashCode());
        result =
                prime
                        * result
                        + ((flowControlDisable == null) ? 0
                                : flowControlDisable.hashCode());
        result =
                prime
                        * result
                        + ((highPriority == null) ? 0 : highPriority.hashCode());
        result = prime * result + ((hoqLife == null) ? 0 : hoqLife.hashCode());
        result = prime * result + ((maxMtu == null) ? 0 : maxMtu.hashCode());
        result = prime * result + ((maxRate == null) ? 0 : maxRate.hashCode());
        result = prime * result + ((members == null) ? 0 : members.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((pkey == null) ? 0 : pkey.hashCode());
        result =
                prime
                        * result
                        + ((pktLifeTimeMult == null) ? 0 : pktLifeTimeMult
                                .hashCode());
        result =
                prime * result
                        + ((preemptRank == null) ? 0 : preemptRank.hashCode());
        result = prime * result + ((qos == null) ? 0 : qos.hashCode());
        result =
                prime * result + ((security == null) ? 0 : security.hashCode());
        result = prime * result + ((standby == null) ? 0 : standby.hashCode());
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
        VirtualFabric other = (VirtualFabric) obj;
        if (applications == null) {
            if (other.applications != null) {
                return false;
            }
        } else if (!applications.equals(other.applications)) {
            return false;
        }
        if (bandwidth == null) {
            if (other.bandwidth != null) {
                return false;
            }
        } else if (!bandwidth.equals(other.bandwidth)) {
            return false;
        }
        if (baseSL == null) {
            if (other.baseSL != null) {
                return false;
            }
        } else if (!baseSL.equals(other.baseSL)) {
            return false;
        }
        if (enable == null) {
            if (other.enable != null) {
                return false;
            }
        } else if (!enable.equals(other.enable)) {
            return false;
        }
        if (flowControlDisable == null) {
            if (other.flowControlDisable != null) {
                return false;
            }
        } else if (!flowControlDisable.equals(other.flowControlDisable)) {
            return false;
        }
        if (highPriority == null) {
            if (other.highPriority != null) {
                return false;
            }
        } else if (!highPriority.equals(other.highPriority)) {
            return false;
        }
        if (hoqLife == null) {
            if (other.hoqLife != null) {
                return false;
            }
        } else if (!hoqLife.equals(other.hoqLife)) {
            return false;
        }
        if (maxMtu == null) {
            if (other.maxMtu != null) {
                return false;
            }
        } else if (!maxMtu.equals(other.maxMtu)) {
            return false;
        }
        if (maxRate == null) {
            if (other.maxRate != null) {
                return false;
            }
        } else if (!maxRate.equals(other.maxRate)) {
            return false;
        }
        if (members == null) {
            if (other.members != null) {
                return false;
            }
        } else if (!members.equals(other.members)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (pkey == null) {
            if (other.pkey != null) {
                return false;
            }
        } else if (!pkey.equals(other.pkey)) {
            return false;
        }
        if (pktLifeTimeMult == null) {
            if (other.pktLifeTimeMult != null) {
                return false;
            }
        } else if (!pktLifeTimeMult.equals(other.pktLifeTimeMult)) {
            return false;
        }
        if (preemptRank == null) {
            if (other.preemptRank != null) {
                return false;
            }
        } else if (!preemptRank.equals(other.preemptRank)) {
            return false;
        }
        if (qos == null) {
            if (other.qos != null) {
                return false;
            }
        } else if (!qos.equals(other.qos)) {
            return false;
        }
        if (security == null) {
            if (other.security != null) {
                return false;
            }
        } else if (!security.equals(other.security)) {
            return false;
        }
        if (standby == null) {
            if (other.standby != null) {
                return false;
            }
        } else if (!standby.equals(other.standby)) {
            return false;
        }
        return true;
    }

    /**
     * <i>Description:</i>
     * 
     * @return
     */
    public VirtualFabric copy() {
        VirtualFabric res = new VirtualFabric(name);
        res.enable = enable.copy();
        res.security = security.copy();
        res.qos = qos.copy();
        if (pkey != null) {
            res.pkey = pkey.copy();
        }
        if (maxMtu != null) {
            res.maxMtu = maxMtu.copy();
        }
        if (maxRate != null) {
            res.maxRate = maxRate.copy();
        }
        if (standby != null) {
            res.standby = standby.copy();
        }
        if (highPriority != null) {
            res.highPriority = highPriority.copy();
        }
        if (bandwidth != null) {
            res.bandwidth = bandwidth.copy();
        }
        if (pktLifeTimeMult != null) {
            res.pktLifeTimeMult = pktLifeTimeMult.copy();
        }
        if (baseSL != null) {
            res.baseSL = baseSL.copy();
        }
        if (flowControlDisable != null) {
            res.flowControlDisable = flowControlDisable.copy();
        }
        if (preemptRank != null) {
            res.preemptRank = preemptRank.copy();
        }
        if (hoqLife != null) {
            res.hoqLife = hoqLife.copy();
        }
        if (members != null) {
            res.members = new ArrayList<StringNode>(members.size());
            for (StringNode member : members) {
                res.members.add(member.copy());
            }
        }
        if (applications != null) {
            res.applications =
                    new ArrayList<ApplicationName>(applications.size());
            for (ApplicationName an : applications) {
                res.applications.add(an.copy());
            }
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualFabric [name=" + name + ", enable=" + enable + ", pkey="
                + pkey + ", security=" + security + ", qos=" + qos
                + ", maxMtu=" + maxMtu + ", maxRate=" + maxRate + ", standby="
                + standby + ", highPriority=" + highPriority + ", bandwidth="
                + bandwidth + ", pktLifeTimeMult=" + pktLifeTimeMult
                + ", baseSL=" + baseSL + ", flowControlDisable="
                + flowControlDisable + ", preemptRank=" + preemptRank
                + ", hoqLife=" + hoqLife + ", members=" + members
                + ", applications=" + applications + "]";
    }

}
