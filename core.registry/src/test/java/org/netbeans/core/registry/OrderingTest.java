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
import org.netbeans.api.registry.fs.FileSystemContextFactory;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.XMLFileSystem;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

public class OrderingTest extends NbTestCase {
    
    private FileObject root = null;
    
    public OrderingTest(String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(OrderingTest.class));
    }
    
    protected void setUp () throws Exception {
    }
    
    public void testDefaultOrdering() throws Exception {
        URL u1 = getClass().getResource("data/layer1.xml");
        URL u2 = getClass().getResource("data/layer2.xml");
               
        FileSystem xfs1 = new XMLFileSystem( u1 );
        FileSystem xfs2 = new XMLFileSystem( u2 );        
        FileSystem mfs = new TestMFS( new FileSystem[] { xfs1, xfs2 } );
    
        BasicContext rootCtx = FileSystemContextFactory.createContext(mfs.getRoot());
        Context cotoxo = SpiUtils.createContext(rootCtx);
        Context ctx = cotoxo.getSubcontext("folder1");
        Collection c = ctx.getOrderedNames();
        Iterator it = c.iterator();
        String[] arr = new String[]{"fileA", "file1", "fileB/", "file2", "fileC", "file3/", "fileD", "file4" };
        int i=0;
        while (it.hasNext()) {
            String name = (String)it.next();
            assertEquals("They must be the same", arr[i], name);
            i++;
        }
        
        ctx = cotoxo.getSubcontext("folder2");
        c = ctx.getOrderedNames();
        System.err.println("c="+c);
        it = c.iterator();
        i=0;
        while (it.hasNext()) {
            String name = (String)it.next();
            assertEquals("They must be the same", arr[i], name);
            i++;
        }

        ctx = cotoxo.getSubcontext("folder3");
        c = ctx.getOrderedNames();
        it = c.iterator();
        i=0;
        while (it.hasNext()) {
            String name = (String)it.next();
            assertEquals("They must be the same", arr[i], name);
            i++;
        }
    }
    
}
