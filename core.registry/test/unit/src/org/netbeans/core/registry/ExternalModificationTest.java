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
import org.netbeans.api.registry.*;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.LocalFileSystem;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

import java.io.OutputStream;
import java.util.ArrayList;

/**
 *
 * @author  David Konecny
 */
public class ExternalModificationTest extends NbTestCase {
    
    public ExternalModificationTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ExternalModificationTest.class));
    }
    
    protected void setUp () throws Exception {
        clearWorkDir();
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    public void testExternalModification() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());
        FileObject exte = lfs.getRoot().createFolder("exte");

        BasicContext root = new ResettableContextImpl(lfs.getRoot());
        Context rootContext = SpiUtils.createContext(root);
        
        Context ctx = rootContext.getSubcontext("exte");

        FileSystemListener l = new FileSystemListener();
        rootContext.addContextListener(l);
        FileSystemListener l2 = new FileSystemListener();
        ctx.addContextListener(l2);
        
        assertTrue("There cannot be any binding yet", ctx.getBindingNames().size() == 0);
        
        FileObject fo2 = exte.createData("smth", "instance");
        // this should create one binding
        assertEquals("There must be one event fired", 1, l.b.size());
        assertEquals("There must be one event fired", 1, l.all.size());
        assertEquals("There must be one event fired", 1, l2.b.size());
        assertEquals("There must be one event fired", 1, l2.all.size());
        assertEquals("The event must be BindingEvent.BINDING_ADDED", BindingEvent.BINDING_ADDED, ((BindingEvent)l.b.get(0)).getType());
        assertEquals("There must be one binding", 1, ctx.getBindingNames().size());
        l.reset();
        l2.reset();

        FileLock lock = fo2.lock();
        OutputStream os = fo2.getOutputStream(lock);
        // write something
        os.write(74);
        os.close();
        lock.releaseLock();
        // this will generate another event
        assertEquals("There must be one event fired", 1, l.b.size());
        assertEquals("There must be one event fired", 1, l.all.size());
        assertEquals("There must be one event fired", 1, l2.b.size());
        assertEquals("There must be one event fired", 1, l2.all.size());
        assertEquals("The event must be BindingEvent.BINDING_MODIFIED", BindingEvent.BINDING_MODIFIED, ((BindingEvent)l.b.get(0)).getType());
        assertEquals("There must be one binding", 1, ctx.getBindingNames().size());
        l.reset();
        l2.reset();

        fo2.delete();
        // this should be one binding removed
        assertEquals("There must be one event fired", 1, l.b.size());
        assertEquals("There must be one event fired", 1, l.all.size());
        assertEquals("There must be one event fired", 1, l2.b.size());
        assertEquals("There must be one event fired", 1, l2.all.size());
        assertEquals("The event must be BindingEvent.BINDING_REMOVED", BindingEvent.BINDING_REMOVED, ((BindingEvent)l.b.get(0)).getType());
        assertEquals("There must be zero binding", 0, ctx.getBindingNames().size());
        l.reset();
        l2.reset();
        
        
        // create some trash ignored by registry
        fo2 = exte.createData("thrash", "odpad");
        // there should be no change:
        assertEquals("There must be zero event fired", 0, l.b.size());
        assertEquals("There must be zero event fired", 0, l.all.size());
        assertEquals("There must be zero event fired", 0, l2.b.size());
        assertEquals("There must be zero event fired", 0, l2.all.size());
        assertEquals("There must be zero binding only", 0, ctx.getBindingNames().size());
        l.reset();
        l2.reset();

        lock = fo2.lock();
        os = fo2.getOutputStream(lock);
        // write something
        os.write(74);
        os.close();
        lock.releaseLock();
        // there should be no change:
        assertEquals("There must be zero event fired", 0, l.b.size());
        assertEquals("There must be zero event fired", 0, l.all.size());
        assertEquals("There must be zero event fired", 0, l2.b.size());
        assertEquals("There must be zero event fired", 0, l2.all.size());
        assertEquals("There must be zero binding only", 0, ctx.getBindingNames().size());
        l.reset();
        l2.reset();
        
        fo2.delete();
        // there should be no change:
        assertEquals("There must be zero event fired", 0, l.b.size());
        assertEquals("There must be zero event fired", 0, l.all.size());
        assertEquals("There must be zero event fired", 0, l2.b.size());
        assertEquals("There must be zero event fired", 0, l2.all.size());
        assertEquals("There must be zero binding only", 0, ctx.getBindingNames().size());
        l.reset();
        l2.reset();
        
    }
    
    public static class FileSystemListener implements ContextListener {

        ArrayList s;
        ArrayList a;
        ArrayList b;
        ArrayList all;
        String error = null;
        
        public FileSystemListener() {
            reset();
        }
        
        public void reset() {
            s = new ArrayList();
            b = new ArrayList();
            a = new ArrayList();
            all = new ArrayList();
            error = null;
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            s.add(evt);
            all.add(evt);
        }
    
        public void bindingChanged(BindingEvent evt) {
            b.add(evt);
            all.add(evt);
        }
        
        public void attributeChanged(AttributeEvent evt) {
            a.add(evt);
            all.add(evt);
        }
        
    }
}
