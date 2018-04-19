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

import javax.swing.ImageIcon;

/**
 */
public interface IPageController extends IContextAware {

    String getDescription();

    Component getView();

    ImageIcon getIcon();

    /**
     * 
     * Description: final cleanup before we destroy this page
     * 
     */
    void cleanup();

    /**
     * 
     * Description: enter this page
     * 
     */
    void onEnter();

    /**
     * 
     * <i>Description:</i> indicate whether we can exit from this page. Useful
     * when there are unsaved data, such as when there are unsaved user input, a
     * user will decide whether to stay and go to the next page.
     * 
     * @return
     */
    boolean canExit();

    /**
     * 
     * Description: leave this page
     * 
     */
    void onExit();

    /**
     * 
     * <i>Description:</i> refresh this page
     * 
     */
    void onRefresh(IProgressObserver observer);

    /**
     * 
     * Description: Clear subpage sections and deregister tasks
     * 
     */
    void clear();
}
