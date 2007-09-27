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

package org.netbeans.spi.looks;

import java.net.URL;
import java.util.*;

import junit.framework.*;
import org.netbeans.api.nodes2looks.Nodes;

import org.openide.filesystems.*;
import org.netbeans.junit.*;
import org.openide.util.Lookup;
import org.netbeans.api.registry.*;
import org.netbeans.modules.looks.SelectorListener;
import org.netbeans.spi.registry.*;
import org.openide.nodes.Node;
import org.openide.nodes.NodeListener;

/** Tests the behavior of NamespaceSelector when changing
 * the underlying layers.
 *
 * @author Petr Hrebejk
 */
public class NamespaceSelectorEventsTest extends NbTestCase {
    
    private FileSystem xfs1, xfs2;
    private TestMFS mfs;
    
    public NamespaceSelectorEventsTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new NbTestSuite(NamespaceSelectorEventsTest.class);
        return suite;
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        
        URL u1 = getClass ().getResource ("SelectorEventTest1.xml");
        URL u2 = getClass ().getResource ("SelectorEventTest2.xml");
               
        xfs1 = new XMLFileSystem( u1 );
        xfs2 = new XMLFileSystem( u2 );        
        mfs = new TestMFS( new FileSystem[] { xfs1, xfs2 } );
        
        FileObject mfsRoot = mfs.getRoot();        
        org.netbeans.modules.looks.RegistryBridge.setDefault( mfsRoot );
        
    }
    
    public void testRegistryEvents() throws Exception {
        
        Map env = new HashMap();
        
        Context ctx = SpiUtils.createContext( 
                org.netbeans.api.registry.fs.FileSystemContextFactory.createContext( mfs.getRoot() ) );
        
        Context sCtx = ctx.getSubcontext( "Selectors/Simple/" );        
        Context jlCtx = sCtx.getSubcontext( "java/lang" );
        
        
        LookSelector selector = Selectors.namespaceTypes( "Selectors/Simple/" );
        
        String ro = "REPRESENTED OBJECT";        
        Enumeration e = selector.getLooks( ro );        
        
        assertEquals( "Should only contain two bindings", 2, 
                      jlCtx.getSubcontextNames().size() + jlCtx.getBindingNames().size() );        
        
        TestContextListener tcl = new TestContextListener();
        
        sCtx.addContextListener( tcl );
        
        System.gc();
        
        mfs.setDels( new FileSystem[] { xfs1 } ); 
        
        assertEquals( "Should only contain one binding", 1, 
                      jlCtx.getSubcontextNames().size() + jlCtx.getBindingNames().size() );        
        assertEquals( "Should get one event ", 1, tcl.events.size() ); 
        
    }
    
    public void testNamespaceSelectorEvents() {
        
        LookSelector selector = Selectors.namespaceTypes( "Selectors/Simple/" );
        TestLookSelectorListener tlsl = new TestLookSelectorListener();
        assertNotNull( "Selector should exist.", selector );
        
        String ro = "REPRESENTED OBJECT";        
        Enumeration e = selector.getLooks( ro );
                       
        org.netbeans.modules.looks.Accessor.DEFAULT.addSelectorListener( selector, tlsl );
           
        System.gc();
        
        mfs.setDels( new FileSystem[] { xfs1 } ); 
        
        assertEquals( "Should get one event ", 1, tlsl.events.size() ); 
    }
    
    
    public void testNamespaceSelectorEventsOnNode() throws Exception {
        
        LookSelector selector = Selectors.namespaceTypes( "Selectors/Simple/" );        
        assertNotNull( "Selector should exist.", selector );
        
        String ro = "REPRESENTED OBJECT";        
        
        Node node = Nodes.node( ro, null, selector );
        TestNodeListener tnl = new TestNodeListener();
        node.addNodeListener( tnl );
        
        assertEquals( "Node name should be created by the StringLook.", StringLook.computeName( ro ), node.getName() ); 
        assertEquals( "No events should be fired", 0, tnl.events.size() );
                
        mfs.setDels( new FileSystem[] { xfs1 } ); 
               
        assertEquals( "Now it should change to ObjectLook.", ObjectLook.computeName( ro ), node.getName() ); 
        assertEquals( "6 events should be fired", 5, tnl.events.size() );
                
        mfs.setDels( new FileSystem[] { xfs1, xfs2 } ); 
        
        assertEquals( "Now it should change back to StringLook.", StringLook.computeName( ro ), node.getName() ); 
        assertEquals( "Anodther 6 events should be fired", 11, tnl.events.size() );
        
        mfs.setDels( new FileSystem[] { xfs2 } ); 
        
        assertEquals( "Now it should rmain StringLook.", StringLook.computeName( ro ), node.getName() ); 
        assertEquals( "No additional events should be fired", 11, tnl.events.size() );

    }
    
    public void testProxyLook() {
        
        Look look = (Look)org.netbeans.modules.looks.RegistryBridge.getDefault( null ).resolve( "Looks/Composite/TEST_COMPOSITE" );
        assertNotNull( "Look should exist.", look );
        
        String ro1 = "REPRESENTED OBJECT 1";
        String ro2 = "REPRESENTED OBJECT 2";
        
        Node n1 = Nodes.node( ro1, look ); 
        Node n2 = Nodes.node( ro2, look ); 
        
        TestNodeListener tnl1 = new TestNodeListener();
        n1.addNodeListener( tnl1 );
        TestNodeListener tnl2 = new TestNodeListener();
        n2.addNodeListener( tnl2 );
        
        // Just make sure the proxy does work
        List children = look.getChildObjects( ro1, Lookup.EMPTY );
        assertNotNull( "There should be some children", children );
        assertEquals( "There should be two children", 2, children.size() );
        
        assertTrue( "First child should be from ObjectLook", ObjectLook.CHILD == children.get( 0 ) );
        assertTrue( "Second child should be from StringLook", StringLook.CHILD == children.get( 1 ) );
        
        
        mfs.setDels( new FileSystem[] { xfs1 } ); 
        
        children = look.getChildObjects( ro1, Lookup.EMPTY );
        assertEquals( "There should be oly one child now", 1, children.size() );
        assertTrue( "The child should be from ObjectLook", ObjectLook.CHILD == children.get( 0 ) );
        
        assertEquals( "7 events should be fired from first node", 7, tnl1.events.size() );
        assertEquals( "7 events should be fired drom second node", 7, tnl2.events.size() );
        
    }
    
    
    private void printList( List list ) {
        System.err.println("------------------------" + list.size() );
        for( Iterator it = list.iterator(); it.hasNext(); ) {
            System.err.println( it.next() );
        }
        
    }
    
    // Helper methods ----------------------------------------------------------
    
    private static Look objectLook() {
        return ObjectLook.INSTANCE;
    }
    
    private static Look stringLook() {
        return StringLook.INSTANCE;
    }
    
    // Helper classes ----------------------------------------------------------
    
    /** Used from other SelectorEventsTests
     */
    public static class TestLookSelectorListener implements SelectorListener {
        
        List events = new ArrayList();
        
        public void contentsChanged(org.netbeans.modules.looks.SelectorEvent event) {
            events.add( event );            
        }
        
    }
    
    private static class TestContextListener implements ContextListener {
            
        private List events = new ArrayList();
        
        public void attributeChanged(AttributeEvent evt) {
            events.add( evt );
        }
        
        public void bindingChanged(BindingEvent evt) {
            events.add( evt );
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            events.add( evt );
        }
        
    }
    
         
    private static class TestNodeListener implements NodeListener {
        
        List events = new ArrayList();
        
        public void childrenAdded(org.openide.nodes.NodeMemberEvent ev) {
            events.add( ev );
        }        
        
        public void childrenRemoved(org.openide.nodes.NodeMemberEvent ev) {
            events.add( ev );
        }        
        
        public void childrenReordered(org.openide.nodes.NodeReorderEvent ev) {
            events.add( ev );
        }
        
        public void nodeDestroyed(org.openide.nodes.NodeEvent ev) {
            events.add( ev );
        }
        
        public void propertyChange(java.beans.PropertyChangeEvent ev ) {            
            events.add( ev );
        }
        
    }
    
    private static class TestMFS extends MultiFileSystem {
        
        public TestMFS() {
            super();
        }
        
        
        public TestMFS( FileSystem[] delegates ) {
            super( delegates );
        }
        
        public void setDels( FileSystem[] fss ) {
            setDelegates( fss );
        }
        
    }
    
    private static class ObjectLook extends Look {
        
        public static final String CHILD = "OBJECT_CHILD";
        
        public static final Look INSTANCE = new ObjectLook();
        
        public ObjectLook() {
            super( "TestObjectLook" );
        }
        
        public String getName( Object representedObject, Lookup env ) {
            return computeName( representedObject );
        }
        
        public List getChildObjects( Object representedObject, Lookup env ) {
            List ch = new ArrayList();
            ch.add( CHILD );
            return ch;
        }
        
        public static String computeName( Object representedObject ) {
            return representedObject.getClass().getName() + " : " + representedObject.hashCode(); 
        }
        
    }
    
    
    private static class StringLook extends Look {
        
        public static final Look INSTANCE = new StringLook();
        
        public static final String CHILD = "STRING_CHILD";
        
        public StringLook() {
            super( "TestStringLook" );
        }
        
        public String getName( Object representedObject, Lookup env ) {
            return computeName( representedObject ); 
        }
        
        public List getChildObjects( Object representedObject, Lookup env ) {
            List ch = new ArrayList();
            ch.add( CHILD );
            return ch;
        }
        
        public static String computeName( Object representedObject ) {
            return (String)representedObject;
        }
        
    }
    
    private static class TestProxyLook extends ProxyLook {
        
        TestProxyLook( String name, LookSelector selector ) {
            super( name, selector );
        }
    }
        
}
