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

package org.netbeans.api.nodes2looks;

import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.LookSelector;
import org.openide.nodes.Node;

/**
 * Interoperability of nodes and looks.
 */
public final class Nodes {
    private static final Look NODE_LOOK = new NodeProxyLook( "NodeProxyLook" );

    private Nodes() {}

    /** Creates a new node representing an object using a look.
     * @see org.netbeans.spi.looks.Selectors#defaultTypes
     * @param representedObject the object which the node will represent
     */
    public static Node node( Object representedObject ) {
        return node (representedObject, null, null );
    }

    /** Creates new LookNode.
     * @see Look#attachTo(Object)
     * @param representedObject The object which the node will represent.
     * @param look Explicit look which will be set on the node.
     */
    public static Node node (Object representedObject, Look look ) {
        return node ( representedObject, look, null );
    }

    /** Creates new LookNode.
     * @see Look#attachTo(Object)
     * @param representedObject The object which the node will represent.
     * @param look Explicit look which will be set on the node. If null
     *        first look from the lookSelector which accepts the represented
     *        object will be used for this node. 
     * @param lookSelector LookSelector for this node.
     */
    public static Node node (Object representedObject, Look look, LookSelector lookSelector ) {
        return node ( representedObject, look, lookSelector, null );
    }

    /** Creates new LookNode.
     * @see Look#attachTo(Object)
     * @param representedObject The object which the node will represent.
     * @param look Explicit look which will be set on the node. If null
     *        first look from the lookSelector which accepts the represented
     *        object will be used for this node. 
     * @param lookSelector LookSelector for this node.
     * @param handle Node.Handle which will take care of the persistence.
     */
    public static Node node(Object representedObject, Look look, LookSelector lookSelector, Node.Handle handle ) {
        return new LookNode(
            representedObject,
            look,
            lookSelector,
            handle
        );
    }

    /** Look that works with {@link Node}. If the represented object
     * is <code>Node</code> the look takes its name, actions, properties, etc.
     * Very useful for bridging to already written nodes.
     * <P>
     * To create this look from an XML layer type:
     * <pre>
     * &lt;file name="NameOfYourLook.instance" &gt;
     *   &lt;attr name="instanceClass" stringvalue="org.netbeans.spi.looks.Look" /&gt;
     *   &lt;attr name="instanceCreate" methodvalue="org.netbeans.api.nodes2looks.Nodes.nodeLook" /&gt;
     * &lt;/file&gt;
     * </pre>
     * @return Look for bridging functionality of existing nodes
     */
    public static final Look nodeLook() {
        return NODE_LOOK;
    }


}
