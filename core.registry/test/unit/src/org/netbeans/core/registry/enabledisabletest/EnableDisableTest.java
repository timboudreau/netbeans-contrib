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

package org.netbeans.core.registry.enabledisabletest;

import junit.textui.TestRunner;
import org.netbeans.api.registry.*;
import org.netbeans.core.registry.ResettableContextImpl;
import org.netbeans.core.registry.TestMFS;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.openide.util.Lookup;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  David Konecny
 */
public class EnableDisableTest extends NbTestCase {
    
    public EnableDisableTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(EnableDisableTest.class));
    }
    
    protected void setUp () throws Exception {
        clearWorkDir();
    }
    
    public void testEnableDisable() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());

        URL u1 = getClass().getResource("data/module_layer.xml");
               
        FileSystem xfs1 = new XMLFileSystem( u1 );
        FileSystem mfs = new TestMFS( new FileSystem[] { lfs, xfs1 } );
    
        BasicContext root = new ResettableContextImpl(mfs.getRoot(), null, xfs1, lfs);
        Context rootContext = SpiUtils.createContext(root);

        Context ctx = rootContext.getSubcontext("module");
        assertEquals("There cannot be more than 2 items because no modules are installed.", 2, ctx.getBindingNames().size());

        FileSystemListener l = new FileSystemListener();
        FileSystemListener l2 = new FileSystemListener();
        ctx.addContextListener(l);
        rootContext.addContextListener(l2);
        
        // this should fire context change that two bindings were added
        ModuleUtils.DEFAULT.install();        
        ModuleUtils.DEFAULT.enableBookModule(true);
        assertEquals("Two events must be fired", 2, l.b.size());
        assertEquals("Two events must be fired", 2, l.all.size());
        assertEquals("Two events must be fired", 2, l2.b.size());
        assertEquals("Two events must be fired", 2, l2.all.size());
        assertEquals("There must be 4 items", 4, ctx.getBindingNames().size());
        
        assertEquals("Objects must be equal", createBook("N/A", "N/A"), ctx.getObject("book1", null));
        assertEquals("Objects must be equal", createBook("Jesse, Tim & others", "NetBeans Definitive Guide"), ctx.getObject("book2", null));
        
        Object o = createBook("David Scott", "Zen");
        ctx.putObject("newbook", o);
        Collection oldBindingNames = ctx.getBindingNames();        
        assertEquals("There must be 5 items", 5, oldBindingNames.size());
        
        l.reset();
        l2.reset();
        ModuleUtils.DEFAULT.enableBookModule(false);
        assertEquals("Three events must be fired", 3, l.b.size());
        assertEquals("Three events must be fired", 3, l.all.size());
        assertEquals("Three events must be fired", 3, l2.b.size());
        assertEquals("Three events must be fired", 3, l2.all.size());
        Collection newBindingNames = ctx.getBindingNames();
        assertEquals("There must be 2 items", 2, newBindingNames.size());

        oldBindingNames.removeAll(newBindingNames);
        for (Iterator iterator = oldBindingNames.iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            assertFalse(newBindingNames.contains(name));
            assertNull(ctx.getObject(name, null));
        }
        
        l.reset();
        l2.reset();
        ModuleUtils.DEFAULT.enableBookModule(true);
        assertEquals("Three events must be fired", 3, l.b.size());
        assertEquals("Three events must be fired", 3, l.all.size());
        assertEquals("Three events must be fired", 3, l2.b.size());
        assertEquals("Three events must be fired", 3, l2.all.size());
        assertEquals("There must be 5 items", 5, ctx.getBindingNames().size());
        
        l.reset();
        l2.reset();
        ModuleUtils.DEFAULT.enableCDModule(true);
        assertEquals("Two events must be fired", 2, l.b.size());
        assertEquals("Two events must be fired", 2, l.all.size());
        assertEquals("Two events must be fired", 2, l2.b.size());
        assertEquals("Two events must be fired", 2, l2.all.size());
        assertEquals("There must be 7 items", 7, ctx.getBindingNames().size());
        
        assertEquals("Objects must be equal", createCD("Philip Glass", "The Hours"), ctx.getObject("cd1", null));
        assertEquals("Objects must be equal", createCD("Philip Glass", "Music from The Screens"), ctx.getObject("cd2", null));
        
        o = createCD("Philip Glass", "Naqoyqatsi");
        ctx.putObject("newcd", o);
        assertEquals("There must be 8 items", 8, ctx.getBindingNames().size());
        
        l.reset();
        l2.reset();
        ModuleUtils.DEFAULT.enableCDModule(false);
        
        assertEquals("Three events must be fired", 3, l.b.size());
        assertEquals("Three events must be fired", 3, l.all.size());
        assertEquals("Three events must be fired", 3, l2.b.size());
        assertEquals("Three events must be fired", 3, l2.all.size());
        assertEquals("There must be 5 items", 5, ctx.getBindingNames().size());
        
        l.reset();
        l2.reset();
        ModuleUtils.DEFAULT.enableCDModule(true);
        assertEquals("Three events must be fired", 3, l.b.size());
        assertEquals("Three events must be fired", 3, l.all.size());
        assertEquals("Three events must be fired", 3, l2.b.size());
        assertEquals("Three events must be fired", 3, l2.all.size());
        assertEquals("There must be 8 items", 8, ctx.getBindingNames().size());
        
    }
    
    private Object createBook(String author, String title) throws Exception {
        Class clazz = findClass("org.bookmodule.Book");
        assertTrue("found class must be owned by its module "+clazz.getClassLoader(), ModuleUtils.DEFAULT.getBookModule().owns(clazz));
        Constructor c = clazz.getConstructor(new Class[]{String.class, String.class});
        return c.newInstance(new Object[]{author, title});
    }
    
    private Object createCD(String artits, String album) throws Exception {
        Class clazz = findClass("org.cdmodule.CD");
        assertTrue("found class must be owned by its module "+clazz.getClassLoader(), ModuleUtils.DEFAULT.getCDModule().owns(clazz));
        Constructor c = clazz.getConstructor(new Class[]{String.class, String.class});
        return c.newInstance(new Object[]{artits, album});
    }
    
    private Class findClass(String name) throws ClassNotFoundException {
        ClassLoader c = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
        if (c == null) {
            return Class.forName(name, true, null);
        } else {
            return Class.forName(name, true, c);
        }
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
            if (evt.getType() == SubcontextEvent.SUBCONTEXT_ADDED) {
                if (evt.getContext().getSubcontext(evt.getSubcontextName()) == null) {
                    error = "Added subcontext must already exist: "+evt;
                }
            }
            if (evt.getType() == SubcontextEvent.SUBCONTEXT_REMOVED) {
                if (evt.getContext().getSubcontext(evt.getSubcontextName()) != null) {
                    error = "Removed subcontext must be already deleted: "+evt;
                }
            }
        }
    
        public void bindingChanged(BindingEvent evt) {
            b.add(evt);
            all.add(evt);
            if (evt.getType() == BindingEvent.BINDING_ADDED || evt.getType() == BindingEvent.BINDING_MODIFIED) {
                if (evt.getContext().getObject(evt.getBindingName(), null) == null) {
                    error = "Added or modified binding cannot have null value: "+evt;
                }
            }
            if (evt.getType() == BindingEvent.BINDING_REMOVED) {
                if (!evt.getContext().getObject(evt.getBindingName(), "abcd").equals("abcd")) {
                    error = "Removed binding must have null value: "+evt;
                }
            }
        }
        
        public void attributeChanged(AttributeEvent evt) {
            a.add(evt);
            all.add(evt);
            if (evt.getType() == AttributeEvent.ATTRIBUTE_ADDED || evt.getType() == AttributeEvent.ATTRIBUTE_MODIFIED) {
                if (evt.getContext().getAttribute(evt.getBindingName(), evt.getAttributeName(), null) == null) {
                    error = "Added or modified attribute cannot have null value: "+evt;
                }
            }
            if (evt.getType() == AttributeEvent.ATTRIBUTE_REMOVED) {
                if (!evt.getContext().getAttribute(evt.getBindingName(), evt.getAttributeName(), "abcd").equals("abcd")) {
                    error = "Removed attribute must have null value: "+evt;
                }
            }
        }
        
    }

}
