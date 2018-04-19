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

package com.intel.stl.ui.network;

import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.GraphEdge;
import com.intel.stl.ui.model.GraphNode;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.network.view.ResourceAllView;
import com.intel.stl.ui.network.view.ResourceLinkSubpageView;
import com.intel.stl.ui.network.view.ResourceSubpageView;
import com.intel.stl.ui.network.view.ResourceView;

/**
 * Top level controller for providing swappable JCards on the topology page
 * depending on whether or not a component has been selected on the graph
 */
public class ResourceController {

    private final ResourceView view;

    private ResourceAllSection allCard;

    private ResourceNodeSection nodeSubpageCard;

    private ResourceLinkSection linkSubpageCard;

    private ResourceLinkSection pathSubpageCard;

    private Map<ResourceScopeType, ResourceSection<?>> cards =
            new HashMap<ResourceScopeType, ResourceSection<?>>();

    private ResourceScopeType currentResourceType;

    private final MBassador<IAppEvent> eventBus;

    public ResourceController(ResourceView view, MBassador<IAppEvent> eventBus) {
        this.eventBus = eventBus;
        cards = getCards();
        this.view = view;
        this.view.initializeViews(cards);
    }

    protected Map<ResourceScopeType, ResourceSection<?>> getCards() {
        allCard =
                new ResourceAllSection(new ResourceAllView(
                        STLConstants.K1033_TOP_OVERVIEW.getValue()), eventBus);
        cards.put(ResourceScopeType.ALL, allCard);

        nodeSubpageCard =
                new ResourceNodeSection(new ResourceSubpageView(
                        STLConstants.K1021_RESOURCE_DETAILS.getValue()),
                        eventBus);
        cards.put(ResourceScopeType.NODE, nodeSubpageCard);

        linkSubpageCard =
                new ResourceLinkSection(new ResourceLinkSubpageView(
                        STLConstants.K0013_LINKS.getValue(),
                        UIImages.LINKS.getImageIcon()), eventBus);
        cards.put(ResourceScopeType.LINK, linkSubpageCard);

        pathSubpageCard =
                new ResourceLinkSection(new ResourceLinkSubpageView(
                        STLConstants.K1028_ROUTE_RESOURCE.getValue(),
                        UIImages.ROUTE.getImageIcon()), eventBus);
        cards.put(ResourceScopeType.PATH, pathSubpageCard);

        return cards;
    }

    public void setContext(Context context, IProgressObserver observer) {
        IProgressObserver[] subObservers =
                observer.createSubObservers(cards.size());
        // Pass the context to the cards for this view
        int i = 0;
        for (ResourceSection<?> card : cards.values()) {
            card.setContext(context, subObservers[i]);
            subObservers[i++].onFinish();
        }
    }

    public void showAll(final FVResourceNode[] selectedResources,
            final String name, final Icon icon,
            final TopologyTreeModel topArch, final TopGraph graph) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                currentResourceType = ResourceScopeType.ALL;
                // Clear subpages on all other views
                pathSubpageCard.clearSubpages();
                linkSubpageCard.clearSubpages();

                view.showLayout(currentResourceType);
                ResourceAllSection resourceAllSelection =
                        (ResourceAllSection) cards.get(currentResourceType);
                if (resourceAllSelection != null) {
                    resourceAllSelection.showAll(selectedResources, name, icon,
                            topArch, graph, graph);
                }
            }
        });
    }

    public void showGroup(final FVResourceNode[] selectedResources,
            final String name, final Icon icon,
            final TopologyTreeModel topArch, final TopGraph graph,
            final TopGraph fullGraph) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                currentResourceType = ResourceScopeType.ALL;
                // Clear subpages on all other views
                pathSubpageCard.clearSubpages();
                linkSubpageCard.clearSubpages();

                view.showLayout(currentResourceType);

                ResourceAllSection resourceAllSelection =
                        (ResourceAllSection) cards.get(currentResourceType);
                if (resourceAllSelection != null) {
                    resourceAllSelection.showAll(selectedResources, name, icon,
                            topArch, graph, fullGraph);
                }
            }
        });
    }

    public void showNode(final FVResourceNode source, final GraphNode node) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                currentResourceType = ResourceScopeType.NODE;
                // Clear subpages on all other views
                pathSubpageCard.clearSubpages();
                linkSubpageCard.clearSubpages();

                view.showLayout(currentResourceType);

                ResourceNodeSection resourceNodeSection =
                        (ResourceNodeSection) cards.get(currentResourceType);
                if (resourceNodeSection != null) {
                    resourceNodeSection.showNode(source, node);
                }
            }
        });
    }

    public void showLinks(final List<GraphEdge> links, final String vfName) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                currentResourceType = ResourceScopeType.LINK;
                // Clear subpages on all other views
                pathSubpageCard.clearSubpages();

                view.showLayout(currentResourceType);

                ResourceLinkSection resourceLinkSection =
                        (ResourceLinkSection) cards.get(currentResourceType);
                if (resourceLinkSection != null) {
                    resourceLinkSection.showLinks(links, vfName);
                }
            }
        });
    }

    public void showPath(final Map<GraphEdge, List<GraphEdge>> traceMap,
            final String vfName) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                currentResourceType = ResourceScopeType.PATH;
                // Clear subpages on all other views
                linkSubpageCard.clearSubpages();

                view.showLayout(currentResourceType);

                ResourceLinkSection resourceLinkSection =
                        (ResourceLinkSection) cards.get(currentResourceType);
                if (resourceLinkSection != null) {
                    resourceLinkSection.showPath(traceMap, vfName);
                }
            }
        });
    }

    public Component getView() {
        return this.view;
    }

    /**
     * <i>Description:</i>
     * 
     * @param subpageName
     */
    public void setCurrentSubpage(String subpageName) {
        for (ResourceSection<?> rs : cards.values()) {
            rs.setCurrentSubpage(subpageName);
        }
    }

    public String getPreviousSubpage() {
        ResourceSection<?> rs = cards.get(currentResourceType);
        if (rs != null) {
            return rs.getPreviousSubpage();
        }
        return null;
    }

    public String getCurrentSubpage() {
        ResourceSection<?> rs = cards.get(currentResourceType);
        if (rs != null) {
            return rs.getCurrentSubpage();
        }
        return null;
    }
}
