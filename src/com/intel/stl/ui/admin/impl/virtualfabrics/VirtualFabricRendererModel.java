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

package com.intel.stl.ui.admin.impl.virtualfabrics;

import java.util.LinkedHashMap;
import java.util.Map;

import com.intel.stl.api.management.XMLConstants;
import com.intel.stl.api.management.virtualfabrics.ApplicationName;
import com.intel.stl.api.management.virtualfabrics.LimitedMember;
import com.intel.stl.api.management.virtualfabrics.Member;
import com.intel.stl.ui.admin.impl.IRendererModel;
import com.intel.stl.ui.admin.view.IAttrRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.ApplicationRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.BandwidthRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.BaseSLRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.FlowControlDisableRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.HighPriorityRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.HoqLifeRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.LimitedMemberRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.MaxMtuRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.MaxRateRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.MemberRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.PKeyRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.PktLifeTimeMultRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.PreemptRankRenderer;
import com.intel.stl.ui.admin.view.virtualfabrics.StandbyRenderer;

public class VirtualFabricRendererModel implements IRendererModel {
    private final static Map<String, Class<? extends IAttrRenderer<?>>> map =
            new LinkedHashMap<String, Class<? extends IAttrRenderer<?>>>() {
                private static final long serialVersionUID =
                        7007977245246746722L;

                {
                    put(XMLConstants.PKEY, PKeyRenderer.class);
                    put(XMLConstants.MAX_MTU, MaxMtuRenderer.class);
                    put(XMLConstants.MAX_RATE, MaxRateRenderer.class);
                    put(XMLConstants.STANDBY, StandbyRenderer.class);
                    put(XMLConstants.HIGH_PRIORITY, HighPriorityRenderer.class);
                    put(XMLConstants.BANDWIDTH, BandwidthRenderer.class);
                    put(XMLConstants.PKT_LT_MULT, PktLifeTimeMultRenderer.class);
                    put(XMLConstants.BASE_SL, BaseSLRenderer.class);
                    put(XMLConstants.FLOW_CONTR_DISABLE,
                            FlowControlDisableRenderer.class);
                    put(XMLConstants.PREEMPT_RANK, PreemptRankRenderer.class);
                    put(XMLConstants.HOQ_LIFE, HoqLifeRenderer.class);
                    put(XMLConstants.MEMBER, MemberRenderer.class);
                    put(XMLConstants.LIMITED_MEMBER,
                            LimitedMemberRenderer.class);
                    put(XMLConstants.APPLICATION, ApplicationRenderer.class);
                }
            };

    private String[] appNames;

    private String[] dgNames;

    /**
     * @param appNames
     *            the appNames to set
     */
    public void setAppNames(String[] appNames) {
        this.appNames = appNames;
    }

    public void setDgNames(String[] dgNames) {
        this.dgNames = dgNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.impl.IRendererModel#getRendererNames()
     */
    @Override
    public String[] getRendererNames() {
        return map.keySet().toArray(new String[0]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.impl.IRendererModel#getRenderer(java.lang.String)
     */
    @Override
    public IAttrRenderer<?> getRenderer(String name) throws Exception {
        Class<? extends IAttrRenderer<?>> klass = map.get(name);
        if (klass != null) {
            IAttrRenderer<?> res = klass.newInstance();
            initRenderer(name, res);
            return res;
        } else {
            throw new IllegalArgumentException("Unknown renderer '" + name
                    + "'");
        }
    }

    protected void initRenderer(String name, IAttrRenderer<?> renderer) {
        if (name.equals(XMLConstants.MEMBER)) {
            MemberRenderer memebers = (MemberRenderer) renderer;
            Member[] sns = new Member[dgNames.length];
            for (int i = 0; i < sns.length; i++) {
                sns[i] = new Member(dgNames[i]);
            }
            memebers.setList(sns);
        } else if (name.equals(XMLConstants.LIMITED_MEMBER)) {
            LimitedMemberRenderer limitedMembers =
                    (LimitedMemberRenderer) renderer;
            LimitedMember[] sns = new LimitedMember[dgNames.length];
            for (int i = 0; i < sns.length; i++) {
                sns[i] = new LimitedMember(dgNames[i]);
            }
            limitedMembers.setList(sns);
        } else if (name.equals(XMLConstants.APPLICATION)) {
            ApplicationRenderer appMemebers = (ApplicationRenderer) renderer;
            ApplicationName[] sns = new ApplicationName[appNames.length];
            for (int i = 0; i < sns.length; i++) {
                sns[i] = new ApplicationName(appNames[i]);
            }
            appMemebers.setList(sns);
        }
    }

    public boolean isRepeatabledAttr(String name) {
        return name.equals(XMLConstants.MEMBER)
                || name.equals(XMLConstants.LIMITED_MEMBER)
                || name.equals(XMLConstants.APPLICATION);
    }

}
