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
