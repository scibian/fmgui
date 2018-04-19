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

package com.intel.stl.ui.configuration.view;

import static com.intel.stl.ui.common.UIConstants.INTEL_PALE_BLUE;
import static com.intel.stl.ui.common.UIConstants.INTEL_WHITE;
import static com.intel.stl.ui.common.UIImages.BACK_BLUE_ICON;
import static com.intel.stl.ui.common.UIImages.FORWARD_BLUE_ICON;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

import com.intel.stl.ui.model.DevicePropertyCategory;

public class PagingCategoryPanel extends MultiColumnCategoryPanel {

    private static final long serialVersionUID = 1L;

    private JPanel pagingPanel;

    private int start;

    private int totalPages;

    private JButton leftArrow;

    private JButton rightArrow;

    private JButton[] pageButtons;

    private final int itemsPerPage;

    private final int centerButtons;

    private final int numButtons;

    private final int slots;

    /**
     * 
     * Description: displays PropertyItems in a multi-column view with paging
     * support
     * 
     * @param numCols
     *            the number of columns in the page
     * @param itemsPerPage
     *            the number of PropertyItems displayed per page
     * @param centerButtons
     *            the number of center buttons in the paging area; the paging
     *            area has buttons in the following layout: <br>
     *            < 1 ... P1 P2 P3 - - - Pn ... T > <br>
     *            where n is the number of center buttons, T is the number of
     *            total possible pages being displayed given the number of
     *            PropertyItems in the model. The view adds two slots for the
     *            first page and the last page and also two more slots for the
     *            ellipsis (which can be used to link to a page depending of the
     *            sliding position)
     */
    public PagingCategoryPanel(int numCols, int itemsPerPage,
            int centerButtons, PropertyVizStyle style) {
        super(numCols, style);
        this.itemsPerPage = itemsPerPage;
        this.centerButtons = centerButtons;
        // Two ellipsis + first page button + last page button
        this.numButtons = centerButtons + 4;
        // Left arrow + right arrow
        this.slots = numButtons + 2;
    }

    @Override
    public void modelChanged(DevicePropertyCategory model) {
        this.model = model;
        start = 0;
        totalPages = (model.size() + (itemsPerPage - 1)) / itemsPerPage;
        displayPage(start, itemsPerPage);
        resetPagingButtons();
    }

    @Override
    public void initComponents() {
        createPagePanel();
        createPagingPanel();
        mainPanel.add(pagePanel);
        mainPanel.add(pagingPanel);
    }

    private void resetPagingButtons() {
        resetButtons();
        if (totalPages <= numButtons) {
            resetNormalPagingButtons();
        } else {
            resetSlidingPagingButtons();
        }
    }

    private void resetNormalPagingButtons() {
        int currPage = (start / itemsPerPage) + 1;
        resetArrowButtons(currPage);
        int skip = (numButtons - totalPages) / 2;
        for (int i = 0; i < skip; i++) {
            pageButtons[i].setText("");
        }
        int pageNum = 1;
        for (int i = skip; i < (skip + totalPages); i++) {
            pageButtons[i].setText(Integer.toString(pageNum));
            pageNum++;
        }
        for (int i = (skip + totalPages); i < numButtons; i++) {
            pageButtons[i].setText("");
            pageNum++;
        }
        pageButtons[skip + currPage - 1].setBackground(INTEL_PALE_BLUE);
    }

    private void resetSlidingPagingButtons() {
        int currPage = (start / itemsPerPage) + 1;
        resetArrowButtons(currPage);
        if (currPage <= centerButtons) {
            resetLastPageButtons();
            for (int i = 0; i < (numButtons - 2); i++) {
                pageButtons[i].setText(Integer.toString(i + 1));
            }
            pageButtons[currPage - 1].setBackground(INTEL_PALE_BLUE);
        } else {
            if (currPage >= (totalPages - centerButtons - 1)) {
                resetFirtPageButtons();
                int pageNum = totalPages - centerButtons - 1;
                for (int i = 2; i < numButtons; i++) {
                    pageButtons[i].setText(Integer.toString(pageNum));
                    if (pageNum == currPage) {
                        pageButtons[i].setBackground(INTEL_PALE_BLUE);
                    }
                    pageNum++;
                }

            } else {
                resetFirtPageButtons();
                resetLastPageButtons();
                int beforeCurrPage = (centerButtons - 1) / 2;
                int pageNum = currPage - beforeCurrPage;
                for (int i = 2; i < (numButtons - 2); i++) {
                    pageButtons[i].setText(Integer.toString(pageNum));
                    if (pageNum == currPage) {
                        pageButtons[i].setBackground(INTEL_PALE_BLUE);
                    }
                    pageNum++;
                }
            }
        }
    }

    private void resetFirtPageButtons() {
        pageButtons[0].setText(Integer.toString(1));
        pageButtons[1].setText("...");
        pageButtons[1].setEnabled(false);
    }

    private void resetLastPageButtons() {
        pageButtons[numButtons - 2].setText("...");
        pageButtons[numButtons - 2].setEnabled(false);
        pageButtons[numButtons - 1].setText(Integer.toString(totalPages));
    }

    private void resetArrowButtons(int currPage) {
        if (currPage == 1) {
            leftArrow.setEnabled(false);
            rightArrow.setEnabled(true);
        } else {
            if (currPage == totalPages) {
                leftArrow.setEnabled(true);
                rightArrow.setEnabled(false);
            } else {
                leftArrow.setEnabled(true);
                rightArrow.setEnabled(true);
            }
        }
    }

    private void resetButtons() {
        for (int i = 0; i < numButtons; i++) {
            pageButtons[i].setBackground(INTEL_WHITE);
            pageButtons[i].setEnabled(true);
        }
    }

    private void createPagingPanel() {
        pagingPanel = new JPanel();
        pagingPanel.setLayout(new GridLayout(1, slots));
        pagingPanel.setBackground(INTEL_WHITE);
        MouseAdapter cursorChanger = new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                e.getComponent().setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                e.getComponent().setCursor(
                        Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        };
        pageButtons = new JButton[numButtons];
        // left arrow button
        leftArrow = getPlainButton();
        leftArrow.setMargin(new Insets(3, 5, 3, 4));
        leftArrow.setIcon(BACK_BLUE_ICON.getImageIcon());
        leftArrow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currPage = (start / itemsPerPage) + 1;
                skipToPage(currPage - 1);
            }
        });
        leftArrow.addMouseListener(cursorChanger);
        pagingPanel.add(leftArrow);
        for (int i = 0; i < numButtons; i++) {
            pageButtons[i] = getPlainButton();
            pageButtons[i].setMargin(new Insets(3, 4, 3, 4));
            // pageButtons[i].setBackground(UIConstants.INTEL_PALE_BLUE);
            pageButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String pageNum = ((JButton) e.getSource()).getText();
                    try {
                        int num = Integer.parseInt(pageNum);
                        skipToPage(num);
                    } catch (NumberFormatException nfe) {
                    }
                }
            });
            pageButtons[i].addMouseListener(cursorChanger);
            pagingPanel.add(pageButtons[i]);
        }
        // right arrow button
        rightArrow = getPlainButton();
        rightArrow.setIcon(FORWARD_BLUE_ICON.getImageIcon());
        rightArrow.setMargin(new Insets(3, 4, 3, 5));
        rightArrow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currPage = (start / itemsPerPage) + 1;
                skipToPage(currPage + 1);
            }
        });
        rightArrow.addMouseListener(cursorChanger);
        pagingPanel.add(rightArrow);

        pagingPanel.repaint();
    }

    private void skipToPage(int pageNum) {
        if (pageNum > 0 && pageNum <= totalPages) {
            start = (pageNum - 1) * itemsPerPage;
            displayPage(start, itemsPerPage);
            resetPagingButtons();
        }
    }

    private JButton getPlainButton() {
        JButton btn = new JButton();
        btn.setUI(new BasicButtonUI());
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        return btn;
    }
}
