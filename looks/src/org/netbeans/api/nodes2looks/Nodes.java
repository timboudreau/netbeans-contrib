/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
