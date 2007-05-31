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
