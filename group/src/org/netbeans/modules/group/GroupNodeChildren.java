/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.group;

import java.util.Collections;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/** This class defines children of a <code>GroupNode</code>. */
class GroupNodeChildren extends Children.Keys {
    
    /** data object represented by these children's parent node */
    private final GroupShadow groupShadow;

    /**
     * Creates a set of children.
     *
     * @param  groupShadow  data object represented by these children's parent
     *                      node
     */
    public GroupNodeChildren(GroupShadow groupShadow) {
        this.groupShadow = groupShadow;
        groupShadow.getPrimaryFile().addFileChangeListener(
                new FileChangeAdapter() {
                    public void fileChanged(FileEvent fe) { //group file changed
                        update();
                }
            }
        );
    }

    /** */
    protected void addNotify() {
        setKeys(Collections.EMPTY_SET);
        RequestProcessor.postRequest(new Runnable() {
                                         public void run() {
                                             update();
                                         }
                                     });
    }

    /** */
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
    }

    /**
     * Updates the list of children according to the current contents
     * of the group shadow's primary file.
     */
    void update() {
        setKeys(groupShadow.getLinks());
    }

    /** */
    protected Node[] createNodes(Object key) {
        Node nodes[] = new Node[1];

        if (key instanceof DataObject) {
            nodes[0] = new LinkNode(groupShadow,
                                    ((DataObject) key).getNodeDelegate());
        } else {
            nodes[0] = new ErrorNode(groupShadow, (String) key);
        }
        return nodes;
    }
}
