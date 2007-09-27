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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.LookSelector;

/** Cache that stores and persists mapping between
 * nodes and their associated look descriptors.
 *
 * @author  Petr Hrebejk
 */
final class Cache extends Object implements Serializable, Node.Handle {
    static final long serialVersionUID = 5338245712342L;

    private Node.Handle handle;
    private Map cache = new HashMap();

    public Cache() {}

    public Cache (Node.Handle del) {
        this.handle = del instanceof Cache ? ((Cache)del).handle : del;
    }
    
    public Node getNode() throws java.io.IOException {
        Node n = handle.getNode ();
        if (n instanceof LookNode) {
            LookNode ln = (LookNode)n;
            this.handle = ln.getCache().handle;
            ln.setCache( this );
        }
        return n;
    }


    public void store (LookNode node) {

        org.netbeans.spi.looks.Look ld = node.getLook();
        Object ro = node.getRepresentedObject();
        String roID = ld.getName( ro, node.getLookup() );


        if ( roID == null ) {
            return;
        }

        String nodePath = getLookNodePath( node.getParentNode() );

        List items = (List)cache.get( nodePath );
        if ( items == null ) {
            items = new ArrayList();
            cache.put( nodePath, items );
        }

        String item[] = { ld.getName(), ro.getClass().getName(), roID};
        items.add( item );


    }

    public Look find (LookNode parentNode, Object representedObject ) {

        String path = getLookNodePath( parentNode );

        List items = (List)cache.get( path );

        if ( items == null ) {
            return null;
        }

        // findItem( );

        HashMap ldName2ld = new HashMap();

        for( Iterator it = items.iterator(); it.hasNext(); ) {
            String[] item = (String[])it.next();

            if (!item[1].equals (representedObject.getClass().getName())) {
                continue;
            }

            Look look = LookSelector_getLook ( parentNode.getLookSelectorForChildren(), item[0], representedObject );
            if (look == null) {
                continue;
            }

            if (look != null && item[2].equals (look.getName ( representedObject, Lookup.EMPTY ))) { // PENDING
                return look;
            }
        }

        return null;
    }


    // methods to track the state of the tree ----------------------------------

    // PENDING to be removed
    private static Look LookSelector_getLook ( LookSelector ls, String name, Object representedObject ) {

        Enumeration e = ls.getLooks( representedObject );

        while( e.hasMoreElements() ) {
            Look l = (Look)e.nextElement();
            if ( l.getName().equals( name ) ) {
                return l;
            }
        }

        return null;
    }


    private static String getLookNodePath( Node node ) {
        StringBuffer buf = new StringBuffer (512);

        for (;;) {
            if (!(node instanceof LookNode) ) {
                break;
            }

            buf.insert (0, node.getName() );
            node = node.getParentNode();
        }

        return buf.toString();
    }

}
