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

import javax.swing.*;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  Vitezslav Stejskal
 * @author  David Konecny
 */
public class EnumerationTest extends NbTestCase {
    public EnumerationTest (String name) {
        super (name);
    }

    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(EnumerationTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    public void testSubcontextNames() throws Exception {
        Context ctx = getRootContext().createSubcontext("aa/bb/cc");
        implSubcontextNames(ctx);
        getRootContext().destroySubcontext("aa");
    }
    
    public void implSubcontextNames(Context context) throws Exception {
        context.createSubcontext("aa11");
        context.createSubcontext("aa22");
        context.createSubcontext("aa33");
        context.createSubcontext("aa44");
        Collection coll = context.getSubcontextNames();
        assertTrue("Collection should not contain any other subcontexts - "+coll.size(), coll.size() == 4);
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            String s = (String)it.next();
            if (s.equals("aa11") || s.equals("aa22") || s.equals("aa33") || s.equals("aa44")) {
                // OK
            } else {
                assertTrue("Collection contains unknown subcontext "+s, false);
            }
        }
        context.destroySubcontext("aa11");
        context.destroySubcontext("aa22");
        context.destroySubcontext("aa33");
        context.destroySubcontext("aa44");
    }
    
    public void testBindingNames() throws Exception {
        Context ctx = getRootContext().createSubcontext("aaa1/bbb2/ccc3");
        implBindingNames(ctx);
        getRootContext().destroySubcontext("aaa1");
    }
    
    public void implBindingNames(Context context) throws Exception {
        context.putObject("aa11", new JLabel("123"));
        context.putObject("aa22", new JLabel("12"));
        context.putObject("aa33", new JLabel("1"));
        context.putObject("aa44", new JLabel("1234"));
        Collection coll = context.getBindingNames();
        assertTrue("Collection should not contain any other bindings - "+coll.size(), coll.size() == 4);
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            String s = (String)it.next();
            if (s.equals("aa11") || s.equals("aa22") || s.equals("aa33") || s.equals("aa44")) {
                // OK
            } else {
                assertTrue("Collection contains unknown binding "+s, false);
            }
        }
        context.putObject("aa11", null);
        context.putObject("aa22", null);
        context.putObject("aa33", null);
        context.putObject("aa44", null);
    }
    
    public void testAttributeNames() throws Exception {
        Context ctx = getRootContext().createSubcontext("aaa1_/bbb2_/ccc3_");
        implAttributeNames(null, ctx);
        getRootContext().destroySubcontext("aaa1_");
    }
    
    public void testAttributeNames1() throws Exception {
        Context ctx = getRootContext().createSubcontext("aaa1__/bbb2_/ccc3_");
        ctx.putObject("someB", new JLabel("labelo"));
        implAttributeNames("someB", ctx);
        getRootContext().destroySubcontext("aaa1__");
    }
    
    public void testAttributeNames2() throws Exception {
        Context ctx = getRootContext().createSubcontext("aaa1___/bbb2_/ccc3_");
        ctx.putInt("someI", 654);
        implAttributeNames("someI", ctx);
        getRootContext().destroySubcontext("aaa1___");
    }
    
    public void implAttributeNames(String bindingName, Context context) throws Exception {
        context.setAttribute(bindingName, "aa11", "123");
        context.setAttribute(bindingName, "aa12", "123");
        context.setAttribute(bindingName, "aa13", "123");
        context.setAttribute(bindingName, "aa14", "123");
        Collection coll = context.getAttributeNames(bindingName);
        int count = 4;
        if (bindingName == null) {
            // each context has in addition artificial Context.DEFAULT_SORTING attribute.
            count++;
        }
        assertTrue("Number of elements in collection is different.", coll.size() == count);
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            String s = (String)it.next();
            if (s.equals("aa11") || s.equals("aa12") || s.equals("aa13") || s.equals("aa14") || s.equals("default.context.sorting")) {
                // OK
            } else {
                assertTrue("Collection contains unknown binding "+s, false);
            }
        }
        context.setAttribute(bindingName, "aa11", null);
        context.setAttribute(bindingName, "aa12", null);
        context.setAttribute(bindingName, "aa13", null);
        context.setAttribute(bindingName, "aa14", null);
    }
    
    public void testMixedAttributeNames() throws Exception {
        Context context = getRootContext().createSubcontext("soso/bbb2_/ccc3_");
        context.putInt("someI", 654);
        context.setAttribute("someI", "aa11", "123");
        context.setAttribute("someI", "aa12", "123");
        context.setAttribute("someI", "bb13", "123");
        context.setAttribute("someI", "bb14", "123");
        
        context.putObject("someO", new JLabel("labelo labelo"));
        context.setAttribute("someO", "aa11", "123");
        context.setAttribute("someO", "aa12", "123");
        context.setAttribute("someO", "cc13", "123");
        context.setAttribute("someO", "cc14", "123");
        
        context.setAttribute(null, "aa11", "123");
        context.setAttribute(null, "aa12", "123");
        context.setAttribute(null, "dd13", "123");
        context.setAttribute(null, "dd14", "123");
        
        Collection coll = context.getAttributeNames(null);
        // each context has in addition artificial Context.DEFAULT_SORTING attribute.
        assertTrue("Collection should contain 5 attrs, but has "+coll.size(), coll.size() == 5);
        coll = context.getAttributeNames("someO");
        assertTrue("Collection should contain 4 attrs, but has "+coll.size(), coll.size() == 4);
        coll = context.getAttributeNames("someI");
        assertTrue("Collection should contain 4 attrs, but has "+coll, coll.size() == 4);
        
        getRootContext().destroySubcontext("soso");
    }

    protected Context getRootContext () {
        return Context.getDefault();
    }
    
}
