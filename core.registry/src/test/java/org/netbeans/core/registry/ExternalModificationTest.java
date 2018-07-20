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
