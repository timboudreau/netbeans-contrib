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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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

package org.netbeans.modules.enode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collection;
import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.filesystems.*;
import org.openide.modules.ModuleInfo;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Lookup;

import org.netbeans.api.enode.ExtensibleNode;
import org.netbeans.modules.enode.test.*;
import org.openide.nodes.Node;
import org.openide.nodes.NodeListener;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.ProxyLookup;

/**
 * Tests contained in this class should cover the content
 * of the lookup obtained as <code>ExtensibleNode.getLookup()</code>.<p>
 * This class uses classes from package test as testing
 * classes for the content of the lookup.
 * @author David Strupl
 */
public class ExtensibleLookupTest extends NbTestCase {
    /** root folder FileObject */
    private FileObject root;

    public ExtensibleLookupTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ExtensibleLookupTest.class));
    }
    
    /**
     * Sets up the testing environment by creating testing folders
     * on the system file system.
     */
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        String baseFolder = ExtensibleNode.E_NODE_LOOKUP.substring(1, ExtensibleNode.E_NODE_LOOKUP.length()-1);
        root = Repository.getDefault().getDefaultFileSystem().findResource(baseFolder);
        if (root == null) {
            String s = baseFolder.substring(0, baseFolder.lastIndexOf('/'));
            FileObject f1 = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(s);
            if (f1 == null) {
                f1 = Repository.getDefault().getDefaultFileSystem().getRoot().createFolder(s);
            }
            root = f1.createFolder(baseFolder.substring(baseFolder.lastIndexOf('/')+1));
        }
    }
    
    /**
     * Deletes the folders created in method setUp().
     */
    protected void tearDown() throws Exception {
//        root.getParent().delete();
    }
    
    /**
     * Test the ability to get an object from the declarative specification into
     * the content of the lookup <code>ExtensibleNode.getLookup()</code>.
     * The test performs following steps:
     * <OL> <LI> Create an ExtensibleNode with path "test"
     *      <LI> Check the content of its lookup (should be empty)
     *      <LI> Use Filesystems API to create an object on the system file system
     *      in folder "test".
     *      The configuration file tells the lookup to create an instance of MONodeEnhancer
     *      <LI> MONodeEnhancer should be found in the lookup
     *      <LI> Delete the configuration file
     *      <LI> The lookup should not return the object (it was deleted).
     * </OL>
     */
    public void testFindObjectInLookup() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test", false);
        assertNull("No objects at the start", en1.getLookup().lookup(MONodeEnhancer.class));
        FileObject test = root.getFileObject("test");
        if (test == null) {
            test = root.createFolder("test");
        }
        final FileObject[] a1 = new FileObject[1];
        final FileObject t = test;
        try {
            test.getFileSystem().runAtomicAction(new FileSystem.AtomicAction(){
                public void run() throws IOException {
                    a1[0] = t.createData("cookie1.instance");
                    a1[0].setAttribute("instanceCreate", org.netbeans.spi.enode.LookupContentFactoryManager.create(a1[0]));
                    a1[0].setAttribute("factoryClass", "org.netbeans.modules.enode.test.C1Factory");
                    a1[0].setAttribute("implements", "org.netbeans.modules.enode.test.MONodeEnhancer,org.openide.nodes.Node$Cookie");
                }
            });
            assertNotNull("Object not found", en1.getLookup().lookup(MONodeEnhancer.class));
            // Mantis 241: wrong caching was causing the second node with the same
            // path to fail.
            ExtensibleNode en2 = new ExtensibleNode("test", false);
            assertNotNull("Object not found", en2.getLookup().lookup(MONodeEnhancer.class));
        } finally {
            if (a1[0] != null) {
                a1[0].delete();
            }
            assertNull("Object found but should be gone.", en1.getLookup().lookup(MONodeEnhancer.class));
        }
    }

    /**
     * This test is almost the same as <code>testFindObjectInLookup()</code>. The only
     * difference is that the object is created in parent folder of the folder
     * specified as argument to constructor of the ExtensibleNode. In this setup
     * the hierarchical usage of the the folders should be tested.
     */
    public void testMergingContentOfFolders() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test/t2", true);
        assertNull("No objects at the start", en1.getLookup().lookup(MONodeEnhancer.class));
        FileObject test = root.getFileObject("test");
        if (test == null) {
            test = root.createFolder("test");
        }
        final FileObject t = test;
        final FileObject[] toDelete = new FileObject[1];
        test.getFileSystem().runAtomicAction(new FileSystem.AtomicAction(){
            public void run() throws IOException {
                FileObject a1 = t.createData("cookie2.instance");
                a1.setAttribute("implements", "org.netbeans.modules.enode.test.MONodeEnhancer,org.openide.nodes.Node$Cookie");
                a1.setAttribute("instanceCreate", org.netbeans.spi.enode.LookupContentFactoryManager.create(a1));
                a1.setAttribute("factoryClass", "org.netbeans.modules.enode.test.C1Factory");
                toDelete[0] = a1;
            }
        });
        assertNotNull("Object not found", en1.getLookup().lookup(MONodeEnhancer.class));
        
        toDelete[0].delete();
        assertNull("Object found but should be gone.", en1.getLookup().lookup(MONodeEnhancer.class));
    }

    /**
     * Test the ability to get an object from the declarative specification into
     * the content of the lookup <code>ExtensibleNode.getLookup()</code>.
     * The test performs following steps:
     * <OL> <LI> Create an ExtensibleNode with path "test"
     *      <LI> Check the content of its lookup (should be empty)
     *      <LI> Use Filesystems API to create an object on the system file system
     *      in folder "test".
     *      The configuration file tells the lookup to create an instance of MONodeEnhancer
     *      <LI> MONodeEnhancer should be found in the lookup
     *      <LI> Delete the configuration file
     *      <LI> The lookup should not return the object (it was deleted).
     * </OL>
     */
    public void testSpeedOfFindObjectInLookup() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test", false);
        assertNull("No objects at the start", en1.getLookup().lookup(MONodeEnhancer.class));
        FileObject test = root.getFileObject("test");
        if (test == null) {
            test = root.createFolder("test");
        }
        final FileObject t = test;
        final FileObject[] toDelete = new FileObject[1];
        test.getFileSystem().runAtomicAction(new FileSystem.AtomicAction(){
            public void run() throws IOException {
                FileObject a1 = t.createData("cookie3.instance");
                a1.setAttribute("instanceCreate", org.netbeans.spi.enode.LookupContentFactoryManager.create(a1));
                a1.setAttribute("factoryClass", "org.netbeans.modules.enode.test.C1Factory");
                a1.setAttribute("implements", "org.netbeans.modules.enode.test.MONodeEnhancer,org.openide.nodes.Node$Cookie");
                toDelete[0] = a1;
            }
        });
        
        ExtensibleNode n[] = new ExtensibleNode[1000];
        for (int i = 0; i < n.length; i++) {
            n[i] = new ExtensibleNode("test", false);
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < n.length; i++) {
            n[i].getLookup().lookup(MONodeEnhancer.class);
        }
        long end = System.currentTimeMillis();
        assertTrue("It took " + (end - start), (end - start) < 500);
        
        toDelete[0].delete();
        assertNull("Object found but should be gone.", en1.getLookup().lookup(MONodeEnhancer.class));
    }

    /**
     * 
     */
    public void testCookieActionsLongLoops() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test", false);
        assertNull("No objects at the start", en1.getLookup().lookup(MONodeEnhancer.class));
        FileObject test = root.getFileObject("test");
        if (test == null) {
            test = root.createFolder("test");
        }
        final FileObject t = test;
        final FileObject[] toDelete = new FileObject[1];
        test.getFileSystem().runAtomicAction(new FileSystem.AtomicAction(){
            public void run() throws IOException {
                FileObject a1 = t.createData("cookie3.instance");
                a1.setAttribute("instanceCreate", org.netbeans.spi.enode.LookupContentFactoryManager.create(a1));
                a1.setAttribute("factoryClass", "org.netbeans.modules.enode.test.C1Factory");
                a1.setAttribute("implements", "org.netbeans.modules.enode.test.MONodeEnhancer,org.openide.nodes.Node$Cookie");
                toDelete[0] = a1;
            }
        });
        
        final ExtensibleNode n[] = new ExtensibleNode[1000];
        final Lookup[] lkp = new Lookup[1000];
        final boolean res[] = new boolean[1];
        long start = System.currentTimeMillis();
        for (int i = 0; i < n.length; i++) {
            n[i] = new ExtensibleNode("test", false);
            lkp[i] = n[i].getLookup();
            final Lookup.Result<Node.Cookie> r = lkp[i].lookup(new Lookup.Template<Node.Cookie>(Node.Cookie.class));
            Collection<? extends Node.Cookie> c = r.allInstances();
            r.addLookupListener(new LookupListener() {
                public void resultChanged(LookupEvent ev) {
                    for (int j = 0; j < n.length; j++) {
                        final Lookup.Result<MONodeEnhancer> r2 = lkp[j].lookup(new Lookup.Template<MONodeEnhancer>(MONodeEnhancer.class));
                        Collection<? extends MONodeEnhancer> c = r2.allInstances();
                        if (c.isEmpty()) {
                            res[0] = true;
                        }
                    }
                }
            });
        }
        final Lookup.Result<MONodeEnhancer> r2 = lkp[0].lookup(new Lookup.Template<MONodeEnhancer>(MONodeEnhancer.class));
        Collection<? extends MONodeEnhancer> c = r2.allInstances();
        assertFalse("1 MONodeEnhancer not found!", c.isEmpty());
        assertFalse("2 MONodeEnhancer not found!", res[0]);
        long end = System.currentTimeMillis();
        assertTrue("It took " + (end - start), (end - start) < 500);
        
        toDelete[0].delete();
        assertNull("Object found but should be gone.", en1.getLookup().lookup(MONodeEnhancer.class));
    }
    
    public void testProperRegistration() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("test", false);
        assertNull("No objects at the start", en1.getLookup().lookup(MONodeEnhancer.class));
        FileObject test = root.getFileObject("test");
        if (test == null) {
            test = root.createFolder("test");
        }
        final FileObject t = test;
        final FileObject[] toDelete = new FileObject[1];
        test.getFileSystem().runAtomicAction(new FileSystem.AtomicAction(){
            public void run() throws IOException {
                FileObject a1 = t.createData("cookie3.instance");
                a1.setAttribute("instanceCreate", org.netbeans.spi.enode.LookupContentFactoryManager.create(a1));
                a1.setAttribute("factoryClass", "org.netbeans.modules.enode.test.C1Factory");
                a1.setAttribute("implements", "org.netbeans.modules.enode.test.MONodeEnhancer");
                toDelete[0] = a1;
            }
        });
        try {
            ExtensibleNode n = new ExtensibleNode("test", false);
            Lookup lkp = n.getLookup();
            final Lookup.Result<Node.Cookie> r = lkp.lookup(new Lookup.Template<Node.Cookie>(Node.Cookie.class));
            Collection<? extends Node.Cookie> c = r.allInstances();
            final Lookup.Result<MONodeEnhancer> r2 = lkp.lookup(new Lookup.Template<MONodeEnhancer>(MONodeEnhancer.class));
            Collection<? extends MONodeEnhancer> c2 = r2.allInstances();
            fail("The IllegalStateException should have already been thrown");
        } catch (IllegalStateException ise) {
            // ok this is expected
        } finally {
            toDelete[0].delete();
            assertNull("Object found but should be gone.", en1.getLookup().lookup(MONodeEnhancer.class));
        }
    }

}
