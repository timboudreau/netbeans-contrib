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

package org.netbeans.modules.enode.test;

import org.netbeans.spi.enode.LookupContentFactory;
import org.openide.nodes.Node;

/**
 *
 * @author  david
 */
public class C1Factory implements LookupContentFactory {
    
    /** Creates a new instance of C1Factory */
    public C1Factory() {
    }
    
    public Object create(Node n) {
        return new MONodeEnhancerImpl(n);
    }
    
    public org.openide.util.Lookup createLookup(Node n) {
        return null;
    }
    
}
