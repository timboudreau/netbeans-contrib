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

package org.netbeans.lib.graphlayout;

/**
 *
 * @author Jaroslav Tulach
 */
public final class Edge {
    final Vertex v1, v2;
    final int strength;
    
    /** Creates a new instance of Edge */
    Edge (Vertex n1, Vertex n2, int strength) {
        this.v1 = n1;
        this.v2 = n2;
        this.strength = strength;
        n1.addEdge (this);
        n2.addEdge (this);
    }

    public Vertex getVertex1 () {
        return v1;
    }
    
    public Vertex getVertex2 () {
        return v2;
    }
}
