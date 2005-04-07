/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.adaptable;

/**
 *
 * @author Jaroslav Tulach
 */
public final class Adaptor extends java.lang.Object {
    /**
     * implementation of Adaptor functionality 
     */
    final org.netbeans.modules.adaptable.ProviderImpl impl;
    /** any data associated with the provider */
    final Object data;
    
    /**  */
    Adaptor(
        org.netbeans.modules.adaptable.ProviderImpl impl, Object data
    ) {
        this.impl = impl;
        this.data = data;
    }
    
}
