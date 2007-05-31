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
package org.netbeans.api.registry.mergedctx;

import org.netbeans.api.registry.AttributeTest;
import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.ContextException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;


public class ReusedAttributeTest extends AttributeTest {
    private Context rootCtx;
    protected Context getRootContext() {
        if (rootCtx == null)
            rootCtx = SetUpUtils.getSimpleContext(super.getRootContext());
        return rootCtx;
    }

    public ReusedAttributeTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        rootCtx = null;
        super.setUp();
        getRootContext();
    }

    public void testFlipFlap () throws ContextException {
        Context ctx = getRootContext().createSubcontext("test");;
        final String bindingName = "myBindingName";
        final String bindingValue = "myValue";

        final String attrName = "myAttrName";
        final String attrValue = "myValue";
        final String defAttrValue = "defBindingValue";

        Object tmpObj;

        ctx.putString(bindingName, bindingValue);
        assertEquals(bindingValue, ctx.getString(bindingName,""));

        ctx.setAttribute(bindingName, attrName, attrValue);
        tmpObj = ctx.getAttribute(bindingName, attrName, defAttrValue);
        assertEquals(attrValue, tmpObj);

        ctx.setAttribute(bindingName, attrName, null);
        tmpObj = ctx.getAttribute(bindingName, attrName, defAttrValue);
        assertEquals(defAttrValue, tmpObj);

        ctx.setAttribute(bindingName, attrName, attrValue);
        tmpObj = ctx.getAttribute(bindingName, attrName, defAttrValue);
        assertEquals(attrValue, tmpObj);

        ctx.setAttribute(bindingName, attrName, null);
        tmpObj = ctx.getAttribute(bindingName, attrName, defAttrValue);
        assertEquals(defAttrValue, tmpObj);

    }

    public void testCopyAttribs () throws ContextException {
        Context rCtx = SetUpUtils.getSubctx2();
        String attribs = "cattr1";
        String binding = "cbinding";

        rCtx.putString(binding, binding);
        rCtx.setAttribute(binding, attribs, attribs);
        assertEquals(attribs, rCtx.getAttribute(binding, attribs, "defValue"));
        assertEquals(attribs, getRootContext().getAttribute(binding, attribs, "defValue"));


        /*important tests*/
        getRootContext().putString(binding, binding);
        assertEquals(attribs, getRootContext().getAttribute(binding, attribs, "defValue"));
        rCtx.putString(binding, null);
        assertEquals(attribs, getRootContext().getAttribute(binding, attribs, "defValue"));
    }

    public void testAttribsForCtx () throws ContextException {
        Context ctx1 = SetUpUtils.getSubctx1();
        Context ctx2 = SetUpUtils.getSubctx2();
        Context ctx3 = SetUpUtils.getSubctx3();
        String[] attribs = new String[] {"mattr1","mattr2","mattr3"};

        ctx1.setAttribute(null,attribs[0],attribs[0]);
        ctx2.setAttribute(null,attribs[1],attribs[1]);
        ctx3.setAttribute(null,attribs[2],attribs[2]);

        Context ctx = getRootContext();
        Collection  original = new HashSet (Arrays.asList(attribs));
        Collection  names = ctx.getAttributeNames(null);
        original.removeAll(names);
        assertTrue (original.size() == 0);
        assertEquals(attribs[0], ctx.getAttribute(null, attribs[0], "defValue"));
        assertEquals(attribs[1], ctx.getAttribute(null, attribs[1], "defValue"));
        assertEquals(attribs[2], ctx.getAttribute(null, attribs[2], "defValue"));
    }

    public void testAttribsForBinding () throws ContextException {
        Context ctx1 = SetUpUtils.getSubctx1();
        Context ctx2 = SetUpUtils.getSubctx2();
        Context ctx3 = SetUpUtils.getSubctx3();
        String[] attribs = new String[] {"battr1","battr2","battr3"};
        String binding = "bbinding";

        ctx1.putString(binding, binding);
        ctx2.putString(binding, binding);
        ctx3.putString(binding, binding);

        ctx1.setAttribute(binding,attribs[0],attribs[0]);
        ctx2.setAttribute(binding,attribs[1],attribs[1]);
        ctx3.setAttribute(binding,attribs[2],attribs[2]);

        Context ctx = getRootContext();
        Collection  original = new ArrayList();
        original.add(attribs[0]);
        Collection  names = ctx.getAttributeNames(binding);
        original.removeAll(names);
        assertTrue(names.size() == 1);
        assertTrue (original.size() == 0);
        assertEquals(attribs[0], ctx.getAttribute(binding, attribs[0], "defValue"));
        assertEquals("defValue", ctx.getAttribute(binding, attribs[1], "defValue"));
        assertEquals("defValue", ctx.getAttribute(binding, attribs[2], "defValue"));
    }


}
