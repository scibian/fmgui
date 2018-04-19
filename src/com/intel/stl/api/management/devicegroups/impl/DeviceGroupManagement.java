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

package com.intel.stl.api.management.devicegroups.impl;

import static com.intel.stl.api.management.XMLConstants.DEVICE_GROUP;
import static com.intel.stl.api.management.XMLConstants.DEVICE_GROUPS;
import static com.intel.stl.api.management.XMLConstants.NAME;

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
import com.intel.stl.api.management.ReferenceConflictException;
import com.intel.stl.api.management.XMLUtils;
import com.intel.stl.api.management.devicegroups.DeviceGroup;
import com.intel.stl.api.management.devicegroups.DeviceGroupException;
import com.intel.stl.api.management.devicegroups.DeviceGroups;
import com.intel.stl.api.management.devicegroups.IDeviceGroupManagement;
import com.intel.stl.common.STLMessages;

public class DeviceGroupManagement implements IDeviceGroupManagement {
    private final static Logger log =
            LoggerFactory.getLogger(DeviceGroupManagement.class);

    private final static Set<String> RESERVED = new HashSet<String>() {
        private static final long serialVersionUID = -8507198541424973196L;

        {
            add("All");
            add("AllFIs");
            add("AllSWs");
            add("AllSWE0s");
            add("AllEndNodes");
            add("AllSMs");
            add("AllMgmtAllowed");
            add("HFIDirectConnect");
            add("Self");
        }
    };

    private final FMConfHelper confHelp;

    private final ChangeManager changeMgr = new ChangeManager();

    /**
     * Description:
     *
     * @param confHelp
     */
    public DeviceGroupManagement(FMConfHelper confHelp) {
        super();
        this.confHelp = confHelp;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroups.IDeviceGroupManagement#
     * getReservedDeviceGroups()
     */
    @Override
    public Set<String> getReservedDeviceGroups() {
        return RESERVED;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroup.IDeviceGroupManagement#
     * getDeviceGroups()
     */
    @Override
    public synchronized List<DeviceGroup> getDeviceGroups()
            throws DeviceGroupException {
        try {
            File confFile = confHelp.getConfFile();
            DeviceGroups groups = unmarshal(confFile);
            log.info("Fetch " + groups.getGroups().size()
                    + " Device Groups from host '" + confHelp.getHost() + "'");
            return groups.getGroups();
        } catch (Exception e) {
            throw createDeviceGroupException(STLMessages.STL63011_GET_DGS_ERR,
                    e, confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroup.IDeviceGroupManagement#
     * getDeviceGroup(java.lang.String)
     */
    @Override
    public synchronized DeviceGroup getDeviceGroup(String name)
            throws DeviceGroupException {
        try {
            File confFile = confHelp.getConfFile();
            DeviceGroups groups = unmarshal(confFile);
            return groups.getGroup(name);
        } catch (Exception e) {
            throw createDeviceGroupException(STLMessages.STL63016_GET_DG_ERR, e,
                    name, confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    /**
     * <i>Description:</i>
     *
     * @param confFile
     * @return
     */
    private DeviceGroups unmarshal(File xmlFile) throws Exception {
        XMLInputFactory xif = XMLInputFactory.newFactory();
        StreamSource xml = new StreamSource(xmlFile);
        final XMLStreamReader xsr = xif.createXMLStreamReader(xml);
        while (xsr.hasNext()) {
            if (xsr.isStartElement()
                    && xsr.getLocalName().equals(DEVICE_GROUPS)) {
                break;
            }
            xsr.next();
        }

        JAXBContext jc = JAXBContext.newInstance(DeviceGroups.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<DeviceGroups> jb =
                unmarshaller.unmarshal(xsr, DeviceGroups.class);
        xsr.close();

        return jb.getValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroup.IDeviceGroupManagement#
     * addDeviceGroup(com.intel.stl.api.management.devicegroup.DeviceGroup)
     */
    @Override
    public synchronized void addDeviceGroup(DeviceGroup group)
            throws DeviceGroupException {
        try {
            File confFile = confHelp.getConfFile();
            uniqueNameCheck(null, group.getName());
            // TODO loop check
            addDeviceGroup(confFile, confFile, group);
            log.info("Added application " + group);
            changeMgr.addChange(group.getName());
        } catch (Exception e) {
            throw createDeviceGroupException(STLMessages.STL63012_ADD_DG_ERR, e,
                    group.getName(), confHelp.getHost(),
                    StringUtils.getErrorMessage(e));
        }
    }

    protected void addDeviceGroup(File srcXml, File dstXml, DeviceGroup group)
            throws Exception {
        // transfer app to DOM
        DOMResult res = new DOMResult();
        JAXBContext context = JAXBContext.newInstance(group.getClass());
        context.createMarshaller().marshal(group, res);
        Document groupsDoc = (Document) res.getNode();
        Node newGroup = groupsDoc.getFirstChild();

        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        // check app in old xml
        Node groupsNode = doc.getElementsByTagName(DEVICE_GROUPS).item(0);
        Node matchedGroup = getGroupByName(groupsNode, group.getName());
        if (matchedGroup != null) {
            throw new IllegalArgumentException(
                    "Device Group '" + group.getName() + "' alreday exist!");
        }

        // append app to Applications node
        XMLUtils.appendNode(doc, groupsNode, newGroup);

        // save back to xml file
        XMLUtils.writeDoc(doc, dstXml);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroup.IDeviceGroupManagement#
     * removeDeviceGroup(java.lang.String)
     */
    @Override
    public synchronized void removeDeviceGroup(String name)
            throws DeviceGroupException {
        try {
            File confFile = confHelp.getConfFile();
            referenceCheck(null, name);
            removeDeviceGroup(confFile, confFile, name);
            log.info("Removed application '" + name + "'");
            changeMgr.addChange(name);
        } catch (Exception e) {
            throw createDeviceGroupException(STLMessages.STL63013_REMOVE_DG_ERR,
                    e, name, confHelp.getHost(),
                    StringUtils.getErrorMessage(e));
        }
    }

    protected void removeDeviceGroup(File srcXml, File dstXml, String name)
            throws Exception {
        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        // check app in old xml
        Node groupsNode = doc.getElementsByTagName(DEVICE_GROUPS).item(0);
        Node matchedGroup = getGroupByName(groupsNode, name);
        if (matchedGroup != null) {
            XMLUtils.removeNode(doc, groupsNode, matchedGroup, name);

            // save back to xml file
            XMLUtils.writeDoc(doc, dstXml);
        } else {
            // this can happen when we create a new one and then rename it
            log.warn("Couldn't find Device Group '" + name + "'");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroup.IDeviceGroupManagement#
     * updateDeviceGroup(java.lang.String,
     * com.intel.stl.api.management.devicegroup.DeviceGroup)
     */
    @Override
    public synchronized void updateDeviceGroup(String oldName,
            DeviceGroup group) throws DeviceGroupException {
        try {
            File confFile = confHelp.getConfFile();
            if (!oldName.equals(group.getName())) {
                DeviceGroups groups = unmarshal(confFile);
                uniqueNameCheck(groups, group.getName());
                referenceCheck(groups, oldName);
            }
            // TODO loop check
            updateDeviceGroup(confFile, confFile, oldName, group, false);
            log.info("Updated Device Group " + group);
            changeMgr.addChange(oldName);
            changeMgr.addChange(group.getName());
        } catch (Exception e) {
            throw createDeviceGroupException(STLMessages.STL63014_UPDATE_DG_ERR,
                    e, group.getName(), confHelp.getHost(),
                    StringUtils.getErrorMessage(e));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroup.IDeviceGroupManagement#
     * addOrUpdateDeviceGroup(java.lang.String,
     * com.intel.stl.api.management.devicegroup.DeviceGroup)
     */
    @Override
    public synchronized void addOrUpdateDeviceGroup(String oldName,
            DeviceGroup group) throws DeviceGroupException {
        try {
            File confFile = confHelp.getConfFile();
            referenceCheck(null, oldName);
            // TODO loop check
            updateDeviceGroup(confFile, confFile, oldName, group, true);
            log.info("Added or updated Device Group " + group);
            changeMgr.addChange(oldName);
            changeMgr.addChange(group.getName());
        } catch (Exception e) {
            throw createDeviceGroupException(
                    STLMessages.STL63015_ADDUPDATE_DG_ERR, e, group.getName(),
                    confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    protected void updateDeviceGroup(File srcXml, File dstXml, String oldName,
            DeviceGroup group, boolean allowAdd) throws Exception {
        // transfer app to DOM
        DOMResult res = new DOMResult();
        JAXBContext context = JAXBContext.newInstance(group.getClass());
        context.createMarshaller().marshal(group, res);
        Document groupsDoc = (Document) res.getNode();
        Node newGroup = groupsDoc.getFirstChild();

        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        doc.adoptNode(newGroup);
        // check app in old xml
        Node groupsNode = doc.getElementsByTagName(DEVICE_GROUPS).item(0);
        Node matchedGroup = getGroupByName(groupsNode, oldName);
        if (matchedGroup == null) {
            if (allowAdd) {
                XMLUtils.appendNode(doc, groupsNode, newGroup);
            } else {
                throw new IllegalArgumentException(
                        "Couldn't find Device Gruop '" + oldName + "'");
            }
        } else {
            XMLUtils.replaceNode(doc, groupsNode, matchedGroup, newGroup);
        }
        XMLUtils.writeDoc(doc, dstXml);
    }

    protected void referenceCheck(DeviceGroups groups, String name)
            throws Exception {
        if (groups == null) {
            File confFile = confHelp.getConfFile();
            groups = unmarshal(confFile);
        }
        List<DeviceGroup> refs = groups.getReferencedGroups(name);
        if (!refs.isEmpty()) {
            String[] refNames = new String[refs.size()];
            for (int i = 0; i < refNames.length; i++) {
                refNames[i] = refs.get(i).getName();
            }
            throw new ReferenceConflictException(name, refNames);
        }
    }

    protected void uniqueNameCheck(DeviceGroups groups, String name)
            throws Exception {
        if (groups == null) {
            File confFile = confHelp.getConfFile();
            groups = unmarshal(confFile);
        }
        for (DeviceGroup group : groups.getGroups()) {
            if (group.getName().equals(name)) {
                throw new DuplicateNameException(name);
            }
        }
    }

    private Node getGroupByName(Node appsNode, String name) {
        NodeList children = appsNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals(DEVICE_GROUP)) {
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

    protected DeviceGroupException createDeviceGroupException(IMessage msg,
            Throwable error, Object... args) {
        return new DeviceGroupException(msg, error, args);
    }

    public boolean hasChanges() {
        return !changeMgr.getChanges().isEmpty();
    }

    public void applyChangesTo(DeviceGroupManagement target)
            throws DeviceGroupException {
        List<DeviceGroup> groups = getDeviceGroups();
        Map<String, DeviceGroup> map = new HashMap<String, DeviceGroup>();
        for (DeviceGroup group : groups) {
            map.put(group.getName(), group);
        }
        for (String change : changeMgr.getChanges()) {
            DeviceGroup cur = map.get(change);
            if (cur == null) {
                target.removeDeviceGroup(change);
            } else {
                target.addOrUpdateDeviceGroup(change, cur);
            }
        }
        changeMgr.resetChanges();
    }

    public void reset() {
        changeMgr.resetChanges();
    }
}
