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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.api.enode;

import org.openide.ErrorManager;
import org.openide.util.Lookup;

import org.netbeans.modules.enode.ExtensibleLookupImpl;

/**
 * Special lookup capable of reading its content from the system
 * file system. The lookup is bound to an instance of ExtensibleNode.
 * @author David Strupl
 */
public class ExtensibleLookup extends Lookup {

    /**
     * As the implementation is "hidden" from the API this
     * is a reference to an object with real implementation.
     */
    private ExtensibleLookupImpl impl;

    /**
     * Node to which this lookup is bound.
     */
    private ExtensibleNode myNode;
    
    /**
     * This public constructor needs an ExtensibleNode as paramater.
     * The path is to the objects found by this lookup is computed from
     * the result of calling method ExtensibleNode.getPath() on the
     * associated node.
     */
    public ExtensibleLookup(ExtensibleNode en) {
        myNode = en;
    }
    
    /**
     * The default constructor - you have to call setNode to pass ExtensibleNode
     * instance for the lookup to know from where to take the objects.
     */
    public ExtensibleLookup() {
    }
    
    /**
     * Associates the lookup object with the node.
     */
    public void setNode(ExtensibleNode en) {
        myNode = en;
        if (impl != null) {
            impl.setExtensibleNode(myNode);
        }
    }
    
    /**
     *
     */
    public Object lookup(Class clazz) {
        initializeImpl();
        return impl.lookup(clazz);
    }
    
    /**
     *
     */
    public Result lookup(Template template) {
        initializeImpl();
        return impl.lookup(template);
    }
    
    /**
     * Lazy initialization of the implementation reference.
     */
    private void initializeImpl() {
        if (impl == null) {
            impl = new ExtensibleLookupImpl();
            impl.setExtensibleNode(myNode);
        }
    }
    
}
