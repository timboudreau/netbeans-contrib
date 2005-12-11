/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.spi.nodes.support;

import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public final class Nodes {
    
    /** Creates a new instance of Nodes */
    private Nodes() {
    }
    
    public static Node wrapWithFileBuiltNode(Node original) {
        return new FileBuiltNode(original);
    }
}
