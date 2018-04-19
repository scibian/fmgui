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

package com.intel.stl.ui.admin.impl.devicegroups;

import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;

import com.intel.stl.api.management.IAttribute;
import com.intel.stl.ui.admin.view.devicegroups.ListPanel;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.main.Context;

public abstract class ListSelector<E extends IAttribute>
        implements IResourceSelector {
    protected final ListPanel<E> view;

    protected DefaultListModel<SelectionWrapper<E>> model;

    protected ListSelectionModel selectionModel;

    /**
     * Description:
     *
     * @param name
     * @param desc
     * @param view
     */
    public ListSelector(ListPanel<E> view) {
        super();
        this.view = view;
        view.setSelectionModel(getSelectionModel());
    }

    /**
     * <i>Description:</i>
     *
     * @return
     */
    protected ListSelectionModel getSelectionModel() {
        if (selectionModel == null) {
            selectionModel = new DefaultListSelectionModel() {
                private static final long serialVersionUID =
                        3726388429205355428L;

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * javax.swing.DefaultListSelectionModel#addSelectionInterval
                 * (int, int)
                 */
                @Override
                public void addSelectionInterval(int index0, int index1) {
                    List<Point> validIntervals =
                            getValidIntervals(index0, index1);
                    for (Point interval : validIntervals) {
                        super.addSelectionInterval(interval.x, interval.y);
                    }
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * javax.swing.DefaultListSelectionModel#setSelectionInterval
                 * (int, int)
                 */
                @Override
                public void setSelectionInterval(int index0, int index1) {
                    List<Point> validIntervals =
                            getValidIntervals(index0, index1);
                    for (int i = 0; i < validIntervals.size(); i++) {
                        Point interval = validIntervals.get(i);
                        if (i == 0) {
                            super.setSelectionInterval(interval.x, interval.y);
                        } else {
                            super.addSelectionInterval(interval.x, interval.y);
                        }
                    }
                }

                protected List<Point> getValidIntervals(int index0,
                        int index1) {
                    List<Point> res = new ArrayList<Point>();
                    int min = index0;
                    int max = -1;
                    for (int i = index0; i <= index1; i++) {
                        SelectionWrapper<E> sw = model.getElementAt(i);
                        if (sw.isSelected()) {
                            if (max >= min) {
                                res.add(new Point(min, max));
                            }
                            min = i + 1;
                        } else {
                            max = i;
                        }
                    }
                    if (max >= min) {
                        res.add(new Point(min, max));
                    }
                    System.out.println(index0 + " " + index1 + " " + res);
                    return res;
                }
            };

        }
        return selectionModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getView()
     */
    @Override
    public Component getView() {
        return view;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#cleanup()
     */
    @Override
    public void cleanup() {
        // not support
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
        // not support
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
        // not support
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#canExit()
     */
    @Override
    public boolean canExit() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.IPageController#onRefresh(com.intel.stl.ui.common
     * .IProgressObserver)
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        // not support
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#clear()
     */
    @Override
    public void clear() {
        if (model != null) {
            for (int i = 0; i < model.getSize(); i++) {
                SelectionWrapper<E> element = model.getElementAt(i);
                element.setSelected(false);
                view.repaint();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.IContextAware#setContext(com.intel.stl.ui.main
     * .Context, com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        SwingWorker<DefaultListModel<SelectionWrapper<E>>, Void> worker =
                getInitWorker(context);
        worker.execute();
    }

    /**
     * <i>Description:</i>
     *
     * @return
     */
    private SwingWorker<DefaultListModel<SelectionWrapper<E>>, Void> getInitWorker(
            final Context context) {
        SwingWorker<DefaultListModel<SelectionWrapper<E>>, Void> worker =
                new SwingWorker<DefaultListModel<SelectionWrapper<E>>, Void>() {

                    @Override
                    protected DefaultListModel<SelectionWrapper<E>> doInBackground()
                            throws Exception {
                        List<E> data = getListData(context);
                        DefaultListModel<SelectionWrapper<E>> model =
                                createModel(data);
                        return model;
                    }

                    /*
                     * (non-Javadoc)
                     *
                     * @see javax.swing.SwingWorker#done()
                     */
                    @Override
                    protected void done() {
                        try {
                            model = get();
                            view.setModel(model);
                        } catch (InterruptedException e) {
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            Util.showError(view, e);
                        }
                    }

                };
        return worker;
    }

    protected DefaultListModel<SelectionWrapper<E>> createModel(List<E> data) {
        DefaultListModel<SelectionWrapper<E>> model =
                new DefaultListModel<SelectionWrapper<E>>();
        if (data != null) {
            for (E element : data) {
                model.addElement(new SelectionWrapper<E>(element,
                        getElementDesc(element)));
            }
            model.trimToSize();
        }
        return model;
    }

    protected abstract List<E> getListData(Context context);

    protected abstract String getElementDesc(E element);

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IContextAware#getContextSwitchWeight()
     */
    @Override
    public PageWeight getContextSwitchWeight() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IContextAware#getRefreshWeight()
     */
    @Override
    public PageWeight getRefreshWeight() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.devicegroups.IResourceSelector#getSelections
     * ()
     */
    @Override
    public List<IAttribute> getViewSelections() {
        List<SelectionWrapper<E>> sels = view.getSelectedValuesList();
        List<IAttribute> res = new ArrayList<IAttribute>(sels.size());
        for (SelectionWrapper<E> sel : sels) {
            res.add(sel.getObj());
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.devicegroups.IResourceSelector#addSelections
     * (java.util.List)
     */
    @Override
    public void setModelSelections(List<? extends IAttribute> attrs) {
        if (model == null) {
            // shouldn't happen
            throw new RuntimeException("ListModel is null");
        }

        for (int i = 0; i < model.getSize(); i++) {
            SelectionWrapper<E> element = model.getElementAt(i);
            E realElement = element.getObj();
            for (IAttribute attr : attrs) {
                if (attr.equals(realElement)) {
                    element.setSelected(true);
                    break;
                }
            }
        }
        view.repaint();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.impl.devicegroups.IResourceSelector#
     * removeSelection (com.intel.stl.api.management.IAttribute)
     */
    @Override
    public void removeModelSelection(IAttribute attr) {
        if (model == null) {
            // shouldn't happen
            throw new RuntimeException("ListModel is null");
        }

        for (int i = 0; i < model.getSize(); i++) {
            SelectionWrapper<E> element = model.getElementAt(i);
            E realElement = element.getObj();
            if (attr.equals(realElement)) {
                element.setSelected(false);
                break;
            }
        }
        view.repaint();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.impl.devicegroups.IResourceSelector#
     * clearSelections ()
     */
    @Override
    public void clearViewSelections() {
        view.clearSelection();
    }

}
