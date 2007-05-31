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

import junit.textui.TestRunner;
import org.netbeans.api.registry.Context;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

public class ResettableTest  extends NbTestCase {
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
        Context rootCtx = SetUpUtils.getSimpleContext(Context.getDefault());
        Context actWCtx = SetUpUtils.getSubctx1();
        Context RCtx = SetUpUtils.getSubctx2();

        String binding = "rbinding";
        assertEquals(false, rootCtx.hasDefault(binding));
        assertEquals(true, rootCtx.isModified(binding));

        actWCtx.putString(binding, binding);
        assertEquals(false, rootCtx.hasDefault(binding));
        assertEquals(true, rootCtx.isModified(binding));

        rootCtx.revert(binding);
        assertEquals(false, rootCtx.hasDefault(binding));
        assertEquals(true, rootCtx.isModified(binding));

        actWCtx.putString(binding, binding);
        assertEquals(false, rootCtx.hasDefault(binding));
        assertEquals(true, rootCtx.isModified(binding));

        RCtx.putString(binding, binding);
        assertEquals(true, rootCtx.hasDefault(binding));
        assertEquals(true, rootCtx.isModified(binding));

        rootCtx.revert(binding);
        assertEquals(true, rootCtx.hasDefault(binding));
        assertEquals(false, rootCtx.isModified(binding));

        actWCtx.putString(binding, binding);
        assertEquals(true, rootCtx.hasDefault(binding));
        assertEquals(true, rootCtx.isModified(binding));

        actWCtx.putString(binding, null);
        assertEquals(true, rootCtx.hasDefault(binding));
        assertEquals(false, rootCtx.isModified(binding));
    }
}
