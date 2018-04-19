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
package com.intel.stl.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * Allows loading resource bundles encode as UTF files
 * 
 */
public class UTFControl extends Control {

    private static final String PROPERTIES = "properties";

    private final String encoding;

    public UTFControl(String encoding) {
        this.encoding = encoding;
    }

    public ResourceBundle newBundle(String baseName, Locale locale,
            String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {
        if (!format.equals("java.properties")) {
            throw new IllegalArgumentException("Bundle format '" + format
                    + "' not supported by this ResourceBundle Control");
        }
        ResourceBundle bundle = null;
        String bundleName = toBundleName(baseName, locale);
        final String resourceName = toResourceName(bundleName, PROPERTIES);
        InputStream stream = createInputStream(resourceName, loader, reload);
        if (stream != null) {
            try {
                Reader reader = new InputStreamReader(stream, encoding);
                bundle = new PropertyResourceBundle(reader);
            } finally {
                stream.close();
            }
        }
        return bundle;
    }

    private InputStream createInputStream(final String resourceName,
            final ClassLoader loader, final boolean reload) throws IOException {
        InputStream stream = null;
        try {
            stream =
                    AccessController
                            .doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                                public InputStream run() throws IOException {
                                    InputStream is = null;
                                    if (reload) {
                                        URL url =
                                                loader.getResource(resourceName);
                                        if (url != null) {
                                            URLConnection connection =
                                                    url.openConnection();
                                            if (connection != null) {
                                                // Disable caches to get fresh
                                                // data for
                                                // reloading.
                                                connection.setUseCaches(false);
                                                is =
                                                        connection
                                                                .getInputStream();
                                            }
                                        }
                                    } else {
                                        is =
                                                loader.getResourceAsStream(resourceName);
                                    }
                                    return is;
                                }
                            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
        return stream;
    }

}
