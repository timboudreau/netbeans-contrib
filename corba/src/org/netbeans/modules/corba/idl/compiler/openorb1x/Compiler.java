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
