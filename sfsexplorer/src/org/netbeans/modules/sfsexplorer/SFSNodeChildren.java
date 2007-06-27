/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.sfsexplorer;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
class SFSNodeChildren extends Children.Keys {
    private Node node;
    private String platform;
    SFSNodeChildren(Node node, String platform) {
        this.node = node;
        this.platform = platform;
    }

    public void addNotify() {
        List childrenKeys = new LinkedList();
        DataObject dataObject = (DataObject) node.getLookup().lookup(DataObject.class);
        if (dataObject != null) {
            FileObject fileObject = dataObject.getPrimaryFile();
            if (fileObject != null && fileObject != Repository.getDefault().getDefaultFileSystem().getRoot()) {
                Enumeration attributes = fileObject.getAttributes();
                while (attributes.hasMoreElements()) {
                    String attribute = (String) attributes.nextElement();
                    if (attribute != null) {
                        Object value = fileObject.getAttribute(attribute);
                        childrenKeys.add(
                                new Object[] {node, attribute, String.valueOf(value)}
                        );
                    }
                }
            }
            if (dataObject instanceof DataFolder) {
                DataFolder dataFolder = (DataFolder)dataObject;
                DataObject[] childrenDataObjects = dataFolder.getChildren();
                for (int i = 0; i < childrenDataObjects.length; i++) {
                    childrenKeys.add(childrenDataObjects[i].getNodeDelegate());
                }
                FileObject[] hidden = XMLFileSystemCache.getInstance().
                        getHiddenChildren(dataFolder);
                for (int i = 0; i < hidden.length; i++) {
                    childrenKeys.add(hidden[i]);
                }
            }
        }

        setKeys(childrenKeys);
    }

    protected Node[] createNodes(Object key) {
        if (key instanceof Node) {
            Node node = (Node) key;
            return new Node[] {new SFSNode(node, platform)};
        } else if (key instanceof Object[]) {
                Object[] attrDesc = (Object[])key;
                Node attributeNode = new AttributeNode(
                        (Node)attrDesc[0],
                        (String)attrDesc[1],
                        attrDesc[2],
                        platform);
                return new Node[]{attributeNode};
        } else if (key instanceof FileObject) {
            // it is a hidden creature
            Node hiddenNode = new HiddenNode((FileObject)key);
            return new Node[] { hiddenNode };
        }
        return SFSBrowserTopComponent.EMPTY_NODE_ARRAY;
    }
}