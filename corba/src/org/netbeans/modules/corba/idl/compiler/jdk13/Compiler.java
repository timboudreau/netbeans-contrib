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

package org.netbeans.modules.corba.idl.compiler.jdk13;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Compiler {

    public static final boolean DEBUG = true;
    //public static final boolean DEBUG = false;
  
    public static void main (String[] args) throws Exception {
	System.err.println ("Try to run with Sun or IBM JDK 1.3 IDL Compiler");
	boolean __cant_find = false;
	try {
	    Class __compiler = Class.forName ("com.sun.tools.corba.se.idl.Compile"); // NOI18N
	    System.err.println ("Run Sun JDK 1.3 IDL Compiler");
	    org.netbeans.modules.corba.idl.compiler.jdk13sun.Compiler.main (args);
	    System.exit (0);
	} catch (ClassNotFoundException __ex) {
	    __cant_find = true;
	} catch (Exception __ex) {
	    throw __ex;
	}
	if (__cant_find) {
	    try {
		Class __compiler = Class.forName ("com.ibm.idl.Compile"); // NOI18N
		System.err.println ("Run IBM JDK 1.3 IDL Compiler");
		org.netbeans.modules.corba.idl.compiler.jdk13ibm.Compiler.main (args);
		System.exit (0);
	    } catch (ClassNotFoundException __ex) {
	    }
	}
	System.err.println ("Can't find JDK 1.3 IDL compiler.");
    }

}

/*
 * $Log
 * $
 */

