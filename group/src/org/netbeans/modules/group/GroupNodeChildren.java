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


package org.netbeans.modules.group;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/** This class defines children of a <code>GroupNode</code>. */
class GroupNodeChildren extends Children.Keys {

    /** data object represented by these children's parent node */
    private final GroupShadow groupShadow;
    /** listener listening for changes of the group's content */
    private GroupContentListener groupContentListener;
    /**
     * weak listener listening on the group and notifying
     * the {@link #groupContentListener}
     */
    private PropertyChangeListener propChangeListener;

    /**
     * Creates a set of children.
     *
     * @param  groupShadow  data object represented by these children's parent
     *                      node
     */
    public GroupNodeChildren(GroupShadow groupShadow) {
        this.groupShadow = groupShadow;
    }
    
    /**
     * Listener listening for changes of the group's content.
     * When a change is detected, {@linkplain #update updates}
     * this children object.
     */
    private class GroupContentListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String propName = e.getPropertyName();
            if (DataObject.Container.PROP_CHILDREN.equals(propName)) {
                GroupNodeChildren.this.update();
            }
        }
    }

    /** */
    protected void addNotify() {
        setKeys(Collections.EMPTY_SET);
        RequestProcessor.postRequest(new Runnable() {
                                         public void run() {
                                             update();
                                         }
                                     });
        groupContentListener = new GroupContentListener();
        groupShadow.addPropertyChangeListener(
                propChangeListener = WeakListeners.propertyChange(
                        groupContentListener,
                        groupShadow));
    }

    /** */
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
        groupShadow.removePropertyChangeListener(propChangeListener);
        propChangeListener = null;
        groupContentListener = null;
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
