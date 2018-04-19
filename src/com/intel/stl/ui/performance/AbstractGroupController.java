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

package com.intel.stl.ui.performance;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.intel.stl.ui.common.ChartsCard;
import com.intel.stl.ui.common.IPinCard;
import com.intel.stl.ui.common.IPinDelegator;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.PinArgument;
import com.intel.stl.ui.common.PinDescription;
import com.intel.stl.ui.common.PinDescription.PinID;
import com.intel.stl.ui.common.view.ChartsView;
import com.intel.stl.ui.common.view.IChartCreator;
import com.intel.stl.ui.common.view.OptionChartsView;
import com.intel.stl.ui.event.JumpToEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.PinBoardController;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.main.view.IDataTypeListener;
import com.intel.stl.ui.model.ChartGroup;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.model.DatasetDescription;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.performance.item.IPerformanceItem;
import com.intel.stl.ui.performance.provider.DataProviderName;

import net.engio.mbassy.bus.MBassador;

public abstract class AbstractGroupController<S extends ISource>
        implements IGroupController<S>, IPinDelegator {
    private final static boolean DEBUG = false;

    protected MBassador<IAppEvent> eventBus;

    protected Context context;

    protected int maxDataPoints = 10;

    protected DataType type;

    protected HistoryType historyType;

    private boolean isSleepMode;

    protected IPerformanceItem<S>[] allItems;

    protected List<ChartsCard> cards;

    protected ChartGroup group;

    // pin capability support
    private PinID pinID;

    protected PinBoardController pinBoardCtr;

    protected Map<ChartArgument<S>, IPerformanceItem<S>> pinItems =
            new ConcurrentHashMap<ChartArgument<S>, IPerformanceItem<S>>();

    protected Map<ChartArgument<S>, ChartsCard> pinCards =
            new ConcurrentHashMap<ChartArgument<S>, ChartsCard>();

    protected UndoHandler undoHandler;

    protected JumpToEvent origin;

    /**
     * Description:
     *
     * @param trendName
     * @param topNName
     * @param histogramName
     */
    public AbstractGroupController(MBassador<IAppEvent> eventBus, String name,
            IPerformanceItem<S>[] items) {
        super();
        allItems = items;
        this.eventBus = eventBus;

        Map<String, DatasetDescription> map = initDataset();
        cards = initCards(map);
        if (cards != null && !cards.isEmpty()) {
            group = new ChartGroup(name, cards.get(0).getView());
            for (ChartsCard card : cards) {
                group.addMember(new ChartGroup(card.getView()));
            }
        }
    }

    /**
     * @return the cards
     */
    public List<ChartsCard> getCards() {
        return cards;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.IGroupController#setDataProvider(java.lang
     * .String)
     */
    @Override
    public void setDataProvider(DataProviderName name) {
        for (IPerformanceItem<S> item : allItems) {
            if (item != null) {
                item.setDataProvider(name);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.performance.IGroupController#setDataSources(java
     * .lang.String[])
     */
    @Override
    public void setDataSources(S[] names) {
        for (IPerformanceItem<S> item : allItems) {
            if (item != null) {
                item.setSources(names);
            }
        }
        resetViews();
    }

    protected void resetViews() {
        boolean hasPin = getPinID() != null;
        for (IPerformanceItem<S> item : allItems) {
            if (item == null) {
                continue;
            }

            String name = item.getName();
            ChartArgument<S> arg = getChartArgument(item);
            ChartsView view = getItemView(item);
            if (pinCards.containsKey(arg) || (pinBoardCtr != null
                    && pinBoardCtr.contains(item.getFullName(), arg))) {
                view.enablePin(name, false);
            } else {
                view.enablePin(name, hasPin);
            }
        }
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(DataType type) {
        this.type = type;

        for (IPerformanceItem<S> item : allItems) {
            if (item != null) {
                item.setType(type);
            }
        }

        // pin items shouldn't be impacted by this
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.IGroupController#setDisableDataType(com.
     * intel.stl.ui.model.DataType[])
     */
    @Override
    public void setDisabledDataTypes(DataType defaultType, DataType... types) {
        if (group == null) {
            return;
        }

        for (ChartGroup cg : group.getMembers()) {
            ChartsView view = cg.getChartView();
            if (view instanceof OptionChartsView) {
                ((OptionChartsView) view).setType(defaultType);
                ((OptionChartsView) view).setDisbaledDataTypes(types);
            }
        }
    }

    public void setHistoryType(HistoryType type) {
        this.historyType = type;

        for (IPerformanceItem<S> item : allItems) {
            if (item != null) {
                item.setHistoryType(type, false);
            }
        }

        // pin items shouldn't be impacted by this
    }

    protected Map<String, DatasetDescription> initDataset() {
        Map<String, DatasetDescription> map =
                new LinkedHashMap<String, DatasetDescription>();
        for (IPerformanceItem<S> item : allItems) {
            if (item != null) {
                map.put(item.getName(), item.getDatasetDescription());
            }
        }
        return map;
    }

    /**
     * Description: create ChartsCard based on Dataset
     *
     * @param map
     */
    protected abstract List<ChartsCard> initCards(
            Map<String, DatasetDescription> map);

    protected ChartsCard createCard(IPerformanceItem<S> item,
            Map<String, DatasetDescription> map) {
        String name = item.getName();
        ChartsView view = new ChartsView(name, getChartCreator());
        return createChartsCard(view, map, name);
    }

    /**
     *
     * <i>Description:</i> create card with history type option
     *
     * @param item
     * @param map
     * @return
     */
    protected ChartsCard createOptionCard(final IPerformanceItem<S> item,
            Map<String, DatasetDescription> map, boolean historyType,
            boolean dataType) {
        String name = item.getName();
        final OptionChartsView view =
                new OptionChartsView(name, getChartCreator());
        if (dataType) {
            view.setDataTypeListener(new IDataTypeListener<DataType>() {
                @Override
                public void onDataTypeChange(DataType oldType,
                        DataType newType) {
                    item.setType(newType);
                    view.setType(newType);

                    UndoHandler undoHandler = getUndoHandler();
                    if (undoHandler != null && !undoHandler.isInProgress()) {
                        UndoableDataTypeSelection sel =
                                new UndoableDataTypeSelection(item, view,
                                        oldType, newType);
                        undoHandler.addUndoAction(sel);
                    }
                }
            });
        }

        ChartsCard chartsCard = createChartsCard(view, map, name);

        if (historyType) {
            view.setHistoryTypeListener(new IDataTypeListener<HistoryType>() {
                @Override
                public void onDataTypeChange(HistoryType oldType,
                        HistoryType newType) {
                    // Get the refresh rate here and calculate the maxDataPoints
                    // here.
                    item.setHistoryType(newType, false);
                    view.setHistoryType(newType);

                    UndoHandler undoHandler = getUndoHandler();
                    if (undoHandler != null && !undoHandler.isInProgress()) {
                        UndoableChartHistorySelection sel =
                                new UndoableChartHistorySelection(item, view,
                                        oldType, newType);
                        undoHandler.addUndoAction(sel);
                    }
                }
            });
        }

        return chartsCard;
    }

    /**
     *
     * <i>Description:</i> create card with multiple IPerformanceItem
     *
     * @param items
     * @param map
     * @return
     */
    protected ChartsCard createCard(IPerformanceItem<S>[] items,
            Map<String, DatasetDescription> map) {
        ChartsView auxView = new ChartsView("", getChartCreator());
        String[] names = new String[items.length];
        String name = null;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                names[i] = getItemName(items[i]);
                if (name == null) {
                    name = names[i];
                }
            }
        }
        auxView.setTitle(name);
        return createChartsCard(auxView, map, names);
    }

    protected String getItemName(IPerformanceItem<S> item) {
        return item == null ? null : item.getName();
    }

    protected ChartsCard createChartsCard(ChartsView view,
            Map<String, DatasetDescription> datasets, String... datasetNames) {
        List<DatasetDescription> lst = new ArrayList<DatasetDescription>();
        for (String name : datasetNames) {
            if (name != null) {
                lst.add(datasets.get(name));
            }
        }
        ChartsCard res = new ChartsCard(view, eventBus, lst);
        // turn off pin by default. we will turn it on after we have created
        // chart and then reset view
        view.enablePin(false);
        return res;
    }

    /**
     * @return the group
     */
    @Override
    public ChartGroup getGroup() {
        return group;
    }

    /**
     * @param origin
     *            the origin to set
     */
    @Override
    public void setOrigin(JumpToEvent origin) {
        this.origin = origin;
        for (ChartsCard card : cards) {
            card.setOrigin(origin);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.performance.IGroupController#setContext(com.intel
     * .stl.ui.main.Context, com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        this.context = context;

        if (context == null || context.getController() == null) {
            return;
        }

        undoHandler = context.getController().getUndoHandler();
        for (ChartsCard card : cards) {
            card.setUndoHandler(undoHandler, origin);
        }

        for (IPerformanceItem<S> item : allItems) {
            if (item != null) {
                item.setContext(context, null);
            }
        }

        for (IPerformanceItem<S> item : pinItems.values()) {
            if (item != null) {
                item.setContext(context, null);
            }
        }

        initPinBoardController(context);

        if (observer != null) {
            observer.onFinish();
        }
    }

    protected void initPinBoardController(Context context) {
        PinBoardController ctr =
                context.getController().getPinBoardController();
        if (pinBoardCtr != ctr && getPinID() != null) {
            if (pinBoardCtr != null) {
                pinBoardCtr.deregisterPinProvider(getPinID());
            } else {
                for (ChartsCard card : cards) {
                    card.getView().enablePin(true);
                    card.setPinDelegator(this);
                }
            }
            pinBoardCtr = ctr;
            pinBoardCtr.registerPinProvider(getPinID(), this);
        }
    }

    protected UndoHandler getUndoHandler() {
        return undoHandler;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.IGroupController#onRefresh(com.intel.stl
     * .ui.common.IProgressObserver)
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        for (IPerformanceItem<S> item : allItems) {
            if (item != null) {
                item.onRefresh(null);
            }

            if (observer != null && observer.isCancelled()) {
                break;
            }
        }

        for (IPerformanceItem<S> item : pinItems.values()) {
            if (item != null) {
                item.onRefresh(null);
            }

            if (observer != null && observer.isCancelled()) {
                break;
            }
        }

        if (observer != null) {
            observer.onFinish();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.IGroupController#setSleepMode(boolean)
     */
    @Override
    public void setSleepMode(boolean b) {
        if (DEBUG) {
            System.out.println(
                    "[" + (getGroup() == null ? "null" : getGroup().getName())
                            + "] sleep mode = " + b);
        }

        isSleepMode = b;
        IPerformanceItem<S> primary = getPrimaryItem();
        // primary should always be active because it will provide data for
        // sparkline display
        primary.setActive(true);
        for (IPerformanceItem<S> item : allItems) {
            if (item != null && item != primary) {
                item.setActive(!b);
            }
        }
    }

    protected IPerformanceItem<S> getPrimaryItem() {
        return allItems[0];
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.IGroupController#isSleepMode()
     */
    @Override
    public boolean isSleepMode() {
        return isSleepMode;
    }

    public void clear() {
        for (IPerformanceItem<S> item : allItems) {
            item.clear();
        }
    }

    protected IPerformanceItem<S> findItem(String name) {
        for (IPerformanceItem<S> item : allItems) {
            if (item != null && item.getName().equals(name)) {
                return item;
            }
        }
        throw new IllegalArgumentException(
                "Cannot find PerformanceItem '" + name + "'");
    }

    /**
     * @return the pinID
     */
    @Override
    public PinID getPinID() {
        return pinID;
    }

    /**
     * @param pinID
     *            the pinID to set
     */
    public void setPinID(PinID pinID) {
        this.pinID = pinID;
    }

    @Override
    public void addPin(String title, PinArgument argument) {
        if (pinBoardCtr != null) {
            String name = argument.getProperty(ChartArgument.NAME);
            // fill pin argument
            IPerformanceItem<S> item = findItem(name);
            ChartArgument<S> chartArg = getChartArgument(item);
            PinDescription pin =
                    new PinDescription(getPinID(), getPinTitle(item), chartArg);
            pinBoardCtr.addPin(pin);
            if (DEBUG) {
                System.out.println("Register to pin board: " + pin);
            }
        }
    }

    protected abstract ChartArgument<S> getChartArgument(
            IPerformanceItem<S> item);

    protected String getPinTitle(IPerformanceItem<S> item) {
        return item.getShortName();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPinnable#createPin(com.intel.stl.ui.common.
     * PinDescription)
     */
    @Override
    public IPinCard createPin(PinDescription pin) {
        if (DEBUG) {
            System.out.println("Create pin card for " + pin);
        }
        if (pin.getID() != getPinID()) {
            // shouldn't happen
            throw new IllegalArgumentException("Unmatched PinID. Expect "
                    + getPinID() + ", got " + pin.getID());
        }

        String name = pin.getArgument().getProperty(ChartArgument.NAME);
        if (name == null) {
            // shouldn't happen
            throw new IllegalArgumentException("No chart name in Pin " + pin);
        }

        IPerformanceItem<S> item = findItem(name);
        ChartsView view = getItemView(item);
        if (view != null) {
            view.enablePin(name, false);
            return createPinCard(pin, item);
        } else {
            // this shouldn't happen
            throw new IllegalArgumentException(
                    "Cannot find view for pin " + pin);
        }
    }

    protected abstract ChartsView getItemView(IPerformanceItem<S> item);

    @SuppressWarnings("unchecked")
    protected IPinCard createPinCard(final PinDescription pin,
            final IPerformanceItem<S> source) {
        // the argument restored from persistence is a Properties object, we
        // need to convert it to ChartArgument
        Properties arg = pin.getArgument();
        final ChartArgument<S> chartArg =
                (ChartArgument<S>) ChartArgument.asChartArgument(arg);

        return new IPinCard() {

            @Override
            public PinDescription getDescription() {
                return pin;
            }

            @Override
            public Component getView() {
                ChartsCard card = getPinCard(chartArg, source);
                ChartsView view = card.getView();
                Component comp = view.getContentComponent();
                Dimension size =
                        getItemView(source).getContentComponent().getSize();
                int height = pin.getHeight();
                if (height > 0) {
                    comp.setPreferredSize(new Dimension(size.width, height));
                } else {
                    comp.setPreferredSize(size);
                    pin.setHeight(size.height);
                }
                return comp;
            }

            @Override
            public void unpin() {
                getItemView(source).enablePin(chartArg.getName(), true);
                ChartsCard pinCard = pinCards.remove(chartArg);
                IPerformanceItem<S> pinItem = pinItems.remove(chartArg);
                clearPin(pinCard, pinItem);
            }
        };
    };

    protected void clearPin(ChartsCard pinCard, IPerformanceItem<S> pinItem) {
        if (pinItem != null) {
            pinItem.setActive(false);
            pinItem.clear();
        }
    }

    protected ChartsCard getPinCard(ChartArgument<S> arg,
            IPerformanceItem<S> source) {
        ChartsCard card = pinCards.get(arg);
        if (card == null) {
            String name = arg.getName();
            IPerformanceItem<S> pinItem = source.copy();
            if (!pinItem.isActive()) {
                pinItem.setActive(true);
            }
            // System.out.println("============== " + item + " --> " + pinItem);
            if (!pinItem.getCurrentProviderName().name()
                    .equals(arg.getProvider())) {
                pinItem.setDataProvider(
                        DataProviderName.valueOf(arg.getProvider()));
                pinItem.setSources(arg.getSources());
            } else if (!Arrays.equals(pinItem.getSources(), arg.getSources())) {
                pinItem.setSources(arg.getSources());
            }
            DataType dataType = arg.getDataType();
            if (dataType != null) {
                pinItem.setType(dataType);
            }
            HistoryType historyType = arg.getHistoryTpe();
            if (historyType != null) {
                pinItem.setHistoryType(historyType, false);
            }
            pinItems.put(arg, pinItem);
            ChartsView view = new ChartsView(name, getChartCreator());
            Map<String, DatasetDescription> dataMap = Collections
                    .singletonMap(name, pinItem.getDatasetDescription());
            card = createChartsCard(view, dataMap, name);
            pinCards.put(arg, card);
        }
        return card;
    }

    protected IChartCreator getChartCreator() {
        return PerformanceChartsCreator.instance();
    }
}
