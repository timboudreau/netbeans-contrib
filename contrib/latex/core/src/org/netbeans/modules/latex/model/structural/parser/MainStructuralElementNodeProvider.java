/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural.parser;

import java.beans.IntrospectionException;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.modules.latex.model.structural.NodeProvider;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class MainStructuralElementNodeProvider extends NodeProvider {
    
    /** Creates a new instance of MainStructuralElementNodeFactory */
    public MainStructuralElementNodeProvider() {
    }
    
    public Node createNode(StructuralElement element) throws IntrospectionException {
        if (element instanceof MainStructuralElement) {
            Object cached = getCache().get(element);
            
            if (cached != null)
                return (Node) cached;
            
            Node created = new MainStructuralElementNode((MainStructuralElement) element);
            
            getCache().put(element, created);
            
            return created;
        }
        
        return null;
    }
    
    private static synchronized Map getCache() {
        if (cache == null) {
            cache = new WeakHashMap();
        }
        
        return cache;
    }
    
    private static Map cache = null;
}
