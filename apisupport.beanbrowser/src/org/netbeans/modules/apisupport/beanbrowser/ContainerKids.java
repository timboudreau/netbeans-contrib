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

    protected void removeNotify() {
        if (containerListener != null) {
            container.removeContainerListener(containerListener);
            containerListener = null;
        }
        setKeys(Collections.EMPTY_SET);
    }
    
    private void updateKeys() {
        setKeys(container.getComponents());
    }
    
    protected Node[] createNodes(Object key) {
        return new Node[] { PropSetKids.makeObjectNode(key) };
    }
    
}
