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

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Enumeration;

import org.netbeans.junit.*;
import junit.framework.*;

import org.netbeans.spi.looks.ProxyLook;
import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.Looks;
import org.netbeans.spi.looks.LookSelector;
import org.netbeans.api.nodes2looks.Nodes;

import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.openide.util.Lookup;

public class LooksTest extends NbTestCase {

    private FileSystem fs;
    private Look delegate;
    private long mask = Look.ALL_METHODS;
    private javax.swing.JTextField bean;

    public LooksTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new NbTestSuite(LooksTest.class);

        return suite;
    }

    protected void setUp() throws Exception {
        super.setUp();
        URL u = getClass ().getResource ("LooksTest.xml");

        fs = new XMLFileSystem (u);
        
        org.netbeans.modules.looks.RegistryBridge.setDefault( fs.getRoot() );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testNODES () throws Exception {
        // XXX these should be using Registry API instead!
        checkInstance (Nodes.nodeLook(), "Looks/NODES");
    }

    public void testBEANS () throws Exception {
        checkInstance (Looks.bean(), "Looks/BEANS");
    }

    public void testFILTER_DEFAULT () throws Exception {
        doFILTER_DEFAULT ("FILTER_DEFAULT");
    }

    public void testFILTER_DEFAULT2 () throws Exception {
        doFILTER_DEFAULT ("FILTER_DEFAULT2");
    }

    private void doFILTER_DEFAULT (String name) throws Exception {

        //System.out.println("testFILTER_DEFAULT");
        // set delegate look and representive object
        initFilterLookTest ();

        // reference filter look
        Look filterRef = Looks.filter ( "TestFilter", delegate, mask);
        filterRef.attachTo (bean);

        // get look declared in layer
        Look filter = (Look)checkIfExists ("Looks/" + name);
        filter.attachTo (bean);

        assertTrue (bean.getName ().equals (filterRef.getName (bean, Lookup.EMPTY)));
        assertEquals (filterRef.getName (bean, Lookup.EMPTY), filter.getName (bean, Lookup.EMPTY));
    }

    public void testFILTER_ALL_METHODS () throws Exception {
        doFILTER_ALL_METHODS ("FILTER_ALL_METHODS");
    }

    public void testFILTER_ALL_METHODS2 () throws Exception {
        doFILTER_ALL_METHODS ("FILTER_ALL_METHODS2");
    }

    private void doFILTER_ALL_METHODS (String name) throws Exception {

        //System.out.println("testFILTER_ALL_METHODS");
        // set delegate look and representive object
        initFilterLookTest ();

        // reference filter look
        mask = Look.ALL_METHODS;
        Look filterRef = Looks.filter ( "TestFilter", delegate, mask);
        filterRef.attachTo (bean);

        // get look declared in layer
        Look filter = (Look)checkIfExists ("Looks/" + name);
        filter.attachTo (bean);

        assertTrue (bean.getName ().equals (filterRef.getName (bean, Lookup.EMPTY)));
        assertEquals (filterRef.getName (bean, Lookup.EMPTY), filter.getName (bean, Lookup.EMPTY));

    }

    public void testFILTER_ALL_METHODS_MINUS_GET_NAME () throws Exception {
        doFILTER_ALL_METHODS_MINUS_GET_NAME ("FILTER_ALL_METHODS_MINUS_GET_NAME");
    }

    public void testFILTER_ALL_METHODS_MINUS_GET_NAME2 () throws Exception {
        doFILTER_ALL_METHODS_MINUS_GET_NAME ("FILTER_ALL_METHODS_MINUS_GET_NAME2");
    }

    private void doFILTER_ALL_METHODS_MINUS_GET_NAME (String  name) throws Exception {
        //System.out.println("testFILTER_ALL_METHODS_MINUS_GET_NAME");
        // set delegate look and representive object
        initFilterLookTest ();

        // reference filter look
        mask = Look.ALL_METHODS & ~Look.GET_NAME;
        Look filterRef = Looks.filter ("TestFilter", delegate, mask);
        filterRef.attachTo (bean);

        // get look declared in layer
        Look filter = (Look)checkIfExists ("Looks/" + name);
        filter.attachTo (bean);

        assertTrue (!bean.getName ().equals (filterRef.getName (bean, Lookup.EMPTY)));
        assertEquals (filterRef.getName (bean, Lookup.EMPTY), filter.getName (bean, Lookup.EMPTY));

    }

    public void testFILTER_NO_METHODS () throws Exception {
        doFILTER_NO_METHODS ("FILTER_NO_METHODS");
    }

    public void testFILTER_NO_METHODS2 () throws Exception {
        doFILTER_NO_METHODS ("FILTER_NO_METHODS");
    }

    private void doFILTER_NO_METHODS (String name) throws Exception {
        //System.out.println("testFILTER_NO_METHODS");
        // set delegate look and representive object
        initFilterLookTest ();

        // reference filter look
        mask = Look.NO_METHODS;
        Look filterRef = Looks.filter ("TestFilter", delegate, mask);
        filterRef.attachTo (bean);

        // get look declared in layer
        Look filter = (Look)checkIfExists ("Looks/" + name);
        filter.attachTo (bean);

        assertTrue (!bean.getName ().equals (filterRef.getName (bean, Lookup.EMPTY)));
        assertEquals (filterRef.getName (bean, Lookup.EMPTY), filter.getName (bean, Lookup.EMPTY));
    }

    public void testFILTER_NO_METHODS_PLUS_GET_NAME () throws Exception {
        doFILTER_NO_METHODS_PLUS_GET_NAME ("FILTER_NO_METHODS_PLUS_GET_NAME");
    }

    public void testFILTER_NO_METHODS_PLUS_GET_NAME2 () throws Exception {
        doFILTER_NO_METHODS_PLUS_GET_NAME ("FILTER_NO_METHODS_PLUS_GET_NAME2");
    }

    public void doFILTER_NO_METHODS_PLUS_GET_NAME (String name) throws Exception {
        //System.out.println("testFILTER_NO_METHODS_PLUS_GET_NAME");
        // set delegate look and representive object
        initFilterLookTest ();

        // reference filter look
        mask = Look.NO_METHODS | Look.GET_NAME;
        Look filterRef = Looks.filter ("TestFilter", delegate, mask);
        filterRef.attachTo (bean);

        // get look declared in layer
        Look filter = (Look)checkIfExists ("Looks/" + name );
        filter.attachTo (bean);

        assertTrue (bean.getName ().equals (filterRef.getName (bean, Lookup.EMPTY)));
        assertEquals (filterRef.getName (bean, Lookup.EMPTY), filter.getName (bean, Lookup.EMPTY));
    }

    public void testNamespaceSelector () throws Exception {
        // XXX pending: how set initial context for different filesystem
        LookSelector selectorRef = org.netbeans.spi.looks.Selectors.namespaceTypes ("Looks/Types/Beans/");        
        LookSelector selector = (LookSelector)checkIfExists ("Looks/Selectors/NAMESPACE_SELECTOR");

        String s = new String ();
        Enumeration enRef = selectorRef.getLooks (s);
        Enumeration en = selector.getLooks (s);
        
        int i = 0;
        while (enRef.hasMoreElements () && en.hasMoreElements ()) {
            assertSame ("Found looks are same.", enRef.nextElement (), en.nextElement ());
            i ++;
        }
        
        assertTrue( "Two looks should be found. Found " + i + ".", i == 2  );        
        assertTrue ("No more items from reference selector.", !enRef.hasMoreElements ());
        assertTrue ("No more items from declared selector.", !en.hasMoreElements ());
    }
    
    
    public void testNamespaceSelectorCaching() {
        LookSelector selector = Selectors.namespaceTypes( "Looks/Types/Simple/" );
        
        assertNotNull( "Selector should exist.", selector );
        
        
        // When nobody points to the look it should disapear
        String ro = "REPRESENTED OBJECT";
        for( int i = 0; i < 100; i++ ) {
            
            if ( i % 10 == 0 ) {
                Object k = new Object();
                WeakReference wr = new WeakReference( k );    
                k = null;
                System.gc();
                assertGC( "Reference should disapear",  wr );
            }
            
            Enumeration e = selector.getLooks( ro );        
            assertTrue( "Should find instance counting look.", e.nextElement() instanceof InstanceCountingLook );
            assertTrue( "It should be the only look.", !e.hasMoreElements() );
            assertEquals( "There should be one instance of the look.", 1, InstanceCountingLook.getInstanceCount() );
        }
        
    }

    private static Look instanceCountingLook() {
        return new InstanceCountingLook();
    }
    
    public void testDecoratorSelector () throws Exception {
        doDecoratorSelector (
            "DECORATOR_SELECTOR",
            (Look)checkIfExists ("Looks/Bean/SimpleBeanLook"),
            namespaceSelectorForLooksTypesBeans ()
        );
    }

    public void testDecoratorSelector2 () throws Exception {
        doDecoratorSelector (
            "DECORATOR_SELECTOR2",
            Looks.bean(),
            (LookSelector)checkIfExists ("Looks/Selectors/NAMESPACE_SELECTOR")
        );
    }

    public static LookSelector namespaceSelectorForLooksTypesBeans () {
        return Selectors.namespaceTypes("Looks/Types/Properties");
    }

    private void doDecoratorSelector (String name, Look decoratingLook, LookSelector selector) throws Exception {

        //LookSelector decoratorRef = Looks.decorator (selector, decoratingLook, true, true);
        LookSelector decoratorRef = Selectors.decorator ( selector, decoratingLook );

        LookSelector decorator = (LookSelector)checkIfExists ("Looks/Selectors/" + name);

        String s = new String ();
        Enumeration enRef = decoratorRef.getLooks (s);
        Enumeration en = decorator.getLooks (s);
        Enumeration enSelector = selector.getLooks (s);
        while (enRef.hasMoreElements () && en.hasMoreElements () && enSelector.hasMoreElements ()) {
            // pending: check decorated looks
            en.nextElement ();
            enRef.nextElement ();
            enSelector.nextElement ();
        }
        assertTrue ("No more items from docorated selector.", !enSelector.hasMoreElements ());
        assertTrue ("No more items from reference decorator.", !enRef.hasMoreElements ());
        assertTrue ("No more items from decorator.", !en.hasMoreElements ());


    }

    // helper methods ----------------------------------------------------------
    
    private Object checkInstance (Object inst, String name) throws Exception {
        Object instRef = checkIfExists( name );
        assertEquals ("Instance is the same", inst, instRef);
        return instRef;
    }


    private Object checkIfExists (String name) throws Exception {
        
        org.netbeans.modules.looks.RegistryBridge registryBridge = org.netbeans.modules.looks.RegistryBridge.getDefault( null );
        Object o = registryBridge.resolve( name );
        assertNotNull( "Name " + name + " not found.", o ); 
        return o;        
        
    }

    private void initFilterLookTest () {
        if (bean != null)
            return ;
        try {
            delegate = (Look)checkInstance (Looks.bean(), "Looks/Bean/SimpleBeanLook"); // NOI18N
            assertNotNull (delegate);
            bean = new javax.swing.JTextField ();
            bean.setName ("dummy"); // NOI18N
        } catch (Exception e) {
            fail (e.getMessage ());
        }
    }
    
    // test classes ------------------------------------------------------------
    
    private static class InstanceCountingLook extends Look {
        
        private static int count = 0;
        
        public InstanceCountingLook() {            
            super( "ICL " + count++ ); 
        }
        
        
        public static int getInstanceCount() {
            return count;
        }
        
    }

}



