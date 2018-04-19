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
package com.intel.stl.api.configuration;

import org.w3c.dom.Node;

/**
 */
public class RollingFileAppender extends AppenderConfig {
    private static final long serialVersionUID = 1L;

    private String fileLocation;

    private String fileNamePattern;

    private String maxFileSize;

    private String maxNumOfBackup;

    /**
     * @return the maxFileSize
     */
    public String getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * @param maxFileSize
     *            the maxFileSize to set
     */
    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /**
     * @return the maxNumOfBackUp
     */
    public String getMaxNumOfBackUp() {
        return maxNumOfBackup;
    }

    /**
     * @param maxNumOfBackup
     *            the maxNumOfBackup to set
     */
    public void setMaxNumOfBackUp(String maxNumOfBackup) {
        this.maxNumOfBackup = maxNumOfBackup;
    }

    /**
     * @return the fileLocation
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * @param fileLocation
     *            the fileLocation to set
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /**
     * @return the fileNamePattern
     */
    public String getFileNamePattern() {
        return fileNamePattern;
    }

    /**
     * @param fileNamePattern
     *            the fileNamePattern to set
     */
    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    @Override
    public void updateNode(Node node, ILogConfigFactory factory) {
        factory.updateNode(node, this);
    }

    @Override
    public Node createNode(ILogConfigFactory factory) {
        return factory.createNode(this);
    }

    @Override
    public void populateFromNode(Node node, ILogConfigFactory factory) {
        factory.populateFomNode(node, this);
    }

    @Override
    public String toString() {
        return "RollingFileAppender [name=" + getName() + ", threshold="
                + getThreshold() + ", pattern=" + getConversionPattern()
                + ", file=" + fileLocation + ", maxFileSize=" + maxFileSize
                + ", maxNumOfBackup=" + maxNumOfBackup + "]";
    }
}
