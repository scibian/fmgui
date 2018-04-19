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

import java.awt.Component;
import java.awt.Dimension;
import java.util.Properties;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.common.PinDescription.PinID;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.common.view.JCardView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.PinBoardController;

public abstract class PinnableCardController<E extends ICardListener, V extends JCardView<E>>
        extends BaseCardController<E, V> implements IPinProvider {

    protected PinBoardController pinBoardCtr;

    /**
     * <b>NOTE</b> when we update view, we must update pin view as well if it's
     * not null. So far, we have no good architecture to enforce subclass must
     * do it. So it's the developer's responsibility to keep it in mind and do
     * it properly
     */
    protected V pinView;

    /**
     * Description:
     * 
     * @param view
     * @param eventBus
     */
    public PinnableCardController(V view, MBassador<IAppEvent> eventBus) {
        super(view, eventBus);
    }

    public void setContext(Context context) {
        if (context == null) {
            return;
        }

        PinBoardController ctr =
                context.getController().getPinBoardController();
        if (pinBoardCtr != ctr) {
            if (pinBoardCtr != null) {
                pinBoardCtr.deregisterPinProvider(getPinID());
            } else {
                view.enablePin(true);
            }
            pinBoardCtr = ctr;
            pinBoardCtr.registerPinProvider(getPinID(), this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseCardController#onPin()
     */
    @Override
    public void onPin() {
        if (pinBoardCtr != null) {
            PinDescription pin =
                    new PinDescription(getPinID(), view.getTitle());
            generateArgument(pin.getArgument());
            pinBoardCtr.addPin(pin);
        }
    }

    protected abstract void generateArgument(Properties arg);

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPinnable#createPin(com.intel.stl.ui.common.
     * PinDescription)
     */
    @Override
    public IPinCard createPin(PinDescription pin) {
        if (pin.getID() != getPinID()) {
            // shouldn't happen
            throw new IllegalArgumentException("Unmatched PinID. Expect "
                    + getPinID() + ", got " + pin.getID());
        }
        view.enablePin(false);

        return createPinCard(pin);
    }

    protected IPinCard createPinCard(final PinDescription pin) {
        return new IPinCard() {

            @Override
            public PinDescription getDescription() {
                return pin;
            }

            @Override
            public Component getView() {
                pinView = createPinView();
                initPinView();
                Component comp = pinView.getContentComponent();
                Dimension size = view.getContentComponent().getSize();
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
                clearPinView();
                view.enablePin(true);
            }
        };
    }

    /**
     * 
     * <i>Description:</i> create a pin view
     * 
     * @return
     */
    protected abstract V createPinView();

    /**
     * 
     * <i>Description:</i>initialize pin view with current data
     * 
     */
    protected abstract void initPinView();

    protected void clearPinView() {
        pinView = null;
    }

    @Override
    public abstract PinID getPinID();
}
