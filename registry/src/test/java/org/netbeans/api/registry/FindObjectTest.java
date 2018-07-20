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
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

public class FindObjectTest extends NbTestCase {
    public FindObjectTest(String name) {
        super (name);
    }

    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(FindObjectTest.class));
    }

    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    //TODO: findObject is going to be deprecated - so this test should be removed
    public void testObjectRef() throws Exception {
/*
        Context subctx = getContext().createSubcontext ("ichi");
        Context subctx2 = getContext().createSubcontext ("ichi/tha/kila");
        
        Object o = new JLabel("korosey");
        subctx.putObject("action", o);
        
        ObjectRef or = getContext().findObject(o);
        assertTrue("Object was not found.", or != null);
        assertTrue("Object was not found.", or.getContextAbsoluteName().equals("/ichi"));
        assertTrue("Object was not found.", or.getBindingName().equals("action"));
        or = subctx.findObject(o);
        assertTrue("Object was not found.", or != null);
        assertTrue("Object was not found.", or.getContextAbsoluteName().equals("/ichi"));
        assertTrue("Object was not found.", or.getBindingName().equals("action"));
        or = subctx2.findObject(o);
        assertTrue("The object cannot be found.", or == null);
        
        Object o2 = new JLabel("korosey! korosey! ");
        subctx.putObject("action", o2);
        or = getContext().findObject(o);
        assertTrue("Object was not found.", or != null);
        assertTrue("Object was not found.", or.getContextAbsoluteName().equals("/ichi"));
        assertTrue("Object was not found.", or.getBindingName().equals("action"));
        or = getContext().findObject(o2);
        assertTrue("Object was not found.", or != null);
        assertTrue("Object was not found.", or.getContextAbsoluteName().equals("/ichi"));
        assertTrue("Object was not found.", or.getBindingName().equals("action"));

        
        getContext().destroySubcontext("ichi");
*/
    }

    protected Context getContext() {
        return Context.getDefault();    
    }
}
