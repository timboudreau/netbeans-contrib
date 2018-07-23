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
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

import javax.swing.*;
import java.net.URL;

/**
 *
 * @author  David Konecny
 */
public class ResettableTest extends NbTestCase {
    
    private static final String MY_NULL = new String("MY_NULL");

    public ResettableTest (String name) {
        super (name);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ResettableTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    public void testResettable() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());

        URL u1 = getClass().getResource("data/layer_defaults.xml");
               
        FileSystem xfs1 = new XMLFileSystem( u1 );
        FileSystem mfs = new TestMFS( new FileSystem[] { lfs, xfs1 } );
    
        BasicContext root = new ResettableContextImpl(mfs.getRoot(), null, xfs1, lfs);
        Context rootContext = SpiUtils.createContext(root);

        
        Context ctx = rootContext.createSubcontext("ah/oy");
        
        String bindingName = "myB";
        ctx.putInt(bindingName, 123);
        assertTrue("The binding has default value", ctx.hasDefault(bindingName) == false);
        assertTrue("The binding is not modified", ctx.isModified(bindingName) == true);
        ctx.revert(bindingName);
        assertTrue("The value was not reverted", ctx.getInt(bindingName, 99) == 99);
        
        bindingName = "myBBB";
        ctx.putObject(bindingName, new JLabel("arigato"));
        assertTrue("The binding has default value", ctx.hasDefault(bindingName) == false);
        assertTrue("The binding is not modified", ctx.isModified(bindingName) == true);
        ctx.revert(bindingName);
        assertTrue("The value was not reverted", ctx.getObject(bindingName, null) == null);
        
        // make context dirty
        ctx.setAttribute(null, "smthA", "sayonara");
        ctx.createSubcontext("smthC");
        ctx.putInt("smthB", 123);
        
        assertTrue("The context has default value", ctx.hasDefault(null) == false);
        assertTrue("The context is not modified", ctx.isModified(null) == true);
        ctx.revert(null);
        assertTrue("The context was not reverted - "+ctx.getBindingNames()+ctx.getSubcontextNames()+
            ctx.getAttributeNames(null), (ctx.getBindingNames().size() == 0) &&
            (ctx.getAttributeNames(null).size() == 1 && ctx.getAttributeNames(null).iterator().next().equals("default.context.sorting"))
            && (ctx.getSubcontextNames().size() == 0));
        
        rootContext.destroySubcontext("ah");
        
        ctx = rootContext.getSubcontext("contextZ");
        
        // test object binding
        assertTrue("The binding does not have default value", ctx.hasDefault("objectBindingOne") == true);
        assertTrue("The binding is modified", ctx.isModified("objectBindingOne") == false);
        
        assertEquals("The value does not match", new String(), ctx.getObject("objectBindingOne", null));
        ctx.putObject("objectBindingOne", new String("arigato"));
        assertEquals("The value does not match", new String("arigato"), ctx.getObject("objectBindingOne", null));
        assertTrue("The binding does not have default value", ctx.hasDefault("objectBindingOne") == true);
        assertTrue("The binding is not modified", ctx.isModified("objectBindingOne") == true);

        ctx.putObject("objectBindingOne", null);
        assertTrue("The binding does not have default value", ctx.hasDefault("objectBindingOne") == true);
        assertTrue("The binding is not modified", ctx.isModified("objectBindingOne") == true);
        assertEquals("The value does not match", null, ctx.getObject("objectBindingOne", null));
        
        ctx.revert("objectBindingOne");
        assertTrue("The binding does not have default value", ctx.hasDefault("objectBindingOne") == true);
        assertTrue("The binding is not modified", ctx.isModified("objectBindingOne") == false);
        assertEquals("The value does not match", new String(), ctx.getObject("objectBindingOne", null));
        
        // test primitive binding
        assertTrue("The binding does not have default value", ctx.hasDefault("primitiveBindingOne") == true);
        assertTrue("The binding is modified", ctx.isModified("primitiveBindingOne") == false);
        
        assertEquals("The value does not match", "primitiveBindingOneValue", ctx.getString("primitiveBindingOne", "nono"));
        ctx.putString("primitiveBindingOne", "new value");
        assertEquals("The value does not match", "new value", ctx.getString("primitiveBindingOne", "nono"));
        assertTrue("The binding does not have default value", ctx.hasDefault("primitiveBindingOne") == true);
        assertTrue("The binding is not modified", ctx.isModified("primitiveBindingOne") == true);

        ctx.putObject("primitiveBindingOne", null);
        assertTrue("The binding does not have default value", ctx.hasDefault("primitiveBindingOne") == true);
        assertTrue("The binding is not modified", ctx.isModified("primitiveBindingOne") == true);
        assertEquals("The value does not match", null, ctx.getString("primitiveBindingOne", null));
        
        ctx.revert("primitiveBindingOne");
        assertTrue("The binding does not have default value", ctx.hasDefault("primitiveBindingOne") == true);
        assertTrue("The binding is not modified", ctx.isModified("primitiveBindingOne") == false);
        assertEquals("The value does not match", "primitiveBindingOneValue", ctx.getString("primitiveBindingOne", "nono"));
        
    }
    
}
