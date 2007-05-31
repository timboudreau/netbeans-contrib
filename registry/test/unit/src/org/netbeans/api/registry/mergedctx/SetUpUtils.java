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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

import java.util.ArrayList;

public class SetUpUtils {
    private static Context subctx1;
    private static Context subctx2;
    private static Context subctx3;

    static Context getSimpleContext(Context originalCtx) {
        System.gc();
        System.gc();
        System.gc();
        ArrayList l = new ArrayList ();
        try {
            subctx1 = originalCtx.createSubcontext("first");
            l.add(getSubctx1());
            subctx2 = originalCtx.createSubcontext("second");
            l.add (getSubctx2());
            subctx3 = originalCtx.createSubcontext("third");
            l.add (getSubctx3());

            Context[] retVal = new Context [l.size()];
            l.toArray(retVal);
            Context mretVal  = Context.merge(retVal);
            return mretVal;
        } catch (ContextException e) {
            throw new UnknownError();
        }
    }

    static FileObject getSimpleRoot () {
       return Repository.getDefault ().getDefaultFileSystem ().findResource("first");
    }

    static FileObject findSimpleResource (String resource) {
        if (resource.startsWith("/"))
            resource = resource.substring(1);
        return Repository.getDefault ().getDefaultFileSystem ().findResource("first/"+resource);
    }

    static Context getSubctx1() {
        return subctx1;
    }

    static Context getSubctx2() {
        return subctx2;
    }

    static Context getSubctx3() {
        return subctx3;
    }
}
