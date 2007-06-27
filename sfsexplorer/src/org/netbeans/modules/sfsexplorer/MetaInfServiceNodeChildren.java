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

import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * Children of the MetaInfServiceNode.
 * Sandip V. Chitale (Sandip.Chitale@Sun.Com), David Strupl
 */
class MetaInfServiceNodeChildren extends Children.Keys {
    private List providers;

    /**
     * Constructor.
     * @param providers 
     */
    MetaInfServiceNodeChildren(List providers) {
        this.providers = providers;
    }

    /**
     * Creates the children lazily.
     */
    protected void addNotify() {
        setKeys(providers);
    }

    /**
     * Overriden to supply the children nodes.
     * @param key 
     * @return 
     */
    protected Node[] createNodes(Object key) {
        Node node = new AbstractNode(Children.LEAF);
        String displayName = String.valueOf(key);
        if (displayName.startsWith("#position=")) {
            displayName = displayName.substring("#position=".length());
            node = new PositionNode(displayName);
        }  else {
            node = new ServiceImplNode(displayName);
        }
        return new Node[]{ node };
    }
}