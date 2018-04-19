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

package com.intel.stl.ui.network.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.IBackgroundService;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.network.TopGraph;
import com.intel.stl.ui.network.TopGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraphView;

public class TopologyOutlinePanel extends JPanel {
    private static final long serialVersionUID = -2802225776501250477L;

    private static final Logger log = LoggerFactory
            .getLogger(TopologyOutlinePanel.class);

    private static final int MAX_OUTLINE_HEIGHT = 300;

    public enum Fit {
        WIDTH,
        WINDOW
    };

    private final Fit fit;

    private TopGraphComponent graphComp;

    private double ratio;

    private double scale;

    private BufferedImage img;

    private Timer repaintTimer;

    private IProgressObserver progressObserver;

    private final IBackgroundService updateService;

    private final AtomicInteger imageId = new AtomicInteger();

    protected mxIEventListener repaintHandler = new mxIEventListener() {
        @Override
        public void invoke(Object sender, mxEventObject evt) {
            repaint();
        }
    };

    public TopologyOutlinePanel(IBackgroundService updateService) {
        this(updateService, Fit.WINDOW);
    }

    /**
     * Description:
     * 
     * @param fit
     */
    public TopologyOutlinePanel(IBackgroundService updateService, Fit fit) {
        super();
        this.updateService = updateService;
        this.fit = fit;
        addComponentListener(new ComponentAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.ComponentAdapter#componentResized(java.awt
             * .event.ComponentEvent)
             */
            @Override
            public void componentResized(ComponentEvent e) {
                restartTimer();
            }

        });
    }

    /**
     * @param progressObserver
     *            the progressObserver to set
     */
    public void setProgressObserver(IProgressObserver progressObserver) {
        this.progressObserver = progressObserver;
    }

    public void setGraph(final TopGraph graph) {
        clearTimer();
        if (graphComp == null) {
            graphComp = new TopGraphComponent(updateService, graph);
            graphComp.setSelectionListener(null);
        } else {
            graphComp.getGraph()
                    .removeListener(repaintHandler, mxEvent.REPAINT);
            graphComp.setGraph(graph);
        }
        graph.addListener(mxEvent.REPAINT, repaintHandler);
        restartTimer();
        // updateImage();
    }

    protected void updateImage() {
        if (graphComp == null || graphComp.getGraph() == null) {
            return;
        }

        if (progressObserver != null) {
            progressObserver.publishProgress(0);
        }
        final int id = imageId.incrementAndGet();
        updateService.submit(new Runnable() {
            @Override
            public void run() {
                img = createImage(id);
                revalidate();
                repaint();
                if (progressObserver != null) {
                    progressObserver.onFinish();
                }
            }
        });

    }

    public BufferedImage getImage() {
        return img;
    }

    public void setImage(final BufferedImage image) {
        updateService.submit(new Runnable() {
            @Override
            public void run() {
                img = image;
                repaint();
                if (progressObserver != null) {
                    progressObserver.onFinish();
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        if (fit == Fit.WIDTH && ratio > 0) {
            double panelWidth = Math.max(dim.width, getWidth());
            dim.height = (int) Math.ceil(panelWidth * ratio);
            if (dim.height > MAX_OUTLINE_HEIGHT) {
                dim.height = MAX_OUTLINE_HEIGHT;
                dim.width = (int) Math.ceil(dim.height / ratio);
            }
        }
        return dim;
    }

    /**
     * 
     * <i>Description:</i> Adjust dimension size based on Fit style
     * 
     * @param dim
     */
    protected Dimension adjustSize(Dimension dim) {
        if (ratio > 0) {
            switch (fit) {
                case WIDTH:
                    dim.height = (int) (dim.getWidth() * ratio);
                    if (dim.height > MAX_OUTLINE_HEIGHT) {
                        dim.height = MAX_OUTLINE_HEIGHT;
                        dim.width = (int) (dim.height / ratio);
                    }
                    break;
                case WINDOW:
                    double newHeight = dim.getWidth() * ratio;
                    if (newHeight > dim.height) {
                        dim.width = (int) (dim.height / ratio);
                    } else {
                        dim.height = (int) (newHeight);
                    }
                    break;
            }
        }
        return dim;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (img != null) {
            clearTimer();
            Dimension dim = getSize();
            paintImage((Graphics2D) g, img, adjustSize(dim));
        }
    }

    protected void restartTimer() {
        if (repaintTimer == null) {
            repaintTimer =
                    new Timer(UIConstants.UPDATE_TIME / 2,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (repaintTimer != null) {
                                        updateImage();
                                    }
                                }
                            });
            repaintTimer.setRepeats(false);
        }
        repaintTimer.restart();
    }

    protected void clearTimer() {
        if (repaintTimer != null) {
            if (repaintTimer.isRunning()) {
                repaintTimer.stop();
            }
            repaintTimer = null;
        }
    }

    public void initView() {
        restartTimer();
    }

    // fast with low quality
    protected void paintImage(Graphics2D g2d, BufferedImage img, Dimension dim) {
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, dim.width, dim.height, this);
    }

    // slow with high quality
    protected void paintImage2(Graphics2D g2d, BufferedImage img, Dimension dim) {
        int scale = dim.width / img.getWidth();
        int mode =
                scale < 1 ? BufferedImage.SCALE_AREA_AVERAGING
                        : BufferedImage.SCALE_SMOOTH;
        Image scaledImage = img.getScaledInstance(dim.width, dim.height, mode);
        g2d.drawImage(scaledImage, 0, 0, this);
    }

    protected BufferedImage createImage(int id) {
        if (id < imageId.get()) {
            // System.out.println("OutlineView: Ignore image " + id + "<"
            // + imageId.get());
            return img;
        }

        long t = System.currentTimeMillis();
        BufferedImage img = null;
        mxGraphView view = graphComp.getGraph().getView();
        double graphScale = view.getScale();
        mxPoint graphTrans = view.getTranslate();

        try {
            Rectangle orgGrapSize = view.getGraphBounds().getRectangle();
            view.setTranslate(new mxPoint(-orgGrapSize.getX()
                    + graphTrans.getX(), -orgGrapSize.getY()
                    + graphTrans.getY()));
            ratio = (double) orgGrapSize.height / orgGrapSize.width;
            Insets insets = getInsets();
            int w = getSize().width - insets.left - insets.right;
            int h = getSize().height - insets.top - insets.bottom;
            Dimension dim = adjustSize(new Dimension(w, h));
            scale = (double) dim.width / orgGrapSize.width * graphScale;
            view.setScale(scale);
            img =
                    mxUtils.createBufferedImage(dim.width + insets.left
                            + insets.right, dim.height + insets.top
                            + insets.bottom, null);
            if (img != null) {
                Graphics2D g2 = img.createGraphics();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    AffineTransform trans = new AffineTransform();
                    trans.translate(insets.left, insets.top);
                    g2.setTransform(trans);
                    // Draws the scaled graph
                    graphComp.getGraphControl().paintComponent(g2);
                } finally {
                    g2.dispose();
                }
            }
            return img;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // The method #getToolTipText depends on graph view scale. When
            // multiple outline panels share the same graph view (e.g. the
            // outline popup window and the outline panel within topology page),
            // it is important to set scale back, so #getToolTipText can
            // identify the correct node under a mouse point
            view.setScale(graphScale);
            view.setTranslate(graphTrans);
            log.info("Create OutlineBuffer-"
                    + id
                    + " "
                    + (img == null ? "" : img.getWidth() + "x"
                            + img.getHeight() + " " + img.hashCode()) + " on "
                    + graphComp.getGraph() + " in "
                    + (System.currentTimeMillis() - t) + " ms @ "
                    + Thread.currentThread());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
     */
    @Override
    public String getToolTipText(MouseEvent event) {
        if (graphComp != null) {
            Insets insets = getInsets();
            int x = event.getX() - insets.left;
            int y = event.getY() - insets.top;
            mxGraphView view = graphComp.getGraph().getView();
            double graphScale = view.getScale();
            Rectangle graphSize = view.getGraphBounds().getRectangle();
            x = (int) ((x / scale + graphSize.getX()) * graphScale);
            y = (int) ((y / scale + graphSize.getY()) * graphScale);
            Object cell = graphComp.getCellAt(x, y);
            if (cell != null) {
                String tip = graphComp.getGraph().getToolTipForCell(cell);
                if (tip != null && !tip.isEmpty()) {
                    return tip;
                }
            }
        }
        return null;
    }

}
