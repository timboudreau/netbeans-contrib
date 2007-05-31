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

package org.netbeans.core.registry;

import junit.textui.TestRunner;
import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.fs.FileSystemContextFactory;
import org.netbeans.core.registry.cdconvertor.CD;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.XMLFileSystem;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Collection;

public class GCTest extends NbTestCase {
    
    private Context rootContext;
    private FileSystem mfs;
    
    public GCTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(GCTest.class));
    }
    
    protected void setUp () throws Exception {
    }
    
    private void init() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());
        
        URL u1 = getClass().getResource("data/layer_gctest.xml");
               
        FileSystem xfs1 = new XMLFileSystem( u1 );
        mfs = new TestMFS( new FileSystem[] { lfs, xfs1 } );
    
        BasicContext rootCtx = FileSystemContextFactory.createContext(mfs.getRoot());
        rootContext = SpiUtils.createContext(rootCtx);
    }
    
    public void testGCHold() throws Exception {
        
        init();
        Context ctx = rootContext.getSubcontext("gctest");
        CD cd = (CD)ctx.getObject("cd", null);
        assertNotNull(cd);
        assertEquals(new CD("John Cage", "Early Piano Works"), cd);
        

        // referenced object cannot be garbage collected
        WeakReference ref = new WeakReference(cd);
        System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
        assertTrue("Object was garbage collected", ref.get() != null);

        // retrieving second time existing and referenced object 
        // must return the same instancce
        CD cd2 = (CD)ctx.getObject("cd", null);
        assertTrue("Objects are not the same", cd == cd2 );
        
        // referenced context cannot be garbage collected
        Context ctx2 = rootContext.getSubcontext("gctest2");
        assertNotNull(ctx2);
        ref = new WeakReference(ctx2);
        System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
        assertTrue("Object was garbage collected", ref.get() != null);
        
        // retrieving second time existing and referenced context 
        // must return the same instancce
        Context ctx3 = rootContext.getSubcontext("gctest2");
        assertEquals("Objects are not equal", ctx2, ctx3 );
    }
    
    public void testGCRelease() throws Exception {
        
        init();
        Context ctx = rootContext.getSubcontext("gctest");
        CD cd = (CD)ctx.getObject("cd", null);
        assertNotNull(cd);
        assertEquals(new CD("John Cage", "Early Piano Works"), cd);
        

        // NON-referenced object must be garbage collected
        WeakReference ref = new WeakReference(cd);
        cd = null;
        System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
        assertTrue("Object was not garbage collected", ref.get() == null);

        // test retrieval
        CD cd2 = (CD)ctx.getObject("cd", null);
        assertNotNull(cd2);
        assertEquals(new CD("John Cage", "Early Piano Works"), cd2);
        
        // NON-referenced context must be garbage collected
        Context ctx2 = rootContext.getSubcontext("gctest2");
        assertNotNull(ctx2);
        ref = new WeakReference(ctx2);
        ctx2 = null;
        System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
        assertTrue("Object was not garbage collected", ref.get() == null);
        
        // test retrieval
        Context ctx3 = rootContext.getSubcontext("gctest2");
        assertNotNull(ctx3);
    }

    public void testGCNewBinding() throws Exception {
        
        init();
        Context ctx = rootContext.getSubcontext("gctest");
        CD cd = new CD("aaaa", "bbbbb");
        ctx.putObject("aaa", cd);
        WeakReference ref = new WeakReference(cd);
        cd = null;
        assertGC("Object was not GCed", ref);
    }

    public void testGCExternallyRemovedBinding() throws Exception {
        
        init();
        Context ctx = rootContext.getSubcontext("gctest");
        CD cd = new CD("aaaa", "bbbbb");
        ctx.putObject("aaa", cd);
        
        FileObject fo = mfs.findResource("gctest/aaa.xml");
        fo.delete();
        
        CD cd2 = (CD)ctx.getObject("aaa", null);
        assertTrue("Object was not deleted or removed from cache", cd2 == null);
    }

    public void testGCofRootCtx() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());
        
        URL u1 = getClass().getResource("data/layer_gctest.xml");
               
        FileSystem xfs1 = new XMLFileSystem( u1 );
        FileSystem mfs = new TestMFS( new FileSystem[] { lfs, xfs1 } );
    
        BasicContext rootCtx = FileSystemContextFactory.createContext(mfs.getRoot());
        Context ctx = SpiUtils.createContext(rootCtx);
        Collection coll = ctx.getBindingNames();
        Collection coll2 = ctx.getAttributeNames(null);
        Collection coll3 = ctx.getSubcontextNames();
        
        WeakReference ref1 = new WeakReference(rootCtx);
        WeakReference ref2 = new WeakReference(ctx);
        
        rootCtx = null;
        ctx = null;
        
        System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
        assertTrue("Object was not garbage collected", ref1.get() == null);
        assertTrue("Object was not garbage collected", ref2.get() == null);
        
    }
    
}
