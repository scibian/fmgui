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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 */
public interface UIConstants {
    int UPDATE_TIME = 500; // ms

    int MAX_LABEL_Width = 250;

    long BYTE_PER_FLIT = 8;

    int DEFAULT_SMTP_PORT = 25;

    Color INTEL_WHITE = new Color(255, 255, 255);

    Color INTEL_BLUE = new Color(0, 113, 197);

    Color INTEL_PALE_BLUE = new Color(126, 211, 247);

    Color INTEL_MEDIUM_BLUE = new Color(24, 145, 238);

    Color INTEL_LIGHT_BLUE = new Color(0, 174, 239);

    Color INTEL_MEDIUM_DARK_BLUE = new Color(0, 85, 165);

    Color INTEL_DARK_BLUE = new Color(0, 66, 128);

    Color INTEL_SKY_BLUE = new Color(217, 242, 253);

    Color INTEL_YELLOW = new Color(255, 218, 0);

    Color INTEL_DARK_YELLOW = new Color(253, 184, 19);

    Color INTEL_LIGHT_YELLOW = new Color(255, 218, 0);

    Color INTEL_ORANGE = new Color(253, 184, 19);

    Color INTEL_DARK_ORANGE = new Color(248, 149, 29);

    Color INTEL_GREEN = new Color(166, 206, 57);

    Color INTEL_DARK_GREEN = new Color(141, 198, 63);

    Color INTEL_RED = new Color(237, 28, 36);

    Color INTEL_DARK_RED = new Color(220, 57, 18);

    Color INTEL_MEDIUM_RED = new Color(255, 100, 80);

    Color INTEL_MEDIUM_DARK_RED = new Color(187, 28, 26);

    Color INTEL_LIGHT_RED = new Color(255, 245, 240);

    Color INTEL_GRAY = new Color(147, 149, 152);

    Color INTEL_LIGHT_GRAY = new Color(177, 186, 191);

    Color INTEL_DARK_GRAY = new Color(83, 86, 90);

    Color INTEL_BACKGROUND_GRAY = new Color(237, 239, 240);

    Color INTEL_BORDER_GRAY = new Color(225, 229, 231);

    Color INTEL_TABLE_ROW_GRAY = new Color(247, 248, 249);

    Color INTEL_TABLE_BORDER_GRAY = new Color(211, 216, 219);

    Font H1_FONT = new Font("Segoe UI", Font.PLAIN, 42);

    Font H2_FONT = new Font("Segoe UI", Font.PLAIN, 21);

    Font H3_FONT = new Font("Segoe UI", Font.PLAIN, 16);

    Font H4_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    Font H5_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    Font H6_FONT = new Font("Segoe UI", Font.PLAIN, 11);

    NumberFormat PERCENTAGE = NumberFormat.getPercentInstance();

    NumberFormat PERCENTAGE2 = new DecimalFormat("#.##%");

    NumberFormat DECIMAL = new DecimalFormat("##.##");

    NumberFormat INTEGER = new DecimalFormat("###,###");

    // topology UI constants
    Color DARK_GREEN = new Color(0, 170, 0);

    String DARK_GREEN_STR = "#00AA00";

    String DARK_ORANGE_STR = "#DD8800";

    Stroke VERTEX_SEL_STROKE = new BasicStroke(2);

    /**
     * Stroke used when we have very small bounds (1x1 bounds)
     */
    Stroke VERTEX_SEL_STROKE2 = new BasicStroke(0.5f);

    Color VERTEX_SEL_COLOR = DARK_GREEN;

    Stroke EDGE_SEL_STROKE = new BasicStroke(2);

    Color EDGE_SEL_COLOR = DARK_GREEN;

    Stroke EDGE_MARK_STROKE = new BasicStroke(1);

    Color EDGE_MARK_COLOR = DARK_GREEN;

    String EDGE_HIGHLIGHT_STROKE_STR = "3";

    String EDGE_HIGHLIGHT_COLOR_STR = DARK_ORANGE_STR;

    // characters used in input validation

    String DIGITS = "0123456789";

    String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqsrtuvwxyz";

    String PUNCTUATION = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    String SAFE_STR = DIGITS + LETTERS + PUNCTUATION + " ";

    String HEX_CHARS = DIGITS + "abcdefxABCDEFX";

    String NUMBER_CHARS = DIGITS + "+-.,%";

    String NODE_DESC_CHARS = DIGITS + LETTERS + " .-=";

    // this is used for Application, DG, VF names
    String NAME_CHARS = NODE_DESC_CHARS + "_";

    String MAIL_LIST_DELIMITER = ";";
}
