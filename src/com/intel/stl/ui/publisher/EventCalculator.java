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

package com.intel.stl.ui.publisher;

import static com.intel.stl.ui.model.HealthScoreAttribute.NUM_HFILINKS;
import static com.intel.stl.ui.model.HealthScoreAttribute.NUM_HFIS;
import static com.intel.stl.ui.model.HealthScoreAttribute.NUM_ISLINKS;
import static com.intel.stl.ui.model.HealthScoreAttribute.NUM_NONDEGRADED_HFILINKS;
import static com.intel.stl.ui.model.HealthScoreAttribute.NUM_NONDEGRADED_ISLINKS;
import static com.intel.stl.ui.model.HealthScoreAttribute.NUM_PORTS;
import static com.intel.stl.ui.model.HealthScoreAttribute.NUM_SWITCHES;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.configuration.EventType;
import com.intel.stl.api.notice.EventDescription;
import com.intel.stl.api.notice.IEventListener;
import com.intel.stl.api.notice.IEventSource;
import com.intel.stl.api.notice.NodeSource;
import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.api.performance.ImageInfoBean;
import com.intel.stl.api.subnet.FabricInfoBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.model.HealthScoreAttribute;
import com.intel.stl.ui.model.NodeScore;
import com.intel.stl.ui.model.StateSummary;
import com.intel.stl.ui.model.TimedScore;
import com.intel.stl.ui.model.UserPreference;
import com.intel.stl.ui.publisher.NodeEvents.EventItem;

public class EventCalculator
        implements IEventListener<EventDescription>, IStateMonitor {
    private static Logger log = LoggerFactory.getLogger(EventCalculator.class);

    private static final boolean DEBUG = false;

    public static EnumMap<NoticeSeverity, Double> HEALTH_WEIGHTS =
            new EnumMap<NoticeSeverity, Double>(NoticeSeverity.class) {
                private static final long serialVersionUID =
                        2065678798638714805L;

                {
                    put(NoticeSeverity.INFO, 1.0);
                    put(NoticeSeverity.WARNING, 0.8);
                    put(NoticeSeverity.ERROR, 0.3);
                    put(NoticeSeverity.CRITICAL, 0.1);
                }
            };

    /**
     * Initial nodes distribution. NOTE, we set it once when we construct this
     * class. So if we have a subnet with 100 nodes, and then 50 of them are
     * down, we will get a bad health score. If we update <code>nodes</code> to
     * latest distribution, then we will likely get a health score of 100 that
     * doesn't make sense to a user. If the user close FM GUI and then re-launch
     * it, we will use the new nodes distribution as the reference. In another
     * words, we use whatever nodes distribution we get when we start FM GUI as
     * the reference number.
     */
    private final Map<NodeType, Integer> baseNodesDist;

    /**
     * The total number of nodes.
     */
    private final int totalNodes;

    private volatile int numWorstNodes;

    private volatile int timeWindow;

    private LinkedList<NodeEvents> events;

    private final int[] switchStates;

    private final int[] hfiStates;

    private final Object critical = new Object();

    private List<NodeEvents> eventsImage;

    private int[] switchStatesImage;

    private int[] hfiStatesImage;

    private long sweepTime;

    private boolean hasSweep;

    private boolean weightsChanged = false;

    private final EnumMap<HealthScoreAttribute, Long> values;

    private final EnumMap<HealthScoreAttribute, Long> totals;

    private final EnumMap<HealthScoreAttribute, Integer> weightSettings;

    private final EnumMap<HealthScoreAttribute, Integer> weights;

    private final long[] baseline = new long[] { 0, 0, 0, 0, 0, 0 };

    private int totalWeight;

    private final List<IStateChangeListener> stateChangeListeners =
            new CopyOnWriteArrayList<IStateChangeListener>();

    /**
     * Description:
     *
     * @param timeWindowInSeconds
     */
    public EventCalculator(EnumMap<NodeType, Integer> nodes,
            UserPreference userPreference) {
        super();
        setTimeWindowInSeconds(userPreference.getTimeWindowInSeconds());
        if (nodes != null) {
            baseNodesDist = Collections.unmodifiableMap(nodes);
        } else {
            baseNodesDist = Collections.emptyMap();
        }
        int sum = 0;
        for (Integer count : baseNodesDist.values()) {
            if (count != null) {
                sum += count;
            }
        }
        this.totalNodes = sum;
        setNumWorstNodes(userPreference.getNumWorstNodes());
        switchStates = new int[NoticeSeverity.values().length];
        hfiStates = new int[NoticeSeverity.values().length];
        events = new LinkedList<NodeEvents>();
        weightSettings = new EnumMap<HealthScoreAttribute, Integer>(
                HealthScoreAttribute.class);
        values = new EnumMap<HealthScoreAttribute, Long>(
                HealthScoreAttribute.class);
        totals = new EnumMap<HealthScoreAttribute, Long>(
                HealthScoreAttribute.class);
        weights = new EnumMap<HealthScoreAttribute, Integer>(
                HealthScoreAttribute.class);
        setHealthScoreWeights(userPreference);
        // Initialize switch and HFI states.
        sweep();

    }

    public int getTimeWindowInSeconds() {
        return timeWindow / 1000;
    }

    /**
     * @param timeWindowInSeconds
     *            the timeWindowInSeconds to set
     */
    public void setTimeWindowInSeconds(int timeWindowInSeconds) {
        this.timeWindow = timeWindowInSeconds * 1000;
    }

    /**
     * @return the numWorstNodes
     */
    public int getNumWorstNodes() {
        return numWorstNodes;
    }

    /**
     * @param numWorstNodes
     *            the numWorstNodes to set
     */
    public void setNumWorstNodes(int numWorstNodes) {
        this.numWorstNodes = numWorstNodes;
    }

    /**
     * @return the totalNodes
     */
    public int getTotalNodes() {
        return totalNodes;
    }

    /**
     * @return the baseNodesDist
     */
    public Map<NodeType, Integer> getBaseNodesDist() {
        return baseNodesDist;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.notice.IEventListener#onNewEvent()
     */
    @Override
    public void onNewEvent(EventDescription[] data) {
        // Filter events and add to queue for processing.
        for (EventDescription ed : data) {
            IEventSource source = ed.getSource();
            if (source instanceof NodeSource) {
                NodeSource nodeSource = (NodeSource) source;
                addEvent(nodeSource, ed.getDate().getTime(), ed.getType(),
                        ed.getSeverity());

                if (DEBUG) {
                    Date date = new Date(ed.getDate().getTime());
                    String dateText = Util.getYYYYMMDDHHMMSS().format(date);

                    System.out.println("new event time " + dateText);
                }
            }
        }
        // Remove old events.
        sweep();

        // Notify listeners of newevent.
        updateListeners();
    }

    protected void addEvent(NodeSource nodeSource, long time, EventType type,
            NoticeSeverity severity) {
        synchronized (events) {
            NodeEvents ne = new NodeEvents(nodeSource);
            NoticeSeverity oldSeverity = null;
            int index = events.indexOf(ne);
            if (index >= 0) {
                ne = events.get(index);
                oldSeverity = ne.getOverallSeverity();
            } else {
                events.add(ne);
            }
            NoticeSeverity newSeverity = ne.addEvent(time, type, severity);
            // System.out.println("AddEvent "+oldSeverity+" "+newSeverity+"
            // "+ne);
            updateStates(nodeSource.getNodeType(), oldSeverity, newSeverity);
        }
    }

    public void updateListeners() {
        StateSummary summary = getSummary();
        for (IStateChangeListener listener : stateChangeListeners) {
            listener.onStateChange(summary);
        }
    }

    /**
     *
     * <i>Description:</i>If a user change severity level in setup wizard, clear
     * all events before we apply the new severity levels.
     *
     * @param userSettings
     */
    public void clear() {

        if (events != null && !events.isEmpty()) {
            synchronized (events) {
                events.clear();
                synchronized (critical) {
                    eventsImage.clear();

                    for (int i = 0; i < switchStatesImage.length; i++) {
                        switchStatesImage[i] = 0;
                        switchStates[i] = 0;
                    }

                    for (int i = 0; i < hfiStatesImage.length; i++) {
                        hfiStatesImage[i] = 0;
                        hfiStates[i] = 0;
                    }

                    updateListeners();
                }
            }
        }
    }

    protected void updateStates(NodeType type, NoticeSeverity oldSeverity,
            NoticeSeverity newSeverity) {
        // System.out.println("updateStates "+type+" "+oldSeverity+"
        // "+newSeverity);
        if (type == NodeType.SWITCH) {
            if (oldSeverity != null) {
                switchStates[oldSeverity.ordinal()] -= 1;
            }
            if (newSeverity != null) {
                switchStates[newSeverity.ordinal()] += 1;
            }
        } else if (type == NodeType.HFI) {
            if (oldSeverity != null) {
                hfiStates[oldSeverity.ordinal()] -= 1;
            }
            if (newSeverity != null) {
                hfiStates[newSeverity.ordinal()] += 1;
            }
        }
        // System.out.println("switchStates "+Arrays.toString(switchStates));
        // System.out.println("hfiStates "+Arrays.toString(hfiStates));
    }

    protected void clearEvents(long cutTime) {
        synchronized (events) {
            while (!events.isEmpty()
                    && events.get(0).getEarlistTime() < cutTime) {
                NodeEvents ne = events.remove(0);
                NoticeSeverity oldSeverity = ne.getOverallSeverity();
                NoticeSeverity newSeverity = ne.clear(cutTime);
                if (DEBUG) {
                    Date date = new Date(cutTime);
                    SimpleDateFormat df2 =
                            new SimpleDateFormat("dd/MM/yy HH:mm:ss.sss");
                    String dateText = df2.format(date);

                    System.out.println("clearEvents " + oldSeverity + " "
                            + newSeverity + " " + dateText + " " + ne);
                }

                if (newSeverity != oldSeverity) {
                    updateStates(ne.getNodeType(), oldSeverity, newSeverity);
                }
                if (newSeverity != null) {
                    // re-organize the list to put the NodeEvents in correct
                    // position
                    int index = Collections.binarySearch(events, ne);
                    if (index >= 0) {
                        events.add(index, ne);
                    } else {
                        events.add(-index - 1, ne);
                    }
                }
            }
        }

    }

    /**
     * Description:
     *
     */
    protected void sweep() {
        sweep(System.currentTimeMillis());
    }

    protected void sweep(long time) {
        sweepTime = time;
        synchronized (events) {
            clearEvents(sweepTime - timeWindow);
            List<NodeEvents> eventsCopy = new ArrayList<NodeEvents>();
            for (NodeEvents ne : events) {
                eventsCopy.add(ne.copy());
            }

            int[] switchStatesCopy = new int[switchStates.length];
            System.arraycopy(switchStates, 0, switchStatesCopy, 0,
                    switchStatesCopy.length);
            int[] hfiStatesCopy = new int[hfiStates.length];
            System.arraycopy(hfiStates, 0, hfiStatesCopy, 0,
                    hfiStatesCopy.length);

            synchronized (critical) {
                eventsImage = eventsCopy;
                switchStatesImage = switchStatesCopy;
                hfiStatesImage = hfiStatesCopy;
            }
        }
        if (!hasSweep) {
            hasSweep = true;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.publisher.IStateMonitor#getHealthScore()
     */
    @Override
    public TimedScore getHealthScore() {
        if (totalNodes <= 0 || !hasSweep) {
            return null;
        }

        double healthScore = 0;
        StringBuffer tipBuff = new StringBuffer();
        tipBuff.append("<html>");
        synchronized (critical) {
            for (HealthScoreAttribute attr : values.keySet()) {
                double value = values.get(attr);
                double weight = weights.get(attr);
                double score = (value / totalWeight) * weight;
                healthScore += score;
                if (totals.get(attr) > 0) {
                    long total = totals.get(attr);
                    String desc = attr.getDescription();
                    tipBuff.append(String.format("%1$-25s : %2$6.0f / %3$d",
                            desc, value, total));
                    tipBuff.append("<br>");
                }
            }
        }
        tipBuff.append("</html>");
        // In some instances, we need to round up to show 100% (decimal
        // positions are truncated); this addition is negligible
        healthScore += 0.000000000001;
        return new TimedScore(sweepTime, healthScore * 100, tipBuff.toString());
    }

    /*-
    @Override
    public TimedScore getHealthScore() {
        if (totalNodes <= 0 || !hasSweep) {
            return null;
        }
    
        double penaltySum = 0;
        synchronized (critical) {
            for (int i = 0; i < switchStatesImage.length; i++) {
                penaltySum +=
                        switchStatesImage[i]
     * (1 - HEALTH_WEIGHTS.get(NoticeSeverity
                                        .values()[i]));
            }
            for (int i = 0; i < hfiStatesImage.length; i++) {
                penaltySum +=
                        hfiStatesImage[i]
     * (1 - HEALTH_WEIGHTS.get(NoticeSeverity
                                        .values()[i]));
            }
        }
        return new TimedScore(sweepTime, (1.0 - penaltySum / totalNodes) * 100);
    }
     */

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.publisher.IStateMonitor#getSwitchStates()
     */
    @Override
    public EnumMap<NoticeSeverity, Integer> getSwitchStates() {
        if (!hasSweep) {
            return null;
        }

        EnumMap<NoticeSeverity, Integer> res =
                new EnumMap<NoticeSeverity, Integer>(NoticeSeverity.class);
        synchronized (critical) {
            for (int i = 0; i < switchStatesImage.length; i++) {
                res.put(NoticeSeverity.values()[i], switchStatesImage[i]);
            }
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.publisher.IStateMonitor#getHFIStates()
     */
    @Override
    public EnumMap<NoticeSeverity, Integer> getHFIStates() {
        if (!hasSweep) {
            return null;
        }

        EnumMap<NoticeSeverity, Integer> res =
                new EnumMap<NoticeSeverity, Integer>(NoticeSeverity.class);
        synchronized (critical) {
            for (int i = 0; i < hfiStatesImage.length; i++) {
                res.put(NoticeSeverity.values()[i], hfiStatesImage[i]);
            }
        }
        return res;
    }

    public NodeScore[] getWorstNodes(int size) {
        if (!hasSweep) {
            return null;
        }

        List<NodeEvents> nodes = null;
        synchronized (critical) {
            nodes = new ArrayList<NodeEvents>(eventsImage);
        }
        size = Math.min(size, nodes.size());
        NodeScore[] res = new NodeScore[size];
        for (int i = 0; i < size; i++) {
            NodeEvents ne = nodes.get(i);
            EventItem item = ne.getLatestEvent();
            if (item != null) {
                res[i] = new NodeScore(ne.getName(), ne.getNodeType(),
                        ne.getLid(), item.getType(), sweepTime,
                        item.getHealthScore());
            }
        }
        Arrays.sort(res);
        return res;
    }

    @Override
    public StateSummary getSummary() {
        // System.out.println("EventCalculaor.getSummary");
        if (!hasSweep) {
            // System.out.println("EventCalculaor.getSummary - hasSweep =
            // null");
            return null;
        }

        sweep();
        StateSummary res = new StateSummary(baseNodesDist);
        if (DEBUG) {
            System.out.println("All Events");
            for (NodeEvents ne : eventsImage) {
                System.out.println(" " + ne);
            }
        }
        synchronized (events) {
            res.setHealthScore(getHealthScore());
            res.setSwitchStates(getSwitchStates());
            res.setHfiStates(getHFIStates());
            res.setWorstNodes(getWorstNodes(numWorstNodes));
            res.setEvents(eventsImage);
        }
        if (DEBUG) {
            System.out.println("HealthScore " + res.getHealthScore());
            System.out.println("SwitchStates " + res.getSwitchStates());
            System.out.println("HfiStates " + res.getHfiStates());
            System.out.println("WorstNodes");
            for (NodeScore ns : res.getWorstNodes()) {
                System.out.println(" " + ns);
            }
            System.out.println("All Events");
            for (NodeEvents ne : eventsImage) {
                System.out.println(" " + ne);
            }
        }
        return res;
    }

    public boolean updateUserPreference(UserPreference oldUserPreference,
            UserPreference newUserPreference) {
        int oldTimeWindow = oldUserPreference.getTimeWindowInSeconds();
        int newTimeWindow = newUserPreference.getTimeWindowInSeconds();

        boolean userPrefChanged = false;
        // Set time window only if it's not same as old one.
        if (oldTimeWindow < newTimeWindow) {
            setTimeWindowInSeconds(newTimeWindow);
            userPrefChanged = true;
        } else if (oldTimeWindow > newTimeWindow) {
            setTimeWindowInSeconds(newTimeWindow);
            sweep();
            updateListeners();
            userPrefChanged = true;
        }

        int oldWorstNodes = oldUserPreference.getNumWorstNodes();
        int newWorstNodes = newUserPreference.getNumWorstNodes();
        if (oldWorstNodes != newWorstNodes) {
            setNumWorstNodes(newUserPreference.getNumWorstNodes());
            updateListeners();
            userPrefChanged = true;
        }
        setHealthScoreWeights(newUserPreference);
        return userPrefChanged;
    }

    public void processHealthScoreStats(FabricInfoBean fabricInfo,
            ImageInfoBean imageInfo) {
        if (fabricInfo == null || imageInfo == null) {
            return;
        }

        long numSwitches = fabricInfo.getNumSwitches();
        long numHFIs = fabricInfo.getNumHFIs();
        long numSwitchPorts = imageInfo.getNumSwitchPorts();
        long numHFIPorts = imageInfo.getNumHFIPorts();
        long numISLs = fabricInfo.getNumInternalISLs()
                + fabricInfo.getNumExternalISLs()
                + fabricInfo.getNumDegradedISLs();
        long numHFILinks = fabricInfo.getNumInternalHFILinks()
                + fabricInfo.getNumExternalHFILinks()
                + fabricInfo.getNumDegradedHFILinks();
        long[] newTotal = new long[] { numSwitches, numHFIs, numSwitchPorts,
                numHFIPorts, numISLs, numHFILinks };
        boolean baselineChanged = false;
        for (int i = 0; i < baseline.length; i++) {
            if (newTotal[i] > baseline[i]) {
                baselineChanged = true;
                baseline[i] = newTotal[i];
            }
        }
        if (weightsChanged || baselineChanged) {
            resetWeights();
        }
        values.put(NUM_SWITCHES, numSwitches);
        values.put(NUM_HFIS, numHFIs);
        values.put(NUM_ISLINKS, numISLs);
        values.put(NUM_HFILINKS, numHFILinks);
        values.put(NUM_PORTS, numSwitchPorts + numHFIPorts);
        values.put(NUM_NONDEGRADED_ISLINKS,
                baseline[4] - fabricInfo.getNumDegradedISLs());
        values.put(NUM_NONDEGRADED_HFILINKS,
                baseline[5] - fabricInfo.getNumDegradedHFILinks());
    }

    private void resetWeights() {
        int sumBaselinesWeights = 0;

        // Attribute Number of Switches
        int setting = weightSettings.get(NUM_SWITCHES);
        int newWeight;
        if (baseline[0] == 0) {
            newWeight = 0;
        } else {
            newWeight = (int) ((setting == -1)
                    ? ((baseline[2] / baseline[0]) + 1) : setting); // numSwitchPorts
                                                                    // /
                                                                    // numSwitches
                                                                    // + 1
        }
        weights.put(NUM_SWITCHES, newWeight);
        totals.put(NUM_SWITCHES, baseline[0]);
        sumBaselinesWeights += baseline[0] * newWeight;

        // Attribute Number of HFIs
        setting = weightSettings.get(NUM_HFIS);
        if (baseline[1] == 0) {
            newWeight = 0;
        } else {
            newWeight = (int) ((setting == -1)
                    ? ((baseline[3] / baseline[1]) + 1) : setting); // numHFIPorts
                                                                    // / numHFIs
                                                                    // + 1
        }
        weights.put(NUM_HFIS, newWeight);
        totals.put(NUM_HFIS, baseline[1]);
        sumBaselinesWeights += baseline[1] * newWeight;

        // Attribute Number of Ports
        setting = weightSettings.get(NUM_PORTS);
        weights.put(NUM_PORTS, setting);
        totals.put(NUM_PORTS, baseline[2] + baseline[3]);
        sumBaselinesWeights += (baseline[2] + baseline[3]) * setting;

        // Attribute Number of InterSwitch Links
        setting = weightSettings.get(NUM_ISLINKS);
        weights.put(NUM_ISLINKS, setting);
        totals.put(NUM_ISLINKS, baseline[4]);
        sumBaselinesWeights += baseline[4] * setting;

        // Attribute Number of HFI Links
        setting = weightSettings.get(NUM_HFILINKS);
        weights.put(NUM_HFILINKS, setting);
        totals.put(NUM_HFILINKS, baseline[5]);
        sumBaselinesWeights += baseline[5] * setting;

        // Attribute Number of Non-degraded ISLs
        setting = weightSettings.get(NUM_NONDEGRADED_ISLINKS);
        weights.put(NUM_NONDEGRADED_ISLINKS, setting);
        totals.put(NUM_NONDEGRADED_ISLINKS, baseline[4]);
        sumBaselinesWeights += baseline[4] * setting;

        // Attribute Number of Non-degraded HFI links
        setting = weightSettings.get(NUM_NONDEGRADED_HFILINKS);
        weights.put(NUM_NONDEGRADED_HFILINKS, setting);
        totals.put(NUM_NONDEGRADED_HFILINKS, baseline[5]);
        sumBaselinesWeights += baseline[5] * setting;
        this.totalWeight = sumBaselinesWeights;
    }

    public void addListener(IStateChangeListener listener) {
        // System.out.println("EventCalculator.addListener called - "
        // + listener.toString());
        stateChangeListeners.add(listener);
    }

    public void removeListener(IStateChangeListener listener) {
        // System.out.println("EventCalculator.removeListener called -"
        // + listener.toString());
        stateChangeListeners.remove(listener);
    }

    public void cleanup() {
        events = null;
        eventsImage = null;
    }

    protected void setHealthScoreWeights(UserPreference userPreference) {
        weightsChanged = true;
        for (HealthScoreAttribute attr : HealthScoreAttribute.values()) {
            int weight = userPreference.getWeightForHealthScoreAttribute(attr);
            weightSettings.put(attr, weight);
        }
    }

    // For testing
    protected long[] getBaseline() {
        return baseline;
    }

    protected EnumMap<HealthScoreAttribute, Long> getValues() {
        return values;
    }

    protected EnumMap<HealthScoreAttribute, Long> getTotals() {
        return totals;
    }

    protected int getTotalWeight() {
        return totalWeight;
    }
}
