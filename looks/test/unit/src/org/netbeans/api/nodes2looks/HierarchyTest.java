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

import java.lang.ref.WeakReference;
import org.openide.util.Lookup;
import org.openide.util.io.NbMarshalledObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Handle;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.Looks;
import org.netbeans.spi.looks.Selectors;

public class HierarchyTest extends NbTestCase {

    private Node node;

    public HierarchyTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        
        ArrayList l1 = new ArrayList ();
        
        ArrayList l2 = new ArrayList ();
        l2.add ("1");
        l2.add ("2");
        
        l1.add (l2);
        
        l2 = new ArrayList ();
        l2.add ("A");
        l2.add ("B");
        
        l1.add (l2);
        node = createLookNode (l1, null);
        
    }
                 
    private static Node createLookNode(final Object obj, Handle handle) {        
        if (handle == null) {
            handle = new TestHandle (obj);
        }
        
        return Nodes.node (obj, 
            Looks.childrenSelectorProvider(
                "Root_JLD", 
                CL.FIRST_CL,
                Selectors.array( new Look[] { CL.FIRST_CL, CL.SECOND_CL, CL.FIRST, CL.SECOND } ) ),
            
            Selectors.array( new Look[] { CL.FIRST_CL } ),
            handle  
        );
                
    }
    
    protected void tearDown() throws Exception {
        node = null;
    }
    
    /** Checks the hierarchy of nodes.
     */
    public void testHierarchy () {                
        checkLook (node.getChildren().getNodes(), CL.FIRST_CL);        
    }
    
    /** Tests the look on clone.
     */
    public void testClonedHierarchy () {                
        checkLook (node.cloneNode().getChildren().getNodes(), CL.FIRST_CL);        
    }
    
    /** Changes the look and check the result.
     */
    public void testChangedLookOnHierarchy () {
        Node n = node.cloneNode ();
        
        TestUtil.setLook (n, CL.SECOND_CL);
        
        assertEquals ("Look must change ", CL.SECOND_CL, TestUtil.getLook( n ));
        // have not changed look selector for children
        checkLook (n.getChildren ().getNodes (), CL.FIRST_CL);
        
        TestUtil.setLook ( n, CL.SECOND );
        assertEquals ("LookSelector must change ", CL.SECOND, TestUtil.getLook( n ));
        checkLook (n, CL.SECOND_CL);

    }
        
    /** Selects a look on a subnode, waits till the node is garbage collected
     * and checks whether the look stays there.
     */
    public void testLookSelector () throws Exception {
        
        Node n = node.cloneNode ();
        Node[] arr = n.getChildren ().getNodes ();
                
        Node ln = arr[0];
                
        TestUtil.setLook( ln, CL.SECOND );
        assertEquals( "Look should change. ", CL.SECOND, TestUtil.getLook( ln ) );
        checkLook ( ln.getChildren().getNodes(), CL.SECOND_CL);
        
        WeakReference ref = new WeakReference (ln);
        
        ln = null;
        arr = null;
        
        assertGC ("The node must disapear", ref);
        
        arr = n.getChildren ().getNodes ();
                
        ln = arr[0];
        
        assertEquals ("Look should remain changed", CL.SECOND, TestUtil.getLook( ln ) );
        checkLook (ln.getChildren().getNodes(), CL.SECOND_CL);
        
    }
    
    
    /** Test persistence of change of look descriptor.
     */
    public void testLookSelectorSerialization () throws Exception {
        
        Node n = node.cloneNode ();
        Node[] arr = n.getChildren ().getNodes ();
        
        
        Node ln = arr[0];
                
        TestUtil.setLook( ln, CL.SECOND );
        assertEquals ("Look should change", CL.SECOND, TestUtil.getLook( ln ) );
        checkLook (ln.getChildren().getNodes(), CL.SECOND_CL);
        
        Object oldRO = TestUtil.getRepresentedObject( n );
        
        Handle h = n.getHandle ();
        assertNotNull ("Every node has a handle", h);
        h = (Handle)new NbMarshalledObject (h).get ();
        n = h.getNode ();
        
        assertEquals ("Represented object remains equal", oldRO, TestUtil.getRepresentedObject ( n ));
        assertTrue ("but not the same (deserialized)", oldRO != TestUtil.getRepresentedObject ( n ));
        
        arr = n.getChildren ().getNodes ();
        
        
        ln = arr[0];
        
        assertEquals ("Look should remain changed", CL.SECOND, TestUtil.getLook( ln ) );
        checkLook (ln.getChildren().getNodes(), CL.SECOND_CL);
        
    }
        
        
    /** Checks the look */
        
    private static void checkLook (Node from, Look look) {
        checkLook (from.getChildren ().getNodes (), look);
    }
    
    private static void checkLook (Node[] arr, Look look) {
        for (int i = 0; i < arr.length; i++) {            
            Node l = arr[i];
         
            assertEquals ("Inherits the look", TestUtil.getLook( l ), look);
            
            checkLook (l, look);
        }
    }
     
       
    /** A look that represents java.util.Collection.
     */
   
    private static final class CL extends Look {
        
        // We want two Looks which:
        // 1. change the look to itself
        // 2. do this change fro the whole hierarchy
        
        private static final CL FIRST_CL = new CL ("First_LOOK");
        private static final CL SECOND_CL = new CL ("Second_LOOK");
        
        public static final Look FIRST = Looks.childrenSelectorProvider(
            "First_JLD", 
            FIRST_CL,
            Selectors.array( new CL[] { FIRST_CL, SECOND_CL } ) );
            
        public static final Look SECOND = Looks.childrenSelectorProvider(
            "Second_JLD", 
            SECOND_CL,
            Selectors.array( new CL[] { SECOND_CL, FIRST_CL } ) );
        
        
        private CL (String name) {
            super (name);
        }
        
        public boolean isLeaf ( Object representedObject, Lookup env ) {
            return ! (representedObject instanceof List);
        }
                
        public java.util.List getChildObjects( Object representedObject, Lookup env ) {
            if ("Empty".equals (getName ())) return null;
            
            if (representedObject instanceof Collection) {
                Collection c = (Collection)representedObject;
                return new ArrayList( c );
            } else {
                return null;
            }
        }
                
        public String getName( Object representedObject, Lookup env ) {
            return representedObject.toString();
        }
        
        public String toString () {
            return "CL[" + getName () + "]";
        }
    }
    
    static class TestHandle implements Handle {
        static final long serialVersionUID = 4503853940L;
        
        private Object x;

        public TestHandle (Object x) {
            this.x = x;
        }

        public Node getNode () {
            return createLookNode (x, this);
        }
    }
   
   
}
