/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.spi.enode;

import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * LookupContentFactories are registered for each content prefix
 * to provide content of lookups bound to individual Nodes.
 * @author David Strupl
 */
public interface LookupContentFactory {
    
    /**
     * The create method is asked to create an object
     * returned by lookup bound to the Node n.
     * @param Node n 
     * @returns Object it is the instance returned
     *   by the lookup obtained as <code> n.getLookup() </code>
     */
    public Object create(Node n);
    
    /**
     * The createLookup method is asked to create a lookup
     * that is merged with the lookup of the node n.
     * @param Node n 
     * @returns Lookup which will be merged with other
     * lookups provided by other modules
     */
     public Lookup createLookup(Node n);
}
