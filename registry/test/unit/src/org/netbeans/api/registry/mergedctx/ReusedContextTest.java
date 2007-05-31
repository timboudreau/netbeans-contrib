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

import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.ContextTest;

public class ReusedContextTest extends ContextTest {
    private Context rootCtx;

    protected Context getRootContext() {
        if (rootCtx == null)
            rootCtx = SetUpUtils.getSimpleContext(super.getContext());
        return rootCtx;
    }

    public ReusedContextTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        getRootContext();
    }

    public void testContextMerge() throws Exception {
        Context ctx1 = SetUpUtils.getSubctx1();
        Context ctx2 = SetUpUtils.getSubctx2();
        Context ctx3 = SetUpUtils.getSubctx3();

        String subcontextName = "TestMerge";
        ctx2 = ctx2.createSubcontext(subcontextName);
        ctx3 = ctx3.createSubcontext(subcontextName);

        String subSubcontextName1 = "AMerge";
        String subSubcontextName2 = "BMerge";
        String subSubcontextName3 = "CMerge";

        ctx2.createSubcontext(subSubcontextName2);
        ctx3.createSubcontext(subSubcontextName3);


        Context test = rootCtx.getSubcontext(subcontextName);
        assertTrue(test != null);
        assertTrue(test.getSubcontext(subSubcontextName2) != null);
        assertTrue(test.getSubcontext(subSubcontextName3) != null);

        ctx1 = ctx1.createSubcontext(subcontextName);
        ctx1.createSubcontext(subSubcontextName1);

        assertTrue(test != null);
        assertTrue(test.getSubcontext(subSubcontextName1) != null);
        assertTrue(test.getSubcontext(subSubcontextName2) != null);
        assertTrue(test.getSubcontext(subSubcontextName3) != null);
    }
}
