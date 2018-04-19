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

package com.intel.stl.ui.console;

import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.wittams.gritty.CharacterUtils;

public class CommandFilter {

    private final List<String> topicIdList;

    public CommandFilter(List<String> topicIdList) {

        this.topicIdList = topicIdList;
    }

    public String validate(byte[] bytes) {

        String command = parseCommand(bytes);

        command = (topicIdList.contains(command)) ? command : null;

        return command;

    }

    protected String parseCommand(byte[] bytes) {

        // Assume that the submitted command contains a carriage return
        // and may or may not contain arguments
        String command = new String();
        String tempCommand = "";
        try {
            command = new String(bytes, "UTF-8");
            tempCommand = command;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // If there is a space in the command, then there are parameters
        String baseCommand = new String();
        int spaceIndex = tempCommand.indexOf(" ");
        if (spaceIndex >= 0) {
            baseCommand = tempCommand.substring(0, spaceIndex);
        } else {
            // If the command contains no space, then there are no parameters
            // and the delimiter must be a carriage return
            int crIndex =
                    tempCommand.indexOf(CharacterUtils
                            .getCode(KeyEvent.VK_ENTER)[0]);
            if (crIndex >= 0) {
                baseCommand = tempCommand.substring(0, crIndex);
            }
        }

        return baseCommand;
    }
}
