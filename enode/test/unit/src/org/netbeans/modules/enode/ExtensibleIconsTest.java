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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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

package org.netbeans.modules.enode;


import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

import org.netbeans.api.enode.ExtensibleNode;
import org.netbeans.api.registry.*;

/**
 * This class should test the setting icons in the
 * ExtensibleNode.
 * @author David Strupl
 */
public class ExtensibleIconsTest extends NbTestCase {
    /** root Context */
    private Context root;

    public ExtensibleIconsTest(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(ExtensibleIconsTest.class));
    }
    
    /**
     * Sets up the testing environment by creating testing folders
     * on the system file system.
     */
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
        String baseFolder = ExtensibleNode.E_NODE_ICONS.substring(1, ExtensibleNode.E_NODE_ICONS.length()-1);
        root = Context.getDefault().createSubcontext(baseFolder);
    }
    
    /**
     * Deletes the folders created in method setUp().
     */
    protected void tearDown() throws Exception {
    }

    /**
     */
    public void test1() throws Exception {
        ExtensibleNode en1 = new ExtensibleNode("a/b/c", true);
        java.lang.reflect.Method getIconManagerMethod = ExtensibleNode.class.getDeclaredMethod("getIconManager", new Class[0]);
        getIconManagerMethod.setAccessible(true);
        Object iconMan = getIconManagerMethod.invoke(en1, new Object[0]);
        
        Context b = root.createSubcontext("a/b");
        Context c = root.createSubcontext("a/b/c");
        
//         b.putObject("i1", base1);
        
        root.destroySubcontext("a");
    }
}
