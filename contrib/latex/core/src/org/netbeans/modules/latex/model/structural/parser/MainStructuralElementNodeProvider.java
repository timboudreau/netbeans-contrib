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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
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
