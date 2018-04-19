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

package com.intel.stl.api.management;

import java.io.File;
import java.util.Date;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {

    public static Node getNodeByName(Node parent, String name) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public static void writeDoc(Document doc, File file) throws Exception {
        TransformerFactory transformerFactory =
                SAXTransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // transformer.setOutputProperty(
        // "{http://xml.apache.org/xslt}indent-amount", "2");

        // XPathFactory xpathFactory = XPathFactory.newInstance();
        // // XPath to find empty text nodes.
        // XPathExpression xpathExp =
        // xpathFactory.newXPath().compile(
        // "//text()[normalize-space(.) = '']");
        // NodeList emptyTextNodes =
        // (NodeList) xpathExp.evaluate(doc, XPathConstants.NODESET);
        //
        // // Remove each empty text node from document.
        // for (int i = 0; i < emptyTextNodes.getLength(); i++) {
        // Node emptyTextNode = emptyTextNodes.item(i);
        // emptyTextNode.getParentNode().removeChild(emptyTextNode);
        // }

        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    // -----------------------------------------------------------------------//
    // opafm.xml has text, such as new line, spaces for indent etc.. These
    // texts are treated as text node, and they make it impossible to use java
    // standard api to do pretty print. The following methods try to hacked the
    // format for new or updated Application, Device Group and Virtual Fabric
    // in opafm.xml to have it has the same format as other nodes in opafm.xml.
    // When print a dom into xml file, we do not use pretty print, so we will
    // fully keep the old format (with the new lines, indent spaces etc.) and
    // hacked format for new or updated node.
    // -----------------------------------------------------------------------//

    /**
     * 
     * <i>Description:</i> append a node into specified <code>parent</code> node
     * 
     * @param doc
     * @param parent
     * @param node
     */
    public static void appendNode(Document doc, Node parent, Node node) {
        Node txt = doc.createTextNode("\n    ");
        parent.appendChild(txt);
        Node comment =
                doc.createComment(" Created by FM GUI @ " + (new Date()) + " ");
        parent.appendChild(comment);
        doc.adoptNode(node);
        parent.appendChild(node);
        formatNode(doc, parent, node);
        txt = doc.createTextNode("\n\n  ");
        parent.appendChild(txt);
    }

    public static void removeNode(Document doc, Node parent, Node node,
            String nodeName) {
        Node comment =
                doc.createComment(" '" + nodeName
                        + "' was removed by FM GUI @ " + (new Date()) + " ");
        parent.insertBefore(comment, node);
        parent.removeChild(node);
    }

    public static void replaceNode(Document doc, Node parent, Node oldNode,
            Node newNode) {
        Node comment =
                doc.createComment(" Updated by FM GUI @ " + (new Date()) + " ");
        parent.insertBefore(comment, oldNode);
        parent.replaceChild(newNode, oldNode);
        formatNode(doc, parent, newNode);
    }

    private static void formatNode(Document doc, Node parent, Node node) {
        Node txt = doc.createTextNode("\n    ");
        parent.insertBefore(txt, node);
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            txt = doc.createTextNode("\n      ");
            node.insertBefore(txt, children.item(i));
            i += 1;
        }
        txt = doc.createTextNode("\n    ");
        node.appendChild(txt);
    }
}
