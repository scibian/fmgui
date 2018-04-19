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

package com.intel.stl.ui.common;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.common.view.ChartsSectionView;
import com.intel.stl.ui.common.view.ChartsSectionView.TabbedPanel;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.event.JumpToEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.model.ChartGroup;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.performance.GroupSource;
import com.intel.stl.ui.performance.IGroupController;
import com.intel.stl.ui.performance.provider.DataProviderName;

public abstract class ChartsSectionController extends
        BaseSectionController<ISectionListener, ChartsSectionView> implements
        ChangeListener {
    public static final String UTIL = "Util";

    public static final String ERR = "Error";

    protected int topN = 10;

    private final List<IGroupController<GroupSource>> groups =
            new ArrayList<IGroupController<GroupSource>>();

    private UndoHandler undoHandler;

    public ChartsSectionController(ChartsSectionView view,
            MBassador<IAppEvent> eventBus) {
        super(view, eventBus);

        ChartGroup utilGroup = new ChartGroup(UTIL, null);
        IGroupController<GroupSource>[] tmp = getUtilGroups();
        for (int i = 0; i < tmp.length; i++) {
            utilGroup.addMember(tmp[i].getGroup());
            groups.add(tmp[i]);
        }

        ChartGroup errGroup = new ChartGroup(ERR, null);
        tmp = getErrorGroups();
        for (int i = 0; i < tmp.length; i++) {
            errGroup.addMember(tmp[i].getGroup());
            groups.add(tmp[i]);
        }

        view.setListener(this);
        view.setChartGroups(new ChartGroup[] { utilGroup, errGroup });
    }

    protected abstract IGroupController<GroupSource>[] getUtilGroups();

    protected abstract IGroupController<GroupSource>[] getErrorGroups();

    /**
     * @return the topN
     */
    public int getTopN() {
        return topN;
    }

    /**
     * @param topN
     *            the topN to set
     */
    public void setTopN(int topN) {
        this.topN = topN;
    }

    @Override
    public ICardController<?>[] getCards() {
        return null;
    }

    public void setContext(Context context, IProgressObserver observer) {
        if (context != null && context.getController() != null) {
            undoHandler = context.getController().getUndoHandler();
        }

        if (observer == null) {
            observer = new ObserverAdapter();
        }
        IProgressObserver[] subObservers =
                observer.createSubObservers(groups.size());
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setContext(context, subObservers[i]);
            subObservers[i].onFinish();
        }

        observer.onFinish();
    }

    public void onRefresh(IProgressObserver observer) {
        if (observer == null) {
            observer = new ObserverAdapter();
        }
        IProgressObserver[] subObservers =
                observer.createSubObservers(groups.size());
        for (int i = 0; i < groups.size() && !observer.isCancelled(); i++) {
            groups.get(i).onRefresh(subObservers[i]);
            subObservers[i].onFinish();
        }

        observer.onFinish();
    }

    public void setSource(GroupSource name) {
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setDataSources(new GroupSource[] { name });
        }
    }

    /**
     * Description:
     * 
     * @param name
     */
    public void setDataProvider(DataProviderName name) {
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setDataProvider(name);
        }
    }

    public void setDisabledDataTypes(DataType defaultType, DataType... types) {
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setDisabledDataTypes(defaultType, types);
        }
    }

    public void setOrigin(JumpToEvent origin) {
        for (IGroupController<GroupSource> group : groups) {
            group.setOrigin(origin);
        }
    }

    @Override
    protected ISectionListener getSectionListener() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
     * )
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        TabbedPanel panel = (TabbedPanel) e.getSource();
        String category = panel.getName();
        String selection = panel.getSelection();
        if (category == UTIL) {
            initGroup(selection, getUtilGroups());
        } else if (category == ERR) {
            initGroup(selection, getErrorGroups());
        }

        if (undoHandler != null && !undoHandler.isInProgress()) {
            UndoableChartGroupSelection undoSel =
                    new UndoableChartGroupSelection(panel,
                            panel.getPreviousSelection(), selection);
            undoHandler.addUndoAction(undoSel);
        }
    }

    protected void initGroup(String name, IGroupController<GroupSource>[] groups) {
        for (IGroupController<GroupSource> group : groups) {
            if (group.getGroup().getName().equals(name)) {
                group.setSleepMode(false);
            } else {
                group.setSleepMode(true);
            }
        }
    }
}
