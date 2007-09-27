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

import java.util.Arrays;
import java.util.List;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.looks.Look;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Lookup;

/** Tests whether notification to NodeListener is fired under Mutex.writeAccess
 *
 * @author Jiri Rechtacek
 */
public class IsLeafTest extends NbTestCase {
    
    public IsLeafTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        TestUtil.setUpRegistryToDefault();
    }
    
    public void testChangeLeafToNonLeafRepObjects () {
        
        Children ch = new Children.Array ();
        Object repLeaf = new AbstractNode (Children.LEAF);
        Object repRoot = new AbstractNode (ch);
        Look look = Nodes.nodeLook();
        
        Node lnLeaf = Nodes.node( repLeaf, look );
        Node lnRoot = Nodes.node( repRoot, look );
        
        Node n = (Node)repLeaf;
        assertTrue ("NODE - Leaf is Leaf.", lnLeaf.isLeaf () );
        assertTrue ("NODE - Root is not Leaf.", !lnRoot.isLeaf () );
        
        assertTrue ("LOOK - Leaf is Leaf.", look.isLeaf ( repLeaf, Lookup.EMPTY ) );
        assertTrue ("LOOK - Root is not Leaf.", !look.isLeaf ( repRoot, Lookup.EMPTY ) );
        
        assertEquals ("Leaf has no children.", 0, look.getChildObjects (repLeaf, Lookup.EMPTY).size());
        assertEquals ("Root has no children.", 0, look.getChildObjects (repRoot, Lookup.EMPTY).size());
        
        ch.add (new Node[] { new AbstractNode (Children.LEAF) } );
        
        assertTrue ("NODE - Leaf is Leaf.", lnLeaf.isLeaf () );
        assertTrue ("NODE - Root is not Leaf.", !lnRoot.isLeaf () );
        
        assertTrue ("LOOK - Leaf is Leaf.", look.isLeaf ( repLeaf, Lookup.EMPTY ) );
        assertTrue ("LOOK - Root is not Leaf.", !look.isLeaf ( repRoot, Lookup.EMPTY ) );
        
        assertEquals ("Leaf has no children.", 0, look.getChildObjects (repLeaf, Lookup.EMPTY).size());
        assertEquals ("Root has children.", 1, look.getChildObjects (repRoot, Lookup.EMPTY).size());
        
    }

 
    public void testChangeLeafToNonLeaf () {
        
        
        javax.swing.JTextField rep = new javax.swing.JTextField( TestIsLeafLook.LEAF );
        
        TestIsLeafLook look = new TestIsLeafLook ("LeafTestLook");
        
        
        
        Node node = Nodes.node (rep, look);
        
        Listener l = new Listener ();
        node.addNodeListener (l);
        
        // initially is leaf
        assertTrue ("Look is leaf.", look.isLeaf ( rep, Lookup.EMPTY ));
        assertTrue ("LookNode is leaf.", node.isLeaf ());
        
        if (look.getChildObjects (rep, Lookup.EMPTY ) != null)
            assertEquals ("Look has no child objects.", 0, look.getChildObjects ( rep, Lookup.EMPTY ).size ());
        l.assertEvents ("No property change", 0);
        
        // set children
        rep.setText ( TestIsLeafLook.PARENT );
        
        look.refreshChildren( rep );
        assertTrue ("Look is not leaf.", !look.isLeaf (rep, Lookup.EMPTY ));
        assertTrue ("LookNode is not leaf.", !node.isLeaf ());
        
        if (look.getChildObjects ( rep, Lookup.EMPTY ) != null)
            assertEquals ("Look has one child object.", 1, look.getChildObjects ( rep, Lookup.EMPTY ).size ());
        else
            fail ("Look has one child object.");
        l.assertEvents ("One property change", 1);
        
        // check vasted events
        look.refreshChildren ( rep );
        look.refreshChildren ( rep );
        look.refreshChildren ( rep );
        look.refreshChildren ( rep );
        l.assertEvents ("No property change", 0);
        
        // no children
        rep.setText (TestIsLeafLook.LEAF);
        look.refreshChildren ( rep );
        assertTrue ("Look is leaf.", look.isLeaf ( rep, Lookup.EMPTY ));
        assertTrue ("LookNode is leaf.", node.isLeaf ());
        if (look.getChildObjects ( rep, Lookup.EMPTY ) != null)
            assertEquals ("Look has no child objects.", 0, look.getChildObjects ( rep, Lookup.EMPTY ).size ());
        l.assertEvents ("One property change", 1);
        
        // check vasted events
        look.refreshChildren ( rep );
        look.refreshChildren ( rep );
        look.refreshChildren ( rep );
        look.refreshChildren ( rep );
        l.assertEvents ("No property change", 0);
        
        // set children
        rep.setText (TestIsLeafLook.PARENT);
        look.refreshChildren ( rep );
        look.refreshChildren ( rep );
        assertTrue ("Look is not leaf.", !look.isLeaf ( rep, Lookup.EMPTY ));
        assertTrue ("LookNode is not leaf.", !node.isLeaf ());
        if (look.getChildObjects ( rep, Lookup.EMPTY ) != null)
            assertEquals ("Look has one child object.", 1, look.getChildObjects ( rep, Lookup.EMPTY ).size ());
        else
            fail ("Look has one child object.");
        l.assertEvents ("One property change", 1);
    
    }
    
    private static class Listener extends Object implements NodeListener {
        private int leafs = 0;
        
        public void assertEvents (String txt, int eventsCount) {
            if (leafs != -1) 
                assertEquals (txt + " leafs", eventsCount, leafs);
            leafs = 0;
        }
        
        public void propertyChange (java.beans.PropertyChangeEvent evt) {
            if (Node.PROP_LEAF == evt.getPropertyName()) {
                leafs++;
            }
        }
        public void childrenAdded(NodeMemberEvent ev) {}
        public void childrenRemoved(NodeMemberEvent ev) {}
        public void childrenReordered(NodeReorderEvent ev) {}
        public void objectDestroyed(NodeEvent ev) {}
      
        public void nodeDestroyed(NodeEvent ev) {
        }
        
    } // end of Listener
    
    // substitute with public fireChildrenChange() method
    
    
    private static class TestIsLeafLook extends Look {
        
        final static String LEAF = "Leaf";
        final static String PARENT = "Parent";
                
        TestIsLeafLook( String name ) {
            super( name );
        }

        public List getChildObjects( Object ro, Lookup env ) {
            String s = ((javax.swing.JTextField)ro).getText ();
            if (LEAF.equals (s)) {
                return null;
            } else {
               return Arrays.asList (new Object[] {"Child"});
            }
        }

        public boolean isLeaf ( Object ro, Lookup env ) {
            return getChildObjects (ro, env) == null ? true : getChildObjects (ro, env).size () == 0;
        }

        public void refreshChildren( Object o ) {
            fireChange( o, Look.GET_CHILD_OBJECTS );
        }
    };
    
}
