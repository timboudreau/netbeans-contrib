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

package org.netbeans.spi.registry;

import junit.textui.TestRunner;
import org.netbeans.api.registry.*;
import org.netbeans.api.registry.fs.FileSystemContextFactory;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

import javax.swing.*;

public class SpiUtilsTest extends NbTestCase {

    private FileObject root = null;
    
    public SpiUtilsTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(SpiUtilsTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        root = Repository.getDefault ().getDefaultFileSystem ().getRoot ();
    }
    
    public void testRootContextCreation() throws Exception {
        BasicContext rootCtx = FileSystemContextFactory.createContext(root);
        Context ctx = SpiUtils.createContext(rootCtx);
        Context subctx = ctx.createSubcontext("abcd");
        FileObject fo = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("abcd");
        assertTrue ("Cannot create initial context", fo != null);
        ctx.destroySubcontext("abcd");
    }
    
    public void testArbitraryRootContextCreation() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem ();
        lfs.setRootDirectory (getWorkDir ());
        FileObject r = lfs.getRoot ();
        BasicContext rootCtx = FileSystemContextFactory.createContext(r);
        Context ctx = SpiUtils.createContext(rootCtx);
        Context subctx = ctx.createSubcontext("aaaaa");
        FileObject fo = r.getFileObject("aaaaa");
        assertTrue ("Cannot create initial context on arbitrary fileObject", fo != null);
    }
    
    public void testCreateException() throws Exception {
        BasicContext ctx = FileSystemContextFactory.createContext(root);
        BasicContext ctx2 = ctx;
        ctx = ctx.createSubcontext("something");
        ContextException e = SpiUtils.createContextException(ctx, "abcd");
        assertTrue ("ContextException does not have correct context "+e.getContext(), e.getContext().getContextName().equals("something"));
        ctx2.destroySubcontext("something");
    }
    
    public void testCreateSubcontextEvent() throws Exception {
        BasicContext ctx = FileSystemContextFactory.createContext(root);
        BasicContext ctx2 = ctx;
        ctx = ctx.createSubcontext("something2");
        SubcontextEvent e = SpiUtils.createSubcontextEvent(ctx, "abcd2", SubcontextEvent.SUBCONTEXT_ADDED);
        assertTrue ("SubcontextEvent context does not match "+e, e.getContext().getContextName().equals("something2"));
        assertTrue ("SubcontextEvent subcontext does not match "+e, e.getSubcontextName().equals("abcd2"));
        assertTrue ("SubcontextEvent type does not match "+e, e.getType() == SubcontextEvent.SUBCONTEXT_ADDED);
        ctx2.destroySubcontext("something2");
    }
    
    public void testCreateBindingEvent() throws Exception {
        BasicContext ctx = FileSystemContextFactory.createContext(root);
        BasicContext ctx2 = ctx;
        ctx = ctx.createSubcontext("something3");
        BindingEvent e = SpiUtils.createBindingEvent(ctx, "abcd3", BindingEvent.BINDING_REMOVED);
        assertTrue ("BindingEvent context does not match "+e, e.getContext().getContextName().equals("something3"));
        assertTrue ("BindingEvent binding does not match "+e, e.getBindingName().equals("abcd3"));
        assertTrue ("BindingEvent type does not match "+e, e.getType() == BindingEvent.BINDING_REMOVED);
        ctx2.destroySubcontext("something3");
    }
    
    public void testCreateAttributeEvent() throws Exception {
        BasicContext ctx = FileSystemContextFactory.createContext(root);
        BasicContext ctx2 = ctx;
        ctx = ctx.createSubcontext("something4");
        AttributeEvent e = SpiUtils.createAttributeEvent(ctx, "abcd4", "abcd5", AttributeEvent.ATTRIBUTE_MODIFIED);
        assertTrue ("AttributeEvent context does not match "+e, e.getContext().getContextName().equals("something4"));
        assertTrue ("AttributeEvent binding does not match "+e, e.getBindingName().equals("abcd4"));
        assertTrue ("AttributeEvent attrname does not match "+e, e.getAttributeName().equals("abcd5"));
        assertTrue ("AttributeEvent type does not match "+e, e.getType() == AttributeEvent.ATTRIBUTE_MODIFIED);
        ctx2.destroySubcontext("something4");
    }
    
    public void testCreateObjectRef() throws Exception {
        Object o = new JLabel("my label X");
        BasicContext rootCtx = FileSystemContextFactory.createContext(root);
        BasicContext ctx2 = rootCtx;
        BasicContext ctx = rootCtx.createSubcontext("something5");
        ctx.bindObject("obj25", o);
        ObjectRef or = SpiUtils.createObjectRef(ctx,"obj25");
        assertTrue ("ObjectRef context does not match "+or, or.getContext().getContextName().equals("something5"));
        assertTrue ("ObjectRef binding does not match "+or, or.getBindingName().equals("obj25"));
        assertTrue ("ObjectRef absolutename does not match "+or, or.getContextAbsoluteName().equals("/something5"));
        assertTrue ("ObjectRef isValid does not match "+or, or.isValid() == true);
        assertTrue ("ObjectRef getObject does not match "+or, or.getObject().equals(o));
        ctx2.destroySubcontext("something5");
        
        ctx2 = rootCtx;
        ctx = rootCtx.createSubcontext("something6");
        ctx.bindObject("obj26", o);
        or = SpiUtils.createObjectRef(rootCtx, "/something6", "obj26");
        assertTrue ("ObjectRef context does not match "+or, or.getContext().getContextName().equals("something6"));
        assertTrue ("ObjectRef binding does not match "+or, or.getBindingName().equals("obj26"));
        assertTrue ("ObjectRef absolutename does not match "+or.getContextAbsoluteName(), or.getContextAbsoluteName().equals("/something6"));
        assertTrue ("ObjectRef isValid does not match "+or, or.isValid() == true);
        assertTrue ("ObjectRef getObject does not match "+or, or.getObject().equals(o));
        ctx2.destroySubcontext("something6");
    }
    
}
