/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.beanbrowser;

import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.Collections;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/** Children list of an AWT Container. */
public class ContainerKids extends Children.Keys {
    
    private Container container;
    private ContainerListener containerListener = null;
    
    public ContainerKids(Container container) {
        this.container = container;
    }
    
    protected void addNotify() {
        updateKeys();
        if (containerListener == null) {
            containerListener = new ContainerListener() {
                public void componentAdded(ContainerEvent ev) {
                    updateKeys();
                }
                public void componentRemoved(ContainerEvent ev) {
                    updateKeys();
                }
            };
            container.addContainerListener(containerListener);
        }
    }
    private void cleanUp() {
        if (containerListener != null) {
            container.removeContainerListener(containerListener);
            containerListener = null;
        }
    }
    protected void removeNotify() {
        cleanUp();
        setKeys(Collections.EMPTY_SET);
    }
    protected void finalize() {
        cleanUp();
    }
    
    private void updateKeys() {
        setKeys(container.getComponents());
    }
    
    protected Node[] createNodes(Object key) {
        return new Node[] { PropSetKids.makeObjectNode(key) };
    }
    
}
