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

package com.intel.stl.ui.common.view;

import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.WidgetName;

/**
 */
public class JDuration extends JPanel {
    private static final long serialVersionUID = 109270047037650069L;

    private long durationInSeconds;

    private long days, hours, minutes;

    private JLabel daysLabel, hoursLabel, minutesLabel;

    private Font numberFont;

    private Color numberColor;

    private Font labelFont;

    private Color labelColor;

    public JDuration() {
        this(-1, TimeUnit.SECONDS);
    }

    public JDuration(long duration, TimeUnit unit) {
        super();
        durationInSeconds = unit.toSeconds(duration);
        convert();
        initComponent();
    }

    public void setDuration(long duration, TimeUnit unit) {
        durationInSeconds = unit.toSeconds(duration);
        convert();
        updateComponent();
    }

    public void setLAF(Font numberFont, Color numberColor, Font labelFont,
            Color labelColor) {
        this.numberFont = numberFont;
        this.numberColor = numberColor;
        this.labelFont = labelFont;
        this.labelColor = labelColor;
        removeAll();
        initComponent();
    }

    protected void convert() {
        if (durationInSeconds < 0) {
            days = hours = minutes = -1;
        } else {
            days = (int) TimeUnit.SECONDS.toDays(durationInSeconds);
            hours = TimeUnit.SECONDS.toHours(durationInSeconds) - (days * 24);
            minutes = TimeUnit.SECONDS.toMinutes(durationInSeconds)
                    - (TimeUnit.SECONDS.toHours(durationInSeconds) * 60);
            // seconds = TimeUnit.SECONDS.toSeconds(durationInSeconds) -
            // (TimeUnit.SECONDS.toMinutes(durationInSeconds) *60);
        }
    }

    protected void initComponent() {
        BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
        // FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 3, 2);
        setLayout(layout);
        JLabel label = null;
        if (days > 0) {
            daysLabel = createNumberLabel(days);
            daysLabel.setName(WidgetName.COM_DURATION_DAYS.name());
            add(daysLabel);

            label = createNameLabel(STLConstants.K0009_DAYS.getValue());
            add(label);
        } else {
            daysLabel = null;
        }

        if (days > 0 || hours > 0) {
            hoursLabel = createNumberLabel(hours);
            hoursLabel.setName(WidgetName.COM_DURATION_HOURS.name());
            add(hoursLabel);

            label = createNameLabel(STLConstants.K0010_HOURS.getValue());
            add(label);
        } else {
            hoursLabel = null;
        }

        minutesLabel = createNumberLabel(minutes);
        minutesLabel.setName(WidgetName.COM_DURATION_MINUTES.name());
        add(minutesLabel);

        label = createNameLabel(STLConstants.K0011_MINUTES.getValue());
        add(label);
    }

    protected void updateComponent() {
        if (days > 0 && daysLabel == null) {
            removeAll();
            initComponent();
            return;
        }

        if (hours > 0 && hoursLabel == null) {
            removeAll();
            initComponent();
            return;
        }

        if (daysLabel != null) {
            daysLabel.setText(getValueString(days));
        }

        if (hoursLabel != null) {
            hoursLabel.setText(getValueString(hours));
        }

        minutesLabel.setText(getValueString(minutes));
    }

    protected JLabel createNameLabel(String text) {
        JLabel res = new JLabel(text);
        res.setFont(labelFont);
        res.setForeground(labelColor);
        res.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
        res.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 6));
        return res;
    }

    protected JLabel createNumberLabel(long number) {
        String text = getValueString(number);
        JLabel res = new JLabel(text);
        res.setFont(numberFont);
        res.setForeground(numberColor);
        res.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
        return res;
    }

    protected String getValueString(long number) {
        return number >= 0 ? Long.toString(number)
                : STLConstants.K0039_NOT_AVAILABLE.getValue();
    }

    public void clear() {
        setDuration(-1, TimeUnit.SECONDS);
    }
}
