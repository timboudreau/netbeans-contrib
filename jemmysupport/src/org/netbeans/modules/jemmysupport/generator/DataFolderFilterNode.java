/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.generator;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/** Filter nodes to display only folders (that ones which has DataFolder cookie)
 *
 * @author  Jiri.Skrivanek@sun.com
 */
final class DataFolderFilterNode extends FilterNode {
    
    /** Creates new DataFolderFilterNode. */
    public DataFolderFilterNode(Node node) {
        super(node, new DataFolderFilterChildren(node));
    }
    
    private static class DataFolderFilterChildren extends FilterNode.Children {
        
        /** Creates new DataFolderFilterChildren. */
        public DataFolderFilterChildren(Node node) {
            super(node);
        }
        
        protected Node[] createNodes(Object key) {
            Node node = (Node)key;
            // without filtering
            // return new Node[] { copyNode(n) };
  
            DataObject dataObj = (DataObject)node.getCookie(DataObject.class);
            if(dataObj != null) {
                // if data object exists
                Object dataFolder = node.getCookie(org.openide.loaders.DataFolder.class);
                if(dataFolder == null) {
                    // if it is not a data folder, don't show it
                    return new Node[] {};
                }
            }
            // otherwise continue recursively
            return new Node[] { new DataFolderFilterNode(node) };
        }
    }
}
