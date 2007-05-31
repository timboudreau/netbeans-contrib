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

import org.netbeans.api.registry.BindingTest;
import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.ContextException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

public class ReusedBindingTest extends BindingTest {
    private Context rootCtx;
    protected Context getRootContext() {
        if (rootCtx == null)
            rootCtx = SetUpUtils.getSimpleContext(super.getRootContext());
        return rootCtx;
    }

    public ReusedBindingTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        getRootContext();
    }

    protected FileObject getRoot() {
        return SetUpUtils.getSimpleRoot();
    }

    protected FileObject findResource(String resource) {
        if (resource.startsWith("/"))
            resource = resource.substring(1);
        return Repository.getDefault ().findResource ("first/"+resource);
    }

    public void testFlipFlap () throws ContextException{
        Context ctx = getRootContext().createSubcontext("test");;
        String bindingName = "myBindingName";
        final String value = "myValue";
        final String defValue = "defValue";
        Object obj;

        ctx.putString(bindingName, value);
        //assertEquals("Unexpected: different instances",value, ctx.getString(bindingName,""));

        ctx.putString(bindingName, null);
/*
        obj = ctx.getObject(bindingName,defValue);
        assertTrue("Unexpected different instances: "+obj,obj == defValue);
*/

        ctx.putString(bindingName, value);
        obj = ctx.getString(bindingName,"");
        assertEquals("Unexpected: different instances: " + obj,value, obj);

        ctx.putString(bindingName, null);
        obj = ctx.getObject(bindingName,defValue);
        assertTrue("Unexpected different instances: "+obj,obj == defValue);

    }

}
