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

package org.netbeans.modules.adnode;

import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.Adaptor;
import org.openide.nodes.Node;

/** Allows to create a Node for given Adaptable provider.
 *
 * @author Jaroslav Tulach
 */
public class AdaptableNodes {
    
    /**
     * No instances please.
     */
    private AdaptableNodes () {
    }

    
    /** Given the provider of Adaptable definitions for objects creates 
     * a node that uses such informations to provide visual, in explorer
     * presentation of an object.
     * @param a adaptor providing the aspects of the object
     * @param o the object to display
     */
    public static Node create(Adaptor a, Object o) {
        Adaptable aa = a.getAdaptable(o);
        return new org.netbeans.modules.adnode.ANode(aa);
    }
    
}
