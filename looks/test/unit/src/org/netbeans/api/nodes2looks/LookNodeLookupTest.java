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

import junit.framework.*;
import junit.textui.TestRunner;
import java.util.*;
import org.netbeans.spi.looks.*;
import org.openide.nodes.*;
import org.openide.cookies.InstanceCookie;

import org.netbeans.junit.*;
import org.openide.util.Lookup;

import org.openide.util.LookupListener;
import org.openide.util.lookup.*;

/**
 * @author Jaroslav Tulach, Jiri Rechtacek
 */
public class LookNodeLookupTest extends NbTestCase {
    public LookNodeLookupTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(LookNodeLookupTest.class));
    }

    protected void setUp() throws Exception {
        super.setUp();
        TestUtil.setUpRegistryToDefault();
    }
    
    public void testChangesAreFiredFromLookup () {
        Collection ro = new ArrayList();
        TestLookupLook look = new TestLookupLook( "TLT" );
        
        Node node = Nodes.node (ro, look);

        checkInstanceInGetCookie ( ro, new Node.Cookie () {}, look, node);
        checkInstanceInGetLookup ( ro, new Node.Cookie () {}, look, node, true);
        checkInstanceInGetLookup ( ro, new Node.Cookie () {}, look, node, true);
        checkInstanceInGetLookup ( ro, "Some string", look, node, true);
        
    }
    
    public void testInstanceCookieOfInLookup () throws Exception  {
                
        Object rep = new AbstractNode (Children.LEAF);
        Look look = Nodes.nodeLook ();
        Node node = Nodes.node (rep, look);
        
        InstanceCookie.Of icOf = (InstanceCookie.Of)node.getLookup ().lookup (InstanceCookie.Of.class);
        
        assertNotNull ("Ic cookie should not be null.", icOf);
        assertNotNull ("Ro from LookNode's lookup.", icOf.instanceCreate());
        assertEquals ("Sample look ro is same ro lookup.", icOf.instanceCreate(), rep); 
        
    }

    public void testChangesAreFiredFromLookupThruFilterNode () {
        Collection ro = new ArrayList();
        TestLookupLook look = new TestLookupLook( "TLT" );
        
        Node node = new FilterNode (Nodes.node (ro, look));

        checkInstanceInGetLookup ( ro, new Node.Cookie () {}, look, node, true);
        checkInstanceInGetLookup ( ro, "Some string", look, node, true);
        
    }

    public void testChangesAreFiredFromLookupThruFilterNodeWithOverWrittenGetCookie () {
        
        final Node.Cookie myInstance = new Node.Cookie () { };
        
        Collection ro = new ArrayList();
        TestLookupLook look = new TestLookupLook( "TLT" );
        
        Node node = new FilterNode (Nodes.node (ro, look)) {
            public Node.Cookie getCookie (Class clazz) {
                if (clazz == myInstance.getClass ()) {
                    return myInstance;
                }
                return super.getCookie (clazz);
            }
        };

        checkInstanceInGetCookie (ro, new Node.Cookie () {}, look, node);
        checkInstanceInGetLookup (ro, new Node.Cookie () {}, look, node, true);
        // by overwriting the FilterNode.getCookie we disable enhanced support
        // for non-cookie objects in original lookup
        checkInstanceInGetLookup (ro, "Some string", look, node, false);
        
        assertEquals ("It is possible to get myInstance from getCookie", myInstance, node.getCookie (myInstance.getClass ()));
        assertEquals ("It also possible to get it from getLookup", myInstance, node.getLookup ().lookup (myInstance.getClass ()));
       
    }

    public void testOldEnvInLookup() {

        final String oldCookie = "OLD";
        final String newCookie = "NEW";
        
        Collection ro = new ArrayList();
        ro.add( oldCookie );
        TestLookupLook look = new TestLookupLook( "OETLT" );
        
        Node n = Nodes.node( ro, look );
        n.getLookup().lookup( Object.class ); // To initialize
        
        assertEquals( "Old items should be empty.", 0, look.getValues().size() );
        
        ro.remove( oldCookie );
        ro.add( newCookie );
        
        look.lookupChange( ro );
        
        Lookup newLookup = n.getLookup();
        Collection oldValues = look.getValues();
        
        assertTrue( "New should conntain newCookie. ", newCookie == newLookup.lookup( String.class ) );
        assertEquals( "1 string in new Lookup ", 1, newLookup.lookup( new Lookup.Template( String.class ) ).allItems().size() ); 
        
        assertTrue( "Old should conntain oldCookie. ", oldValues.contains( oldCookie ) );
        assertEquals( "1 string in old Lookup ", 2, oldValues.size() ); 
    }
    
    public void testLookupNoInit() {
        
        Collection ro = new ArrayList();
        TestLookupLook look = new TestLookupLook( "OETLT" );
        Node n = Nodes.node( ro, look );
        
        n.getIcon( 0 );
        n.getDisplayName();
        n.getName();
        
        assertEquals( "getLookupItems should not be called", 0, look.getLookupItemsCallCount );
        
    }
    
    
    private void checkInstanceInGetCookie (Collection ro, Node.Cookie obj, TestLookupLook look, Node node) {
        
        Listener listener = new Listener ();
        node.addNodeListener(listener);
        
        assertNull ("The object is not there yet", node.getCookie (obj.getClass ()));
        
        ro.add (obj);
        look.lookupChange( ro );
        listener.assertEvents ("One change in node", 1, -1);

        if (obj instanceof Node.Cookie) {
            assertEquals ("Can access cookie in the content", obj, node.getCookie (obj.getClass ()));
        } else {
            assertNull ("Cannot access noncookie in the content", node.getCookie (obj.getClass ()));
        }

        ro.remove (obj);
        look.lookupChange( ro );
        listener.assertEvents ("One change in node", 1, -1);
         
    }
    
    private void checkInstanceInGetLookup (Collection ro, Object obj, TestLookupLook look, Node node, boolean shouldBeThere) {
        
        Listener listener = new Listener ();
        Lookup.Result res = node.getLookup ().lookup (new Lookup.Template (obj.getClass ()));
        Collection ignore = res.allItems ();
        res.addLookupListener(listener);

        ro.add (obj);
        look.lookupChange( ro );
        if (shouldBeThere) {
            listener.assertEvents ("One change in node's lookup (add)", -1, 1);
            assertEquals ("Can access object in content via lookup", obj, node.getLookup ().lookup (obj.getClass ()));
        } else {
            assertNull ("Cannot access object in content via lookup", node.getLookup ().lookup (obj.getClass ()));
        }
            
        
        ro.remove (obj);
        look.lookupChange( ro );
        if (shouldBeThere) {
            listener.assertEvents ("One change in node's lookup (remove)", -1, 1);
        }
        assertNull ("Cookie is removed", node.getLookup ().lookup (obj.getClass ()));
        
    }
    
    
    //
    // Test to see correct behaviour from getCookie to lookup
    //
    public void testNodeIsInItsLookup () {        
        Collection ro = new ArrayList();
        TestLookupLook look = new TestLookupLook( "TLT" );
        
        
        Node n = Nodes.node (ro, look);
        ro.add (n);
        look.lookupChange( ro );
        assertEquals ("Node is there", n, n.getLookup ().lookup (Node.class));
        
    }
    
    
    private void checkInstanceInLookup (Node.Cookie obj, CookieSet ic, Lookup l) {
        Listener listener = new Listener ();
        Lookup.Result res = l.lookup (new Lookup.Template (Object.class));
        Collection justToEnsureChangesToListenerWillBeFired = res.allItems ();
        res.addLookupListener(listener);
        
        ic.add (obj);
        listener.assertEvents ("One change in lookup", -1, 1);

        assertEquals ("Can access cookie in the content", obj, l.lookup (obj.getClass ()));

        ic.remove (obj);
        listener.assertEvents ("One change in lookup", -1, 1);
        
        ic.add (obj);
        listener.assertEvents ("One change in lookup", -1, 1);

        assertEquals ("Can access cookie in the content", obj, l.lookup (obj.getClass ()));

        ic.remove (obj);
        listener.assertEvents ("One change in lookup", -1, 1);
        
    }
    
    
    
    //
    // Garbage collect
    //
    
//    public void testBackwardCompatibleAbstractNodeLookupCanBeGarbageCollected () {
//        // Note: doesn't work because lookup is holded by substitute too
//        //AbstractNode n = new AbstractNode (Children.LEAF);
////        InstanceContent ic = new InstanceContent ();
////        final AbstractLookup lookup = new AbstractLookup (ic);
////        Node rep = new AbstractNode (Children.LEAF, lookup);
//        AbstractNode rep = new AbstractNode (Children.LEAF);
//        Look sampleLook = new SampleLook( "Sample look" ) { 
//            public String getName( Look.NodeSubstitute ns ) {
//                return "Node name";
//            }
////            public Look.NodeSubstitute attachTo (java.lang.Object representedObject) {
////                return new Look.NodeSubstitute (representedObject, this, lookup);
////            }
//        };
//        
//        Node n = new LookNode (rep, sampleLook);
//        
//        Lookup l = n.getLookup ();
//        assertEquals ("Two invocations share the same lookup", l, n.getLookup ());
//        
//        java.lang.ref.WeakReference ref = new java.lang.ref.WeakReference (l);
//        l = null;
//        assertGC ("Lookup can be GCed", ref);
//    }        
    
    
    
    private static class Listener implements LookupListener, NodeListener {
        private int cookies;
        private int lookups;
        
        public void assertEvents (String txt, int cookies, int lookups) {
            //org.openide.util.lookup.AbstractLookupTest.waitForFiring();
            if (cookies != -1) 
                assertEquals (txt + " cookies", cookies, this.cookies);
            if (lookups != -1) 
                assertEquals (txt + " lookups", lookups, this.lookups);
            
            this.cookies = 0;
            this.lookups = 0;
        }
        
        public void childrenAdded(NodeMemberEvent ev) {
        }
        
        public void childrenRemoved(NodeMemberEvent ev) {
        }
        
        public void childrenReordered(NodeReorderEvent ev) {
        }
        
        public void nodeDestroyed(NodeEvent ev) {
        }
        
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (Node.PROP_COOKIE == evt.getPropertyName()) {
                cookies++;
            }
        }
        
        public void resultChanged(org.openide.util.LookupEvent ev) {
            lookups++;
        }
        
    } // end of Listener
    

    private static class TestLookupLook extends Look {
        
        private int getLookupItemsCallCount;
        
        private Collection values;
        
        public TestLookupLook( String name ) {
            super( name );
        }
        
        public void lookupChange( Object representedObject ) {
            fireChange( representedObject, Look.GET_LOOKUP_ITEMS );
        }
        
        public Collection getValues() {
            return values;
        }
             
        public Collection getLookupItems(Object representedObject, Lookup oldEnv) {
            
            getLookupItemsCallCount++;
            
            this.values = oldEnv.lookup( new Lookup.Template( Object.class )).allInstances() ;
            
            Collection items = new ArrayList();
            Collection c = (Collection)representedObject;
            
            for( Iterator it = c.iterator(); it.hasNext(); ) {                
                items.add( new GoldenValue.TestLookupItem( it.next() ) );
            }
            
            return items;
        }
        
    }
        
}