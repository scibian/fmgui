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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.intel.stl.api.management.XMLConstants;

@XmlRootElement(name = XMLConstants.APPLICATIONS)
@XmlAccessorType(XmlAccessType.FIELD)
public class Applications {
    @XmlElement(name = XMLConstants.APPLICATION)
    private List<Application> applications;

    public Application getApplication(String name) {
        for (Application app : applications) {
            if (app.getName().equals(name)) {
                return app;
            }
        }
        throw new IllegalArgumentException(
                "Counldn't find Application by name '" + name + "'");
    }

    /**
     * @return the applications
     */
    public List<Application> getApplications() {
        return applications;
    }

    /**
     * @param applications
     *            the applications to set
     */
    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    /**
     * 
     * <i>Description:</i> find applications whose included application list
     * contains the specified application name
     * 
     * @param name
     *            the application name to check
     * @return a list of applications that point to the specified application
     */
    public List<Application> getReferencedApplications(String name) {
        List<Application> res = new ArrayList<Application>();
        IncludeApplication ia = new IncludeApplication(name);
        for (Application app : applications) {
            List<IncludeApplication> children = app.getIncludeApplications();
            if (children != null && children.contains(ia)) {
                res.add(app);
            }
        }
        return res;
    }
}
