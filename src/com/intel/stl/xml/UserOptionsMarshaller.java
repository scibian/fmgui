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

package com.intel.stl.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * Marshals/Unmarshals UserOptions XML document instances
 */
public class UserOptionsMarshaller {

    private static final String USER_OPTIONS_SCHEMA = "UserOptions.xsd";

    private UserOptionsMarshaller() {
    }

    private static Schema schema = null;

    public static Schema getSchema() {
        if (schema == null) {
            createSchema();
        }
        return schema;
    }

    public static UserOptions unmarshal(String xmlDocument)
            throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(UserOptions.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(getSchema());

        StringReader reader = new StringReader(xmlDocument);
        return (UserOptions) unmarshaller.unmarshal(reader);
    }

    public static String marshal(UserOptions userOptions) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(UserOptions.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setSchema(getSchema());
        StringWriter writer = new StringWriter();
        marshaller.marshal(userOptions, writer);
        return writer.toString();
    }

    private static void createSchema() {
        SchemaFactory schemaFactory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL xsdUrl =
                UserOptionsMarshaller.class.getResource(USER_OPTIONS_SCHEMA);
        if (xsdUrl != null) {
            try {
                schema = schemaFactory.newSchema(xsdUrl);
            } catch (SAXException e) {
                // Our schema definition is wrong, this shouldn't happen in
                // production
                RuntimeException rte =
                        new RuntimeException(
                                "Invalid UserOptions schema definition: "
                                        + e.getMessage(), e);
                throw rte;
            }
        } else {
            // This shouldn't happen
            RuntimeException rte =
                    new RuntimeException(
                            "Could not find UserOptions schema file '"
                                    + USER_OPTIONS_SCHEMA + "'.");
            throw rte;
        }
    }
}
