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

package com.intel.stl.api.management.applications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.intel.stl.api.management.XMLConstants;

@XmlRootElement(name = XMLConstants.APPLICATION)
@XmlAccessorType(XmlAccessType.FIELD)
public class Application {

    @XmlElement(name = XMLConstants.NAME)
    private String name;

    @XmlElements({
            @XmlElement(name = XMLConstants.SERVICEID, type = ServiceID.class),
            @XmlElement(name = XMLConstants.SERVICEID_RANGE,
                    type = ServiceIDRange.class),
            @XmlElement(name = XMLConstants.SERVICEID_MASKED,
                    type = ServiceIDMasked.class) })
    private List<ServiceID> serviceIDs;

    @XmlElements({
            @XmlElement(name = XMLConstants.MGID, type = MGID.class),
            @XmlElement(name = XMLConstants.MGID_RANGE, type = MGIDRange.class),
            @XmlElement(name = XMLConstants.MGID_MASKED,
                    type = MGIDMasked.class) })
    private List<MGID> mgids;

    @XmlElement(name = XMLConstants.SELECT, type = String.class)
    @XmlJavaTypeAdapter(AppSelectAdapter.class)
    private List<AppSelect> selects;

    @XmlElement(name = XMLConstants.INCLUDE_APPLICATION,
            type = IncludeApplication.class)
    private List<IncludeApplication> includeApplications;

    /**
     * Description:
     * 
     */
    public Application() {
    }

    /**
     * Description:
     * 
     * @param name
     */
    public Application(String name) {
        super();
        this.name = name;
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

    public void addServiceID(ServiceID id) {
        if (id == null) {
            throw new IllegalArgumentException("null ServiceID is not allowed.");
        }

        if (serviceIDs == null) {
            serviceIDs = new ArrayList<ServiceID>();
        }
        serviceIDs.add(id);
    }

    /**
     * @return the serviceIDs
     */
    public List<ServiceID> getServiceIDs() {
        return serviceIDs;
    }

    public void addMGID(MGID id) {
        if (id == null) {
            throw new IllegalArgumentException("null MGID is not allowed.");
        }

        if (mgids == null) {
            mgids = new ArrayList<MGID>();
        }
        mgids.add(id);
    }

    /**
     * @return the mgids
     */
    public List<MGID> getMgids() {
        return mgids;
    }

    public void addSelect(AppSelect sel) {
        if (sel == null) {
            throw new IllegalArgumentException("null Select is not allowed.");
        }

        if (selects == null) {
            selects = new ArrayList<AppSelect>();
        }
        selects.add(sel);
    }

    /**
     * @return the selects
     */
    public List<AppSelect> getSelects() {
        return selects;
    }

    public void addIncludeApplication(IncludeApplication name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "null IncludedApplication is not allowed.");
        } else if (name.getValue().equals(this.name)) {
            throw new IllegalArgumentException("Can not reference to itself.");
        }

        if (includeApplications == null) {
            includeApplications = new ArrayList<IncludeApplication>();
        }
        includeApplications.add(name);
    }

    public void addIncludeApplication(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "null IncludedApplication is not allowed.");
        } else if (name.equals(this.name)) {
            throw new IllegalArgumentException("Can not reference to itself.");
        }

        if (includeApplications == null) {
            includeApplications = new ArrayList<IncludeApplication>();
        }
        includeApplications.add(new IncludeApplication(name));
    }

    public void removeIncludeApplication(String name) {
        IncludeApplication ia = new IncludeApplication(name);
        if (includeApplications != null) {
            includeApplications.remove(ia);
        }
    }

    public void insertIncludeApplication(int index, String name) {
        if (includeApplications != null) {
            includeApplications.add(index, new IncludeApplication(name));
        }
    }

    public int indexOfIncludeApplication(String name) {
        IncludeApplication ia = new IncludeApplication(name);
        if (includeApplications != null) {
            return includeApplications.indexOf(ia);
        }
        return -1;
    }

    public boolean doesIncludeApplication(String name) {
        IncludeApplication ia = new IncludeApplication(name);
        if (includeApplications != null) {
            return includeApplications.contains(ia);
        }
        return false;
    }

    /**
     * @return the includedApplications
     */
    public List<IncludeApplication> getIncludeApplications() {
        if (includeApplications == null) {
            return null;
        } else {
            return Collections.unmodifiableList(includeApplications);
        }
    }

    public void clear() {
        if (serviceIDs != null) {
            serviceIDs.clear();
        }
        if (mgids != null) {
            mgids.clear();
        }
        if (selects != null) {
            selects.clear();
        }
        if (includeApplications != null) {
            includeApplications.clear();
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
                        + ((includeApplications == null) ? 0
                                : includeApplications.hashCode());
        result = prime * result + ((mgids == null) ? 0 : mgids.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((selects == null) ? 0 : selects.hashCode());
        result =
                prime * result
                        + ((serviceIDs == null) ? 0 : serviceIDs.hashCode());
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
        Application other = (Application) obj;
        if (includeApplications == null) {
            if (other.includeApplications != null) {
                return false;
            }
        } else if (!includeApplications.equals(other.includeApplications)) {
            return false;
        }
        if (mgids == null) {
            if (other.mgids != null) {
                return false;
            }
        } else if (!mgids.equals(other.mgids)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (selects == null) {
            if (other.selects != null) {
                return false;
            }
        } else if (!selects.equals(other.selects)) {
            return false;
        }
        if (serviceIDs == null) {
            if (other.serviceIDs != null) {
                return false;
            }
        } else if (!serviceIDs.equals(other.serviceIDs)) {
            return false;
        }
        return true;
    }

    public Application copy() {
        Application res = new Application(name);
        if (serviceIDs != null) {
            res.serviceIDs = new ArrayList<ServiceID>(serviceIDs.size());
            for (ServiceID sid : serviceIDs) {
                res.serviceIDs.add(sid.copy());
            }
        }
        if (mgids != null) {
            res.mgids = new ArrayList<MGID>(mgids.size());
            for (MGID mgid : mgids) {
                res.mgids.add(mgid.copy());
            }
        }
        if (selects != null) {
            res.selects = new ArrayList<AppSelect>(selects);
        }
        if (includeApplications != null) {
            res.includeApplications =
                    new ArrayList<IncludeApplication>(
                            includeApplications.size());
            for (IncludeApplication ia : includeApplications) {
                res.includeApplications.add(ia.copy());
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
        return "Application [name=" + name + ", serviceIDs=" + serviceIDs
                + ", mgids=" + mgids + ", selects=" + selects
                + ", includeApplications=" + includeApplications + "]";
    }

}
