/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
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
            System.out.println ("IDLCompilerSupport::IDLCompilerSupport (...)");
    }

    protected CompilerType defaultCompilerType () {
        if (DEBUG)
            System.out.println ("IDLCompilerSupport::defaultCompilerType ()");
        return new IDLCompilerType ();
    }


    public static class Compile extends IDLCompilerSupport
        implements CompilerCookie.Compile {

        public Compile (MultiDataObject.Entry entry) {
            super (entry, CompilerCookie.Compile.class);
            if (DEBUG)
                System.out.println ("Compile::Compile (...)");
        }
    }

}

/*
 * $Log
 * $
 */
