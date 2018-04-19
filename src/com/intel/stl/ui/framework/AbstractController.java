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

package com.intel.stl.ui.framework;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.main.Context;

/**
 *
 */
public abstract class AbstractController<M extends AbstractModel, V extends AbstractView<M, C>, C extends AbstractController<M, V, C>>
        implements IController {

    private final List<IModelListener<M>> listeners;

    protected final M model;

    protected final V view;

    protected MBassador<IAppEvent> eventBus;

    private Context context;

    public AbstractController(M model, V view, MBassador<IAppEvent> eventBus) {
        this.listeners = new ArrayList<IModelListener<M>>();
        this.model = model;
        this.view = view;
        this.eventBus = eventBus;
        view.setController(asController());
        view.initView();
        initModel();
        addModelListener(view);
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public abstract void initModel();

    @Override
    public void submitTask(ITask task) {
        task.execute(this);
    }

    /**
     * Description: fires an event through the event bus
     * 
     * @param event
     *            the event being fired
     */
    protected void fireEvent(AbstractEvent event) {
        eventBus.publish(event);
    }

    @Override
    public void onTaskSuccess() {
        notifyModelChanged();
    }

    @Override
    public void onTaskFailure(Throwable caught) {
        notifyModelUpdateFailed(caught);
    }

    @SuppressWarnings("unchecked")
    public C asController() {
        checkController();
        return (C) this;
    }

    public void addModelListener(IModelListener<M> listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            notifyModelChanged(listener);
        }
    }

    public boolean removeModelListener(IModelListener<M> listener) {
        return listeners.remove(listener);
    }

    @Override
    public void notifyModelChanged() {
        for (IModelListener<M> listener : listeners) {
            notifyModelChanged(listener);
        }
    }

    @Override
    public void notifyModelUpdateFailed(Throwable caught) {
        for (IModelListener<M> listener : listeners) {
            notifyModelUpdateFailed(listener, caught);
        }
    }

    protected void notifyModelChanged(IModelListener<M> listener) {
        listener.modelChanged(model);
    }

    protected void notifyModelUpdateFailed(IModelListener<M> listener,
            Throwable caught) {
        listener.modelUpdateFailed(model, caught);
    }

    /*
     * This routine traverses the class hierarchy for the controller and makes
     * sure that the controller being used is a proper instance or extends from
     * the declared controller type in the MVC definition (the 'C' in the
     * generics definition)
     * 
     * We need to traverse the hierarchy because you can extend a parameterized
     * controller and the resulting controller should still be valid (although
     * as a controller, the view would be limited to use only the public
     * interface of the original controller.
     * 
     * The IllegalArgumentException should never happen in production settings.
     * It's just to protect the MVC framework from a developer trying to use a
     * controller that does not belong to the MVC definition. At runtime, you
     * would get a CastException if a view ever tries to use the controller
     * (even if it has the same public interface), so technically we can remove
     * this check for efficiency if desired. Just make sure you remove the unit
     * tests that check the conditions above.
     */
    private void checkController() {
        Type type = getClass();
        while (!AbstractController.class.equals(getClass(type))) {
            if (type instanceof Class) {
                type = ((Class<?>) type).getGenericSuperclass();
                if (type == null) {
                    break;
                }
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                if (!rawType.equals(AbstractController.class)) {
                    type = rawType.getGenericSuperclass();
                }
            }
        }
        if (type != null && type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (rawType.equals(AbstractController.class)) {
                Class<?> expectedController =
                        (Class<?>) parameterizedType.getActualTypeArguments()[2];
                if (!expectedController.isInstance(this)) {
                    throw new IllegalArgumentException(
                            "This class does not correspond to the declared Controller type: "
                                    + expectedController.getSimpleName());
                }
            }
        }
    }

    private Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else {
            return null;
        }
    }
}
