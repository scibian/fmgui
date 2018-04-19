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

package com.intel.stl.api.logs;

import static com.intel.stl.common.STLMessages.STL50011_INVALID_XPATH_EXPRESSION;
import static com.intel.stl.common.STLMessages.STL50013_ERROR_PARSING_FM_CONFIG;
import static javax.xml.xpath.XPathConstants.NODE;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.management.FMConfHelper;

/**
 * The ConfigParser class is responsible for extracting the log file path from
 * the FM Configuration file
 */
public class FMConfigParser {
    private final static Logger log =
            LoggerFactory.getLogger(FMConfigParser.class);

    private static final String XPATH_LOG_FILE =
            "/Config/Common/Shared/LogFile";

    private XPath xPath;

    private Document config;

    private static DocumentBuilderFactory documentBuilderFactory =
            DocumentBuilderFactory.newInstance();

    private static XPathFactory xPathFactory = XPathFactory.newInstance();

    private final FMConfHelper fmConfigHelper;

    private File fmConfigFile;

    public FMConfigParser(FMConfHelper fmConfigHelper) {
        this.fmConfigHelper = fmConfigHelper;
    }

    public String getLogFilePath(final char[] password) throws Exception {

        String logFilePath = null;

        // Set up XPath and FM configuration file
        xPath = xPathFactory.newXPath();
        try {
            fmConfigFile = fmConfigHelper.getConfFile();
            if (fmConfigFile == null) {
                fmConfigHelper.fetchConfigFile(password);
                fmConfigFile = fmConfigHelper.getConfFile();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // Get the FM Config file and locate the log file path/name
        Node node = getLogFileNode();
        if (node != null) {
            logFilePath = node.getFirstChild().getNodeValue();
        }

        return logFilePath;
    }

    protected Node getLogFileNode() {

        Document config = getConfig();
        try {
            Node logFileNode =
                    (Node) xPath.evaluate(XPATH_LOG_FILE, config, NODE);
            return logFileNode;
        } catch (XPathExpressionException e) {
            ConfigurationException ce = new ConfigurationException(
                    STL50011_INVALID_XPATH_EXPRESSION, XPATH_LOG_FILE);
            log.error(ce.getMessage(), e);
            throw ce;
        }
    }

    protected Document getConfig() {
        if (config == null) {
            try {
                DocumentBuilder db =
                        documentBuilderFactory.newDocumentBuilder();
                this.config = db.parse(fmConfigFile);
            } catch (Exception e) {
                ConfigurationException ce = new ConfigurationException(
                        STL50013_ERROR_PARSING_FM_CONFIG,
                        StringUtils.getErrorMessage(e));
                log.error(ce.getMessage(), e);
                throw ce;
            }
        }
        return config;
    }
}
