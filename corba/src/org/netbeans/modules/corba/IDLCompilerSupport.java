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

package org.netbeans.modules.corba;

import org.openide.loaders.CompilerSupport;
import org.openide.loaders.MultiDataObject;
import org.openide.compiler.CompilerType;
import org.openide.cookies.CompilerCookie;
import org.netbeans.*;

/*
 * @author Karel Gardas
 */

public class IDLCompilerSupport extends CompilerSupport {

    //public static final boolean DEBUG = true;
    private static final boolean DEBUG = false;

    public IDLCompilerSupport (MultiDataObject.Entry entry, Class cookie) {
        super (entry, cookie);
        if (DEBUG)
            System.out.println ("IDLCompilerSupport::IDLCompilerSupport (...)"); // NOI18N
    }

    protected CompilerType defaultCompilerType () {
        if (DEBUG)
            System.out.println ("IDLCompilerSupport::defaultCompilerType ()"); // NOI18N
        return new IDLCompilerType ();
    }


    public static class Compile extends IDLCompilerSupport
        implements CompilerCookie.Compile {

        public Compile (MultiDataObject.Entry entry) {
            super (entry, CompilerCookie.Compile.class);
            if (DEBUG)
                System.out.println ("Compile::Compile (...)"); // NOI18N
        }
    }

}

/*
 * $Log
 * $
 */
