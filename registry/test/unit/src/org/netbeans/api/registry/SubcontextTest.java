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

package org.netbeans.api.registry;

import junit.textui.TestRunner;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

/**
 *
 * @author  Vitezslav Stejskal
 * @author  David Konecny
 */
public class SubcontextTest extends NbTestCase {
    public SubcontextTest (String name) {
        super (name);
    }

    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(SubcontextTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    public void testSubcontext() throws Exception {
        implSubcontext(getRootContext(), getRoot());
    }

    public void testSubcontext2() throws Exception {
        Context ctx = getRootContext().createSubcontext("aa/bb/cc");
        FileObject fo = findResource ("aa/bb/cc");
        implSubcontext(ctx, fo);
        getRootContext().destroySubcontext("aa");
    }
    
    public void implSubcontext(Context context, FileObject rootFO) throws Exception {
        // create subcontext
        Context subctx = context.createSubcontext ("subctx");
        FileObject f = rootFO.getFileObject ("subctx");
        assertTrue ("Subcontext doesn't exist", f != null);
        assertTrue ("Subcontext isn't folder", f.isFolder ());
        
        // create another subcontext
        subctx.createSubcontext ("foo");
        f = rootFO.getFileObject("subctx").getFileObject("foo");
        assertTrue ("Subcontext doesn't exist", f != null);
        assertTrue ("Subcontext isn't folder", f.isFolder ());

        // creating subcontext which already exist must pass
        {
            ContextException exc = null;
            try {
                subctx.createSubcontext ("foo");
            } catch (ContextException e) {
                exc = e;
            }
            assertTrue ("ContextException expected when creating existing context",
                exc == null);
        }
        
        // creating of multiple subcontexts in one step must pass
        {
            ContextException exc = null;
            try {
                subctx.createSubcontext ("ctx1/ctx2/ctx3/ctx4");
            } catch (ContextException e) {
                exc = e;
            }
            assertTrue ("ContextException expected for invalid subcontext name", exc == null);
        }
        
        // creating of multiple subcontexts in one step must pass
        {
            ContextException exc = null;
            try {
                subctx.createSubcontext ("ctx1/ctx2/ctx3/ctx4/ctx5/ctx6/ctx7");
            } catch (ContextException e) {
                exc = e;
            }
            assertTrue ("ContextException expected for invalid subcontext name", exc == null);
        }
        
        // destroying non-existing context must throw exception
        {
            ContextException exc = null;
            try {
                subctx.destroySubcontext ("abcdctx");
            } catch (ContextException e) {
                exc = e;
            }
            assertTrue ("ContextException expected when destroying non-existing ctx",
                exc instanceof ContextException);
        }
        
        // destroy multiple context must work
        {
            context.destroySubcontext ("subctx");
            f = rootFO.getFileObject("subctx");
            assertTrue ("Folder isn't deleted for destroyed context", f == null);
        }
        
    }
    
    public void testSubcontext5() throws Exception {
        Context context = getRootContext();
        
        Context ctx = context.getSubcontext("a1/b1/c1/d1/e1");
        assertTrue ("This context cannot exist", ctx == null);
        
        ctx = context.createSubcontext("a1/b1/c1/d1/e1");
        assertTrue ("Context must be created", ctx != null);
        
        FileObject fo = findResource("a1/b1/c1/d1/e1");
        assertTrue ("Folder must exist", fo != null);

        context.destroySubcontext("a1/b1/c1/d1");
        fo = findResource("a1/b1/c1/d1");
        assertTrue ("Folder cannot exist", fo == null);
        
        fo = findResource("a1/b1/c1");
        assertTrue ("Folder must exist", fo != null);
        
        context.destroySubcontext("a1");
        fo = findResource("a1");
        assertTrue ("Folder cannot exist", fo == null);
        // test it on diff root context
    }

    public void testParentContext () throws Exception {
        Context subctx = getRootContext().createSubcontext ("subctxXXX");
        Context parent = subctx.getParentContext();
        assertEquals ("Parent Context not found", parent.getAbsoluteContextName(), getRootContext().getAbsoluteContextName());
        getRootContext().destroySubcontext ("subctxXXX");
    }

    protected Context getRootContext () {
        return Context.getDefault();    
    }

    protected FileObject getRoot() {
        return Repository.getDefault ().getDefaultFileSystem ().getRoot ();
    }

    protected FileObject findResource(String resource) {
        return Repository.getDefault ().findResource (resource);
    }

}
