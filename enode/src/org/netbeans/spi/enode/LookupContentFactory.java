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
 * Software is Nokia. Portions Copyright 2003 Nokia.
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
