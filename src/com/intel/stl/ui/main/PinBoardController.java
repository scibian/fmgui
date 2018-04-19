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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.ui.common.IPinCard;
import com.intel.stl.ui.common.IPinProvider;
import com.intel.stl.ui.common.PinArgument;
import com.intel.stl.ui.common.PinDescription;
import com.intel.stl.ui.common.PinDescription.PinID;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.DecoratedPinCardView;
import com.intel.stl.ui.main.view.PinBoardView;

public class PinBoardController {
    private static final Logger log = LoggerFactory
            .getLogger(PinBoardController.class);

    private static final int DEFAULT_SIZE = 8;

    private static boolean DUMP_PERSISTENCE = true;

    private final IFabricController parent;

    private final PinBoardView view;

    private final Map<PinID, IPinProvider> pinProviders =
            new HashMap<PinID, IPinProvider>();

    private final List<PinItem> pinItems = new ArrayList<PinItem>();

    private final BlockingQueue<PinDescription> toAdd;

    private final int size;

    public PinBoardController(PinBoardView view, IFabricController parent) {
        this(view, parent, DEFAULT_SIZE);
    }

    /**
     * Description:
     * 
     * @param view
     */
    public PinBoardController(PinBoardView view, IFabricController parent,
            int size) {
        super();
        this.view = view;
        this.parent = parent;
        this.size = size;
        toAdd = new ArrayBlockingQueue<PinDescription>(size);
    }

    public void init() {
        if (parent.getCurrentContext() != null) {
            if (pinItems.isEmpty()) {
                // load from DB
                restore();
            } else {
                // refresh current cards
                List<PinDescription> pins = new ArrayList<PinDescription>();
                for (PinItem pi : pinItems) {
                    pins.add(pi.getCard().getDescription());
                }
                restorePins(pins);
            }
        }
    }

    /**
     * @return the view
     */
    public PinBoardView getView() {
        return view;
    }

    public synchronized void registerPinProvider(PinID id, IPinProvider provider) {
        if (pinProviders.containsKey(id)) {
            throw new IllegalArgumentException(id + " Provider already exist!");
        }
        pinProviders.put(id, provider);
    }

    public synchronized void deregisterPinProvider(PinID id) {
        pinProviders.remove(id);
    }

    public void restorePins(List<PinDescription> pins) {
        clear();
        for (PinDescription pin : pins) {
            // continue to the next pin when exception arose
            try {
                addPin(pin);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Cannot restore pin for " + pin, e);
            }
        }
    }

    /**
     * 
     * <i>Description:</i> put a pin in a queue and then add pins in background
     * one by one. This will ensure we do not block EDT and the pins are added
     * in the order of arriving.
     * 
     * @param pin
     */
    public void addPin(PinDescription pin) {
        if (pinItems.size() + toAdd.size() >= size) {
            Util.showErrorMessage(parent.getViewFrame(),
                    UILabels.STL10115_MAX_PINS.getDescription(Integer
                            .toString(size)));
            return;
        }

        toAdd.add(pin);
        if (toAdd.size() == 1) {
            addPinsInBackground();
        }
    }

    /**
     * 
     * <i>Description:</i> this thread will die if no pins to add. And we
     * recreate it if have new pin(s) to add
     * 
     */
    protected void addPinsInBackground() {
        Runnable work = new Runnable() {
            @Override
            public void run() {
                PinDescription pin = null;
                while ((pin = toAdd.poll()) != null) {
                    IPinProvider provider = pinProviders.get(pin.getID());
                    if (provider == null) {
                        log.error("Cannot find provider for " + pin.getID());
                        continue;
                    }
                    IPinCard card = provider.createPin(pin);
                    final DecoratedPinCardView cardView =
                            decoratePinView(card.getView(), pin, card);
                    pinItems.add(new PinItem(card, cardView));
                    Util.runInEDT(new Runnable() {
                        @Override
                        public void run() {
                            if (pinItems.size() > 1) {
                                cardView.enableUpButton(true);
                                pinItems.get(pinItems.size() - 2).getView()
                                        .enableDownButton(true);
                            }
                            view.appendPinCardView(cardView);
                        }
                    });
                }
            }
        };
        Thread addingThread = new Thread(null, work, "PinBoard");
        addingThread.start();
    }

    protected DecoratedPinCardView decoratePinView(Component comp,
            PinDescription pin, final IPinCard card) {
        String title = pin.getTitle();
        DecoratedPinCardView view = new DecoratedPinCardView(comp, title, pin);
        view.setUpAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goUp(card);
            }
        });
        view.setDownAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goDown(card);
            }
        });
        view.setCloseAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unpin(card);
            }
        });
        return view;
    }

    // run in EDT
    protected void goUp(IPinCard card) {
        int index = indexOf(card);
        if (index > 0) {
            PinItem item = pinItems.remove(index);
            pinItems.add(index - 1, item);
            resetView();
        } else {
            // this shouldn't happen, we already enable/disable up/down button
            // to ensure it
            throw new IllegalArgumentException(
                    "Cannot go up because this Pin's index is " + index);
        }
    }

    // run in EDT
    protected void goDown(IPinCard card) {
        int index = indexOf(card);
        if (index < pinItems.size() - 1) {
            PinItem item = pinItems.remove(index);
            pinItems.add(index + 1, item);
            resetView();
        } else {
            // this shouldn't happen, we already enable/disable up/down button
            // to ensure it
            throw new IllegalArgumentException(
                    "Cannot go down because there is total " + pinItems.size()
                            + " pins, and this Pin's index is " + index);
        }
    }

    // run in EDT
    protected void unpin(final IPinCard card) {
        int index = indexOf(card);
        PinItem item = pinItems.remove(index);
        view.removePinCardView(item.getView());
        if (!pinItems.isEmpty()) {
            item = pinItems.get(0);
            item.getView().enableUpButton(false);
            item = pinItems.get(pinItems.size() - 1);
            item.getView().enableDownButton(false);
        }
        Context context = parent.getCurrentContext();
        if (context != null) {
            context.getTaskScheduler().submitToBackground(new Runnable() {
                @Override
                public void run() {
                    card.unpin();
                }
            });
        } else {
            // shouldn't happen
            throw new RuntimeException("No context on FabricController "
                    + parent);
        }
    }

    protected void resetView() {
        DecoratedPinCardView cardViews[] =
                new DecoratedPinCardView[pinItems.size()];
        for (int i = 0; i < pinItems.size(); i++) {
            PinItem item = pinItems.get(i);
            cardViews[i] = item.getView();
            cardViews[i].enableUpButton(i > 0);
            cardViews[i].enableDownButton(i < pinItems.size() - 1);
        }
        view.setPinCardViews(cardViews);
    }

    protected int indexOf(IPinCard card) {
        for (int i = 0; i < pinItems.size(); i++) {
            PinItem item = pinItems.get(i);
            // compare reference
            if (item.getCard() == card) {
                return i;
            }
        }
        throw new IllegalArgumentException("Cannot find the PinCard " + card);
    }

    public boolean contains(PinDescription pin) {
        return contains(pin.getTitle(), pin.getArgument());
    }

    public boolean contains(String title, PinArgument arg) {
        for (PinItem item : pinItems) {
            PinDescription itemPin = item.getCard().getDescription();
            if (title.equals(itemPin.getTitle())
                    && arg.equals(itemPin.getArgument())) {
                return true;
            }
        }
        return false;
    }

    public void cleanup() {
        try {
            save();
        } finally {
            clear();
            pinProviders.clear();
        }
    }

    protected void clear() {
        view.clear();
        for (PinItem pinItem : pinItems) {
            pinItem.getCard().unpin();
        }
        pinItems.clear();
    }

    protected void save() {
        Context context = parent.getCurrentContext();
        if (context != null) {
            UserSettings settings = context.getUserSettings();
            if (settings != null) {
                Properties pinProps = getPinBoardProperties();
                if (DUMP_PERSISTENCE) {
                    System.out.println("=========== to save ==============");
                    System.out.println(pinProps);
                }
                settings.getPreferences().put(UserSettings.SECTION_PIN_BOARD,
                        pinProps);
                // we needn't to save it now. The application will save
                // UserSettings before it exit.
                // IConfigurationApi confApi = context.getConfigurationApi();
                // confApi.saveUserSettings(context.getSubnetDescription()
                // .getName(), settings);
            }
        } else {
            // happens when we cancel or failed to connect to subnet
            log.warn("No context on FabricController " + parent);
        }
    }

    protected void restore() {
        Context context = parent.getCurrentContext();
        if (context != null) {
            UserSettings settings = context.getUserSettings();
            if (settings != null) {
                Properties pinProps =
                        settings.getPreferences().get(
                                UserSettings.SECTION_PIN_BOARD);
                if (DUMP_PERSISTENCE) {
                    System.out.println("=========== to restore ==============");
                    System.out.println(pinProps);
                }
                if (pinProps != null) {
                    List<PinDescription> pinDescs = restorePinDescs(pinProps);
                    for (PinDescription pinDesc : pinDescs) {
                        addPin(pinDesc);
                    }
                }
            }
        } else {
            // happens when we cancel or failed to connect to subnet
            log.warn("No context on FabricController " + parent);
        }
    }

    // we use index as key
    protected Properties getPinBoardProperties() {
        Properties res = new Properties();
        for (int i = 0; i < pinItems.size(); i++) {
            PinItem pinItem = pinItems.get(i);
            PinDescription pinDesc = pinItem.getCard().getDescription();
            pinDesc.setHeight(pinItem.getView().getComponentHeight());
            res.put(Integer.toString(i), pinDesc.persistent());
        }
        return res;
    }

    // restore based on assuming key is index
    protected List<PinDescription> restorePinDescs(Properties props) {
        List<PinDescription> res = new ArrayList<PinDescription>();
        int i = 0;
        String tmp = null;
        while ((tmp = props.getProperty(Integer.toString(i++))) != null) {
            PinDescription pinDesc = PinDescription.restore(tmp);
            res.add(pinDesc);
        }
        return res;
    }

    protected class PinItem {
        private final IPinCard card;

        private final DecoratedPinCardView view;

        /**
         * Description:
         * 
         * @param card
         * @param view
         */
        public PinItem(IPinCard card, DecoratedPinCardView view) {
            super();
            this.card = card;
            this.view = view;
        }

        /**
         * @return the card
         */
        public IPinCard getCard() {
            return card;
        }

        /**
         * @return the view
         */
        public DecoratedPinCardView getView() {
            return view;
        }

    }
}
