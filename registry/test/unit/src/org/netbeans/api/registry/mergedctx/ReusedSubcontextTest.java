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
import org.netbeans.api.registry.ContextException;
import org.netbeans.api.registry.SubcontextTest;
import org.openide.filesystems.FileObject;

public class ReusedSubcontextTest extends SubcontextTest{
    private Context rootCtx;
    protected Context getRootContext() {
        if (rootCtx == null)
            rootCtx = SetUpUtils.getSimpleContext(super.getRootContext());
        return rootCtx;
    }

    protected void setUp() throws Exception {
        super.setUp();
        getRootContext();
    }

    protected FileObject findResource(String resource) {
        return SetUpUtils.findSimpleResource(resource);
    }

    protected FileObject getFileObject() {
        return SetUpUtils.getSimpleRoot();
    }

    protected FileObject getRoot() {
        return SetUpUtils.getSimpleRoot();
    }

    public ReusedSubcontextTest(String name) {
        super(name);
    }

    public void testFlipFlap () throws ContextException {
        Context ctx = getRootContext().createSubcontext("test");;
        final String subCtxName = "subCtx";

        Object tmpObj;

        tmpObj = ctx.createSubcontext(subCtxName);
        assertEquals("Unexpected: different instances",tmpObj, ctx.getSubcontext(subCtxName));

        ctx.destroySubcontext(subCtxName);
        tmpObj = ctx.createSubcontext(subCtxName);
        assertEquals("Unexpected: different instances",tmpObj, ctx.getSubcontext(subCtxName));

        ctx.destroySubcontext(subCtxName);
        tmpObj = ctx.createSubcontext(subCtxName);
        assertEquals("Unexpected: different instances",tmpObj, ctx.getSubcontext(subCtxName));
    }

    public void testDestroyContext () throws Exception {
        String subCtxName = "subct12345";
        Context subctx = getRootContext().createSubcontext (subCtxName);
        Context parent = subctx.getParentContext();
        parent.destroySubcontext (subCtxName);
        assertTrue("Parent Context found",  parent.getSubcontext(subCtxName) == null);
    }

}
