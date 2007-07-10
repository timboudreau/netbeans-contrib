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

package org.netbeans.modules.looks;

import java.beans.IntrospectionException;
import org.netbeans.api.nodes2looks.Nodes;
import org.openide.nodes.BeanNode;

/** A bean look to display an object as a JavaBean.
 *
 * @author Jaroslav Tulach
 */
public final class BeanLook extends FilterLook {

    /** Creates new NodeProxySupport */
    public BeanLook( String name ) {
        super ( name, Nodes.nodeLook()); // NOI18N
    }

    /** The human presentable name of the look.
     * @return human presentable name
     */
    /*
    public String getDisplayName() {
        return NbBundle.getMessage (BeanLook.class, "LAB_JavaBeans");
    }    
    */
    /** Replaces each object with its bean node.
     */
    protected Object delegateObject (Object representedObject) {
        try {
            return new BeanNode (representedObject);
        } catch (IntrospectionException ex) {
            return ex;
        }
    }
    
}
