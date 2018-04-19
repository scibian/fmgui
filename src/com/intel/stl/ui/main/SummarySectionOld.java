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

package com.intel.stl.ui.main;

import java.util.Date;
import java.util.EnumMap;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.BaseSectionController;
import com.intel.stl.ui.common.ICardController;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.view.SummarySectionViewOld;
import com.intel.stl.ui.model.GroupStatistics;
import com.intel.stl.ui.model.NodeScore;
import com.intel.stl.ui.model.TimedScore;

/**
 */
public class SummarySectionOld extends
        BaseSectionController<ISectionListener, SummarySectionViewOld> {
    private final StatisticsCardOld statisticsCard;

    private final NodeStatesCardOld swStatesCard;

    private final NodeStatesCardOld caStatesCard;

    private final HealthHistoryCard healthHistoryCard;

    private final WorstNodesCard worstNodesCard;

    public SummarySectionOld(SummarySectionViewOld view,
            MBassador<IAppEvent> eventBus) {
        super(view, eventBus);
        statisticsCard =
                new StatisticsCardOld(view.getStatisticsView(), eventBus);
        swStatesCard = new NodeStatesCardOld(view.getSwStatesPie(), eventBus);
        caStatesCard = new NodeStatesCardOld(view.getCaStatesPie(), eventBus);
        healthHistoryCard =
                new HealthHistoryCard(view.getHealthHistoryView(), eventBus);
        worstNodesCard = new WorstNodesCard(view.getWorstNodesView(), eventBus);
    }

    /**
     * @return the staticticsCard
     */
    public StatisticsCardOld getStatisticsCard() {
        return statisticsCard;
    }

    /**
     * @return the swStatesCard
     */
    public NodeStatesCardOld getSwStatesCard() {
        return swStatesCard;
    }

    /**
     * @return the caStatesCard
     */
    public NodeStatesCardOld getCaStatesCard() {
        return caStatesCard;
    }

    /**
     * @return the healthHistoryCard
     */
    public HealthHistoryCard getHealthHistoryCard() {
        return healthHistoryCard;
    }

    /**
     * @return the worstNodesCard
     */
    public WorstNodesCard getWorstNodesCard() {
        return worstNodesCard;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.ISection#getCards()
     */
    @Override
    public ICardController<?>[] getCards() {
        return new ICardController[] { statisticsCard, swStatesCard,
                caStatesCard, healthHistoryCard, worstNodesCard };
    }

    /**
     * @param groupStatistics
     */
    public void updateStatistics(GroupStatistics groupStatistics) {
        statisticsCard.updateStatistics(groupStatistics);
    }

    /**
     * @param swStates
     * @param totalSWs
     * @param caStates
     * @param totalCAs
     */
    public void updateStates(EnumMap<NoticeSeverity, Integer> swStates,
            int totalSWs, EnumMap<NoticeSeverity, Integer> caStates,
            int totalCAs) {
        swStatesCard.updateStates(swStates, totalSWs);
        caStatesCard.updateStates(caStates, totalCAs);
    }

    /**
     * @param score
     * @param time
     */
    public void updateHealthScore(TimedScore score) {
        healthHistoryCard.updateHealthScore(score.getScore(),
                new Date(score.getTime()), "");
    }

    /**
     * @param nodes
     */
    public void updateWorstNodes(NodeScore[] nodes) {
        worstNodesCard.updateWorstNodes(nodes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.ISection#clear()
     */
    @Override
    public void clear() {
        healthHistoryCard.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseSectionController#getSectionListener()
     */
    @Override
    protected ISectionListener getSectionListener() {
        return this;
    }

}
