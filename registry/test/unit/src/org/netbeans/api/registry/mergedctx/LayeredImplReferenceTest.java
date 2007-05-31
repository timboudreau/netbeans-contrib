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


public class LayeredImplReferenceTest extends ImplReferenceTest {
    Context rooContext, ctx1, ctx2, ctx3;


    public LayeredImplReferenceTest(String name) {
        super(name);
    }


    Context getMergeContext() {
        if (rooContext == null) {
            Context[] retVal = new Context [] {getActiveDelegate(), getReadOnlyDelegate1(), getReadOnlyDelegate2()};
            rooContext  = Context.merge(retVal);
        }
        return rooContext;
    }

    Context getActiveDelegate() {
        if (ctx1 == null) {
            super.getMergeContext();
            ctx1 = super.getActiveDelegate();
        }
        return ctx1;
    }

    Context getReadOnlyDelegate1() {
        try {
            if (ctx2 == null)
                ctx2 = super.getMergeContext().createSubcontext("ctx2");
        } catch (ContextException e) {
            throw new InternalError("setUp failure");
        }
        return ctx2;
    }

    Context getReadOnlyDelegate2() {
        try {
            if (ctx3 == null)
                ctx3 = super.getMergeContext().createSubcontext("ctx3");
        } catch (ContextException e) {
            throw new InternalError("setUp failure");
        }
        return ctx3;
    }
}
