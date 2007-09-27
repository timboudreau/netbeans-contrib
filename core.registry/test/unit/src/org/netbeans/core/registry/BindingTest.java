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

package org.netbeans.core.registry;

import junit.textui.TestRunner;
import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.ObjectRef;
import org.netbeans.api.registry.fs.FileSystemContextFactory;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.XMLFileSystem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Collection;

/**
 *
 * @author  David Konecny
 */
public class BindingTest extends NbTestCase {
    
    private Context rootContext = null;
    private FileObject root = null;
    
    private static final String MY_NULL = new String("MY_NULL");

    public BindingTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(BindingTest.class));
    }
    
    protected void setUp () throws Exception {
    }
    
    public void testBasicBindingOperations() throws Exception {
        URL u1 = getClass().getResource("data/layer_defaults.xml");
               
        FileSystem xfs1 = new XMLFileSystem( u1 );
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());
        FileSystem mfs = new TestMFS( new FileSystem[] { lfs, xfs1 } );
    
        BasicContext rootCtx = FileSystemContextFactory.createContext(mfs.getRoot());
        Context root = SpiUtils.createContext(rootCtx);
        Context ctx = root.getSubcontext("contextX");

        Collection coll = ctx.getBindingNames();
        assertEquals("Number of bindings is not equal", 4, coll.size());
        assertTrue("Binding 'objectBindingOne' is missing", coll.contains("objectBindingOne"));
        assertTrue("Binding 'objectBindingTwo' is missing", coll.contains("objectBindingTwo"));
        assertTrue("Binding 'primitiveBindingOne' is missing", coll.contains("primitiveBindingOne"));
        assertTrue("Binding 'primitiveBindingTwo' is missing", coll.contains("primitiveBindingTwo"));
        
        coll = ctx.getAttributeNames(null);
        assertEquals("Number of context attributes is not equal", 2, coll.size());
        assertTrue("Attribute 'contextXAttribute' is missing", coll.contains("contextXAttribute"));
        assertTrue("Attribute 'default.context.sorting' is missing", coll.contains("default.context.sorting"));
        
        coll = ctx.getAttributeNames("objectBindingOne");
        assertEquals("Number of binding 'objectBindingOne' attributes is not equal", 1, coll.size());
        assertTrue("Attribute 'objectBindingOneAttribute' is missing", coll.contains("objectBindingOneAttribute"));
        
        coll = ctx.getAttributeNames("objectBindingTwo");
        assertEquals("Number of binding 'objectBindingTwo' attributes is not equal", 1, coll.size());
        assertTrue("Attribute 'objectBindingTwoAttribute' is missing", coll.contains("objectBindingTwoAttribute"));
        
        coll = ctx.getAttributeNames("primitiveBindingOne");
        assertEquals("Number of binding 'primitiveBindingOne' attributes is not equal", 1, coll.size());
        assertTrue("Attribute 'primitiveBindingOneAttribute' is missing", coll.contains("primitiveBindingOneAttribute"));
        
        coll = ctx.getAttributeNames("primitiveBindingTwo");
        assertEquals("Number of binding 'primitiveBindingTwo' attributes is not equal", 1, coll.size());
        assertTrue("Attribute 'primitiveBindingTwoAttribute' is missing", coll.contains("primitiveBindingTwoAttribute"));
                
        
        ctx = root.getSubcontext("contextY");
        String ss = ctx.getString("bndString", "nono");
        assertEquals("string val", ss);
        
        int ii = ctx.getInt("bndInteger", 25);
        assertEquals(1984, ii);
        
        long ll = ctx.getLong("bndLong", 25L);
        assertTrue("Value does not match: "+ll, 19841984L == ll);
        
        float ff = ctx.getFloat("bndFloat", 25F);
        assertTrue("Value does not match: "+ff, 1112.1112F == ff);
        
        boolean bb = ctx.getBoolean("bndBoolean", false);
        assertEquals(true, bb);
        
        URL uu = ctx.getURL("bndURL", null);
        assertEquals(new URL("http://www.netbeans.org/download/"), uu);
        
        Color cc = ctx.getColor("bndColor", null);
        assertEquals(new Color(25000), cc);
        
        Font ft = ctx.getFont("bndFont", null);
        assertEquals(new Font("Arial", Font.BOLD | Font.ITALIC, 15), ft);

        ObjectRef or = SpiUtils.createObjectRef(rootCtx, "/contextX", "primitiveBindingOne");
        ctx.putObject("ref", or);
        or = null;
        System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
        or = ctx.getRef("ref");
        assertEquals("Objects must be equals", SpiUtils.createObjectRef(rootCtx, "/contextX", "primitiveBindingOne"), or);
        
        ctx = root.getSubcontext("contextREF");
        or = ctx.getRef("ref");
        assertEquals("Objects must be equals", SpiUtils.createObjectRef(rootCtx, "/contextZ", "objectBindingOne"), or);
        Object o = ctx.getObject("ref", null);
        o = ctx.getObject("ref", null);
        assertNotNull(o);
        assertEquals("Objects must be equals", new String(), o);

        or = ctx.getRef("ref2");
        assertEquals("Objects must be equals", SpiUtils.createObjectRef(rootCtx, "/contextZ", "primitiveBindingOne"), or);
        o = ctx.getObject("ref2", null);
        assertNotNull(o);
        assertEquals("Objects must be equals", "primitiveBindingOneValue", o);
    }
    
    public void XXX_failing_testBindingAttrs() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());
        FileSystem mfs = new TestMFS( new FileSystem[] { lfs } );
    
        BasicContext rootCtx = FileSystemContextFactory.createContext(mfs.getRoot());
        Context root = SpiUtils.createContext(rootCtx);
        Context ctx = root.createSubcontext("cotoxo");
        
        ctx.putObject("test", new JLabel("labelo"));
        ctx.setAttribute("test", "attr1", "some value");
        ctx.setAttribute("test", "attr2", "some value");
        assertEquals("There must be two attributes", 2, ctx.getAttributeNames("test").size());
        ctx.putObject("test", "labelo");
        assertEquals("There must be two attributes", 2, ctx.getAttributeNames("test").size());
        ctx.setAttribute("test", "attr3", "some value");
        ctx.setAttribute("test", "attr4", "some value");
        assertEquals("There must be two attributes", 4, ctx.getAttributeNames("test").size());
        ctx.putObject("test", new JLabel("labelo"));
        assertEquals("There must be two attributes", 4, ctx.getAttributeNames("test").size());
    }
    
    public void testBindingGCed() throws Exception {
        URL u1 = getClass().getResource("data/layer_defaults.xml");
        File tmpDir = getWorkDir();
        
        FileSystem xfs1 = new XMLFileSystem( u1 );
        LocalFileSystem lfs1 = new LocalFileSystem();
        lfs1.setRootDirectory(tmpDir);
        
        FileSystem mfs = new TestMFS( new FileSystem[] { lfs1, xfs1 } );
    
        BasicContext rootCtx = FileSystemContextFactory.createContext(mfs.getRoot());
        Context root = SpiUtils.createContext(rootCtx);
        Context ctx = root.getSubcontext("contextX");
        
        Integer objInt = new Integer(123);
        ReferenceQueue rqPobj = new ReferenceQueue ();
        WeakReference wrPobj = new WeakReference(objInt, rqPobj);

        ctx.putObject("myBinding", objInt);
        
        // let PM be GCed
        objInt = null;
        for (int i = 0; i < 10; i++) {
            if (i == 4) {
                Thread.sleep (1000);
            }
            System.gc ();
        }
        
        Reference rPobj = rqPobj.poll ();
        assertNotNull("Bound object has not been GCed", rPobj);
    }

}
