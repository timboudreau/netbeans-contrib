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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
        return new org.netbeans.modules.adnode.ANode(aa, a);
    }
    
}
