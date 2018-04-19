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

package com.intel.stl.api.management.virtualfabrics.impl;

import static com.intel.stl.api.management.XMLConstants.NAME;
import static com.intel.stl.api.management.XMLConstants.VIRTUAL_FABRIC;
import static com.intel.stl.api.management.XMLConstants.VIRTUAL_FABRICS;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.intel.stl.api.IMessage;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.management.ChangeManager;
import com.intel.stl.api.management.DuplicateNameException;
import com.intel.stl.api.management.FMConfHelper;
import com.intel.stl.api.management.XMLUtils;
import com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement;
import com.intel.stl.api.management.virtualfabrics.VirtualFabric;
import com.intel.stl.api.management.virtualfabrics.VirtualFabricException;
import com.intel.stl.api.management.virtualfabrics.VirtualFabrics;
import com.intel.stl.common.STLMessages;

public class VirtualFabricManagement implements IVirtualFabricManagement {
    private final static Logger log =
            LoggerFactory.getLogger(VirtualFabricManagement.class);

    private final static Set<String> RESERVED = new HashSet<String>() {
        private static final long serialVersionUID = -8507198541424973196L;

        {
            // add("Default");
            add("Admin");
        }
    };

    private final FMConfHelper confHelp;

    private final ChangeManager changeMgr = new ChangeManager();

    /**
     * Description:
     *
     * @param confHelp
     */
    public VirtualFabricManagement(FMConfHelper confHelp) {
        super();
        this.confHelp = confHelp;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * getReservedVirtualFabrics()
     */
    @Override
    public Set<String> getReservedVirtualFabrics() {
        return RESERVED;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * getVirtualFabrics()
     */
    @Override
    public List<VirtualFabric> getVirtualFabrics()
            throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile();
            VirtualFabrics vfs = unmarshal(confFile);
            log.info("Fetch " + vfs.getVFs().size()
                    + " Device Groups from host '" + confHelp.getHost() + "'");
            return vfs.getVFs();
        } catch (Exception e) {
            throw createVirtualFabricException(STLMessages.STL63021_GET_VFS_ERR,
                    e, confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * getVirtualFabric(java.lang.String)
     */
    @Override
    public VirtualFabric getVirtualFabric(String name)
            throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile();
            VirtualFabrics vfs = unmarshal(confFile);
            return vfs.getVF(name);
        } catch (Exception e) {
            throw createVirtualFabricException(STLMessages.STL63026_GET_VF_ERR,
                    e, name, confHelp.getHost(),
                    StringUtils.getErrorMessage(e));
        }
    }

    /**
     * <i>Description:</i>
     *
     * @param confFile
     * @return
     */
    private VirtualFabrics unmarshal(File xmlFile) throws Exception {
        XMLInputFactory xif = XMLInputFactory.newFactory();
        StreamSource xml = new StreamSource(xmlFile);
        final XMLStreamReader xsr = xif.createXMLStreamReader(xml);
        while (xsr.hasNext()) {
            if (xsr.isStartElement()
                    && xsr.getLocalName().equals(VIRTUAL_FABRICS)) {
                break;
            }
            xsr.next();
        }

        JAXBContext jc = JAXBContext.newInstance(VirtualFabrics.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<VirtualFabrics> jb =
                unmarshaller.unmarshal(xsr, VirtualFabrics.class);
        xsr.close();

        return jb.getValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * addVirtualFabric
     * (com.intel.stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void addVirtualFabric(VirtualFabric vf)
            throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile();
            uniqueNameCheck(null, vf.getName());
            addVirtualFabric(confFile, confFile, vf);
            log.info("Added Virtual Fabric " + vf);
            changeMgr.addChange(vf.getName());
        } catch (Exception e) {
            throw createVirtualFabricException(STLMessages.STL63022_ADD_VF_ERR,
                    e, vf.getName(), confHelp.getHost(),
                    StringUtils.getErrorMessage(e));
        }
    }

    protected void addVirtualFabric(File srcXml, File dstXml, VirtualFabric vf)
            throws Exception {
        // transfer app to DOM
        DOMResult res = new DOMResult();
        JAXBContext context = JAXBContext.newInstance(vf.getClass());
        context.createMarshaller().marshal(vf, res);
        Document groupsDoc = (Document) res.getNode();
        Node newGroup = groupsDoc.getFirstChild();

        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        // check app in old xml
        Node groupsNode = doc.getElementsByTagName(VIRTUAL_FABRICS).item(0);
        Node matchedGroup = getVFByName(groupsNode, vf.getName());
        if (matchedGroup != null) {
            throw new IllegalArgumentException(
                    "Virtual Fabric '" + vf.getName() + "' alreday exist!");
        }

        // append app to Applications node
        XMLUtils.appendNode(doc, groupsNode, newGroup);

        // save back to xml file
        XMLUtils.writeDoc(doc, dstXml);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * removeVirtualFabric(java.lang.String)
     */
    @Override
    public void removeVirtualFabric(String name) throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile();
            removeVirtualFabric(confFile, confFile, name);
            log.info("Removed application '" + name + "'");
            changeMgr.addChange(name);
        } catch (Exception e) {
            throw createVirtualFabricException(
                    STLMessages.STL63023_REMOVE_VF_ERR, e, name,
                    confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    protected void removeVirtualFabric(File srcXml, File dstXml, String name)
            throws Exception {
        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        // check app in old xml
        Node vfsNode = doc.getElementsByTagName(VIRTUAL_FABRICS).item(0);
        Node matchedVf = getVFByName(vfsNode, name);
        if (matchedVf != null) {
            XMLUtils.removeNode(doc, vfsNode, matchedVf, name);

            // save back to xml file
            XMLUtils.writeDoc(doc, dstXml);
        } else {
            // this can happen when we create a new one and then rename it
            log.warn("Couldn't find Virtual Fabric '" + name + "'");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * updateVirtualFabric(java.lang.String,
     * com.intel.stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void updateVirtualFabric(String oldName, VirtualFabric vf)
            throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile();
            if (!oldName.equals(vf.getName())) {
                VirtualFabrics groups = unmarshal(confFile);
                uniqueNameCheck(groups, vf.getName());
            }
            updateVirtualFabric(confFile, confFile, oldName, vf, false);
            log.info("Updated Virtual Fabric " + vf);
            changeMgr.addChange(oldName);
            changeMgr.addChange(vf.getName());
        } catch (Exception e) {
            throw createVirtualFabricException(
                    STLMessages.STL63024_UPDATE_VF_ERR, e, vf.getName(),
                    confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * addOrUpdateVirtualFabric(java.lang.String,
     * com.intel.stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void addOrUpdateVirtualFabric(String oldName, VirtualFabric vf)
            throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile();
            updateVirtualFabric(confFile, confFile, oldName, vf, true);
            log.info("Added or updated Virtual Fabric " + vf);
            changeMgr.addChange(oldName);
            changeMgr.addChange(vf.getName());
        } catch (Exception e) {
            throw createVirtualFabricException(
                    STLMessages.STL63025_ADDUPDATE_VF_ERR, e, vf.getName(),
                    confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    protected void updateVirtualFabric(File srcXml, File dstXml, String oldName,
            VirtualFabric vf, boolean allowAdd) throws Exception {
        // transfer app to DOM
        DOMResult res = new DOMResult();
        JAXBContext context = JAXBContext.newInstance(vf.getClass());
        context.createMarshaller().marshal(vf, res);
        Document vfsDoc = (Document) res.getNode();
        Node newVf = vfsDoc.getFirstChild();

        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        doc.adoptNode(newVf);
        // check vf in old xml
        Node vfsNode = doc.getElementsByTagName(VIRTUAL_FABRICS).item(0);
        Node matchedVf = getVFByName(vfsNode, oldName);
        if (matchedVf == null) {
            if (allowAdd) {
                XMLUtils.appendNode(doc, vfsNode, newVf);
            } else {
                throw new IllegalArgumentException(
                        "Couldn't find Virtual Fabric '" + oldName + "'");
            }
        } else {
            XMLUtils.replaceNode(doc, vfsNode, matchedVf, newVf);
        }
        XMLUtils.writeDoc(doc, dstXml);
    }

    protected void uniqueNameCheck(VirtualFabrics vfs, String name)
            throws Exception {
        if (vfs == null) {
            File confFile = confHelp.getConfFile();
            vfs = unmarshal(confFile);
        }
        for (VirtualFabric group : vfs.getVFs()) {
            if (group.getName().equals(name)) {
                throw new DuplicateNameException(name);
            }
        }
    }

    private Node getVFByName(Node appsNode, String name) {
        NodeList children = appsNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals(VIRTUAL_FABRIC)) {
                Node nameNode = XMLUtils.getNodeByName(child, NAME);
                if (nameNode != null) {
                    if (nameNode.getTextContent().equals(name)) {
                        return child;
                    }
                }
            }
        }
        return null;
    }

    protected VirtualFabricException createVirtualFabricException(IMessage msg,
            Throwable error, Object... args) {
        return new VirtualFabricException(msg, error, args);
    }

    public boolean hasChanges() {
        return !changeMgr.getChanges().isEmpty();
    }

    public void applyChangesTo(VirtualFabricManagement target)
            throws VirtualFabricException {
        List<VirtualFabric> vfs = getVirtualFabrics();
        Map<String, VirtualFabric> map = new HashMap<String, VirtualFabric>();
        for (VirtualFabric vf : vfs) {
            map.put(vf.getName(), vf);
        }
        for (String change : changeMgr.getChanges()) {
            VirtualFabric cur = map.get(change);
            if (cur == null) {
                target.removeVirtualFabric(change);
            } else {
                target.addOrUpdateVirtualFabric(change, cur);
            }
        }
        changeMgr.resetChanges();
    }

    public void reset() {
        changeMgr.resetChanges();
    }

}
