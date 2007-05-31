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

/**
 *
 * @author  Vitezslav Stejskal
 * @author  David Konecny
 */
public class AttributeTest extends NbTestCase {
    private static final String MY_NULL = new String("MY_NULL");

    public AttributeTest (String name) {
        super (name);
    }

    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(AttributeTest.class));
    }
    
    protected void setUp () throws Exception {
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    

    public void testAttrsOnObjectBinding() throws Exception {
        getRootContext().putObject("XXXaa11", new Integer(123));
        implTestAttrs(getRootContext(), "XXXaa11");
        getRootContext().putObject("XXXaa11", null);
    }
    
    public void testAttrsOnObjectBinding2() throws Exception {
        Context ctx = getRootContext().createSubcontext("abcd");
        ctx.putObject("XXXaa11", new Integer(123));
        implTestAttrs(ctx, "XXXaa11");
        getRootContext().destroySubcontext("abcd");
    }
    
    public void testAttrsOnPrimitiveBinding() throws Exception {
        getRootContext().putInt("aa11", 123);
        implTestAttrs(getRootContext(), "aa11");
        getRootContext().putObject("aa11", null);
    }
    
    public void testAttrsOnPrimitiveBinding2() throws Exception {
        Context ctx = getRootContext().createSubcontext("abcd");
        ctx.putInt("aa11", 123);
        implTestAttrs(ctx, "aa11");
        getRootContext().destroySubcontext("abcd");
    }
    
    public void testAttrsOnContext() throws Exception {
        Context ctx = getRootContext().createSubcontext("abcd");
        implTestAttrs(ctx, null);
        getRootContext().destroySubcontext("abcd");
    }
    
    
    public void implTestAttrs(Context context, String bindingName) throws Exception {
        String attrName1 = "attr1";
        String attrName2 = "attr2";
        assertTrue("Attribute should not exist", context.getAttribute(bindingName, attrName1, MY_NULL) == MY_NULL);
        assertTrue("Attribute should not exist", context.getAttribute(bindingName, attrName2, MY_NULL) == MY_NULL);
        
        context.setAttribute(bindingName, attrName1, "someValue1111");
        assertTrue("Attribute value does not match - " + context.getAttribute(bindingName, attrName1, MY_NULL), "someValue1111".equals(context.getAttribute(bindingName, attrName1, MY_NULL)));
        
        context.setAttribute(bindingName, attrName2, "someValue");
        assertTrue("Attribute value does not match - " + context.getAttribute(bindingName, attrName2, MY_NULL), "someValue".equals(context.getAttribute(bindingName, attrName2, MY_NULL)));
        
        context.setAttribute(bindingName, attrName1, null);
        assertTrue("Attribute should not exist", context.getAttribute(bindingName, attrName1, MY_NULL) == MY_NULL);
        
        context.setAttribute(bindingName, attrName2, null);
        assertTrue("Attribute should not exist", context.getAttribute(bindingName, attrName2, MY_NULL) == MY_NULL);
    }
    
    public void testRemovedBindingAttrs() throws Exception {
        Context ctx = getRootContext().createSubcontext("spam");

        ctx.setAttribute("XXXaa11", "A1", "valueOfA1");
        assertEquals("Attribute should not exist", "nononono", ctx.getAttribute("XXXaa11", "A1", "nononono"));
        
        ctx.putObject("XXXaa11", new JLabel("123"));
        ctx.setAttribute("XXXaa11", "A1", "valueOfA1");
        assertEquals("Attribute does not match", "valueOfA1", ctx.getAttribute("XXXaa11", "A1", "nononono"));

        ctx.putObject("XXXaa11", null);
        assertEquals("Attribute should not exist", "nononono", ctx.getAttribute("XXXaa11", "A1", "nononono"));
        
        ctx.setAttribute("prim", "at", "va");
        assertEquals("Attribute should not exist", "nova", ctx.getAttribute("prim", "at", "nova"));
        
        ctx.putInt("prim", 1989);
        ctx.setAttribute("prim", "at", "vava");
        assertEquals("Attribute does not match", "vava", ctx.getAttribute("prim", "at", "nova"));

        ctx.putObject("prim", null);
        assertEquals("Attribute should not exist", "nova", ctx.getAttribute("prim", "at", "nova"));
        
        getRootContext().destroySubcontext("spam");
    }

    protected Context getRootContext () {
        return Context.getDefault();
    }

}
