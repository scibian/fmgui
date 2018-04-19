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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.JHelpContentViewer;
import javax.help.Map.ID;

import com.intel.stl.ui.common.view.HelpView;

/**
 *
 * Controller for the console help utility
 */
public class HelpController implements IHelp {
    public static final String TOC = "toc";

    private final HelpView helpView;

    private final HelpSet helpSet;

    private final List<String> topicIdList = new ArrayList<String>();

    @SuppressWarnings("unchecked")
    public HelpController(String title, String helpSetFilename) {
        helpSet = initHelpSet(helpSetFilename);
        if (helpSet == null) {
            throw new IllegalArgumentException("Connot load help set '"
                    + helpSetFilename);
        }

        Enumeration<ID> ids = helpSet.getCombinedMap().getAllIDs();
        Set<String> cmds = new HashSet<String>();
        while (ids.hasMoreElements()) {
            String id = ids.nextElement().id;
            CLIHelpId cliId = CLIHelpId.valueOf(id);
            cmds.add(cliId.getCmd());
        }
        topicIdList.addAll(cmds);
        Collections.sort(topicIdList);

        JHelpContentViewer viewer = new JHelpContentViewer(helpSet);
        helpView = new HelpView(title, topicIdList, viewer, this);
        helpView.selectTopic(TOC);
    }

    protected HelpSet initHelpSet(String fileName) {

        URL hsURL = HelpSet.findHelpSet(null, fileName);

        HelpSet helpSet = null;
        try {
            helpSet = new HelpSet(null, hsURL);
        } catch (HelpSetException e) {
            e.printStackTrace();
        }

        return helpSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IHelp#showTopic(java.lang.String)
     */
    @Override
    public void showTopic(String topicId) {
        if (helpView != null) {
            helpView.displayTopic(topicId);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IHelp#getView()
     */
    @Override
    public HelpView getView() {
        return helpView;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IHelp#updateSelection(java.lang.String)
     */
    @Override
    public void updateSelection(String value) {
        helpView.updateSelection(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IHelp#getTopicIdList()
     */
    @Override
    public List<String> getTopicIdList() {
        return topicIdList;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IHelp#resetView()
     */
    @Override
    public void resetView() {
        helpView.resetView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IHelp#parseCommand(java.lang.String)
     */
    @Override
    public void parseCommand(String command) {

        if (command != null) {

            // If the command isn't in the topic id list, return
            if ((!command.contains("-")) && (!topicIdList.contains(command))) {
                return;
            }

            int sectionIndex = 0;
            String[] options = command.split(" ");
            String topic = options[0];

            for (String option : options) {

                boolean hasLongOption = (option.contains("--"));
                boolean isLongOptionLength = (option.length() == 2);

                boolean hasShortOption = (option.contains("-"));
                boolean isShortOptionLength = (option.length() == 1);

                // Append the option to the command name
                if ((hasLongOption && !isLongOptionLength)
                        || (hasShortOption && !isShortOptionLength)) {

                    sectionIndex =
                            hasLongOption ? option.indexOf("--") + 2
                                    : hasShortOption ? option.indexOf("-") + 1
                                            : 0;
                    topic =
                            options[0] + CLIHelpId.DELIMITER
                                    + option.substring(sectionIndex);
                }

                showTopic(topic);
            }
        }
    }

}
