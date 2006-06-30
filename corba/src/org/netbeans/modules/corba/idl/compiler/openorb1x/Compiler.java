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

package org.netbeans.modules.corba.idl.compiler.openorb1x;

import java.io.PrintStream;

import java.lang.reflect.Method;

public class Compiler extends PrintStream {

    public static void main (String[] __args) throws Exception {
	//System.out.println ("OpenORB 1.x IDL Compiler Wrapper 1.0");
        System.setOut(new Compiler (System.out));
        //org.openorb.compiler.IdlCompiler.main(__args);
	String[] __s_array = new String[0];
	Class __class = Class.forName ("org.openorb.compiler.IdlCompiler");
	Method __main = __class.getDeclaredMethod
	    ("main", new Class [] {__s_array.getClass ()});
	__main.invoke (null, new Object[] {__args});
    }

    Compiler (PrintStream out) {
        super (out);
    }

    public void println(String x) {
        if (x != null && x.startsWith("file:") && x.charAt(7) == ':') {
            super.print(x.substring(0, 5));
            println(x.substring(6));
        }
        else
            super.println(x);
    }

}
